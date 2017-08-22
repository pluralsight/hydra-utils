package hydra.common.reflect

import hydra.common.reflect.ReflectionHelpers._
import org.scalatest.{FlatSpec, Matchers}

import scala.reflect.runtime.universe._


class ReflectionHelpersSpec extends FlatSpec with Matchers {

  "The reflection helpers object" should "return the enumeration tag for a given enum value" in {
    val et = enumForValue[Direction.type#Value]
    et.tpe =:= typeOf[Direction.type] should be(true)
  }

  it should "return constructor parameters for case classes" in {
    import scala.collection.immutable.ListMap
    caseClassParamsOf[Animal] should be(ListMap("sound" -> typeTag[String]))
  }

  it should "return constructor parameters for case classes with multiple constructors" in {
    caseClassParamsOf[Person] should have size (2)
  }

  it should "return enum symbols" in {
    symbolsOf[WeekDay.type] shouldBe Seq("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
  }


  it should "extract default values for case class parameters" in {
    val default = Exclamation(volume = 11)
    val defaultArgs = defaultCaseClassValues[Exclamation]
    defaultArgs("word") should equal(Some(default.word))
    defaultArgs("volume") should equal(None)
  }

  it should "identify companion objects" in {
    val md = ReflectionHelpers.CompanionMetadata[TestClass].get
    md.instance shouldBe TestClass
    ReflectionHelpers.CompanionMetadata[TesterNoCompanion] shouldBe None
    ReflectionHelpers.CompanionMetadata[(String) => String] shouldBe None
  }

  it should "extract name and values for sealed trait enum" in {
    val (name, values) = nameAndValues[Color]
    val m: Map[String, Color] = values
    name should equal("Color")
    m should equal(Map("Black" -> Color.Black, "Blue" -> Color.Blue, "Green" -> Color.Green))
  }

  it should "error when named enum is a case class" in {
    intercept[IllegalArgumentException] {
      nameAndValues[ErrorObj]
    }
  }

  it should "Instantiates a class with constructor params" in {
    ReflectionHelpers.instantiateType[TestClass](List("value")) shouldBe TestClass("value")
    ReflectionHelpers.instantiateClass(classOf[TestClass], List("value")) shouldBe TestClass("value")
  }

  it should "Instantiates classes by name" in {
    val obj = ReflectionHelpers.instantiateClassByName[TestClass]("hydra.common.reflect.TestClass", List("value"))
    obj shouldBe TestClass("value")
  }

  it should "gets class fields" in {
    ReflectionHelpers.fieldsOf[TestClass].map(_.name.toString) shouldBe Seq("value")
  }
}

case class TestClass(value: String)

object TestClass {
  def apply(n: Int) = new TestClass(n.toString)
}

class TesterNoCompanion()

