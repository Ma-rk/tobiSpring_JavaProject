package dao;

import core.DConnectionMaker;

public class DaoFactory {
	public UserDao userDao() {
		return new UserDao(new DConnectionMaker());
	}
}