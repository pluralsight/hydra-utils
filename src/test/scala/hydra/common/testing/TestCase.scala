package hydra.common.testing

import scala.concurrent.duration._

/**
  * Created by alexsilva on 3/6/17.
  */
case class TestCase(name: String, value: Int, duration: Duration = 1.second) {
  def this(name: String) = this(name, 1, 1.second)
}

object TestCase {
  def apply(name: String): TestCase = TestCase(name, 1, 1.second)
}

case class TestCase1(name: String, value: Int, duration: Duration = 1.second)