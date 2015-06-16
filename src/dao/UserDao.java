package dao;

import javax.sql.DataSource;

import entity.UserEntity;

public interface UserDao {
	void setDataSource(DataSource dataSource);

	void add(UserEntity user);

	void deleteAll();

	void update(UserEntity user);

	UserEntity get(String id);

	int getCount();
}