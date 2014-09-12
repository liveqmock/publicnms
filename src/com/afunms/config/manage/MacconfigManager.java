package com.afunms.config.manage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import visio.ClassFactory;
import visio.IVDocument;
import visio.IVPage;
import visio.IVPages;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.Fileupload;
import com.afunms.config.dao.DistrictDao;
import com.afunms.config.dao.IpConfigDao;
import com.afunms.config.dao.MacconfigDao;
import com.afunms.config.model.DistrictConfig;
import com.afunms.config.model.IpConfig;
import com.afunms.config.model.Macconfig;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.util.ExcelUtil;



public class MacconfigManager  extends BaseManager implements ManagerInterface{

	  
	/**
	 * 查询所有的方法
	 * @return
	 */  
	private String list() {
		String where = getWhere();
		String jsp = "/config/macconfig/list.jsp";
		MacconfigDao dao = new MacconfigDao();		
		setTarget(jsp); 
		list(dao , where);
		
	    DistrictDao districtDao = new DistrictDao();
	    List districtList = null;
		try {
			districtList = districtDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    districtDao.close();
	    
	    Hashtable hashtable = new Hashtable();
	    
	    List list = (List)request.getAttribute("list");
	    if(list!=null&&list.size()>0){
	    	for(int i = 0 ; i < list.size() ; i++){
	    		Macconfig macconfig = (Macconfig)list.get(i);
	    		
	    		if(districtList!=null&&districtList.size()>0){
	    			for(int j = 0 ; j < districtList.size() ; j++){
	    				DistrictConfig districtConfig = (DistrictConfig)districtList.get(j);
	    				if(macconfig.getDiscrictid() == districtConfig.getId()){
	    					hashtable.put(macconfig.getId(), districtConfig);
	    				}
		    		}
	    		}
	    		
	    	}
	    }
	    request.setAttribute("hashtable", hashtable);
	    request.setAttribute("districtList", districtList);
        return jsp;
	}
	
	private String list1() {
		String where = getWhere();
		String jsp = "/config/macconfig/list1.jsp";
		IpConfigDao dao = new IpConfigDao();		
		setTarget(jsp); 
		list(dao , where);
		
	    DistrictDao districtDao = new DistrictDao();
	    List districtList = null;
		try {
			districtList = districtDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    districtDao.close();
	    
	    Hashtable hashtable = new Hashtable();
	    
	    List list = (List)request.getAttribute("list");
	    if(list!=null&&list.size()>0){
	    	for(int i = 0 ; i < list.size() ; i++){
	    		IpConfig ipconfig = (IpConfig)list.get(i);
	    		
	    		if(districtList!=null&&districtList.size()>0){
	    			for(int j = 0 ; j < districtList.size() ; j++){
							DistrictConfig districtConfig = (DistrictConfig)districtList.get(j);
							if(ipconfig.getDiscrictid() == districtConfig.getId()){
	    					hashtable.put(ipconfig.getId(), districtConfig);
	    				}
		    		}
	    		}
	    		
	    	}
	    }
	    request.setAttribute("hashtable", hashtable);
	    request.setAttribute("districtList", districtList);
        return jsp;
	}
	
	private String getWhere(){
		String condition = getParaValue("condition");
		String sql = "";
		
	 request.setAttribute("condition", condition);
		if(condition == null || condition.trim().length() == 0){
			return sql;
		}
		sql = sql + " where";
		if("mac".equals(condition)){
			sql = sql + getMacSql();
		}
		if("district".equals(condition)){
			sql = sql + getDistrictIdSql();
		}
		return sql;
	}
	
	private String getMacSql(){
		String sql = "";
		String searchMac = getParaValue("searchMac");
		if(searchMac==null || searchMac.trim().length() == 0){
			sql = " mac = mac";
		}else {
			sql = " mac='" + searchMac.trim()+"'";
		}
		request.setAttribute("searchMac", searchMac);
		return sql;
	}
	
	private String getDistrictIdSql(){
		String sql = "";
		String searchDistrictId = getParaValue("searchDistrictId");
		if(searchDistrictId==null || searchDistrictId.trim().length() == 0 || "-1".equals(searchDistrictId)){
			sql = " discrictid = discrictid";
			searchDistrictId = "-1";
		}else{
			sql = " discrictid = " + searchDistrictId;
		}
		request.setAttribute("searchDistrictId", searchDistrictId);
		return sql;
	}
	
	
	
    
	/**
	 * 删除方法
	 * @return
	 */
	public String delete()
	{
		String[] ids = getParaArrayValue("checkbox");
		MacconfigDao dao = new MacconfigDao();
		try
		{
			if(ids!=null && ids.length>0){
				dao.delete(ids);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			dao.close();
		}
			
        return "/macconfig.do?action=list";
	}
	
	public String delete1()
	{
	String[] ids = getParaArrayValue("checkbox");
	IpConfigDao dao = new IpConfigDao();
	try
	{
		if(ids!=null && ids.length>0){
			dao.delete(ids);
		}
		
	}catch(Exception e)
	{
		e.printStackTrace();
	}finally
	{
		dao.close();
	}
		
    return "/macconfig.do?action=list1";
	}

	
	private String ready_edit(){
		
		MacconfigDao dao = new MacconfigDao();
		BaseVo vo = null;
		try{
	    	   vo = dao.findByID(getParaValue("id"));       
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   dao.close();
	       }
	    request.setAttribute("vo",vo);
		return "/config/macconfig/edit.jsp";
	}
	
	private String ready_edit1() {
		IpConfigDao dao = new IpConfigDao();
		BaseVo vo = null;
		try {
			vo = dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("vo", vo);
		return "/config/macconfig/edit1.jsp";
	}
	
	/**
	 * 修改方法
	 * @return
	 */
	   private String update()
       {    	   
		   Macconfig vo=new Macconfig();
		vo.setId(getParaIntValue("id"));
    	vo.setMac(getParaValue("mac"));
    	vo.setMacdesc(getParaValue("macdesc")); 
    	vo.setDiscrictid(this.getParaIntValue("discrictid"));
    	MacconfigDao dao = new MacconfigDao();
        try
        {
        	
        	  dao.update(vo);	
        }catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally
        {
        	dao.close();
        }
          
        return "/macconfig.do?action=list";
    }
	   
	   private String update1() {
			IpConfig vo = new IpConfig();
			vo.setId(getParaIntValue("id"));
			vo.setIpaddress(getParaValue("mac"));
			vo.setIpdesc(getParaValue("macdesc"));
			vo.setDiscrictid(this.getParaIntValue("discrictid"));
			IpConfigDao dao = new IpConfigDao();
			try {

				dao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			return "/macconfig.do?action=list1";
	}
	   
	/**
	 * 添加方法
	 * @return
	 */
	public String add()
    {   
		
		Macconfig vo=new Macconfig();
		MacconfigDao dao = new MacconfigDao();
		
		
		vo.setId(getParaIntValue("id"));
    	vo.setMac(getParaValue("mac"));
    	vo.setDiscrictid(getParaIntValue("discrictid")); 
    	vo.setMacdesc(getParaValue("macdesc"));
    	
	        try{
	        	dao.save(vo);
	        }catch(Exception e){
	        	e.printStackTrace();
	        }finally{
	        	dao.close();
	        }
        return "/macconfig.do?action=list";
    }  
	
	public String add1()
    {   
		
		IpConfig vo=new IpConfig();
		IpConfigDao dao = new IpConfigDao();
		
		
		vo.setId(getParaIntValue("id"));
    		vo.setIpaddress(getParaValue("mac"));
    		vo.setDiscrictid(getParaIntValue("discrictid")); 
    		vo.setIpdesc(getParaValue("macdesc"));
    	
	        try{
	        	dao.save(vo);
	        }catch(Exception e){
	        	e.printStackTrace();
	        }finally{
	        	dao.close();
	        }
        		return "/macconfig.do?action=list1";
    }
	
	 public String execute(String action) {
		// TODO Auto-generated method stub
		 
		if(action.equals("list"))
		{
			return list();
		}
		if(action.equals("list1"))
		{
			return list1();
		}
		if(action.equals("add"))
		{
			return add();
		}
		if(action.equals("add1"))
		{
			return add1();
		}
		if(action.equals("delete"))
		{
			return delete();
		}
		if(action.equals("delete1"))
		{
			return delete1();
		}
	      if(action.equals("update"))
	      {
	            return update();
	      }
	      if (action.equals("update1")) {
				return update1();
			}
		if(action.equals("ready_edit"))
		{
            return ready_edit();
		}
		if (action.equals("ready_edit1")) {
			return ready_edit1();
		}
		if(action.equals("ready_add")){
			return "/config/macconfig/add.jsp";
		}
		if(action.equals("ready_add1")){
			return "/config/macconfig/add1.jsp";
		}
		if("toImportExcel".equals(action)){
			return toImportExcel();
		}
		if("importExcel".equals(action)){
			return importExcel();
		}
		if("exportExcel".equals(action)){
			return exportExcel();
		}
		if("importVisioTopo".equals(action)){
			return importVisioTopo();
		}
		
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	 private String importVisioTopo() {

			String flag = request.getParameter("flag");
			HostNode importVisioTopohostnode = new HostNode();
			List _list = new ArrayList();
			if ("1".equals(flag)) {

				String discrictid = "";
				String saveDirPath = ResourceCenter.getInstance().getSysPath()
						+ "WEB-INF/macConfig";

				String community = getParaValue("community");
				String writecommunity = getParaValue("writecommunity");
				int snmpversion = getParaIntValue("snmpversion");
				int collecttype = 0;
				try {
					collecttype = getParaIntValue("collecttype");
				} catch (Exception e) {

				}
				String bid = getParaValue("bid");

				importVisioTopohostnode.setCommunity(community);
				importVisioTopohostnode.setWriteCommunity(writecommunity);
				importVisioTopohostnode.setSnmpversion(snmpversion);
				importVisioTopohostnode.setCollecttype(collecttype);
				importVisioTopohostnode.setBid(bid);
				
				session.setAttribute("importVisioTopohostnode", importVisioTopohostnode);
				
				return "/tool/importvisiotopo_list.jsp";
			}
			if ("2".equals(flag)) {
				String discrictid = "";
				String saveDirPath = ResourceCenter.getInstance().getSysPath()+ "WEB-INF/macConfig";
				Fileupload fileupload = new Fileupload(saveDirPath);
				fileupload.doupload(request, 10000000);
				
				HostNode _importVisioTopohostnode = (HostNode)session.getAttribute("importVisioTopohostnode");

				List formFieldList = fileupload.getFormFieldList();

				if (null == formFieldList || formFieldList.size() == 0) {
					request.setAttribute("success", false);
				} else {
					for (int i = 0; i < formFieldList.size(); i++) {

						List formField = (List) formFieldList.get(i);
						String formFieldType = (String) formField.get(0);
						String formFieldName = (String) formField.get(1);
						String formFieldValue = (String) formField.get(2);

						if ("file".equals(formFieldType)) {
							if ("fileName".equals(formFieldName)) {
								visio.IVInvisibleApp app = ClassFactory
										.createInvisibleApp();
								try {
									IVDocument doc = app.documents().open(
											formFieldValue);
									IVPages pages = doc.pages();
									
									for (int index = 1; index <= pages.count(); index++) {
										IVPage page = pages.item(index);
									
										importOnePage(page);
									}
									request.setAttribute("success", true);
								} catch (Exception e) {
									e.printStackTrace();
									request.setAttribute("success", false);
								} finally {
									app.quit();
								}
							}
						}
					}
				}
				return "/tool/importvisiotopo_list.jsp";
			}
			return null;
		}

	 private void importOnePage(IVPage page) {
		    visio.IVShapes shapes = page.shapes();
		    visio.IVShape shape = null;
			if (shapes.count() > 0) {
				String pattern = "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])";
				Pattern p = Pattern.compile(pattern);
				Matcher m = null;
				Set<String> allDevIPs = new HashSet<String>();
				for (int i = 1; i <= shapes.count(); i++) {
					shape = shapes.item(i);
					m = p.matcher(shape.text());
					if (m.find()) {
						allDevIPs.add(m.group());
					}
				}
				
				visio.IVConnects connects = page.connects();
				//System.out.println(connects.count());

				if (connects.count() > 0) {
					List<String> list = new ArrayList<String>();
					for (int i = 1; i <= connects.count(); i++) {
						visio.IVConnect connect = connects.item(i);
						list.add(connect.fromSheet().text() + "_"
								+ connect.toSheet().text());
						if (list.size() == 2) {
							String startPoint = list.get(0);// 起点Gi1/1-Fa0/1_192.168.1.1
							String endPoint = list.get(1);// 终点
							// Gi1/1-Fa0/1_192.168.1.2
							String[] intIP1 = startPoint.split("_"); // Gi1/1-Fa0/1,
							// 192.168.1.1

							String intsStr1 = intIP1[0];// Gi1/1-Fa0/1
							String startIP = intIP1[1];// 192.168.1.1
							// startIP = Regex.Match(startIP, pattern).ToString();
							m = p.matcher(startIP);
							if (m.find()) {
								startIP = m.group();
							}
							if (startIP == "") {
								list.clear();
								continue;
							}

							String[] intIP2 = endPoint.split("_");// Gi1/1-Fa0/1,
							// 192.168.1.2
							String intsStr2 = intIP2[0];// Gi1/1-Fa0/1
							String endIP = intIP2[1];// 192.168.1.2
							// endIP = Regex.Match(endIP, pattern).ToString();
							m = p.matcher(endIP);
							if (m.find()) {
								endIP = m.group();
							}
							if (endIP == "") {
								list.clear();
								continue;
							}
							
							String startInt = "", endInt = "";

							if (intsStr1.equals(intsStr2))// 按理说 这个if是多余的
							{
								// MySQLCommand comm = null;
								// /接口连接信息拆分成两部分，分别转换成全拼
								String[] ints = intsStr1.split("-");
								startInt = ints[0];// Gi1/1(n)
								String startIndex = "", endIndex = "";//端口索引号
								String[] intstrIndex = split(startInt);
								
								startInt = intstrIndex[0];//parseIntface(startInt);
								startIndex = intstrIndex[1];
									
								endInt = ints[1];// Fa
//								endInt = parseIntface(endInt);
								intstrIndex = split(endInt);
								endInt = intstrIndex[0];//parseIntface(startInt);
								endIndex = intstrIndex[1];
								
								// /计算设备id
								HostNodeDao dao = new HostNodeDao();
								int startNodeID = 0, endNodeID = 0;
								startNodeID = dao.getNodeID(startIP);
								HostNode node = new HostNode();
								if (dao.getCountByIpaddress(startIP) == 0) {
									node.setId(startNodeID);
									node.setIpAddress(startIP);
									node.setSysName(startIP);
									node.setCommunity(startIP);
									node.setWriteCommunity(startIP);
									int _startIP = Integer.parseInt(startIP);
									node.setSnmpversion(_startIP);
									node.setBid(startIP);
									node.setCollecttype(_startIP);
									node.setAlias(startIP);
									dao.save(node);
								}
								dao = new HostNodeDao();
								endNodeID = dao.getNodeID(endIP);
								if (dao.getCountByIpaddress(endIP) == 0) {
									node.setId(endNodeID);
									node.setIpAddress(endIP);
									node.setSysName(endIP);
									node.setAlias(endIP);
									dao.save(node);
								}
								allDevIPs.remove(startIP);
								allDevIPs.remove(endIP);
								
								LinkDao linkDao = new LinkDao();
								boolean isLinkExists = linkDao.linkExists2(
										startNodeID, startInt, endNodeID, endInt);
								if (!isLinkExists) {
									Link vo = new Link();
									vo.setStartId(startNodeID);
									vo.setStartIp(startIP);
									vo.setStartDescr(startInt);
									vo.setStartIndex(startIndex);
									vo.setEndId(endNodeID);
									vo.setEndIp(endIP);
									vo.setEndDescr(endInt);
									vo.setEndIndex(endIndex);
									vo.setLinkName(startIP+"_"+startIndex+"/"+endIP+"_"+endIndex);
									linkDao.saveLinkOnly(vo);
								}
							}
							list.clear();
						}
					}
				} 
				if (allDevIPs.size()>0) {
					HostNode node = new HostNode();
					for (String ipaddress : allDevIPs) {
						HostNodeDao dao = new HostNodeDao();
						int nodeID = dao.getNodeID(ipaddress);
						if (null == dao.findByIpaddress(ipaddress)) {
							node.setId(nodeID);
							node.setIpAddress(ipaddress);
							node.setSysName(ipaddress);
							node.setAlias(ipaddress);
							dao.save(node);
						}
					}
				}
			}
		}


	
	private String toImportExcel(){
        DistrictDao districtDao=new DistrictDao();
	    List list = null;
	    try {
			list = districtDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			districtDao.close();
		}
		request.setAttribute("list", list);
		return "/config/macconfig/importexcel.jsp";
	}
    
    
    private String importExcel(){
        String discrictid = "";
        String saveDirPath = ResourceCenter.getInstance().getSysPath() + "WEB-INF/macConfig";
        Fileupload fileupload = new Fileupload(saveDirPath);
        fileupload.doupload(request);
        List formFieldList = fileupload.getFormFieldList();
        List allMacList = null;
        for(int i = 0 ; i < formFieldList.size() ; i++){
        	List formField = (List)formFieldList.get(i);
        	String formFieldType = (String)formField.get(0);
        	String formFieldName = (String)formField.get(1);
        	String formFieldValue = (String)formField.get(2);
        	if("file".equals(formFieldType)){
        		if("fileName".equals(formFieldName)){
        			List excellist = ExcelUtil.readExcel(new File(formFieldValue));
        			allMacList = getAllMacList(excellist);
        		}
        		
        	}else if("formField".equals(formFieldType)){
        		if("discrictid".equals(formFieldName)){
        			discrictid = formFieldValue;
        		}
        	}
        }
        MacconfigDao macconfigDao = new MacconfigDao();
        List allMacConfiglist = null;
        try {
        	allMacConfiglist = macconfigDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			macconfigDao.close();
		}
		if(allMacConfiglist==null){
			allMacConfiglist = new ArrayList();
		}
        List macConfigList = new ArrayList();
        if(allMacList!=null&&allMacList.size()>0){
        	for(int i = 0 ; i < allMacList.size(); i++){
        		String mac = (String)allMacList.get(i);
        		for(int j = 0 ; j < allMacConfiglist.size() ; j++){
        			Macconfig macConfigflag = new Macconfig();
        			macConfigflag = (Macconfig)allMacConfiglist.get(j);
            		if(mac.trim().equals(macConfigflag.getMac().trim())){
            			allMacConfiglist.remove(j);
            		}
        		}
    			Macconfig macConfig = new Macconfig();
        		macConfig.setMac(mac);
        		macConfig.setDiscrictid(Integer.parseInt(discrictid));
        		macConfig.setMacdesc("");
        		allMacConfiglist.add(macConfig);
        	}
        }
        macconfigDao = new MacconfigDao();
        
        try {
			macconfigDao.deleteAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			macconfigDao.close();
		}
		
		macconfigDao = new MacconfigDao();
		
		try {
			macconfigDao.saveBatch(allMacConfiglist);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			macconfigDao.close();
		}
        
		return list();
	}
    
    private String exportExcel(){
    	
    	String where = getWhere();
    	
    	System.out.println(where+"========wheres=======");
    	
		String jsp = "/config/macconfig/list.jsp";
		MacconfigDao dao = new MacconfigDao();		
		setTarget(jsp); 
		list(dao , where);
		
    	List list = (List)request.getAttribute("list");
    	
    	
    	AbstractionReport1 abstractionReport1 = new ExcelReport1(new IpResourceReport());
    	abstractionReport1.createReport_macconfiglist("temp/macconfiglist_report.xls", "", list);
    	request.setAttribute("filename", abstractionReport1.getFileName());

    	return "/topology/ipregional/download.jsp";
    }
    
    
    private List getAllMacList(List excellist){
    	List allMacList = new ArrayList();
    	if(excellist!=null&&excellist.size()>0){
    		for(int i = 0 ; i < excellist.size(); i++){
    			List numList = (List)excellist.get(i);
    			if(numList!=null && numList.size()>0){
    				for(int j = 0 ; j < numList.size(); j++){
    	    			List cellList = (List)numList.get(j);
    	    			if(cellList!=null && cellList.size()>0){
    	    				for(int k = 0 ; k < cellList.size(); k++){
    	    					String mac = (String)cellList.get(k);
    	    					if(mac!=null&&mac.trim().length()>0){
    	    						allMacList.add(mac);
    	    					}
    	    					
    	    				}
    	    			}
    	    		}
    			}
    		}
    	}
    	return allMacList;
    }
	 
	 
	 
//	  /**
//	    * 分页显示记录
//	    * targetJsp:目录jsp
//	    */
//	   protected String list(DaoInterface dao)
//	   {
//		   String targetJsp = null;
//		   	int perpage = getPerPagenum();
//	       List list = dao.listByPage(getCurrentPage(),perpage);
//	       if(list==null) return null;
//	       
//	       request.setAttribute("page",dao.getPage());
//	       request.setAttribute("list",list);
//	       targetJsp = getTarget(); 
//		   return targetJsp;
//	   }
    private String parseIntface(String startInt)
    {
        String prefix = "";
        String suffix = "";
        if (startInt.length() == 0)
            return "";
        else {
            for (int i = 0; i < startInt.length(); i++) {
                char c = startInt.charAt(i);
                if (c >= '0' && c <= '9')
                {
                    suffix = startInt.substring(i);
                    break;
                }
            }
            if (startInt.startsWith("f") || startInt.startsWith("F"))
            {
                prefix = "FastEthernet";
            }
            else {
                prefix = "GigabitEthernet";
            }
            return prefix + suffix;
        }
    }
    
    private String[] split(String interfaceStr){
    	String[] strs = new String[2];
    	int bracket = interfaceStr.indexOf("(")>0? interfaceStr.indexOf("("):interfaceStr.indexOf("（");
		if (bracket > 0) {
			strs[0] = parseIntface(interfaceStr.substring(0,bracket));
			strs[1] = interfaceStr.substring(bracket+1, interfaceStr.length()-1);
			if ("".equals(strs[1])) {
				strs[1] = "0";
			}
		}else{
			strs[0] = parseIntface(interfaceStr);
			strs[1] = "0";
		}
    	return strs;
    }
    
}
