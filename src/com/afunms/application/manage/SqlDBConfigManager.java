/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.util.*;
import java.text.*;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.common.base.*;
import com.afunms.common.*;
import com.afunms.common.util.*;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.event.dao.EventListDao;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.loader.*;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Sqldbconfig;
import com.afunms.system.model.User;
import com.afunms.topology.util.*;
import com.afunms.application.dao.*;
import com.afunms.application.util.DBPool;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.polling.impl.*;
import com.afunms.polling.api.*;

import com.afunms.report.jfree.ChartCreator;

import org.jfree.data.general.DefaultPieDataset;

public class SqlDBConfigManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		List ips = new ArrayList();
		User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		String bids = operator.getBusinessids();
		DBVo vo = new DBVo();
		
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
		DBTypeVo typevo = null;
		try{
			typevo = (DBTypeVo)typedao.findByDbtype("sqlserver");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			typedao.close();
		}
		
		
		DBDao dao = new DBDao();
		try{
			oraList = dao.getDbByTypeAndBID(typevo.getId(),rbids);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
		if(oraList != null && oraList.size()>0){
			for(int i=0;i<oraList.size();i++){
				DBVo dbmonitorlist = (DBVo)oraList.get(i);
				ips.add(dbmonitorlist.getIpAddress());
			}
		}
		request.setAttribute("iplist",ips);
		SqldbconfigDao configdao = new SqldbconfigDao();	
		setTarget("/application/db/sqldbconfiglist.jsp");
		return list(configdao);
	}

	private String add()
    {    	   
		DBVo vo = new DBVo();
    	vo.setId(KeyGenerator.getInstance().getNextKey());
    	vo.setUser(getParaValue("user"));
    	vo.setPassword(getParaValue("password"));        
        vo.setAlias(getParaValue("alias"));
        vo.setIpAddress(getParaValue("ip_address"));
        vo.setPort(getParaValue("port"));
        vo.setDbName(getParaValue("db_name"));
        vo.setCategory(getParaIntValue("category"));
        vo.setDbuse(getParaValue("dbuse"));
        vo.setSendmobiles(getParaValue("sendmobiles"));
        vo.setSendemail(getParaValue("sendemail"));
        String allbid = "";
        String[] businessids = getParaArrayValue("checkbox");
        if(businessids != null && businessids.length>0){
        	for(int i=0;i<businessids.length;i++){
        		
        		String bid = businessids[i];
        		allbid= allbid+bid+",";
        	}
        } 
        vo.setBid(allbid);
        vo.setManaged(getParaIntValue("managed"));
        vo.setDbtype(getParaIntValue("dbtype"));
        //在数据库里增加被监控指标
        //DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
        //dcDao.addDBMonitor(vo.getId(),vo.getIpAddress(),"mysql");
        
        //在轮询线程中增加被监视节点
        //DBLoader loader = new DBLoader();
        //loader.loadOne(vo);
        //loader.close();
        
        DBDao dao = new DBDao();
        try{
        	dao.save(vo);	    
        }catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
        return "/db.do?action=list";
    }    
	
	public String delete()
	{
		String id = getParaValue("radio"); 
		DBDao dao = new DBDao();
		try{
			dao.delete(id);		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		int nodeId = Integer.parseInt(id);
        PollingEngine.getInstance().deleteNodeByID(nodeId);
        DBPool.getInstance().removeConnect(nodeId);
        
        return "/db.do?action=list";
	}
	
	private String update()
    {    	   
		Sqldbconfig vo = new Sqldbconfig();
		DBVo dbvo = new DBVo();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";		
		
    	vo.setId(getParaIntValue("id"));
        vo.setIpaddress(getParaValue("ipaddress"));
        vo.setDbname(getParaValue("dbname"));
        vo.setLinkuse(getParaValue("linkuse"));
        vo.setAlarmvalue(getParaIntValue("alarmvalue"));
        vo.setLogflag(getParaIntValue("logflag"));
        vo.setBak(getParaValue("bak"));
        vo.setReportflag(getParaIntValue("reportflag"));
        vo.setSms(getParaIntValue("sms"));
        
        try{
        	SqldbconfigDao configdao = new SqldbconfigDao();
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

			List oraList = new ArrayList();
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByDbtype("sqlserver");
			}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			typedao.close();
    		}
			
			DBDao dao = new DBDao();
			try{
				oraList = dao.getDbByTypeAndBID(typevo.getId(),rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
			if(oraList != null && oraList.size()>0){
				for(int i=0;i<oraList.size();i++){
					DBVo dbmonitorlist = (DBVo)oraList.get(i);
					ips.add(dbmonitorlist.getIpAddress());
				}
			}
			
			
			//configdao = new SqldbconfigDao();
			//configdao.fromLastToOraspaceconfig();			
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new SqldbconfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
	    			e.printStackTrace();
	    		}finally{
	    			configdao.close();
	    		}
				if (list == null || list.size() == 0){
					configdao = new SqldbconfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
		    			e.printStackTrace();
		    		}finally{
		    			configdao.close();
		    		}
				}
			}else{
				configdao = new SqldbconfigDao();
				try{
					list = configdao.loadAll();		
				}catch(Exception e){
	    			e.printStackTrace();
	    		}finally{
	    			configdao.close();
	    		}
			}
			
//			if(list != null && list.size()>0){
//				for(int k=0;k<list.size();k++){
//					Sqldbconfig oraspaceconfig = (Sqldbconfig)list.get(k);
//					if(ips.contains(oraspaceconfig.getIpaddress()))conflist.add(oraspaceconfig);
//				}
//			}
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        String showNodeList = getParaValue("showNodeList");
        if ("showNodeList".equals(showNodeList)) {
            return showNodeList();
        }
        
        //request.setAttribute("Oraspaceconfiglist", conflist);
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
		request.setAttribute("list",list);
        
        
		return "/application/db/sqldbconfigsearchlist.jsp";
    }

	
	private String createSpaceConfig()
    {    	   
		DBVo vo = new DBVo();
		
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";

		try{
			ipaddress = getParaValue("ip");
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
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByDbtype("sqlserver");
			}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			typedao.close();
    		}
			
			DBDao dao = new DBDao();
			try{
				oraList = dao.getDbByTypeAndBID(typevo.getId(),rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
			if(oraList != null && oraList.size()>0){
				for(int i=0;i<oraList.size();i++){
					DBVo dbmonitorlist = (DBVo)oraList.get(i);
					ips.add(dbmonitorlist.getIpAddress());
				}
			}
			
			
			SqldbconfigDao configdao = new SqldbconfigDao();
			try{
				configdao.fromLastToSqldbconfig();
			}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			configdao.close();
    		}
			
			ipaddress = (String)session.getAttribute("ipaddress");			
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new SqldbconfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
	    			e.printStackTrace();
	    		}finally{
	    			configdao.close();
	    		}
				if (list == null || list.size() == 0){
					configdao = new SqldbconfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
		    			e.printStackTrace();
		    		}finally{
		    			configdao.close();
		    		}
				}
			}else{
				configdao = new SqldbconfigDao();
				try{
					list = configdao.loadAll();		
				}catch(Exception e){
	    			e.printStackTrace();
	    		}finally{
	    			configdao.close();
	    		}
			}
			
			if(list != null && list.size()>0){
				for(int k=0;k<list.size();k++){
					Sqldbconfig oraspaceconfig = (Sqldbconfig)list.get(k);
					if(ips.contains(oraspaceconfig.getIpaddress()))conflist.add(oraspaceconfig);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		String showNodeList = getParaValue("showNodeList");
        if ("showNodeList".equals(showNodeList)) {
            return showNodeList();
        }
		request.setAttribute("Oraspaceconfiglist", conflist);
		request.setAttribute("iplist",ips);
		SqldbconfigDao configdao = new SqldbconfigDao();	
		setTarget("/application/db/sqldbconfiglist.jsp");
		return list(configdao);
    }
	
	private String search()
    {    	   
		DBVo vo = new DBVo();
		
		
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
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByDbtype("sqlserver");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			DBDao dao = new DBDao();
			try{
				oraList = dao.getDbByTypeAndBID(typevo.getId(),rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
			if(oraList != null && oraList.size()>0){
				for(int i=0;i<oraList.size();i++){
					DBVo dbmonitorlist = (DBVo)oraList.get(i);
					ips.add(dbmonitorlist.getIpAddress());
				}
			}
			
			
			SqldbconfigDao configdao = new SqldbconfigDao();			
			if (ipaddress != null && ipaddress.trim().length()>0){
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					configdao = new SqldbconfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
				}
			}else{
				try{
					list = configdao.loadAll();		
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
			}
			
//			if(list != null && list.size()>0){
//				for(int k=0;k<list.size();k++){
//					Sqldbconfig oraspaceconfig = (Sqldbconfig)list.get(k);
//					if(ips.contains(oraspaceconfig.getIpaddress()))conflist.add(oraspaceconfig);
//				}
//			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//request.setAttribute("Oraspaceconfiglist", conflist);
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
//		SqldbconfigDao configdao = new SqldbconfigDao();
//		try{
//			list = configdao.getByIp(ipaddress);
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			configdao.close();
//		}
		request.setAttribute("list",list);
		return "/application/db/sqldbconfigsearchlist.jsp";
    }
	
	private String addalert()
    {    
		Sqldbconfig vo = new Sqldbconfig();
		
		DBVo dbvo = new DBVo();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			SqldbconfigDao configdao = new SqldbconfigDao();
			try{
				vo = (Sqldbconfig)configdao.findByID(getParaValue("id"));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
			ipaddress = vo.getIpaddress();
			vo.setSms(1);
			configdao = new SqldbconfigDao();
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

			List oraList = new ArrayList();
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByDbtype("sqlserver");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			
			DBDao dao = new DBDao();
			try{
				oraList = dao.getDbByTypeAndBID(typevo.getId(),rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
			if(oraList != null && oraList.size()>0){
				for(int i=0;i<oraList.size();i++){
					DBVo dbmonitorlist = (DBVo)oraList.get(i);
					ips.add(dbmonitorlist.getIpAddress());
				}
			}
			
			
			//configdao = new SqldbconfigDao();
			//configdao.fromLastToSqldbconfig();			
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new SqldbconfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					configdao = new SqldbconfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
				}
			}else{
				configdao = new SqldbconfigDao();
				try{
					list = configdao.loadAll();		
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
			}
			
//			if(list != null && list.size()>0){
//				for(int k=0;k<list.size();k++){
//					Sqldbconfig oraspaceconfig = (Sqldbconfig)list.get(k);
//					if(ips.contains(oraspaceconfig.getIpaddress()))conflist.add(oraspaceconfig);
//				}
//			}
		}catch(Exception e){
			e.printStackTrace();
		}
		String showNodeList = getParaValue("showNodeList");
        if ("showNodeList".equals(showNodeList)) {
            return showNodeList();
        }
		//request.setAttribute("Oraspaceconfiglist", conflist);
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
		request.setAttribute("list",list);
		return "/application/db/sqldbconfigsearchlist.jsp";
    }
	
	private String cancelalert()
    {    
		Sqldbconfig vo = new Sqldbconfig();
		
		DBVo dbvo = new DBVo();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			SqldbconfigDao configdao = new SqldbconfigDao();
			try{
				vo = (Sqldbconfig)configdao.findByID(getParaValue("id"));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			ipaddress = vo.getIpaddress();
			vo.setSms(0);
			configdao = new SqldbconfigDao();
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

			List oraList = new ArrayList();
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByDbtype("sqlserver");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			DBDao dao = new DBDao();
			try{
				oraList = dao.getDbByTypeAndBID(typevo.getId(),rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
			if(oraList != null && oraList.size()>0){
				for(int i=0;i<oraList.size();i++){
					DBVo dbmonitorlist = (DBVo)oraList.get(i);
					ips.add(dbmonitorlist.getIpAddress());
				}
			}
			
			
			//configdao = new SqldbconfigDao();
			//configdao.fromLastToOraspaceconfig();			
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new SqldbconfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					configdao = new SqldbconfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
				}
			}else{
				configdao = new SqldbconfigDao();
				try{
					list = configdao.loadAll();		
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
			}
			
			if(list != null && list.size()>0){
				for(int k=0;k<list.size();k++){
					Sqldbconfig oraspaceconfig = (Sqldbconfig)list.get(k);
					if(ips.contains(oraspaceconfig.getIpaddress()))conflist.add(oraspaceconfig);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		String showNodeList = getParaValue("showNodeList");
        if ("showNodeList".equals(showNodeList)) {
            return showNodeList();
        }
		//request.setAttribute("Oraspaceconfiglist", conflist);
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
		request.setAttribute("list",list);
		return "/application/db/sqldbconfigsearchlist.jsp";
    }
	
	/**
	 * @author hukelei add for 
	 * @since 2010-01-21
	 * @return
	 */
	private String ready_edit(){
		String jsp = "/application/db/sqldbconfigedit.jsp";
		SqldbconfigDao dao = new SqldbconfigDao();
		try{
			setTarget(jsp);
			jsp = readyEdit(dao);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		String showNodeList = getParaValue("showNodeList");
		request.setAttribute("showNodeList", showNodeList);
	    return jsp;
	}

	/**
     * showNodeList:
     * <p>获取单个库的表空间阀值设置
     *
     * @return {@link String}
     *         - 返回单个库的表空间阀值设备
     *
     * @since   v1.01
     */
	public String showNodeList() {
	    String ipaddress = getParaValue("ipaddress");
        String nodeid = getParaValue("nodeid");
        String showNodeList = getParaValue("showNodeList");
        
        SqldbconfigDao configdao = new SqldbconfigDao();
        List list = null;
        try {
            list = configdao.getByIp(ipaddress);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            configdao.close();
        }

        request.setAttribute("ipaddress", ipaddress);// ipaddress
        request.setAttribute("sid", nodeid);
        request.setAttribute("list", list);
        request.setAttribute("showNodeList", showNodeList);
        return"/application/db/sqldbconfignodelist.jsp";
	}

	public String execute(String action) 
	{	
        if(action.equals("list"))
            return list();    
        if(action.equals("ready_add"))
        	return "/application/db/add.jsp";
        if(action.equals("add"))
        	return add();
        if(action.equals("delete"))
            return delete();
        if(action.equals("ready_edit"))
        	return ready_edit();
//        {	  
// 		    DaoInterface dao = new SqldbconfigDao();
//    	    setTarget("/application/db/sqldbconfigedit.jsp");
//            return readyEdit(dao);
//        }
        if(action.equals("update"))
            return update();
        if(action.equals("addalert"))
            return addalert();
        if(action.equals("cancelalert"))
            return cancelalert();
        if(action.equals("search"))
            return search();
        if(action.equals("createspaceconfig"))
            return createSpaceConfig();
        if ("showNodeList".equals(action)) {
            return showNodeList();
        }
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
}