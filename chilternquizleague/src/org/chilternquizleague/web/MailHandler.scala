package org.chilternquizleague.web

import scala.collection.JavaConversions._
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.Properties
import javax.mail.Session
import javax.mail.internet.MimeMessage
import org.chilternquizleague.domain.GlobalApplicationData
import com.googlecode.objectify.ObjectifyService.ofy
import com.googlecode.objectify.Key
import javax.mail.internet.InternetAddress
import org.chilternquizleague.domain.Team
import javax.mail.Address
import org.chilternquizleague.domain.User
import java.util.logging.Logger
import java.util.logging.Level
import javax.mail.Message.RecipientType
import javax.mail.Transport
import org.chilternquizleague.util.Storage._
import org.chilternquizleague.domain.util.RefUtils._

class MailHandler extends HttpServlet {

  val LOG: Logger = Logger.getLogger(classOf[MailHandler].getName());

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {

    val props: Properties = new Properties;
    val session: Session = Session.getDefaultInstance(props, null);

    try {
      val message = new MimeMessage(session,
        req.getInputStream());

      val globaldata = Application.globalData.get
      val recipientParts = req.getPathInfo().replaceFirst("/", "").split("@")

      val recipientName = recipientParts(0);

      globaldata.emailAliases.filter(_.alias == recipientName).foreach { alias => sendMail(message, globaldata, new InternetAddress(alias.user.email)); return }

      entityList(classOf[Team]).filter(_.emailName == recipientName).foreach { team:Team =>

        val addresses: Array[Address] = team.users.map { a => new InternetAddress(a.email) }.toArray

        sendMail(message, globaldata, addresses: _*)

        return

      }

      LOG.fine("No matching addressees for any recipients");

    } catch {
      case e: Exception => LOG.log(Level.SEVERE, "Failure recieving mail", e);
    }

  }

  def sendMail(message: MimeMessage, globaldata: GlobalApplicationData,
    addresses: Address*): Unit =
    {

      try {

        message.setRecipients(RecipientType.TO, addresses.toArray);

        message.setSubject(s"via  ${globaldata.leagueName} : ${message.getSubject}");

        LOG.fine(s"${message.getFrom()(0)} to ${message.getAllRecipients()(0).toString}");

        Transport.send(message);
      } catch {

        case e: Exception => {
          LOG.log(Level.SEVERE, "Failure sending mail", e);

          val session = Session.getDefaultInstance(new Properties(), null);

          val notification = new MimeMessage(session);
          notification.addRecipient(RecipientType.TO, message.getFrom()(0));
          notification.setSender(message.getAllRecipients()(0));
          notification.setSubject(s"${globaldata.leagueName} : Message delivery failed");
          notification.setText("Message delivery failed, probably due to an attachment.\nThis mail service does not allow attachments.  Try resending as text only.");

          Transport.send(notification);
        }
      }
    }

}

object EmailSender{
  
  def apply(sender: String, recipientName: String, text: String) = new EmailSender().sendMail(sender, recipientName, text)
}

private class EmailSender {

  val LOG: Logger = Logger.getLogger(classOf[EmailSender].getName);

  def sendMail(sender: String, recipientName: String, text: String): Unit = {

    try {

      val globaldata = Application.globalData

      globaldata.foreach { g =>
        g.emailAliases.filter(_.alias == recipientName).foreach {
          alias =>
            {
              sendMail(sender, text, g,
                new InternetAddress(alias.user.email));
              return
            }

        }
        val teams = entityList(classOf[Team])

        teams.filter(recipientName == _.emailName).foreach {
          team:Team =>
            {

              sendMail(sender, text, g, team.users.map(user => new InternetAddress(user.email)).toSeq: _*)
              return
            }
        }
      }

      LOG.fine("No matching addressees for any recipients");

    } catch {
      case e: Exception => LOG.log(Level.SEVERE, "Failure recieving mail", e);
    }
  }

  def sendMail(sender: String, text: String, globalApplicationData: GlobalApplicationData,
    addresses: Address*): Unit = {

    val props = new Properties();
    val session = Session.getDefaultInstance(props, null);

    try {
      val outMessage = new MimeMessage(session);
      outMessage.addRecipients(RecipientType.TO, addresses.toArray);
      outMessage
        .setSender(new InternetAddress(sender));
      outMessage.setText(text);
      outMessage.setSubject(s"Sent via ${globalApplicationData.leagueName}");

      LOG.fine(s"${outMessage.getFrom()(0)} to ${outMessage.getAllRecipients()(0).toString}");

      Transport.send(outMessage);
    } catch {
      case e: Exception => LOG.log(Level.SEVERE, "Failure sending mail", e);
    }
  }
}