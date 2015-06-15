package dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import code.Level;
import entity.UserEntity;

public class UserDaoJdbc implements UserDao {
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void add(UserEntity user) {
		this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommend) values (?,?,?,?,?,?)", user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());
	}

	public void deleteAll() {
		this.jdbcTemplate.update("delete from users");
	}

	public List<Map<String, Object>> get(String id) {
		return this.jdbcTemplate.queryForList("select * from users where id = ?", id);
	}

	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}
}
