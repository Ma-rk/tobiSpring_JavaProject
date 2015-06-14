package dao;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dao.UserDao;
import entity.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserDaoTest {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private UserDao dao;

	private UserEntity user1;
	private UserEntity user2;

	@Before
	public void setup() {
		this.user1 = new UserEntity("idid", "namename", "pwpw");
		this.user2 = new UserEntity("idid2", "nmnm", "pwpw");
	}

	@Test
	public void addUserTest() throws SQLException, ClassNotFoundException {
		dao.add(user1);

		UserEntity retrievedUser = dao.get(user1.getId());

		assertEquals(user1.getId(), retrievedUser.getId());
		assertEquals(user1.getName(), retrievedUser.getName());
		assertEquals(user1.getPassword(), retrievedUser.getPassword());
	}

	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException {
		dao.deleteAll();
		assertEquals(0, dao.getCount());

		dao.add(user1);
		assertEquals(1, dao.getCount());

		dao.add(user2);
		assertEquals(2, dao.getCount());

		dao.deleteAll();
		assertEquals(0, dao.getCount());
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException, ClassNotFoundException {
		dao.deleteAll();
		assertEquals(0, dao.getCount());

		dao.get("unknownId");
	}
}