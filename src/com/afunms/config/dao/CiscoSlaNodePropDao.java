package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.CiscoSlaCfgCmdFile;
import com.afunms.config.model.CiscoSlaNodeProp;

public class CiscoSlaNodePropDao extends BaseDao implements DaoInterface {
	public CiscoSlaNodePropDao(){
    	super("nms_slanodeprop");
    }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		CiscoSlaNodeProp vo=new CiscoSlaNodeProp();
		try {
			vo.setId(rs.getInt("id"));
			vo.setTelnetconfigid(rs.getInt("telnetconfigid"));
			vo.setEntrynumber(rs.getInt("entrynumber"));
			vo.setCreateBy(rs.getString("operatorid"));
			vo.setCreateTime(rs.getString("createtime"));
			vo.setSlatype(rs.getString("slatype"));
			vo.setBak(rs.getString("bak"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vo;
	}

	public boolean save(BaseVo vo) {
		CiscoSlaNodeProp nodeprop=(CiscoSlaNodeProp)vo;
		StringBuffer sql = new StringBuffer();
		int id=this.getNextID();
		sql.append("insert into nms_slanodeprop(id,telnetconfigid,entrynumber,operatorid,createtime,slatype,bak) values(");
		sql.append(id);
		sql.append(",");
		sql.append(nodeprop.getTelnetconfigid());
		sql.append(",");
		sql.append(nodeprop.getEntrynumber());
		sql.append(",'");
		sql.append(nodeprop.getCreateBy());
		sql.append("','");
		sql.append(nodeprop.getCreateTime());
		sql.append("','");
		sql.append(nodeprop.getSlatype());
		sql.append("','");
		sql.append(nodeprop.getBak());
		sql.append("')");
		
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		CiscoSlaCfgCmdFile cmdFile=(CiscoSlaCfgCmdFile)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update sla_config_command set name='");
		sql.append(cmdFile.getName());
		sql.append("',filename='");
		sql.append(cmdFile.getFilename());
		sql.append("',operatorid='");
		sql.append(cmdFile.getCreateBy());
		sql.append("',createtime='");
		sql.append(cmdFile.getCreateTime());
		sql.append("',fileDesc='");
		sql.append(cmdFile.getFileDesc());
		sql.append("',slatype='");
		sql.append(cmdFile.getSlatype());
		sql.append("' where id="+cmdFile.getId());
		
		return saveOrUpdate(sql.toString());
	}
	
	public boolean delete(String[] id)
	{
		boolean result = false;
	    try
	    {	    
	        for(int i=0;i<id.length;i++)
	        {
	            conn.addBatch("delete from nms_slanodeprop where id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("SlaCfgCmdFileDao.delete()",e);	        
	    }
	    finally
	    {
	         conn.close();
	    }
	    return result;
	}
	public List findSlaType(){
		List result = new ArrayList();
		String sql = "select distinct(slatype) from sla_config_command";
		try {
			rs = conn.executeQuery(sql);
			while(rs.next()){
				result.add((String)rs.getString("slatype"));
			}
		}catch (Exception e) {
		}
		return result;
	}
	public HashMap<Integer,String> findTelnetIP(){
		HashMap<Integer,String> result = new HashMap<Integer,String>();
		String sql = "select distinct(ip_address),id from topo_node_telnetconfig";
		try {
			rs = conn.executeQuery(sql);
			while(rs.next()){
				result.put(rs.getInt("id"),rs.getString("ip_address"));
			}
		}catch (Exception e) {
		}
		return result;
	}
}
