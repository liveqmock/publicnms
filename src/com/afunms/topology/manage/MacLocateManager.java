/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import com.afunms.common.base.*;
import com.afunms.common.util.CreateTableManager;
import com.afunms.polling.*;
import com.afunms.polling.node.*;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpMacBase;
import com.afunms.system.dao.DepartmentDao;
import com.afunms.topology.dao.IpMacDao;
import com.afunms.topology.dao.IpMacBaseDao;
import com.afunms.topology.util.*;
import com.afunms.common.util.DBManager;
import com.afunms.config.model.*;
import com.afunms.config.dao.*;
import com.afunms.common.util.*;
import java.util.*;

/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class MacLocateManager extends BaseManager implements ManagerInterface
{
	private String readyfind()
	{
		//IpMacDao dao = new IpMacDao();
		//setTarget("/config/ipmac/list.jsp");
        //return list(dao);
		return "/config/maclocate/readyfind.jsp";
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
	   int flag = getParaIntValue("flag");
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
		if(flag != null && flag.trim().length()>0 && flag.equalsIgnoreCase("1")){
			return "/monitor.do?action=netarp&id="+host.getId()+"&ipaddress="+host.getIpAddress();
		}else{
			return "/ipmac.do?action=list&jp=1";
		}
		
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
	   int flag = getParaIntValue("flag");
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
	   String mac = getParaValue("mac");
	   System.out.println("MacLocateManager.java-------find()---->"+mac);
	   List maclist = new ArrayList();
	   //String value = getParaValue("value");	 
	   IpMacDao dao = new IpMacDao();
	   String where = "";
	   where = "where mac like '%" + mac + "%'";
	   List list = null;
	   try{
		   maclist = dao.loadAll(where);
	   }catch(Exception e){
		   e.printStackTrace();
	   }finally{
		   dao.close();
	   }
//	   if(list != null && list.size()>0){
//		   for(int i=0;i<list.size();i++){
//			   IpMac vo = (IpMac)list.get(i);
//			//   System.out.println("=======>"+vo.getId());
//		//	   System.out.println("MacLocateManager.java-------find()---->"+mac+"======="+vo.getMac());
//			   if(mac.equalsIgnoreCase(vo.getMac())){
//				   maclist.add(vo);
//			   }
//		   }
//	   }
	   request.setAttribute("maclist", maclist);
       return "/config/maclocate/findlist.jsp";
   }
    
   public String execute(String action)
   { 
	  if(action.equals("readyfind"))
		 return readyfind();      
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
	  if (action.equals("delete"))
      {	  
		    DaoInterface dao = new IpMacDao();
    	    setTarget("/ipmac.do?action=list");
            return delete(dao);
        }
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
}
