package dao;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import dao.UserDao;
import entity.UserEntity;

public class UserDaoTest {

	@Test
	public void addUserTest() throws SQLException, ClassNotFoundException {
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class);

		UserEntity user = new UserEntity();
		user.setId("idid");
		user.setName("namename");
		user.setPassword("pwpw");

		dao.add(user);

		UserEntity user2 = dao.get(user.getId());

		assertEquals(user.getId(), user2.getId());
		assertEquals(user.getName(), user2.getName());
		assertEquals(user.getPassword(), user2.getPassword());
	}

	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException {
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class);

		dao.deleteAll();
		assertEquals(0, dao.getCout());

		dao.add(new UserEntity("idid", "nmnm", "pwpw"));
		assertEquals(1, dao.getCout());

		dao.add(new UserEntity("idid2", "nmnm", "pwpw"));
		assertEquals(2, dao.getCout());
	}
}