package com.afunms.security.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.HostNode;

public class UpsDao extends BaseDao implements DaoInterface {
	
private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
	
	public UpsDao()
	{
		super("topo_host_node");		
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		HostNode vo = new HostNode();
	       try
	       {
			   vo.setId(rs.getInt("id"));
			   vo.setAssetid(rs.getString("asset_id"));
			   vo.setLocation(rs.getString("location"));
			   vo.setIpAddress(rs.getString("ip_address"));
			   vo.setIpLong(rs.getLong("ip_long"));
			   vo.setSysName(rs.getString("sys_name"));
			   vo.setAlias(rs.getString("alias"));
			   vo.setNetMask(rs.getString("net_mask"));
			   vo.setSysDescr(rs.getString("sys_descr"));
			   vo.setSysLocation(rs.getString("sys_location"));
			   vo.setSysContact(rs.getString("sys_contact"));
			   vo.setSysOid(rs.getString("sys_oid"));
			   vo.setCommunity(rs.getString("community"));
			   vo.setWriteCommunity(rs.getString("write_community"));
			   vo.setSnmpversion(rs.getInt("snmpversion"));
			   vo.setTransfer(rs.getInt("transfer"));
			   vo.setCategory(rs.getInt("category"));		   
			   vo.setManaged(rs.getInt("managed")==1?true:false);
			   vo.setType(rs.getString("type"));
			   vo.setSuperNode(rs.getInt("super_node"));
			   vo.setLocalNet(rs.getInt("local_net"));
			   vo.setLayer(rs.getInt("layer"));
			   vo.setBridgeAddress(rs.getString("bridge_address"));
			   vo.setStatus(rs.getInt("status"));
			   vo.setDiscovertatus(rs.getInt("discoverstatus"));
			   vo.setOstype(rs.getInt("ostype"));
			   vo.setCollecttype(rs.getInt("collecttype"));
			   vo.setSendemail(rs.getString("sendemail"));
			   vo.setSendmobiles(rs.getString("sendmobiles"));
			   vo.setSendphone(rs.getString("sendphone"));
			   vo.setBid(rs.getString("bid"));
			   vo.setEndpoint(rs.getInt("endpoint"));
			   vo.setSupperid(rs.getInt("supperid"));//snow add at 2010-05-18
			   //SNMP V3
			   vo.setSecuritylevel(rs.getInt("securitylevel"));
			   vo.setSecurityName(rs.getString("securityName"));
			   vo.setV3_ap(rs.getInt("v3_ap"));
			   vo.setAuthpassphrase(rs.getString("authpassphrase"));
			   vo.setV3_privacy(rs.getInt("v3_privacy"));
			   vo.setPrivacyPassphrase(rs.getString("privacyPassphrase"));
			   //SysLogger.info(vo.getSendemail()+"==="+vo.getBid());
	       }
	       catch(Exception e)
	       {
	 	       SysLogger.error("HostNodeDao.loadFromRS()",e); 
	       }	   
	       return vo;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
