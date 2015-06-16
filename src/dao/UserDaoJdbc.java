package dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import code.Level;
import entity.UserEntity;

public class UserDaoJdbc implements UserDao {
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void add(UserEntity user) {
		this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommend) values (?,?,?,?,?,?)", user.getId(), user.getName(),
				user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());
	}

	public void deleteAll() {
		this.jdbcTemplate.update("delete from users");
	}

	public UserEntity get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?", new Object[] { id }, new RowMapper<UserEntity>() {
			public UserEntity mapRow(ResultSet rs, int arg1) throws SQLException {
				return new UserEntity(rs.getString("id"), rs.getString("name"), rs.getString("password"), Level.valueOf(rs.getInt("level")), rs.getInt("login"), rs
						.getInt("recommend"));
			}
		});
	}

	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}
}
