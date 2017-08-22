package hydra.common.util

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import hydra.common.testing.{DummyActor, DummyActor2}
import hydra.util.ActorUtils
import org.scalatest.{BeforeAndAfterAll, FunSpecLike, Matchers}

/**
  * Created by alexsilva on 3/2/17.
  */
class ActorUtilsSpec extends TestKit(ActorSystem("test")) with Matchers with FunSpecLike
  with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  describe("When using ActorUtils") {
    it("names actors correctly") {
      ActorUtils.actorName(classOf[DummyActor]) shouldBe "dummy_actor"
      ActorUtils.actorName[DummyActor] shouldBe "dummy_actor"
    }

    it("strips out akka router paths from actor names") {
      val act = TestActorRef[DummyActor](Props[DummyActor], "dummy_actor")
      ActorUtils.routeeParent(act) shouldBe "dummy_actor"
      val act2 = act.underlyingActor.context.actorOf(Props[DummyActor2])
      ActorUtils.routeeParent(act2) shouldBe "dummy_actor"
    }
  }

}


