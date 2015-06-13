package dao;

import core.ConnectionMaker;
import core.DConnectionMaker;

public class DaoFactory {
	public UserDao userDao() {
		return new UserDao(connectionMaker());
	}

	public ConnectionMaker connectionMaker() {
		return new DConnectionMaker();
	}
}