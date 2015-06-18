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
		usersFixture = Arrays.asList(new UserEntity("id_1", "user_1", "pwpw", Level.BASIC, 49, 0), new UserEntity("id_2", "user_2", "pwpw", Level.BASIC, 50, 0),
				new UserEntity("id_3", "user_3", "pwpw", Level.SILVER, 60, 29), new UserEntity("id_4", "user_4", "pwpw", Level.SILVER, 60, 30), new UserEntity("id_5",
						"user_5", "pwpw", Level.GOLD, 100, 100));
	}

	@Test
	public void upgradeUserLevelTest() {
		userDao.deleteAll();

		for (UserEntity user : usersFixture)
			userDao.add(user);

		userService.upgradeUserLevel();

		checkUserLevel(usersFixture.get(0), Level.BASIC);
		checkUserLevel(usersFixture.get(1), Level.SILVER);
		checkUserLevel(usersFixture.get(2), Level.SILVER);
		checkUserLevel(usersFixture.get(3), Level.GOLD);
		checkUserLevel(usersFixture.get(4), Level.GOLD);
	}

	private void checkUserLevel(UserEntity user, Level expectedLevel) {
		System.out.println("checking user level of user [" + user.getId() + "]");
		UserEntity updatedUser = userDao.get(user.getId());
		System.out.println("expected: [" + expectedLevel + "], actual: [" + updatedUser.getLevel() + "]");
		assertEquals(expectedLevel, updatedUser.getLevel());
	}
}