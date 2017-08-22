package hydra.common.reflect

case class Animal(sound: String)

// case class with multiple constructors
case class Person(name: String, age: Int) {
  def this(name: String) = this(name, 0)
}

object Person {
  def apply(age: Int*): Person = Person("", age(0))
}

class A

abstract class B extends A

case class C() extends A

case class D() extends B

// a case class with default values
case class Exclamation(volume: Int, word: String = "Eureka!")

object WeekDay extends Enumeration {
  type WeekDay = Value
  val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value
}

sealed trait Color

object Color {

  case object Black extends Color

  case object Blue extends Color

  case object Green extends Color
}

sealed trait ErrorObj
object ErrorObj {
  case class ErrorTest(err:String) extends ErrorObj
}

object Direction extends Enumeration {
  type Direction = Value
  val NORTH, EAST, SOUTH, WEST = Value
}