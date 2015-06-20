package service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MockMailSender implements MailSender {
	private List<String> requests=new ArrayList<String>();

	public List<String> getRequest(){
		return requests;
	}

	public void send(SimpleMailMessage mailMessage) throws MailException {
		requests.add(mailMessage.getTo()[0]);
	}

	public void send(SimpleMailMessage[] arg0) throws MailException {
	}
}