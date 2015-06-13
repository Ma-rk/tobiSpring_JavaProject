package dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import core.ConnectionMaker;
import core.ConnectionMakerCounting;
import core.DConnectionMaker;

@Configuration
public class DaoFactoryCounting {
	@Bean
	public UserDao userDao() {
		return new UserDao(connectionMaker());
	}

	@Bean
	public ConnectionMaker connectionMaker() {
		return new ConnectionMakerCounting(realConnectionMaker());
	}

	@Bean
	public ConnectionMaker realConnectionMaker() {
		return new DConnectionMaker();
	}
}