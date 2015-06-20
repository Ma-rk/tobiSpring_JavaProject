package service;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import code.Level;
import dao.UserDao;
import entity.UserEntity;

public class UserService {
	public static final int MIN_LOGCOUT_FOR_SILVER = 50;
	public static final int MIN_RECCOMMEND_FOR_GOLD = 30;

	/*
	 * DI codes
	 */
	private UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	private PlatformTransactionManager transactionManager;

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/*
	 * functional methods
	 */
	public void add(UserEntity user) {
		if (user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}

	public void upgradeLevelOfEveryUser() throws Exception {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			List<UserEntity> users = userDao.getAll();
			for (UserEntity user : users) {
				if (isQualifiedToUpgradeUserLevel(user)) upgradeLevelOfOneUser(user);
			}
			this.transactionManager.commit(status);
		} catch (Exception e) {
			this.transactionManager.rollback(status);
			throw e;
		}
	}

	private boolean isQualifiedToUpgradeUserLevel(UserEntity user) {
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
		case BASIC:
			return (user.getLogin() >= MIN_LOGCOUT_FOR_SILVER);
		case SILVER:
			return (user.getRecommend() >= MIN_RECCOMMEND_FOR_GOLD);
		case GOLD:
			return false;
		default:
			throw new IllegalArgumentException("Unknown user level: " + currentLevel);
		}
	}

	private void sendUpgradeEmail(UserEntity user) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "mail.com");
		Session s = Session.getInstance(props, null);

		MimeMessage message = new MimeMessage(s);

		try {
			message.setFrom(new InternetAddress("mailmail@mail.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
			message.setSubject("User level has been upgraded.");
			message.setText("User level has been upgraded to [" + user.getLevel() + "]");

			Transport.send(message);
		} catch (AddressException e) {
			throw new RuntimeException(e);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	protected void upgradeLevelOfOneUser(UserEntity user) {
		user.upgradeLevel();
		userDao.update(user);
		sendUpgradeEmail(user);
	}
}