/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpMacBase;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.IpMacBaseDao;
import com.afunms.topology.dao.IpMacDao;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetDatatempFdbRtosql;

/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class IpMacManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		IpMacDao dao = new IpMacDao();
		setTarget("/config/ipmac/list.jsp");
        return list(dao);
	}
	
	private String deleteall()
	{
		IpMacDao dao = new IpMacDao();
		dao.deleteall();
		dao.close();
		dao = new IpMacDao();
		setTarget("/config/ipmac/list.jsp");
        return list(dao);
	}
	
	private String monitornodelist()
	{
		IpMacDao dao = new IpMacDao();	 
		setTarget("/config/ipmac/ipmaclist.jsp");
        return list(dao," where managed=1");
	}

    
	private String readyEdit()
	{
	    DaoInterface dao = new IpMacDao();
	    setTarget("/config/ipmac/edit.jsp");
        return readyEdit(dao);
	}
	
	private String update()
	{  
		IpMac vo = new IpMac();
		int id = getParaIntValue("id");
		IpMacDao dao = new IpMacDao(); 	
		vo = dao.loadIpMac(id);
		String ifband = getParaValue("ifband");
		String ifsms = getParaValue("ifsms");
		int flag = 0;
		if(ifband != null && ifband.trim().length()>0){
			vo.setIfband(ifband);
			flag = 1;
		}
		if(ifsms != null && ifsms.trim().length()>0){
			vo.setIfsms(ifsms);
			flag = 1;
		}
		if(flag == 1){
		dao = new IpMacDao(); 
		dao.update(vo);
		dao.close();
		}
		dao.close();
		return "/ipmac.do?action=list";
	    //setTarget("/action=list");
        //return update(dao,vo);
    }
  

    private String updateselect()
    {
	   String key = getParaValue("key");
	   String value = getParaValue("value");	 
	   IpMacDao dao = new IpMacDao();
	   request.setAttribute("key", key);
	   request.setAttribute("value", value);
	   IpMac vo = new IpMac();
		int id = getParaIntValue("id"); 	
		vo = dao.loadIpMac(id);
		String ifband = getParaValue("ifband");
		String ifsms = getParaValue("ifsms");
		int flag = 0;
		if(ifband != null && ifband.trim().length()>0){
			vo.setIfband(ifband);
			flag = 1;
		}
		if(ifsms != null && ifsms.trim().length()>0){
			vo.setIfsms(ifsms);
			flag = 1;
		}
		if(flag == 1){
			dao = new IpMacDao(); 
			dao.update(vo);
			dao.close();
		}
		dao = new IpMacDao();
	   setTarget("/config/ipmac/findlist.jsp");
       return list(dao," where "+key+" = '"+value+"'");
   }
    
    private String setipmacbase()
    {	 
	   IpMacBaseDao dao = new IpMacBaseDao();
	   
	   IpMacBase vo = new IpMacBase();
	   int id = getParaIntValue("id"); 
	   int flag = getParaIntValue("doflag");
	   Host host = (Host)PollingEngine.getInstance().getNodeByID(id);
		//vo = dao.loadIpMac(id);
	    String mac = getParaValue("mac");
		String ifindex = getParaValue("ifindex");
		String macip = getParaValue("macip");
		List existlist = dao.loadIpMacBaseByRIPMAC(host.getIpAddress(), mac);
		if(existlist != null && existlist.size()>0){
			vo = (IpMacBase)existlist.get(0);			
			dao = new IpMacBaseDao();
			vo.setIfband(flag);//0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			try{
				if(flag == -1){
					String[] ids = new String[1];
					ids[0] = vo.getId()+"";
					dao.delete(ids);
				}else
					dao.update(vo);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
		}else{
			vo.setRelateipaddr(host.getIpAddress());
			vo.setIfindex(ifindex);
			vo.setIpaddress(macip);
			vo.setMac(mac);
			//vo.setIfband(SystemConstant.MACBAND_BASE);//设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			vo.setIfband(flag);//设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			vo.setIfsms("0");//不发信息
			vo.setIftel("0");//不打电话
			vo.setIfemail("0");//不发邮件
			vo.setBak("");
			vo.setCollecttime(Calendar.getInstance());
			try{
				dao = new IpMacBaseDao();
				dao.save(vo);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
		}
		dao.close();
		return "/monitor.do?action=netfdb&id="+id+"&ipaddress="+host.getIpAddress();
   }
    
    private String setmacbase()
    {	 
	   IpMacBaseDao dao = new IpMacBaseDao();
	   
	   IpMacBase vo = new IpMacBase();
	   String relateip = getParaValue("relateip"); 
	   //int flag = getParaIntValue("flag");
	   Host host = (Host)PollingEngine.getInstance().getNodeByIP(relateip);
		//vo = dao.loadIpMac(id);
	    String mac = getParaValue("mac");
		String ifindex = getParaValue("ifindex");
		String macip = getParaValue("macip");
		String flag = getParaValue("flag");
		List existlist = dao.loadIpMacBaseByRIPMAC(host.getIpAddress(), mac);
		if(existlist != null && existlist.size()>0){
		}else{
			vo.setRelateipaddr(host.getIpAddress());
			vo.setIfindex(ifindex);
			vo.setIpaddress(macip);
			vo.setMac(mac);
			//vo.setIfband(SystemConstant.MACBAND_BASE);//设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			vo.setIfband(0);//0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			vo.setIfsms("0");//不发信息
			vo.setIftel("0");//不打电话
			vo.setIfemail("0");//不发邮件
			vo.setBak("");
			vo.setCollecttime(Calendar.getInstance());
			try{
				dao = new IpMacBaseDao();
				dao.save(vo);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
		}
		dao.close();
		String type = getParaValue("type");
		if("firewall".equals(type)){
			return "/monitor.do?action=firewallarp&id="+host.getId()+"&ipaddress="+host.getIpAddress();
		}
		if(flag != null && flag.trim().length()>0 && flag.equalsIgnoreCase("1")){
			return "/monitor.do?action=netarp&id="+host.getId()+"&ipaddress="+host.getIpAddress();
		}else{
			return "/ipmac.do?action=list&jp=1";
		}
		
   }
    
    private String setmultimacbase()
    {	 
    	int nodeid = getParaIntValue("id"); 
    	String flag = getParaValue("flag");
    	String type = getParaValue("type");
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeid);
	   
   		IpMacBaseDao dao = new IpMacBaseDao();
   		Hashtable allmacbase = new Hashtable();
   		try{
   			//获取所有MAC基线信息
   			allmacbase = dao.loadMacBaseByRIPAndIpAndMac();
   		}catch(Exception e){
   			e.printStackTrace();
   		}finally{
   			dao.close();
   		}
   		if(allmacbase == null)allmacbase = new Hashtable();
    	
    	
	   String[] ids = getParaArrayValue("checkbox");
	   	if(ids != null && ids.length > 0){
	   		//进行修改
	   		DBManager dbmanager = new DBManager();
	   		try{
	   		for(int i=0;i<ids.length;i++){
	   				try{
	   					String id = ids[i];
		   		        if(id != null && id.length()>0){
		   		        	String[] paras = id.split("&");
		   		        	if(paras != null && paras.length==4){
		   		        		String relateipaddr = paras[0];
		   		        		String ifindex = paras[1];
		   		        		String macipaddress = paras[2];
		   		        		String mac = paras[3];
		   		        		
		   		        		if(!allmacbase.containsKey(relateipaddr+":"+macipaddress+":"+mac)){
		   		        			IpMacBase vo = new IpMacBase();
		   		        			vo.setRelateipaddr(host.getIpAddress());
		   		        			vo.setIfindex(ifindex);
		   		        			vo.setIpaddress(macipaddress);
		   		        			vo.setMac(mac);
		   		        			//vo.setIfband(SystemConstant.MACBAND_BASE);//设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
		   		        			vo.setIfband(1);//0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
		   		        			vo.setIfsms("0");//不发信息
		   		        			vo.setIftel("0");//不打电话
		   		        			vo.setIfemail("0");//不发邮件
		   		        			vo.setBak("");
		   		        			vo.setCollecttime(Calendar.getInstance());
		   		        			try{
										dbmanager.executeUpdate(ipmacbaseInsertSQL(vo));
									}catch(Exception ex){
										ex.printStackTrace();
									}
		   		        		}
		   		        	}
		   		        }
	   				}catch(Exception e){
	   			e.printStackTrace();
	   			}
	   		}
	   	}catch(Exception e){
	   		e.printStackTrace();
	   	}finally{
	   		dbmanager.close();
	   	}
	   	}
	   	if("firewall".equals(type)){
	   		return "/monitor.do?action=firewallarp&id="+host.getId()+"&ipaddress="+host.getIpAddress(); 
	   	}
   		if(flag != null && flag.trim().length()>0 && flag.equalsIgnoreCase("1")){
   			return "/monitor.do?action=netarp&id="+host.getId()+"&ipaddress="+host.getIpAddress();
   		}else{
   			return "/ipmac.do?action=list&jp=1";
   		}
    }
    
    private String setlistbaseline()
    {	 
    	String flag = getParaValue("flag");
	   
	   String[] ids = getParaArrayValue("checkbox");
	   	if(ids != null && ids.length > 0){
	   		//进行修改
	   		DBManager dbmanager = new DBManager();
	   		
	   		IpMacBaseDao dao = new IpMacBaseDao();
	   		Hashtable allmacbase = new Hashtable();
       		try{
       			//获取所有MAC基线信息
       			allmacbase = dao.loadMacBaseByRIPAndIpAndMac();
       		}catch(Exception e){
       			e.printStackTrace();
       		}finally{
       			dao.close();
       		}
       		if(allmacbase == null)allmacbase = new Hashtable();
       		
	   		
	   		try{
	   		for(int i=0;i<ids.length;i++){
	   				try{
	   					String id = ids[i];
		   		        if(id != null && id.length()>0){
		   		        	//装载当前MAC信息
		   		        	IpMacDao macdao = new IpMacDao();
		   		        	IpMac ipmac = null;
		   		        	try{
		   		        		ipmac = macdao.loadIpMac(Integer.parseInt(id));
		   		        	}catch(Exception e){
		   		        		e.printStackTrace();
		   		        	}finally{
		   		        		macdao.close();
		   		        	}
	   		        		if(ipmac != null && ipmac.getIpaddress() != null){
	   		        			if(!allmacbase.containsKey(ipmac.getRelateipaddr()+":"+ipmac.getIpaddress()+":"+ipmac.getMac())){
		   		        			//若不存在该基线,则加入
		   		        			IpMacBase vo = new IpMacBase();
		   		        			vo.setRelateipaddr(ipmac.getRelateipaddr());
		   		        			vo.setIfindex(ipmac.getIfindex());
		   		        			vo.setIpaddress(ipmac.getIpaddress());
		   		        			vo.setMac(ipmac.getMac());
		   		        			//vo.setIfband(SystemConstant.MACBAND_BASE);//设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
		   		        			vo.setIfband(1);//0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
		   		        			vo.setIfsms("0");//不发信息
		   		        			vo.setIftel("0");//不打电话
		   		        			vo.setIfemail("0");//不发邮件
		   		        			vo.setBak("");
		   		        			vo.setCollecttime(Calendar.getInstance());
		   		        			try{
										dbmanager.executeUpdate(ipmacbaseInsertSQL(vo));
									}catch(Exception ex){
										ex.printStackTrace();
									}
		   		        		}
	   		        		}	
		   		        }
	   				}catch(Exception e){
	   			e.printStackTrace();
	   			}
	   		}
	   	}catch(Exception e){
	   		e.printStackTrace();
	   	}finally{
	   		dbmanager.close();
	   	}
	   	}

   			return "/ipmac.do?action=list&jp=1";
    }
    
    private String selsetmacbase()
    {	
    	String key = getParaValue("key");
 	   String value = getParaValue("value");	 
 	   IpMacDao macdao = new IpMacDao();
 	   request.setAttribute("key", key);
 	   request.setAttribute("value", value);
	   IpMacBaseDao dao = new IpMacBaseDao();
	   
	   IpMacBase vo = new IpMacBase();
	   String relateip = getParaValue("relateip"); 
	   //int flag = getParaIntValue("flag");
	   Host host = (Host)PollingEngine.getInstance().getNodeByIP(relateip);
		//vo = dao.loadIpMac(id);
	    String mac = getParaValue("mac");
		String ifindex = getParaValue("ifindex");
		String macip = getParaValue("macip");
		List existlist = dao.loadIpMacBaseByRIPMAC(host.getIpAddress(), mac);
		if(existlist != null && existlist.size()>0){
		}else{
			vo.setRelateipaddr(host.getIpAddress());
			vo.setIfindex(ifindex);
			vo.setIpaddress(macip);
			vo.setMac(mac);
			//vo.setIfband(SystemConstant.MACBAND_BASE);//设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			vo.setIfband(0);//0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			vo.setIfsms("0");//不发信息
			vo.setIftel("0");//不打电话
			vo.setIfemail("0");//不发邮件
			vo.setBak("");
			vo.setCollecttime(Calendar.getInstance());
			try{
				dao = new IpMacBaseDao();
				dao.save(vo);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
		}
		dao.close();
		setTarget("/config/ipmac/findlist.jsp");
	    return list(macdao," where "+key+" = '"+value+"'");
		//return "/ipmac.do?action=list";
   }
    
    private String cancelipmacbase()
    {	 
	   IpMacBaseDao dao = new IpMacBaseDao();
	   
	   IpMacBase vo = new IpMacBase();
	   int id = getParaIntValue("id"); 
	   int flag = getParaIntValue("doflag");
	   Host host = (Host)PollingEngine.getInstance().getNodeByID(id);
	   
	   
		//vo = dao.loadIpMac(id);
	    String mac = getParaValue("mac");
		String ifindex = getParaValue("ifindex");
		String macip = getParaValue("macip");
		List existlist = dao.loadIpMacBaseByRIPMAC(host.getIpAddress(), mac);
		if(existlist != null && existlist.size()>0){
			vo = (IpMacBase)existlist.get(0);
			
			dao = new IpMacBaseDao();
			//vo.setIfband(SystemConstant.MACBAND_BASE);//设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			vo.setIfband(flag);//0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			try{
				if(flag == -1){
					String[] ids = new String[1];
					ids[0] = vo.getId()+"";
					dao.delete(ids);
				}else
					dao.update(vo);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
		}else{
			dao = new IpMacBaseDao();
			vo.setRelateipaddr(host.getIpAddress());
			vo.setIfindex(ifindex);
			vo.setIpaddress(macip);
			vo.setMac(mac);
			//vo.setIfband(SystemConstant.MACBAND_BASE);//设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			vo.setIfband(flag);//设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			vo.setIfsms("0");//不发信息
			vo.setIftel("0");//不打电话
			vo.setIfemail("0");//不发邮件
			try{
				dao.save(vo);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
		}
		dao.close();

		return "/monitor.do?action=netfdb&id="+id+"&ipaddress="+host.getIpAddress();
   }
    
    private String cancelmacbase()
    {	 
	   IpMacBaseDao dao = new IpMacBaseDao();
	   
	   IpMacBase vo = new IpMacBase();
	   String relateip = getParaValue("relateip"); 
	   Host host = (Host)PollingEngine.getInstance().getNodeByIP(relateip);
	    String mac = getParaValue("mac");
		String ifindex = getParaValue("ifindex");
		String macip = getParaValue("macip");
		String flag = getParaValue("flag");
		List existlist = dao.loadIpMacBaseByRIPMAC(host.getIpAddress(), mac);
		if(existlist != null && existlist.size()>0){
			vo = (IpMacBase)existlist.get(0);
			
			dao = new IpMacBaseDao();
			//vo.setIfband(0);//0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			try{
					String[] ids = new String[1];
					ids[0] = vo.getId()+"";
					dao.delete(ids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
		}
		dao.close();
		if(flag != null && flag.trim().length()>0 && flag.equalsIgnoreCase("1")){
			return "/monitor.do?action=netarp&id="+host.getId()+"&ipaddress="+host.getIpAddress();
		}else{
			return "/ipmac.do?action=list&jp=1";
		}
   }
    private String selcancelmacbase()
    {	 
    	String key = getParaValue("key");
  	   String value = getParaValue("value");	 
  	   IpMacDao macdao = new IpMacDao();
  	   request.setAttribute("key", key);
  	   request.setAttribute("value", value);
 	   IpMacBaseDao dao = new IpMacBaseDao();
	   
	   IpMacBase vo = new IpMacBase();
	   String relateip = getParaValue("relateip"); 
	   Host host = (Host)PollingEngine.getInstance().getNodeByIP(relateip);
	    String mac = getParaValue("mac");
		String ifindex = getParaValue("ifindex");
		String macip = getParaValue("macip");
		List existlist = dao.loadIpMacBaseByRIPMAC(host.getIpAddress(), mac);
		if(existlist != null && existlist.size()>0){
			vo = (IpMacBase)existlist.get(0);
			
			dao = new IpMacBaseDao();
			//vo.setIfband(0);//0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
			try{
					String[] ids = new String[1];
					ids[0] = vo.getId()+"";
					dao.delete(ids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
		}
		dao.close();
		setTarget("/config/ipmac/findlist.jsp");
	    return list(macdao," where "+key+" = '"+value+"'");
   }

    private String find()
    {
	   String key = getParaValue("key");
	   String value = getParaValue("value");	 
	   IpMacDao dao = new IpMacDao();
	   request.setAttribute("key", key);
	   request.setAttribute("value", value);
	   setTarget("/config/ipmac/findlist.jsp");
       return list(dao," where "+key+" like '%"+value+"%'");
   }
    
  //quzhi
	public String downloadipmacreportall()
	{
		//Hashtable allcpuhash = new Hashtable();
		IpMacDao ipmacdao = new IpMacDao();
		List list = ipmacdao.loadAll();
		//int startRow = ((Integer)session.getAttribute("startRow")).intValue();
		Hashtable reporthash = new Hashtable();
		if (list!=null) {
			reporthash.put("list", list);
		}
		else {
			list = new ArrayList();
		}
		//reporthash.put("startRow", startRow);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		
		report.createReport_ipmacall("/temp/ipmacall_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/alarm/syslog/download.jsp";
	}
	
	public String downloadipmacreport()
	{
		//Hashtable allcpuhash = new Hashtable();
		List list = (List) session.getAttribute("list");
		int startRow = ((Integer)session.getAttribute("startRow")).intValue();
		Hashtable reporthash = new Hashtable();
		if (list!=null) {
			reporthash.put("list", list);
		}
		else {
			list = new ArrayList();
		}
		reporthash.put("startRow", startRow);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		
		report.createReport_ipmac("/temp/ipmac_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/alarm/syslog/download.jsp";
	}
	public String downloadipmacfindreportall()
	{
		 String key = (String)session.getAttribute("key");
		 String value = (String)session.getAttribute("value");	
		IpMacDao ipmacdao = new IpMacDao();
		List list = ipmacdao.findByCondition(key, value);
		Hashtable reporthash = new Hashtable();
		if (list!=null) {
			reporthash.put("list", list);
		}
		else {
			list = new ArrayList();
		}
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		
		report.createReport_ipmacall("/temp/ipmacfindall_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/alarm/syslog/download.jsp";
	}
    
   public String execute(String action)
   {
	  if(action.equals("sychronizeData")){
		  return sychronizeData();
	  }
	  if(action.equals("list"))
	     return list(); 
	  if(action.equals("monitornodelist"))
		 return monitornodelist();      
	  if(action.equals("ready_edit"))
         return readyEdit();
      if(action.equals("update"))
         return update(); 
      if(action.equals("deleteall"))
          return deleteall();
      if(action.equals("find"))
         return find();  
      if(action.equals("updateselect"))
          return updateselect();
      if(action.equals("setipmacbase"))
          return setipmacbase();
      if(action.equals("setmacbase"))
          return setmacbase();
      if(action.equals("setlistbaseline"))
          return setlistbaseline();
      if(action.equals("setmultimacbase"))
          return setmultimacbase();
      if(action.equals("selsetmacbase"))
          return selsetmacbase();
      if(action.equals("cancelipmacbase"))
          return cancelipmacbase();
      if(action.equals("cancelmacbase"))
          return cancelmacbase();
      if(action.equals("selcancelmacbase"))
          return selcancelmacbase();
	  if(action.equals("ready_add"))
	     return "/topology/network/add.jsp";
	  if(action.equals("downloadipmacreportall"))
          return downloadipmacreportall();
	  if(action.equals("downloadipmacreport"))
          return downloadipmacreport();
	  if(action.equals("downloadipmacfindreportall"))
          return downloadipmacfindreportall();
	  if (action.equals("delete"))
      {	  
		    DaoInterface dao = new IpMacDao();
    	    setTarget("/ipmac.do?action=list");
            return delete(dao);
        }
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
   
	public String ipmacbaseInsertSQL(IpMacBase ipmacband){
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = "";
		String time = sdf.format(ipmacband.getCollecttime().getTime());
		sql = "insert into nms_ipmacbase(relateipaddr,ifindex,ipaddress,mac,collecttime,ifband,ifsms,iftel,ifemail,employee_id)values('";
		sql = sql+ipmacband.getRelateipaddr()+"','"+ipmacband.getIfindex()+"','"+ipmacband.getIpaddress()+"','";
		sql = sql+ipmacband.getMac()+"','"+time+"','"+ipmacband.getIfband()+"','"+ipmacband.getIfsms()+"','"+ipmacband.getIftel()+"','"+ipmacband.getIfemail()+"',"+ipmacband.getEmployee_id()+")";
		return sql;
	}
	
	public String sychronizeData(){
		
		HostNodeDao dao = new HostNodeDao();
		List list = (List)dao.loadNetwork(1);
		HostNode hostnode = null;
		Host node = null;
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				hostnode = (HostNode)list.get(i);
				node = (Host) PollingEngine.getInstance().getNodeByID(hostnode.getId());
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());

				NetDatatempFdbRtosql totempsql = new NetDatatempFdbRtosql();
				totempsql.CreateResultTosql1(ipAllData, node);
			}
		}
		IpMacDao ipmacdao = new IpMacDao();
		setTarget("/config/ipmac/list.jsp");
        return list(ipmacdao);
	}
}
