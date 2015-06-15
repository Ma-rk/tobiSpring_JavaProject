package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import entity.UserEntity;

public class UserDao {
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private JdbcContext jdbcContext;

	public void setJdbcContext(JdbcContext jdbcContext) {
		this.jdbcContext = jdbcContext;
	}

	public void add(UserEntity user) throws ClassNotFoundException, SQLException {
		this.jdbcContext.executeSql("insert into users(id, name, password) values (?,?,?)", user.getId(), user.getName(), user.getPassword());
	}

	public void deleteAll() throws SQLException {
		this.jdbcContext.executeSql("delete from users");
	}

	public UserEntity get(String id) throws ClassNotFoundException, SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement("select * from users where id = ?");

		pstmt.setString(1, id);

		ResultSet rs = pstmt.executeQuery();
		UserEntity user = null;
		if (rs.next()) {
			user = new UserEntity();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
		}

		rs.close();
		pstmt.close();
		conn.close();

		if (user == null) throw new EmptyResultDataAccessException(1);

		return user;
	}

	public int getCount() throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement("select count(*) from users");
			rs = pstmt.executeQuery();

			rs.next();
			int numOfUser = rs.getInt(1);
			return numOfUser;

		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}
}