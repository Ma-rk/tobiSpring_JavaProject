package service;

import java.util.List;

import code.Level;
import dao.UserDao;
import entity.UserEntity;

public class UserService {
	UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void upgradeUserLevel() {
		List<UserEntity> users = userDao.getAll();
		for (UserEntity user : users) {
			Boolean changed = null;
			if (user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
				user.setLevel(Level.SILVER);
				changed = true;
			} else if (user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
				user.setLevel(Level.GOLD);
				changed = true;
			} else if (user.getLevel() == Level.GOLD) {
				changed = false;
			} else {
				changed = false;
			}
			if (changed) userDao.update(user);
		}
	}
}