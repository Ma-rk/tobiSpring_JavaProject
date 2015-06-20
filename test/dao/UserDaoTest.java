package dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import code.Level;

import com.google.gson.Gson;

import entity.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserDaoTest {
	Gson gson = new Gson();

	@Autowired
	private ApplicationContext context;

	@Autowired
	private UserDao dao;

	private UserEntity user1;
	private UserEntity user2;
	private UserEntity user3;

	@Before
	public void setup() {
		this.user1 = new UserEntity("idid", "namename", "pwpw", Level.BASIC, 1, 0, "mailmail@mail.com");
		this.user2 = new UserEntity("idid2", "nmnm", "pwpw", Level.SILVER, 55, 10, "mailmail@mail.com");
		this.user3 = new UserEntity("idid3", "nm_nm", "pwpw", Level.GOLD, 100, 40, "mailmail@mail.com");
	}

	@Test
	public void addUserTest() {
		this.dao.add(user1);
		String userListJson = gson.toJson(dao.get(user1.getId()));
		System.out.println(userListJson);

		this.dao.deleteAll();

		this.dao.add(user2);
		userListJson = gson.toJson(dao.get(user2.getId()));
		System.out.println(userListJson);
	}

	@Test
	public void getAllTest() {
		this.dao.deleteAll();

		assertEquals(0, this.dao.getCount());

		List<UserEntity> users0 = this.dao.getAll();
		assertEquals(0, users0.size());

		this.dao.add(this.user1);
		List<UserEntity> users1 = this.dao.getAll();
		assertEquals(1, users1.size());
		checkUserCoidentity(this.user1, users1.get(0));

		this.dao.add(user2);
		List<UserEntity> users2 = this.dao.getAll();
		assertEquals(2, users2.size());
		checkUserCoidentity(this.user1, users2.get(0));
		checkUserCoidentity(this.user2, users2.get(1));

		this.dao.add(this.user3);
		List<UserEntity> users3 = this.dao.getAll();
		assertEquals(3, users3.size());
		checkUserCoidentity(this.user1, users3.get(0));
		checkUserCoidentity(this.user2, users3.get(1));
		checkUserCoidentity(this.user3, users3.get(2));
	}

	@Test
	public void addAndGet() {
		this.dao.deleteAll();
		assertEquals(0, this.dao.getCount());

		this.dao.add(this.user1);
		assertEquals(1, this.dao.getCount());
		checkUserCoidentity(this.user1, this.dao.get(this.user1.getId()));

		this.dao.add(this.user2);
		assertEquals(2, this.dao.getCount());
		checkUserCoidentity(this.user2, this.dao.get(this.user2.getId()));

		this.dao.deleteAll();
		assertEquals(0, this.dao.getCount());
	}

	@Test
	public void updateTest() {
		this.dao.deleteAll();

		this.dao.add(this.user1);
		this.dao.add(this.user2);
		this.dao.add(this.user3);

		assertEquals(3, this.dao.getCount());

		this.user1.setLogin(12);
		this.user1.setPassword("new_pw_pw");

		this.dao.update(this.user1);

		checkUserCoidentity(this.user2, this.dao.get(this.user2.getId()));
		checkUserCoidentity(this.user3, this.dao.get(this.user3.getId()));
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void getUserFailure() {
		this.dao.deleteAll();
		assertEquals(0, this.dao.getCount());

		this.dao.get("unknownId");
	}

	private void checkUserCoidentity(UserEntity user1, UserEntity user2) {
		assertEquals(user1.getId(), user2.getId());
		assertEquals(user1.getName(), user2.getName());
		assertEquals(user1.getPassword(), user2.getPassword());
		assertEquals(user1.getLevel(), user2.getLevel());
		assertEquals(user1.getLogin(), user2.getLogin());
		assertEquals(user1.getRecommend(), user2.getRecommend());
	}
}