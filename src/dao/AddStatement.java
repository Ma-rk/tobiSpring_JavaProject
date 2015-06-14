package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import entity.UserEntity;

public class AddStatement implements StatementStrategy {
	UserEntity user;

	public AddStatement(UserEntity user) {
		this.user = user;
	}

	public PreparedStatement makePreparedStatement(Connection conn) throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement("insert into users(id, name, password) values (?,?,?)");

		pstmt.setString(1, user.getId());
		pstmt.setString(2, user.getName());
		pstmt.setString(3, user.getPassword());

		return pstmt;
	}
}