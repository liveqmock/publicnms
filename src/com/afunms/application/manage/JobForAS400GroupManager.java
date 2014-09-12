package com.afunms.application.manage;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.JobForAS400GroupDao;
import com.afunms.application.dao.JobForAS400SubSystemDao;
import com.afunms.application.model.JobForAS400Group;
import com.afunms.application.model.JobForAS400GroupDetail;
import com.afunms.application.model.JobForAS400SubSystem;
import com.afunms.application.util.JobForAS400GroupDetailUtil;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.util.Constant;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.JobForAS400Dao;
import com.afunms.topology.model.SubsystemForAS400;
import com.afunms.topology.util.JobConstantForAS400;
import com.afunms.topology.util.KeyGenerator;




public class JobForAS400GroupManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {
		// TODO Auto-generated method stub
		if("list".equals(action)){
			return list();
		}else if ("subsystemlist".equals(action)){
			return subsystemlist();
		}else if ("add".equals(action)){
			return add();
		}else if ("subsystemadd".equals(action)){
			return subsystemadd();
		}else if ("save".equals(action)){
			return save();
		}else if ("savesubsystem".equals(action)){
			return savesubsystem();
		}else if ("delete".equals(action)){
			return delete();
		}else if ("subsystemdelete".equals(action)){
			return subsystemdelete();
		}else if ("edit".equals(action)){
			return edit();
		}else if ("subsystemedit".equals(action)){
			return subsystemedit();
		}else if ("update".equals(action)){
			return update();
		}else if ("updatesubsystem".equals(action)){
			return updatesubsystem();
		}else if ("chooseJobForAS400".equals(action)){
			return chooseJobForAS400();
		}else if ("chooseSubSystemForAS400".equals(action)){
			return chooseSubSystemForAS400();
		}else if ("showlist".equals(action)){
			return showlist();
		}else if ("chooseJobActiveStatus".equals(action)){
			return chooseJobActiveStatus();
		}
		return null;
	}
	
	public String subsystemlist(){
		
		String ipaddress = getParaValue("ipaddress");
		String nodeid = getParaValue("nodeid");
		
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("nodeid", nodeid);
		String jsp = "/application/jobforas400/subsystemlist.jsp";
		setTarget(jsp);
		JobForAS400SubSystemDao jobForAS400SubSystemDao = new JobForAS400SubSystemDao();
		try {
			list(jobForAS400SubSystemDao , " where nodeid='" + nodeid + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jobForAS400SubSystemDao.close();
		}
		return jsp;
	}
	
	public String list(){
		
		String ipaddress = getParaValue("ipaddress");
		String nodeid = getParaValue("nodeid");
		
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("nodeid", nodeid);
		String jsp = "/application/jobforas400/list.jsp";
		setTarget(jsp);
		JobForAS400GroupDao jobForAS400GroupDao = new JobForAS400GroupDao();
		try {
			list(jobForAS400GroupDao , " where nodeid='" + nodeid + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jobForAS400GroupDao.close();
		}
		return jsp;
	}
	
	/**
	 * 所有进程组的列表
	 * @return
	 */
	public String showlist(){
		
////		String ipaddress = getParaValue("ipaddress");
////		String nodeid = getParaValue("nodeid");
////		
////		request.setAttribute("ipaddress", ipaddress);
////		request.setAttribute("nodeid", nodeid);
		String jsp = "/application/processgroup/showlist.jsp";
//		try {
//			setTarget(jsp);
//			ProcessGroupDao processGroupDao = new ProcessGroupDao();
////			list(processGroupDao , getSQLWhere());
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		List hostNodeList = null;
//		HostNodeDao hostNodeDao =  new HostNodeDao();
//		try {
//			hostNodeList = hostNodeDao.loadAll();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			hostNodeDao.close();
//		}
//		
//		Hashtable hostNodeHashtable = new Hashtable();
//		if(hostNodeList != null){
//			for(int i = 0 ; i < hostNodeList.size() ; i++){
//				HostNode hostNode = (HostNode)hostNodeList.get(i);
//				hostNodeHashtable.put(hostNode.getId() + "", hostNode);
//			}
//		}
//		
//		request.setAttribute("hostNodeHashtable", hostNodeHashtable);
//		
		return jsp;
	}
	
	public String subsystemadd(){
		String jsp = "";
		request.setAttribute("nodeid", getParaValue("nodeid"));
		request.setAttribute("ipaddress", getParaValue("ipaddress"));
		jsp = "/application/jobforas400/addsubsystem.jsp";
		return jsp;
	}
	
	public String add(){
		String jsp = "";
		String foward = getParaValue("foward");
		if("showadd".equals(foward)){
			jsp =  "/application/jobforas400/showadd.jsp";
		} else {
			request.setAttribute("nodeid", getParaValue("nodeid"));
			request.setAttribute("ipaddress", getParaValue("ipaddress"));
			jsp = "/application/jobforas400/add.jsp";
		}
		return jsp;
	}
	   
	public String savesubsystem(){
		
		JobForAS400SubSystem jobForAS400SubSystem = createJobForAS400SubSystem();
		JobForAS400SubSystemDao jobForAS400SubSystemDao = new JobForAS400SubSystemDao();
		try {
			jobForAS400SubSystem.setId(KeyGenerator.getInstance().getNextKey());
			jobForAS400SubSystemDao.save(jobForAS400SubSystem);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jobForAS400SubSystemDao.close();
		}
		return subsystemlist();
		
	}
	
	public String save(){
		
		JobForAS400Group jobForAS400Group = createJobForAS400Group();
		List jobForAS400GroupDetialList= createJobForAS400GroupDetailList();
		JobForAS400GroupDetailUtil jobForAS400GroupDetailUtil = new JobForAS400GroupDetailUtil();
		jobForAS400GroupDetailUtil.saveProcessGroupAndDetail(jobForAS400Group, jobForAS400GroupDetialList);
		String forward = getParaValue("forward");
		if("showlist".equals(forward)){
			return showlist();
		} else {
			return list();
		}
		
	}
	
	public String subsystemdelete(){
		String[] ids = getParaArrayValue("checkbox");
		JobForAS400SubSystemDao jobForAS400SubSystemDao = new JobForAS400SubSystemDao();
		try {
			jobForAS400SubSystemDao.delete(ids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jobForAS400SubSystemDao.close();
		}
		return subsystemlist();
	}
	
	public String delete(){
		String[] ids = getParaArrayValue("checkbox");
		JobForAS400GroupDetailUtil jobForAS400GroupDetailUtil = new JobForAS400GroupDetailUtil();
		jobForAS400GroupDetailUtil.deleteJobForAS400GroupAndDetail(ids);
		
		String foward = getParaValue("foward");
		if ("showlist".equals(foward)){
			return showlist();
		} else {
			return list();
		}
	}
	
	public String subsystemedit(){
		
		String jsp = "/application/jobforas400/editsubsystem.jsp";
		
		request.setAttribute("nodeid", getParaValue("nodeid"));
		request.setAttribute("ipaddress", getParaValue("ipaddress"));
		
		String groupId = getParaValue("groupId");
		JobForAS400SubSystem jobForAS400SubSystem = null;
		JobForAS400SubSystemDao jobForAS400SubSystemDao = new JobForAS400SubSystemDao();
		try {
			jobForAS400SubSystem = (JobForAS400SubSystem)jobForAS400SubSystemDao.findByID(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jobForAS400SubSystemDao.close();
		}
		
		request.setAttribute("jobForAS400SubSystem", jobForAS400SubSystem);
		request.setAttribute("groupId", groupId);
		
		return jsp;
	}
	
	public String edit(){
		
		String jsp = "/application/jobforas400/edit.jsp";
		
		request.setAttribute("nodeid", getParaValue("nodeid"));
		request.setAttribute("ipaddress", getParaValue("ipaddress"));
		
		String groupId = getParaValue("groupId");
		JobForAS400GroupDetailUtil jobForAS400GroupDetailUtil = new JobForAS400GroupDetailUtil();
		List list = jobForAS400GroupDetailUtil.getJobForAS400GroupDetailByGroupId(groupId);
		
		JobForAS400Group jobForAS400Group = jobForAS400GroupDetailUtil.getJobForAS400Group(groupId);
		
		request.setAttribute("jobForAS400Group", jobForAS400Group);
		
		request.setAttribute("groupId", groupId);
		request.setAttribute("list", list);
		String forward = getParaValue("forward");
		if("showedit".equals(forward)){
			jsp = "/application/jobforas400/showedit.jsp";
		}
		
		return jsp;
	}

	public String updatesubsystem(){
		String groupId = getParaValue("groupId");
		JobForAS400SubSystem jobForAS400SubSystem = createJobForAS400SubSystem();
		JobForAS400SubSystemDao jobForAS400SubSystemDao = new JobForAS400SubSystemDao();
		try {
			jobForAS400SubSystem.setId(Integer.parseInt(groupId));
			jobForAS400SubSystemDao.update(jobForAS400SubSystem);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jobForAS400SubSystemDao.close();
		}
		return subsystemlist();
	}
	
	public String update(){
		String groupId = getParaValue("groupId");
		JobForAS400Group jobForAS400Group = createJobForAS400Group();
		jobForAS400Group.setId(Integer.parseInt(groupId));
		List list = createJobForAS400GroupDetailList();
		JobForAS400GroupDetailUtil jobForAS400GroupDetailUtil = new JobForAS400GroupDetailUtil();
		jobForAS400GroupDetailUtil.updateJobForAS400GroupAndDetail(jobForAS400Group, list);
		String forward = getParaValue("forward");
		if("showlist".equals(forward)){
			return showlist();
		}else{
			return list();
		}
	}
	
	public String chooseSubSystemForAS400(){
		String jsp = "/application/jobforas400/choosesubsystemforas400list.jsp";
		String nodeid = getParaValue("nodeid");
		String eventId = getParaValue("eventId");
		String ipaddress = getParaValue("ipaddress");
		
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.valueOf(nodeid));
    	Hashtable hashtable= (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
    	Hashtable jobHashtable = new Hashtable();
    	if(hashtable!=null){
			List list = (List)hashtable.get("subSystem");
			if(list!=null){
				System.out.println(list.size()+"============list.size(0================");
				for(int i = 0 ; i < list.size(); i++){
					SubsystemForAS400 subsystemForAS400 = (SubsystemForAS400)list.get(i);
					JobForAS400Dao jobForAS400Dao = new JobForAS400Dao();
					List joblist = null;
					try {
						joblist = jobForAS400Dao.findByNodeidAndPath(nodeid, subsystemForAS400.getPath());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(joblist == null){
						joblist = new ArrayList();
					}
					jobHashtable.put(subsystemForAS400, joblist);
				}
			}
			request.setAttribute("list", list);
		}
    	request.setAttribute("jobHashtable", jobHashtable);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("eventId", eventId);
		request.setAttribute("nodeid", nodeid);
		return jsp;
	}
	
	public String chooseJobForAS400(){
		String jsp = "/application/jobforas400/choosejobforas400list.jsp";
		String nodeid = getParaValue("nodeid");
		String eventId = getParaValue("eventId");
		String ipaddress = getParaValue("ipaddress");
		
		JobForAS400Dao jobForAS400Dao = new JobForAS400Dao();
		List list = null ;
		try {
			list = jobForAS400Dao.findByNodeid(nodeid);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			jobForAS400Dao.close();
		}
		request.setAttribute("list", list);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("eventId", eventId);
		request.setAttribute("nodeid", nodeid);
		return jsp;
	}
	
	public String chooseJobActiveStatus(){
		String jsp = "/application/jobforas400/choosejobactivestatus.jsp";
		String nodeid = getParaValue("nodeid");
		String eventId = getParaValue("eventId");
		String ipaddress = getParaValue("ipaddress");
		
		
		request.setAttribute("hashtable", JobConstantForAS400.getActiveStatushashtable());
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("eventId", eventId);
		request.setAttribute("nodeid", nodeid);
		
		return jsp;
	}
	
	public JobForAS400SubSystem createJobForAS400SubSystem(){
		
		String name = getParaValue("name");
		
		String ipaddress = getParaValue("ipaddress");
		
		String nodeid = getParaValue("nodeid");
		
		String mon_flag = getParaValue("mon_flag");
		
		String alarm_level = getParaValue("alarm_level");
		
		String jobActiveStatusType = getParaValue("jobActiveStatusType");
		
		String jobActiveStatus = getParaValue("jobActiveStatus");
		
		String num = getParaValue("num");
		
		JobForAS400SubSystem jobForAS400SubSystem = new JobForAS400SubSystem();
		jobForAS400SubSystem.setIpaddress(ipaddress);
		jobForAS400SubSystem.setName(name);
		jobForAS400SubSystem.setNodeid(nodeid);
		jobForAS400SubSystem.setMon_flag(mon_flag);
		jobForAS400SubSystem.setAlarm_level(alarm_level);
		jobForAS400SubSystem.setActive_status(jobActiveStatus);
		jobForAS400SubSystem.setActive_status_type(jobActiveStatusType);
		jobForAS400SubSystem.setNum(num);
		
		return jobForAS400SubSystem;
	}
	
	public JobForAS400Group createJobForAS400Group(){
		
		String name = getParaValue("name");
		
		String ipaddress = getParaValue("ipaddress");
		
		String nodeid = getParaValue("nodeid");
		
		String mon_flag = getParaValue("mon_flag");
		
		String alarm_level = getParaValue("alarm_level");
		
		JobForAS400Group jobForAS400Group = new JobForAS400Group();
		jobForAS400Group.setIpaddress(ipaddress);
		jobForAS400Group.setName(name);
		jobForAS400Group.setNodeid(nodeid);
		jobForAS400Group.setMon_flag(mon_flag);
		jobForAS400Group.setAlarm_level(alarm_level);
		
		return jobForAS400Group;
	}
	
	public List createJobForAS400GroupDetailList(){
		List jobForAS400GroupDetailList = new ArrayList(); 
		String rowNum = request.getParameter("rowNum");
		int rowNum_int = 0;
		try {
			rowNum_int = Integer.parseInt(rowNum);
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
   			
			String jobName = request.getParameter("jobName" + partName);
			String jobNum = request.getParameter("jobNum" + partName);
			String jobStatus = request.getParameter("jobStatus" + partName);
			String jobActiveStatus = request.getParameter("jobActiveStatus" + partName);
			String jobActiveStatusType = request.getParameter("jobActiveStatusType" + partName);
			if(jobName == null || jobName.trim().length() == 0){
				continue;
			}
			
			if(jobStatus == null || jobStatus.trim().length() == 0){
				continue;
			}
			JobForAS400GroupDetail jobForAS400GroupDetail = new JobForAS400GroupDetail();
			jobForAS400GroupDetail.setName(jobName);
			jobForAS400GroupDetail.setNum(jobNum);
			jobForAS400GroupDetail.setStatus(jobStatus);
			jobForAS400GroupDetail.setActiveStatus(jobActiveStatus);
			jobForAS400GroupDetail.setActiveStatusType(jobActiveStatusType);
			jobForAS400GroupDetailList.add(jobForAS400GroupDetail);
   		}
   		return jobForAS400GroupDetailList;
	}
	
	
//	public String chooseNode(){
//		String jsp = "/application/processgroup/choosenodelist.jsp";
//		String nodeIdevent = getParaValue("nodeIdevent");
//		String ipaddressevent = getParaValue("ipaddressevent");
//		try {
//			User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
//			NodeUtil nodeUtil = new NodeUtil();
//			nodeUtil.setBid(user.getBusinessids());
//			List<BaseVo> list = nodeUtil.getNodeByTyeAndSubtype(Constant.TYPE_HOST, Constant.TYPE_HOST_SUBTYPE_WINDOWS);
//			List nodeDTOlist = nodeUtil.conversionToNodeDTO(list);
//			request.setAttribute("nodeDTOlist", nodeDTOlist);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		request.setAttribute("nodeIdevent", nodeIdevent);
//		request.setAttribute("ipaddressevent", ipaddressevent);
//		System.out.println(ipaddressevent);
//		return jsp;
//	}
//	
//	
//	private String getSQLWhere(){
//		String sql = " where 1=1";
//		
//		String ipSQL = getIpSQL();
//		
//		String processgroupNameSQL = getProcessgroupNameSQL();
//		
//		sql = sql + ipSQL + processgroupNameSQL;
//		
//		return sql;
//	}
//	
//	private String getIpSQL(){
//		String sql = "";
//		String ipaddress = getParaValue("ipaddress");
//		if(ipaddress!=null && !"null".equals(ipaddress) && ipaddress.length()>0){
//			sql = " and ipaddress like '%"+ ipaddress +"%'";
//		}
//		request.setAttribute("ipaddress", ipaddress);
//		return sql;
//	}
//	
//	private String getProcessgroupNameSQL(){
//		String sql = "";
//		String processgroupname = getParaValue("processgroupname");
//		if(processgroupname!=null && !"null".equals(processgroupname) && processgroupname.length()>0){
//			sql = " and name like '%"+ processgroupname +"%'";
//		}
//		request.setAttribute("processgroupname", processgroupname);
//		return sql;
//
//	}
}
