package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.CfgCmdFile;
import com.afunms.config.model.CiscoSlaCfgCmdFile;

public class SlaCfgCmdFileDao extends BaseDao implements DaoInterface{
    public SlaCfgCmdFileDao(){
    	super("sla_config_command");
    }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		CiscoSlaCfgCmdFile vo=new CiscoSlaCfgCmdFile();
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setFilename(rs.getString("filename"));
			vo.setCreateBy(rs.getString("create_by"));
			vo.setCreateTime(rs.getString("create_time"));
			vo.setFileDesc(rs.getString("fileDesc"));
			vo.setSlatype(rs.getString("slatype"));
			vo.setDevicetype(rs.getString("devicetype"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vo;
	}

	public boolean save(BaseVo vo) {
		CiscoSlaCfgCmdFile cmdFile=(CiscoSlaCfgCmdFile)vo;
		StringBuffer sql = new StringBuffer();
		int id=this.getNextID();
		sql.append("insert into sla_config_command(id,name,filename,create_by,create_time,fileDesc,slatype,devicetype) values(");
		sql.append(id);
		sql.append(",'");
		sql.append(cmdFile.getName());
		sql.append("','");
		sql.append(cmdFile.getFilename());
		sql.append("','");
		sql.append(cmdFile.getCreateBy());
		sql.append("','");
		sql.append(cmdFile.getCreateTime());
		sql.append("','");
		sql.append(cmdFile.getFileDesc());
		sql.append("','");
		sql.append(cmdFile.getSlatype());
		sql.append("','");
		sql.append(cmdFile.getDevicetype());
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
		sql.append("',create_by='");
		sql.append(cmdFile.getCreateBy());
		sql.append("',create_time='");
		sql.append(cmdFile.getCreateTime());
		sql.append("',fileDesc='");
		sql.append(cmdFile.getFileDesc());
		sql.append("',slatype='");
		sql.append(cmdFile.getSlatype());
		sql.append("',devicetype='");
		sql.append(cmdFile.getDevicetype());
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
	            conn.addBatch("delete from sla_config_command where id=" + id[i]);
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
	public List loadAllType()
	   {
		   List list = new ArrayList();
		   try 
		   {
			   rs = conn.executeQuery("select distinct slatype from sla_config_command order by slatype");
			   if(rs == null)return null;
			   while(rs.next())
				  list.add(rs.getString("slatype"));				
		   } 
		   catch(Exception e) 
		   {
			   e.printStackTrace();
	           list = null;
			   SysLogger.error("BaseDao.loadAll()",e);
		   }
		   finally
		   {
			   if(rs != null){
				   try{
					   rs.close();
				   }catch(Exception e){
				   }
			   }
			   conn.close();
		   }
		   return list;	
	   }
}
