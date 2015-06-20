package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import code.Level;
import entity.UserEntity;

public class UserDaoJdbc implements UserDao {
	/*
	 * DI codes
	 */
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/*
	 * functional methods
	 */
	public void add(UserEntity user) {
		this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommend, email) values (?,?,?,?,?,?,?)", user.getId(), user.getName(),
				user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
	}

	public void deleteAll() {
		this.jdbcTemplate.update("delete from users");
	}

	public void update(UserEntity user) {
		this.jdbcTemplate.update("update users set name=?, password=?, level=?, login=?, recommend=?, email=? where id=?", user.getName(), user.getPassword(), user
				.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());
	}

	public UserEntity get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?", new Object[] { id }, this.userMapper);
	}

	public List<UserEntity> getAll() {
		return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
	}

	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}

	/*
	 * support methods
	 */
	RowMapper<UserEntity> userMapper = new RowMapper<UserEntity>() {
		public UserEntity mapRow(ResultSet rs, int arg1) throws SQLException {
			return new UserEntity(rs.getString("id"), rs.getString("name"), rs.getString("password"), Level.valueOf(rs.getInt("level")), rs.getInt("login"),
					rs.getInt("recommend"), rs.getString("email"));
		}
	};
}