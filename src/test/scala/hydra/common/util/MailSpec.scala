package hydra.common.util

import courier._
import hydra.util.Emailer
import org.jvnet.mock_javamail.Mailbox
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

class MailSpec extends FlatSpec with Matchers with ScalaFutures {
  override implicit val patienceConfig = PatienceConfig(timeout = 10.seconds)
  "the mailer" should "send an email" in {
    val mailer = new Emailer("localhost", Some(25), "test", "test", "test" at "ps.com")
    val future = mailer.send("miss you", "hi mom", "mom@gmail.com".addr)
    whenReady(future) { _ =>
      val momsInbox = Mailbox.get("mom@gmail.com")
      momsInbox.size shouldBe 1
      val momsMsg = momsInbox.get(0)
      momsMsg.getContent shouldBe "hi mom"
      momsMsg.getSubject shouldBe "miss you"
    }
  }
}
