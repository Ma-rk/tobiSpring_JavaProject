package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public class JdbcContext {
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void workWithStatementStrategy(StatementStrategy stmtSt) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = dataSource.getConnection();

			pstmt = stmtSt.makePreparedStatement(conn);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
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

	public void executeSql(String qry, String... params) throws SQLException {
		workWithStatementStrategy(new StatementStrategy() {
			public PreparedStatement makePreparedStatement(Connection conn) throws SQLException {
				PreparedStatement pstmt = conn.prepareStatement(qry);
				for (int idx = 0; idx < params.length; idx++) {
					pstmt.setString(idx + 1, params[idx]);
				}
				return pstmt;
			}
		});
	}
}