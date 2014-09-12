package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.JspPage;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.model.VPNFileConfig;

public class VPNFileConfigDao extends BaseDao implements DaoInterface {

	public VPNFileConfigDao() {
		super("nms_vpncfg");
	}

	public void updateBaseLine(String id, int flag) {
		String sql = "";
		if (flag == 1) {
			sql = "update nms_vpncfg set baseline=1 where id=" + id;
		} else if (flag == 2) {
			sql = "update nms_vpncfg set baseline=0";
		}

		conn.executeUpdate(sql);
		// return
	}

	public List loadByIp(String[] ipaddress) {
		List list = new ArrayList();
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < ipaddress.length; i++) {
			sBuffer.append(ipaddress[i] + ",");
		}
		String ips = sBuffer.substring(0, sBuffer.length() - 1);
		StringBuffer sql = new StringBuffer();
		sql
				.append("select * from nms_vpncfg where ipaddress in('" + ips
						+ "')");
		rs = conn.executeQuery(sql.toString());
		try {
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean deleteFileByIps(String[] ips) {
		boolean result = false;
		try {
			for (int i = 0; i < ips.length; i++)
				conn.addBatch("delete from nms_vpncfg where ipaddress='"
						+ ips[i] + "'");
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			SysLogger.error("BaseDao.delete()", ex);
			result = false;
		}
		return result;
	}

	public List loadByIps(String[] ips) {
		String split = "";
		for (int i = 0; i < ips.length; i++) {
			split = split + "'" + ips[i] + "',";
		}
		List list = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from nms_vpncfg where ipaddress in("
					+ split.substring(0, split.length() - 1) + ")");
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public List loadAllIps() {
		
		List list = new ArrayList();
		try {
			String sql="select distinct(ipaddress) from nms_vpncfg";
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(rs.getString("ipaddress"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List loadByIds(String[] ids) {
		String split = "";
		for (int i = 0; i < ids.length; i++) {
			split = "," + ids[i] + split;
		}
		List list = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from nms_vpncfg where id in("
					+ split.substring(1) + ")");
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List listByPage(int curpage, int perpage) {
		return listByPage(curpage, "", perpage);
	}

	public List listByPage(int curpage, String where, int perpage) {
		List list = new ArrayList();
		try {
			rs = conn
					.executeQuery("select count(*) from (select max(id) 'id',ipaddress,fileName,content,backup_time,file_size,bkp_type from nms_hua3vpncfg group by ipaddress) t");
			if (rs.next())
				jspPage = new JspPage(perpage, curpage, rs.getInt(1));

			rs = conn
					.executeQuery("select max(id) 'id',ipaddress,fileName,content,backup_time,file_size,bkp_type from nms_hua3vpncfg group by ipaddress;");
			// SysLogger.info("select * from " + table + " " + where );
			int loop = 0;
			while (rs.next()) {
				loop++;
				if (loop < jspPage.getMinNum())
					continue;
				list.add(loadFromRS(rs));
				if (loop == jspPage.getMaxNum())
					break;
			}
		} catch (Exception e) {
			SysLogger.error("BaseDao.listByPage()", e);
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		VPNFileConfig h3 = (VPNFileConfig) vo;
		StringBuffer sql = new StringBuffer(
				"insert into nms_vpncfg(id,timingId,ipaddress,fileName,content,backup_time,file_size,bkp_type) values(");
		sql.append(this.getNextID());
		sql.append(",");
		sql.append(h3.getTimingId());
		sql.append(",'");
		sql.append(h3.getIpaddress());
		sql.append("','");
		sql.append(h3.getFileName());
		sql.append("','");
		sql.append(h3.getContent());
		sql.append("','");
		sql.append(h3.getBackupTime());
		sql.append("',");
		sql.append(h3.getFileSize());
		sql.append(",'");
		sql.append(h3.getBkpType());
		sql.append("')");
		return this.saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub

		VPNFileConfig vo = new VPNFileConfig();
		try {
			vo.setId(rs.getInt("id"));
			vo.setTimingId(rs.getInt("timingId"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setFileName(rs.getString("fileName"));
			vo.setContent(rs.getString("content"));
			vo.setFileSize(rs.getInt("file_size"));
			vo.setBackupTime(rs.getTimestamp("backup_time"));
			vo.setBkpType(rs.getString("bkp_type"));
			vo.setBaseline(rs.getInt("baseline"));
		} catch (Exception e) {
			SysLogger.error("VPNFileConfigDao.loadFromRS()", e);
			vo = null;
		}
		return vo;
	}

	/*
	 * 获得含有配置文件的设备，List返回含有的设备。
	 */
	public List getAllcfgList() {
		List list = null;
		String sql = "";
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			sql = "select max(id) 'id',timingId,ipaddress,fileName,content,max(backup_time) 'backup_time',file_size,bkp_type,baseline from nms_vpncfg  group by ipaddress";
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			sql = "select * from nms_vpncfg where backup_time in(select max(backup_time) as backup_time from nms_vpncfg   group by  ipaddress) and id in(select max(id) as id from nms_vpncfg  group by  ipaddress)";
		}
		
		// String sql = "select * from nms_vpncfg order by
		// ipaddress,backup_time";
		list = this.findByCriteria(sql);
		return list;
	}

	public List getcfgListByIp(String ip) {
		List list = null;
		String sql = "select * from nms_vpncfg where ipaddress='" + ip
				+ "' order by ipaddress,backup_time";
		list = this.findByCriteria(sql);
		return list;
	}

	/*
	 * 获得含有配置文件的设备，List返回含有最近一次备份文件更新的设备。
	 */
	public List getDeviceByIps(List iplist, String type) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < iplist.size(); i++) {
			if (i < iplist.size() - 1) {
				sBuffer.append(iplist.get(i) + "','");
			} else {
				sBuffer.append(iplist.get(i) + "'");
			}

		}
		List list = null;
		String sql = "select max(id) 'id',timingId,ipaddress,fileName,content,max(backup_time) 'backup_time',file_size,bkp_type from nms_hua3vpncfg where ipaddress in('"
				+ sBuffer.toString()
				+ ") and bkp_type='"
				+ type
				+ "' group by ipaddress";
		list = this.findByCriteria(sql);
		return list;
	}

}
