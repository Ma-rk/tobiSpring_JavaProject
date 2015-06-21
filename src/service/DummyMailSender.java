package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DummyMailSender implements MailSender {
	private static final Logger logger = LoggerFactory.getLogger(DummyMailSender.class);

	public void send(SimpleMailMessage arg0) throws MailException {
		logger.info("sent single mail.");
	}

	public void send(SimpleMailMessage[] arg0) throws MailException {
		logger.info("sent multy mails.");
	}
}