package entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import code.Level;

public class UserEntity {
	private static final Logger logger = LoggerFactory.getLogger(UserEntity.class);

	String id;
	String name;
	String password;
	Level level;
	int login;
	int recommend;
	String email;

	public UserEntity() {
	}

	public UserEntity(String id, String name, String password, Level level, int login, int recommend, String email) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
		this.email = email;
	}

	public void upgradeMyLevel() {
		logger.info("upgradeMyLevel() to target [{}]", this.id);
		Level nextLevel = this.level.getNextLevel();
		if (nextLevel == null) {
			throw new IllegalStateException("can't upgrade user level from this level [" + this.level + "]");
		} else {
			this.level = nextLevel;
			logger.info("level upgrade on [{}] has been executed.", this.id);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}