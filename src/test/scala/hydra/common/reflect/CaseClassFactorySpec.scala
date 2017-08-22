package hydra.common.reflect


import hydra.common.testing.{DummyActor, TestCase, TestCase1}
import org.scalatest.{FunSpecLike, Matchers}

import scala.concurrent.duration._

/**
  * Created by alexsilva on 3/3/17.
  */
class CaseClassFactorySpec extends Matchers with FunSpecLike {
  describe("When using ReflectionUtils") {
    it("Instantiates a case class with constructor params") {
      new CaseClassFactory(classOf[TestCase1]).buildWith(Seq("name", 120, 2.seconds)) shouldBe
        TestCase1("name", 120, 2 seconds)
    }

    it("Rejects non-case classes") {
      intercept[IllegalArgumentException] {
        new CaseClassFactory(classOf[DummyActor]).buildWith(Seq("name", 120))
      }
    }

    it("Throws error if supplied constructor params do not match class's") {
      intercept[IllegalArgumentException] {
        new CaseClassFactory(classOf[TestCase]).buildWith(Seq("name"))
      }
    }

    it("instantiates a class with multiple constructors") {
      new CaseClassFactory(classOf[TestCase]).defaultConstructor
        .typeParams.map(_.name.toString) shouldBe Seq.empty
    }
  }
}
class TestCaseError(name: String, value: String) {
  def this(name: String) = this(name, "DEFAULT")
}






