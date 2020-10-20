package seleniumUtils;
import java.util.Date;
import java.util.Properties;

//import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

	/**
	 * Utility method to send simple HTML email
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	public static void sendEmail(String toEmail, String subject, String body)
	{
		try
	    {
		  
			Properties props = System.getProperties();
			String smtpHostServer = "transmit.supraits.com";
			props.put("mail.smtp.host", smtpHostServer);
			props.put("mail.smtp.port", "25");
			//props.put("mail.smtp.auth", "true");

			Session session = Session.getInstance(props, null);
			    
			MimeMessage msg = new MimeMessage(session);
			//set message headers
			
			//msg.setFrom(new InternetAddress("aaa@bbb.com", "NoReply-websitetest"));
			//msg.setReplyTo(InternetAddress.parse("aaa@bbb.com", false));
			msg.setFrom(new InternetAddress("myaddr@org.ca", "NoReply-websitetest"));
			msg.setReplyTo(InternetAddress.parse("myaddr@org.ca", false));
			msg.setSubject(subject, "UTF-8");
			msg.setContent(body, "text/html");
			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
			System.out.println("Message from host: "+ smtpHostServer + "is ready." );
			Transport.send(msg);  
			//System.out.println("EMail Sent Successfully!!");
		}
		catch (Exception e) {
		  JUnitHTMLReporter.report("<b>Error: unable to send e-mail</b>: " + e.toString());
		  e.printStackTrace();
		}
	}
}
