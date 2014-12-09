package scala.org.chilternquizleague.web

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


class MailHandler extends HttpServlet {
  
  val LOG: Logger = Logger.getLogger(classOf[MailHandler].getName());
  
  override def doPost(req:HttpServletRequest, resp:HttpServletResponse):Unit = {
     
    	val props:Properties = new Properties;
		val session:Session = Session.getDefaultInstance(props, null);
		
				try {
			val message = new MimeMessage(session,
					req.getInputStream());

			val globaldata = ofy().load().now(
					Key.create(classOf[GlobalApplicationData],
							AppStartListener.globalApplicationDataId.get));
			val recipientParts = req.getPathInfo().replaceFirst("/", "")
					.split("@");

			val recipientName = recipientParts(0);

			globaldata.getEmailAliases.filter(_.getAlias == recipientName).foreach{alias => sendMail(message, globaldata, new InternetAddress(alias.getUser().getEmail()));return }
			
			ofy().load().`type`(classOf[Team]).list().filter(_.getEmailName == recipientName).foreach {team =>
			  
			  val addresses:Array[Address] = team.getUsers().map{a:User => new InternetAddress(a.getEmail)}.toArray
			  
			  sendMail(message, globaldata, addresses:_*)
			  
			  return
			  
			}

			LOG.fine("No matching addressees for any recipients");

		} catch {
		  case e:Exception => LOG.log(Level.SEVERE, "Failure recieving mail", e);
		}
    
    
    
  }
  
  	def sendMail(message:MimeMessage, globaldata:GlobalApplicationData,
			 addresses:Address*):Unit =
			{
		
		try {
			
			message.setRecipients(RecipientType.TO, addresses.toArray);
			
			message.setSubject("via " + globaldata.getLeagueName + " : " + message.getSubject);

			LOG.fine(message.getFrom()(0) + " to "
					+ message.getAllRecipients()(0).toString());

			Transport.send(message);
		} catch {
		  
		  case e:Exception =>{
			LOG.log(Level.SEVERE, "Failure sending mail", e);
			
			val session = Session.getDefaultInstance(new Properties(), null);
			
			val notification = new MimeMessage(session);
			notification.addRecipient(RecipientType.TO, message.getFrom()(0));
			notification.setSender(message.getAllRecipients()(0));
			notification.setSubject(globaldata.getLeagueName() + " : Message delivery failed");
			notification.setText("Message delivery failed, probably due to an attachment.\nThis mail service does not allow attachments.  Try resending as text only.");
			
			Transport.send(notification);
		  }
		}
	}

}