/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DHCPConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.Pingcollectdata;

public class DHCPConfigDao extends BaseDao implements DaoInterface {

	public DHCPConfigDao() {
		super("nms_dhcpconfig");
	}

	public boolean delete(String[] ids) {
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				delete(ids[i]);
			}
		}
		return true;
	}

	public boolean delete(String id) {
		boolean result = false;
		try {
			DHCPConfig pvo = (DHCPConfig) findByID(id + "");
			String ipstr = pvo.getIpAddress();
			String allipstr = SysUtil.doip(ipstr);

			CreateTableManager ctable = new CreateTableManager();

			ctable.deleteTable(conn, "dhcpping", allipstr, "dhcpping");// Ping
			ctable.deleteTable(conn, "dhcpphour", allipstr, "dhcpphour");// Ping
			ctable.deleteTable(conn, "dhcppday", allipstr, "dhcppday");// Ping
			conn.addBatch("delete from nms_dhcpconfig where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			SysLogger.error("DHCPConfigDao.delete()", e);
		}
		return result;
	}

	public List getByFlag(Integer flag) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_dhcpconfig where monflag= ");
		sql.append(flag);
		return findByCriteria(sql.toString());
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {

		DHCPConfig vo = new DHCPConfig();

		try {
			vo.setId(rs.getInt("id"));
			vo.setAlias(rs.getString("name"));
			vo.setIpAddress(rs.getString("ipaddress"));
			vo.setCommunity(rs.getString("community"));
			vo.setMon_flag(rs.getInt("monflag"));
			vo.setNetid(rs.getString("netid"));
			vo.setSupperid(rs.getInt("supperid"));
			vo.setDhcptype(rs.getString("dhcptype"));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return vo;
	}

	public boolean save(BaseVo vo) {

		boolean flag = true;

		DHCPConfig vo1 = (DHCPConfig) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_dhcpconfig(id,name,ipaddress,community,monflag,netid,supperid,dhcptype) values(");
		sql.append(vo1.getId());
		sql.append(",'");
		sql.append(vo1.getAlias());
		sql.append("','");
		sql.append(vo1.getIpAddress());
		sql.append("','");
		sql.append(vo1.getCommunity());
		sql.append("','");
		sql.append(vo1.getMon_flag());
		sql.append("','");
		sql.append(vo1.getNetid());
		sql.append("','");
		sql.append(vo1.getSupperid());
		sql.append("','");
		sql.append(vo1.getDhcptype());
		sql.append("')");
		try {
			SysLogger.info(sql.toString());
			saveOrUpdate(sql.toString());
			CreateTableManager ctable = new CreateTableManager();
			String ip = vo1.getIpAddress();
			String allipstr = SysUtil.doip(ip);
			conn = new DBManager();
			ctable.createTable(conn, "dhcpping", allipstr, "dhcpping");// Ping
			ctable.createTable(conn, "dhcpphour", allipstr, "dhcpphour");// Ping
			ctable.createTable(conn, "dhcppday", allipstr, "dhcppday");// Ping

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

	public boolean update(BaseVo vo) {

		boolean flag = true;

		DHCPConfig vo1 = (DHCPConfig) vo;
		DHCPConfig pvo = (DHCPConfig) findByID(vo1.getId() + "");

		StringBuffer sql = new StringBuffer();
		sql.append("update nms_dhcpconfig set name='");
		sql.append(vo1.getAlias());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpAddress());
		sql.append("',community='");
		sql.append(vo1.getCommunity());
		sql.append("',monflag='");
		sql.append(vo1.getMon_flag());
		sql.append("',netid='");
		sql.append(vo1.getNetid());
		sql.append("',supperid='");
		sql.append(vo1.getSupperid());
		sql.append("',dhcptype='");
		sql.append(vo1.getDhcptype());
		sql.append("' where id=" + vo1.getId());

		saveOrUpdate(sql.toString());

		if (!vo1.getIpAddress().equals(pvo.getIpAddress())) {
			// 修改了IP
			// 若IP地址发生改变,先把表删除，然后在重新建立
			String ipstr = pvo.getIpAddress();
			String allipstr = SysUtil.doip(ipstr);
			try {
				CreateTableManager ctable = new CreateTableManager();

				conn = new DBManager();
				ctable.deleteTable(conn, "dhcpping", allipstr, "dhcpping");// Ping
				ctable.deleteTable(conn, "dhcpphour", allipstr, "dhcpphour");// Ping
				ctable.deleteTable(conn, "dhcppday", allipstr, "dhcppday");// Ping

				// 测试生成表
				String ip = vo1.getIpAddress();
				allipstr = SysUtil.doip(ip);
				ctable = new CreateTableManager();
				ctable.createTable(conn, "dhcpping", allipstr, "dhcpping");// Ping
				ctable.createTable(conn, "dhcpphour", allipstr, "dhcpphour");// Ping
				ctable.createTable(conn, "dhcppday", allipstr, "dhcppday");// Ping
			} catch (Exception e) {
				flag = false;
				e.printStackTrace();
			} finally {
				try {
					conn.executeBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}
				conn.close();
			}
		}

		return flag;
	}

	// 处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(Vector pingdataV, DHCPConfig dhcpconf) {
		if (pingdataV == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < pingdataV.size(); i++) {
				Pingcollectdata pingdata = (Pingcollectdata) pingdataV.get(i);
				String ip = pingdata.getIpaddress();
				if (pingdata.getRestype().equals("dynamic")) {
					String allipstr = "";
					allipstr = SysUtil.doip(ip);
					Calendar tempCal = (Calendar) pingdata.getCollecttime();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					String tablename = "dhcpping" + allipstr;
					String sql = "";
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + pingdata.getRestype() + "','" + pingdata.getCategory() + "','" + pingdata.getEntity() + "','" + pingdata.getSubentity() + "','" + pingdata.getUnit() + "','" + pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'" + pingdata.getThevalue() + "','" + time + "')";
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + pingdata.getRestype() + "','" + pingdata.getCategory() + "','" + pingdata.getEntity() + "','" + pingdata.getSubentity() + "','" + pingdata.getUnit() + "','" + pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'" + pingdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
					}
					conn.executeUpdate(sql);

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
		}
		return true;
	}

	public List getDHCPByBID(Vector bids) {
		StringBuffer sql = new StringBuffer();
		String wstr = "";
		if (bids != null && bids.size() > 0) {
			for (int i = 0; i < bids.size(); i++) {
				if (wstr.trim().length() == 0) {
					wstr = wstr + " where ( netid like '%," + bids.get(i) + ",%' ";
				} else {
					wstr = wstr + " or netid like '%," + bids.get(i) + ",%' ";
				}

			}
			wstr = wstr + ")";
		}
		sql.append("select * from nms_dhcpconfig " + wstr);
		return findByCriteria(sql.toString());
	}

	public int getidByIp(String ip) {
		String string = "select id from nms_dhcpconfig where ipaddress =" + "'" + ip + "'";
		int id = 0;
		ResultSet rSet = null;
		rSet = conn.executeQuery(string);
		try {
			while (rSet.next()) {
				id = rSet.getInt(1);
			}
		} catch (SQLException e) {
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

	public List getidByIDS(String[] ids) {
		if (ids != null && ids.length > 0) {
			String where = "";
			for (int i = 0; i < ids.length; i++) {
				if (i == 0) {
					where = where + " where id=" + ids[i];
				} else
					where = where + " or id=" + ids[i];
			}
			StringBuffer sql = new StringBuffer();
			sql.append("select * from nms_dhcpconfig " + where);
			return findByCriteria(sql.toString());
		}
		return null;
	}

	public Hashtable getPingDataById(String ip, Integer id, String starttime, String endtime) {
		Hashtable hash = new Hashtable();
		if (!starttime.equals("") && !endtime.equals("")) {
			List list1 = new ArrayList();
			String sql = "";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select a.thevalue,a.collecttime from dhcpping" + ip + " a where " + "(subentity = 'ConnectUtilization' and a.collecttime >= '" + starttime + "' and  a.collecttime <= '" + endtime + "') order by id";
				;
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select a.thevalue from dhcpping" + ip + " a where " + " ( subentity = 'ConnectUtilization' and a.collecttime >= " + "to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')" + " and  a.collecttime <= " + "to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')" + ") order by id";
				;
			}
			SysLogger.info("DHCP:" + sql);
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

}