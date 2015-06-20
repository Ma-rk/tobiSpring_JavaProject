package service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DummyMailSender implements MailSender {
	public void send(SimpleMailMessage arg0) throws MailException {
		System.out.println("sent single mail.");
	}

	public void send(SimpleMailMessage[] arg0) throws MailException {
		System.out.println("sent multi mails.");
	}
}