/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;


import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleLockInfo;
import com.afunms.application.model.OracleTopSqlReadWrite;
import com.afunms.application.model.OracleTopSqlSort;
import com.afunms.application.model.Oracle_sessiondata;
import com.afunms.application.model.Sqlserver_processdata;
import com.afunms.application.model.SybaseVO;
import com.afunms.application.model.TablesVO;
import com.afunms.application.util.DbConversionUtil;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DB2JdbcUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.InformixJdbcUtil;
import com.afunms.common.util.JdbcUtil;
import com.afunms.common.util.MySqlJdbcUtil;
import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.PersistenceService;
import com.afunms.common.util.PersistenceServiceable;
import com.afunms.common.util.ReflactUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SysbaseJdbcUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.EventListDao;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Task;
import com.afunms.polling.om.oraclerac.Raccrstatuscollectdata;
import com.afunms.polling.om.oraclerac.Raccrstatusdata;
import com.afunms.polling.om.oraclerac.Raclistenerstatuscollectdata;
import com.afunms.polling.om.oraclerac.Racstatuscollectdata;
import com.afunms.polling.task.TaskXml;

import edu.emory.mathcs.backport.java.util.Arrays;

public class DBDao extends BaseDao implements DaoInterface {
	/* 缓存管理统计 */
	private double bufferCacheHitRatio; // *$ 缓存击中率
	private double planCacheHitRatio; // plan Cache 命中率
	private double cursorManagerByTypeHitRatio; // cursor manager 命中率
	private double catalogMetadataHitRatio; // catalogMetadata命中率
	private double dbPages; // * 数据库页
	private double totalPageLookups; // 查找页数
	private double totalPageLookupsRate; // * 查找页数/分
	private double totalPageReads; // 已读页数
	private double totalPageReadsRate; // * 已读页数/分
	private double totalPageWrites; // 已写页数
	private double totalPageWritesRate; // * 已写页数/分
	private double totalPages; // * 总页数
	private double freePages; // * 空闲页

	/* 数据库页连接统计 */
	private double connections; // * 活动连接
	private double totalLogins; // 登录
	private double totalLoginsRate; // * 登录/分
	private double totalLogouts; // 退出
	private double totalLogoutsRate; // * 退出/分

	/* 锁明细 */
	private double lockRequests; // 锁定请求
	private double lockRequestsRate; // *$ 锁定请求/分
	private double lockWaits; // 锁定等待
	private double lockWaitsRate; // * 锁定等待/分
	private double lockTimeouts; // 锁定超时
	private double lockTimeoutsRate; // * 锁定超时/分
	private double deadLocks; // 死锁数
	private double deadLocksRate; // * 死锁数/分
	private double avgWaitTime; // * 平均锁定等待时间
	private double avgWaitTimeBase; // 平均锁定等待时间计算的基数

	/* 闭锁的明细 */
	private double latchWaits; // 锁定等待
	private double latchWaitsRate; // * 锁定等待/分
	private double avgLatchWait; // *$ 平均闭锁等待时间;

	/* 缓存明细 */
	private double cacheHitRatio; // *$ 缓存击中率
	private double cacheHitRatioBase; // 缓存击中率计算的基数
	private double cacheCount; // * 缓存数
	private double cachePages; // * 缓存页
	private double cacheUsed; // 使用的缓存
	private double cacheUsedRate; // * 使用的缓存/分

	/* 内存利用情况 */
	private double totalMemory; // *$ 内存总数
	private double sqlMem; // *$ SQL缓存存储
	private double optMemory; // *$ 内存优化
	private double memGrantPending; // * 内存分配未决
	private double memGrantSuccess; // * 内存分配成功
	private double lockMem; // *$ 锁定内存
	private double conMemory; // *$ 连接内存
	private double grantedWorkspaceMem; // * 产生的工作空间内存

	/* SQL统计 */
	private double batchRequests; // 批请求
	private double batchRequestsRate; // *$ 批请求/分
	private double sqlCompilations;
	// SQL编辑
	private double sqlCompilationsRate; // * SQL编辑/分
	private double sqlRecompilation; // SQL重编辑
	private double sqlRecompilationRate; // * SQL重编辑/分
	private double autoParams; // 自动参数化尝试次数
	private double autoParamsRate; // * 自动参数化尝试次数/分
	private double failedAutoParams; // 自动参数化失败次数
	private double failedAutoParamsRate; // * 自动参数化失败次数/分

	/* 访问方法的明细 */
	private double fullScans; // 完全扫描
	private double fullScansRate; // *$ 完全扫描/分
	private double rangeScans; // 范围扫描
	private double rangeScansRate; // * 范围扫描/分
	private double probeScans; // 探针扫描
	private double probeScansRate; // * 探针扫描/分

	// 表锁情况

	private String Table_locks_immediate;
	private String Table_locks_waited;

	private String Key_read_requests;
	private String Key_reads;

	// 线程
	private String Threads_cached;
	private String Threads_connected;
	private String Threads_created;
	private String Threads_running;

	private Vector databaseNames; // 数据库系统中所有数据库的名称
	private HashMap dbNamesAndDetails; // key为数据库的名称(String),value为数据库详细信息(MsSqlServerDatabase)

	public DBDao() {
		super("app_db_node");
	}

	public List getDbByTypeMonFlag(int dbtypeid, int flag) {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from app_db_node where managed= " + flag
				+ " and dbtype =" + dbtypeid);
		// SysLogger.info(sql.toString());
		return findByCriteria(sql.toString());
	}

	/*------modify  by zhao -------------------------------------*/
	public List getDbByType(int dbtypeid) {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from app_db_node where dbtype =" + dbtypeid);
		// SysLogger.info(sql.toString());
		return findByCriteria(sql.toString());

	}

	/*--------------------------------------------------------------*/
	public List getDbByMonFlag(int flag) {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		if (flag == -1) {
			sql.append("select * from app_db_node ");
		} else {
			sql.append("select * from app_db_node where managed= " + flag);
		}
		// sql.append("select * from app_db_node where managed= "+flag);
		return findByCriteria(sql.toString());
	}

	public List getDbByTypeAndIpaddress(int dbtypeid, String ipaddress) {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from app_db_node where ip_address= '" + ipaddress
				+ "' and dbtype =" + dbtypeid);
		return findByCriteria(sql.toString());
	}

	public List getDbByBID(String businessid) {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		StringBuffer s = new StringBuffer();

		int flag = 0;
		if (businessid != null) {
			if (businessid != "-1") {
				String[] bids = businessid.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								// s.append(" and ( bid like '%," +
								// bids[i].trim() + ",%' ");
								s.append(" and ( bid like '%" + bids[i].trim()
										+ "%' ");
								flag = 1;
							} else {
								// flag = 1;
								// s.append(" or bid like '%," + bids[i].trim()
								// + ",%' ");
								s.append(" or bid like '%" + bids[i].trim()
										+ "%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}

		sql.append("select * from app_db_node where 1=1 " + s.toString());
		// //System.out.println("##############################"+sql);
		// SysLogger.info(sql.toString());
		return findByCriteria(sql.toString());
	}

	/**
	 * @date 2011-4-19
	 * @author wxy add
	 * @按照业务及类型查询节点
	 * @return
	 */
	public List getDbNodeByBID(int dbtype, String businessid) {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		StringBuffer s = new StringBuffer();

		int flag = 0;
		if (businessid != null) {
			if (businessid != "-1") {
				String[] bids = businessid.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								// s.append(" and ( bid like '%," +
								// bids[i].trim() + ",%' ");
								s.append(" and ( bid like '%" + bids[i].trim()
										+ "%' ");
								flag = 1;
							} else {
								// flag = 1;
								// s.append(" or bid like '%," + bids[i].trim()
								// + ",%' ");
								s.append(" or bid like '%" + bids[i].trim()
										+ "%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		if (dbtype == 0 || dbtype == 10) {
			sql.append("select * from app_db_node where 1=1 " + s.toString());
		} else {
			sql.append("select * from app_db_node where 1=1 " + s.toString()
					+ " and dbtype=" + dbtype);
		}
		return findByCriteria(sql.toString());
	}

	public List getDbByBIDUnderCurrentUser(String businessid, String currentid) {

		List rlist = new ArrayList();
		StringBuffer sql1 = new StringBuffer();
		StringBuffer s1 = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer s2 = new StringBuffer();
		int flag = 0;

		if (businessid != null) {
			if (businessid != "-1") {
				String[] bids = businessid.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								// s.append(" and ( bid like '%," +
								// bids[i].trim() + ",%' ");
								s2.append(" and ( bid like '%" + bids[i].trim()
										+ "%' ");
								flag = 1;
							} else {
								// flag = 1;
								// s.append(" or bid like '%," + bids[i].trim()
								// + ",%' ");
								s2.append(" or bid like '%" + bids[i].trim()
										+ "%' ");
							}
						}
					}
					s2.append(") ");
				}

			}
		}
		sql2.append("select * from app_db_node where 1=1 " + s2.toString());

		flag = 0;
		if (currentid != null) {
			// //System.out.println("%%%%%%%%%%%%%%%%%%"+currentid);
			if (currentid != "-1") {
				String[] bids = currentid.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								// s.append(" and ( bid like '%," +
								// bids[i].trim() + ",%' ");
								s1.append(" and ( bid like '%" + bids[i].trim()
										+ "%' ");
								flag = 1;
							} else {
								// flag = 1;
								// s.append(" or bid like '%," + bids[i].trim()
								// + ",%' ");
								s1.append(" or bid like '%" + bids[i].trim()
										+ "%' ");
							}
						}
					}
					s1.append(") ");
				}

			}
		}
		sql1.append("select * from (" + sql2.toString() + ") as t where 1=1 "
				+ s1.toString());
		// sql1.append(sql2);
		// sql1.append("select * from app_db_node where 1=1 " + s1.toString());
		// //System.out.println("##############################"+sql1);
		// SysLogger.info(sql.toString());
		return findByCriteria(sql1.toString());
	}

	public List getDbByTypeAndBID(int dbtype, Vector bids) {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		String fstr = " where dbtype=" + dbtype;
		String wstr = "";
		if (bids != null && bids.size() > 0) {
			for (int i = 0; i < bids.size(); i++) {
				if (wstr.trim().length() == 0) {
					wstr = wstr + " and ( bid like '%," + bids.get(i) + ",%' ";
				} else {
					wstr = wstr + " or bid like '%," + bids.get(i) + ",%' ";
				}

			}
			wstr = wstr + ")";
		}
		sql.append("select * from app_db_node " + fstr + wstr);
		// SysLogger.info(sql.toString());
		return findByCriteria(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		boolean flag = true;
		DBVo vo = (DBVo) baseVo;
		DBVo pvo = (DBVo) findByID(vo.getId() + "");
		DBTypeDao typedao = new DBTypeDao();
		DBTypeVo typevo = (DBTypeVo) typedao.findByID(pvo.getDbtype() + "");
		StringBuffer sql = new StringBuffer();
		sql.append("update app_db_node set alias='");
		sql.append(vo.getAlias());
		sql.append("',ip_address='");
		sql.append(vo.getIpAddress());
		sql.append("',ip_long=");
		sql.append(NetworkUtil.ip2long(vo.getIpAddress()));
		sql.append(",port='");
		sql.append(vo.getPort());
		sql.append("',users='");
		sql.append(vo.getUser());
		sql.append("',password='");
		sql.append(vo.getPassword());
		sql.append("',db_name='");
		sql.append(vo.getDbName());

		sql.append("',managed=");
		sql.append(vo.getManaged());
		sql.append(",dbuse='");
		sql.append(vo.getDbuse());
		sql.append("',sendmobiles='");
		sql.append(vo.getSendmobiles());
		sql.append("',sendemail='");
		sql.append(vo.getSendemail());
		sql.append("',sendphone='");
		sql.append(vo.getSendphone());
		sql.append("',bid='");
		sql.append(vo.getBid());
		sql.append("',dbtype=");
		sql.append(vo.getDbtype());
		sql.append(",collecttype=");
		sql.append(vo.getCollecttype());
		sql.append(",supperid=");// snow add 2010-05-20
		sql.append(vo.getSupperid());
		sql.append(" where id=");
		sql.append(vo.getId());
		try {
			saveOrUpdate(sql.toString());
			// 若IP地址发生改变,先把表删除，然后在重新建立
			if (!vo.getIpAddress().equals(pvo.getIpAddress())) {
				// 修改了IP
				// 若IP地址发生改变,先把表删除，然后在重新建立
				String ipstr = pvo.getIpAddress();
				// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
				// String[] ipdot = ipstr.split(".");
				// String tempStr = "";
				String allipstr = "";
				// if (ipstr.indexOf(".") > 0) {
				// ip1 = ipstr.substring(0, ipstr.indexOf("."));
				// ip4 = ipstr.substring(ipstr.lastIndexOf(".") + 1,
				// ipstr.length());
				// tempStr = ipstr.substring(ipstr.indexOf(".") + 1,
				// ipstr.lastIndexOf("."));
				// }
				// ip2 = tempStr.substring(0, tempStr.indexOf("."));
				// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
				// tempStr.length());
				// allipstr = ip1 + ip2 + ip3 + ip4;
				//allipstr = SysUtil.doip(ipstr);
				allipstr = vo.getId()+"";

				CreateTableManager ctable = new CreateTableManager();
				if (typevo.getDbtype().equalsIgnoreCase("sqlserver")) {
					conn = new DBManager();
					ctable.deleteTable(conn, "sqlping", allipstr, "sqlping");// Ping
					conn = new DBManager();
					ctable.deleteTable(conn, "sqlpinghour", allipstr,
							"sqlpinghour");// Ping
					conn = new DBManager();
					ctable.deleteTable(conn, "sqlpingday", allipstr,
							"sqlpingday");// Ping

				} else if (typevo.getDbtype().equalsIgnoreCase("oracle")) {
					conn = new DBManager();
					ctable.deleteTable(conn, "oraping", allipstr, "oraping");// Ping
					conn = new DBManager();
					ctable.deleteTable(conn, "orapinghour", allipstr,
							"orapinghour");// Ping
					conn = new DBManager();
					ctable.deleteTable(conn, "orapingday", allipstr,
							"orapingday");// Ping
				} else if (typevo.getDbtype().equalsIgnoreCase("db2")) {
					conn = new DBManager();
					ctable.deleteTable(conn, "db2ping", allipstr, "db2ping");// Ping
					conn = new DBManager();
					ctable.deleteTable(conn, "db2pinghour", allipstr,
							"db2pinghour");// Ping
					conn = new DBManager();
					ctable.deleteTable(conn, "db2pingday", allipstr,
							"db2pingday");// Ping
				} else if (typevo.getDbtype().equalsIgnoreCase("sysbase")) {
					conn = new DBManager();
					ctable.deleteTable(conn, "sysping", allipstr, "sysping");// Ping
					conn = new DBManager();
					ctable.deleteTable(conn, "syspinghour", allipstr,
							"syspinghour");// Ping
					conn = new DBManager();
					ctable.deleteTable(conn, "syspingday", allipstr,
							"syspingday");// Ping
				} else if (typevo.getDbtype().equalsIgnoreCase("mysql")) {
					conn = new DBManager();
					ctable.deleteTable(conn, "myping", allipstr, "myping");// Ping
					conn = new DBManager();
					ctable.deleteTable(conn, "mypinghour", allipstr,
							"mypinghour");// Ping
					conn = new DBManager();
					ctable
							.deleteTable(conn, "mypingday", allipstr,
									"mypingday");// Ping
				} else if (typevo.getDbtype().equalsIgnoreCase("informix")) {
					conn = new DBManager();
					ctable.deleteTable(conn, "informixping", allipstr,
							"informixping");// Ping
					conn = new DBManager();
					ctable.deleteTable(conn, "informixpinghour", allipstr,
							"informixpinghour");// Ping
					conn = new DBManager();
					ctable.deleteTable(conn, "informixpingday", allipstr,
							"informixpingday");// Ping
					ctable.deleteTable(conn, "informixlog", allipstr,
							"informixlog");// 日志信息
				}

				// 测试生成表
				String ip = vo.getIpAddress();
				// ip1 = "";
				// ip2 = "";
				// ip3 = "";
				// ip4 = "";
				// ipdot = ip.split(".");
				// tempStr = "";
				// allipstr = "";
				// if (ip.indexOf(".") > 0) {
				// ip1 = ip.substring(0, ip.indexOf("."));
				// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
				// tempStr = ip.substring(ip.indexOf(".") + 1,
				// ip.lastIndexOf("."));
				// }
				// ip2 = tempStr.substring(0, tempStr.indexOf("."));
				// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
				// tempStr.length());
				// allipstr = ip1 + ip2 + ip3 + ip4;
				//allipstr = SysUtil.doip(ip);

				ctable = new CreateTableManager();

				if (typevo.getDbtype().equalsIgnoreCase("sqlserver")) {
					conn = new DBManager();
					ctable.createTable(conn, "sqlping", allipstr, "sqlping");// Ping
					conn = new DBManager();
					ctable.createTable(conn, "sqlpinghour", allipstr,
							"sqlpinghour");// Ping
					conn = new DBManager();
					ctable.createTable(conn, "sqlpingday", allipstr,
							"sqlpingday");// Ping
				} else if (typevo.getDbtype().equalsIgnoreCase("oracle")) {
					conn = new DBManager();
					ctable.createTable(conn, "oraping", allipstr, "oraping");// Ping
					conn = new DBManager();
					ctable.createTable(conn, "orapinghour", allipstr,
							"orapinghour");// Ping
					conn = new DBManager();
					ctable.createTable(conn, "orapingday", allipstr,
							"orapingday");// Ping
				} else if (typevo.getDbtype().equalsIgnoreCase("db2")) {
					conn = new DBManager();
					ctable.createTable(conn, "db2ping", allipstr, "db2ping");// Ping
					conn = new DBManager();
					ctable.createTable(conn, "db2pinghour", allipstr,
							"db2pinghour");// Ping
					conn = new DBManager();
					ctable.createTable(conn, "db2pingday", allipstr,
							"db2pingday");// Ping
				} else if (typevo.getDbtype().equalsIgnoreCase("sybase")) {
					conn = new DBManager();
					ctable.createTable(conn, "sysping", allipstr, "sysping");// Ping
					conn = new DBManager();
					ctable.createTable(conn, "syspinghour", allipstr,
							"syspinghour");// Ping
					conn = new DBManager();
					ctable.createTable(conn, "syspingday", allipstr,
							"syspingday");// Ping
				} else if (typevo.getDbtype().equalsIgnoreCase("mysql")) {
					conn = new DBManager();
					ctable.createTable(conn, "myping", allipstr, "myping");// Ping
					conn = new DBManager();
					ctable.createTable(conn, "mypinghour", allipstr,
							"mypinghour");// Ping
					conn = new DBManager();
					ctable
							.createTable(conn, "mypingday", allipstr,
									"mypingday");// Ping
				} else if (typevo.getDbtype().equalsIgnoreCase("informix")) {
					conn = new DBManager();
					ctable.createTable(conn, "informixping", allipstr,
							"informixping");// Ping
					conn = new DBManager();
					ctable.createTable(conn, "informixpinghour", allipstr,
							"informixpinghour");// Ping
					conn = new DBManager();
					ctable.createTable(conn, "informixpingday", allipstr,
							"informixpingday");// Ping
					conn = new DBManager();
					ctable.createInformixLogTable(conn, "informixlog",
							allipstr, "informixlog");// 存放日志信息

				}

			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	public boolean save(BaseVo baseVo) {
		boolean flag = true;
		DBVo vo = (DBVo) baseVo;
		StringBuffer sql = new StringBuffer();
		sql
				.append("insert into app_db_node(id,alias,ip_address,ip_long,port,users,password,category,db_name,dbuse,sendmobiles,sendemail,sendphone,bid,managed,dbtype,collecttype,supperid)values(");
		sql.append(vo.getId());
		sql.append(",'");
		sql.append(vo.getAlias());
		sql.append("','");
		sql.append(vo.getIpAddress());
		sql.append("',");
		sql.append(NetworkUtil.ip2long(vo.getIpAddress()));
		sql.append(",'");
		sql.append(vo.getPort());
		sql.append("','");
		sql.append(vo.getUser());
		sql.append("','");
		sql.append(vo.getPassword());
		sql.append("',");
		sql.append(vo.getCategory());
		sql.append(",'");
		sql.append(vo.getDbName());
		sql.append("','");
		sql.append(vo.getDbuse());
		sql.append("','");
		sql.append(vo.getSendmobiles());
		sql.append("','");
		sql.append(vo.getSendemail());
		sql.append("','");
		sql.append(vo.getSendphone());
		sql.append("','");
		sql.append(vo.getBid());
		sql.append("',");
		sql.append(vo.getManaged());
		sql.append(",");
		sql.append(vo.getDbtype());
		sql.append(",");
		sql.append(vo.getCollecttype());
		sql.append(",");
		sql.append(vo.getSupperid());
		sql.append(")");

		try {
			saveOrUpdate(sql.toString());
			CreateTableManager ctable = new CreateTableManager();
			// 测试生成表
			String ip = vo.getIpAddress();
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {

			} finally {
				typedao.close();
			}
			// DBManager conn = new DBManager();
			// SysLogger.info(ip+"======================================="+typevo.getDbtype());
			if (typevo.getDbtype().equalsIgnoreCase("sqlserver")) {
				conn = new DBManager();
				try {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.createTable(conn, "sqlping", vo.getId() + "",
								"sqlping");// Ping
						ctable.createTable(conn, "sqlpinghour",
								vo.getId() + "", "sqlpinghour");// Ping小时数据
						ctable.createTable(conn, "sqlpingday", vo.getId() + "",
								"sqlpingday");// Ping天数据
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.createTable(conn, "sqlping", vo.getId() + "",
								"sqlping");// Ping
						ctable.createSeq(conn, "sqlping", vo.getId() + "");// 生成序列
						ctable.createTriger(conn, "sqlping", vo.getId() + "",
								"sqlping");// 生成触发器
						ctable.createTable(conn, "sqlpinghour",
								vo.getId() + "", "sqlpinghour");// Ping
						ctable.createSeq(conn, "sqlpinghour", vo.getId() + "");// 生成序列
						ctable.createTriger(conn, "sqlpinghour", vo.getId()
								+ "", "sqlpinghour");// 生成触发器
						ctable.createTable(conn, "sqlpingday", vo.getId() + "",
								"sqlpingday");// Ping
						ctable.createSeq(conn, "sqlpingday", vo.getId() + "");// 生成序列
						ctable.createTriger(conn, "sqlpingday",
								vo.getId() + "", "sqlpingday");// 生成触发器
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						conn.executeBatch();
					} catch (Exception e) {

					}
					conn.close();
				}

			} else if (typevo.getDbtype().equalsIgnoreCase("oracle")  || typevo.getDbtype().equalsIgnoreCase("oraclerac")) {
				conn = new DBManager();
				try {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.createTable(conn, "oraping", vo.getId() + "",
								"oraping");// Ping
						ctable.createTable(conn, "orapinghour",
								vo.getId() + "", "orapinghour");// Ping
						ctable.createTable(conn, "orapingday", vo.getId() + "",
								"orapingday");// Ping
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.createTable(conn, "oraping", vo.getId() + "",
								"oraping");// Ping
						// ctable.createSeq(conn, "oraping", allipstr);//生成序列
						// ctable.createTriger(conn, "oraping", allipstr,
						// "oraping");//生成触发器

						ctable.createTable(conn, "orapinghour",
								vo.getId() + "", "orapinghour");// Ping
						// ctable.createSeq(conn, "orapinghour",
						// allipstr);//生成序列
						// ctable.createTriger(conn, "orapinghour", allipstr,
						// "orapinghour");//生成触发器

						ctable.createTable(conn, "orapingday", vo.getId() + "",
								"orapingday");// Ping
						// ctable.createSeq(conn, "orapingday", allipstr);//生成序列
						// ctable.createTriger(conn, "orapingday", allipstr,
						// "orapingday");//生成触发器
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						conn.executeBatch();
					} catch (Exception e) {

					}
					conn.close();
				}

			} else if (typevo.getDbtype().equalsIgnoreCase("db2")) {
				conn = new DBManager();
				try {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.createTable(conn, "db2ping", vo.getId() + "",
								"db2ping");// Ping
						ctable.createTable(conn, "db2pinghour",
								vo.getId() + "", "db2pinghour");// Ping
						ctable.createTable(conn, "db2pingday", vo.getId() + "",
								"db2pingday");// Ping
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.createTable(conn, "db2ping", vo.getId() + "",
								"db2ping");// Ping
						// ctable.createSeq(conn, "db2ping",
						// vo.getId()+"");//生成序列
						// ctable.createTriger(conn, "db2ping", vo.getId()+"",
						// "db2ping");//生成触发器

						ctable.createTable(conn, "db2pinghour",
								vo.getId() + "", "db2pinghour");// Ping
						// ctable.createSeq(conn, "db2pinghour",
						// vo.getId()+"");//生成序列
						// ctable.createTriger(conn, "db2pinghour",
						// vo.getId()+"", "db2pinghour");//生成触发器

						ctable.createTable(conn, "db2pingday", vo.getId() + "",
								"db2pingday");// Ping
						// ctable.createSeq(conn, "db2pingday", allipstr);//生成序列
						// ctable.createTriger(conn, "db2pingday", allipstr,
						// "db2pingday");//生成触发器
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						conn.executeBatch();
					} catch (Exception e) {

					}
					conn.close();
				}

			} else if (typevo.getDbtype().equalsIgnoreCase("sybase")) {
				conn = new DBManager();
				try {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.createTable(conn, "sysping", vo.getId() + "",
								"sysping");// Ping
						ctable.createTable(conn, "syspinghour",
								vo.getId() + "", "syspinghour");// Ping
						ctable.createTable(conn, "syspingday", vo.getId() + "",
								"syspingday");// Ping
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.createTable(conn, "sysping", vo.getId() + "",
								"sysping");// Ping
						// ctable.createSeq(conn, "sysping", allipstr);//生成序列
						// ctable.createTriger(conn, "sysping", allipstr,
						// "sysping");//生成触发器

						ctable.createTable(conn, "syspinghour",
								vo.getId() + "", "syspinghour");// Ping
						// ctable.createSeq(conn, "syspinghour",
						// allipstr);//生成序列
						// ctable.createTriger(conn, "syspinghour", allipstr,
						// "syspinghour");//生成触发器

						ctable.createTable(conn, "syspingday", vo.getId() + "",
								"syspingday");// Ping
						// ctable.createSeq(conn, "syspingday", allipstr);//生成序列
						// ctable.createTriger(conn, "syspingday", allipstr,
						// "syspingday");//生成触发器
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						conn.executeBatch();
					} catch (Exception e) {

					}
					conn.close();
				}

			} else if (typevo.getDbtype().equalsIgnoreCase("mysql")) {
				conn = new DBManager();
				try {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.createTable(conn, "myping", vo.getId() + "",
								"myping");// Ping
						ctable.createTable(conn, "mypinghour", vo.getId() + "",
								"mypinghour");// Ping
						ctable.createTable(conn, "mypingday", vo.getId() + "",
								"mypingday");// Ping
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.createTable(conn, "myping", vo.getId() + "",
								"myping");// Ping
						// ctable.createSeq(conn, "myping", allipstr);//生成序列
						// ctable.createTriger(conn, "myping", allipstr,
						// "myping");//生成触发器

						ctable.createTable(conn, "mypinghour", vo.getId() + "",
								"mypinghour");// Ping
						// ctable.createSeq(conn, "mypinghour", allipstr);//生成序列
						// ctable.createTriger(conn, "mypinghour", allipstr,
						// "mypinghour");//生成触发器

						ctable.createTable(conn, "mypingday", vo.getId() + "",
								"mypingday");// Ping
						// ctable.createSeq(conn, "mypingday", allipstr);//生成序列
						// ctable.createTriger(conn, "mypingday", allipstr,
						// "mypingday");//生成触发器
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						conn.executeBatch();
					} catch (Exception e) {

					}
					conn.close();
				}

			} else if (typevo.getDbtype().equalsIgnoreCase("informix")) {
				conn = new DBManager();
				try {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.createTable(conn, "informixping", vo.getId()
								+ "", "informixping");// Ping
						ctable.createTable(conn, "informixpinghour", vo.getId()
								+ "", "informixpinghour");// Ping
						ctable.createTable(conn, "informixpingday", vo.getId()
								+ "", "informixpingday");// Ping
						ctable.createInformixLogTable(conn, "informixlog", vo
								.getId()
								+ "", "informixlog");// 存放日志信息
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.createTable(conn, "informixping", vo.getId()
								+ "", "informixping");// Ping
						// ctable.createSeq(conn, "informixping",
						// allipstr);//生成序列
						// ctable.createTriger(conn, "informixping", allipstr,
						// "informixping");//生成触发器

						ctable.createTable(conn, "informixpinghour", vo.getId()
								+ "", "informixpinghour");// Ping
						// ctable.createSeq(conn, "informixpinghour",
						// allipstr);//生成序列
						// ctable.createTriger(conn, "informixpinghour",
						// allipstr, "informixpinghour");//生成触发器

						ctable.createTable(conn, "informixpingday", vo.getId()
								+ "", "informixpingday");// Ping
						// ctable.createSeq(conn, "informixpingday",
						// allipstr);//生成序列
						// ctable.createTriger(conn, "informixpingday",
						// allipstr, "informixpingday");//生成触发器
						ctable.createInformixLogTable(conn, "informixlog", vo
								.getId()
								+ "", "informixlog");// 存放日志信息
						ctable.createSeq(conn, "informixlog", vo.getId() + "");// 生成序列
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						conn.executeBatch();
					} catch (Exception e) {

					}
					conn.close();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			// conn.close();
		}
		return flag;
	}

	public boolean delete(String id) {
		boolean result = false;
		try {
			DBVo vo = (DBVo) findByID(id);
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {

			} finally {
				typedao.close();
			}
			conn.addBatch("delete from app_db_node where id=" + id);
			conn.executeBatch();
			result = true;
			CreateTableManager ctable = new CreateTableManager();
			// 测试生成表
			String ip = vo.getIpAddress();
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			// String allipstr = SysUtil.doip(ip);
			if (typevo.getDbtype().equalsIgnoreCase("sqlserver")) {
				try {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.deleteTable(conn, "sqlping", id, "sqlping");// Ping
						ctable.deleteTable(conn, "sqlpinghour", id,
								"sqlpinghour");// Ping
						ctable
								.deleteTable(conn, "sqlpingday", id,
										"sqlpingday");// Ping
						ctable.dropDbconfigInfo(conn, "system_sqldbconf", id);// 告警阀值
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.deleteTable(conn, "sqlping", id, "sqlping");// Ping
						ctable.dropSeq(conn, "sqlping", id);// 删除序列
						ctable.deleteTable(conn, "sqlpinghour", id,
								"sqlpinghour");// Ping
						ctable.dropSeq(conn, "sqlpinghour", id);// 删除序列
						ctable
								.deleteTable(conn, "sqlpingday", id,
										"sqlpingday");// Ping
						ctable.dropSeq(conn, "sqlpingday", id);// 删除序列

						ctable.dropDbconfigInfo(conn, "system_sqldbconf", id);// 告警阀值
					}

				} catch (Exception ex) {

				}

			} else if (typevo.getDbtype().equalsIgnoreCase("oracle")) {
				try {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.deleteTable(conn, "oraping", id, "oraping");// Ping
						ctable.deleteTable(conn, "orapinghour", id,
								"orapinghour");// Ping
						ctable
								.deleteTable(conn, "orapingday", id,
										"orapingday");// Ping
						ctable
								.dropDbconfigInfo(conn, "system_oraspaceconf",
										id);// 告警阀值
						conn
								.addBatch("delete from system_orasessiondata where serverip='"
										+ id
										+ "' and dbname='"
										+ vo.getDbName() + "'");
						conn.executeBatch();
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.deleteTable(conn, "oraping", id, "oraping");// Ping
						ctable.dropSeq(conn, "oraping", id);// 删除序列
						ctable.deleteTable(conn, "orapinghour", id,
								"orapinghour");// Ping
						ctable.dropSeq(conn, "orapinghour", id);// 删除序列
						ctable
								.deleteTable(conn, "orapingday", id,
										"orapingday");// Ping
						ctable.dropSeq(conn, "orapingday", id);// 删除序列
						ctable
								.dropDbconfigInfo(conn, "system_oraspaceconf",
										id);// 告警阀值
						conn
								.addBatch("delete from system_orasessiondata where serverip='"
										+ id
										+ "' and dbname='"
										+ vo.getDbName() + "'");
						conn.executeBatch();
					}

				} catch (Exception ex) {

				}

			} else if (typevo.getDbtype().equalsIgnoreCase("db2")) {
				try {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.deleteTable(conn, "db2ping", id, "db2ping");// Ping
						ctable.deleteTable(conn, "db2pinghour", id,
								"db2pinghour");// Ping
						ctable
								.deleteTable(conn, "db2pingday", id,
										"db2pingday");// Ping
						ctable
								.dropDbconfigInfo(conn, "system_db2spaceconf",
										id);// 告警阀值
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.deleteTable(conn, "db2ping", id, "db2ping");// Ping
						ctable.dropSeq(conn, "db2ping", id);// 删除序列
						ctable.deleteTable(conn, "db2pinghour", id,
								"db2pinghour");// Ping
						ctable.dropSeq(conn, "db2pinghour", id);// 删除序列
						ctable
								.deleteTable(conn, "db2pingday", id,
										"db2pingday");// Ping
						ctable.dropSeq(conn, "db2pingday", id);// 删除序列
						ctable
								.dropDbconfigInfo(conn, "system_db2spaceconf",
										id);// 告警阀值
					}

				} catch (Exception ex) {

				}

			} else if (typevo.getDbtype().equalsIgnoreCase("sysbase")) {
				try {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.deleteTable(conn, "sysping", id, "sysping");// Ping
						ctable.deleteTable(conn, "syspinghour", id,
								"syspinghour");// Ping
						ctable
								.deleteTable(conn, "syspingday", id,
										"syspingday");// Ping
						ctable
								.dropDbconfigInfo(conn, "system_sybspaceconf",
										id);// 告警阀值
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.deleteTable(conn, "sysping", id, "sysping");// Ping
						ctable.dropSeq(conn, "sysping", id);// 删除序列
						ctable.deleteTable(conn, "syspinghour", id,
								"syspinghour");// Ping
						ctable.dropSeq(conn, "syspinghour", id);// 删除序列
						ctable
								.deleteTable(conn, "syspingday", id,
										"syspingday");// Ping
						ctable.dropSeq(conn, "syspingday", id);// 删除序列
						ctable
								.dropDbconfigInfo(conn, "system_sybspaceconf",
										id);// 告警阀值
					}

				} catch (Exception ex) {

				}

			} else if (typevo.getDbtype().equalsIgnoreCase("mysql")) {
				try {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.deleteTable(conn, "myping", id, "myping");// Ping
						ctable
								.deleteTable(conn, "mypinghour", id,
										"mypinghour");// Ping
						ctable.deleteTable(conn, "mypingday", id, "mypingday");// Ping
						ctable.dropDbconfigInfo(conn, "system_mysqlspaceconf",
								id);// 告警阀值
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.deleteTable(conn, "myping", id, "myping");// Ping
						ctable.dropSeq(conn, "myping", id);// 删除序列
						ctable
								.deleteTable(conn, "mypinghour", id,
										"mypinghour");// Ping
						ctable.dropSeq(conn, "mypinghour", id);// 删除序列
						ctable.deleteTable(conn, "mypingday", id, "mypingday");// Ping
						ctable.dropSeq(conn, "mypingday", id);// 删除序列
						ctable.dropDbconfigInfo(conn, "system_mysqlspaceconf",
								id);// 告警阀值
					}

				} catch (Exception ex) {

				}

			} else if (typevo.getDbtype().equalsIgnoreCase("informix")) {
				try {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.deleteTable(conn, "informixping", id,
								"informixping");// Ping
						ctable.deleteTable(conn, "informixpinghour", id,
								"informixpinghour");// Ping
						ctable.deleteTable(conn, "informixpingday", id,
								"informixpingday");// Ping
						ctable.dropDbconfigInfo(conn,
								"system_infomixspaceconf", id);// 告警阀值
						ctable.deleteTable(conn, "informixlog", id,
								"informixlog");// 日志信息
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						ctable.deleteTable(conn, "informixping", id,
								"informixping");// Ping
						ctable.dropSeq(conn, "informixping", id);// 删除序列
						ctable.deleteTable(conn, "informixpinghour", id,
								"informixpinghour");// Ping
						ctable.dropSeq(conn, "informixpinghour", id);// 删除序列
						ctable.deleteTable(conn, "informixpingday", id,
								"informixpingday");// Ping
						ctable.dropSeq(conn, "informixpingday", id);// 删除序列
						ctable.dropDbconfigInfo(conn,
								"system_informixspaceconf", id);// 告警阀值
						ctable.deleteTable(conn, "informixlog", id,
								"informixlog");// 日志信息
						ctable.dropSeq(conn, "informixlog", id);// 删除序列
					}

				} catch (Exception ex) {

				}

			}
			try {
				// 同时删除事件表里的相关数据
				EventListDao eventdao = new EventListDao();
				eventdao.delete(Integer.parseInt(id), "db");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error("DBDao.delete()", e);
		} finally {
			// conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		DBVo vo = new DBVo();
		try {
			vo.setId(rs.getInt("id"));
			vo.setAlias(rs.getString("alias"));
			vo.setIpAddress(rs.getString("ip_address"));
			vo.setPort(rs.getString("port"));
			vo.setUser(rs.getString("users"));
			vo.setCategory(rs.getInt("category"));
			vo.setDbName(rs.getString("db_name"));
			vo.setPassword(rs.getString("password"));
			vo.setDbuse(rs.getString("dbuse"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setBid(rs.getString("bid"));
			vo.setManaged(rs.getInt("managed"));
			vo.setDbtype(rs.getInt("dbtype"));
			vo.setCollecttype(rs.getInt("collecttype"));
			vo.setSupperid(rs.getInt("supperid"));// snow add 2010-05-20
			vo.setDb_url(rs.getString("db_url"));
		} catch (Exception e) {
			// SysLogger.error("DBDao.loadFromRS()", e);
		}
		return vo;
	}

	// 处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(Pingcollectdata pingdata) {
		if (pingdata == null)
			return false;
		DBManager dbmanager = new DBManager();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Vector v = new Vector();
			// for (int i = 0; i < hostdatavec.size(); i++) {
			// Pingcollectdata pingdata =
			// (Pingcollectdata)hostdatavec.elementAt(i);
			String ip = pingdata.getIpaddress();
			String dbid = "";
			if (pingdata.getCategory().equalsIgnoreCase("ORAPing")) {
				ip = pingdata.getIpaddress().split(":")[0];
				dbid = pingdata.getIpaddress().split(":")[1];

			} else {
				// DBNode dbnode = (DBNode)
//				PollingEngine.getInstance().getDbByIP(ip);
				DaoInterface type = new DBTypeDao();
				//SysLogger.info("==============="+pingdata.getCategory()+"===================");
				List l = null;
				if(pingdata.getCategory().equalsIgnoreCase("myping")){
					System.out.println("###############################################");
					l = type.loadAll(" where dbtype='mysql'");
				}else if(pingdata.getCategory().equalsIgnoreCase("sqlping")){
					l = type.loadAll(" where dbtype='sqlserver'");
				}else if(pingdata.getCategory().equalsIgnoreCase("sysping")){
					l = type.loadAll(" where dbtype='Sybase'");
				}else {
//					System.out.println(pingdata.getCategory().equalsIgnoreCase("myping")+"&&&&&&&&&&&&&&&&"+pingdata.getCategory());
					l = type.loadAll(" where dbtype='"+pingdata.getCategory().split("Ping")[0]+"'");
				}
				DBTypeVo dbtype = (DBTypeVo) l.get(0);
				DaoInterface dao = new DBDao();
				List list = dao.loadAll(" where ip_address = '" + ip
						+ "' and dbtype='"
						+ dbtype.getId() + "'");
				
				DBVo dbnode = (DBVo) list.get(0);
				if (dbnode != null)
					dbid = dbnode.getId() + "";
			}
			if (pingdata.getRestype().equals("dynamic")) {
				// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
				// String[] ipdot = ip.split(".");
				// String tempStr = "";
				// String allipstr = "";
				//
				// if (ip.indexOf(".") > 0) {
				// ip1 = ip.substring(0, ip.indexOf("."));
				// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
				// tempStr = ip.substring(ip.indexOf(".") + 1,
				// ip.lastIndexOf("."));
				// }
				// ip2 = tempStr.substring(0, tempStr.indexOf("."));
				// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
				// tempStr.length());
				// allipstr = ip1 + ip2 + ip3 + ip4;
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
				String tablename = "";
				// //System.out.println("----"+pingdata.getCategory());
				if (!dbid.equals("")) {
					if (pingdata.getCategory().equalsIgnoreCase("ORAPing")) {
						tablename = "oraping" + dbid;
					} else if (pingdata.getCategory().equalsIgnoreCase(
							"SQLPing")) {
						tablename = "sqlping" + dbid;
					} else if (pingdata.getCategory().equalsIgnoreCase(
							"SYSPing")) {
						tablename = "sysping" + dbid;
					} else if (pingdata.getCategory().equalsIgnoreCase(
							"DB2Ping")) {
						tablename = "db2ping" + dbid;
					} else if (pingdata.getCategory()
							.equalsIgnoreCase("MYPing")) {
						tablename = "myping" + dbid;
					} else if (pingdata.getCategory().equalsIgnoreCase(
							"INFORMIXPing")) {
						tablename = "informixping" + dbid;
					} else if (pingdata.getCategory().equalsIgnoreCase(
							"DomPing")) {
						tablename = "dominoping" + dbid;
					}
					String sql = "insert into "
							+ tablename
							+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
							+ "values('" + pingdata.getIpaddress() + "','"
							+ pingdata.getRestype() + "','"
							+ pingdata.getCategory() + "','"
							+ pingdata.getEntity() + "','"
							+ pingdata.getSubentity() + "','"
							+ pingdata.getUnit() + "','" + pingdata.getChname()
							+ "','" + pingdata.getBak() + "',"
							+ pingdata.getCount() + ",'"
							+ pingdata.getThevalue() + "'," + time + ")";

					dbmanager.executeUpdate(sql);
				}
			}
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();

		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dhcc.webnms.manage.resource.api.I_Dbmonitorlist#addDbmonitorlist(com.dhcc.webnms.manage.om.Dbmonitorlist)
	 */
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public boolean addSqlserver_processdata(Sqlserver_processdata sp)
			throws Exception {
		try {
			// session.save(sp);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean addOracle_sessiondata(Oracle_sessiondata os)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Date dd = os.getLogontime();
			String logontime = sdf.format(dd);
			dd = os.getMon_time();
			String montime = sdf.format(dd);
			String sql = "insert into system_orasessiondata(serverip,dbname,machine,username,program,status,sessiontype,command,logontime,mon_time) "
					+ "values('"
					+ os.getServerip()
					+ "','"
					+ os.getDbname()
					+ "','"
					+ os.getMachine()
					+ "','"
					+ os.getUsername()
					+ "','"
					+ os.getProgram()
					+ "','"
					+ os.getStatus()
					+ "','"
					+ os.getSessiontype() + "','" + os.getCommand();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + "'" + logontime + "','" + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + "to_date('" + logontime
						+ "','YYYY-MM-DD HH24:MI:SS'),to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}

			// System.out.println("DBdao.java-------1168-------=====================>"+sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean addOracle_nmssessiondata(Oracle_sessiondata os)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {

			Date dd = os.getLogontime();
			// Date cc = tempCal.getTime();
			String logontime = sdf.format(dd);
			dd = os.getMon_time();
			String montime = sdf.format(dd);
			String sql = "insert into nms_orasessiondata(serverip,dbname,machine,username,program,status,sessiontype,command,logontime,mon_time) "
					+ "values('"
					+ os.getServerip()
					+ "','"
					+ os.getDbname()
					+ "','"
					+ os.getMachine()
					+ "','"
					+ os.getUsername()
					+ "','"
					+ os.getProgram()
					+ "','"
					+ os.getStatus()
					+ "','"
					+ os.getSessiontype() + "','" + os.getCommand();

			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + "'" + logontime + "','" + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + "to_date('" + logontime
						+ "','YYYY-MM-DD HH24:MI:SS'),to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}

			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean addOracle_nmsoraspaces(String serverip, String tablespace,
			String free_mb, String size_mb, String percent_free,
			String file_name, String status) throws Exception {
		// Connection con = null;
		// PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_oraspaces(serverip,tablespace,free_mb,size_mb,percent_free,file_name,status,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ tablespace
					+ "','"
					+ free_mb
					+ "','"
					+ size_mb
					+ "','"
					+ percent_free
					+ "','"
					+ file_name + "','" + status;
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "','" + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "',to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}

			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean clear_nmsspacedata(String serverip) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_oraspaces where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean clear_nmssessiondata(String serverip) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_orasessiondata where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean addOracle_nmsorasys(String serverip, String INSTANCE_NAME,
			String HOST_NAME, String DBNAME, String VERSION,
			String STARTUP_TIME, String status, String ARCHIVER, String BANNER,
			String java_pool) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_orasys(serverip,INSTANCE_NAME,HOST_NAME,DBNAME,VERSION,STARTUP_TIME,status,ARCHIVER,BANNER,java_pool,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ INSTANCE_NAME
					+ "','"
					+ INSTANCE_NAME
					+ "','"
					+ DBNAME
					+ "','"
					+ VERSION
					+ "','"
					+ STARTUP_TIME
					+ "','"
					+ status
					+ "','"
					+ ARCHIVER
					+ "','"
					+ BANNER + "','" + java_pool;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "','" + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "',to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean clear_nmssys(String serverip) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_orasys where serverip='" + serverip
					+ "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean addOracle_nmsorabanner(String serverip, String detail)
			throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_orabanner(serverip,detail) "
					+ "values('" + serverip + "','" + detail + "')";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean clear_nmsbanner(String serverip) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_orabanner where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean addOracle_nmsorarollback(String serverip, String rollback,
			String wraps, String shrink, String ashrink, String extend)
			throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_orarollback(serverip,rollback,wraps,shrink,ashrink,extend,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ rollback
					+ "','"
					+ wraps
					+ "','" + shrink + "','" + ashrink + "','" + extend;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "','" + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "',to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean clear_nmsorarollback(String serverip) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_orarollback where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean addOracle_nmsoralock(String serverip, String username,
			String status, String machine, String sessiontype,
			String logontime, String program, String locktype, String lmode,
			String requeststr) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_oralock(serverip,username,status,machine,sessiontype,logontime,program,locktype,lmode,requeststr,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ username
					+ "','"
					+ status
					+ "','"
					+ machine
					+ "','"
					+ sessiontype
					+ "','"
					+ logontime
					+ "','"
					+ program
					+ "','"
					+ locktype
					+ "','"
					+ lmode
					+ "','" + requeststr;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "','" + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "',to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean clear_nmsoralock(String serverip) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_oralock where serverip='" + serverip
					+ "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean addOracle_nmsoratables(String serverip, String segment_name,
			String spaces) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_oratables(serverip,segment_name,spaces,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ segment_name
					+ "','"
					+ spaces;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "','" + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "',to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean clear_nmsoratables(String serverip) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_oratables where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean addOracle_nmsoratopsql(String serverip, String sql_text,
			String pct_bufgets, String username) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		sql_text = sql_text.replaceAll("'", "''");
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_oratopsql(serverip,sql_text,pct_bufgets,username,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ sql_text
					+ "','"
					+ pct_bufgets + "','" + username;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "','" + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "',to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean clear_nmsoratopsql(String serverip) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_oratopsql where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将原来实时表里的控制文件数据清除
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public boolean clear_nmsoracontrfile(String serverip) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_oracontrfile where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将控制文件信息插入到数据库表
	 * 
	 * @param serverip
	 * @param status
	 * @param name
	 * @param is_recovery_dest_file
	 * @param block_size
	 * @param file_size_blks
	 * @return
	 * @throws Exception
	 */
	public boolean addOracle_nmsoracontrfile(String serverip, String status,
			String name, String is_recovery_dest_file, String block_size,
			String file_size_blks) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_oracontrfile(serverip,status,name,is_recovery_dest_file,block_size,file_size_blks,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ status
					+ "','"
					+ name
					+ "','"
					+ is_recovery_dest_file
					+ "','"
					+ block_size
					+ "','" + file_size_blks;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "','" + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "',to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将原来实时表里的数据库创建信息清除
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public boolean clear_nmsoraisarchive(String serverip) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_oraisarchive where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将数据库创建信息插入到数据库表
	 * 
	 * @param serverip
	 * @param created
	 * @param log_mode
	 * @return
	 * @throws Exception
	 */
	public boolean addOracle_nmsoraisarchive(String serverip, String created,
			String log_mode) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			Date createdDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
					.parse(created);
			created = sdf.format(createdDate);
			String sql = "insert into nms_oraisarchive(serverip,created,log_mode,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ created
					+ "','"
					+ log_mode;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "','" + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "',to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将原来实时表里的固定缓存对象信息清除
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public boolean clear_nmsorakeepobj(String serverip) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_orakeepobj where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将固定缓存对象信息插入数据库
	 * 
	 * @param serverip
	 * @param owner
	 * @param name
	 * @param db_link
	 * @param namespace
	 * @param type
	 * @param sharable_mem
	 * @param loads
	 * @param executions
	 * @param locks
	 * @param pins
	 * @param kept
	 * @param child_latch
	 * @param invalidations
	 * @return
	 * @throws Exception
	 */
	public boolean addOracle_nmsorakeepobj(String serverip, String owner,
			String name, String db_link, String namespace, String type,
			String sharable_mem, String loads, String executions, String locks,
			String pins, String kept, String child_latch, String invalidations)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_orakeepobj(serverip, owner, name, db_link, namespace, type, sharable_mem, loads,"
							+ " executions, locks, pins, kept, child_latch, invalidations, mon_time) ");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(owner);
			sBuffer.append("','");
			sBuffer.append(name);
			sBuffer.append("','");
			sBuffer.append(db_link);
			sBuffer.append("','");
			sBuffer.append(namespace);
			sBuffer.append("','");
			sBuffer.append(type);
			sBuffer.append("','");
			sBuffer.append(sharable_mem);
			sBuffer.append("','");
			sBuffer.append(loads);
			sBuffer.append("','");
			sBuffer.append(executions);
			sBuffer.append("','");
			sBuffer.append(locks);
			sBuffer.append("','");
			sBuffer.append(pins);
			sBuffer.append("','");
			sBuffer.append(kept);
			sBuffer.append("','");
			sBuffer.append(child_latch);
			sBuffer.append("','");
			sBuffer.append(invalidations);
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// //System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将原来实时表里的扩展信息清除
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public boolean clear_nmsoraextent(String serverip) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_oraextent where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将扩展信息插入数据库
	 * 
	 * @param serverip
	 * @param created
	 * @param log_mode
	 * @return
	 * @throws Exception
	 */
	public boolean addOracle_nmsoraextent(String serverip,
			String tablespace_name, String extents) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_oraextent(serverip,tablespace_name,extents,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ tablespace_name
					+ "','"
					+ extents;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + "to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将原来实时表里的日志文件信息清除
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public boolean clear_nmsoralogfile(String serverip) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_oralogfile where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将日志文件信息插入数据库
	 * 
	 * @param serverip
	 * @param group
	 * @param status
	 * @param type
	 * @param member
	 * @return
	 * @throws Exception
	 */
	public boolean addOracle_nmsoralogfile(String serverip, String group,
			String status, String type, String member) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_oralogfile(serverip,groupstr,status,type,member,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ group
					+ "','"
					+ status
					+ "','" + type + "','" + member;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + "to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将原来实时表里的用户信息清除
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public boolean clear_nmsorauserinfo(String serverip) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_orauserinfo where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将用户信息插入数据库
	 * 
	 * @param serverip
	 * @param parsing_user_id
	 * @param cpu_time
	 * @param sorts
	 * @param buffer_gets
	 * @param runtime_mem
	 * @param version_count
	 * @param disk_reads
	 * @param user
	 * @param usernameStr
	 * @param user_id
	 * @param account_status
	 * @return
	 * @throws Exception
	 */
	public boolean addOracle_nmsorauserinfo(String serverip,
			Hashtable returnHash, String lable) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = null;
			if ("0".equals(lable)) {
				sBuffer = new StringBuffer();
				sBuffer
						.append("insert into nms_orauserinfo(serverip, parsing_user_id, cpu_time, sorts, buffer_gets, "
								+ "runtime_mem, version_count, disk_reads,label, mon_time) ");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(String
						.valueOf(returnHash.get("parsing_user_id")));
				sBuffer.append("','");
				sBuffer.append(String
						.valueOf(returnHash.get("sum(a.cpu_time)")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(returnHash.get("sum(a.sorts)")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(returnHash
						.get("sum(a.buffer_gets)")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(returnHash
						.get("sum(a.runtime_mem)")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(returnHash
						.get("sum(a.version_count)")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(returnHash
						.get("sum(a.disk_reads)")));
				sBuffer.append("','");
				sBuffer.append(lable);
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
			} else if ("1".equals(lable)) {
				sBuffer = new StringBuffer();
				sBuffer
						.append("insert into nms_orauserinfo(serverip, users,label, mon_time) ");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(String.valueOf(returnHash.get("user#")));
				sBuffer.append("','");
				sBuffer.append(lable);
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
			} else {
				sBuffer = new StringBuffer();
				sBuffer
						.append("insert into nms_orauserinfo(serverip,username, user_id, account_status,label, mon_time) ");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(String.valueOf(returnHash.get("username")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(returnHash.get("user_id")));
				sBuffer.append("','");
				sBuffer
						.append(String
								.valueOf(returnHash.get("account_status")));
				sBuffer.append("','");
				sBuffer.append(lable);
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
			}
			// //System.out.println(sBuffer.toString());

			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将原来实时表里的指针信息清除
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public boolean clear_nmsoracursors(String serverip) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_oracursors where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将指针信息 插入数据库
	 * 
	 * @param serverip
	 * @param curconnect
	 * @return
	 * @throws Exception
	 */
	public boolean addOracle_nmsoracursors(String serverip, String curconnect,
			String opencur) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_oracursors(serverip,curconnect,opencur,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ curconnect
					+ "','"
					+ opencur;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + "to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将原来实时表里的等待信息清除
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public boolean clear_nmsorawait(String serverip) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_orawait where serverip='" + serverip
					+ "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将等待信息 插入数据库
	 * 
	 * @param serverip
	 * @param event
	 * @param prev
	 * @param curr
	 * @param tot
	 * @return
	 * @throws Exception
	 */
	public boolean addOracle_nmsorawait(String serverip, String event,
			String prev, String curr, String tot) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_orawait(serverip,event,prev,curr,tot,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ event
					+ "','"
					+ prev
					+ "','" + curr + "','" + tot;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + "to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将原来实时表里的IO信息清除
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public boolean clear_nmsoradbio(String serverip) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_oradbio where serverip='" + serverip
					+ "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将IO信息插入数据库
	 * 
	 * @param serverip
	 * @param name
	 * @param file
	 * @param pyr
	 * @param pbr
	 * @param pyw
	 * @param pbw
	 * @return
	 * @throws Exception
	 */
	public boolean addOracle_nmsoradbio(String serverip, String name,
			String file, String pyr, String pbr, String pyw, String pbw)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_oradbio(serverip, name, filename, pyr, pbr, pyw, pbw,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ name
					+ "','"
					+ file
					+ "','" + pyr + "','" + pbr + "','" + pyw + "','" + pbw;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + "to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将原来实时表里的memValue信息清除
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public boolean clear_nmsoramemvalue(String serverip) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_oramemvalue where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * /将memValue信息插入数据库
	 * 
	 * @param serverip
	 * @param memValue
	 * @return
	 * @throws Exception
	 */
	public boolean addOracle_nmsoramemvalue(String serverip, Hashtable memValue)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			String aggregate_pga_auto_target = String.valueOf(memValue
					.get("aggregate_PGA_auto_target"));
			String total_pga_used_for_manual_workareas = String
					.valueOf(memValue
							.get("total_PGA_used_for_manual_workareas"));
			String total_pga_inuse = String.valueOf(memValue
					.get("total_PGA_inuse"));
			String maximum_pga_allocated = String.valueOf(memValue
					.get("maximum_PGA_allocated"));
			String cache_hit_percentage = String.valueOf(memValue
					.get("cache_hit_percentage"));
			String recycle_buffer_cache = String.valueOf(memValue
					.get("RECYCLE_buffer_cache"));
			String keep_buffer_cache = String.valueOf(memValue
					.get("KEEP_buffer_cache"));
			String process_count = String
					.valueOf(memValue.get("process_count"));
			String total_pga_used_for_auto_workareas = String.valueOf(memValue
					.get("total_PGA_used_for_auto_workareas"));
			String asm_buffer_cache = String.valueOf(memValue
					.get("ASM_Buffer_Cache"));
			String over_allocation_count = String.valueOf(memValue
					.get("over_allocation_count"));
			String bytes_processed = String.valueOf(memValue
					.get("bytes_processed"));
			String java_pool = String.valueOf(memValue.get("java_pool"));
			String maximum_pga_used_for_manual_workareas = String
					.valueOf(memValue
							.get("maximum_PGA_used_for_manual_workareas"));
			String streams_pool = String.valueOf(memValue.get("streams_pool"));
			String default_2k_buffer_cache = String.valueOf(memValue
					.get("DEFAULT_2K_buffer_cache"));
			String max_processes_count = String.valueOf(memValue
					.get("max_processes_count"));
			String total_pga_allocated = String.valueOf(memValue
					.get("total_PGA_allocated"));
			String default_4k_buffer_cache = String.valueOf(memValue
					.get("DEFAULT_4K_buffer_cache"));
			String shared_pool = String.valueOf(memValue.get("shared_pool"));
			String default_32k_buffer_cache = String.valueOf(memValue
					.get("DEFAULT_32K_buffer_cache"));
			String default_buffer_cache = String.valueOf(memValue
					.get("DEFAULT_buffer_cache"));
			String large_pool = String.valueOf(memValue.get("large_pool"));
			String aggregate_pga_target_parameter = String.valueOf(memValue
					.get("aggregate_PGA_target_parameter"));
			String default_16k_buffer_cache = String.valueOf(memValue
					.get("DEFAULT_16K_buffer_cache"));
			String global_memory_bound = String.valueOf(memValue
					.get("global_memory_bound"));
			String default_8k_buffer_cache = String.valueOf(memValue
					.get("DEFAULT_8K_buffer_cache"));
			String extra_bytes_read_written = String.valueOf(memValue
					.get("extra_bytes_read/written"));
			String pga_memory_freed_back_to_os = String.valueOf(memValue
					.get("PGA_memory_freed_back_to_OS"));
			String total_freeable_pga_memory = String.valueOf(memValue
					.get("total_freeable_PGA_memory"));
			String recompute_count_total = String.valueOf(memValue
					.get("recompute_count_(total)"));
			String maximum_pga_used_for_auto_workareas = String
					.valueOf(memValue
							.get("maximum_PGA_used_for_auto_workareas"));

			sBuffer
					.append("insert into nms_oramemvalue(serverip, agg_pga_auto_target, tpga_used_manu_workareas, total_pga_inuse, "
							+ "maximum_pga_allocated,cache_hit_percentage,recycle_buffer_cache,keep_buffer_cache,process_count ,tpga_used_auto_workareas,"
							+ "asm_buffer_cache,over_allocation_count,bytes_processed,java_pool,maxpga_used_manu_workareas,streams_pool,"
							+ "default_2k_buffer_cache,max_processes_count,total_pga_allocated,default_4k_buffer_cache,shared_pool,"
							+ "default_32k_buffer_cache,default_buffer_cache,large_pool,agg_pga_target_parameter ,default_16k_buffer_cache,"
							+ "global_memory_bound,default_8k_buffer_cache,extra_bytes_read_written,pga_mem_freed_back_os,tot_free_pga_memory,recom_count_total,"
							+ "maxpga_used_auto_workareas,mon_time) ");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(aggregate_pga_auto_target);
			sBuffer.append("','");
			sBuffer.append(total_pga_used_for_manual_workareas);
			sBuffer.append("','");
			sBuffer.append(total_pga_inuse);
			sBuffer.append("','");
			sBuffer.append(maximum_pga_allocated);
			sBuffer.append("','");
			sBuffer.append(cache_hit_percentage);
			sBuffer.append("','");
			sBuffer.append(recycle_buffer_cache);
			sBuffer.append("','");
			sBuffer.append(keep_buffer_cache);
			sBuffer.append("','");
			sBuffer.append(process_count);
			sBuffer.append("','");
			sBuffer.append(total_pga_used_for_auto_workareas);
			sBuffer.append("','");
			sBuffer.append(asm_buffer_cache);
			sBuffer.append("','");
			sBuffer.append(over_allocation_count);
			sBuffer.append("','");
			sBuffer.append(bytes_processed);
			sBuffer.append("','");
			sBuffer.append(java_pool);
			sBuffer.append("','");
			sBuffer.append(maximum_pga_used_for_manual_workareas);
			sBuffer.append("','");
			sBuffer.append(streams_pool);
			sBuffer.append("','");
			sBuffer.append(default_2k_buffer_cache);
			sBuffer.append("','");
			sBuffer.append(max_processes_count);
			sBuffer.append("','");
			sBuffer.append(total_pga_allocated);
			sBuffer.append("','");
			sBuffer.append(default_4k_buffer_cache);
			sBuffer.append("','");
			sBuffer.append(shared_pool);
			sBuffer.append("','");
			sBuffer.append(default_32k_buffer_cache);
			sBuffer.append("','");
			sBuffer.append(default_buffer_cache);
			sBuffer.append("','");
			sBuffer.append(large_pool);
			sBuffer.append("','");
			sBuffer.append(aggregate_pga_target_parameter);
			sBuffer.append("','");
			sBuffer.append(default_16k_buffer_cache);
			sBuffer.append("','");
			sBuffer.append(global_memory_bound);
			sBuffer.append("','");
			sBuffer.append(default_8k_buffer_cache);
			sBuffer.append("','");
			sBuffer.append(extra_bytes_read_written);
			sBuffer.append("','");
			sBuffer.append(pga_memory_freed_back_to_os);
			sBuffer.append("','");
			sBuffer.append(total_freeable_pga_memory);
			sBuffer.append("','");
			sBuffer.append(recompute_count_total);
			sBuffer.append("','");
			sBuffer.append(maximum_pga_used_for_auto_workareas);
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// //System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将原来实时表里的memPerfValue信息清除
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public boolean clear_nmsoramemperfvalue(String serverip) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_oramemperfvalue where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将memPerfValue信息插入数据库
	 * 
	 * @param serverip
	 * @param pctmemorysorts
	 * @param pctbufgets
	 * @param dictionarycache
	 * @param buffercache
	 * @param librarycache
	 * @return
	 * @throws Exception
	 */
	public boolean addOracle_nmsoramemperfvalue(String serverip,
			String pctmemorysorts, String pctbufgets, String dictionarycache,
			String buffercache, String librarycache) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_oramemperfvalue(serverip, pctmemorysorts, pctbufgets, dictionarycache, buffercache, librarycache,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ pctmemorysorts
					+ "','"
					+ pctbufgets
					+ "','"
					+ dictionarycache
					+ "','"
					+ buffercache + "','" + librarycache;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + "to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将原来实时表里的nms_orastatus状态信息清除
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public boolean clear_nmsorastatus(String serverip) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from nms_orastatus where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将status和lstrnStatu状态信息插入数据库
	 * 
	 * @param serverip
	 * @param lstrnStatu
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public boolean addOracle_nmsorastatus(String serverip, String lstrnStatu,
			String status) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "insert into nms_orastatus(serverip, lstrnstatu, status,mon_time) "
					+ "values('"
					+ serverip
					+ "','"
					+ lstrnStatu
					+ "','"
					+ status;
			// + "','" + montime + "')";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + montime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = sql + "'," + "to_date('" + montime
						+ "','YYYY-MM-DD HH24:MI:SS'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 得到oracle的状态信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getOracle_nmsorastatus(String serverip) throws Exception {
		String slq = "select * from nms_orastatus where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("lstrnstatu", rs.getString("lstrnstatu"));
				retValue.put("status", rs.getString("status"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的mempervalue信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getOracle_nmsoramemperfvalue(String serverip)
			throws Exception {
		String slq = "select * from nms_oramemperfvalue where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("pctmemorysorts", rs.getString("pctmemorysorts"));
				retValue.put("pctbufgets", rs.getString("pctbufgets"));
				retValue
						.put("dictionarycache", rs.getString("dictionarycache"));
				retValue.put("buffercache", rs.getString("buffercache"));
				retValue.put("librarycache", rs.getString("librarycache"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的sysvalue信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getOracle_nmsorasys(String serverip) throws Exception {
		String sysSqlString = "select * from nms_orasys where serverip = '"
				+ serverip + "'";
		String bannerSqlString = "select * from nms_orabanner where serverip = '"
				+ serverip + "'";
		Hashtable sysValue = new Hashtable();
		Vector banners = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(bannerSqlString);
			while (rs.next()) {
				banners.add(rs.getString("detail"));
			}
			rs.close();
			rs = conn.executeQuery(sysSqlString);
			while (rs.next()) {
				sysValue.put("INSTANCE_NAME", rs.getString("instance_name"));
				sysValue.put("HOST_NAME", rs.getString("host_name"));
				sysValue.put("DBNAME", rs.getString("dbname"));
				sysValue.put("VERSION", rs.getString("version"));
				sysValue.put("STARTUP_TIME", rs.getString("startup_time"));
				sysValue.put("CREATED_TIME", rs.getString("created_time"));
				sysValue.put("STATUS", rs.getString("status"));
				sysValue.put("ARCHIVER", rs.getString("archiver"));
				sysValue.put("java_pool", rs.getString("java_pool"));
				sysValue.put("BANNER", banners);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return sysValue;
	}

	/**
	 * 得到oracle的数据库创建信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getOracle_nmsoraisarchive(String serverip)
			throws Exception {
		String slq = "select * from nms_oraisarchive where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("CREATED", rs.getString("created"));
				retValue.put("Log_Mode", rs.getString("log_mode"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据锁数据 nms_oralock
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsoralock(String serverip) throws Exception {
		String slq = "select * from nms_oralock where serverip = '" + serverip
				+ "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable item = new Hashtable();
				item.put("username", rs.getString("username"));
				item.put("status", rs.getString("status"));
				item.put("machine", rs.getString("machine"));
				item.put("sessiontype", rs.getString("sessiontype"));
				item.put("logontime", rs.getString("logontime"));
				item.put("program", rs.getString("program"));
				item.put("locktype", rs.getString("locktype"));
				item.put("lmode", rs.getString("lmode"));
				item.put("request", rs.getString("requeststr"));
				retValue.add(item);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据日志文件信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsoralogfile(String serverip) throws Exception {
		String slq = "select * from nms_oralogfile where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable item = new Hashtable();
				item.put("group#", rs.getString("groupstr"));
				item.put("status", rs.getString("status"));
				item.put("type", rs.getString("type"));
				item.put("member", rs.getString("member"));
				item.put("is_recovery_dest_file", rs
						.getString("is_recovery_dest_file")
						+ "");
				retValue.add(item);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据库扩展信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsoraextent(String serverip) throws Exception {
		String slq = "select * from nms_oraextent where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable item = new Hashtable();
				item.put("tablespace_name", rs.getString("tablespace_name"));
				item.put("sum(a.extents)", rs.getString("extents"));
				retValue.add(item);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据库扩展信息 nms_orakeepobj
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsorakeepobj(String serverip) throws Exception {
		String slq = "select * from nms_orakeepobj where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable item = new Hashtable();
				item.put("owner", rs.getString("owner"));
				item.put("name", rs.getString("name"));
				item.put("db_link", rs.getString("db_link"));
				item.put("namespace", rs.getString("namespace"));
				item.put("type", rs.getString("type"));
				item.put("sharable_mem", rs.getString("sharable_mem"));
				item.put("loads", rs.getString("loads"));
				item.put("executions", rs.getString("executions"));
				item.put("locks", rs.getString("locks"));
				item.put("pins", rs.getString("pins"));
				item.put("kept", rs.getString("kept"));
				item.put("child_latch", rs.getString("child_latch"));
				item.put("invalidations", rs.getString("invalidations"));
				retValue.add(item);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据库扩展信息 nms_oracursors
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getOracle_nmsoracursors(String serverip) throws Exception {
		String slq = "select * from nms_oracursors where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("curconnect", rs.getString("curconnect"));
				retValue.put("opencur", rs.getString("opencur"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据控制文件信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsoracontrfile(String serverip) throws Exception {
		String slq = "select * from nms_oracontrfile where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable item = new Hashtable();
				item.put("status", rs.getString("status"));
				item.put("name", rs.getString("name"));
				item.put("is_recovery_dest_file", rs
						.getString("is_recovery_dest_file"));
				item.put("block_size", rs.getString("block_size"));
				item.put("file_size_blks", rs.getString("file_size_blks"));
				retValue.add(item);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据控制SESSION信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsorasessiondata(String serverip) throws Exception {
		String slq = "select * from nms_orasessiondata where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable item = new Hashtable();
				item.put("machine", rs.getString("machine"));
				item.put("username", rs.getString("username"));
				item.put("program", rs.getString("program"));
				item.put("status", rs.getString("status"));
				item.put("sessiontype", rs.getString("sessiontype"));
				item.put("command", rs.getString("command"));
				item.put("logontime", rs.getString("logontime"));
				retValue.add(item);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据控制回滚信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsorarollback(String serverip) throws Exception {
		String slq = "select * from nms_orarollback where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable item = new Hashtable();
				item.put("rollback_segment", rs.getString("rollback"));
				item.put("wraps", rs.getString("wraps"));
				item.put("shrinks", rs.getString("ashrink"));
				item.put("average_shrink", rs.getString("ashrink"));
				item.put("extends", rs.getString("extend"));
				retValue.add(item);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}
	
	/**
	 * 得到oracle的作业信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsorajobs(String serverip) throws Exception {
		String slq = "select * from nms_orajob where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable item = new Hashtable();
				item.put("job", rs.getString("job"));
				item.put("loguser", rs.getString("loguser"));
				Calendar tempCal = Calendar.getInstance();							
	    		Date cc = new Date();
	    		cc.setTime(rs.getTimestamp("lastdate").getTime());
	    		//tempCal.setTime(cc);
	    		//Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				item.put("lastdate", time);
				item.put("failures", rs.getString("failures"));
				retValue.add(item);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据表信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsoratables(String serverip) throws Exception {
		String slq = "select * from nms_oratables where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable item = new Hashtable();
				item.put("spaces", rs.getString("spaces"));
				item.put("segment_name", rs.getString("segment_name"));
				retValue.add(item);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据表空间数据
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsoraspaces(String serverip) throws Exception {
		String slq = "select * from nms_oraspaces where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable item = new Hashtable();
				item.put("file_name", rs.getString("file_name"));
				item.put("tablespace", rs.getString("tablespace"));
				item.put("size_mb", rs.getString("size_mb"));
				item.put("free_mb", rs.getString("free_mb"));
				item.put("percent_free", rs.getString("percent_free"));
				item.put("status", rs.getString("status"));
				item.put("chunks", rs.getString("chunks"));
				retValue.add(item);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据IO信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getOracle_nmsoradbio(String serverip) throws Exception {
		String slq = "select * from nms_oradbio where serverip = '" + serverip
				+ "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable dbioDetail = new Hashtable();
				dbioDetail.put("pyr", rs.getString("pyr"));
				dbioDetail.put("pbr", rs.getString("pbr"));
				dbioDetail.put("pyw", rs.getString("pyw"));
				dbioDetail.put("pbw", rs.getString("pbw"));
				retValue.put(rs.getString("filename"), dbioDetail);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据TOPSql信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsoratopsql(String serverip) throws Exception {
		String slq = "select * from nms_oratopsql where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable dbioDetail = new Hashtable();
				dbioDetail.put("sql_text", rs.getString("sql_text"));
				dbioDetail.put("pct_bufgets", rs.getString("pct_bufgets"));
				dbioDetail.put("username", rs.getString("username"));
				retValue.add(dbioDetail);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的锁指标信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public OracleLockInfo getOracle_nmsoralockinfo(String serverip)
			throws Exception {
		String slq = "select subentity,thevalue from nms_oralockinfo where serverip = '"
				+ serverip + "'";
		OracleLockInfo oracleLockInfo = new OracleLockInfo();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				String subentity = rs.getString("subentity");
				String thevalue = rs.getString("thevalue");
				if (subentity.equals("deadlockcount")) {
					oracleLockInfo.setDeadlockcount(thevalue);
				}
				if (subentity.equals("lockwaitcount")) {
					oracleLockInfo.setLockwaitcount(thevalue);
				}
				if (subentity.equals("maxprocesscount")) {
					oracleLockInfo.setMaxprocesscount(thevalue);
				}
				if (subentity.equals("processcount")) {
					oracleLockInfo.setProcesscount(thevalue);
				}
				if (subentity.equals("currentsessioncount")) {
					oracleLockInfo.setCurrentsessioncount(thevalue);
				}
				if (subentity.equals("useablesessioncount")) {
					oracleLockInfo.setUseablesessioncount(thevalue);
				}
				if (subentity.equals("useablesessionpercent")) {
					oracleLockInfo.setUseablesessionpercent(thevalue);
				}
				if (subentity.equals("lockdsessioncount")) {
					oracleLockInfo.setLockdsessioncount(thevalue);
				}
				if (subentity.equals("rollbackcommitpercent")) {
					oracleLockInfo.setRollbackcommitpercent(thevalue);
				}
				if (subentity.equals("rollbacks")) {
					oracleLockInfo.setRollbacks(thevalue);
				}
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return oracleLockInfo;
	}

	/**
	 * 得到oracle的数据TOPSql信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsoratopsql_readwrite(String serverip)
			throws Exception {
		String slq = "select * from nms_oratopsql_readwrite where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable dbioDetail = new Hashtable();
				dbioDetail.put("sqltext", rs.getString("sqltext"));
				dbioDetail.put("totaldisk", rs.getString("totaldisk"));
				dbioDetail.put("totalexec", rs.getString("totalexec"));
				dbioDetail.put("diskreads", rs.getString("diskreads"));
				retValue.add(dbioDetail);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据排序TOPSql信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsoratopsql_sort(String serverip) throws Exception {
		String slq = "select * from nms_oratopsql_sort where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable dbioDetail = new Hashtable();
				dbioDetail.put("sqltext", rs.getString("sqltext"));
				dbioDetail.put("sorts", rs.getString("sorts"));
				dbioDetail.put("executions", rs.getString("executions"));
				dbioDetail.put("sortsexec", rs.getString("sortsexec"));
				retValue.add(dbioDetail);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的基础信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getOracle_nmsorabaseinfo(String serverip) throws Exception {
		String slq = "select * from nms_orabaseinfo where serverip = '"
				+ serverip + "'";
		Hashtable<String, String> retHashtable = new Hashtable<String, String>();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retHashtable.put(rs.getString("subentity"), rs
						.getString("thevalue"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retHashtable;
	}

	/**
	 * 得到oracle的数据用户信息
	 * 
	 * @param serverip
	 * @param label
	 *            类别
	 * @return
	 * @throws Exception
	 */
	public Hashtable getOracle_nmsorauserinfo(String serverip) throws Exception {
		String sql_0 = "select parsing_user_id,cpu_time,sorts,buffer_gets,runtime_mem,version_count,disk_reads from nms_orauserinfo where serverip = '"
				+ serverip + "' and label = '0'";
		String sql_1 = "select users from nms_orauserinfo where serverip = '"
				+ serverip + "' and label = '1'";
		String sql_2 = "select username,user_id,account_status from nms_orauserinfo where serverip = '"
				+ serverip + "' and label = '2'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		Vector returnVal0 = new Vector();
		Vector returnVal1 = new Vector();
		Vector returnVal2 = new Vector();
		String user_ = "";
		try {
			// sql_0
			rs = dbmanager.executeQuery(sql_0);
			while (rs.next()) {
				Hashtable temp = new Hashtable();
				temp.put("parsing_user_id", rs.getString("parsing_user_id"));
				temp.put("sum(a.cpu_time)", rs.getString("cpu_time"));
				temp.put("sum(a.sorts)", rs.getString("sorts"));
				temp.put("sum(a.buffer_gets)", rs.getString("buffer_gets"));
				temp.put("sum(a.runtime_mem)", rs.getString("runtime_mem"));
				temp.put("sum(a.version_count)", rs.getString("version_count"));
				temp.put("sum(a.disk_reads)", rs.getString("disk_reads"));
				returnVal0.add(temp);
			}
			// sql_1
			rs = dbmanager.executeQuery(sql_1);
			while (rs.next()) {
				Hashtable temp = new Hashtable();
				temp.put("user#", rs.getString("users"));
				returnVal1.add(temp);

			}
			// sql_2
			rs = dbmanager.executeQuery(sql_2);
			while (rs.next()) {
				Hashtable temp = new Hashtable();
				temp.put("username", rs.getString("username"));
				temp.put("user_id", rs.getString("user_id"));
				temp.put("account_status", rs.getString("account_status"));
				returnVal2.add(temp);
			}
			rs.close();
			retValue.put("returnVal0", returnVal0);
			retValue.put("returnVal1", returnVal1);
			retValue.put("returnVal2", returnVal2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的数据等待信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getOracle_nmsorawait(String serverip) throws Exception {
		String slq = "select * from nms_orawait where serverip = '" + serverip
				+ "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable tempHash = new Hashtable();
				tempHash.put("event", rs.getString("event"));
				tempHash.put("prev", rs.getString("prev"));
				tempHash.put("curr", rs.getString("curr"));
				tempHash.put("tot", rs.getString("tot"));
				retValue.add(tempHash);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到oracle的memValue信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getOracle_nmsoramemvalue(String serverip) throws Exception {
		String slq = "select * from nms_oramemvalue where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("aggregate_PGA_auto_target", rs
						.getString("agg_pga_auto_target"));
				retValue.put("total_PGA_used_for_manual_workareas", rs
						.getString("tpga_used_manu_workareas"));
				retValue
						.put("total_PGA_inuse", rs.getString("total_pga_inuse"));
				retValue.put("maximum_PGA_allocated", rs
						.getString("maximum_pga_allocated"));
				retValue.put("cache_hit_percentage", rs
						.getString("cache_hit_percentage"));
				retValue.put("RECYCLE_buffer_cache", rs
						.getString("recycle_buffer_cache"));
				retValue.put("KEEP_buffer_cache", rs
						.getString("keep_buffer_cache"));
				retValue.put("process_count", rs.getString("process_count"));
				retValue.put("total_PGA_used_for_auto_workareas", rs
						.getString("tpga_used_auto_workareas"));
				retValue.put("ASM_Buffer_Cache", rs
						.getString("asm_buffer_cache"));
				retValue.put("over_allocation_count", rs
						.getString("over_allocation_count"));
				retValue
						.put("bytes_processed", rs.getString("bytes_processed"));
				retValue.put("java_pool", rs.getString("java_pool"));
				retValue.put("maximum_PGA_used_for_manual_workareas", rs
						.getString("maxpga_used_manu_workareas"));
				retValue.put("streams_pool", rs.getString("streams_pool"));
				retValue.put("DEFAULT_2K_buffer_cache", rs
						.getString("default_2k_buffer_cache"));
				retValue.put("max_processes_count", rs
						.getString("max_processes_count"));
				retValue.put("total_PGA_allocated", rs
						.getString("total_pga_allocated"));
				retValue.put("DEFAULT_4K_buffer_cache", rs
						.getString("default_4k_buffer_cache"));
				retValue.put("shared_pool", rs.getString("shared_pool"));
				retValue.put("DEFAULT_32K_buffer_cache", rs
						.getString("default_32k_buffer_cache"));
				retValue.put("DEFAULT_buffer_cache", rs
						.getString("default_buffer_cache"));
				retValue.put("large_pool", rs.getString("large_pool"));
				retValue.put("aggregate_PGA_target_parameter", rs
						.getString("agg_pga_target_parameter"));
				retValue.put("DEFAULT_16K_buffer_cache", rs
						.getString("default_16k_buffer_cache"));
				retValue.put("global_memory_bound", rs
						.getString("global_memory_bound"));
				retValue.put("DEFAULT_8K_buffer_cache", rs
						.getString("default_8k_buffer_cache"));
				retValue.put("extra_bytes_read/written", rs
						.getString("extra_bytes_read_written"));
				retValue.put("PGA_memory_freed_back_to_OS", rs
						.getString("pga_mem_freed_back_os"));
				retValue.put("total_freeable_PGA_memory", rs
						.getString("tot_free_pga_memory"));
				retValue.put("recompute_count_(total)", rs
						.getString("recom_count_total"));
				retValue.put("maximum_PGA_used_for_auto_workareas", rs
						.getString("maxpga_used_auto_workareas"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 清空指定表的数据
	 * 
	 * @param tableName
	 *            表名称
	 * @param ip
	 * @return
	 */
	public Boolean clearTableData(String tableName, String serverip) {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from " + tableName + " where serverip='"
					+ serverip + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}
	public Boolean clearMysqlTableData(String tableName, String serverip) {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from " + tableName + " where serverip='"
					+ serverip + "' and typename <> 'status'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}
	//删除数据库的告警阀值，
	public Boolean clearTable(String tableName, String ipaddress) {
		DBManager dbmanager = new DBManager();
		try {
			String sql = "delete from " + tableName + " where ipaddress='"
					+ ipaddress + "'";
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 清空多张指定表的数据
	 * 
	 * @param tableName
	 *            表名称
	 * @param ip
	 * @return
	 */
	public Boolean clearTablesData(String[] tableNames, String serverip) {
		DBManager dbmanager = new DBManager();
		try {
			for (String tableName : tableNames) {

				String sql = "delete from " + tableName + " where serverip='"
						+ serverip + "'";
//				 SysLogger.info("...................>>>>"+sql);
				dbmanager.addBatch(sql);
			}
			// //System.out.println(sql);
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 清空多张指定表的数据
	 * 
	 * @param tableNames
	 *            表名称
	 * @param nodeids
	 *            结点ID数组
	 * @return
	 */
	public Boolean clearTablesDataByNodeIds(String[] tableNames,
			String[] nodeids) {
		DBManager dbmanager = new DBManager();
		try {
			for (String nodeid : nodeids) {
				for (String tableName : tableNames) {
					String sql = "delete from " + tableName + " where nodeid='"
							+ nodeid + "'";
					dbmanager.addBatch(sql);
				}
			}
			// //System.out.println(sql);
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 清空多张指定表的数据
	 * 
	 * @param tableNames
	 *            表名称
	 * @param uniqueKey
	 *            唯一键 如：nodeid
	 * @param nodeids
	 *            唯一键对应的值 如：结点ID数组
	 * @return
	 */
	public Boolean clearTablesData(String[] tableNames, String uniqueKey,
			String[] uniqueKeyValues) {
		DBManager dbmanager = new DBManager();
		try {
			for (String uniqueValue : uniqueKeyValues) {
				for (String tableName : tableNames) {
					String sql = "delete from " + tableName + " where "
							+ uniqueKey + " = '" + uniqueValue + "'";
					dbmanager.addBatch(sql);
				}
			}
			// //System.out.println(sql);
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将指针信息 插入数据库
	 * 
	 * @param serverip
	 * @param curconnect
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmsstatus(String serverip, String status)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "";
			// = "insert into nms_sqlserverstatus(serverip,status,mon_time) "
			// + "values('"
			// + serverip
			// + "','"
			// + status
			// + "','" + montime + "')";
			if (SystemConstant.DBType.equals("mysql")) {
				sql = "insert into nms_sqlserverstatus(serverip,status,mon_time) "
						+ "values('"
						+ serverip
						+ "','"
						+ status
						+ "','"
						+ montime + "')";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql = "insert into nms_sqlserverstatus(serverip,status,mon_time) "
						+ "values('"
						+ serverip
						+ "','"
						+ status
						+ "',to_date('"
						+ montime
						+ "','yyyy-mm-dd hh24:mi:ss'))";
			}
			// //System.out.println(sql);
			dbmanager.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 
	 * @param serverip
	 * @param pages
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmspages(String serverip, Hashtable pages)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserverpages(serverip, bufferCacheHitRatio, planCacheHitRatio, "
							+ "cursorManagerByTypeHitRatio, catalogMetadataHitRatio, dbOfflineErrors, killConnectionErrors, userErrors,"
							+ " infoErrors, sqlServerErrors_total, cachedCursorCounts, cursorCacheUseCounts, cursorRequests_total, "
							+ "activeCursors,cursorMemoryUsage,cursorWorktableUsage,activeOfCursorPlans,dbPages,totalPageLookups,"
							+ "totalPageLookupsRate,totalPageReads,totalPageReadsRate,totalPageWrites,totalPageWritesRate,totalPages,freePages,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("bufferCacheHitRatio")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("planCacheHitRatio")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages
					.get("cursorManagerByTypeHitRatio")));
			sBuffer.append("','");
			sBuffer
					.append(String
							.valueOf(pages.get("catalogMetadataHitRatio")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("dbOfflineErrors")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("killConnectionErrors")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("userErrors")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("infoErrors")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("sqlServerErrors_total")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("cachedCursorCounts")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("cursorCacheUseCounts")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("cursorRequests_total")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("activeCursors")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("cursorMemoryUsage")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("cursorWorktableUsage")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("activeOfCursorPlans")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("dbPages")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPageLookups")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPageLookupsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPageReads")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPageReadsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPageWrites")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPageWritesRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("totalPages")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(pages.get("freePages")));

			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// //System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 插入SqlServer的链接信息到数据库
	 * 
	 * @param serverip
	 * @param conns
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmsconns(String serverip, Hashtable conns)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserverconns(serverip, connections, totalLogins, "
							+ "totalLoginsRate, totalLogouts, totalLogoutsRate,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(conns.get("connections")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(conns.get("totalLogins")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(conns.get("totalLoginsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(conns.get("totalLogouts")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(conns.get("totalLogoutsRate")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// //System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将SqlServer锁信息插入数据库
	 * 
	 * @param serverip
	 * @param locks
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmslocks(String serverip, Hashtable locks)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserverlocks(serverip, lockRequests, lockRequestsRate, "
							+ "lockWaits, lockWaitsRate, lockTimeouts,lockTimeoutsRate,deadLocks,deadLocksRate,avgWaitTime,"
							+ "avgWaitTimeBase,latchWaits,latchWaitsRate,avgLatchWait,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("lockRequests")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("lockRequestsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("lockWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("lockWaitsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("lockTimeouts")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("lockTimeoutsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("deadLocks")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("deadLocksRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("avgWaitTime")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("avgWaitTimeBase")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("latchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("latchWaitsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(locks.get("avgLatchWait")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// //System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将SqlServer缓存信息放入数据库
	 * 
	 * @param serverip
	 * @param caches
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmscaches(String serverip, Hashtable caches)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlservercaches(serverip, cacheHitRatio, cacheHitRatioBase, "
							+ "cacheCount, cachePages, cacheUsed,cacheUsedRate,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(caches.get("cacheHitRatio")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(caches.get("cacheHitRatioBase")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(caches.get("cacheCount")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(caches.get("cachePages")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(caches.get("cacheUsed")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(caches.get("cacheUsedRate")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// //System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将SqlServer的sql统计信息插入数据库
	 * 
	 * @param serverip
	 * @param sqls
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmssqls(String serverip, Hashtable sqls)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserversqls(serverip, batchRequests, batchRequestsRate, "
							+ "sqlCompilations, sqlCompilationsRate, sqlRecompilation,sqlRecompilationRate,autoParams,autoParamsRate,failedAutoParams,"
							+ "failedAutoParamsRate,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("batchRequests")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("batchRequestsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("sqlCompilations")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("sqlCompilationsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("sqlRecompilation")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("sqlRecompilationRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("autoParams")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("autoParamsRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("failedAutoParams")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sqls.get("failedAutoParamsRate")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将SqlServer的内存利用情况插入数据库
	 * 
	 * @param serverip
	 * @param mems
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmsmems(String serverip, Hashtable mems)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlservermems(serverip, totalMemory, sqlMem, "
							+ "optMemory, memGrantPending, memGrantSuccess,lockMem,conMemory,grantedWorkspaceMem,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("totalMemory")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("sqlMem")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("optMemory")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("memGrantPending")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("memGrantSuccess")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("lockMem")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("conMemory")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(mems.get("grantedWorkspaceMem")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将SqlServer的访问方法的明细插入数据库
	 * 
	 * @param serverip
	 * @param scans
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmsscans(String serverip, Hashtable scans)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserverscans(serverip, fullScans, fullScansRate, "
							+ "rangeScans, rangeScansRate, probeScans,probeScansRate,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(scans.get("fullScans")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(scans.get("fullScansRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(scans.get("rangeScans")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(scans.get("rangeScansRate")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(scans.get("probeScans")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(scans.get("probeScansRate")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 
	 * @param serverip
	 * @param statisticsHash
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmsstatisticsHash(String serverip,
			Hashtable statisticsHash) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserverstatisticshash(serverip, pj_lockWaits , pj_memGrantQueWaits , ");
			sBuffer
					.append("pj_thdSafeMemObjWaits , pj_logWriteWaits, pj_logBufferWaits, pj_networkIOWaits, pj_pageIOLatchWaits,");
			sBuffer
					.append(" pj_pageLatchWaits, pj_nonPageLatchWaits, pj_waitForTheWorker, pj_workspaceSynWaits , pj_traOwnershipWaits , ");
			sBuffer
					.append("jx_lockWaits,jx_memGrantQueWaits ,jx_thdSafeMemObjWaits ,jx_logWriteWaits,jx_logBufferWaits,jx_networkIOWaits,");
			sBuffer
					.append("jx_pageIOLatchWaits,jx_pageLatchWaits,jx_nonPageLatchWaits,jx_waitForTheWorker,jx_workspaceSynWaits ,jx_traOwnershipWaits ,");
			sBuffer
					.append("qd_lockWaits,qd_memGrantQueWaits ,qd_thdSafeMemObjWaits ,qd_logWriteWaits,qd_logBufferWaits,qd_networkIOWaits,qd_pageIOLatchWaits,qd_pageLatchWaits,");
			sBuffer
					.append("qd_nonPageLatchWaits,qd_waitForTheWorker,qd_workspaceSynWaits ,qd_traOwnershipWaits ,lj_lockWaits,lj_memGrantQueWaits ,lj_thdSafeMemObjWaits ,");
			sBuffer
					.append("lj_logWriteWaits,lj_logBufferWaits,lj_networkIOWaits,lj_pageIOLatchWaits,lj_pageLatchWaits,lj_nonPageLatchWaits,lj_waitForTheWorker,lj_workspaceSynWaits ,lj_traOwnershipWaits ,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_lockWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_memoryGrantQueueWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_threadSafeMemoryObjectWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_logWriteWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_logBufferWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_networkIOWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_pageIOLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_pageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_nonPageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_waitForTheWorker")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_workspaceSynchronizationWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("pingjun_transactionOwnershipWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_lockWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_memoryGrantQueueWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_threadSafeMemoryObjectWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_logWriteWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_logBufferWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_networkIOWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_pageIOLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_pageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_nonPageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_waitForTheWorker")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_workspaceSynchronizationWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("jingxing_transactionOwnershipWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_lockWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_memoryGrantQueueWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_threadSafeMemoryObjectWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_logWriteWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_logBufferWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_networkIOWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_pageIOLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_pageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_nonPageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_waitForTheWorker")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_workspaceSynchronizationWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("qidong_transactionOwnershipWaits")));
			sBuffer.append("','");
			sBuffer.append(String
					.valueOf(statisticsHash.get("leiji_lockWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_memoryGrantQueueWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_threadSafeMemoryObjectWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_logWriteWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_logBufferWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_networkIOWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_pageIOLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_pageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_nonPageLatchWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_waitForTheWorker")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_workspaceSynchronizationWaits")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(statisticsHash
					.get("leiji_transactionOwnershipWaits")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 
	 * @param serverip
	 * @param scans
	 * @param instance_name
	 * @param label
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmsdbvalue(String serverip, Hashtable scans,
			String instance_name, String label) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			if (!"2".equals(label)) {
				sBuffer
						.append("insert into nms_sqlserverdbvalue(serverip, usedperc, usedsize, "
								+ "sizes, logname, dbname,label,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("usedperc")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("usedsize")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("size")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("logname")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("dbname")));
				sBuffer.append("','");
				sBuffer.append(label);
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
			} else {
				sBuffer = new StringBuffer();
				sBuffer
						.append("insert into nms_sqlserverdbvalue(serverip,instance_name,label,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(instance_name);
				sBuffer.append("','");
				sBuffer.append(label);
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将sqlserver的系统信息插入数据库
	 * 
	 * @param serverip
	 * @param sysvalue
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmssysvalue(String serverip, Hashtable sysvalue)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserversysvalue(serverip, productlevel, version, "
							+ "machinename, issingleuser, processid,isintegratedsecurityonly,isclustered,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("productlevel")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("VERSION")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("MACHINENAME")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("IsSingleUser")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("ProcessID")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue
					.get("IsIntegratedSecurityOnly")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("IsClustered")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将sqlserver锁信息插入数据库
	 * 
	 * @param serverip
	 * @param lockinfo
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmslockinfo_v(String serverip,
			Hashtable lockinfo) throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserverlockinfo_v(serverip, rsc_text, rsc_dbid, ");
			sBuffer
					.append("dbname, rsc_indid, rsc_objid,rsc_type,rsc_flag,req_mode,req_status,req_refcnt,req_cryrefcnt,");
			sBuffer
					.append("req_lifetime,req_spid,req_ecid,req_ownertype,req_transactionID,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("rsc_text")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("rsc_dbid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("dbname")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("rsc_indid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("rsc_objid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("rsc_type")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("rsc_flag")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_mode")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_status")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_refcnt")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_cryrefcnt")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_lifetime")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_spid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_ecid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_ownertype")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lockinfo.get("req_transactionID")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将sqlserver进程信息插入数据库
	 * 
	 * @param serverip
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public boolean addSqlserver_nmsinfo_v(String serverip, Hashtable info)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sqlserverinfo_v(serverip, spid, waittime, ");
			sBuffer
					.append("lastwaittype, waitresource, dbname,username,cpu,physical_io,memusage,login_time,last_batch,");
			sBuffer
					.append("status,hostname,program_name,hostprocess,cmd,nt_domain,nt_username,net_library,loginame,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("spid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("waittime")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("lastwaittype")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("waitresource")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("dbname")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("username")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("cpu")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("physical_io")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("memusage")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("login_time")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("last_batch")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("status")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("hostname")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("program_name")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("hostprocess")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("cmd")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("nt_domain")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("nt_username")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("net_library")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("loginame")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println("DBDao.java==========4232
			// hang--------->>>>>."+sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getSqlserver_nmspages(String serverip) throws Exception {
		String slq = "select * from nms_sqlserverpages where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("bufferCacheHitRatio", rs
						.getString("bufferCacheHitRatio"));
				retValue.put("planCacheHitRatio", rs
						.getString("planCacheHitRatio"));
				retValue.put("cursorManagerByTypeHitRatio", rs
						.getString("cursorManagerByTypeHitRatio"));
				retValue.put("catalogMetadataHitRatio", rs
						.getString("catalogMetadataHitRatio"));
				retValue
						.put("dbOfflineErrors", rs.getString("dbOfflineErrors"));
				retValue.put("killConnectionErrors", rs
						.getString("killConnectionErrors"));
				retValue.put("userErrors", rs.getString("userErrors"));
				retValue.put("infoErrors", rs.getString("infoErrors"));
				retValue.put("sqlServerErrors_total", rs
						.getString("sqlServerErrors_total"));
				retValue.put("cachedCursorCounts", rs
						.getString("cachedCursorCounts"));
				retValue.put("cursorCacheUseCounts", rs
						.getString("cursorCacheUseCounts"));
				retValue.put("cursorRequests_total", rs
						.getString("cursorRequests_total"));
				retValue.put("activeCursors", rs.getString("activeCursors"));
				retValue.put("cursorMemoryUsage", rs
						.getString("cursorMemoryUsage"));
				retValue.put("cursorWorktableUsage", rs
						.getString("cursorWorktableUsage"));
				retValue.put("activeOfCursorPlans", rs
						.getString("activeOfCursorPlans"));
				retValue.put("dbPages", rs.getString("dbPages"));
				retValue.put("totalPageLookups", rs
						.getString("totalPageLookups"));
				retValue.put("totalPageLookupsRate", rs
						.getString("totalPageLookupsRate"));
				retValue.put("totalPageReads", rs.getString("totalPageReads"));
				retValue.put("totalPageReadsRate", rs
						.getString("totalPageReadsRate"));
				retValue
						.put("totalPageWrites", rs.getString("totalPageWrites"));
				retValue.put("totalPageWritesRate", rs
						.getString("totalPageWritesRate"));
				retValue.put("totalPages", rs.getString("totalPages"));
				retValue.put("freePages", rs.getString("freePages"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getSqlserver_nmssysvalue(String serverip) throws Exception {
		String slq = "select * from nms_sqlserversysvalue where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("productlevel", rs.getString("productlevel"));
				retValue.put("VERSION", rs.getString("version"));
				retValue.put("MACHINENAME", rs.getString("machinename"));
				retValue.put("IsSingleUser", rs.getString("issingleuser"));
				retValue.put("ProcessID", rs.getString("processid"));
				retValue.put("IsIntegratedSecurityOnly", rs
						.getString("isintegratedsecurityonly"));
				retValue.put("IsClustered", rs.getString("isclustered"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到sqlserver数据库连接信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getSqlserver_nmsconns(String serverip) throws Exception {
		String slq = "select * from nms_sqlserverconns where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("connections", rs.getString("connections"));
				retValue.put("totalLogins", rs.getString("totalLogins"));
				retValue
						.put("totalLoginsRate", rs.getString("totalLoginsRate"));
				retValue.put("totalLogouts", rs.getString("totalLogouts"));
				retValue.put("totalLogoutsRate", rs
						.getString("totalLogoutsRate"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到sqlserver数据库缓存信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getSqlserver_nmscaches(String serverip) throws Exception {
		String slq = "select * from nms_sqlservercaches where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("cacheHitRatio", rs.getString("cacheHitRatio"));
				retValue.put("cacheHitRatioBase", rs
						.getString("cacheHitRatioBase"));
				retValue.put("cacheCount", rs.getString("cacheCount"));
				retValue.put("cachePages", rs.getString("cachePages"));
				retValue.put("cacheUsed", rs.getString("cacheUsed"));
				retValue.put("cacheUsedRate", rs.getString("cacheUsedRate"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到sqlserver数据库sql信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getSqlserver_nmssqls(String serverip) throws Exception {
		String slq = "select * from nms_sqlserversqls where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("batchRequests", rs.getString("batchRequests"));
				retValue.put("batchRequestsRate", rs
						.getString("batchRequestsRate"));
				retValue
						.put("sqlCompilations", rs.getString("sqlCompilations"));
				retValue.put("sqlCompilationsRate", rs
						.getString("sqlCompilationsRate"));
				retValue.put("sqlRecompilation", rs
						.getString("sqlRecompilation"));
				retValue.put("sqlRecompilationRate", rs
						.getString("sqlRecompilationRate"));
				retValue.put("autoParams", rs.getString("autoParams"));
				retValue.put("autoParamsRate", rs.getString("autoParamsRate"));
				retValue.put("failedAutoParams", rs
						.getString("failedAutoParams"));
				retValue.put("failedAutoParamsRate", rs
						.getString("failedAutoParamsRate"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getSqlserver_nmsscans(String serverip) throws Exception {
		String slq = "select * from nms_sqlserverscans where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("fullScans", rs.getString("fullScans"));
				retValue.put("fullScansRate", rs.getString("fullScansRate"));
				retValue.put("rangeScans", rs.getString("rangeScans"));
				retValue.put("rangeScansRate", rs.getString("rangeScansRate"));
				retValue.put("probeScans", rs.getString("probeScans"));
				retValue.put("probeScansRate", rs.getString("probeScansRate"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 取得sqlserver锁信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getSqlserver_nmslockinfo_v(String serverip) throws Exception {
		String slq = "select * from nms_sqlserverlockinfo_v where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable item = new Hashtable();
				item.put("rsc_text", rs.getString("rsc_text"));
				item.put("rsc_dbid", rs.getString("rsc_dbid"));
				item.put("dbname", rs.getString("dbname"));
				item.put("rsc_indid", rs.getString("rsc_indid"));
				item.put("rsc_objid", rs.getString("rsc_objid"));
				item.put("rsc_type", rs.getString("rsc_type"));
				item.put("rsc_flag", rs.getString("rsc_flag"));
				item.put("req_mode", rs.getString("req_mode"));
				item.put("req_status", rs.getString("req_status"));
				item.put("req_refcnt", rs.getString("req_refcnt"));
				item.put("req_cryrefcnt", rs.getString("req_cryrefcnt"));
				item.put("req_lifetime", rs.getString("req_lifetime"));
				item.put("req_spid", rs.getString("req_spid"));
				item.put("req_ecid", rs.getString("req_ecid"));
				item.put("req_ownertype", rs.getString("req_ownertype"));
				item
						.put("req_transactionID", rs
								.getString("req_transactionID"));
				retValue.add(item);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getSqlserver_nmslocks(String serverip) throws Exception {
		String slq = "select * from nms_sqlserverlocks where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("lockRequests", rs.getString("lockRequests"));
				retValue.put("lockRequestsRate", rs
						.getString("lockRequestsRate"));
				retValue.put("lockWaits", rs.getString("lockWaits"));
				retValue.put("lockWaitsRate", rs.getString("lockWaitsRate"));
				retValue.put("lockTimeouts", rs.getString("lockTimeouts"));
				retValue.put("lockTimeoutsRate", rs
						.getString("lockTimeoutsRate"));
				retValue.put("deadLocks", rs.getString("deadLocks"));
				retValue.put("deadLocksRate", rs.getString("deadLocksRate"));
				retValue.put("avgWaitTime", rs.getString("avgWaitTime"));
				retValue
						.put("avgWaitTimeBase", rs.getString("avgWaitTimeBase"));
				retValue.put("latchWaits", rs.getString("latchWaits"));
				retValue.put("latchWaitsRate", rs.getString("latchWaitsRate"));
				retValue.put("avgLatchWait", rs.getString("avgLatchWait"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Vector getSqlserver_nmsinfo_v(String serverip) throws Exception {
		String slq = "select * from nms_sqlserverinfo_v where serverip = '"
				+ serverip + "'";
		Vector retValue = new Vector();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				Hashtable tempHashtable = new Hashtable();
				tempHashtable.put("spid", rs.getString("spid"));
				tempHashtable.put("waittime", rs.getString("waittime"));
				tempHashtable.put("lastwaittype", rs.getString("lastwaittype"));
				tempHashtable.put("waitresource", rs.getString("waitresource"));
				tempHashtable.put("dbname", rs.getString("dbname"));
				tempHashtable.put("username", rs.getString("username"));
				tempHashtable.put("cpu", rs.getString("cpu"));
				tempHashtable.put("physical_io", rs.getString("physical_io"));
				tempHashtable.put("memusage", rs.getString("memusage"));
				tempHashtable.put("login_time", rs.getString("login_time"));
				tempHashtable.put("last_batch", rs.getString("last_batch"));
				tempHashtable.put("status", rs.getString("status"));
				tempHashtable.put("hostname", rs.getString("hostname"));
				tempHashtable.put("program_name", rs.getString("program_name"));
				tempHashtable.put("hostprocess", rs.getString("hostprocess"));
				tempHashtable.put("cmd", rs.getString("cmd"));
				tempHashtable.put("nt_domain", rs.getString("nt_domain"));
				tempHashtable.put("nt_username", rs.getString("nt_username"));
				tempHashtable.put("net_library", rs.getString("net_library"));
				tempHashtable.put("loginame", rs.getString("loginame"));
				retValue.add(tempHashtable);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getSqlserver_nmsstatisticsHash(String serverip)
			throws Exception {
		String slq = "select * from nms_sqlserverstatisticshash where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("pingjun_lockWaits", rs.getString("pj_lockWaits"));
				retValue.put("pingjun_memoryGrantQueueWaits", rs
						.getString("pj_memGrantQueWaits"));
				retValue.put("pingjun_threadSafeMemoryObjectWaits", rs
						.getString("pj_thdSafeMemObjWaits"));
				retValue.put("pingjun_logWriteWaits", rs
						.getString("pj_logWriteWaits"));
				retValue.put("pingjun_logBufferWaits", rs
						.getString("pj_logBufferWaits"));
				retValue.put("pingjun_networkIOWaits", rs
						.getString("pj_networkIOWaits"));
				retValue.put("pingjun_pageIOLatchWaits", rs
						.getString("pj_pageIOLatchWaits"));
				retValue.put("pingjun_pageLatchWaits", rs
						.getString("pj_pageLatchWaits"));
				retValue.put("pingjun_nonPageLatchWaits", rs
						.getString("pj_nonPageLatchWaits"));
				retValue.put("pingjun_waitForTheWorker", rs
						.getString("pj_waitForTheWorker"));
				retValue.put("pingjun_workspaceSynchronizationWaits", rs
						.getString("pj_workspaceSynWaits"));
				retValue.put("pingjun_transactionOwnershipWaits", rs
						.getString("pj_traOwnershipWaits"));
				retValue
						.put("jingxing_lockWaits", rs.getString("jx_lockWaits"));
				retValue.put("jingxing_memoryGrantQueueWaits", rs
						.getString("jx_memGrantQueWaits"));
				retValue.put("jingxing_threadSafeMemoryObjectWaits", rs
						.getString("jx_thdSafeMemObjWaits"));
				retValue.put("jingxing_logWriteWaits", rs
						.getString("jx_logWriteWaits"));
				retValue.put("jingxing_logBufferWaits", rs
						.getString("jx_logBufferWaits"));
				retValue.put("jingxing_networkIOWaits", rs
						.getString("jx_networkIOWaits"));
				retValue.put("jingxing_pageIOLatchWaits", rs
						.getString("jx_pageIOLatchWaits"));
				retValue.put("jingxing_pageLatchWaits", rs
						.getString("jx_pageLatchWaits"));
				retValue.put("jingxing_nonPageLatchWaits", rs
						.getString("jx_nonPageLatchWaits"));
				retValue.put("jingxing_waitForTheWorker", rs
						.getString("jx_waitForTheWorker"));
				retValue.put("jingxing_workspaceSynchronizationWaits", rs
						.getString("jx_workspaceSynWaits"));
				retValue.put("jingxing_transactionOwnershipWaits", rs
						.getString("jx_traOwnershipWaits"));
				retValue.put("qidong_lockWaits", rs.getString("qd_lockWaits"));
				retValue.put("qidong_memoryGrantQueueWaits", rs
						.getString("qd_memGrantQueWaits"));
				retValue.put("qidong_threadSafeMemoryObjectWaits", rs
						.getString("qd_thdSafeMemObjWaits"));
				retValue.put("qidong_logWriteWaits", rs
						.getString("qd_logWriteWaits"));
				retValue.put("qidong_logBufferWaits", rs
						.getString("qd_logBufferWaits"));
				retValue.put("qidong_networkIOWaits", rs
						.getString("qd_networkIOWaits"));
				retValue.put("qidong_pageIOLatchWaits", rs
						.getString("qd_pageIOLatchWaits"));
				retValue.put("qidong_pageLatchWaits", rs
						.getString("qd_pageLatchWaits"));
				retValue.put("qidong_nonPageLatchWaits", rs
						.getString("qd_nonPageLatchWaits"));
				retValue.put("qidong_waitForTheWorker", rs
						.getString("qd_waitForTheWorker"));
				retValue.put("qidong_workspaceSynchronizationWaits", rs
						.getString("qd_workspaceSynWaits"));
				retValue.put("qidong_transactionOwnershipWaits", rs
						.getString("qd_traOwnershipWaits"));
				retValue.put("leiji_lockWaits", rs.getString("lj_lockWaits"));
				retValue.put("leiji_memoryGrantQueueWaits", rs
						.getString("lj_memGrantQueWaits"));
				retValue.put("leiji_threadSafeMemoryObjectWaits", rs
						.getString("lj_thdSafeMemObjWaits"));
				retValue.put("leiji_logWriteWaits", rs
						.getString("lj_logWriteWaits"));
				retValue.put("leiji_logBufferWaits", rs
						.getString("lj_logBufferWaits"));
				retValue.put("leiji_networkIOWaits", rs
						.getString("lj_networkIOWaits"));
				retValue.put("leiji_pageIOLatchWaits", rs
						.getString("lj_pageIOLatchWaits"));
				retValue.put("leiji_pageLatchWaits", rs
						.getString("lj_pageLatchWaits"));
				retValue.put("leiji_nonPageLatchWaits", rs
						.getString("lj_nonPageLatchWaits"));
				retValue.put("leiji_waitForTheWorker", rs
						.getString("lj_waitForTheWorker"));
				retValue.put("leiji_workspaceSynchronizationWaits", rs
						.getString("lj_workspaceSynWaits"));
				retValue.put("leiji_transactionOwnershipWaits", rs
						.getString("lj_traOwnershipWaits"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getSqlserver_nmsstatus(String serverip) throws Exception {
		String slq = "select * from nms_sqlserverstatus where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("status", rs.getString("status"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getSqlserver_nmsmems(String serverip) throws Exception {
		String slq = "select * from nms_sqlservermems where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(slq);
			while (rs.next()) {
				retValue.put("serverip", rs.getString("serverip"));
				retValue.put("totalMemory", rs.getString("totalMemory"));
				retValue.put("sqlMem", rs.getString("sqlMem"));
				retValue.put("optMemory", rs.getString("optMemory"));
				retValue
						.put("memGrantPending", rs.getString("memGrantPending"));
				retValue
						.put("memGrantSuccess", rs.getString("memGrantSuccess"));
				retValue.put("lockMem", rs.getString("lockMem"));
				retValue.put("conMemory", rs.getString("conMemory"));
				retValue.put("grantedWorkspaceMem", rs
						.getString("grantedWorkspaceMem"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getSqlserver_nmsdbvalue(String serverip) throws Exception {
		String sql_0 = "select usedperc,usedsize,sizes,logname from nms_sqlserverdbvalue where serverip = '"
				+ serverip + "' and label = '0'";
		String sql_1 = "select usedperc,usedsize,sizes,dbname from nms_sqlserverdbvalue where serverip = '"
				+ serverip + "' and label = '1'";
		String sql_2 = "select instance_name from nms_sqlserverdbvalue where serverip = '"
				+ serverip + "' and label = '2'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		Hashtable logfile = new Hashtable();
		Hashtable database = new Hashtable();
		Vector names = new Vector();
		String user_ = "";
		try {
			// sql_0
			rs = dbmanager.executeQuery(sql_0);
			while (rs.next()) {
				Hashtable temp = new Hashtable();
				temp.put("usedperc", rs.getString("usedperc"));
				temp.put("usedsize", rs.getString("usedsize"));
				temp.put("sizes", rs.getString("sizes"));
				temp.put("logname", rs.getString("logname"));
				logfile.put(rs.getString("logname"), temp);
			}
			// sql_1
			rs = dbmanager.executeQuery(sql_1);
			while (rs.next()) {
				Hashtable temp = new Hashtable();
				temp.put("usedperc", rs.getString("usedperc"));
				temp.put("usedsize", rs.getString("usedsize"));
				temp.put("sizes", rs.getString("sizes"));
				temp.put("dbname", rs.getString("dbname"));
				database.put(rs.getString("dbname"), temp);
			}
			// sql_2
			rs = dbmanager.executeQuery(sql_2);
			while (rs.next()) {
				names.add(rs.getString("instance_name"));
			}
			rs.close();
			retValue.put("logfile", logfile);
			retValue.put("database", database);
			retValue.put("names", names);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 插入Informix的配置信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exception
	 */
	public boolean addInformix_nmsconfig(String serverip, Hashtable configs)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			String cf_default = String.valueOf(configs.get("cf_default"));
			String cf_original = String.valueOf(configs.get("cf_original"));
			String cf_name = String.valueOf(configs.get("cf_name"));
			String cf_effective = String.valueOf(configs.get("cf_effective"));
			cf_default = cf_default.replaceAll("\\\\", "/");
			cf_original = cf_original.replaceAll("\\\\", "/");
			cf_name = cf_name.replaceAll("\\\\", "/");
			cf_effective = cf_effective.replaceAll("\\\\", "/");
			sBuffer
					.append("insert into nms_informixconfig(serverip, cf_default, cf_original,cf_name,cf_effective ,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(cf_default);
			sBuffer.append("','");
			sBuffer.append(cf_original);
			sBuffer.append("','");
			sBuffer.append(cf_name);
			sBuffer.append("','");
			sBuffer.append(cf_effective);
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 插入Informix的日志信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exception
	 */
	public boolean addInformix_nmslog(String serverip, Hashtable log)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_informixlog(serverip, is_backed_up, is_current,sizes,used ,is_temp,uniqid,is_archived,is_used,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(log.get("is_backed_up")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(log.get("is_current")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(log.get("size")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(log.get("used")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(log.get("is_temp")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(log.get("uniqid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(log.get("is_archived")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(log.get("is_used")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 插入Informix的数据库信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exception
	 */
	public boolean addInformix_nmsdatabase(String serverip, Hashtable database)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_informixdatabase(serverip, bufflog, createtime,log,dbserver ,gls,createuser,ansi,dbname,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(database.get("bufflog")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(database.get("createtime")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(database.get("log")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(database.get("dbserver")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(database.get("gls")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(database.get("createuser")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(database.get("ansi")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(database.get("dbname")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 插入Informix的session信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exception
	 */
	public boolean addInformix_nmssession(String serverip, Hashtable session)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_informixsession(serverip, bufwrites, pagwrites,pagreads,locksheld ,bufreads,accesses,");
			sBuffer
					.append("connected,username,lktouts,lockreqs,hostname,lockwts,deadlks,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("bufwrites")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("pagwrites")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("pagreads")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("locksheld")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("bufreads")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("access")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("connected")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("username")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("lktouts")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("lockreqs")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("hostname")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("lockwts")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(session.get("deadlks")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 插入Informix的其他信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exception
	 */
	public boolean addInformix_nmsabout(String serverip, Hashtable about)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_informixabout(serverip, name,value,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(about.get("name")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(about.get("value")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 插入Informix的io信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exception
	 */
	public boolean addInformix_nmsio(String serverip, Hashtable io)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_informixio(serverip, pagesread,readsstr,writes,mwrites,chunknum,mreads,pageswritten,mpagesread,mpageswritten,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(io.get("pagesread")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(io.get("reads")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(io.get("writes")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(io.get("mwrites")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(io.get("chunknum")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(io.get("mreads")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(io.get("pageswritten")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(io.get("mpagesread")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(io.get("mpageswritten")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 插入Informix的表空间信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exceptspacen
	 */
	public boolean addInformix_nmsspace(String serverip, Hashtable space)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_informixspace(serverip, owner,pages_free,dbspace,pages_size,pages_used,file_name,fname,percent_free,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(space.get("owner")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(space.get("pages_free")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(space.get("dbspace")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(space.get("pages_size")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(space.get("pages_used")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(space.get("file_name")).replaceAll(
					"\\\\", "/"));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(space.get("fname")).replaceAll(
					"\\\\", "/"));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(space.get("percent_free")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 插入Informix的锁信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exception
	 */
	public boolean addInformix_nmslock(String serverip, Hashtable lock)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_informixlock(serverip, username,hostname,dbsname,tabname,type,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lock.get("username")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lock.get("hostname")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lock.get("dbsname")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lock.get("tabname")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(lock.get("type")));
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 插入Informix的状态信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exceptstatusn
	 */
	public boolean addInformix_nmsstatus(String serverip, String status)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_informixstatus(serverip,status,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(status);
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 插入Informix的其他信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exceptstatusn
	 */
	public boolean addInformix_nmsother(String serverip,
			Hashtable informixOtherData) throws Exception {
		List<String> waitingThreads = null;
		String fgWrites = "";// 数据库性能监控FOREGROUND WRITE情况
		String lokwaits = "";// 等待锁的线程数量
		String ovbuff = "";// 缓冲池溢出数
		String ovlock = "";// 数据库锁溢出
		String deadlks = "";// 死锁数
		String bufwaits = "";// 数据库缓存容量Buffer等待情况
		String bufreads_cached = "";// 共享内存读命中率
		String bufwrits_cached = "";// 共享内存写命中率
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			if (informixOtherData != null) {
				dbmanager
						.executeUpdate("delete from nms_informixother where serverip='"
								+ serverip
								+ "' and entity in ('other','waitingThread')");
				if (informixOtherData.containsKey("waitingThreads")) {
					waitingThreads = (ArrayList<String>) informixOtherData
							.get("waitingThreads");
					if (waitingThreads != null && waitingThreads.size() > 0) {
						for (String waitingThread : waitingThreads) {
							StringBuffer sBuffer = new StringBuffer();
							sBuffer
									.append("insert into nms_informixother(serverip,entity,subentity,thevalue,mon_time)");
							sBuffer.append(" values('");
							sBuffer.append(serverip);
							sBuffer.append("','");
							sBuffer.append("waitingThread");
							sBuffer.append("','");
							sBuffer.append("waitingThread");
							sBuffer.append("','");
							sBuffer.append(waitingThread);
							// sBuffer.append("','");
							// sBuffer.append(montime);
							// sBuffer.append("')");
							if (SystemConstant.DBType.equals("mysql")) {
								sBuffer.append("','");
								sBuffer.append(montime);
								sBuffer.append("')");
							} else if (SystemConstant.DBType.equals("oracle")) {
								sBuffer.append("',to_date('" + montime
										+ "','yyyy-mm-dd hh24:mi:ss'))");
							}
							dbmanager.addBatch(sBuffer.toString());
							sBuffer = null;
						}
					}
				}
				if (informixOtherData.containsKey("fgWrites")) {
					fgWrites = (String) informixOtherData.get("fgWrites");
					if (fgWrites != null) {
						StringBuffer sBuffer = new StringBuffer();
						sBuffer
								.append("insert into nms_informixother(serverip,entity,subentity,thevalue,mon_time)");
						sBuffer.append(" values('");
						sBuffer.append(serverip);
						sBuffer.append("','");
						sBuffer.append("other");
						sBuffer.append("','");
						sBuffer.append("fgWrites");
						sBuffer.append("','");
						sBuffer.append(fgWrites);
						// sBuffer.append("','");
						// sBuffer.append(montime);
						// sBuffer.append("')");
						if (SystemConstant.DBType.equals("mysql")) {
							sBuffer.append("','");
							sBuffer.append(montime);
							sBuffer.append("')");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						dbmanager.addBatch(sBuffer.toString());
						sBuffer = null;
					}
				}
				if (informixOtherData.containsKey("lokwaits")) {
					lokwaits = (String) informixOtherData.get("lokwaits");
					if (lokwaits != null) {
						StringBuffer sBuffer = new StringBuffer();
						sBuffer
								.append("insert into nms_informixother(serverip,entity,subentity,thevalue,mon_time)");
						sBuffer.append(" values('");
						sBuffer.append(serverip);
						sBuffer.append("','");
						sBuffer.append("other");
						sBuffer.append("','");
						sBuffer.append("lokwaits");
						sBuffer.append("','");
						sBuffer.append(lokwaits);
						// sBuffer.append("','");
						// sBuffer.append(montime);
						// sBuffer.append("')");
						if (SystemConstant.DBType.equals("mysql")) {
							sBuffer.append("','");
							sBuffer.append(montime);
							sBuffer.append("')");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						dbmanager.addBatch(sBuffer.toString());
						sBuffer = null;
					}
				}
				if (informixOtherData.containsKey("ovbuff")) {
					ovbuff = (String) informixOtherData.get("ovbuff");
					if (ovbuff != null) {
						StringBuffer sBuffer = new StringBuffer();
						sBuffer
								.append("insert into nms_informixother(serverip,entity,subentity,thevalue,mon_time)");
						sBuffer.append(" values('");
						sBuffer.append(serverip);
						sBuffer.append("','");
						sBuffer.append("other");
						sBuffer.append("','");
						sBuffer.append("ovbuff");
						sBuffer.append("','");
						sBuffer.append(ovbuff);
						// sBuffer.append("','");
						// sBuffer.append(montime);
						// sBuffer.append("')");
						if (SystemConstant.DBType.equals("mysql")) {
							sBuffer.append("','");
							sBuffer.append(montime);
							sBuffer.append("')");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						dbmanager.addBatch(sBuffer.toString());
						sBuffer = null;
					}
				}
				if (informixOtherData.containsKey("ovlock")) {
					ovlock = (String) informixOtherData.get("ovlock");
					if (ovlock != null) {
						StringBuffer sBuffer = new StringBuffer();
						sBuffer
								.append("insert into nms_informixother(serverip,entity,subentity,thevalue,mon_time)");
						sBuffer.append(" values('");
						sBuffer.append(serverip);
						sBuffer.append("','");
						sBuffer.append("other");
						sBuffer.append("','");
						sBuffer.append("ovlock");
						sBuffer.append("','");
						sBuffer.append(ovlock);
						// sBuffer.append("','");
						// sBuffer.append(montime);
						// sBuffer.append("')");
						if (SystemConstant.DBType.equals("mysql")) {
							sBuffer.append("','");
							sBuffer.append(montime);
							sBuffer.append("')");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						dbmanager.addBatch(sBuffer.toString());
						sBuffer = null;
					}
				}
				if (informixOtherData.containsKey("deadlks")) {
					deadlks = (String) informixOtherData.get("deadlks");
					if (deadlks != null) {
						StringBuffer sBuffer = new StringBuffer();
						sBuffer
								.append("insert into nms_informixother(serverip,entity,subentity,thevalue,mon_time)");
						sBuffer.append(" values('");
						sBuffer.append(serverip);
						sBuffer.append("','");
						sBuffer.append("other");
						sBuffer.append("','");
						sBuffer.append("deadlks");
						sBuffer.append("','");
						sBuffer.append(deadlks);
						// sBuffer.append("','");
						// sBuffer.append(montime);
						// sBuffer.append("')");
						if (SystemConstant.DBType.equals("mysql")) {
							sBuffer.append("','");
							sBuffer.append(montime);
							sBuffer.append("')");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						dbmanager.addBatch(sBuffer.toString());
						sBuffer = null;
					}
				}
				if (informixOtherData.containsKey("bufwaits")) {
					bufwaits = (String) informixOtherData.get("bufwaits");
					if (bufwaits != null) {
						StringBuffer sBuffer = new StringBuffer();
						sBuffer
								.append("insert into nms_informixother(serverip,entity,subentity,thevalue,mon_time)");
						sBuffer.append(" values('");
						sBuffer.append(serverip);
						sBuffer.append("','");
						sBuffer.append("other");
						sBuffer.append("','");
						sBuffer.append("bufwaits");
						sBuffer.append("','");
						sBuffer.append(bufwaits);
						// sBuffer.append("','");
						// sBuffer.append(montime);
						// sBuffer.append("')");
						if (SystemConstant.DBType.equals("mysql")) {
							sBuffer.append("','");
							sBuffer.append(montime);
							sBuffer.append("')");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						dbmanager.addBatch(sBuffer.toString());
						sBuffer = null;
					}
				}
				if (informixOtherData.containsKey("bufreads_cached")) {
					bufreads_cached = (String) informixOtherData
							.get("bufreads_cached");
					if (bufreads_cached != null) {
						StringBuffer sBuffer = new StringBuffer();
						sBuffer
								.append("insert into nms_informixother(serverip,entity,subentity,thevalue,mon_time)");
						sBuffer.append(" values('");
						sBuffer.append(serverip);
						sBuffer.append("','");
						sBuffer.append("other");
						sBuffer.append("','");
						sBuffer.append("bufreads_cached");
						sBuffer.append("','");
						sBuffer.append(bufreads_cached);
						// sBuffer.append("','");
						// sBuffer.append(montime);
						// sBuffer.append("')");
						if (SystemConstant.DBType.equals("mysql")) {
							sBuffer.append("','");
							sBuffer.append(montime);
							sBuffer.append("')");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						dbmanager.addBatch(sBuffer.toString());
						sBuffer = null;
					}
				}
				if (informixOtherData.containsKey("bufwrits_cached")) {
					bufwrits_cached = (String) informixOtherData
							.get("bufwrits_cached");
					if (bufwrits_cached != null) {
						StringBuffer sBuffer = new StringBuffer();
						sBuffer
								.append("insert into nms_informixother(serverip,entity,subentity,thevalue,mon_time)");
						sBuffer.append(" values('");
						sBuffer.append(serverip);
						sBuffer.append("','");
						sBuffer.append("other");
						sBuffer.append("','");
						sBuffer.append("bufwrits_cached");
						sBuffer.append("','");
						sBuffer.append(bufwrits_cached);
						// sBuffer.append("','");
						// sBuffer.append(montime);
						// sBuffer.append("')");
						if (SystemConstant.DBType.equals("mysql")) {
							sBuffer.append("','");
							sBuffer.append(montime);
							sBuffer.append("')");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						dbmanager.addBatch(sBuffer.toString());
						sBuffer = null;
					}
				}
				dbmanager.executeBatch();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 得到其他informix的指标信息（127.0.0.1_informix.log采集的数据）
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getInformix_nmsother(String serverip) throws Exception {
		Hashtable retHash = new Hashtable();
		List<String> waitingThreads = new ArrayList<String>();
		String fgWrites = "";// 数据库性能监控FOREGROUND WRITE情况
		String lokwaits = "";// 等待锁的线程数量
		String ovbuff = "";// 缓冲池溢出数
		String ovlock = "";// 数据库锁溢出
		String deadlks = "";// 死锁数
		String bufwaits = "";// 数据库缓存容量Buffer等待情况
		String bufreads_cached = "";// 共享内存读命中率
		String bufwrits_cached = "";// 共享内存写命中率
		StringBuffer sBuffer = new StringBuffer();
		sBuffer
				.append("select thevalue from nms_informixother where serverip = '");
		sBuffer.append(serverip);
		sBuffer.append("' and entity = 'waitingThread'");
		StringBuffer sBufferTwo = new StringBuffer();
		sBufferTwo
				.append("select subentity,thevalue from nms_informixother where serverip = '");
		sBufferTwo.append(serverip);
		sBufferTwo.append("' and entity = 'other'");
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sBuffer.toString());
			if (rs != null) {
				while (rs.next()) {
					waitingThreads.add(rs.getString("thevalue"));
				}
			}
			rs.close();
			rs = dbmanager.executeQuery(sBufferTwo.toString());
			if (rs != null) {
				while (rs.next()) {
					retHash.put(rs.getString("subentity"), rs
							.getString("thevalue"));
				}
			}
			retHash.put("waitingThreads", waitingThreads);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			dbmanager.close();
			sBuffer = null;
			sBufferTwo = null;
		}
		return retHash;
	}

	/**
	 * 得到Informix的配置信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getInformix_nmsconfig(String serverip) throws Exception {
		String sql = "select * from nms_informixconfig where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		Hashtable itemHash = null;
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				itemHash = new Hashtable();
				itemHash.put("cf_default", rs.getString("cf_default"));
				itemHash.put("cf_original", rs.getString("cf_original"));
				itemHash.put("cf_name", rs.getString("cf_name"));
				itemHash.put("cf_effective", rs.getString("cf_effective"));
				retValue.add(itemHash);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到Informix的日志信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getInformix_nmslog(String serverip) throws Exception {
		String sql = "select * from nms_informixlog where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		Hashtable itemHash = null;
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				itemHash = new Hashtable();
				itemHash.put("is_backed_up", rs.getString("is_backed_up"));
				itemHash.put("is_current", rs.getString("is_current"));
				itemHash.put("size", rs.getString("sizes"));
				itemHash.put("used", rs.getString("used"));
				itemHash.put("is_temp", rs.getString("is_temp"));
				itemHash.put("uniqid", rs.getString("uniqid"));
				itemHash.put("is_archived", rs.getString("is_archived"));
				itemHash.put("is_used", rs.getString("is_used"));
				retValue.add(itemHash);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到Informix的database数据库信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getInformix_nmsdatabase(String serverip) throws Exception {
		String sql = "select * from nms_informixdatabase where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		Hashtable itemHash = null;
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				itemHash = new Hashtable();
				itemHash.put("bufflog", rs.getString("bufflog"));
				itemHash.put("createtime", rs.getString("createtime"));
				itemHash.put("log", rs.getString("log"));
				itemHash.put("dbserver", rs.getString("dbserver"));
				itemHash.put("gls", rs.getString("gls"));
				itemHash.put("createuser", rs.getString("createuser"));
				itemHash.put("ansi", rs.getString("ansi"));
				itemHash.put("dbname", rs.getString("dbname"));
				retValue.add(itemHash);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到Informix的session信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getInformix_nmssession(String serverip) throws Exception {
		String sql = "select * from nms_informixsession where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		Hashtable itemHash = null;
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				itemHash = new Hashtable();
				itemHash.put("bufwrites", rs.getString("bufwrites"));
				itemHash.put("pagwrites", rs.getString("pagwrites"));
				itemHash.put("pagreads", rs.getString("pagreads"));
				itemHash.put("locksheld", rs.getString("locksheld"));
				itemHash.put("bufreads", rs.getString("bufreads"));
				itemHash.put("access", rs.getString("accesses"));
				itemHash.put("connected", rs.getString("connected"));
				itemHash.put("username", rs.getString("username"));
				itemHash.put("lktouts", rs.getString("lktouts"));
				itemHash.put("lockreqs", rs.getString("lockreqs"));
				itemHash.put("hostname", rs.getString("hostname"));
				itemHash.put("lockwts", rs.getString("lockwts"));
				itemHash.put("deadlks", rs.getString("deadlks"));
				retValue.add(itemHash);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 插入Informix的其他信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exceptstatusn
	 */
	public boolean addInformix_nmsbaractlog(String serverip,
			Hashtable informixbaractData) throws Exception {
		String[] baractlogs = null;
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			if (informixbaractData != null) {
				dbmanager
						.executeUpdate("delete from nms_informixbaractlog where serverip='"
								+ serverip
								+ "' and entity in ('lastBackDateToThisBackDay','backdate')");
				if (informixbaractData.containsKey("baractlogs")) {
					baractlogs = (String[]) informixbaractData
							.get("baractlogs");
					for (String baractlog : baractlogs) {
						if (baractlog != null && !baractlog.trim().equals("")) {
							StringBuffer sBuffer = new StringBuffer();
							sBuffer
									.append("insert into nms_informixbaractlog(serverip,entity,subentity,thevalue,mon_time)");
							sBuffer.append(" values('");
							sBuffer.append(serverip);
							sBuffer.append("','");
							sBuffer.append("baractlogs");
							sBuffer.append("','");
							sBuffer.append("baractlogs");
							sBuffer.append("','");
							sBuffer.append(baractlog);
							// sBuffer.append("','");
							// sBuffer.append(montime);
							// sBuffer.append("')");
							if (SystemConstant.DBType.equals("mysql")) {
								sBuffer.append("','");
								sBuffer.append(montime);
								sBuffer.append("')");
							} else if (SystemConstant.DBType.equals("oracle")) {
								sBuffer.append("',to_date('" + montime
										+ "','yyyy-mm-dd hh24:mi:ss'))");
							}
							dbmanager.addBatch(sBuffer.toString());
							sBuffer = null;
						}
					}
				}
				if (informixbaractData.containsKey("backdate")) {
					String backdate = (String) informixbaractData
							.get("backdate");
					if (backdate != null && !backdate.trim().equals("")) {
						StringBuffer sBuffer = new StringBuffer();
						sBuffer
								.append("insert into nms_informixbaractlog(serverip,entity,subentity,thevalue,mon_time)");
						sBuffer.append(" values('");
						sBuffer.append(serverip);
						sBuffer.append("','");
						sBuffer.append("backdate");
						sBuffer.append("','");
						sBuffer.append("backdate");
						sBuffer.append("','");
						sBuffer.append(backdate);
						// sBuffer.append("','");
						// sBuffer.append(montime);
						// sBuffer.append("')");
						if (SystemConstant.DBType.equals("mysql")) {
							sBuffer.append("','");
							sBuffer.append(montime);
							sBuffer.append("')");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						dbmanager.addBatch(sBuffer.toString());
						sBuffer = null;
					}
				}
				// if(informixbaractData.containsKey("lastBackDateToThisBackDay")){
				// String lastBackDateToThisBackDay =
				// (String)informixbaractData.get("lastBackDateToThisBackDay");
				// StringBuffer sBuffer = new StringBuffer();
				// sBuffer.append("insert into
				// nms_informixbaractlog(serverip,entity,subentity,thevalue,mon_time)");
				// sBuffer.append(" values('");
				// sBuffer.append(serverip);
				// sBuffer.append("','");
				// sBuffer.append("lastBackDateToThisBackDay");
				// sBuffer.append("','");
				// sBuffer.append("lastBackDateToThisBackDay");
				// sBuffer.append("','");
				// sBuffer.append(lastBackDateToThisBackDay);
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				// dbmanager.addBatch(sBuffer.toString());
				// sBuffer = null;
				// }
			}
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 获取上一次0级保存的日期
	 * 
	 * @return
	 */
	public String getInformix_nmsbaractBackTime(String serverip) {
		// 获取最后一条记录
		String backdate = "";
		StringBuffer sBuffer = new StringBuffer(
				"select thevalue from nms_informixbaractlog where serverip = '");
		sBuffer.append(serverip);
		sBuffer.append("' and entity = 'backdate'");
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sBuffer.toString());
			if (rs != null && rs.next()) {
				backdate = rs.getString("thevalue");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			dbmanager.close();
		}
		return backdate;
	}

	/**
	 * 得到Informix的其他信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getInformix_nmsabout(String serverip) throws Exception {
		String sql = "select * from nms_informixabout where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		Hashtable itemHash = null;
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				itemHash = new Hashtable();
				itemHash.put("name", rs.getString("name"));
				itemHash.put("value", rs.getString("value"));
				retValue.add(itemHash);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到Informix的io信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getInformix_nmsio(String serverip) throws Exception {
		String sql = "select * from nms_informixio where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		Hashtable itemHash = null;
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				itemHash = new Hashtable();
				itemHash.put("pagesread", rs.getString("pagesread"));
				itemHash.put("reads", rs.getString("readsstr"));
				itemHash.put("writes", rs.getString("writes"));
				itemHash.put("mwrites", rs.getString("mwrites"));
				itemHash.put("chunknum", rs.getString("chunknum"));
				itemHash.put("mreads", rs.getString("mreads"));
				itemHash.put("pageswritten", rs.getString("pageswritten"));
				itemHash.put("mpagesread", rs.getString("mpagesread"));
				itemHash.put("mpageswritten", rs.getString("mpageswritten"));
				retValue.add(itemHash);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	public Hashtable getInformix_nmsbaractlog(String serverip,
			String showAllLogFlag) throws Exception {
		Hashtable retHash = new Hashtable();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 获取最后一条记录
		StringBuffer sBuffer = null;
		if (showAllLogFlag.equals("0")) {// 所有
			sBuffer = new StringBuffer(
					"select thevalue from nms_informixbaractlog where serverip = '");
			sBuffer.append(serverip);
			sBuffer.append("' and entity = 'baractlogs'");
		} else if (showAllLogFlag.equals("1")) {// 包含warn
			sBuffer = new StringBuffer(
					"select thevalue from nms_informixbaractlog where serverip = '");
			sBuffer.append(serverip);
			sBuffer
					.append("' and entity = 'baractlogs' and thevalue like '%warn%'");
		} else if (showAllLogFlag.equals("2")) {
			sBuffer = new StringBuffer(
					"select thevalue from nms_informixbaractlog where serverip = '");
			sBuffer.append(serverip);
			sBuffer
					.append("' and entity = 'baractlogs' and thevalue like '%error%'");
		} else if (showAllLogFlag.equals("3")) {
			sBuffer = new StringBuffer(
					"select thevalue from nms_informixbaractlog where serverip = '");
			sBuffer.append(serverip);
			sBuffer
					.append("' and entity = 'baractlogs' and (thevalue like '%error%' or thevalue like '%error%')");
		}
		StringBuffer sBufferbackdate = new StringBuffer(
				"select thevalue from nms_informixbaractlog where serverip = '");
		sBufferbackdate.append(serverip);
		sBufferbackdate.append("' and entity = 'backdate'");
		// StringBuffer sBufferlastBackDateToThisBackDay = new
		// StringBuffer("select thevalue from nms_informixbaractlog where
		// serverip = '");
		// sBufferlastBackDateToThisBackDay.append(serverip);
		// sBufferlastBackDateToThisBackDay.append("' and entity =
		// 'lastBackDateToThisBackDay'");
		// 取出最后一条0级备份的日期
		StringBuffer sBufferlastBackDateToThisBackDay = new StringBuffer(
				"select * from nms_informixbaractlog where  serverip = '");
		sBufferlastBackDateToThisBackDay.append(serverip);
		sBufferlastBackDateToThisBackDay
				.append("' and thevalue like '%onbar_d -b -l%' and id = (select max(id) from  nms_informixbaractlog where serverip = '");
		sBufferlastBackDateToThisBackDay.append(serverip);
		sBufferlastBackDateToThisBackDay
				.append("' and thevalue like '%onbar_d -b -l%')");

		DBManager dbmanager = new DBManager();
		Vector<String> baractlogs = null;
		String backdate = "";
		String lastSaveDate = "";
		String lastBackDateToThisBackDay = "";
		try {
			rs = dbmanager.executeQuery(sBuffer.toString());
			if (rs != null) {
				baractlogs = new Vector<String>();
				while (rs.next()) {
					baractlogs.add(rs.getString("thevalue"));
				}
			}
			rs.close();
			rs = dbmanager.executeQuery(sBufferbackdate.toString());
			if (rs != null && rs.next()) {
				backdate = rs.getString("thevalue");
			}
			rs.close();
			rs = dbmanager.executeQuery(sBufferlastBackDateToThisBackDay
					.toString());
			if (rs != null && rs.next()) {
				lastSaveDate = rs.getString("thevalue");
				if (lastSaveDate != null) {
					lastSaveDate = lastSaveDate.trim().split("\\s++")[0];
					long d = (new Date().getTime() - sdf.parse(lastSaveDate)
							.getTime())
							/ (24 * 3600 * 1000);
					lastBackDateToThisBackDay = String.valueOf(d);
				}
			}
			rs.close();
			retHash.put("lastBackDateToThisBackDay", lastBackDateToThisBackDay);
			retHash.put("baractlogs", baractlogs);
			retHash.put("backdate", backdate);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			dbmanager.close();
		}
		return retHash;
	}

	/**
	 * 得到Informix的表空间信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getInformix_nmsspace(String serverip) throws Exception {
		String sql = "select * from nms_informixspace where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		Hashtable itemHash = null;
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				itemHash = new Hashtable();
				itemHash.put("owner", rs.getString("owner"));
				itemHash.put("pages_free", rs.getString("pages_free"));
				itemHash.put("dbspace", rs.getString("dbspace"));
				itemHash.put("pages_size", rs.getString("pages_size"));
				itemHash.put("pages_used", rs.getString("pages_used"));
				itemHash.put("file_name", rs.getString("file_name"));
				itemHash.put("fname", rs.getString("fname"));
				itemHash.put("percent_free", rs.getString("percent_free"));
				retValue.add(itemHash);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到Informix的锁信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getInformix_nmslock(String serverip) throws Exception {
		String sql = "select * from nms_informixlock where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		Hashtable itemHash = null;
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				itemHash = new Hashtable();
				itemHash.put("username", rs.getString("username"));
				itemHash.put("hostname", rs.getString("hostname"));
				itemHash.put("dbsname", rs.getString("dbsname"));
				itemHash.put("tabname", rs.getString("tabname"));
				itemHash.put("type", rs.getString("type"));
				retValue.add(itemHash);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到Informix的状态信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getInformix_nmsstatus(String serverip) throws Exception {
		String sql = "select * from nms_informixstatus where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				retValue.put("status", String.valueOf(rs.getString("status")));
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 根据ip地址、关键字、时间段取出informix的log信息
	 * 
	 * @param starttime
	 * @param toime
	 * @param serverip
	 * @param detail
	 * @return
	 */
	public List getInformixLogList(String starttime, String toime,
			String serverip, String detail) {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("select * from informixlog");
		sBuffer.append(serverip);
		sBuffer.append(" where collecttime >= '");
		sBuffer.append(starttime);
		sBuffer.append("' and collecttime <= '");
		sBuffer.append(toime);
		sBuffer.append("'");
		if (detail != null && !detail.equals("")) {
			sBuffer.append(" and detail like '%");
			sBuffer.append(detail);
			sBuffer.append("%'");
		}
		// SysLogger.info(sBuffer.toString());
		List retValue = new ArrayList();
		Hashtable itemHash = null;
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sBuffer.toString());
			while (rs.next()) {
				itemHash = new Hashtable();
				itemHash.put("id", rs.getString("id"));
				itemHash.put("dbnodeid", rs.getString("dbnodeid"));
				itemHash.put("detail", rs.getString("detail"));
				itemHash.put("collecttime", rs.getString("collecttime"));
				retValue.add(itemHash);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;

	}

	/**
	 * 插入Sybase的状态信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exceptstatusn
	 */
	public boolean addSybase_nmsstatus(String serverip, String status)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sybasestatus(serverip,status,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(status);
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'),");
			}
			 
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将Sybase的性能信息插入到数据库
	 * 
	 * @param serverip
	 * @param sybaseVO
	 * @return
	 * @throws Exception
	 */
	public boolean addSybase_nmsperformance(String serverip, SybaseVO sybaseVO)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_sybaseperformance(serverip, cpu_busy, idle,version,io_busy ,");
			sBuffer
					.append("Sent_rate,Received_rate,Write_rate,Read_rate,ServerName,Cpu_busy_rate,Io_busy_rate,");
			sBuffer
					.append("Disk_count,Locks_count,Xact_count,Total_dataCache,Total_physicalMemory,Metadata_cache,");
			sBuffer
					.append("Procedure_cache,Total_logicalMemory,Data_hitrate,Procedure_hitrate,mon_time,");
			sBuffer
					.append("offlineengine,maxconn,usedconn,processcount,sleepingproccount,maxlock,");
			sBuffer.append("longtrans,maxPageSize,totallog,freelog,usedlog,");
			sBuffer
					.append("reservedlog,logusedperc,maxsegusedperc,curdate,boottime)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getCpu_busy());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getIdle());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getVersion());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getIo_busy());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getSent_rate());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getReceived_rate());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getWrite_rate());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getRead_rate());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getServerName());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getCpu_busy_rate());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getIo_busy_rate());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getDisk_count());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getLocks_count());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getXact_count());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getTotal_dataCache());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getTotal_physicalMemory());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getMetadata_cache());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getProcedure_cache());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getTotal_logicalMemory());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getData_hitrate());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getProcedure_hitrate());
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("','");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("','");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'),'");
			}
			sBuffer.append(sybaseVO.getOfflineengine());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getMaxconn());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getUsedconn());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getProcesscount());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getSleepingproccount());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getMaxlock());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getLongtrans());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getMaxPageSize());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getTotallog());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getFreelog());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getUsedlog());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getReservedlog());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getLogusedperc());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getMaxsegusedperc());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getCurdate());
			sBuffer.append("','");
			sBuffer.append(sybaseVO.getBoottime());
			sBuffer.append("')");
//			 SysLogger.info(sBuffer.toString());
			// System.out.println(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将Sybase的数据库信息插入到数据库
	 * 
	 * @param serverip
	 * @param sybaseVO
	 * @return
	 * @throws Exception
	 */
	public boolean addSybase_nmsdbinfo(String serverip, List dbInfo)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			for (int i = 0; i < dbInfo.size(); i++) {
				TablesVO tablesVO = (TablesVO) dbInfo.get(i);
				StringBuffer sBuffer = new StringBuffer();
				sBuffer
						.append("insert into nms_sybasedbinfo(serverip,db_created,db_freesize ,db_namer,db_owner,db_size,db_status,db_usedperc,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(tablesVO.getDb_created());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getDb_freesize());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getDb_name());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getDb_owner());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getDb_size());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getDb_status());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getDb_usedperc());
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
				dbmanager.addBatch(sBuffer.toString());
			}
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将Sybase的数据库详细信息插入到数据库
	 * 
	 * @param serverip
	 * @param sybaseVO
	 * @return
	 * @throws Exception
	 */
	public boolean addSybase_nmsdbdetailinfo(String serverip, List dbInfo)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			for (int i = 0; i < dbInfo.size(); i++) {
				/*
				 * tvo.setName(rs.getString("name"));
				 * tvo.setCreatedate(rs.getString("crdate"));
				 * tvo.setDumptrdate(rs.getString("dumptrdate"));
				 * tvo.setStatus(rs.getString("status"));
				 */
				TablesVO tablesVO = (TablesVO) dbInfo.get(i);
				StringBuffer sBuffer = new StringBuffer();
				sBuffer
						.append("insert into nms_sybasedbdetailinfo(serverip,name,status,db_created,dumptrdate,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(tablesVO.getName());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getStatus());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getCreatedate());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getDumptrdate());
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
				// System.out.println(sBuffer.toString());
				dbmanager.addBatch(sBuffer.toString());
			}
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将Sybase的设备信息插入到数据库
	 * 
	 * @param serverip
	 * @param sybaseVO
	 * @return
	 * @throws Exception
	 */
	public boolean addSybase_nmsdeviceinfo(String serverip, List deviceInfo)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			for (int i = 0; i < deviceInfo.size(); i++) {
				TablesVO tablesVO = (TablesVO) deviceInfo.get(i);
				StringBuffer sBuffer = new StringBuffer();
				sBuffer
						.append("insert into nms_sybasedeviceinfo(serverip,device_description ,device_name,device_physical_name,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(tablesVO.getDevice_description().trim());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getDevice_name().trim());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getDevice_physical_name().trim()
						.replaceAll("\\\\", "/"));
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
				// System.out.println(sBuffer.toString());
				dbmanager.addBatch(sBuffer.toString());
			}
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将Sybase的进程信息插入到数据库
	 * 
	 * @param serverip
	 * @param sybaseVO
	 * @return
	 * @throws Exception
	 */
	public boolean addSybase_nmsprocessinfo(String serverip, List deviceInfo)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
//            System.out.println("==========>"+deviceInfo.size());			
			for (int i = 0; i < deviceInfo.size(); i++) {
				TablesVO tablesVO = (TablesVO) deviceInfo.get(i);
				StringBuffer sBuffer = new StringBuffer();
				sBuffer
						.append("insert into nms_sybaseprocessinfo(serverip,spid,hostname,status,hostprocess,program_name,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(tablesVO.getSpid());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getHostname());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getProstatus());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getHostprocess());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getProgram_name());
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
//				 System.out.println(sBuffer.toString());
				dbmanager.addBatch(sBuffer.toString());
			}
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将Sybase的用户信息插入到数据库
	 * 
	 * @param serverip
	 * @param sybaseVO
	 * @return
	 * @throws Exception
	 */
	public boolean addSybase_nmsuserinfo(String serverip, List userInfo)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			for (int i = 0; i < userInfo.size(); i++) {
				TablesVO tablesVO = (TablesVO) userInfo.get(i);
				StringBuffer sBuffer = new StringBuffer();
				sBuffer
						.append("insert into nms_sybaseuserinfo(serverip,Group_name ,ID_in_db,Login_name,Users_name,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(tablesVO.getGroup_name().trim());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getID_in_db().trim());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getLogin_name());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getUsers_name().trim());
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
				// System.out.println(sBuffer.toString());
				dbmanager.addBatch(sBuffer.toString());
			}
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将Sybase的服务器信息插入到数据库
	 * 
	 * @param serverip
	 * @param sybaseVO
	 * @return
	 * @throws Exception
	 */
	public boolean addSybase_nmsserversinfo(String serverip, List serversInfo)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			for (int i = 0; i < serversInfo.size(); i++) {
				TablesVO tablesVO = (TablesVO) serversInfo.get(i);
				StringBuffer sBuffer = new StringBuffer();
				sBuffer
						.append("insert into nms_sybaseserversinfo(serverip,Server_class ,Server_name,Server_network_name,Server_status,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(tablesVO.getServer_class());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getServer_name());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getServer_network_name());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getServer_status());
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
				// System.out.println(sBuffer.toString());
				dbmanager.addBatch(sBuffer.toString());
			}
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将Sybase的engine信息插入到数据库
	 * 
	 * @param serverip
	 * @param sybaseVO
	 * @return
	 * @throws Exception
	 */
	public boolean addSybase_nmsengineinfo(String serverip, List engineInfo)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			for (int i = 0; i < engineInfo.size(); i++) {
				TablesVO tablesVO = (TablesVO) engineInfo.get(i);
				StringBuffer sBuffer = new StringBuffer();
				sBuffer
						.append("insert into nms_sybaseengineinfo(serverip,engine ,status,starttime,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(tablesVO.getEngine());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getStatus());
				sBuffer.append("','");
				sBuffer.append(tablesVO.getStarttime());
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
				// System.out.println(sBuffer.toString());
				dbmanager.addBatch(sBuffer.toString());
			}
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 得到Sybase的状态信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getSybase_nmsstatus(String serverip) throws Exception {
		String sql = "select * from nms_sybasestatus where serverip = '"
				+ serverip + "'";
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				retValue.put("status", String.valueOf(rs.getString("status")));
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到sybase的性能信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public SybaseVO getSybase_nmssybaseperformance(String serverip)
			throws Exception {
		String sql = "select * from nms_sybaseperformance where serverip = '"
				+ serverip + "'";
		SybaseVO sybaseVO = new SybaseVO();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				sybaseVO.setCpu_busy(rs.getString("cpu_busy"));
				sybaseVO.setIdle(rs.getString("idle"));
				sybaseVO.setVersion(rs.getString("version"));
				sybaseVO.setIo_busy(rs.getString("io_busy"));
				sybaseVO.setSent_rate(rs.getString("Sent_rate"));
				sybaseVO.setReceived_rate(rs.getString("Received_rate"));
				sybaseVO.setWrite_rate(rs.getString("Write_rate"));
				sybaseVO.setRead_rate(rs.getString("Read_rate"));
				sybaseVO.setServerName(rs.getString("ServerName"));
				sybaseVO.setCpu_busy_rate(rs.getString("Cpu_busy_rate"));
				sybaseVO.setIo_busy_rate(rs.getString("Io_busy_rate"));
				sybaseVO.setDisk_count(rs.getString("Disk_count"));
				sybaseVO.setLocks_count(rs.getString("Locks_count"));
				sybaseVO.setXact_count(rs.getString("Xact_count"));
				sybaseVO.setTotal_dataCache(rs.getString("Total_dataCache"));
				sybaseVO.setTotal_physicalMemory(rs
						.getString("Total_physicalMemory"));
				sybaseVO.setMetadata_cache(rs.getString("Metadata_cache"));
				sybaseVO.setProcedure_cache(rs.getString("Procedure_cache"));
				sybaseVO.setTotal_logicalMemory(rs
						.getString("Total_logicalMemory"));
				sybaseVO.setData_hitrate(rs.getString("Data_hitrate"));
				sybaseVO
						.setProcedure_hitrate(rs.getString("Procedure_hitrate"));

				sybaseVO.setOfflineengine(rs.getString("offlineengine"));
				sybaseVO.setMaxconn(rs.getString("maxconn"));
				sybaseVO.setUsedconn(rs.getString("usedconn"));
				sybaseVO.setProcesscount(rs.getString("processcount"));
				sybaseVO
						.setSleepingproccount(rs.getString("sleepingproccount"));
				sybaseVO.setMaxlock(rs.getString("maxlock"));
				sybaseVO.setLongtrans(rs.getString("longtrans"));
				sybaseVO.setMaxPageSize(rs.getString("maxPageSize"));
				sybaseVO.setTotallog(rs.getString("totallog"));
				sybaseVO.setFreelog(rs.getString("freelog"));
				sybaseVO.setUsedlog(rs.getString("usedlog"));
				sybaseVO.setReservedlog(rs.getString("reservedlog"));
				sybaseVO.setLogusedperc(rs.getString("logusedperc"));
				sybaseVO.setMaxsegusedperc(rs.getString("maxsegusedperc"));
				sybaseVO.setCurdate(rs.getString("curdate"));
				sybaseVO.setBoottime(rs.getString("boottime"));

			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return sybaseVO;
	}

	/**
	 * 得到sybase的数据库信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getSybase_nmsdbinfo(String serverip) throws Exception {
		String sql = "select * from nms_sybasedbinfo where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				TablesVO tablesVO = new TablesVO();
				tablesVO.setDb_created(rs.getString("db_created"));
				tablesVO.setDb_freesize(rs.getString("db_freesize"));
				tablesVO.setDb_name(rs.getString("db_namer"));
				tablesVO.setDb_owner(rs.getString("db_owner"));
				tablesVO.setDb_size(rs.getString("db_size"));
				tablesVO.setDb_status(rs.getString("db_status"));
				tablesVO.setDb_usedperc(rs.getString("db_usedperc"));
				retValue.add(tablesVO);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到sybase的数据库信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getSybase_nmsdbdetailsinfo(String serverip) throws Exception {
		String sql = "select * from nms_sybasedbdetailinfo where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				TablesVO tablesVO = new TablesVO();
				tablesVO.setDb_created(rs.getString("db_created"));
				tablesVO.setStatus(rs.getString("status"));
				tablesVO.setName(rs.getString("name"));
				tablesVO.setDumptrdate(rs.getString("dumptrdate"));
				retValue.add(tablesVO);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到sybase的设备信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getSybase_nmsdeviceinfo(String serverip) throws Exception {
		String sql = "select * from nms_sybasedeviceinfo where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				TablesVO tablesVO = new TablesVO();
				tablesVO.setDevice_description(rs
						.getString("device_description"));
				tablesVO.setDevice_name(rs.getString("device_name"));
				tablesVO.setDevice_physical_name(rs
						.getString("device_physical_name"));
				retValue.add(tablesVO);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到sybase的进程信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getSybase_nmsprocessinfo(String serverip) throws Exception {
		String sql = "select * from nms_sybaseprocessinfo where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				TablesVO tablesVO = new TablesVO();
				tablesVO.setSpid(rs.getString("spid"));
				tablesVO.setHostname(rs.getString("hostname"));
				tablesVO.setProstatus(rs.getString("status"));
				tablesVO.setHostprocess(rs.getString("hostprocess"));
				tablesVO.setProgram_name(rs.getString("program_name"));
				retValue.add(tablesVO);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到sybase的用户信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getSybase_nmsuserinfo(String serverip) throws Exception {
		String sql = "select * from nms_sybaseuserinfo where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				TablesVO tablesVO = new TablesVO();
				tablesVO.setGroup_name(rs.getString("Group_name"));
				tablesVO.setID_in_db(rs.getString("ID_in_db"));
				tablesVO.setLogin_name(rs.getString("Login_name"));
				tablesVO.setUsers_name(rs.getString("Users_name"));
				retValue.add(tablesVO);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到sybase的服务器信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getSybase_nmsserversinfo(String serverip) throws Exception {
		String sql = "select * from nms_sybaseserversinfo where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				TablesVO tablesVO = new TablesVO();
				tablesVO.setServer_class(rs.getString("Server_class"));
				tablesVO.setServer_name(rs.getString("Server_name"));
				tablesVO.setServer_network_name(rs
						.getString("Server_network_name"));
				tablesVO.setServer_status(rs.getString("Server_status"));
				retValue.add(tablesVO);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 得到sybase的engine信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public List getSybase_nmsengineinfo(String serverip) throws Exception {
		String sql = "select * from nms_sybaseengineinfo where serverip = '"
				+ serverip + "'";
		List retValue = new ArrayList();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				TablesVO tablesVO = new TablesVO();
				tablesVO.setEngine(rs.getString("engine"));
				tablesVO.setStatus(rs.getString("status"));
				tablesVO.setStarttime(rs.getString("starttime"));
				retValue.add(tablesVO);
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 获取Sybase的数据信息
	 * 
	 * @param serverip
	 * @return
	 */
	public SybaseVO getSybaseDataByServerip(String serverip) {
		String status = null;// 数据库的状态信息
		SybaseVO sybaseVO = null;
		List dbInfo = null;// 数据库信息
		List deviceInfo = null;// 设备信息
		List userInfo = null;// 用户信息
		List serversInfo = null;// 服务器信息
		List processInfo = null;// 进程信息
		List dbdetailInfo = null;// 数据库详细信息
		List engineInfo = null;// 数据库详细信息
		DBDao dao = new DBDao();
		try {
			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			if (tempStatusHashtable != null
					&& tempStatusHashtable.containsKey("status")) {
				status = (String) tempStatusHashtable.get("status");
			}
			sybaseVO = dao.getSybase_nmssybaseperformance(serverip);
			dbInfo = dao.getSybase_nmsdbinfo(serverip);
			dbdetailInfo = dao.getSybase_nmsdbdetailsinfo(serverip);
			deviceInfo = dao.getSybase_nmsdeviceinfo(serverip);
			processInfo = dao.getSybase_nmsprocessinfo(serverip);
			userInfo = dao.getSybase_nmsuserinfo(serverip);
			serversInfo = dao.getSybase_nmsserversinfo(serverip);
			engineInfo = dao.getSybase_nmsengineinfo(serverip);
			sybaseVO.setDbInfo(dbInfo);
			sybaseVO.setDeviceInfo(deviceInfo);
			sybaseVO.setUserInfo(userInfo);
			sybaseVO.setServersInfo(serversInfo);
			sybaseVO.setProcessInfo(processInfo);
			sybaseVO.setDbsInfo(dbdetailInfo);
			sybaseVO.setEngineInfo(engineInfo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return sybaseVO;
	}

	/**
	 * 更新单个表的某一key的value
	 * 
	 * @param tableName
	 *            表名
	 * @param uniqueKey
	 *            唯一键名
	 * @param uniqueKeyValue
	 *            唯一键的值
	 * @param key
	 *            要更新的键
	 * @param value
	 *            要更新的键的值
	 * @return
	 * @throws Exception
	 */
	public boolean updateNmsValueByUniquekeyAndTablenameAndKey(
			String tableName, String uniqueKey, String uniqueKeyValue,
			String key, String value) throws Exception {
		boolean b = true;
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("update ");
		sBuffer.append(tableName);
		sBuffer.append(" set ");
		sBuffer.append(key);
		sBuffer.append(" ='");
		sBuffer.append(value);
		sBuffer.append("' ");
		sBuffer.append(" where ");
		sBuffer.append(uniqueKey);
		sBuffer.append(" ='");
		sBuffer.append(uniqueKeyValue);
		sBuffer.append("' ");
		DBManager dbmanager = new DBManager();
		try {
			// SysLogger.info(sBuffer.toString());
			dbmanager.executeUpdate(sBuffer.toString());
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return b;
	}

	/**
	 * 插入Mysql的状态信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exceptstatusn
	 */
	public boolean addOrUpdateMysql_nmsstatus(String serverip, String status)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);

			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("delete from nms_mysqlinfo where serverip = '");
			sqlBuffer.append(serverip);
			sqlBuffer.append("' and typename = 'status'");
			// System.out.println(sqlBuffer.toString());
			dbmanager.addBatch(sqlBuffer.toString());

			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_mysqlinfo(serverip,variable_name,value,dbname,typename,mon_time) values ('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append("status");
			sBuffer.append("','");
			sBuffer.append(status);
			sBuffer.append("','");
			sBuffer.append("");
			sBuffer.append("','");
			sBuffer.append("status");
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.addBatch(sBuffer.toString());
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 将mysql的采集信息都放到nms_mysqlinfo表中 通过 typename 来区分是属于哪一类的信息 typename: 1、status
	 * 2、dbnames 3、global_status 4、variables 5、dispose 6、dispose1 7、dispose2
	 * 8、dispose3 9、sessionsDetail 10、Val 11、tablesDetail 12、configVal
	 * 
	 * @param serverip
	 * @param monitorValue
	 * @param dbnames
	 * @return
	 */
	public boolean addMysql_nmsinfo(String serverip, Hashtable monitorValue,
			String[] dbnames) {
		if (monitorValue == null || monitorValue.size() == 0) {
			return false;
		}
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = DbConversionUtil.coversionTimeSql(sdf.format(cc));

			// 保存数据库名--类别为dbnames
			StringBuffer sqlBuffer_0 = new StringBuffer();
			sqlBuffer_0
					.append("insert into nms_mysqlinfo(serverip,variable_name,value,dbname,typename,mon_time) values ('");
			sqlBuffer_0.append(serverip);
			sqlBuffer_0.append("','");
			sqlBuffer_0.append("dbnames");
			sqlBuffer_0.append("','");
			sqlBuffer_0.append(Arrays.toString(dbnames));
			sqlBuffer_0.append("','");
			sqlBuffer_0.append("");
			sqlBuffer_0.append("','");
			sqlBuffer_0.append("dbnames");
			// sqlBuffer_0.append("',");
			// sqlBuffer_0.append(montime);
			// sqlBuffer_0.append(")");
			if (SystemConstant.DBType.equals("mysql")) {
				sqlBuffer_0.append("',");
				sqlBuffer_0.append(montime);
				sqlBuffer_0.append(")");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sqlBuffer_0.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sqlBuffer_0.toString());
			dbmanager.addBatch(sqlBuffer_0.toString());

			for (int i = 0; i < dbnames.length; i++) {
				String dbname = dbnames[i];
				Hashtable retValue = null;// 每个数据库的值
				Vector global_status = null;// 全局状态
				Vector variables = null;
				Vector dispose = null;
				Vector dispose1 = null;
				Vector dispose2 = null;
				Vector dispose3 = null;
				List sessionsDetail = null;
				Vector Val = null;
				List tablesDetail = null;
				Hashtable configVal = null;// 配置文件的值

				if (monitorValue.containsKey(dbnames[i])) {
					retValue = (Hashtable) monitorValue.get(dbnames[i]);
				}
				if (retValue == null) {
					return false;
				}

				// --类别为global_status
				if (retValue.containsKey("global_status")) {
					global_status = (Vector) retValue.get("global_status");
					for (int j = 0; j < global_status.size(); j++) {
						Hashtable tempHashtable = (Hashtable) global_status
								.get(j);
						StringBuffer sqlBuffer = new StringBuffer();
						sqlBuffer
								.append("insert into nms_mysqlinfo(serverip,variable_name,value,dbname,typename,mon_time) values ('");
						sqlBuffer.append(serverip);
						sqlBuffer.append("','");
						sqlBuffer.append(tempHashtable.get("variable_name"));
						sqlBuffer.append("','");
						sqlBuffer.append(tempHashtable.get("value"));
						sqlBuffer.append("','");
						sqlBuffer.append(dbname);
						sqlBuffer.append("','");
						sqlBuffer.append("global_status");
						// sqlBuffer.append("',");
						// sqlBuffer.append(montime);
						// sqlBuffer.append(")");
						if (SystemConstant.DBType.equals("mysql")) {
							sqlBuffer.append("',");
							sqlBuffer.append(montime);
							sqlBuffer.append(")");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sqlBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						// System.out.println(sqlBuffer.toString());
						dbmanager.addBatch(sqlBuffer.toString());
					}

				}
				// --类别为variables
				if (retValue.containsKey("variables")) {
					variables = (Vector) retValue.get("variables");
					for (int j = 0; j < variables.size(); j++) {
						Hashtable tempHashtable = (Hashtable) variables.get(j);
						StringBuffer sqlBuffer = new StringBuffer();
						String value = (String.valueOf(tempHashtable
								.get("value"))).replaceAll("\\\\", "/");
						sqlBuffer
								.append("insert into nms_mysqlinfo(serverip,variable_name,value,dbname,typename,mon_time) values ('");
						sqlBuffer.append(serverip);
						sqlBuffer.append("','");
						sqlBuffer.append(tempHashtable.get("variable_name"));
						sqlBuffer.append("','");
						sqlBuffer.append(value);
						sqlBuffer.append("','");
						sqlBuffer.append(dbname);
						sqlBuffer.append("','");
						sqlBuffer.append("variables");
						// sqlBuffer.append("',");
						// sqlBuffer.append(montime);
						// sqlBuffer.append(")");
						if (SystemConstant.DBType.equals("mysql")) {
							sqlBuffer.append("',");
							sqlBuffer.append(montime);
							sqlBuffer.append(")");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sqlBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						// System.out.println(sqlBuffer.toString());
						dbmanager.addBatch(sqlBuffer.toString());
					}
				}
				// --类别为dispose
				if (retValue.containsKey("dispose")) {
					dispose = (Vector) retValue.get("dispose");
					for (int j = 0; j < dispose.size(); j++) {
						Hashtable tempHashtable = (Hashtable) dispose.get(j);
						StringBuffer sqlBuffer = new StringBuffer();
						sqlBuffer
								.append("insert into nms_mysqlinfo(serverip,variable_name,value,dbname,typename,mon_time) values ('");
						sqlBuffer.append(serverip);
						sqlBuffer.append("','");
						sqlBuffer.append(tempHashtable.get("variable_name"));
						sqlBuffer.append("','");
						sqlBuffer.append(tempHashtable.get("value"));
						sqlBuffer.append("','");
						sqlBuffer.append(dbname);
						sqlBuffer.append("','");
						sqlBuffer.append("dispose");
						// sqlBuffer.append("',");
						// sqlBuffer.append(montime);
						// sqlBuffer.append(")");
						if (SystemConstant.DBType.equals("mysql")) {
							sqlBuffer.append("',");
							sqlBuffer.append(montime);
							sqlBuffer.append(")");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sqlBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						// System.out.println(sqlBuffer.toString());
						dbmanager.addBatch(sqlBuffer.toString());
					}
				}
				// --类别为dispose1
				if (retValue.containsKey("dispose1")) {
					dispose1 = (Vector) retValue.get("dispose1");
					for (int j = 0; j < dispose1.size(); j++) {
						Hashtable tempHashtable = (Hashtable) dispose1.get(j);
						StringBuffer sqlBuffer = new StringBuffer();
						sqlBuffer
								.append("insert into nms_mysqlinfo(serverip,variable_name,value,dbname,typename,mon_time) values ('");
						sqlBuffer.append(serverip);
						sqlBuffer.append("','");
						sqlBuffer.append(tempHashtable.get("variable_name"));
						sqlBuffer.append("','");
						sqlBuffer.append(tempHashtable.get("value"));
						sqlBuffer.append("','");
						sqlBuffer.append(dbname);
						sqlBuffer.append("','");
						sqlBuffer.append("dispose1");
						// sqlBuffer.append("',");
						// sqlBuffer.append(montime);
						// sqlBuffer.append(")");
						if (SystemConstant.DBType.equals("mysql")) {
							sqlBuffer.append("',");
							sqlBuffer.append(montime);
							sqlBuffer.append(")");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sqlBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						// System.out.println(sqlBuffer.toString());
						dbmanager.addBatch(sqlBuffer.toString());
					}
				}
				// --类别为dispose2
				if (retValue.containsKey("dispose2")) {
					dispose2 = (Vector) retValue.get("dispose2");
					for (int j = 0; j < dispose2.size(); j++) {
						Hashtable tempHashtable = (Hashtable) dispose2.get(j);
						StringBuffer sqlBuffer = new StringBuffer();
						sqlBuffer
								.append("insert into nms_mysqlinfo(serverip,variable_name,value,dbname,typename,mon_time) values ('");
						sqlBuffer.append(serverip);
						sqlBuffer.append("','");
						sqlBuffer.append(tempHashtable.get("variable_name"));
						sqlBuffer.append("','");
						sqlBuffer.append(tempHashtable.get("value"));
						sqlBuffer.append("','");
						sqlBuffer.append(dbname);
						sqlBuffer.append("','");
						sqlBuffer.append("dispose2");
						// sqlBuffer.append("',");
						// sqlBuffer.append(montime);
						// sqlBuffer.append(")");
						if (SystemConstant.DBType.equals("mysql")) {
							sqlBuffer.append("',");
							sqlBuffer.append(montime);
							sqlBuffer.append(")");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sqlBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						// System.out.println(sqlBuffer.toString());
						dbmanager.addBatch(sqlBuffer.toString());
					}
				}
				// --类别为dispose3
				if (retValue.containsKey("dispose3")) {
					dispose3 = (Vector) retValue.get("dispose3");
					for (int j = 0; j < dispose3.size(); j++) {
						Hashtable tempHashtable = (Hashtable) dispose3.get(j);
						StringBuffer sqlBuffer = new StringBuffer();
						sqlBuffer
								.append("insert into nms_mysqlinfo(serverip,variable_name,value,dbname,typename,mon_time) values ('");
						sqlBuffer.append(serverip);
						sqlBuffer.append("','");
						sqlBuffer.append(tempHashtable.get("variable_name"));
						sqlBuffer.append("','");
						sqlBuffer.append(tempHashtable.get("value"));
						sqlBuffer.append("','");
						sqlBuffer.append(dbname);
						sqlBuffer.append("','");
						sqlBuffer.append("dispose3");
						// sqlBuffer.append("',");
						// sqlBuffer.append(montime);
						// sqlBuffer.append(")");
						if (SystemConstant.DBType.equals("mysql")) {
							sqlBuffer.append("',");
							sqlBuffer.append(montime);
							sqlBuffer.append(")");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sqlBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						// System.out.println(sqlBuffer.toString());
						dbmanager.addBatch(sqlBuffer.toString());
					}
				}
				// --类别为sessionsDetail
				if (retValue.containsKey("sessionsDetail")) {
					sessionsDetail = (ArrayList) retValue.get("sessionsDetail");
					for (int j = 0; j < sessionsDetail.size(); j++) {
						String[] tempValues = (String[]) sessionsDetail.get(j);
						String tempValue = Arrays.toString(tempValues);
						StringBuffer sqlBuffer = new StringBuffer();
						sqlBuffer
								.append("insert into nms_mysqlinfo(serverip,variable_name,value,dbname,typename,mon_time) values ('");
						sqlBuffer.append(serverip);
						sqlBuffer.append("','");
						sqlBuffer.append("");
						sqlBuffer.append("','");
						sqlBuffer.append(tempValue);
						sqlBuffer.append("','");
						sqlBuffer.append(dbname);
						sqlBuffer.append("','");
						sqlBuffer.append("sessionsDetail");
						// sqlBuffer.append("',");
						// sqlBuffer.append(montime);
						// sqlBuffer.append(")");
						if (SystemConstant.DBType.equals("mysql")) {
							sqlBuffer.append("',");
							sqlBuffer.append(montime);
							sqlBuffer.append(")");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sqlBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						// System.out.println(sqlBuffer.toString());
						dbmanager.addBatch(sqlBuffer.toString());
					}
				}
				// --类别为Val
				if (retValue.containsKey("Val")) {
					Val = (Vector) retValue.get("Val");
					for (int j = 0; j < Val.size(); j++) {
						Hashtable tempHashtable = (Hashtable) Val.get(j);
						String value = (String.valueOf(tempHashtable
								.get("value"))).replaceAll("\\\\", "/");
						StringBuffer sqlBuffer = new StringBuffer();
						sqlBuffer
								.append("insert into nms_mysqlinfo(serverip,variable_name,value,dbname,typename,mon_time) values ('");
						sqlBuffer.append(serverip);
						sqlBuffer.append("','");
						sqlBuffer.append(tempHashtable.get("variable_name"));
						sqlBuffer.append("','");
						sqlBuffer.append(value);
						sqlBuffer.append("','");
						sqlBuffer.append(dbname);
						sqlBuffer.append("','");
						sqlBuffer.append("Val");
						// sqlBuffer.append("',");
						// sqlBuffer.append(montime);
						// sqlBuffer.append(")");
						if (SystemConstant.DBType.equals("mysql")) {
							sqlBuffer.append("',");
							sqlBuffer.append(montime);
							sqlBuffer.append(")");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sqlBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
//						 System.out.println(sqlBuffer.toString());
						dbmanager.addBatch(sqlBuffer.toString());
					}
				}
				// --类别为tablesDetail
				if (retValue.containsKey("tablesDetail")) {
					tablesDetail = (ArrayList) retValue.get("tablesDetail");
					for (int j = 0; j < tablesDetail.size(); j++) {
						String[] tempValues = (String[]) tablesDetail.get(j);
						String tempValue = Arrays.toString(tempValues);
						StringBuffer sqlBuffer = new StringBuffer();
						sqlBuffer
								.append("insert into nms_mysqlinfo(serverip,variable_name,value,dbname,typename,mon_time) values ('");
						sqlBuffer.append(serverip);
						sqlBuffer.append("','");
						sqlBuffer.append("");
						sqlBuffer.append("','");
						sqlBuffer.append(tempValue);
						sqlBuffer.append("','");
						sqlBuffer.append(dbname);
						sqlBuffer.append("','");
						sqlBuffer.append("tablesDetail");
						// sqlBuffer.append("',");
						// sqlBuffer.append(montime);
						// sqlBuffer.append(")");
						if (SystemConstant.DBType.equals("mysql")) {
							sqlBuffer.append("',");
							sqlBuffer.append(montime);
							sqlBuffer.append(")");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sqlBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
//						 System.out.println(sqlBuffer.toString());
						dbmanager.addBatch(sqlBuffer.toString());
					}
				}
				// --类别为configVal
				if (retValue.containsKey("configVal")) {
					configVal = (Hashtable) retValue.get("configVal");
					Iterator iter = configVal.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = String.valueOf(entry.getKey());
						String val = String.valueOf(entry.getValue())
								.replaceAll("\\\\", "/");
						StringBuffer sqlBuffer = new StringBuffer();
						sqlBuffer
								.append("insert into nms_mysqlinfo(serverip,variable_name,value,dbname,typename,mon_time) values ('");
						sqlBuffer.append(serverip);
						sqlBuffer.append("','");
						sqlBuffer.append(key);
						sqlBuffer.append("','");
						sqlBuffer.append(val);
						sqlBuffer.append("','");
						sqlBuffer.append(dbname);
						sqlBuffer.append("','");
						sqlBuffer.append("configVal");
						// sqlBuffer.append("',");
						// sqlBuffer.append(montime);
						// sqlBuffer.append(")");
						if (SystemConstant.DBType.equals("mysql")) {
							sqlBuffer.append("',");
							sqlBuffer.append(montime);
							sqlBuffer.append(")");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sqlBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
//						 SysLogger.info("文字与格式字符串不匹配：--------"+sqlBuffer.toString());
						dbmanager.addBatch(sqlBuffer.toString());
					}
				}
			}
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 得到Mysql的状态信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getMysql_nmsstatus(String serverip) throws Exception {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("select value from nms_mysqlinfo where serverip = '");
		sBuffer.append(serverip);
		sBuffer
				.append("' and typename = 'status' and variable_name = 'status'");
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sBuffer.toString());
			while (rs.next()) {
				retValue.put("status", String.valueOf(rs.getString("value")));
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	/**
	 * 获取Mysql的数据信息
	 * 
	 * @param serverip
	 * @return
	 */
	public Hashtable getMysqlDataByServerip(String serverip) {
		Hashtable monitorValue = new Hashtable();
		String status = null;// 数据库的状态信息
		String[] dbnames = null;// 数据库名数组
		DBManager dbmanager = new DBManager();
		StringBuffer sqlBuffer_0 = new StringBuffer();
		sqlBuffer_0
				.append("select value from nms_mysqlinfo where serverip = '");
		sqlBuffer_0.append(serverip);
		sqlBuffer_0.append("' and typename ='dbnames'");
		ResultSet rs = null;
		try {
			// 取出所有的数据库名信息
			try {
				rs = dbmanager.executeQuery(sqlBuffer_0.toString());
				while (rs.next()) {
					String dbnameString = rs.getString(1);
					dbnames = dbnameString.replaceAll("\\[", "").replaceAll(
							"\\]", "").split(",");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					rs.close();
				}
			}

			if (dbnames == null) {
				return null;
			}

			// 取状态信息
			StringBuffer sqlBuffer_1 = new StringBuffer();
			sqlBuffer_1
					.append("select value from nms_mysqlinfo where serverip = '");
			sqlBuffer_1.append(serverip);
			sqlBuffer_1.append("' and typename = 'status'");
			try {
				rs = dbmanager.executeQuery(sqlBuffer_1.toString());
				if (rs.next()) {
					String value = rs.getString("value");
					status = value.trim();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
			if ("1".equals(status)) {
				monitorValue.put("runningflag", "正在运行");
			} else {
				monitorValue.put("runningflag", "<font color=red>服务停止</font>");
			}

			for (int i = 0; i < dbnames.length; i++) {
				Hashtable retValue = new Hashtable();// 单个数据库的信息
				Vector global_status = new Vector();// 全局状态
				Vector variables = new Vector();
				Vector dispose = new Vector();
				Vector dispose1 = new Vector();
				Vector dispose2 = new Vector();
				Vector dispose3 = new Vector();
				List sessionsDetail = new ArrayList();
				Vector Val = new Vector();
				List tablesDetail = new ArrayList();
				Hashtable configVal = new Hashtable();// 配置文件的值
				String dbname = dbnames[i].trim();
				// 取出global_status
				StringBuffer sqlBuffer_2 = new StringBuffer();
				sqlBuffer_2
						.append("select variable_name ,value from nms_mysqlinfo where serverip = '");
				sqlBuffer_2.append(serverip);
				sqlBuffer_2
						.append("' and typename = 'global_status' and dbname = '");
				sqlBuffer_2.append(dbname);
				sqlBuffer_2.append("'");
				try {
					rs = dbmanager.executeQuery(sqlBuffer_2.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String variable_name = rs.getString("variable_name");
						String value = rs.getString("value");
						tempHashtable.put("variable_name", variable_name);
						tempHashtable.put("value", value);
						global_status.add(tempHashtable);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						rs.close();
					}
				}

				// 取出variables
				StringBuffer sqlBuffer_ = new StringBuffer();
				sqlBuffer_
						.append("select variable_name ,value from nms_mysqlinfo where serverip = '");
				sqlBuffer_.append(serverip);
				sqlBuffer_
						.append("' and typename = 'variables' and dbname = '");
				sqlBuffer_.append(dbname);
				sqlBuffer_.append("'");
				try {
					rs = dbmanager.executeQuery(sqlBuffer_.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String variable_name = rs.getString("variable_name");
						String value = rs.getString("value");
						tempHashtable.put("variable_name", variable_name);
						tempHashtable.put("value", value);
						variables.add(tempHashtable);
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						rs.close();
					}
				}

				// 取出dispose
				StringBuffer sqlBuffer_3 = new StringBuffer();
				sqlBuffer_3
						.append("select variable_name ,value from nms_mysqlinfo where serverip = '");
				sqlBuffer_3.append(serverip);
				sqlBuffer_3.append("' and typename = 'dispose' and dbname = '");
				sqlBuffer_3.append(dbname);
				sqlBuffer_3.append("'");
				try {
					rs = dbmanager.executeQuery(sqlBuffer_3.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String variable_name = rs.getString("variable_name");
						String value = rs.getString("value");
						tempHashtable.put("variable_name", variable_name);
						tempHashtable.put("value", value);
						dispose.add(tempHashtable);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						rs.close();
					}
				}

				// 取出dispose1
				StringBuffer sqlBuffer_4 = new StringBuffer();
				sqlBuffer_4
						.append("select variable_name ,value from nms_mysqlinfo where serverip = '");
				sqlBuffer_4.append(serverip);
				sqlBuffer_4
						.append("' and typename = 'dispose1' and dbname = '");
				sqlBuffer_4.append(dbname);
				sqlBuffer_4.append("'");
				try {
					rs = dbmanager.executeQuery(sqlBuffer_4.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String variable_name = rs.getString("variable_name");
						String value = rs.getString("value");
						tempHashtable.put("variable_name", variable_name);
						tempHashtable.put("value", value);
						dispose1.add(tempHashtable);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						rs.close();
					}
				}

				// 取出dispose2
				StringBuffer sqlBuffer_5 = new StringBuffer();
				sqlBuffer_5
						.append("select variable_name ,value from nms_mysqlinfo where serverip = '");
				sqlBuffer_5.append(serverip);
				sqlBuffer_5
						.append("' and typename = 'dispose2' and dbname = '");
				sqlBuffer_5.append(dbname);
				sqlBuffer_5.append("'");
				try {
					rs = dbmanager.executeQuery(sqlBuffer_5.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String variable_name = rs.getString("variable_name");
						String value = rs.getString("value");
						tempHashtable.put("variable_name", variable_name);
						tempHashtable.put("value", value);
						dispose2.add(tempHashtable);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						rs.close();
					}
				}

				// 取出dispose3
				StringBuffer sqlBuffer_6 = new StringBuffer();
				sqlBuffer_6
						.append("select variable_name ,value from nms_mysqlinfo where serverip = '");
				sqlBuffer_6.append(serverip);
				sqlBuffer_6
						.append("' and typename = 'dispose3' and dbname = '");
				sqlBuffer_6.append(dbname);
				sqlBuffer_6.append("'");
				try {
					rs = dbmanager.executeQuery(sqlBuffer_6.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String variable_name = rs.getString("variable_name");
						String value = rs.getString("value");
						tempHashtable.put("variable_name", variable_name);
						tempHashtable.put("value", value);
						dispose3.add(tempHashtable);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						rs.close();
					}
				}

				// 取出sessionsDetail
				StringBuffer sqlBuffer_7 = new StringBuffer();
				sqlBuffer_7
						.append("select variable_name ,value from nms_mysqlinfo where serverip = '");
				sqlBuffer_7.append(serverip);
				sqlBuffer_7
						.append("' and typename = 'sessionsDetail' and dbname = '");
				sqlBuffer_7.append(dbname);
				sqlBuffer_7.append("'");
				try {
					rs = dbmanager.executeQuery(sqlBuffer_7.toString());
					while (rs.next()) {
						String variable_name = rs.getString("variable_name");
						String value = rs.getString("value");
						sessionsDetail.add(value.replaceAll("\\[", "")
								.replaceAll("\\]", "").split(","));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						rs.close();
					}
				}

				// 取出Val
				StringBuffer sqlBuffer_8 = new StringBuffer();
				sqlBuffer_8
						.append("select variable_name ,value from nms_mysqlinfo where serverip = '");
				sqlBuffer_8.append(serverip);
				sqlBuffer_8.append("' and typename = 'Val' and dbname = '");
				sqlBuffer_8.append(dbname);
				sqlBuffer_8.append("'");
				try {
					rs = dbmanager.executeQuery(sqlBuffer_8.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String variable_name = rs.getString("variable_name");
						String value = rs.getString("value");
						tempHashtable.put("variable_name", variable_name);
						tempHashtable.put("value", value);
						Val.add(tempHashtable);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						rs.close();
					}
				}

				// 取出tablesDetail
				StringBuffer sqlBuffer_9 = new StringBuffer();
				sqlBuffer_9
						.append("select variable_name ,value from nms_mysqlinfo where serverip = '");
				sqlBuffer_9.append(serverip);
				sqlBuffer_9
						.append("' and typename = 'tablesDetail' and dbname = '");
				sqlBuffer_9.append(dbname);
				sqlBuffer_9.append("'");
				try {
					rs = dbmanager.executeQuery(sqlBuffer_9.toString());
					while (rs.next()) {
						String variable_name = rs.getString("variable_name");
						String value = rs.getString("value");
						tablesDetail.add(value.replaceAll("\\[", "")
								.replaceAll("\\]", "").split(","));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						rs.close();
					}
				}

				// 取出configVal
				StringBuffer sqlBuffer_10 = new StringBuffer();
				sqlBuffer_10
						.append("select variable_name ,value from nms_mysqlinfo where serverip = '");
				sqlBuffer_10.append(serverip);
				sqlBuffer_10
						.append("' and typename = 'configVal' and dbname = '");
				sqlBuffer_10.append(dbname);
				sqlBuffer_10.append("'");
				try {
					rs = dbmanager.executeQuery(sqlBuffer_10.toString());
					while (rs.next()) {
						String variable_name = rs.getString("variable_name");
						String value = rs.getString("value");
						configVal.put(variable_name, value);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						rs.close();
					}
				}

				// 得到retValue
				retValue.put("variables", variables);
				retValue.put("dispose", dispose);
				retValue.put("dispose1", dispose1);
				retValue.put("dispose2", dispose2);
				retValue.put("dispose3", dispose3);
				retValue.put("global_status", global_status);
				retValue.put("configVal", configVal);
				retValue.put("tablesDetail", tablesDetail);
				retValue.put("sessionsDetail", sessionsDetail);
				retValue.put("Val", Val);
				monitorValue.put(dbname, retValue);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dbmanager.close();
		}
		return monitorValue;
	}

	/**
	 * 将采集到的DB2的信息存入数据库之中
	 * 
	 * @param serverip
	 * @param monitorValue
	 * @param dbnames
	 * @return
	 */
	public boolean addDB2_nmsinfo(String serverip, Hashtable monitorValue,
			String[] dbnames) {
		if (monitorValue == null || monitorValue.size() == 0) {
			return false;
		}
		Hashtable allDb2Data = (Hashtable) monitorValue.get("allDb2Data");
		Hashtable alltype6spaceHash = (Hashtable) monitorValue.get("alltype6spaceHash");

//		String status = (String) allDb2Data.get("status");
		String ip = (String) monitorValue.get("ip");
		Hashtable tablespaceHashtable = (Hashtable) alltype6spaceHash.get(ip);
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);

			// 保存 全局的缓存
			if (allDb2Data.containsKey("cach")) {
				Hashtable cacheAndApplhash = (Hashtable) allDb2Data.get("cach");
				System.out.println("cacheAndApplhash==============="+cacheAndApplhash.size());
				if(cacheAndApplhash!=null&&cacheAndApplhash.size()>0){
					try {
						StringBuffer poolBuffer = new StringBuffer();
						poolBuffer
								.append("insert into nms_db2cach(serverip,cat_cache_lookups ,cat_cache_inserts,cat_cache_overflows ,pkg_cache_lookups,pkg_cache_inserts ,pkg_cache_num_overflows,appl_section_lookups  ,appl_section_inserts ) values ('");
						poolBuffer.append(serverip);
						poolBuffer.append("','");
						poolBuffer.append(String.valueOf(cacheAndApplhash
								.get("cat_cache_lookups")));
						poolBuffer.append("','");
						poolBuffer.append(String.valueOf(cacheAndApplhash
								.get("cat_cache_inserts")));
						poolBuffer.append("','");
						poolBuffer.append(String.valueOf(cacheAndApplhash
								.get("cat_cache_overflows")));
						poolBuffer.append("','");
						poolBuffer.append(String.valueOf(cacheAndApplhash
								.get("pkg_cache_lookups")));
						poolBuffer.append("','");
						poolBuffer.append(String.valueOf(cacheAndApplhash
								.get("pkg_cache_inserts")));
						poolBuffer.append("','");
						poolBuffer.append(String.valueOf(cacheAndApplhash
								.get("pkg_cache_num_overflows")));
						poolBuffer.append("','");
						poolBuffer.append(String.valueOf(cacheAndApplhash
								.get("appl_section_lookups")));
						poolBuffer.append("','");
						poolBuffer.append(String.valueOf(cacheAndApplhash
								.get("appl_section_inserts")));
						poolBuffer.append("')");
						dbmanager.executeUpdate(poolBuffer.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// 存common
			if (allDb2Data.containsKey("commonhash")) {
				Hashtable commonhash = (Hashtable) allDb2Data.get("commonhash");
//				System.out.println("commonhash==============="+commonhash.size());
				if(commonhash!=null&&commonhash.size()>0){
					try {
						StringBuffer sqlBuffer = new StringBuffer();
						sqlBuffer
								.append("insert into nms_db2common(serverip,host_name,prod_release,total_memory,os_name,configured_cpu,installed_prod,total_cpu,mon_time) values ('");
						sqlBuffer.append(serverip);
						sqlBuffer.append("','");
						sqlBuffer.append(String.valueOf(commonhash.get("host_name")));
						sqlBuffer.append("','");
						sqlBuffer
								.append(String.valueOf(commonhash.get("prod_release")));
						sqlBuffer.append("','");
						sqlBuffer
								.append(String.valueOf(commonhash.get("total_memory")));
						sqlBuffer.append("','");
						sqlBuffer.append(String.valueOf(commonhash.get("os_name")));
						sqlBuffer.append("','");
						sqlBuffer.append(String.valueOf(commonhash
								.get("configured_cpu")));
						sqlBuffer.append("','");
						sqlBuffer.append(String.valueOf(commonhash
								.get("installed_prod")));
						sqlBuffer.append("','");
						sqlBuffer.append(String.valueOf(commonhash.get("total_cpu")));
						// sqlBuffer.append("','");
						// sqlBuffer.append(montime);
						// sqlBuffer.append("')");
						if (SystemConstant.DBType.equals("mysql")) {
							sqlBuffer.append("','");
							sqlBuffer.append(montime);
							sqlBuffer.append("')");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sqlBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						dbmanager.executeUpdate(sqlBuffer.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			for (int i = 0; i < dbnames.length; i++) {
				String dbname = dbnames[i].trim();
				// 存tablespace
				if (tablespaceHashtable.containsKey(dbname)) {
					List tablespace = (ArrayList) tablespaceHashtable.get(dbname);
					System.out.println("tablespace==============="+tablespace.size());
					for (int j = 0; j < tablespace.size(); j++) {
						try {
							Hashtable tempHashtable = (Hashtable) tablespace.get(j);
							// String val =
							// String.valueOf(entry.getValue()).replaceAll("\\\\",
							// "/");
							// sqlBuffer.append(String.valueOf(tempHashtable.get("")));
							StringBuffer sqlBuffer = new StringBuffer();
							sqlBuffer
									.append("insert into nms_db2tablespace(serverip,usablespac,totalspac,usableper,tablespace_name,dbname,mon_time) values ('");
							sqlBuffer.append(serverip);
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("usablespac")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("totalspac")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("usableper")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("tablespace_name")));
							sqlBuffer.append("','");
							sqlBuffer.append(dbname);
							// sqlBuffer.append("','");
							// sqlBuffer.append(montime);
							// sqlBuffer.append("')");
							if (SystemConstant.DBType.equals("mysql")) {
								sqlBuffer.append("','");
								sqlBuffer.append(montime);
								sqlBuffer.append("')");
							} else if (SystemConstant.DBType.equals("oracle")) {
								sqlBuffer.append("',to_date('" + montime
										+ "','yyyy-mm-dd hh24:mi:ss'))");
							}
							dbmanager.executeUpdate(sqlBuffer.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				// 存conn
				if (allDb2Data.containsKey("conn")) {
					List conn = (ArrayList) (((Hashtable) allDb2Data
							.get("conn")).get(dbname));
					for (int j = 0; j < conn.size(); j++) {
						try {
							Hashtable tempHashtable = (Hashtable) conn.get(j);
							// String val =
							// String.valueOf(entry.getValue()).replaceAll("\\\\",
							// "/");
							// sqlBuffer.append(String.valueOf(tempHashtable.get("")));
							StringBuffer sqlBuffer = new StringBuffer();
							sqlBuffer
									.append("insert into nms_db2conn(serverip,db_name,commitsql,db_location,appls_cur_cons,total_cons,");
							sqlBuffer
									.append("db_conn_time,sqlm_elm_last_backup,db_status,failedsql,connections_top,db_path,dbname,mon_time) values ('");
							sqlBuffer.append(serverip);
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("db_name")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("commitsql")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("db_location")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("appls_cur_cons")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("total_cons")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("db_conn_time")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("sqlm_elm_last_backup")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("db_status")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("failedsql")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("connections_top")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(
									tempHashtable.get("db_path")).replaceAll(
									"\\\\", "/"));
							sqlBuffer.append("','");
							sqlBuffer.append(dbname);
							// sqlBuffer.append("','");
							// sqlBuffer.append(montime);
							// sqlBuffer.append("')");
							if (SystemConstant.DBType.equals("mysql")) {
								sqlBuffer.append("','");
								sqlBuffer.append(montime);
								sqlBuffer.append("')");
							} else if (SystemConstant.DBType.equals("oracle")) {
								sqlBuffer.append("',to_date('" + montime
										+ "','yyyy-mm-dd hh24:mi:ss'))");
							}
							// SysLogger.info(sqlBuffer.toString());
							dbmanager.executeUpdate(sqlBuffer.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				// 存sysInfo
				if (allDb2Data.containsKey("sysInfo")) {
					try {
						Hashtable tempHashtable = (Hashtable) (((Hashtable) allDb2Data
								.get("sysInfo")).get(dbname));
						// String val =
						// String.valueOf(entry.getValue()).replaceAll("\\\\", "/");
						// sqlBuffer.append(String.valueOf(tempHashtable.get("")));
						StringBuffer sqlBuffer = new StringBuffer();
						sqlBuffer
								.append("insert into nms_db2sysinfo(serverip,host_name,prod_release,total_memory,os_name,configured_cpu,installed_prod,total_cpu,dbname,mon_time) values ('");
						sqlBuffer.append(serverip);
						sqlBuffer.append("','");
						sqlBuffer.append(String.valueOf(tempHashtable
								.get("host_name")));
						sqlBuffer.append("','");
						sqlBuffer.append(String.valueOf(tempHashtable
								.get("prod_release")));
						sqlBuffer.append("','");
						sqlBuffer.append(String.valueOf(tempHashtable
								.get("total_memory")));
						sqlBuffer.append("','");
						sqlBuffer.append(String.valueOf(tempHashtable
								.get("os_name")));
						sqlBuffer.append("','");
						sqlBuffer.append(String.valueOf(tempHashtable
								.get("configured_cpu")));
						sqlBuffer.append("','");
						sqlBuffer.append(String.valueOf(tempHashtable
								.get("installed_prod")));
						sqlBuffer.append("','");
						sqlBuffer.append(String.valueOf(tempHashtable
								.get("total_cpu")));
						sqlBuffer.append("','");
						sqlBuffer.append(dbname);
						// sqlBuffer.append("','");
						// sqlBuffer.append(montime);
						// sqlBuffer.append("')");
						if (SystemConstant.DBType.equals("mysql")) {
							sqlBuffer.append("','");
							sqlBuffer.append(montime);
							sqlBuffer.append("')");
						} else if (SystemConstant.DBType.equals("oracle")) {
							sqlBuffer.append("',to_date('" + montime
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
						dbmanager.executeUpdate(sqlBuffer.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// spaceInfo
				// 存conn
				if (allDb2Data.containsKey("spaceInfo")) {
					List conn = (ArrayList) (((Hashtable) allDb2Data
							.get("spaceInfo")).get(dbname));
					for (int j = 0; j < conn.size(); j++) {
						try {
							Hashtable tempHashtable = (Hashtable) conn.get(j);
							// String val =
							// String.valueOf(entry.getValue()).replaceAll("\\\\",
							// "/");
							// sqlBuffer.append(String.valueOf(tempHashtable.get("")));
							String usableper = CommonUtil.getValue(tempHashtable,
									"usableper", "0").toString();
							String totalspac = CommonUtil.getValue(tempHashtable,
									"totalspac", "0").toString();
							String usablespac = CommonUtil.getValue(tempHashtable,
									"usablespac", "0").toString();
							if (totalspac.equals(usablespac)) {
								usableper = "100";
							}
							StringBuffer sqlBuffer = new StringBuffer();
							sqlBuffer
									.append("insert into nms_db2spaceinfo(serverip,usablespac,totalspac,usableper,tablespace_name,dbname,mon_time) values ('");
							sqlBuffer.append(serverip);
							sqlBuffer.append("','");
							sqlBuffer.append(usablespac);
							sqlBuffer.append("','");
							sqlBuffer.append(totalspac);
							sqlBuffer.append("','");
							sqlBuffer.append(usableper);
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("tablespace_name")));
							sqlBuffer.append("','");
							sqlBuffer.append(dbname);
							// sqlBuffer.append("','");
							// sqlBuffer.append(montime);
							// sqlBuffer.append("')");
							if (SystemConstant.DBType.equals("mysql")) {
								sqlBuffer.append("','");
								sqlBuffer.append(montime);
								sqlBuffer.append("')");
							} else if (SystemConstant.DBType.equals("oracle")) {
								sqlBuffer.append("',to_date('" + montime
										+ "','yyyy-mm-dd hh24:mi:ss'))");
							}
							// SysLogger.info(sqlBuffer.toString());
							dbmanager.executeUpdate(sqlBuffer.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				// log
				if (allDb2Data.containsKey("log")) {
					List log = (ArrayList) ((Hashtable) allDb2Data.get("log")).get(dbname);
					if(log!=null&&log.size()>0){
						for (int j = 0; j < log.size(); j++) {
							try {
								Hashtable tempHashtable = (Hashtable) log.get(j);
								StringBuffer sqlBuffer = new StringBuffer();
								sqlBuffer
										.append("insert into nms_db2log(serverip,maxlogused,logused,pctused,logspacefree,maxsecused,dbname,mon_time) values ('");
								sqlBuffer.append(serverip);
								sqlBuffer.append("','");
								sqlBuffer.append(String.valueOf(tempHashtable
										.get("maxlogused")));
								sqlBuffer.append("','");
								sqlBuffer.append(String.valueOf(tempHashtable
										.get("logused")));
								sqlBuffer.append("','");
								sqlBuffer.append(String.valueOf(tempHashtable
										.get("pctused")));
								sqlBuffer.append("','");
								sqlBuffer.append(String.valueOf(tempHashtable
										.get("logspacefree")));
								sqlBuffer.append("','");
								sqlBuffer.append(String.valueOf(tempHashtable
										.get("maxsecused")));
								sqlBuffer.append("','");
								sqlBuffer.append(dbname);
								// sqlBuffer.append("','");
								// sqlBuffer.append(montime);
								// sqlBuffer.append("')");
								if (SystemConstant.DBType.equals("mysql")) {
									sqlBuffer.append("','");
									sqlBuffer.append(montime);
									sqlBuffer.append("')");
								} else if (SystemConstant.DBType.equals("oracle")) {
									sqlBuffer.append("',to_date('" + montime
											+ "','yyyy-mm-dd hh24:mi:ss'))");
								}
								dbmanager.executeUpdate(sqlBuffer.toString());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

				// poolInfo
				// {page_reorgs=0, overflow_accesses=0, tbname=SYSTABLES ,
				// rows_read=9, tbschema=SYSIBM, rows_written=0},
				if (allDb2Data.containsKey("poolInfo")) {
					Hashtable poolInfoHashtable = (Hashtable) ((Hashtable) allDb2Data
							.get("poolInfo")).get(dbname);
					List writeValue = (ArrayList) poolInfoHashtable
							.get("writeValue");
					List poolValue = (ArrayList) poolInfoHashtable
							.get("poolValue");
					List lockValue = (ArrayList) poolInfoHashtable
							.get("lockValue");
					List readValue = (ArrayList) poolInfoHashtable
							.get("readValue");
					// 保存writeValue
					for (int j = 0; j < writeValue.size(); j++) {
						try {
							Hashtable tempHashtable = (Hashtable) writeValue.get(j);
							StringBuffer sqlBuffer = new StringBuffer();
							sqlBuffer
									.append("insert into nms_db2write(serverip,page_reorgs,overflow_accesses,tbname,rows_read,");
							sqlBuffer
									.append("tbschema,rows_written,dbname,mon_time) values ('");
							sqlBuffer.append(serverip);
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("page_reorgs")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("overflow_accesses")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(
									tempHashtable.get("tbname")).trim());
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("rows_read")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(
									tempHashtable.get("tbschema")).trim());
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("rows_written")));
							sqlBuffer.append("','");
							sqlBuffer.append(dbname);
							// sqlBuffer.append("','");
							// sqlBuffer.append(montime);
							// sqlBuffer.append("')");
							if (SystemConstant.DBType.equals("mysql")) {
								sqlBuffer.append("','");
								sqlBuffer.append(montime);
								sqlBuffer.append("')");
							} else if (SystemConstant.DBType.equals("oracle")) {
								sqlBuffer.append("',to_date('" + montime
										+ "','yyyy-mm-dd hh24:mi:ss'))");
							}
							dbmanager.executeUpdate(sqlBuffer.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// 保存poolValue
					for (int j = 0; j < poolValue.size(); j++) {
						try {
							Hashtable tempHashtable = (Hashtable) poolValue.get(j);
							StringBuffer sqlBuffer = new StringBuffer();
							sqlBuffer
									.append("insert into nms_db2pool(serverip,index_hit_ratio,Async_read_pct,bp_name,Direct_RW_Ratio,");
							sqlBuffer
									.append("data_hit_ratio,BP_hit_ratio,dbname,mon_time) values ('");
							sqlBuffer.append(serverip);
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("index_hit_ratio")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("Async_read_pct")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(
									tempHashtable.get("bp_name")).trim());
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("Direct_RW_Ratio")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("data_hit_ratio")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("BP_hit_ratio")));
							sqlBuffer.append("','");
							sqlBuffer.append(dbname);
							// sqlBuffer.append("','");
							// sqlBuffer.append(montime);
							// sqlBuffer.append("')");
							if (SystemConstant.DBType.equals("mysql")) {
								sqlBuffer.append("','");
								sqlBuffer.append(montime);
								sqlBuffer.append("')");
							} else if (SystemConstant.DBType.equals("oracle")) {
								sqlBuffer.append("',to_date('" + montime
										+ "','yyyy-mm-dd hh24:mi:ss'))");
							}
							dbmanager.executeUpdate(sqlBuffer.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// //保存lockValue
					// for (int j = 0 ; j < lockValue.size() ; j++) {
					// Hashtable tempHashtable = (Hashtable)lockValue.get(j);
					// StringBuffer sqlBuffer = new StringBuffer();
					// sqlBuffer.append("insert into
					// nms_db2lock(serverip,db_name,total_sorts,lock_waits,lock_escals,lock_wait_time,rows_selected,");
					// sqlBuffer.append("deadlocks,total_sort_time,rows_read,dbname,mon_time)
					// values ('");
					// sqlBuffer.append(serverip);
					// sqlBuffer.append("','");
					// sqlBuffer.append(String.valueOf(tempHashtable.get("db_name")));
					// sqlBuffer.append("','");
					// sqlBuffer.append(String.valueOf(tempHashtable.get("total_sorts")));
					// sqlBuffer.append("','");
					// sqlBuffer.append(String.valueOf(tempHashtable.get("lock_waits")));
					// sqlBuffer.append("','");
					// sqlBuffer.append(String.valueOf(tempHashtable.get("lock_escals")));
					// sqlBuffer.append("','");
					// sqlBuffer.append(String.valueOf(tempHashtable.get("lock_wait_time")));
					// sqlBuffer.append("','");
					// sqlBuffer.append(String.valueOf(tempHashtable.get("rows_selected")));
					// sqlBuffer.append("','");
					// sqlBuffer.append(String.valueOf(tempHashtable.get("deadlocks")));
					// sqlBuffer.append("','");
					// sqlBuffer.append(String.valueOf(tempHashtable.get("total_sort_time")));
					// sqlBuffer.append("','");
					// sqlBuffer.append(String.valueOf(tempHashtable.get("rows_read")));
					// sqlBuffer.append("','");
					// sqlBuffer.append(dbname);
					// sqlBuffer.append("','");
					// sqlBuffer.append(montime);
					// sqlBuffer.append("')");
					// dbmanager.addBatch(sqlBuffer.toString());
					// }
					// 保存lockValue
					for (int j = 0; j < lockValue.size(); j++) {
						try {
							Hashtable tempHashtable = (Hashtable) lockValue.get(j);
							StringBuffer sqlBuffer = new StringBuffer();
							sqlBuffer
									.append("insert into nms_db2lock(serverip,db_name,rows_read,rows_selected, deadlocks ,lock_waits,lock_timeouts,");
							sqlBuffer
									.append("lock_wait_time, lock_escals,x_lock_escals, lock_list_in_use, total_sorts,total_sort_time,mon_time ) ");
							sqlBuffer.append(" values ('");
							sqlBuffer.append(serverip);
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(
									tempHashtable.get("db_name")).trim());
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("rows_read")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("rows_selected")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("deadlocks")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("lock_waits")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("lock_timeouts")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("lock_wait_time")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("lock_escals")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("x_lock_escals")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("lock_list_in_use")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("total_sorts")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("total_sort_time")));
							// sqlBuffer.append("','");
							// sqlBuffer.append(montime);
							// sqlBuffer.append("')");
							if (SystemConstant.DBType.equals("mysql")) {
								sqlBuffer.append("','");
								sqlBuffer.append(montime);
								sqlBuffer.append("')");
							} else if (SystemConstant.DBType.equals("oracle")) {
								sqlBuffer.append("',to_date('" + montime
										+ "','yyyy-mm-dd hh24:mi:ss'))");
							}
							dbmanager.executeUpdate(sqlBuffer.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// 保存readValue
					for (int j = 0; j < readValue.size(); j++) {
						try {
							Hashtable tempHashtable = (Hashtable) readValue.get(j);
							StringBuffer sqlBuffer = new StringBuffer();
							sqlBuffer
									.append("insert into nms_db2read(serverip,page_reorgs,overflow_accesses,tbname,");
							sqlBuffer
									.append("rows_read,tbschema,rows_written,dbname,mon_time) values ('");
							sqlBuffer.append(serverip);
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("page_reorgs")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("overflow_accesses")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("tbname")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("rows_read")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("tbschema")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("rows_written")));
							sqlBuffer.append("','");
							sqlBuffer.append(dbname);
							// sqlBuffer.append("','");
							// sqlBuffer.append(montime);
							// sqlBuffer.append("')");
							if (SystemConstant.DBType.equals("mysql")) {
								sqlBuffer.append("','");
								sqlBuffer.append(montime);
								sqlBuffer.append("')");
							} else if (SystemConstant.DBType.equals("oracle")) {
								sqlBuffer.append("',to_date('" + montime
										+ "','yyyy-mm-dd hh24:mi:ss'))");
							}
							dbmanager.executeUpdate(sqlBuffer.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 保存session
				if (allDb2Data.containsKey("session")) {
					List conn = (ArrayList) (((Hashtable) allDb2Data
							.get("session")).get(dbname));
					for (int j = 0; j < conn.size(); j++) {
						try {
							Hashtable tempHashtable = (Hashtable) conn.get(j);
							// String val =
							// String.valueOf(entry.getValue()).replaceAll("\\\\",
							// "/");
							// sqlBuffer.append(String.valueOf(tempHashtable.get("")));
							StringBuffer sqlBuffer = new StringBuffer();
							sqlBuffer
									.append("insert into nms_db2session(serverip,CLIENT_PLATFORM,APPL_STATUS,APPL_NAME,SNAPSHOT_TIMESTAMP,");
							sqlBuffer
									.append("CLIENT_PROTOCOL,CLIENT_NNAME,dbname,mon_time) values ('");
							sqlBuffer.append(serverip);
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("CLIENT_PLATFORM")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("APPL_STATUS")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("APPL_NAME")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("SNAPSHOT_TIMESTAMP")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("CLIENT_PROTOCOL")));
							sqlBuffer.append("','");
							sqlBuffer.append(String.valueOf(tempHashtable
									.get("CLIENT_NNAME")));
							sqlBuffer.append("','");
							sqlBuffer.append(dbname);
							// sqlBuffer.append("','");
							// sqlBuffer.append(montime);
							// sqlBuffer.append("')");
							if (SystemConstant.DBType.equals("mysql")) {
								sqlBuffer.append("','");
								sqlBuffer.append(montime);
								sqlBuffer.append("')");
							} else if (SystemConstant.DBType.equals("oracle")) {
								sqlBuffer.append("',to_date('" + montime
										+ "','yyyy-mm-dd hh24:mi:ss'))");
							}
							// SysLogger.info(sqlBuffer.toString());
							dbmanager.executeUpdate(sqlBuffer.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
//			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 获取DB2的数据信息
	 * 
	 * @param serverip
	 * @return
	 */
	public Hashtable getDB2DataByServerip(String serverip) {
		Hashtable monitorValue = new Hashtable();
		Hashtable tablespaceHashtable = new Hashtable();
		Hashtable allDb2Data = new Hashtable();
		Hashtable alltype6spaceHash = new Hashtable();
		String ipHex = serverip.split(":")[0];
		IpTranslation tranfer = new IpTranslation();
		String[] ips = java.util.Arrays.toString(tranfer.getIpFromHex(ipHex))
				.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
		StringBuffer ipBuffer = new StringBuffer();
		for (int index = 0; index < ips.length; index++) {
			ipBuffer.append(ips[index].trim());
			if (index != ips.length - 1) {
				ipBuffer.append(".");
			}
		}
		monitorValue.put("ip", ipBuffer.toString());
		DBManager dbManager = new DBManager();
		ResultSet rs = null;
		try {
			// 取状态信息
			String status = "0";
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer
					.append("select value from nms_db2variable where serverip = '");
			sqlBuffer.append(serverip);
			sqlBuffer
					.append("' and typename = 'status' and variable_name = 'status'");
			rs = dbManager.executeQuery(sqlBuffer.toString());
			if (rs.next()) {
				status = rs.getString("value");
			}
			rs.close();

			// 取数据库名数组
			String[] dbnames = null;
			StringBuffer sqlBuffer_ = new StringBuffer();
			sqlBuffer_
					.append("select value from nms_db2variable where serverip = '");
			sqlBuffer_.append(serverip);
			sqlBuffer_
					.append("' and typename ='dbnames' and variable_name = 'dbnames'");
			rs = dbManager.executeQuery(sqlBuffer_.toString());
			while (rs.next()) {
				String dbnameString = rs.getString(1);
				dbnames = dbnameString.replaceAll("\\[", "").replaceAll("\\]",
						"").split(",");
			}
			rs.close();

			// common
			Hashtable sysInfoHashtable = new Hashtable();
			Hashtable commonHashtable = new Hashtable();
			List common = new ArrayList();
			StringBuffer sqlBuffer0 = new StringBuffer();
			sqlBuffer0.append("select * from nms_db2common where serverip = '");
			sqlBuffer0.append(serverip);
			sqlBuffer0.append("'");
			// SysLogger.info(sqlBuffer0.toString());
			rs = dbManager.executeQuery(sqlBuffer0.toString());
			if (rs.next()) {
				Hashtable tempHashtable = new Hashtable();
				String host_name = rs.getString("host_name");
				String prod_release = rs.getString("prod_release");
				String total_memory = rs.getString("total_memory");
				String os_name = rs.getString("os_name");
				String configured_cpu = rs.getString("configured_cpu");
				String installed_prod = rs.getString("installed_prod");
				String total_cpu = rs.getString("total_cpu");
				commonHashtable.put("host_name", host_name);
				commonHashtable.put("prod_release", prod_release);
				commonHashtable.put("total_memory", total_memory);
				commonHashtable.put("os_name", os_name);
				commonHashtable.put("configured_cpu", configured_cpu);
				commonHashtable.put("installed_prod", installed_prod);
				commonHashtable.put("total_cpu", total_cpu);
			}
			rs.close();

			Hashtable connHashtable = new Hashtable();
			Hashtable spaceInfoHashtable = new Hashtable();
			Hashtable logHashtable = new Hashtable();
			Hashtable poolInfoHashtable = new Hashtable();
			Hashtable poolInfoHashtableItem = new Hashtable();
			Hashtable sessionHashtable = new Hashtable();
			if (dbnames != null && dbnames.length > 0) {
				for (int i = 0; i < dbnames.length; i++) {
					String dbname = dbnames[i];
					// 表空间
					List tablespace = new ArrayList();
					StringBuffer sqlBuffer_1 = new StringBuffer();
					sqlBuffer_1
							.append("select * from nms_db2tablespace where serverip = '");
					sqlBuffer_1.append(serverip);
					sqlBuffer_1.append("' and dbname = '");
					sqlBuffer_1.append(dbname);
					sqlBuffer_1.append("'");
					// SysLogger.info(sqlBuffer_1.toString());
					rs = dbManager.executeQuery(sqlBuffer_1.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String usablespac = rs.getString("usablespac");
						String totalspac = rs.getString("totalspac");
						String usableper = rs.getString("usableper");
						String tablespace_name = rs
								.getString("tablespace_name");
						tempHashtable.put("usablespac", usablespac);
						tempHashtable.put("totalspac", totalspac);
						tempHashtable.put("usableper", usableper);
						tempHashtable.put("tablespace_name", tablespace_name);
						tablespace.add(tempHashtable);
					}
					rs.close();
					tablespaceHashtable.put(dbname, tablespace);

					// conn
					List conn = new ArrayList();
					StringBuffer sqlBuffer_2 = new StringBuffer();
					sqlBuffer_2
							.append("select * from nms_db2conn where serverip = '");
					sqlBuffer_2.append(serverip);
					sqlBuffer_2.append("' and dbname = '");
					sqlBuffer_2.append(dbname);
					sqlBuffer_2.append("'");
					// SysLogger.info(sqlBuffer_2.toString());
					rs = dbManager.executeQuery(sqlBuffer_2.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String db_name = rs.getString("db_name");
						String commitsql = rs.getString("commitsql");
						String db_location = rs.getString("db_location");
						String appls_cur_cons = rs.getString("appls_cur_cons");
						String total_cons = rs.getString("total_cons");
						String db_conn_time = rs.getString("db_conn_time");
						String sqlm_elm_last_backup = rs
								.getString("sqlm_elm_last_backup");
						String db_status = rs.getString("db_status");
						String failedsql = rs.getString("failedsql");
						String connections_top = rs
								.getString("connections_top");
						String db_path = rs.getString("db_path");
						tempHashtable.put("db_name", db_name);
						tempHashtable.put("commitsql", commitsql);
						tempHashtable.put("db_location", db_location);
						tempHashtable.put("appls_cur_cons", appls_cur_cons);
						tempHashtable.put("total_cons", total_cons);
						tempHashtable.put("db_conn_time", db_conn_time);
						tempHashtable.put("sqlm_elm_last_backup",
								sqlm_elm_last_backup);
						tempHashtable.put("db_status", db_status);
						tempHashtable.put("failedsql", failedsql);
						tempHashtable.put("connections_top", connections_top);
						tempHashtable.put("db_path", db_path);
						conn.add(tempHashtable);
					}
					rs.close();
					connHashtable.put(dbname, conn);

					// sysInfo
					List sysInfo = new ArrayList();
					StringBuffer sql = new StringBuffer();
					sql
							.append("select * from nms_db2sysinfo where serverip = '");
					sql.append(serverip);
					sql.append("' and dbname = '");
					sql.append(dbname);
					sql.append("'");
					// SysLogger.info(sql.toString());
					rs = dbManager.executeQuery(sql.toString());
					if (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String host_name = rs.getString("host_name");
						String prod_release = rs.getString("prod_release");
						String total_memory = rs.getString("total_memory");
						String os_name = rs.getString("os_name");
						String configured_cpu = rs.getString("configured_cpu");
						String installed_prod = rs.getString("installed_prod");
						String total_cpu = rs.getString("total_cpu");
						tempHashtable.put("host_name", host_name);
						tempHashtable.put("prod_release", prod_release);
						tempHashtable.put("total_memory", total_memory);
						tempHashtable.put("os_name", os_name);
						tempHashtable.put("configured_cpu", configured_cpu);
						tempHashtable.put("installed_prod", installed_prod);
						tempHashtable.put("total_cpu", total_cpu);
						sysInfo.add(tempHashtable);
					}
					rs.close();
					sysInfoHashtable.put(dbname, sysInfo);

					// spaceInfo
					List spaceInfo = new ArrayList();
					StringBuffer sqlBuffer_3 = new StringBuffer();
					sqlBuffer_3
							.append("select * from nms_db2spaceinfo where serverip = '");
					sqlBuffer_3.append(serverip);
					sqlBuffer_3.append("' and dbname = '");
					sqlBuffer_3.append(dbname);
					sqlBuffer_3.append("'");
					// SysLogger.info(sqlBuffer_3.toString());
					rs = dbManager.executeQuery(sqlBuffer_3.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String usablespac = rs.getString("usablespac");
						String totalspac = rs.getString("totalspac");
						String usableper = rs.getString("usableper");
						String tablespace_name = rs
								.getString("tablespace_name");
						tempHashtable.put("usablespac", usablespac);
						tempHashtable.put("totalspac", totalspac);
						tempHashtable.put("usableper", usableper);
						tempHashtable.put("tablespace_name", tablespace_name);
						spaceInfo.add(tempHashtable);
					}
					rs.close();
					spaceInfoHashtable.put(dbname, spaceInfo);

					// log
					List log = new ArrayList();
					StringBuffer sqlBuffer_4 = new StringBuffer();
					sqlBuffer_4
							.append("select * from nms_db2log where serverip = '");
					sqlBuffer_4.append(serverip);
					sqlBuffer_4.append("' and dbname = '");
					sqlBuffer_4.append(dbname);
					sqlBuffer_4.append("'");
					// SysLogger.info(sqlBuffer_4.toString());
					rs = dbManager.executeQuery(sqlBuffer_4.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String maxlogused = rs.getString("maxlogused");
						String logused = rs.getString("logused");
						String pctused = rs.getString("pctused");
						String logspacefree = rs.getString("logspacefree");
						String maxsecused = rs.getString("maxsecused");
						tempHashtable.put("maxlogused", maxlogused);
						tempHashtable.put("logused", logused);
						tempHashtable.put("pctused", pctused);
						tempHashtable.put("logspacefree", logspacefree);
						tempHashtable.put("maxsecused", maxsecused);
						log.add(tempHashtable);
					}
					rs.close();
					logHashtable.put(dbname, log);

					// writeValue
					List writeValue = new ArrayList();
					StringBuffer sqlBuffer_5 = new StringBuffer();
					sqlBuffer_5
							.append("select * from nms_db2write where serverip = '");
					sqlBuffer_5.append(serverip);
					sqlBuffer_5.append("' and dbname = '");
					sqlBuffer_5.append(dbname);
					sqlBuffer_5.append("'");
					// SysLogger.info(sqlBuffer_5.toString());
					rs = dbManager.executeQuery(sqlBuffer_5.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String page_reorgs = rs.getString("page_reorgs");
						String overflow_accesses = rs
								.getString("overflow_accesses");
						String tbname = rs.getString("tbname");
						String rows_read = rs.getString("rows_read");
						String tbschema = rs.getString("tbschema");
						String rows_written = rs.getString("rows_written");
						tempHashtable.put("page_reorgs", page_reorgs);
						tempHashtable.put("overflow_accesses",
								overflow_accesses);
						tempHashtable.put("tbname", tbname);
						tempHashtable.put("rows_read", rows_read);
						tempHashtable.put("tbschema", tbschema);
						tempHashtable.put("rows_written", rows_written);
						writeValue.add(tempHashtable);
					}
					rs.close();

					// poolValue
					List poolValue = new ArrayList();
					StringBuffer sqlBuffer_6 = new StringBuffer();
					sqlBuffer_6
							.append("select * from nms_db2pool where serverip = '");
					sqlBuffer_6.append(serverip);
					sqlBuffer_6.append("' and dbname = '");
					sqlBuffer_6.append(dbname);
					sqlBuffer_6.append("'");
					// SysLogger.info(sqlBuffer_6.toString());
					rs = dbManager.executeQuery(sqlBuffer_6.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String index_hit_ratio = rs
								.getString("index_hit_ratio");
						String Async_read_pct = rs.getString("Async_read_pct");
						String bp_name = rs.getString("bp_name");
						String Direct_RW_Ratio = rs
								.getString("Direct_RW_Ratio");
						String data_hit_ratio = rs.getString("data_hit_ratio");
						String BP_hit_ratio = rs.getString("BP_hit_ratio");
						tempHashtable.put("index_hit_ratio", index_hit_ratio);
						tempHashtable.put("Async_read_pct", Async_read_pct);
						tempHashtable.put("bp_name", bp_name);
						tempHashtable.put("Direct_RW_Ratio", Direct_RW_Ratio);
						tempHashtable.put("data_hit_ratio", data_hit_ratio);
						tempHashtable.put("BP_hit_ratio", BP_hit_ratio);
						poolValue.add(tempHashtable);
					}
					rs.close();

					// //lockValue
					// List lockValue = new ArrayList();
					// StringBuffer sqlBuffer_7 = new StringBuffer();
					// sqlBuffer_7.append("select * from nms_db2lock where
					// serverip = '");
					// sqlBuffer_7.append(serverip);
					// sqlBuffer_7.append("' and dbname = '");
					// sqlBuffer_7.append(dbname);
					// sqlBuffer_7.append("'");
					// //SysLogger.info(sqlBuffer_7.toString());
					// rs = dbManager.executeQuery(sqlBuffer_7.toString());
					// while(rs.next()){
					// Hashtable tempHashtable = new Hashtable();
					// String db_name = rs.getString("db_name");
					// String total_sorts = rs.getString("total_sorts");
					// String lock_waits = rs.getString("lock_waits");
					// String lock_escals = rs.getString("lock_escals");
					// String lock_wait_time = rs.getString("lock_wait_time");
					// String rows_selected = rs.getString("rows_selected");
					// String deadlocks = rs.getString("deadlocks");
					// String total_sort_time = rs.getString("total_sort_time");
					// String rows_read = rs.getString("rows_read");
					// tempHashtable.put("db_name", db_name);
					// tempHashtable.put("total_sorts", total_sorts);
					// tempHashtable.put("lock_waits", lock_waits);
					// tempHashtable.put("lock_escals", lock_escals);
					// tempHashtable.put("lock_wait_time", lock_wait_time);
					// tempHashtable.put("rows_selected", rows_selected);
					// tempHashtable.put("deadlocks", deadlocks);
					// tempHashtable.put("total_sort_time", total_sort_time);
					// tempHashtable.put("rows_read", rows_read);
					// lockValue.add(tempHashtable);
					// }
					// rs.close();

					// lockValue
					List lockValue = new ArrayList();
					StringBuffer sqlBuffer_7 = new StringBuffer();
					sqlBuffer_7
							.append("select * from nms_db2lock where serverip = '");
					sqlBuffer_7.append(serverip);
					sqlBuffer_7.append("' and db_name = '");
					sqlBuffer_7.append(dbname);
					sqlBuffer_7.append("'");
					// SysLogger.info(sqlBuffer_7.toString());
					rs = dbManager.executeQuery(sqlBuffer_7.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						tempHashtable.put("db_name", rs.getString("db_name"));
						tempHashtable.put("rows_read", rs
								.getString("rows_read"));
						tempHashtable.put("rows_selected", rs
								.getString("rows_selected"));
						tempHashtable.put("deadlocks", rs
								.getString("deadlocks"));
						tempHashtable.put("lock_waits", rs
								.getString("lock_waits"));
						tempHashtable.put("lock_timeouts", rs
								.getString("lock_timeouts"));
						tempHashtable.put("lock_wait_time", rs
								.getString("lock_wait_time"));
						tempHashtable.put("lock_escals", rs
								.getString("lock_escals"));
						tempHashtable.put("x_lock_escals", rs
								.getString("x_lock_escals"));
						tempHashtable.put("lock_list_in_use", rs
								.getString("lock_list_in_use"));
						tempHashtable.put("total_sorts", rs
								.getString("total_sorts"));
						tempHashtable.put("total_sort_time", rs
								.getString("total_sort_time"));

						lockValue.add(tempHashtable);
					}
					rs.close();

					// readValue
					List readValue = new ArrayList();
					StringBuffer sqlBuffer_8 = new StringBuffer();
					sqlBuffer_8
							.append("select * from nms_db2read where serverip = '");
					sqlBuffer_8.append(serverip);
					sqlBuffer_8.append("' and dbname = '");
					sqlBuffer_8.append(dbname);
					sqlBuffer_8.append("'");
					// SysLogger.info(sqlBuffer_8.toString());
					rs = dbManager.executeQuery(sqlBuffer_8.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String page_reorgs = rs.getString("page_reorgs");
						String overflow_accesses = rs
								.getString("overflow_accesses");
						String tbname = rs.getString("tbname");
						String rows_read = rs.getString("rows_read");
						String tbschema = rs.getString("tbschema");
						String rows_written = rs.getString("rows_written");
						tempHashtable.put("page_reorgs", page_reorgs);
						tempHashtable.put("overflow_accesses",
								overflow_accesses);
						tempHashtable.put("tbname", tbname);
						tempHashtable.put("rows_read", rows_read);
						tempHashtable.put("tbschema", tbschema);
						tempHashtable.put("rows_written", rows_written);
						readValue.add(tempHashtable);
					}
					rs.close();
					// session
					List session = new ArrayList();
					StringBuffer sqlBuffer_9 = new StringBuffer();
					sqlBuffer_9
							.append("select * from nms_db2session where serverip = '");
					sqlBuffer_9.append(serverip);
					sqlBuffer_9.append("' and dbname = '");
					sqlBuffer_9.append(dbname);
					sqlBuffer_9.append("'");
					// SysLogger.info(sqlBuffer_9.toString());
					rs = dbManager.executeQuery(sqlBuffer_9.toString());
					while (rs.next()) {
						Hashtable tempHashtable = new Hashtable();
						String CLIENT_PLATFORM = rs
								.getString("CLIENT_PLATFORM");
						String APPL_STATUS = rs.getString("APPL_STATUS");
						String APPL_NAME = rs.getString("APPL_NAME");
						String SNAPSHOT_TIMESTAMP = rs
								.getString("SNAPSHOT_TIMESTAMP");
						String CLIENT_PROTOCOL = rs
								.getString("CLIENT_PROTOCOL");
						String CLIENT_NNAME = rs.getString("CLIENT_NNAME");
						tempHashtable.put("CLIENT_PLATFORM", CLIENT_PLATFORM);
						tempHashtable.put("APPL_STATUS", APPL_STATUS);
						tempHashtable.put("APPL_NAME", APPL_NAME);
						tempHashtable.put("SNAPSHOT_TIMESTAMP",
								SNAPSHOT_TIMESTAMP);
						tempHashtable.put("CLIENT_PROTOCOL", CLIENT_PROTOCOL);
						tempHashtable.put("CLIENT_NNAME", CLIENT_NNAME);
						session.add(tempHashtable);
					}
					sessionHashtable.put(dbname, session);
					poolInfoHashtableItem.put("readValue", readValue);
					poolInfoHashtableItem.put("writeValue", writeValue);
					poolInfoHashtableItem.put("poolValue", poolValue);
					poolInfoHashtableItem.put("lockValue", lockValue);
					poolInfoHashtable.put(dbname, poolInfoHashtableItem);
				}
			}

			allDb2Data.put("commonhash", commonHashtable);
			allDb2Data.put("sysInfo", sysInfoHashtable);
			allDb2Data.put("conn", connHashtable);
			allDb2Data.put("log", logHashtable);
			allDb2Data.put("status", status);
			allDb2Data.put("spaceInfo", spaceInfoHashtable);
			// allDb2Data.put("spaceInfo", spaceInfoHashtable);
			allDb2Data.put("session", sessionHashtable);
			allDb2Data.put("poolInfo", poolInfoHashtable);
			allDb2Data.put("topread", new Hashtable());// 取DB2指标时未取出该数据
			allDb2Data.put("topwrite", new Hashtable());// 取DB2指标时未取出该数据
			allDb2Data.put("lock", new Hashtable());// 取DB2指标时未取出该数据

			allDb2Data.put("cach", getDB2_nmsCach(serverip));// 获取cach

			alltype6spaceHash.put(ipBuffer.toString(), tablespaceHashtable);
			monitorValue.put("alltype6spaceHash", alltype6spaceHash);
			Hashtable allDb2DataHashtable = new Hashtable();
			allDb2DataHashtable.put(ipBuffer.toString(), allDb2Data);
			monitorValue.put("allDb2Data", allDb2DataHashtable);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return monitorValue;
	}

	/**
	 * 得到DB2 全局 Cach
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getDB2_nmsCach(String serverip) {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer
				.append("select cat_cache_lookups ,cat_cache_inserts,cat_cache_overflows ,pkg_cache_lookups,pkg_cache_inserts ,pkg_cache_num_overflows,appl_section_lookups  ,appl_section_inserts from nms_db2cach where serverip = '");
		sBuffer.append(serverip);
		sBuffer.append("'");
		Hashtable returnHashtable = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sBuffer.toString());
			while (rs.next()) {
				String cat_cache_lookups = rs.getString("cat_cache_lookups");
				String cat_cache_inserts = rs.getString("cat_cache_inserts");
				String cat_cache_overflows = rs
						.getString("cat_cache_overflows");
				String pkg_cache_lookups = rs.getString("pkg_cache_lookups");
				String pkg_cache_inserts = rs.getString("pkg_cache_inserts");
				String pkg_cache_num_overflows = rs
						.getString("pkg_cache_num_overflows");
				String appl_section_lookups = rs
						.getString("appl_section_lookups");
				String appl_section_inserts = rs
						.getString("appl_section_inserts");

				returnHashtable.put("cat_cache_lookups", cat_cache_lookups);
				returnHashtable.put("cat_cache_inserts", cat_cache_inserts);
				returnHashtable.put("cat_cache_overflows", cat_cache_overflows);
				returnHashtable.put("pkg_cache_lookups", pkg_cache_lookups);
				returnHashtable.put("pkg_cache_inserts", pkg_cache_inserts);
				returnHashtable.put("pkg_cache_num_overflows",
						pkg_cache_num_overflows);
				returnHashtable.put("appl_section_lookups",
						appl_section_lookups);
				returnHashtable.put("appl_section_inserts",
						appl_section_inserts);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return returnHashtable;
	}

	/**
	 * 插入DB2的状态信息插入到数据库
	 * 
	 * @param serverip
	 * @param configs
	 * @return
	 * @throws Exceptstatusn
	 */
	public boolean addOrUpdateDB2_nmsstatus(String serverip, String status)
			throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);

			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("delete from nms_db2variable where serverip = '");
			sqlBuffer.append(serverip);
			sqlBuffer.append("' and typename = 'status'");
			// System.out.println(sqlBuffer.toString());
			dbmanager.addBatch(sqlBuffer.toString());

			StringBuffer sBuffer = new StringBuffer();
			sBuffer
					.append("insert into nms_db2variable(serverip,variable_name,value,typename,mon_time) values ('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append("status");
			sBuffer.append("','");
			sBuffer.append(status);
			sBuffer.append("','");
			sBuffer.append("status");
			// sBuffer.append("','");
			// sBuffer.append(montime);
			// sBuffer.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			// System.out.println(sBuffer.toString());
			dbmanager.addBatch(sBuffer.toString());
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}
	public boolean addOrUpdateDB2_nmsdbnames(String serverip, String[] dbnames)
	throws Exception {
		DBManager dbmanager = new DBManager();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
		
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("delete from nms_db2variable where serverip = '");
			sqlBuffer.append(serverip);
			sqlBuffer.append("' and typename = 'dbnames'");
			// System.out.println(sqlBuffer.toString());
			dbmanager.addBatch(sqlBuffer.toString());
		
//			 保存数据库信息--类别为dbnames
			StringBuffer sBuffer_ = new StringBuffer();
			sBuffer_
					.append("insert into nms_db2variable(serverip,variable_name,value,typename,mon_time) values ('");
			sBuffer_.append(serverip);
			sBuffer_.append("', 'dbnames', '");
			sBuffer_.append(Arrays.toString(dbnames));
			sBuffer_.append("','dbnames','");
			// sBuffer_.append(montime);
			// sBuffer_.append("')");
			if (SystemConstant.DBType.equals("mysql")) {
				System.out.println(SystemConstant.DBType.equals("mysql"));
				sBuffer_.append(montime);
				sBuffer_.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer_.append("to_date('" + montime
						+ "','yyyy-mm-dd hh24:mi:ss'))");
			}
			dbmanager.addBatch(sBuffer_.toString());
			dbmanager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}
	/**
	 * 得到DB2的状态信息
	 * 
	 * @param serverip
	 * @return
	 * @throws Exception
	 */
	public Hashtable getDB2_nmsstatus(String serverip) throws Exception {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("select value from nms_db2variable where serverip = '");
		sBuffer.append(serverip);
		sBuffer
				.append("' and typename = 'status' and variable_name = 'status'");
		Hashtable retValue = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sBuffer.toString());
			while (rs.next()) {
				retValue.put("status", String.valueOf(rs.getString("value")));
			}
			rs.close();
			// SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return retValue;
	}

	public List getDbmonitorlist(Integer netid) throws Exception {
		List dbmonitorlistList = new ArrayList();
		Session session = null;

		try {
			Query query = null;
			if (netid == 99) {
				query = session
						.createQuery("from Dbmonitorlist dbmonitorlist  order by dbmonitorlist.id");
			} else if (netid == 999) {
				query = session
						.createQuery("from Dbmonitorlist dbmonitorlist  where (dbmonitorlist.netlocation.id =2 or dbmonitorlist.netlocation.id =3 or dbmonitorlist.netlocation.id =11 or dbmonitorlist.netlocation.id =12 ) order by dbmonitorlist.id");
			} else {
				query = session
						.createQuery("from Dbmonitorlist dbmonitorlist where dbmonitorlist.netlocation.id ="
								+ netid + " order by dbmonitorlist.id");
			}

			dbmonitorlistList = query.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return dbmonitorlistList;
	}

	public List getDbserver(String type) {
		List list = new ArrayList();
		List returnList = new ArrayList();
		Session session = null;
		try {
			Query query = session
					.createQuery("from Dbmonitorlist dbmonitorlist where dbmonitorlist.dict.name = '"
							+ type + "' order by dbmonitorlist.id");
			list = query.list();

			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					DBVo db = (DBVo) list.get(i);
					boolean flag = false;
					if (type.equals("oracle")) {
						flag = getOracleIsOK(db.getIpAddress(), Integer
								.parseInt(db.getPort()), db.getDbName(), db
								.getUser(), db.getPassword());
					} else if (type.equals("sqlserver")) {
						flag = getSqlserverIsOk(db.getIpAddress(),
								db.getUser(), db.getPassword());
					} else if (type.equals("sysbase")) {
						flag = getSysbaseIsOk(db.getIpAddress(), db.getUser(),
								db.getPassword(), Integer
										.parseInt(db.getPort()));
					} else if (type.equals("mysql")) {
						flag = getMySqlIsOk(db.getIpAddress(), db.getUser(), db
								.getPassword(), db.getDbName());
					} else if (type.equals("informix")) {
						flag = getInformixIsOk(db.getIpAddress(), db.getPort(),
								db.getUser(), db.getPassword(), db.getDbName(),
								db.getAlias());
					} else if (type.equals("db2")) {
						// flag=getDb2IsOk(db.getIpaddress(),db.getUsername(),db.getPasswords());

						String dbnames = db.getDbName();
						String[] dbs = dbnames.split(",");
						int allFlag = 0;
						for (int k = 0; k < dbs.length; k++) {
							String dbStr = dbs[k];
							boolean db2IsOK = getDB2IsOK(db.getIpAddress(),
									Integer.parseInt(db.getPort()), dbStr, db
											.getUser(), db.getPassword());
							if (!db2IsOK) {
								allFlag = 1;
								// break;
							}
						}
						if (allFlag == 1) {
							// 有一个是不通的
							flag = false;
						} else
							flag = true;
					}
					if (flag == true) {
						// db.setBak("正在运行");
					} else {
						// db.setBak("未启动");
					}
					returnList.add(i, db);
					// this.endTransaction(false);
				}
			}
		} catch (Exception exp) {
			// exp.printStackTrace();
		}
		return returnList;
	}

	/*
	 * public List getDbserverAndMonflag(String type,Integer monflag){ List list =
	 * new ArrayList(); List returnList = new ArrayList(); Session session =
	 * null; try { Query query = null; if(type.equals("99")){
	 * session.createQuery("from Dbmonitorlist dbmonitorlist where
	 * dbmonitorlist.mon_flag = "+monflag+" order by dbmonitorlist.id"); }else
	 * session.createQuery("from Dbmonitorlist dbmonitorlist where
	 * dbmonitorlist.dict.name = '"+type+"' and dbmonitorlist.mon_flag =
	 * "+monflag+" order by dbmonitorlist.id"); list = query.list();
	 * 
	 * if (list != null && list.size()>0){ for(int i=0;i<list.size();i++){
	 * Dbmonitorlist db = (Dbmonitorlist)list.get(i); session =
	 * this.beginTransaction(); boolean flag = false; if
	 * (type.equals("oracle")){ flag =
	 * getOracleIsOK(db.getIpaddress(),db.getPort(),db.getDbname(),db.getUsername(),db.getPasswords());
	 * }else if (type.equals("sqlserver")){
	 * flag=getSqlserverIsOk(db.getIpaddress(),db.getUsername(),db.getPasswords());
	 * }else if (type.equals("sysbase")){
	 * flag=getSysbaseIsOk(db.getIpaddress(),db.getUsername(),db.getPasswords(),db.getPort());
	 * }else if (type.equals("db2")){
	 * //flag=getDb2IsOk(db.getIpaddress(),db.getUsername(),db.getPasswords());
	 * 
	 * String dbnames = db.getDbname(); String[] dbs = dbnames.split(","); int
	 * allFlag = 0; for(int k=0;k<dbs.length;k++){ String dbStr = dbs[k];
	 * boolean db2IsOK =
	 * getDB2IsOK(db.getIpaddress(),db.getPort(),dbStr,db.getUsername(),db.getPasswords());
	 * if (!db2IsOK){ allFlag = 1; //break; } } if(allFlag == 1){ //有一个是不通的 flag =
	 * false; }else flag = true; } if (flag == true){ db.setBak("正在运行"); }else{
	 * db.setBak("未启动"); } returnList.add(i,db); //this.endTransaction(false); } }
	 * }catch(Exception exp){ //exp.printStackTrace(); } return returnList; }
	 */
	/*
	 * public List getDbserverAndMonflag(String type,Integer monflag,Integer
	 * netid){ List list = new ArrayList(); List returnList = new ArrayList();
	 * Session session = null; try { session = this.beginTransaction(); Query
	 * query = null; String sql = "from Dbmonitorlist dbmonitorlist where
	 * dbmonitorlist.mon_flag = "+monflag; if(netid != 99 && netid != 999){ sql =
	 * sql+" and dbmonitorlist.netlocation.id = "+netid; } if(netid == 999){ sql =
	 * sql+" and (dbmonitorlist.netlocation.id =2 or
	 * dbmonitorlist.netlocation.id =3 or dbmonitorlist.netlocation.id =11 or
	 * dbmonitorlist.netlocation.id =12 ) "; } if(type.equals("99")){ //sql =
	 * sql +" and dbmonitorlist.mon_flag = "+monflag;
	 * //session.createQuery("from Dbmonitorlist dbmonitorlist where
	 * dbmonitorlist.mon_flag = "+monflag+" order by dbmonitorlist.id"); }else {
	 * sql = sql +" and dbmonitorlist.dict.name = '"+type+"' ";
	 * //session.createQuery("from Dbmonitorlist dbmonitorlist where
	 * dbmonitorlist.dict.name = '"+type+"' and dbmonitorlist.mon_flag =
	 * "+monflag+" order by dbmonitorlist.id"); } sql = sql +" order by
	 * dbmonitorlist.id"; //System.out.println(sql);
	 * query=session.createQuery(sql); list = query.list();
	 * 
	 * if (list != null && list.size()>0){ for(int i=0;i<list.size();i++){
	 * Dbmonitorlist db = (Dbmonitorlist)list.get(i); session =
	 * this.beginTransaction(); boolean flag = false; if
	 * (type.equals("oracle")){ flag =
	 * getOracleIsOK(db.getIpaddress(),db.getPort(),db.getDbname(),db.getUsername(),db.getPasswords());
	 * }else if (type.equals("sqlserver")){
	 * flag=getSqlserverIsOk(db.getIpaddress(),db.getUsername(),db.getPasswords());
	 * }else if (type.equals("sysbase")){
	 * flag=getSysbaseIsOk(db.getIpaddress(),db.getUsername(),db.getPasswords(),db.getPort());
	 * }else if (type.equals("db2")){
	 * //flag=getDb2IsOk(db.getIpaddress(),db.getUsername(),db.getPasswords());
	 * 
	 * String dbnames = db.getDbname(); String[] dbs = dbnames.split(","); int
	 * allFlag = 0; for(int k=0;k<dbs.length;k++){ String dbStr = dbs[k];
	 * boolean db2IsOK =
	 * getDB2IsOK(db.getIpaddress(),db.getPort(),dbStr,db.getUsername(),db.getPasswords());
	 * if (!db2IsOK){ allFlag = 1; //break; } } if(allFlag == 1){ //有一个是不通的 flag =
	 * false; }else flag = true; } if (flag == true){ db.setBak("正在运行"); }else{
	 * db.setBak("未启动"); } returnList.add(i,db); //this.endTransaction(false); } }
	 * this.endTransaction(true); } catch (HibernateException e) { try{
	 * this.endTransaction(false); }catch(Exception ex){ //ex.printStackTrace(); }
	 * this.error("getDbmonitorlist error--------------------" + e);
	 * }catch(Exception exp){ //exp.printStackTrace(); } return returnList; }
	 */

	public List getDbserverByType(String type) {
		List list = new ArrayList();
		Session session = null;
		try {
			Query query = session
					.createQuery("from Dbmonitorlist dbmonitorlist where dbmonitorlist.dict.name = '"
							+ type + "' order by dbmonitorlist.id");
			list = query.list();
		} catch (Exception exp) {
			// exp.printStackTrace();
		}
		return list;

	}

	/*
	 * public List getDbByTypeMonFlag(String type,Integer monflag){ List list =
	 * new ArrayList(); Session session = null; try { session =
	 * this.beginTransaction(); Query query = null; if(type == null ||
	 * type.trim().length()==0){ query = session.createQuery("from Dbmonitorlist
	 * dbmonitorlist where dbmonitorlist.mon_flag = "+monflag+" order by
	 * dbmonitorlist.dict.name"); }else query = session.createQuery("from
	 * Dbmonitorlist dbmonitorlist where dbmonitorlist.dict.name = '"+type+"'
	 * and dbmonitorlist.mon_flag = "+monflag+" order by dbmonitorlist.id");
	 * list = query.list(); this.endTransaction(true); } catch
	 * (HibernateException e) { try{ this.endTransaction(false);
	 * }catch(Exception ex){ //ex.printStackTrace(); }
	 * this.error("getDbmonitorlist error--------------------" + e);
	 * }catch(Exception exp){ //exp.printStackTrace(); } return list; }
	 */
	/*
	 * public List getDbByTypeMonFlag(String type,Integer monflag,Integer
	 * netid){ List list = new ArrayList(); Session session = null; try {
	 * session = this.beginTransaction(); Query query = null;
	 * 
	 * String sql = "from Dbmonitorlist dbmonitorlist where
	 * dbmonitorlist.mon_flag = "+monflag; if(netid != 99 && netid != 999){ sql =
	 * sql+" and dbmonitorlist.netlocation.id = "+netid; } if(netid==999){ sql =
	 * sql+" and (dbmonitorlist.netlocation.id =2 or
	 * dbmonitorlist.netlocation.id =3 or dbmonitorlist.netlocation.id =11 or
	 * dbmonitorlist.netlocation.id =12 ) "; } if(type.equals("99")){ //sql =
	 * sql +" and dbmonitorlist.mon_flag = "+monflag;
	 * //session.createQuery("from Dbmonitorlist dbmonitorlist where
	 * dbmonitorlist.mon_flag = "+monflag+" order by dbmonitorlist.id"); }else {
	 * sql = sql +" and dbmonitorlist.dict.name = '"+type+"' ";
	 * //session.createQuery("from Dbmonitorlist dbmonitorlist where
	 * dbmonitorlist.dict.name = '"+type+"' and dbmonitorlist.mon_flag =
	 * "+monflag+" order by dbmonitorlist.id"); } sql = sql +" order by
	 * dbmonitorlist.id"; ////System.out.println(sql);
	 * query=session.createQuery(sql); list = query.list();
	 * 
	 * this.endTransaction(true); } catch (HibernateException e) { try{
	 * this.endTransaction(false); }catch(Exception ex){ //ex.printStackTrace(); }
	 * this.error("getDbmonitorlist error--------------------" + e);
	 * }catch(Exception exp){ //exp.printStackTrace(); } return list; }
	 */
	public List getDisSqlserverIp() throws Exception {
		List list = new ArrayList();
		Session session = null;

		try {
			Query query = session
					.createQuery("select distinct a.serverip from Sqlserver_processdata a");
			list = query.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getSqlserverDbByServer(String serverip) throws Exception {
		List list = new ArrayList();
		Session session = null;

		try {
			Query query = session
					.createQuery("select distinct a.dbname from Sqlserver_processdata a where a.serverip ='"
							+ serverip + "'");
			list = query.list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getSqlserverInfo(String serverip, String dbname,
			String startdate, String todate) throws Exception {
		List list = new ArrayList();
		List returnVal = new ArrayList();
		Session session = null;
		// startdate = startdate+" 00:00:00";
		// todate = todate+" 23:59:59";
		try {
			Query query = session
					.createQuery("select distinct a.serverip,a.spid,a.dbname,a.username,a.cpu,a.physical_io,a.memusage,"
							+ "a.status,a.hostname,a.program_name,a.login_time "
							+ "from Sqlserver_processdata a where "
							+ "a.serverip ='"
							+ serverip
							+ "' and a.dbname='"
							+ dbname
							+ "' "
							+ "and a.login_time >=to_date('"
							+ startdate
							+ "','YYYY-MM-DD HH24:MI:SS') and a.login_time<=to_date('"
							+ todate
							+ "','YYYY-MM-DD HH24:MI:SS') order by a.login_time desc");
			list = query.list();
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[]) list.get(i);
				Sqlserver_processdata sp = new Sqlserver_processdata();
				for (int j = 0; j < obj.length; j++) {
					sp.setServerip(obj[0].toString());
					sp.setSpid(obj[1].toString());
					sp.setDbname(obj[2].toString());
					sp.setUsername(obj[3].toString());
					sp.setCpu(Integer.parseInt(obj[4].toString()));
					sp.setPhysical_io(Integer.parseInt(obj[5].toString()));
					sp.setMemusage(Integer.parseInt(obj[6].toString()));
					sp.setStatus(obj[7].toString());
					sp.setHostname(obj[8].toString());
					sp.setProgram_name(obj[9].toString());
					sp.setLogin_time((Date) obj[10]);

				}
				returnVal.add(sp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnVal;
	}

	public Hashtable getAllOracleData(String ip, int port, String db,
			String username, String password) throws Exception {
		Hashtable return_hash = new Hashtable();
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sqlSession = "select substr(s.username,1,18) username,s.status status,s.MACHINE machine,"
				+ "s.type sessiontype, to_char(s.LOGON_TIME,'yyyy-mm-dd hh24-mi-ss') logontime,"
				+ "substr(s.program,1,15) program,decode(s.command, 0,'No Command',1,"
				+ "'Create Table',2,'Insert',3,'Select',6,'Update',7,'Delete',9,"
				+ "'Create Index',15,'Alter Table',21,'Create View',23,'Validate Index',35,"
				+ "'Alter Database',39,'Create Tablespace',41,'Drop Tablespace',40,'Alter Tablespace'"
				+ ",53,'Drop User',62,'Analyze Table',63,'Analyze Index',s.command||': Other') "
				+ "command from v$session s,v$process p,v$transaction t,v$rollstat r,v$rollname n "
				+ "where s.paddr = p.addr and   s.taddr = t.addr (+) and   t.xidusn = r.usn (+) and"
				+ "  r.usn = n.usn (+) order by logon_time desc";
		String sqlTablespace = "select a.tablespace_name \"Tablespace\", "
				+ "round(a.bytes/1024/1024,0) \"Size MB\","
				+ "round((decode(b.bytes,null,0,b.bytes))/1024/1024,0) \"Free MB\","
				+ "round((decode(b.bytes,null,0,b.bytes)) / a.bytes * 100,1) \"Percent Free\","
				+ "c.file_name,c.status from   sys.sm$ts_avail a, sys.sm$ts_free b,dba_data_files c  "
				+ "where  a.tablespace_name = b.tablespace_name(+) and a.tablespace_name=c.tablespace_name "
				+ "order by a.tablespace_name";
		String sqlRollback = "select distinct name \"Rollback Segment\", "
				+ "optsize \"Optimal\",shrinks, AveShrink \"Average Shrink\",Wraps, "
				+ "Extends from v$rollstat,v$rollname where "
				+ "v$rollstat.USN=v$rollname.USN order by name";
		String sqlSys = "select * from v$instance";
		String sqlVer = "select * from v$version";
		String sqlsga = "select a.COMPONENT,round(a.CURRENT_SIZE/1024/1024,0) as val from v$sga_dynamic_components a";
		String sqlpga = "select * from v$pgastat";
		String sqlLock = "select distinct substr(s.username,1,18) username,s.status status,s.MACHINE machine,"
				+ "s.type sessiontype,to_char(s.LOGON_TIME,'yyyy-mm-dd hh24-mi-ss') logontime,"
				+ "substr(s.program,1,15) program , a.type as locktype,a.lmode as lmode,"
				+ "a.request as request  from v$session s,v$lock a where a.sid=s.sid";
		String sqlTable = "select sum(bytes)/1024/1024 spaces,segment_name from dba_extents group by segment_name order by spaces desc";
		String sqlTsql = "select sql_text,pct_bufgets,username from (select rank() over(order by disk_reads desc) as rank_bufgets,"
				+ "to_char(100 * ratio_to_report(disk_reads) over(), '999.99') pct_bufgets,sql_text,b.username as username from"
				+ " v$sqlarea,dba_users b where b.user_id = PARSING_USER_ID) where rank_bufgets < 11";
		String sqlcf = "select * from v$controlfile";
		String sqlArc = "Select Created, Log_Mode From V$Database";
		String sqlLog = "select * from v$logfile";
		String sqlko = "select * from v$db_object_cache where sharable_mem > 100000 and type in ('PACKAGE', 'PACKAGE BODY', 'PROCEDURE', 'FUNCTION')";
		String sqllstr = "select open_mode from v$database";
		String sqlExtent = "select a.tablespace_name, sum(a.extents) from dba_segments a, dba_tablespaces b "
				+ "where a.tablespace_name = b.tablespace_name and b.extent_management = 'DICTIONARY' "
				+ "group by a.tablespace_name order by sum(a.extents)";
		String sqlWait = "SELECT EVENT,SUM(DECODE(WAIT_TIME,0,0,1)) PREV,SUM(DECODE(WAIT_TIME,0,1,0)) CURR,COUNT(*) TOT FROM V$SESSION_WAIT GROUP BY EVENT ORDER BY 4";
		String sqldbio = "SELECT DF.TABLESPACE_NAME NAME,DF.FILE_NAME FILENAME,F.PHYRDS PYR,"
				+ "F.PHYBLKRD PBR,F.PHYWRTS PYW, F.PHYBLKWRT PBW FROM V$FILESTAT F, DBA_DATA_FILES DF WHERE F.FILE# = DF.FILE_ID "
				+ " ORDER BY DF.TABLESPACE_NAME";

		OracleJdbcUtil util = null;
		ResultSet rs = null;

		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();

			// 获取session信息
			Vector returnVal1 = new Vector();
			rs = util.stmt.executeQuery(sqlSession);
			ResultSetMetaData rsmd1 = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd1.getColumnCount(); i++) {
					String col = rsmd1.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal1.addElement(return_value);
				return_value = null;
			}
			return_hash.put("session", returnVal1);
			rs.close();

			// 获取表空间信息
			Vector returnVal2 = new Vector();
			Map<String, Integer> names = new HashMap<String, Integer>();
			rs = util.stmt.executeQuery(sqlTablespace);
			ResultSetMetaData rsmd2 = rs.getMetaData();
			while (rs.next()) {
				String name = rs.getString("Tablespace");
				if (names.get(name) == null) {
					Hashtable return_value = new Hashtable();
					names.put(name, 1);
					for (int i = 1; i <= rsmd2.getColumnCount(); i++) {
						String col = rsmd2.getColumnName(i);
						if (rs.getString(i) != null) {
							String tmp = rs.getString(i).toString();
							return_value.put(col.toLowerCase()
									.replace(" ", "_"), tmp);
						} else
							return_value.put(col.toLowerCase(), "--");
					}
					returnVal2.addElement(return_value);
					return_value = null;
				} else {
					for (int i = 0; i < returnVal2.size(); i++) {
						Hashtable infos = (Hashtable) returnVal2.get(i);
						if (name.equals(infos.get("tablespace"))) {
							String tem1 = (String) infos.get("free_mb");
							String tem2 = rs.getString("Free MB");
							float te1 = 0;
							float te2 = 0;
							if (tem1 != null && !"".equals(tem1))
								te1 = Float.parseFloat(tem1);
							if (tem2 != null && !"".equals(tem2))
								te2 = Float.parseFloat(tem2);
							String total1 = (String) infos.get("size_mb");
							String total2 = rs.getString("Size MB");
							float tt1 = 0;
							float tt2 = 0;
							if (total1 != null && !"".equals(total1)) {
								tt1 = Float.parseFloat(total1);
							}
							if (total2 != null && !"".equals(total2)) {
								tt2 = Float.parseFloat(total2);
							}
							float f = (te1 + te2) / (tt1 + tt2);
							infos.put("percent_free", String.valueOf(f * 100));
							infos.put("size_mb", String.valueOf(tt1 + tt2));
							infos.put("free_mb", String.valueOf(te1 + te2));
							String path = rs.getString("file_name");
							int len = path.lastIndexOf("\\");
							if (len != -1) {
								String tpath = path.substring(0, len);
								infos.put("file_name", tpath);
							}
							break;
						}
					}

				}

			}
			return_hash.put("tablespace", returnVal2);
			rs.close();

			// 回滚段信息
			Vector returnVal3 = new Vector();
			rs = util.stmt.executeQuery(sqlRollback);
			ResultSetMetaData rsmd3 = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd3.getColumnCount(); i++) {
					String col = rsmd3.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(
								col.toLowerCase().replaceAll(" ", "_"), tmp);
					} else
						return_value.put(
								col.toLowerCase().replaceAll(" ", "_"), "--");
				}
				returnVal3.addElement(return_value);
				return_value = null;
			}
			return_hash.put("rollback", returnVal3);
			rs.close();

			// 数据库系统信息
			Hashtable sys_hash = new Hashtable();
			rs = util.stmt.executeQuery(sqlSys);
			while (rs.next()) {
				if (rs.getString(2) != null) {
					// 得到该数据库的实例
					sys_hash.put("INSTANCE_NAME", rs.getString(2).toString());
					sys_hash.put("HOST_NAME", rs.getString(3).toString());
					sys_hash.put("DBNAME", db);
					sys_hash.put("VERSION", rs.getString(4).toString());
					sys_hash.put("STARTUP_TIME", rs.getDate(5) + " "
							+ rs.getTime(5));
					if (rs.getString(6).toString().equalsIgnoreCase("open")) {
						sys_hash.put("STATUS", "打开");
					} else {
						sys_hash.put("STATUS", "关闭");
					}
					if (rs.getString(7).toString().equalsIgnoreCase("stopped")) {
						sys_hash.put("ARCHIVER", "NOARCHIVERLOG");
					} else {
						sys_hash.put("ARCHIVER", "ARCHIVERLOG");
					}
					break;
				}
			}
			rs = util.stmt.executeQuery(sqlVer);
			Vector bannerV = new Vector();
			while (rs.next()) {
				bannerV.add(rs.getString("BANNER").toString());
			}
			sys_hash.put("BANNER", bannerV);
			return_hash.put("sysinfo", sys_hash);

			// SGA、PGA信息
			Hashtable ga_hash = new Hashtable();
			rs = util.stmt.executeQuery(sqlsga);
			while (rs.next()) {
				ga_hash.put(rs.getString("COMPONENT").toString().replaceAll(
						" ", "_"), rs.getString("VAL").toString());
			}
			rs.close();
			sqlsga = "select a.POOL,round(a.BYTES/1024/1024,0) as val from v$sgastat a";
			rs = util.stmt.executeQuery(sqlsga);
			while (rs.next()) {
				if (rs.getString("POOL") != null) {
					if (rs.getString("POOL").toString().equalsIgnoreCase(
							"java pool")) {
						sys_hash.put("java_pool", rs.getString("VAL")
								.toString());
					}
				}
			}
			rs.close();

			// PGA信息
			rs = util.stmt.executeQuery(sqlpga);
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				String tmp_name = rs.getString(1).toString();
				String tmp_value = rs.getString(2).toString();
				if (tmp_name.equalsIgnoreCase("cache hit percentage")) {
					tmp_value = tmp_value + "%";
				} else if (tmp_name
						.equalsIgnoreCase("aggregate PGA target parameter")) {
					float tmp_v = new Float(tmp_value).floatValue();
					tmp_value = tmp_v / 1024 / 1024 + "MB";
				} else {
					float tmp_v = 0;
					try {
						tmp_v = new Float(tmp_value);
						if (tmp_v / 1024 >= 1000) {
							tmp_value = new Float(tmp_v / 1024 / 1024)
									.floatValue()
									+ "MB";
						} else {
							tmp_value = new Float(tmp_v / 1024).floatValue()
									+ "KB";
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				ga_hash.put(tmp_name.replaceAll(" ", "_"), tmp_value);
			}
			return_hash.put("ga_hash", ga_hash);
			rs.close();
			// 锁信息
			// Vector returnVal4 = new Vector();
			// rs = util.stmt.executeQuery(sqlLock);
			// ResultSetMetaData rsmd4 = null;
			// if(rs.next()) {
			// rsmd4 = rs.getMetaData();
			// }
			// while (rs.next()) {
			// Hashtable return_value = new Hashtable();
			// for (int i = 1; i <= rsmd4.getColumnCount(); i++) {
			// String col = rsmd4.getColumnName(i);
			// if (rs.getString(i) != null) {
			// String tmp = rs.getString(i).toString();
			// return_value.put(col.toLowerCase(), tmp);
			// } else
			// return_value.put(col.toLowerCase(), "--");
			// }
			// returnVal4.addElement(return_value);
			// return_value = null;
			// }
			// return_hash.put("lock", returnVal4);
			// rs.close();

			// 锁信息
			Vector returnVal4 = new Vector();
			Vector returnval_1 = new Vector();
			Vector returnval_2 = new Vector();
			String sql_1 = "select distinct substr(s.username,1,18) username,s.sid as sid,s.status status,s.MACHINE machine,"
					+ "s.type sessiontype,to_char(s.LOGON_TIME,'yyyy-mm-dd hh24-mi-ss') logontime,"
					+ "substr(s.program,1,15) program from v$session s";
			String sql_2 = "select a.sid as sid,a.type as locktype,a.lmode as lmode,a.request as request  from v$lock a";

			rs = util.stmt.executeQuery(sql_1);
			ResultSetMetaData rsmd_1 = null;
			if (rs.next()) {
				rsmd_1 = rs.getMetaData();
			}
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd_1.getColumnCount(); i++) {
					String col = rsmd_1.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnval_1.addElement(return_value);
				return_value = null;
			}
			rs.close();
			rs = util.stmt.executeQuery(sql_2);
			ResultSetMetaData rsmd_2 = null;
			if (rs.next()) {
				rsmd_2 = rs.getMetaData();
			}
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd_2.getColumnCount(); i++) {
					String col = rsmd_2.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnval_2.addElement(return_value);
				return_value = null;
			}
			rs.close();
			for (int i = 0; i < returnval_1.size(); i++) {
				Hashtable return_value = new Hashtable();
				Hashtable return_value1 = new Hashtable();
				return_value1 = (Hashtable) returnval_1.get(i);
				for (int j = 0; j < returnval_2.size(); j++) {
					Hashtable return_value2 = new Hashtable();
					return_value2 = (Hashtable) returnval_2.get(j);
					if (return_value2.get("sid").equals(
							return_value1.get("sid"))) {
						return_value.put("username", return_value1
								.get("username"));
						return_value.put("status", return_value1.get("status"));
						return_value.put("machine", return_value1
								.get("machine"));
						return_value.put("sessiontype", return_value1
								.get("sessiontype"));
						return_value.put("logontime", return_value1
								.get("logontime"));
						return_value.put("program", return_value1
								.get("program"));

						return_value.put("locktype", return_value2
								.get("locktype"));
						return_value.put("lmode", return_value2.get("lmode"));
						return_value.put("request", return_value2
								.get("request"));

						returnVal4.add(return_value);
						break;
					}
				}
			}
			return_hash.put("lock", returnVal4);

			// 缓存命中率
			Hashtable memoryPerf = new Hashtable();
			String sql = "select round((1 - (sum(decode(name, 'physical reads', value, 0)) /(sum(decode(name, 'db block gets', value, 0)) +sum(decode(name, 'consistent gets', value, 0))))) * 100) as HitRatio from v$sysstat";
			try {
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					memoryPerf.put("buffercache", rs.getString("HitRatio")
							.toString());
					// SysLogger.info("buffercache :
					// "+rs.getString("HitRatio").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}

			// 数据字典命中率
			sql = "select round((1 - (sum(getmisses) / sum(gets))) * 100) as HitRatio from v$rowcache";
			try {
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					memoryPerf.put("dictionarycache", rs.getString("HitRatio")
							.toString());
					// SysLogger.info("dictionarycache :
					// "+rs.getString("HitRatio").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}

			// 库缓存命中率
			sql = "select round(sum(pins) / (sum(pins) + sum(reloads)) * 100) as HitRatio from v$librarycache";
			try {
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					memoryPerf.put("librarycache", rs.getString("HitRatio")
							.toString());
					// SysLogger.info("librarycache :
					// "+rs.getString("HitRatio").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}

			// 内存中的排序
			sql = "select a.value as DiskSorts,b.value as MemorySorts,round((100 * b.value) /decode((a.value + b.value), 0, 1, (a.value + b.value)),2) as PctMemorySorts from v$sysstat a, v$sysstat b where a.name = 'sorts (disk)' and b.name = 'sorts (memory)'";
			try {
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					memoryPerf.put("pctmemorysorts", rs.getString(
							"PctMemorySorts").toString());
					// SysLogger.info("pctmemorysorts :
					// "+rs.getString("PctMemorySorts").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}

			// 最浪费内存的前10个语句占全部内存读取量的比例
			sql = "select sum(pct_bufgets) as pctbufgets from (select rank() over(order by buffer_gets desc) as rank_bufgets,to_char(100 * ratio_to_report(buffer_gets) over(), '999.99') as pct_bufgets from v$sqlarea) where rank_bufgets < 11";
			try {
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					memoryPerf.put("pctbufgets", rs.getString("pctbufgets")
							.toString());
					// SysLogger.info("pctbufgets :
					// "+rs.getString("pctbufgets").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}
			return_hash.put("memoryPerf", memoryPerf);

			Hashtable cursors = new Hashtable();
			// //最大游标
			// sql = "show parameter open_cursors";
			// try{
			// rs = util.stmt.executeQuery(sql);
			// while (rs.next()) {
			// cursors.put("config", rs.getString("value").toString());
			// SysLogger.info("config : "+rs.getString("value").toString());
			// }
			// }catch(Exception e){
			// e.printStackTrace();
			// }finally{
			// rs.close();
			// }
			// return_hash.put("memoryPerf", memoryPerf);

			// 打开的游标
			sql = "select count(*) as opencur from v$open_cursor";
			try {
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					cursors.put("opencur", rs.getString("opencur").toString());
					// SysLogger.info("opencur :
					// "+rs.getString("opencur").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}

			// 当前连接的游标
			sql = "select count(*) as curconnect  from   v$session";
			try {
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					cursors.put("curconnect", rs.getString("curconnect")
							.toString());
					// SysLogger.info("curconnect :
					// "+rs.getString("curconnect").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}
			// SysLogger.info(cursors.size()+" ############################");
			return_hash.put("cursors", cursors);

			// 表信息
			Vector returnVal5 = new Vector();
			rs = util.stmt.executeQuery(sqlTable);
			ResultSetMetaData rsmd5 = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd5.getColumnCount(); i++) {
					String col = rsmd5.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal5.addElement(return_value);
				return_value = null;
			}
			return_hash.put("table", returnVal5);
			rs.close();

			// 最浪费内存的前10个语句
			Vector returnVal6 = new Vector();
			rs = util.stmt.executeQuery(sqlTsql);
			ResultSetMetaData rsmd6 = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd6.getColumnCount(); i++) {
					String col = rsmd6.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal6.addElement(return_value);
				return_value = null;
			}
			return_hash.put("topsql", returnVal6);
			rs.close();

			// 控制文件
			Vector returnVal7 = new Vector();
			rs = util.stmt.executeQuery(sqlcf);
			ResultSetMetaData rsmd7 = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd7.getColumnCount(); i++) {
					String col = rsmd7.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal7.addElement(return_value);
				return_value = null;
			}
			return_hash.put("controlfile", returnVal7);
			rs.close();

			//
			Hashtable sy_hash = new Hashtable();
			rs = util.stmt.executeQuery(sqlArc);
			while (rs.next()) {
				sy_hash.put("CREATED", rs.getString("CREATED").toString());
				sy_hash.put("Log_Mode", rs.getString("LOG_MODE").toString());
			}
			return_hash.put("sy_hash", sy_hash);
			rs.close();

			// 日志文件
			Vector returnVal8 = new Vector();
			rs = util.stmt.executeQuery(sqlLog);
			ResultSetMetaData rsmd8 = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd8.getColumnCount(); i++) {
					String col = rsmd8.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal8.addElement(return_value);
				return_value = null;
			}
			return_hash.put("log", returnVal8);
			rs.close();

			// 固定缓存对象
			Vector returnVal9 = new Vector();
			rs = util.stmt.executeQuery(sqlko);
			ResultSetMetaData rsmd9 = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd9.getColumnCount(); i++) {
					String col = rsmd9.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal9.addElement(return_value);
				return_value = null;
			}
			return_hash.put("keepobj", returnVal9);
			rs.close();

			// 监听状态
			String open_mode = null;
			rs = util.stmt.executeQuery(sqllstr);
			if (rs.next()) {
				open_mode = rs.getString("OPEN_MODE").toString();
			}
			return_hash.put("open_mode", open_mode);
			rs.close();

			// 字典管理表空间中的Extent总数
			Vector returnVal10 = new Vector();
			rs = util.stmt.executeQuery(sqlExtent);
			ResultSetMetaData rsmd10 = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd10.getColumnCount(); i++) {
					String col = rsmd10.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal10.addElement(return_value);
				return_value = null;
			}
			return_hash.put("extent", returnVal10);
			rs.close();

			// 数据库I/O状况
			Hashtable dbio = new Hashtable();
			try {
				// SysLogger.info(sqldbio);
				rs = util.stmt.executeQuery(sqldbio);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// ResultSetMetaData waitrsmd = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();

				return_value.put("name", rs.getString("NAME").toString());
				return_value.put("file", rs.getString("FILENAME").toString());
				return_value.put("pyr", rs.getString("PYR").toString());
				return_value.put("pbr", rs.getString("PBR").toString());
				return_value.put("pyw", rs.getString("PYW").toString());
				return_value.put("pbw", rs.getString("PBW").toString());

				dbio.put(rs.getString("FILENAME").toString(), return_value);
				return_value = null;
			}
			return_hash.put("dbio", dbio);
			rs.close();

			// WAIT状况
			Vector waits = new Vector();
			try {
				rs = util.stmt.executeQuery(sqlWait);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// ResultSetMetaData waitrsmd = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();

				return_value.put("event", rs.getString("event").toString());
				return_value.put("prev", rs.getString("prev").toString());
				return_value.put("curr", rs.getString("curr").toString());
				return_value.put("tot", rs.getString("tot").toString());

				waits.addElement(return_value);
				return_value = null;
			}
			return_hash.put("wait", waits);
			rs.close();

			// 数据库用户信息
			Hashtable r_hash = new Hashtable();
			Vector returnVal_0 = new Vector();
			Vector returnVal_1 = new Vector();
			Vector returnVal_2 = new Vector();
			String sql1 = "Select USER# from v$session where type = 'USER' and username <>'null'";// 当前活动的用户
			String sql2 = "select USERNAME,USER_ID,ACCOUNT_STATUS from dba_users";// 所有用户
			String sql3 = "Select a.PARSING_USER_ID,sum(a.CPU_TIME),sum(a.SORTS),sum(a.BUFFER_GETS),sum(a.RUNTIME_MEM),sum(a.VERSION_COUNT),sum(a.DISK_READS) "
					+ "from v$sqlarea a,dba_users b where a.PARSING_USER_ID = b.user_id group by PARSING_USER_ID";
			rs = util.stmt.executeQuery(sql3);
			ResultSetMetaData rsmd11 = null;
			if (rs.next()) {
				rsmd11 = rs.getMetaData();
			}
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd11.getColumnCount(); i++) {
					String col = rsmd11.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal_0.addElement(return_value);
				return_value = null;
			}
			r_hash.put("returnVal0", returnVal_0);
			rs.close();
			rs = util.stmt.executeQuery(sql1);
			ResultSetMetaData rsmd12 = null;
			if (rs.next()) {
				rsmd12 = rs.getMetaData();
			}
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd12.getColumnCount(); i++) {
					String col = rsmd12.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal_1.addElement(return_value);
				return_value = null;
			}
			r_hash.put("returnVal1", returnVal_1);
			rs.close();
			rs = util.stmt.executeQuery(sql2);
			ResultSetMetaData rsmd13 = null;
			if (rs.next()) {
				rsmd13 = rs.getMetaData();
			}
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd13.getColumnCount(); i++) {
					String col = rsmd13.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal_2.addElement(return_value);
				return_value = null;
			}
			r_hash.put("returnVal2", returnVal_2);
			return_hash.put("user", r_hash);
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
			try {
				util.closeStmt();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				util.closeConn();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return return_hash;
	}

	public Hashtable getAllOracleData(String ip, int port, String db,
			String username, String password, Hashtable collectHash)
			throws Exception {
		Hashtable return_hash = new Hashtable();
		if (collectHash == null || collectHash.size() == 0)
			return return_hash;
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sqlSession = "select substr(s.username,1,18) username,s.status status,s.MACHINE machine,"
				+ "s.type sessiontype, to_char(s.LOGON_TIME,'yyyy-mm-dd hh24-mi-ss') logontime,"
				+ "substr(s.program,1,15) program,decode(s.command, 0,'No Command',1,"
				+ "'Create Table',2,'Insert',3,'Select',6,'Update',7,'Delete',9,"
				+ "'Create Index',15,'Alter Table',21,'Create View',23,'Validate Index',35,"
				+ "'Alter Database',39,'Create Tablespace',41,'Drop Tablespace',40,'Alter Tablespace'"
				+ ",53,'Drop User',62,'Analyze Table',63,'Analyze Index',s.command||': Other') "
				+ "command from v$session s,v$process p,v$transaction t,v$rollstat r,v$rollname n "
				+ "where s.paddr = p.addr and   s.taddr = t.addr (+) and   t.xidusn = r.usn (+) and"
				+ "  r.usn = n.usn (+) order by logon_time desc";
		// String sqlTablespace = "select a.tablespace_name \"Tablespace\", " +
		// "round(a.bytes/1024/1024,0) \"Size MB\","
		// + "round((decode(b.bytes,null,0,b.bytes))/1024/1024,0) \"Free MB\","
		// + "round((decode(b.bytes,null,0,b.bytes)) / a.bytes * 100,1)
		// \"Percent Free\","
		// + "c.file_name,c.status from sys.sm$ts_avail a, sys.sm$ts_free
		// b,dba_data_files c "
		// + "where a.tablespace_name = b.tablespace_name(+) and
		// a.tablespace_name=c.tablespace_name "
		// + "order by a.tablespace_name";
		/*
		 * String sqlTablespace = "select a.tablespace_name \"Tablespace\", " +
		 * "round(a.bytes/1024/1024,0) \"Size MB\"," +
		 * "round((decode(b.bytes,null,0,b.bytes))/1024/1024,0) \"Free MB\"," +
		 * "round((decode(b.bytes,null,0,b.bytes)) / a.bytes * 100,1) \"Percent
		 * Free\"," + "c.file_name,c.status from sys.sm$ts_avail a,
		 * sys.sm$ts_free b,dba_data_files c " + "where a.tablespace_name =
		 * b.tablespace_name(+) and a.tablespace_name=c.tablespace_name " +
		 * "order by a.tablespace_name";
		 */
		String sqlTablespace = "SELECT UPPER(F.TABLESPACE_NAME) \"Tablespace\", D.TOT_GROOTTE_MB \"Size MB\",F.TOTAL_BYTES \"Free MB\",chunks,"
				+ "TO_CHAR(ROUND( F.TOTAL_BYTES / D.TOT_GROOTTE_MB * 100,2),'990.99') \"Percent Free\" ,c.file_name,c.status "
				+ "FROM (SELECT TABLESPACE_NAME,　ROUND(SUM(BYTES) / (1024 * 1024), 2) TOTAL_BYTES,count(*) chunks,"
				+ "ROUND(MAX(BYTES) / (1024 * 1024), 2) MAX_BYTES　　FROM SYS.DBA_FREE_SPACE GROUP BY TABLESPACE_NAME) F,　　"
				+ "(SELECT DD.TABLESPACE_NAME,　　ROUND(SUM(DD.BYTES) / (1024 * 1024), 2) TOT_GROOTTE_MB　　FROM SYS.DBA_DATA_FILES DD　　"
				+ "GROUP BY DD.TABLESPACE_NAME) D,    dba_data_files c　　WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME "
				+ "and D.tablespace_name=c.tablespace_name　ORDER BY 1";

		String sqlRollback = "select distinct name \"Rollback Segment\", "
				+ "optsize \"Optimal\",shrinks, AveShrink \"Average Shrink\",Wraps, "
				+ "Extends from v$rollstat,v$rollname where "
				+ "v$rollstat.USN=v$rollname.USN order by name";
		String sqlSys = "select * from v$instance";
		String sqlVer = "select * from v$version";
		String sqlJob = "select job,log_user,to_char(last_date,'yyyy-mm-dd hh24-mi-ss') last_date,failures from dba_jobs";
		String sqlsga = "select a.COMPONENT,round(a.CURRENT_SIZE/1024/1024,0) as val from v$sga_dynamic_components a";
		String sqlpga = "select * from v$pgastat";
		String sqlLock = "select distinct substr(s.username,1,18) username,s.status status,s.MACHINE machine,"
				+ "s.type sessiontype,to_char(s.LOGON_TIME,'yyyy-mm-dd hh24-mi-ss') logontime,"
				+ "substr(s.program,1,15) program , a.type as locktype,a.lmode as lmode,"
				+ "a.request as request  from v$session s,v$lock a where a.sid=s.sid";
		String sqlTable = "select sum(bytes)/1024/1024 spaces,segment_name from dba_extents group by segment_name order by spaces desc";
		String sqlTsql = "select sql_text,pct_bufgets,username from (select rank() over(order by disk_reads desc) as rank_bufgets,"
				+ "to_char(100 * ratio_to_report(disk_reads) over(), '999.99') pct_bufgets,sql_text,b.username as username from"
				+ " v$sqlarea,dba_users b where b.user_id = PARSING_USER_ID) where rank_bufgets < 11";

		String sqlcf = "select * from v$controlfile";
		String sqlArc = "Select Created, Log_Mode From V$Database";
		String sqlLog = "select * from v$logfile";
		String sqlko = "select * from v$db_object_cache where sharable_mem > 100000 and type in ('PACKAGE', 'PACKAGE BODY', 'PROCEDURE', 'FUNCTION')";
		String sqllstr = "select open_mode from v$database";
		String sqlExtent = "select a.tablespace_name, sum(a.extents) from dba_segments a, dba_tablespaces b "
				+ "where a.tablespace_name = b.tablespace_name and b.extent_management = 'DICTIONARY' "
				+ "group by a.tablespace_name order by sum(a.extents)";
		String sqlWait = "SELECT EVENT,SUM(DECODE(WAIT_TIME,0,0,1)) PREV,SUM(DECODE(WAIT_TIME,0,1,0)) CURR,COUNT(*) TOT FROM V$SESSION_WAIT GROUP BY EVENT ORDER BY 4";
		String sqldbio = "SELECT DF.TABLESPACE_NAME NAME,DF.FILE_NAME FILENAME,F.PHYRDS PYR,"
				+ "F.PHYBLKRD PBR,F.PHYWRTS PYW, F.PHYBLKWRT PBW FROM V$FILESTAT F, DBA_DATA_FILES DF WHERE F.FILE# = DF.FILE_ID "
				+ " ORDER BY DF.TABLESPACE_NAME";

		// HONGLI ADD
		StringBuffer baseInfoSql = new StringBuffer();// 基本配置
		baseInfoSql.append(" SELECT name, value ");
		baseInfoSql.append(" FROM v$parameter ");
		baseInfoSql
				.append(" WHERE name in ('processes', 'sessions', 'cpu_count', 'control_files',  ");
		baseInfoSql
				.append(" 'compatible', 'db_files', 'db_files', 'log_checkpoint_interval', ");
		baseInfoSql
				.append(" 'db_create_file_dest', 'dml_locks', 'transactions', ");
		baseInfoSql
				.append(" 'transactions_per_rollback_segment', 'max_rollback_segments', ");
		baseInfoSql.append(" 'rollback_segments', 'undo_tablespace', ");
		baseInfoSql.append(" 'instance_name', 'service_names', ");
		baseInfoSql
				.append(" 'background_dump_dest', 'user_dump_dest', 'core_dump_dest', ");
		baseInfoSql.append(" 'db_name', 'open_cursors', ");
		baseInfoSql.append(" 'sort_area_size',  ");
		baseInfoSql.append(" 'statistic_level', ");
		baseInfoSql.append(" 'sga_max_size', ");
		baseInfoSql.append(" 'shared_pool_size', ");
		baseInfoSql.append(" 'log_buffer', ");
		baseInfoSql.append(" 'java_pool_size', ");
		baseInfoSql.append(" 'large_pool_size', ");
		baseInfoSql.append(" 'pga_aggregate_target', ");
		baseInfoSql.append(" 'db_block_size', 'db_block_buffers')  ");
		baseInfoSql.append(" OR name like 'log_archive_%' ");
		baseInfoSql.append(" OR name like 'db%cache_size' ");
		baseInfoSql.append(" UNION ALL ");
		baseInfoSql.append(" SELECT 'DB_DATA_CACHE_SIZE' name, TO_CHAR(GREATEST(v9o.value, v8b.value)) value ");
		baseInfoSql.append(" FROM (SELECT SUM(value) value ");
		baseInfoSql.append(" FROM v$parameter ");
		baseInfoSql.append(" WHERE name='db_cache_size' ");
		baseInfoSql.append(" OR name LIKE 'db__k_cache_size' ");
		baseInfoSql.append(" OR name LIKE 'db___k_cache_size') v9o, ");
		baseInfoSql.append(" (SELECT a.bufs*b.blksz value ");
		baseInfoSql
				.append(" FROM (SELECT value bufs FROM v$parameter WHERE name='db_block_buffers') a, ");
		baseInfoSql
				.append(" (SELECT value blksz FROM v$parameter WHERE name='db_block_size') b ) v8b ");
		baseInfoSql.append(" UNION ALL ");
		baseInfoSql.append(" SELECT parameter name, value ");
		baseInfoSql.append(" FROM v$nls_parameters ");
		baseInfoSql.append(" UNION ALL ");
		baseInfoSql.append(" SELECT 'DB_'||parameter name, value ");
		baseInfoSql.append(" FROM nls_database_parameters ");

		StringBuffer lockSqlBuffer = new StringBuffer();
		lockSqlBuffer
				.append(" select currentsessioncount,useablesessioncount,  trunc(currentsessioncount*100/useablesessioncount,2)  useablesessionpercent,  lockdsessioncount , ");
		lockSqlBuffer
				.append(" lockwaitcount,deadlockcount,processcount,maxprocesscount,rollbacks,trunc(rollbacks*100/commits,5) rollbackcommitpercent   ");
		lockSqlBuffer
				.append(" from (select count(*) currentsessioncount from v$session) table1, ");
		lockSqlBuffer
				.append(" (SELECT display_value useablesessioncount FROM V$PARAMETER WHERE NAME = 'sessions') table2, ");
		lockSqlBuffer
				.append(" (select count(*) lockdsessioncount from v$locked_object t1,v$session t2,v$sqltext t3 ");
		lockSqlBuffer
				.append(" where t1.session_id=t2.sid and t2.sql_address=t3.address) table3, ");
		lockSqlBuffer.append(" (select count(*) lockwaitcount   ");
		lockSqlBuffer.append(" from v$session a, v$lock b  ");
		lockSqlBuffer.append(" where a.lockwait = b.kaddr ) table4,  ");
		lockSqlBuffer.append(" (select count(*) deadlockcount  ");
		lockSqlBuffer
				.append(" from v$locked_object t1,v$session t2,v$sqltext t3  ");
		lockSqlBuffer
				.append(" where t1.session_id=t2.sid and t2.sql_address=t3.address) table5,  ");
		lockSqlBuffer
				.append(" (select count(*) processcount from v$process) table6,  ");
		lockSqlBuffer
				.append(" (select value maxprocesscount from v$parameter where name = 'processes') table7,  ");
		lockSqlBuffer
				.append(" (select sum(shrinks) rollbacks from v$rollstat, v$rollname where v$rollstat.usn = v$rollname.usn) table8, ");
		lockSqlBuffer
				.append(" (select value  commits from v$sysstat where name = 'user commits') table9");

		StringBuffer topReadAndWriteSql = new StringBuffer();
		topReadAndWriteSql
				.append(" SELECT * FROM (SELECT sql_text sqltext,disk_reads totaldisk ,executions totalexec, ");
		topReadAndWriteSql
				.append(" trunc(disk_reads/executions,2) diskreads FROM v$sql WHERE executions>0 ");
		topReadAndWriteSql
				.append(" AND is_obsolete='N' ORDER BY 4 desc) WHERE ROWNUM<11");

		StringBuffer topSortSql = new StringBuffer();
		topSortSql
				.append(" SELECT sql_text sqltext, sorts,executions,trunc(\"Sorts/Exec\",2) sortsexec  ");
		topSortSql
				.append(" FROM (SELECT SQL_TEXT, SORTS, EXECUTIONS, SORTS/EXECUTIONS \"Sorts/Exec\" ");
		topSortSql.append(" FROM V$SQLAREA ");
		topSortSql.append(" WHERE EXECUTIONS>0 ");
		topSortSql.append(" AND SORTS>0 ");
		topSortSql.append(" ORDER BY ");
		topSortSql.append(" 4 DESC) ");
		topSortSql.append(" WHERE ROWNUM<11 ");

		OracleJdbcUtil util = null;
		ResultSet rs = null;

		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();

			if (collectHash.containsKey("session")) {
				// 获取session信息
				// SysLogger.info("开始采集SESSION 信息 #######################");
				Vector returnVal1 = new Vector();
				try {
					rs = util.stmt.executeQuery(sqlSession);
					ResultSetMetaData rsmd1 = rs.getMetaData();
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd1.getColumnCount(); i++) {
							String col = rsmd1.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						returnVal1.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}

				return_hash.put("session", returnVal1);
			}

			if (collectHash.containsKey("tablespace")) {
				// 获取表空间信息
				Vector returnVal2 = new Vector();
				Map<String, Integer> names = new HashMap<String, Integer>();
				rs = null;
				try {
					rs = util.stmt.executeQuery(sqlTablespace);
					ResultSetMetaData rsmd2 = rs.getMetaData();
					while (rs.next()) {
						String name = rs.getString("Tablespace");
						if (names.get(name) == null) {
							Hashtable return_value = new Hashtable();
							names.put(name, 1);
							for (int i = 1; i <= rsmd2.getColumnCount(); i++) {
								String col = rsmd2.getColumnName(i);
								if (rs.getString(i) != null) {
									String tmp = rs.getString(i).toString();
									return_value.put(col.toLowerCase().replace(
											" ", "_"), tmp);
								} else
									return_value.put(col.toLowerCase(), "--");
							}
							returnVal2.addElement(return_value);
							return_value = null;
						} else {
							for (int i = 0; i < returnVal2.size(); i++) {
								Hashtable infos = (Hashtable) returnVal2.get(i);
								if (name.equals(infos.get("tablespace"))) {
									String tem1 = (String) infos.get("free_mb");
									String chunks = (String) infos
											.get("chunks");
									String tem2 = rs.getString("Free MB");
									float te1 = 0;
									float te2 = 0;
									if (tem1 != null && !"".equals(tem1))
										te1 = Float.parseFloat(tem1);
									if (tem2 != null && !"".equals(tem2))
										te2 = Float.parseFloat(tem2);
									String total1 = (String) infos
											.get("size_mb");
									String total2 = rs.getString("Size MB");
									float tt1 = 0;
									float tt2 = 0;
									if (total1 != null && !"".equals(total1)) {
										tt1 = Float.parseFloat(total1);
									}
									if (total2 != null && !"".equals(total2)) {
										tt2 = Float.parseFloat(total2);
									}
									float f = (te1 + te2) / (tt1 + tt2);
									infos.put("chunks", chunks);// 每个表空间空闲块数目
									infos.put("percent_free", String
											.valueOf(f * 100));
									if (i == returnVal2.size() - 1) {
										f = te2 / tt2;
										infos.put("size_mb", String
												.valueOf(tt2));
										infos.put("percent_free", String
												.valueOf(f * 100));
										infos.put("free_mb", String
												.valueOf(te2));
									} else {
										infos.put("size_mb", String.valueOf(tt1
												+ tt2));
										infos.put("percent_free", String
												.valueOf(f * 100));
										infos.put("free_mb", String.valueOf(te1
												+ te2));
									}
									String path = rs.getString("file_name");
									int len = path.lastIndexOf("\\");
									if (len != -1) {
										String tpath = path.substring(0, len);
										infos.put("file_name", tpath);
									}
									break;
								}
							}

						}

					}
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}

				return_hash.put("tablespace", returnVal2);
				// rs.close();
			}

			if (collectHash.containsKey("rollback")) {
				// 回滚段信息
				Vector returnVal3 = new Vector();
				rs = null;
				try {
					rs = util.stmt.executeQuery(sqlRollback);
					ResultSetMetaData rsmd3 = rs.getMetaData();
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd3.getColumnCount(); i++) {
							String col = rsmd3.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase().replaceAll(
										" ", "_"), tmp);
							} else
								return_value.put(col.toLowerCase().replaceAll(
										" ", "_"), "--");
						}
						returnVal3.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}

				return_hash.put("rollback", returnVal3);
			}

			Hashtable sys_hash = new Hashtable();
			if (collectHash.containsKey("sysinfo")) {
				// 数据库系统信息
				rs = null;
				try {
					rs = util.stmt.executeQuery(sqlSys);
					while (rs.next()) {
						if (rs.getString(2) != null) {
							// 得到该数据库的实例
							sys_hash.put("INSTANCE_NAME", rs.getString(2)
									.toString());
							sys_hash.put("HOST_NAME", rs.getString(3)
									.toString());
							sys_hash.put("DBNAME", db);
							sys_hash.put("VERSION", rs.getString(4).toString());
							sys_hash.put("STARTUP_TIME", rs.getDate(5) + " "
									+ rs.getTime(5));
							if (rs.getString(6).toString().equalsIgnoreCase(
									"open")) {
								sys_hash.put("STATUS", "打开");
							} else {
								sys_hash.put("STATUS", "关闭");
							}
							if (rs.getString(7).toString().equalsIgnoreCase(
									"stopped")) {
								sys_hash.put("ARCHIVER", "NOARCHIVERLOG");
							} else {
								sys_hash.put("ARCHIVER", "ARCHIVERLOG");
							}
							break;
						}
					}
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}

				rs = null;
				try {
					rs = util.stmt.executeQuery(sqlVer);
					Vector bannerV = new Vector();
					while (rs.next()) {
						bannerV.add(rs.getString("BANNER").toString());
					}
					sys_hash.put("BANNER", bannerV);
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}

				return_hash.put("sysinfo", sys_hash);
			}

			Hashtable ga_hash = new Hashtable();
			if (collectHash.containsKey("sga")) {
				// SGA信息
				rs = null;
				try {
					rs = util.stmt.executeQuery(sqlsga);
					while (rs.next()) {
						ga_hash.put(rs.getString("COMPONENT").toString()
								.replaceAll(" ", "_"), rs.getString("VAL")
								.toString());
					}
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}

				rs = null;
				sqlsga = "select a.POOL,round(a.BYTES/1024/1024,0) as val from v$sgastat a";
				try {
					rs = util.stmt.executeQuery(sqlsga);
					while (rs.next()) {
						if (rs.getString("POOL") != null) {
							if (rs.getString("POOL").toString()
									.equalsIgnoreCase("java pool")) {
								sys_hash.put("java_pool", rs.getString("VAL")
										.toString());
							}
						}
					}
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}
			}

			if (collectHash.containsKey("pga")) {
				// PGA信息
				rs = null;
				try {
					rs = util.stmt.executeQuery(sqlpga);
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						String tmp_name = rs.getString(1).toString();
						String tmp_value = rs.getString(2).toString();
						if (tmp_name.equalsIgnoreCase("cache hit percentage")) {
							tmp_value = tmp_value + "%";
						} else if (tmp_name
								.equalsIgnoreCase("aggregate PGA target parameter")) {
							float tmp_v = new Float(tmp_value).floatValue();
							tmp_value = tmp_v / 1024 / 1024 + "MB";
						} else {
							float tmp_v = 0;
							try {
								tmp_v = new Float(tmp_value);
								if (tmp_v / 1024 >= 1000) {
									tmp_value = new Float(tmp_v / 1024 / 1024)
											.floatValue()
											+ "MB";
								} else {
									tmp_value = new Float(tmp_v / 1024)
											.floatValue()
											+ "KB";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
						ga_hash.put(tmp_name.replaceAll(" ", "_"), tmp_value);
					}
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}

				return_hash.put("ga_hash", ga_hash);
			}

			// 锁信息
			// Vector returnVal4 = new Vector();
			// rs = util.stmt.executeQuery(sqlLock);
			// ResultSetMetaData rsmd4 = null;
			// if(rs.next()) {
			// rsmd4 = rs.getMetaData();
			// }
			// while (rs.next()) {
			// Hashtable return_value = new Hashtable();
			// for (int i = 1; i <= rsmd4.getColumnCount(); i++) {
			// String col = rsmd4.getColumnName(i);
			// if (rs.getString(i) != null) {
			// String tmp = rs.getString(i).toString();
			// return_value.put(col.toLowerCase(), tmp);
			// } else
			// return_value.put(col.toLowerCase(), "--");
			// }
			// returnVal4.addElement(return_value);
			// return_value = null;
			// }
			// return_hash.put("lock", returnVal4);
			// rs.close();

			if (collectHash.containsKey("lock")) {
				// 锁信息
				Vector returnVal4 = new Vector();
				Vector returnval_1 = new Vector();
				Vector returnval_2 = new Vector();
				String sql_1 = "select distinct substr(s.username,1,18) username,s.sid as sid,s.status status,s.MACHINE machine,"
						+ "s.type sessiontype,to_char(s.LOGON_TIME,'yyyy-mm-dd hh24-mi-ss') logontime,"
						+ "substr(s.program,1,15) program from v$session s";
				String sql_2 = "select a.sid as sid,a.type as locktype,a.lmode as lmode,a.request as request  from v$lock a";

				rs = null;
				try {
					rs = util.stmt.executeQuery(sql_1);
					ResultSetMetaData rsmd_1 = null;
					if (rs.next()) {
						rsmd_1 = rs.getMetaData();
					}
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd_1.getColumnCount(); i++) {
							String col = rsmd_1.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						returnval_1.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}

				rs = null;
				try {
					rs = util.stmt.executeQuery(sql_2);
					ResultSetMetaData rsmd_2 = null;
					if (rs.next()) {
						rsmd_2 = rs.getMetaData();
					}
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd_2.getColumnCount(); i++) {
							String col = rsmd_2.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						returnval_2.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}

				for (int i = 0; i < returnval_1.size(); i++) {
					Hashtable return_value = new Hashtable();
					Hashtable return_value1 = new Hashtable();
					return_value1 = (Hashtable) returnval_1.get(i);
					for (int j = 0; j < returnval_2.size(); j++) {
						Hashtable return_value2 = new Hashtable();
						return_value2 = (Hashtable) returnval_2.get(j);
						if (return_value2.get("sid").equals(
								return_value1.get("sid"))) {
							return_value.put("username", return_value1
									.get("username"));
							return_value.put("status", return_value1
									.get("status"));
							return_value.put("machine", return_value1
									.get("machine"));
							return_value.put("sessiontype", return_value1
									.get("sessiontype"));
							return_value.put("logontime", return_value1
									.get("logontime"));
							return_value.put("program", return_value1
									.get("program"));

							return_value.put("locktype", return_value2
									.get("locktype"));
							return_value.put("lmode", return_value2
									.get("lmode"));
							return_value.put("request", return_value2
									.get("request"));

							returnVal4.add(return_value);
							break;
						}
					}
				}
				return_hash.put("lock", returnVal4);
			}

			Hashtable memoryPerf = new Hashtable();
			if (collectHash.containsKey("buffercache")) {
				// 缓存命中率
				String sql = "select round((1 - (sum(decode(name, 'physical reads', value, 0)) /(sum(decode(name, 'db block gets', value, 0)) +sum(decode(name, 'consistent gets', value, 0))))) * 100) as HitRatio from v$sysstat";
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						memoryPerf.put("buffercache", rs.getString("HitRatio")
								.toString());
						// SysLogger.info("buffercache :
						// "+rs.getString("HitRatio").toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					rs.close();
				}
			}

			if (collectHash.containsKey("dictionarycache")) {
				// 数据字典命中率
				String sql = "select round((1 - (sum(getmisses) / sum(gets))) * 100) as HitRatio from v$rowcache";
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						memoryPerf.put("dictionarycache", rs.getString(
								"HitRatio").toString());
						// SysLogger.info("dictionarycache :
						// "+rs.getString("HitRatio").toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					rs.close();
				}
			}

			if (collectHash.containsKey("librarycache")) {
				// 库缓存命中率
				String sql = "select round(sum(pins) / (sum(pins) + sum(reloads)) * 100) as HitRatio from v$librarycache";
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						memoryPerf.put("librarycache", rs.getString("HitRatio")
								.toString());
						// SysLogger.info("librarycache :
						// "+rs.getString("HitRatio").toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					rs.close();
				}
			}

			if (collectHash.containsKey("pctmemorysorts")) {
				// 内存中的排序
				String sql = "select a.value as DiskSorts,b.value as MemorySorts,round((100 * b.value) /decode((a.value + b.value), 0, 1, (a.value + b.value)),2) as PctMemorySorts from v$sysstat a, v$sysstat b where a.name = 'sorts (disk)' and b.name = 'sorts (memory)'";
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						memoryPerf.put("pctmemorysorts", rs.getString(
								"PctMemorySorts").toString());
						// SysLogger.info("pctmemorysorts :
						// "+rs.getString("PctMemorySorts").toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					rs.close();
				}
			}

			if (collectHash.containsKey("pctbufgets")) {
				// 最浪费内存的前10个语句占全部内存读取量的比例
				String sql = "select sum(pct_bufgets) as pctbufgets from (select rank() over(order by buffer_gets desc) as rank_bufgets,to_char(100 * ratio_to_report(buffer_gets) over(), '999.99') as pct_bufgets from v$sqlarea) where rank_bufgets < 11";
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						memoryPerf.put("pctbufgets", rs.getString("pctbufgets")
								.toString());
						// SysLogger.info("pctbufgets :
						// "+rs.getString("pctbufgets").toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					rs.close();
				}
				return_hash.put("memoryPerf", memoryPerf);
			}

			Hashtable cursors = new Hashtable();
			// //最大游标
			// sql = "show parameter open_cursors";
			// try{
			// rs = util.stmt.executeQuery(sql);
			// while (rs.next()) {
			// cursors.put("config", rs.getString("value").toString());
			// SysLogger.info("config : "+rs.getString("value").toString());
			// }
			// }catch(Exception e){
			// e.printStackTrace();
			// }finally{
			// rs.close();
			// }
			// return_hash.put("memoryPerf", memoryPerf);

			if (collectHash.containsKey("opencur")) {
				// 打开的游标
				String sql = "select count(*) as opencur from v$open_cursor";
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						cursors.put("opencur", rs.getString("opencur")
								.toString());
						// SysLogger.info("opencur :
						// "+rs.getString("opencur").toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					rs.close();
				}
			}

			if (collectHash.containsKey("curconnect")) {
				// 当前连接的游标
				String sql = "select count(*) as curconnect  from   v$session";
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						cursors.put("curconnect", rs.getString("curconnect")
								.toString());
						// SysLogger.info("curconnect :
						// "+rs.getString("curconnect").toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					rs.close();
				}
				// SysLogger.info(cursors.size()+"
				// ############################");
				return_hash.put("cursors", cursors);
			}

			if (collectHash.containsKey("table")) {
				// 表信息
				Vector returnVal5 = new Vector();
				rs = util.stmt.executeQuery(sqlTable);
				ResultSetMetaData rsmd5 = rs.getMetaData();
				while (rs.next()) {
					Hashtable return_value = new Hashtable();
					for (int i = 1; i <= rsmd5.getColumnCount(); i++) {
						String col = rsmd5.getColumnName(i);
						if (rs.getString(i) != null) {
							String tmp = rs.getString(i).toString();
							return_value.put(col.toLowerCase(), tmp);
						} else
							return_value.put(col.toLowerCase(), "--");
					}
					returnVal5.addElement(return_value);
					return_value = null;
				}
				return_hash.put("table", returnVal5);
				rs.close();
			}

			if (collectHash.containsKey("topsql")) {
				// 最浪费内存的前10个语句
				Vector returnVal6 = new Vector();
				rs = util.stmt.executeQuery(sqlTsql);
				ResultSetMetaData rsmd6 = rs.getMetaData();
				while (rs.next()) {
					Hashtable return_value = new Hashtable();
					for (int i = 1; i <= rsmd6.getColumnCount(); i++) {
						String col = rsmd6.getColumnName(i);
						if (rs.getString(i) != null) {
							String tmp = rs.getString(i).toString();
							return_value.put(col.toLowerCase(), tmp);
						} else
							return_value.put(col.toLowerCase(), "--");
					}
					returnVal6.addElement(return_value);
					return_value = null;
				}
				return_hash.put("topsql", returnVal6);
				rs.close();
			}

			if (collectHash.containsKey("controlfile")) {
				// 控制文件
				Vector returnVal7 = new Vector();
				rs = util.stmt.executeQuery(sqlcf);
				ResultSetMetaData rsmd7 = rs.getMetaData();
				while (rs.next()) {
					Hashtable return_value = new Hashtable();
					for (int i = 1; i <= rsmd7.getColumnCount(); i++) {
						String col = rsmd7.getColumnName(i);
						if (rs.getString(i) != null) {
							String tmp = rs.getString(i).toString();
							return_value.put(col.toLowerCase(), tmp);
						} else
							return_value.put(col.toLowerCase(), "--");
					}
					returnVal7.addElement(return_value);
					return_value = null;
				}
				return_hash.put("controlfile", returnVal7);
				rs.close();
			}
			
			if (collectHash.containsKey("job")) {
				// 作业
				Vector jobs = new Vector();
				rs = util.stmt.executeQuery(sqlJob);
				ResultSetMetaData rsmdjob = rs.getMetaData();
				while (rs.next()) {
					Hashtable return_value = new Hashtable();
					for (int i = 1; i <= rsmdjob.getColumnCount(); i++) {
						String col = rsmdjob.getColumnName(i);
						if (rs.getString(i) != null) {
							String tmp = rs.getString(i).toString();
							return_value.put(col.toLowerCase(), tmp);
						} else
							return_value.put(col.toLowerCase(), "--");
					}
					jobs.addElement(return_value);
					return_value = null;
				}
				return_hash.put("job", jobs);
				rs.close();
			}

			if (collectHash.containsKey("logmode")) {
				//
				Hashtable sy_hash = new Hashtable();
				rs = util.stmt.executeQuery(sqlArc);
				while (rs.next()) {
					sy_hash.put("CREATED", rs.getString("CREATED").toString());
					sy_hash
							.put("Log_Mode", rs.getString("LOG_MODE")
									.toString());
				}
				return_hash.put("sy_hash", sy_hash);
				rs.close();
			}

			if (collectHash.containsKey("log")) {
				// 日志文件
				Vector returnVal8 = new Vector();
				rs = util.stmt.executeQuery(sqlLog);
				ResultSetMetaData rsmd8 = rs.getMetaData();
				while (rs.next()) {
					Hashtable return_value = new Hashtable();
					for (int i = 1; i <= rsmd8.getColumnCount(); i++) {
						String col = rsmd8.getColumnName(i);
						if (rs.getString(i) != null) {
							String tmp = rs.getString(i).toString();
							return_value.put(col.toLowerCase(), tmp);
						} else
							return_value.put(col.toLowerCase(), "--");
					}
					returnVal8.addElement(return_value);
					return_value = null;
				}
				return_hash.put("log", returnVal8);
				rs.close();
			}

			if (collectHash.containsKey("keepobj")) {
				// 固定缓存对象
				Vector returnVal9 = new Vector();
				rs = util.stmt.executeQuery(sqlko);
				ResultSetMetaData rsmd9 = rs.getMetaData();
				while (rs.next()) {
					Hashtable return_value = new Hashtable();
					for (int i = 1; i <= rsmd9.getColumnCount(); i++) {
						String col = rsmd9.getColumnName(i);
						if (rs.getString(i) != null) {
							String tmp = rs.getString(i).toString();
							return_value.put(col.toLowerCase(), tmp);
						} else
							return_value.put(col.toLowerCase(), "--");
					}
					returnVal9.addElement(return_value);
					return_value = null;
				}
				return_hash.put("keepobj", returnVal9);
				rs.close();
			}

			if (collectHash.containsKey("openmode")) {
				// 监听状态
				String open_mode = null;
				rs = util.stmt.executeQuery(sqllstr);
				if (rs.next()) {
					open_mode = rs.getString("OPEN_MODE").toString();
				}
				return_hash.put("open_mode", open_mode);
				rs.close();
			}

			if (collectHash.containsKey("extent")) {
				// 字典管理表空间中的Extent总数
				Vector returnVal10 = new Vector();
				rs = util.stmt.executeQuery(sqlExtent);
				ResultSetMetaData rsmd10 = rs.getMetaData();
				while (rs.next()) {
					Hashtable return_value = new Hashtable();
					for (int i = 1; i <= rsmd10.getColumnCount(); i++) {
						String col = rsmd10.getColumnName(i);
						if (rs.getString(i) != null) {
							String tmp = rs.getString(i).toString();
							return_value.put(col.toLowerCase(), tmp);
						} else
							return_value.put(col.toLowerCase(), "--");
					}
					returnVal10.addElement(return_value);
					return_value = null;
				}
				return_hash.put("extent", returnVal10);
				rs.close();
			}

			if (collectHash.containsKey("dbio")) {
				// 数据库I/O状况
				Hashtable dbio = new Hashtable();
				try {
					// SysLogger.info(sqldbio);
					rs = util.stmt.executeQuery(sqldbio);
					while (rs.next()) {
						Hashtable return_value = new Hashtable();

						return_value.put("name", rs.getString("NAME")
								.toString());
						return_value.put("file", rs.getString("FILENAME")
								.toString());
						return_value.put("pyr", rs.getString("PYR").toString());
						return_value.put("pbr", rs.getString("PBR").toString());
						return_value.put("pyw", rs.getString("PYW").toString());
						return_value.put("pbw", rs.getString("PBW").toString());

						dbio.put(rs.getString("FILENAME").toString(),
								return_value);
						return_value = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
				return_hash.put("dbio", dbio);
			}

			if (collectHash.containsKey("wait")) {
				// WAIT状况
				Vector waits = new Vector();
				try {
					rs = util.stmt.executeQuery(sqlWait);
					while (rs.next()) {
						Hashtable return_value = new Hashtable();

						return_value.put("event", rs.getString("event")
								.toString());
						return_value.put("prev", rs.getString("prev")
								.toString());
						return_value.put("curr", rs.getString("curr")
								.toString());
						return_value.put("tot", rs.getString("tot").toString());

						waits.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
				return_hash.put("wait", waits);
			}

			if (collectHash.containsKey("user")) {
				// 数据库用户信息
				Hashtable r_hash = new Hashtable();
				Vector returnVal_0 = new Vector();
				Vector returnVal_1 = new Vector();
				Vector returnVal_2 = new Vector();
				String sql1 = "Select USER# from v$session where type = 'USER' and username <>'null'";// 当前活动的用户
				String sql2 = "select USERNAME,USER_ID,ACCOUNT_STATUS from dba_users";// 所有用户
				String sql3 = "Select a.PARSING_USER_ID,sum(a.CPU_TIME),sum(a.SORTS),sum(a.BUFFER_GETS),sum(a.RUNTIME_MEM),sum(a.VERSION_COUNT),sum(a.DISK_READS) "
						+ "from v$sqlarea a,dba_users b where a.PARSING_USER_ID = b.user_id group by PARSING_USER_ID";

				rs = null;
				try {
					rs = util.stmt.executeQuery(sql3);
					ResultSetMetaData rsmd11 = null;
					if (rs.next()) {
						rsmd11 = rs.getMetaData();
					}
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd11.getColumnCount(); i++) {
							String col = rsmd11.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						returnVal_0.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}

				r_hash.put("returnVal0", returnVal_0);

				rs = null;
				ResultSetMetaData rsmd12 = null;
				try {
					rs = util.stmt.executeQuery(sql1);
					if (rs.next()) {
						rsmd12 = rs.getMetaData();
					}
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd12.getColumnCount(); i++) {
							String col = rsmd12.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						returnVal_1.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}
				r_hash.put("returnVal1", returnVal_1);

				rs = null;
				try {
					rs = util.stmt.executeQuery(sql2);
					ResultSetMetaData rsmd13 = null;
					if (rs.next()) {
						rsmd13 = rs.getMetaData();
					}
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd13.getColumnCount(); i++) {
							String col = rsmd13.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						returnVal_2.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}
				r_hash.put("returnVal2", returnVal_2);
				return_hash.put("user", r_hash);
			}
			// 采集基本配置信息
			Hashtable<String, String> baseInfoHash = new Hashtable<String, String>();

			String keyString = "";
			String valueString = "";
			rs = null;
			try {
				rs = util.stmt.executeQuery(baseInfoSql.toString());
				while (rs.next()) {
					keyString = rs.getString(1);
					valueString = rs.getString(2);
					if (valueString == null) {
						valueString = "--";
					}
					baseInfoHash.put(keyString, valueString);
				}
			} catch (Exception e) {

			} finally {
				if (rs != null)
					rs.close();
			}
			return_hash.put("baseInfoHash", baseInfoHash);
			baseInfoHash = null;

			OracleLockInfo oracleLockInfo = new OracleLockInfo();
			rs = null;
			try {
				rs = util.stmt.executeQuery(lockSqlBuffer.toString());
				if (rs.next()) {
					oracleLockInfo.setDeadlockcount(rs
							.getString("deadlockcount"));
					oracleLockInfo.setLockwaitcount(rs
							.getString("lockwaitcount"));
					oracleLockInfo.setMaxprocesscount(rs
							.getString("maxprocesscount"));
					oracleLockInfo
							.setProcesscount(rs.getString("processcount"));
					oracleLockInfo.setCurrentsessioncount(rs
							.getString("currentsessioncount"));
					oracleLockInfo.setUseablesessioncount(rs
							.getString("useablesessioncount"));
					oracleLockInfo.setUseablesessionpercent(rs
							.getString("useablesessionpercent"));
					oracleLockInfo.setLockdsessioncount(rs
							.getString("lockdsessioncount"));
					oracleLockInfo.setRollbackcommitpercent(rs
							.getString("rollbackcommitpercent"));
					oracleLockInfo.setRollbacks(rs.getString("rollbacks"));
				}
			} catch (Exception e) {

			} finally {
				if (rs != null)
					rs.close();
			}

			return_hash.put("oracleLockInfo", oracleLockInfo);

			Vector<OracleTopSqlReadWrite> topSqlReadWriteVector = new Vector<OracleTopSqlReadWrite>();
			rs = util.stmt.executeQuery(topReadAndWriteSql.toString());
			while (rs.next()) {
				OracleTopSqlReadWrite oracleTopSqlReadWrite = new OracleTopSqlReadWrite();
				oracleTopSqlReadWrite.setSqltext(rs.getString("sqltext"));
				oracleTopSqlReadWrite.setTotaldisk(rs.getString("totaldisk"));
				oracleTopSqlReadWrite.setDiskreads(rs.getString("diskreads"));
				oracleTopSqlReadWrite.setTotalexec(rs.getString("totalexec"));
				topSqlReadWriteVector.add(oracleTopSqlReadWrite);
				oracleTopSqlReadWrite = null;
			}
			rs.close();
			return_hash.put("topSqlReadWriteVector", topSqlReadWriteVector);

			Vector<OracleTopSqlSort> topSqlSortVector = new Vector<OracleTopSqlSort>();
			rs = null;
			try {
				rs = util.stmt.executeQuery(topSortSql.toString());
				while (rs.next()) {
					OracleTopSqlSort oracleTopSqlSort = new OracleTopSqlSort();
					oracleTopSqlSort.setSqltext(rs.getString("sqltext"));
					oracleTopSqlSort.setExecutions(rs.getString("executions"));
					oracleTopSqlSort.setSorts(rs.getString("sorts"));
					oracleTopSqlSort.setSortsexec(rs.getString("sortsexec"));
					topSqlSortVector.add(oracleTopSqlSort);
					oracleTopSqlSort = null;
				}
			} catch (Exception e) {

			} finally {
				if (rs != null)
					rs.close();
			}
			return_hash.put("topSqlSortVector", topSqlSortVector);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
			try {
				util.closeStmt();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				util.closeConn();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return return_hash;
	}

	public Vector getSqlserverAltfile(String ip, String username,
			String password) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:jtds:sqlserver://" + ip
				+ ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
		String sql = "select a.fileid as fileid,floor(a.size/128) as size,b.name as dbname,a.name as name,a.filename as filename "
				+ "from sysaltfiles a,sysdatabases b where a.dbid=b.dbid";
		JdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new JdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						if ("fileid".equals(col)) {
							if ("1".equals(tmp)) {
								tmp = "数据文件";
							} else {
								tmp = "日志文件";
							}
						}
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();

		}
		return returnVal;
	}

	public Hashtable getSqlserverDB(String ip, String username, String password)
			throws Exception {
		Hashtable retValue = new Hashtable();
		String dburl = "jdbc:jtds:sqlserver://" + ip
				+ ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
		String sql = "select counter_name,instance_name,cntr_value from master.dbo.sysperfinfo where object_name ='SQLServer:Databases' and (counter_name='Data File(s) Size (KB)' or counter_name='Log File(s) Size (KB)') order by instance_name ";
		JdbcUtil util = null;
		ResultSet rs = null;
		Hashtable retdatabase = new Hashtable();
		Hashtable retlogfile = new Hashtable();
		Vector names = new Vector();
		try {
			util = new JdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			Hashtable alldatabase = new Hashtable();
			Hashtable alllogfile = new Hashtable();
			Hashtable database = new Hashtable();
			Hashtable logfile = new Hashtable();

			while (rs.next()) {
				database = new Hashtable();
				logfile = new Hashtable();
				Hashtable return_value = new Hashtable();
				String counter_name = rs.getString("counter_name").toString();
				String instance_name = rs.getString("instance_name").toString()
						.trim();

				int cntr_value = rs.getInt("cntr_value");
				if (counter_name.trim().equals("Data File(s) Size (KB)")) {
					// 数据文件大小
					/*--------------------------modify 2010-5-10--------------*/
					if (alldatabase.get(instance_name) == null) {
						database.put("dbname", instance_name);
						database.put("size", cntr_value / 1024);
						// SysLogger.info("DB :
						// "+instance_name+"==="+cntr_value);
						alldatabase.put(instance_name, database);
						names.add(instance_name);
					} else {
						Hashtable preValue = (Hashtable) alldatabase
								.get(instance_name);
						int presize = (Integer) preValue.get("size");
						preValue.put("size", presize + cntr_value / 1024);
					}
					/*--------------------------------------------------------*/

				} else if (counter_name.trim().equals("Log File(s) Size (KB)")) {
					// LOG文件大小
					if (alllogfile.get(instance_name) == null) {
						logfile.put("logname", instance_name);
						logfile.put("size", cntr_value / 1024);
						// SysLogger.info("LOG :
						// "+instance_name+"==="+cntr_value);
						alllogfile.put(instance_name, logfile);
					} else {
						Hashtable preVal = (Hashtable) alllogfile
								.get(instance_name);
						int presize = (Integer) preVal.get("size");
						preVal.put("size", presize + cntr_value / 1024);
					}

				}
			}
			sql = "select B.name as dbName,A.name as logicName,A.fileid,size from master.dbo.sysaltfiles A,master.dbo.sysdatabases B where A.dbid=B.dbid order by B.name";
			try {
				rs = util.stmt.executeQuery(sql);
				rsmd = rs.getMetaData();
				while (rs.next()) {
					String dbName = rs.getString("dbName").toString().trim();
					// String logicName = rs.getString(1).toString();
					String field = rs.getString("fileid").toString();
					int usedsize = rs.getInt("size");
					float f_usedsize = new Float(usedsize);
					if ("1".equals(field)) {
						// 数据文件
						if (alldatabase.get(dbName) != null) {
							Hashtable _database = (Hashtable) alldatabase
									.get(dbName);
							int preused = 0;
							if (_database.get("usedsize") != null) {
								preused = Integer.parseInt((String) _database
										.get("usedsize"));
							}
							_database.put("usedsize", String.valueOf(usedsize
									/ 1024 + preused));
							float allsize = new Float(_database.get("size")
									.toString());
							f_usedsize = usedsize / 1024 + preused;
							float f_usedperc = 100 * (f_usedsize / allsize);
							int usedperc = new Float(f_usedperc).intValue();
							_database.put("usedperc", usedperc + "");
							// SysLogger.info("DB : usedperc : "+usedperc);
							retdatabase.put(dbName, _database);

						}

					} else {
						// 日志文件
						if (alllogfile.get(dbName) != null) {
							Hashtable _logfile = (Hashtable) alllogfile
									.get(dbName);
							int preuse = 0;
							// //System.out.println(dbName);
							if (_logfile.get("usedsize") != null)
								preuse = Integer.parseInt((String) _logfile
										.get("usedsize"));

							_logfile.put("usedsize", String.valueOf(usedsize
									/ 1024 + preuse));
							// //System.out.println(_logfile.get("usedsize"));
							f_usedsize = usedsize / 1024 + preuse;
							float allsize = new Float(_logfile.get("size")
									.toString());
							float f_usedperc = (100 * f_usedsize / (allsize));
							int usedperc = new Float(f_usedperc).intValue();
							_logfile.put("usedperc", usedperc + "");
							// SysLogger.info("LOG : usedperc : "+usedperc);
							retlogfile.put(dbName, _logfile);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			retValue.put("database", retdatabase);
			retValue.put("logfile", retlogfile);
			retValue.put("names", names);
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();

		}
		return retValue;
	}

	public Vector getSqlserverProcesses(String ip, String username,
			String password) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:jtds:sqlserver://" + ip
				+ ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
		String sql = "select distinct a.spid,a.waittime,a.lastwaittype,a.waitresource,b.name as dbname,c.name as "
				+ "username,a.cpu,a.physical_io,a.memusage,a.login_time,a.last_batch,a.status,a.hostname,"
				+ "a.program_name,a.hostprocess,a.cmd,a.nt_domain,a.nt_username,a.net_library,a.loginame from "
				+ "sysprocesses a,sysdatabases b,sysusers c where a.dbid= b.dbid and a.uid=c.uid";
		JdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new JdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();

		}
		return returnVal;
	}

	public Vector getSqlserverUser(String ip, String username, String password)
			throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:jtds:sqlserver://" + ip
				+ ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
		String sql = "select * from sysusers";
		JdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new JdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();

		}
		return returnVal;
	}

	public Vector getSqlserverLockinfo(String ip, String username,
			String password) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:jtds:sqlserver://" + ip
				+ ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
		String sql = "select distinct a.rsc_text,a.rsc_dbid,b.name as dbname,a.rsc_indid,a.rsc_objid,a.rsc_type,a.rsc_flag,a.req_mode,a.req_status,"
				+ "a.req_refcnt,a.req_cryrefcnt,a.req_lifetime,a.req_spid,a.req_ecid,a.req_ownertype,a.req_transactionID from syslockinfo a,"
				+ "sysdatabases b where a.rsc_dbid=b.dbid;";
		JdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new JdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();

		}
		return returnVal;
	}

	public Hashtable getSqlServerSys(String ip, String username, String password)
			throws Exception {
		String dburl = "jdbc:jtds:sqlserver://" + ip
				+ ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
		String sql = "SELECT SERVERPROPERTY ('productlevel') as productlevel, @@VERSION as VERSION,SERVERPROPERTY('MACHINENAME') as MACHINENAME,SERVERPROPERTY('IsSingleUser') as IsSingleUser,SERVERPROPERTY('ProcessID') as ProcessID,SERVERPROPERTY('IsIntegratedSecurityOnly') as IsIntegratedSecurityOnly,SERVERPROPERTY('IsClustered') as IsClustered";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sys_hash = new Hashtable();
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString("productlevel") != null) {
					sys_hash.put("productlevel", rs.getString("productlevel"));
				}
				if (rs.getString("VERSION") != null) {
					sys_hash.put("VERSION", rs.getString("VERSION"));
				}
				if (rs.getString("MACHINENAME") != null) {
					sys_hash.put("MACHINENAME", rs.getString("MACHINENAME"));
				}
				if (rs.getString("IsSingleUser") != null) {
					String IsSingleUser = rs.getString("IsSingleUser");
					if (IsSingleUser.equalsIgnoreCase("1")) {
						sys_hash.put("IsSingleUser", "单个用户");
					} else {
						sys_hash.put("IsSingleUser", "非单个用户");
					}
				}
				if (rs.getString("ProcessID") != null) {
					sys_hash.put("ProcessID", rs.getString("ProcessID"));
				}
				if (rs.getString("IsIntegratedSecurityOnly") != null) {
					String IsSingleUser = rs
							.getString("IsIntegratedSecurityOnly");
					if (IsSingleUser.equalsIgnoreCase("1")) {
						sys_hash.put("IsIntegratedSecurityOnly", "集成安全性");
					} else {
						sys_hash.put("IsIntegratedSecurityOnly", "非集成安全性");
					}
				}
				if (rs.getString("IsClustered") != null) {
					String IsSingleUser = rs.getString("IsClustered");
					if (IsSingleUser.equalsIgnoreCase("1")) {
						sys_hash.put("IsClustered", "群集");
					} else {
						sys_hash.put("IsClustered", "非群集");
					}
				}
				// collectSQLServerMonitItemsDetail(util.jdbc());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();

		}
		return sys_hash;
	}

	public boolean getSqlserverIsOk(String ip, String username, String password)
			throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:jtds:sqlserver://" + ip
				+ ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
		String sql = "select distinct a.spid from sysprocesses a";
		JdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new JdbcUtil(dburl, username, password);
			util.jdbc();
			if (util.stmt == null) {// HONGLI ADD
				return false;
			}
			rs = util.stmt.executeQuery(sql);
			if (rs.next()) {

				sql = "select cntr_value from master.dbo.sysperfinfo where object_name ='SQLServer:Databases' and instance_name='##' and counter_name='Data File(s) Size (KB)'";
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				util.closeStmt();
				util.closeConn();
			} catch (Exception ep) {
			}

		}
	}

	public boolean getSysbaseIsOk(String ip, String username, String password,
			int port) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "";
		// if (ip.equals("10.10.152.30")) {
		dburl = "jdbc:sybase:Tds:" + ip + ":" + port
				+ "/master?charset=iso_1&jconnect_version=0";
		// } else
		// dburl = "jdbc:sybase:Tds:" + ip + ":" + port + "/master";
		// dburl =
		// "jdbc:sybase:Tds:"+ip+":"+port+"/master?charset=iso_1&jconnect_version=0";
		String sql = "SELECT @@CPU_BUSY";
		SysbaseJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new SysbaseJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				rs.close();
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
			try {
				util.closeStmt();
				util.closeConn();
			} catch (Exception ep) {
			}

		}
	}

	public Vector getOracleTableinfo(String ip, int port, String db,
			String username, String password) throws Exception {
		Vector returnVal = new Vector();
		// jdbc:oracle:thin:@MyDbComputerNameOrIP:1521:ORCL
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select a.tablespace_name \"Tablespace\", "
				+ "round(a.bytes/1024/1024,0) \"Size MB\","
				+ "round((decode(b.bytes,null,0,b.bytes))/1024/1024,0) \"Free MB\","
				+ "round((decode(b.bytes,null,0,b.bytes)) / a.bytes * 100,1) \"Percent Free\","
				+ "c.file_name,c.status from   sys.sm$ts_avail a, sys.sm$ts_free b,dba_data_files c  "
				+ "where  a.tablespace_name = b.tablespace_name(+) and a.tablespace_name=c.tablespace_name "
				+ "order by a.tablespace_name";
		OracleJdbcUtil util = null;
		// select a.tablespace_name "Tablespace", round(a.bytes/1024/1024,0)
		// "Size MB",round((decode(b.bytes,null,0,b.bytes))/1024/1024,0) "Free
		// MB",round((decode(b.bytes,null,0,b.bytes)) / a.bytes * 100,1)
		// "Percent Free",c.file_name,c.status from sys.sm$ts_avail a,
		// sys.sm$ts_free b,dba_data_files c where a.tablespace_name =
		// b.tablespace_name(+) and a.tablespace_name=c.tablespace_name order by
		// a.tablespace_name
		ResultSet rs = null;
		Map<String, Integer> names = new HashMap<String, Integer>();
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				String name = rs.getString("Tablespace");
				if (names.get(name) == null) {
					Hashtable return_value = new Hashtable();
					names.put(name, 1);
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						String col = rsmd.getColumnName(i);
						if (rs.getString(i) != null) {
							String tmp = rs.getString(i).toString();
							return_value.put(col.toLowerCase()
									.replace(" ", "_"), tmp);
						} else
							return_value.put(col.toLowerCase(), "--");
					}
					returnVal.addElement(return_value);
					return_value = null;
				} else {
					for (int i = 0; i < returnVal.size(); i++) {
						Hashtable infos = (Hashtable) returnVal.get(i);
						if (name.equals(infos.get("tablespace"))) {
							String tem1 = (String) infos.get("free_mb");
							String tem2 = rs.getString("Free MB");
							float te1 = 0;
							float te2 = 0;
							if (tem1 != null && !"".equals(tem1))
								te1 = Float.parseFloat(tem1);
							if (tem2 != null && !"".equals(tem2))
								te2 = Float.parseFloat(tem2);
							String total1 = (String) infos.get("size_mb");
							String total2 = rs.getString("Size MB");
							float tt1 = 0;
							float tt2 = 0;
							if (total1 != null && !"".equals(total1)) {
								tt1 = Float.parseFloat(total1);
							}
							if (total2 != null && !"".equals(total2)) {
								tt2 = Float.parseFloat(total2);
							}
							float f = (te1 + te2) / (tt1 + tt2);
							infos.put("percent_free", String.valueOf(f * 100));
							infos.put("size_mb", String.valueOf(tt1 + tt2));
							infos.put("free_mb", String.valueOf(te1 + te2));
							String path = rs.getString("file_name");
							int len = path.lastIndexOf("\\");
							if (len != -1) {
								String tpath = path.substring(0, len);
								infos.put("file_name", tpath);
							}
							break;
						}
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();

		}
		return returnVal;
	}

	// 检查表所占用的空间
	public Vector getOracleTable(String ip, int port, String db,
			String username, String password) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select sum(bytes)/1024/1024 spaces,segment_name from dba_extents where owner='"
				+ username.toUpperCase()
				+ "' group by segment_name order by spaces desc";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	// 查看控制文件
	public Vector getOracleControlFile(String ip, int port, String db,
			String username, String password) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select * from v$controlfile";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	// 查看数据库的创建日期和归档方式
	public Hashtable getOracleIsArchive(String ip, int port, String db,
			String username, String password) throws Exception {
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "Select Created, Log_Mode From V$Database";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sys_hash = new Hashtable();
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				sys_hash.put("CREATED", rs.getString("CREATED").toString());
				sys_hash.put("Log_Mode", rs.getString("LOG_MODE").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return sys_hash;
	}

	// 查看日志文件
	public Vector getOracleLogFile(String ip, int port, String db,
			String username, String password) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select * from v$logfile";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	public Hashtable getOracleUserinfo(String ip, int port, String db,
			String username, String password) {
		Hashtable return_hash = new Hashtable();
		Vector returnVal0 = new Vector();
		Vector returnVal1 = new Vector();
		Vector returnVal2 = new Vector();
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql1 = "Select USER# from v$session where type = 'USER' and username <>'null'";// 当前活动的用户
		String sql2 = "select USERNAME,USER_ID,ACCOUNT_STATUS from dba_users";// 所有用户
		String sql = "Select a.PARSING_USER_ID,sum(a.CPU_TIME),sum(a.SORTS),sum(a.BUFFER_GETS),sum(a.RUNTIME_MEM),sum(a.VERSION_COUNT),sum(a.DISK_READS) from v$sqlarea a,dba_users b where a.PARSING_USER_ID = b.user_id group by PARSING_USER_ID";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = null;
			if (rs.next()) {
				rsmd = rs.getMetaData();
			}
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal0.addElement(return_value);
				return_value = null;
			}
			return_hash.put("returnVal0", returnVal0);
			// rs.close();
			rs = util.stmt.executeQuery(sql1);
			ResultSetMetaData rsmd1 = null;
			if (rs.next()) {
				rsmd1 = rs.getMetaData();
			}
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd1.getColumnCount(); i++) {
					String col = rsmd1.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal1.addElement(return_value);
				return_value = null;
			}
			return_hash.put("returnVal1", returnVal1);
			// rs1.close();
			rs = util.stmt.executeQuery(sql2);
			ResultSetMetaData rsmd2 = null;
			if (rs.next()) {
				rsmd2 = rs.getMetaData();
			}
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd2.getColumnCount(); i++) {
					String col = rsmd2.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal2.addElement(return_value);
				return_value = null;
			}
			return_hash.put("returnVal2", returnVal2);
			// rs2.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return null;
				}
			}
			util.closeStmt();
			util.closeConn();
		}
		return return_hash;
	}

	// 内存中的排序,磁盘中的排序
	public Vector getOracleSort(String ip, int port, String db,
			String username, String password) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select a.value \"Disk Sorts\",b.value \"Memory Sorts\",round((100 * b.value) /"
				+ "decode((a.value + b.value), 0, 1, (a.value + b.value)),2) \"Pct Memory Sorts\" "
				+ "from v$sysstat a, v$sysstat b where a.name = 'sorts (disk)' and b.name = 'sorts (memory)'";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	// 调整前25个最浪费内存的语句
	public Hashtable getOracleTopSql(String ip, int port, String db,
			String username, String password) throws Exception {
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "set serveroutput on size 1000000 "
				+ "declare "
				+ "top25 number; "
				+ "text1 varchar2(4000); "
				+ "x     number; "
				+ "len1  number; "
				+ "cursor c1 is "
				+ "select buffer_gets, substr(sql_text, 1, 4000) from v$sqlarea order by buffer_gets desc; "
				+ "begin "
				+ "dbms_output.put_line('Gets' || '     ' || 'Text'); "
				+ "dbms_output.put_line('--------' || ' ' || '---------------'); "
				+ "open c1; "
				+ "for i in 1 .. 25 loop "
				+ "fetch c1 "
				+ "into top25, text1; "
				+ "dbms_output.put_line(rpad(to_char(top25), 9) || ' ' ||substr(text1, 1, 66)); "
				+ "len1 := length(text1); " + "x    := 66; "
				+ "while len1 > x - 1 loop "
				+ "dbms_output.put_line('        ' || substr(text1, x, 66)); "
				+ "x := x + 66; " + "end loop; " + "end loop; " + "end;";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sys_hash = new Hashtable();
		return sys_hash;
	}

	// 固定缓存对象
	public Vector getOracleKeepObj(String ip, int port, String db,
			String username, String password) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select * from v$db_object_cache where sharable_mem > 100000 and type in ('PACKAGE', 'PACKAGE BODY', 'PROCEDURE', 'FUNCTION')";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	// 监听的状态
	public String getOracleLstn(String ip, int port, String db,
			String username, String password) throws Exception {
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select open_mode from v$database";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getString("OPEN_MODE").toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
	}

	// 表的行链接问题
	public Hashtable getOracleLineConn(String ip, int port, String db,
			String username, String password) throws Exception {
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select count(*) chained_rows, table_name from chained_rows group by table_name";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sys_hash = new Hashtable();
		return sys_hash;
	}

	// 字典管理表空间中的Extent总数
	public Vector getOracleExtent(String ip, int port, String db,
			String username, String password) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select a.tablespace_name, sum(a.extents) from dba_segments a, dba_tablespaces b "
				+ "where a.tablespace_name = b.tablespace_name and b.extent_management = 'DICTIONARY' "
				+ "group by a.tablespace_name order by sum(a.extents)";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	//
	public Hashtable getOracleSys(String ip, int port, String db,
			String username, String password) throws Exception {
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select * from v$instance";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sys_hash = new Hashtable();
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString(2) != null) {
					// 得到该数据库的实例
					sys_hash.put("INSTANCE_NAME", rs.getString(2).toString());
					sys_hash.put("HOST_NAME", rs.getString(3).toString());
					sys_hash.put("DBNAME", db);
					sys_hash.put("VERSION", rs.getString(4).toString());
					sys_hash.put("STARTUP_TIME", rs.getDate(5) + " "
							+ rs.getTime(5));
					if (rs.getString(6).toString().equalsIgnoreCase("open")) {
						sys_hash.put("STATUS", "打开");
					} else {
						sys_hash.put("STATUS", "关闭");
					}
					if (rs.getString(7).toString().equalsIgnoreCase("stopped")) {
						sys_hash.put("ARCHIVER", "NOARCHIVERLOG");
					} else {
						sys_hash.put("ARCHIVER", "ARCHIVERLOG");
					}
					break;
				}
			}
			sql = "select * from v$version";
			rs = util.stmt.executeQuery(sql);
			Vector bannerV = new Vector();
			while (rs.next()) {
				bannerV.add(rs.getString("BANNER").toString());
			}
			sys_hash.put("BANNER", bannerV);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();

		}
		return sys_hash;
	}

	public Hashtable getDB2Sys(String ip, int port, String db, String username,
			String password) throws Exception {

		String[] dbs = db.split(",");
		DB2JdbcUtil util = null;
		ResultSet rs = null;
		Hashtable returnhash = new Hashtable();
		for (int i = 0; i < dbs.length; i++) {
			String dbStr = dbs[i];
			String dburl = "jdbc:db2://" + ip + ":" + port + "/" + dbStr;
			String sql = "select os_name, host_name, total_memory from table(sysproc.env_get_sys_info()) as systeminfo";
			Hashtable sys_hash = new Hashtable();
			try {
				util = new DB2JdbcUtil(dburl, username, password);
				util.jdbc();
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						sys_hash.put("os_name", rs.getString("os_name")
								.toString());
						sys_hash.put("host_name", rs.getString("HOST_NAME")
								.toString());
						sys_hash.put("total_memory", rs.getString(
								"total_memory").toString());
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
				sql = "select installed_prod, prod_release from table(sysproc.env_get_prod_info()) as productinfo";
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						sys_hash.put("installed_prod", rs.getString(
								"installed_prod").toString());
						sys_hash.put("prod_release", rs.getString(
								"prod_release").toString());
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
				sql = "SELECT db_name,db_path,db_conn_time,db_status,db_location,sqlm_elm_last_backup,total_cons,connections_top FROM TABLE( SNAPSHOT_DATABASE( '', -1 )) as SNAPSHOT_DATABASE";
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						sys_hash.put("db_name", rs.getString("db_name")
								.toString());
						sys_hash.put("db_path", rs.getString("db_path")
								.toString());
						sys_hash.put("db_conn_time", rs.getString(
								"db_conn_time").toString());
						sys_hash.put("db_status", rs.getString("db_status")
								.toString());
						sys_hash.put("db_location", rs.getString("db_location")
								.toString());
						if (rs.getString("sqlm_elm_last_backup") != null) {
							sys_hash.put("sqlm_elm_last_backup", rs.getString(
									"sqlm_elm_last_backup").toString());
						} else {
							sys_hash.put("sqlm_elm_last_backup", "");
						}
						sys_hash.put("total_cons", rs.getString("total_cons")
								.toString());
						sys_hash.put("connections_top", rs.getString(
								"connections_top").toString());
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
				// returnhash.put(dbStr, sys_hash);
				sql = "select db_name, rows_read, rows_selected, lock_waits, lock_wait_time, deadlocks, lock_escals, total_sorts, total_sort_time from table (snapshot_database ('', -1) ) as snapshot_database";
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						sys_hash.put("db_name", rs.getString("db_name")
								.toString());
						sys_hash.put("rows_read", rs.getString("rows_read")
								.toString());
						sys_hash.put("rows_selected", rs.getString(
								"rows_selected").toString());
						sys_hash.put("lock_waits", rs.getString("lock_waits")
								.toString());
						sys_hash.put("lock_wait_time", rs.getString(
								"lock_wait_time").toString());
						sys_hash.put("deadlocks", rs.getString("deadlocks")
								.toString());
						sys_hash.put("lock_escals", rs.getString("lock_escals")
								.toString());
						sys_hash.put("total_sorts", rs.getString("total_sorts")
								.toString());
						sys_hash.put("total_sort_time", rs.getString(
								"total_sort_time").toString());
						// retList.add(sys_hash);
						// sys_hash = new Hashtable();
						break;
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
				returnhash.put(dbStr, sys_hash);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null)
					rs.close();
				util.closeStmt();
				util.closeConn();
			}
		}
		return returnhash;
	}

	public Hashtable getDB2Space(String ip, int port, String db,
			String username, String password) throws Exception {
		String[] dbs = db.split(",");
		DB2JdbcUtil util = null;
		ResultSet rs = null;
		Hashtable returnhash = new Hashtable();
		for (int i = 0; i < dbs.length; i++) {
			String dbStr = dbs[i];
			String dburl = "jdbc:db2://" + ip + ":" + port + "/" + dbStr;
			// String sql = "select
			// aa.tablespace_name,aa.container_name,aa.container_type,
			// (aa.total_pages*bb.pagesize)/1024/1024 as
			// totalspac,(aa.usable_pages*bb.pagesize)/1024/1024 as
			// usablespac,(aa.usable_pages/(nullif(aa.total_pages,0)))*100 as
			// usableper from table(snapshot_container('',-1)) AS
			// AA,syscat.tablespaces bb where aa.tablespace_id=bb.tbspaceid";
			String sql = "select substr(tablespace_name,1,120) as tablespace_name,(usable_pages*bb.pagesize)/1024/1024 as totalspac,(free_pages*bb.pagesize)/1024/1024 as usablespac from table (snapshot_tbs_cfg (' ', -1)) as aa, syscat.tablespaces bb where aa.tablespace_id=bb.tbspaceid";
			Hashtable sys_hash = new Hashtable();
			List retList = new ArrayList();
			try {
				util = new DB2JdbcUtil(dburl, username, password);
				util.jdbc();
				rs = util.stmt.executeQuery(sql);
				Map<String, Integer> names = new HashMap<String, Integer>();
				while (rs.next()) {
					String name = rs.getString("tablespace_name").toString()
							.trim();
					if (names.get(name) == null) {
						sys_hash.put("tablespace_name", name);
						// sys_hash.put("container_name",
						// rs.getString("container_name").toString());
						// sys_hash.put("container_type",
						// rs.getString("container_type").toString());
						sys_hash.put("totalspac", rs.getString("totalspac")
								.toString());
						sys_hash.put("usablespac", rs.getString("usablespac")
								.toString());
						float totalsp = new Float(rs.getString("totalspac")
								.toString());
						float useablesp = new Float(rs.getString("usablespac")
								.toString());
						float usedperc = 0;
						if (!rs.getString("totalspac").toString().equals("0")) {
							if ((totalsp - useablesp) > 0) {
								usedperc = 100 * useablesp / totalsp;
								sys_hash.put("usableper", new Float(usedperc)
										.intValue()
										+ "");
							}
						} else {
							sys_hash.put("usableper", "");
						}
						/*
						 * if (rs.getString("usableper") == null){
						 * sys_hash.put("usableper", ""); }else
						 * sys_hash.put("usableper",
						 * rs.getString("usableper").toString());
						 */
						retList.add(sys_hash);
						sys_hash = new Hashtable();
						names.put(name, 1);
					} else {
						for (int j = 0; j < retList.size(); j++) {
							Hashtable col = (Hashtable) retList.get(j);
							if (name.equals(col.get("tablespace_name"))) {
								String v_total = (String) col.get("totalspac");
								String v_userspace = (String) col
										.get("usablespac");
								String c_total = rs.getString("totalspac");
								String c_userspace = rs.getString("usablespac");
								float f_v_total = 0;
								if (v_total != null && !"".equals(v_total))
									f_v_total = Float.parseFloat(v_total);
								float f_v_userspace = 0;
								if (v_userspace != null
										&& !"".equals(v_userspace))
									f_v_userspace = Float
											.parseFloat(v_userspace);
								float f_c_total = 0;
								if (c_total != null && !"".equals(c_total))
									f_c_total = Float.parseFloat(c_total);
								float f_c_userspace = 0;
								if (c_userspace != null
										&& !"".equals(c_userspace))
									f_c_userspace = Float
											.parseFloat(c_userspace);
								float total = f_v_total + f_c_total;
								float userspace = f_c_userspace + f_v_userspace;
								col.put("totalspac", String.valueOf(total));
								col
										.put("usablespac", String
												.valueOf(userspace));
								if (total > 0) {
									if ((total - userspace) > 0) {
										float usedperc = 100 * userspace
												/ total;
										col.put("usableper",
												new Float(usedperc).intValue()
														+ "");
									}
								} else {
									col.put("usableper", "");
								}
								break;
							}
						}
					}

				}
				returnhash.put(dbStr, retList);
			} catch (Exception e) {
				// e.printStackTrace();
			} finally {
				if (rs != null)
					rs.close();
				util.closeStmt();
				util.closeConn();
			}

		}
		return returnhash;
	}

	public Hashtable getDB2Pool(String ip, int port, String db,
			String username, String password) throws Exception {
		String[] dbs = db.split(",");
		DB2JdbcUtil util = null;
		ResultSet rs = null;
		Hashtable returnhash = new Hashtable();
		for (int i = 0; i < dbs.length; i++) {
			String dbStr = dbs[i];
			String dburl = "jdbc:db2://" + ip + ":" + port + "/" + dbStr;
			String sql = "select substr(bp_name,1,20) as bp_name, int ((1- (decimal(pool_data_p_reads) / nullif(pool_data_l_reads,0)))*100) as data_hit_ratio, int ((1-(decimal(pool_index_p_reads)/nullif(pool_index_l_reads,0)))*100) as index_hit_ratio, int ((1-(decimal(pool_data_p_reads+pool_index_p_reads)/nullif((pool_data_l_reads+pool_index_l_reads),0)))*100) as BP_hit_ratio, int ((1-(decimal(pool_async_data_reads+pool_async_index_reads)/nullif((pool_async_data_reads+pool_async_index_reads+direct_reads),0)))*100) as Async_read_pct, int ((1-(decimal(direct_writes)/nullif(direct_reads,0)))*100) as Direct_RW_Ratio from table (snapshot_bp ('', -1)) as snapshot_bp";
			Hashtable sys_hash = new Hashtable();
			Hashtable rethash = new Hashtable();
			List retList = new ArrayList();
			List readList = new ArrayList();
			List writeList = new ArrayList();
			try {
				util = new DB2JdbcUtil(dburl, username, password);
				util.jdbc();
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						sys_hash.put("bp_name", rs.getString("bp_name")
								.toString());
						if (rs.getString("data_hit_ratio") == null) {
							sys_hash.put("data_hit_ratio", "0");
						} else
							sys_hash.put("data_hit_ratio", rs.getString(
									"data_hit_ratio").toString());

						if (rs.getString("index_hit_ratio") == null) {
							sys_hash.put("index_hit_ratio", "0");
						} else
							sys_hash.put("index_hit_ratio", rs.getString(
									"index_hit_ratio").toString());

						if (rs.getString("BP_hit_ratio") == null) {
							sys_hash.put("BP_hit_ratio", "0");
						} else
							sys_hash.put("BP_hit_ratio", rs.getString(
									"BP_hit_ratio").toString());

						if (rs.getString("Async_read_pct") == null) {
							sys_hash.put("Async_read_pct", "0");
						} else
							sys_hash.put("Async_read_pct", rs.getString(
									"Async_read_pct").toString());

						if (rs.getString("Direct_RW_Ratio") == null) {
							sys_hash.put("Direct_RW_Ratio", "0");
						} else
							sys_hash.put("Direct_RW_Ratio", rs.getString(
									"Direct_RW_Ratio").toString());
						retList.add(sys_hash);
						sys_hash = new Hashtable();
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
				sql = "select substr(table_schema,1,10) as tbschema, substr(table_name,1,30) as tbname,rows_read,rows_written,overflow_accesses,page_reorgs from table (SNAPSHOT_TABLE('', -1)) as snapshot_table order by rows_read desc fetch first 10 rows only";
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						sys_hash.put("tbschema", rs.getString("tbschema")
								.toString());
						sys_hash.put("tbname", rs.getString("tbname")
								.toString());
						sys_hash.put("rows_read", rs.getString("rows_read")
								.toString());
						sys_hash.put("rows_written", rs.getString(
								"rows_written").toString());
						sys_hash.put("overflow_accesses", rs.getString(
								"overflow_accesses").toString());
						sys_hash.put("page_reorgs", rs.getString("page_reorgs")
								.toString());
						readList.add(sys_hash);
						sys_hash = new Hashtable();
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}

				sql = "select substr(table_schema,1,10) as tbschema,substr(table_name,1,30) as tbname, rows_read, rows_written, overflow_accesses, page_reorgs from table (SNAPSHOT_TABLE('', -1)) as snapshot_table order by rows_written desc fetch first 10 rows only";
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						sys_hash.put("tbschema", rs.getString("tbschema")
								.toString());
						sys_hash.put("tbname", rs.getString("tbname")
								.toString());
						sys_hash.put("rows_read", rs.getString("rows_read")
								.toString());
						sys_hash.put("rows_written", rs.getString(
								"rows_written").toString());
						sys_hash.put("overflow_accesses", rs.getString(
								"overflow_accesses").toString());
						sys_hash.put("page_reorgs", rs.getString("page_reorgs")
								.toString());
						writeList.add(sys_hash);
						sys_hash = new Hashtable();
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}

				rethash.put("poolValue", retList);
				rethash.put("readValue", readList);
				rethash.put("writeValue", writeList);
				returnhash.put(dbStr, rethash);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null)
					rs.close();
				util.closeStmt();
				util.closeConn();
			}
		}

		return returnhash;
	}

	public Hashtable getDB2Session(String ip, int port, String db,
			String username, String password) throws Exception {
		String[] dbs = db.split(",");
		DB2JdbcUtil util = null;
		ResultSet rs = null;
		Hashtable returnhash = new Hashtable();
		for (int i = 0; i < dbs.length; i++) {
			String dbStr = dbs[i];
			String dburl = "jdbc:db2://" + ip + ":" + port + "/" + dbStr;
			String sql = "SELECT SNAPSHOT_TIMESTAMP,APPL_STATUS,CLIENT_NNAME,CLIENT_PLATFORM,CLIENT_PROTOCOL,APPL_NAME FROM TABLE( SNAPSHOT_APPL_INFO( '', -1 )) as SNAPSHOT_APPL_INFO";
			Hashtable sys_hash = new Hashtable();
			List retList = new ArrayList();
			try {
				util = new DB2JdbcUtil(dburl, username, password);
				util.jdbc();
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					sys_hash.put("SNAPSHOT_TIMESTAMP", rs.getString(
							"SNAPSHOT_TIMESTAMP").toString());
					sys_hash.put("APPL_STATUS", rs.getString("APPL_STATUS")
							.toString());
					if (rs.getString("CLIENT_NNAME") != null) {
						sys_hash.put("CLIENT_NNAME", rs.getString(
								"CLIENT_NNAME").toString());
					} else {
						sys_hash.put("CLIENT_NNAME", "");
					}
					if (rs.getString("CLIENT_PLATFORM") != null) {
						sys_hash.put("CLIENT_PLATFORM", rs.getString(
								"CLIENT_PLATFORM").toString());
					} else {
						sys_hash.put("CLIENT_PLATFORM", "");
					}
					if (rs.getString("CLIENT_PROTOCOL") != null) {
						sys_hash.put("CLIENT_PROTOCOL", rs.getString(
								"CLIENT_PROTOCOL").toString());
					} else
						sys_hash.put("CLIENT_PROTOCOL", "");
					if (rs.getString("APPL_NAME") != null) {
						sys_hash.put("APPL_NAME", rs.getString("APPL_NAME")
								.toString());
					} else
						sys_hash.put("APPL_NAME", "");
					retList.add(sys_hash);
					sys_hash = new Hashtable();
				}
			} catch (Exception e) {
				// e.printStackTrace();
			} finally {
				if (rs != null)
					rs.close();
				util.closeStmt();
				util.closeConn();
			}
			returnhash.put(dbStr, retList);
		}

		return returnhash;
	}

	public List getDB2Lock(String ip, int port, String db, String username,
			String password) throws Exception {
		String dburl = "jdbc:db2://" + ip + ":" + port + "/" + db;
		String sql = "select db_name,rows_read,rows_selected, deadlocks ,lock_waits,lock_timeouts,lock_wait_time, lock_escals,x_lock_escals, lock_list_in_use,total_sorts,total_sort_time from table (snapshot_database ('', -1) ) as snapshot_database ";
		DB2JdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sys_hash = new Hashtable();
		List retList = new ArrayList();
		try {
			util = new DB2JdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				sys_hash.put("db_name", rs.getString("db_name").toString());
				sys_hash.put("rows_read", rs.getString("rows_read").toString());
				sys_hash.put("rows_selected", rs.getString("rows_selected")
						.toString());
				sys_hash.put("deadlocks", rs.getString("deadlocks").toString());
				sys_hash.put("lock_waits", rs.getString("lock_waits")
						.toString());
				sys_hash.put("lock_timeouts", rs.getString("lock_timeouts")
						.toString());
				sys_hash.put("lock_wait_time", rs.getString("lock_wait_time")
						.toString());
				sys_hash.put("lock_escals", rs.getString("lock_escals")
						.toString());
				sys_hash.put("x_lock_escals", rs.getString("x_lock_escals")
						.toString());
				sys_hash.put("lock_list_in_use", rs.getString(
						"lock_list_in_use").toString());
				sys_hash.put("total_sorts", rs.getString("total_sorts")
						.toString());
				sys_hash.put("total_sort_time", rs.getString("total_sort_time")
						.toString());

				retList.add(sys_hash);
				sys_hash = new Hashtable();
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return retList;
	}

	public List getDB2TopRead(String ip, int port, String db, String username,
			String password) throws Exception {
		String dburl = "jdbc:db2://" + ip + ":" + port + "/" + db;
		String sql = "select substr(table_schema,1,10) as tbschema, substr(table_name,1,30) as tbname,rows_read,rows_written,overflow_accesses,page_reorgs from table (SNAPSHOT_TABLE('', -1)) as snapshot_table order by rows_read desc fetch first 10 rows only";
		DB2JdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sys_hash = new Hashtable();
		List retList = new ArrayList();
		try {
			util = new DB2JdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				sys_hash.put("tbschema", rs.getString("tbschema").toString());
				sys_hash.put("tbname", rs.getString("tbname").toString());
				sys_hash.put("rows_read", rs.getString("rows_read").toString());
				sys_hash.put("rows_written", rs.getString("rows_written")
						.toString());
				sys_hash.put("overflow_accesses", rs.getString(
						"overflow_accesses").toString());
				sys_hash.put("page_reorgs", rs.getString("page_reorgs")
						.toString());
				retList.add(sys_hash);
				sys_hash = new Hashtable();
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return retList;
	}

	public List getDB2TopWrite(String ip, int port, String db, String username,
			String password) throws Exception {
		String dburl = "jdbc:db2://" + ip + ":" + port + "/" + db;
		String sql = "select substr(table_schema,1,10) as tbschema,substr(table_name,1,30) as tbname, rows_read, rows_written, overflow_accesses, page_reorgs from table (SNAPSHOT_TABLE('', -1)) as snapshot_table order by rows_written desc fetch first 10 rows only";
		DB2JdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sys_hash = new Hashtable();
		List retList = new ArrayList();
		try {
			util = new DB2JdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				sys_hash.put("tbschema", rs.getString("tbschema").toString());
				sys_hash.put("tbname", rs.getString("tbname").toString());
				sys_hash.put("rows_read", rs.getString("rows_read").toString());
				sys_hash.put("rows_written", rs.getString("rows_written")
						.toString());
				sys_hash.put("overflow_accesses", rs.getString(
						"overflow_accesses").toString());
				sys_hash.put("page_reorgs", rs.getString("page_reorgs")
						.toString());
				retList.add(sys_hash);
				sys_hash = new Hashtable();
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return retList;
	}

	public Hashtable getDB2CacheAndAppl(String ip, int port, String db,
			String username, String password) {
		String dburl = "jdbc:db2://" + ip + ":" + port + "/" + db;
		String sql = "select cat_cache_lookups ,cat_cache_inserts,cat_cache_overflows ,pkg_cache_lookups,pkg_cache_inserts ,PKG_CACHE_NUM_OVERFLOWS,appl_section_lookups ,appl_section_inserts from table (snapshot_database ('', -1) ) as snapshot_database";
		DB2JdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sys_hash = new Hashtable();
		try {
			util = new DB2JdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				sys_hash.put("cat_cache_lookups", rs.getString(
						"cat_cache_lookups").toString());
				sys_hash.put("cat_cache_inserts", rs.getString(
						"cat_cache_inserts").toString());
				sys_hash.put("cat_cache_overflows", rs.getString(
						"cat_cache_overflows").toString());
				sys_hash.put("pkg_cache_lookups", rs.getString(
						"pkg_cache_lookups").toString());
				sys_hash.put("pkg_cache_inserts", rs.getString(
						"pkg_cache_inserts").toString());
				sys_hash.put("PKG_CACHE_NUM_OVERFLOWS", rs.getString(
						"PKG_CACHE_NUM_OVERFLOWS").toString());
				sys_hash.put("appl_section_lookups", rs.getString(
						"appl_section_lookups").toString());
				sys_hash.put("cat_cache_lookups", rs.getString(
						"cat_cache_lookups").toString());
				sys_hash.put("appl_section_inserts", rs.getString(
						"appl_section_inserts").toString());
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			util.closeStmt();
			util.closeConn();
		}
		return sys_hash;
	}

	public Hashtable getDB2Data(String ip, int port, String db,
			String username, String password, Hashtable gatherHash)
			throws Exception {

		String[] dbs = db.split(",");
		DB2JdbcUtil util = null;
		ResultSet rs = null;
		Hashtable returnhash = new Hashtable();
		Hashtable syshash = new Hashtable();
		Hashtable spacehash = new Hashtable();
		Hashtable poolhash = new Hashtable();
		Hashtable sessionhash = new Hashtable();
		Hashtable lockhash = new Hashtable();
		Hashtable loghash = new Hashtable();
		Hashtable connhash = new Hashtable();
		Hashtable topreadhash = new Hashtable();
		Hashtable topwritehash = new Hashtable();
		Hashtable commonhash = new Hashtable();// 存放全局的变量信息,如服务器名称/操作系统
		Hashtable cacheAndApplhash = new Hashtable();// 缓冲区域命中

		for (int i = 0; i < dbs.length; i++) {
			String dbStr = dbs[i];
			String dburl = "jdbc:db2://" + ip + ":" + port + "/" + dbStr;
			String sql = "select os_name, host_name, total_memory,total_cpus,CONFIGURED_CPUS from table(sysproc.env_get_sys_info()) as systeminfo";

			Hashtable sys_hash = new Hashtable();
			try {
				util = new DB2JdbcUtil(dburl, username, password);
				util.jdbc();

				// 获取系统信息
				try {
					if (gatherHash.containsKey("sysvalue")) {
						try {
							rs = util.stmt.executeQuery(sql);
							while (rs.next()) {
								sys_hash.put("os_name", rs.getString("os_name")
										.toString());
								sys_hash.put("host_name", rs.getString(
										"HOST_NAME").toString());
								sys_hash.put("total_memory", rs.getString(
										"total_memory").toString());
								sys_hash.put("total_cpu", rs.getString(
										"total_cpus").toString());
								sys_hash.put("configured_cpu", rs.getString(
										"CONFIGURED_CPUS").toString());

								if (!commonhash.containsKey("os_name")) {
									commonhash.put("os_name", rs.getString(
											"os_name").toString());
								}
								if (!commonhash.containsKey("host_name")) {
									commonhash.put("host_name", rs.getString(
											"HOST_NAME").toString());
								}
								if (!commonhash.containsKey("total_memory")) {
									commonhash.put("total_memory", rs
											.getString("total_memory")
											.toString());
								}
								if (!commonhash.containsKey("total_cpu")) {
									commonhash.put("total_cpu", rs.getString(
											"total_cpus").toString());
								}
								if (!commonhash.containsKey("configured_cpu")) {
									commonhash.put("configured_cpu", rs
											.getString("CONFIGURED_CPUS")
											.toString());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (rs != null)
								rs.close();
						}
						sql = "select installed_prod, prod_release from table(sysproc.env_get_prod_info()) as productinfo";
						try {
							rs = util.stmt.executeQuery(sql);
							while (rs.next()) {
								sys_hash.put("installed_prod", rs.getString(
										"installed_prod").toString());
								sys_hash.put("prod_release", rs.getString(
										"prod_release").toString());
								if (!commonhash.containsKey("installed_prod")) {
									commonhash.put("installed_prod", rs
											.getString("installed_prod")
											.toString());
								}
								if (!commonhash.containsKey("prod_release")) {
									commonhash.put("prod_release", rs
											.getString("prod_release")
											.toString());
								}
							}
						} catch (Exception e) {
							// e.printStackTrace();
						} finally {
							if (rs != null)
								rs.close();
						}

					}
					syshash.put(dbStr, sys_hash);
				} catch (Exception e) {

				} finally {
					if (rs != null)
						rs.close();
				}
				// }

				cacheAndApplhash = getDB2CacheAndAppl(ip, port, db, username,
						password);

				if (gatherHash.containsKey("space")) {
					// 获取表空间信息
					List retList = new ArrayList();
					sys_hash = new Hashtable();
					try {
						sql = "select substr(tablespace_name,1,120) as tablespace_name,(usable_pages*bb.pagesize)/1024/1024 as totalspac,(free_pages*bb.pagesize)/1024/1024 as usablespac from table (snapshot_tbs_cfg (' ', -1)) as aa, syscat.tablespaces bb where aa.tablespace_id=bb.tbspaceid";
						rs = util.stmt.executeQuery(sql);
						Map<String, Integer> names = new HashMap<String, Integer>();
						while (rs.next()) {
							String name = rs.getString("tablespace_name")
									.toString().trim();
							if (names.get(name) == null) {
								sys_hash.put("tablespace_name", name);
								// sys_hash.put("container_name",
								// rs.getString("container_name").toString());
								// sys_hash.put("container_type",
								// rs.getString("container_type").toString());
								sys_hash.put("totalspac", rs.getString(
										"totalspac").toString());
								sys_hash.put("usablespac", rs.getString(
										"usablespac").toString());
								float totalsp = new Float(rs.getString(
										"totalspac").toString());
								float useablesp = new Float(rs.getString(
										"usablespac").toString());
								float usedperc = 0;
								if (!rs.getString("totalspac").toString()
										.equals("0")) {
									if ((totalsp - useablesp) > 0) {
										usedperc = 100 * useablesp / totalsp;
										sys_hash.put("usableper", new Float(
												usedperc).intValue()
												+ "");
									}
								} else {
									sys_hash.put("usableper", "");
								}
								retList.add(sys_hash);
								sys_hash = new Hashtable();
								names.put(name, 1);
							} else {
								for (int j = 0; j < retList.size(); j++) {
									Hashtable col = (Hashtable) retList.get(j);
									if (name.equals(col.get("tablespace_name"))) {
										String v_total = (String) col
												.get("totalspac");
										String v_userspace = (String) col
												.get("usablespac");
										String c_total = rs
												.getString("totalspac");
										String c_userspace = rs
												.getString("usablespac");
										float f_v_total = 0;
										if (v_total != null
												&& !"".equals(v_total))
											f_v_total = Float
													.parseFloat(v_total);
										float f_v_userspace = 0;
										if (v_userspace != null
												&& !"".equals(v_userspace))
											f_v_userspace = Float
													.parseFloat(v_userspace);
										float f_c_total = 0;
										if (c_total != null
												&& !"".equals(c_total))
											f_c_total = Float
													.parseFloat(c_total);
										float f_c_userspace = 0;
										if (c_userspace != null
												&& !"".equals(c_userspace))
											f_c_userspace = Float
													.parseFloat(c_userspace);
										float total = f_v_total + f_c_total;
										float userspace = f_c_userspace
												+ f_v_userspace;
										col.put("totalspac", String
												.valueOf(total));
										col.put("usablespac", String
												.valueOf(userspace));
										if (total > 0) {
											if ((total - userspace) > 0) {
												float usedperc = 100
														* userspace / total;
												col.put("usableper", new Float(
														usedperc).intValue()
														+ "");
											}
										} else {
											col.put("usableper", "");
										}
										break;
									}
								}
							}

						}
						spacehash.put(dbStr, retList);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
					}
				}

				// 获取POOL信息
				Hashtable rethash = new Hashtable();
				sys_hash = new Hashtable();
				List retpoolList = new ArrayList();
				List readList = new ArrayList();
				List writeList = new ArrayList();
				List lockList = new ArrayList();
				try {
					if (gatherHash.containsKey("pool")) {
						try {
							sql = "select substr(bp_name,1,20) as bp_name, int ((1- (decimal(pool_data_p_reads) / nullif(pool_data_l_reads,0)))*100) as data_hit_ratio, int ((1-(decimal(pool_index_p_reads)/nullif(pool_index_l_reads,0)))*100) as index_hit_ratio, int ((1-(decimal(pool_data_p_reads+pool_index_p_reads)/nullif((pool_data_l_reads+pool_index_l_reads),0)))*100) as BP_hit_ratio, int ((1-(decimal(pool_async_data_reads+pool_async_index_reads)/nullif((pool_async_data_reads+pool_async_index_reads+direct_reads),0)))*100) as Async_read_pct, int ((1-(decimal(direct_writes)/nullif(direct_reads,0)))*100) as Direct_RW_Ratio from table (snapshot_bp ('', -1)) as snapshot_bp";
							rs = util.stmt.executeQuery(sql);
							while (rs.next()) {
								sys_hash.put("bp_name", rs.getString("bp_name")
										.toString());
								if (rs.getString("data_hit_ratio") == null) {
									sys_hash.put("data_hit_ratio", "0");
								} else
									sys_hash.put("data_hit_ratio", rs
											.getString("data_hit_ratio")
											.toString());

								if (rs.getString("index_hit_ratio") == null) {
									sys_hash.put("index_hit_ratio", "0");
								} else
									sys_hash.put("index_hit_ratio", rs
											.getString("index_hit_ratio")
											.toString());

								if (rs.getString("BP_hit_ratio") == null) {
									sys_hash.put("BP_hit_ratio", "0");
								} else
									sys_hash.put("BP_hit_ratio", rs.getString(
											"BP_hit_ratio").toString());

								if (rs.getString("Async_read_pct") == null) {
									sys_hash.put("Async_read_pct", "0");
								} else
									sys_hash.put("Async_read_pct", rs
											.getString("Async_read_pct")
											.toString());

								if (rs.getString("Direct_RW_Ratio") == null) {
									sys_hash.put("Direct_RW_Ratio", "0");
								} else
									sys_hash.put("Direct_RW_Ratio", rs
											.getString("Direct_RW_Ratio")
											.toString());
								retpoolList.add(sys_hash);
								sys_hash = new Hashtable();
							}
						} catch (Exception e) {
							// e.printStackTrace();
						}
					}

					if (gatherHash.containsKey("topread")) {
						sql = "select substr(table_schema,1,10) as tbschema, substr(table_name,1,30) as tbname,rows_read,rows_written,overflow_accesses,page_reorgs from table (SNAPSHOT_TABLE('', -1)) as snapshot_table order by rows_read desc fetch first 10 rows only";
						try {
							rs = util.stmt.executeQuery(sql);
							while (rs.next()) {
								sys_hash.put("tbschema", rs.getString(
										"tbschema").toString());
								sys_hash.put("tbname", rs.getString("tbname")
										.toString());
								sys_hash.put("rows_read", rs.getString(
										"rows_read").toString());
								sys_hash.put("rows_written", rs.getString(
										"rows_written").toString());
								sys_hash.put("overflow_accesses", rs.getString(
										"overflow_accesses").toString());
								sys_hash.put("page_reorgs", rs.getString(
										"page_reorgs").toString());
								readList.add(sys_hash);
								sys_hash = new Hashtable();
							}
						} catch (Exception e) {
							// e.printStackTrace();
						}
					}

					if (gatherHash.containsKey("topwrite")) {
						sql = "select substr(table_schema,1,10) as tbschema,substr(table_name,1,30) as tbname, rows_read, rows_written, overflow_accesses, page_reorgs from table (SNAPSHOT_TABLE('', -1)) as snapshot_table order by rows_written desc fetch first 10 rows only";
						try {
							rs = util.stmt.executeQuery(sql);
							while (rs.next()) {
								sys_hash.put("tbschema", rs.getString(
										"tbschema").toString());
								sys_hash.put("tbname", rs.getString("tbname")
										.toString());
								sys_hash.put("rows_read", rs.getString(
										"rows_read").toString());
								sys_hash.put("rows_written", rs.getString(
										"rows_written").toString());
								sys_hash.put("overflow_accesses", rs.getString(
										"overflow_accesses").toString());
								sys_hash.put("page_reorgs", rs.getString(
										"page_reorgs").toString());
								writeList.add(sys_hash);
								sys_hash = new Hashtable();
							}
						} catch (Exception e) {
							// e.printStackTrace();
						}
					}

					// if(gatherHash.containsKey("lock")){
					// //获取锁信息
					// sys_hash = new Hashtable();
					// //List retList = new ArrayList();
					// try {
					// sql = "select db_name, rows_read, rows_selected,
					// lock_waits, lock_wait_time, deadlocks, lock_escals,
					// total_sorts, total_sort_time from table
					// (snapshot_database ('', -1) ) as snapshot_database";
					// rs = util.stmt.executeQuery(sql);
					// while (rs.next()) {
					// sys_hash.put("db_name",
					// rs.getString("db_name").toString());
					// sys_hash.put("rows_read",
					// rs.getString("rows_read").toString());
					// sys_hash.put("rows_selected",
					// rs.getString("rows_selected").toString());
					// sys_hash.put("lock_waits",
					// rs.getString("lock_waits").toString());
					// sys_hash.put("lock_wait_time",
					// rs.getString("lock_wait_time").toString());
					// sys_hash.put("deadlocks",
					// rs.getString("deadlocks").toString());
					// sys_hash.put("lock_escals",
					// rs.getString("lock_escals").toString());
					// sys_hash.put("total_sorts",
					// rs.getString("total_sorts").toString());
					// sys_hash.put("total_sort_time",
					// rs.getString("total_sort_time").toString());
					//
					// lockList.add(sys_hash);
					// sys_hash = new Hashtable();
					// }
					// } catch (Exception e) {
					// //e.printStackTrace();
					// } finally {
					// if (rs != null)
					// rs.close();
					// }
					// }
					if (gatherHash.containsKey("lock")) {
						// 获取锁信息
						sys_hash = new Hashtable();
						// List retList = new ArrayList();
						try {
							sql = "select db_name,rows_read,rows_selected, deadlocks ,lock_waits,lock_timeouts,lock_wait_time, lock_escals,x_lock_escals, lock_list_in_use,total_sorts,total_sort_time from table (snapshot_database ('', -1) ) as snapshot_database ";
							rs = util.stmt.executeQuery(sql);
							while (rs.next()) {
								sys_hash.put("db_name", rs.getString("db_name")
										.toString());
								sys_hash.put("rows_read", rs.getString(
										"rows_read").toString());
								sys_hash.put("rows_selected", rs.getString(
										"rows_selected").toString());
								sys_hash.put("deadlocks", rs.getString(
										"deadlocks").toString());
								sys_hash.put("lock_waits", rs.getString(
										"lock_waits").toString());
								sys_hash.put("lock_timeouts", rs.getString(
										"lock_timeouts").toString());
								sys_hash.put("lock_wait_time", rs.getString(
										"lock_wait_time").toString());
								sys_hash.put("lock_escals", rs.getString(
										"lock_escals").toString());
								sys_hash.put("x_lock_escals", rs.getString(
										"x_lock_escals").toString());
								sys_hash.put("lock_list_in_use", rs.getString(
										"lock_list_in_use").toString());
								sys_hash.put("total_sorts", rs.getString(
										"total_sorts").toString());
								sys_hash.put("total_sort_time", rs.getString(
										"total_sort_time").toString());

								lockList.add(sys_hash);
								sys_hash = new Hashtable();
							}
						} catch (Exception e) {
							// e.printStackTrace();
						} finally {
							if (rs != null)
								rs.close();
						}
					}

					// SysLogger.info("retpoolList size
					// ================"+retpoolList.size());
					rethash.put("poolValue", retpoolList);
					rethash.put("readValue", readList);
					rethash.put("writeValue", writeList);
					rethash.put("lockValue", lockList);
					poolhash.put(dbStr, rethash);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}

				if (gatherHash.containsKey("session")) {
					// 获取SESSION信息
					sys_hash = new Hashtable();
					List retList = new ArrayList();
					try {
						sql = "SELECT SNAPSHOT_TIMESTAMP,APPL_STATUS,CLIENT_NNAME,CLIENT_PLATFORM,CLIENT_PROTOCOL,APPL_NAME FROM TABLE( SNAPSHOT_APPL_INFO( '', -1 )) as SNAPSHOT_APPL_INFO";
						rs = util.stmt.executeQuery(sql);
						while (rs.next()) {
							sys_hash.put("SNAPSHOT_TIMESTAMP", rs.getString(
									"SNAPSHOT_TIMESTAMP").toString());
							sys_hash.put("APPL_STATUS", rs.getString(
									"APPL_STATUS").toString());
							if (rs.getString("CLIENT_NNAME") != null) {
								sys_hash.put("CLIENT_NNAME", rs.getString(
										"CLIENT_NNAME").toString());
							} else {
								sys_hash.put("CLIENT_NNAME", "");
							}
							if (rs.getString("CLIENT_PLATFORM") != null) {
								sys_hash.put("CLIENT_PLATFORM", rs.getString(
										"CLIENT_PLATFORM").toString());
							} else {
								sys_hash.put("CLIENT_PLATFORM", "");
							}
							if (rs.getString("CLIENT_PROTOCOL") != null) {
								sys_hash.put("CLIENT_PROTOCOL", rs.getString(
										"CLIENT_PROTOCOL").toString());
							} else
								sys_hash.put("CLIENT_PROTOCOL", "");
							if (rs.getString("APPL_NAME") != null) {
								sys_hash.put("APPL_NAME", rs.getString(
										"APPL_NAME").toString());
							} else
								sys_hash.put("APPL_NAME", "");
							retList.add(sys_hash);
							sys_hash = new Hashtable();
						}
					} catch (Exception e) {
						// e.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
					}
					sessionhash.put(dbStr, retList);
				}

				if (gatherHash.containsKey("conn")) {
					// 获取连接信息
					sys_hash = new Hashtable();
					List retList = new ArrayList();
					try {

						sql = "SELECT db_name,db_path,db_conn_time,db_status,db_location,sqlm_elm_last_backup,total_cons,connections_top,APPLS_CUR_CONS,FAILED_SQL_STMTS,COMMIT_SQL_STMTS FROM TABLE( SNAPSHOT_DATABASE( '', -1 )) as SNAPSHOT_DATABASE";
						try {
							rs = util.stmt.executeQuery(sql);
							while (rs.next()) {
								Hashtable temp_connhash = new Hashtable();
								temp_connhash.put("db_name", rs.getString(
										"db_name").toString());
								temp_connhash.put("db_path", rs.getString(
										"db_path").toString());
								temp_connhash.put("db_conn_time", rs.getString(
										"db_conn_time").toString());
								temp_connhash.put("db_status", rs.getString(
										"db_status").toString());
								temp_connhash.put("db_location", rs.getString(
										"db_location").toString());
								if (rs.getString("sqlm_elm_last_backup") != null) {
									temp_connhash
											.put(
													"sqlm_elm_last_backup",
													rs
															.getString(
																	"sqlm_elm_last_backup")
															.toString());
								} else {
									temp_connhash.put("sqlm_elm_last_backup",
											"");
								}
								// SysLogger.info("back time:
								// "+rs.getString("sqlm_elm_last_backup").toString());
								temp_connhash.put("total_cons", rs.getString(
										"total_cons").toString());
								temp_connhash.put("connections_top", rs
										.getString("connections_top")
										.toString());
								temp_connhash
										.put("appls_cur_cons", rs.getString(
												"appls_cur_cons").toString());
								temp_connhash.put("failedsql", rs.getString(
										"FAILED_SQL_STMTS").toString());
								temp_connhash.put("commitsql", rs.getString(
										"COMMIT_SQL_STMTS").toString());
								retList.add(temp_connhash);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (rs != null)
								rs.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
					}
					connhash.put(dbStr, retList);
				}

				if (gatherHash.containsKey("log")) {
					// 获取SESSION信息
					sys_hash = new Hashtable();
					List retList = new ArrayList();
					try {
						sql = "select db_name,int(total_log_used/1024/1024) as LogUsed,int(total_log_available/1024/1024) as LogSpaceFree,"
								+ "int((float(total_log_used)/float(total_log_used+total_log_available))*100) as PctUsed,int(tot_log_used_top/1024/1024) as MaxLogUsed,"
								+ "int(sec_log_used_top/1024/1024) as MaxSecUsed,int(sec_logs_allocated) as Secondaries from sysibmadm.snapdb";
						rs = util.stmt.executeQuery(sql);
						while (rs.next()) {
							sys_hash.put("logused", rs.getString("LogUsed")
									.toString());
							sys_hash.put("logspacefree", rs.getString(
									"LogSpaceFree").toString());
							if (rs.getString("PctUsed") != null) {
								sys_hash.put("pctused", rs.getString("PctUsed")
										.toString());
							} else {
								sys_hash.put("pctused", "");
							}
							if (rs.getString("MaxLogUsed") != null) {
								sys_hash.put("maxlogused", rs.getString(
										"MaxLogUsed").toString());
							} else {
								sys_hash.put("maxlogused", "");
							}
							if (rs.getString("MaxSecUsed") != null) {
								sys_hash.put("maxsecused", rs.getString(
										"MaxSecUsed").toString());
							} else
								sys_hash.put("maxsecused", "");
							retList.add(sys_hash);
							sys_hash = new Hashtable();
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (rs != null)
							rs.close();
					}
					loghash.put(dbStr, retList);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null)
					rs.close();
				util.closeStmt();
				util.closeConn();
			}
		}
		returnhash.put("sysInfo", syshash);
		returnhash.put("spaceInfo", spacehash);
		returnhash.put("poolInfo", poolhash);
		returnhash.put("session", sessionhash);
		returnhash.put("lock", lockhash);
		returnhash.put("log", loghash);
		returnhash.put("conn", connhash);
		returnhash.put("topread", topreadhash);
		returnhash.put("topwrite", topwritehash);
		returnhash.put("commonhash", commonhash);

		returnhash.put("cach", cacheAndApplhash);

		return returnhash;
	}

	/*
	 * Sysbase数据库的采集任务
	 * 
	 * @see com.dhcc.webnms.database.api.I_Dbmonitorlist#getDB2TopWrite(java.lang.String,
	 *      int, java.lang.String, java.lang.String, java.lang.String)
	 */
	public SybaseVO getSysbaseInfo(String ip, int port, String username,
			String password) throws Exception {
		// String dburl = "jdbc:sybase:Tds:"+ip+":"+port+"/master";
		String dburl = "";
		if (ip.equals("10.10.152.30")) {
			dburl = "jdbc:sybase:Tds:" + ip + ":" + port
					+ "/master?charset=iso_1&jconnect_version=0";
		} else
			dburl = "jdbc:sybase:Tds:" + ip + ":" + port + "/master";
		dburl = "jdbc:sybase:Tds:" + ip + ":" + port + "/master";
		// String sql = "select substr(table_schema,1,10) as
		// tbschema,substr(table_name,1,30) as tbname, rows_read, rows_written,
		// overflow_accesses, page_reorgs from table (SNAPSHOT_TABLE('', -1)) as
		// snapshot_table order by rows_written desc fetch first 10 rows only";
		SysbaseJdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sys_hash = new Hashtable();
		List retList = new ArrayList();
		SybaseVO vo = new SybaseVO();
		ResultSet rs_Procedure_hitrate = null;
		ResultSet rs_Data_hitrate = null;
		ResultSet rs_Total_logicalMemory = null;
		ResultSet rs_Procedure_cache = null;
		ResultSet rs_Metadata_cache = null;
		ResultSet rs_Total_physicalMemory = null;
		ResultSet rs_Total_dataCache = null;
		ResultSet rs_Xact_count = null;
		ResultSet rs_Locks_count = null;
		ResultSet rs_Disk_count = null;
		ResultSet rs_Io_busy_rate = null;
		ResultSet rs_ServerName = null;
		ResultSet rs_Cpu_busy_rate = null;
		ResultSet rs_cpu_busy = null;
		ResultSet rs_idle = null;
		ResultSet rs_version = null;
		ResultSet rs_io_busy = null;
		ResultSet rs_Sent_rate = null;
		ResultSet rs_Received_rate = null;
		ResultSet rs_Write_rate = null;
		ResultSet rs_Read_rate = null;
		ResultSet rs_devices = null;
		ResultSet rs_users = null;
		ResultSet rs_dbs = null;
		ResultSet rs_servers = null;

		try {
			util = new SysbaseJdbcUtil(dburl, username, password);
			util.jdbc();

			List list = new ArrayList();

			// cpu工作时间 秒
			StringBuffer cpu_busy = new StringBuffer("SELECT @@CPU_BUSY");
			rs = util.executeQuery(cpu_busy.toString());
			try {
				while (rs.next()) {
					vo.setCpu_busy(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getCpu_busy());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 空闲时间 秒
			StringBuffer idle = new StringBuffer("SELECT @@IDLE");
			rs = util.executeQuery(idle.toString());
			try {
				while (rs.next()) {
					vo.setIdle(rs.getString(""));
					break;
					// list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 版本
			StringBuffer version = new StringBuffer("SELECT @@version");
			rs = util.executeQuery(version.toString());
			try {
				while (rs.next()) {
					vo.setVersion(rs.getString(""));
					break;
					// list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 输入输出执行时间 秒
			StringBuffer io_busy = new StringBuffer("SELECT @@io_busy");
			rs = util.executeQuery(io_busy.toString());
			try {
				while (rs.next()) {
					vo.setIo_busy(rs.getString(""));
					break;
					// list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 写到网络上的输出数据速率 包数/秒
			// StringBuffer Sent_rate = new StringBuffer("select
			// convert(decimal(10,2),round(@@pack_sent/(nullif(@@io_busy,0)*1.00,2))");
			StringBuffer Sent_rate = new StringBuffer(
					"select @@pack_sent as packs,@@io_busy as seconds");
			rs = util.executeQuery(Sent_rate.toString());
			try {
				while (rs.next()) {
					String packs = rs.getString("packs");
					String seconds = rs.getString("seconds");
					if (seconds.equals("0")) {
						vo.setSent_rate(packs);
					} else {
						int sent_rate = Integer.parseInt(packs)
								/ Integer.parseInt(seconds);
						vo.setSent_rate(sent_rate + "");
					}
					break;
					// list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 从网络上读取的输入数据速率 包数/秒
			StringBuffer Received_rate = new StringBuffer(
					"select @@pack_received as packs,@@io_busy as seconds");
			rs = util.executeQuery(Received_rate.toString());
			try {
				while (rs.next()) {
					String packs = rs.getString("packs");
					String secends = rs.getString("seconds");
					if (secends.equals("0")) {
						vo.setReceived_rate(packs);
					} else {
						int sent_rate = Integer.parseInt(packs)
								/ Integer.parseInt(secends);
						vo.setReceived_rate(sent_rate + "");
					}
					break;

					// vo.setReceived_rate(rs_Received_rate.getString(""));
					// list.add(vo);
					// //System.out.println(vo.getReceived_rate());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 写入磁盘速率 次/秒
			StringBuffer Write_rate = new StringBuffer(
					"select @@TOTAL_WRITE as packs,@@io_busy as seconds");
			rs = util.executeQuery(Write_rate.toString());
			try {
				while (rs.next()) {
					String packs = rs.getString("packs");
					String secends = rs.getString("seconds");
					if (secends.equals("0")) {
						vo.setWrite_rate(packs);
					} else {
						int sent_rate = Integer.parseInt(packs)
								/ Integer.parseInt(secends);
						vo.setWrite_rate(sent_rate + "");
					}
					break;

					// vo.setWrite_rate(rs_Write_rate.getString(""));
					// list.add(vo);
					// //System.out.println(vo.getWrite_rate());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 读取磁盘速率 Read_rate
			StringBuffer Read_rate = new StringBuffer(
					"select @@TOTAL_WRITE as packs,@@io_busy as seconds");
			rs = util.executeQuery(Read_rate.toString());
			try {
				while (rs.next()) {
					String packs = rs.getString("packs");
					String secends = rs.getString("seconds");
					if (secends.equals("0")) {
						vo.setRead_rate(packs);
					} else {
						int sent_rate = Integer.parseInt(packs)
								/ Integer.parseInt(secends);
						vo.setRead_rate(sent_rate + "");
					}
					break;

					// vo.setRead_rate(rs_Read_rate.getString(""));
					// list.add(vo);
					// //System.out.println(vo.getRead_rate());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// SQL服务器名称
			StringBuffer ServerName = new StringBuffer("SELECT  @@SERVERNAME");
			rs = util.executeQuery(ServerName.toString());
			try {
				while (rs.next()) {
					vo.setServerName(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getServerName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// cpu利用率(已经乘以了100) %
			StringBuffer Cpu_busy_rate = new StringBuffer(
					"select convert(decimal(10,2),round(@@cpu_busy*100/((@@cpu_busy+@@io_busy+@@idle)*1.00),2))");
			rs = util.executeQuery(Cpu_busy_rate.toString());
			try {
				while (rs.next()) {
					vo.setCpu_busy_rate(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getCpu_busy_rate());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// IO利用率(已经乘以了100) %
			StringBuffer Io_busy_rate = new StringBuffer(
					"select convert(decimal(10,2),round(@@io_busy*100/((@@cpu_busy+@@io_busy+@@idle)*1.00),2))");
			rs = util.executeQuery(Io_busy_rate.toString());
			try {
				while (rs.next()) {
					vo.setIo_busy_rate(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getIo_busy_rate());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 转储设备或数据库设备个数
			StringBuffer Disk_count = new StringBuffer(
					"select count(*) from sysdevices");
			rs = util.executeQuery(Disk_count.toString());
			try {
				while (rs.next()) {
					vo.setDisk_count(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getDisk_count());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 活动锁个数
			StringBuffer Locks_count = new StringBuffer(
					"select count(*) from syslocks");
			rs = util.executeQuery(Locks_count.toString());
			try {
				while (rs.next()) {
					vo.setLocks_count(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getLocks_count());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 事务的个数
			StringBuffer Xact_count = new StringBuffer(
					"select count(*) from systransactions");
			rs = util.executeQuery(Xact_count.toString());
			try {
				while (rs.next()) {
					vo.setXact_count(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getXact_count());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 总数据高速缓存大小 MB
			StringBuffer Total_dataCache = new StringBuffer(
					"select convert(decimal(10,0),c.value/512.00) from sysconfigures t ,syscurconfigs c where t.comment like '%total data cache size%' and c.config=t.config");
			rs = util.executeQuery(Total_dataCache.toString());
			try {
				while (rs.next()) {
					vo.setTotal_dataCache(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getTotal_dataCache());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 总物理内存大小 MB
			StringBuffer Total_physicalMemory = new StringBuffer(
					"select convert(decimal(10,0),c.value/512.00) from sysconfigures t ,syscurconfigs c where t.comment like '%total physical memory%' and c.config=t.config");
			rs = util.executeQuery(Total_physicalMemory.toString());
			try {
				while (rs.next()) {
					vo.setTotal_physicalMemory(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getTotal_physicalMemory());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Metadata缓存 MB
			StringBuffer Metadata_cache = new StringBuffer(
					"select convert(decimal(10,0),c.value/512.00) from sysconfigures t ,syscurconfigs c where t.comment like '%Meta-Data Caches%' and c.config=t.config");
			rs = util.executeQuery(Metadata_cache.toString());
			try {
				while (rs.next()) {
					vo.setMetadata_cache(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getMetadata_cache());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 存储过程缓存大小 MB
			StringBuffer Procedure_cache = new StringBuffer(
					"select convert(decimal(10,0),c.value/512.00) from sysconfigures t ,syscurconfigs c where t.comment like '%procedure cache size%' and c.config=t.config");
			rs = util.executeQuery(Procedure_cache.toString());
			try {
				while (rs.next()) {
					vo.setProcedure_cache(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getProcedure_cache());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 总逻辑内存大小 MB
			StringBuffer Total_logicalMemory = new StringBuffer(
					"select convert(decimal(10,0),c.value/512.00) from sysconfigures t ,syscurconfigs c where t.comment like '%total logical memory%' and c.config=t.config");
			rs = util.executeQuery(Total_logicalMemory.toString());
			try {
				while (rs.next()) {
					vo.setTotal_logicalMemory(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getTotal_logicalMemory());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 数据缓存匹配度 %
			StringBuffer Data_hitrate = new StringBuffer(
					"select convert(decimal(10,0),c.value*100/c.memory_used) from sysconfigures t ,syscurconfigs c where t.comment ='total data cache size' and c.config=t.config");

			try {
				rs = util.executeQuery(Data_hitrate.toString());
				while (rs.next()) {
					vo.setData_hitrate(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getData_hitrate());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 存储缓存匹配度 %
			StringBuffer Procedure_hitrate = new StringBuffer(
					"select convert(decimal(10,0),c.value*200/c.memory_used) from sysconfigures t ,syscurconfigs c where t.comment ='procedure cache size' and c.config=t.config");

			try {

				rs = util.executeQuery(Procedure_hitrate.toString());
				while (rs.next()) {
					vo.setProcedure_hitrate(rs.getString(""));
					break;
					// list.add(vo);
					// //System.out.println(vo.getProcedure_hitrate());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// *************************转储设备或数据库设备信息
			StringBuffer devices = new StringBuffer("sp_helpdevice");
			List deviceInfo = new ArrayList();

			try {

				rs = util.executeQuery(devices.toString());
				while (rs.next()) {
					TablesVO tvo = new TablesVO();
					tvo.setDevice_name(rs.getString("device_name"));
					tvo.setDevice_physical_name(rs.getString("physical_name"));
					tvo.setDevice_description(rs.getString("description"));
					deviceInfo.add(tvo);
					// //System.out.println(vo.getDevice_name()+"@"+vo.getDevice_physical_name()+"@"+vo.getDevice_description());
				}
				vo.setDeviceInfo(deviceInfo);

			} catch (Exception e) {
				e.printStackTrace();
			}
			// ************** 当前数据库所有用户的信息

			StringBuffer users = new StringBuffer("sp_helpuser");
			List userInfo = new ArrayList();

			try {

				rs = util.executeQuery(users.toString());
				while (rs.next()) {
					TablesVO tvo = new TablesVO();
					tvo.setUsers_name(rs.getString("Users_name"));
					tvo.setID_in_db(rs.getString("ID_in_db"));
					tvo.setGroup_name(rs.getString("Group_name"));
					tvo.setLogin_name(rs.getString("Login_name"));
					userInfo.add(tvo);
					// //System.out.println(vo.getUsers_name()+vo.getID_in_db()+vo.getGroup_name()+vo.getLogin_name());
				}
				vo.setUserInfo(userInfo);

			} catch (Exception e) {
				e.printStackTrace();
			}
			// ************** 当前数据库大小信息
			List dbInfo = new ArrayList();
			// rs = util.executeQuery("select
			// c.name,sum(a.size)*(@@maxpagesize/1024)/1024 as
			// size,sum(a.unreservedpgs)*(@@maxpagesize/1024)/1024 as freesize,
			// (sum(a.size)-sum(a.unreservedpgs))*100/sum(a.size) as perc from
			// sysusages a,sysdevices b ,sysdatabases c where a.dbid=c.dbid and
			// (a.vstart between b.low and b.high) group by c.name");
			// rs = util.executeQuery("select
			// c.name,sum(a.size)*(@@maxpagesize/1024)/1024 as
			// size,sum(a.unreservedpgs)*(@@maxpagesize/1024)/1024 as freesize,
			// (sum(a.size)-sum(a.unreservedpgs))*100/sum(a.size) as perc from
			// sysusages a,sysdatabases c where a.dbid=c.dbid group by c.name");

			try {
				Map<String, Integer> names = new HashMap<String, Integer>();
				rs = util
						.executeQuery("select c.name,sum(a.size)*(@@maxpagesize/1024)/1024 as size,sum(a.unreservedpgs)*(@@maxpagesize/1024)/1024 as freesize from sysusages a,sysdatabases c where a.dbid=c.dbid group by c.name");
				while (rs.next()) {
					String name = rs.getString("name");
					if (names.get(name) == null) {
						TablesVO tvo = new TablesVO();
						String _allsize = rs.getString("size");
						String _freesize = rs.getString("freesize");
						tvo.setDb_name(rs.getString("name"));
						tvo.setDb_size(_allsize);
						tvo.setDb_freesize(_freesize);
						int perc = (Integer.parseInt(_allsize) - Integer
								.parseInt(_freesize))
								* 100 / Integer.parseInt(_allsize);
						tvo.setDb_usedperc(perc + "");
						dbInfo.add(tvo);
						names.put(name, 1);
					} else {
						for (int i = 0; i < dbInfo.size(); i++) {
							TablesVO tvo = (TablesVO) dbInfo.get(i);
							if (name.equals(tvo.getDb_name())) {
								String vofree = tvo.getDb_freesize();
								String dbsize = tvo.getDb_size();
								String userpred = tvo.getDb_usedperc();
								float vo_size = 0;
								if (dbsize != null && !"".equals(dbsize)) {
									vo_size = Float.parseFloat(dbsize);
								}
								float vo_used = 0;
								if (userpred != null && !"".equals(userpred))
									vo_used = Float.parseFloat(userpred);
								float vo_free = 0;
								if (vofree != null && !"".equals(vofree))
									vo_free = Float.parseFloat(vofree);
								String _allsize = rs.getString("size");
								String _freesize = rs.getString("freesize");
								float f_allsize = 0;
								if (_allsize != null && !"".equals(_allsize))
									f_allsize = Float.parseFloat(_allsize);
								float f_free = 0;
								if (_freesize != null && !"".equals(_freesize))
									f_free = Float.parseFloat(_freesize);
								float total = vo_size + f_allsize;
								float _free = f_free + vo_free;
								tvo.setDb_freesize(String.valueOf(_free));
								tvo.setDb_size(String.valueOf(total));
								tvo
										.setDb_usedperc(String
												.valueOf((total - _free)
														/ total * 100));
								break;
							}
						}
					}

				}
				vo.setDbInfo(dbInfo);

			} catch (Exception e) {
				e.printStackTrace();
			}
			// ＊＊＊＊＊＊＊＊远程服务器信息
			StringBuffer servers = new StringBuffer("sp_helpserver");
			List serversInfo = new ArrayList();

			try {
				rs = util.query(servers.toString());
				while (rs.next()) {
					TablesVO tvo = new TablesVO();
					tvo.setServer_name(rs.getString("name"));
					tvo.setServer_network_name(rs.getString("network_name"));
					tvo.setServer_class(rs.getString("class"));
					tvo.setServer_status(rs.getString("status"));
					serversInfo.add(tvo);
				}
				vo.setServersInfo(serversInfo);

			} catch (Exception e) {
				// util.jdbc();
				e.printStackTrace();
			}

			// return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs_dbs != null)
				rs_dbs.close();
			if (rs_users != null)
				rs_users.close();
			if (rs_devices != null)
				rs_devices.close();
			if (rs_Procedure_hitrate != null)
				rs_Procedure_hitrate.close();
			if (rs_Data_hitrate != null)
				rs_Data_hitrate.close();
			if (rs_Total_logicalMemory != null)
				rs_Total_logicalMemory.close();
			if (rs_Procedure_cache != null)
				rs_Procedure_cache.close();
			if (rs_Metadata_cache != null)
				rs_Metadata_cache.close();
			if (rs_Total_physicalMemory != null)
				rs_Total_physicalMemory.close();
			if (rs_Total_dataCache != null)
				rs_Total_dataCache.close();
			if (rs_Xact_count != null)
				rs_Xact_count.close();
			if (rs_Locks_count != null)
				rs_Locks_count.close();
			if (rs_Disk_count != null)
				rs_Disk_count.close();
			if (rs_Io_busy_rate != null)
				rs_Io_busy_rate.close();
			if (rs_Cpu_busy_rate != null)
				rs_Cpu_busy_rate.close();
			if (rs_cpu_busy != null)
				rs_cpu_busy.close();
			if (rs_idle != null)
				rs_idle.close();
			if (rs_version != null)
				rs_version.close();
			if (rs_io_busy != null)
				rs_io_busy.close();
			if (rs_Sent_rate != null)
				rs_Sent_rate.close();
			if (rs_Received_rate != null)
				rs_Received_rate.close();
			if (rs_Write_rate != null)
				rs_Write_rate.close();
			if (rs_Read_rate != null)
				rs_Read_rate.close();
			if (rs_ServerName != null)
				rs_ServerName.close();

			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return vo;
	}

	public Hashtable getOracleMem(String ip, int port, String db,
			String username, String password) throws Exception {
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select a.COMPONENT,round(a.CURRENT_SIZE/1024/1024,0) as val from v$sga_dynamic_components a";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sys_hash = new Hashtable();
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				sys_hash.put(rs.getString("COMPONENT").toString().replaceAll(
						" ", "_"), rs.getString("VAL").toString());
			}
			rs.close();
			sql = "select a.POOL,round(a.BYTES/1024/1024,0) as val from v$sgastat a";
			rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString("POOL") != null) {
					if (rs.getString("POOL").toString().equalsIgnoreCase(
							"java pool")) {
						sys_hash.put("java_pool", rs.getString("VAL")
								.toString());
					}
				}
			}
			rs.close();
			// PGA信息
			sql = "select * from v$pgastat";
			rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				String tmp_name = rs.getString(1).toString();
				String tmp_value = rs.getString(2).toString();
				// //System.out.println(tmp_name+"--------"+tmp_value);
				if (tmp_name.equalsIgnoreCase("cache hit percentage")) {
					tmp_value = tmp_value + "%";
				} else if (tmp_name
						.equalsIgnoreCase("aggregate PGA target parameter")) {
					float tmp_v = new Float(tmp_value).floatValue();
					tmp_value = tmp_v / 1024 / 1024 + "MB";
				} else {
					float tmp_v = 0;
					try {
						tmp_v = new Float(tmp_value);
						if (tmp_v / 1024 >= 1000) {
							tmp_value = new Float(tmp_v / 1024 / 1024)
									.floatValue()
									+ "MB";
						} else {
							tmp_value = new Float(tmp_v / 1024).floatValue()
									+ "KB";
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				sys_hash.put(tmp_name.replaceAll(" ", "_"), tmp_value);
			}
			/*
			 * sql = "select a.NAME,a.VALUE from v$pgastat a where a.NAME
			 * ='cache hit percentage'"; rs = util.stmt.executeQuery(sql); while
			 * (rs.next()) { if (rs.getString("NAME")!=null &&
			 * rs.getString("NAME").equalsIgnoreCase("cache hit percentage")){
			 * //if(rs.getString("POOL").toString().equalsIgnoreCase("java
			 * pool")){ sys_hash.put(rs.getString("NAME"),
			 * rs.getString("VALUE").toString()); //} } }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();

		}
		return sys_hash;
	}

	public Hashtable getOracleMemPerf(String ip, int port, String db,
			String username, String password) throws Exception {
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;

		String sql = "";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		Hashtable memoryPerf = new Hashtable();
		try {
			// 缓冲区命中率
			sql = "select round((1 - (sum(decode(name, 'physical reads', value, 0)) /(sum(decode(name, 'db block gets', value, 0)) +sum(decode(name, 'consistent gets', value, 0))))) * 100) as HitRatio from v$sysstat";
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			try {
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					memoryPerf.put("buffercache", rs.getString("HitRatio")
							.toString());
					// SysLogger.info("buffercache :
					// "+rs.getString("HitRatio").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}

			// 数据字典命中率
			sql = "select round((1 - (sum(getmisses) / sum(gets))) * 100) as HitRatio from v$rowcache";
			try {
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					memoryPerf.put("dictionarycache", rs.getString("HitRatio")
							.toString());
					// SysLogger.info("dictionarycache :
					// "+rs.getString("HitRatio").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}

			// 库缓存命中率
			sql = "select round(sum(pins) / (sum(pins) + sum(reloads)) * 100) as HitRatio from v$librarycache";
			try {
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					memoryPerf.put("librarycache", rs.getString("HitRatio")
							.toString());
					// SysLogger.info("librarycache :
					// "+rs.getString("HitRatio").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}

			// 内存中的排序
			sql = "select a.value as DiskSorts,b.value as MemorySorts,round((100 * b.value) /decode((a.value + b.value), 0, 1, (a.value + b.value)),2) as PctMemorySorts from v$sysstat a, v$sysstat b where a.name = 'sorts (disk)' and b.name = 'sorts (memory)'";
			try {
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					memoryPerf.put("pctmemorysorts", rs.getString(
							"PctMemorySorts").toString());
					// SysLogger.info("pctmemorysorts :
					// "+rs.getString("PctMemorySorts").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}

			// 最浪费内存的前10个语句占全部内存读取量的比例
			sql = "select sum(pct_bufgets) as pctbufgets from (select rank() over(order by buffer_gets desc) as rank_bufgets,to_char(100 * ratio_to_report(buffer_gets) over(), '999.99') as pct_bufgets from v$sqlarea) where rank_bufgets < 11";
			try {
				rs = util.stmt.executeQuery(sql);
				while (rs.next()) {
					memoryPerf.put("pctbufgets", rs.getString("pctbufgets")
							.toString());
					// SysLogger.info("pctbufgets :
					// "+rs.getString("pctbufgets").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			util.closeStmt();
			util.closeConn();

		}
		return memoryPerf;
	}

	public boolean getOracleIsOK(String ip, int port, String db,
			String username, String password) {
		Vector returnVal = new Vector();
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
//		System.out.println("------oracle--ping---------------"+dburl);
		java.util.Properties info = new java.util.Properties();
		info.put("user", username);
		info.put("password", password);
		info.put("internal_logon", "sysdba");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		Connection conn=null;
		try {
			if(username.equalsIgnoreCase("sys") || username.equalsIgnoreCase("system"))
			{
				conn = DriverManager.getConnection(dburl, info);
			}else{
				conn = DriverManager.getConnection(dburl, username, password);
			}
			if(conn !=null){
				System.out.println("connect is true ");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally{
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		if(conn !=null){
			System.out.println("connect is true ");
			return true;
		}
		return false;
	}

	public boolean getDB2IsOK(String ip, int port, String db, String username,
			String password) {
		Vector returnVal = new Vector();
		// jdbc:oracle:thin:@MyDbComputerNameOrIP:1521:ORCL
		String dburl = "jdbc:db2://" + ip + ":" + port + "/" + db;
		String sql = "select * from SYSIBM.SYSTABLESPACES";
		DB2JdbcUtil util = null;
		ResultSet rs = null;
		boolean flag = false;
		// //System.out.println("begin check db2 "+ip+"----dbname---"+db);
		try {
			util = new DB2JdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			if (rs.next()) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				util.closeStmt();
				util.closeConn();
			} catch (Exception ep) {
			}
		}
		// //System.out.println("end check db2 "+ip+"----dbname---"+db);
		return flag;
	}

	public Vector getOracleTop10Sql(String ip, int port, String db,
			String username, String password) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select sql_text,pct_bufgets from (select rank() over(order by disk_reads desc) as rank_bufgets,"
				+ "to_char(100 * ratio_to_report(disk_reads) over(), '999.99') pct_bufgets,sql_text from v$sqlarea) where rank_bufgets < 11";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	public Vector getOracleSession(String ip, int port, String db,
			String username, String password) throws Exception {
		Vector returnVal = new Vector();
		// jdbc:oracle:thin:@MyDbComputerNameOrIP:1521:ORCL
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select substr(s.username,1,18) username,s.status status,s.MACHINE machine,"
				+ "s.type sessiontype, to_char(s.LOGON_TIME,'yyyy-mm-dd hh24-mi-ss') logontime,"
				+ "substr(s.program,1,15) program,decode(s.command, 0,'No Command',1,"
				+ "'Create Table',2,'Insert',3,'Select',6,'Update',7,'Delete',9,"
				+ "'Create Index',15,'Alter Table',21,'Create View',23,'Validate Index',35,"
				+ "'Alter Database',39,'Create Tablespace',41,'Drop Tablespace',40,'Alter Tablespace'"
				+ ",53,'Drop User',62,'Analyze Table',63,'Analyze Index',s.command||': Other') "
				+ "command from v$session s,v$process p,v$transaction t,v$rollstat r,v$rollname n "
				+ "where s.paddr = p.addr and   s.taddr = t.addr (+) and   t.xidusn = r.usn (+) and"
				+ "  r.usn = n.usn (+) order by logon_time desc";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception ex) {

				}
			}
			try {
				util.closeStmt();
			} catch (Exception ex) {

			}
			try {
				util.closeConn();
			} catch (Exception ex) {

			}

		}
		return returnVal;
	}

	public Vector getOracleRollbackinfo(String ip, int port, String db,
			String username, String password) throws Exception {
		Vector returnVal = new Vector();
		// jdbc:oracle:thin:@MyDbComputerNameOrIP:1521:ORCL
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select distinct name \"Rollback Segment\", "
				+ "optsize \"Optimal\",shrinks, AveShrink \"Average Shrink\",Wraps, "
				+ "Extends from v$rollstat,v$rollname where "
				+ "v$rollstat.USN=v$rollname.USN order by name";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(
								col.toLowerCase().replaceAll(" ", "_"), tmp);
					} else
						return_value.put(
								col.toLowerCase().replaceAll(" ", "_"), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();

		}
		return returnVal;
	}

	public Vector getOracleLockinfo(String ip, int port, String db,
			String username, String password) throws Exception {
		Vector returnVal = new Vector();
		// jdbc:oracle:thin:@MyDbComputerNameOrIP:1521:ORCL
		String dburl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + db;
		String sql = "select distinct substr(s.username,1,18) username,s.status status,s.MACHINE machine,"
				+ "s.type sessiontype,to_char(s.LOGON_TIME,'yyyy-mm-dd hh24-mi-ss') logontime,"
				+ "substr(s.program,1,15) program , a.type as locktype,a.lmode as lmode,"
				+ "a.request as request  from v$session s,v$lock a where a.sid=s.sid";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();

		}
		return returnVal;
	}

	public Vector getSqlserverTablerows(String ip, String username,
			String password, String dbname) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:jtds:sqlserver://" + ip + ";DatabaseName="
				+ dbname + ";charset=GBK;SelectMethod=CURSOR";
		String sql = "select a.name,b.rows from sysobjects a,sysindexes b where a.type='u' and b.id=object_id(a.name) and indid in(0,1) order by a.name;";
		JdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new JdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();

		}
		return returnVal;
	}

	public Hashtable collectSQLServerMonitItemsDetail(String ip, String dbname,
			String username, String password, Hashtable gatherHash) {
		boolean returnbool = false;
		String tmpResult[][] = null;
		double hitratio = 0;
		double hitratiobase = 0;
		double tmpDouble = 0.0D;

		double dbOfflineErrors = 0;
		double killConnectionErrors = 0;
		double userErrors = 0;
		double infoErrors = 0;
		double sqlServerErrors_total = 0;

		double cachedCursorCounts = 0; // cachedCursorCounts_total
		double cursorCacheUseCounts = 0;// cursorCacheUseCounts_total
		double cursorRequests_total = 0;
		double activeCursors = 0;// activeCursors_total
		double cursorMemoryUsage = 0;// cursorMemoryUsage_total
		double cursorWorktableUsage = 0;// cursorWorktableUsage_total
		double activeOfCursorPlans = 0;// Number of active cursor plans_total

		double pingjun_lockWaits = 0;
		double pingjun_memoryGrantQueueWaits = 0;
		double pingjun_threadSafeMemoryObjectWaits = 0;
		double pingjun_logWriteWaits = 0;
		double pingjun_logBufferWaits = 0;
		double pingjun_networkIOWaits = 0;
		double pingjun_pageIOLatchWaits = 0;
		double pingjun_pageLatchWaits = 0;
		double pingjun_nonPageLatchWaits = 0;
		double pingjun_waitForTheWorker = 0;
		double pingjun_workspaceSynchronizationWaits = 0;
		double pingjun_transactionOwnershipWaits = 0;

		double jingxing_lockWaits = 0;
		double jingxing_memoryGrantQueueWaits = 0;
		double jingxing_threadSafeMemoryObjectWaits = 0;
		double jingxing_logWriteWaits = 0;
		double jingxing_logBufferWaits = 0;
		double jingxing_networkIOWaits = 0;
		double jingxing_pageIOLatchWaits = 0;
		double jingxing_pageLatchWaits = 0;
		double jingxing_nonPageLatchWaits = 0;
		double jingxing_waitForTheWorker = 0;
		double jingxing_workspaceSynchronizationWaits = 0;
		double jingxing_transactionOwnershipWaits = 0;

		double qidong_lockWaits = 0;
		double qidong_memoryGrantQueueWaits = 0;
		double qidong_threadSafeMemoryObjectWaits = 0;
		double qidong_logWriteWaits = 0;
		double qidong_logBufferWaits = 0;
		double qidong_networkIOWaits = 0;
		double qidong_pageIOLatchWaits = 0;
		double qidong_pageLatchWaits = 0;
		double qidong_nonPageLatchWaits = 0;
		double qidong_waitForTheWorker = 0;
		double qidong_workspaceSynchronizationWaits = 0;
		double qidong_transactionOwnershipWaits = 0;

		double leiji_lockWaits = 0;
		double leiji_memoryGrantQueueWaits = 0;
		double leiji_threadSafeMemoryObjectWaits = 0;
		double leiji_logWriteWaits = 0;
		double leiji_logBufferWaits = 0;
		double leiji_networkIOWaits = 0;
		double leiji_pageIOLatchWaits = 0;
		double leiji_pageLatchWaits = 0;
		double leiji_nonPageLatchWaits = 0;
		double leiji_waitForTheWorker = 0;
		double leiji_workspaceSynchronizationWaits = 0;
		double leiji_transactionOwnershipWaits = 0;

		long tmpLong = 0L;
		double tmpdou = 0;
		String dburl = "jdbc:jtds:sqlserver://" + ip
				+ ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
		String sql = "select a.name,b.rows from sysobjects a,sysindexes b where a.type='u' and b.id=object_id(a.name) and indid in(0,1) order by a.name;";
		JdbcUtil util = null;
		ResultSet rs = null;

		// long tmpAppPollInterval = (System.currentTimeMillis()-
		// getLastUpdatetime())/1000;
		long tmpAppPollInterval = getTaskInterval();
		PersistenceServiceable service = PersistenceService.getInstance();

		/*
		 * 取得系统表master..sysperfinfo的信息 第一列为object_name: 性能对象名，如 SQL Server：锁管理器或
		 * SQL Server：缓冲区管理器 第二列为counter_name: 对象内的性能计数器名称，如页请求或请求的锁
		 * 第三列为instance_name:
		 * 计数器的命名实例。例如，有为各类型的锁（如表锁、页锁、键锁等）维护的计数器。实例名在相似的计数器之间是有区别的 第四列为value:
		 * 结合cntr_value，cntr_type属性，经过分析和计算得到的性能值
		 */
		String queryStr = "SELECT perf1.object_name, perf1.counter_name, perf1.instance_name, "
				+ "'value' = CASE perf1.cntr_type WHEN 537003008 THEN CONVERT(FLOAT,perf1.cntr_value) /(SELECT CASE perf2.cntr_value "
				+ "WHEN 0 THEN 1 ELSE perf2.cntr_value END "
				+ "FROM master..sysperfinfo perf2 "
				+ "WHERE (perf1.counter_name + ' '= SUBSTRING(perf2.counter_name,1,PATINDEX('% base%', perf2.counter_name))) "
				+ "AND (perf1.instance_name = perf2.instance_name)  AND (perf2.cntr_type = 1073939459)) "
				+ "ELSE perf1.cntr_value  END "
				+ "FROM master..sysperfinfo perf1 "
				+ "WHERE (perf1.cntr_type <> 1073939459)";
		// String queryStraa = "SELECT perf1.object_name, perf1.counter_name,
		// perf1.instance_name, 'value' = CASE perf1.cntr_type WHEN 537003008
		// THEN CONVERT(FLOAT,perf1.cntr_value) /(SELECT CASE perf2.cntr_value
		// WHEN 0 THEN 1 ELSE perf2.cntr_value END FROM master..sysperfinfo
		// perf2 WHERE (perf1.counter_name + ' '=
		// SUBSTRING(perf2.counter_name,1,PATINDEX('% base%',
		// perf2.counter_name))) AND (perf1.instance_name = perf2.instance_name)
		// AND (perf2.cntr_type = 1073939459)) ELSE perf1.cntr_value END " +
		// "FROM master..sysperfinfo perf1 " + "WHERE (perf1.cntr_type <>
		// 1073939459)";
		try {
			util = new JdbcUtil(dburl, username, password);
			// PersistenceServiceable service =
			// PersistenceService.getInstance();
			// tmpResult =
			// CommMonitOperate.persistService.executeQuery(queryStr, conn);
			tmpResult = service.executeQuery(queryStr, util.jdbc());
			// //System.out.println(tmpResult.length);

			for (int i = 1; i < tmpResult.length; i++) {

				// 判断是否采集 缓存击中率
				if (gatherHash.containsKey("bufferhit")) {
					/* 缓存击中率 */
					tmpDouble = 0;
					hitratiobase = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Buffer cache hit ratio")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratio = tmpDouble;
						// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);
					}
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Buffer cache hit ratio base")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratiobase = tmpDouble;
						// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);

					}
					try {
						if (hitratiobase != 0)
							bufferCacheHitRatio = formatDouble((hitratio / hitratiobase) * 100D);
					} catch (Exception ex) {

					}
				}

				// 判断是否采集 plan Cache 命中率
				if (gatherHash.containsKey("planhit")) {
					// plan Cache 命中率
					tmpDouble = 0;
					hitratiobase = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Plan Cache")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"cache hit ratio")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratio = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Plan Cache")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"cache hit ratio base")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratiobase = tmpDouble;
						// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);

					}
					try {
						if (hitratiobase != 0)
							planCacheHitRatio = formatDouble((hitratio / hitratiobase) * 100D);
					} catch (Exception ex) {

					}
				}

				// 判断是否采集 cursor manager 命中率
				if (gatherHash.containsKey("cursorhit")) {
					// cursor manager 命中率
					tmpDouble = 0;
					hitratiobase = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"cache hit ratio")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratio = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"cache hit ratio base")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratiobase = tmpDouble;
						// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);

					}
					try {
						if (hitratiobase != 0)
							cursorManagerByTypeHitRatio = formatDouble((hitratio / hitratiobase) * 100D);
					} catch (Exception ex) {

					}
				}

				// 判断是否采集 catalogMetadata命中率
				if (gatherHash.containsKey("cataloghit")) {
					// catalogMetadata命中率
					tmpDouble = 0;
					hitratiobase = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Catalog Metadata")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cache Hit Ratio")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratio = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Catalog Metadata")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cache Hit Ratio Base")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						hitratiobase = tmpDouble;
						// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);

					}
					try {
						if (hitratiobase != 0)
							catalogMetadataHitRatio = formatDouble((hitratio / hitratiobase) * 100D);
					} catch (Exception ex) {

					}
				}

				// 判断是否采集 ERRORS
				if (gatherHash.containsKey("error")) {
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:SQL Errors")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Errors/sec")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"DB Offline Errors")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						dbOfflineErrors = tmpDouble;
					}
					tmpDouble = 0;
					//
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:SQL Errors")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Errors/sec")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"Kill Connection Errors")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						killConnectionErrors = tmpDouble;
					}
					tmpDouble = 0;
					//
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:SQL Errors")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Errors/sec")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"User Errors")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						userErrors = tmpDouble;
					}
					tmpDouble = 0;
					String ss = tmpResult[i][2];
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:SQL Errors")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Errors/sec")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"Info Errors")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						infoErrors = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:SQL Errors")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Errors/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						sqlServerErrors_total = tmpDouble;
					}
				}

				// 判断是否采集 游标
				if (gatherHash.containsKey("cursor")) {
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cached Cursor Counts")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cachedCursorCounts = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cursor Cache Use Counts/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cursorCacheUseCounts = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cursor Requests/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cursorRequests_total = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Active cursors")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						activeCursors = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cursor memory usage")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cursorMemoryUsage = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Cursor worktable usage")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cursorWorktableUsage = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Cursor Manager by Type")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Number of active cursor plans")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						activeOfCursorPlans = tmpDouble;
					}
				}

				// 判断是否采集 等待信息
				if (gatherHash.containsKey("wait")) {
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Lock waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_lockWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Memory grant queue waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_memoryGrantQueueWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Thread-safe momory objects waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_threadSafeMemoryObjectWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log write waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_logWriteWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log buffer waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_logBufferWaits = tmpDouble;
					}

					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Network IO waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_networkIOWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page IO latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_pageIOLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_pageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Non-Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_nonPageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Wait for the worker")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_waitForTheWorker = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Workspace synchronization waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_workspaceSynchronizationWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Transaction ownership waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"平均等待时间(ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						pingjun_transactionOwnershipWaits = tmpDouble;
					}
					tmpDouble = 0;
					// /////////////////////////////////////////////

					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Lock waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_lockWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Memory grant queue waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_memoryGrantQueueWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Thread-safe momory objects waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_threadSafeMemoryObjectWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log write waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_logWriteWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log buffer waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_logBufferWaits = tmpDouble;
					}
					tmpDouble = 0;

					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Network IO waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_networkIOWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page IO latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_pageIOLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_pageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Non-Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_nonPageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Wait for the worker")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_waitForTheWorker = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Workspace synchronization waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_workspaceSynchronizationWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Transaction ownership waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"正在进行的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						jingxing_transactionOwnershipWaits = tmpDouble;
					}

					// //////////////////////////////////////
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Lock waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_lockWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Memory grant queue waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_memoryGrantQueueWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Thread-safe momory objects waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_threadSafeMemoryObjectWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log write waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_logWriteWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log buffer waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_logBufferWaits = tmpDouble;
					}

					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Network IO waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_networkIOWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page IO latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_pageIOLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_pageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Non-Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_nonPageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Wait for the worker")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_waitForTheWorker = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Workspace synchronization waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_workspaceSynchronizationWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Transaction ownership waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒启动的等待数")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						qidong_transactionOwnershipWaits = tmpDouble;
					}
					// //////////////////////////////////////////////////////////////////////
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Lock waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_lockWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Memory grant queue waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_memoryGrantQueueWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Thread-safe momory objects waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_threadSafeMemoryObjectWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log write waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_logWriteWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Log buffer waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_logBufferWaits = tmpDouble;
					}

					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Network IO waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_networkIOWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page IO latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_pageIOLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_pageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Non-Page latch waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_nonPageLatchWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Wait for the worker")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_waitForTheWorker = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Workspace synchronization waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_workspaceSynchronizationWaits = tmpDouble;
					}
					tmpDouble = 0;
					if (tmpResult[i][0].trim().equalsIgnoreCase(
							"SQLServer:Wait Statistics")
							&& tmpResult[i][1].trim().equalsIgnoreCase(
									"Transaction ownership waits")
							&& tmpResult[i][2].trim().equalsIgnoreCase(
									"每秒的累积等待时间")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						leiji_transactionOwnershipWaits = tmpDouble;
					}
				}

				// 判断是否采集 页信息
				if (gatherHash.containsKey("page")) {
					/* 数据库页 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Database pages")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						dbPages = tmpDouble;
					}
					/* 查找页数/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Page lookups/sec")
							|| tmpResult[i][1].trim().equalsIgnoreCase(
									"Page Requests/sec")) {
						if (totalPageLookups == 0L) {
							totalPageLookups = new Double(tmpResult[i][3])
									.doubleValue();
							totalPageLookupsRate = 0.0D;
						} else {
							tmpdou = totalPageLookups;
							totalPageLookups = new Double(tmpResult[i][3])
									.doubleValue();
							totalPageLookupsRate = calculateRate(
									tmpAppPollInterval, totalPageLookups,
									tmpdou);
						}
					}
					/* 已读页数/分 */

					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Page reads/sec")) {
						if (totalPageReads == 0) {
							totalPageReads = new Double(tmpResult[i][3])
									.doubleValue();
							totalPageReadsRate = 0.0D;
						} else {
							tmpdou = totalPageReads;
							totalPageReads = new Double(tmpResult[i][3])
									.doubleValue();
							totalPageReadsRate = calculateRate(
									tmpAppPollInterval, totalPageReads, tmpdou);
						}
					}
					/* 已写页数/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Page writes/sec")) {
						if (totalPageWrites == 0) {
							totalPageWrites = new Double(tmpResult[i][3])
									.doubleValue();
							totalPageWritesRate = 0.0D;
						} else {
							tmpdou = totalPageWrites;
							totalPageWrites = new Double(tmpResult[i][3])
									.doubleValue();
							totalPageWritesRate = calculateRate(
									tmpAppPollInterval, totalPageWrites, tmpdou);
						}
					}
					/* 总页数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase("Total pages")) {
						tmpdou = new Double(tmpResult[i][3]).doubleValue();
						totalPages = tmpdou;
					}
					/* 空闲页 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase("Free pages")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						freePages = tmpDouble;
					}
				}

				// 判断是否采集 活动连接信息
				if (gatherHash.containsKey("connect")) {
					/* 活动连接 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"User Connections")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						connections = tmpDouble;
					}
					/* 登录/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase("Logins/sec")) {
						if (totalLogins == 0.0D) {
							totalLogins = new Double(tmpResult[i][3])
									.doubleValue();
							totalLoginsRate = 0.0D;
						} else {
							tmpDouble = totalLogins;
							totalLogins = new Double(tmpResult[i][3])
									.doubleValue();
							totalLoginsRate = calculateRate(tmpAppPollInterval,
									totalLogins, tmpDouble);
						}
					}
					/* 退出/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase("Logouts/sec")) {
						if (totalLogouts == 0.0D) {
							totalLogouts = new Double(tmpResult[i][3])
									.doubleValue();
							totalLogoutsRate = 0.0D;
						} else {
							tmpDouble = totalLogouts;
							totalLogouts = new Double(tmpResult[i][3])
									.doubleValue();
							totalLogoutsRate = calculateRate(
									tmpAppPollInterval, totalLogouts, tmpDouble);
						}
					}
				}

				// 判断是否采集 锁汇总信息
				if (gatherHash.containsKey("locktotal")) {
					/* 锁定请求/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Requests/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						if (lockRequests == 0.0D) {
							lockRequests = new Double(tmpResult[i][3])
									.doubleValue();
							lockRequestsRate = 0.0D;
						} else {
							tmpDouble = lockRequests;
							lockRequests = new Double(tmpResult[i][3])
									.doubleValue();
							lockRequestsRate = calculateRate(
									tmpAppPollInterval, lockRequests, tmpDouble);
						}
					}
					/* 锁定等待/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Waits/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						if (lockWaits == 0.0D) {
							lockWaits = new Double(tmpResult[i][3])
									.doubleValue();
							lockWaitsRate = 0.0D;
						} else {
							tmpDouble = lockWaits;
							lockWaits = new Double(tmpResult[i][3])
									.doubleValue();
							lockWaitsRate = calculateRate(tmpAppPollInterval,
									lockWaits, tmpDouble);
						}
					}
					/* 锁定超时/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Timeouts/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						if (lockTimeouts == 0.0D) {
							lockTimeouts = new Double(tmpResult[i][3])
									.doubleValue();
							lockTimeoutsRate = 0.0D;
						} else {
							tmpDouble = lockTimeouts;
							lockTimeouts = new Double(tmpResult[i][3])
									.doubleValue();
							lockTimeoutsRate = calculateRate(
									tmpAppPollInterval, lockTimeouts, tmpDouble);
						}
					}
					/* 死锁数/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Number of Deadlocks/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						deadLocks = tmpDouble;
						if (totalLogouts == 0.0D) {
							totalLogouts = new Double(tmpResult[i][3])
									.doubleValue();
							totalLogoutsRate = 0.0D;
						} else {
							tmpDouble = totalLogouts;
							totalLogouts = new Double(tmpResult[i][3])
									.doubleValue();
							totalLogoutsRate = calculateRate(
									tmpAppPollInterval, totalLogouts, tmpDouble);
						}
					}
					/* 平均锁定等待时间 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Average Wait Time (ms)")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						if (!tmpResult[i][3].equals("")) {
							tmpDouble = new Double(tmpResult[i][3])
									.doubleValue();
							avgWaitTime = formatDouble(tmpDouble);
						} else {
							avgWaitTime = 0;
						}
					}
					/* 锁定等待/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Latch Waits/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						latchWaits = tmpDouble;
						if (latchWaits == 0.0D) {
							latchWaits = new Double(tmpResult[i][3])
									.doubleValue();
							latchWaitsRate = 0.0D;
						} else {
							tmpDouble = latchWaits;
							latchWaits = new Double(tmpResult[i][3])
									.doubleValue();
							latchWaitsRate = calculateRate(tmpAppPollInterval,
									latchWaits, tmpDouble);
						}
					}
					/* 平均闭锁等待时间 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Average Latch Wait Time")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						avgLatchWait = formatDouble(tmpDouble);
					}
				}

				// 判断是否采集 缓存信息
				if (gatherHash.containsKey("cache")) {
					/* 缓存击中率 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Hit Ratio")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheHitRatio = formatDouble(tmpDouble * 100D);
					}

					/* 缓存数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Object Counts")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheCount = tmpDouble;
					}
					/* 缓存页 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase("Cache Pages")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cachePages = tmpDouble;
					}
					/* 使用的缓存/秒 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Use Counts/sec")
							&& tmpResult[i][2].trim()
									.equalsIgnoreCase("_Total")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheUsed = tmpDouble;
						if (cacheUsed == 0.0D) {
							cacheUsed = new Double(tmpResult[i][3])
									.doubleValue();
							cacheUsedRate = 0.0D;
						} else {
							tmpDouble = cacheUsed;
							cacheUsed = new Double(tmpResult[i][3])
									.doubleValue();
							cacheUsedRate = calculateRate(tmpAppPollInterval,
									cacheUsed, tmpDouble);
						}
					}
				}

				// 判断是否采集内存信息
				if (gatherHash.containsKey("memory")) {
					/* 内存总数 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Total Server Memory (KB)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						totalMemory = tmpDouble;
					}
					/* SQL缓存存储 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"SQL Cache Memory (KB)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						sqlMem = tmpDouble;
					}
					/* 内存优化 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Optimizer Memory (KB)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						optMemory = tmpDouble;
					}
					/* 内存分配未决 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Memory Grants Pending")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						memGrantPending = tmpDouble;
					}
					/* 内存分配成功 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Memory Grants Outstanding")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						memGrantPending = tmpDouble;
					}
					/* 锁定内存 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Memory (KB)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						lockMem = tmpDouble;
					}
					/* 连接内存 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Connection Memory (KB)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						conMemory = tmpDouble;
					}
					/* 产生的工作空间内存 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Granted Workspace Memory (KB)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						grantedWorkspaceMem = tmpDouble;
					}
				}

				// 判断是否采集SQL信息
				if (gatherHash.containsKey("sql")) {
					/* 批请求/秒 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Batch Requests/sec")) {
						if (batchRequests == 0.0D) {
							batchRequests = new Double(tmpResult[i][3])
									.doubleValue();
							batchRequestsRate = 0.0D;
						} else {
							tmpDouble = batchRequests;
							batchRequests = new Double(tmpResult[i][3])
									.doubleValue();
							batchRequestsRate = calculateRate(
									tmpAppPollInterval, batchRequests,
									tmpDouble);
						}
					}
					/* SQL编辑/秒 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"SQL Compilations/sec")) {
						if (sqlCompilations == 0.0D) {
							sqlCompilations = new Double(tmpResult[i][3])
									.doubleValue();
							sqlCompilationsRate = 0.0D;
						} else {
							tmpDouble = sqlCompilations;
							sqlCompilations = new Double(tmpResult[i][3])
									.doubleValue();
							sqlCompilationsRate = calculateRate(
									tmpAppPollInterval, sqlCompilations,
									tmpDouble);
						}
					}
					/* SQL重编辑/秒 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"SQL Re-Compilations/sec")) {
						if (sqlRecompilation == 0.0D) {
							sqlRecompilation = new Double(tmpResult[i][3])
									.doubleValue();
							sqlRecompilationRate = 0.0D;
						} else {
							tmpDouble = sqlRecompilation;
							sqlRecompilation = new Double(tmpResult[i][3])
									.doubleValue();
							sqlRecompilationRate = calculateRate(
									tmpAppPollInterval, sqlRecompilation,
									tmpDouble);
						}
					}
					/* 自动参数化尝试次数/秒 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Auto-Param Attempts/sec")) {
						if (autoParams == 0.0D) {
							autoParams = new Double(tmpResult[i][3])
									.doubleValue();
							autoParamsRate = 0.0D;
						} else {
							tmpDouble = autoParams;
							autoParams = new Double(tmpResult[i][3])
									.doubleValue();
							autoParamsRate = calculateRate(tmpAppPollInterval,
									autoParams, tmpDouble);
						}
					}
					/* 自动参数化失败次数/秒 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Auto-Param Attempts/sec")) {
						if (failedAutoParams == 0.0D) {
							failedAutoParams = new Double(tmpResult[i][3])
									.doubleValue();
							failedAutoParamsRate = 0.0D;
						} else {
							tmpDouble = failedAutoParams;
							failedAutoParams = new Double(tmpResult[i][3])
									.doubleValue();
							failedAutoParamsRate = calculateRate(
									tmpAppPollInterval, failedAutoParams,
									tmpDouble);
						}
					}
				}

				// 判断是否采集SQL信息
				if (gatherHash.containsKey("scan")) {
					/* 完全扫描/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Full Scans/sec")) {
						if (fullScans == 0.0D) {
							fullScans = new Double(tmpResult[i][3])
									.doubleValue();
							fullScansRate = 0.0D;
						} else {
							tmpDouble = fullScans;
							fullScans = new Double(tmpResult[i][3])
									.doubleValue();
							fullScansRate = calculateRate(tmpAppPollInterval,
									fullScans, tmpDouble);
						}
					}
					/* 范围扫描/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Range Scans/sec")) {
						if (rangeScans == 0.0D) {
							rangeScans = new Double(tmpResult[i][3])
									.doubleValue();
							rangeScansRate = 0.0D;
						} else {
							tmpDouble = rangeScans;
							rangeScans = new Double(tmpResult[i][3])
									.doubleValue();
							rangeScansRate = calculateRate(tmpAppPollInterval,
									rangeScans, tmpDouble);
						}
					}
					/* 探针扫描/分 */
					tmpDouble = 0;
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Probe Scans/sec")) {
						if (probeScans == 0.0D) {
							probeScans = new Double(tmpResult[i][3])
									.doubleValue();
							probeScansRate = 0.0D;
						} else {
							tmpDouble = probeScans;
							probeScans = new Double(tmpResult[i][3])
									.doubleValue();
							probeScansRate = calculateRate(tmpAppPollInterval,
									probeScans, tmpDouble);
						}
					}
				}

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			util.closeStmt();
			util.closeConn();

		}

		/* 未取到"_Total"库的值则取其他库的总和，锁明细，缓存管理统计 */
		if (cacheHitRatio == 0.0D) {

			queryStr = "select counter_name ,sum(cntr_value) from sysperfinfo "
					+ "where object_name in ('SQLServer:Locks','SQLServer:Cache Manager') "
					+ "and instance_name!='_Total' group by counter_name";
			try {
				util = new JdbcUtil(dburl, username, password);
				String[][] tmpResult2 = service.executeQuery(queryStr, util
						.jdbc());
				for (int i = 1; i < tmpResult2.length; i++) {
					/* 锁定请求/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Requests/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						lockRequests = tmpDouble;
					}
					/* 锁定等待/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Waits/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						lockWaits = tmpDouble;
					}
					/* 锁定超时/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Timeouts/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						lockTimeouts = tmpDouble;
					}
					/* 死锁数/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Number of Deadlocks/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						deadLocks = tmpDouble;
					}
					/* 平均锁定等待时间 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Average Wait Time (ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						avgWaitTime = formatDouble(tmpDouble);
					}
					/* 缓存击中率 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Hit Ratio")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheHitRatio = formatDouble(tmpDouble * 100D);
					}
					/* 缓存数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Object Counts")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheCount = tmpDouble;
					}
					/* 缓存页 */
					if (tmpResult[i][1].trim().equalsIgnoreCase("Cache Pages")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cachePages = tmpDouble;
					}
					/* 使用的缓存/秒 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Use Counts/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheUsed = tmpDouble;
					}
					/* 平均锁定等待时间基数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Average Wait Time Base")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						avgWaitTimeBase = tmpDouble;
					}
					/* 缓存击中率基数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Hit Ratio Base")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheHitRatioBase = tmpDouble;
					}

					if (cacheHitRatio != 0.0D)
						cacheHitRatio = calculateValue(cacheHitRatio,
								cacheHitRatioBase, true);
					if (avgWaitTime != 0.0D)
						avgWaitTime = calculateValue(avgWaitTime,
								avgWaitTimeBase, false);
				}
				if (tmpResult2 != null)
					tmpResult2 = null;
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				util.closeStmt();
				util.closeConn();
			}
		}
		Hashtable retValue = new Hashtable();
		/* 缓存管理统计 */
		Hashtable pages = new Hashtable();
		pages.put("bufferCacheHitRatio", bufferCacheHitRatio + ""); // *$ 缓存击中率
		pages.put("planCacheHitRatio", planCacheHitRatio + ""); // *$ plan
		// cache击中率
		pages.put("cursorManagerByTypeHitRatio", cursorManagerByTypeHitRatio
				+ ""); // *$ Cursor Manager by Type击中率
		pages.put("catalogMetadataHitRatio", catalogMetadataHitRatio + ""); // *$
		// Catalog
		// Metadata击中率

		pages.put("dbOfflineErrors", dbOfflineErrors + "");
		pages.put("killConnectionErrors", killConnectionErrors + "");
		pages.put("userErrors", userErrors + "");
		pages.put("infoErrors", infoErrors + "");
		pages.put("sqlServerErrors_total", sqlServerErrors_total + "");

		pages.put("cachedCursorCounts", cachedCursorCounts + "");
		pages.put("cursorCacheUseCounts", cursorCacheUseCounts + "");
		pages.put("cursorRequests_total", cursorRequests_total + "");
		pages.put("activeCursors", activeCursors + "");
		pages.put("cursorMemoryUsage", cursorMemoryUsage + "");
		pages.put("cursorWorktableUsage", cursorWorktableUsage + "");
		pages.put("activeOfCursorPlans", activeOfCursorPlans + "");

		pages.put("dbPages", dbPages + "");// * 数据库页
		pages.put("totalPageLookups", totalPageLookups + "");// 查找页数
		pages.put("totalPageLookupsRate", totalPageLookupsRate + "");// *
		// 查找页数/分
		pages.put("totalPageReads", totalPageReads + "");// 已读页数
		pages.put("totalPageReadsRate", totalPageReadsRate + ""); // * 已读页数/分
		pages.put("totalPageWrites", totalPageWrites + "");// 已写页数
		pages.put("totalPageWritesRate", totalPageWritesRate + "");// * 已写页数/分
		pages.put("totalPages", totalPages + "");// * 总页数
		pages.put("freePages", freePages + "");// * 空闲页

		Hashtable statisticsHash = new Hashtable();
		statisticsHash.put("pingjun_lockWaits", pingjun_lockWaits + "");
		statisticsHash.put("pingjun_memoryGrantQueueWaits",
				pingjun_memoryGrantQueueWaits + "");
		statisticsHash.put("pingjun_threadSafeMemoryObjectWaits",
				pingjun_threadSafeMemoryObjectWaits + "");
		statisticsHash.put("pingjun_logWriteWaits", pingjun_logWriteWaits + "");
		statisticsHash.put("pingjun_logBufferWaits", pingjun_logBufferWaits
				+ "");
		statisticsHash.put("pingjun_networkIOWaits", pingjun_networkIOWaits
				+ "");
		statisticsHash.put("pingjun_pageIOLatchWaits", pingjun_pageIOLatchWaits
				+ "");
		statisticsHash.put("pingjun_pageLatchWaits", pingjun_pageLatchWaits
				+ "");
		statisticsHash.put("pingjun_nonPageLatchWaits",
				pingjun_nonPageLatchWaits + "");
		statisticsHash.put("pingjun_waitForTheWorker", pingjun_waitForTheWorker
				+ "");
		statisticsHash.put("pingjun_workspaceSynchronizationWaits",
				pingjun_workspaceSynchronizationWaits + "");
		statisticsHash.put("pingjun_transactionOwnershipWaits",
				pingjun_transactionOwnershipWaits + "");

		statisticsHash.put("jingxing_lockWaits", jingxing_lockWaits + "");
		statisticsHash.put("jingxing_memoryGrantQueueWaits",
				jingxing_memoryGrantQueueWaits + "");
		statisticsHash.put("jingxing_threadSafeMemoryObjectWaits",
				jingxing_threadSafeMemoryObjectWaits + "");
		statisticsHash.put("jingxing_logWriteWaits", jingxing_logWriteWaits
				+ "");
		statisticsHash.put("jingxing_logBufferWaits", jingxing_logBufferWaits
				+ "");
		statisticsHash.put("jingxing_networkIOWaits", jingxing_networkIOWaits
				+ "");
		statisticsHash.put("jingxing_pageIOLatchWaits",
				jingxing_pageIOLatchWaits + "");
		statisticsHash.put("jingxing_pageLatchWaits", jingxing_pageLatchWaits
				+ "");
		statisticsHash.put("jingxing_nonPageLatchWaits",
				jingxing_nonPageLatchWaits + "");
		statisticsHash.put("jingxing_waitForTheWorker",
				jingxing_waitForTheWorker + "");
		statisticsHash.put("jingxing_workspaceSynchronizationWaits",
				jingxing_workspaceSynchronizationWaits + "");
		statisticsHash.put("jingxing_transactionOwnershipWaits",
				jingxing_transactionOwnershipWaits + "");

		statisticsHash.put("qidong_lockWaits", qidong_lockWaits + "");
		statisticsHash.put("qidong_memoryGrantQueueWaits",
				qidong_memoryGrantQueueWaits + "");
		statisticsHash.put("qidong_threadSafeMemoryObjectWaits",
				qidong_threadSafeMemoryObjectWaits + "");
		statisticsHash.put("qidong_logWriteWaits", qidong_logWriteWaits + "");
		statisticsHash.put("qidong_logBufferWaits", qidong_logBufferWaits + "");
		statisticsHash.put("qidong_networkIOWaits", qidong_networkIOWaits + "");
		statisticsHash.put("qidong_pageIOLatchWaits", qidong_pageIOLatchWaits
				+ "");
		statisticsHash.put("qidong_pageLatchWaits", qidong_pageLatchWaits + "");
		statisticsHash.put("qidong_nonPageLatchWaits", qidong_nonPageLatchWaits
				+ "");
		statisticsHash.put("qidong_waitForTheWorker", qidong_waitForTheWorker
				+ "");
		statisticsHash.put("qidong_workspaceSynchronizationWaits",
				qidong_workspaceSynchronizationWaits + "");
		statisticsHash.put("qidong_transactionOwnershipWaits",
				qidong_transactionOwnershipWaits + "");

		statisticsHash.put("leiji_lockWaits", leiji_lockWaits + "");
		statisticsHash.put("leiji_memoryGrantQueueWaits",
				leiji_memoryGrantQueueWaits + "");
		statisticsHash.put("leiji_threadSafeMemoryObjectWaits",
				leiji_threadSafeMemoryObjectWaits + "");
		statisticsHash.put("leiji_logWriteWaits", leiji_logWriteWaits + "");
		statisticsHash.put("leiji_logBufferWaits", leiji_logBufferWaits + "");
		statisticsHash.put("leiji_networkIOWaits", leiji_networkIOWaits + "");
		statisticsHash.put("leiji_pageIOLatchWaits", leiji_pageIOLatchWaits
				+ "");
		statisticsHash.put("leiji_pageLatchWaits", leiji_pageLatchWaits + "");
		statisticsHash.put("leiji_nonPageLatchWaits", leiji_nonPageLatchWaits
				+ "");
		statisticsHash.put("leiji_waitForTheWorker", leiji_waitForTheWorker
				+ "");
		statisticsHash.put("leiji_workspaceSynchronizationWaits",
				leiji_workspaceSynchronizationWaits + "");
		statisticsHash.put("leiji_transactionOwnershipWaits",
				leiji_transactionOwnershipWaits + "");

		/* 数据库页连接统计 */
		Hashtable conns = new Hashtable();
		conns.put("connections", connections + ""); // * 活动连接
		conns.put("totalLogins", totalLogins + "");// 登录
		conns.put("totalLoginsRate", totalLoginsRate + "");// * 登录/分
		conns.put("totalLogouts", totalLogouts + "");// 退出
		conns.put("totalLogoutsRate", totalLogoutsRate + ""); // * 退出/分

		/* 锁明细 */
		Hashtable locks = new Hashtable();
		locks.put("lockRequests", lockRequests + ""); // 锁定请求
		locks.put("lockRequestsRate", lockRequestsRate + "");// *$ 锁定请求/分
		locks.put("lockWaits", lockWaits + "");// 锁定等待
		locks.put("lockWaitsRate", lockWaitsRate + "");// * 锁定等待/分
		locks.put("lockTimeouts", lockTimeouts + ""); // 锁定超时
		locks.put("lockTimeoutsRate", lockTimeoutsRate + ""); // * 锁定超时/分
		locks.put("deadLocks", deadLocks + "");// 死锁数
		locks.put("deadLocksRate", deadLocksRate + "");// * 死锁数/分
		locks.put("avgWaitTime", avgWaitTime + "");// * 平均锁定等待时间
		locks.put("avgWaitTimeBase", avgWaitTimeBase + ""); // 平均锁定等待时间计算的基数
		/* 闭锁的明细 */
		locks.put("latchWaits", latchWaits + "");// 闭锁等待
		locks.put("latchWaitsRate", latchWaitsRate + "");// * 闭锁等待/分
		locks.put("avgLatchWait", avgLatchWait + ""); // *$ 平均闭锁等待时间;

		/* 缓存明细 */
		Hashtable caches = new Hashtable();
		caches.put("cacheHitRatio", cacheHitRatio + ""); // *$ 缓存击中率
		caches.put("cacheHitRatioBase", cacheHitRatioBase + "");// 缓存击中率计算的基数
		caches.put("cacheCount", cacheCount + "");// * 缓存数
		caches.put("cachePages", cachePages + "");// * 缓存页
		caches.put("cacheUsed", cacheUsed + ""); // 使用的缓存
		caches.put("cacheUsedRate", cacheUsedRate + ""); // * 使用的缓存/分

		/* 内存利用情况 */
		Hashtable mems = new Hashtable();
		mems.put("totalMemory", totalMemory + ""); // *$ 内存总数
		mems.put("sqlMem", sqlMem + ""); // *$ SQL缓存存储
		mems.put("optMemory", optMemory + "");// *$ 内存优化
		mems.put("memGrantPending", memGrantPending + "");// * 内存分配未决
		mems.put("memGrantSuccess", memGrantSuccess + ""); // * 内存分配成功
		mems.put("lockMem", lockMem + ""); // *$ 锁定内存
		mems.put("conMemory", conMemory + "");// *$ 连接内存
		mems.put("grantedWorkspaceMem", grantedWorkspaceMem + ""); // *
		// 产生的工作空间内存

		/* SQL统计 */
		Hashtable sqls = new Hashtable();
		sqls.put("batchRequests", batchRequests + "" + ""); // 批请求
		sqls.put("batchRequestsRate", batchRequestsRate + "");// *$ 批请求/分
		sqls.put("sqlCompilations", sqlCompilations + "");// SQL编辑
		sqls.put("sqlCompilationsRate", sqlCompilationsRate + "");// * SQL编辑/分
		sqls.put("sqlRecompilation", sqlRecompilation + "");// //SQL重编辑
		sqls.put("sqlRecompilationRate", sqlRecompilationRate + ""); // *
		// SQL重编辑/分
		sqls.put("autoParams", autoParams + "");// 自动参数化尝试次数
		sqls.put("autoParamsRate", autoParamsRate + "");// * 自动参数化尝试次数/分
		sqls.put("failedAutoParams", failedAutoParams + "");// 自动参数化失败次数
		sqls.put("failedAutoParamsRate", failedAutoParamsRate + "");// *
		// 自动参数化失败次数/分

		/* 访问方法的明细 */
		Hashtable scans = new Hashtable();
		scans.put("fullScans", fullScans + ""); // 完全扫描
		scans.put("fullScansRate", fullScansRate + "");// *$ 完全扫描/分
		scans.put("rangeScans", rangeScans + "");// 范围扫描
		scans.put("rangeScansRate", rangeScansRate + "");// * 范围扫描/分
		scans.put("probeScans", probeScans + "");// 探针扫描
		scans.put("probeScansRate", probeScansRate + ""); // * 探针扫描/分
		/*
		 * Vector altfiles_v = new Vector(); try{ altfiles_v =
		 * getSqlserverAltfile(ip,username,password); }catch(Exception e){
		 * e.printStackTrace(); } Vector lockinfo_v = new Vector(); try{
		 * lockinfo_v = getSqlserverLockinfo(ip,username,password);
		 * }catch(Exception e){ e.printStackTrace(); } Vector process_v = new
		 * Vector(); try{ process_v =
		 * getSqlserverProcesses(ip,username,password); }catch(Exception e){
		 * e.printStackTrace(); }
		 * 
		 * Hashtable sysValue = new Hashtable(); try{ sysValue =
		 * getSqlServerSys(ip,username,password); }catch(Exception e){
		 * e.printStackTrace(); }
		 */
		retValue.put("pages", pages);// nms_sqlserverpages
		retValue.put("conns", conns);// nms_sqlserverconns
		retValue.put("locks", locks);// nms_sqlserverlocks
		retValue.put("caches", caches);// nms_sqlservercaches
		retValue.put("mems", mems);// nms_sqlservermems
		retValue.put("sqls", sqls);// nms_sqlserversqls
		retValue.put("scans", scans);// nms_sqlserverscans
		retValue.put("statisticsHash", statisticsHash);// nms_sqlserverstatisticshash
		return retValue;

	}

	public Hashtable getSqlServerData(String ip, String username,
			String password, Hashtable gatherHash) throws Exception {
		String dburl = "jdbc:jtds:sqlserver://" + ip
				+ ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
		String sql = "SELECT SERVERPROPERTY ('productlevel') as productlevel, @@VERSION as VERSION,SERVERPROPERTY('MACHINENAME') as MACHINENAME,SERVERPROPERTY('IsSingleUser') as IsSingleUser,SERVERPROPERTY('ProcessID') as ProcessID,SERVERPROPERTY('IsIntegratedSecurityOnly') as IsIntegratedSecurityOnly,SERVERPROPERTY('IsClustered') as IsClustered";
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sqlserverDataHash = new Hashtable();
		Hashtable sys_hash = new Hashtable();
		Vector lockVector = new Vector();
		Vector processVector = new Vector();
		Hashtable dbHashValue = new Hashtable();

		try {
			util = new OracleJdbcUtil(dburl, username, password);
			util.jdbc();

			// 判断是否需要采集系统信息
			if (gatherHash.containsKey("sysvalue")) {
				// 获取SQLSERVER系统信息
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						if (rs.getString("productlevel") != null) {
							sys_hash.put("productlevel", rs
									.getString("productlevel"));
						}
						if (rs.getString("VERSION") != null) {
							sys_hash.put("VERSION", rs.getString("VERSION"));
						}
						if (rs.getString("MACHINENAME") != null) {
							sys_hash.put("MACHINENAME", rs
									.getString("MACHINENAME"));
						}
						if (rs.getString("IsSingleUser") != null) {
							String IsSingleUser = rs.getString("IsSingleUser");
							if (IsSingleUser.equalsIgnoreCase("1")) {
								sys_hash.put("IsSingleUser", "单个用户");
							} else {
								sys_hash.put("IsSingleUser", "非单个用户");
							}
						}
						if (rs.getString("ProcessID") != null) {
							sys_hash
									.put("ProcessID", rs.getString("ProcessID"));
						}
						if (rs.getString("IsIntegratedSecurityOnly") != null) {
							String IsSingleUser = rs
									.getString("IsIntegratedSecurityOnly");
							if (IsSingleUser.equalsIgnoreCase("1")) {
								sys_hash.put("IsIntegratedSecurityOnly",
										"集成安全性");
							} else {
								sys_hash.put("IsIntegratedSecurityOnly",
										"非集成安全性");
							}
						}
						if (rs.getString("IsClustered") != null) {
							String IsSingleUser = rs.getString("IsClustered");
							if (IsSingleUser.equalsIgnoreCase("1")) {
								sys_hash.put("IsClustered", "群集");
							} else {
								sys_hash.put("IsClustered", "非群集");
							}
						}
						// collectSQLServerMonitItemsDetail(util.jdbc());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					rs.close();
				}
			}

			// 判断是否需要采集锁信息
			if (gatherHash.containsKey("lock")) {
				// 获取SQLSERVER锁信息
				sql = "select distinct a.rsc_text,a.rsc_dbid,b.name as dbname,a.rsc_indid,a.rsc_objid,a.rsc_type,a.rsc_flag,a.req_mode,a.req_status,"
						+ "a.req_refcnt,a.req_cryrefcnt,a.req_lifetime,a.req_spid,a.req_ecid,a.req_ownertype,a.req_transactionID from syslockinfo a,"
						+ "sysdatabases b where a.rsc_dbid=b.dbid;";
				try {
					rs = util.stmt.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();

					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								// SysLogger.info(col.toLowerCase()+"============"+tmp);
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						lockVector.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
			}

			// 判断是否需要采集进程信息
			if (gatherHash.containsKey("process")) {
				// 获取SQLSERVER进程信息
				sql = "select distinct a.spid,a.waittime,a.lastwaittype,a.waitresource,b.name as dbname,c.name as "
						+ "username,a.cpu,a.physical_io,a.memusage,a.login_time,a.last_batch,a.status,a.hostname,"
						+ "a.program_name,a.hostprocess,a.cmd,a.nt_domain,a.nt_username,a.net_library,a.loginame from "
						+ "sysprocesses a,sysdatabases b,sysusers c where a.dbid= b.dbid and a.uid=c.uid";
				try {
					rs = util.stmt.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();

					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								// SysLogger.info(col.toLowerCase()+"=======process
								// ================ "+tmp);
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						processVector.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
			}

			// 判断是否需要采集DB和日志文件信息
			if (gatherHash.containsKey("db")) {
				// 获取SQLSERVER的DB和日志文件信息
				sql = "select counter_name,instance_name,cntr_value from master.dbo.sysperfinfo where (object_name ='SQLServer:Databases' or object_name ='MSSQL$SQLSERVER:Databases') and (counter_name='Data File(s) Size (KB)' or counter_name='Log File(s) Size (KB)') order by instance_name ";
				Hashtable retdatabase = new Hashtable();
				Hashtable retlogfile = new Hashtable();
				Vector names = new Vector();
				try {
					rs = util.stmt.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();
					Hashtable alldatabase = new Hashtable();
					Hashtable alllogfile = new Hashtable();
					Hashtable database = new Hashtable();
					Hashtable logfile = new Hashtable();

					while (rs.next()) {
						database = new Hashtable();
						logfile = new Hashtable();
						Hashtable return_value = new Hashtable();
						String counter_name = rs.getString("counter_name")
								.toString();
						String instance_name = rs.getString("instance_name")
								.toString().trim();

						int cntr_value = rs.getInt("cntr_value");
						if (counter_name.trim()
								.equals("Data File(s) Size (KB)")) {
							// 数据文件大小
							/*--------------------------modify 2010-5-10--------------*/
							if (alldatabase.get(instance_name) == null) {
								database.put("dbname", instance_name);
								database.put("size", cntr_value / 1024);
								alldatabase.put(instance_name, database);
								names.add(instance_name);
							} else {
								Hashtable preValue = (Hashtable) alldatabase
										.get(instance_name);
								int presize = (Integer) preValue.get("size");
								preValue.put("size", presize + cntr_value
										/ 1024);
							}
							/*--------------------------------------------------------*/

						} else if (counter_name.trim().equals(
								"Log File(s) Size (KB)")) {
							// LOG文件大小
							if (alllogfile.get(instance_name) == null) {
								logfile.put("logname", instance_name);
								logfile.put("size", cntr_value / 1024);
								// SysLogger.info("LOG :
								// "+instance_name+"==="+cntr_value);
								alllogfile.put(instance_name, logfile);
							} else {
								Hashtable preVal = (Hashtable) alllogfile
										.get(instance_name);
								int presize = (Integer) preVal.get("size");
								preVal.put("size", presize + cntr_value / 1024);
							}

						}
					}
					sql = "select B.name as dbName,A.name as logicName,A.fileid,size from master.dbo.sysaltfiles A,master.dbo.sysdatabases B where A.dbid=B.dbid order by B.name";
					try {
						rs = util.stmt.executeQuery(sql);
						rsmd = rs.getMetaData();
						while (rs.next()) {
							String dbName = rs.getString("dbName").toString()
									.trim();
							// String logicName = rs.getString(1).toString();
							String field = rs.getString("fileid").toString();
							int usedsize = rs.getInt("size");
							float f_usedsize = new Float(usedsize);
							if ("1".equals(field)) {
								// 数据文件
								if (alldatabase.get(dbName) != null) {
									Hashtable _database = (Hashtable) alldatabase
											.get(dbName);
									int preused = 0;
									if (_database.get("usedsize") != null) {
										preused = Integer
												.parseInt((String) _database
														.get("usedsize"));
									}
									_database
											.put("usedsize", String
													.valueOf(usedsize / 1024
															+ preused));
									float allsize = new Float(_database.get(
											"size").toString());
									f_usedsize = usedsize / 1024 + preused;
									float f_usedperc = 100 * (f_usedsize / allsize);
									int usedperc = new Float(f_usedperc)
											.intValue();
									_database.put("usedperc", usedperc + "");
									// SysLogger.info("DB : usedperc :
									// "+usedperc);
									retdatabase.put(dbName, _database);

								}

							} else {
								// 日志文件
								if (alllogfile.get(dbName) != null) {
									Hashtable _logfile = (Hashtable) alllogfile
											.get(dbName);
									int preuse = 0;
									// //System.out.println(dbName);
									if (_logfile.get("usedsize") != null)
										preuse = Integer
												.parseInt((String) _logfile
														.get("usedsize"));

									_logfile.put("usedsize", String
											.valueOf(usedsize / 1024 + preuse));
									// //System.out.println(_logfile.get("usedsize"));
									f_usedsize = usedsize / 1024 + preuse;
									float allsize = new Float(_logfile.get(
											"size").toString());
									float f_usedperc = (100 * f_usedsize / (allsize));
									int usedperc = new Float(f_usedperc)
											.intValue();
									_logfile.put("usedperc", usedperc + "");
									// SysLogger.info("LOG : usedperc :
									// "+usedperc);
									retlogfile.put(dbName, _logfile);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					dbHashValue.put("database", retdatabase);
					dbHashValue.put("logfile", retlogfile);
					dbHashValue.put("names", names);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();

				}
			}

			sqlserverDataHash.put("sysValue", sys_hash);
			sqlserverDataHash.put("lockinfo_v", lockVector);
			sqlserverDataHash.put("info_v", processVector);
			sqlserverDataHash.put("dbValue", dbHashValue);

			// return retValue;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return sqlserverDataHash;
	}

	public Hashtable collectSQLServerMonitItemsDetail(String ip, String dbname,
			String username, String password) {
		boolean returnbool = false;
		String tmpResult[][] = null;
		double hitratio = 0;
		double hitratiobase = 0;
		double tmpDouble = 0.0D;

		double dbOfflineErrors = 0;
		double killConnectionErrors = 0;
		double userErrors = 0;
		double infoErrors = 0;
		double sqlServerErrors_total = 0;

		double cachedCursorCounts = 0; // cachedCursorCounts_total
		double cursorCacheUseCounts = 0;// cursorCacheUseCounts_total
		double cursorRequests_total = 0;
		double activeCursors = 0;// activeCursors_total
		double cursorMemoryUsage = 0;// cursorMemoryUsage_total
		double cursorWorktableUsage = 0;// cursorWorktableUsage_total
		double activeOfCursorPlans = 0;// Number of active cursor plans_total

		double pingjun_lockWaits = 0;
		double pingjun_memoryGrantQueueWaits = 0;
		double pingjun_threadSafeMemoryObjectWaits = 0;
		double pingjun_logWriteWaits = 0;
		double pingjun_logBufferWaits = 0;
		double pingjun_networkIOWaits = 0;
		double pingjun_pageIOLatchWaits = 0;
		double pingjun_pageLatchWaits = 0;
		double pingjun_nonPageLatchWaits = 0;
		double pingjun_waitForTheWorker = 0;
		double pingjun_workspaceSynchronizationWaits = 0;
		double pingjun_transactionOwnershipWaits = 0;

		double jingxing_lockWaits = 0;
		double jingxing_memoryGrantQueueWaits = 0;
		double jingxing_threadSafeMemoryObjectWaits = 0;
		double jingxing_logWriteWaits = 0;
		double jingxing_logBufferWaits = 0;
		double jingxing_networkIOWaits = 0;
		double jingxing_pageIOLatchWaits = 0;
		double jingxing_pageLatchWaits = 0;
		double jingxing_nonPageLatchWaits = 0;
		double jingxing_waitForTheWorker = 0;
		double jingxing_workspaceSynchronizationWaits = 0;
		double jingxing_transactionOwnershipWaits = 0;

		double qidong_lockWaits = 0;
		double qidong_memoryGrantQueueWaits = 0;
		double qidong_threadSafeMemoryObjectWaits = 0;
		double qidong_logWriteWaits = 0;
		double qidong_logBufferWaits = 0;
		double qidong_networkIOWaits = 0;
		double qidong_pageIOLatchWaits = 0;
		double qidong_pageLatchWaits = 0;
		double qidong_nonPageLatchWaits = 0;
		double qidong_waitForTheWorker = 0;
		double qidong_workspaceSynchronizationWaits = 0;
		double qidong_transactionOwnershipWaits = 0;

		double leiji_lockWaits = 0;
		double leiji_memoryGrantQueueWaits = 0;
		double leiji_threadSafeMemoryObjectWaits = 0;
		double leiji_logWriteWaits = 0;
		double leiji_logBufferWaits = 0;
		double leiji_networkIOWaits = 0;
		double leiji_pageIOLatchWaits = 0;
		double leiji_pageLatchWaits = 0;
		double leiji_nonPageLatchWaits = 0;
		double leiji_waitForTheWorker = 0;
		double leiji_workspaceSynchronizationWaits = 0;
		double leiji_transactionOwnershipWaits = 0;

		long tmpLong = 0L;
		double tmpdou = 0;
		String dburl = "jdbc:jtds:sqlserver://" + ip
				+ ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
		String sql = "select a.name,b.rows from sysobjects a,sysindexes b where a.type='u' and b.id=object_id(a.name) and indid in(0,1) order by a.name;";
		JdbcUtil util = null;
		ResultSet rs = null;

		// long tmpAppPollInterval = (System.currentTimeMillis()-
		// getLastUpdatetime())/1000;
		long tmpAppPollInterval = getTaskInterval();
		PersistenceServiceable service = PersistenceService.getInstance();

		/*
		 * 取得系统表master..sysperfinfo的信息 第一列为object_name: 性能对象名，如 SQL Server：锁管理器或
		 * SQL Server：缓冲区管理器 第二列为counter_name: 对象内的性能计数器名称，如页请求或请求的锁
		 * 第三列为instance_name:
		 * 计数器的命名实例。例如，有为各类型的锁（如表锁、页锁、键锁等）维护的计数器。实例名在相似的计数器之间是有区别的 第四列为value:
		 * 结合cntr_value，cntr_type属性，经过分析和计算得到的性能值
		 */
		String queryStr = "SELECT perf1.object_name, perf1.counter_name, perf1.instance_name, "
				+ "'value' = CASE perf1.cntr_type WHEN 537003008 THEN CONVERT(FLOAT,perf1.cntr_value) /(SELECT CASE perf2.cntr_value "
				+ "WHEN 0 THEN 1 ELSE perf2.cntr_value END "
				+ "FROM master..sysperfinfo perf2 "
				+ "WHERE (perf1.counter_name + ' '= SUBSTRING(perf2.counter_name,1,PATINDEX('% base%', perf2.counter_name))) "
				+ "AND (perf1.instance_name = perf2.instance_name)  AND (perf2.cntr_type = 1073939459)) "
				+ "ELSE perf1.cntr_value  END "
				+ "FROM master..sysperfinfo perf1 "
				+ "WHERE (perf1.cntr_type <> 1073939459)";
		// String queryStraa = "SELECT perf1.object_name, perf1.counter_name,
		// perf1.instance_name, 'value' = CASE perf1.cntr_type WHEN 537003008
		// THEN CONVERT(FLOAT,perf1.cntr_value) /(SELECT CASE perf2.cntr_value
		// WHEN 0 THEN 1 ELSE perf2.cntr_value END FROM master..sysperfinfo
		// perf2 WHERE (perf1.counter_name + ' '=
		// SUBSTRING(perf2.counter_name,1,PATINDEX('% base%',
		// perf2.counter_name))) AND (perf1.instance_name = perf2.instance_name)
		// AND (perf2.cntr_type = 1073939459)) ELSE perf1.cntr_value END " +
		// "FROM master..sysperfinfo perf1 " + "WHERE (perf1.cntr_type <>
		// 1073939459)";
		try {
			util = new JdbcUtil(dburl, username, password);
			// PersistenceServiceable service =
			// PersistenceService.getInstance();
			// tmpResult =
			// CommMonitOperate.persistService.executeQuery(queryStr, conn);
			tmpResult = service.executeQuery(queryStr, util.jdbc());
			// //System.out.println(tmpResult.length);

			for (int i = 1; i < tmpResult.length; i++) {
				// if(tmpResult[i][2].trim().equalsIgnoreCase("Info Errors"))
				// {
				// //System.out.println(tmpResult[i][1].trim());
				// //System.out.println(tmpResult[i][0].trim());
				// //System.out.println(tmpResult[i][3].trim());
				// //System.out.println(tmpResult[i][2].trim());
				// }

				/* 缓存击中率 */
				tmpDouble = 0;
				hitratiobase = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Buffer cache hit ratio")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					hitratio = tmpDouble;
					// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);
				}
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Buffer cache hit ratio base")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					hitratiobase = tmpDouble;
					// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);

				}
				try {
					if (hitratiobase != 0)
						bufferCacheHitRatio = formatDouble((hitratio / hitratiobase) * 100D);
				} catch (Exception ex) {

				}
				// plan Cache 命中率
				tmpDouble = 0;
				hitratiobase = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Plan Cache")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"cache hit ratio")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					hitratio = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Plan Cache")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"cache hit ratio base")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					hitratiobase = tmpDouble;
					// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);

				}
				try {
					if (hitratiobase != 0)
						planCacheHitRatio = formatDouble((hitratio / hitratiobase) * 100D);
				} catch (Exception ex) {

				}

				// cursor manager 命中率
				tmpDouble = 0;
				hitratiobase = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Cursor Manager by Type")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"cache hit ratio")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					hitratio = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Cursor Manager by Type")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"cache hit ratio base")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					hitratiobase = tmpDouble;
					// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);

				}
				try {
					if (hitratiobase != 0)
						cursorManagerByTypeHitRatio = formatDouble((hitratio / hitratiobase) * 100D);
				} catch (Exception ex) {

				}

				// catalogMetadata命中率
				tmpDouble = 0;
				hitratiobase = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Catalog Metadata")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Cache Hit Ratio")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					hitratio = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Catalog Metadata")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Cache Hit Ratio Base")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					hitratiobase = tmpDouble;
					// bufferCacheHitRatio = formatDouble(tmpDouble * 100D);

				}
				try {
					if (hitratiobase != 0)
						catalogMetadataHitRatio = formatDouble((hitratio / hitratiobase) * 100D);
				} catch (Exception ex) {

				}

				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:SQL Errors")
						&& tmpResult[i][1].trim()
								.equalsIgnoreCase("Errors/sec")
						&& tmpResult[i][2].trim().equalsIgnoreCase(
								"DB Offline Errors")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					dbOfflineErrors = tmpDouble;
				}
				tmpDouble = 0;
				//
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:SQL Errors")
						&& tmpResult[i][1].trim()
								.equalsIgnoreCase("Errors/sec")
						&& tmpResult[i][2].trim().equalsIgnoreCase(
								"Kill Connection Errors")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					killConnectionErrors = tmpDouble;
				}
				tmpDouble = 0;
				//
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:SQL Errors")
						&& tmpResult[i][1].trim()
								.equalsIgnoreCase("Errors/sec")
						&& tmpResult[i][2].trim().equalsIgnoreCase(
								"User Errors")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					userErrors = tmpDouble;
				}
				tmpDouble = 0;
				String ss = tmpResult[i][2];
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:SQL Errors")
						&& tmpResult[i][1].trim()
								.equalsIgnoreCase("Errors/sec")
						&& tmpResult[i][2].trim().equalsIgnoreCase(
								"Info Errors")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					infoErrors = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:SQL Errors")
						&& tmpResult[i][1].trim()
								.equalsIgnoreCase("Errors/sec")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					sqlServerErrors_total = tmpDouble;
				}

				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Cursor Manager by Type")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Cached Cursor Counts")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					cachedCursorCounts = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Cursor Manager by Type")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Cursor Cache Use Counts/sec")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					cursorCacheUseCounts = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Cursor Manager by Type")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Cursor Requests/sec")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					cursorRequests_total = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Cursor Manager by Type")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Active cursors")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					activeCursors = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Cursor Manager by Type")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Cursor memory usage")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					cursorMemoryUsage = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Cursor Manager by Type")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Cursor worktable usage")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					cursorWorktableUsage = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Cursor Manager by Type")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Number of active cursor plans")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					activeOfCursorPlans = tmpDouble;
				}

				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim()
								.equalsIgnoreCase("Lock waits")
						&& tmpResult[i][2].trim()
								.equalsIgnoreCase("平均等待时间(ms)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					pingjun_lockWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Memory grant queue waits")
						&& tmpResult[i][2].trim()
								.equalsIgnoreCase("平均等待时间(ms)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					pingjun_memoryGrantQueueWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Thread-safe momory objects waits")
						&& tmpResult[i][2].trim()
								.equalsIgnoreCase("平均等待时间(ms)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					pingjun_threadSafeMemoryObjectWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Log write waits")
						&& tmpResult[i][2].trim()
								.equalsIgnoreCase("平均等待时间(ms)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					pingjun_logWriteWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Log buffer waits")
						&& tmpResult[i][2].trim()
								.equalsIgnoreCase("平均等待时间(ms)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					pingjun_logBufferWaits = tmpDouble;
				}

				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Network IO waits")
						&& tmpResult[i][2].trim()
								.equalsIgnoreCase("平均等待时间(ms)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					pingjun_networkIOWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Page IO latch waits")
						&& tmpResult[i][2].trim()
								.equalsIgnoreCase("平均等待时间(ms)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					pingjun_pageIOLatchWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Page latch waits")
						&& tmpResult[i][2].trim()
								.equalsIgnoreCase("平均等待时间(ms)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					pingjun_pageLatchWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Non-Page latch waits")
						&& tmpResult[i][2].trim()
								.equalsIgnoreCase("平均等待时间(ms)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					pingjun_nonPageLatchWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Wait for the worker")
						&& tmpResult[i][2].trim()
								.equalsIgnoreCase("平均等待时间(ms)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					pingjun_waitForTheWorker = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Workspace synchronization waits")
						&& tmpResult[i][2].trim()
								.equalsIgnoreCase("平均等待时间(ms)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					pingjun_workspaceSynchronizationWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Transaction ownership waits")
						&& tmpResult[i][2].trim()
								.equalsIgnoreCase("平均等待时间(ms)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					pingjun_transactionOwnershipWaits = tmpDouble;
				}
				tmpDouble = 0;
				// /////////////////////////////////////////////

				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim()
								.equalsIgnoreCase("Lock waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("正在进行的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					jingxing_lockWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Memory grant queue waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("正在进行的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					jingxing_memoryGrantQueueWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Thread-safe momory objects waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("正在进行的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					jingxing_threadSafeMemoryObjectWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Log write waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("正在进行的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					jingxing_logWriteWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Log buffer waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("正在进行的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					jingxing_logBufferWaits = tmpDouble;
				}
				tmpDouble = 0;

				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Network IO waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("正在进行的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					jingxing_networkIOWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Page IO latch waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("正在进行的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					jingxing_pageIOLatchWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Page latch waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("正在进行的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					jingxing_pageLatchWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Non-Page latch waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("正在进行的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					jingxing_nonPageLatchWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Wait for the worker")
						&& tmpResult[i][2].trim().equalsIgnoreCase("正在进行的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					jingxing_waitForTheWorker = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Workspace synchronization waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("正在进行的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					jingxing_workspaceSynchronizationWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Transaction ownership waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("正在进行的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					jingxing_transactionOwnershipWaits = tmpDouble;
				}

				// //////////////////////////////////////
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim()
								.equalsIgnoreCase("Lock waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒启动的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					qidong_lockWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Memory grant queue waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒启动的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					qidong_memoryGrantQueueWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Thread-safe momory objects waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒启动的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					qidong_threadSafeMemoryObjectWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Log write waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒启动的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					qidong_logWriteWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Log buffer waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒启动的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					qidong_logBufferWaits = tmpDouble;
				}

				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Network IO waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒启动的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					qidong_networkIOWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Page IO latch waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒启动的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					qidong_pageIOLatchWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Page latch waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒启动的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					qidong_pageLatchWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Non-Page latch waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒启动的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					qidong_nonPageLatchWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Wait for the worker")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒启动的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					qidong_waitForTheWorker = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Workspace synchronization waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒启动的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					qidong_workspaceSynchronizationWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Transaction ownership waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒启动的等待数")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					qidong_transactionOwnershipWaits = tmpDouble;
				}
				// //////////////////////////////////////////////////////////////////////
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim()
								.equalsIgnoreCase("Lock waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒的累积等待时间")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					leiji_lockWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Memory grant queue waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒的累积等待时间")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					leiji_memoryGrantQueueWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Thread-safe momory objects waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒的累积等待时间")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					leiji_threadSafeMemoryObjectWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Log write waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒的累积等待时间")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					leiji_logWriteWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Log buffer waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒的累积等待时间")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					leiji_logBufferWaits = tmpDouble;
				}

				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Network IO waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒的累积等待时间")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					leiji_networkIOWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Page IO latch waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒的累积等待时间")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					leiji_pageIOLatchWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Page latch waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒的累积等待时间")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					leiji_pageLatchWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Non-Page latch waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒的累积等待时间")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					leiji_nonPageLatchWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Wait for the worker")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒的累积等待时间")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					leiji_waitForTheWorker = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Workspace synchronization waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒的累积等待时间")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					leiji_workspaceSynchronizationWaits = tmpDouble;
				}
				tmpDouble = 0;
				if (tmpResult[i][0].trim().equalsIgnoreCase(
						"SQLServer:Wait Statistics")
						&& tmpResult[i][1].trim().equalsIgnoreCase(
								"Transaction ownership waits")
						&& tmpResult[i][2].trim().equalsIgnoreCase("每秒的累积等待时间")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					leiji_transactionOwnershipWaits = tmpDouble;
				}

				/* 数据库页 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("Database pages")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					dbPages = tmpDouble;
				}
				/* 查找页数/分 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("Page lookups/sec")
						|| tmpResult[i][1].trim().equalsIgnoreCase(
								"Page Requests/sec")) {
					if (totalPageLookups == 0L) {
						totalPageLookups = new Double(tmpResult[i][3])
								.doubleValue();
						totalPageLookupsRate = 0.0D;
					} else {
						tmpdou = totalPageLookups;
						totalPageLookups = new Double(tmpResult[i][3])
								.doubleValue();
						totalPageLookupsRate = calculateRate(
								tmpAppPollInterval, totalPageLookups, tmpdou);
					}
				}
				/* 已读页数/分 */

				if (tmpResult[i][1].trim().equalsIgnoreCase("Page reads/sec")) {
					if (totalPageReads == 0) {
						totalPageReads = new Double(tmpResult[i][3])
								.doubleValue();
						totalPageReadsRate = 0.0D;
					} else {
						tmpdou = totalPageReads;
						totalPageReads = new Double(tmpResult[i][3])
								.doubleValue();
						totalPageReadsRate = calculateRate(tmpAppPollInterval,
								totalPageReads, tmpdou);
					}
				}
				/* 已写页数/分 */
				if (tmpResult[i][1].trim().equalsIgnoreCase("Page writes/sec")) {
					if (totalPageWrites == 0) {
						totalPageWrites = new Double(tmpResult[i][3])
								.doubleValue();
						totalPageWritesRate = 0.0D;
					} else {
						tmpdou = totalPageWrites;
						totalPageWrites = new Double(tmpResult[i][3])
								.doubleValue();
						totalPageWritesRate = calculateRate(tmpAppPollInterval,
								totalPageWrites, tmpdou);
					}
				}
				/* 总页数 */
				if (tmpResult[i][1].trim().equalsIgnoreCase("Total pages")) {
					tmpdou = new Double(tmpResult[i][3]).doubleValue();
					totalPages = tmpdou;
				}
				/* 空闲页 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("Free pages")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					freePages = tmpDouble;
				}
				/* 活动连接 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("User Connections")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					connections = tmpDouble;
				}
				/* 登录/分 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("Logins/sec")) {
					if (totalLogins == 0.0D) {
						totalLogins = new Double(tmpResult[i][3]).doubleValue();
						totalLoginsRate = 0.0D;
					} else {
						tmpDouble = totalLogins;
						totalLogins = new Double(tmpResult[i][3]).doubleValue();
						totalLoginsRate = calculateRate(tmpAppPollInterval,
								totalLogins, tmpDouble);
					}
				}
				/* 退出/分 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("Logouts/sec")) {
					if (totalLogouts == 0.0D) {
						totalLogouts = new Double(tmpResult[i][3])
								.doubleValue();
						totalLogoutsRate = 0.0D;
					} else {
						tmpDouble = totalLogouts;
						totalLogouts = new Double(tmpResult[i][3])
								.doubleValue();
						totalLogoutsRate = calculateRate(tmpAppPollInterval,
								totalLogouts, tmpDouble);
					}
				}
				/* 锁定请求/分 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim()
						.equalsIgnoreCase("Lock Requests/sec")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					if (lockRequests == 0.0D) {
						lockRequests = new Double(tmpResult[i][3])
								.doubleValue();
						lockRequestsRate = 0.0D;
					} else {
						tmpDouble = lockRequests;
						lockRequests = new Double(tmpResult[i][3])
								.doubleValue();
						lockRequestsRate = calculateRate(tmpAppPollInterval,
								lockRequests, tmpDouble);
					}
				}
				/* 锁定等待/分 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("Lock Waits/sec")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					if (lockWaits == 0.0D) {
						lockWaits = new Double(tmpResult[i][3]).doubleValue();
						lockWaitsRate = 0.0D;
					} else {
						tmpDouble = lockWaits;
						lockWaits = new Double(tmpResult[i][3]).doubleValue();
						lockWaitsRate = calculateRate(tmpAppPollInterval,
								lockWaits, tmpDouble);
					}
				}
				/* 锁定超时/分 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim()
						.equalsIgnoreCase("Lock Timeouts/sec")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					if (lockTimeouts == 0.0D) {
						lockTimeouts = new Double(tmpResult[i][3])
								.doubleValue();
						lockTimeoutsRate = 0.0D;
					} else {
						tmpDouble = lockTimeouts;
						lockTimeouts = new Double(tmpResult[i][3])
								.doubleValue();
						lockTimeoutsRate = calculateRate(tmpAppPollInterval,
								lockTimeouts, tmpDouble);
					}
				}
				/* 死锁数/分 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Number of Deadlocks/sec")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					deadLocks = tmpDouble;
					if (totalLogouts == 0.0D) {
						totalLogouts = new Double(tmpResult[i][3])
								.doubleValue();
						totalLogoutsRate = 0.0D;
					} else {
						tmpDouble = totalLogouts;
						totalLogouts = new Double(tmpResult[i][3])
								.doubleValue();
						totalLogoutsRate = calculateRate(tmpAppPollInterval,
								totalLogouts, tmpDouble);
					}
				}
				/* 平均锁定等待时间 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Average Wait Time (ms)")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					if (!tmpResult[i][3].equals("")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						avgWaitTime = formatDouble(tmpDouble);
					} else {
						avgWaitTime = 0;
					}
				}
				/* 锁定等待/分 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("Latch Waits/sec")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					latchWaits = tmpDouble;
					if (latchWaits == 0.0D) {
						latchWaits = new Double(tmpResult[i][3]).doubleValue();
						latchWaitsRate = 0.0D;
					} else {
						tmpDouble = latchWaits;
						latchWaits = new Double(tmpResult[i][3]).doubleValue();
						latchWaitsRate = calculateRate(tmpAppPollInterval,
								latchWaits, tmpDouble);
					}
				}
				/* 平均闭锁等待时间 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Average Latch Wait Time")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					avgLatchWait = formatDouble(tmpDouble);
				}
				/* 缓存击中率 */
				if (tmpResult[i][1].trim().equalsIgnoreCase("Cache Hit Ratio")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					cacheHitRatio = formatDouble(tmpDouble * 100D);
				}

				/* 缓存数 */
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Cache Object Counts")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					cacheCount = tmpDouble;
				}
				/* 缓存页 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("Cache Pages")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					cachePages = tmpDouble;
				}
				/* 使用的缓存/秒 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Cache Use Counts/sec")
						&& tmpResult[i][2].trim().equalsIgnoreCase("_Total")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					cacheUsed = tmpDouble;
					if (cacheUsed == 0.0D) {
						cacheUsed = new Double(tmpResult[i][3]).doubleValue();
						cacheUsedRate = 0.0D;
					} else {
						tmpDouble = cacheUsed;
						cacheUsed = new Double(tmpResult[i][3]).doubleValue();
						cacheUsedRate = calculateRate(tmpAppPollInterval,
								cacheUsed, tmpDouble);
					}
				}
				/* 内存总数 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Total Server Memory (KB)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					totalMemory = tmpDouble;
				}
				/* SQL缓存存储 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"SQL Cache Memory (KB)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					sqlMem = tmpDouble;
				}
				/* 内存优化 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Optimizer Memory (KB)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					optMemory = tmpDouble;
				}
				/* 内存分配未决 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Memory Grants Pending")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					memGrantPending = tmpDouble;
				}
				/* 内存分配成功 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Memory Grants Outstanding")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					memGrantPending = tmpDouble;
				}
				/* 锁定内存 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("Lock Memory (KB)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					lockMem = tmpDouble;
				}
				/* 连接内存 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Connection Memory (KB)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					conMemory = tmpDouble;
				}
				/* 产生的工作空间内存 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Granted Workspace Memory (KB)")) {
					tmpDouble = new Double(tmpResult[i][3]).doubleValue();
					grantedWorkspaceMem = tmpDouble;
				}
				// //System.out.println("cacheHitRatio--"+cacheHitRatio+"cacheCount--"+cacheCount+"totalMemory--"+totalMemory);
				/* 批请求/秒 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Batch Requests/sec")) {
					if (batchRequests == 0.0D) {
						batchRequests = new Double(tmpResult[i][3])
								.doubleValue();
						batchRequestsRate = 0.0D;
					} else {
						tmpDouble = batchRequests;
						batchRequests = new Double(tmpResult[i][3])
								.doubleValue();
						batchRequestsRate = calculateRate(tmpAppPollInterval,
								batchRequests, tmpDouble);
					}
				}
				/* SQL编辑/秒 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"SQL Compilations/sec")) {
					if (sqlCompilations == 0.0D) {
						sqlCompilations = new Double(tmpResult[i][3])
								.doubleValue();
						sqlCompilationsRate = 0.0D;
					} else {
						tmpDouble = sqlCompilations;
						sqlCompilations = new Double(tmpResult[i][3])
								.doubleValue();
						sqlCompilationsRate = calculateRate(tmpAppPollInterval,
								sqlCompilations, tmpDouble);
					}
				}
				/* SQL重编辑/秒 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"SQL Re-Compilations/sec")) {
					if (sqlRecompilation == 0.0D) {
						sqlRecompilation = new Double(tmpResult[i][3])
								.doubleValue();
						sqlRecompilationRate = 0.0D;
					} else {
						tmpDouble = sqlRecompilation;
						sqlRecompilation = new Double(tmpResult[i][3])
								.doubleValue();
						sqlRecompilationRate = calculateRate(
								tmpAppPollInterval, sqlRecompilation, tmpDouble);
					}
				}
				/* 自动参数化尝试次数/秒 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Auto-Param Attempts/sec")) {
					if (autoParams == 0.0D) {
						autoParams = new Double(tmpResult[i][3]).doubleValue();
						autoParamsRate = 0.0D;
					} else {
						tmpDouble = autoParams;
						autoParams = new Double(tmpResult[i][3]).doubleValue();
						autoParamsRate = calculateRate(tmpAppPollInterval,
								autoParams, tmpDouble);
					}
				}
				/* 自动参数化失败次数/秒 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase(
						"Auto-Param Attempts/sec")) {
					if (failedAutoParams == 0.0D) {
						failedAutoParams = new Double(tmpResult[i][3])
								.doubleValue();
						failedAutoParamsRate = 0.0D;
					} else {
						tmpDouble = failedAutoParams;
						failedAutoParams = new Double(tmpResult[i][3])
								.doubleValue();
						failedAutoParamsRate = calculateRate(
								tmpAppPollInterval, failedAutoParams, tmpDouble);
					}
				}
				/* 完全扫描/分 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("Full Scans/sec")) {
					if (fullScans == 0.0D) {
						fullScans = new Double(tmpResult[i][3]).doubleValue();
						fullScansRate = 0.0D;
					} else {
						tmpDouble = fullScans;
						fullScans = new Double(tmpResult[i][3]).doubleValue();
						fullScansRate = calculateRate(tmpAppPollInterval,
								fullScans, tmpDouble);
					}
				}
				/* 范围扫描/分 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("Range Scans/sec")) {
					if (rangeScans == 0.0D) {
						rangeScans = new Double(tmpResult[i][3]).doubleValue();
						rangeScansRate = 0.0D;
					} else {
						tmpDouble = rangeScans;
						rangeScans = new Double(tmpResult[i][3]).doubleValue();
						rangeScansRate = calculateRate(tmpAppPollInterval,
								rangeScans, tmpDouble);
					}
				}
				/* 探针扫描/分 */
				tmpDouble = 0;
				if (tmpResult[i][1].trim().equalsIgnoreCase("Probe Scans/sec")) {
					if (probeScans == 0.0D) {
						probeScans = new Double(tmpResult[i][3]).doubleValue();
						probeScansRate = 0.0D;
					} else {
						tmpDouble = probeScans;
						probeScans = new Double(tmpResult[i][3]).doubleValue();
						probeScansRate = calculateRate(tmpAppPollInterval,
								probeScans, tmpDouble);
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			util.closeStmt();
			util.closeConn();

		}

		/* 未取到"_Total"库的值则取其他库的总和，锁明细，缓存管理统计 */
		if (cacheHitRatio == 0.0D) {

			queryStr = "select counter_name ,sum(cntr_value) from sysperfinfo "
					+ "where object_name in ('SQLServer:Locks','SQLServer:Cache Manager') "
					+ "and instance_name!='_Total' group by counter_name";
			try {
				util = new JdbcUtil(dburl, username, password);
				String[][] tmpResult2 = service.executeQuery(queryStr, util
						.jdbc());
				for (int i = 1; i < tmpResult2.length; i++) {
					/* 锁定请求/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Requests/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						lockRequests = tmpDouble;
					}
					/* 锁定等待/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Waits/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						lockWaits = tmpDouble;
					}
					/* 锁定超时/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Lock Timeouts/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						lockTimeouts = tmpDouble;
					}
					/* 死锁数/分 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Number of Deadlocks/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						deadLocks = tmpDouble;
					}
					/* 平均锁定等待时间 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Average Wait Time (ms)")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						avgWaitTime = formatDouble(tmpDouble);
					}
					/* 缓存击中率 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Hit Ratio")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheHitRatio = formatDouble(tmpDouble * 100D);
					}
					/* 缓存数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Object Counts")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheCount = tmpDouble;
					}
					/* 缓存页 */
					if (tmpResult[i][1].trim().equalsIgnoreCase("Cache Pages")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cachePages = tmpDouble;
					}
					/* 使用的缓存/秒 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Use Counts/sec")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheUsed = tmpDouble;
					}
					/* 平均锁定等待时间基数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Average Wait Time Base")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						avgWaitTimeBase = tmpDouble;
					}
					/* 缓存击中率基数 */
					if (tmpResult[i][1].trim().equalsIgnoreCase(
							"Cache Hit Ratio Base")) {
						tmpDouble = new Double(tmpResult[i][3]).doubleValue();
						cacheHitRatioBase = tmpDouble;
					}

					if (cacheHitRatio != 0.0D)
						cacheHitRatio = calculateValue(cacheHitRatio,
								cacheHitRatioBase, true);
					if (avgWaitTime != 0.0D)
						avgWaitTime = calculateValue(avgWaitTime,
								avgWaitTimeBase, false);
				}
				if (tmpResult2 != null)
					tmpResult2 = null;
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				util.closeStmt();
				util.closeConn();
			}
		}
		Hashtable retValue = new Hashtable();
		/* 缓存管理统计 */
		Hashtable pages = new Hashtable();
		pages.put("bufferCacheHitRatio", bufferCacheHitRatio + ""); // *$ 缓存击中率
		pages.put("planCacheHitRatio", planCacheHitRatio + ""); // *$ plan
		// cache击中率
		pages.put("cursorManagerByTypeHitRatio", cursorManagerByTypeHitRatio
				+ ""); // *$ Cursor Manager by Type击中率
		pages.put("catalogMetadataHitRatio", catalogMetadataHitRatio + ""); // *$
		// Catalog
		// Metadata击中率

		pages.put("dbOfflineErrors", dbOfflineErrors + "");
		pages.put("killConnectionErrors", killConnectionErrors + "");
		pages.put("userErrors", userErrors + "");
		pages.put("infoErrors", infoErrors + "");
		pages.put("sqlServerErrors_total", sqlServerErrors_total + "");

		pages.put("cachedCursorCounts", cachedCursorCounts + "");
		pages.put("cursorCacheUseCounts", cursorCacheUseCounts + "");
		pages.put("cursorRequests_total", cursorRequests_total + "");
		pages.put("activeCursors", activeCursors + "");
		pages.put("cursorMemoryUsage", cursorMemoryUsage + "");
		pages.put("cursorWorktableUsage", cursorWorktableUsage + "");
		pages.put("activeOfCursorPlans", activeOfCursorPlans + "");

		pages.put("dbPages", dbPages + "");// * 数据库页
		pages.put("totalPageLookups", totalPageLookups + "");// 查找页数
		pages.put("totalPageLookupsRate", totalPageLookupsRate + "");// *
		// 查找页数/分
		pages.put("totalPageReads", totalPageReads + "");// 已读页数
		pages.put("totalPageReadsRate", totalPageReadsRate + ""); // * 已读页数/分
		pages.put("totalPageWrites", totalPageWrites + "");// 已写页数
		pages.put("totalPageWritesRate", totalPageWritesRate + "");// * 已写页数/分
		pages.put("totalPages", totalPages + "");// * 总页数
		pages.put("freePages", freePages + "");// * 空闲页

		Hashtable statisticsHash = new Hashtable();
		statisticsHash.put("pingjun_lockWaits", pingjun_lockWaits + "");
		statisticsHash.put("pingjun_memoryGrantQueueWaits",
				pingjun_memoryGrantQueueWaits + "");
		statisticsHash.put("pingjun_threadSafeMemoryObjectWaits",
				pingjun_threadSafeMemoryObjectWaits + "");
		statisticsHash.put("pingjun_logWriteWaits", pingjun_logWriteWaits + "");
		statisticsHash.put("pingjun_logBufferWaits", pingjun_logBufferWaits
				+ "");
		statisticsHash.put("pingjun_networkIOWaits", pingjun_networkIOWaits
				+ "");
		statisticsHash.put("pingjun_pageIOLatchWaits", pingjun_pageIOLatchWaits
				+ "");
		statisticsHash.put("pingjun_pageLatchWaits", pingjun_pageLatchWaits
				+ "");
		statisticsHash.put("pingjun_nonPageLatchWaits",
				pingjun_nonPageLatchWaits + "");
		statisticsHash.put("pingjun_waitForTheWorker", pingjun_waitForTheWorker
				+ "");
		statisticsHash.put("pingjun_workspaceSynchronizationWaits",
				pingjun_workspaceSynchronizationWaits + "");
		statisticsHash.put("pingjun_transactionOwnershipWaits",
				pingjun_transactionOwnershipWaits + "");

		statisticsHash.put("jingxing_lockWaits", jingxing_lockWaits + "");
		statisticsHash.put("jingxing_memoryGrantQueueWaits",
				jingxing_memoryGrantQueueWaits + "");
		statisticsHash.put("jingxing_threadSafeMemoryObjectWaits",
				jingxing_threadSafeMemoryObjectWaits + "");
		statisticsHash.put("jingxing_logWriteWaits", jingxing_logWriteWaits
				+ "");
		statisticsHash.put("jingxing_logBufferWaits", jingxing_logBufferWaits
				+ "");
		statisticsHash.put("jingxing_networkIOWaits", jingxing_networkIOWaits
				+ "");
		statisticsHash.put("jingxing_pageIOLatchWaits",
				jingxing_pageIOLatchWaits + "");
		statisticsHash.put("jingxing_pageLatchWaits", jingxing_pageLatchWaits
				+ "");
		statisticsHash.put("jingxing_nonPageLatchWaits",
				jingxing_nonPageLatchWaits + "");
		statisticsHash.put("jingxing_waitForTheWorker",
				jingxing_waitForTheWorker + "");
		statisticsHash.put("jingxing_workspaceSynchronizationWaits",
				jingxing_workspaceSynchronizationWaits + "");
		statisticsHash.put("jingxing_transactionOwnershipWaits",
				jingxing_transactionOwnershipWaits + "");

		statisticsHash.put("qidong_lockWaits", qidong_lockWaits + "");
		statisticsHash.put("qidong_memoryGrantQueueWaits",
				qidong_memoryGrantQueueWaits + "");
		statisticsHash.put("qidong_threadSafeMemoryObjectWaits",
				qidong_threadSafeMemoryObjectWaits + "");
		statisticsHash.put("qidong_logWriteWaits", qidong_logWriteWaits + "");
		statisticsHash.put("qidong_logBufferWaits", qidong_logBufferWaits + "");
		statisticsHash.put("qidong_networkIOWaits", qidong_networkIOWaits + "");
		statisticsHash.put("qidong_pageIOLatchWaits", qidong_pageIOLatchWaits
				+ "");
		statisticsHash.put("qidong_pageLatchWaits", qidong_pageLatchWaits + "");
		statisticsHash.put("qidong_nonPageLatchWaits", qidong_nonPageLatchWaits
				+ "");
		statisticsHash.put("qidong_waitForTheWorker", qidong_waitForTheWorker
				+ "");
		statisticsHash.put("qidong_workspaceSynchronizationWaits",
				qidong_workspaceSynchronizationWaits + "");
		statisticsHash.put("qidong_transactionOwnershipWaits",
				qidong_transactionOwnershipWaits + "");

		statisticsHash.put("leiji_lockWaits", leiji_lockWaits + "");
		statisticsHash.put("leiji_memoryGrantQueueWaits",
				leiji_memoryGrantQueueWaits + "");
		statisticsHash.put("leiji_threadSafeMemoryObjectWaits",
				leiji_threadSafeMemoryObjectWaits + "");
		statisticsHash.put("leiji_logWriteWaits", leiji_logWriteWaits + "");
		statisticsHash.put("leiji_logBufferWaits", leiji_logBufferWaits + "");
		statisticsHash.put("leiji_networkIOWaits", leiji_networkIOWaits + "");
		statisticsHash.put("leiji_pageIOLatchWaits", leiji_pageIOLatchWaits
				+ "");
		statisticsHash.put("leiji_pageLatchWaits", leiji_pageLatchWaits + "");
		statisticsHash.put("leiji_nonPageLatchWaits", leiji_nonPageLatchWaits
				+ "");
		statisticsHash.put("leiji_waitForTheWorker", leiji_waitForTheWorker
				+ "");
		statisticsHash.put("leiji_workspaceSynchronizationWaits",
				leiji_workspaceSynchronizationWaits + "");
		statisticsHash.put("leiji_transactionOwnershipWaits",
				leiji_transactionOwnershipWaits + "");

		/* 数据库页连接统计 */
		Hashtable conns = new Hashtable();
		conns.put("connections", connections + ""); // * 活动连接
		conns.put("totalLogins", totalLogins + "");// 登录
		conns.put("totalLoginsRate", totalLoginsRate + "");// * 登录/分
		conns.put("totalLogouts", totalLogouts + "");// 退出
		conns.put("totalLogoutsRate", totalLogoutsRate + ""); // * 退出/分

		/* 锁明细 */
		Hashtable locks = new Hashtable();
		locks.put("lockRequests", lockRequests + ""); // 锁定请求
		locks.put("lockRequestsRate", lockRequestsRate + "");// *$ 锁定请求/分
		locks.put("lockWaits", lockWaits + "");// 锁定等待
		locks.put("lockWaitsRate", lockWaitsRate + "");// * 锁定等待/分
		locks.put("lockTimeouts", lockTimeouts + ""); // 锁定超时
		locks.put("lockTimeoutsRate", lockTimeoutsRate + ""); // * 锁定超时/分
		locks.put("deadLocks", deadLocks + "");// 死锁数
		locks.put("deadLocksRate", deadLocksRate + "");// * 死锁数/分
		locks.put("avgWaitTime", avgWaitTime + "");// * 平均锁定等待时间
		locks.put("avgWaitTimeBase", avgWaitTimeBase + ""); // 平均锁定等待时间计算的基数
		/* 闭锁的明细 */
		locks.put("latchWaits", latchWaits + "");// 闭锁等待
		locks.put("latchWaitsRate", latchWaitsRate + "");// * 闭锁等待/分
		locks.put("avgLatchWait", avgLatchWait + ""); // *$ 平均闭锁等待时间;

		/* 缓存明细 */
		Hashtable caches = new Hashtable();
		caches.put("cacheHitRatio", cacheHitRatio + ""); // *$ 缓存击中率
		caches.put("cacheHitRatioBase", cacheHitRatioBase + "");// 缓存击中率计算的基数
		caches.put("cacheCount", cacheCount + "");// * 缓存数
		caches.put("cachePages", cachePages + "");// * 缓存页
		caches.put("cacheUsed", cacheUsed + ""); // 使用的缓存
		caches.put("cacheUsedRate", cacheUsedRate + ""); // * 使用的缓存/分

		/* 内存利用情况 */
		Hashtable mems = new Hashtable();
		mems.put("totalMemory", totalMemory + ""); // *$ 内存总数
		mems.put("sqlMem", sqlMem + ""); // *$ SQL缓存存储
		mems.put("optMemory", optMemory + "");// *$ 内存优化
		mems.put("memGrantPending", memGrantPending + "");// * 内存分配未决
		mems.put("memGrantSuccess", memGrantSuccess + ""); // * 内存分配成功
		mems.put("lockMem", lockMem + ""); // *$ 锁定内存
		mems.put("conMemory", conMemory + "");// *$ 连接内存
		mems.put("grantedWorkspaceMem", grantedWorkspaceMem + ""); // *
		// 产生的工作空间内存

		/* SQL统计 */
		Hashtable sqls = new Hashtable();
		sqls.put("batchRequests", batchRequests + "" + ""); // 批请求
		sqls.put("batchRequestsRate", batchRequestsRate + "");// *$ 批请求/分
		sqls.put("sqlCompilations", sqlCompilations + "");// SQL编辑
		sqls.put("sqlCompilationsRate", sqlCompilationsRate + "");// * SQL编辑/分
		sqls.put("sqlRecompilation", sqlRecompilation + "");// //SQL重编辑
		sqls.put("sqlRecompilationRate", sqlRecompilationRate + ""); // *
		// SQL重编辑/分
		sqls.put("autoParams", autoParams + "");// 自动参数化尝试次数
		sqls.put("autoParamsRate", autoParamsRate + "");// * 自动参数化尝试次数/分
		sqls.put("failedAutoParams", failedAutoParams + "");// 自动参数化失败次数
		sqls.put("failedAutoParamsRate", failedAutoParamsRate + "");// *
		// 自动参数化失败次数/分

		/* 访问方法的明细 */
		Hashtable scans = new Hashtable();
		scans.put("fullScans", fullScans + ""); // 完全扫描
		scans.put("fullScansRate", fullScansRate + "");// *$ 完全扫描/分
		scans.put("rangeScans", rangeScans + "");// 范围扫描
		scans.put("rangeScansRate", rangeScansRate + "");// * 范围扫描/分
		scans.put("probeScans", probeScans + "");// 探针扫描
		scans.put("probeScansRate", probeScansRate + ""); // * 探针扫描/分
		/*
		 * Vector altfiles_v = new Vector(); try{ altfiles_v =
		 * getSqlserverAltfile(ip,username,password); }catch(Exception e){
		 * e.printStackTrace(); } Vector lockinfo_v = new Vector(); try{
		 * lockinfo_v = getSqlserverLockinfo(ip,username,password);
		 * }catch(Exception e){ e.printStackTrace(); } Vector process_v = new
		 * Vector(); try{ process_v =
		 * getSqlserverProcesses(ip,username,password); }catch(Exception e){
		 * e.printStackTrace(); }
		 * 
		 * Hashtable sysValue = new Hashtable(); try{ sysValue =
		 * getSqlServerSys(ip,username,password); }catch(Exception e){
		 * e.printStackTrace(); }
		 */
		retValue.put("pages", pages);
		retValue.put("conns", conns);
		retValue.put("locks", locks);
		retValue.put("caches", caches);
		retValue.put("mems", mems);
		retValue.put("sqls", sqls);
		retValue.put("scans", scans);
		retValue.put("statisticsHash", statisticsHash);
		return retValue;

	}

	/* 对于double型的数字，定其精度为小数点后四位小数 */
	public double formatDouble(double d) {
		String doubleStr = d + "";
		doubleStr = doubleStr.trim();
		int length = doubleStr.length();
		double doubleReturnd = 0.0D;
		if (doubleStr.indexOf(".") > -1) {
			if (length > doubleStr.indexOf(".") + 4)
				try {
					doubleStr = doubleStr.substring(0,
							doubleStr.indexOf(".") + 4);
					doubleReturnd = Double.parseDouble(doubleStr);
				} catch (Exception exception) {
					exception.printStackTrace();
					return doubleReturnd;
				}
			else
				try {
					doubleReturnd = Double.parseDouble(doubleStr);
				} catch (Exception exception1) {
					exception1.printStackTrace();
					return doubleReturnd;
				}
		} else {
			try {
				doubleReturnd = Double.parseDouble(doubleStr);
			} catch (Exception exception2) {
				exception2.printStackTrace();
				return doubleReturnd;
			}
		}
		return doubleReturnd;
	}

	/* 计算传入参数特定意义的百分比形式（命中率等） */
	public double calculateValue(double value, double valueBase, boolean flag) {
		double valueReturn = 0.0D;
		try {
			double valueD = value;
			double valueDBase = valueBase;
			if (valueDBase <= 0.0D)
				valueDBase = 1.0D;

			valueReturn = valueD / valueDBase;

			if (flag)
				valueReturn *= 100D;
			valueReturn = formatDouble(valueReturn);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return valueReturn;
	}

	/* 计算一段时间内某值的变化率(/分) */
	public double calculateRate(long timespan, double currentVal,
			double formerVal) {
		double rate = 0.0D;
		if (timespan < 60L)
			timespan = 60L;
		if (currentVal <= formerVal)
			rate = 0.0D;
		else
			rate = (currentVal - formerVal) / (double) (timespan / 60L);
		return rate;
	}

	public long getTaskInterval() {
		TaskXml taskxml = new TaskXml();
		List list = taskxml.ListXml();
		Task task = new Task();
		long retValue = 300;
		try {
			for (int i = 0; i < list.size(); i++) {
				BeanUtils.copyProperties(task, list.get(i));
				if (task.getTaskname().equalsIgnoreCase("dbtask")) {
					break;
				}
			}
			if (task.getPolltimeunit().equalsIgnoreCase("m")) {
				// 分钟
				retValue = task.getPolltime().longValue() * 60;
			} else if (task.getPolltimeunit().equalsIgnoreCase("h")) {
				// 小时
				retValue = task.getPolltime().longValue() * 60 * 60;
			} else if (task.getPolltimeunit().equalsIgnoreCase("d")) {
				// 天
				retValue = task.getPolltime().longValue() * 60 * 60 * 24;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return retValue;

	}

	/*
	 * ############################################ add by hukelei 2010-10-9
	 * ############################################
	 */

	public Hashtable getMYSQLData(String ip, String username, String password,
			String db, Hashtable gatherhash) throws Exception {
		String basePath = ""; // 基本路径
		String dataPath = ""; // 数据路径
		String logerrorPath = ""; // 数据路径
		String version = ""; // 数据库版本
		String hostOS = ""; // 服务器操作系统
		String maxconnections = ""; // 最大连接数
		String buffersize = ""; // 内存
		List tablesDetail = new ArrayList(); // 表明细
		List sessionsDetail = new ArrayList(); // 当前连接
		Hashtable returnVal = new Hashtable();
		Hashtable configVal = new Hashtable();
		Vector variablesVector = new Vector();
		Vector scanVector = new Vector();
		Vector statusVector = new Vector();
		Vector opentableVector = new Vector();
		Vector tmptableVal = new Vector();
		Vector tmptableCreateVal = new Vector();
		Vector handlerreadVal = new Vector();

		Hashtable allmysqldata = new Hashtable();

		Vector Val = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show variables;";
		MySqlJdbcUtil util = null;
		ResultSet rs = null;

		try {
			// 获取MYSQL的配置信息,表明细,当前连接信息
			try {
				util = new MySqlJdbcUtil(dburl, username, password);
				util.jdbc();

				if (gatherhash.containsKey("config")) {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						if (rs.getString("variable_name").equals("basedir"))
							basePath = rs.getString("value");
						else if (rs.getString("variable_name")
								.equals("datadir"))
							dataPath = rs.getString("value");
						else if (rs.getString("variable_name").equals(
								"log_error"))
							logerrorPath = rs.getString("value");
						else if (rs.getString("variable_name")
								.equals("version"))
							version = rs.getString("value");
						else if (rs.getString("variable_name").equals(
								"version_compile_os"))
							hostOS = rs.getString("value");
						else if (rs.getString("variable_name").equals(
								"max_connections"))
							maxconnections = rs.getString("value");
						else if (rs.getString("variable_name").equals(
								"key_buffer_size"))
							buffersize = rs.getString("value");
					}
					rs.close();
					configVal.put("basePath", basePath);
					configVal.put("dataPath", dataPath);
					configVal.put("logerrorPath", logerrorPath);
					configVal.put("version", version);
					configVal.put("hostOS", hostOS);
					configVal.put("maxconnections", maxconnections);
					configVal.put("buffersize", buffersize);
				}

				if (gatherhash.containsKey("tablestatus")) {
					rs = util.stmt.executeQuery("show table status from " + db);
					tablesDetail = new ArrayList();
					while (rs.next()) {
						try {
							String[] item = new String[4];
							item[0] = rs.getString("name"); // 表名
							item[1] = rs.getString("rows"); // 表行数
							item[2] = rs.getLong("data_length") / 1024 + " k"; // 表大小
							item[3] = rs.getString("create_time").substring(0,
									16); // 创建时间
							// SysLogger.info(item[0]+"===="+item[1]);
							tablesDetail.add(item);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					rs.close();
				}

				sessionsDetail = new ArrayList();
				if (gatherhash.containsKey("process")) {
					rs = util.stmt.executeQuery("show processlist");
					while (rs.next()) {
						if (!db.equals(rs.getString("db")))
							continue;

						String[] item = new String[5];
						item[0] = rs.getString("user"); // 用户
						item[1] = rs.getString("host"); // 主机
						item[2] = rs.getString("command"); // 命令
						item[3] = rs.getString("time") + " s"; // 连接时间
						item[4] = rs.getString("db"); // 数据库名
						sessionsDetail.add(item);
					}
					rs.close();
				}

				ResultSetMetaData rsmd = null;
				if (gatherhash.containsKey("maxusedconnect")) {
					rs = util.stmt
							.executeQuery("show global status like 'Max_used_connections'");
					rsmd = rs.getMetaData();
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						Val.add(return_value);
					}
					rs.close();
				}

				// 表锁情况
				if (gatherhash.containsKey("lock")) {
					rs = util.stmt
							.executeQuery("show global status like 'table_locks%'");
					rsmd = rs.getMetaData();
					// Vector Val = new Vector();
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						Val.add(return_value);
					}
					rs.close();
				}

				if (gatherhash.containsKey("keyread")) {
					rs = util.stmt
							.executeQuery("show global status like 'key_read%'");
					rsmd = rs.getMetaData();
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						Val.add(return_value);
					}
					rs.close();
				}

				// 慢查询
				if (gatherhash.containsKey("slow")) {
					rs = util.stmt.executeQuery("show variables like '%slow%'");
					rsmd = rs.getMetaData();
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						Val.add(return_value);
					}
					rs.close();
				}

				// 进程使用情况
				if (gatherhash.containsKey("thread")) {
					rs = util.stmt
							.executeQuery("show global status like 'Thread%'");
					rsmd = rs.getMetaData();
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						Val.add(return_value);
					}
					rs.close();
				}

				// 表打开情况
				if (gatherhash.containsKey("opentable")) {
					rs = util.stmt
							.executeQuery("show global status like 'open%tables%'");
					rsmd = rs.getMetaData();
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						Val.add(return_value);
					}
					rs.close();
				}

				// 索引扫描
				if (gatherhash.containsKey("handlerread")) {
					rs = util.stmt
							.executeQuery("show global status like 'handler_read%'");
					rsmd = rs.getMetaData();
					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						Val.add(return_value);
					}
					rs.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null)
					rs.close();
			}
			returnVal.put("configVal", configVal);
			returnVal.put("tablesDetail", tablesDetail);
			returnVal.put("sessionsDetail", sessionsDetail);
			returnVal.put("Val", Val);

			// 变量信息
			if (gatherhash.containsKey("variables")) {
				sql = "show variables;";
				try {
					util = new MySqlJdbcUtil(dburl, username, password);
					util.jdbc();
					rs = util.stmt.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();

					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						variablesVector.addElement(return_value);
						return_value = null;
					}
					// rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
			}

			// 状态信息
			if (gatherhash.containsKey("status")) {
				sql = "show global status;";
				String variable_name_trans = null;
				try {
					util = new MySqlJdbcUtil(dburl, username, password);
					util.jdbc();
					rs = util.stmt.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();

					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");

						}
						statusVector.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
			}

			// 扫描情况
			if (gatherhash.containsKey("scan")) {
				sql = "show global status like 'handler_read%'; ";
				try {
					util = new MySqlJdbcUtil(dburl, username, password);
					util.jdbc();
					rs = util.stmt.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();

					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						scanVector.addElement(return_value);
						return_value = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
			}

			// 打开的表的使用情况
			if (gatherhash.containsKey("opentable")) {
				sql = "show global status like 'open%tables%'; ";
				try {
					util = new MySqlJdbcUtil(dburl, username, password);
					util.jdbc();
					rs = util.stmt.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();

					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						opentableVector.addElement(return_value);
						return_value = null;
					}
					// rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
			}

			// 创建的临时表信息
			if (gatherhash.containsKey("createdtmp")) {
				sql = "show global status like 'created_tmp%';";
				try {
					util = new MySqlJdbcUtil(dburl, username, password);
					util.jdbc();
					rs = util.stmt.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();

					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						tmptableVal.addElement(return_value);
						return_value = null;
					}
					// rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
			}

			// 服务器对临时表创建信息
			if (gatherhash.containsKey("tmptable")) {
				sql = "Show variables where Variable_name in ('tmp_table_size', 'max_heap_table_size');";
				try {
					util = new MySqlJdbcUtil(dburl, username, password);
					util.jdbc();
					rs = util.stmt.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();

					while (rs.next()) {
						Hashtable return_value = new Hashtable();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							String col = rsmd.getColumnName(i);
							if (rs.getString(i) != null) {
								String tmp = rs.getString(i).toString();
								return_value.put(col.toLowerCase(), tmp);
							} else
								return_value.put(col.toLowerCase(), "--");
						}
						tmptableCreateVal.addElement(return_value);
						return_value = null;
					}
					// rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
			}

		} catch (Exception e) {

		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}

		// allmysqldata.put("returnVal", returnVal);
		returnVal.put("variables", variablesVector);
		returnVal.put("global_status", statusVector);
		returnVal.put("dispose3", scanVector);
		returnVal.put("dispose2", opentableVector);// 打开表使用情况
		returnVal.put("dispose", tmptableVal); // 临时表创建情况
		returnVal.put("dispose1", tmptableCreateVal);

		return returnVal;

	}

	/**
	 * 判断是否连接到所监控的数据库 是则返回true
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public boolean getMySqlIsOk(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		// //System.out.println(dburl);
		String sql = "show variables;";
		MySqlJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
			try {
				util.closeStmt();
				util.closeConn();
			} catch (Exception ep) {
			}
		}
	}

	/**
	 * 显示服务器可以连接的最大连接数
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getMySqlPerfom(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show variables like 'max_connections';";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * 用户正在使用的响应的连接数
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getMySqlPerfom1(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show global status like 'Max_used_connections';";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * 产看key_buffer_size内存分配情况
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getMySqlPerfom2(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show variables like 'key_buffer_size'; ";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * 表锁情况
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getMySqlPerfom3(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show global status like 'table_locks%';";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * 查看key_buffer_size内存分配情况
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getMySqlPerfom4(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show global status like 'key_read%'; ";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * 慢查询
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getMySqlPerfom5(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show variables like '%slow%';";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * 进程使用情况
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getMySqlPerfom6(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show global status like 'Thread%'; ";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * 创建临时表的信息
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getDispose(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show global status like 'created_tmp%';";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * 服务器对临时表配置的信息
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getDispose1(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "Show variables where Variable_name in ('tmp_table_size', 'max_heap_table_size');";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * open table使用情况
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getDispose2(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show global status like 'open%tables%'; ";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * 扫描情况
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getDispose3(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show global status like 'handler_read%'; ";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * status信息
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getStatus(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String sql = null;
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		sql = "show global status;";
		MySqlJdbcUtil util = null;
		String variable_name_trans = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");

				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * Variables信息
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Vector getVariables(String ip, String username, String password,
			String db) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show variables;";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				returnVal.addElement(return_value);
				return_value = null;
			}
			// rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return returnVal;
	}

	/**
	 * 获取MYSQL的配置信息,表明细,当前连接信息
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Hashtable getMySqlDBConfig(String ip, String username,
			String password, String db) throws Exception {
		String basePath = ""; // 基本路径
		String dataPath = ""; // 数据路径
		String logerrorPath = ""; // 数据路径
		String version = ""; // 数据库版本
		String hostOS = ""; // 服务器操作系统
		String maxconnections = ""; // 最大连接数
		String buffersize = ""; // 内存
		List tablesDetail = new ArrayList(); // 表明细
		List sessionsDetail = new ArrayList(); // 当前连接
		Hashtable returnVal = new Hashtable();
		Hashtable configVal = new Hashtable();
		Vector Val = new Vector();
		String dburl = "jdbc:mysql://"
				+ ip
				+ "/"
				+ db
				+ "?user="
				+ username
				+ "&password="
				+ password
				+ "&useUnicode=true&&characterEncoding=gbk&autoReconnect = true";
		String sql = "show variables;";
		MySqlJdbcUtil util = null;
		try {
			util = new MySqlJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString("variable_name").equals("basedir"))
					basePath = rs.getString("value");
				else if (rs.getString("variable_name").equals("datadir"))
					dataPath = rs.getString("value");
				else if (rs.getString("variable_name").equals("log_error"))
					logerrorPath = rs.getString("value");
				else if (rs.getString("variable_name").equals("version"))
					version = rs.getString("value");
				else if (rs.getString("variable_name").equals(
						"version_compile_os"))
					hostOS = rs.getString("value");
				else if (rs.getString("variable_name")
						.equals("max_connections"))
					maxconnections = rs.getString("value");
				else if (rs.getString("variable_name")
						.equals("key_buffer_size"))
					buffersize = rs.getString("value");
			}
			rs.close();
			configVal.put("basePath", basePath);
			configVal.put("dataPath", dataPath);
			configVal.put("logerrorPath", logerrorPath);
			configVal.put("version", version);
			configVal.put("hostOS", hostOS);
			configVal.put("maxconnections", maxconnections);
			configVal.put("buffersize", buffersize);

			rs = util.stmt.executeQuery("show table status from " + db);
			tablesDetail = new ArrayList();
			while (rs.next()) {
				try {
					String[] item = new String[4];
					item[0] = rs.getString("name"); // 表名
					item[1] = rs.getString("rows"); // 表行数
					item[2] = rs.getLong("data_length") / 1024 + " k"; // 表大小
					item[3] = rs.getString("create_time").substring(0, 16); // 创建时间
					// SysLogger.info(item[0]+"===="+item[1]);
					tablesDetail.add(item);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			rs.close();

			rs = util.stmt.executeQuery("show processlist");
			sessionsDetail = new ArrayList();
			while (rs.next()) {
				if (!db.equals(rs.getString("db")))
					continue;

				String[] item = new String[5];
				item[0] = rs.getString("user"); // 用户
				item[1] = rs.getString("host"); // 主机
				item[2] = rs.getString("command"); // 命令
				item[3] = rs.getString("time") + " s"; // 连接时间
				item[4] = rs.getString("db"); // 数据库名
				sessionsDetail.add(item);
			}

			rs = util.stmt
					.executeQuery("show global status like 'Max_used_connections'");
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				Val.add(return_value);
				// return_value = null;
			}
			// 表锁情况
			rs = util.stmt
					.executeQuery("show global status like 'table_locks%'");
			rsmd = rs.getMetaData();
			// Vector Val = new Vector();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				Val.add(return_value);
			}
			//
			rs = util.stmt.executeQuery("show global status like 'key_read%'");
			rsmd = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				Val.add(return_value);
			}
			// 慢查询
			rs = util.stmt.executeQuery("show variables like '%slow%'");
			rsmd = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				Val.add(return_value);
			}
			// 进程使用情况
			rs = util.stmt.executeQuery("show global status like 'Thread%'");
			rsmd = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				Val.add(return_value);
			}
			// 表打开情况
			rs = util.stmt
					.executeQuery("show global status like 'open%tables%'");
			rsmd = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				Val.add(return_value);
			}
			// 索引扫描
			rs = util.stmt
					.executeQuery("show global status like 'handler_read%'");
			rsmd = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				Val.add(return_value);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		returnVal.put("configVal", configVal);
		returnVal.put("tablesDetail", tablesDetail);
		returnVal.put("sessionsDetail", sessionsDetail);
		returnVal.put("Val", Val);
		return returnVal;
	}

	// quzhi

	// oracle 列表
	public List getOracleList() {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from app_db_node where  dbtype =" + 1);
		return findByCriteria(sql.toString());
	}

	// db2列表
	public List getDB2List() {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from app_db_node where  dbtype =" + 5);
		return findByCriteria(sql.toString());
	}

	// sqlserver列表
	public List getSqlserverList() {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from app_db_node where  dbtype =" + 2);
		return findByCriteria(sql.toString());
	}

	// Sybase列表
	public List getSybaseList() {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from app_db_node where  dbtype =" + 6);
		return findByCriteria(sql.toString());
	}

	// Informix列表
	public List getInformixList() {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from app_db_node where  dbtype =" + 7);
		return findByCriteria(sql.toString());
	}

	// MySQL列表
	public List getMySQLList() {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from app_db_node where  dbtype =" + 4);
		return findByCriteria(sql.toString());
	}

	// 按条件查询
	public List findByCondition(String key, String value, int dbtype) {
		return findByCriteria("select * from app_db_node where  dbtype="
				+ dbtype + " and  " + key + "='" + value + "'");
	}

	public boolean getInformixIsOk(String ip, String port, String username,
			String password, String db, String server) throws Exception {
		Vector returnVal = new Vector();
		String dburl = "jdbc:informix-sqli://" + ip + ":" + port + "/" + db
				+ ":INFORMIXSERVER=" + server + ";user=" + username
				+ ";password=" + password; // myDB为数据库名
		// String dburl = "jdbc:informix-sqli://" + ip + ":" + port + "/" + db +
		// ":INFORMIXSERVER=" + server +
		// ";DB_LOCALE=zh_cn.GB18030-2000;CLIENT_LOCALE=zh_cn.GB18030-2000;NEWCODESET=GB18030,GB18030-2000,5488;user="
		// + username
		// + ";password=" + password ; // myDB为数据库名
		// System.out.println(dburl);
		boolean returnFlag = false;
		String sql = "select count(*) from systables";
		InformixJdbcUtil util = null;
		ResultSet rs = null;
		try {
			util = new InformixJdbcUtil(dburl, username, password);
			// SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			// SysLogger.info("&&&&&&&&&&&开始连接INFORMIX 数据库
			// IP:"+ip+"&&&&&&&&&&");
			// SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			if (rs.next()) {
				returnFlag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return returnFlag;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
			}
			try {
				util.closeStmt();
				util.closeConn();
			} catch (Exception ep) {
			}
			return returnFlag;
		}
	}

	/**
	 * 获取Informix的配置信息,表明细,当前连接信息
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param db
	 * @return
	 * @throws Exception
	 */
	public Hashtable getInformixDBConfig(String ip, String port,
			String username, String password, String db, String server)
			throws Exception {
		/*
		 * String basePath=""; //基本路径 String dataPath=""; //数据路径 String
		 * logerrorPath=""; //数据路径 String version=""; //数据库版本 String hostOS="";
		 * //服务器操作系统 String maxconnections=""; //最大连接数 String buffersize="";
		 * //内存 List tablesDetail = new ArrayList(); //表明细 List sessionsDetail =
		 * new ArrayList(); //当前连接 Hashtable configVal = new Hashtable();
		 */
		String dbname = db; // 数据库名称
		String dbserver = server;// 对应的服务名称
		String createuser = "";// 创建者
		String createtime = "";// 创建时间
		String log = "";// 日志记录是否活动
		String bufflog = "";// 日志记录是否已缓存
		String ansi = "";// 是否符合ansi
		String gls = "";// 是否启动gls
		// String logstatus = "";//日志记录标示
		Hashtable returnVal = new Hashtable();
		Vector Val = new Vector();
		String dburl = "jdbc:informix-sqli://" + ip + ":" + port + "/"
				+ "sysmaster" + ":INFORMIXSERVER=" + server + "; user="
				+ username + ";password=" + password; // myDB为数据库名
		// String dburl = "jdbc:informix-sqli://" + ip + ":" + port + "/" + db +
		// ":INFORMIXSERVER=" + server +
		// ";DB_LOCALE=zh_cn.GB18030-2000;CLIENT_LOCALE=zh_cn.GB18030-2000;NEWCODESET=GB18030,GB18030-2000,5488;user="
		// + username
		// + ";password=" + password ; // myDB为数据库名
		String sql = "select * from sysdatabases where name='" + db + "'";
		InformixJdbcUtil util = null;
		ArrayList databaselist = new ArrayList();// 数据库信息
		ArrayList loglist = new ArrayList();// 日志文件信息
		ArrayList spaceList = new ArrayList();// 空间信息
		ArrayList configList = new ArrayList();// 配置信息
		ArrayList sessionList = new ArrayList();// 会话信息
		ArrayList lockList = new ArrayList();// 锁信息信息
		ArrayList iolist = new ArrayList();// IO信息
		ArrayList aboutlist = new ArrayList();// 概要信息
		try {
			util = new InformixJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				Hashtable databaseVal = new Hashtable();
				createuser = rs.getString("owner");// 创建者
				createtime = rs.getString("created");// 创建时间
				if (rs.getInt("is_logging") == 1) {
					log = "日志记录是活动的";// 日志记录是否活动
				} else {
					log = "日志记录是不活动的";
				}
				if (rs.getInt("is_buff_log") == 1) {
					bufflog = "日志记录是已缓存";
				} else {
					bufflog = "日志记录未缓存";
				}
				if (rs.getInt("is_ansi") == 1) {
					ansi = "符合ansi";
				} else {
					ansi = "不符合ansi";
				}
				if (rs.getInt("is_nls") == 1) {
					gls = "已启动gls";
				} else {
					gls = "没有启动gls";
				}
				databaseVal.put("dbname", dbname);
				databaseVal.put("dbserver", dbserver);
				databaseVal.put("createuser", createuser);
				databaseVal.put("createtime", createtime);
				databaseVal.put("log", log);
				databaseVal.put("bufflog", bufflog);
				databaseVal.put("ansi", ansi);
				databaseVal.put("gls", gls);
				databaselist.add(databaseVal);
			}
			rs.close();

			// 系统日志文件
			rs = util.stmt.executeQuery("select * from syslogs");
			int uniqid = 0;// 日志文件标示
			int size = 0;// 日志文件的页数
			int used = 0;// 日志文件中已用的页数
			int is_used = 0;// 是否被使用（当前状态）
			int is_current = 0;// 是否是当前文件
			int is_backed_up = 0;// 是否已经备份过
			int is_archived = 0;// 是否已置于备份磁盘上
			int is_temp = 0;// 是否为临时日志文件
			while (rs.next()) {
				Hashtable informixlog = new Hashtable();
				try {
					uniqid = rs.getInt("uniqid");
					size = rs.getInt("size");
					used = rs.getInt("used");
					is_used = rs.getInt("is_used");
					is_current = rs.getInt("is_current");
					is_backed_up = rs.getInt("is_backed_up");
					is_archived = rs.getInt("is_archived");
					is_temp = rs.getInt("is_temp");
					informixlog.put("uniqid", uniqid);
					informixlog.put("size", size);
					informixlog.put("used", used);
					if (is_used == 1) {
						informixlog.put("is_used", "true");
					} else {
						informixlog.put("is_used", "false");
					}
					if (is_current == 1) {
						informixlog.put("is_current", "true");
					} else {
						informixlog.put("is_current", "false");
					}
					if (is_backed_up == 1) {
						informixlog.put("is_backed_up", "true");
					} else {
						informixlog.put("is_backed_up", "false");
					}
					if (is_archived == 1) {
						informixlog.put("is_archived", "true");
					} else {
						informixlog.put("is_archived", "false");
					}
					if (is_temp == 1) {
						informixlog.put("is_temp", "true");
					} else {
						informixlog.put("is_temp", "false");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				loglist.add(informixlog);
			}
			rs.close();

			// // 表空间
			// rs = util.stmt
			// .executeQuery("select d.name[1,8]
			// dbspace,d.owner,c.fname,sum(c.chksize) Pages_size,sum(c.chksize)
			// - sum(c.nfree) Pages_used,sum(c.nfree) Pages_free,round
			// ((sum(c.nfree)) / (sum(c.chksize)) * 100, 2) percent_free from
			// sysdbspaces d, syschunks c where d.dbsnum = c.dbsnum group by
			// 1,2,3 order by 1");
			// Map<String, Integer> names = new HashMap<String, Integer>();
			// while (rs.next()) {
			// Hashtable informixspaces = new Hashtable();
			// String name = rs.getString("dbspace").trim();
			// if (names.get(name) == null) {
			// informixspaces.put("dbspace", name);//空间名
			// informixspaces.put("owner",
			// rs.getString("owner").trim());//空间的所有者
			// String fname = rs.getString("fname").trim();
			// //informixspaces.put("fname", );//该块文件的路径
			// String page_size=rs.getString("pages_size").trim();
			// float p_size=0;
			// if(page_size!=null&&!page_size.equals(""))
			// p_size=Float.parseFloat(page_size);
			// informixspaces.put("pages_size",
			// String.valueOf(p_size/1024));//空间大小
			// String page_used=rs.getString("pages_used").trim();
			// float p_used=0;
			// if(page_used!=null&&!"".equals(page_used))
			// p_used=Float.parseFloat(page_used);
			// informixspaces.put("pages_used",String.valueOf(p_used/1024));//已使用空间
			// String page_free=rs.getString("pages_free").trim();
			// float p_free=0;
			// if(page_free!=null&&!"".equals(page_free))
			// p_free=Float.parseFloat(page_free);
			// informixspaces.put("pages_free",
			// String.valueOf(p_free/1024));//空闲空间
			// informixspaces.put("percent_free",String.valueOf(p_free/p_size));//空闲百分比
			// int len = fname.lastIndexOf("\\");
			// if (len != -1) {
			// String tpath = fname.substring(0, len);
			// informixspaces.put("file_name", tpath);
			// }
			// spaceList.add(informixspaces);
			// names.put(name, 1);
			// } else {
			// for (int i = 0; i < spaceList.size(); i++) {
			// Hashtable col = (Hashtable) spaceList.get(i);
			// if (name.equals(col.get("dbspace"))) {
			// String v_page_size = (String) col.get("pages_size");
			// String v_page_userd = (String) col.get("pages_used");
			// String c_page_size = rs.getString("pages_size");
			// String c_page_userd = rs.getString("pages_used");
			// float f_v_size = 0;
			// if (v_page_size != null && !"".equals(v_page_size))
			// f_v_size = Float.parseFloat(v_page_size);
			// float f_v_userd = 0;
			// if (v_page_userd != null && !"".equals(v_page_userd))
			// f_v_userd = Float.parseFloat(v_page_userd);
			// float f_c_size = 0;
			// if (c_page_size != null && !"".equals(c_page_size))
			// f_c_size = Float.parseFloat(c_page_size)/1024;
			// float f_c_userd = 0;
			// if (c_page_userd != null && !"".equals(c_page_userd))
			// f_c_userd = Float.parseFloat(c_page_userd)/1024;
			// float total = f_v_size + f_c_size;
			// float userd = f_v_userd + f_c_userd;
			// col.put("pages_size", String.valueOf(total));
			// col.put("pages_used", String.valueOf(userd));
			// col.put("pages_free", String.valueOf(total - userd));
			// col.put("percent_free", String.valueOf((total - userd) / total));
			// //col.put("");
			// String path = (String) informixspaces.get("fname");
			// int len = path.lastIndexOf("\\");
			// if (len > 0) {
			// col.put("fname", path.substring(0, len));
			// }
			// break;
			// }
			// }
			// }
			// }
			//
			// rs.close();
			// 表空间
			rs = util.stmt
					.executeQuery("select d.name[1,8] dbspace,d.owner,c.fname,sum(c.chksize) Pages_size,sum(c.chksize) - sum(c.nfree) Pages_used,sum(c.nfree) Pages_free,round ((sum(c.nfree)) / (sum(c.chksize)) * 100, 2) percent_free from      sysdbspaces d, syschunks c where d.dbsnum = c.dbsnum group by 1,2,3 order by 1");
			Map<String, Integer> names = new HashMap<String, Integer>();
			while (rs.next()) {
				Hashtable informixspaces = new Hashtable();
				String name = rs.getString("dbspace").trim();
				if (names.get(name) == null) {
					informixspaces.put("dbspace", name);// 空间名
					informixspaces.put("owner", rs.getString("owner").trim());// 空间的所有者
					String fname = rs.getString("fname").trim();
					// informixspaces.put("fname", );//该块文件的路径
					String page_size = rs.getString("pages_size").trim();
					float p_size = 0;
					if (page_size != null && !page_size.equals(""))
						p_size = Float.parseFloat(page_size);
					informixspaces.put("pages_size", String
							.valueOf(p_size / 1024));// 空间大小
					String page_used = rs.getString("pages_used").trim();
					float p_used = 0;
					if (page_used != null && !"".equals(page_used))
						p_used = Float.parseFloat(page_used);
					informixspaces.put("pages_used", String
							.valueOf(p_used / 1024));// 已使用空间
					String page_free = rs.getString("pages_free").trim();
					float p_free = 0;
					if (page_free != null && !"".equals(page_free))
						p_free = Float.parseFloat(page_free);
					informixspaces.put("pages_free", String
							.valueOf(p_free / 1024));// 空闲空间

					String percent_free = rs.getString("percent_free").trim();// 空闲百分比
					informixspaces.put("percent_free", percent_free);// 已使用空间百分比
					int len = 0;
					if (fname.lastIndexOf("/") != -1) {
						len = fname.lastIndexOf("/");
					} else {
						len = fname.length();
					}
					informixspaces.put("fname", fname);
					if (len != -1) {
						String tpath = fname.substring(0, len);
						informixspaces.put("file_name", tpath);
					}
					spaceList.add(informixspaces);
					names.put(name, 1);
				} else {
					for (int i = 0; i < spaceList.size(); i++) {
						Hashtable col = (Hashtable) spaceList.get(i);
						if (name.equals(col.get("dbspace"))) {
							String v_page_size = (String) col.get("pages_size");
							String v_page_userd = (String) col
									.get("pages_used");
							String c_page_size = rs.getString("pages_size");
							String c_page_userd = rs.getString("pages_used");
							String fname = rs.getString("fname").trim();
							float f_v_size = 0;
							if (v_page_size != null && !"".equals(v_page_size))
								f_v_size = Float.parseFloat(v_page_size);
							float f_v_userd = 0;
							if (v_page_userd != null
									&& !"".equals(v_page_userd))
								f_v_userd = Float.parseFloat(v_page_userd);
							float f_c_size = 0;
							if (c_page_size != null && !"".equals(c_page_size))
								f_c_size = Float.parseFloat(c_page_size) / 1024;
							float f_c_userd = 0;
							if (c_page_userd != null
									&& !"".equals(c_page_userd))
								f_c_userd = Float.parseFloat(c_page_userd) / 1024;

							float total = f_v_size + f_c_size;
							float userd = f_v_userd + f_c_userd;
							informixspaces.put("dbspace", name);// 空间名
							informixspaces.put("owner", rs.getString("owner")
									.trim());// 空间的所有者
							informixspaces.put("pages_size", String
									.valueOf(total));
							informixspaces.put("pages_used", String
									.valueOf(userd));
							informixspaces.put("pages_free", String
									.valueOf(total - userd));
							informixspaces.put("percent_free", String
									.valueOf((total - userd) / total));
							// col.put("");
							fname = fname.replaceAll("\\", "/");
							int len = fname.lastIndexOf("/");
							if (len > 0) {
								informixspaces.put("fname", fname.substring(0,
										len));
							}
							spaceList.remove(i);
							spaceList.add(informixspaces);
						}
					}
				}
			}

			rs.close();

			// 配置信息
			rs = util.stmt.executeQuery("select * from sysconfig");
			while (rs.next()) {
				Hashtable informixconfig = new Hashtable();
				informixconfig.put("cf_name", rs.getString("cf_name").trim());// 配置参数名
				informixconfig.put("cf_original", rs.getString("cf_original")
						.trim());// 引导时onconfig文件中的值
				informixconfig.put("cf_effective", rs.getString("cf_effective")
						.trim());// 当前正在使用的值
				informixconfig.put("cf_default", rs.getString("cf_default")
						.trim());// 如果onconfig文件没有指定值，数据库服务所提供的值
				configList.add(informixconfig);
			}
			rs.close();

			// 会话信息
			rs = util.stmt
					.executeQuery("select username, hostname,connected,(isreads+bufreads+bufwrites+pagreads+pagwrites) access,lockreqs,locksheld,lockwts,deadlks,lktouts,logrecs,longtxs,bufreads,bufwrites,seqscans,pagreads,pagwrites,total_sorts,dsksorts,max_sortdiskspace from syssessions s, syssesprof f where s.sid =f.sid");
			while (rs.next()) {
				Hashtable informixsession = new Hashtable();
				informixsession.put("username", rs.getString("username"));// 用户名
				informixsession.put("hostname", rs.getString("hostname"));// 主机
				informixsession.put("connected", rs.getString("connected"));// 用户连接到数据库服务器上的时间
				informixsession.put("access", rs.getString("access"));// 命中次数
				informixsession.put("lockreqs", rs.getInt("lockreqs"));// 所请求锁的数量
				informixsession.put("locksheld", rs.getInt("locksheld"));// 当前持有锁的数量
				informixsession.put("lockwts", rs.getInt("lockwts"));// 等待锁的次数
				informixsession.put("deadlks", rs.getInt("deadlks"));// 检测到的死锁数量
				informixsession.put("lktouts", rs.getInt("lktouts"));// 死锁超时数
				/*
				 * informixsession.put("logrecs",
				 * rs.getInt("logrecs").trim());//已写入逻辑日志的记录数
				 * informixsession.put("longtxs",
				 * rs.getInt("longtxs").trim());//长事物数
				 */
				informixsession.put("bufreads", rs.getInt("bufreads"));// 缓冲区读取数
				informixsession.put("bufwrites", rs.getInt("bufwrites"));// 缓冲区写入数
				// informixsession.put("seqscans",
				// rs.getInt("seqscans").trim());//顺序扫描数
				informixsession.put("pagreads", rs.getInt("pagreads"));// 页读取数
				informixsession.put("pagwrites", rs.getInt("pagwrites"));// 页写入数
				/*
				 * informixsession.put("total_sorts",
				 * rs.getInt("total_sorts").trim());//排序总数
				 * informixsession.put("dsksorts",
				 * rs.getInt("dsksorts").trim());//不适合内存的排序数
				 * informixsession.put("max_sortdiskspace",
				 * rs.getInt("max_sortdiskspace").trim());//排序所使用的最大空间
				 */
				sessionList.add(informixsession);
			}
			rs.close();

			// 锁信息
			rs = util.stmt
					.executeQuery("select l.owner, s.username, s.hostname, l.dbsname, l.tabname, l.type from syssessions s, syslocks l where s.sid = l.owner and l.tabname not like 'sys%' order by 3,4,5,2");
			while (rs.next()) {
				Hashtable informixlock = new Hashtable();
				informixlock.put("username", rs.getString("username").trim());// 用户名
				informixlock.put("hostname", rs.getString("hostname").trim());// 主机
				informixlock.put("dbsname", rs.getString("dbsname").trim());// 数据库名称
				informixlock.put("tabname", rs.getString("tabname").trim());// 表名称
				informixlock.put("type", rs.getString("type").trim());// 锁的类型
				lockList.add(informixlock);
			}
			rs.close();

			// IO信息
			rs = util.stmt.executeQuery("select * from syschkio");
			while (rs.next()) {
				Hashtable informixio = new Hashtable();
				informixio.put("chunknum", rs.getString("chunknum"));// 块编号
				informixio.put("reads", rs.getInt("reads"));// 物理读取数
				informixio.put("pagesread", rs.getInt("pagesread"));// 读取的页数
				informixio.put("writes", rs.getInt("writes"));// 物理写入数
				informixio.put("pageswritten", rs.getInt("pageswritten"));// 写入的页数
				informixio.put("mreads", rs.getInt("mreads"));// 物理读取（镜像）数
				informixio.put("mpagesread", rs.getInt("mpagesread"));// 读取（镜像）的页数
				informixio.put("mwrites", rs.getInt("mwrites"));// 物理写入（镜像）数
				informixio.put("mpageswritten", rs.getInt("mpageswritten"));// 写入（镜像）的页数
				iolist.add(informixio);
			}
			rs.close();

			// 数据库服务器的概要文件信息
			rs = util.stmt.executeQuery("select * from sysprofile");
			while (rs.next()) {
				Hashtable informixabout = new Hashtable();
				String name = rs.getString("name");
				String value = rs.getString("value");
				informixabout.put("name", name);
				informixabout.put("value", value);
				aboutlist.add(informixabout);
			}
			rs.close();

			/*
			 * rs = util.stmt .executeQuery("select sum(size) from syslogs");
			 * //逻辑文件所占空间
			 */// select count(*) from syslogs //逻辑日志文件总数
			// select count(chknum) from syschunks //块总数
			// select count(*) from sysdbspaces //空间总数
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		returnVal.put("informixspaces", spaceList);// 空间信息
		returnVal.put("informixlog", loglist);// 日志信息
		returnVal.put("databaselist", databaselist);// 数据库信息
		returnVal.put("configList", configList);// 配置信息
		returnVal.put("sessionList", sessionList);// 会话信息
		returnVal.put("lockList", lockList);// 锁信息信息
		returnVal.put("iolist", iolist);// IO信息
		returnVal.put("aboutlist", aboutlist);// 概要文件信息

		return returnVal;
	}

	public Hashtable getInformixDBConfig(String dbid, String ip, String port,
			String username, String password, String db, String server,
			Hashtable gatherHash) throws Exception {
		/*
		 * String basePath=""; //基本路径 String dataPath=""; //数据路径 String
		 * logerrorPath=""; //数据路径 String version=""; //数据库版本 String hostOS="";
		 * //服务器操作系统 String maxconnections=""; //最大连接数 String buffersize="";
		 * //内存 List tablesDetail = new ArrayList(); //表明细 List sessionsDetail =
		 * new ArrayList(); //当前连接 Hashtable configVal = new Hashtable();
		 */

		String dbname = db; // 数据库名称
		String dbserver = server;// 对应的服务名称
		String createuser = "";// 创建者
		String createtime = "";// 创建时间
		String log = "";// 日志记录是否活动
		String bufflog = "";// 日志记录是否已缓存
		String ansi = "";// 是否符合ansi
		String gls = "";// 是否启动gls
		// String logstatus = "";//日志记录标示
		Hashtable returnVal = new Hashtable();
		Vector Val = new Vector();
		String dburl = "jdbc:informix-sqli://" + ip + ":" + port + "/"
				+ "sysmaster" + ":INFORMIXSERVER=" + server + "; user="
				+ username + ";password=" + password; // myDB为数据库名
		String sql = "select * from sysdatabases where name='" + db + "'";
		InformixJdbcUtil util = null;
		ArrayList databaselist = new ArrayList();// 数据库信息
		ArrayList loglist = new ArrayList();// 日志文件信息
		ArrayList spaceList = new ArrayList();// 空间信息
		ArrayList configList = new ArrayList();// 配置信息
		ArrayList sessionList = new ArrayList();// 会话信息
		ArrayList lockList = new ArrayList();// 锁信息信息
		ArrayList iolist = new ArrayList();// IO信息
		ArrayList aboutlist = new ArrayList();// 概要信息
		try {
			util = new InformixJdbcUtil(dburl, username, password);
			util.jdbc();
			ResultSet rs = null;

			// 判断是否获取系统信息
			if (gatherHash.containsKey("sysvalue")) {
				try {
					rs = util.stmt.executeQuery(sql);
					while (rs.next()) {
						Hashtable databaseVal = new Hashtable();
						createuser = rs.getString("owner");// 创建者
						createtime = rs.getString("created");// 创建时间
						if (rs.getInt("is_logging") == 1) {
							log = "日志记录是活动的";// 日志记录是否活动
						} else {
							log = "日志记录是不活动的";
						}
						if (rs.getInt("is_buff_log") == 1) {
							bufflog = "日志记录是已缓存";
						} else {
							bufflog = "日志记录未缓存";
						}
						if (rs.getInt("is_ansi") == 1) {
							ansi = "符合ansi";
						} else {
							ansi = "不符合ansi";
						}
						if (rs.getInt("is_nls") == 1) {
							gls = "已启动gls";
						} else {
							gls = "没有启动gls";
						}
						databaseVal.put("dbname", dbname);
						databaseVal.put("dbserver", dbserver);
						databaseVal.put("createuser", createuser);
						databaseVal.put("createtime", createtime);
						databaseVal.put("log", log);
						databaseVal.put("bufflog", bufflog);
						databaseVal.put("ansi", ansi);
						databaseVal.put("gls", gls);
						databaselist.add(databaseVal);
					}
				} catch (Exception e) {
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
					}
				}
			}

			// 判断是否获系统日志文件信息
			if (gatherHash.containsKey("log")) {
				// 系统日志文件
				try {
					rs = util.stmt.executeQuery("select * from syslogs");
					int uniqid = 0;// 日志文件标示
					int size = 0;// 日志文件的页数
					int used = 0;// 日志文件中已用的页数
					int is_used = 0;// 是否被使用（当前状态）
					int is_current = 0;// 是否是当前文件
					int is_backed_up = 0;// 是否已经备份过
					int is_archived = 0;// 是否已置于备份磁盘上
					int is_temp = 0;// 是否为临时日志文件
					while (rs.next()) {
						Hashtable informixlog = new Hashtable();
						try {
							uniqid = rs.getInt("uniqid");
							size = rs.getInt("size");
							used = rs.getInt("used");
							is_used = rs.getInt("is_used");
							is_current = rs.getInt("is_current");
							is_backed_up = rs.getInt("is_backed_up");
							is_archived = rs.getInt("is_archived");
							is_temp = rs.getInt("is_temp");
							informixlog.put("uniqid", uniqid);
							informixlog.put("size", size);
							informixlog.put("used", used);
							if (is_used == 1) {
								informixlog.put("is_used", "true");
							} else {
								informixlog.put("is_used", "false");
							}
							if (is_current == 1) {
								informixlog.put("is_current", "true");
							} else {
								informixlog.put("is_current", "false");
							}
							if (is_backed_up == 1) {
								informixlog.put("is_backed_up", "true");
							} else {
								informixlog.put("is_backed_up", "false");
							}
							if (is_archived == 1) {
								informixlog.put("is_archived", "true");
							} else {
								informixlog.put("is_archived", "false");
							}
							if (is_temp == 1) {
								informixlog.put("is_temp", "true");
							} else {
								informixlog.put("is_temp", "false");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						loglist.add(informixlog);
					}
				} catch (Exception e) {
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
					}
				}
			}

			// 判断是否获表空间信息
			if (gatherHash.containsKey("space")) {// sysmaster
				// 表空间
				try {
					rs = util.stmt
							.executeQuery("select d.name[1,8] dbspace,d.owner,c.fname,sum(c.chksize) Pages_size,sum(c.chksize) - sum(c.nfree) Pages_used,sum(c.nfree) Pages_free,round ((sum(c.nfree)) / (sum(c.chksize)) * 100, 2) percent_free from      sysdbspaces d, syschunks c where d.dbsnum = c.dbsnum group by 1,2,3 order by 1");
					Map<String, Integer> names = new HashMap<String, Integer>();
					while (rs.next()) {
						Hashtable informixspaces = new Hashtable();
						String name = rs.getString("dbspace").trim();
						// if (names.get(name) == null) {
						informixspaces.put("dbspace", name);// 空间名
						informixspaces.put("owner", rs.getString("owner")
								.trim());// 空间的所有者
						String fname = rs.getString("fname").trim();
						// informixspaces.put("fname", );//该块文件的路径
						String page_size = rs.getString("pages_size").trim();
						float p_size = 0;
						if (page_size != null && !page_size.equals(""))
							p_size = Float.parseFloat(page_size);
						informixspaces.put("pages_size", String
								.valueOf(p_size / 1024));// 空间大小
						String page_used = rs.getString("pages_used").trim();
						float p_used = 0;
						if (page_used != null && !"".equals(page_used))
							p_used = Float.parseFloat(page_used);
						informixspaces.put("pages_used", String
								.valueOf(p_used / 1024));// 已使用空间
						String page_free = rs.getString("pages_free").trim();
						float p_free = 0;
						if (page_free != null && !"".equals(page_free))
							p_free = Float.parseFloat(page_free);
						informixspaces.put("pages_free", String
								.valueOf(p_free / 1024));// 空闲空间

						String percent_free = rs.getString("percent_free")
								.trim();// 空闲百分比
						informixspaces.put("percent_free", percent_free);// 已使用空间百分比
						int len = 0;
						if (fname.lastIndexOf("/") != -1) {
							len = fname.lastIndexOf("/");
						} else {
							len = fname.length();
						}
						informixspaces.put("fname", fname);
						if (len != -1) {
							String tpath = fname.substring(0, len);
							informixspaces.put("file_name", tpath);
						}
						spaceList.add(informixspaces);
						// names.put(name, 1);
						/*
						 * } else { for (int i = 0; i < spaceList.size(); i++) {
						 * Hashtable col = (Hashtable) spaceList.get(i); if
						 * (name.equals(col.get("dbspace"))) { String
						 * v_page_size = (String) col.get("pages_size"); String
						 * v_page_userd = (String) col.get("pages_used"); String
						 * c_page_size = rs.getString("pages_size"); String
						 * c_page_userd = rs.getString("pages_used"); String
						 * fname = rs.getString("fname").trim(); float f_v_size =
						 * 0; if (v_page_size != null &&
						 * !"".equals(v_page_size)) f_v_size =
						 * Float.parseFloat(v_page_size); float f_v_userd = 0;
						 * if (v_page_userd != null && !"".equals(v_page_userd))
						 * f_v_userd = Float.parseFloat(v_page_userd); float
						 * f_c_size = 0; if (c_page_size != null &&
						 * !"".equals(c_page_size)) f_c_size =
						 * Float.parseFloat(c_page_size)/1024; float f_c_userd =
						 * 0; if (c_page_userd != null &&
						 * !"".equals(c_page_userd)) f_c_userd =
						 * Float.parseFloat(c_page_userd)/1024;
						 * 
						 * float total = f_v_size + f_c_size; float userd =
						 * f_v_userd + f_c_userd; informixspaces.put("dbspace",
						 * name);//空间名 informixspaces.put("owner",
						 * rs.getString("owner").trim());//空间的所有者
						 * informixspaces.put("pages_size",
						 * String.valueOf(total));
						 * informixspaces.put("pages_used",
						 * String.valueOf(userd));
						 * informixspaces.put("pages_free", String.valueOf(total -
						 * userd)); informixspaces.put("percent_free",
						 * String.valueOf((total - userd) / total));
						 * //col.put(""); fname = fname.replaceAll("\\", "/");
						 * int len = fname.lastIndexOf("/"); if (len > 0) {
						 * informixspaces.put("fname", fname.substring(0, len)); }
						 * spaceList.remove(i); spaceList.add(informixspaces); } } }
						 */
					}
				} catch (Exception e) {
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
					}
				}
			}

			// 判断是否配置信息
			if (gatherHash.containsKey("config")) {
				// 配置信息
				try {
					rs = util.stmt.executeQuery("select * from sysconfig");
					while (rs.next()) {
						Hashtable informixconfig = new Hashtable();
						informixconfig.put("cf_name", rs.getString("cf_name")
								.trim());// 配置参数名
						informixconfig.put("cf_original", rs.getString(
								"cf_original").trim());// 引导时onconfig文件中的值
						informixconfig.put("cf_effective", rs.getString(
								"cf_effective").trim());// 当前正在使用的值
						informixconfig.put("cf_default", rs.getString(
								"cf_default").trim());// 如果onconfig文件没有指定值，数据库服务所提供的值
						configList.add(informixconfig);
					}
				} catch (Exception e) {
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
					}
				}
			}

			// 判断是否获取会话信息
			if (gatherHash.containsKey("session")) {
				// 会话信息
				try {
					rs = util.stmt
							.executeQuery("select username, hostname,connected,(isreads+bufreads+bufwrites+pagreads+pagwrites) access,lockreqs,locksheld,lockwts,deadlks,lktouts,logrecs,longtxs,bufreads,bufwrites,seqscans,pagreads,pagwrites,total_sorts,dsksorts,max_sortdiskspace from syssessions s, syssesprof f where s.sid =f.sid");
					while (rs.next()) {
						Hashtable informixsession = new Hashtable();
						informixsession.put("username", rs
								.getString("username"));// 用户名
						informixsession.put("hostname", rs
								.getString("hostname"));// 主机
						informixsession.put("connected", rs
								.getString("connected"));// 用户连接到数据库服务器上的时间
						informixsession.put("access", rs.getString("access"));// 命中次数
						informixsession.put("lockreqs", rs.getInt("lockreqs"));// 所请求锁的数量
						informixsession
								.put("locksheld", rs.getInt("locksheld"));// 当前持有锁的数量
						informixsession.put("lockwts", rs.getInt("lockwts"));// 等待锁的次数
						informixsession.put("deadlks", rs.getInt("deadlks"));// 检测到的死锁数量
						informixsession.put("lktouts", rs.getInt("lktouts"));// 死锁超时数
						/*
						 * informixsession.put("logrecs",
						 * rs.getInt("logrecs").trim());//已写入逻辑日志的记录数
						 * informixsession.put("longtxs",
						 * rs.getInt("longtxs").trim());//长事物数
						 */
						informixsession.put("bufreads", rs.getInt("bufreads"));// 缓冲区读取数
						informixsession
								.put("bufwrites", rs.getInt("bufwrites"));// 缓冲区写入数
						// informixsession.put("seqscans",
						// rs.getInt("seqscans").trim());//顺序扫描数
						informixsession.put("pagreads", rs.getInt("pagreads"));// 页读取数
						informixsession
								.put("pagwrites", rs.getInt("pagwrites"));// 页写入数
						/*
						 * informixsession.put("total_sorts",
						 * rs.getInt("total_sorts").trim());//排序总数
						 * informixsession.put("dsksorts",
						 * rs.getInt("dsksorts").trim());//不适合内存的排序数
						 * informixsession.put("max_sortdiskspace",
						 * rs.getInt("max_sortdiskspace").trim());//排序所使用的最大空间
						 */
						sessionList.add(informixsession);
					}
				} catch (Exception e) {
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
					}
				}
			}

			// 判断是否获取锁信息
			if (gatherHash.containsKey("lock")) {
				// 锁信息
				try {
					rs = util.stmt
							.executeQuery("select l.owner, s.username, s.hostname, l.dbsname, l.tabname, l.type from syssessions s, syslocks l where s.sid = l.owner and l.tabname not like 'sys%' order by 3,4,5,2");
					while (rs.next()) {
						Hashtable informixlock = new Hashtable();
						informixlock.put("username", rs.getString("username")
								.trim());// 用户名
						informixlock.put("hostname", rs.getString("hostname")
								.trim());// 主机
						informixlock.put("dbsname", rs.getString("dbsname")
								.trim());// 数据库名称
						informixlock.put("tabname", rs.getString("tabname")
								.trim());// 表名称
						informixlock.put("type", rs.getString("type").trim());// 锁的类型
						lockList.add(informixlock);
					}
				} catch (Exception e) {
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
					}
				}
			}

			// 判断是否获取IO信息
			if (gatherHash.containsKey("io")) {
				// IO信息
				try {
					rs = util.stmt.executeQuery("select * from syschkio");
					while (rs.next()) {
						Hashtable informixio = new Hashtable();
						informixio.put("chunknum", rs.getString("chunknum"));// 块编号
						informixio.put("reads", rs.getInt("reads"));// 物理读取数
						informixio.put("pagesread", rs.getInt("pagesread"));// 读取的页数
						informixio.put("writes", rs.getInt("writes"));// 物理写入数
						informixio.put("pageswritten", rs
								.getInt("pageswritten"));// 写入的页数
						informixio.put("mreads", rs.getInt("mreads"));// 物理读取（镜像）数
						informixio.put("mpagesread", rs.getInt("mpagesread"));// 读取（镜像）的页数
						informixio.put("mwrites", rs.getInt("mwrites"));// 物理写入（镜像）数
						informixio.put("mpageswritten", rs
								.getInt("mpageswritten"));// 写入（镜像）的页数
						// SysLogger.info("pagesread ====
						// "+informixio.get("pagesread"));
						// SysLogger.info("pageswritten ====
						// "+informixio.get("pageswritten"));
						iolist.add(informixio);
					}
				} catch (Exception e) {
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
					}
				}
			}

			// SysLogger.info("###########################################");
			// SysLogger.info("######### 获取INFORMIX日志信息 ##############");
			// SysLogger.info("###########################################");
			// 获取日志信息
			// if(gatherHash.containsKey("io")){
			String offset = "";
			try {
				offset = getInformixOffset(dbid);
			} catch (Exception e) {

			}
			// int lastoffset = 0;
			// try{
			// SysLogger.info("######sysonlinelog--offset>"+offset+"######");
			// if(offset != null && offset.trim().length()>0){
			// rs = util.stmt.executeQuery("select * from sysonlinelog where
			// offset > "+offset);
			// }else{
			// rs = util.stmt.executeQuery("select * from sysonlinelog");
			// //rs = util.stmt.executeQuery("select first 3 * from sysonlinelog
			// where 1=1");
			// }
			// List informixloglist = new ArrayList();
			// try{
			// while (rs.next()) {
			// Hashtable loghash = new Hashtable();
			// int offsetstr = rs.getInt("offset");
			// String linestr = rs.getString("line");
			// lastoffset = offsetstr;
			// informixloglist.add(linestr);
			// }
			// SysLogger.info("######sysonlinelog执行结束######");
			// }catch(Exception e){
			// e.printStackTrace();
			// }
			// try{
			// addInformixOffset(ip,dbid,informixloglist);
			// //SysLogger.info("last offset =========="+lastoffset);
			// updateInformixOffset(dbid+"",lastoffset+"");
			// }catch(Exception e){
			// e.printStackTrace();
			// }
			//			
			// }catch(Exception e){
			// e.printStackTrace();
			// }finally{
			// try{
			// rs.close();
			//				
			// }catch(Exception e){
			// }
			// }
			// //}

			// *******分段取sysonlinelog中的数据-START*******
			int count = -1;
			try {
				if (offset != null && offset.trim().length() > 0) {
					rs = util.stmt
							.executeQuery("select count(*) from sysonlinelog where offset > "
									+ offset);
				} else {
					rs = util.stmt
							.executeQuery("select count(*) from sysonlinelog");
				}
				if (rs.next()) {
					count = Integer.parseInt(rs.getString(1));
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			List informixloglist = new ArrayList();
			int setnum = 10000;// 每次取出的条数
			int num = count / setnum + 1;// 循环的次数
			int lastoffset = 0;
			for (int i = 0; i < num; i++) {
				StringBuffer sqlString = new StringBuffer();
				lastoffset = 0;
				try {
					if (offset != null && offset.trim().length() > 0) {
						// sqlString = "select skip "+i*setnum+" limit " +
						// (i+1)*setnum +" * from sysonlinelog where offset >
						// "+offset;
						sqlString.append("select skip ");
						sqlString.append(i * setnum);
						sqlString.append(" limit ");
						sqlString.append((i + 1) * setnum);
						sqlString
								.append(" * from sysonlinelog  where offset > ");
						sqlString.append(offset);
						rs = util.stmt.executeQuery(sqlString.toString());
					} else {
						// sqlString = "select skip "+i*setnum+" limit " +
						// (i+1)*setnum +" * from sysonlinelog";
						sqlString.append("select skip ");
						sqlString.append(i * setnum);
						sqlString.append(" limit ");
						sqlString.append((i + 1) * setnum);
						sqlString.append(" * from sysonlinelog");
						// rs = util.stmt.executeQuery("select * from
						// sysonlinelog");
						rs = util.stmt.executeQuery(sqlString.toString());
					}
					// SysLogger.info("########"+sqlString+"########");
					while (rs.next()) {
						// Hashtable loghash = new Hashtable();
						int offsetstr = rs.getInt("offset");
						String linestr = rs.getString("line");
						lastoffset = offsetstr;
						informixloglist.add(linestr);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			try {
				addInformixOffset(ip, dbid, informixloglist);
				// SysLogger.info("last offset =========="+lastoffset);
				updateInformixOffset(dbid + "", lastoffset + "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }
			// ******分段取sysonlinelog中的数据-END*******

			// 判断是否获取概要文件信息
			if (gatherHash.containsKey("profile")) {
				// 数据库服务器的概要文件信息
				try {
					rs = util.stmt.executeQuery("select * from sysprofile");
					while (rs.next()) {
						Hashtable informixabout = new Hashtable();
						String name = rs.getString("name");
						String value = rs.getString("value");
						informixabout.put("name", name);
						informixabout.put("value", value);
						aboutlist.add(informixabout);
					}
				} catch (Exception e) {
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// if (rs != null)
			// rs.close();
			util.closeStmt();
			util.closeConn();
		}
		returnVal.put("informixspaces", spaceList);// 空间信息
		returnVal.put("informixlog", loglist);// 日志信息
		returnVal.put("databaselist", databaselist);// 数据库信息
		returnVal.put("configList", configList);// 配置信息
		returnVal.put("sessionList", sessionList);// 会话信息
		returnVal.put("lockList", lockList);// 锁信息信息
		returnVal.put("iolist", iolist);// IO信息
		returnVal.put("aboutlist", aboutlist);// 概要文件信息

		return returnVal;
	}

	public String getInformixOffset(String dbnodeid) throws Exception {
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String lastoffset = "";
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			String sql = "select lastoffset from nms_informixoffset where dbnodeid='"
					+ dbnodeid + "'";
			// //System.out.println(sql);
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				lastoffset = rs.getString("lastoffset");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		return lastoffset;
	}

	public boolean updateInformixOffset(String dbnodeid, String lastoffset)
			throws Exception {
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			String sql = "select * from nms_informixoffset where dbnodeid='"
					+ dbnodeid + "'";
			// System.out.println(sql);
			rs = dbmanager.executeQuery(sql);
			if (rs != null && rs.next()) {
				// 存在记录,则需要修改
				rs.close();
				sql = "update nms_informixoffset set lastoffset = '"
						+ lastoffset + "' where dbnodeid='" + dbnodeid + "'";
				// System.out.println(sql);
				dbmanager.executeUpdate(sql);
			} else {
				// 不存在,需要添加进去
				rs.close();
				sql = "insert into nms_informixoffset(dbnodeid,lastoffset) "
						+ "values('" + dbnodeid + "','" + lastoffset + "')";
				// System.out.println(sql);
				dbmanager.executeUpdate(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	public boolean addInformixOffset(String ip, String dbnodeid, List list)
			throws Exception {
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			if (list != null && list.size() > 0) {
				StringBuffer sql = null;
				for (int i = 0; i < list.size(); i++) {
					sql = new StringBuffer();
					String detail = (String) list.get(i);
					if (detail != null && detail.trim().length() > 0) {
						// String sql = "insert into
						// informixlog"+allipstr+"(dbnodeid,detail,collecttime)
						// "
						// + "values('"
						// + dbnodeid
						// + "',\""
						// + detail
						// + "\",'" + montime + "')";
						sql.append("insert into informixlog");
						sql.append(dbnodeid);
						sql.append("(dbnodeid,detail,collecttime) ");
						sql.append("values('");
						sql.append(dbnodeid);
						sql.append("',\"");
						sql.append(detail);
						sql.append("\",'");
						sql.append(montime);
						sql.append("')");
						dbmanager.addBatch(sql.toString());
					}
				}
				dbmanager.executeBatch();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/*
	 * Sysbase数据库的采集任务
	 * 
	 * @see com.dhcc.webnms.database.api.I_Dbmonitorlist#getDB2TopWrite(java.lang.String,
	 *      int, java.lang.String, java.lang.String, java.lang.String)
	 */
	public SybaseVO getSysbaseInfo(String ip, int port, String username,
			String password, Hashtable gatherHash) throws Exception {
		// String dburl = "jdbc:sybase:Tds:"+ip+":"+port+"/master";
		String dburl = "";
		// if (ip.equals("10.10.152.30")) {
		// dburl = "jdbc:sybase:Tds:" + ip + ":" + port +
		// "/master?charset=iso_1&jconnect_version=0";
		// } else
		// dburl = "jdbc:sybase:Tds:" + ip + ":" + port + "/master";
		dburl = "jdbc:sybase:Tds:" + ip + ":" + port + "/master";
		// String sql = "select substr(table_schema,1,10) as
		// tbschema,substr(table_name,1,30) as tbname, rows_read, rows_written,
		// overflow_accesses, page_reorgs from table (SNAPSHOT_TABLE('', -1)) as
		// snapshot_table order by rows_written desc fetch first 10 rows only";
		SysbaseJdbcUtil util = null;
		ResultSet rs = null;
		Hashtable sys_hash = new Hashtable();
		List retList = new ArrayList();
		SybaseVO vo = new SybaseVO();
		ResultSet rs_Procedure_hitrate = null;
		ResultSet rs_Data_hitrate = null;
		ResultSet rs_Total_logicalMemory = null;
		ResultSet rs_Procedure_cache = null;
		ResultSet rs_Metadata_cache = null;
		ResultSet rs_Total_physicalMemory = null;
		ResultSet rs_Total_dataCache = null;
		ResultSet rs_Xact_count = null;
		ResultSet rs_Locks_count = null;
		ResultSet rs_Disk_count = null;
		ResultSet rs_Io_busy_rate = null;
		ResultSet rs_ServerName = null;
		ResultSet rs_Cpu_busy_rate = null;
		ResultSet rs_cpu_busy = null;
		ResultSet rs_idle = null;
		ResultSet rs_version = null;
		ResultSet rs_io_busy = null;
		ResultSet rs_Sent_rate = null;
		ResultSet rs_Received_rate = null;
		ResultSet rs_Write_rate = null;
		ResultSet rs_Read_rate = null;
		ResultSet rs_devices = null;
		ResultSet rs_users = null;
		ResultSet rs_dbs = null;
		ResultSet rs_servers = null;

		try {
			util = new SysbaseJdbcUtil(dburl, username, password);
			util.jdbc();

			List list = new ArrayList();

			if (gatherHash.containsKey("cpu")) {
				// cpu工作时间 秒
				StringBuffer cpu_busy = new StringBuffer("SELECT @@CPU_BUSY");
				try {
					rs = util.executeQuery(cpu_busy.toString());
					try {
						while (rs.next()) {
							vo.setCpu_busy(rs.getString(""));
							break;
							// list.add(vo);
							// //System.out.println(vo.getCpu_busy());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 空闲时间 秒
					StringBuffer idle = new StringBuffer("SELECT @@IDLE");
					rs = util.executeQuery(idle.toString());
					try {
						while (rs.next()) {
							vo.setIdle(rs.getString(""));
							break;
							// list.add(vo);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {

				} finally {
					rs.clearWarnings();
				}

				// cpu利用率(已经乘以了100) %
				StringBuffer Cpu_busy_rate = new StringBuffer(
						"select convert(decimal(10,2),round(@@cpu_busy*100/((@@cpu_busy+@@io_busy+@@idle)*1.00),2))");
				rs = util.executeQuery(Cpu_busy_rate.toString());
				try {
					while (rs.next()) {
						vo.setCpu_busy_rate(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getCpu_busy_rate());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (gatherHash.containsKey("version")) {
				// 版本
				StringBuffer version = new StringBuffer("SELECT @@version");
				rs = util.executeQuery(version.toString());
				try {
					while (rs.next()) {
						vo.setVersion(rs.getString(""));
						break;
						// list.add(vo);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// 页大小
			StringBuffer version = new StringBuffer("SELECT @@maxpagesize");
			rs = util.executeQuery(version.toString());
			try {
				while (rs.next()) {
					vo.setMaxPageSize(rs.getString(""));
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (gatherHash.containsKey("servername")) {
				// SQL服务器名称
				StringBuffer ServerName = new StringBuffer(
						"SELECT  @@SERVERNAME");
				rs = util.executeQuery(ServerName.toString());
				try {
					while (rs.next()) {
						vo.setServerName(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getServerName());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// 最大连接数
			StringBuffer maxconn = new StringBuffer("SELECT @@max_connections");
			rs = util.executeQuery(maxconn.toString());
			try {
				while (rs.next()) {
					vo.setMaxconn(rs.getString(""));
					// SysLogger.info(vo.getMaxconn()+"=========maxconn");
					break;
					// list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 数据库当前日期
			StringBuffer curdate = new StringBuffer("SELECT getdate()");
			rs = util.executeQuery(curdate.toString());
			try {
				while (rs.next()) {
					// SysLogger.info(rs.getString("")+"=====curdate");
					vo.setCurdate(rs.getString(""));
					break;
					// list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 数据库启动时间
			StringBuffer boottime = new StringBuffer("SELECT @@boottime");
			rs = util.executeQuery(boottime.toString());
			try {
				while (rs.next()) {
					// SysLogger.info(rs.getString("")+"=====boottime");
					vo.setBoottime(rs.getString(""));
					break;
					// list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 持有锁并且阻塞其他进程的进程数

			if (gatherHash.containsKey("io")) {
				// 输入输出执行时间 秒
				StringBuffer io_busy = new StringBuffer("SELECT @@io_busy");
				rs = util.executeQuery(io_busy.toString());
				try {
					while (rs.next()) {
						vo.setIo_busy(rs.getString(""));
						break;
						// list.add(vo);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// IO利用率(已经乘以了100) %
				StringBuffer Io_busy_rate = new StringBuffer(
						"select convert(decimal(10,2),round(@@io_busy*100/((@@cpu_busy+@@io_busy+@@idle)*1.00),2))");
				rs = util.executeQuery(Io_busy_rate.toString());
				try {
					while (rs.next()) {
						vo.setIo_busy_rate(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getIo_busy_rate());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (gatherHash.containsKey("pack")) {
				// 写到网络上的输出数据速率 包数/秒
				// StringBuffer Sent_rate = new StringBuffer("select
				// convert(decimal(10,2),round(@@pack_sent/(nullif(@@io_busy,0)*1.00,2))");
				StringBuffer Sent_rate = new StringBuffer(
						"select @@pack_sent as packs,@@io_busy as seconds");
				rs = util.executeQuery(Sent_rate.toString());
				try {
					while (rs.next()) {
						String packs = rs.getString("packs");
						String seconds = rs.getString("seconds");
						if (seconds.equals("0")) {
							vo.setSent_rate(packs);
						} else {
							int sent_rate = Integer.parseInt(packs)
									/ Integer.parseInt(seconds);
							vo.setSent_rate(sent_rate + "");
						}
						break;
						// list.add(vo);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 从网络上读取的输入数据速率 包数/秒
				StringBuffer Received_rate = new StringBuffer(
						"select @@pack_received as packs,@@io_busy as seconds");
				rs = util.executeQuery(Received_rate.toString());
				try {
					while (rs.next()) {
						String packs = rs.getString("packs");
						String secends = rs.getString("seconds");
						if (secends.equals("0")) {
							vo.setReceived_rate(packs);
						} else {
							int sent_rate = Integer.parseInt(packs)
									/ Integer.parseInt(secends);
							vo.setReceived_rate(sent_rate + "");
						}
						break;

						// vo.setReceived_rate(rs_Received_rate.getString(""));
						// list.add(vo);
						// //System.out.println(vo.getReceived_rate());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (gatherHash.containsKey("diskpack")) {
				// 写入磁盘速率 次/秒
				StringBuffer Write_rate = new StringBuffer(
						"select @@TOTAL_WRITE as packs,@@io_busy as seconds");
				rs = util.executeQuery(Write_rate.toString());
				try {
					while (rs.next()) {
						String packs = rs.getString("packs");
						String secends = rs.getString("seconds");
						if (secends.equals("0")) {
							vo.setWrite_rate(packs);
						} else {
							int sent_rate = Integer.parseInt(packs)
									/ Integer.parseInt(secends);
							vo.setWrite_rate(sent_rate + "");
						}
						break;

						// vo.setWrite_rate(rs_Write_rate.getString(""));
						// list.add(vo);
						// //System.out.println(vo.getWrite_rate());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 读取磁盘速率 Read_rate
				StringBuffer Read_rate = new StringBuffer(
						"select @@TOTAL_WRITE as packs,@@io_busy as seconds");
				rs = util.executeQuery(Read_rate.toString());
				try {
					while (rs.next()) {
						String packs = rs.getString("packs");
						String secends = rs.getString("seconds");
						if (secends.equals("0")) {
							vo.setRead_rate(packs);
						} else {
							int sent_rate = Integer.parseInt(packs)
									/ Integer.parseInt(secends);
							vo.setRead_rate(sent_rate + "");
						}
						break;

						// vo.setRead_rate(rs_Read_rate.getString(""));
						// list.add(vo);
						// //System.out.println(vo.getRead_rate());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (gatherHash.containsKey("sysdevices")) {
				// 转储设备或数据库设备个数
				StringBuffer Disk_count = new StringBuffer(
						"select count(*) from sysdevices");
				rs = util.executeQuery(Disk_count.toString());
				try {
					while (rs.next()) {
						vo.setDisk_count(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getDisk_count());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (gatherHash.containsKey("lock")) {
				// 活动锁个数
				StringBuffer Locks_count = new StringBuffer(
						"select count(*) from syslocks");
				rs = util.executeQuery(Locks_count.toString());
				try {
					while (rs.next()) {
						vo.setLocks_count(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getLocks_count());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 持有锁并且阻塞其他进程的进程数
				Locks_count = new StringBuffer(
						"select count(*) from syslocks where type=256");
				rs = util.executeQuery(Locks_count.toString());
				try {
					while (rs.next()) {
						vo.setLong_locks_count(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getLocks_count());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 允许的最大锁数
				Locks_count = new StringBuffer("sp_configure 'number of locks'");
				rs = util.executeQuery(Locks_count.toString());
				try {
					while (rs.next()) {
						vo.setMaxlock(rs.getString("config value"));
						// SysLogger.info(vo.getMaxlock()+"===========lock");
						break;
						// list.add(vo);
						// //System.out.println(vo.getLocks_count());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			if (gatherHash.containsKey("systransactions")) {
				// 事务的个数
				StringBuffer Xact_count = new StringBuffer(
						"select count(*) from systransactions");
				rs = util.executeQuery(Xact_count.toString());
				try {
					while (rs.next()) {
						vo.setXact_count(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getXact_count());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// if(gatherHash.containsKey("systransactions")){
			// 长事务的个数
			int longtrans = 0;
			StringBuffer Xact_count = new StringBuffer(
					"select dbid,spid,starttime,name from syslogshold where spid <> 0");
			rs = util.executeQuery(Xact_count.toString());
			List longtranslist = new ArrayList();
			try {
				while (rs.next()) {
					TablesVO tvo = new TablesVO();
					tvo.setDbid(rs.getString("dbid"));
					tvo.setSpid(rs.getString("spid"));
					tvo.setStarttime(rs.getString("starttime"));
					tvo.setName(rs.getString("name"));
					longtranslist.add(tvo);
				}
				vo.setLongtransInfo(longtranslist);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }

			if (gatherHash.containsKey("totaldatacache")) {
				// 总数据高速缓存大小 MB
				StringBuffer Total_dataCache = new StringBuffer(
						"select convert(decimal(10,0),c.value/512.00) from sysconfigures t ,syscurconfigs c where t.comment like '%total data cache size%' and c.config=t.config");
				rs = util.executeQuery(Total_dataCache.toString());
				try {
					while (rs.next()) {
						vo.setTotal_dataCache(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getTotal_dataCache());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (gatherHash.containsKey("totalphysicalmemory")) {
				// 总物理内存大小 MB
				StringBuffer Total_physicalMemory = new StringBuffer(
						"select convert(decimal(10,0),c.value/512.00) from sysconfigures t ,syscurconfigs c where t.comment like '%total physical memory%' and c.config=t.config");
				rs = util.executeQuery(Total_physicalMemory.toString());
				try {
					while (rs.next()) {
						vo.setTotal_physicalMemory(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getTotal_physicalMemory());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (gatherHash.containsKey("metadatacache")) {
				// Metadata缓存 MB
				StringBuffer Metadata_cache = new StringBuffer(
						"select convert(decimal(10,0),c.value/512.00) from sysconfigures t ,syscurconfigs c where t.comment like '%Meta-Data Caches%' and c.config=t.config");
				rs = util.executeQuery(Metadata_cache.toString());
				try {
					while (rs.next()) {
						vo.setMetadata_cache(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getMetadata_cache());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (gatherHash.containsKey("procedurecache")) {
				// 存储过程缓存大小 MB
				StringBuffer Procedure_cache = new StringBuffer(
						"select convert(decimal(10,0),c.value/512.00) from sysconfigures t ,syscurconfigs c where t.comment like '%procedure cache size%' and c.config=t.config");
				rs = util.executeQuery(Procedure_cache.toString());
				try {
					while (rs.next()) {
						vo.setProcedure_cache(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getProcedure_cache());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (gatherHash.containsKey("totallogicalmemory")) {
				// 总逻辑内存大小 MB
				StringBuffer Total_logicalMemory = new StringBuffer(
						"select convert(decimal(10,0),c.value/512.00) from sysconfigures t ,syscurconfigs c where t.comment like '%total logical memory%' and c.config=t.config");
				rs = util.executeQuery(Total_logicalMemory.toString());
				try {
					while (rs.next()) {
						vo.setTotal_logicalMemory(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getTotal_logicalMemory());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (gatherHash.containsKey("datahitrate")) {
				// 数据缓存匹配度 %
				StringBuffer Data_hitrate = new StringBuffer(
						"select convert(decimal(10,0),c.value*100/c.memory_used) from sysconfigures t ,syscurconfigs c where t.comment ='total data cache size' and c.config=t.config");

				try {
					rs = util.executeQuery(Data_hitrate.toString());
					while (rs.next()) {
						vo.setData_hitrate(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getData_hitrate());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (gatherHash.containsKey("procedurehitrate")) {
				// 存储缓存匹配度 %
				StringBuffer Procedure_hitrate = new StringBuffer(
						"select convert(decimal(10,0),c.value*200/c.memory_used) from sysconfigures t ,syscurconfigs c where t.comment ='procedure cache size' and c.config=t.config");

				try {

					rs = util.executeQuery(Procedure_hitrate.toString());
					while (rs.next()) {
						vo.setProcedure_hitrate(rs.getString(""));
						break;
						// list.add(vo);
						// //System.out.println(vo.getProcedure_hitrate());
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// if(gatherHash.containsKey("device")){
			// *************************引擎数信息
			StringBuffer engines = new StringBuffer(
					"select engine, status, affinitied, starttime from sysengines");
			List engineInfo = new ArrayList();
			int offlineengine = 0;
			try {

				rs = util.executeQuery(engines.toString());
				while (rs.next()) {
					TablesVO tvo = new TablesVO();
					tvo.setEngine(rs.getString("engine"));
					tvo.setStatus(rs.getString("status"));
					tvo.setStarttime(rs.getString("starttime"));
					engineInfo.add(tvo);
					if ("offline".equalsIgnoreCase(tvo.getStatus())) {
						offlineengine = offlineengine + 1;
					}
					// //System.out.println(vo.getDevice_name()+"@"+vo.getDevice_physical_name()+"@"+vo.getDevice_description());
				}
				vo.setEngineInfo(engineInfo);
				vo.setOfflineengine(offlineengine + "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }

			if (gatherHash.containsKey("device")) {
				// *************************转储设备或数据库设备信息
				StringBuffer devices = new StringBuffer("sp_helpdevice");
				List deviceInfo = new ArrayList();
				try {

					rs = util.executeQuery(devices.toString());
					while (rs.next()) {
						TablesVO tvo = new TablesVO();
						tvo.setDevice_name(rs.getString("device_name"));
						tvo.setDevice_physical_name(rs
								.getString("physical_name"));
						tvo.setDevice_description(rs.getString("description"));
						deviceInfo.add(tvo);
						// //System.out.println(vo.getDevice_name()+"@"+vo.getDevice_physical_name()+"@"+vo.getDevice_description());
					}
					vo.setDeviceInfo(deviceInfo);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			int usedconn = 0;
			if (gatherHash.containsKey("user")) {
				// ************** 当前数据库所有用户的信息
				StringBuffer users = new StringBuffer("sp_helpuser");
				List userInfo = new ArrayList();

				try {

					rs = util.executeQuery(users.toString());
					while (rs.next()) {
						TablesVO tvo = new TablesVO();
						tvo.setUsers_name(rs.getString("Users_name"));
						tvo.setID_in_db(rs.getString("ID_in_db"));
						tvo.setGroup_name(rs.getString("Group_name"));
						tvo.setLogin_name(rs.getString("Login_name"));
						userInfo.add(tvo);
						// SysLogger.info(tvo.getLogin_name()+"=========loginname");
						// if(tvo.getLogin_name() != null &&
						// !"NULL".equalsIgnoreCase(tvo.getLogin_name())){
						// usedconn = usedconn + 1;
						// }
						// //System.out.println(vo.getUsers_name()+vo.getID_in_db()+vo.getGroup_name()+vo.getLogin_name());
					}
					vo.setUserInfo(userInfo);

				} catch (Exception e) {
					e.printStackTrace();
				}
				// ************** 当前数据库连接的用户的信息
				StringBuffer _users = new StringBuffer("sp_who");
				List _userInfo = new ArrayList();

				try {

					rs = util.executeQuery(_users.toString());
					while (rs.next()) {
						String loginname = rs.getString("loginame");
						if (loginname != null && loginname.trim().length() > 0
								&& !"NULL".equalsIgnoreCase(loginname))
							usedconn = usedconn + 1;

						// TablesVO tvo = new TablesVO();
						// tvo.setUsers_name(rs.getString("Users_name"));
						// tvo.setID_in_db(rs.getString("ID_in_db"));
						// tvo.setGroup_name(rs.getString("Group_name"));
						// tvo.setLogin_name(rs.getString("Login_name"));
						// userInfo.add(tvo);
						// SysLogger.info(tvo.getLogin_name()+"=========loginname");
						// if(tvo.getLogin_name() != null &&
						// !"NULL".equalsIgnoreCase(tvo.getLogin_name())){
						// usedconn = usedconn + 1;
						// }
						// //System.out.println(vo.getUsers_name()+vo.getID_in_db()+vo.getGroup_name()+vo.getLogin_name());
					}
					// vo.setUserInfo(userInfo);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			vo.setUsedconn(usedconn + "");
			// SysLogger.info(vo.getUsedconn()+"========usedconn");
			// *************************日志信息
			StringBuffer logspaces = new StringBuffer("sp_spaceused syslogs");
			List logspacesInfo = new ArrayList();
			try {
				rs = util.executeQuery(logspaces.toString());
				while (rs.next()) {
					// ResultSetMetaData rsmd = rs.getMetaData();
					String total_pages = rs.getString("total_pages").trim();
					String free_pages = rs.getString("free_pages").trim();
					String used_pages = rs.getString("used_pages").trim();
					String reserved_pages = rs.getString("reserved_pages")
							.trim();
					vo.setTotallog((Integer.parseInt(total_pages))
							* (Integer.parseInt(vo.getMaxPageSize())) / 1024
							+ "");
					vo.setFreelog((Integer.parseInt(free_pages))
							* (Integer.parseInt(vo.getMaxPageSize())) / 1024
							+ "");
					vo.setUsedlog((Integer.parseInt(used_pages))
							* (Integer.parseInt(vo.getMaxPageSize())) / 1024
							+ "");
					vo.setReservedlog((Integer.parseInt(reserved_pages))
							* (Integer.parseInt(vo.getMaxPageSize())) / 1024
							+ "");
					vo.setLogusedperc(Math.round(Float.parseFloat(used_pages)
							/ Float.parseFloat(total_pages))
							+ "");
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// //*************************段信息
			// StringBuffer _segments = new StringBuffer("select d.name,u.size
			// as size,curunreservedpgs(db_id(),u.lstart,u.unreservedpgs) as
			// free_pages from master.dbo.sysusages u,master.dbo.sysdevices d
			// where u.vstart between d.low and d.high group by d.name order by
			// d.name");
			// //List segmentsInfo = new ArrayList();
			// //List seglogInfo = new ArrayList();
			// //Hashtable segloghash = new Hashtable();
			// //int maxsegusedperc = 0;
			// try {
			// rs = util.executeQuery(_segments.toString());
			// while (rs.next()) {
			// //ResultSetMetaData rsmd = rs.getMetaData();
			// String name = rs.getString("name");
			// String size = rs.getString("size");
			// String free_pages = rs.getString("free_pages");
			// SysLogger.info(name+"========"+size+"======"+free_pages);
			// //if(!"1".equalsIgnoreCase(status))segmentsInfo.add(name);
			// }
			// // if(segmentsInfo != null && segmentsInfo.size()>0){}
			// // vo.setSeglogInfo(segloghash);
			// // vo.setMaxsegusedperc(maxsegusedperc+"");
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			// //*************************段信息
			// StringBuffer segments = new StringBuffer("sp_helpsegment");
			// List segmentsInfo = new ArrayList();
			// List seglogInfo = new ArrayList();
			// Hashtable segloghash = new Hashtable();
			// int maxsegusedperc = 0;
			// try {
			// rs = util.executeQuery(segments.toString());
			// while (rs.next()) {
			// //ResultSetMetaData rsmd = rs.getMetaData();
			// String name = rs.getString("name");
			// String status = rs.getString("status");
			// if(!"1".equalsIgnoreCase(status))segmentsInfo.add(name);
			// }
			// if(segmentsInfo != null && segmentsInfo.size()>0){
			// int segperc = 0;
			// for(int i=0;i<segmentsInfo.size();i++){
			// String name = (String)segmentsInfo.get(i);
			// segments = null;
			// segments = new StringBuffer("sp_helpsegment '"+name+"'");
			// rs = null;
			// try{
			// //SysLogger.info(segments.toString()+"============");
			// rs = util.executeQuery(segments.toString());
			// SysLogger.info(rs.toString());
			// while (rs.next()) {
			// ResultSetMetaData rsmd = rs.getMetaData();
			// //SysLogger.info(rsmd.getColumnCount()+"=========columncount");
			// //if(rsmd.getColumnCount() == 5){
			// //SysLogger.info(rsmd.getColumnName(1)+"==="+rs.getString(rsmd.getColumnName(1)));
			// //
			// SysLogger.info(rsmd.getColumnName(2)+"==="+rs.getString(rsmd.getColumnName(2)));
			// //
			// SysLogger.info(rsmd.getColumnName(3)+"==="+rs.getString(rsmd.getColumnName(3)));
			// //
			// SysLogger.info(rsmd.getColumnName(4)+"==="+rs.getString(rsmd.getColumnName(4)));
			// //
			// SysLogger.info(rsmd.getColumnName(5)+"==="+rs.getString(rsmd.getColumnName(5)));
			// //}
			//
			// // String total_pages = rs.getString("total_pages");
			// // String used_pages = rs.getString("used_pages");
			// // segperc =
			// Math.round(Float.parseFloat(used_pages)/Float.parseFloat(total_pages));
			// // if(segperc > maxsegusedperc)maxsegusedperc = segperc;
			// // segloghash.put(name, segperc);
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			// }
			// vo.setSeglogInfo(segloghash);
			// vo.setMaxsegusedperc(maxsegusedperc+"");
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			// 显示进程信息
			StringBuffer process = new StringBuffer(
					"select spid,hostname,status,hostprocess,program_name from master..sysprocesses");
			List processinfo = new ArrayList();
			int sleepingproccount = 0;
			int proccount = 0;
			try {
//                System.out.println("------->"+process.toString());
				rs = util.executeQuery(process.toString());
				while (rs.next()) {
//					SysLogger.info(rs.getString("spid")+"==="+rs.getString("hostname")+"==="+rs.getString("status")+"==="+rs.getString("hostprocess")+"==="+rs.getString("program_name"));
					TablesVO tvo = new TablesVO();
					tvo.setSpid(rs.getString("spid"));
					tvo.setHostname(rs.getString("hostname"));
					tvo.setProstatus(rs.getString("status"));
					tvo.setHostprocess(rs.getString("hostprocess"));
					tvo.setProgram_name(rs.getString("program_name"));
//					SysLogger.info(tvo.getSpid()+"==="+tvo.getHostname()+"==="+tvo.getProstatus()+"==="+tvo.getHostprocess()+"==="+tvo.getProgram_name());
					processinfo.add(tvo);
					if ("sleepging".equalsIgnoreCase(tvo.getStatus())) {
						sleepingproccount = sleepingproccount + 1;
					}
					proccount = proccount + 1;
					// //System.out.println(vo.getUsers_name()+vo.getID_in_db()+vo.getGroup_name()+vo.getLogin_name());
				}
				vo.setProcessInfo(processinfo);
				vo.setProcesscount(proccount + "");
				vo.setSleepingproccount(sleepingproccount + "");
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (gatherHash.containsKey("db")) {
				// ************** 当前数据库大小信息
				List dbInfo = new ArrayList();
				// rs = util.executeQuery("select
				// c.name,sum(a.size)*(@@maxpagesize/1024)/1024 as
				// size,sum(a.unreservedpgs)*(@@maxpagesize/1024)/1024 as
				// freesize, (sum(a.size)-sum(a.unreservedpgs))*100/sum(a.size)
				// as perc from sysusages a,sysdevices b ,sysdatabases c where
				// a.dbid=c.dbid and (a.vstart between b.low and b.high) group
				// by c.name");
				// rs = util.executeQuery("select
				// c.name,sum(a.size)*(@@maxpagesize/1024)/1024 as
				// size,sum(a.unreservedpgs)*(@@maxpagesize/1024)/1024 as
				// freesize, (sum(a.size)-sum(a.unreservedpgs))*100/sum(a.size)
				// as perc from sysusages a,sysdatabases c where a.dbid=c.dbid
				// group by c.name");
				try {
					Map<String, Integer> names = new HashMap<String, Integer>();
					rs = util
							.executeQuery("select c.name,sum(a.size)*(@@maxpagesize/1024)/1024 as size,sum(a.unreservedpgs)*(@@maxpagesize/1024)/1024 as freesize from sysusages a,sysdatabases c where a.dbid=c.dbid group by c.name");
					while (rs.next()) {
						String name = rs.getString("name");
						if (names.get(name) == null) {
							TablesVO tvo = new TablesVO();
							String _allsize = rs.getString("size");
							String _freesize = rs.getString("freesize");
							tvo.setDb_name(rs.getString("name"));
							tvo.setDb_size(_allsize);
							tvo.setDb_freesize(_freesize);
							int perc = (Integer.parseInt(_allsize) - Integer
									.parseInt(_freesize))
									* 100 / Integer.parseInt(_allsize);
							tvo.setDb_usedperc(perc + "");
							dbInfo.add(tvo);
							names.put(name, 1);
						} else {
							for (int i = 0; i < dbInfo.size(); i++) {
								TablesVO tvo = (TablesVO) dbInfo.get(i);
								if (name.equals(tvo.getDb_name())) {
									String vofree = tvo.getDb_freesize();
									String dbsize = tvo.getDb_size();
									String userpred = tvo.getDb_usedperc();
									float vo_size = 0;
									if (dbsize != null && !"".equals(dbsize)) {
										vo_size = Float.parseFloat(dbsize);
									}
									float vo_used = 0;
									if (userpred != null
											&& !"".equals(userpred))
										vo_used = Float.parseFloat(userpred);
									float vo_free = 0;
									if (vofree != null && !"".equals(vofree))
										vo_free = Float.parseFloat(vofree);
									String _allsize = rs.getString("size");
									String _freesize = rs.getString("freesize");
									float f_allsize = 0;
									if (_allsize != null
											&& !"".equals(_allsize))
										f_allsize = Float.parseFloat(_allsize);
									float f_free = 0;
									if (_freesize != null
											&& !"".equals(_freesize))
										f_free = Float.parseFloat(_freesize);
									float total = vo_size + f_allsize;
									float _free = f_free + vo_free;
									tvo.setDb_freesize(String.valueOf(_free));
									tvo.setDb_size(String.valueOf(total));
									tvo.setDb_usedperc(String
											.valueOf((total - _free) / total
													* 100));
									break;
								}
							}
						}

					}
					vo.setDbInfo(dbInfo);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (gatherHash.containsKey("servers")) {
				// ＊＊＊＊＊＊＊＊远程服务器信息
				StringBuffer servers = new StringBuffer("sp_helpserver");
				List serversInfo = new ArrayList();

				try {
					rs = util.query(servers.toString());
					while (rs.next()) {
						TablesVO tvo = new TablesVO();
						tvo.setServer_name(rs.getString("name"));
						tvo
								.setServer_network_name(rs
										.getString("network_name"));
						tvo.setServer_class(rs.getString("class"));
						tvo.setServer_status(rs.getString("status"));
						serversInfo.add(tvo);
					}
					vo.setServersInfo(serversInfo);

				} catch (Exception e) {
					// util.jdbc();
					e.printStackTrace();
				}
			}

			// ＊＊＊＊＊＊＊＊数据库信息
			StringBuffer dbs = new StringBuffer(
					"select name,status,crdate,dumptrdate from sysdatabases");
			List dbsInfo = new ArrayList();

			try {
				rs = util.query(dbs.toString());
				while (rs.next()) {
					TablesVO tvo = new TablesVO();
					tvo.setName(rs.getString("name"));
					tvo.setCreatedate(rs.getString("crdate"));
					tvo.setDumptrdate(rs.getString("dumptrdate"));
					tvo.setStatus(rs.getString("status"));
					if ("-32768".equalsIgnoreCase(tvo.getStatus())
							|| "64".equalsIgnoreCase(tvo.getStatus())
							|| "256".equalsIgnoreCase(tvo.getStatus())
							|| "32".equalsIgnoreCase(tvo.getStatus())
							|| "320".equalsIgnoreCase(tvo.getStatus())
							|| "288".equalsIgnoreCase(tvo.getStatus())) {
						tvo.setStatus("可疑或不可用");
					} else {
						tvo.setStatus("正常");
					}
					// SysLogger.info(tvo.getName()+"======"+tvo.getStatus());
					dbsInfo.add(tvo);
				}
				vo.setDbsInfo(dbsInfo);

			} catch (Exception e) {
				// util.jdbc();
				e.printStackTrace();
			}

			// return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs_dbs != null)
				rs_dbs.close();
			if (rs_users != null)
				rs_users.close();
			if (rs_devices != null)
				rs_devices.close();
			if (rs_Procedure_hitrate != null)
				rs_Procedure_hitrate.close();
			if (rs_Data_hitrate != null)
				rs_Data_hitrate.close();
			if (rs_Total_logicalMemory != null)
				rs_Total_logicalMemory.close();
			if (rs_Procedure_cache != null)
				rs_Procedure_cache.close();
			if (rs_Metadata_cache != null)
				rs_Metadata_cache.close();
			if (rs_Total_physicalMemory != null)
				rs_Total_physicalMemory.close();
			if (rs_Total_dataCache != null)
				rs_Total_dataCache.close();
			if (rs_Xact_count != null)
				rs_Xact_count.close();
			if (rs_Locks_count != null)
				rs_Locks_count.close();
			if (rs_Disk_count != null)
				rs_Disk_count.close();
			if (rs_Io_busy_rate != null)
				rs_Io_busy_rate.close();
			if (rs_Cpu_busy_rate != null)
				rs_Cpu_busy_rate.close();
			if (rs_cpu_busy != null)
				rs_cpu_busy.close();
			if (rs_idle != null)
				rs_idle.close();
			if (rs_version != null)
				rs_version.close();
			if (rs_io_busy != null)
				rs_io_busy.close();
			if (rs_Sent_rate != null)
				rs_Sent_rate.close();
			if (rs_Received_rate != null)
				rs_Received_rate.close();
			if (rs_Write_rate != null)
				rs_Write_rate.close();
			if (rs_Read_rate != null)
				rs_Read_rate.close();
			if (rs_ServerName != null)
				rs_ServerName.close();

			if (rs != null)
				rs.close();
			util.closeStmt();
			util.closeConn();
		}
		return vo;
	}

	/**
	 * 将oracle数据批量入库
	 * 
	 * @param oracledatas
	 */
	public void processOracleData(Hashtable oracledatas) {
		if (oracledatas == null || oracledatas.isEmpty()) {
			return;
		}
		Hashtable sysValueHash = new Hashtable();
		Hashtable memValueHash = new Hashtable();
		Hashtable memPerfValueHash = new Hashtable();
		Hashtable tableinfoHash = new Hashtable();// 表空间
		Hashtable rollbackinfo_vHash = new Hashtable();// 回滚段信息
		Hashtable lockinfo_vHash = new Hashtable();// 锁信息
		Hashtable info_vHash = new Hashtable();// session信息
		Hashtable table_vHash = new Hashtable();// 表信息
		Hashtable sql_vHash = new Hashtable();// TOPN的SQL语句信息
		Hashtable contrFile_vHash = new Hashtable();// 控制文件 nms_oracontrfile
		Hashtable isArchive_hHash = new Hashtable();// nms_oraisarchive
		Hashtable keepObj_vHash = new Hashtable();// nms_orakeepobj
		Hashtable lstrnStatuHash = new Hashtable();// 监听状态 nms_orastatus
		Hashtable extent_vHash = new Hashtable();// 扩展信息 //nms_oraextent
		Hashtable logFile_vHash = new Hashtable();// 日志文件 //nms_oralogfile
		Hashtable userinfo_hHash = new Hashtable();// 用户信息 //nms_orauserinfo
		Hashtable cursorsHash = new Hashtable();// 指针信息 //nms_oracursors
		Hashtable waitHash = new Hashtable();// 等待信息 //nms_orawait
		Hashtable dbioHash = new Hashtable();// IO信息 //nms_oradbio
		Hashtable statusHash = new Hashtable();// 状态信息 nms_orastatus
		Hashtable baseInfoHash = new Hashtable();// 基本信息
		Hashtable oracleLockInfoHash = new Hashtable();
		Hashtable topSqlReadWriteHash = new Hashtable();
		Hashtable topSqlSortHash = new Hashtable();
		Hashtable jobhash = new Hashtable();// 任务信息

		DBManager dbManager = new DBManager();
		IpTranslation tranfer = new IpTranslation();
		try {
			Iterator iterator = oracledatas.keySet().iterator();
			while (iterator.hasNext()) {
				String ipid = String.valueOf(iterator.next());
				if (ipid == null || ipid.equals("")) {
					continue;
				}
				// String ip = ipid.split(":")[0];
				// String id = ipid.split(":")[1];
				String ip = ipid.split(":")[0];
				String sid = ipid.split(":")[1];
				String hex = tranfer.formIpToHex(ip);
				String serverip = hex + ":" + sid;
				Hashtable oracledata = (Hashtable) oracledatas.get(ipid);
				if (oracledata == null || oracledata.isEmpty()) {
					continue;
				}
				if (oracledata.containsKey("oracleTopSqlSortVector")) {
					Vector<OracleTopSqlSort> topSqlSortVector = (Vector<OracleTopSqlSort>) oracledata
							.get("oracleTopSqlSortVector");
					if (topSqlSortVector != null && topSqlSortVector.size() > 0) {
						topSqlSortHash.put(serverip, topSqlSortVector);
					}
				}
				if (oracledata.containsKey("oracleTopSqlReadWriteVector")) {
					Vector<OracleTopSqlReadWrite> topSqlReadWriteVector = (Vector<OracleTopSqlReadWrite>) oracledata
							.get("oracleTopSqlReadWriteVector");
					if (topSqlReadWriteVector != null
							&& topSqlReadWriteVector.size() > 0) {
						topSqlReadWriteHash
								.put(serverip, topSqlReadWriteVector);
					}
				}
				if (oracledata.containsKey("oracleLockInfo")) {
					OracleLockInfo oracleLockInfo = (OracleLockInfo) oracledata
							.get("oracleLockInfo");
					if (oracleLockInfo != null) {
						oracleLockInfoHash.put(serverip, oracleLockInfo);
					}
				}
				if (oracledata.containsKey("baseInfoHash")) {
					Hashtable tempHashtable = (Hashtable) oracledata
							.get("baseInfoHash");
					if (tempHashtable != null && !tempHashtable.isEmpty()) {
						baseInfoHash.put(serverip, tempHashtable);
					}
				}
				if (oracledata.containsKey("sysValue")) {
					Hashtable tempHashtable = (Hashtable) oracledata
							.get("sysValue");
					if (tempHashtable != null && !tempHashtable.isEmpty()) {
						sysValueHash.put(serverip, tempHashtable);
					}
				}
				if (oracledata.containsKey("memValue")) {
					Hashtable tempHashtable = (Hashtable) oracledata
							.get("memValue");
					if (tempHashtable != null && !tempHashtable.isEmpty()) {
						memValueHash.put(serverip, tempHashtable);
					}
				}
				if (oracledata.containsKey("memPerfValue")) {
					Hashtable tempHashtable = (Hashtable) oracledata
							.get("memPerfValue");
					if (tempHashtable != null && !tempHashtable.isEmpty()) {
						memPerfValueHash.put(serverip, tempHashtable);
					}
				}
				if (oracledata.containsKey("tableinfo_v")) {
					Vector tempVector = (Vector) oracledata.get("tableinfo_v");
					if (tempVector != null && tempVector.size() > 0) {
						tableinfoHash.put(serverip, tempVector);
					}
				}
				if (oracledata.containsKey("rollbackinfo_v")) {
					Vector tempVector = (Vector) oracledata
							.get("rollbackinfo_v");
					if (tempVector != null) {
						rollbackinfo_vHash.put(serverip, tempVector);
					}
				}
				if (oracledata.containsKey("lockinfo_v")) {
					Vector tempVector = (Vector) oracledata.get("lockinfo_v");
					if (tempVector != null && tempVector.size() > 0) {
						lockinfo_vHash.put(serverip, tempVector);
					}
				}
				if (oracledata.containsKey("info_v")) {
					Vector tempVector = (Vector) oracledata.get("info_v");
					if (tempVector != null && tempVector.size() > 0) {
						info_vHash.put(serverip, tempVector);
					}
				}

				// #######################
				if (oracledata.containsKey("table_v")) {
					Vector tempVector = (Vector) oracledata.get("table_v");
					if (tempVector != null && tempVector.size() > 0) {
						table_vHash.put(serverip, tempVector);
					}
				}
				if (oracledata.containsKey("sql_v")) {
					Vector tempVector = (Vector) oracledata.get("sql_v");
					if (tempVector != null && tempVector.size() > 0) {
						sql_vHash.put(serverip, tempVector);
					}
				}
				if (oracledata.containsKey("contrFile_v")) {
					Vector tempVector = (Vector) oracledata.get("contrFile_v");
					if (tempVector != null && tempVector.size() > 0) {
						contrFile_vHash.put(serverip, tempVector);
					}
				}
				if (oracledata.containsKey("isArchive_h")) {
					Hashtable tempHashtable = (Hashtable) oracledata.get("isArchive_h");
					if (tempHashtable != null && !tempHashtable.isEmpty()) {
						isArchive_hHash.put(serverip, tempHashtable);
					}
				}
				// ########################
				if (oracledata.containsKey("keepObj_v")) {
					Vector tempVector = (Vector) oracledata.get("keepObj_v");
					if (tempVector != null && tempVector.size() > 0) {
						keepObj_vHash.put(serverip, tempVector);
					}
				}
				if (oracledata.containsKey("lstrnStatu")) {
					String lstrnStatu = (String) oracledata.get("lstrnStatu");
					if (lstrnStatu != null) {
						lstrnStatuHash.put(serverip, lstrnStatu);
					}
				}
				if (oracledata.containsKey("extent_v")) {
					Vector tempVector = (Vector) oracledata.get("extent_v");
					if (tempVector != null && tempVector.size() > 0) {
						extent_vHash.put(serverip, tempVector);
					}
				}
				if (oracledata.containsKey("logFile_v")) {
					Vector tempVector = (Vector) oracledata.get("logFile_v");
					if (tempVector != null && tempVector.size() > 0) {
						logFile_vHash.put(serverip, tempVector);
					}
				}
				if (oracledata.containsKey("userinfo_h")) {
					Hashtable tempHashtable = (Hashtable) oracledata
							.get("userinfo_h");
					if (tempHashtable != null && !tempHashtable.isEmpty()) {
						userinfo_hHash.put(serverip, tempHashtable);
					}
				}
				if (oracledata.containsKey("cursors")) {
					Hashtable tempHashtable = (Hashtable) oracledata
							.get("cursors");
					if (tempHashtable != null && !tempHashtable.isEmpty()) {
						cursorsHash.put(serverip, tempHashtable);
					}
				}
				if (oracledata.containsKey("wait")) {
					Vector tempVector = (Vector) oracledata.get("wait");
					if (tempVector != null && tempVector.size() > 0) {
						waitHash.put(serverip, tempVector);
					}
				}
				if (oracledata.containsKey("dbio")) {
					Hashtable tempHashtable = (Hashtable) oracledata
							.get("dbio");
					if (tempHashtable != null && !tempHashtable.isEmpty()) {
						dbioHash.put(serverip, tempHashtable);
					}
				}
				if (oracledata.containsKey("status")) {
					String str = (String) oracledata.get("status");
					if (str != null) {
						statusHash.put(serverip, str);
					}
				}
			}
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);

			// 排序最多的前10条sql
			if (topSqlSortHash != null && !topSqlSortHash.isEmpty()) {
				Iterator ipidIterator = topSqlSortHash.keySet().iterator();
				String sqltext = "";
				StringBuffer sql = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Vector<OracleTopSqlSort> oracleTopSqlSortVector = (Vector<OracleTopSqlSort>) topSqlSortHash
							.get(serverip);
					String deletesql = "delete from nms_oratopsql_sort where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);
					for (OracleTopSqlSort oracleTopSqlSort : oracleTopSqlSortVector) {
						if (oracleTopSqlSort != null) {
							// System.out.println(sqltext);
							sqltext = oracleTopSqlSort.getSqltext();

							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sqltext = sqltext.replaceAll("\\\\", "/");
								sqltext = sqltext.replaceAll("\'", "\\\\'");
								sqltext = sqltext.replaceAll("\"", "\\\\'");
							} else if ("oracle"
									.equalsIgnoreCase(SystemConstant.DBType)) {
								sqltext = sqltext.replaceAll("\\\\", "/");
								sqltext = sqltext.replaceAll("\'", "''");
								sqltext = sqltext.replaceAll("\"", "''");
							}

							sql = new StringBuffer();
							sql
									.append("insert into nms_oratopsql_sort(serverip,sqltext,sorts,executions,sortsexec,mon_time) values ('");
							sql.append(serverip);
							sql.append("','");
							sql.append(sqltext);
							sql.append("','");
							sql.append(oracleTopSqlSort.getSorts());
							sql.append("','");
							sql.append(oracleTopSqlSort.getExecutions());
							sql.append("','");
							sql.append(oracleTopSqlSort.getSortsexec());
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql.append("','");
								sql.append(montime);
								sql.append("')");
							} else if ("oracle"
									.equalsIgnoreCase(SystemConstant.DBType)) {
								sql.append("',");
								sql.append("to_date('" + montime
										+ "','YYYY-MM-DD HH24:MI:SS')");
								sql.append(")");
							}

							// System.out.println(sql.toString());
							dbManager.addBatch(sql.toString());
						}
					}
				}
			}

			// 磁盘读写最多的前10条sql
			if (topSqlReadWriteHash != null && !topSqlReadWriteHash.isEmpty()) {
				Iterator ipidIterator = topSqlReadWriteHash.keySet().iterator();
				String sqltext = null;
				StringBuffer sql = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Vector<OracleTopSqlReadWrite> oracleTopSqlReadWriteVector = (Vector<OracleTopSqlReadWrite>) topSqlReadWriteHash
							.get(serverip);
					String deletesql = "delete from nms_oratopsql_readwrite where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);
					for (OracleTopSqlReadWrite oracleTopSqlReadWrite : oracleTopSqlReadWriteVector) {
						if (oracleTopSqlReadWrite != null) {
							sqltext = oracleTopSqlReadWrite.getSqltext();
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sqltext = sqltext.replaceAll("\\\\", "/");
								sqltext = sqltext.replaceAll("\'", "\\\\'");
								sqltext = sqltext.replaceAll("\"", "\\\\'");
							} else if ("oracle"
									.equalsIgnoreCase(SystemConstant.DBType)) {
								sqltext = sqltext.replaceAll("\\\\", "/");
								sqltext = sqltext.replaceAll("\'", "''");
								sqltext = sqltext.replaceAll("\"", "''");
							}

							sql = new StringBuffer();
							sql
									.append("insert into nms_oratopsql_readwrite(serverip,sqltext,totaldisk,totalexec,diskreads,mon_time) values ('");
							sql.append(serverip);
							sql.append("','");
							sql.append(sqltext);
							sql.append("','");
							sql.append(oracleTopSqlReadWrite.getTotaldisk());
							sql.append("','");
							sql.append(oracleTopSqlReadWrite.getTotalexec());
							sql.append("','");
							sql.append(oracleTopSqlReadWrite.getDiskreads());
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql.append("','");
								sql.append(montime);
								sql.append("')");
							} else if ("oracle"
									.equalsIgnoreCase(SystemConstant.DBType)) {
								sql.append("',");
								sql.append("to_date('" + montime
										+ "','YYYY-MM-DD HH24:MI:SS')");
								sql.append(")");
							}

							// System.out.println(sql.toString());
							dbManager.addBatch(sql.toString());
						}
					}
				}
			}

			// 锁信息
			if (oracleLockInfoHash != null && !oracleLockInfoHash.isEmpty()) {
				Iterator ipidIterator = oracleLockInfoHash.keySet().iterator();
				StringBuffer sql = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					OracleLockInfo oracleLockInfo = (OracleLockInfo) oracleLockInfoHash
							.get(serverip);
					String deletesql = "delete from nms_oralockinfo where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);
					String[] fields = { "lockwaitcount", "deadlockcount",
							"processcount", "maxprocesscount",
							"currentsessioncount", "useablesessioncount",
							"useablesessionpercent", "lockdsessioncount",
							"rollbackcommitpercent", "rollbacks" };
					if (oracleLockInfo != null) {
						// 此处可以利用反射来插入对象...
						for (String field : fields) {
							String value = "--";
							Object obj = ReflactUtil.invokeGet(oracleLockInfo,
									field);
							if (obj != null && obj instanceof String) {
								value = (String) obj;
							}
							sql = new StringBuffer();
							sql
									.append("insert into nms_oralockinfo(serverip,entity,subentity,thevalue,mon_time) values ('");
							sql.append(serverip);
							sql.append("','");
							sql.append("lockinfo");
							sql.append("','");
							sql.append(field);
							sql.append("','");
							sql.append(value);
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql.append("','");
								sql.append(montime);
								sql.append("')");
							} else if ("oracle"
									.equalsIgnoreCase(SystemConstant.DBType)) {
								sql.append("',");
								sql.append("to_date('" + montime
										+ "','YYYY-MM-DD HH24:MI:SS')");
								sql.append(")");
							}
							dbManager.addBatch(sql.toString());
						}
					}
				}
			}
			
			

			// 基本信息
			if (baseInfoHash != null && !baseInfoHash.isEmpty()) {
				Iterator ipidIterator = baseInfoHash.keySet().iterator();
				StringBuffer sql = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Hashtable<String, String> baseInfo = (Hashtable<String, String>) baseInfoHash
							.get(serverip);
					String deletesql = "delete from nms_orabaseinfo where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);
					Iterator<String> keyIterator = baseInfo.keySet().iterator();
					while (keyIterator.hasNext()) {
						String subentity = keyIterator.next();
						String thevalue = baseInfo.get(subentity);
						thevalue = thevalue.replaceAll("\\\\", "/");
						sql = new StringBuffer();
						sql
								.append("insert into nms_orabaseinfo(serverip,entity,subentity,thevalue,mon_time) values ('");
						sql.append(serverip);
						sql.append("','");
						sql.append("baseinfo");
						sql.append("','");
						sql.append(subentity);
						sql.append("','");
						sql.append(thevalue);
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql.append("','");
							sql.append(montime);
							sql.append("')");
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql.append("',");
							sql.append("to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS')");
							sql.append(")");
						}
						// System.out.println("sql===="+sql.toString());
						dbManager.addBatch(sql.toString());
						sql = null;
					}
				}
			}

			// 遍历系统信息
			if (sysValueHash != null && !sysValueHash.isEmpty()) {
				Iterator ipidIterator = sysValueHash.keySet().iterator();
				String sql = null;
				String serverip = null;
				Hashtable sysValue = null;
				while (ipidIterator.hasNext()) {
					serverip = String.valueOf(ipidIterator.next());
					sysValue = (Hashtable) sysValueHash.get(serverip);
					String deletesql = "delete from nms_orasys where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);

					String INSTANCE_NAME = sysValue.get("INSTANCE_NAME")
							.toString();
					String HOST_NAME = sysValue.get("HOST_NAME").toString();
					String DBNAME = sysValue.get("DBNAME").toString();
					String VERSION = sysValue.get("VERSION").toString();
					String STARTUP_TIME = sysValue.get("STARTUP_TIME")
							.toString();
					String STATUS = sysValue.get("STATUS").toString();
					String ARCHIVER = sysValue.get("ARCHIVER").toString();
					String java_pool = sysValue.get("java_pool").toString();
					Vector banners = (Vector) sysValue.get("BANNER");
					sql = "insert into nms_orasys(serverip,INSTANCE_NAME,HOST_NAME,DBNAME,VERSION,STARTUP_TIME,status,ARCHIVER,BANNER,java_pool,mon_time) "
							+ "values('"
							+ serverip
							+ "','"
							+ INSTANCE_NAME
							+ "','"
							+ INSTANCE_NAME
							+ "','"
							+ DBNAME
							+ "','"
							+ VERSION
							+ "','"
							+ STARTUP_TIME
							+ "','"
							+ STATUS
							+ "','" + ARCHIVER + "','" + "" + "','" + java_pool;
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sql = sql + "','" + montime + "')";
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sql = sql + "',to_date('" + montime
								+ "','YYYY-MM-DD HH24:MI:SS'))";
					}

					dbManager.addBatch(sql);
				}
			}

			// 遍历memValue
			if (memValueHash != null && !memValueHash.isEmpty()) {
				Iterator ipidIterator = memValueHash.keySet().iterator();
				StringBuffer sBuffer = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Hashtable memValue = (Hashtable) memValueHash.get(serverip);
					String deletesql = "delete from nms_oramemvalue where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);

					sBuffer = new StringBuffer();
					String aggregate_pga_auto_target = String.valueOf(memValue
							.get("aggregate_PGA_auto_target"));
					String total_pga_used_for_manual_workareas = String
							.valueOf(memValue
									.get("total_PGA_used_for_manual_workareas"));
					String total_pga_inuse = String.valueOf(memValue
							.get("total_PGA_inuse"));
					String maximum_pga_allocated = String.valueOf(memValue
							.get("maximum_PGA_allocated"));
					String cache_hit_percentage = String.valueOf(memValue
							.get("cache_hit_percentage"));
					String recycle_buffer_cache = String.valueOf(memValue
							.get("RECYCLE_buffer_cache"));
					String keep_buffer_cache = String.valueOf(memValue
							.get("KEEP_buffer_cache"));
					String process_count = String.valueOf(memValue
							.get("process_count"));
					String total_pga_used_for_auto_workareas = String
							.valueOf(memValue
									.get("total_PGA_used_for_auto_workareas"));
					String asm_buffer_cache = String.valueOf(memValue
							.get("ASM_Buffer_Cache"));
					String over_allocation_count = String.valueOf(memValue
							.get("over_allocation_count"));
					String bytes_processed = String.valueOf(memValue
							.get("bytes_processed"));
					String java_pool = String
							.valueOf(memValue.get("java_pool"));
					String maximum_pga_used_for_manual_workareas = String
							.valueOf(memValue
									.get("maximum_PGA_used_for_manual_workareas"));
					String streams_pool = String.valueOf(memValue
							.get("streams_pool"));
					String default_2k_buffer_cache = String.valueOf(memValue
							.get("DEFAULT_2K_buffer_cache"));
					String max_processes_count = String.valueOf(memValue
							.get("max_processes_count"));
					String total_pga_allocated = String.valueOf(memValue
							.get("total_PGA_allocated"));
					String default_4k_buffer_cache = String.valueOf(memValue
							.get("DEFAULT_4K_buffer_cache"));
					String shared_pool = String.valueOf(memValue
							.get("shared_pool"));
					String default_32k_buffer_cache = String.valueOf(memValue
							.get("DEFAULT_32K_buffer_cache"));
					String default_buffer_cache = String.valueOf(memValue
							.get("DEFAULT_buffer_cache"));
					String large_pool = String.valueOf(memValue
							.get("large_pool"));
					String aggregate_pga_target_parameter = String
							.valueOf(memValue
									.get("aggregate_PGA_target_parameter"));
					String default_16k_buffer_cache = String.valueOf(memValue
							.get("DEFAULT_16K_buffer_cache"));
					String global_memory_bound = String.valueOf(memValue
							.get("global_memory_bound"));
					String default_8k_buffer_cache = String.valueOf(memValue
							.get("DEFAULT_8K_buffer_cache"));
					String extra_bytes_read_written = String.valueOf(memValue
							.get("extra_bytes_read/written"));
					String pga_memory_freed_back_to_os = String
							.valueOf(memValue
									.get("PGA_memory_freed_back_to_OS"));
					String total_freeable_pga_memory = String.valueOf(memValue
							.get("total_freeable_PGA_memory"));
					String recompute_count_total = String.valueOf(memValue
							.get("recompute_count_(total)"));
					String maximum_pga_used_for_auto_workareas = String
							.valueOf(memValue
									.get("maximum_PGA_used_for_auto_workareas"));

					// sBuffer.append("insert into nms_oramemvalue(serverip,
					// aggregate_pga_auto_target,
					// total_pga_used_for_manual_workareas, total_pga_inuse, " +
					// "maximum_pga_allocated,cache_hit_percentage,recycle_buffer_cache,keep_buffer_cache,process_count
					// ,total_pga_used_for_auto_workareas," +
					// "asm_buffer_cache,over_allocation_count,bytes_processed,java_pool,maximum_pga_used_for_manual_workareas,streams_pool,"
					// +
					// "default_2k_buffer_cache,max_processes_count,total_pga_allocated,default_4k_buffer_cache,shared_pool,"
					// +
					// "default_32k_buffer_cache,default_buffer_cache,large_pool,aggregate_pga_target_parameter,default_16k_buffer_cache,"
					// +
					// "global_memory_bound,default_8k_buffer_cache,extra_bytes_read_written,pga_memory_freed_back_to_os,total_freeable_pga_memory,recompute_count_total,"
					// +
					// "maximum_pga_used_for_auto_workareas,mon_time) ");
					sBuffer
							.append("insert into nms_oramemvalue(serverip, agg_pga_auto_target, tpga_used_manu_workareas, total_pga_inuse, "
									+ "maximum_pga_allocated,cache_hit_percentage,recycle_buffer_cache,keep_buffer_cache,process_count ,tpga_used_auto_workareas,"
									+ "asm_buffer_cache,over_allocation_count,bytes_processed,java_pool,maxpga_used_manu_workareas,streams_pool,"
									+ "default_2k_buffer_cache,max_processes_count,total_pga_allocated,default_4k_buffer_cache,shared_pool,"
									+ "default_32k_buffer_cache,default_buffer_cache,large_pool,agg_pga_target_parameter ,default_16k_buffer_cache,"
									+ "global_memory_bound,default_8k_buffer_cache,extra_bytes_read_written,pga_mem_freed_back_os,tot_free_pga_memory,recom_count_total,"
									+ "maxpga_used_auto_workareas,mon_time) ");
					sBuffer.append(" values('");
					sBuffer.append(serverip);
					sBuffer.append("','");
					sBuffer.append(aggregate_pga_auto_target);
					sBuffer.append("','");
					sBuffer.append(total_pga_used_for_manual_workareas);
					sBuffer.append("','");
					sBuffer.append(total_pga_inuse);
					sBuffer.append("','");
					sBuffer.append(maximum_pga_allocated);
					sBuffer.append("','");
					sBuffer.append(cache_hit_percentage);
					sBuffer.append("','");
					sBuffer.append(recycle_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(keep_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(process_count);
					sBuffer.append("','");
					sBuffer.append(total_pga_used_for_auto_workareas);
					sBuffer.append("','");
					sBuffer.append(asm_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(over_allocation_count);
					sBuffer.append("','");
					sBuffer.append(bytes_processed);
					sBuffer.append("','");
					sBuffer.append(java_pool);
					sBuffer.append("','");
					sBuffer.append(maximum_pga_used_for_manual_workareas);
					sBuffer.append("','");
					sBuffer.append(streams_pool);
					sBuffer.append("','");
					sBuffer.append(default_2k_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(max_processes_count);
					sBuffer.append("','");
					sBuffer.append(total_pga_allocated);
					sBuffer.append("','");
					sBuffer.append(default_4k_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(shared_pool);
					sBuffer.append("','");
					sBuffer.append(default_32k_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(default_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(large_pool);
					sBuffer.append("','");
					sBuffer.append(aggregate_pga_target_parameter);
					sBuffer.append("','");
					sBuffer.append(default_16k_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(global_memory_bound);
					sBuffer.append("','");
					sBuffer.append(default_8k_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(extra_bytes_read_written);
					sBuffer.append("','");
					sBuffer.append(pga_memory_freed_back_to_os);
					sBuffer.append("','");
					sBuffer.append(total_freeable_pga_memory);
					sBuffer.append("','");
					sBuffer.append(recompute_count_total);
					sBuffer.append("','");
					sBuffer.append(maximum_pga_used_for_auto_workareas);
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(montime);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + montime
								+ "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}

					dbManager.addBatch(sBuffer.toString());
				}
			}

			// 遍历memPerfValue
			if (memPerfValueHash != null && !memPerfValueHash.isEmpty()) {
				Iterator ipidIterator = memPerfValueHash.keySet().iterator();
				String sql = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Hashtable memPerfValue = (Hashtable) memPerfValueHash
							.get(serverip);
					String deletesql = "delete from nms_oramemperfvalue where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);

					String pctmemorysorts = String.valueOf(memPerfValue
							.get("pctmemorysorts"));
					String pctbufgets = String.valueOf(memPerfValue
							.get("pctbufgets"));
					String dictionarycache = String.valueOf(memPerfValue
							.get("dictionarycache"));
					String buffercache = String.valueOf(memPerfValue
							.get("buffercache"));
					String librarycache = String.valueOf(memPerfValue
							.get("librarycache"));
					sql = "insert into nms_oramemperfvalue(serverip, pctmemorysorts, pctbufgets, dictionarycache, buffercache, librarycache,mon_time) "
							+ "values('"
							+ serverip
							+ "','"
							+ pctmemorysorts
							+ "','"
							+ pctbufgets
							+ "','"
							+ dictionarycache
							+ "','" + buffercache + "','" + librarycache;

					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sql = sql + "','" + montime + "')";
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sql = sql + "',to_date('" + montime
								+ "','YYYY-MM-DD HH24:MI:SS'))";
					}

					dbManager.addBatch(sql);
				}
			}
			
			// 遍历cursorsHash
			if (cursorsHash != null && !cursorsHash.isEmpty()) {
				Iterator ipidIterator = cursorsHash.keySet().iterator();
				String sql = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Hashtable cursorsValue = (Hashtable) cursorsHash.get(serverip);
					String deletesql = "delete from nms_oracursors where serverip='"+ serverip + "'";
					dbManager.addBatch(deletesql);

					String opencur = String.valueOf(cursorsValue.get("opencur"));
					String curconnect = String.valueOf(cursorsValue.get("curconnect"));
					sql = "insert into nms_oracursors(serverip, opencur, curconnect,mon_time) "
							+ "values('"
							+ serverip
							+ "','"
							+ opencur
							+ "','"
							+ curconnect;

					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sql = sql + "','" + montime + "')";
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sql = sql + "',to_date('" + montime
								+ "','YYYY-MM-DD HH24:MI:SS'))";
					}

					dbManager.addBatch(sql);
				}
			}

			// 遍历tableinfo
			if (tableinfoHash != null && !tableinfoHash.isEmpty()) {
				Iterator ipidIterator = tableinfoHash.keySet().iterator();
				String serverip = null;
				Vector tableinfo = null;
				String deletesql = null;
				String sql = null;
				while (ipidIterator.hasNext()) {
					serverip = String.valueOf(ipidIterator.next());
					tableinfo = (Vector) tableinfoHash.get(serverip);
					deletesql = "delete from nms_oraspaces where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);

					for (int k = 0; k < tableinfo.size(); k++) {
						Hashtable ht = (Hashtable) tableinfo.get(k);
						String file_name = ht.get("file_name").toString();
						String tablespace = ht.get("tablespace").toString();
						String size_mb = ht.get("size_mb").toString();
						String free_mb = ht.get("free_mb").toString();
						String percent_free = ht.get("percent_free").toString();
						String status = ht.get("status").toString();
						String chunks = ht.get("chunks").toString();// 每个表空间空闲块数目
						file_name = file_name.replaceAll("\\\\", "/");
						sql = "";
						sql = "insert into nms_oraspaces(serverip,tablespace,free_mb,size_mb,percent_free,file_name,status,chunks,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ tablespace
								+ "','"
								+ free_mb
								+ "','"
								+ size_mb
								+ "','"
								+ percent_free
								+ "','"
								+ file_name
								+ "','"
								+ status + "','" + chunks;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}

						dbManager.addBatch(sql);
					}
				}
			}

			// 遍历rollbackinfo_v
			if (rollbackinfo_vHash != null && !rollbackinfo_vHash.isEmpty()) {
				Iterator ipidIterator = rollbackinfo_vHash.keySet().iterator();
				String serverip = null;
				Vector rollbackinfo_v = null;
				String deletesql = null;
				String sql = null;
				while (ipidIterator.hasNext()) {
					serverip = String.valueOf(ipidIterator.next());
					rollbackinfo_v = (Vector) rollbackinfo_vHash.get(serverip);
					deletesql = "delete from nms_orarollback where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);

					for (int k = 0; k < rollbackinfo_v.size(); k++) {
						Hashtable ht = (Hashtable) rollbackinfo_v.get(k);
						String rollback = ht.get("rollback_segment").toString()
								.trim();
						String wraps = ht.get("wraps").toString();
						String shrink = ht.get("shrinks").toString();
						String ashrink = ht.get("average_shrink").toString();
						String extend = ht.get("extends").toString();
						sql = "";
						sql = "insert into nms_orarollback(serverip,rollback,wraps,shrink,ashrink,extend,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ rollback
								+ "','"
								+ wraps
								+ "','"
								+ shrink
								+ "','"
								+ ashrink + "','" + extend;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}

						dbManager.addBatch(sql);
					}
				}
			}
			
			// 遍历job
			if (jobhash != null && !jobhash.isEmpty()) {
				Iterator ipidIterator = jobhash.keySet().iterator();
				String serverip = null;
				Vector job_v = null;
				String deletesql = null;
				String sql = null;
				while (ipidIterator.hasNext()) {
					serverip = String.valueOf(ipidIterator.next());
					job_v = (Vector) jobhash.get(serverip);
					deletesql = "delete from nms_orajob where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);

					for (int k = 0; k < job_v.size(); k++) {
						Hashtable ht = (Hashtable) job_v.get(k);
						String job = ht.get("job").toString().trim();
						String loguser = ht.get("log_user").toString();
						String lastdate = ht.get("last_date").toString();
						String failures = ht.get("failures").toString();
						sql = "";
						sql = "insert into nms_orajob(serverip,job,loguser,lastdate,failures,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ job;
								
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = sql + "','" + lastdate + "')";
								} else if ("oracle"
										.equalsIgnoreCase(SystemConstant.DBType)) {
									sql = sql + "',to_date('" + montime
											+ "','YYYY-MM-DD HH24:MI:SS'))";
								}
								sql = sql + "','"+ failures
								;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}

						dbManager.addBatch(sql);
					}
				}
			}

			// 遍历lockinfo_v
			if (lockinfo_vHash != null && !lockinfo_vHash.isEmpty()) {
				Iterator ipidIterator = lockinfo_vHash.keySet().iterator();
				String serverip = null;
				Vector lockinfo_v = null;
				String deletesql = null;
				String sql = null;
				while (ipidIterator.hasNext()) {
					serverip = String.valueOf(ipidIterator.next());
					lockinfo_v = (Vector) lockinfo_vHash.get(serverip);
					deletesql = "delete from nms_oralock where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);

					for (int k = 0; k < lockinfo_v.size(); k++) {
						Hashtable ht = (Hashtable) lockinfo_v.get(k);
						String usernames = ht.get("username").toString().trim();
						String status = ht.get("status").toString().trim();
						String machine = ht.get("machine").toString().trim();
						String sessiontype = ht.get("sessiontype").toString()
								.trim();
						String logontime = ht.get("logontime").toString()
								.trim();
						String program = ht.get("program").toString().trim();
						String locktype = ht.get("locktype").toString().trim();
						String lmode = ht.get("lmode").toString().trim();
						String requeststr = ht.get("request").toString().trim();
						sql = "";
						sql = "insert into nms_oralock(serverip,username,status,machine,sessiontype,logontime,program,locktype,lmode,requeststr,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ usernames
								+ "','"
								+ status
								+ "','"
								+ machine
								+ "','"
								+ sessiontype
								+ "','"
								+ logontime
								+ "','"
								+ program
								+ "','"
								+ locktype
								+ "','"
								+ lmode
								+ "','" + requeststr;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}

						dbManager.addBatch(sql);
					}
				}
			}

			// 遍历table_v
			if (table_vHash != null && !table_vHash.isEmpty()) {
				Iterator ipidIterator = table_vHash.keySet().iterator();
				String serverip = null;
				Vector table_v = null;
				String deletesql = null;
				String sql = null;
				while (ipidIterator.hasNext()) {
					serverip = String.valueOf(ipidIterator.next());
					table_v = (Vector) table_vHash.get(serverip);
					deletesql = "delete from nms_oratables where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);

					for (int k = 0; k < table_v.size(); k++) {
						Hashtable ht = (Hashtable) table_v.get(k);
						String spaces = ht.get("spaces").toString().trim();
						String segment_name = ht.get("segment_name").toString();
						sql = "";
						sql = "insert into nms_oratables(serverip,segment_name,spaces,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ segment_name
								+ "','" + spaces;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						// System.out.println(sql);
						dbManager.addBatch(sql);
					}
				}
			}

			// 遍历sql_v
			if (sql_vHash != null && !sql_vHash.isEmpty()) {
				Iterator ipidIterator = sql_vHash.keySet().iterator();
				String serverip = null;
				Vector sql_v = null;
				String deletesql = null;
				String sql = null;
				String sql_text = null;
				while (ipidIterator.hasNext()) {
					serverip = String.valueOf(ipidIterator.next());
					sql_v = (Vector) sql_vHash.get(serverip);
					deletesql = "delete from nms_oratopsql where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);

					for (int k = 0; k < sql_v.size(); k++) {
						Hashtable ht = (Hashtable) sql_v.get(k);
						sql_text = ht.get("sql_text").toString();
						String pct_bufgets = ht.get("pct_bufgets").toString();
						String usernames = ht.get("username").toString();
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql_text = sql_text.replaceAll("\\\\", "/");
							sql_text = sql_text.replaceAll("\"", "\'");
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql_text = sql_text.replaceAll("\\\\", "/");
							sql_text = sql_text.replaceAll("\'", "''");
							sql_text = sql_text.replaceAll("\"", "''");
						}

						sql = "";
						sql = "insert into nms_oratopsql(serverip,sql_text,pct_bufgets,username,mon_time) "
								+ "values('" + serverip;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',\"" + sql_text + "\",'"
									+ pct_bufgets + "','" + usernames;
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + sql_text + "','" + pct_bufgets
									+ "','" + usernames;
						}

						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						// System.out.println(sql);
						dbManager.addBatch(sql);
					}
				}
			}

			// 遍历contrFile_v
			if (contrFile_vHash != null && !contrFile_vHash.isEmpty()) {
				Iterator ipidIterator = contrFile_vHash.keySet().iterator();
				String serverip = null;
				Vector contrFile_v = null;
				String deletesql = null;
				String sql = null;
				while (ipidIterator.hasNext()) {
					serverip = String.valueOf(ipidIterator.next());
					contrFile_v = (Vector) contrFile_vHash.get(serverip);
					deletesql = "delete from nms_oracontrfile where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);
					for (int k = 0; k < contrFile_v.size(); k++) {
						Hashtable ht = (Hashtable) contrFile_v.get(k);
						String status = ht.get("status").toString();
						String name = ht.get("name").toString();
						String is_recovery_dest_file = CommonUtil.getValue(ht,
								"is_recovery_dest_file", "--");
						String block_size = CommonUtil.getValue(ht,
								"block_size", "--");
						String file_size_blks = CommonUtil.getValue(ht,
								"file_size_blks", "--");
						name = name.replaceAll("\\\\", "/");
						sql = "";
						sql = "insert into nms_oracontrfile(serverip,status,name,is_recovery_dest_file,block_size,file_size_blks,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ status
								+ "','"
								+ name
								+ "','"
								+ is_recovery_dest_file
								+ "','" + block_size + "','" + file_size_blks;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						dbManager.addBatch(sql);
					}
				}
			}

			// 遍历isArchive_h
			if (isArchive_hHash != null && !isArchive_hHash.isEmpty()) {
				Iterator ipidIterator = isArchive_hHash.keySet().iterator();
				String sql = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Hashtable isArchive_h = (Hashtable) isArchive_hHash
							.get(serverip);
					String deletesql = "delete from nms_oraisarchive where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);
					String created = isArchive_h.get("CREATED").toString();
					String log_mode = isArchive_h.get("Log_Mode").toString();
					Date createdDate = new SimpleDateFormat(
							"MM/dd/yyyy HH:mm:ss").parse(created);
					created = sdf.format(createdDate);
					sql = "insert into nms_oraisarchive(serverip,created,log_mode,mon_time) "
							+ "values('"
							+ serverip
							+ "','"
							+ created
							+ "','"
							+ log_mode;
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sql = sql + "','" + montime + "')";
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sql = sql + "',to_date('" + montime
								+ "','YYYY-MM-DD HH24:MI:SS'))";
					}
					dbManager.addBatch(sql);
				}
			}

			// 遍历keepObj_v
			if (keepObj_vHash != null && !keepObj_vHash.isEmpty()) {
				Iterator ipidIterator = contrFile_vHash.keySet().iterator();
				StringBuffer sBuffer = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Vector keepObj_v = (Vector) keepObj_vHash.get(serverip);
					String deletesql = "delete from nms_orakeepobj where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);

					for (int k = 0; k < keepObj_v.size(); k++) {
						Hashtable ht = (Hashtable) keepObj_v.get(k);
						String owner = ht.get("owner").toString();
						String name = ht.get("name").toString();
						String db_link = ht.get("db_link").toString();
						String namespace = ht.get("namespace").toString();
						String type = ht.get("type").toString();
						String sharable_mem = ht.get("sharable_mem").toString();
						String loads = ht.get("loads").toString();
						String executions = ht.get("executions").toString();
						String locks = ht.get("locks").toString();
						String pins = ht.get("pins").toString();
						String kept = ht.get("kept").toString();
						String child_latch = ht.get("child_latch").toString();
						String invalidations = CommonUtil.getValue(ht,
								"invalidations", "--");
						sBuffer = new StringBuffer();
						sBuffer
								.append("insert into nms_orakeepobj(serverip, owner, name, db_link, namespace, type, sharable_mem, loads,"
										+ " executions, locks, pins, kept, child_latch, invalidations, mon_time) ");
						sBuffer.append(" values('");
						sBuffer.append(serverip);
						sBuffer.append("','");
						sBuffer.append(owner);
						sBuffer.append("','");
						sBuffer.append(name);
						sBuffer.append("','");
						sBuffer.append(db_link);
						sBuffer.append("','");
						sBuffer.append(namespace);
						sBuffer.append("','");
						sBuffer.append(type);
						sBuffer.append("','");
						sBuffer.append(sharable_mem);
						sBuffer.append("','");
						sBuffer.append(loads);
						sBuffer.append("','");
						sBuffer.append(executions);
						sBuffer.append("','");
						sBuffer.append(locks);
						sBuffer.append("','");
						sBuffer.append(pins);
						sBuffer.append("','");
						sBuffer.append(kept);
						sBuffer.append("','");
						sBuffer.append(child_latch);
						sBuffer.append("','");
						sBuffer.append(invalidations);
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sBuffer.append("','");
							sBuffer.append(montime);
							sBuffer.append("')");
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sBuffer.append("',");
							sBuffer.append("to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS')");
							sBuffer.append(")");
						}
						dbManager.addBatch(sBuffer.toString());
					}
				}
			}

			// 遍历lstrnStatu
			if (lstrnStatuHash != null && !lstrnStatuHash.isEmpty()) {
				Iterator ipidIterator = contrFile_vHash.keySet().iterator();
				String sql = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					String lstrnStatu = (String) lstrnStatuHash.get(serverip);
					String status = (String) statusHash.get(serverip);
					String deletesql = "delete from nms_orastatus where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);
					sql = "insert into nms_orastatus(serverip, lstrnstatu, status,mon_time) "
							+ "values('"
							+ serverip
							+ "','"
							+ lstrnStatu
							+ "','" + status;
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sql = sql + "','" + montime + "')";
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sql = sql + "',to_date('" + montime
								+ "','YYYY-MM-DD HH24:MI:SS'))";
					}
					dbManager.addBatch(sql);
				}
			}

			// 遍历extent_v
			if (extent_vHash != null && !extent_vHash.isEmpty()) {
				Iterator ipidIterator = extent_vHash.keySet().iterator();
				String sql = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Vector extent_v = (Vector) extent_vHash.get(serverip);
					String deletesql = "delete from nms_oraextent where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);

					for (int k = 0; k < extent_v.size(); k++) {
						Hashtable ht = (Hashtable) extent_v.get(k);
						String tablespace_name = ht.get("tablespace_name")
								.toString();
						String extents = ht.get("sum(a.extents)").toString();
						tablespace_name = tablespace_name.replaceAll("\\\\",
								"/");
						sql = "insert into nms_oraextent(serverip,tablespace_name,extents,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ tablespace_name + "','" + extents;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						dbManager.addBatch(sql);
					}
				}
			}

			// 遍历logFile_v
			if (logFile_vHash != null && !logFile_vHash.isEmpty()) {
				Iterator ipidIterator = logFile_vHash.keySet().iterator();
				String sql = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Vector logFile_v = (Vector) logFile_vHash.get(serverip);
					String deletesql = "delete from nms_oralogfile where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);
					for (int k = 0; k < logFile_v.size(); k++) {
						Hashtable ht = (Hashtable) logFile_v.get(k);
						String group = ht.get("group#").toString();
						String status = ht.get("status").toString();
						String type = ht.get("type").toString();
						String member = ht.get("member").toString();
						String is_recovery_dest_file = CommonUtil.getValue(ht,
								"is_recovery_dest_file", "--");
						member = member.replaceAll("\\\\", "/");
						sql = "";
						sql = "insert into nms_oralogfile(serverip,groupstr,status,type,member,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ group
								+ "','"
								+ status + "','" + type + "','" + member;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						dbManager.addBatch(sql);
					}
				}
			}

			// 遍历userinfo_h
			if (userinfo_hHash != null && !userinfo_hHash.isEmpty()) {
				Iterator ipidIterator = userinfo_hHash.keySet().iterator();
				StringBuffer sBuffer = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Hashtable userinfo_h = (Hashtable) userinfo_hHash
							.get(serverip);
					String deletesql = "delete from nms_orauserinfo where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);
					Vector returnVal = null;
					Vector returnVal1 = null;
					Vector returnVal2 = null;
					if (userinfo_h != null) {
						returnVal = (Vector) userinfo_h.get("returnVal0");
						returnVal1 = (Vector) userinfo_h.get("returnVal1");
						returnVal2 = (Vector) userinfo_h.get("returnVal2");
					}
					try {
						// label:0 只插入returnVal的信息 label:1 只插入returnVal1的信息
						// label:2只插入returnVal2的信息
						String label = "0";
						for (int i = 0; i < returnVal.size(); i++) {
							Hashtable returnHash = (Hashtable) returnVal.get(i);
							sBuffer = new StringBuffer();
							sBuffer = new StringBuffer();
							sBuffer
									.append("insert into nms_orauserinfo(serverip, parsing_user_id, cpu_time, sorts, buffer_gets, "
											+ "runtime_mem, version_count, disk_reads,label, mon_time) ");
							sBuffer.append(" values('");
							sBuffer.append(serverip);
							sBuffer.append("','");
							sBuffer.append(String.valueOf(returnHash
									.get("parsing_user_id")));
							sBuffer.append("','");
							sBuffer.append(String.valueOf(returnHash
									.get("sum(a.cpu_time)")));
							sBuffer.append("','");
							sBuffer.append(String.valueOf(returnHash
									.get("sum(a.sorts)")));
							sBuffer.append("','");
							sBuffer.append(String.valueOf(returnHash
									.get("sum(a.buffer_gets)")));
							sBuffer.append("','");
							sBuffer.append(String.valueOf(returnHash
									.get("sum(a.runtime_mem)")));
							sBuffer.append("','");
							sBuffer.append(String.valueOf(returnHash
									.get("sum(a.version_count)")));
							sBuffer.append("','");
							sBuffer.append(String.valueOf(returnHash
									.get("sum(a.disk_reads)")));
							sBuffer.append("','");
							sBuffer.append(label);
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sBuffer.append("','");
								sBuffer.append(montime);
								sBuffer.append("')");
							} else if ("oracle"
									.equalsIgnoreCase(SystemConstant.DBType)) {
								sBuffer.append("',");
								sBuffer.append("to_date('" + montime
										+ "','YYYY-MM-DD HH24:MI:SS')");
								sBuffer.append(")");
							}

							dbManager.addBatch(sBuffer.toString());
						}
						label = "1";
						for (int i = 0; i < returnVal1.size(); i++) {
							sBuffer = new StringBuffer();
							Hashtable returnHash = (Hashtable) returnVal1
									.get(i);
							sBuffer = new StringBuffer();
							sBuffer
									.append("insert into nms_orauserinfo(serverip, users,label, mon_time) ");
							sBuffer.append(" values('");
							sBuffer.append(serverip);
							sBuffer.append("','");
							sBuffer.append(String.valueOf(returnHash
									.get("user#")));
							sBuffer.append("','");
							sBuffer.append(label);
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sBuffer.append("','");
								sBuffer.append(montime);
								sBuffer.append("')");
							} else if ("oracle"
									.equalsIgnoreCase(SystemConstant.DBType)) {
								sBuffer.append("',");
								sBuffer.append("to_date('" + montime
										+ "','YYYY-MM-DD HH24:MI:SS')");
								sBuffer.append(")");
							}
							dbManager.addBatch(sBuffer.toString());
						}
						label = "2";
						for (int i = 0; i < returnVal2.size(); i++) {
							sBuffer = new StringBuffer();
							Hashtable returnHash = (Hashtable) returnVal2
									.get(i);
							sBuffer = new StringBuffer();
							sBuffer
									.append("insert into nms_orauserinfo(serverip,username, user_id, account_status,label, mon_time) ");
							sBuffer.append(" values('");
							sBuffer.append(serverip);
							sBuffer.append("','");
							sBuffer.append(String.valueOf(returnHash
									.get("username")));
							sBuffer.append("','");
							sBuffer.append(String.valueOf(returnHash
									.get("user_id")));
							sBuffer.append("','");
							sBuffer.append(String.valueOf(returnHash
									.get("account_status")));
							sBuffer.append("','");
							sBuffer.append(label);
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sBuffer.append("','");
								sBuffer.append(montime);
								sBuffer.append("')");
							} else if ("oracle"
									.equalsIgnoreCase(SystemConstant.DBType)) {
								sBuffer.append("',");
								sBuffer.append("to_date('" + montime
										+ "','YYYY-MM-DD HH24:MI:SS')");
								sBuffer.append(")");
							}
							dbManager.addBatch(sBuffer.toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// 遍历wait
			if (waitHash != null && !waitHash.isEmpty()) {
				Iterator ipidIterator = waitHash.keySet().iterator();
				String sql = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Vector wait = (Vector) waitHash.get(serverip);
					String deletesql = "delete from nms_orawait where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);

					for (int k = 0; k < wait.size(); k++) {
						Hashtable ht = (Hashtable) wait.get(k);
						String event = ht.get("event").toString();
						String prev = ht.get("prev").toString();
						String curr = ht.get("curr").toString();
						String tot = ht.get("tot").toString();
						sql = "";
						sql = "insert into nms_orawait(serverip,event,prev,curr,tot,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ event
								+ "','"
								+ prev + "','" + curr + "','" + tot;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						dbManager.addBatch(sql);
					}
				}
			}

			// 遍历tableinfo
			if (tableinfoHash != null && !tableinfoHash.isEmpty()) {
				Iterator ipidIterator = tableinfoHash.keySet().iterator();
				String sql = null;
				while (ipidIterator.hasNext()) {
					String serverip = String.valueOf(ipidIterator.next());
					Vector tableinfo = (Vector) tableinfoHash.get(serverip);
					String deletesql = "delete from nms_orawait where serverip='"
							+ serverip + "'";
					dbManager.addBatch(deletesql);
					Hashtable dbio = (Hashtable) dbioHash.get(serverip);
					for (int i = 0; i < tableinfo.size(); i++) {
						Hashtable ht = (Hashtable) tableinfo.get(i);
						String filename = ht.get("file_name").toString();
						String tablespace = ht.get("tablespace").toString();
						String size = ht.get("size_mb").toString();
						String free = ht.get("free_mb").toString();
						String percent = ht.get("percent_free").toString();
						String status = ht.get("status").toString();
						String name = "";
						String pyr = "";
						String pbr = "";
						String pyw = "";
						String pbw = "";
						if (dbio.containsKey(filename)) {
							Hashtable iodetail = (Hashtable) dbio.get(filename);
							if (iodetail != null && iodetail.size() > 0) {
								name = (String) iodetail.get("name");
								pyr = (String) iodetail.get("pyr");
								pbr = (String) iodetail.get("pbr");
								pyw = (String) iodetail.get("pyw");
								pbw = (String) iodetail.get("pbw");
							}
							filename = filename.replaceAll("\\\\", "/");
						}
						sql = "insert into nms_oradbio(serverip, name, filename, pyr, pbr, pyw, pbw,mon_time) "
								+ "values('"
								+ serverip
								+ "','"
								+ name
								+ "','"
								+ filename
								+ "','"
								+ pyr
								+ "','"
								+ pbr
								+ "','"
								+ pyw + "','" + pbw;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "','" + montime + "')";
						} else if ("oracle"
								.equalsIgnoreCase(SystemConstant.DBType)) {
							sql = sql + "',to_date('" + montime
									+ "','YYYY-MM-DD HH24:MI:SS'))";
						}
						dbManager.addBatch(sql);
					}
				}
			}
			dbManager.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbManager.close();
		}
	}
	
	 public boolean addOracleRac(Hashtable returnhash) throws Exception {
			String ipaddress = (String)returnhash.get("ipaddress");
			
			if(returnhash.containsKey("racstatus")){
				DBManager dbmanager = new DBManager();
				try{
					Vector racstatusVector = null;
					racstatusVector = (Vector)returnhash.get("racstatus");
					Racstatuscollectdata vo = null;
					if(racstatusVector != null && racstatusVector.size() > 0){
						String sql1 = "delete from nms_oracle_racstatus where ipaddress='" + ipaddress + "'";
						dbmanager.addBatch(sql1);
						
						for(int i = 0; i < racstatusVector.size(); i++){
							vo = (Racstatuscollectdata)racstatusVector.get(i);
							
							String sql = "";
							try {
								 sql = "insert into nms_oracle_racstatus(name,ipaddress,type,target,state,collecttime) "
								 	+ "values('"
									+ vo.getName()
									+ "','"
									+ vo.getIpaddress()
									+ "','"
									+ vo.getType()
									+ "','"
									+ vo.getTarget()
									+ "','"
									+ vo.getState()
									+ "','"
									+ vo.getCollecttime() + "')";
								 
								dbmanager.addBatch(sql);
							} catch (Exception e) {
								e.printStackTrace();
								return false;
							} 
						}
						dbmanager.executeBatch();
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally {
					dbmanager.close();
				}
			}
			if(returnhash.containsKey("raclistenerstatus")){
				DBManager dbmanager = new DBManager();
				Raclistenerstatuscollectdata vo= (Raclistenerstatuscollectdata)returnhash.get("raclistenerstatus");
				String sql1 = "delete from nms_oracle_raclistenerstatus where ipaddress='" + ipaddress + "'";
				dbmanager.addBatch(sql1);
				
						String sql = "";
						try {
							 sql = "insert into nms_oracle_raclistenerstatus(ipaddress,listenerstatus,listenerstatusrun,listenerstatusnode,collecttime) "
							 	+ "values('"
								+ vo.getIpaddress()
								+ "','"
								+ vo.getListenerstatus()
								+ "','"
								+ vo.getListenerstatusrun()
								+ "','"
								+ vo.getListenerstatusnode()
								+ "','"
								+ vo.getCollecttime() + "')";
								
							dbmanager.addBatch(sql);
							dbmanager.executeBatch();
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						} finally{
							if(dbmanager != null){
								dbmanager.close();
							}
						}
			}
			if(returnhash.containsKey("raccrstatus")){
				DBManager dbmanager = new DBManager();
				Raccrstatuscollectdata vo= (Raccrstatuscollectdata)returnhash.get("raccrstatus");
				
				String sql1 = "delete from nms_oracle_raccrstatus where ipaddress='" + ipaddress + "'";
				dbmanager.addBatch(sql1);
				
						String sql = "";
						try {
							 sql = "insert into nms_oracle_raccrstatus(ipaddress,raccrstatus,collecttime) "
								 + "values('"
									+ vo.getIpaddress()
									+ "','"
									+ vo.getRaccrstatus()
									+ "','"
									+ vo.getCollecttime() + "')";
							
							 dbmanager.addBatch(sql);
							 dbmanager.executeBatch();
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						} finally {
							if(dbmanager != null){
								dbmanager.close();
							}
						}
				
			}
			
			if(returnhash.containsKey("raccrstatusdata")){
				DBManager dbmanager = new DBManager();
				try{
					Vector racstatusVector = null;
					racstatusVector = (Vector)returnhash.get("raccrstatusdata");
					Raccrstatusdata vo = null;
					if(racstatusVector != null && racstatusVector.size() > 0){
						
						String sql1 = "delete from nms_oracle_raccrstatusdata where ipaddress='" + ipaddress + "'";
						dbmanager.addBatch(sql1);
						
						for(int i = 0; i < racstatusVector.size(); i++){
							vo = (Raccrstatusdata)racstatusVector.get(i);
							
							String sql = "";
							try {
								 sql = "insert into nms_oracle_raccrstatusdata(ipaddress,numNo,name,status,collecttime) "
									 	+ "values('"
										+ vo.getIpaddress()
										+ "','"
										+ vo.getNumNo()
										+ "','"
										+ vo.getName()
										+ "','"
										+ vo.getStatus()
										+ "','"
										+ vo.getCollecttime() + "')";
									
								dbmanager.addBatch(sql);
							} catch (Exception e) {
								e.printStackTrace();
								return false;
							} 
						}
						dbmanager.executeBatch();
					}
				} catch (Exception e){
					e.printStackTrace();
				} finally {
					dbmanager.close();
				}
				
				
			}
			return true;
		}
	 
	 /**
		 * 得到oraclerac的状态信息
		 * 
		 * @param serverip
		 * @return
		 * @throws Exception
		 */
		public Hashtable getOracle_racstatus(String serverip) throws Exception {
			
			Hashtable returnHash = new Hashtable();
			String sql = "select * from nms_oracle_racstatus where ipaddress = '"
					+ serverip + "'";
			Hashtable retValue = new Hashtable();
			Racstatuscollectdata racstatusVo = new Racstatuscollectdata();
			Vector v = new Vector();
			DBManager dbmanager = new DBManager();
			try {
				rs = dbmanager.executeQuery(sql);
				while (rs.next()) {
					racstatusVo = new Racstatuscollectdata();
					racstatusVo.setId(rs.getInt("id"));
					racstatusVo.setIpaddress(rs.getString("ipaddress"));
					racstatusVo.setName(rs.getString("name"));
					racstatusVo.setType(rs.getString("type"));
					racstatusVo.setTarget(rs.getString("target"));
					racstatusVo.setState(rs.getString("state"));
					racstatusVo.setCollecttime(rs.getString("collecttime"));
					v.add(racstatusVo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
				sql = "";
			}
			
			returnHash.put("racstatus", v);
			
			
			sql = "select * from nms_oracle_raccrstatusdata where ipaddress = '" + serverip + "'";
						Raccrstatusdata raccrstatusdataVo = new Raccrstatusdata();
						Vector crstatusdataV = new Vector();
						try {
							dbmanager = new DBManager();
							rs = dbmanager.executeQuery(sql);
							while (rs.next()) {
								raccrstatusdataVo = new Raccrstatusdata();
								raccrstatusdataVo.setId(rs.getInt("id"));
								raccrstatusdataVo.setIpaddress(rs.getString("ipaddress"));
								raccrstatusdataVo.setNumNo(rs.getString("numNo"));
								raccrstatusdataVo.setName(rs.getString("name"));
								raccrstatusdataVo.setStatus(rs.getString("status"));
								crstatusdataV.add(raccrstatusdataVo);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dbmanager.close();
							sql = "";
						}
						
						returnHash.put("raccrstatusdata", crstatusdataV);
			
			sql = "select * from nms_oracle_raclistenerstatus where ipaddress = '"
				+ serverip + "'";
			Raclistenerstatuscollectdata raclistenerstatusVo = new Raclistenerstatuscollectdata();
			Vector raclistenerV = new Vector();
			try {
				dbmanager = new DBManager();
				rs = dbmanager.executeQuery(sql);
				while (rs.next()) {
					raclistenerstatusVo.setId(rs.getInt("id"));
					raclistenerstatusVo.setIpaddress(rs.getString("ipaddress"));
					raclistenerstatusVo.setListenerstatus(rs.getString("listenerstatus"));
					raclistenerstatusVo.setListenerstatusrun(rs.getString("listenerstatusrun"));
					raclistenerstatusVo.setListenerstatusnode(rs.getString("listenerstatusnode"));
					raclistenerV.add(raclistenerstatusVo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
				sql = "";
			}
			returnHash.put("raclistenerstatus", raclistenerV);
			
			sql = "select * from nms_oracle_raccrstatus where ipaddress = '"
				+ serverip + "'";
			Raccrstatuscollectdata raccrstatusVo = new Raccrstatuscollectdata();
			Vector raccrstatusV = new Vector();
			try {
				dbmanager = new DBManager();
				rs = dbmanager.executeQuery(sql);
				while (rs.next()) {
					raccrstatusVo.setId(rs.getInt("id"));
					raccrstatusVo.setIpaddress(rs.getString("ipaddress"));
					raccrstatusVo.setRaccrstatus(rs.getString("raccrstatus"));
					raccrstatusVo.setCollecttime(rs.getString("collecttime"));
					raccrstatusV.add(raccrstatusVo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
				sql = "";
			}
			returnHash.put("raccrstatus", raccrstatusV);
			
			return returnHash;
		}
		
		 /**
		 * 得到oracleraccrstatus的状态信息
		 * 
		 * @param serverip
		 * @return
		 * @throws Exception
		 */
		public Vector getOracle_raccrstatus(String serverip) throws Exception {
			
			Hashtable returnHash = new Hashtable();
			String sql = "select * from nms_oracle_racstatus where ipaddress = '"
					+ serverip + "'";
			Hashtable retValue = new Hashtable();
			Racstatuscollectdata racstatusVo = new Racstatuscollectdata();
			Vector v = new Vector();
			DBManager dbmanager = new DBManager();
			
			sql = "select * from nms_oracle_raccrstatusdata where ipaddress = '" + serverip + "'";
						Raccrstatusdata raccrstatusdataVo = new Raccrstatusdata();
						Vector crstatusdataV = new Vector();
						try {
							rs = dbmanager.executeQuery(sql);
							while (rs.next()) {
								raccrstatusdataVo = new Raccrstatusdata();
								raccrstatusdataVo.setId(rs.getInt("id"));
								raccrstatusdataVo.setIpaddress(rs.getString("ipaddress"));
								raccrstatusdataVo.setNumNo(rs.getString("numNo"));
								raccrstatusdataVo.setName(rs.getString("name"));
								raccrstatusdataVo.setStatus(rs.getString("status"));
								crstatusdataV.add(raccrstatusdataVo);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dbmanager.close();
							sql = "";
						}
						returnHash.put("raccrstatusdata", crstatusdataV);
			
			
			return crstatusdataV;
		}
		
		/**
		 * 得到oracle的数据控制disk信息
		 * 
		 * @param serverip
		 * @return
		 * @throws Exception
		 */
		public Vector getOracle_nmsoradiskdata(String serverip) throws Exception {
			String slq = "select * from nms_oradisk where serverip = '"
					+ serverip + "'";
			Vector retValue = new Vector();
			DBManager dbmanager = new DBManager();
			try {
				rs = dbmanager.executeQuery(slq);
				while (rs.next()) {
					Hashtable item = new Hashtable();
					item.put("disk_number", rs.getString("disk_number"));
					item.put("group_number", rs.getString("group_number"));
					item.put("mount_status", rs.getString("mount_status"));
					item.put("mode_status", rs.getString("mode_status"));
					item.put("state", rs.getString("state"));
					item.put("total_mb", rs.getString("total_mb"));
					item.put("free_mb", rs.getString("free_mb"));
					item.put("name", rs.getString("name"));
					retValue.add(item);
				}
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
			}
			return retValue;
		}
		
		/**
		 * 得到oracle的数据控制asmclient信息
		 * 
		 * @param serverip
		 * @return
		 * @throws Exception
		 */
		public Vector getOracle_nmsoraasmclient(String serverip) throws Exception {
			String slq = "select * from nms_oraasmclient where serverip = '"
					+ serverip + "'";
			Vector retValue = new Vector();
			DBManager dbmanager = new DBManager();
			try {
				rs = dbmanager.executeQuery(slq);
				while (rs.next()) {
					Hashtable item = new Hashtable();
					item.put("group_number", rs.getString("group_number"));
					item.put("db_name", rs.getString("db_name"));
					item.put("status", rs.getString("status"));
					item.put("software_version", rs.getString("software_version"));
					item.put("compatible_version", rs.getString("compatible_version"));
					item.put("mon_time", rs.getString("mon_time"));
					retValue.add(item);
				}
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
			}
			return retValue;
		}
		
		/**
		 * 得到oracle的数据控制asmdiskgroup信息
		 * 
		 * @param serverip
		 * @return
		 * @throws Exception
		 */
		public Vector getOracle_nmsoraasmdiskgroup(String serverip) throws Exception {
			String slq = "select * from nms_oraasmdiskgroup where serverip = '"
					+ serverip + "'";
			Vector retValue = new Vector();
			DBManager dbmanager = new DBManager();
			try {
				rs = dbmanager.executeQuery(slq);
				while (rs.next()) {
					Hashtable item = new Hashtable();
					item.put("group_number", rs.getString("group_number"));
					item.put("name", rs.getString("name"));
					item.put("state", rs.getString("state"));
					item.put("total_mb", rs.getString("total_mb"));
					item.put("free_mb", rs.getString("free_mb"));
					item.put("mon_time", rs.getString("mon_time"));
					retValue.add(item);
				}
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
			}
			return retValue;
		}
		/**
		 * 得到oracle的数据控制asmdiskgroup信息
		 * 
		 * @param serverip
		 * @return
		 * @throws Exception
		 */
		public Vector getOracle_nmsorabackup(String serverip) throws Exception {
			String slq = "select * from nms_orabackup where serverip = '"
					+ serverip + "' ORDER BY START_TIME DESC";
			Vector retValue = new Vector();
			DBManager dbmanager = new DBManager();
			try {
				rs = dbmanager.executeQuery(slq);
				while (rs.next()) {
					Hashtable item = new Hashtable();
					item.put("name", rs.getString("COMMAND_ID"));
					item.put("starttime", rs.getString("START_TIME"));
					item.put("usedtime", rs.getString("TIME_TAKEN_DISPLAY"));
					item.put("status", rs.getString("STATUS"));
					item.put("types", rs.getString("INPUT_TYPE"));
					item.put("devicetype", rs.getString("OUTPUT_DEVICE_TYPE"));
					item.put("innum", rs.getString("INPUT_BYTES_DISPLAY"));
					item.put("outnum", rs.getString("OUTPUT_BYTES_DISPLAY"));
					item.put("out_per_second", rs.getString("OUTPUT_BYTES_PER_SEC_DISPLAY"));
					retValue.add(item);
				}
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
			}
			return retValue;
		}

}