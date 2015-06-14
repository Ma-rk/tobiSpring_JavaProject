package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAllStatement implements StatementStrategy {
	public PreparedStatement makePreparedStatement(Connection conn) throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement("delete from users");
		return pstmt;
	}
}