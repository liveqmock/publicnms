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

import com.afunms.application.dao.CicsConfigDao;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.model.CicsConfig;
import com.afunms.application.util.CicsHelper;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.discovery.KeyGenerator;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.loader.CicsLoader;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;

public class CicsManager extends BaseManager implements ManagerInterface {

	private String list() {
		User operator = (User) session
				.getAttribute(SessionConstant.CURRENT_USER);
		String bids = operator.getBusinessids();
		if (bids == null)
			bids = "";
		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				if (bid[i] != null && bid[i].trim().length() > 0)
					rbids.add(bid[i].trim());
			}
		}
		CicsConfigDao configdao = new CicsConfigDao();
		List list = new ArrayList();
		try{
			list = configdao.getCicsByBID(rbids);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		request.setAttribute("list", list);
		setTarget("/application/cics/CicsConfigList.jsp");
		return "/application/cics/CicsConfigList.jsp";
	}
	
	/**
	 * snow 增加前将供应商查找到
	 * @return
	 */
	private String ready_add(){
		SupperDao supperdao = new SupperDao();
    	List<Supper> allSupper = supperdao.loadAll();
    	request.setAttribute("allSupper", allSupper);
		return "/application/cics/add.jsp";
	}
	

	private String add() {
		CicsConfig vo = new CicsConfig();
		vo.setId(KeyGenerator.getInstance().getHostKey());//snow add at 2010-05-20
		vo.setRegion_name(getParaValue("region_name"));
		vo.setAlias(getParaValue("alias"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setPort_listener(getParaValue("port_listener"));
		vo.setConn_timeout(getParaIntValue("conn_timeout"));
		vo.setNetwork_protocol(getParaValue("network_protocol"));
		vo.setFlag(getParaIntValue("flag"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setGateway(getParaValue("gateway"));
		vo.setSupperid(getParaIntValue("supperid"));//snow add 2010-5-20
		
		String allbid = ",";
		String[] businessids = getParaArrayValue("checkbox");
		if (businessids != null && businessids.length > 0) {
			for (int i = 0; i < businessids.length; i++) {

				String bid = businessids[i];
				allbid = allbid + bid + ",";
			}
		}
		vo.setNetid(allbid);
		CicsConfigDao dao = new CicsConfigDao();
		try{
			dao.save(vo);
			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("16"));
			/* snow add end*/
			//保存应用
			HostApplyManager.save(vo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		try{
			dao = new CicsConfigDao();
			List list = dao.loadAll();
			if(list == null)list = new ArrayList();
			ShareData.setCicslist(list);
			CicsLoader cicsloader = new CicsLoader();
			cicsloader.clearRubbish(list);
		}catch(Exception e){
				
		}finally{
			dao.close();
		}
		return "/cics.do?action=list&jp=1";
	}
	
	/**
	 * snow 增加前将供应商查找到
	 * @return
	 */
	private String ready_find(){
		SupperDao supperdao = new SupperDao();
    	List<Supper> allSupper = supperdao.loadAll();
    	request.setAttribute("allSupper", allSupper);
		return "/application/cics/find.jsp";
	}

	private String find() {
		CicsHelper cicsHelp = new CicsHelper();
		String find_type = getParaValue("find_type");
		String ip_address = getParaValue("ip_address");
		String port_gateway = getParaValue("port_gateway");
		String url_str = "tcp://" + ip_address + ":" + port_gateway;
		if ("local".equals(find_type)) {
			cicsHelp.findServer(find_type);
		}
		if ("remote".equals(find_type)) {
			cicsHelp.findServer(url_str);
		}
		return "/cics.do?action=list&jp=1";

	}

	public String delete() {
		String[] ids = getParaArrayValue("checkbox");
		List list = new ArrayList();
		CicsConfigDao configdao = new CicsConfigDao();
		if (ids != null && ids.length > 0) {
			try{
				configdao.delete(ids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
		
			/* snow add 2010-5-20 */
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			for (String str : ids) {
				tg.deleteTimeGratherConfig(str, tg.getObjectType("16"));
				Node node = PollingEngine.getInstance().getCicsByID(Integer.parseInt(str));
    			//删除应用
				HostApplyDao hostApplyDao = null;
				try{
					hostApplyDao = new HostApplyDao();
					hostApplyDao.delete(" where ipaddress = '"+node.getIpAddress()+"' and subtype = 'cics' and nodeid = '"+str+"'");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(hostApplyDao != null){
						hostApplyDao.close();
					}
				}
				PollingEngine.getInstance().deleteCicsByID(Integer.parseInt(str));
			}
			/* snow end*/
		}
		try {
			User operator = (User) session
					.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			configdao = new CicsConfigDao();
			try{
				list = configdao.getCicsByBID(rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
			configdao = new CicsConfigDao();
			List _list = configdao.loadAll();
			if(_list == null)_list = new ArrayList();
			ShareData.setCicslist(_list);
			CicsLoader cicsloader = new CicsLoader();
			cicsloader.clearRubbish(_list);
		}catch(Exception e){
				
		}finally{
			configdao.close();
		}
		request.setAttribute("list", list);
		return "/application/cics/CicsConfigSearchList.jsp";
	}
	/**
	 * snow
	 * 修改cics前获得其数据库中的关联数据，采集时间
	 * @return url
	 */
	private String ready_edit(){
		DaoInterface dao = new CicsConfigDao();
		setTarget("/application/cics/edit.jsp");
		String jsp = "";
    	try {
    		jsp = readyEdit(dao);
			//提供供应商信息
			SupperDao supperdao = new SupperDao();
	    	List<Supper> allSupper = supperdao.loadAll();
	    	request.setAttribute("allSupper", allSupper);
	    	//采集时间信息
			TimeGratherConfigUtil tg  = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("16"));
			for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
				timeGratherConfig.setHourAndMin();
			}
			request.setAttribute("timeGratherConfigList", timeGratherConfigList);
		} catch (Exception e) {
			e.printStackTrace();
		}  
    	return jsp;
    }

	private String update() {
		CicsConfig vo = new CicsConfig();
		List list = new ArrayList();
		vo.setId(getParaIntValue("id"));
		vo.setRegion_name(getParaValue("region_name"));
		vo.setAlias(getParaValue("alias"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setPort_listener(getParaValue("port_listener"));
		vo.setConn_timeout(getParaIntValue("conn_timeout"));
		vo.setNetwork_protocol(getParaValue("network_protocol"));
		vo.setFlag(getParaIntValue("flag"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setGateway(getParaValue("gateway"));
		vo.setSupperid(getParaIntValue("supperid"));//snow add 2010-5-20

		String allbid = ",";
		String[] businessids = getParaArrayValue("checkbox");
		if (businessids != null && businessids.length > 0) {
			for (int i = 0; i < businessids.length; i++) {

				String bid = businessids[i];
				allbid = allbid + bid + ",";
			}
		}
		vo.setNetid(allbid);
		
		try {
			CicsConfigDao configdao = new CicsConfigDao();
			try{
				configdao.update(vo);
				/* 增加采集时间设置 snow add at 2010-5-20 */
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("16"));
				/* snow add end*/
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
		
			User operator = (User) session
					.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			if (bids == null)
				bids = "";
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			CicsConfigDao dao = new CicsConfigDao();
			try{
				list = dao.getCicsByBID(rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			try{
				configdao = new CicsConfigDao();
				List _list = configdao.loadAll();
				if(_list == null)_list = new ArrayList();
				ShareData.setCicslist(_list);
				CicsLoader cicsloader = new CicsLoader();
				cicsloader.clearRubbish(_list);
			}catch(Exception e){
					
			}finally{
				configdao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//dao.close();
		}
	
		request.setAttribute("list", list);

		return "/application/cics/CicsConfigSearchList.jsp";
	}

	private String addalert() {
		CicsConfig vo = new CicsConfig();
		

		List list = new ArrayList();

		try {
			CicsConfigDao configdao = new CicsConfigDao();
			try{
				vo = (CicsConfig) configdao.findByID(getParaValue("id"));
				vo.setFlag(1);
				configdao.update(vo);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			User operator = (User) session
					.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			if (bids == null)
				bids = "";
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			configdao = new CicsConfigDao();
			try{
				list = configdao.getCicsByBID(rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		CicsLoader loader=new CicsLoader();
		loader.loading();
		request.setAttribute("list", list);
		return "/application/cics/CicsConfigSearchList.jsp";
	}

	private String cancelalert() {
		CicsConfig vo = new CicsConfig();
		

		List list = new ArrayList();

		try {
			CicsConfigDao configdao = new CicsConfigDao();
			try{
				vo = (CicsConfig) configdao.findByID(getParaValue("id"));
				vo.setFlag(0);
				configdao.update(vo);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			User operator = (User) session
					.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			if (bids == null)
				bids = "";
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			configdao = new CicsConfigDao();
			try{
				list = configdao.getCicsByBID(rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		CicsLoader loader=new CicsLoader();
		loader.loading();
		request.setAttribute("list", list);
		return "/application/cics/CicsConfigSearchList.jsp";
	}

	private String detail() {
		CicsHelper cicsHelp = new CicsHelper();
		CicsConfig vo = null;
		String server = "";
		String urlStr = "";
		String ipAddress = "";
		String Port = "";
		Integer queryid = getParaIntValue("id");
		if (queryid != null) {
			vo = new CicsConfig();
			CicsConfigDao configdao = new CicsConfigDao();
			try{
				vo = (CicsConfig) configdao.findByID(queryid + "");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
		}
		if(vo!=null){
			server = vo.getRegion_name();
			urlStr = vo.getGateway();
			ipAddress = vo.getIpaddress();
			Port = vo.getPort_listener();
		}
		
		// 默认显示CST1的数据
		String displayText = cicsHelp.displayData(urlStr, server, "CST1");
		System.out.println(displayText);
		request.setAttribute("displayInfo", displayText);
		request.setAttribute("transactionId", transactionName("CST1"));
		request.setAttribute("ipAddress", ipAddress);
		return "/application/cics/detail.jsp?serverName=" + server + "&urlStr="
				+ urlStr + "&Port=" + Port;

	}

	private String displayData() {

		CicsHelper cicsHelp = new CicsHelper();
		String server = request.getParameter("serverName");
		String urlStr = request.getParameter("urlStr");
		String transactionId = request.getParameter("transactionId");
		String ipAddress = request.getParameter("ipAddress");
		String Port = request.getParameter("Port");
		if ("".equals(transactionId)) {
			request.setAttribute("displayInfo", "请选择...");
		} else {
			// 显示指定transactionId的数据
			String displayText = cicsHelp.displayData(urlStr, server,
					transactionId);
			System.out.println(displayText);
			request.setAttribute("displayInfo", displayText);
		}
		request.setAttribute("transactionId", transactionName(transactionId));
		request.setAttribute("ipAddress", ipAddress);
		return "/application/cics/detail.jsp?Port=" + Port;
	}

	private String transactionName(String transactionId) {
		if (transactionId.equals("CST1"))
			return "TS 与 TD 统计";
		if (transactionId.equals("CST2"))
			return "池存储器统计";
		if (transactionId.equals("CST3"))
			return "杂项统计";
		if (transactionId.equals("CST4"))
			return "ISC详细统计";
		if (transactionId.equals("CST5"))
			return "文件统计";
		if (transactionId.equals("CST6"))
			return "终端统计";
		if (transactionId.equals("CST7"))
			return "程序统计";
		if (transactionId.equals("CST8"))
			return "事务统计";
		if (transactionId.equals("CST9"))
			return "类的最大任务统计";
		if (transactionId.equals("CSTA"))
			return "ISC摘要统计";
		if (transactionId.equals("CSTB"))
			return "事务处理/程序速率";
		return "";
	}
	
	

	public String execute(String action) {
		if (action.equals("list"))
			return list();
		if (action.equals("ready_add"))
			return ready_add();
		if (action.equals("add"))
			return add();
		if (action.equals("ready_find"))
			return ready_find();
		if (action.equals("find"))
			return find();
		if (action.equals("delete"))
			return delete();
		if (action.equals("ready_edit")) {
			return ready_edit();//snow modify 2010-5-20
		}
		if (action.equals("update"))
			return update();
		if (action.equals("addalert"))
			return addalert();
		if (action.equals("cancelalert"))
			return cancelalert();
		if (action.equals("detail"))
			return detail();
		if (action.equals("displayData"))
			return displayData();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

}