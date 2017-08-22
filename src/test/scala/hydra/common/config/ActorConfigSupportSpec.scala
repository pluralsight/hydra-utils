package hydra.common.config

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import com.typesafe.config.ConfigFactory
import hydra.common.testing.{DummyActor, DummyActor2}
import org.scalatest.{BeforeAndAfterAll, FunSpecLike, Matchers}

/**
  * Created by alexsilva on 3/2/17.
  */
class ActorConfigSupportSpec extends TestKit(ActorSystem("test")) with Matchers with FunSpecLike
  with ConfigSupport with BeforeAndAfterAll {

  val dummy = TestActorRef[DummyActor]

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  describe("When mixing the trait in an actor") {
    it("has the correct actor name") {
      dummy.underlyingActor.thisActorName shouldBe "dummy_actor"
    }

    it("has the correct actor config") {
      dummy.underlyingActor.actorConfig shouldBe rootConfig.getConfig("hydraTest.actors.dummy_actor")
    }

    it("returns an empty config if none found") {
      val act = TestActorRef[DummyActor2]
      act.underlyingActor.actorConfig shouldBe ConfigFactory.empty
    }

  }
}
