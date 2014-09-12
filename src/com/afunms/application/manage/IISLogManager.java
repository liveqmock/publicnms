/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.application.dao.IISLogConfigDao;
import com.afunms.application.dao.IISLog_historyDao;
import com.afunms.application.model.IISLogConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.polling.loader.IISLoader;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.util.KeyGenerator;

public class IISLogManager extends BaseManager implements ManagerInterface
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
		IISLogConfigDao configdao = new IISLogConfigDao();
		List list = new ArrayList();
		try{
			//list = configdao.getIISLogByBID(rbids);
			list = configdao.loadAll();
			System.out.println(list.size()+"====");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		request.setAttribute("list",list);
		return "/application/iislog/list.jsp";
	}
	
	/**
	 * snow 增加前将供应商查找到
	 * @return
	 */
	private String ready_add(){
		SupperDao supperdao = new SupperDao();
    	List<Supper> allSupper = supperdao.loadAll();
    	request.setAttribute("allSupper", allSupper);
		return "/application/iislog/add.jsp";
	}


	private String add()
    {    	   
		IISLogConfig vo = new IISLogConfig();
		System.out.println(getParaValue("name"));
		System.out.println(getParaValue("name"));
		try {
			request.setCharacterEncoding("gb2312");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setName(getParaValue("name"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setHistory_row(0);
		vo.setFlag(getParaIntValue("_flag"));
		//vo.setNetid(rs.getString("netid"));
		vo.setSupperid(getParaIntValue("supperid"));//snow add 2010-5-20
		
        String allbid = ",";
        String[] businessids = getParaArrayValue("bid");
        if(businessids != null && businessids.length>0){
        	for(int i=0;i<businessids.length;i++){
        		
        		String bid = businessids[i];
        		allbid= allbid+bid+",";
        	}
        } 
        vo.setNetid(allbid);
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
		IISLogConfigDao configdao = new IISLogConfigDao();
        try{
        	configdao.save(vo);
        	/* 增加采集时间设置 snow add at 2010-5-20 */
        	TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
        	timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("18"));
        	/* snow add end*/

        }catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
        configdao = new IISLogConfigDao();
		List list = new ArrayList();
		try{
			//list = configdao.getIISLogByBID(rbids);
			list = configdao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		request.setAttribute("list",list);
		return "/application/iislog/list.jsp";
    }    
	
	public String delete()
	{
		String[] ids = getParaArrayValue("checkbox");
		List list = new ArrayList();
		IISLogConfigDao configdao = null;
    	if(ids != null && ids.length > 0){
    		/* snow add at 2010-5-20*/
    		TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil(); // snow add at 2010-5-20 删除数据库采集时间
    		for (String id : ids) {
    			timeGratherConfigUtil.deleteTimeGratherConfig(id, timeGratherConfigUtil.getObjectType("18")); // snow add at 2010-5-20 
			}
    		/* snow add end */
    		configdao = new IISLogConfigDao();
    		try{
    			
    			for(int i = 0 ; i< ids.length ; i++){
    				IISLogConfigDao _configdao = new IISLogConfigDao();
    				IISLogConfig vo = new IISLogConfig();
    				try{
    					vo = (IISLogConfig)_configdao.findByID(ids[i]);
    				}catch(Exception e){
    					e.printStackTrace();
    				}finally{
    					_configdao.close();
    				}
    				if(vo == null)continue;
    				String ip = vo.getIpaddress();
//    				String ip1 ="",ip2="",ip3="",ip4="";
//    				String[] ipdot = ip.split(".");	
//    				String tempStr = "";
//    				String allipstr = "";
//    				if (ip.indexOf(".")>0){
//    					ip1=ip.substring(0,ip.indexOf("."));
//    					ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//    					tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//    				}
//    				ip2=tempStr.substring(0,tempStr.indexOf("."));
//    				ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//    				allipstr=ip1+ip2+ip3+ip4;
    				String allipstr = SysUtil.doip(ip);
    		 		//删除SYSLOG表格
    				DBManager conn = new DBManager();
    				String _sql = "";
    				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
    					_sql = "drop table if exists iislog"+allipstr;
    				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
    					_sql = "drop table iislog"+allipstr;				
    					CreateTableManager.dropSeqOrcl(conn, "iislog", allipstr);
    				}
    				 	
    				 	System.out.println(_sql);		 	
    				 	try{
    				 		conn.executeUpdate(_sql);
    				 	}catch(Exception e){
    				 		e.printStackTrace();
    				 		try{		
    				 			conn.rollback();		 			
    				 		}catch(Exception ex){
    				
    				 		}
    			
    				 	}finally{
    				 		conn.close();
    				 	}
    			}
    			configdao.delete(ids);
    		}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			configdao.close();
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
			configdao = new IISLogConfigDao();
			try{
				//list = configdao.getIISLogByBID(rbids);
				list = configdao.loadAll();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("list",list);
		return "/application/iislog/list.jsp";
	}
	
	
	/**
	 * @author nielin add for time-sharing at 2010-01-05
	 * @return
	 */
	private String ready_edit(){
		String jsp = "/application/iislog/edit.jsp";
		IISLogConfigDao dao = new IISLogConfigDao();
		try{
			setTarget(jsp);
			jsp = readyEdit(dao);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		/* 获得设备的采集时间 snow add at 2010-05-20 */
		//提供供应商信息
		SupperDao supperdao = new SupperDao();
    	List<Supper> allSupper = supperdao.loadAll();
    	request.setAttribute("allSupper", allSupper);
    	//提供已设置的采集时间信息
    	TimeGratherConfigUtil tg  = new TimeGratherConfigUtil();
    	List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("18"));
    	for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
    		timeGratherConfig.setHourAndMin();
		}
    	request.setAttribute("timeGratherConfigList", timeGratherConfigList);  
    	/* snow end */

	    return jsp;
	}
	
	private String update()
    {    	   
		IISLogConfig vo = new IISLogConfig();
		IISLogConfig oldvo = new IISLogConfig();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";		
		
    	vo.setId(getParaIntValue("id"));
		vo.setName(getParaValue("name"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setHistory_row(0);
		vo.setFlag(getParaIntValue("mon_flag"));
		vo.setSupperid(getParaIntValue("supperid"));//snow add 2010-5-20

        String allbid = ",";
        String[] businessids = getParaArrayValue("checkbox");
        if(businessids != null && businessids.length>0){
        	for(int i=0;i<businessids.length;i++){
        		
        		String bid = businessids[i];
        		allbid= allbid+bid+",";
        	}
        } 
        vo.setNetid(allbid);
        
        
        IISLogConfigDao configdao = new IISLogConfigDao();
        try{
        	oldvo = (IISLogConfig)configdao.findByID(vo.getId()+"");
        	
        	try{
        		configdao.update(vo);
        		if(!vo.getIpaddress().equalsIgnoreCase(oldvo.getIpaddress())){
        			//修改了IP
        			//删除原来的IISLOG表,新建IISLOG表
        			String ip = oldvo.getIpaddress();
//    				String ip1 ="",ip2="",ip3="",ip4="";
//    				String[] ipdot = ip.split(".");	
//    				String tempStr = "";
//    				String allipstr = "";
//    				if (ip.indexOf(".")>0){
//    					ip1=ip.substring(0,ip.indexOf("."));
//    					ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//    					tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//    				}
//    				ip2=tempStr.substring(0,tempStr.indexOf("."));
//    				ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//    				allipstr=ip1+ip2+ip3+ip4;
        			String allipstr = SysUtil.doip(ip);
    		        CreateTableManager ctable = new CreateTableManager();
    		        DBManager conn = new DBManager();
    		        try{
    		        	conn.executeUpdate("drop table iislog"+allipstr);
    		        }catch(Exception e){
    		        	e.printStackTrace();
    		        }finally{
    		        	conn.close();
    		        }
    		        
    		        //新建
    		      //生成表
    				ip = vo.getIpaddress();
//    				tempStr = "";
//    				allipstr = "";
//    				if (ip.indexOf(".")>0){
//    					ip1=ip.substring(0,ip.indexOf("."));
//    					ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//    					tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//    				}
//    				ip2=tempStr.substring(0,tempStr.indexOf("."));
//    				ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//    				allipstr=ip1+ip2+ip3+ip4;
    				allipstr = SysUtil.doip(ip);
    			 	String _sql = "";
    			 	//if (tablename.indexOf("hour")>=0){
    			 		//创建SYSLOG表格
    			 	if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
    			 		_sql="create table iislog"+allipstr
    				 	+"(ID bigint(20) not null auto_increment,configid bigint(20),ssitename VARCHAR(20),sip VARCHAR(15),"
    				 	+"csmethod VARCHAR(10),csuristem VARCHAR(50),csuriquery VARCHAR(100),sport VARCHAR(5),"
    				 	+"csusername VARCHAR(100),cip VARCHAR(15),csagent VARCHAR(1000),"
    				 	+"recordtime timestamp,scstatus bigint(10),scsubstatus bigint(10),scwin32status bigint(10),PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";	 		
    			 	}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
    			 		_sql="create table iislog"+allipstr
    				 	+"(ID number(20) not null,configid number(20),ssitename VARCHAR(20),sip VARCHAR(15),"
    				 	+"csmethod VARCHAR(10),csuristem VARCHAR(50),csuriquery VARCHAR(100),sport VARCHAR(5),"
    				 	+"csusername VARCHAR(100),cip VARCHAR(15),csagent VARCHAR(1000),"
    				 	+"recordtime date default sysdate-1,scstatus number(10),scsubstatus number(10),scwin32status number(10),PRIMARY KEY  (ID))";	 		
    			 	}	 		
    			 	//}
    				 	System.out.println(_sql);
    				 	try{
    				 		conn.executeUpdate(_sql);
    				 		if(SystemConstant.DBType.equals("oracle")){
    							CreateTableManager.createSeqOrcl(conn, "iislog", allipstr);
    							CreateTableManager.createTrigerOrcl(conn, "iislog", allipstr,"iislog");
    				 		}
    				 	}catch(Exception e){
    				 		e.printStackTrace();
    				 		try{		
    				 			conn.rollback();		 			
    				 		}catch(Exception ex){
    				
    				 		}
    				 	}finally{
        		        	conn.close();
        		        }
        		}
        	}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
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
			configdao = new IISLogConfigDao();
			try{
				//list = configdao.getIISLogByBID(rbids);
				list = configdao.loadAll();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
			
			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("18"));
			/* snow add end*/
			
        }catch(Exception e){
        	e.printStackTrace();
        }
		request.setAttribute("list",list);
             
		return "/application/iislog/list.jsp";
    }
	
	private String todayList(){
		IISLog_historyDao dao = new IISLog_historyDao();
		String ipaddress=getParaValue("ipaddress");
		String sql=getSQL(ipaddress);
		try
		{
			List list = dao.findByCriteria(sql);
			request.setAttribute("list", list);
		    request.setAttribute("ipaddress", ipaddress);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			dao.close();
		}
		
		return "/application/iislog/todaylist.jsp";
	}
	
	private String getSQL(String ip){
		String allipstr1 = "";
		int status = 99;
		int level1 = 99;
		int bid = 0;
		String b_time = "";
		String t_time = "";
        String cip = getParaValue("cip");
        String sip=getParaValue("sip");
        System.out.println("sip"+sip);
        System.out.println("cip"+cip);
		bid = getParaIntValue("businessid");
		request.setAttribute("businessid", bid);
		BusinessDao bdao = new BusinessDao();
		List businesslist = bdao.loadAll();
		request.setAttribute("businesslist", businesslist);
		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		if (b_time == null) {
			Calendar   day=Calendar.getInstance();   
            day.add(Calendar.DATE,-1);   
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(day.getTime());
		}
		if (t_time == null) {
			Calendar   day=Calendar.getInstance();   
            day.add(Calendar.DATE,-1); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(day.getTime());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";
		String sql = "";
		
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
		if(sip!=null&&!" ".equals(sip))
		{
	//		String ip5 ="",ip6="",ip7="",ip8="";
	//		String[] ipdot1 = sip.split(".");	
	//		String tempStr1 = "";
	
	//		if (sip.indexOf(".")>0){
	//			ip5=sip.substring(0,sip.indexOf("."));
	//			ip8=sip.substring(sip.lastIndexOf(".")+1,sip.length());			
	//			tempStr1 = sip.substring(sip.indexOf(".")+1,sip.lastIndexOf("."));
	//		}
	//		ip6=tempStr1.substring(0,tempStr1.indexOf("."));
	//		ip7=tempStr1.substring(tempStr1.indexOf(".")+1,tempStr1.length());
	//		allipstr1=ip5+ip6+ip7+ip8;
			allipstr = SysUtil.doip(sip);
		}
		try {
			User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			StringBuffer s = new StringBuffer();
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				if(cip!=null&&!" ".equals(cip)&&" ".equals(sip))
				{
				s.append("select * from iislog"+allipstr+" where recordtime>= '" + starttime1 + "' "
						+ "and recordtime<='" + totime1 + "' and cip like '%"+cip+"%'");
				} else if(sip!=null&&"".equals(cip)){
					s.append("select * from iislog"+allipstr1+" where recordtime>= '" + starttime1 + "' "
							+ "and recordtime<='" + totime1 + "'");
					
				}else if(sip!=null&&cip!=null)
					{
					s.append("select * from iislog"+allipstr1+" where recordtime>= '" + starttime1 + "' "
							+ "and recordtime<='" + totime1 + "' and cip like '%"+cip+"%'");
					
					}
				else 
				{  
					s.append("select * from iislog"+allipstr+" where recordtime>= '" + starttime1 + "' "
							+ "and recordtime<='" + totime1 + "'");
				}
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				if(cip!=null&&!" ".equals(cip)&&" ".equals(sip))
				{
				s.append("select * from iislog"+allipstr+" where recordtime>= to_date('"+starttime1+"','YYYY-MM-DD HH24:MI:SS') "
						+ "and recordtime<=to_date('"+totime1+"','YYYY-MM-DD HH24:MI:SS') and cip like '%"+cip+"%'");
				} else if(sip!=null&&"".equals(cip)){
					s.append("select * from iislog"+allipstr1+" where recordtime>=to_date('"+starttime1+"','YYYY-MM-DD HH24:MI:SS') "
							+ "and recordtime<=to_date('"+totime1+"','YYYY-MM-DD HH24:MI:SS')");
					
				}else if(sip!=null&&cip!=null)
					{
					s.append("select * from iislog"+allipstr1+" where recordtime>=to_date('"+starttime1+"','YYYY-MM-DD HH24:MI:SS') "
							+ "and recordtime<=to_date('"+totime1+"','YYYY-MM-DD HH24:MI:SS') and cip like '%"+cip+"%'");
					
					}
				else 
				{  
					s.append("select * from iislog"+allipstr+" where recordtime>= to_date('"+starttime1+"','YYYY-MM-DD HH24:MI:SS') "
							+ "and recordtime<=to_date('"+totime1+"','YYYY-MM-DD HH24:MI:SS')");
				}
			}

			
			sql = s.toString();
			sql = sql + " order by id desc";
			SysLogger.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return sql;
	}
	
	
	private String search()
    {    	   
//		DBVo vo = new DBVo();
//		DBDao dao = new DBDao();
//		SybspaceconfigDao configdao = new SybspaceconfigDao();
//		List list = new ArrayList();
//		List conflist = new ArrayList();
//		List ips = new ArrayList();
//		String ipaddress ="";
//
//		try{
//			ipaddress = getParaValue("ipaddress");
//			User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
//			String bids = operator.getBusinessids();
//			String bid[] = bids.split(",");
//			Vector rbids = new Vector();
//			if(bid != null && bid.length>0){
//				for(int i=0;i<bid.length;i++){
//					if(bid[i] != null && bid[i].trim().length()>0)
//						rbids.add(bid[i].trim());
//				}
//			}
//
//			List oraList = new ArrayList();
//			DBTypeDao typedao = new DBTypeDao();
//			DBTypeVo typevo = (DBTypeVo)typedao.findByDbtype("sybase");
//			try{
//				oraList = dao.getDbByTypeAndBID(typevo.getId(),rbids);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			
//			if(oraList != null && oraList.size()>0){
//				for(int i=0;i<oraList.size();i++){
//					DBVo dbmonitorlist = (DBVo)oraList.get(i);
//					ips.add(dbmonitorlist.getIpAddress());
//				}
//			}
//			
//			
//			configdao = new SybspaceconfigDao();
//			//configdao.fromLastToOraspaceconfig();
//			
//			//ipaddress = (String)session.getAttribute("ipaddress");			
//			if (ipaddress != null && ipaddress.trim().length()>0){
//				configdao = new SybspaceconfigDao();
//				list =configdao.getByIp(ipaddress);
//				if (list == null || list.size() == 0){
//					list = configdao.loadAll();
//				}
//			}else{
//				configdao = new SybspaceconfigDao();
//				list = configdao.loadAll();		
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		//request.setAttribute("Oraspaceconfiglist", conflist);
//		request.setAttribute("iplist",ips);
//		request.setAttribute("ipaddress",ipaddress);
//		configdao = new SybspaceconfigDao();
//		list = configdao.getByIp(ipaddress);
//		request.setAttribute("list",list);
		return "/application/db/sybaseconfigsearchlist.jsp";
    }
	
	private String addalert()
    {    
		IISLogConfig vo = new IISLogConfig();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		IISLogConfigDao configdao =null;
		try{
			 configdao = new IISLogConfigDao();
				vo = (IISLogConfig)configdao.findByID(getParaValue("id"));
			    vo.setFlag(1);
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
			configdao = new IISLogConfigDao();
				//list = configdao.getIISLogByBID(rbids);
				list = configdao.loadAll();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		
		request.setAttribute("list",list);
		return "/application/iislog/list.jsp";
    }
	
	private String cancelalert()
    {    
		IISLogConfig vo = new IISLogConfig();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			IISLogConfigDao configdao = new IISLogConfigDao();
			try{
				vo = (IISLogConfig)configdao.findByID(getParaValue("id"));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
			vo.setFlag(0);
			configdao = new IISLogConfigDao();
			try{
				configdao.update(vo);	
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
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
			configdao = new IISLogConfigDao();
			try{
				//list = configdao.getIISLogByBID(rbids);
				list = configdao.loadAll();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("list",list);
		return "/application/iislog/list.jsp";
    }
	
	private String detail()
    {    
//		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		WebConfigDao configdao = new WebConfigDao();
//		Urlmonitor_realtimeDao realtimedao = new Urlmonitor_realtimeDao();
//		Urlmonitor_historyDao historydao = new Urlmonitor_historyDao();
//		List urllist = new ArrayList();      //用做条件选择列表
//		WebConfig initconf = new WebConfig();    //当前的对象
//        String lasttime="";
//        String nexttime="";
//        String conn_name="";
//        String valid_name = "";
//        String fresh_name = "";
//        String wave_name = "";
//        String delay_name = "";
//        String connrate="0";
//        String validrate="0";
//        String freshrate="0";
//        Calendar now = Calendar.getInstance();
//        now.setTime(new Date());
//        Date nowdate = new Date();
//        nowdate.getHours();
//        String from_date1 = getParaValue("from_date1");
//        if (from_date1 == null){
//        	from_date1 = timeFormatter.format(new Date());
//        	request.setAttribute("from_date1", from_date1);
//        }
//        String to_date1 = getParaValue("to_date1");
//        if (to_date1 == null){
//        	to_date1 = timeFormatter.format(new Date());
//        	request.setAttribute("to_date1", to_date1);
//        }
//        String from_hour = getParaValue("from_hour");
//        if (from_hour == null){
//        	from_hour = "00";
//        	request.setAttribute("from_hour", from_hour);
//        }
//        String to_hour = getParaValue("to_hour");
//        if(to_hour == null){
//        	to_hour = nowdate.getHours()+"";            	            
//        	request.setAttribute("to_hour", to_hour);
//        }
//        String starttime = from_date1+" "+from_hour+":00:00";
//        String totime = to_date1+" "+to_hour+":59:59";            
//        int flag = 0;
//        try {
//        	User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
//			String bids = operator.getBusinessids();
//			String bid[] = bids.split(",");
//			Vector rbids = new Vector();
//			if(bid != null && bid.length>0){
//				for(int i=0;i<bid.length;i++){
//					if(bid[i] != null && bid[i].trim().length()>0)
//						rbids.add(bid[i].trim());
//				}
//			}
//			configdao = new WebConfigDao();
//			try{
//				urllist = configdao.getWebByBID(rbids);
//			}catch(Exception e){
//				
//			}
//			
//        	Integer queryid = getParaIntValue("id");//.getUrl_id();
//
//        	if(urllist.size()>0&&queryid==null){
//        		Object obj = urllist.get(0);           		
//        	}
//        	if(queryid!=null){					
//        		//如果是链接过来则取用查询条件
//        		configdao = new WebConfigDao();
//        		initconf = (WebConfig)configdao.findByID(queryid+"");
//        	}
//        	queryid = initconf.getId();
//        	 conn_name=queryid+"urlmonitor-conn";
//             valid_name =queryid+"urlmonitor-valid";
//             fresh_name =queryid+"urlmonitor-refresh";
//             wave_name = queryid+"urlmonitor-rec";
//             delay_name = queryid+"urlmonitor-delay";
//        	
//             List urlList = realtimedao.getByUrlId(queryid);
//             
//        	Calendar last = null;
//        	if(urlList != null && urlList.size()>0){
//        		last = ((Urlmonitor_realtime)urlList.get(0)).getMon_time();
//        	}
//        	int interval = 0;
//        	try {
//    			List numList = new ArrayList();
//    			TaskXml taskxml = new TaskXml();
//    			numList = taskxml.ListXml();
//    			for (int i = 0; i < numList.size(); i++) {
//    				Task task = new Task();
//    				BeanUtils.copyProperties(task, numList.get(i));
//    				if (task.getTaskname().equals("urltask")){
//    					interval = task.getPolltime().intValue();
//    				}
//    			}
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//        	DateE de=new DateE();
//        	if (last == null){
//        		last = new GregorianCalendar();
//        		flag=1;
//        	}
//        	lasttime = de.getDateDetail(last);
//        	last.add(Calendar.MINUTE,interval);
//        	nexttime = de.getDateDetail(last);
//        	
//        	int hour=1;
//        	if(getParaValue("hour")!=null){
//        		 hour = Integer.parseInt(getParaValue("hour"));
//        	}else{
//        		request.setAttribute("hour", "1");
//        	}
//        	
//        	InitCoordinate initer=new InitCoordinate(new GregorianCalendar(),hour,1);
//        	TimeSeries ss1 = new TimeSeries("", Minute.class); 
//        	TimeSeries ss2 = new TimeSeries("", Minute.class); 
//        	
//        	TimeSeries[] s = new TimeSeries[1];
//        	TimeSeries[] s_ = new TimeSeries[1];
//        	Vector wave_v = historydao.getByUrlid(queryid, starttime, totime,0);
//        	
//        	for(int i=0;i<wave_v.size();i++){
//        		Hashtable ht = (Hashtable)wave_v.get(i);
//        		double conn = Double.parseDouble(ht.get("conn").toString());
//        		double fresh = Double.parseDouble(ht.get("refresh").toString());
//        		double condelay = Double.parseDouble(ht.get("condelay").toString());
//        		String time = ht.get("mon_time").toString();
//          		ss1.addOrUpdate(new Minute(sdf1.parse(time)),conn);
//          		ss2.addOrUpdate(new Minute(sdf1.parse(time)),condelay);
//        	}
//        	s[0] = ss1;
//        	s_[0] = ss2;
//        	ChartGraph cg = new ChartGraph();
//        	cg.timewave(s,"时间","连通","",wave_name,600,120);
//        	cg.timewave(s_,"时间","时延(ms)","",delay_name,600,120);
//        	
//        	//是否连通
//        	String conn[] = new String[2];
//        	if (flag == 0)
//        		conn = historydao.getAvailability(queryid,starttime,totime,"is_canconnected");
//        	else{
//        		conn = historydao.getAvailability(queryid,starttime,totime,"is_canconnected");
//        	}
//        	String[] key1 = {"连通","未连通"};
//        	drawPiechart(key1,conn,"",conn_name);
//
//        	if (flag == 0)
//        	connrate = getF(String.valueOf(Float.parseFloat(conn[0])/(Float.parseFloat(conn[0])+Float.parseFloat(conn[1]))*100))+"%";
//        	//是否有效
//        	String avail[] = new String[2];
//        	if (flag == 0)
//        		avail = historydao.getAvailability(queryid,initer,"is_valid");
//        	else{
//        		avail[0] = "0";
//        		avail[1] = "0";
//        	}
//        	String[] key2 = {"有效","无效"};
//        	drawPiechart(key2,avail,"页面有效情况",valid_name);
//        	if (flag == 0)
//        	validrate = getF(String.valueOf(Float.parseFloat(avail[0])/(Float.parseFloat(avail[0])+Float.parseFloat(avail[1]))*100))+"%";
//
//        	//            	是否刷新
//        	String refresh[] = new String[2];
//        	if (flag == 0)
//        	refresh = historydao.getAvailability(queryid,initer,"is_refresh");
//        	else{
//        		refresh[0] = "0";
//        		refresh[1] = "0";
//        	}
//      
//        	String[] key3 = {"刷新","未刷新"};
//        	drawPiechart(key3,refresh,"页面刷新情况",fresh_name);
//        	if (flag == 0)
//        	freshrate = getF(String.valueOf(Float.parseFloat(refresh[0])/(Float.parseFloat(refresh[0])+Float.parseFloat(refresh[1]))*100))+"%";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        request.setAttribute("urllist",urllist);
//        request.setAttribute("initconf",initconf);
//        request.setAttribute("lasttime",lasttime);
//        request.setAttribute("nexttime",nexttime);
//        request.setAttribute("conn_name",conn_name);
//        request.setAttribute("valid_name",valid_name);
//        request.setAttribute("fresh_name",fresh_name);
//        request.setAttribute("wave_name",wave_name);
//        request.setAttribute("delay_name",delay_name);
//        request.setAttribute("connrate",connrate);
//        request.setAttribute("validrate",validrate);
//        request.setAttribute("freshrate",freshrate);
//        
//        request.setAttribute("from_date1",from_date1);
//        request.setAttribute("to_date1",to_date1);
//        
//        request.setAttribute("from_hour",from_hour);
//        request.setAttribute("to_hour",to_hour);
		return "/application/web/detail.jsp";
    }
	
	public String execute(String action) 
	{	
        if(action.equals("list"))
            return list();    
        if(action.equals("ready_add"))
        	return ready_add();
        if(action.equals("add"))
        	return add();
        if(action.equals("delete"))
            return delete();
        if(action.equals("ready_edit"))
        	return ready_edit();
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
        if(action.equals("todaylist"))
            return todayList();
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