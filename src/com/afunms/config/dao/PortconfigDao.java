/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.dao;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.hibernate.Session;


import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.Portconfig;
import com.afunms.common.base.BaseVo;
import com.afunms.system.model.Department;
import com.afunms.common.util.ShareData;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.polling.om.Interfacecollectdata;

public class PortconfigDao extends BaseDao implements DaoInterface
{
	public PortconfigDao()
  	{
	  	super("system_portconfig");	  
  	}
  
  	//-------------load all --------------
  	public List loadAll()
  	{
  		List list = new ArrayList(5);
  		try
  		{
  			rs = conn.executeQuery("select * from system_portconfig order by ipaddress,portindex");
  			while(rs.next())
  				list.add(loadFromRS(rs)); 
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("PortconfigDao:loadAll()",e);
  			list = null;
  		}
  		finally
  		{
  			conn.close();
  		}
  		return list;
  	}
  	
    public Portconfig loadPortconfig(int id)
    {
 	   
 	   List retList = new ArrayList();
 	   List portconfigList = findByCriteria("select * from system_portconfig where id="+id); 
 	   if(portconfigList != null && portconfigList.size()>0){
 		  Portconfig portconfig = (Portconfig)portconfigList.get(0);
 			   return portconfig;
 		   
 	   }
 	   return null;
    }

  	public List loadByIpaddress(String ip)
  	{
  		List list = new ArrayList(5);
  		try
  		{
  			rs = conn.executeQuery("select * from system_portconfig where ipaddress='"+ip+"' order by portindex");
  			while(rs.next())
  				list.add(loadFromRS(rs)); 
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("PortconfigDao:loadAll()",e);
  			list = null;
  		}
  		finally
  		{
  			conn.close();
  		}
  		return list;
  	}
  	
  	public boolean deleteByIpaddress(String ip)
  	{
  		boolean flag = true;
  		try
  		{
  			conn.executeUpdate("delete from system_portconfig where ipaddress='"+ip+"' ");
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("PortconfigDao:deleteByIpaddress()",e);
  			flag = false;
  		}
  		finally
  		{
  			conn.close();
  		}
  		return flag;
  	}
  	
  	public Portconfig getByipandindex(String ip,String portindex)
  	{
  		List list = new ArrayList(5);
  		try
  		{
  			rs = conn.executeQuery("select * from system_portconfig where ipaddress='"+ip+"' and portindex ="+portindex+" order by id");
  			if(rs.next())
  				return (Portconfig)loadFromRS(rs); 
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("PortconfigDao:getByipandindex()",e);
  			list = null;
  		}
  		finally
  		{
  			conn.close();
  		}
  		return null;
  	}
  	public Portconfig getByip(String ip)
  	{
  		List list = new ArrayList(5);
  		try
  		{
  			rs = conn.executeQuery("select * from system_portconfig where ipaddress='"+ip+"' order by id");
  			if(rs.next())
  				return (Portconfig)loadFromRS(rs); 
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("PortconfigDao:getByip()",e);
  			list = null;
  		}
  		finally
  		{
  			conn.close();
  		}
  		return null;
  	}
  	
  	public Portconfig getPanelByipandindex(String ip,String portindex)
  	{
  		List list = new ArrayList(5);
  		try
  		{
  			rs = conn.executeQuery("select * from system_portconfig where ipaddress='"+ip+"' and portindex ="+portindex+" order by id");
  			if(rs != null && rs.next())
  				return (Portconfig)loadFromRS(rs); 
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("PortconfigDao:getByipandindex()",e);
  			list = null;
  		}
  		finally
  		{
  			//conn.close();
  		}
  		return null;
  	}
  	
  	public void empty()
  	{
  		try
  		{
  			conn.executeUpdate("delete from system_portconfig ");
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("PortconfigDao:empty()",e);
  		}
  		finally
  		{
  			conn.close();
  		}
  	}
  	
	public List getIps() {
		List list = new ArrayList();
		try{
			String sql="select distinct h.ipaddress from system_portconfig h order by h.ipaddress";
			rs = conn.executeQuery(sql);
	        while(rs.next()){
	        	list.add(rs.getString("ipaddress"));
	        }
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	/*
	 * 
	 * 从内存和数据库表里获取每个IP的端口信息，存入端口配置表里
	 */
	public void fromLastToPortconfig() {
		List list=new ArrayList();
		List list1=new ArrayList();
		List shareList = new ArrayList();
		Hashtable porthash= new Hashtable();
		Session session=null;
		Portconfig portconfig = null;
		Vector configV = new Vector();
		try{
			//从内存中得到所有端口的采集信息
			Hashtable sharedata = ShareData.getSharedata();
			//从数据库得到监视IP列表
			HostNodeDao hostnodedao = new HostNodeDao();
			shareList = hostnodedao.loadMonitorNet();	
			if (shareList != null && shareList.size()>0){
				for(int i=0;i<shareList.size();i++){
					HostNode monitornode = (HostNode)shareList.get(i);				
					Hashtable ipdata = (Hashtable)sharedata.get(monitornode.getIpAddress());
					if (ipdata == null )continue;
					Vector vector = (Vector)ipdata.get("interface");
					if (vector !=null && vector.size()>0){
						for(int k=0;k<vector.size();k++){
							Interfacecollectdata inter = (Interfacecollectdata)vector.get(k);
							if (inter.getEntity().equalsIgnoreCase("ifname")){
								list.add(inter);
							}
						}
					}				
				}
			}
		//从端口配置表里获取列表
		//Query query1=session.createQuery("from Portconfig portconfig order by portconfig.ipaddress,portconfig.portindex");
		list1=loadAll();		
		if (list1 != null && list1.size()>0){
			for(int i=0;i<list1.size();i++){
				portconfig = (Portconfig)list1.get(i);				
				porthash.put(portconfig.getIpaddress()+":"+portconfig.getPortindex(),portconfig);
			}
		}
		//判断采集到的端口信息是否已经在端口配置表里已经存在，若不存在则加入
		if (list != null && list.size()>0){
			for(int i=0;i<list.size();i++){
				Interfacecollectdata hostlastcollectdata = (Interfacecollectdata)list.get(i);
				if (!porthash.containsKey(hostlastcollectdata.getIpaddress()+":"+hostlastcollectdata.getSubentity())){
					portconfig = new Portconfig();
					portconfig.setBak("");
					portconfig.setIpaddress(hostlastcollectdata.getIpaddress());
					portconfig.setLinkuse("");
					portconfig.setName(hostlastcollectdata.getThevalue());
					portconfig.setPortindex(new Integer(hostlastcollectdata.getSubentity()));
					portconfig.setSms(new Integer(0));// 0：不发送短信 1：发送短信，默认的情况是不发送短信
					portconfig.setReportflag(new Integer(0));// 0：不存在于报表 1：存在于报表，默认的情况是不存在于报表
					portconfig.setInportalarm("2000");//默认入口流速阀值
					portconfig.setOutportalarm("2000");//默认出口流速阀值
					portconfig.setSpeed("10000");
					portconfig.setAlarmlevel("1");
					portconfig.setFlag("1");
					PortconfigDao dao = new PortconfigDao();
					try{
						dao.save(portconfig);
					}catch(Exception ex){
						ex.printStackTrace();
					}finally{
						dao.close();
					}
				}
			}
		}
		RefreshPortconfigs();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	// TODO Auto-generated method stub
	}
	
	public Hashtable getIpsHash(String ipaddress) {
		List list = new ArrayList();
		Hashtable hash = new Hashtable();
		try{
			String sql="select * from system_portconfig h where h.ipaddress='"+ipaddress+"' order by h.portindex";
			rs = conn.executeQuery(sql);
	        while(rs.next()){
	        	Portconfig portconfig = (Portconfig)loadFromRS(rs);
				if (portconfig.getLinkuse()!= null && portconfig.getLinkuse().trim().length()>0){						
					hash.put(portconfig.getIpaddress()+":"+portconfig.getPortindex(),portconfig.getLinkuse());
				}else{
					hash.put(portconfig.getIpaddress()+":"+portconfig.getPortindex(),"");
				}
	        }
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return hash;
	}
	public Vector getSmsByIp(String ipaddress) {
		Vector vector = new Vector();
		try{
			String sql="select h.portindex from system_portconfig h where h.ipaddress='"+ipaddress+"' and h.sms='1' order by h.portindex";
			rs = conn.executeQuery(sql);
			while(rs.next()){
			//	Portconfig portconfig = (Portconfig)loadFromRS(rs);
			//	if (portconfig!= null){						
					vector.add(rs.getString(1));
			//	}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			if (rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (conn!=null) {
				conn.close();
			}
		}
		return vector;
	}
	
	/*
	 * 根据IP和是否要显示于日报表的标志位查询
	 * 
	 */
	public List getByIpAndReportflag(String ip,Integer reportflag) {
		List list = new ArrayList();
		try{
			String sql="select * from system_portconfig h where h.ipaddress = '"+ip+"' and h.reportflag="+reportflag+" order by h.portindex";
			rs = conn.executeQuery(sql);
	        while(rs.next())
	        	list.add(loadFromRS(rs));
	        
		}
		catch(Exception e){
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		//SysLogger.info("size==="+list.size());
		return list;
	}	
	
	//查询要监控的端口   by Quzhi
	public List getBySms(String ipAddress){
		List list = new ArrayList();
		try{
			String sql="select * from system_portconfig h where h.sms = 1 and h.ipaddress = '"+ipAddress+"'";
			rs = conn.executeQuery(sql);
	        while(rs.next())
	        	list.add(loadFromRS(rs));
	        
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally
  		{
  			conn.close();
  		}
		// TODO Auto-generated method stub
		return list;
	}
	
	//查询所有要监控的端口   by hukelei
	public List getAllBySms(){
		List list = new ArrayList();
		try{
			String sql="select * from system_portconfig h where h.sms = 1 ";
			rs = conn.executeQuery(sql);
	        while(rs.next())
	        	list.add(loadFromRS(rs));
	        
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally
  		{
  			conn.close();
  		}
		// TODO Auto-generated method stub
		return list;
	}
	
	public List getAllBySmsAndId(String id){
		List list = new ArrayList();
		try{
			String sql="select t1.* from system_portconfig t1,topo_host_node t2 " +
					"where t1.IPADDRESS=t2.ip_address and t1.sms = 1 and t2.id='"+id+"'";
			rs = conn.executeQuery(sql);
	        while(rs.next())
	        	list.add(loadFromRS(rs));
	        
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally
  		{
  			conn.close();
  		}
		return list;
	}
	//查询指定IP所有要监控的端口   by zhangys
	public List getInfsBySms(String ipaddress){
		List list = new ArrayList();
		try{
			String sql="select * from system_portconfig h where h.ipaddress = '" + ipaddress + "' and h.sms = 1";
			rs = conn.executeQuery(sql);
	        while(rs.next())
	        	list.add(loadFromRS(rs));
	        
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally
  		{
  			conn.close();
  		}
		// TODO Auto-generated method stub
		return list;
	}
	
	public List getInportalramByIndex(String ipAddress,int portindex){
		List list = new ArrayList();
		try{
			String sql="select * from system_portconfig h where h.portindex ="+portindex+" and h.ipaddress = '"+ipAddress+"'";
			rs = conn.executeQuery(sql);
			//System.out.println("---------rs.next()---------------------------"+rs.next());
	        while(rs.next())     	
	        	list.add(loadFromRS(rs));
	        	        
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally
  		{
  			conn.close();
  		}
		// TODO Auto-generated method stub
		return list;
	}
	
	
	
	
	
	public boolean save(BaseVo baseVo)
	{
		Portconfig vo = (Portconfig)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_portconfig(ipaddress,name,portindex,linkuse,sms,bak,reportflag,inportalarm,outportalarm,speed,alarmlevel,flag)values(");
		sql.append("'");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getName());	
		sql.append("',");
		sql.append(vo.getPortindex());
		sql.append(",'");
		sql.append(vo.getLinkuse());
		sql.append("',");
		sql.append(vo.getSms());
		sql.append(",'");
		sql.append(vo.getBak());
		sql.append("',");
		sql.append(vo.getReportflag());
		sql.append(",'");
		sql.append(vo.getInportalarm());
		sql.append("','");
		sql.append(vo.getOutportalarm());
		sql.append("','");
		sql.append(vo.getSpeed());
		sql.append("','");
		sql.append(vo.getAlarmlevel());
		sql.append("','");
		sql.append(vo.getFlag());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean save(List portlist)
	{
		if(portlist != null && portlist.size()>0){
			Portconfig vo = null;
			StringBuffer sql = null;
			for(int i=0;i<portlist.size();i++){
				vo = (Portconfig)portlist.get(i);
				sql = new StringBuffer(100);
				sql.append("insert into system_portconfig(ipaddress,name,portindex,linkuse,sms,bak,reportflag,inportalarm,outportalarm,speed,alarmlevel,flag)values(");
				sql.append("'");
				sql.append(vo.getIpaddress());
				sql.append("','");
				sql.append(vo.getName());	
				sql.append("',");
				sql.append(vo.getPortindex());
				sql.append(",'");
				sql.append(vo.getLinkuse());
				sql.append("',");
				sql.append(vo.getSms());
				sql.append(",'");
				sql.append(vo.getBak());
				sql.append("',");
				sql.append(vo.getReportflag());
				sql.append(",'");
				sql.append(vo.getInportalarm());
				sql.append("','");
				sql.append(vo.getOutportalarm());
				sql.append("','");
				sql.append(vo.getSpeed());
				sql.append("','");
				sql.append(vo.getAlarmlevel());
				sql.append("','");
				sql.append(vo.getFlag());
				sql.append("')");
				SysLogger.info(sql.toString());
				conn.addBatch(sql.toString());
			}
			try{
				conn.executeBatch();
			}catch(Exception e){
				return false;
			}finally{
				conn.close();
			}
		}
		
		return true;
	}
	
  //---------------update a portconfig----------------
  public boolean update(BaseVo baseVo)
  {
	  Portconfig vo = (Portconfig)baseVo;
     boolean result = false;
     StringBuffer sql = new StringBuffer();
     sql.append("update system_portconfig set ipaddress='");
		sql.append(vo.getIpaddress());
		sql.append("',name='");
		sql.append(vo.getName());	
		sql.append("',portindex=");
		sql.append(vo.getPortindex());
		sql.append(",linkuse='");
		if(vo.getLinkuse() != null){
			sql.append(vo.getLinkuse());
		}else{
			sql.append("");
		}
		sql.append("',sms=");
		sql.append(vo.getSms());
		sql.append(",bak='");
		sql.append(vo.getBak());
		sql.append("',reportflag=");
		sql.append(vo.getReportflag());
		sql.append(",inportalarm='");
		sql.append(vo.getInportalarm());
		sql.append("',outportalarm='");
		sql.append(vo.getOutportalarm());
		sql.append("',speed='");
		sql.append(vo.getSpeed());
		sql.append("',alarmlevel='");
		sql.append(vo.getAlarmlevel());
		sql.append("',flag='");
		sql.append(vo.getFlag());
     sql.append("' where id=");
     sql.append(vo.getId());
     
     try
     {
         conn.executeUpdate(sql.toString());
         result = true;
     }
     catch(Exception e)
     {
    	 result = false;
         SysLogger.error("PortconfigDao:update()",e);
     }
     finally
     {
    	 conn.close();
     }  
     RefreshPortconfigs();
     return result;
  }
  
	public boolean delete(String[] id)
	{
		boolean result = false;
	    try
	    {	    
	        for(int i=0;i<id.length;i++)
	        {
	            conn.addBatch("delete from system_portconfig where id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("PortconfigDao.delete()",e);	        
	    }
	    finally
	    {
	         conn.close();
	    }
	    RefreshPortconfigs();
	    return result;
	}
  
  public BaseVo findByID(String id)
  {
     BaseVo vo = null;
     try
     {
        rs = conn.executeQuery("select * from system_portconfig where id=" + id );
        if(rs.next())
           vo = loadFromRS(rs);
     }
     catch(Exception e)
     {
         SysLogger.error("PortconfigDao.findByID()",e);
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
	   Portconfig vo = new Portconfig();
      try
      {
          vo.setId(rs.getInt("id"));
          vo.setBak(rs.getString("bak"));
          vo.setIpaddress(rs.getString("ipaddress"));
          if(rs.getString("linkuse") == null || "null".equalsIgnoreCase(rs.getString("linkuse"))){
        	  vo.setLinkuse("");
          }else{
        	  vo.setLinkuse(rs.getString("linkuse"));
          }
//          vo.setLinkuse(rs.getString("linkuse"));
          vo.setName(rs.getString("name"));
          vo.setPortindex(rs.getInt("portindex"));
          vo.setReportflag(rs.getInt("reportflag"));
          vo.setSms(rs.getInt("sms"));
          vo.setInportalarm(rs.getString("inportalarm"));
          vo.setOutportalarm(rs.getString("outportalarm"));
          vo.setSpeed(rs.getString("speed"));
          vo.setAlarmlevel(rs.getString("alarmlevel"));
          vo.setFlag(rs.getString("flag"));
      }
      catch(Exception e)
      {
          //SysLogger.error("PortconfigDao.loadFromRS()",e);
    	  e.printStackTrace();
          vo = null;
      }
      return vo;
   }  
   
	public Boolean updatePortConfigOfAlarmlevelByID(String id,String alarmlevel){
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update system_portconfig set alarmlevel = '");
		sqlBuffer.append(alarmlevel);
		sqlBuffer.append("' where id = '");
		sqlBuffer.append(id);
		sqlBuffer.append("'");
		DBManager dbmanager = new DBManager();
		try{
			dbmanager.executeUpdate(sqlBuffer.toString());
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		RefreshPortconfigs();
		return true;
	}
	
	public void RefreshPortconfigs(){
		PortconfigDao portconfigdao = new PortconfigDao();
		List portconfglist = new ArrayList();
		try{
			portconfglist = portconfigdao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			portconfigdao.close();
		}
		if(portconfglist != null && portconfglist.size()>0){
			for(int i=0;i<portconfglist.size();i++){
				Portconfig portconfig = (Portconfig)portconfglist.get(i);
				if(ShareData.getAllportconfigs() != null){
					ShareData.getAllportconfigs().put(portconfig.getIpaddress()+":"+portconfig.getPortindex(), portconfig);
				}else{
					Hashtable hash = new Hashtable();
					hash.put(portconfig.getIpaddress()+":"+portconfig.getPortindex(), portconfig);
					ShareData.setAllportconfigs(hash);
				}
			}
		}
	}
	public void updateportflag(String ip,String[] array){
		   try{
			   if(array!=null&&array.length>0){
				   StringBuffer port=new StringBuffer();
				   for(int i=0;i<array.length;i++){
					   port.append(array[i]);
					   port.append(",");
				   }
				   conn.executeUpdate("update system_portconfig set flag='1' where ipaddress='"+ip+"'");
				   conn.executeUpdate("update system_portconfig set flag='0' where ipaddress='"+ip+"' and portindex not in ("+port.substring(0, port.length()-1)+")");
			   }else{
				   conn.executeUpdate("update system_portconfig set flag='0' where ipaddress='"+ip+"'");
			   }
			   }catch(Exception e){
			   SysLogger.error("portconfigDao.updateportflag",e);
		   }
	   }
	public void updateportflag(String ip,String[] array,String flag){
		   try{
			   if(array!=null&&array.length>0){
				   StringBuffer port=new StringBuffer();
				   for(int i=0;i<array.length;i++){
					   port.append(array[i]);
					   port.append(",");
				   }
				   conn.executeUpdate("update system_portconfig set flag='0' where ipaddress='"+ip+"' and portindex in ("+port.substring(0, port.length()-1)+")");
			   }else{
				   conn.executeUpdate("update system_portconfig set flag='0' where ipaddress='"+ip+"'");
			   }
			   }catch(Exception e){
			   SysLogger.error("portconfigDao.updateportflag",e);
		   }
	   }
}
