package dbHelper;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHelper {

	
	private static Logger logger = Logger.getLogger(DBHelper.class);
	
	public static boolean runUpdateSql(String sql){
		if (sql == null) {
			//logger.info("sql is null in runUpdateSql.");
			return false;
		}
		Connection connection = ConnectionPool.getInstance().getConnection();
		if (connection == null) {
			//logger.error("connection is null in runUpdateSql.");
			return false;
		}
		int resultInt = -1;
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			resultInt = statement.executeUpdate();// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
			statement.close();
			statement = null;
			if (resultInt == -1) {
				//logger.error("runUpdateSql error:" + sql);
				return false;
			}else {
				//logger.info("runUpdateSql success: " + sql);

				ConnectionPool.getInstance().returnConnection(connection);
				return true;
			}
		} catch (SQLException e) {
			ConnectionPool.getInstance().returnConnection(connection);
			//logger.error(e.getMessage());
			//logger.error("runUpdateSql error:" + sql);
		}
		return false;
	}
	
	@SuppressWarnings("finally")
	public static ResultSet runQuerySql(String sql){
		if (sql == null) {
			//logger.info("sql is null in runQuerySql.");
			return null;
		}
		Connection connection = ConnectionPool.getInstance().getConnection();
		if (connection == null) {
			//logger.error("connection is null in runQuerySql.");
			return null;
		}
		ResultSet rs = null;
		try {
			rs = connection.createStatement().executeQuery(sql);
		} catch (SQLException e) {
			//logger.error(e.getMessage());
			//logger.error("runQuerySql error: " + sql);
		}finally {
			ConnectionPool.getInstance().returnConnection(connection);
			return rs;
		}
	}
	
	
}
