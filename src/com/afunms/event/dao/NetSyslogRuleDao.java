/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.dao;

import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.ResultSet;


import com.afunms.common.util.SysLogger;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.DaoInterface;
import com.afunms.event.model.NetSyslogRule;
import com.afunms.common.base.BaseVo;

public class NetSyslogRuleDao extends BaseDao implements DaoInterface
{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public NetSyslogRuleDao()
  {
	  super("nms_netsyslogrule");	  
  }
  
  //-------------load all --------------
  public List loadAll()
  {
     List list = new ArrayList(5);
     try
     {
         rs = conn.executeQuery("select * from nms_netsyslogrule order by id desc");
         while(rs.next())
        	list.add(loadFromRS(rs)); 
     }
     catch(Exception e)
     {
         SysLogger.error("NetSyslogRuleDao:loadAll()",e);
         list = null;
     }
     finally
     {
         conn.close();
     }
     return list;
  }

	public List getQuery(String starttime,String totime,String status,String level,String businessid,Integer nodeid) throws Exception{
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		s.append("select * from system_eventlist e where e.recordtime>= '"+starttime+"' " +
				"and e.recordtime<='"+totime+"'");
		if(!"99".equals(level)){
			s.append(" and e.level1="+level);
		}
		if(!"99".equals(status)){
			s.append(" and e.managesign="+status);
		}
		
		if (nodeid != null){
			if(nodeid.intValue()!=99){
				s.append(" and nodeid="+nodeid);
			}	
		}
		int flag = 0;
		if (businessid != null){
			if(businessid !="-1"){
				String[] bids = businessid.split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(flag==0){
								s.append(" and ( businessid = ',"+bids[i].trim()+",' ");
								flag = 1;
							}else{
								//flag = 1;
								s.append(" or businessid = ',"+bids[i].trim()+",' ");
							}
						}
					}
					s.append(") ") ;
				}
				
			}	
		}
		s.append(" order by e.recordtime desc");		
		String sql = s.toString();
		SysLogger.info(sql);
		try{
			rs = conn.executeQuery(sql);
			while(rs.next())
	        	list.add(loadFromRS(rs));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
  
	public boolean save(BaseVo baseVo)
	{
		NetSyslogRule vo = (NetSyslogRule)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_netsyslogrule(facility,priority)values(");
		sql.append("\"");
		sql.append(vo.getFacility());
		sql.append("\",\"");
		sql.append(vo.getPriority());
		sql.append("\")");
		return saveOrUpdate(sql.toString());
	}
	
  //---------------update a business----------------
  public boolean update(BaseVo baseVo)
  {
	  boolean result = false;
	  NetSyslogRule vo = (NetSyslogRule)baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_netsyslogrule set facility='");
		sql.append(vo.getFacility());
		sql.append("',priority='");
		sql.append(vo.getPriority());	 
		sql.append("' where id=");
		sql.append(vo.getId());
     
     try
     {
    	 //SysLogger.info(sql.toString());
         conn.executeUpdate(sql.toString());
         result = true;
     }
     catch(Exception e)
     {
    	 result = false;
         SysLogger.error("EventListDao:update()",e);
     }
     finally
     {
    	 conn.close();
     }     
     return result;
  }
  
	public boolean delete(String[] id)
	{
		boolean result = false;
	    try
	    {	    
	        for(int i=0;i<id.length;i++)
	        {
	            conn.addBatch("delete from system_eventlist where id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("EventListDao.delete()",e);	        
	    }
	    finally
	    {
	         conn.close();
	    }
	    return result;
	}
  
  public BaseVo findByID(String id)
  {
     BaseVo vo = null;
     try
     {
        rs = conn.executeQuery("select * from system_eventlist where id=" + id );
        if(rs.next())
           vo = loadFromRS(rs);
     }
     catch(Exception e)
     {
         SysLogger.error("EventListDao.findByID()",e);
         vo = null;
     }
     finally
     {
        conn.close();
     }
     return vo;
  }
  
   public BaseVo loadFromRS(ResultSet rs)
   {
	   NetSyslogRule vo = new NetSyslogRule();
      try
      {
          vo.setId(rs.getLong("id"));
          vo.setFacility(rs.getString("facility"));
          vo.setPriority(rs.getString("priority"));
      }
      catch(Exception e)
      {
          SysLogger.error("NetSyslogRuleDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   }  
}
