package service;

import static org.junit.Assert.*;
import static service.UserService.MIN_LOGCOUT_FOR_SILVER;
import static service.UserService.MIN_RECCOMMEND_FOR_GOLD;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import code.Level;
import dao.UserDao;
import entity.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService;
	@Autowired
	UserDao userDao;
	@Autowired
	DataSource dataSource;
	@Autowired
	PlatformTransactionManager transactionManager;
	@Autowired
	MailSender mailSender;

	List<UserEntity> usersFixture;

	@Before
	public void setUp() {
		usersFixture = Arrays.asList(new UserEntity("id_1", "user_1", "pwpw", Level.BASIC, MIN_LOGCOUT_FOR_SILVER - 1, 0, "mailmail@mail.com"), new UserEntity(
				"id_2", "user_2", "pwpw", Level.BASIC, MIN_LOGCOUT_FOR_SILVER, 0, "mailmail@mail.com"), new UserEntity("id_3", "user_3", "pwpw", Level.SILVER, 60,
				MIN_RECCOMMEND_FOR_GOLD - 1, "mailmail@mail.com"), new UserEntity("id_4", "user_4", "pwpw", Level.SILVER, 60, MIN_RECCOMMEND_FOR_GOLD,
				"mailmail@mail.com"), new UserEntity("id_5", "user_5", "pwpw", Level.GOLD, 100, Integer.MAX_VALUE, "mailmail@mail.com"));
	}

	@Test
	public void upgradeUserLevelTest() throws Exception {
		prepareDbTestData();

		this.userService.upgradeLevelOfEveryUser();

		checkUserLevel(this.usersFixture.get(0), false);
		checkUserLevel(this.usersFixture.get(1), true);
		checkUserLevel(this.usersFixture.get(2), false);
		checkUserLevel(this.usersFixture.get(3), true);
		checkUserLevel(this.usersFixture.get(4), false);
	}

	@Test
	public void levelHandlingUserAddTest() {
		this.userDao.deleteAll();

		UserEntity userWithLevel = this.usersFixture.get(4);
		UserEntity userWithoutLevel = this.usersFixture.get(0);
		userWithoutLevel.setLevel(null);

		this.userService.add(userWithLevel);
		this.userService.add(userWithoutLevel);

		UserEntity userWithLevelRetrieved = this.userDao.get(userWithLevel.getId());
		UserEntity userWithoutLevelRetrieved = this.userDao.get(userWithoutLevel.getId());

		assertEquals(Level.GOLD, userWithLevelRetrieved.getLevel());
		assertEquals(Level.BASIC, userWithoutLevelRetrieved.getLevel());
	}

	@Test
	public void upgradeAllorNothing() throws Exception {
		UserService testUserService = new TestUserService(this.usersFixture.get(3).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setTransactionManager(this.transactionManager);
		testUserService.setMailSender(this.mailSender);

		prepareDbTestData();

		try {
			testUserService.upgradeLevelOfEveryUser();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
		}
		checkUserLevel(this.usersFixture.get(1), false);
	}

	@Test
	@DirtiesContext
	public void upgradeLevels() throws Exception {
		prepareDbTestData();

		MockMailSender mockMailSender = new MockMailSender();
		this.userService.setMailSender(mockMailSender);

		this.userService.upgradeLevelOfEveryUser();

		checkUserLevel(this.usersFixture.get(0), false);
		checkUserLevel(this.usersFixture.get(1), true);
		checkUserLevel(this.usersFixture.get(2), false);
		checkUserLevel(this.usersFixture.get(3), true);
		checkUserLevel(this.usersFixture.get(4), false);

		List<String> requests = mockMailSender.getRequest();
		assertEquals(requests.size(), 2);
		assertEquals(requests.get(0), this.usersFixture.get(1).getEmail());
		assertEquals(requests.get(1), this.usersFixture.get(3).getEmail());
	}

	private void checkUserLevel(UserEntity user, boolean upgraded) {
		System.out.print("checking user level of user [" + user.getId() + "]: ");
		UserEntity updatedUser = this.userDao.get(user.getId());
		if (upgraded) {
			System.out.println("updated.");
			assertEquals(updatedUser.getLevel(), user.getLevel().getNextLevel());
		} else {
			System.out.println("not updated.");
			assertEquals(updatedUser.getLevel(), user.getLevel());
		}
	}

	private void prepareDbTestData() {
		this.userDao.deleteAll();
		for (UserEntity user : this.usersFixture)
			this.userDao.add(user);
	}

	static class TestUserService extends UserService {
		private String id;

		private TestUserService(String id) {
			this.id = id;
		}

		protected void upgradeLevelOfOneUser(UserEntity user) {
			if (user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevelOfOneUser(user);
		}
	}

	static class TestUserServiceException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
}