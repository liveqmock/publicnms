package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.MQchannel;

/**
 * mq 通道信息
 * @author
 * 
 */
public class MQchannelDao extends BaseDao implements DaoInterface {
	public MQchannelDao() {
		super("nms_diskbusyconfig");
	}

	public List loadAll() {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select t1.*,t2.NAME as mqname from nms_mqchannel t1,nms_mqconfig t2 " +
					"where t1.IPADDRESS=t2.IPADDRESS " +
					"order by t1.ipaddress,t1.name");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return list;
	}
	/**
	 * 根据id，查询通道信息
	 * @return
	 */
	public List loadById(String id) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select t1.*,t2.NAME as mqname from nms_mqchannel t1,nms_mqconfig t2 " +
					"where t1.IPADDRESS=t2.IPADDRESS  and t2.id='"+id+"' " +
					"order by t1.ipaddress,t1.name");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return list;
	}
	/**
	 * 查询所有 带权限
	 * @author 曹小虎
	 * @return
	 */
	public List loadAllByBID(Vector bids) {
		String wstr = "";
		if (bids != null && bids.size() > 0) {
			for (int i = 0; i < bids.size(); i++) {
				if (wstr.trim().length() == 0) {
					wstr = wstr + " and ( t2.netid = '," + bids.get(i) + ",' ";
				} else {
					wstr = wstr + " or t2.netid = '," + bids.get(i) + ",' ";
				}

			}
			wstr = wstr + ")";
		}
		
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select t1.*,t2.NAME as mqname from nms_mqchannel t1,nms_mqconfig t2 " +
					"where t1.ipaddress=t2.ipaddress "+wstr+" order by t1.ipaddress,t1.name");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return list;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		MQchannel vo = new MQchannel();
		try {
			vo.setId(rs.getInt("id"));
			vo.setBak(rs.getString("bak"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setLinkuse(rs.getString("linkuse"));
			vo.setName(rs.getString("name"));
			vo.setMqName(rs.getString("mqname"));
			vo.setChannelindex(rs.getInt("channelindex"));
			vo.setMonflag(rs.getInt("monflag"));
			vo.setReportflag(rs.getInt("reportflag"));
			vo.setSms(rs.getInt("sms"));
			vo.setSms1(rs.getInt("sms1"));
			vo.setSms2(rs.getInt("sms2"));
			vo.setSms3(rs.getInt("sms3"));
			vo.setLimenvalue(rs.getInt("limenvalue"));
			vo.setLimenvalue1(rs.getInt("limenvalue1"));
			vo.setLimenvalue2(rs.getInt("limenvalue2"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean update(BaseVo vo) {
		return false;
	}
}
