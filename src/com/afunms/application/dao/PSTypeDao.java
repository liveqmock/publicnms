package com.afunms.application.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.PSTypeVo;
import com.afunms.application.util.DbConversionUtil;
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

public class PSTypeDao extends BaseDao implements DaoInterface {

	public PSTypeDao() {
		super("nms_portservice");
	}

	public boolean update(BaseVo baseVo) {
		boolean flag = true;
		PSTypeVo vo = (PSTypeVo) baseVo;
		PSTypeVo pvo = (PSTypeVo) findByID(vo.getId() + "");
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_portservice set ipaddress ='");
		sql.append(vo.getIpaddress());
		sql.append("',port='");
		sql.append(vo.getPort());
		sql.append("',portdesc='");
		sql.append(vo.getPortdesc());
		sql.append("',monflag='");
		sql.append(vo.getMonflag());
		sql.append("',flag='");
		sql.append(vo.getFlag());
		sql.append("',timeout=");
		sql.append(vo.getTimeout());
		sql.append(",bid='");
		sql.append(vo.getBid());
		sql.append("',sendemail='");
		sql.append(vo.getSendemail());
		sql.append("',sendmobiles='");
		sql.append(vo.getSendmobiles());
		sql.append("',sendphone='");
		sql.append(vo.getSendphone());
		sql.append("',supperid='");
		sql.append(vo.getSupperid());
		sql.append("' where id=");
		sql.append(vo.getId());

		saveOrUpdate(sql.toString());

		if (!vo.getIpaddress().equals(pvo.getIpaddress()) || !vo.getPort().equalsIgnoreCase(pvo.getPort())) {
			// 修改了IP
			// 若IP地址发生改变,先把表删除，然后在重新建立
			try {
				String ipstr = pvo.getIpaddress();
				String allipstr = SysUtil.doip(ipstr);

				conn = new DBManager();
				CreateTableManager ctable = new CreateTableManager();
				ctable.deleteTable(conn, "spping" + pvo.getPort(), allipstr, "spping");// Ping
				ctable.deleteTable(conn, "sppinghour" + pvo.getPort(), allipstr, "sppinghour");// PingHour
				ctable.deleteTable(conn, "sppingday" + pvo.getPort(), allipstr, "sppingday");// PingDay

				// 测试生成表
				String ip = vo.getIpaddress();
				allipstr = SysUtil.doip(ip);
				ctable = new CreateTableManager();
				ctable.createTable(conn, "spping" + vo.getPort(), allipstr, "spping");// Ping
				ctable.createTable(conn, "sppinghour" + vo.getPort(), allipstr, "sppinghour");// PingHour
				ctable.createTable(conn, "sppingday" + vo.getPort(), allipstr, "sppingday");// PingDay
			} catch (Exception e) {
				flag = false;
				e.printStackTrace();
			} finally {
				try {
					conn.executeBatch();
				} catch (Exception e) {
					SysLogger.error("PSTypeDao.update()");
				}
				conn.close();
			}
		}

		return flag;
	}

	public boolean save(BaseVo baseVo) {
		boolean flag = true;
		PSTypeVo vo = (PSTypeVo) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_portservice(id,ipaddress,port,portdesc,monflag,flag,timeout,sendemail,sendmobiles,sendphone,bid,supperid)values(");
		sql.append(vo.getId());
		sql.append(",'");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getPort());
		sql.append("','");
		sql.append(vo.getPortdesc());
		sql.append("','");
		sql.append(vo.getMonflag());
		sql.append("','");
		sql.append(vo.getFlag());
		sql.append("',");
		sql.append(vo.getTimeout());
		sql.append(",'");
		sql.append(vo.getSendemail());
		sql.append("','");
		sql.append(vo.getSendmobiles());
		sql.append("','");
		sql.append(vo.getSendphone());
		sql.append("','");
		sql.append(vo.getBid());
		sql.append("','");
		sql.append(vo.getSupperid());
		sql.append("')");

		try {
			saveOrUpdate(sql.toString());
			CreateTableManager ctable = new CreateTableManager();
			// 测试生成表
			String ip = vo.getIpaddress();
			String allipstr = SysUtil.doip(ip);
			conn = new DBManager();
			ctable.createTable(conn, "spping" + vo.getPort(), allipstr, "spping");// Ping
			ctable.createTable(conn, "sppinghour" + vo.getPort(), allipstr, "sppinghour");// PingHour
			ctable.createTable(conn, "sppingday" + vo.getPort(), allipstr, "sppingday");// PingDay

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

	public List getSocketByBID(Vector bids) {
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
		sql.append("select * from nms_portservice " + wstr);
		return findByCriteria(sql.toString());
	}

	public PSTypeVo findByFwtype(String port) {
		PSTypeVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_portservice where port='" + port + "'");
			if (rs.next())
				vo = (PSTypeVo) loadFromRS(rs);
		} catch (Exception e) {
			SysLogger.error("PSTypeDao.findByFwtype", e);
			vo = null;
		} finally {
			conn.close();
		}
		return vo;
	}

	public boolean delete(String id) {
		boolean result = false;
		try {
			PSTypeVo vo = (PSTypeVo) findByID(id + "");
			String ipstr = vo.getIpaddress();
			String allipstr = SysUtil.doip(ipstr);
			CreateTableManager ctable = new CreateTableManager();
			conn = new DBManager();
			ctable.deleteTable(conn, "spping" + vo.getPort(), allipstr, "spping");// Ping
			ctable.deleteTable(conn, "sppinghour" + vo.getPort(), allipstr, "sppinghour");// PingHour
			ctable.deleteTable(conn, "sppingday" + vo.getPort(), allipstr, "sppingday");// PingDay
			conn.addBatch("delete from nms_portservice where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			SysLogger.error("PSTypeDao.delete()", e);
		} finally {
			conn.close();
		}
		return result;
	}

	// 处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(Pingcollectdata pingdata, String port) {
		if (pingdata == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ip = pingdata.getIpaddress();
			if (pingdata.getRestype().equals("dynamic")) {
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
				String tablename = "";
				tablename = "spping" + port + allipstr;
				String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + pingdata.getRestype() + "','" + pingdata.getCategory() + "','" + pingdata.getEntity() + "','" + pingdata.getSubentity() + "','" + pingdata.getUnit() + "','" + pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'" + pingdata.getThevalue() + "'," + time + ")";
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

	public BaseVo loadFromRS(ResultSet rs) {
		PSTypeVo vo = new PSTypeVo();
		try {
			vo.setId(rs.getInt("id"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setPort(rs.getString("port"));
			vo.setPortdesc(rs.getString("portdesc"));
			vo.setMonflag(rs.getInt("monflag"));
			vo.setFlag(rs.getInt("flag"));
			vo.setTimeout(rs.getInt("timeout"));
			vo.setBid(rs.getString("bid"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setSupperid(rs.getInt("supperid"));
		} catch (Exception e) {
			SysLogger.error("PSTypeDao.loadFromRS()", e);
		}
		return vo;
	}

	public Hashtable getCategory(String ip, String category, String subentity, String starttime, String endtime, String port) throws Exception {
		Hashtable hash = new Hashtable();
		ResultSet rs = null;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				String allipstr = SysUtil.doip(ip);
				String sql = "";
				StringBuffer sb = new StringBuffer();
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime ,h.unit from spping" + port + allipstr + " h where ");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append(" select h.thevalue,h.collecttime ,h.unit from spping" + port + allipstr + " h where ");
				}
				sb.append(" h.category='");
				sb.append(category);
				sb.append("' and h.subentity='");
				sb.append(subentity);
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime);
					sb.append("' order by h.collecttime");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= ");
					sb.append("to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') ");
					sb.append(" and h.collecttime <= ");
					sb.append("to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS') ");
					sb.append(" order by h.collecttime");
				}
				sql = sb.toString();

				rs = conn.executeQuery(sql);
				List list1 = new ArrayList();
				String unit = "";
				String cur = "0";
				double tempfloat = 0;
				double pingcon = 0;
				double cpucon = 0;
				int downnum = 0;
				int i = 0;
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));

					if (category.equals("SOCKETPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
						cur = thevalue;
					} else if (category.equals("ORAPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					} else if (category.equals("SQLPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					} else if (category.equals("DB2Ping") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					} else if (category.equals("SYSPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					}
					if (subentity.equalsIgnoreCase("ConnectUtilization")) {
						if (i == 1)
							tempfloat = getfloat(thevalue);
						if (tempfloat > getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					} else if (subentity.equalsIgnoreCase("ResponseTime")) {
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					} else if (category.equalsIgnoreCase("CPU")) {
						cpucon = cpucon + getfloat(thevalue);
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					} else {
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					}
					list1.add(v);
				}
				rs.close();
				Integer size = new Integer(0);
				hash.put("list", list1);
				if (list1.size() != 0) {
					size = new Integer(list1.size());
					if (list1.get(0) != null) {
						Vector tempV = (Vector) list1.get(0);
						unit = (String) tempV.get(2);
					}
				}
				if ((category.equals("Ping") || category.equals("ORAPing") || category.equals("DB2Ping") || category.equals("SYSPing") || category.equals("SQLPing") || category.equals("SOCKETPing")) && subentity.equalsIgnoreCase("ConnectUtilization")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgpingcon", CEIString.round(pingcon / list1.size(), 2) + unit);
						hash.put("pingmax", tempfloat + "");
						hash.put("downnum", downnum + "");
						hash.put("curping", cur);
					} else {
						hash.put("avgpingcon", "0.0%");
						hash.put("pingmax", "0.0%");
						hash.put("downnum", "0");
						hash.put("curping", "0.0%");
					}
				}
				if (category.equals("CPU")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgcpucon", CEIString.round(cpucon / list1.size(), 2) + unit);
					} else {
						hash.put("avgcpucon", "0.0%");
					}
				}

				hash.put("size", size);
				hash.put("max", CEIString.round(tempfloat, 2) + unit);
				hash.put("unit", unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
		}

		return hash;
	}

	private String emitStr(String num) {
		if (num != null) {
			if (num.indexOf(".") >= 0) {
				if (num.substring(num.indexOf(".") + 1).length() > 7) {
					String tempStr = num.substring(num.indexOf(".") + 1);
					num = num.substring(0, num.indexOf(".") + 1) + tempStr.substring(0, 7);
				}
			}
		}
		return num;
	}

	private double getfloat(String num) {
		double snum = 0.0;
		if (num != null) {
			if (num.indexOf(".") >= 0) {
				if (num.substring(num.indexOf(".") + 1).length() > 7) {
					String tempStr = num.substring(num.indexOf(".") + 1);
					num = num.substring(0, num.indexOf(".") + 1) + tempStr.substring(0, 7);
				}
			}
			int inum = (int) (Float.parseFloat(num) * 100);
			snum = new Double(inum / 100.0).doubleValue();
		}
		return snum;
	}
}
