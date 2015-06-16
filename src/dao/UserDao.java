package dao;

import javax.sql.DataSource;

import entity.UserEntity;

public interface UserDao {
	void setDataSource(DataSource dataSource);

	void add(UserEntity user);

	void deleteAll();

	UserEntity get(String id);

	int getCount();
}