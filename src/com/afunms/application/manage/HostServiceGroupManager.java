package com.afunms.application.manage;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;


import com.afunms.application.dao.HostServiceGroupDao;
import com.afunms.application.model.HostServiceGroup;
import com.afunms.application.model.HostServiceGroupConfiguration;
import com.afunms.application.util.HostServiceGroupConfigurationUtil;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.detail.service.serviceInfo.ServiceInfoService;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.Constant;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.ProcessNetData;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Servicecollectdata;
import com.afunms.system.model.User;
import com.afunms.temp.model.ServiceNodeTemp;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;





public class HostServiceGroupManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {
		// TODO Auto-generated method stub
		if("list".equals(action)){
			return list();
		}else if ("ready_add".equals(action)){
			return ready_add();
		}else if ("add".equals(action)){
			return add();
		}else if ("delete".equals(action)){
			return delete();
		}else if ("ready_edit".equals(action)){
			return ready_edit();
		}else if ("edit".equals(action)){
			return edit();
		}else if ("chooseHostService".equals(action)){
			return chooseHostService();
		}else if ("showlist".equals(action)){
			return showlist();
		}else if ("chooseNode".equals(action)){
			return chooseNode();
		}
		return null;
	}
	
	
	public String list(){
		
		String ipaddress = getParaValue("ipaddress");
		String nodeid = getParaValue("nodeid");
		
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("nodeid", nodeid);
		String jsp = "/application/hostservicegroup/list.jsp";
		List list = new ArrayList();
		HostServiceGroupDao hostServiceGroupDao = new HostServiceGroupDao();
		try {
			list = hostServiceGroupDao.findByCondition(" where nodeid='" + nodeid + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			hostServiceGroupDao.close();
		}
		request.setAttribute("list", list);
		return jsp;
	}
	
	public String ready_add(){
		request.setAttribute("nodeid", getParaValue("nodeid"));
		request.setAttribute("ipaddress", getParaValue("ipaddress"));
		String jsp = "";
		String foward = getParaValue("foward");
		if("showadd".equals(foward)){
			jsp =  "/application/hostservicegroup/showadd.jsp";
		} else {
			request.setAttribute("nodeid", getParaValue("nodeid"));
			request.setAttribute("ipaddress", getParaValue("ipaddress"));
			jsp = "/application/hostservicegroup/add.jsp";
		}
		return jsp;
	}
	
	public String add(){
		
		HostServiceGroup hostservicegroup = createhostservicegroup();
		List hostservicegroupConfigurationList= createhostservicegroupConfigurationList();
		HostServiceGroupConfigurationUtil hostservicegroupConfigurationUtil = new HostServiceGroupConfigurationUtil();
		hostservicegroupConfigurationUtil.savehostservicegroupAndConfiguration(hostservicegroup, hostservicegroupConfigurationList);
		String forward = getParaValue("forward");
		if("showlist".equals(forward)){
			return showlist();
		} else {
			return list();
		}
	}
	
	public String delete(){
		String[] ids = getParaArrayValue("checkbox");
		HostServiceGroupConfigurationUtil hostservicegroupConfigurationUtil = new HostServiceGroupConfigurationUtil();
		hostservicegroupConfigurationUtil.deletehostservicegroupAndConfiguration(ids);
		String foward = getParaValue("foward");
		if ("showlist".equals(foward)){
			return showlist();
		} else {
			return list();
		}
	}
	
	public String ready_edit(){
		
		String jsp = "/application/hostservicegroup/edit.jsp";
		
		request.setAttribute("nodeid", getParaValue("nodeid"));
		request.setAttribute("ipaddress", getParaValue("ipaddress"));
		
		String groupId = getParaValue("groupId");
		
		HostServiceGroupConfigurationUtil hostservicegroupConfigurationUtil = new HostServiceGroupConfigurationUtil();
		List list = hostservicegroupConfigurationUtil.gethostservicegroupConfigurationByGroupId(groupId);
		
		HostServiceGroup hostservicegroup = hostservicegroupConfigurationUtil.gethostservicegroup(groupId);
		
		request.setAttribute("hostservicegroup", hostservicegroup);
		
		request.setAttribute("groupId", groupId);
		
		request.setAttribute("list", list);
		
		String forward = getParaValue("forward");
		if("showedit".equals(forward)){
			jsp = "/application/hostservicegroup/showedit.jsp";
		}
		
		return jsp;
	}
	
	public String edit(){
		String groupId = getParaValue("groupId");
		HostServiceGroup hostservicegroup = createhostservicegroup();
		hostservicegroup.setId(Integer.parseInt(groupId));
		List hostservicegroupConfigurationList= createhostservicegroupConfigurationList();
		HostServiceGroupConfigurationUtil hostservicegroupConfigurationUtil = new HostServiceGroupConfigurationUtil();
		hostservicegroupConfigurationUtil.updatehostservicegroupAndConfiguration(hostservicegroup, hostservicegroupConfigurationList);
		String forward = getParaValue("forward");
		if("showlist".equals(forward)){
			return showlist();
		}else{
			return list();
		}
	}
	
	public String chooseHostService(){
		String jsp = "/application/hostservicegroup/choosehostservicelist.jsp";
		String tmp = getParaValue("nodeid");
		String eventId = getParaValue("eventId");
		String ipaddress = getParaValue("ipaddress");
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		
		Vector serviceV = new Vector();
		List serviceList = null;
		String runmodel = PollingEngine.getCollectwebflag();//系统运行模式
		NodeUtil nodeUtil = new NodeUtil();
       	NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
       	if("0".equals(runmodel)){//采集与访问是集成模式
       		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
       		//WINDOWS服务器WMI采集
       		if(host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI && host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")){
       			serviceList = (ArrayList)ipAllData.get("service");
       			for(int i=0; i<serviceList.size(); i++){
       	   			Object obj = serviceList.get(i);
       	   			if(obj instanceof Hashtable){
       	   				Hashtable serviceItemHash = (Hashtable)obj;
       	   				ProcessNetData processNetData = new ProcessNetData();
       	   				ServiceNodeTemp serviceNodeTemp = processNetData.getServiceNodeTempByHashtable(serviceItemHash);
       	   				Servicecollectdata servicecollectdata = new Servicecollectdata();
       	   				try {
       						BeanUtils.copyProperties(servicecollectdata,serviceNodeTemp);
       					} catch (IllegalAccessException e) {
       						e.printStackTrace();
       					} catch (InvocationTargetException e) {
       						e.printStackTrace();
       					}
       	   				serviceV.add(servicecollectdata);
       	   			}
       	   		}
       		}else{//WINDOWS SNMP采集
       			serviceV = (Vector)ipAllData.get("winservice");
       		}
       	}else{//采集与访问是分离模式
       		//得到service信息
			ServiceInfoService serviceInfoService = new ServiceInfoService(nodedto.getId()+"",nodedto.getType(),nodedto.getSubtype());
			serviceList = serviceInfoService.getServicelistInfoAll(); 
			for(int i=0; i<serviceList.size(); i++){
   	   			Object obj = serviceList.get(i);
   	   			if(obj instanceof Hashtable){
   	   				Hashtable serviceItemHash = (Hashtable)obj;
   	   				ProcessNetData processNetData = new ProcessNetData();
   	   				ServiceNodeTemp serviceNodeTemp = processNetData.getServiceNodeTempByHashtable(serviceItemHash);
   	   				Servicecollectdata servicecollectdata = new Servicecollectdata();
   	   				try {
   						BeanUtils.copyProperties(servicecollectdata,serviceNodeTemp);
   					} catch (IllegalAccessException e) {
   						e.printStackTrace();
   					} catch (InvocationTargetException e) {
   						e.printStackTrace();
   					}
   	   				serviceV.add(servicecollectdata);
   	   			}
   	   		}
       	}
       	if(serviceList == null){
       		serviceList = new ArrayList();
       	}
//		Vector serviceV = (Vector)ipAllData.get("winservice");
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("serviceV", serviceV);
		request.setAttribute("eventId", eventId);
		return jsp;
	}
	
	public HostServiceGroup createhostservicegroup(){
		
		String name = getParaValue("name");
		
		String ipaddress = getParaValue("ipaddress");
		
		String nodeid = getParaValue("nodeid");
		
		String mon_flag = getParaValue("mon_flag");
		
		String alarm_level = getParaValue("alarm_level");
		
		HostServiceGroup hostservicegroup = new HostServiceGroup();
		hostservicegroup.setIpaddress(ipaddress);
		hostservicegroup.setName(name);
		hostservicegroup.setNodeid(nodeid);
		hostservicegroup.setMon_flag(mon_flag);
		hostservicegroup.setAlarm_level(alarm_level);
		
		return hostservicegroup;
	}
	
	public List createhostservicegroupConfigurationList(){
		List hostservicegroupConfigurationList = new ArrayList(); 
		int num = 0;
		if(request.getParameter("rowNum")!=null && request.getParameter("rowNum").trim().length() > 0 ){
			num = Integer.parseInt(request.getParameter("rowNum"));
		}
		 
		for (int i = 1; i <= num; i++) {
   			String partName = "";
   			if (i < 10) {
   				partName = "0" + String.valueOf(i);
   			} else {
   				partName = String.valueOf(i);
   			}
   			
			String serviceName = request.getParameter("hostServiceName" + partName);
			String serviceStatus = request.getParameter("hostServiceStatus" + partName);
			if(serviceName == null || serviceName.trim().length() == 0){
				continue;
			}
			
			if(serviceStatus == null || serviceStatus.trim().length() == 0){
				continue;
			}
			HostServiceGroupConfiguration hostservicegroupConfiguration = new HostServiceGroupConfiguration();
			hostservicegroupConfiguration.setName(serviceName);
			hostservicegroupConfiguration.setStatus(serviceStatus);
			hostservicegroupConfigurationList.add(hostservicegroupConfiguration);
   		}
   		return hostservicegroupConfigurationList;
	}
	
	
	/**
	 * 所有服务组的列表
	 * @return
	 */
	public String showlist(){
		
//		String ipaddress = getParaValue("ipaddress");
//		String nodeid = getParaValue("nodeid");
//		
//		request.setAttribute("ipaddress", ipaddress);
//		request.setAttribute("nodeid", nodeid);
		String jsp = "/application/hostservicegroup/showlist.jsp";
		try {
			setTarget(jsp);
			HostServiceGroupDao hostServiceGroupDao = new HostServiceGroupDao();
			list(hostServiceGroupDao , getSQLWhere());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List hostNodeList = null;
		HostNodeDao hostNodeDao =  new HostNodeDao();
		try {
			hostNodeList = hostNodeDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			hostNodeDao.close();
		}
		
		Hashtable hostNodeHashtable = new Hashtable();
		if(hostNodeList != null){
			for(int i = 0 ; i < hostNodeList.size() ; i++){
				HostNode hostNode = (HostNode)hostNodeList.get(i);
				//SysLogger.info(hostNode.getAlias()+"##############getAlias");
				hostNodeHashtable.put(hostNode.getId() + "", hostNode);
			}
		}
		
		request.setAttribute("hostNodeHashtable", hostNodeHashtable);
		
		return jsp;
	}
	
	public String chooseNode(){
		String jsp = "/application/processgroup/choosenodelist.jsp";
		String nodeIdevent = getParaValue("nodeIdevent");
		String ipaddressevent = getParaValue("ipaddressevent");
		try {
			User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
			NodeUtil nodeUtil = new NodeUtil();
			nodeUtil.setBid(user.getBusinessids());
			List<BaseVo> list = nodeUtil.getNodeByTyeAndSubtype(Constant.TYPE_HOST, Constant.TYPE_HOST);
			List nodeDTOlist = nodeUtil.conversionToNodeDTO(list);
			request.setAttribute("nodeDTOlist", nodeDTOlist);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("nodeIdevent", nodeIdevent);
		request.setAttribute("ipaddressevent", ipaddressevent);
		System.out.println(ipaddressevent);
		return jsp;
	}

	
	private String getSQLWhere(){
		String sql = " where 1=1";
		
		String ipSQL = getIpSQL();
		
		String processgroupNameSQL = getProcessgroupNameSQL();
		
		sql = sql + ipSQL + processgroupNameSQL;
		
		return sql;
	}
	
	private String getIpSQL(){
		String sql = "";
		String ipaddress = getParaValue("ipaddress");
		if(ipaddress!=null && !"null".equals(ipaddress) && ipaddress.length()>0){
			sql = " and ipaddress like '%"+ ipaddress +"%'";
		}
		request.setAttribute("ipaddress", ipaddress);
		return sql;
	}
	
	private String getProcessgroupNameSQL(){
		String sql = "";
		String hostservicegroupname = getParaValue("hostservicegroupname");
		if(hostservicegroupname!=null && !"null".equals(hostservicegroupname) && hostservicegroupname.length()>0){
			sql = " and name like '%"+ hostservicegroupname +"%'";
		}
		request.setAttribute("hostservicegroupname", hostservicegroupname);
		return sql;

	}
}
