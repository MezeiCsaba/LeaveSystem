package holiday.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import holiday.entity.User;

@Service
public class EmailService implements Runnable {

	private final Log log = LogFactory.getLog(this.getClass());

	@Value("${spring.mail.username}")
	private String MESSAGE_FROM;
	
	private User user;
	private String subject;
	private String messageText;

	private JavaMailSender javaMailSender;

	
	
	public EmailService() {}

	public EmailService(User user, String subject, String messageText, JavaMailSender javaMailSender) {
		this.user = user;
		this.subject = subject;
		this.messageText = messageText;
		this.javaMailSender=javaMailSender;
	}



	@Override
	public void run() {

		SimpleMailMessage message = null;
		String name = user.getName();
		String emailAddres = user.getEmail();

		try {

			message = new SimpleMailMessage();
			message.setFrom(MESSAGE_FROM);
			message.setTo(emailAddres);
			message.setSubject(subject); // regisztrációs, jóváhagyói, stb
			message.setText("Kedves " + name + "! \n\n" + messageText);

			javaMailSender.send(message);
			log.debug("E-mai sikeresen elküldve a(z)" + emailAddres + " címre!");

		} catch (Exception e) {
			log.debug("Hiba az e-mail küldésekor, a(z)" + emailAddres + " címre! (" + e + ")");
			System.out.println("Hiba az e-mail küldésekor, a(z)" + emailAddres + " címre! (" + e + ")");
		}
		
		

	}

	

}
