package service;

import static org.junit.Assert.*;
import static service.UserServiceImpl.MIN_LOGCOUT_FOR_SILVER;
import static service.UserServiceImpl.MIN_RECCOMMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
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
	@Autowired
	UserServiceImpl userServiceImpl;

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
		logger.info("==>upgradeUserLevelTest()");
		prepareDbTestData();

		this.userService.upgradeLevelOfEveryUser();

		checkUserLevel(this.usersFixture.get(0), false);
		checkUserLevel(this.usersFixture.get(1), true);
		checkUserLevel(this.usersFixture.get(2), false);
		checkUserLevel(this.usersFixture.get(3), true);
		checkUserLevel(this.usersFixture.get(4), false);
		logger.info("<==upgradeUserLevelTest()\n");
	}

	@Test
	public void levelHandlingUserAddTest() {
		logger.info("==>levelHandlingUserAddTest()");
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
		logger.info("<==levelHandlingUserAddTest()\n");
	}

	@Test
	public void upgradeAllorNothing() throws Exception {
		logger.info("==>upgradeAllorNothing()");
		TestUserService testUserService = new TestUserService(this.usersFixture.get(3).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setMailSender(this.mailSender);

		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(this.transactionManager);
		txUserService.setUserService(testUserService);

		prepareDbTestData();

		try {
			txUserService.upgradeLevelOfEveryUser();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
			logger.info("catched TestUserServiceException() on [{}]", this.toString());
		}
		checkUserLevel(this.usersFixture.get(1), false);
		logger.info("<==upgradeAllorNothing()\n");
	}

	@Test
	@DirtiesContext
	public void upgradeLevelsTest() throws Exception {
		logger.info("==>upgradeLevelsTest()");
		prepareDbTestData();

		UserServiceImpl userServiceImpl = new UserServiceImpl();

		MockUserDao mockUserDao = new MockUserDao(this.usersFixture);
		MockMailSender mockMailSender = new MockMailSender();

		userServiceImpl.setUserDao(mockUserDao);
		userServiceImpl.setMailSender(mockMailSender);

		userServiceImpl.upgradeLevelOfEveryUser();

		List<UserEntity> updatedUsers = mockUserDao.getUpdated();
		assertEquals(2, updatedUsers.size());
		checkUserAndLevel(updatedUsers.get(0), "id_2", Level.SILVER);
		checkUserAndLevel(updatedUsers.get(1), "id_4", Level.GOLD);

		List<String> requests = mockMailSender.getRequest();
		assertEquals(requests.size(), 2);
		assertEquals(requests.get(0), this.usersFixture.get(1).getEmail());
		assertEquals(requests.get(1), this.usersFixture.get(3).getEmail());
		logger.info("<==upgradeLevelsTest()\n");
	}

	private void checkUserLevel(UserEntity user, boolean upgraded) {
		logger.info("checkUserLevel(user) user id: [{}]", user.getId());
		UserEntity updatedUser = this.userDao.get(user.getId());
		if (upgraded) {
			logger.info("level of [{}] was updated.", user.getId());
			assertEquals(updatedUser.getLevel(), user.getLevel().getNextLevel());
		} else {
			logger.info("level of [{}] was not updated.", user.getId());
			assertEquals(updatedUser.getLevel(), user.getLevel());
		}
	}

	private void checkUserAndLevel(UserEntity updatedUser, String expectedId, Level expectedLevel) {
		assertEquals(expectedId, updatedUser.getId());
		assertEquals(expectedLevel, updatedUser.getLevel());
	}

	static class TestUserService extends UserServiceImpl {
		private String id;

		private TestUserService(String id) {
			this.id = id;
		}

		protected void upgradeLevelOfOneUser(UserEntity user) {
			logger.info("upgradeLevelOfOneUser(UserEntity)====>");
			if (user.getId().equals(this.id)) {
				logger.info("throwed TestUserServiceException() on [" + this.toString() + "]");
				throw new TestUserServiceException();
			}
			super.upgradeLevelOfOneUser(user);
			logger.info("upgradeLevelOfOneUser(UserEntity)<====");
		}
	}

	static class TestUserServiceException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	static class MockUserDao implements UserDao {
		private List<UserEntity> users;
		private List<UserEntity> updated = new ArrayList<UserEntity>();

		private MockUserDao(List<UserEntity> users) {
			this.users = users;
		}

		public List<UserEntity> getUpdated() {
			return this.updated;
		}

		// mock method
		public void update(UserEntity user) {
			updated.add(user);
		}

		// stub method
		public List<UserEntity> getAll() {
			return this.users;
		}

		/*
		 * test does not use these methods
		 */
		public UserEntity get(String id) {
			throw new UnsupportedOperationException();
		}

		public void setDataSource(DataSource dataSource) {
			throw new UnsupportedOperationException();
		}

		public void add(UserEntity user) {
			throw new UnsupportedOperationException();
		}

		public void deleteAll() {
			throw new UnsupportedOperationException();
		}

		public int getCount() {
			throw new UnsupportedOperationException();
		}
	}

	private void prepareDbTestData() {
		this.userDao.deleteAll();
		for (UserEntity user : this.usersFixture)
			this.userDao.add(user);
	}
}