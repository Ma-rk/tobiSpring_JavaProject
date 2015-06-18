package service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import code.Level;
import dao.UserDao;
import entity.UserEntity;

import static service.UserService.MIN_LOGCOUT_FOR_SILVER;
import static service.UserService.MIN_RECCOMMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService;
	@Autowired
	UserDao userDao;

	List<UserEntity> usersFixture;

	@Before
	public void setUp() {
		usersFixture = Arrays.asList(new UserEntity("id_1", "user_1", "pwpw", Level.BASIC, MIN_LOGCOUT_FOR_SILVER - 1, 0), new UserEntity("id_2", "user_2", "pwpw",
				Level.BASIC, MIN_LOGCOUT_FOR_SILVER, 0), new UserEntity("id_3", "user_3", "pwpw", Level.SILVER, 60, MIN_RECCOMMEND_FOR_GOLD - 1), new UserEntity(
				"id_4", "user_4", "pwpw", Level.SILVER, 60, MIN_RECCOMMEND_FOR_GOLD), new UserEntity("id_5", "user_5", "pwpw", Level.GOLD, 100, Integer.MAX_VALUE));
	}

	@Test
	public void upgradeUserLevelTest() {
		userDao.deleteAll();

		for (UserEntity user : usersFixture)
			userDao.add(user);

		userService.upgradeUserLevel();

		checkUserLevel(usersFixture.get(0), false);
		checkUserLevel(usersFixture.get(1), true);
		checkUserLevel(usersFixture.get(2), false);
		checkUserLevel(usersFixture.get(3), true);
		checkUserLevel(usersFixture.get(4), false);
	}

	@Test
	public void levelHandlingUserAddTest() {
		userDao.deleteAll();

		UserEntity userWithLevel = usersFixture.get(4);
		UserEntity userWithoutLevel = usersFixture.get(0);
		userWithoutLevel.setLevel(null);

		userService.add(userWithLevel);
		userService.add(userWithoutLevel);

		UserEntity userWithLevelRetrieved = userDao.get(userWithLevel.getId());
		UserEntity userWithoutLevelRetrieved = userDao.get(userWithoutLevel.getId());

		assertEquals(Level.GOLD, userWithLevelRetrieved.getLevel());
		assertEquals(Level.BASIC, userWithoutLevelRetrieved.getLevel());
	}

	private void checkUserLevel(UserEntity user, boolean upgraded) {
		System.out.print("checking user level of user [" + user.getId() + "]: ");
		UserEntity updatedUser = userDao.get(user.getId());
		if (upgraded) {
			System.out.println("updated.");
			assertEquals(updatedUser.getLevel(), user.getLevel().getNextLevel());
		} else {
			System.out.println("not updated.");
			assertEquals(updatedUser.getLevel(), user.getLevel());
		}
	}
}