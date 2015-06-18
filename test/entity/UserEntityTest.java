package entity;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.Level;

public class UserEntityTest {
	UserEntity user;

	@Before
	public void setUp() {
		user = new UserEntity();
	}

	@Test
	public void upgradeLevel() {
		Level[] levels = Level.values();
		for (Level level : levels) {
			if (level.getNextLevel() == null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertEquals(user.getLevel(), level.getNextLevel());
		}
	}

	@Test(expected = IllegalStateException.class)
	public void cannotUpgradeLevel() {
		Level[] levels = Level.values();
		for (Level level : levels) {
			if (level.getNextLevel() != null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertEquals(user.getLevel(), level.getNextLevel());
		}

	}
}