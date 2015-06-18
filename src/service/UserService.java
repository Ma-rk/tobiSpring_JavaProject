package service;

import java.util.List;

import code.Level;
import dao.UserDao;
import entity.UserEntity;

public class UserService {
	UserDao userDao;

	public static final int MIN_LOGCOUT_FOR_SILVER = 50;
	public static final int MIN_RECCOMMEND_FOR_GOLD = 30;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void add(UserEntity user) {
		if (user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}

	public void upgradeUserLevel() {
		List<UserEntity> users = userDao.getAll();
		for (UserEntity user : users) {
			if (isQualifiedToUpgradeUserLevel(user)) upgradeUserLevel(user);
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

	private void upgradeUserLevel(UserEntity user) {
		user.upgradeLevel();
		userDao.update(user);
	}
}