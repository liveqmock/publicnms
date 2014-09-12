package com.afunms.application.dao;

import java.sql.ResultSet;

import com.afunms.application.model.FWTypeVo;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class FWTypeDao extends BaseDao implements DaoInterface
{
	  

	public FWTypeDao()
	   {
		   super("nms_firewalltype");
	   }

	   public boolean update(BaseVo baseVo)
	   {
		   FWTypeVo vo = (FWTypeVo)baseVo;	
		   StringBuffer sql = new StringBuffer();
		   sql.append("update nms_firewalltype set firewalltype ='");
		   sql.append(vo.getFirewalltype());
		   sql.append("',firewalldesc='");
		   sql.append(vo.getFirewalldesc());	   
		   sql.append("' where id=");
		   sql.append(vo.getId());
		   
		   return saveOrUpdate(sql.toString());
	   }
	   
	   public boolean save(BaseVo baseVo)
	   {
		   FWTypeVo vo = (FWTypeVo)baseVo;	
		   StringBuffer sql = new StringBuffer();
		   sql.append("insert into nms_firewalltype(id,firewalltype,firewalldesc)values(");
		   sql.append(vo.getId());
		   sql.append(",'");
		   sql.append(vo.getFirewalltype());
		   sql.append("','");
		   sql.append(vo.getFirewalldesc());   
		   sql.append("')");
		   
		   return saveOrUpdate(sql.toString());
	   }
	   
	   public FWTypeVo findByFwtype(String firewalltype)
	   {
		   FWTypeVo vo = null;
	      try
	      {
	          rs = conn.executeQuery("select * from nms_firewalltype where firewalltye='" + firewalltype + "'");
	          if(rs.next())
	             vo = (FWTypeVo)loadFromRS(rs);
	      }
	      catch(Exception e)
	      {
	          SysLogger.error("FWTypeDao.findByFwtype",e);
	          vo = null;
	      }
	      finally
	      {
	          conn.close();
	      }
	      return vo;
	   }
	   
	   public boolean delete(String id)
	   {
		   boolean result = false;
		   try
		   {
			   conn.addBatch("delete from nms_firewalltype where id=" + id);
			   conn.executeBatch();
			   result = true;
		   }
		   catch(Exception e)
		   {
			   SysLogger.error("FWTypeDao.delete()",e); 
		   }
		   finally
		   {
			   conn.close();
		   }
		   return result;
	   }
	   
	   public BaseVo loadFromRS(ResultSet rs)
	   {
		   FWTypeVo vo = new FWTypeVo();
	       try
	       {
			   vo.setId(rs.getInt("id"));
			   vo.setFirewalltype(rs.getString("firewalltype"));
			   vo.setFirewalldesc(rs.getString("firewalldesc"));		   
	       }
	       catch(Exception e)
	       {
	   	       SysLogger.error("FWTypeDao.loadFromRS()",e); 
	       }	   
	       return vo;
	   }
	}  