package hydra.common.reflect

import hydra.common.logging.LoggingAdapter

import scala.collection.immutable.ListMap
import scala.reflect.api.{Mirror, TypeCreator, Universe}
import scala.reflect.runtime.universe._

/**
  * Companion object for [[ReflectionHelpers]]
  */
object ReflectionHelpers extends ReflectionHelpers

trait ReflectionHelpers extends LoggingAdapter {

  protected[hydra] val classLoaderMirror = runtimeMirror(getClass.getClassLoader)

  /**
    * Returns a sequence of Strings, each of which names a value of the
    * supplied enumeration type.
    */
  def symbolsOf[E <: Enumeration : TypeTag]: Seq[String] = {
    val valueType = typeOf[E#Value]

    val isValueType = (sym: Symbol) => {
      !sym.isMethod && !sym.isType &&
        sym.typeSignature.baseType(valueType.typeSymbol) =:= valueType
    }

    typeOf[E].members.collect {
      case sym: Symbol if isValueType(sym) => sym.name.toString.trim
    }.toSeq.reverse
  }

  /**
    * Returns a type tag for the parent `scala.Enumeration` of the supplied
    * enumeration value type.
    */
  def enumForValue[V <: Enumeration#Value : TypeTag]: TypeTag[_ <: Enumeration] = {
    val TypeRef(enclosing, _, _) = typeOf[V]
    tagForType(enclosing).asInstanceOf[TypeTag[_ <: Enumeration]]
  }

  /**
    * return name and values of sealed trait enum
    */
  def nameAndValues[T: TypeTag]: (String, Map[String, T]) = {
    val tt = typeTag[T]
    val children = tt.tpe.typeSymbol.asClass.knownDirectSubclasses.toList
    if (!children.forall(_.isModuleClass)) {
      throw new IllegalArgumentException("all children must be objects")
    }
    (tt.tpe.typeSymbol.name.toString, children.map(v => (v.name.toString, instanceBySymbol[T](v))).toMap)
  }

  private def instanceBySymbol[T](sym: Symbol): T = {
    classLoaderMirror.runtimeClass(sym.asClass).getField("MODULE$").get(null).asInstanceOf[T]
  }

  /**
    * Returns a map from formal parameter names to type tags, containing one
    * mapping for each constructor argument.  The resulting map (a ListMap)
    * preserves the order of the primary constructor's parameter list.
    *
    * @tparam T the type of the case class to inspect
    */
  def caseClassParamsOf[T: TypeTag]: ListMap[String, TypeTag[_]] = {
    val tpe = typeOf[T]
    val constructorSymbol = tpe.decl(termNames.CONSTRUCTOR)
    val defaultConstructor =
      if (constructorSymbol.isMethod) {
        constructorSymbol.asMethod
      }
      else {
        val ctors = constructorSymbol.asTerm.alternatives
        ctors.map {
          _.asMethod
        }.find {
          _.isPrimaryConstructor
        }.get
      }

    ListMap[String, TypeTag[_]]() ++ defaultConstructor.paramLists.reduceLeft(_ ++ _).map {
      sym => sym.name.toString -> tagForType(tpe.member(sym.name).asMethod.returnType)
    }
  }

  /**
    * Returns `Some(value)` if there is a default value for the supplied
    * parameter name for the supplied case class type and `None` otherwise.
    * If the supplied parameter is not defined for the type's apply method,
    * this method simply returns `None`.
    *
    * @tparam T the type of the case class to inspect
    */
  def defaultCaseClassValues[T: TypeTag]: Map[String, Option[Any]] = {
    val companion = CompanionMetadata[T].get

    val applySymbol: MethodSymbol = {
      val symbol = companion.classType.member(TermName("apply"))
      if (symbol.isMethod) symbol.asMethod else symbol.asTerm.alternatives.head.asMethod // symbol.isTerm
    }

    def valueFor(i: Int): Option[Any] = {
      val defaultValueThunkName = TermName(s"apply$$default$$${i + 1}")
      val defaultValueThunkSymbol = companion.classType member defaultValueThunkName

      if (defaultValueThunkSymbol == NoSymbol) {
        None
      }
      else {
        val defaultValueThunk = companion.instanceMirror reflectMethod defaultValueThunkSymbol.asMethod
        Some(defaultValueThunk.apply())
      }
    }

    applySymbol.paramLists.flatten.zipWithIndex.map { case (p, i) => p.name.toString -> valueFor(i) }.toMap
  }

  /**
    * Returns a TypeTag in the current runtime universe for the supplied type.
    */
  def tagForType(tpe: Type): TypeTag[_] = TypeTag(
    classLoaderMirror,
    new TypeCreator {
      def apply[U <: Universe with Singleton](m: Mirror[U]) = tpe.asInstanceOf[U#Type]
    }
  )

  /**
    * Wraps information about a companion object for a type.
    */
  case class CompanionMetadata[T](symbol: ModuleSymbol,
                                  instance: Any,
                                  instanceMirror: InstanceMirror,
                                  classType: Type)

  object CompanionMetadata {
    /**
      * Returns a Some wrapping CompanionMetadata for the supplied class type, if
      * that class type has a companion, and None otherwise.
      */
    def apply[T: TypeTag]: Option[CompanionMetadata[T]] = {

      val typeSymbol = typeOf[T].typeSymbol

      val companion: Option[ModuleSymbol] = {
        if (!typeSymbol.isClass) {
          None // supplied type is not a class
        }
        else {
          val classSymbol = typeSymbol.asClass
          val classMirror = rootMirror.reflectClass(classSymbol)
          val mr = classMirror.symbol.companion
          if (!mr.isModule) None else Some(classSymbol.companion.asModule)
        }
      }

      companion.map { symbol =>
        val instance = classLoaderMirror.reflectModule(symbol).instance
        val instanceMirror = classLoaderMirror reflect instance
        val classType = symbol.moduleClass.asClass.asType.toType
        CompanionMetadata(symbol, instance, instanceMirror, classType)
      }
    }
  }

  def instantiateType[M: TypeTag](args: List[Any]): M = {
    val cl = typeOf[M].typeSymbol.asClass
    instance(cl, args)
  }

  def instantiateClass[M](cls: Class[M], args: List[Any]): M = {
    import scala.reflect.runtime.{currentMirror => cm}
    val cl = cm.classSymbol(cls)
    instance(cl, args)
  }

  def instantiateClassByName[M: TypeTag](clazz: String, args: List[Any]): M = {
    import scala.reflect.runtime.{currentMirror => cm}
    val cl = cm.classSymbol(Class.forName(clazz))
    instance(cl, args)
  }

  def fieldsOf[T: TypeTag]: Seq[MethodSymbol] = {
    import scala.reflect.runtime.{universe => ru}
    ru.typeOf[T].members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }.toSeq
  }

  private def instance[M: TypeTag](cl: ClassSymbol, args: List[Any]): M = {
    import scala.reflect.runtime.{currentMirror => cm}
    val clazz = cm.reflectClass(cl)
    val ctor = cl.toType.decl(termNames.CONSTRUCTOR).asMethod
    val ctorm = clazz.reflectConstructor(ctor)
    val obj = ctorm(args: _*).asInstanceOf[M]
    obj
  }
}
