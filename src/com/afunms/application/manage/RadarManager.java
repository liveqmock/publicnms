/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.text.*;

import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.common.base.*;
import com.afunms.common.*;
import com.afunms.common.util.*;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.event.dao.EventListDao;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.BaseTask;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.om.Task;
import com.afunms.polling.task.TaskXml;
import com.afunms.polling.loader.*;
import com.afunms.application.model.*;
import com.afunms.system.model.User;
import com.afunms.topology.util.*;
import com.afunms.application.dao.*;
import com.afunms.application.util.DBPool;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.polling.impl.*;
import com.afunms.polling.api.*;

import com.afunms.report.jfree.ChartCreator;


import org.jfree.data.general.DefaultPieDataset;

public class RadarManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		List ips = new ArrayList();
		User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		String bids = operator.getBusinessids();
		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if(bid != null && bid.length>0){
			for(int i=0;i<bid.length;i++){
				if(bid[i] != null && bid[i].trim().length()>0)
					rbids.add(bid[i].trim());
			}
		}
		//request.setAttribute("iplist",ips);
		RadarConfigDao configdao = new RadarConfigDao();	
		setTarget("/application/radar/list.jsp");
		return list(configdao);
	}

	private String add()
    {    	   
		RadarConfig vo = new RadarConfig();
		
		vo.setName(getParaValue("name"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setSupperdir(getParaValue("supperdir"));
		vo.setSubdir(getParaValue("subdir"));
		vo.setInter(getParaValue("inter"));
		vo.setFilesize(getParaIntValue("filesize"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setMon_flag(getParaIntValue("mon_flag"));
		vo.setSendemail(getParaValue("sendemail"));

        String allbid = ",";
        String[] businessids = getParaArrayValue("checkbox");
        if(businessids != null && businessids.length>0){
        	for(int i=0;i<businessids.length;i++){
        		
        		String bid = businessids[i];
        		allbid= allbid+bid+",";
        	}
        } 
        vo.setNetid(allbid);
        
        RadarConfigDao dao = new RadarConfigDao();
        dao.save(vo);
      //生成相应的表
        String ip = vo.getIpaddress();
//		String ip1 ="",ip2="",ip3="",ip4="";
//		String[] ipdot = ip.split(".");	
//		String tempStr = "";
//		String allipstr = "";
//		if (ip.indexOf(".")>0){
//			ip1=ip.substring(0,ip.indexOf("."));
//			ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//			tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//		}
//		ip2=tempStr.substring(0,tempStr.indexOf("."));
//		ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//		allipstr=ip1+ip2+ip3+ip4;
        String allipstr = SysUtil.doip(ip);
		CreateTableManager ctable = new CreateTableManager();
		DBManager conn = new DBManager();
		try{
			ctable.createTable(conn,"radar",allipstr,"ping");//Ping
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				conn.executeBatch();
			}catch(Exception e){
				e.printStackTrace();
			}
			conn.close();
		}
        return "/radar.do?action=list&jp=1";
    }    
	
	public String delete()
	{
		String[] ids = getParaArrayValue("checkbox");
		RadarConfig vo = new RadarConfig();
		List list = new ArrayList();
		RadarConfigDao configdao = new RadarConfigDao();
    	if(ids != null && ids.length > 0){		
    		configdao.delete(ids);
    		for(int i=0;i<ids.length;i++){
    			vo = (RadarConfig)configdao.findByID(ids[i]);
    			//删除相应的表
    	        String ip = vo.getIpaddress();
//    			String ip1 ="",ip2="",ip3="",ip4="";
//    			String[] ipdot = ip.split(".");	
//    			String tempStr = "";
//    			String allipstr = "";
//    			if (ip.indexOf(".")>0){
//    				ip1=ip.substring(0,ip.indexOf("."));
//    				ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//    				tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//    			}
//    			ip2=tempStr.substring(0,tempStr.indexOf("."));
//    			ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//    			allipstr=ip1+ip2+ip3+ip4;
    	        String allipstr = SysUtil.doip(ip);
    			CreateTableManager ctable = new CreateTableManager();
    			DBManager conn = new DBManager();
    			try{
    				ctable.deleteTable(conn,"radar",allipstr,"ping");//Ping
    			}catch(Exception e){
    				
    			}finally{
    				conn.close();
    			}
    		}
    	}
		try{
			User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if(bid != null && bid.length>0){
				for(int i=0;i<bid.length;i++){
					if(bid[i] != null && bid[i].trim().length()>0)
						rbids.add(bid[i].trim());
				}
			}
			configdao = new RadarConfigDao();
			list = configdao.getRadarByBID(rbids);
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("list",list);
		return "/application/radar/searchlist.jsp";
	}
	
	private String update()
    {    	   
		RadarConfig vo = new RadarConfig();
		RadarConfigDao configdao = new RadarConfigDao();
		RadarConfig formervo = new RadarConfig();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";		
		formervo = (RadarConfig)configdao.findByID(getParaValue("id"));
		
    	vo.setId(getParaIntValue("id"));
		vo.setName(getParaValue("name"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setSupperdir(getParaValue("supperdir"));
		vo.setSubdir(getParaValue("subdir"));
		vo.setInter(getParaValue("inter"));
		vo.setFilesize(getParaIntValue("filesize"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setMon_flag(getParaIntValue("mon_flag"));
		//vo.setNetid(rs.getString("netid"));
		vo.setSendemail(getParaValue("sendemail"));

        String allbid = ",";
        String[] businessids = getParaArrayValue("checkbox");
        if(businessids != null && businessids.length>0){
        	for(int i=0;i<businessids.length;i++){
        		
        		String bid = businessids[i];
        		allbid= allbid+bid+",";
        	}
        } 
        vo.setNetid(allbid);
        //WebConfigDao configdao = new WebConfigDao();
        try{
        	configdao.update(vo);	
        	if(!vo.getIpaddress().equalsIgnoreCase(formervo.getIpaddress())){
        		//删除相应的表
        		 String ip = formervo.getIpaddress();
//     			String ip1 ="",ip2="",ip3="",ip4="";
//     			String[] ipdot = ip.split(".");	
//     			String tempStr = "";
//     			String allipstr = "";
//     			if (ip.indexOf(".")>0){
//     				ip1=ip.substring(0,ip.indexOf("."));
//     				ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//     				tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//     			}
//     			ip2=tempStr.substring(0,tempStr.indexOf("."));
//     			ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//     			allipstr=ip1+ip2+ip3+ip4;
        		String allipstr = SysUtil.doip(ip);
     			CreateTableManager ctable = new CreateTableManager();
     			DBManager conn = new DBManager();
     			try{
     				ctable.deleteTable(conn,"radar",allipstr,"ping");//Ping
     			}catch(Exception e){
     				
     			}finally{
     				conn.close();
     			}
     			//生成新的表
     			//生成相应的表
     	        ip = vo.getIpaddress();
//     			ip1 ="";ip2="";ip3="";ip4="";
//     			ipdot = ip.split(".");	
//     			tempStr = "";
//     			allipstr = "";
//     			if (ip.indexOf(".")>0){
//     				ip1=ip.substring(0,ip.indexOf("."));
//     				ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//     				tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//     			}
//     			ip2=tempStr.substring(0,tempStr.indexOf("."));
//     			ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//     			allipstr=ip1+ip2+ip3+ip4;
     	        allipstr = SysUtil.doip(ip);
     			ctable = new CreateTableManager();
     			conn = new DBManager();
     			try{
     				ctable.createTable(conn,"radar",allipstr,"ping");//Ping
     			}catch(Exception e){
     				
     			}finally{
     				try{
     					conn.executeBatch();
     				}catch(Exception e){
     					
     				}
     				conn.close();
     			}
        	}
			User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if(bid != null && bid.length>0){
				for(int i=0;i<bid.length;i++){
					if(bid[i] != null && bid[i].trim().length()>0)
						rbids.add(bid[i].trim());
				}
			}
			configdao = new RadarConfigDao();
			list = configdao.getRadarByBID(rbids);
			
        }catch(Exception e){
        	e.printStackTrace();
        }
		request.setAttribute("list",list);
             
		return "/application/radar/searchlist.jsp";
        //return "/grapes.do?action=list&jp=1";
    }
	
	private String search()
    {    	   
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		SybspaceconfigDao configdao = new SybspaceconfigDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";

		try{
			ipaddress = getParaValue("ipaddress");
			User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if(bid != null && bid.length>0){
				for(int i=0;i<bid.length;i++){
					if(bid[i] != null && bid[i].trim().length()>0)
						rbids.add(bid[i].trim());
				}
			}

			List oraList = new ArrayList();
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = (DBTypeVo)typedao.findByDbtype("sybase");
			try{
				oraList = dao.getDbByTypeAndBID(typevo.getId(),rbids);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(oraList != null && oraList.size()>0){
				for(int i=0;i<oraList.size();i++){
					DBVo dbmonitorlist = (DBVo)oraList.get(i);
					ips.add(dbmonitorlist.getIpAddress());
				}
			}
			
			
			configdao = new SybspaceconfigDao();
			//configdao.fromLastToOraspaceconfig();
			
			//ipaddress = (String)session.getAttribute("ipaddress");			
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new SybspaceconfigDao();
				list =configdao.getByIp(ipaddress);
				if (list == null || list.size() == 0){
					list = configdao.loadAll();
				}
			}else{
				configdao = new SybspaceconfigDao();
				list = configdao.loadAll();		
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//request.setAttribute("Oraspaceconfiglist", conflist);
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
		configdao = new SybspaceconfigDao();
		list = configdao.getByIp(ipaddress);
		request.setAttribute("list",list);
		return "/application/db/sybaseconfigsearchlist.jsp";
    }
	
	private String addalert()
    {    
		RadarConfig vo = new RadarConfig();
		RadarConfigDao configdao = new RadarConfigDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			vo = (RadarConfig)configdao.findByID(getParaValue("id"));
			vo.setMon_flag(1);
			configdao = new RadarConfigDao();
			configdao.update(vo);	
			User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if(bid != null && bid.length>0){
				for(int i=0;i<bid.length;i++){
					if(bid[i] != null && bid[i].trim().length()>0)
						rbids.add(bid[i].trim());
				}
			}
			configdao = new RadarConfigDao();
			list = configdao.getRadarByBID(rbids);
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("list",list);
		return "/application/radar/searchlist.jsp";
    }
	
	private String cancelalert()
    {    
		RadarConfig vo = new RadarConfig();
		RadarConfigDao configdao = new RadarConfigDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			vo = (RadarConfig)configdao.findByID(getParaValue("id"));
			vo.setMon_flag(0);
			configdao = new RadarConfigDao();
			configdao.update(vo);	
			User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if(bid != null && bid.length>0){
				for(int i=0;i<bid.length;i++){
					if(bid[i] != null && bid[i].trim().length()>0)
						rbids.add(bid[i].trim());
				}
			}
			configdao = new RadarConfigDao();
			list = configdao.getRadarByBID(rbids);
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("list",list);
		return "/application/radar/searchlist.jsp";
    }
	
	private String detail()
    {    
        List remotelist = new ArrayList();
        List locallist = new ArrayList();
        Vector mqValue = new Vector();
        Hashtable rValue = new Hashtable();
        MQConfig vo = new MQConfig();
		MQConfigDao configdao = new MQConfigDao();
		
        try{
        	vo = (MQConfig)configdao.findByID(getParaValue("id"));
        	String ip = vo.getIpaddress();
        	Hashtable allMqValues = ShareData.getMqdata();
        	if (allMqValues != null && allMqValues.size()>0){
        		SysLogger.info(vo.getIpaddress()+":"+vo.getManagername()+"========");
				rValue = (Hashtable)allMqValues.get(ip+":"+vo.getManagername());
        		mqValue = (Vector)rValue.get("mqValue");
        		remotelist = (List)rValue.get("remote");
        		locallist = (List)rValue.get("local");
        		if(mqValue == null)mqValue = new Vector();
        		if(remotelist == null)remotelist = new ArrayList();
        		if(locallist == null)locallist = new ArrayList();
        	}
            request.setAttribute("ipaddress", ip);
            request.setAttribute("managername", vo.getManagername());
            request.setAttribute("mqname", vo.getName());
        	
        }catch(Exception e){
        	e.printStackTrace();
        }
        request.setAttribute("mqValue", mqValue);
        request.setAttribute("remote", remotelist);
        request.setAttribute("local", locallist);
		return "/application/mq/detail.jsp";
    }
	
	public String execute(String action) 
	{	
        if(action.equals("list"))
            return list();    
        if(action.equals("ready_add"))
        	return "/application/radar/add.jsp";
        if(action.equals("add"))
        	return add();
        if(action.equals("delete"))
            return delete();
        if(action.equals("ready_edit"))
        {	  
 		    DaoInterface dao = new RadarConfigDao();
    	    setTarget("/application/radar/edit.jsp");
            return readyEdit(dao);
        }
        if(action.equals("update"))
            return update();
        if(action.equals("addalert"))
            return addalert();
        if(action.equals("cancelalert"))
            return cancelalert();
        if(action.equals("detail"))
            return detail();
        if(action.equals("search"))
            return search();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
	private void drawPiechart(String[] keys,String[] values,String chname,String enname){
		ChartGraph cg = new ChartGraph();
		DefaultPieDataset piedata = new DefaultPieDataset();
		for(int i=0;i<keys.length;i++){
		  piedata.setValue(keys[i], new Double(values[i]).doubleValue());
		}
		cg.pie(chname,piedata,enname,300,120);    	
	}
	
	private void drawchart(Minute[] minutes,String keys,String[][] values,String chname,String enname){
		try{
		//int size = keys.length;
		TimeSeries[] s2 = new TimeSeries[1];
		String[] keymemory = new String[1];
		String[] unitMemory = new String[1];
		//for(int i = 0 ; i < size ; i++){
			String key = keys;
		//	System.out.println("in drawchart -------------- i="+i+" key="+key+" ");
			TimeSeries ss2 = new TimeSeries(key,Minute.class);
			String[] hmemory = values[0];
			arrayTochart(ss2,hmemory,minutes);
			keymemory[0] = key;
			s2[0] = ss2;
	//		}
		ChartGraph cg = new ChartGraph();
		cg.timewave(s2,"x","y(MB)",chname,enname,300,150);    	
		}catch(Exception e){
		System.out.println("drawchart error:" + e);
	}
	}
	
	private void arrayTochart(TimeSeries s,String[] h,Minute[] minutes){
		try{
		for(int j=0;j<h.length;j++){
			//System.out.println("h[i]: " + h[j]);
			String value=h[j];
			Double v=new Double(0);
			if(value!=null)	 {
				v=new Double(value);
			}
			s.addOrUpdate(minutes[j],v);
			}
		}catch(Exception e){
			System.out.println("arraytochart error:" + e);
		}
	}
	public String getF(String s){
		if(s.length()>5)
			s=s.substring(0,5);
		return s;
	}
	
	private void p_draw_line(Hashtable hash,String title1,String title2,int w,int h){
		List list = (List)hash.get("list");
		try{
		if(list==null || list.size()==0){
			draw_blank(title1,title2,w,h);
		}
		else{
		String unit = (String)hash.get("unit");
		if (unit == null)unit="%";
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1,Minute.class);
		TimeSeries[] s = {ss};
		for(int j=0; j<list.size(); j++){
			//if (title1.equals("Cpu利用率")){
				Vector v = (Vector)list.get(j);
				//CPUcollectdata obj = (CPUcollectdata)list.get(j);
				Double	d=new Double((String)v.get(0));			
				String dt = (String)v.get(1);			
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date time1 = sdf.parse(dt);				
				Calendar temp = Calendar.getInstance();
				temp.setTime(time1);
				Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
				ss.addOrUpdate(minute,d);
			//}
		}
		cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
		}
		hash = null;
		}
		catch(Exception e){e.printStackTrace();}
		}

	private void draw_blank(String title1,String title2,int w,int h){
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1,Minute.class);
		TimeSeries[] s = {ss};
		try{
			Calendar temp = Calendar.getInstance();
			Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
			ss.addOrUpdate(minute,null);
			cg.timewave(s,"x(时间)","y",title1,title2,w,h);
		}
		catch(Exception e){e.printStackTrace();}
	}
	
}