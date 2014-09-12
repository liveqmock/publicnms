/**
 * <p>Description: topo_tomcat_node</p>
 * <p>Company:dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-13
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.Resin;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.Pingcollectdata;

public class ResinDao extends BaseDao implements DaoInterface {
	public ResinDao() {
		super("app_resin_node");
	}

	public List getResinAll() {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from app_resin_node ");
		return findByCriteria(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		boolean flag = true;
		Resin vo = (Resin) baseVo;
		Resin pvo = (Resin) findByID(vo.getId() + "");

		StringBuffer sql = new StringBuffer();
		sql.append("update app_resin_node set alias='");
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
		sql.append("',bid='");
		sql.append(vo.getBid());
		sql.append("',sendemail='");
		sql.append(vo.getSendemail());
		sql.append("',sendmobiles='");
		sql.append(vo.getSendmobiles());
		sql.append("',sendphone='");
		sql.append(vo.getSendphone());
		sql.append("',monflag=");
		sql.append(vo.getMonflag());
		sql.append(",version='");
		sql.append(vo.getVersion());
		sql.append("',jvmversion='");
		sql.append(vo.getJvmversion());
		sql.append("',jvmvender='");
		sql.append(vo.getJvmvender());
		sql.append("',os='");
		sql.append(vo.getOs());
		sql.append("',osversion='");
		sql.append(vo.getVersion());
		sql.append("',supperid='");
		sql.append(vo.getSupperid());
		sql.append("' where id=");
		sql.append(vo.getId());

		// return saveOrUpdate(sql.toString());
		try {

			saveOrUpdate(sql.toString());

			if (!vo.getIpAddress().equals(pvo.getIpAddress())) {
				// 修改了IP
				// 若IP地址发生改变,先把表删除，然后在重新建立
				String ipstr = pvo.getIpAddress();
				// String ip1 ="",ip2="",ip3="",ip4="";
				// String[] ipdot = ipstr.split(".");
				// String tempStr = "";
				// String allipstr = "";
				// if (ipstr.indexOf(".")>0){
				// ip1=ipstr.substring(0,ipstr.indexOf("."));
				// ip4=ipstr.substring(ipstr.lastIndexOf(".")+1,ipstr.length());
				// tempStr =
				// ipstr.substring(ipstr.indexOf(".")+1,ipstr.lastIndexOf("."));
				// }
				// ip2=tempStr.substring(0,tempStr.indexOf("."));
				// ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
				// allipstr=ip1+ip2+ip3+ip4;
				String allipstr = SysUtil.doip(ipstr);

				conn = new DBManager();
				CreateTableManager ctable = new CreateTableManager();
				ctable.deleteTable(conn, "resin_mem", allipstr, "resin_mem");// Ping
				ctable.deleteTable(conn, "resinping", allipstr, "resinping");// Ping
				ctable.deleteTable(conn, "resinpingh", allipstr, "resinpingh");// Ping
				ctable.deleteTable(conn, "resinpingd", allipstr, "resinpingd");// Ping

				// 测试生成表
				String ip = vo.getIpAddress();
				// ip1 ="";ip2="";ip3="";ip4="";
				// ipdot = ip.split(".");
				// tempStr = "";
				// allipstr = "";
				// if (ip.indexOf(".")>0){
				// ip1=ip.substring(0,ip.indexOf("."));
				// ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());
				// tempStr =
				// ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
				// }
				// ip2=tempStr.substring(0,tempStr.indexOf("."));
				// ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
				// allipstr=ip1+ip2+ip3+ip4;
				allipstr = SysUtil.doip(ip);

				ctable = new CreateTableManager();
				ctable.createTable(conn, "resin_mem", allipstr, "resin_mem");// Ping
				ctable.createTable(conn, "resinping", allipstr, "resinping");// Ping
				ctable.createTable(conn, "resinpingh", allipstr, "resinpingh");// Ping
				ctable.createTable(conn, "resinpingd", allipstr, "resinpingd");// Ping
				conn.executeBatch();
			}

		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return flag;
	}

	public boolean save(BaseVo baseVo) {
		boolean flag = true;
		Resin vo = (Resin) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into app_resin_node(id,alias,ip_address,ip_long,port,users,password,monflag,bid,sendemail,sendmobiles,sendphone,version,jvmversion,jvmvender,os,osversion)values(");
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
		sql.append("','");
		sql.append(vo.getMonflag());
		sql.append("','");
		sql.append(vo.getBid());
		sql.append("','");
		sql.append(vo.getSendemail());
		sql.append("','");
		sql.append(vo.getSendmobiles());
		sql.append("',");
		sql.append(vo.getSendphone());
		sql.append(",'");
		sql.append(vo.getVersion());
		sql.append("','");
		sql.append(vo.getJvmversion());
		sql.append("','");
		sql.append(vo.getJvmvender());
		sql.append("','");
		sql.append(vo.getOs());
		sql.append("','");
		sql.append(vo.getOsversion());
		sql.append("')");
		try {
			saveOrUpdate(sql.toString());
			CreateTableManager ctable = new CreateTableManager();
			// 测试生成表
			String ip = vo.getIpAddress();
			String allipstr = SysUtil.doip(ip);
			conn = new DBManager();
			ctable.createTable(conn, "resin_mem", allipstr, "resin_mem");// Ping
			ctable.createTable(conn, "resinping", allipstr, "resinping");// Ping
			ctable.createTable(conn, "resinpingh", allipstr, "resinpingh");// Ping
			ctable.createTable(conn, "resinpingd", allipstr, "resinpingd");// Ping

		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			try {
				conn.executeBatch();
			} catch (Exception e) {

			}
			conn.close();
		}
		return flag;
	}

	public boolean delete(String id) {
		boolean result = false;
		try {
			Resin pvo = (Resin) findByID(id + "");
			String ipstr = pvo.getIpAddress();
			// String ip1 ="",ip2="",ip3="",ip4="";
			// String[] ipdot = ipstr.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ipstr.indexOf(".")>0){
			// ip1=ipstr.substring(0,ipstr.indexOf("."));
			// ip4=ipstr.substring(ipstr.lastIndexOf(".")+1,ipstr.length());
			// tempStr =
			// ipstr.substring(ipstr.indexOf(".")+1,ipstr.lastIndexOf("."));
			// }
			// ip2=tempStr.substring(0,tempStr.indexOf("."));
			// ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
			// allipstr=ip1+ip2+ip3+ip4;
			String allipstr = SysUtil.doip(ipstr);

			CreateTableManager ctable = new CreateTableManager();
			ctable.deleteTable(conn, "resin_mem", allipstr, "resin_mem");// Ping
			ctable.deleteTable(conn, "resinping", allipstr, "resinping");// Ping
			ctable.deleteTable(conn, "resinpingh", allipstr, "resinpingh");// Ping
			ctable.deleteTable(conn, "resinpingd", allipstr, "resinpingd");// Ping

			conn.addBatch("delete from app_resin_node where id=" + id);
			conn.addBatch("delete from topo_node_single_data where node_id=" + id);
			conn.addBatch("delete from topo_node_multi_data where node_id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			SysLogger.error("resinDao.delete()", e);
		} finally {
			conn.close();
		}
		return result;
	}

	// 处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(Pingcollectdata pingdata) {
		if (pingdata == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Vector v = new Vector();
			String ip = pingdata.getIpaddress();
			if (pingdata.getRestype().equals("dynamic")) {
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";
				String type = pingdata.getCategory();
				if ("ResinPing".equals(type)) {
					tablename = "resinping" + allipstr;
				} else if (type.contains("resin_")) {
					tablename = "resin_mem" + allipstr;
				}
				String sql = "";
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + pingdata.getRestype() + "','" + pingdata.getCategory() + "','" + pingdata.getEntity() + "','"
							+ pingdata.getSubentity() + "','" + pingdata.getUnit() + "','" + pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'" + pingdata.getThevalue() + "','" + time + "')";

				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + pingdata.getRestype() + "','" + pingdata.getCategory() + "','" + pingdata.getEntity() + "','"
							+ pingdata.getSubentity() + "','" + pingdata.getUnit() + "','" + pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'" + pingdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";

				}

				conn.executeUpdate(sql);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();

		}
		return true;
	}

	public List getResinByBID(Vector bids) {
		List rlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		String wstr = "";
		if (bids != null && bids.size() > 0) {
			for (int i = 0; i < bids.size(); i++) {
				if (wstr.trim().length() == 0) {
					wstr = wstr + " where ( bid like '%," + bids.get(i) + ",%' ";
				} else {
					wstr = wstr + " or bid like '%," + bids.get(i) + ",%' ";
				}

			}
			wstr = wstr + ")";
		}
		sql.append("select * from app_resin_node " + wstr);
		// SysLogger.info(sql.toString());
		return findByCriteria(sql.toString());
	}

	public BaseVo loadFromRS(ResultSet rs) {
		Resin vo = new Resin();
		try {
			vo.setId(rs.getInt("id"));
			vo.setAlias(rs.getString("alias"));
			vo.setIpAddress(rs.getString("ip_address"));
			vo.setPort(rs.getString("port"));
			vo.setUser(rs.getString("users"));
			vo.setPassword(rs.getString("password"));
			vo.setBid(rs.getString("bid"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setMonflag(rs.getInt("monflag"));
			vo.setVersion(rs.getString("version"));
			vo.setJvmversion(rs.getString("jvmversion"));
			vo.setJvmvender(rs.getString("jvmvender"));
			vo.setOs(rs.getString("os"));
			vo.setOsversion(rs.getString("osversion"));
			vo.setSupperid(rs.getInt("supperid"));// snow add supperid at
			// 2010-5-20
		} catch (Exception e) {
			SysLogger.error("AlarmDao.loadFromRS()", e);
		}
		return vo;
	}

	// resin------------------/jgxy/src/com/afunms/application/dao/resinDao.java----
	// zhushouzhi-----------------resin
	public int getidByIp(String ip) {
		String string = "select id from app_resin_node where ip_address =" + "'" + ip + "'";
		int id = 0;
		ResultSet rSet = null;
		rSet = conn.executeQuery(string);
		try {
			while (rSet.next()) {
				id = rSet.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rSet != null) {
				try {
					rSet.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			conn.close();
		}

		return id;
	}

	// public List getEvent(String ip){
	// String sql="select * from system_eventlist where id='"+ip+"';";
	// List list = new ArrayList();
	// try {
	// rs = conn.executeQuery(sql);
	// while (rs.next()) {
	// list.add(rs.getString(""));
	// list.add(rs.getString(""));
	// list.add(rs.getString(""));
	// }
	// } catch (Exception e) {
	// }
	// return null;
	// }

	/**
	 * 从数据库中获取resin的采集的数据信息
	 */
	public Hashtable getResinDataHashtable(String nodeid) throws SQLException {
		Hashtable hm = new Hashtable();
		try {
			// SysLogger.info();
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select * from nms_resin_temp where nodeid = '");
			sqlBuffer.append(nodeid);
			sqlBuffer.append("'");
			rs = conn.executeQuery(sqlBuffer.toString());
			// 取得列名,
			while (rs.next()) {
				String entity = rs.getString("entity");
				String value = rs.getString("value");
				hm.put(entity, value);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return hm;
	}

	public Hashtable getPingDataById(String ip, Integer id, String starttime, String endtime) {
		Hashtable hash = new Hashtable();
		if (!starttime.equals("") && !endtime.equals("")) {
			List list1 = new ArrayList();
			String sql = "";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select a.thevalue,a.collecttime from resinping" + ip.replace(".", "_") + " a where " + "(a.collecttime >= '" + starttime + "' and  a.collecttime <= '" + endtime + "') order by id";
				;
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select a.thevalue from resinping" + ip.replace(".", "_") + " a where " + " (a.collecttime >= " + "to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')" + " and  a.collecttime <= " + "to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')" + ") order by id";
				;
			}
			// SysLogger.info(sql);
			int i = 0;
			double curPing = 0;
			double avgPing = 0;
			double minPing = 0;
			rs = conn.executeQuery(sql);
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					// String reason = rs.getString("reason");
					thevalue = String.valueOf(Integer.parseInt(thevalue));
					v.add(0, thevalue);
					v.add(1, collecttime);
					v.add(2, "%");
					avgPing = avgPing + Float.parseFloat(thevalue);
					curPing = Float.parseFloat(thevalue);
					if (curPing < minPing)
						minPing = curPing;
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (conn != null)
						conn.close();

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			hash.put("list", list1);
			if (list1 != null && list1.size() > 0) {
				hash.put("avgPing", CEIString.round(avgPing / list1.size(), 2) + "");
			} else {
				hash.put("avgPing", "0");
			}
			hash.put("minPing", minPing + "");
			hash.put("curPing", curPing + "");
		}
		return hash;
	}

	public Hashtable getMemDataById(String ip, Integer id, String subentity, String starttime, String endtime) {
		Hashtable hash = new Hashtable();
		if (!starttime.equals("") && !endtime.equals("")) {
			List list1 = new ArrayList();
			StringBuffer sqlBuf = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sqlBuf.append("select a.thevalue,a.collecttime from resin_mem" + ip.replace(".", "_") + " a where " + "a.collecttime >= '" + starttime + "' and  a.collecttime <= '" + endtime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sqlBuf.append("select a.thevalue from resin_mem" + ip.replace(".", "_") + " a where " + " (a.collecttime >= " + "to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')" + " and  a.collecttime <= " + "to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')"
						+ ") order by id");
			}

			sqlBuf.append(" and a.subentity='");
			sqlBuf.append(subentity);
			sqlBuf.append("' order by id");

			int i = 0;
			double curPing = 0;
			double avgPing = 0;
			double maxmemcon = 0;
			rs = conn.executeQuery(sqlBuf.toString());
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, thevalue);
					v.add(1, collecttime);
					v.add(2, "%");
					avgPing = avgPing + Float.parseFloat(thevalue);
					curPing = Double.parseDouble(thevalue);
					if (curPing > maxmemcon)
						maxmemcon = curPing;
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (conn != null)
						conn.close();

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			hash.put("list", list1);
			if (list1 != null && list1.size() > 0) {
				hash.put("avgmemcon", CEIString.round(avgPing / list1.size(), 2) + "");
			} else {
				hash.put("avgmemcon", "0");
			}
			hash.put("maxmemcon", maxmemcon + "");
			hash.put("curmemcon", curPing + "");
		}
		return hash;
	}
}