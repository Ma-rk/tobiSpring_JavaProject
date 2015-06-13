package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entity.UserEntity;

public class UserDao {
	public void add(UserEntity user) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.56.101:3306/dev", "mark", "k");
		PreparedStatement pstmt = conn.prepareStatement("insert into users(id, name, password) values (?,?,?)");

		pstmt.setString(1, user.getId());
		pstmt.setString(2, user.getName());
		pstmt.setString(3, user.getPassword());

		pstmt.executeUpdate();

		pstmt.close();
		conn.close();
	}

	public UserEntity get(String id) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.56.101:3306/dev", "mark", "k");
		PreparedStatement pstmt = conn.prepareStatement("select * from users where id = ?");

		pstmt.setString(1, id);

		ResultSet rs = pstmt.executeQuery();
		rs.next();
		UserEntity user = new UserEntity();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		pstmt.close();
		conn.close();
		
		return user;
	}
}
