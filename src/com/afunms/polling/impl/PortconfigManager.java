package com.afunms.polling.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.Order;
import net.sf.hibernate.*; 

//import com.dhcc.webnms.host.om.Monitoriplist;
import com.afunms.polling.api.I_Portconfig;
import com.afunms.polling.om.Portconfig;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.A_BaseMap;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;

public class PortconfigManager extends BaseDao implements I_Portconfig {

	public PortconfigManager() {
		super("portconfig");
	}

	public Portconfig loadPortconfig(Integer id) throws Exception {
		// TODO Auto-generated method stub
		Portconfig vo = null;
		try{
			vo = (Portconfig)findByID(id+"");
		}catch(Exception e){
			e.printStackTrace();
		}
		return vo;		
	}
	
	public boolean createPortconfig(Portconfig portconfig)
		throws Exception {
		try{
			insert(portconfig);
		}
		catch(Exception  e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean createPortconfig(Vector configV)
	throws Exception {
		try{
				//Session session=this.beginTransaction();
			if (configV!= null && configV.size()>0){
				for(int i=0;i<configV.size();i++){
					Portconfig portconfig = (Portconfig)configV.get(i);
					insert(portconfig);
				}
			}
		}
		catch(Exception  e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	public boolean updatePortconfig(Portconfig portconfig)
		throws Exception {
		List list = new ArrayList();
		try{
			update(portconfig);
		}
		catch(Exception  e){
			e.printStackTrace();
			return false;

		}
		return true;
	}
	
	public boolean deletePortconfig(String[] id) throws Exception {
		// TODO Auto-generated method stub
		Session session=null;
		try{
			for(int i=0;i<id.length;i++){
				Portconfig portconfig=(Portconfig)session.load(Portconfig.class,new Integer(id[i]));
				try{
					delete(portconfig.getId()+"");
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
					
			return true;
		}
		catch(HibernateException  e){
			e.printStackTrace();
			return false;
		}
	}

	public List getByip(String ip) {
			List list = new ArrayList();
			Session session=null;
			try{
				String sql = "select * from portconfig where ipaddress='"+ip+"' order by portindex";
				list=findByCriteria(sql);	
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return list;
	}
	/*
	 * 根据IP和是否要显示于日报表的标志位查询
	 * 
	 */
	public List getByIpAndReportflag(String ip,Integer reportflag) {
		List list = new ArrayList();
		Session session=null;
		try{
			String sql = "select * from portconfig where ipaddress='"+ip+"' and reportflag = "+reportflag+" order by portindex";
			list=findByCriteria(sql);
			//this.endTransaction(true);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	
	public List getIps() {
		List list = new ArrayList();
		Session session=null;
		try{
			String sql = "select distinct h.ipaddress from Portconfig h order by h.ipaddress";
			list=findOneColByCriteria(sql);
		}
		catch(Exception e){
			e.printStackTrace();
			//this.error("getIps error:"+e);
		}
	// TODO Auto-generated method stub
		return list;
	}
	public Hashtable getIpsHash(String ipaddress) {
		List list = new ArrayList();
		Hashtable hash = new Hashtable();
		try{
			String sql = "select * from portconfig where ipaddress='"+ipaddress+"' order by portindex";
			list=findByCriteria(sql);
			if (list != null && list.size()>0){
				for(int i=0;i<list.size();i++){
					Portconfig portconfig = (Portconfig)list.get(i);
					if (portconfig.getLinkuse()!= null && portconfig.getLinkuse().trim().length()>0){						
						hash.put(portconfig.getIpaddress()+":"+portconfig.getPortindex(),portconfig.getLinkuse());
					}else{
						hash.put(portconfig.getIpaddress()+":"+portconfig.getPortindex(),"");
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return hash;
	}
	
	public Portconfig getByipandindex(String ip,String portindex){
			List list=new ArrayList();
			Portconfig portconfig = null;
			try{
				String sql = "select * from portconfig where ipaddress='"+ip+"' and portindex = "+portindex;
				list=findByCriteria(sql);
			
				if (list != null && list.size()>0){				
					portconfig=(Portconfig)list.get(0);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		return portconfig;
	}

	public List getPortconfig() throws Exception {
		List list=new ArrayList();
		try{
			String sql = "select * from portconfig order by ipaddress,portindex";
			list=findByCriteria(sql);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public List getBySearch(String searchfield,String searchkeyword) throws Exception{
		List list=new ArrayList();
				return list;
	}	

	public List Search(String searchfield,String searchkeyword) throws Exception{
		List list=new ArrayList();
		return list;
	}

	/*
	 * 
	 * 从内存和数据库表里获取每个IP的端口信息，存入端口配置表里
	 */
	public void fromLastToPortconfig()
	throws Exception {
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
		
    	HostNodeDao nodeDao = new HostNodeDao();    	
    	List nodeList = nodeDao.loadMonitorNet();
	
		if (nodeList != null && nodeList.size()>0){
			for(int i=0;i<nodeList.size();i++){
				
				HostNode monitorNode = (HostNode)nodeList.get(i);				
				Hashtable ipdata = (Hashtable)sharedata.get(monitorNode.getIpAddress());
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
		
		//String sql = "select * from Portconfig h order by h.ipaddress,h.portindex";
		//Query query1=session.createQuery("from Portconfig portconfig order by portconfig.ipaddress,portconfig.portindex");
		list1=getPortconfig();		
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
					configV.add(portconfig);					
				}
			}
		}
		if (configV!=null && configV.size()>0){
			createPortconfig(configV);
		}
		}
		catch(HibernateException e){
			e.printStackTrace();
		}
	// TODO Auto-generated method stub
	}
	
	   public BaseVo loadFromRS(ResultSet rs)
	   {
		   Portconfig vo = new Portconfig();
	       try
	       {	
			   vo.setId(rs.getInt("id"));
			   vo.setIpaddress(rs.getString("ipaddress"));
			   vo.setLinkuse(rs.getString("linkuse"));
			   vo.setName(rs.getString("name"));
			   vo.setPortindex(rs.getInt("portindex"));
			   vo.setSms(rs.getInt("sms"));// 0：不发送短信 1：发送短信，默认的情况是不发送短信
			   vo.setBak(rs.getString("bak"));
			   vo.setReportflag(rs.getInt("reportflag"));// 0：不存在于报表 1：存在于报表，默认的情况是不存在于报表
	       }
	       catch(Exception e)
	       {
	 	       SysLogger.error("HostNodeDao.loadFromRS()",e); 
	       }	   
	       return vo;
	   }
	   public String loadOneColFromRS(ResultSet rs)
	   {
		   String ipaddress = "";
	       try
	       {	
	    	   ipaddress = rs.getString("ipaddress");
	       }
	       catch(Exception e)
	       {
	 	       SysLogger.error("HostNodeDao.loadFromRS()",e); 
	       }	   
	       return ipaddress;
	   }
	   
	   public boolean update(Portconfig vo)
	   {   
		   //int id = getNextID();	   
		   StringBuffer sql = new StringBuffer(200);
		   sql.append("update portconfig set ");
		   sql.append(" id = "+vo.getId());
		   sql.append(", ipaddress='"+vo.getIpaddress()+"'");
		   sql.append(", linkuse='"+vo.getLinkuse()+"'");
		   sql.append(", name='"+vo.getName()+"'");
		   sql.append(", portindex="+vo.getPortindex());
		   sql.append(", reportflag="+vo.getReportflag());
		   sql.append(", sms="+vo.getSms());
		   sql.append(", bak='"+vo.getBak()+"'");
		   sql.append(" where id = "+vo.getId());		   
		   return saveOrUpdate(sql.toString()); 
	   }
	   
	   public boolean insert(Portconfig vo)
	   {   
		   //int id = getNextID();	   
		   StringBuffer sql = new StringBuffer(200);
		   sql.append("insert into portconfig(ipaddress,linkuse,name,portindex,reportflag,sms,bak) values ( ");
		   sql.append("'"+vo.getIpaddress()+"'");
		   sql.append(",'"+vo.getLinkuse()+"'");
		   sql.append(",'"+vo.getName()+"'");
		   sql.append(","+vo.getPortindex());
		   sql.append(","+vo.getReportflag());
		   sql.append(","+vo.getSms());
		   sql.append(",'"+vo.getBak()+"')");		   
		   return saveOrUpdate(sql.toString()); 
	   }
	   
	   public boolean delete(String id)
	   {
		   boolean result = false;
		   try
		   {
			   conn.executeUpdate("delete from portconfig where id=" + id);
			   result = true;
		   }
		   catch(Exception e)
		   {
			   SysLogger.error("PortconfigDao.delete()",e); 
		   }
		   finally
		   {
			   //conn.close();   
		   }
		   return result;
	   }
	   
	   public List findOneColByCriteria(String sql)
	   {
		   List list = new ArrayList();
		   try 
		   {
			   rs = conn.executeQuery(sql);
			   while(rs.next())
				  list.add(loadOneColFromRS(rs));				
		   } 
		   catch(Exception e) 
		   {
	           list = null;
			   SysLogger.error("BaseDao.findByCondition()",e);
		   }
		   finally
		   {
			   conn.close();
		   }
		   return list;	
	   }

 }