package dbHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHelper {

	
	public static boolean runUpdateSql(String sql){
		if (sql == null) {
			return false;
		}
		Connection connection = ConnectionPool.getInstance().getConnection();
		if (connection == null) {
			return false;
		}
		int resultInt = -1;
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			resultInt = statement.executeUpdate();// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
			statement.close();
			statement = null;
			if (resultInt == -1) {
				return false;
			}else {

				ConnectionPool.getInstance().returnConnection(connection);
				return true;
			}
		} catch (SQLException e) {
			ConnectionPool.getInstance().returnConnection(connection);
		}
		return false;
	}
	
	@SuppressWarnings("finally")
	public static ResultSet runQuerySql(String sql){
		if (sql == null) {
			return null;
		}
		Connection connection = ConnectionPool.getInstance().getConnection();
		if (connection == null) {
			return null;
		}
		ResultSet rs = null;
		try {
			rs = connection.createStatement().executeQuery(sql);
		} catch (SQLException e) {
		}finally {
			ConnectionPool.getInstance().returnConnection(connection);
			return rs;
		}
	}
	
	
}
