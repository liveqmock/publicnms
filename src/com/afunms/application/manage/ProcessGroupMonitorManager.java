package com.afunms.application.manage;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.ProcessGroupDao;
import com.afunms.application.model.ProcessGroup;
import com.afunms.application.model.ProcessGroupConfiguration;
import com.afunms.application.util.ProcessGroupConfigurationUtil;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.indicators.util.Constant;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.system.model.TimeShareConfig;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;




public class ProcessGroupMonitorManager extends BaseManager implements ManagerInterface{

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
		}else if ("chooseProcess".equals(action)){
			return chooseProcess();
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
		String jsp = "/application/processgroup/list.jsp";
		setTarget(jsp);
		ProcessGroupDao processGroupDao = new ProcessGroupDao();
		list(processGroupDao , " where nodeid='" + nodeid + "'");
		return jsp;
	}
	
	/**
	 * 所有进程组的列表
	 * @return
	 */
	public String showlist(){
		
//		String ipaddress = getParaValue("ipaddress");
//		String nodeid = getParaValue("nodeid");
//		
//		request.setAttribute("ipaddress", ipaddress);
//		request.setAttribute("nodeid", nodeid);
		String jsp = "/application/processgroup/showlist.jsp";
		try {
			setTarget(jsp);
			ProcessGroupDao processGroupDao = new ProcessGroupDao();
			list(processGroupDao , getSQLWhere());
		} catch (RuntimeException e) {
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
				hostNodeHashtable.put(hostNode.getId() + "", hostNode);
			}
		}
		
		request.setAttribute("hostNodeHashtable", hostNodeHashtable);
		
		return jsp;
	}
	
	public String ready_add(){
		String jsp = "";
		String foward = getParaValue("foward");
		if("showadd".equals(foward)){
			jsp =  "/application/processgroup/showadd.jsp";
		} else {
			request.setAttribute("nodeid", getParaValue("nodeid"));
			request.setAttribute("ipaddress", getParaValue("ipaddress"));
			jsp = "/application/processgroup/add.jsp";
		}
		return jsp;
	}
	
	public String add(){
		
		ProcessGroup processGroup = createProcessGroup();
		List processGroupConfigurationList= createProcessGroupConfigurationList();
		ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
		processGroupConfigurationUtil.saveProcessGroupAndConfiguration(processGroup, processGroupConfigurationList);
		String forward = getParaValue("forward");
		if("showlist".equals(forward)){
			return showlist();
		} else {
			return list();
		}
		
	}
	
	public String delete(){
		String[] ids = getParaArrayValue("checkbox");
		ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
		processGroupConfigurationUtil.deleteProcessGroupAndConfiguration(ids);
		
		String foward = getParaValue("foward");
		if ("showlist".equals(foward)){
			return showlist();
		} else {
			return list();
		}
	}
	
	public String ready_edit(){
		
		String jsp = "/application/processgroup/edit.jsp";
		
		request.setAttribute("nodeid", getParaValue("nodeid"));
		request.setAttribute("ipaddress", getParaValue("ipaddress"));
		
		String groupId = getParaValue("groupId");
		ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
		List list = processGroupConfigurationUtil.getProcessGroupConfigurationByGroupId(groupId);
		
		ProcessGroup processGroup = processGroupConfigurationUtil.getProcessGroup(groupId);
		
		request.setAttribute("processGroup", processGroup);
		
		request.setAttribute("groupId", groupId);
		request.setAttribute("list", list);
		String forward = getParaValue("forward");
		if("showedit".equals(forward)){
			jsp = "/application/processgroup/showedit.jsp";
		}
		
		return jsp;
	}
	
	public String edit(){
		String groupId = getParaValue("groupId");
		ProcessGroup processGroup = createProcessGroup();
		processGroup.setId(Integer.parseInt(groupId));
		List processGroupConfigurationList= createProcessGroupConfigurationList();
		ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
		processGroupConfigurationUtil.updateProcessGroupAndConfiguration(processGroup, processGroupConfigurationList);
		String forward = getParaValue("forward");
		if("showlist".equals(forward)){
			return showlist();
		}else{
			return list();
		}
	}
	
	public String chooseProcess(){
		String jsp = "/application/processgroup/chooseprocesslist.jsp";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());
		String starttime = date + " 00:00:00";
		String endtime = date + " 23:59:59";
		String tmp = getParaValue("nodeid");
		String eventId = getParaValue("eventId");
		String ipaddress = getParaValue("ipaddress");
		String order = "MemoryUtilization";
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		Hashtable processhash = null;
		String runmodel = PollingEngine.getCollectwebflag();
		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		try {
			if("0".equals(runmodel)){
				//采集与访问是集成模式
				processhash = hostlastmanager.getProcess_share(host.getIpAddress(),"Process",order,starttime,endtime);
			}else{
				//采集与访问是分离模式
				processhash = hostlastmanager.getProcess(host.getIpAddress(),"Process",order,starttime,endtime); 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("processhash", processhash);
		request.setAttribute("eventId", eventId);
		return jsp;
	}
	
	public ProcessGroup createProcessGroup(){
		
		String name = getParaValue("name");
		
		String ipaddress = getParaValue("ipaddress");
		
		String nodeid = getParaValue("nodeid");
		
		String mon_flag = getParaValue("mon_flag");
		
		String alarm_level = getParaValue("alarm_level");
		
		ProcessGroup processGroup = new ProcessGroup();
		processGroup.setIpaddress(ipaddress);
		processGroup.setName(name);
		processGroup.setNodeid(nodeid);
		processGroup.setMon_flag(mon_flag);
		processGroup.setAlarm_level(alarm_level);
		
		return processGroup;
	}
	
	public List createProcessGroupConfigurationList(){
		List processGroupConfigurationList = new ArrayList(); 
		String rowNum = request.getParameter("rowNum");
		int rowNum_int = 0;
		try {
			System.out.println(rowNum.equals("")+"======================");
			if(rowNum.equals("")){
				
			}else{
				rowNum_int = Integer.parseInt(rowNum);
			}
//			rowNum_int = Integer.parseInt(rowNum);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int num = rowNum_int;
		for (int i = 1; i <= num; i++) {
   			String partName = "";
   			if (i < 10) {
   				partName = "0" + String.valueOf(i);
   			} else {
   				partName = String.valueOf(i);
   			}
   			
			String processName = request.getParameter("processName" + partName);
			String processStatus = request.getParameter("processStatus" + partName);
			String processTimes = request.getParameter("processTimes" + partName);
			if(processName == null || processName.trim().length() == 0){
				continue;
			}
			
			if(processTimes == null || processTimes.trim().length() == 0){
				continue;
			}
			ProcessGroupConfiguration processGroupConfiguration = new ProcessGroupConfiguration();
			processGroupConfiguration.setName(processName);
			processGroupConfiguration.setStatus(processStatus);
			processGroupConfiguration.setTimes(processTimes);
			processGroupConfigurationList.add(processGroupConfiguration);
   		}
   		return processGroupConfigurationList;
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
		String processgroupname = getParaValue("processgroupname");
		if(processgroupname!=null && !"null".equals(processgroupname) && processgroupname.length()>0){
			sql = " and name like '%"+ processgroupname +"%'";
		}
		request.setAttribute("processgroupname", processgroupname);
		return sql;

	}
}
