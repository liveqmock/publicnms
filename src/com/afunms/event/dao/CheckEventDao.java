/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.CheckEvent;

public class CheckEventDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public CheckEventDao() {
		super("nms_checkevent");
	}

	// -------------load all --------------
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_checkevent");
			while (rs.next())
        	list.add(loadFromRS(rs)); 
     }
     catch(Exception e)
     {
         SysLogger.error("CheckEventDao:loadAll()",e);
         list = null;
     }
     finally
     {
         conn.close();
     }
     return list;
  }

  public List loadByWhere(String where){
	  List list = new ArrayList(); 
      try {
    	  rs = conn.executeQuery("select * from nms_checkevent " + where);
		while(rs.next()){
			  list.add(loadFromRS(rs)); 
		  }
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	  return list;
  }
	
	
	
  
	public boolean save(BaseVo baseVo)
	{
		CheckEvent vo = (CheckEvent)baseVo;
		//先删除,如果有该指标告警
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_checkevent(nodeid,indicators_name,sindex,type,subtype,alarmlevel,thevalue,collecttime,bid)values(");
		sql.append("'");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIndicatorsName());
		sql.append("','");
		sql.append(vo.getSindex());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("',");
		sql.append(vo.getAlarmlevel());	
		sql.append(",'");
		sql.append(vo.getThevalue());	
		sql.append("','");
		sql.append(vo.getCollecttime());	
		sql.append("','");
		sql.append(vo.getBid());	
		sql.append("')");
		System.out.println(sql.toString());
		return saveOrUpdate(sql.toString());
	}
	
	public boolean savecheckevent(BaseVo baseVo)
	{
		CheckEvent vo = (CheckEvent)baseVo;
		//先删除,如果有该指标告警
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_checkevent(nodeid,indicators_name,sindex,type,subtype,alarmlevel,content,thevalue,collecttime)values(");
		sql.append("'");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIndicatorsName());
		sql.append("','");
		sql.append(vo.getSindex());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("',");
		sql.append(vo.getAlarmlevel());	
		sql.append(",'");
		sql.append(vo.getContent());
		sql.append("','");
		sql.append(vo.getThevalue());	
		sql.append("','");
		sql.append(vo.getCollecttime());	
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
//	public boolean delete(String name)
//	{
//		boolean flag = true;
//		String sql = "delete from nms_checkevent where name='"+name+"'";
//		//SysLogger.info(sql);
//		try{
//			conn.executeUpdate(sql);
//		}catch(Exception e){
//			flag = false;
//		}
//		return flag;
//	}
	
	public boolean deleteByNodeType(String nodeid,String type)
	{
		boolean flag = true;
		String sql = "delete from nms_checkevent where nodeid='"+nodeid+"' and type ='"+type+"'";
		try{
			conn.executeUpdate(sql);
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}
	
	public boolean empty()
	{
		boolean flag = true;
		String sql = "delete from nms_checkevent";
		try{
			conn.executeUpdate(sql);
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}
	
  //---------------update a business----------------
  public boolean update(BaseVo baseVo)
  {    
     return true;
  }
  
	public boolean delete(String[] id)
	{
	    return true;
	}
  
//  public int findByName(String name)
//  {
//	  int flag = 0;
//	  CheckEvent vo = null;
//     try
//     {
////    	 System.out.println("select * from nms_checkevent where name='" + name+"'" );
////    	 System.out.println(rs);
////    	 System.out.println(conn);
//        rs = conn.executeQuery("select * from nms_checkevent where name='" + name+"'" );
//        if(rs.next()){
//        	vo = (CheckEvent)loadFromRS(rs);
//        	flag = vo.getAlarmlevel();
//        }
//           
//     }
//     catch(Exception e)
//     {
//    	 e.printStackTrace();
//         //SysLogger.error("EventListDao.findByID()",e);
//         //vo = null;
//     }
//     finally
//     {
//        //conn.close();
//     }
//     return flag;
//  }
  
  public CheckEvent findLikeName(String name)
  {
	  int flag = 0;
	  CheckEvent vo = null;
     try
     {
//    	 System.out.println("select * from nms_checkevent where name='" + name+"'" );
//    	 System.out.println(rs);
//    	 System.out.println(conn);
        rs = conn.executeQuery("select * from nms_checkevent where name like '" + name+"'" );
        if(rs.next()){
        	vo = (CheckEvent)loadFromRS(rs);
        	flag = vo.getAlarmlevel();
        }
           
     }
     catch(Exception e)
     {
    	 e.printStackTrace();
         //SysLogger.error("EventListDao.findByID()",e);
         //vo = null;
     }
     finally
     {
        //conn.close();
     }
     return vo;
  }
  
//  	public int findMaxAlarmLevelByName(String name) {
//		int flag = 0;
//		try {
//			rs = conn.executeQuery("select max(alarmlevel) from nms_checkevent where name like '%"
//					+ name + "%'");
//			if (rs.next()) {
//				flag = rs.getInt("max(alarmlevel)");
//			}
//
//		} catch (Exception e) {
//			
//		} finally {
//			
//		}
//		
//		//System.out.println(flag+"==================" + name);
//		return flag;
//	}
  	
//  	public Hashtable findMaxAlarmLevelsOrderByNames() {
//		int flag = 0;
//		Hashtable temphash = new Hashtable();
//		CheckEvent vo = null;
//		try {
//			rs = conn.executeQuery("select * from nms_checkevent ");
//			while(rs.next()){
//				  vo = (CheckEvent)loadFromRS(rs); 
//				  String name = vo.getName();
//				  String[] splits = name.split(":");
//				  if(splits.length > 2){
//					  String twonames = splits[0]+":"+splits[1];
//					  if(temphash.containsKey(twonames)){
//						  int oldlevel = (Integer)temphash.get(twonames);
//						  if(oldlevel < vo.getAlarmlevel()){
//							  temphash.put(twonames, vo.getAlarmlevel());
//						  }
//					  }else{
//						  temphash.put(twonames, vo.getAlarmlevel());
//					  }
//				  }				  
//			  }
//		} catch (Exception e) {
//			
//		} finally {
//			
//		}
//		return temphash;
//	}
  
   public BaseVo loadFromRS(ResultSet rs)
   {
	   CheckEvent vo = new CheckEvent();
      try
      {
    	  vo.setNodeid(rs.getString("nodeid"));
    	  vo.setType(rs.getString("type"));
    	  vo.setSubtype(rs.getString("subtype"));
    	  vo.setContent(rs.getString("content"));
          vo.setIndicatorsName(rs.getString("indicators_name"));
          vo.setSindex(rs.getString("sindex"));
          vo.setAlarmlevel(rs.getInt("alarmlevel"));
          vo.setCollecttime(rs.getString("collecttime"));
          vo.setThevalue(rs.getString("thevalue"));
          vo.setBid(rs.getString("bid"));
      }
      catch(Exception e)
      {
          SysLogger.error("EventListDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   } 
   
   public BaseVo findCheckEventByName(String nodeId,String type,String subtype,String name,String sindex){
	    CheckEvent vo = null;
	    try
	    {
	    	if(sindex!=null&&!"".equals(sindex)&&!"null".equals(sindex)){
	    		rs = conn.executeQuery("select * from nms_checkevent where nodeid='"+ nodeId +"' and type='" + type+"' and subtype='" + subtype+"' and indicators_name='" +name +"' and sindex='"+sindex+"'");
	    	} else {
	    		rs = conn.executeQuery("select * from nms_checkevent where nodeid='"+ nodeId +"' and type='" + type+"' and subtype='" + subtype+"' and indicators_name='" +name +"'");
	    	}
	        if(rs.next()){
	        	vo = (CheckEvent)loadFromRS(rs);
	        }
	    } catch(Exception e) {
	    	SysLogger.error("CheckEventDao.findByID()",e);
	    }
	    return vo;
  }
   
    public List<CheckEvent> findCheckEvent(String nodeId,String type,String subtype,String name,String sindex){
    	List<CheckEvent> list = new ArrayList<CheckEvent>();
	    try {
	    	if(sindex!=null&&!"".equals(sindex)&&!"null".equals(sindex)){
	    		rs = conn.executeQuery("select * from nms_checkevent where nodeid='"+ nodeId +"' and type='" + type+"' and subtype='" + subtype+"' and indicators_name='" +name +"' and sindex='"+sindex+"'");
	    	} else {
	    		rs = conn.executeQuery("select * from nms_checkevent where nodeid='"+ nodeId +"' and type='" + type+"' and subtype='" + subtype+"' and indicators_name='" +name +"'");
	    	}
	    	while(rs.next()){
	    		list.add((CheckEvent)loadFromRS(rs));
	    	}
	    } catch(Exception e) {
	    	SysLogger.error("CheckEventDao.findCheckEvent(String nodeId,String type,String subtype,String name,String sindex)",e);
	    }
	    return list;
    }
    
    public List<CheckEvent> findCheckEvent(String nodeId,String type,String subtype,String name){
    	List<CheckEvent> list = new ArrayList<CheckEvent>();
	    try {
	    	rs = conn.executeQuery("select * from nms_checkevent where nodeid='"+ nodeId +"' and type='" + type+"' and subtype='" + subtype+"' and indicators_name='" +name +"'");
	    	while(rs.next()){
	    		list.add((CheckEvent)loadFromRS(rs));
	    	}
	    } catch(Exception e) {
	    	SysLogger.error("CheckEventDao.findCheckEvent(String nodeId,String type,String subtype,String name)",e);
	    }
	    return list;
    }
    
    public List<CheckEvent> findCheckEvent(String nodeId){
    	List<CheckEvent> list = new ArrayList<CheckEvent>();
	    try {
	    	rs = conn.executeQuery("select * from nms_checkevent where nodeid='"+ nodeId +"'");
	    	while(rs.next()){
	    		list.add((CheckEvent)loadFromRS(rs));
	    	}
	    } catch(Exception e) {
	    	SysLogger.error("CheckEventDao.findCheckEvent(String nodeId)",e);
	    }
	    return list;
    }
    
    public boolean deleteCheckEvent(String nodeId,String type,String subtype,String name){
	    return saveOrUpdate("delete from nms_checkevent where nodeid='"+ nodeId +"' and type='" + type+"' and subtype='" + subtype+"' and indicators_name='" +name +"'");
    }
    public boolean deleteCheckEvent(String nodeId,String type,String subtype){
	    return saveOrUpdate("delete from nms_checkevent where nodeid='"+ nodeId +"' and type='" + type+"' and subtype='" + subtype+"'" );
    }
    public boolean deleteCheckEvent(String nodeId,String type,String subtype,String name, String sindex){
	    SysLogger.info("delete from nms_checkevent where nodeid='"+ nodeId +"' and type='" + type+"' and subtype='" + subtype+"' and indicators_name='" +name +"' and sindex='" + sindex + "'");
    	return saveOrUpdate("delete from nms_checkevent where nodeid='"+ nodeId +"' and type='" + type+"' and subtype='" + subtype+"' and indicators_name='" +name +"' and sindex='" + sindex + "'");
    }
    
    public boolean deleteCheckEvent(){
	    return saveOrUpdate("delete from nms_checkevent where 1=1");
    }
}
