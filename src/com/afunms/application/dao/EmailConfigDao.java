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
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class EmailConfigDao extends BaseDao implements DaoInterface {

	public EmailConfigDao() {
		super("nms_emailmonitorconf");
	}

	public boolean delete(String[] ids) {
		return super.delete(ids);
	}

	public BaseVo loadFromRS(ResultSet rs) {
		EmailMonitorConfig vo = new EmailMonitorConfig();

		try {
			vo.setId(rs.getInt("id"));
			vo.setAddress(rs.getString("address"));
			vo.setBid(rs.getString("bid"));
			vo.setFlag(rs.getInt("flag"));
			vo.setMonflag(rs.getInt("monflag"));
			vo.setName(rs.getString("name"));
			vo.setPassword(rs.getString("password"));
			vo.setRecivemail(rs.getString("recivemail"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setTimeout(rs.getInt("timeout"));
			vo.setUsername(rs.getString("username"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setSupperid(rs.getInt("supperid"));// snow add at 2010-5-21
			vo.setReceiveAddress(rs.getString("receiveAddress"));
		} catch (SQLException e) {

			SysLogger.error("WebConfigDao.loadFromRS()", e);
		}
		return vo;
	}

	public List<EmailMonitorConfig> getEmailConfigListByMonFlag(Integer flag) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_emailmonitorconf where monflag= ");
		sql.append(flag);
		return findByCriteria(sql.toString());
	}

	public boolean save(BaseVo vo) {
		EmailMonitorConfig vo1 = (EmailMonitorConfig) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_emailmonitorconf(id,address,bid,flag,monflag,name,password,");
		sql.append("recivemail,sendmobiles,sendemail,sendphone,timeout,username,ipaddress,supperid,receiveAddress) values('");
		sql.append(vo1.getId());
		sql.append("','");
		sql.append(vo1.getAddress());
		sql.append("','");
		sql.append(vo1.getBid());
		sql.append("','");
		sql.append(vo1.getFlag());
		sql.append("','");
		sql.append(vo1.getMonflag());
		sql.append("','");
		sql.append(vo1.getName());
		sql.append("','");
		sql.append(vo1.getPassword());
		sql.append("','");
		sql.append(vo1.getRecivemail());
		sql.append("','");
		sql.append(vo1.getSendmobiles());
		sql.append("','");
		sql.append(vo1.getSendemail());
		sql.append("','");
		sql.append(vo1.getSendphone());
		sql.append("','");
		sql.append(vo1.getTimeout());
		sql.append("','");
		sql.append(vo1.getUsername());
		sql.append("','");
		sql.append(vo1.getIpaddress());
		sql.append("','");
		sql.append(vo1.getSupperid());
		sql.append("','");
		sql.append(vo1.getReceiveAddress());
		sql.append("')");
		return saveOrUpdate(sql.toString());

	}

	public List getByBID(Vector bids) {
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
		sql.append("select * from nms_emailmonitorconf " + wstr);
		return findByCriteria(sql.toString());
	}

	public List getByBIDAndFlag(String _bids, int flag) {
		StringBuffer sql = new StringBuffer();
		String[] bids = _bids.split(",");
		StringBuffer s = new StringBuffer();
		if (bids.length > 0) {
			int _flag = 0;
			for (int i = 0; i < bids.length; i++) {
				if (bids[i].trim().length() > 0) {
					if (_flag == 0) {
						s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
						_flag = 1;
					} else {
						s.append(" or bid like '%," + bids[i].trim() + ",%' ");
					}
				}
			}
			s.append(") ");
		}
		if (flag != -1) {
			s.append(" and monflag =" + flag);
		}
		sql.append("select * from nms_emailmonitorconf where 1=1 " + s.toString());
		return findByCriteria(sql.toString());
	}

	public boolean update(BaseVo vo) {
		EmailMonitorConfig vo1 = (EmailMonitorConfig) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_emailmonitorconf set address='");
		sql.append(vo1.getAddress());
		sql.append("',bid='");
		sql.append(vo1.getBid());
		sql.append("',flag='");
		sql.append(vo1.getFlag());
		sql.append("',monflag='");
		sql.append(vo1.getMonflag());
		sql.append("',name='");
		sql.append(vo1.getName());
		sql.append("',password='");
		sql.append(vo1.getPassword());
		sql.append("',recivemail='");
		sql.append(vo1.getRecivemail());
		sql.append("',sendmobiles='");
		sql.append(vo1.getSendmobiles());
		sql.append("',sendemail='");
		sql.append(vo1.getSendemail());
		sql.append("',sendphone='");
		sql.append(vo1.getSendphone());
		sql.append("',timeout='");
		sql.append(vo1.getTimeout());
		sql.append("',username='");
		sql.append(vo1.getUsername());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpaddress());
		sql.append("',supperid='");
		sql.append(vo1.getSupperid());
		sql.append("',receiveAddress='");
		sql.append(vo1.getReceiveAddress());
		sql.append("' where id=" + vo1.getId());
		return saveOrUpdate(sql.toString());
	}

}