package com.afunms.application.manage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.net.URLDecoder;

import com.afunms.alarm.dao.AlarmIndicatorsDao;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.NodeIndicatorAlarmDao;
import com.afunms.application.dao.PerformancePanelDao;
import com.afunms.application.dao.PerformancePanelIndicatorsDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.NodeIndicatorAlarm;
import com.afunms.application.model.PerformancePanelIndicatorsModel;
import com.afunms.application.model.PerformancePanelModel;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Aug 15, 2011 7:22:03 PM
 * 类说明:性能面板控制类
 */
public class PerformancePanelManager extends BaseManager implements ManagerInterface {
	private static PerformancePanelManager performancePanelManager;
	
	public static synchronized PerformancePanelManager getInstance(){
		if(performancePanelManager == null){
			performancePanelManager = new PerformancePanelManager();
		}
		return performancePanelManager;
	}
	
	/**
	 * 初始化性能面板中的告警数据
	 */
	public void init(){
		NodeIndicatorAlarmDao nodeIndicatorAlarmDao = new NodeIndicatorAlarmDao();
		try {
			nodeIndicatorAlarmDao.clearData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			nodeIndicatorAlarmDao.close();
		}
	}
	
	/**
	 * 工程名
	 */
	private static String rootPath;

	
	public String execute(String action) {
		if("addperformancePanel".equals(action)){
			return addperformancePanel();
		}
		if("secondStep".equals(action)){
			return secondStep();
		}
		if("thirdStep".equals(action)){
			return thirdStep();
		}
		if("createNewPanel".equals(action)){
			return createNewPanel();
		}
		if("delete".equals(action)){
			return delete();
		}
		if("toEditPanelDevice".equals(action)){
			return toEditPanelDevice();
		}
		if("toEditPanelIndicators".equals(action)){
			return toEditPanelIndicators();
		}
		if("panelList".equals(action)){
			return panelList();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
	
	/**
	 * 性能面板列表
	 * @return
	 */
	private String panelList(){
		String freshTimeMinute = getParaValue("freshTimeMinute");
		if(freshTimeMinute == null){
			freshTimeMinute = "300";//页面默认为60秒刷新一次
		}
		//监控管理列表
		//性能监控面板的table表格
		rootPath = request.getContextPath();
		List<String> tableList = getPerformanceList();
		//返回监控管理的界面
		request.setAttribute("tableList", tableList);
		request.setAttribute("freshTimeMinute", freshTimeMinute);
		return "/performance/xnmb.jsp";
	}
	
	/**
	 * 编辑性能指标
	 * @return
	 */
	private String toEditPanelIndicators(){
		String deviceType = request.getParameter("deviceType");
		String panelName = request.getParameter("panelName"); 
		try {
			panelName = URLDecoder.decode(panelName,"UTF-8");
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		
		//根据节点的id取出所有指标的集合
		List alarmIndicatorsList = null;
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		try {
			alarmIndicatorsList = alarmIndicatorsDao.getByType(deviceType);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			alarmIndicatorsDao.close();
		}
		//指标集合存入request中
		PerformancePanelIndicatorsDao panelIndicatorsDao = null;
		List perList = null;
		try{
			panelIndicatorsDao = new PerformancePanelIndicatorsDao();
			perList = panelIndicatorsDao.findByCondition(" where panelName = '"+panelName.trim()+"'");
		} catch(Exception e) {
			e.printStackTrace();
		} finally{
			if(panelIndicatorsDao != null){
				panelIndicatorsDao.close();
			}
		}
		//根据面板名称，得到该面板中的所有指标
		StringBuffer indicatorsIdsBuffer = new StringBuffer();
		if(perList != null){
			for(int i=0; i<perList.size(); i++){
				PerformancePanelIndicatorsModel panelModel = (PerformancePanelIndicatorsModel)perList.get(i);
				indicatorsIdsBuffer.append(",");
				indicatorsIdsBuffer.append(panelModel.getIndicatorName());
				if(i == perList.size()-1){
					indicatorsIdsBuffer.append(",");
				}
			}
		}
		request.setAttribute("indicatorsIds", indicatorsIdsBuffer.toString());
		request.setAttribute("alarmIndicatorsList", alarmIndicatorsList);
		request.setAttribute("panelName", panelName);
		request.setAttribute("deviceType", deviceType);
		return "/performance/performancePanelChoseAlarmIndicators.jsp";
	}
	
	/**
	 * 到编辑面板的设备的页面
	 * @return
	 */
	private String toEditPanelDevice(){
		String deviceType = request.getParameter("deviceType");
		String panelName = request.getParameter("panelName"); 
		try {
			panelName = URLDecoder.decode(panelName,"UTF-8");
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
			
		//根据当前用户的权限得到设备资源树
		//默认的设备资源类型为网络设备  deviceType = 'net'
		List deviceList = null;
		int typeInt = 1;
		if(deviceType == null || deviceType.equals("null")){
			deviceType = "net";
		}
		if(deviceType.equals("net")){
			typeInt = 1;
		}else if(deviceType.equals("host")){
			typeInt = 4;
		}else if(deviceType.equals("db")){
			typeInt = 5;//数据库
		}
		String[] bids = getBids();
		deviceList = getDeviceList(1,typeInt,bids);
		//根据面板名称，得到该面板中的设备元素
		PerformancePanelDao performancePanelDao = null;
		List perList = null;
		try{
			performancePanelDao = new PerformancePanelDao();
			perList = performancePanelDao.findByCondition(" where name = '"+panelName.trim()+"'");
		} catch(Exception e) {
			e.printStackTrace();
		} finally{
			if(performancePanelDao != null){
				performancePanelDao.close();
			}
		}
		//节点id字符串
		StringBuffer deviceIds = new StringBuffer();
		if(perList != null){
			for(int i=0; i<perList.size(); i++){
				PerformancePanelModel panelModel = (PerformancePanelModel)perList.get(i);
				deviceIds.append(",");
				deviceIds.append(panelModel.getDeviceId());
				if(i == perList.size()-1){
					deviceIds.append(",");
				}
			}
		}
		request.setAttribute("deviceIds", deviceIds.toString());
		request.setAttribute("panelName", panelName);
		request.setAttribute("deviceType", deviceType);
		request.setAttribute("deviceList", deviceList);
		return "/performance/performancePanelChoseDevice.jsp";
	}
	
	private String delete(){
		String panelName = request.getParameter("panelName");
		
		//删除当前选中的面板
		PerformancePanelDao performancePanelDao = new PerformancePanelDao();
		try {
			performancePanelDao.deleteByName(panelName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			performancePanelDao.close();
		}
		//删除当前面板的指标中的数据
		PerformancePanelIndicatorsDao performancePanelIndicatorsDao = new PerformancePanelIndicatorsDao();
		try {
			performancePanelIndicatorsDao.deleteByPanelName(panelName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			performancePanelIndicatorsDao.close();
		}
		
		//监控管理列表
		//性能监控面板的table表格
		List<String> tableList = getPerformanceList();
		//返回监控管理的界面
		request.setAttribute("tableList", tableList);
		return "/performance/xnmb.jsp";
	}
	
	/**
	 * 第四部，创建性能指标面板
	 * @return
	 */
	private String createNewPanel(){
		//性能面板名称
		String panelName = getParaValue("panelName");
		//得到设备类型
		String deviceType = getParaValue("deviceType");
		//得到所有设备id
		String deviceIds = getParaValue("deviceIds");
		//得到所有告警指标名称
		String indicatorNames = getParaValue("indicatorNames");
		if(indicatorNames != null && !"null".equals(indicatorNames)){
			indicatorNames = indicatorNames.replaceAll("root,", "");
		}
		//面板信息入库
		List<PerformancePanelModel> performancePanelList = getPerformancePanelModelList(panelName, deviceType, deviceIds);
		PerformancePanelDao performancePanelDao = new PerformancePanelDao();
		try {
			performancePanelDao.savePreformance(performancePanelList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			performancePanelDao.close();
		}
		//面板对应的指标中间表信息入库 
		PerformancePanelIndicatorsDao performancePanelIndicatorsDao = new PerformancePanelIndicatorsDao();
		try {
			performancePanelIndicatorsDao.savePreformancePanelIndicators(panelName,indicatorNames);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			performancePanelIndicatorsDao.close();
		}
		
		//监控管理列表
		//性能监控面板的table表格
		rootPath = request.getContextPath();
		List<String> tableList = getPerformanceList();
		//返回监控管理的界面
		request.setAttribute("tableList", tableList);
		return "/performance/xnmb.jsp";
	}
	
	/**
	 * 获取性能面板列表
	 * @return
	 */
	public List<String> getPerformanceList(){
		List<PerformancePanelModel> allPaneList = null;
		PerformancePanelDao performancePanelDao = new PerformancePanelDao();
		try {
			allPaneList = performancePanelDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			performancePanelDao.close();
		}
		//面板对应的指标中间表信息入库 
		List<PerformancePanelIndicatorsModel> allPaneIndicatorsList = null;
		PerformancePanelIndicatorsDao performancePanelIndicatorsDao = new PerformancePanelIndicatorsDao();
		try {
			allPaneIndicatorsList = performancePanelIndicatorsDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			performancePanelIndicatorsDao.close();
		}
		//取出告警信息列表
		Hashtable<String, List<NodeIndicatorAlarm>> nodeIndicatorAlarmHash = null;
		NodeIndicatorAlarmDao nodeIndicatorAlarmDao = new NodeIndicatorAlarmDao();
		try {
			nodeIndicatorAlarmHash = nodeIndicatorAlarmDao.getNodeIndicaorAlarmHash();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			performancePanelIndicatorsDao.close();
		}
		
		//监控管理列表
		//性能监控面板的table表格
		List<String> tableList = new ArrayList<String>();
		//组合构造出所有的性能面板-以面板名称为单位  
		Hashtable<String, List<PerformancePanelModel>> allPanelHash = getPanelHash(allPaneList);
		//组合构造出所有面板对应的指标集合-以面板的名称为单位
		Hashtable<String, List<PerformancePanelIndicatorsModel>> allPanelIndicatorsHash = getPanelIndicatorsHash(allPaneIndicatorsList);
		Iterator<String> panelNamesIterator = allPanelHash.keySet().iterator();
		while(panelNamesIterator.hasNext()){
			String pName = panelNamesIterator.next();//面板名称
			List<PerformancePanelModel> performancePanelModelList = allPanelHash.get(pName);//面板设备id集合
			List<PerformancePanelIndicatorsModel> performancePanelIndicatorsModelList = allPanelIndicatorsHash.get(pName);//面板指标id集合
			//根据面板数据集合构造出页面展现的二维表格
			//根据内容动态构造出table表格
			StringBuffer tableBuffer = new StringBuffer();
			tableBuffer.append("<br><!--监控分组名称--!><table><tr><td><a href='#' onclick=\"display(\'table_");//添加隐藏的table动作
			tableBuffer.append(pName);
			tableBuffer.append("\',\'table_img_");
			tableBuffer.append(pName);
			tableBuffer.append("\')\"><img id='table_img_");
			tableBuffer.append(pName);
			tableBuffer.append("' src='");
			tableBuffer.append(rootPath);
			tableBuffer.append("/resource/image/tree/minus.gif'></a>  分组名称：");
			tableBuffer.append(pName);
			tableBuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;设备类型：");
			String deviceType = "net";
			if(performancePanelModelList != null && performancePanelModelList.size() != 0){
				deviceType = performancePanelModelList.get(0).getDeviceType();
				tableBuffer.append(getTypeDescByTypeStr(deviceType));
			}
			tableBuffer.append("</td><td align='right'>");
			tableBuffer.append("<a href='#' onclick=\"editPanelDevice(\'");
			tableBuffer.append(deviceType);
			tableBuffer.append("\',\'");
			tableBuffer.append(pName);
			tableBuffer.append("\')\"");
			tableBuffer.append("'>配置监控设备</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
			tableBuffer.append("<a href='#' onclick=\"editPanelIndicators(\'");
			tableBuffer.append(deviceType);
			tableBuffer.append("\',\'");
			tableBuffer.append(pName);
			tableBuffer.append("\')\">配置监控指标</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
			tableBuffer.append("<a onclick=\"confrimDeletePanel('");
			tableBuffer.append(pName);
			tableBuffer.append("')\" href='#'>删除监控分组</a></td></tr></table>");
			tableBuffer.append("<table class=\"data\" cellpadding=\"0\" cellspacing=\"0\" id='table_");
			tableBuffer.append(pName);
			tableBuffer.append("'><tr style='background-color: #ECECEC' >");
			tableBuffer.append("<td align=\"center\" class=\"body-data-title\">设备</td>");
			if(performancePanelIndicatorsModelList != null){
				for(int i=0; i<performancePanelIndicatorsModelList.size(); i++){//增加表头
					PerformancePanelIndicatorsModel pIndicators = performancePanelIndicatorsModelList.get(i);
					tableBuffer.append("<td align=\"center\" class=\"body-data-title\">");
					tableBuffer.append(pIndicators.getIndicatorDesc());//根据指标的名称 得到指标的描述
					tableBuffer.append("</td>");
				}
			}
			tableBuffer.append("</tr>");
			for(int i=0; i<performancePanelModelList.size(); i++){//增加表体
				PerformancePanelModel pModel = performancePanelModelList.get(i);
				//根据设备id和设备的类型  得到设备的超链接
				String url = getDeviceUrl(pModel.getDeviceId(), pModel.getDeviceType());
				tableBuffer.append("<tr onmouseout=\"this.style.background='#FFFFFF'\"  onmouseover=\"this.style.background='#AACCFF'\" id='tr_");
				tableBuffer.append(pModel.getName()+"_"+pModel.getDeviceId());
				tableBuffer.append("'>");
				tableBuffer.append("<td class=\"data_content center\"><a href='#' onclick=\"toDetailPage('");
				tableBuffer.append(url);
				tableBuffer.append("')\">");
				//根据设备id和设备的类型  得到设备名称
				tableBuffer.append(getDeviceName(pModel.getDeviceId(),pModel.getDeviceType()));
				tableBuffer.append("</a></td>");
				//告警信息及状态信息
				if(performancePanelIndicatorsModelList != null){
					for(int j=0; j<performancePanelIndicatorsModelList.size(); j++){//外层循环:单个面板的指标列表
						PerformancePanelIndicatorsModel panelIndicatorsModel = performancePanelIndicatorsModelList.get(j);
						String panelIndicatorsName = panelIndicatorsModel.getIndicatorName().trim();//面板中的某条告警指标名称
						//得到该节点的所有指标的告警信息的集合
						List<NodeIndicatorAlarm> nodeIndicatorAlarmList = nodeIndicatorAlarmHash.get(pModel.getDeviceId()+":"+pModel.getDeviceType());
						if(nodeIndicatorAlarmList == null){//第一种情形: 该设备无告警信息 
							tableBuffer.append("<td align='center' class=\"data_content center\"><img src='");
							tableBuffer.append(getCurrentStatusImage(0));
							tableBuffer.append("' alt='正常'/>&nbsp;");
							tableBuffer.append("</td>");
							continue;
						}
						//该设备存在告警信息时，比较告警信息的指标名称跟当前面板指标名称
						boolean hashAlarm = false;
						for(int k=0; k < nodeIndicatorAlarmList.size(); k++){//内层循环 告警列表
							NodeIndicatorAlarm nodeIndicatorAlarm = nodeIndicatorAlarmList.get(k);
							String nodeIndicatorAlarmName = nodeIndicatorAlarm.getIndicatorName().trim();//告警信息列表中的告警指标名称
							if(panelIndicatorsName.equalsIgnoreCase(nodeIndicatorAlarmName)){//告警指标名相同时   （第二种情形：该设备有告警信息，并且告警指标名跟面板中的指标表头同名时）
								tableBuffer.append("<td align='center' class=\"data_content center\"><img src='");
								tableBuffer.append(getCurrentStatusImage(Integer.parseInt(nodeIndicatorAlarm.getAlarmLevel())));
								tableBuffer.append("' alt='");
								tableBuffer.append(nodeIndicatorAlarm.getAlarmDesc());
								tableBuffer.append("'/>");
								tableBuffer.append("</td>");
								hashAlarm = true;
								continue;//退出循环体
							}
						}
						//（第三种情形：该设备有告警信息，告警指标名跟面板中的指标表头 不同名时）
						if(!hashAlarm){
							tableBuffer.append("<td align='center' class=\"data_content center\"><img src='");
							tableBuffer.append(getCurrentStatusImage(0));
							tableBuffer.append("' alt='正常");
							tableBuffer.append("'/>&nbsp;");
							tableBuffer.append("</td>");
						}
					}
				}
				tableBuffer.append("</tr>");
			}
			tableBuffer.append("</table>");
			tableList.add(tableBuffer.toString());
		}
		return tableList;
	}
	
	/**
	 * <p>此处超链接有问题，不正确（待修正）</p>
	 * 根据设备类型和设备的id得到的超连接->跳转到性能详细页  
	 * @param deviceId
	 * @param deviceType
	 * @return
	 */
	public static String getDeviceUrl(String deviceId, String deviceType){
		String url = null;
		if(deviceType == null){
			url = "";
		}else if(deviceType.equals("net") || deviceType.equals("host")){
			url = "/detail/dispatcher.jsp?id=net"+deviceId+"&flag=1&fromtopo=true";
		}else if(deviceType.equals("db")){
			url = "/detail/dispatcher.jsp?id=dbs"+deviceId+"&flag=1&fromtopo=true";
		}  
		return rootPath+url;
	}
	
	/**
	 * 根据设备类型的英文名得到其中文名
	 * @param deviceType
	 * @return
	 */
	public static String getTypeDescByTypeStr(String deviceType){
		String retString = null;
		if(deviceType == null){
			retString = "";
		}else if(deviceType.equals("net")){
			retString = "网络设备";
		}else if(deviceType.equals("host")){
			retString = "服务器";
		}else if(deviceType.equals("db")){
			retString = "数据库";
		}
		return retString;
	}
	
	/**
	 * 节点状态标志
	 */
	public static String getCurrentStatusImage(int status) {
		String image = null;
		if (status == 0)
			image = "a_level_0.gif";
		else if (status == 1)
			image = "a_level_1.gif";
		else if (status == 2)
			image = "a_level_2.gif";
		else if (status == 3)
			 image = "a_level_3.gif";
		else
			image = "unmanaged.gif";
		return rootPath+"/resource/image/topo/" + image;
	}
	
	/**
	 * 根据设备的id 以及设备类型 得到设备的名称
	 * @param id
	 * @param deviceType
	 * @return
	 */
	private String getDeviceName(String id, String deviceType){
		String name = null;
		if(deviceType.equals("net") || deviceType.equals("host")){
			Node node = (Node)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
			if(node == null){
				name = id;
				return name;
			}
			name = node.getAlias()+"("+node.getIpAddress()+")";
		}else if(deviceType.equals("db")){
			DBDao dbDao = new DBDao();
			DBVo dbvo = null;
			try {
				dbvo = (DBVo)dbDao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				dbDao.close();
			}
			if(dbvo != null){
				name = dbvo.getAlias()+"("+dbvo.getIpAddress()+")";
			}
		}
		return name;
	}
	
	/**
	 * 组合构造出所有的性能面板指标集合 key：面板名称  value：面板的指标集合
	 * @param allPaneList  所有的面板数据
	 * @return
	 */
	private Hashtable<String, List<PerformancePanelIndicatorsModel>> getPanelIndicatorsHash(
			List<PerformancePanelIndicatorsModel> allPaneIndicatorsList) {
		Hashtable<String, List<PerformancePanelIndicatorsModel>> allPanelHash = new Hashtable<String, List<PerformancePanelIndicatorsModel>>();
		//Set<id>
		Set<String> panelNameSet = new HashSet<String>();
		for(int i=0; i<allPaneIndicatorsList.size(); i++){
			panelNameSet.add(allPaneIndicatorsList.get(i).getPanelName());
		}
		Iterator<String> panelNameIterator = panelNameSet.iterator();
		while(panelNameIterator.hasNext()){
			String panelName = panelNameIterator.next();
			if(panelName == null){
				continue;
			}
			List<PerformancePanelIndicatorsModel> panelList = new ArrayList<PerformancePanelIndicatorsModel>();
			for(int i=0; i<allPaneIndicatorsList.size(); i++){
				if(panelName.equals(allPaneIndicatorsList.get(i).getPanelName())){
					panelList.add(allPaneIndicatorsList.get(i));
				}
			}
			allPanelHash.put(panelName, panelList);
		}
		return allPanelHash;
	}

	/**
	 * 组合构造出所有的性能面板 key：面板名称  value：面板的记录
	 * @param allPaneList  所有的面板数据
	 * @return
	 */
	private Hashtable<String, List<PerformancePanelModel>> getPanelHash(List<PerformancePanelModel> allPaneList){
		Hashtable<String, List<PerformancePanelModel>> allPanelHash = new Hashtable<String, List<PerformancePanelModel>>();
		//Set<id>
		Set<String> panelNameSet = new HashSet<String>();
		for(int i=0; i<allPaneList.size(); i++){
			panelNameSet.add(allPaneList.get(i).getName());
		}
		Iterator<String> panelNameIterator = panelNameSet.iterator();
		while(panelNameIterator.hasNext()){
			String panelName = panelNameIterator.next();
			if(panelName == null){
				continue;
			}
			List<PerformancePanelModel> panelList = new ArrayList<PerformancePanelModel>();
			for(int i=0; i<allPaneList.size(); i++){
				if(panelName.equals(allPaneList.get(i).getName())){
					panelList.add(allPaneList.get(i));
				}
			}
			allPanelHash.put(panelName, panelList);
		}
		return allPanelHash;
	}
	
	/**
	 * @param panelName      面板名称
	 * @param deviceType     面板中的设备类别
	 * @param deviceIds      面板中的所有设备的id
	 * @return
	 */
	private List<PerformancePanelModel> getPerformancePanelModelList(String panelName, String deviceType, String deviceIds){
		if(panelName == null){
			return null;
		}
		List<PerformancePanelModel> performancePanelList = new ArrayList<PerformancePanelModel>();
		String[] deviceIdsArry = deviceIds.split(",");
		for(String deviceId:deviceIdsArry){
			PerformancePanelModel panelModel = new PerformancePanelModel();
			panelModel.setDeviceId(deviceId);
			panelModel.setDeviceType(deviceType);
			panelModel.setName(panelName);
			performancePanelList.add(panelModel);
		}
		return performancePanelList;
	}
	
	/**
	 * 第三步，选择指标
	 * @return
	 */
	private String thirdStep(){
		//得到所有的节点id
		String[] nodeids = getParaArrayValue("selectedDevice");
		String deviceType = getParaValue("deviceType");
		String panelName = getParaValue("panelName");
		if(deviceType == null || deviceType.equals("deviceType")){
			deviceType = "net";
		}
		String deviceIds = getParaValue("deviceIds");
		if(deviceIds != null && !deviceIds.equals("null")){
			deviceIds = deviceIds.replaceAll("root,", "");
		}
		//根据节点的id取出所有指标的集合
		List alarmIndicatorsList = null;
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		try {
			alarmIndicatorsList = alarmIndicatorsDao.getByType(deviceType);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			alarmIndicatorsDao.close();
		}
		//指标集合存入request中
		request.setAttribute("alarmIndicatorsList", alarmIndicatorsList);
		request.setAttribute("nodeids", nodeids);
		request.setAttribute("panelName", panelName);
		request.setAttribute("deviceIds", deviceIds);
		request.setAttribute("deviceType", deviceType);
		return "/performance/addPerformancePanelChoseAlarmIndicators.jsp";
	}
	
	/**
	 * 第二步，选择设备
	 * @return
	 */
	private String secondStep(){
		//根据当前用户的权限得到设备资源树
		//默认的设备资源类型为网络设备  deviceType = 'net'
		List deviceList = null;
		String deviceType= getParaValue("deviceType");
		String panelName = getParaValue("panelName");
		int typeInt = 1;
		if(deviceType == null || deviceType.equals("null")){
			deviceType = "net";
		}
		if(deviceType.equals("net")){
			typeInt = 1;
		}else if(deviceType.equals("host")){
			typeInt = 4;
		}else if(deviceType.equals("db")){
			typeInt = 5;//数据库
		}
		String[] bids = getBids();
		deviceList = getDeviceList(1,typeInt,bids);
		request.setAttribute("panelName", panelName);
		request.setAttribute("deviceType", deviceType);
		request.setAttribute("deviceList", deviceList);
		return "/performance/addPerformancePanelChoseDevice.jsp";
	}
	
	/**
	 * 获取 list
	 * @param <code>DBVo</code>
	 * @return
	 */
	public List getDBList(String[] bids){
		List list = new ArrayList();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer s2 = new StringBuffer();
		int flag = 0;
		if (bids != null && bids.length > 0) {
			for (int i = 0; i < bids.length; i++) {
				if (bids[i].trim().length() > 0) {
					if (flag == 0) {
						//s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
						s2.append(" and ( bid like '%" + bids[i].trim() + "%' ");
						flag = 1;
					} else {
						//flag = 1;
						//s.append(" or bid like '%," + bids[i].trim() + ",%' ");
						s2.append(" or bid like '%" + bids[i].trim() + "%' ");
					}
				}
			}
			s2.append(") ");
		}
		sql2.append("select * from app_db_node where 1=1 " + s2.toString());
		
		DBDao dao = new DBDao();
		try {
			list = dao.findByCriteria(sql2.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return list;
	}
	
	/**
	 * @param managed   被管理的状态
	 * @param category  类别
	 * @param bids      业务id
	 * @return
	 */
	private List getDeviceList(int managed,int category,String[] bids){
		List deviceList = null;
		if(category == 1 || category == 4){//网络设备或者服务器
			HostNodeDao hostNodeDao = new HostNodeDao();
			try {
				deviceList = hostNodeDao.loadMonitorByMonCategory(managed,category,bids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				hostNodeDao.close();
			}
		}else{//数据库
			deviceList = getDBList(bids);
		}
		return deviceList;
	}
	
	/**
	 * 第一步，增加页面
	 * @return
	 */
	private String addperformancePanel(){
		return "/performance/addPerformancePanel.jsp";
	}
	
	/**
	 * 获得业务权限数组
	 * @return
	 */
	public String[] getBids(){
		String[] bids = null;
		User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		if (current_user.getBusinessids() != null){
			if(current_user.getBusinessids() !="-1"){
				bids = current_user.getBusinessids().split(",");
			}
		}
		return bids; 
	}
}
