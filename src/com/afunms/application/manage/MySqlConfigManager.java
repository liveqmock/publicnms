package com.afunms.application.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.MySqlSpaceConfigDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.MySqlSpaceConfig;
import com.afunms.application.util.DBPool;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.loader.DBLoader;
import com.afunms.system.model.User;
import com.afunms.topology.util.KeyGenerator;

public class MySqlConfigManager extends BaseManager implements ManagerInterface{ 
	
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
			typevo = (DBTypeVo)typedao.findByDbtype("mysql");
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
		MySqlSpaceConfigDao configdao = new MySqlSpaceConfigDao();
		setTarget("/application/db/mysqlconfiglist.jsp");
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
        DBLoader loader = new DBLoader();
        loader.loadOne(vo);
        loader.close();
        
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
        PollingEngine.getInstance().deleteDbByID(nodeId);
        DBPool.getInstance().removeConnect(nodeId);
        
        return "/db.do?action=list";
	}
	
	private String update()
    {    	   
		MySqlSpaceConfig vo = new MySqlSpaceConfig();
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
        	MySqlSpaceConfigDao configdao = new MySqlSpaceConfigDao();
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
				typevo = (DBTypeVo)typedao.findByDbtype("mysql");
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
			
			
			//configdao = new MySqlSpaceConfigDao();
			//configdao.fromLastToOraspaceconfig();			
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new MySqlSpaceConfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					configdao = new MySqlSpaceConfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
				}
			}else{
				configdao = new MySqlSpaceConfigDao();
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
					MySqlSpaceConfig oraspaceconfig = (MySqlSpaceConfig)list.get(k);
					if(ips.contains(oraspaceconfig.getIpaddress()))conflist.add(oraspaceconfig);
				}
			}
        }catch(Exception e){
        	e.printStackTrace();
        }
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
		request.setAttribute("list",list);
		return list();
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
				typevo = (DBTypeVo)typedao.findByDbtype("mysql");
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
			
			
			MySqlSpaceConfigDao configdao = new MySqlSpaceConfigDao();
			try{
				configdao.fromLastToMySqlSpaceConfig();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
			ipaddress = (String)session.getAttribute("ipaddress");			
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new MySqlSpaceConfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					configdao = new MySqlSpaceConfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
				}
			}else{
				configdao = new MySqlSpaceConfigDao();
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
					MySqlSpaceConfig oraspaceconfig = (MySqlSpaceConfig)list.get(k);
					if(ips.contains(oraspaceconfig.getIpaddress()))conflist.add(oraspaceconfig);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("Oraspaceconfiglist", conflist);
		request.setAttribute("iplist",ips);
		MySqlSpaceConfigDao configdao = new MySqlSpaceConfigDao();	
		setTarget("/application/db/mysqlconfiglist.jsp");
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
				typevo = (DBTypeVo)typedao.findByDbtype("mysql");
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
			
			
				
			if (ipaddress != null && ipaddress.trim().length()>0){
				MySqlSpaceConfigDao configdao = new MySqlSpaceConfigDao();	
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					configdao = new MySqlSpaceConfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
				}
			}else{
				MySqlSpaceConfigDao configdao = new MySqlSpaceConfigDao();
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
					MySqlSpaceConfig oraspaceconfig = (MySqlSpaceConfig)list.get(k);
					if(ips.contains(oraspaceconfig.getIpaddress()))conflist.add(oraspaceconfig);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
		MySqlSpaceConfigDao configdao = new MySqlSpaceConfigDao();
		try{
			list = configdao.getByIp(ipaddress);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		request.setAttribute("list",list);
		return "/application/db/mysqlconfigsearchlist.jsp";
    }
	
	private String addalert()
    {    
		MySqlSpaceConfig vo = new MySqlSpaceConfig();
		
		DBVo dbvo = new DBVo();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			MySqlSpaceConfigDao configdao = new MySqlSpaceConfigDao();
			try{
				vo = (MySqlSpaceConfig)configdao.findByID(getParaValue("id"));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			ipaddress = vo.getIpaddress();
			vo.setSms(1);
			configdao = new MySqlSpaceConfigDao();
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
				typevo = (DBTypeVo)typedao.findByDbtype("mysql");
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
			
			
			//configdao = new MySqlSpaceConfigDao();		
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new MySqlSpaceConfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					configdao = new MySqlSpaceConfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
				}
			}else{
				configdao = new MySqlSpaceConfigDao();
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
					MySqlSpaceConfig oraspaceconfig = (MySqlSpaceConfig)list.get(k);
					if(ips.contains(oraspaceconfig.getIpaddress()))conflist.add(oraspaceconfig);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("Oraspaceconfiglist", conflist);
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
		request.setAttribute("list",list);
		return "/application/db/mysqlconfigsearchlist.jsp";
    }
	
	private String cancelalert()
    {    
		MySqlSpaceConfig vo = new MySqlSpaceConfig();
		
		DBVo dbvo = new DBVo();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			MySqlSpaceConfigDao configdao = new MySqlSpaceConfigDao();
			try{
				vo = (MySqlSpaceConfig)configdao.findByID(getParaValue("id"));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			ipaddress = vo.getIpaddress();
			vo.setSms(0);
			configdao = new MySqlSpaceConfigDao();
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
				typevo = (DBTypeVo)typedao.findByDbtype("mysql");
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
			
			
			//configdao = new MySqlSpaceConfigDao();
			//configdao.fromLastToOraspaceconfig();			
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new MySqlSpaceConfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					configdao = new MySqlSpaceConfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
				}
			}else{
				configdao = new MySqlSpaceConfigDao();
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
					MySqlSpaceConfig oraspaceconfig = (MySqlSpaceConfig)list.get(k);
					if(ips.contains(oraspaceconfig.getIpaddress()))conflist.add(oraspaceconfig);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
		request.setAttribute("list",list);
		return "/application/db/mysqlconfigsearchlist.jsp";
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
        {	  
 		    DaoInterface dao = new MySqlSpaceConfigDao();
    	    setTarget("/application/db/mysqlconfigedit.jsp");
            return readyEdit(dao);
        }
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
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}
