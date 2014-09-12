/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.JspPage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.DistrictDao;
import com.afunms.config.dao.IPDistrictDao;
import com.afunms.config.dao.MacconfigDao;
import com.afunms.config.model.DistrictConfig;
import com.afunms.config.model.IPDistrictConfig;
import com.afunms.config.model.Macconfig;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.portscan.dao.PortScanDao;
import com.afunms.portscan.model.PortConfig;
import com.afunms.portscan.model.PortScanConfig;
import com.afunms.portscan.util.PortScanUtil;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.topology.dao.IpDistrictMatchConfigDao;
import com.afunms.topology.dao.IpMacDao;
import com.afunms.topology.model.IpDistrictDetail;
import com.afunms.topology.model.IpDistrictMatchConfig;
import com.afunms.topology.model.NetDistrictDetail;
import com.afunms.topology.model.NetDistrictIpDetail;
import com.afunms.topology.util.ComparatorIpDistrictMatchConfig;
import com.afunms.topology.util.IPDistrictMatchUtil;
import com.afunms.topology.util.IPOnlineDistrictMatchUtil;
import com.afunms.config.dao.IpConfigDao;
import com.afunms.config.model.IpConfig;

/**
 * ip网段管理
 */
public class IpDistrictMatchManager extends BaseManager implements ManagerInterface
{
    public String execute(String action)
    {
        if("list".equals(action)){
        	return list();
        }
        if("districtDetails".equals(action)){
        	return districtDetails();
        }
        if("netDistrictDetails".equals(action)){
        	return netDistrictDetails();
        }
        if("netDistrictIpDetails".equals(action)){
        	return netDistrictIpDetails();
        }
        if("searchNetDistrictIpByIp".equals(action)){
        	return searchNetDistrictIpByIp();
        }
        if("searchNetDistrictIpByWhere".equals(action)){
        	return searchNetDistrictIpByWhere();
        }
        if("createReport".endsWith(action)){
        	return createReport();
        }
        if("portscan".equals(action)){
        	return portScan();
        }
        if("savePortScan".equals(action)){
        	return savePortScan();
        }
        if("searchPortScanByIp".equals(action)){
        	return searchPortScanByIp();
        }
        if("ready_addPortScan".equals(action)){
        	return ready_addPortScan();
        }
        if("hostCompositeReport".equals(action))
        {
        	return hostCompositeReport();
        }
        if("hostPingReport".equals(action))
        {
        	return hostPingReport();
        }
        if("hostCapacityReport".equals(action))
        {
        	return hostCapacityReport();
        }
        if("hostDiskReport".equals(action))
        {
        	return hostDiskReport();
        }
        if("hostAnalyseReport".equals(action))
        {
        	return hostAnalyseReport();
        }
        if("networkPingReport".equals(action))
        {
        	return networkPingReport();
        }
        if("networkEventReport".equals(action))
        {
        	return networkEventReport();
        }
        if("networkCompositeReport".equals(action))
        {
        	return networkCompositeReport();
        }
        if("networkConfigReport".equals(action))
        {
        	return networkConfigReport();
        }
        if("addPortScan".equals(action)){
        	return addPortScan();
        }
        if("delete_portscan".equals(action)){
        	return delete_portscan();
        }
        if("createReport_portscan".equals(action)){
        	return createReport_portscan();
        }
        
        setErrorCode(ErrorMessage.ACTION_NO_FOUND);
        return null;
    }
   
    public String list(){
    	
    	List list = new ArrayList();
    	
    	String refresh = getParaValue("refresh");
    	if("refreshIpOnline".equals(refresh)){
    		//如果同步刷新 则进行刷新
    		list = refreshIpOnline();
    		//刷新完后 如果获得的list 不为null且大小大于0 则清空数据 把刷新的后的数据存入数据库中
    		if(list != null && list.size() >0){
    			IpDistrictMatchConfigDao ipDistrictMatchConfigDao = new IpDistrictMatchConfigDao();
    			try {
    				ipDistrictMatchConfigDao.deleteAll();

    				//用DBManager代替DAO				
    				DBManager dbmanager = new DBManager();
    				try{
        				for(int i =0 ; i< list.size() ; i++){
        					IpDistrictMatchConfig ipDistrictMatchConfig = (IpDistrictMatchConfig)list.get(i);
        					StringBuffer sql = new StringBuffer(100);
        					sql.append("insert into nms_ip_district_match(relateipaddr,node_ip,node_name,is_online,original_district,current_district,is_match,time)values(");
        					sql.append("'");
        					sql.append(ipDistrictMatchConfig.getRelateipaddr());
        					sql.append("','");
        					sql.append(ipDistrictMatchConfig.getNodeIp());
        					sql.append("','");
        					sql.append(ipDistrictMatchConfig.getNodeName());
        					sql.append("','");
        					sql.append(ipDistrictMatchConfig.getIsOnline());
        					sql.append("','");
        					sql.append(ipDistrictMatchConfig.getOriginalDistrict());
        					sql.append("','");
        					sql.append(ipDistrictMatchConfig.getCurrentDistrict());
        					sql.append("','");
        					sql.append(ipDistrictMatchConfig.getIsMatch());
        					sql.append("','");
        					sql.append(ipDistrictMatchConfig.getTime());
        					sql.append("')");
        					String sqlall=sql.toString();
        					dbmanager.addBatch(sqlall);
        				}
    					dbmanager.executeBatch();
    				}catch(Exception e){
    					
    				}finally{
    					dbmanager.close();
    				}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					ipDistrictMatchConfigDao.close();
				}
    		}
    	}
    	
    	
    	if("refresh".equals(refresh)){
    		//如果同步刷新 则进行刷新
    		list = refresh();
    		//刷新完后 如果获得的list 不为null且大小大于0 则清空数据 把刷新的后的数据存入数据库中
    		if(list != null && list.size() >0){
    			IpDistrictMatchConfigDao ipDistrictMatchConfigDao = new IpDistrictMatchConfigDao();
    			try {
    				ipDistrictMatchConfigDao.deleteAll();

    				//用DBManager代替DAO				
    				DBManager dbmanager = new DBManager();
    				for(int i =0 ; i< list.size() ; i++){
    					IpDistrictMatchConfig ipDistrictMatchConfig = (IpDistrictMatchConfig)list.get(i);
    					StringBuffer sql = new StringBuffer(100);
    					sql.append("insert into nms_ip_district_match(relateipaddr,node_ip,node_name,is_online,original_district,current_district,is_match,time)values(");
    					sql.append("'");
    					sql.append(ipDistrictMatchConfig.getRelateipaddr());
    					sql.append("','");
    					sql.append(ipDistrictMatchConfig.getNodeIp());
    					sql.append("','");
    					sql.append(ipDistrictMatchConfig.getNodeName());
    					sql.append("','");
    					sql.append(ipDistrictMatchConfig.getIsOnline());
    					sql.append("','");
    					sql.append(ipDistrictMatchConfig.getOriginalDistrict());
    					sql.append("','");
    					sql.append(ipDistrictMatchConfig.getCurrentDistrict());
    					sql.append("','");
    					sql.append(ipDistrictMatchConfig.getIsMatch());
    					sql.append("','");
    					sql.append(ipDistrictMatchConfig.getTime());
    					sql.append("')");
    					String sqlall=sql.toString();
    					dbmanager.addBatch(sqlall);
    				}
    				try{
    					dbmanager.executeBatch();
    				}catch(Exception e){
    					
    				}finally{
    					dbmanager.close();
    				}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					ipDistrictMatchConfigDao.close();
				}
    		}
    	}
//    	else{
//    		IpDistrictMatchConfigDao ipDistrictMatchConfigDao = new IpDistrictMatchConfigDao();
//    		try {
//				list = ipDistrictMatchConfigDao.loadAll();
//			} catch (RuntimeException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally{
//				ipDistrictMatchConfigDao.close();
//			}
//    	}
		//request.setAttribute("list", list);
    	
    	List districtList = null;
    	
    	DistrictDao districtDao = new DistrictDao();
    	try {
			districtList = districtDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			districtDao.close();
		}
    	
		request.setAttribute("districtList", districtList);
    	IpDistrictMatchConfigDao ipDistrictMatchConfigDao = new IpDistrictMatchConfigDao();
		String jsp = "/topology/ipregionalmatch/list.jsp";
		setTarget(jsp);
        return list(ipDistrictMatchConfigDao);
    }
    
    private String districtDetails(){
    	DistrictDao districtDao = new DistrictDao();
//    	List districtList = null;
//		try {
//			districtList = districtDao.loadAll();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    	String jsp = "/topology/ipregional/list.jsp";
    	setTarget(jsp);
    	
    	String where = getWhere();
    	
    	// 利用分页查找出区域
    	list(districtDao , where);
    	
    	List districtList = (List)request.getAttribute("list");
    	
    	request.setAttribute("districtList", districtList);
    	
		List list = new ArrayList();
		if( districtList != null && districtList.size() > 0){
	    	for(int i =0 ; i < districtList.size() ; i++){
	    		
	    		long ipTotal = 0L;       // 该区域的ip总数
	    		long usedTotal = 0L;     // 该区域使用总数
	    		long unusedTotal = 0L;   // 该区域未使用总数
	    		long isOnlineTotal = 0L; // 该区域在线总数
				long unOnlineTotal = 0L; // 该区域在线离数
				
	    		
	    		
	    		// 取出每个区域进行计算
	    		DistrictConfig districtConfig = (DistrictConfig)districtList.get(i);
	    		int districtId = districtConfig.getId();
	    		IPDistrictDao ipDistrictDao = new IPDistrictDao();
	    		
	    		List ipDistrictList = null;
				try {
					ipDistrictList = ipDistrictDao.loadByDistrictId(String.valueOf(districtId));
				} catch (RuntimeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally{
					ipDistrictDao.close();
				}
	    		
	    		
	    		for(int j = 0; j <ipDistrictList.size() ; j++ ){
	    			// 取出每个区域的各个网段 计算分配的ip总数
	    			IPDistrictConfig iPDistrictConfig = (IPDistrictConfig)ipDistrictList.get(j);
	    			String startip = iPDistrictConfig.getStartip();
	    			String endip = iPDistrictConfig.getEndip();
	    			if(startip!=null && endip!=null){
	    				long startipLong = ip2long(startip);
	    				long endipLong = ip2long(endip);
	    				ipTotal = ipTotal + endipLong - startipLong+1;
	    			}else if( startip!=null && endip==null ){
	    				ipTotal = ipTotal + 1;
	    			}
	    		}
	    		
	    		List ipDistrictMatchConfiglist = null;
	    		IpDistrictMatchConfigDao ipDistrictMatchConfigDao = new IpDistrictMatchConfigDao();
	    		try {
	    			ipDistrictMatchConfiglist = ipDistrictMatchConfigDao.findByOriDistrictId(String.valueOf(districtId));
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					ipDistrictMatchConfigDao.close();
				}
				
				if( ipDistrictMatchConfiglist != null && ipDistrictMatchConfiglist.size() > 0){
					for(int k = 0 ; k < ipDistrictMatchConfiglist.size() ; k++){
						// 取出每个扫描出来的ip 计算在线总数和未在线总数
						IpDistrictMatchConfig ipDistrictMatchConfig = (IpDistrictMatchConfig)ipDistrictMatchConfiglist.get(k);
						String isOnline = ipDistrictMatchConfig.getIsOnline();
						if("1".equals(isOnline)){
							isOnlineTotal = isOnlineTotal + 1;
						}else{
							unOnlineTotal = unOnlineTotal + 1;
						}
					}
					
					usedTotal = ipDistrictMatchConfiglist.size();
				}
				
				if( ipTotal > usedTotal){
					// 如果分配的ip总数大于使用的总数则算出未使用的 否则为0
					unusedTotal = ipTotal - usedTotal;
				}
				
				IpDistrictDetail ipDistrictDetail = new IpDistrictDetail();
				ipDistrictDetail.setId(districtConfig.getId());
				ipDistrictDetail.setDistrict(districtConfig.getName());
				ipDistrictDetail.setIpTotal(String.valueOf(ipTotal));
				ipDistrictDetail.setUsedTotal(String.valueOf(usedTotal));
				ipDistrictDetail.setUnusedTotal(String.valueOf(unusedTotal));
				ipDistrictDetail.setIsOnlineTotal(String.valueOf(isOnlineTotal));
				ipDistrictDetail.setUnOnlineToatl(String.valueOf(unOnlineTotal));
				list.add(ipDistrictDetail);
	    	}
		}
    	request.setAttribute("list", list);
    	return "/topology/ipregional/list.jsp";
    }
    
    private String netDistrictDetails(){
    	
    	List list = new ArrayList();
    	
    	String districtId = getParaValue("districtId");
    	
    	DistrictConfig districtConfig = null;
    	
    	DistrictDao districtDao = new DistrictDao();
		try {
			districtConfig =  (DistrictConfig)districtDao.findByID(districtId);
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally{
			districtDao.close();
		}
		
		if(districtConfig == null){
			return "/topology/ipregional/netDistrictDetail.jsp";
		}
		
		request.setAttribute("districtConfig", districtConfig);
    	
		IPDistrictDao ipDistrictDao = new IPDistrictDao();
		List ipDistrictList = null;
		try {
			ipDistrictList = ipDistrictDao.loadByDistrictId(districtId);
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally{
			ipDistrictDao.close();
		}
		
		
		
		
		List ipDistrictMatchConfiglist = null;
		IpDistrictMatchConfigDao ipDistrictMatchConfigDao = new IpDistrictMatchConfigDao();
		try {
			ipDistrictMatchConfiglist = ipDistrictMatchConfigDao.findByOriDistrictId(String.valueOf(districtId));
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			ipDistrictMatchConfigDao.close();
		}
		
		if( ipDistrictList != null && ipDistrictList.size() > 0 ){
			for(int j = 0; j <ipDistrictList.size() ; j++ ){
				String netDistrictName = "";
				long ipTotal = 0L;
				long usedTotal = 0L;
				long unusedTotal = 0L;
				long isOnlineTotal = 0L;
				long unOnlineTotal = 0L;
				
				IPDistrictConfig iPDistrictConfig = (IPDistrictConfig)ipDistrictList.get(j);
				String startip = iPDistrictConfig.getStartip();
				String endip = iPDistrictConfig.getEndip();
				
				long startipLong = -1L;
				long endipLong = -1L;
				if(startip!=null && endip!=null){
					startipLong= ip2long(startip);
					endipLong = ip2long(endip);
					ipTotal = ipTotal + endipLong - startipLong;
					netDistrictName = startip + "---" + endip;
				}else if( startip!=null && endip==null ){
					startipLong= ip2long(startip);
					ipTotal = ipTotal + 1;
					netDistrictName = startip;
				}else{
					continue;
				}
				
				if(ipDistrictMatchConfiglist != null && ipDistrictMatchConfiglist.size() > 0){
					for( int i = 0 ; i < ipDistrictMatchConfiglist.size() ; i ++){
						IpDistrictMatchConfig ipDistrictMatchConfig = (IpDistrictMatchConfig)ipDistrictMatchConfiglist.get(i);
						String nodeIp = ipDistrictMatchConfig.getNodeIp();
						long nodeIpLong = ip2long(nodeIp);
						if( nodeIpLong >= startipLong || nodeIpLong < endipLong){
							usedTotal = usedTotal + 1;
							if("1".equals(ipDistrictMatchConfig.getIsOnline())){
								isOnlineTotal = isOnlineTotal + 1;
							}else{
								unOnlineTotal = unOnlineTotal + 1;
							}
						}
						
						
					}
				}
				unusedTotal = ipTotal - usedTotal;
				NetDistrictDetail netDistrictDetail = new NetDistrictDetail();
				netDistrictDetail.setId(iPDistrictConfig.getId());
				netDistrictDetail.setDistrictId(districtId);
				netDistrictDetail.setIpDistrictId(String.valueOf(iPDistrictConfig.getId()));
				netDistrictDetail.setDistrictName(districtConfig.getName());
				netDistrictDetail.setNetDistrictName(netDistrictName);
				netDistrictDetail.setIpTotal(String.valueOf(ipTotal));
				netDistrictDetail.setUsedTotal(String.valueOf(usedTotal));
				netDistrictDetail.setUnusedTotal(String.valueOf(unusedTotal));
				netDistrictDetail.setIsOnlineTotal(String.valueOf(isOnlineTotal));
				netDistrictDetail.setUnOnlineToatal(String.valueOf(unOnlineTotal));
				list.add(netDistrictDetail);
			}
		}
		request.setAttribute("list", list);
		
    	return "/topology/ipregional/netDistrictDetail.jsp";
    }
    
    private String netDistrictIpDetails(){
    	request.setAttribute("beforeAction", "netDistrictIpDetails");
    	request.setAttribute("list", getListByPage(getAllDistrictIp()));
    	return "/topology/ipregional/netDistrictIpDetail.jsp";
    }
    
    private String searchNetDistrictIpByIp(){
    	String searchIp = getParaValue("searchIp");
    	List list = getNetDistrictIpListByIp(searchIp);
    	
    	request.setAttribute("beforeAction", "searchNetDistrictIpByIp");
    	request.setAttribute("searchIp", searchIp);
    	request.setAttribute("list", getListByPage(list));
    	return "/topology/ipregional/netDistrictIpDetail.jsp";
    }
    
    private List getNetDistrictIpListByIp(String searchIp){
    	List allNetDistrictIplist = getAllDistrictIp();
    	List list = new ArrayList();
    	
    	if(searchIp !=null && searchIp.trim().length() > 0 && 
    			allNetDistrictIplist!= null && allNetDistrictIplist.size()>0){
    		for(int i = 0 ; i < allNetDistrictIplist.size() ; i++){
    			NetDistrictIpDetail netDistrictIpDetail = (NetDistrictIpDetail)allNetDistrictIplist.get(i);
    			if( searchIp.equals(netDistrictIpDetail.getIpaddress().trim())){
    				list.add(netDistrictIpDetail);
    			}
    		}
    	}
    	
    	return list;
    }
    
    
    private String searchNetDistrictIpByWhere(){
    	String isUsed = getParaValue("isUsed");
    	String isOnline = getParaValue("isOnline");
    	
    	if(isUsed==null || ("-1").equals(isUsed.trim())){
    		isUsed = "-1";
    	}
    	
    	if(isOnline==null || ("-1").equals(isOnline.trim())){
    		isOnline = "-1";
    	}
    	
    	String isUsed_trim = isUsed.trim();
    	String isOnline_trim = isOnline.trim();
    	
    	List list = getNetDistrictIpListByWhere(isUsed_trim, isOnline_trim);
    	
    	request.setAttribute("beforeAction", "searchNetDistrictIpByWhere");
    	request.setAttribute("isUsed", isUsed_trim);
    	request.setAttribute("isOnline", isOnline_trim);
    	request.setAttribute("list", getListByPage(list));
    	return "/topology/ipregional/netDistrictIpDetail.jsp";
    }
    
    private List getNetDistrictIpListByWhere(String isUsed_trim , String isOnline_trim){
    	boolean isUsed_trim_b = false;
    	boolean isOnline_trim_b = false;
    	
    	
    	List allNetDistrictIplist = getAllDistrictIp();
    	List list = new ArrayList();
    	
    	
    	if(allNetDistrictIplist!= null && allNetDistrictIplist.size()>0){
    		if("-1".equals(isUsed_trim)&&"-1".equals(isOnline_trim)){
    			list = allNetDistrictIplist;
    		}else if("-1".equals(isUsed_trim)&& !"-1".equals(isOnline_trim)){
    			for(int i = 0 ; i < allNetDistrictIplist.size() ; i++){
        			NetDistrictIpDetail netDistrictIpDetail = (NetDistrictIpDetail)allNetDistrictIplist.get(i);
        			isOnline_trim_b = isOnline_trim.equals(netDistrictIpDetail.getIsOnline().trim());
        			
        			if( isOnline_trim_b ){
        				list.add(netDistrictIpDetail);
        			}
        		}
    		}else if( !"-1".equals(isUsed_trim)&& "-1".equals(isOnline_trim)){
    			for(int i = 0 ; i < allNetDistrictIplist.size() ; i++){
        			NetDistrictIpDetail netDistrictIpDetail = (NetDistrictIpDetail)allNetDistrictIplist.get(i);
        			isUsed_trim_b = isUsed_trim.equals(netDistrictIpDetail.getIsUsed().trim());
        			if( isUsed_trim_b ){
        				list.add(netDistrictIpDetail);
        			}
        		}
    		}else if( !"-1".equals(isUsed_trim)&& !"-1".equals(isOnline_trim)){
    			for(int i = 0 ; i < allNetDistrictIplist.size() ; i++){
        			NetDistrictIpDetail netDistrictIpDetail = (NetDistrictIpDetail)allNetDistrictIplist.get(i);
        			
        			
        			isOnline_trim_b = isOnline_trim.equals(netDistrictIpDetail.getIsOnline().trim());
        			isUsed_trim_b = isUsed_trim.equals(netDistrictIpDetail.getIsUsed().trim());
        			if( isUsed_trim_b && isOnline_trim_b){
        				list.add(netDistrictIpDetail);
        			}
        		}
    		}
    		
    	}
    	
    	return list;
    }
    
    private String createReport(){
    	String beforeAction = getParaValue("beforeAction");
    	List list = null;
    	if( beforeAction == null || beforeAction.trim().length() == 0){
    		beforeAction = "netDistrictIpDetails";
    	}
    	
    	if( "netDistrictIpDetails".equals(beforeAction)){
    		list = getAllDistrictIp();
    	}else if("searchNetDistrictIpByIp".equals(beforeAction)){
    		String searchIp = getParaValue("searchIp");
    		list = getNetDistrictIpListByIp(searchIp);
    	}else if("searchNetDistrictIpByWhere".equals(beforeAction)){
    		String isUsed = getParaValue("isUsed");
        	String isOnline = getParaValue("isOnline");
        	if(isUsed==null || ("-1").equals(isUsed.trim())){
        		isUsed = "-1";
        	}
        	
        	if(isOnline==null || ("-1").equals(isOnline.trim())){
        		isOnline = "-1";
        	}
        	
        	String isUsed_trim = isUsed.trim();
        	String isOnline_trim = isOnline.trim();
        	
        	list = getNetDistrictIpListByWhere(isUsed_trim, isOnline_trim);
    	}
    	
    	String districtName = (String)request.getAttribute("districtName");
    	String netDistrictName = (String)request.getAttribute("netDistrictName");
    	AbstractionReport1 abstractionReport1 = new ExcelReport1(new IpResourceReport());
    	abstractionReport1.createReport_netDistrictIplist("temp/netDistrictIplist_report.xls", "区域：" + districtName +"    " + "网段：" + netDistrictName, list);
    	request.setAttribute("filename", abstractionReport1.getFileName());

    	
    	return "/topology/ipregional/download.jsp";
    }
    
    private String portScan(){
    	String allIpaddress_str = getParaValue("ipaddress");
    	String[] allIpaddress = null; 
    	
    	if(allIpaddress_str!=null && allIpaddress_str.trim().length()>0){
    		allIpaddress = allIpaddress_str.split("-");
    	}
    	
    	String times = "1";
    	
    	String refresh = getParaValue("refresh");   	
    	
    	List list = new ArrayList();
    	
    	//扫描是否完成
    	boolean status = true;
    	
    	int scanTotal = 0;
    	
    	int isScannedTotal = 0;
    	
    	int unScannedTotal = 0;
    	
    	PortScanUtil portScanUtil = PortScanUtil.getInstance();
    	
    	if(refresh == null || !"refresh".equals(refresh)){
    		
    		if(allIpaddress!=null && allIpaddress.length>0){
    	    	for(int i = 0 ; i < allIpaddress.length ; i ++){
    	    		String ipaddress = allIpaddress[i];
					portScanUtil.init(ipaddress);
					PortScanConfig  portScanConfig= (PortScanConfig)portScanUtil.getData().get(ipaddress);
		    		portScanConfig.setStatus("0");
		    		List isScannedList = portScanConfig.getIsScannedList();
		    		List unScannedList = portScanConfig.getUnScannedList();
		    		unScannedList.addAll(isScannedList);
					isScannedList.removeAll(isScannedList);
		    		portScanUtil.scan(ipaddress);
    	    	}
    	    	
    	    }
    		
    		times = "0";
    	}
    	
    	if(allIpaddress!=null && allIpaddress.length>0){    		
	    	for(int i = 0 ; i < allIpaddress.length ; i ++){
	    		String ipaddress = allIpaddress[i];
	    		
	    		portScanUtil.scan(ipaddress);
	    		
	    		PortScanConfig  portScanConfig= (PortScanConfig)portScanUtil.getData().get(ipaddress);
	    		if(!"1".equals(portScanConfig.getStatus())){
	    			status = false;
	    		}
	    		
	    		isScannedTotal = isScannedTotal + portScanConfig.getIsScannedList().size();
	    		
	    		unScannedTotal = unScannedTotal + portScanConfig.getUnScannedList().size();
	    		
	    		portScanConfig.setTotal(portScanConfig.getIsScannedList().size()+portScanConfig.getUnScannedList().size());
	    		
	    		scanTotal = scanTotal + portScanConfig.getTotal();
	    		list.add(portScanConfig);
	    	}
    	}
    	System.out.println(status);
    	
    	request.setAttribute("status", status);
    	
    	request.setAttribute("isScannedTotal", isScannedTotal+"");
    	
    	request.setAttribute("unScannedTotal", unScannedTotal+"");
    	
    	request.setAttribute("times", times);    	
    	
    	request.setAttribute("scanTotal", scanTotal+"");
    	
    	request.setAttribute("ipaddress", allIpaddress_str);
    	
    	request.setAttribute("list", list);
    	return "/topology/ipregional/portscan.jsp";
    }
    
    public String savePortScan(){
    	
    	String allIpaddress_str = getParaValue("ipaddress");    	
    	String[] allIpaddress = null; 
    	
    	if(allIpaddress_str!=null){
    		allIpaddress = allIpaddress_str.split("-");
    	}
    	List list = new ArrayList();
    	List ipaddresslist = new ArrayList();
    	if(allIpaddress!=null && allIpaddress.length>0){    		
    		Hashtable data = PortScanUtil.getInstance().getData();
    		if(data == null || data.get(allIpaddress_str) == null)
    		{    			
    			PortScanUtil.getInstance().init(allIpaddress_str);
    		}    		
    		for(int i = 0 ; i < allIpaddress.length ; i++){
    			String ipaddress = allIpaddress[i];    			
    			PortScanConfig portScanConfig = (PortScanConfig)data.get(ipaddress);
    			List isScannedList = portScanConfig.getIsScannedList();    			
    			if(isScannedList!=null){
    				list.addAll(isScannedList);
    			}
    			
    			ipaddresslist.add(ipaddress);
    		}    		
    	}
    	
    	PortScanDao portScanDao = new PortScanDao();
    	try {
			portScanDao.deleteByIpaddress(ipaddresslist);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			portScanDao.close();
		}
    	
    	portScanDao = new PortScanDao();
    	try {
			portScanDao.saveBatch(list);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			portScanDao.close();
		}
    	
    	return portScan();
    }
    
    private String searchPortScanByIp(){
    	List list = new ArrayList();
    	String ipaddress = getParaValue("ipaddress");
    	PortScanDao portScanDao = new PortScanDao();
    	portScanDao = new PortScanDao();
    	try {
    		list = portScanDao.findByIpaddress(ipaddress);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			portScanDao.close();
		}
		request.setAttribute("list", list);
		request.setAttribute("ipaddress", ipaddress);
    	return "/topology/ipregional/portscanlist.jsp";
    }
    
    private String ready_addPortScan(){
    	String ipaddress = getParaValue("ipaddress");
    	request.setAttribute("ipaddress", ipaddress);
    	return "/topology/ipregional/addportscan.jsp";
    }
    private String hostCompositeReport()
    {
    	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
    	Date d = new Date();
		String startdate = getParaValue("startdate");
		if(startdate==null){
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if(todate==null){
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate",startdate);
		request.setAttribute("todate",todate);
    	String ipaddress = getParaValue("ipaddress");
    	request.setAttribute("ipaddress", ipaddress);
    	request.setAttribute("type", 1);
    	return "/capreport/host/compositeReport.jsp";
    }
    private String hostPingReport()
    {
    	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
    	Date d = new Date();
		String startdate = getParaValue("startdate");
		if(startdate==null){
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if(todate==null){
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate",startdate);
		request.setAttribute("todate",todate);
    	String ipaddress = getParaValue("ipaddress");
    	request.setAttribute("ipaddress", ipaddress);
    	request.setAttribute("type", 1);
    	return "/capreport/host/pingReport.jsp";
    }
    private String hostCapacityReport()
    {
    	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
    	Date d = new Date();
		String startdate = getParaValue("startdate");
		if(startdate==null){
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if(todate==null){
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate",startdate);
		request.setAttribute("todate",todate);
    	String ipaddress = getParaValue("ipaddress");
    	request.setAttribute("ipaddress", ipaddress);
    	request.setAttribute("type", 1);
    	return "/capreport/host/capacityReport.jsp";
    }
    private String hostDiskReport()
    {
    	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
    	Date d = new Date();
		String startdate = getParaValue("startdate");
		if(startdate==null){
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if(todate==null){
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate",startdate);
		request.setAttribute("todate",todate);
    	String ipaddress = getParaValue("ipaddress");
    	request.setAttribute("ipaddress", ipaddress);
    	request.setAttribute("type", 1);
    	return "/capreport/host/diskReport.jsp";
    }
    private String hostAnalyseReport()
    {
    	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
    	Date d = new Date();
		String startdate = getParaValue("startdate");
		if(startdate==null){
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if(todate==null){
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate",startdate);
		request.setAttribute("todate",todate);
    	String ipaddress = getParaValue("ipaddress");
    	request.setAttribute("ipaddress", ipaddress);
    	request.setAttribute("type", 2);
    	return "/capreport/host/analyseReport.jsp";
    }
    private String networkPingReport()
    {
    	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
    	Date d = new Date();
		String startdate = getParaValue("startdate");
		if(startdate==null){
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if(todate==null){
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate",startdate);
		request.setAttribute("todate",todate);
    	String ipaddress = getParaValue("ipaddress");
    	request.setAttribute("ipaddress", ipaddress);
    	return "/capreport/net/networkPingReport.jsp";
    }
    private String networkEventReport()
    {
    	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
    	Date d = new Date();
		String startdate = getParaValue("startdate");
		if(startdate==null){
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if(todate==null){
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate",startdate);
		request.setAttribute("todate",todate);
    	String ipaddress = getParaValue("ipaddress");
    	request.setAttribute("ipaddress", ipaddress);
    	return "/capreport/net/networkEventReport.jsp";
    }
    private String networkCompositeReport()
    {
    	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
    	Date d = new Date();
		String startdate = getParaValue("startdate");
		if(startdate==null){
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if(todate==null){
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate",startdate);
		request.setAttribute("todate",todate);
    	String ipaddress = getParaValue("ipaddress");
    	request.setAttribute("ipaddress", ipaddress);
    	return "/capreport/net/networkCompositeReport.jsp";
    }
    private String networkConfigReport()
    {
    	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
    	Date d = new Date();
		String startdate = getParaValue("startdate");
		if(startdate==null){
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if(todate==null){
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate",startdate);
		request.setAttribute("todate",todate);
    	String ipaddress = getParaValue("ipaddress");
    	request.setAttribute("ipaddress", ipaddress);
    	return "/capreport/net/networkConfigReport.jsp";
    }
    
    private String addPortScan(){
    	String ipaddress = getParaValue("ipaddress");
    	String startport = getParaValue("startport");
    	String endport = getParaValue("endport");
    	String portName = getParaValue("portName");
    	String description = getParaValue("description");
    	String type = getParaValue("type");
    	String timeout = getParaValue("timeout");
    	
    	
    	if(portName==null || portName.trim().length() == 0){
    		portName = "未定义";
    	}
    	
    	if(description==null || description.trim().length() == 0){
    		description = "未定义";
    	}
    	
    	if(type==null || type.trim().length() == 0){
    		type = "未定义";
    	}
    	
    	int startport_int = 0 ;
    	
    	int endport_int = 0;
    	if(startport!=null && startport.trim().length()>0){
    		try {
				startport_int = Integer.parseInt(startport);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	if(endport!=null&& endport.trim().length()>0){
    		try {
    			endport_int = Integer.parseInt(endport) +1 ;
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				endport_int = startport_int + 1;
			}
    	}else {
    		endport_int = startport_int + 1;
    	}
    	
    	PortScanDao portScanDao = new PortScanDao();
    	List list = null;
    	try {
			list = portScanDao.findByIpaddress(ipaddress);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			portScanDao.close();
		}
		
		if(list == null || list.size() == 0){
			list = new ArrayList();
		}
    	
    	for(int i = startport_int ; i < endport_int ; i++){
    		PortConfig portConfig = new PortConfig();
    		int add_port = i ;
    		for(int j = 0 ; j < list.size() ; j++){
    			PortConfig portConfig2 = (PortConfig)list.get(j);
    			if(add_port == Integer.parseInt(portConfig2.getPort())){
    				
    				list.remove(j);
    				break;
    			}
    			
    		}
    		portConfig.setIsScanned("0");
    		portConfig.setStatus("0");
			portConfig.setScantime("-- --");
    		portConfig.setIpaddress(ipaddress);
			portConfig.setPort(String.valueOf(add_port));
			portConfig.setDescription(description);
			portConfig.setPortName(portName);
			portConfig.setTimeout(timeout);
			portConfig.setType(type);
			list.add(portConfig);
    		
    	}
    	
    	portScanDao = new PortScanDao();
    	try {
			portScanDao.deleteByIpaddress(ipaddress);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			portScanDao.close();
		}
    	
		portScanDao = new PortScanDao();
		try {
			portScanDao.saveBatch(list);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			portScanDao.close();
		}
		
		
    	request.setAttribute("ipaddress", ipaddress);
    	return "/";
    }
    
    private String delete_portscan(){
    	String[] ids = getParaArrayValue("checkbox");
    	PortScanDao portScanDao = new PortScanDao();
    	try {
			portScanDao.delete(ids);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			portScanDao.close();
		}
    	return searchPortScanByIp();
    }
    
    private String createReport_portscan(){
    	
    	String allIpaddress_str = getParaValue("ipaddress");
    	String[] allIpaddress = null; 
    	
    	if(allIpaddress_str!=null){
    		allIpaddress = allIpaddress_str.split("-");
    	}
    	List list = new ArrayList();
    	List ipaddresslist = new ArrayList();
    	if(allIpaddress!=null && allIpaddress.length>0){
    		Hashtable data = PortScanUtil.getData();
    		for(int i = 0 ; i < allIpaddress.length ; i++){
    			String ipaddress = allIpaddress[i];
    			PortScanConfig portScanConfig =  (PortScanConfig)data.get(ipaddress);
    			List isScannedList = portScanConfig.getIsScannedList();
    			if(isScannedList!=null){
    				list.addAll(isScannedList);
    			}
    			
    			ipaddresslist.add(ipaddress);
    		}
    		
    	}
    	
    	AbstractionReport1 abstractionReport1 = new ExcelReport1(new IpResourceReport());
    	abstractionReport1.createReport_portscanlist("temp/portscan_report.xls", "IP 地址 ： " + allIpaddress_str , list);
    	request.setAttribute("filename", abstractionReport1.getFileName());

    	return "/topology/ipregional/download.jsp";
    }
    
    private List getAllDistrictIp(){
    	String netDistrictId = getParaValue("netDistrictId");
    	request.setAttribute("netDistrictId", netDistrictId);
    	
    	String districtId = getParaValue("districtId");
    	String ipDistrictId = getParaValue("ipDistrictId");
    	
    	DistrictConfig districtConfig = null;
    	DistrictDao districtDao = new DistrictDao();
		try {
			// 找出此网段所属区域
			districtConfig =  (DistrictConfig)districtDao.findByID(districtId);
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally{
			districtDao.close();
		}
		
		if(districtConfig == null){
			return null;
		}
		
		request.setAttribute("districtConfig", districtConfig);
    	
		IPDistrictConfig ipDistrictConfig = null;
    	IPDistrictDao ipDistrictDao = new IPDistrictDao();
		try {
			// 找出此网段所属区域的 关联
			ipDistrictConfig = (IPDistrictConfig)ipDistrictDao.findByID(ipDistrictId);
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally{
			ipDistrictDao.close();
		}
		
		request.setAttribute("ipDistrictConfig", ipDistrictConfig);
		
		List list = new ArrayList();
		
		if(ipDistrictConfig!=null){
			List ipDistrictMatchConfiglist = null;
			IpDistrictMatchConfigDao ipDistrictMatchConfigDao = new IpDistrictMatchConfigDao();
			try {
				// 根据所属区域 找出 ipDistrictMatch
				ipDistrictMatchConfiglist = ipDistrictMatchConfigDao.findByOriDistrictId(String.valueOf(districtId));
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				ipDistrictMatchConfigDao.close();
			}
			
			String startip_str = ipDistrictConfig.getStartip();  // 获取网段的开始ip
			String endip_str = ipDistrictConfig.getEndip();      // 获取网段的结束ip
			
			String netDistrictName = "";
			
			long startip_long = 0L;
			long endip_long = 0L;
			if(startip_str!=null && endip_str==null){
				startip_long = ip2long(startip_str);
				endip_long = startip_long + 1;
				netDistrictName = startip_str;
			}else if( startip_str != null && endip_str != null){
				startip_long = ip2long(startip_str);
				endip_long = ip2long(endip_str);
				netDistrictName = startip_str + "---" + endip_str;
			}else{
				// 说明数据库内的数据有错误 应该立即返回一个空list
			}
			
			long ipTotal= endip_long - startip_long;    // ip总数 = 结束ip - 开始ip
			
			if(ipDistrictMatchConfiglist == null){
				ipDistrictMatchConfiglist = new ArrayList();
			}
			
			// 循环网段内的所有ip
			for(int i = 0 ; i < ipTotal ; i ++){
				// 将ip地址转换成long型 + i 后在转换成String类型的ip 这样就可以 循环取出网段内的每个ip
				long testip_long = startip_long + i;
				String testip_str = iplongToIp(testip_long);
				
				String isUsed = "0";
				String isOnline = "0";
				
				for( int j = 0 ; j < ipDistrictMatchConfiglist.size() ; j++){
					// 循环取出每个 ipDistrictMatchConfig  如果 ipDistrictMatchConfig.getNodeIp()和测试的ip相等
					// 则说明此ip被分配了 即被使用了
					// 然后将其在线状态 赋值个 isOnline
					IpDistrictMatchConfig ipDistrictMatchConfig = (IpDistrictMatchConfig)ipDistrictMatchConfiglist.get(j);
					if(testip_str.equals(ipDistrictMatchConfig.getNodeIp())){
						isUsed = "1";
						isOnline = ipDistrictMatchConfig.getIsOnline();
						break;
					}
					
				}
				
				NetDistrictIpDetail netDistrictIpDetail = new NetDistrictIpDetail();
				netDistrictIpDetail.setId(i);
				netDistrictIpDetail.setIpaddress(testip_str);
				netDistrictIpDetail.setDistrictId(districtId);
				netDistrictIpDetail.setDistrictName(districtConfig.getName());
				netDistrictIpDetail.setIpDistrictId(ipDistrictId);
				//netDistrictIpDetail.setIpTotal(ipTotal);
				netDistrictIpDetail.setNetDistrictName(netDistrictName);
				netDistrictIpDetail.setIsOnline(isOnline);
				netDistrictIpDetail.setIsUsed(isUsed);
				list.add(netDistrictIpDetail);
			}
			
			request.setAttribute("netDistrictName", netDistrictName);
			
		}
		
		//request.setAttribute("list", getListByPage(list));
		request.setAttribute("districtName", districtConfig.getName());
		
		return list;
    }
    
    private List getListByPage(List list){
    	List returnList = new ArrayList();
    	
    	int totalRecord = 0;               // 总页数
    	int perpage = getPerPagenum();     // 每页允许记录数
    	int curpage = getCurrentPage();    // 当前页数
    	
    	if(list == null || list.size() ==0){
    		totalRecord = 0;
    		JspPage jspPage = new JspPage(perpage,curpage,totalRecord);
    		request.setAttribute("page", jspPage);
    		return returnList;
    	}
    	totalRecord = list.size();
    	JspPage jspPage = new JspPage(perpage,curpage,totalRecord);
    	int loop = 0;
    	Iterator it = list.iterator();
	    while(it.hasNext())
	    {
		    loop++;
		    Object object= it.next();
		    if(loop<jspPage.getMinNum()) continue;
		    returnList.add(object);
		    if(loop==jspPage.getMaxNum()) break;
	    }
	    request.setAttribute("page", jspPage);
    	return returnList;
    }
    
    
    private List refresh(){
    	List ipMacList = new ArrayList();
    	IpMacDao ipMacDao = new IpMacDao();
    	try {
			ipMacList = ipMacDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			ipMacDao.close();
		}
		
//		List list = new ArrayList();
		List list = new IPDistrictMatchUtil().pingUtil(ipMacList);
		ComparatorIpDistrictMatchConfig comparatorIpDistrictMatchConfig = new ComparatorIpDistrictMatchConfig();
		Collections.sort(list, comparatorIpDistrictMatchConfig);
		return list;
    }
    
    
    
    
   /* private List refreshIpOnline(){
    	List ipList = new ArrayList();
    	MacconfigDao ipConfigDao = new MacconfigDao();
    	try {
    		ipList = ipConfigDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			ipConfigDao.close();
		}
		
//		List list = new ArrayList();
		List iplist = new IPOnlineDistrictMatchUtil().pingIpUtil(ipList);
		ComparatorIpDistrictMatchConfig comparatorIpDistrictMatchConfig = new ComparatorIpDistrictMatchConfig();
		Collections.sort(iplist, comparatorIpDistrictMatchConfig);
		return iplist;
    }*/
		
    
    private List refreshIpOnline(){
    	List ipList = new ArrayList();
    	IpConfigDao ipConfigDao = new IpConfigDao();
    	try {
    		ipList = ipConfigDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			ipConfigDao.close();
		}
		
//		List list = new ArrayList();
		List iplist = new IPOnlineDistrictMatchUtil().pingIpUtil(ipList);
		ComparatorIpDistrictMatchConfig comparatorIpDistrictMatchConfig = new ComparatorIpDistrictMatchConfig();
		Collections.sort(iplist, comparatorIpDistrictMatchConfig);
		return iplist;
    }

    
    private DistrictConfig getOriginalDistrict(String mac){
    	DistrictConfig districtConfig = null;
    	MacconfigDao macconfigDao = new MacconfigDao();
		try {
			List MacConfigList = macconfigDao.findByMac(mac);
			if(MacConfigList!=null&&MacConfigList.size()>0){
				Macconfig macConfig = (Macconfig)MacConfigList.get(0);
				String districtConfigId = String.valueOf(macConfig.getDiscrictid());
				DistrictDao districtDao = new DistrictDao();
				try {
					districtConfig = (DistrictConfig)districtDao.findByID(districtConfigId);
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					districtDao.close();
					districtConfig = null;
				}
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			districtConfig = null ;
		}finally{
			macconfigDao.close();
		}
		return districtConfig;
    }
    
    private boolean getIsOnline(String ipaddress){
    	boolean isOnline = false;
    	try {
			if(ipaddress == null){
				System.out.println("IpDistrictMatchManager.getIsOnline()中的参数ipaddress为null");
				return false;
			}
			PingUtil pingU=new PingUtil(ipaddress);
			Integer[] packet=pingU.ping();
			Vector vector=pingU.addhis(packet); 
			//此Vector内 有两个元素 都是 Pingcollectdata 类型
			//第一个元素为连通值 
			//第二个元素为响应时间
			if(vector==null || vector.size()==0){
				return false;
			}
			Pingcollectdata pingcollectdata = (Pingcollectdata)vector.get(0);
			String thevalue = pingcollectdata.getThevalue();
			if(Double.valueOf(thevalue) > 50){
				isOnline = true;
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			SysLogger.info("IpDistrictMatchManager.getIsOnline()判断是否在线中出错！！！");
			e.printStackTrace();
		}
    	return isOnline;
    }
    
    private long ip2long(String ip) {
		long result = 0;
		try {
			StringTokenizer st = new StringTokenizer(ip, ".");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				int part = Integer.parseInt(token);
				result = result * 256 + part;
			}
		} catch (Exception e) {
			result = 0;
		}
		return result;
	}
    
    private String iplongToIp(long ipaddress) {  
        StringBuffer sb = new StringBuffer("");  
        sb.append(String.valueOf((ipaddress >>> 24)));  
        sb.append(".");  
        sb.append(String.valueOf((ipaddress & 0x00FFFFFF) >>> 16));  
        sb.append(".");  
        sb.append(String.valueOf((ipaddress & 0x0000FFFF) >>> 8));  
        sb.append(".");  
        sb.append(String.valueOf((ipaddress & 0x000000FF)));  
        return sb.toString();  
    } 
    
    private DistrictConfig getCurrentDistrict(String ipaddress , List ipDistrictList){
    	DistrictConfig districtConfig = null ;
    	try {
			IPDistrictConfig ipDistrictConfig = getCurrentIPDistrictConfig(ipaddress, ipDistrictList);
			if(ipDistrictConfig == null){
				return districtConfig;
			}
			int districtid = ipDistrictConfig.getDistrictid();
			DistrictDao districtDao = new DistrictDao();
			try {
				districtConfig= (DistrictConfig)districtDao.findByID(String.valueOf(districtid));
			} catch (RuntimeException e) {
				districtConfig = null ;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				districtDao.close();
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			districtConfig = null ;
		}
    	return districtConfig;
    }
    
    
    private IPDistrictConfig getCurrentIPDistrictConfig(String ipaddress , List ipDistrictList){
    	try {
			long ipaddresslong = ip2long(ipaddress);
			long startiplong = 0L; 
			long endiplong = 0L; 
			for(int i =0 ; i < ipDistrictList.size() ; i ++){
				IPDistrictConfig ipDistrictConfig = (IPDistrictConfig)ipDistrictList.get(i);
				String startip = ipDistrictConfig.getStartip();
				String endip = ipDistrictConfig.getEndip();
				startiplong = ip2long(startip);
				endiplong = ip2long(endip);
				if( startiplong!=0 && endiplong != 0){
					//如果开始网段和结束网段都不为空 则判断该地址是否在网段区域内
					if(ipaddresslong>startiplong && ipaddresslong<endiplong){
						return ipDistrictConfig;
					}
				}else if( startiplong!=0 && endiplong == 0){
					if( ipaddresslong == startiplong ){
						return ipDistrictConfig;
					}
				}
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	return null;
    }
    
    private boolean getIsMatch(DistrictConfig originalDistrict , DistrictConfig currentDistrict){
    	boolean isMatch = false;
    	try {
			if(originalDistrict ==null || currentDistrict == null){
				isMatch = false;
				return isMatch;
			}
			int originalDistrictId = originalDistrict.getId();
			int currentDistrictId = currentDistrict.getId();
			
			if(originalDistrictId == currentDistrictId ){
				isMatch = true;
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isMatch = false;
		}
    	return isMatch;
    }
    
    /**
     * 获取查询条件
     * @return
     */
    public String getWhere(){
    	String sql = " where ";
    	
    	String sqlDistrict = getSqlDistrict();
    	
    	sql = sql + sqlDistrict;
    	return sql;
    }
    
    /**
     * 拼接 按区域查找的 SQL 语句
     * @return
     */
    public String getSqlDistrict(){
    	String sqlDistrict = "";
    	String searchDistrictId = getParaValue("searchDistrictId");
    	if(searchDistrictId == null || "-1".equals(searchDistrictId)){
    		sqlDistrict = "-1=-1";
    		searchDistrictId = "-1";
    	}else{
    		sqlDistrict = "id='" + searchDistrictId + "' ";
    	}
    	request.setAttribute("searchDistrictId", searchDistrictId);
    	return sqlDistrict;
    }
    
    
    
}
