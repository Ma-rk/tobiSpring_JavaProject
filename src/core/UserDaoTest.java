package core;

import java.sql.SQLException;

import dao.UserDao;
import entity.UserEntity;

public class UserDaoTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		UserDao dao = new UserDao(new DConnectionMaker());

		UserEntity user = new UserEntity();
		user.setId("idid");
		user.setName("namename");
		user.setPassword("pwpw");

		dao.add(user);

		System.out.println("registerd [" + user.getId() + "]");

		UserEntity user2 = dao.get(user.getId());

		System.out.println("retrieved [" + user2.getId() + "]");
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
	}
}