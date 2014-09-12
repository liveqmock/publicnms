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
import com.afunms.config.model.SlaNodeProp;

public class SlaNodePropDao extends BaseDao implements DaoInterface{
    public SlaNodePropDao(){
    	super("nms_slanodeprop");
    }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		SlaNodeProp vo=new SlaNodeProp();
		try {
			vo.setId(rs.getInt("id"));
			vo.setTelnetconfigid(rs.getInt("telnetconfigid"));
			vo.setEntrynumber(rs.getInt("entrynumber"));
			vo.setSlatype(rs.getString("slatype"));
			vo.setBak(rs.getString("bak"));
			vo.setCreatetime(rs.getString("createtime"));
			vo.setOperatorid(rs.getInt("operatorid"));
			vo.setAdminsign(rs.getString("adminsign"));
			vo.setOperatesign(rs.getString("operatesign"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vo;
	}

	public boolean save(BaseVo vo) {
		SlaNodeProp node=(SlaNodeProp)vo;
		StringBuffer sql = new StringBuffer();
		int id=this.getNextID();
		sql.append("insert into nms_slanodeprop(id,telnetconfigid,entrynumber,slatype,bak,createtime,operatorid,adminsign,operatesign) values(");
		sql.append(id);
		sql.append(",");
		sql.append(node.getTelnetconfigid());
		sql.append(",");
		sql.append(node.getEntrynumber());
		sql.append(",'");
		sql.append(node.getSlatype());
		sql.append("','");
		sql.append(node.getBak());
		sql.append("','");
		sql.append(node.getCreatetime());
		sql.append("',");
		sql.append(node.getOperatorid());
		sql.append(",'");
		sql.append(node.getAdminsign());
		sql.append("','");
		sql.append(node.getOperatesign());
		sql.append("')");
		
		return saveOrUpdate(sql.toString());
	}
	
	  public int  getNextEntryNumberByNodeId(int telnetconfigid){
		  int nextentry=0;
		  try
		     {
		         rs = conn.executeQuery("select max(entrynumber) as maxentry from nms_slanodeprop where telnetconfigid="+telnetconfigid);
		         while(rs.next())
		         {
		        	 
		        	 nextentry = rs.getInt("maxentry");
		         }
		         nextentry = nextentry +1;
		        	
		     }
		     catch(Exception e)
		     {
		         SysLogger.error("SlaNodePropDao:getNextEntryNumberByNodeId()",e);
		         
		     }
		     finally
		     {
		    	 try{
		    		 rs.close();
		    	 }catch(Exception e){
		    		 
		    	 }
		         //conn.close();
		     }
		     return nextentry;
	  }

	public boolean update(BaseVo vo) {
		SlaNodeProp node=(SlaNodeProp)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_slanodeprop set telnetconfigid=");
		sql.append(node.getTelnetconfigid());
		sql.append(",entrynumber=");
		sql.append(node.getEntrynumber());
		sql.append("',slatype='");
		sql.append(node.getSlatype());
		sql.append("',bak='");
		sql.append(node.getBak());
		sql.append("',createtime='");
		sql.append(node.getCreatetime());
		sql.append("',operatorid=");
		sql.append(node.getOperatorid());
		sql.append("',adminsign='");
		sql.append(node.getAdminsign());
		sql.append("',operatesign='");
		sql.append(node.getOperatesign());
		sql.append("' where id="+node.getId());
		
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
