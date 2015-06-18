package service;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import code.Level;
import dao.UserDao;
import entity.UserEntity;

public class UserService {
	public static final int MIN_LOGCOUT_FOR_SILVER = 50;
	public static final int MIN_RECCOMMEND_FOR_GOLD = 30;

	private UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void add(UserEntity user) {
		if (user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}

	public void upgradeUserLevel() throws Exception {
		PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
		TransactionStatus status=transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			List<UserEntity> users = userDao.getAll();
			for (UserEntity user : users) {
				if (isQualifiedToUpgradeUserLevel(user)) upgradeUserLevel(user);
			}
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
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

	protected void upgradeUserLevel(UserEntity user) {
		user.upgradeLevel();
		userDao.update(user);
	}
}