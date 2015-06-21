package service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import code.Level;
import dao.UserDao;
import entity.UserEntity;

public class UserServiceImpl implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	public static final int MIN_LOGCOUT_FOR_SILVER = 50;
	public static final int MIN_RECCOMMEND_FOR_GOLD = 30;

	/*
	 * DI codes
	 */
	private UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	private MailSender mailSender;

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	/*
	 * functional methods
	 */
	public void add(UserEntity user) {
		if (user.getLevel() == null) {
			user.setLevel(Level.BASIC);
		}
		this.userDao.add(user);
	}

	public void upgradeLevelOfEveryUser() {
		logger.info("upgradeLevelOfEveryUser()====>");
		List<UserEntity> users = this.userDao.getAll();
		for (UserEntity user : users) {
			if (isQualifiedToUpgradeUserLevel(user)) {
				upgradeLevelOfOneUser(user);
			}
		}
		logger.info("upgradeLevelOfEveryUser()<====");
	}

	/*
	 * supporting methods
	 */
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
		logger.info("upgradeLevelOfOneUser(UserEntity)======>");
		user.upgradeMyLevel();
		this.userDao.update(user);
		sendUpgradeEmail(user);
		logger.info("upgradeLevelOfOneUser(UserEntity)<======");
	}
}