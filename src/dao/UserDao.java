package dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import entity.UserEntity;

public interface UserDao {
	void setDataSource(DataSource dataSource);

	void add(UserEntity user);

	void deleteAll();

	List<Map<String, Object>> get(String id);

	int getCount();
}