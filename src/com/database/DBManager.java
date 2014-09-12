package com.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;
import java.util.Hashtable;

/**
 * 
 * @author konglq
 * @创建日期 2010-8-24 上午01:05:06
 * @功能 实现连接数据库的功能，并提供部分方法，返回List和HashMap
 */
public class DBManager {
	private static final Logger logger = Logger.getLogger(DBManager.class);
	private Connection conn = null;// 数据连接对象
	private Statement stmt = null;
	private ResultSet rs = null;// 结果集

	private DBConnectionManager mg = new DBConnectionManager();// 数据库管理对象

	/**
	 * 我们默认系统数据库是可以正常连接的，所以不抛出错误
	 */
	public DBManager() {
		logger
				.info("创建connection11111111111111111111111111111111111111111111111");
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Can not connect system DB!", e);
		}
	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午01:03:11
	 * @方法名  init void
	 * @功能如下 初始化连接池连接和Statement
	 */
	private void init() {

		try {
			conn = mg.getConnection();
			stmt = conn.createStatement();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午01:02:56
	 * @方法名  executeQuery
	 * @param sql
	 * @return ResultSet
	 * @功能如下 执行查询，返回ResultSet
	 */
	public ResultSet executeQuery(String sql) {
		try {
			if (conn == null) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
			}
			logger.info("执行sql语句如下" + sql);
			rs = stmt.executeQuery(sql);
		} catch (SQLException se) {
			se.printStackTrace();
			logger.error("Error in DBManager.executeQuery(),SQL=\n" + sql);
		}

		return rs;
	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午01:02:42
	 * @方法名  executeUpdate
	 * @param sql
	 * @return int
	 * @功能如下 更新后，默认提交
	 */
	public int executeUpdate(String sql) {
		int iresult = -1;
		iresult = executeUpdate(sql, true);
		return iresult;
	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午01:02:31
	 * @方法名  executeUpdate
	 * @param sql
	 * @param bCommit
	 * @return int
	 * @功能如下 更新，根据bCommit来决定是否直接提交
	 */
	private int executeUpdate(String sql, boolean bCommit) {
		int iresult = -1;
		try {
			if (conn == null) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
			}
			logger.info("执行sql语句如下" + sql);
			iresult = stmt.executeUpdate(sql);
			if (bCommit) {
				conn.commit();
			}
			return iresult;
		} catch (SQLException se) {
			se.printStackTrace();
			logger.error("DBManager.executeUpdate():更新数据出错:\n" + sql, se);
			try {
				conn.rollback();
				return iresult;
			} catch (Exception sqle) {
				return iresult;
			}

		}

	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午01:02:19
	 * @方法名  commit void
	 * @功能如下 提交
	 */
	private void commit() {
		try {
			conn.commit();
		} catch (SQLException se) {
			logger.error("DBManager.commit()出现错误:", se);
		}
	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午01:02:10
	 * @方法名  rollback void
	 * @功能如下 回滚
	 */
	private void rollback() {
		try {
			conn.rollback();
		} catch (SQLException se) {
			logger.error("DBManager.rollback()出现错误:", se);
		}
	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午01:01:58
	 * @方法名  addBatch
	 * @param sql void
	 * @功能如下 加入批处理
	 */
	public void addBatch(String sql) {
		try {

			if (null == conn) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
			}
			//logger.info("加入了批处理sql语句如下==" + sql);
			stmt.addBatch(sql);
		} catch (SQLException se) {
			logger.error("DBManager.addBatch() 出现错误!" + sql, se);
		}
	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午01:01:44
	 * @方法名  executeBatch
	 * @return int[]
	 * @功能如下 执行批处理
	 */
	public int[] executeBatch() {
		int[] intlist = null;
		try {
			if (null == conn) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
				conn.setAutoCommit(false);
			}
			conn.setAutoCommit(false);
			intlist = stmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			return intlist;
		} catch (BatchUpdateException bse) {
			logger.error("至少有一条SQL语句错误", bse);
			return intlist;
		} catch (SQLException se) {
			logger.error("Error in DBManager.executeBatch()!", se);
			return intlist;
		} finally {
			try {
				stmt.clearBatch();
				conn.setAutoCommit(true);
				return intlist;
			} catch (SQLException xe) {
				return intlist;
			}
		}
	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午01:01:27
	 * @方法名  close void
	 * @功能如下 关闭连接
	 */
	public void close() {
		try {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null && !conn.isClosed()) {
				logger
						.info("关闭connection2222222222222222222222222222222222222222222222");
				conn.close();
			}
		} catch (SQLException se) {
			se.printStackTrace();
			logger.error("Error in DBManager.close()!", se);
		}
	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午01:01:11
	 * @方法名  getNextID
	 * @return int
	 * @功能如下 获取唯一id
	 */
	public synchronized int getNextID() {
		String sql = "SELECT SYS_BACK_SQ.NEXTVAL FROM DUAL";

		int max = -1;
		try {

			if (null == conn) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
			}
			logger.info("执行sql如下：" + sql);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				max = rs.getInt(1);
			}
		} catch (Exception se) {
			logger.error("执行sql出现问题，sql如下：" + sql + se);
			return max;
		}

		return max;
	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午12:39:28
	 * @方法名 executeQueryOne
	 * @param sql
	 * @return
	 * @throws SQLException
	 *             Hashtable
	 * @功能如下 执行单条查询，sql语句必须保证只返回一条记录， 用于更新时提取该条记录信息
	 */
	public HashMap executeQueryHashMap(String sql) throws SQLException {
		HashMap hm = new HashMap();
		try {
			logger.info("执行sql如下：" + sql);
			rs = executeQuery(sql);
			// 取得列名,
			ResultSetMetaData rsmd = rs.getMetaData();
			int numCols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= numCols; i++) {
					String key = rsmd.getColumnName(i);
					String value = rs.getString(i);
					if (value == null) {
						value = "";
					}
					hm.put(key, value);
				}
			}
		} catch (SQLException e) {
			logger.error("执行sql出现问题：" + sql + e);
			return null;

		}
		return hm;
	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午12:58:26
	 * @方法名  executeQueryListHashMap
	 * @param sql
	 * @param indexkey 代表一条记录的唯一key
	 * @return
	 * @throws SQLException List
	 * @功能如下 执行数据库查询语句返回向量类型的数据,向量的每一个值是哈希表 
	 * 每个哈希表代表一条记录
	 */
	public Hashtable executeQuerykeyoneListHashMap(String sql, String indexkey)
			throws SQLException {
		ResultSetMetaData rsmd = null;
		Hashtable list = new Hashtable();
		int columnCount = 0;
		try {
			logger.info("执行sql如下：" + sql);
			rs = executeQuery(sql);
			if (rs == null) {
				return null;
			}
			rsmd = rs.getMetaData();
			if (rsmd == null) {
				return null;
			}
			columnCount = rsmd.getColumnCount(); // 得到字段数量
			if (columnCount == 0) {
				return null;
			}

			// 向量的第一个值是列名集合
			String[] keys = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				keys[i - 1] = rsmd.getColumnName(i); // 获得字段名
			}
			// vct.add(keys);
			while (rs.next()) {
				Hashtable hm = new Hashtable();
				hm.clear();
				String key = null;
				for (int i = 1; i <= columnCount; i++) {
					String result = rs.getString(i);
					if ((result == null) || (result.length() == 0)) {
						result = "";
					}

					if (keys[i - 1].equals(indexkey)) {
						key = result;
					}

					hm.put(keys[i - 1], result); // 将每条记录保存到一个哈希表中，key为字段名，result为值
				}
				list.put(key, hm); // 将数据集的每一行插入向量
			}
		} catch (SQLException e) {
			logger.error("执行sql出现问题：" + sql + e);
			return null;
		}
		return list; // 返回SQL语言的查询结果集。
	}

	/**
	 * 
	 * 通过 onekye与twokey组合成一个唯一的key，并把数据库的查询结果
	 * 放到hashtable 中;onekey-towkey 为主键
	 * @author konglq
	 * @创建时间 2010-8-24 上午12:58:26
	 * @方法名  executeQueryListHashMap
	 * @param sql
	 * @param onekey 代表一条记录的唯一key
	 * @param twokey 第二个key
	 * @return
	 * @throws SQLException List
	 * @功能如下 执行数据库查询语句返回向量类型的数据,向量的每一个值是哈希表 
	 * 每个哈希表代表一条记录
	 */
	public Hashtable executeQuerykeytwoListHashMap(String sql, String onekey,
			String twokey) throws SQLException {
		ResultSetMetaData rsmd = null;
		Hashtable list = new Hashtable();
		int columnCount = 0;
		try {
			logger.info("执行sql如下：" + sql);
			rs = executeQuery(sql);
			if (rs == null) {
				return null;
			}
			rsmd = rs.getMetaData();
			if (rsmd == null) {
				return null;
			}
			columnCount = rsmd.getColumnCount(); // 得到字段数量
			if (columnCount == 0) {
				return null;
			}

			// 向量的第一个值是列名集合
			String[] keys = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				keys[i - 1] = rsmd.getColumnName(i); // 获得字段名
			}
			// vct.add(keys);
			while (rs.next()) {
				Hashtable hm = new Hashtable();
				hm.clear();
				String key1 = null;
				String key2=null;
				for (int i = 1; i <= columnCount; i++) {
					String result = rs.getString(i);
					if ((result == null) || (result.length() == 0)) {
						result = "";
					}

					if (keys[i - 1].equals(onekey)) {
						key1 = result;
					}
					
					if (keys[i - 1].equals(twokey)) {
						key2 = result;
					}

					hm.put(keys[i - 1], result); // 将每条记录保存到一个哈希表中，key为字段名，result为值
				}
				list.put(key1+"-"+key2, hm); // 将数据集的每一行插入向量
			}
		} catch (SQLException e) {
			logger.error("执行sql出现问题：" + sql + e);
			return null;
		}
		return list; // 返回SQL语言的查询结果集。
	}

	/**
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午12:58:26
	 * @方法名  executeQueryListHashMap
	 * @param sql
	 * @return
	 * @throws SQLException List
	 * @功能如下 执行数据库查询语句返回向量类型的数据,向量的每一个值是哈希表 
	 * 每个哈希表代表一条记录
	 */
	public List executeQueryListHashMap(String sql) throws SQLException {
		ResultSetMetaData rsmd = null;
		List list = new ArrayList();
		int columnCount = 0;
		try {
			logger.info("执行sql如下：" + sql);
			rs = executeQuery(sql);
			if (rs == null) {
				return null;
			}
			rsmd = rs.getMetaData();
			if (rsmd == null) {
				return null;
			}
			columnCount = rsmd.getColumnCount(); // 得到字段数量
			if (columnCount == 0) {
				return null;
			}

			// 向量的第一个值是列名集合
			String[] keys = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				keys[i - 1] = rsmd.getColumnName(i); // 获得字段名
			}
			// vct.add(keys);
			while (rs.next()) {
				HashMap hm = new HashMap();
				hm.clear();
				for (int i = 1; i <= columnCount; i++) {
					String result = rs.getString(i);
					if ((result == null) || (result.length() == 0)) {
						result = "";
					}
					hm.put(keys[i - 1], result); // 将每条记录保存到一个哈希表中，key为字段名，result为值
				}
				list.add(hm); // 将数据集的每一行插入向量
			}
		} catch (SQLException e) {
			logger.error("执行sql出现问题：" + sql + e);
			return null;
		}
		return list; // 返回SQL语言的查询结果集。
	}

	/**
	 * 
	 * @author konglq
	 * @date 2010-8-26 下午12:43:40
	 * @param list
	 *            list中每个节点是一条sql语句
	 * @return boolean
	 * @Description: TODO批量执行更新类sql语句(insert delete update)
	 */
	public boolean excuteBatchSql(List<String> list) {
		if (list == null) {
			return false;
		}
		try {
			if (!list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					addBatch(list.get(i));
				}
				int[] iResult = null;
				iResult = executeBatch();
				if (iResult != null) {
					String str = iResult.toString();
					if (str.indexOf(-1) >= 0) {
						return false;
					}
				} else {
					return false;
				}

			}
		} catch (Exception e) {
			logger.error("批量执行sql出现问题\n");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	
	
	
	/**
	 * 
	 * @author konglq
	 * @date 2010-8-26 下午12:43:40
	 * @param list
	 *            list中每个节点是一条sql语句
	 * @return boolean
	 * @Description: TODO批量执行更新类sql语句(insert delete update)
	 */
	public boolean excuteBatchSql(Queue<String> list) {
		if (list == null) {
			return false;
		}
		
		try{
			
				if (null == conn) {
					conn = mg.getConnection();	
					stmt = conn.createStatement();
				}
				
				conn.setAutoCommit(false);
				//logger.info("加入了批处理sql语句如下==" + sql);
				
			if(list.size()>0)
			{
				       String sql="";
						while((sql=list.poll())!=null)
						{
							//logger.info(sql);
							stmt.addBatch(sql);
						}
							
						stmt.executeBatch();
						conn.commit();
						conn.close();
						stmt.close();
						
						
						//logger.info("=======完成数据入库========");
			}		
	
				} catch (Exception e) {
					logger.error("批量执行sql出现问题");
					e.printStackTrace();
				}
				
				
				
		return true;
	}

	/**
	 * oracle中获取序列的方法
	 * @author gaoguangfei
	 * @date 2010-9-2 下午02:29:05
	 * @return int
	 * @Description: TODO(特殊用处，userid序列)
	 */
	public synchronized int getNextUserID() {
		String sql = "SELECT SYSUSER_SQ.NEXTVAL FROM DUAL";

		int max = -1;
		try {

			if (null == conn) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
			}
			//SysLogger.info();
			logger.info("执行sql如下：" + sql);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				max = rs.getInt(1);
			}
		} catch (Exception se) {
			//SysLogger.error();
			logger.info("执行sql出现问题，sql如下：" + sql + se);
			return max;
		}

		return max;
	}

	/**
	 * 
	 * 测试方法
	 * 
	 * @param arg
	 */
	public static void main(String arg[]) {

		DBManager db = new DBManager();
		try {
			System.out.println(db.executeQuerykeytwoListHashMap(
					"select * from nms_alarm_indicators_node", "nodeid","name").toString());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
