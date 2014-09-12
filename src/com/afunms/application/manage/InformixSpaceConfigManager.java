/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.DominoConfigDao;
import com.afunms.application.dao.InformixspaceconfigDao;
import com.afunms.application.dao.OraspaceconfigDao;
import com.afunms.application.dao.SqldbconfigDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Informixspaceconfig;
import com.afunms.application.model.Oraspaceconfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.util.KeyGenerator;

public class InformixSpaceConfigManager extends BaseManager implements ManagerInterface
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
			typevo = (DBTypeVo)typedao.findByDbtype("informix");
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
		InformixspaceconfigDao configdao = new InformixspaceconfigDao();	
		setTarget("/application/db/informixspaceconfiglist.jsp");
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
//		String id = getParaValue("radio"); 
//		DBDao dao = new DBDao();
//		dao.delete(id);		
//		int nodeId = Integer.parseInt(id);
//        PollingEngine.getInstance().deleteNodeByID(nodeId);
//        DBPool.getInstance().removeConnect(nodeId);
//        
        return "/db.do?action=list";
	}
	/*
	 * 修改INFORMIX的表空间告警配置
	 * auth@hukelei
	 * date@2010-01-20
	 */
	private String update()
    {    	   
		Informixspaceconfig vo = new Informixspaceconfig();	
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";		
		
    	vo.setId(getParaIntValue("id"));
        vo.setIpaddress(getParaValue("ipaddress"));
        vo.setSpacename(getParaValue("spacename"));
        vo.setLinkuse(getParaValue("linkuse"));
        vo.setAlarmvalue(getParaIntValue("alarmvalue"));
        vo.setBak(getParaValue("bak"));
        vo.setReportflag(getParaIntValue("reportflag"));
        vo.setSms(getParaIntValue("sms"));
        
        try{
        	InformixspaceconfigDao configdao = new InformixspaceconfigDao();
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
			
			
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByDbtype("informix");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			DBDao dao = new DBDao();
			List dblist = new ArrayList();
			try{
				dblist = dao.getDbByTypeAndIpaddress(typevo.getId(), ipaddress);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			List informixList = new ArrayList();
			
			dao = new DBDao();
			try{
				informixList = dao.getDbByTypeAndBID(typevo.getId(), rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
			if(informixList != null && informixList.size()>0){
				for(int i=0;i<informixList.size();i++){
					DBVo dbmonitorlist = (DBVo)informixList.get(i);
					ips.add(dbmonitorlist.getIpAddress());
				}
			}
			
			
			configdao = new InformixspaceconfigDao();
			try{
				configdao.fromLastToInformixspaceconfig();			
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new InformixspaceconfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					list = configdao.loadAll();
				}
			}else{
				configdao = new InformixspaceconfigDao();
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
					Informixspaceconfig infromixspaceconfig = (Informixspaceconfig)list.get(k);
					if(ips.contains(infromixspaceconfig.getIpaddress()))conflist.add(infromixspaceconfig);
				}
			}
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        String showNodeList = getParaValue("showNodeList");
        if ("showNodeList".equals(showNodeList)) {
            return showNodeList();
        }
        request.setAttribute("Informixspaceconfiglist", conflist);
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
		request.setAttribute("list",list);
        
        
		return "/application/db/informixspaceconfigsearchlist.jsp";
    }
	
	private String cancelmanage()
    {    	   
//		DBVo vo = new DBVo();
//		DBDao dao = new DBDao();
//		vo = (DBVo)dao.findByID(getParaValue("id"));
//		vo.setManaged(0);
//        dao = new DBDao();
//        dao.update(vo);	    
        return "/db.do?action=list";
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
			//获取数据库类型
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByDbtype("informix");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			DBDao dao = new DBDao();
			List dblist = new ArrayList();
			try{
				dblist = dao.getDbByTypeAndIpaddress(typevo.getId(), ipaddress);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
//        	if(dblist != null && dblist.size()>0)
//        	vo = (DBVo)dblist.get(0);
			List informixList = new ArrayList();
			dao = new DBDao();
			try{
				informixList = dao.getDbByTypeAndBID(typevo.getId(), rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
			if(informixList != null && informixList.size()>0){
				for(int i=0;i<informixList.size();i++){
					DBVo dbmonitorlist = (DBVo)informixList.get(i);
					ips.add(dbmonitorlist.getIpAddress());
				}
			}
			
			
			InformixspaceconfigDao configdao = new InformixspaceconfigDao();
			try{
				configdao.fromLastToInformixspaceconfig();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
			ipaddress = (String)session.getAttribute("ipaddress");			
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new InformixspaceconfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					try{
						list = configdao.loadAll();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
				}
			}else{
				configdao = new InformixspaceconfigDao();
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
					Informixspaceconfig informixspaceconfig = (Informixspaceconfig)list.get(k);
					if(ips.contains(informixspaceconfig.getIpaddress()))conflist.add(informixspaceconfig);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		String showNodeList = getParaValue("showNodeList");
        if ("showNodeList".equals(showNodeList)) {
            return showNodeList();
        }
		request.setAttribute("Informixspaceconfiglist", conflist);
		request.setAttribute("iplist",ips);
		InformixspaceconfigDao configdao = new InformixspaceconfigDao();	
		setTarget("/application/db/informixspaceconfiglist.jsp");
		return list(configdao);
    }
	
	/*
	 * 依据IP进行告警查询
	 * auth@hukelei
	 * date 2010-01-21
	 */
	private String search()
    {  
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
			
			//获取数据库类型
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByDbtype("informix");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			DBDao dao = new DBDao();
			List dblist = new ArrayList();
			try{
				dblist = dao.getDbByTypeAndIpaddress(typevo.getId(), ipaddress);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}

			List informixList = new ArrayList();
			
			dao = new DBDao();
			try{
				informixList = dao.getDbByTypeAndBID(typevo.getId(), rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
			if(informixList != null && informixList.size()>0){
				for(int i=0;i<informixList.size();i++){
					DBVo dbmonitorlist = (DBVo)informixList.get(i);
					ips.add(dbmonitorlist.getIpAddress());
				}
			}
			
			
			InformixspaceconfigDao configdao = new InformixspaceconfigDao();
			try{
				configdao.fromLastToInformixspaceconfig();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
			//ipaddress = (String)session.getAttribute("ipaddress");			
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new InformixspaceconfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					configdao = new InformixspaceconfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
				}
			}else{
				configdao = new InformixspaceconfigDao();
				try{
					list = configdao.loadAll();		
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
		request.setAttribute("list",list);
		return "/application/db/informixspaceconfigsearchlist.jsp";
    }
	
	private String addalert()
    {    
		Informixspaceconfig vo = new Informixspaceconfig();
		
		DBVo dbvo = new DBVo();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			InformixspaceconfigDao configdao = new InformixspaceconfigDao();
			try{
				vo = (Informixspaceconfig)configdao.findByID(getParaValue("id"));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
			ipaddress = vo.getIpaddress();
			vo.setSms(1);
			
			configdao = new InformixspaceconfigDao();
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
			
			//获取数据库类型
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByDbtype("informix");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			DBDao dao = new DBDao();
			List dblist = new ArrayList();
			try{
				dblist = dao.getDbByTypeAndIpaddress(typevo.getId(), ipaddress);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
        	if(dblist != null && dblist.size()>0)
        	dbvo = (DBVo)dblist.get(0);
			List informixList = new ArrayList();
			dao = new DBDao();
			try{
				informixList = dao.getDbByTypeAndBID(typevo.getId(), rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
			if(informixList != null && informixList.size()>0){
				for(int i=0;i<informixList.size();i++){
					DBVo dbmonitorlist = (DBVo)informixList.get(i);
					ips.add(dbmonitorlist.getIpAddress());
				}
			}
			
			
			configdao = new InformixspaceconfigDao();
			try{
				configdao.fromLastToInformixspaceconfig();			
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new InformixspaceconfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					configdao = new InformixspaceconfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
				}
			}else{
				configdao = new InformixspaceconfigDao();
				try{
					list = configdao.loadAll();		
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		String showNodeList = getParaValue("showNodeList");
        if ("showNodeList".equals(showNodeList)) {
            return showNodeList();
        }
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
		request.setAttribute("list",list);
		return "/application/db/informixspaceconfigsearchlist.jsp";
    }
	
	private String cancelalert()
    {    
		Informixspaceconfig vo = new Informixspaceconfig();
		
		DBVo dbvo = new DBVo();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			InformixspaceconfigDao configdao = new InformixspaceconfigDao();
			try{
				vo = (Informixspaceconfig)configdao.findByID(getParaValue("id"));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
			ipaddress = vo.getIpaddress();
			vo.setSms(0);
			configdao = new InformixspaceconfigDao();
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
			
			//获取数据库类型
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try{
				typevo = (DBTypeVo)typedao.findByDbtype("informix");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				typedao.close();
			}
			
			DBDao dao = new DBDao();
			List dblist = new ArrayList();
			try{
				dblist = dao.getDbByTypeAndIpaddress(typevo.getId(), ipaddress);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
        	if(dblist != null && dblist.size()>0)
        	dbvo = (DBVo)dblist.get(0);
			List informixList = new ArrayList();
			
			dao = new DBDao();
			try{
				informixList = dao.getDbByTypeAndBID(typevo.getId(), rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
			if(informixList != null && informixList.size()>0){
				for(int i=0;i<informixList.size();i++){
					DBVo dbmonitorlist = (DBVo)informixList.get(i);
					ips.add(dbmonitorlist.getIpAddress());
				}
			}
			
			
			configdao = new InformixspaceconfigDao();
			try{
				configdao.fromLastToInformixspaceconfig();			
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			if (ipaddress != null && ipaddress.trim().length()>0){
				configdao = new InformixspaceconfigDao();
				try{
					list =configdao.getByIp(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
				if (list == null || list.size() == 0){
					configdao = new InformixspaceconfigDao();
					try{
						list = configdao.loadAll();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						configdao.close();
					}
				}
			}else{
				configdao = new InformixspaceconfigDao();
				try{
					list = configdao.loadAll();		
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					configdao.close();
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		String showNodeList = getParaValue("showNodeList");
        if ("showNodeList".equals(showNodeList)) {
            return showNodeList();
        }
		request.setAttribute("iplist",ips);
		request.setAttribute("ipaddress",ipaddress);
		request.setAttribute("list",list);
		return "/application/db/informixspaceconfigsearchlist.jsp";
    }
	
	/**
	 * @author hukelei add for 
	 * @since 2010-01-21
	 * @return
	 */
	private String ready_edit(){
		String jsp = "/application/db/informixspaceconfigedit.jsp";
		InformixspaceconfigDao dao = new InformixspaceconfigDao();
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
       
       InformixspaceconfigDao dao = new InformixspaceconfigDao();
       List list = null;
       try {
           list = dao.getByIp(ipaddress);
       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           dao.close();
       }

       request.setAttribute("ipaddress", ipaddress);// ipaddress
       request.setAttribute("sid", nodeid);
       request.setAttribute("list", list);
       request.setAttribute("showNodeList", showNodeList);
       return"/application/db/informixspaceconfignodelist.jsp";
   }

    public String execute(String action) 
	{	
        if(action.equals("list"))
            return list();    
        if(action.equals("ready_add"))
        	return "/application/db/add.jsp";
        if(action.equals("add"))
        	return add();
        if(action.equals("ready_edit"))
        	return ready_edit();
        if(action.equals("update"))
            return update();
        if(action.equals("cancelmanage"))
            return cancelmanage();
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