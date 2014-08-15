package org.chilternquizleague.web;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chilternquizleague.domain.GlobalApplicationData;
import org.chilternquizleague.domain.GlobalApplicationData.EmailAlias;
import org.chilternquizleague.domain.Team;
import org.chilternquizleague.domain.User;

import com.googlecode.objectify.Key;



@SuppressWarnings("serial")
public class MailHandlerServlet extends HttpServlet { 
    
	
	
	@Override
    public void doPost(HttpServletRequest req, 
                       HttpServletResponse resp) 
            throws IOException { 
        Properties props = new Properties(); 
        Session session = Session.getDefaultInstance(props, null); 
        try {
			final MimeMessage message = new MimeMessage(session, req.getInputStream());
		
			final GlobalApplicationData globaldata = ofy().load().now(
					Key.create(GlobalApplicationData.class,
							AppStartListener.globalApplicationDataId));
			
			final String localHost = new URI(req.getRequestURI()).getHost();

			for(Address recip : message.getAllRecipients()){

				final String recipientName = recip.toString().split("@")[0];
			
				for(EmailAlias alias:globaldata.getEmailAliases()){
				
				if(alias.getAlias().equals(recipientName)){
					sendMail(message, localHost, new MimeMessage(session), new InternetAddress(alias.getUser().getEmail()));					
					return;
				}
			}
			
			
			final List<Team> teams = ofy().load().type(Team.class).list();
			
			for(Team team : teams){
				
					
					
					if(recipientName.equals(team.getEmailName())){
						
					MimeMessage  outMessage = new MimeMessage(session);
					
					final Address[] addresses = new Address[team.getUsers().size()];
					

					int idx  = 0;
					for(User user : team.getUsers()){
						addresses[idx++] = new InternetAddress(user.getEmail());
					}
					
					sendMail(message, localHost, outMessage, addresses);
					
					return;
					}
				}
				
				
			}
			
			
        
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	private void sendMail(final MimeMessage message, final String localHost,
			final MimeMessage outMessage, final Address... addresses)
			throws MessagingException, AddressException, IOException {
		outMessage.addRecipients(RecipientType.TO, addresses);
		outMessage.setSender(new InternetAddress("forwarding@"+ localHost));
		outMessage.setContent(message.getContent(), message.getContentType());
		outMessage.setSubject(message.getSubject());
		
		System.out.println(outMessage.getFrom()[0] + " to " + outMessage.getAllRecipients()[0].toString());

		
		Transport.send(outMessage);
	}
}
