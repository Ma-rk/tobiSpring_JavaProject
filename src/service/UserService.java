package service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
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

	private MailSender mailSender;

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	/*
	 * functional methods
	 */
	public void add(UserEntity user) {
		if (user.getLevel() == null) user.setLevel(Level.BASIC);
		this.userDao.add(user);
	}

	public void upgradeLevelOfEveryUser() throws Exception {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			List<UserEntity> users = this.userDao.getAll();
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
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("mailmail@mail.com");
		mailMessage.setSubject("User level has been upgraded.");
		mailMessage.setText("User level has been upgraded to [" + user.getLevel() + "]");

		this.mailSender.send(mailMessage);
	}

	protected void upgradeLevelOfOneUser(UserEntity user) {
		user.upgradeLevel();
		this.userDao.update(user);
		sendUpgradeEmail(user);
	}
}