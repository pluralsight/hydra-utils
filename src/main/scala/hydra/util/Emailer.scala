package hydra.util

import javax.mail.internet.InternetAddress

import courier.{Mailer, _}

import scala.concurrent.{ExecutionContext, Future}

class Emailer(smtpServer: String, port: Option[Int],
              username: String, password: String, sender: InternetAddress) {

  lazy val mailer = Mailer(smtpServer, port.getOrElse(587))
    .auth(true)
    .as(username, password)
    .startTtls(true)()

  def send(subject: String, body: String, recipient: InternetAddress*)(implicit ec: ExecutionContext): Future[Unit] = {
    mailer(Envelope.from(sender).to(recipient: _*).subject(subject).content(Text(body)))
  }
}
