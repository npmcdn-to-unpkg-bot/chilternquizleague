package org.chilternquizleague.contact;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.chilternquizleague.domain.GlobalApplicationData;
import org.chilternquizleague.domain.GlobalApplicationData.EmailAlias;
import org.chilternquizleague.domain.Team;
import org.chilternquizleague.domain.User;
import org.chilternquizleague.web.AppStartListener;

import com.googlecode.objectify.Key;

public class EmailSender {

	private static Logger LOG = Logger.getLogger(EmailSender.class.getName());
	
	public EmailSender() {}
	
	public void sendMail(String sender, String recipientName, String text){
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			final MimeMessage message = new MimeMessage(session,
					new ByteArrayInputStream(text.getBytes()));

			final GlobalApplicationData globaldata = ofy().load().now(
					Key.create(GlobalApplicationData.class,
							AppStartListener.globalApplicationDataId));

			for (EmailAlias alias : globaldata.getEmailAliases()) {

				if (alias.getAlias().equals(recipientName)) {
					sendMail(message, sender, new MimeMessage(session),
							new InternetAddress(alias.getUser().getEmail()));
					return;
				}
			}

			final List<Team> teams = ofy().load().type(Team.class).list();

			for (Team team : teams) {

				if (recipientName.equals(team.getEmailName())) {

					MimeMessage outMessage = new MimeMessage(session);

					final Address[] addresses = new Address[team.getUsers()
							.size()];

					int idx = 0;
					for (User user : team.getUsers()) {
						addresses[idx++] = new InternetAddress(user.getEmail());
					}

					sendMail(message, sender, outMessage, addresses);

					return;
				}
			}

			LOG.fine("No matching addressees for any recipients");

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failure recieving mail", e);
		}
	}
	
	private void sendMail(final MimeMessage message, final String sender,
			final MimeMessage outMessage, final Address... addresses)
			throws MessagingException, AddressException, IOException {
		try {

			outMessage.addRecipients(RecipientType.TO, addresses);
			outMessage
					.setSender(new InternetAddress(sender));
			outMessage.setContent(message.getContent(),
					message.getContentType());
			outMessage.setSubject(message.getSubject());

			LOG.fine(outMessage.getFrom()[0] + " to "
					+ outMessage.getAllRecipients()[0].toString());

			Transport.send(outMessage);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failure sending mail", e);
		}
	}

}
