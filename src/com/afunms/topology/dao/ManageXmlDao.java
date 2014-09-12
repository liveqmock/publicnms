/**
 * <p>Description:operate table nms_custom_view_ip and nms_custom_view_xml</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project 聂成海
 * @date 2006-10-21
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.ManageXml;

public class ManageXmlDao extends BaseDao implements DaoInterface
{
    public ManageXmlDao() {
    	super("topo_manage_xml");
	}
	  
	public BaseVo delete(String xmlName)
	{
		BaseVo vo = null;
	    try
	    {
	    	rs = conn.executeQuery("select * from topo_manage_xml where xml_name='" + xmlName + "' and default_view<>1");
	    	if(rs.next()){
	    		vo = loadFromRS(rs);
	    	}
	    	conn.executeUpdate("delete from topo_manage_xml where xml_name='" + xmlName + "' and default_view<>1");
	    }
	    catch(Exception ex)
	    {
	        SysLogger.error("Error in ManageXmlDao.delete()",ex);
	    }
	    finally
	    {
	        conn.close();
	    }
	    return vo;
	}
	
	public String deleteAll()
	{
		String xmlNameStr = "";
	    try
	    {
	        rs = conn.executeQuery("select * from topo_manage_xml where default_view<>1");
	        while(rs.next()){
	        	xmlNameStr = xmlNameStr + rs.getString("xml_name") + ",";
	        }
	        conn.addBatch("delete from topo_manage_xml where default_view<>1");
		    conn.executeBatch();
	    }
	    catch(Exception ex)
	    {
	        SysLogger.error("Error in ManageXmlDao.deleteAll()",ex);
	    }
	    finally
	    {
	        conn.close();
	    }
	    return xmlNameStr;
	}
	/**
	    * 按权限载入所有业务视图
	    */
   public List loadByPerAll(String bid)
   {
        List list = new ArrayList();
	    StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (bid != null){
			if(bid !="-1"){
				String[] bids = bid.split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(_flag==0){
								s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
								_flag = 1;
							}else{
								s.append(" or bid like '%,"+bids[i].trim()+",%' ");
							}
						}
					}
					s.append(") ") ;
				}
				
			}	
		}
	   try 
	   {
		   rs = conn.executeQuery("select * from topo_manage_xml where topo_type=1 "+s+" order by id");
		   if(rs == null)return null;
		   while(rs.next())
			  list.add(loadFromRS(rs));				
	   } 
	   catch(Exception e) 
	   {
		   e.printStackTrace();
           list = null;
		   SysLogger.error("ManageXmlDao.loadByPerAll()",e);
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;	
   }
   /**
    * 按权限载入所有业务视图
    */
	public List loadSubXmlByPerAll(String bid)
	{
	    List list = new ArrayList();
	    StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (bid != null){
			if(bid !="-1"){
				String[] bids = bid.split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(_flag==0){
								s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
								_flag = 1;
							}else{
								s.append(" or bid like '%,"+bids[i].trim()+",%' ");
							}
						}
					}
					s.append(") ") ;
				}
				
			}	
		}
	   try 
	   {
		   rs = conn.executeQuery("select * from topo_manage_xml where topo_type=4 or topo_type=0 "+s+" order by id");
		   if(rs == null)return null;
		   while(rs.next())
			  list.add(loadFromRS(rs));				
	   } 
	   catch(Exception e) 
	   {
		   e.printStackTrace();
	       list = null;
		   SysLogger.error("ManageXmlDao.loadByPerAll()",e);
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;	
	}
    public boolean save(BaseVo baseVo) 
    {
    	ManageXml vo = (ManageXml)baseVo;		
	    StringBuffer sql = new StringBuffer(500);
	    sql.append("insert into topo_manage_xml(id,xml_name,topo_name,alias_name,topo_title,topo_area,topo_bg,topo_type,bid,home_view,bus_home_view,zoom_percent)values(");
	    sql.append(getNextID());
	    sql.append(",'");
	    sql.append(vo.getXmlName());
	    sql.append("','");
	    sql.append(vo.getTopoName());
	    sql.append("','");
	    sql.append(vo.getAliasName());
	    sql.append("','");
	    sql.append(vo.getTopoTitle());
	    sql.append("','");
	    sql.append(vo.getTopoArea());
	    sql.append("','");
	    sql.append(vo.getTopoBg());
	    sql.append("',");
	    sql.append(vo.getTopoType());
	    sql.append(",'");
	    sql.append(vo.getBid());
	    sql.append("',");
	    sql.append(vo.getHome_view());
	    sql.append(",");
	    sql.append(vo.getBus_home_view());
	    sql.append(",");
	    sql.append(vo.getPercent());
	    sql.append(")");
	   
	    return saveOrUpdate(sql.toString());
    }
	
	public boolean update(BaseVo baseVo) 
	{
		ManageXml vo = (ManageXml)baseVo;		
	    StringBuffer sql = new StringBuffer(500);
	    sql.append("update topo_manage_xml set xml_name='");
	    sql.append(vo.getXmlName());
	    sql.append("',topo_name='");
	    sql.append(vo.getTopoName());
	    sql.append("',alias_name='");
	    sql.append(vo.getAliasName());
	    sql.append("',topo_title='");
	    sql.append(vo.getTopoTitle());
	    sql.append("',topo_area='");
	    sql.append(vo.getTopoArea());
	    sql.append("',topo_bg='");
	    sql.append(vo.getTopoBg());
	    sql.append("',topo_type=");
	    sql.append(vo.getTopoType());
	    sql.append(",bid='");
	    sql.append(vo.getBid());
	    sql.append("',max_utilhdx='");
	    sql.append(vo.getUtilhdx());
	    sql.append("',max_utilhdxperc='");
	    sql.append(vo.getUtilhdxperc());
	    sql.append("',home_view=");
	    sql.append(vo.getHome_view());
	    sql.append(",bus_home_view=");
	    sql.append(vo.getBus_home_view());
	    sql.append(",zoom_percent=");
	    sql.append(vo.getPercent());
	    sql.append(" where id=");
	    sql.append(vo.getId());
	    
	    return saveOrUpdate(sql.toString());
	}
	
	public BaseVo findByXml(String filename){
	       
    	BaseVo vo = null;
        try {
		    rs = conn.executeQuery("select * from topo_manage_xml where xml_name='" + filename + "'"); 
		    if(rs.next())
		        vo = loadFromRS(rs);
	    } catch(Exception ex) {
		    SysLogger.error("ManageXmlDao.findByXml()",ex);
	    } finally {
	    	conn.close();
	    } 
	    return vo;
	}
	
	public boolean updateView(String bid) 
	{
	    StringBuffer sql = new StringBuffer(100);
	    StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (bid != null){
			if(bid !="-1"){
				String[] bids = bid.split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(_flag==0){
								s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
								_flag = 1;
							}else{
								s.append(" or bid like '%,"+bids[i].trim()+",%' ");
							}
						}
					}
					if(_flag == 1) s.append(") ") ;
				}
				
			}	
		}
	    sql.append("update topo_manage_xml t set t.home_view = 0,t.zoom_percent = 1 where t.home_view = 1 "+s);
	    return saveOrUpdate(sql.toString());
	}
	
	public BaseVo findByView(String view,String bid){
	       
    	BaseVo vo = null;
    	StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (bid != null){
			if(bid !="-1"){
				String[] bids = bid.split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(_flag==0){
								s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
								_flag = 1;
							}else{
								s.append(" or bid like '%,"+bids[i].trim()+",%' ");
							}
						}
					}
					if(_flag == 1) s.append(") ") ;
				}
				
			}	
		}
        try {
		    rs = conn.executeQuery("select * from topo_manage_xml where home_view=" + view +" " + s); 
		    if(rs.next())
		        vo = loadFromRS(rs);
	    } catch(Exception ex) {
		    SysLogger.error("ManageXmlDao.findByView()",ex);
	    } finally {
	    	conn.close();
	    } 
	    return vo;
	}
	
	public boolean updateBusView(String bid) 
	{
	    StringBuffer sql = new StringBuffer(100);
	    StringBuffer s = new StringBuffer();
    	int _flag = 0;
		if (bid != null){
			if(bid !="-1"){
				String[] bids = bid.split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(_flag==0){
								s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
								_flag = 1;
							}else{
								s.append(" or bid like '%,"+bids[i].trim()+",%' ");
							}
						}
					}
					if(_flag == 1) s.append(") ") ;
				}
				
			}	
		}
	    sql.append("update topo_manage_xml as t set t.bus_home_view = 0,t.zoom_percent = 1 where t.bus_home_view = 1 and topo_type = 1 "+s);
	    return saveOrUpdate(sql.toString());
	}
	
	public BaseVo findByBusView(String view,String bid){
	       
    	BaseVo vo = null;
    	StringBuffer s = new StringBuffer();
    	int _flag = 0;
		if (bid != null){
			if(bid !="-1"){
				String[] bids = bid.split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(_flag==0){
								s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
								_flag = 1;
							}else{
								s.append(" or bid like '%,"+bids[i].trim()+",%' ");
							}
						}
					}
					if(_flag == 1) s.append(") ") ;
				}
				
			}	
		}
        try {
        	//SysLogger.info("select * from topo_manage_xml where bus_home_view=" + view + " and topo_type = 1 " + s);
		    rs = conn.executeQuery("select * from topo_manage_xml where bus_home_view=" + view + " and topo_type = 1 " + s); 
		    if(rs.next())
		        vo = loadFromRS(rs);
	    } catch(Exception ex) {
		    SysLogger.error("ManageXmlDao.findByBusView()",ex);
	    } finally {
	    	conn.close();
	    } 
	    return vo;
	}
	
	public List findByTopoType(int topotype){
	       
		List list = new ArrayList();
        try {
		    rs = conn.executeQuery("select * from topo_manage_xml where topo_type=" + topotype ); 
		    while(rs.next())
		    	list.add(loadFromRS(rs));
	    } catch(Exception ex) {
		    SysLogger.error("ManageXmlDao.findByXml()",ex);
	    } finally {
	    	conn.close();
	    } 
	    return list;
	}
	/**
	    * 按ID找一条记录
	    */
    public BaseVo findById(int id)
    {
	    BaseVo vo = null;
        try
	    {
		    rs = conn.executeQuery("select * from topo_manage_xml where id=" + id + " and default_view<>1"); 
		    if(rs.next())
		        vo = loadFromRS(rs);
	    }    
	    catch(Exception ex)
	    {
		    SysLogger.error("ManageXmlDao.findById()",ex);
	    }  
        return vo;
    }
			
	public List<ManageXml> loadAll() {
		List list = new ArrayList();
	   
	    String sql = "select * from topo_manage_xml";
	              
	    try {
		    rs = conn.executeQuery(sql);
		    while(rs.next())
		        list.add(loadFromRS(rs));
	    } catch(Exception e) {
		    SysLogger.error("ManageXmlDao.loadAll()",e); 
	    } finally {
		    conn.close();
	    }
	    return list;
	}
//	 根据id查找视图名称-----quzhi
    public String findNameById(int id)
    {
	    String viewname = null;
        try
	    {
		    rs = conn.executeQuery("select * from topo_manage_xml where id=" + id + " and topo_type = 1"); 
		    if(rs.next())
		    	viewname = rs.getString("topo_name");
	    }    
	    catch(Exception ex)
	    {
		    SysLogger.error("ManageXmlDao.findById()",ex);
	    }  
        return viewname;
    }
	public BaseVo loadFromRS(ResultSet rs)
	{
		ManageXml vo = new ManageXml();
		try
		{
			vo.setId(rs.getInt("id"));
			vo.setXmlName(rs.getString("xml_name"));
			vo.setTopoName(rs.getString("topo_name"));
			vo.setAliasName(rs.getString("alias_name"));
			vo.setTopoTitle(rs.getString("topo_title"));
            vo.setTopoArea(rs.getString("topo_area"));
            vo.setTopoBg(rs.getString("topo_bg"));
            vo.setTopoType(rs.getInt("topo_type"));
            vo.setBid(rs.getString("bid"));
            vo.setHome_view(rs.getInt("home_view"));
            vo.setBus_home_view(rs.getInt("bus_home_view"));
            vo.setPercent(rs.getFloat("zoom_percent"));
            vo.setUtilhdx(rs.getString("max_utilhdx"));
            vo.setUtilhdxperc(rs.getString("max_utilhdxperc"));
            vo.setSupperid(rs.getString("supperid"));
            vo.setFatherid(rs.getString("fatherid"));
		}
		catch(Exception e)
		{
			SysLogger.error("ManageXmlDao.loadFromRS()",e);
		}
	    return vo;
	}
	
	public List findByTopoTypeAndBid(int topotype, String bid){
	       
		List list = new ArrayList();
        try {
		    rs = conn.executeQuery("select * from topo_manage_xml where topo_type=" + topotype + " and bid like '%," + bid + ",%'"); 
		    while(rs.next())
		    	list.add(loadFromRS(rs));
	    } catch(Exception ex) {
		    SysLogger.error("ManageXmlDao.findByXml()",ex);
	    } finally {
	    	conn.close();
	    } 
	    return list;
	}
}   