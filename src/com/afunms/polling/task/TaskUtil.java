package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.model.IpaddressPanel;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.Constant;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Task;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.util.XmlOperator;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：May 8, 2011 10:34:10 AM
 * Task任务工具类
 */
public class TaskUtil {

	/**
	 * 增加采集定时任务
	 */
	public void addTask(List<String> nodeidList){
		if(nodeidList == null){
			return;
		}
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = null;
		long delay = 1000L;
		long period = 5*60*1000;
		Hashtable docollectHash = null;
		for(String nodeid:nodeidList){
			//判断是网络设备还是服务器
			//Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid));
			nodedto = nodeUtil.creatNodeDTOByNode((Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid))); 
			if(Constant.TYPE_NET.equals(nodedto.getType())){//网络设备
				
				//网络设备5分钟采集
				if(!ShareData.getM5TimerMap().containsKey(nodeid)){
					docollectHash = M5TaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						//List dolist = (ArrayList)docollectHash.get(nodeid);
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						M5TaskTest m5Task = new M5TaskTest(); 
						m5Task.setNodeid(nodeid);
						delay = 1000L;
						//period = 5*60*1000;
						period = 5*60*1000;
						try { 
							timer.schedule(m5Task, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getM5TimerMap().put(nodeid, timer);
						//dolist = null;
					}
					docollectHash = null;
				}
				
				//网络设备10分钟采集
				if(!ShareData.getM10TimerMap().containsKey(nodeid)){
					docollectHash = M10TaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						//List dolist = (ArrayList)docollectHash.get(nodeid);
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						M10TaskTest m10Task = new M10TaskTest(); 
						m10Task.setNodeid(nodeid);
						delay = 20000L;
						period = 10*60*1000;
//						period = 80*1000;
						try {
							timer.schedule(m10Task, delay, period);// 调度monitorTask 为 M10Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getM10TimerMap().put(nodeid, timer);
						//dolist = null;
					}
					docollectHash = null;
				}
				
				//网络设备30分钟采集
				if(!ShareData.getM30TimerMap().containsKey(nodeid)){
					docollectHash = M30TaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						M30TaskTest m30Task = new M30TaskTest(); 
						m30Task.setNodeid(nodeid);
						delay = 20000L;
						period = 30*60*1000;
						try {
							timer.schedule(m30Task, delay, period);// 调度monitorTask 为 M30Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getM30TimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
				
				//网络设备1小时采集
				if(!ShareData.getH1TimerMap().containsKey(nodeid)){
					docollectHash = H1TaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						H1TaskTest h1Task = new H1TaskTest(); 
						h1Task.setNodeid(nodeid);
						delay = 20000L;
						period = 60*60*1000;
						try {
							timer.schedule(h1Task, delay, period);// 调度monitorTask 为 H1Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getH1TimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
				
				//网络设备1天采集
				if(!ShareData.getD1TimerMap().containsKey(nodeid)){
					docollectHash = D1TaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						D1TaskTest d1Task = new D1TaskTest(); 
						d1Task.setNodeid(nodeid);
						delay = 20000L;
						period = 24*60*60*1000;
						try {
							timer.schedule(d1Task, delay, period);// 调度monitorTask 为 D1Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getD1TimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
			}
			if(Constant.TYPE_HOST.equals(nodedto.getType())){//服务器
				//服务器5分钟采集
				if(!ShareData.getM5HostTimerMap().containsKey(nodeid)){
					docollectHash = M5HostTaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						M5HostTaskTest m5HostTask = new M5HostTaskTest(); 
						m5HostTask.setNodeid(nodeid);
						delay = 1000L;
						period = 5*60*1000;
						try {
							timer.schedule(m5HostTask, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getM5HostTimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
				//服务器10分钟采集
				if(!ShareData.getM10HostTimerMap().containsKey(nodeid)){
					docollectHash = M10HostTaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						M10HostTaskTest m10HostTask = new M10HostTaskTest(); 
						m10HostTask.setNodeid(nodeid);
						delay = 1000L;
						period = 10*60*1000;
						try {
							timer.schedule(m10HostTask, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getM10HostTimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
				//服务器30分钟采集
				if(!ShareData.getM30HostTimerMap().containsKey(nodeid)){
					docollectHash = M30HostTaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						M30HostTaskTest m30HostTask = new M30HostTaskTest(); 
						m30HostTask.setNodeid(nodeid);
						delay = 1000L;
						period = 30*60*1000;
						try {
							timer.schedule(m30HostTask, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getM30HostTimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
				//服务器1小时采集
				if(!ShareData.getH1HostTimerMap().containsKey(nodeid)){
					docollectHash = H1HostTaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						H1HostTaskTest h1HostTask = new H1HostTaskTest(); 
						h1HostTask.setNodeid(nodeid);
						delay = 1000L;
						period = 60*60*1000;
						try {
							timer.schedule(h1HostTask, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getH1HostTimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
				//服务器1天采集
				if(!ShareData.getD1HostTimerMap().containsKey(nodeid)){
					docollectHash = D1HostTaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						D1HostTaskTest d1HostTask = new D1HostTaskTest(); 
						d1HostTask.setNodeid(nodeid);
						delay = 1000L;
						period = 24*60*60*1000;
						try {
							timer.schedule(d1HostTask, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getD1HostTimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
			}
		}
	}
	
	/**
	 * 增加采集定时任务
	 */
	public void addHostLogAgentTask(List<HostNode> nodeidList){
		if(nodeidList == null){
			return;
		}
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = null;
		long delay = 1000L;
		long period = 5*60*1000;
		Hashtable hash = getInterval("hostcollecttask");
		period = getInterval(Float.parseFloat((String)hash.get("interval")), (String)hash.get("unit"));
		
		Hashtable docollectHash = null;
		for(HostNode node:nodeidList){
			//判断是网络设备还是服务器
			//Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid));
			nodedto = nodeUtil.creatNodeDTOByNode(node); 
			//HostNode node = (HostNode)nodeList.get(i);
			if(node.getEndpoint() == 2){
        		//REMOTEPING的子节点，跳过
        		return;
        	}
			//若只用PING TELNET SSH方式检测可用性,则性能数据不采集,跳过
			if(node.getCollecttype() == SystemConstant.COLLECTTYPE_PING ||
					node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT||
					node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT){
				//SysLogger.info("只PING TELNET SSH方式检测可用性,性能数据不采集,跳过");
			}
			
			if(Constant.TYPE_HOST.equals(nodedto.getType())){//服务器
				//服务器5分钟采集
				if(!ShareData.getM5AgentHostTimerMap().containsKey(node.getId()+"")){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						HostCollectDataTaskTest m5AgentHostTask = new HostCollectDataTaskTest(); 
						m5AgentHostTask.setNodeid(node.getId()+"");
						delay = 1000L;
						//period = 5*60*1000;
						try {
							timer.schedule(m5AgentHostTask, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+node.getId()+" 所在的Timer定时器出现异常，自动重启Timer");
//							List<String> nodeids = new ArrayList<String>();
//							nodeids.add(nodeid);
//							removeTask(nodeids);//移除
//							addTask(nodeids);//重新添加
						}
						ShareData.getM5AgentHostTimerMap().put(node.getId()+"", timer);
				}
//				//服务器10分钟采集
//				if(!ShareData.getM10HostTimerMap().containsKey(nodeid)){
//					docollectHash = M10HostTaskTest.getDocollcetHash();
//					if(docollectHash.containsKey(nodeid)){
//						MonitorTimer timer = new MonitorTimer(true); //启动Timer
//						M10HostTaskTest m10HostTask = new M10HostTaskTest(); 
//						m10HostTask.setNodeid(nodeid);
//						delay = 1000L;
//						period = 10*60*1000;
//						try {
//							timer.schedule(m10HostTask, delay, period);// 调度monitorTask 为 M5Task
//						} catch (IllegalStateException  e) {
//							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
//							List<String> nodeids = new ArrayList<String>();
//							nodeids.add(nodeid);
//							removeTask(nodeids);//移除
//							addTask(nodeids);//重新添加
//						}
//						ShareData.getM10HostTimerMap().put(nodeid, timer);
//					}
//					docollectHash = null;
//				}
//				//服务器30分钟采集
//				if(!ShareData.getM30HostTimerMap().containsKey(nodeid)){
//					docollectHash = M30HostTaskTest.getDocollcetHash();
//					if(docollectHash.containsKey(nodeid)){
//						MonitorTimer timer = new MonitorTimer(true); //启动Timer
//						M30HostTaskTest m30HostTask = new M30HostTaskTest(); 
//						m30HostTask.setNodeid(nodeid);
//						delay = 1000L;
//						period = 30*60*1000;
//						try {
//							timer.schedule(m30HostTask, delay, period);// 调度monitorTask 为 M5Task
//						} catch (IllegalStateException  e) {
//							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
//							List<String> nodeids = new ArrayList<String>();
//							nodeids.add(nodeid);
//							removeTask(nodeids);//移除
//							addTask(nodeids);//重新添加
//						}
//						ShareData.getM30HostTimerMap().put(nodeid, timer);
//					}
//					docollectHash = null;
//				}
//				//服务器1小时采集
//				if(!ShareData.getH1HostTimerMap().containsKey(nodeid)){
//					docollectHash = H1HostTaskTest.getDocollcetHash();
//					if(docollectHash.containsKey(nodeid)){
//						MonitorTimer timer = new MonitorTimer(true); //启动Timer
//						H1HostTaskTest h1HostTask = new H1HostTaskTest(); 
//						h1HostTask.setNodeid(nodeid);
//						delay = 1000L;
//						period = 60*60*1000;
//						try {
//							timer.schedule(h1HostTask, delay, period);// 调度monitorTask 为 M5Task
//						} catch (IllegalStateException  e) {
//							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
//							List<String> nodeids = new ArrayList<String>();
//							nodeids.add(nodeid);
//							removeTask(nodeids);//移除
//							addTask(nodeids);//重新添加
//						}
//						ShareData.getH1HostTimerMap().put(nodeid, timer);
//					}
//					docollectHash = null;
//				}
//				//服务器1天采集
//				if(!ShareData.getD1HostTimerMap().containsKey(nodeid)){
//					docollectHash = D1HostTaskTest.getDocollcetHash();
//					if(docollectHash.containsKey(nodeid)){
//						MonitorTimer timer = new MonitorTimer(true); //启动Timer
//						D1HostTaskTest d1HostTask = new D1HostTaskTest(); 
//						d1HostTask.setNodeid(nodeid);
//						delay = 1000L;
//						period = 24*60*60*1000;
//						try {
//							timer.schedule(d1HostTask, delay, period);// 调度monitorTask 为 M5Task
//						} catch (IllegalStateException  e) {
//							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
//							List<String> nodeids = new ArrayList<String>();
//							nodeids.add(nodeid);
//							removeTask(nodeids);//移除
//							addTask(nodeids);//重新添加
//						}
//						ShareData.getD1HostTimerMap().put(nodeid, timer);
//					}
//					docollectHash = null;
//				}
			}
		}
	}
	
	/**
	 * 增加采集定时任务
	 */
	public void addLinkTask(int interval,List<String> linkList){
		if(linkList == null){
			return;
		}
		long delay = 1000L;
		//long period = 5*60*1000;
		long period = interval;
		for(String linkid:linkList){			
				//链路采集
				if(!ShareData.getLinkTimerMap().containsKey(linkid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						CheckLinkTaskTest linkTask = new CheckLinkTaskTest(); 
						linkTask.setLinkid(linkid);
						delay = 1000L;
						//period = 80*1000;
						try {
							timer.schedule(linkTask, delay, period);// 调度monitorTask 为 CheckLinkTask
							ShareData.getLinkTimerMap().put(linkid, timer);
							SysLogger.info("启动 linkid："+linkid+" 所在的Timer定时器");
						} catch (IllegalStateException  e) {
							SysLogger.info("linkid："+linkid+" 所在的Timer定时器出现异常，自动重启Timer");
						}
						
				}
			//}
		}
	}
	
	/**
	 * 增加采集定时任务
	 */
	public void addPanelTask(int interval,List<IpaddressPanel> panelList){
		if(panelList == null){
			return;
		}
		long delay = 1000L;
		//long period = 5*60*1000;
		long period = interval;
		for(IpaddressPanel panel:panelList){			
				//面板采集
				if(!ShareData.getPanelTimerMap().containsKey(panel.getId()+"")){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						UpdatePanelTaskTest panelTask = new UpdatePanelTaskTest(); 
						panelTask.setIpaddressPanel(panel);
						delay = 1000L;
						period = 60*1000;
						try {
							timer.schedule(panelTask, delay, period);// 调度monitorTask 为 CheckLinkTask
							ShareData.getPanelTimerMap().put(panel.getId()+"", timer);
							//SysLogger.info("启动 panelid："+panel.getId()+" 所在的Timer定时器");
						} catch (IllegalStateException  e) {
							SysLogger.info("linkid："+panel.getId()+" 所在的Timer定时器出现异常，自动重启Timer");
						}
						
				}
			//}
		}
	}
	
	/**
	 * 增加设备更新的定时任务
	 */
	public void addPollTask(int interval,List<Host> hostlist){
		if(hostlist == null){
			return;
		}
		long delay = 1000L;
		//long period = 5*60*1000;
		long period = interval;
		MonitorTimer timer = null;
		PollTaskTest pollTask = null;
		delay = 1000L;
		for(Host host:hostlist){			
				//设备更新
				if(!ShareData.getPollTimerMap().containsKey(host.getId()+"")){
						timer = new MonitorTimer(true); //启动Timer
						pollTask = new PollTaskTest(); 
						pollTask.setHost(host);
						
						period = 60*1000;
						try {
							timer.schedule(pollTask, delay, period);// 调度monitorTask 为 CheckLinkTask
							ShareData.getPollTimerMap().put(host.getId()+"", timer);
						} catch (IllegalStateException  e) {
							SysLogger.info("hostid："+host.getId()+" 所在的Timer定时器出现异常，自动重启Timer");
						}
						
				}
			//}
		}
	}
	
	/**
	 * 增加XML更新的定时任务
	 */
	public void addXmlTask(int interval,List<ManageXml> xmllist){
		if(xmllist == null){
			return;
		}
		long delay = 1000L;
		//long period = 5*60*1000;
		long period = interval;
		MonitorTimer timer = null;
		XmlOperator xmlOpr = null;
		UpdateXmlTaskTest xmlTask = null; 
		for(ManageXml xml:xmllist){			
				//设备更新
				if(!ShareData.getXmlTimerMap().containsKey(xml.getId()+"")){
						timer = new MonitorTimer(true); //启动Timer
						xmlOpr = new XmlOperator();
						xmlTask = new UpdateXmlTaskTest(); 
						xmlTask.setManageXml(xml);
						xmlTask.setXmlOperator(xmlOpr);
						delay = 1000L;
						period = 60*1000;
						try {
							timer.schedule(xmlTask, delay, period);// 调度monitorTask 为 CheckLinkTask
							ShareData.getXmlTimerMap().put(xml.getId()+"", timer);
						} catch (IllegalStateException  e) {
							SysLogger.info("xmlid："+xml.getId()+" 所在的Timer定时器出现异常，自动重启Timer");
						}
						
				}
			//}
		}
	}
	
	/**
	 * 增加采集定时任务
	 */
	public void addTask(String nodeid){
		if(nodeid == null){
			return;
		}
		//HashMap m5TimerMap = ShareData.getM5TimerMap();
		//HashMap m10TimerMap = ShareData.getM10TimerMap();
		NodeUtil nodeUtil = new NodeUtil();
		Hashtable docollectHash = null;
		NodeDTO nodedto = null;
		long delay = 1000L;
		long period = 5*60*1000;
		//for(String nodeid:nodeidList){
			//判断是网络设备还是服务器
			nodedto = nodeUtil.creatNodeDTOByNode((Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid))); 
			if(Constant.TYPE_NET.equals(nodedto.getType())){//网络设备
				if(!ShareData.getM5TimerMap().containsKey(nodeid)){//网络设备5分钟采集
					docollectHash = M5TaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						//List dolist = (ArrayList)docollectHash.get(nodeid);
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						M5TaskTest m5Task = new M5TaskTest(); 
						m5Task.setNodeid(nodeid);
						delay = 1000L;
						period = 80*1000;
						try {
							timer.schedule(m5Task, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getM5TimerMap().put(nodeid, timer);
						//dolist = null;
					}
					docollectHash = null;
				}
				//网络设备10分钟采集
				if(!ShareData.getM10TimerMap().containsKey(nodeid)){
					docollectHash = M10TaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						//List dolist = (ArrayList)docollectHash.get(nodeid);
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						M10TaskTest m10Task = new M10TaskTest(); 
						m10Task.setNodeid(nodeid);
						delay = 20000L;
						period = 2*60*1000;
						try {
							timer.schedule(m10Task, delay, period);// 调度monitorTask 为 M10Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getM10TimerMap().put(nodeid, timer);
						//dolist = null;
					}
					docollectHash = null;
				}
				
				//网络设备30分钟采集
				if(!ShareData.getM30TimerMap().containsKey(nodeid)){
					docollectHash = M30TaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						M30TaskTest m30Task = new M30TaskTest(); 
						m30Task.setNodeid(nodeid);
						delay = 20000L;
						period = 30*60*1000;
						try {
							timer.schedule(m30Task, delay, period);// 调度monitorTask 为 M30Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getM30TimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
				
				//网络设备1小时采集
				if(!ShareData.getH1TimerMap().containsKey(nodeid)){
					docollectHash = H1TaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						H1TaskTest h1Task = new H1TaskTest(); 
						h1Task.setNodeid(nodeid);
						delay = 20000L;
						period = 60*60*1000;
						try {
							timer.schedule(h1Task, delay, period);// 调度monitorTask 为 H1Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getH1TimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
				
				//网络设备1天采集
				if(!ShareData.getD1TimerMap().containsKey(nodeid)){
					docollectHash = D1TaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						D1TaskTest d1Task = new D1TaskTest(); 
						d1Task.setNodeid(nodeid);
						delay = 20000L;
						period = 24*60*60*1000;
						try {
							timer.schedule(d1Task, delay, period);// 调度monitorTask 为 D1Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getD1TimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
			}
			if(Constant.TYPE_HOST.equals(nodedto.getType())){//服务器
				//服务器5分钟采集
				if(!ShareData.getM5HostTimerMap().containsKey(nodeid)){
					docollectHash = M5HostTaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						M5HostTaskTest m5HostTask = new M5HostTaskTest(); 
						m5HostTask.setNodeid(nodeid);
						delay = 1000L;
						period = 80*1000;
						try {
							timer.schedule(m5HostTask, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getM5HostTimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
				//服务器10分钟采集
				if(!ShareData.getM10HostTimerMap().containsKey(nodeid)){
					docollectHash = M10HostTaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						M10HostTaskTest m10HostTask = new M10HostTaskTest(); 
						m10HostTask.setNodeid(nodeid);
						delay = 1000L;
						period = 10*60*1000;
						try {
							timer.schedule(m10HostTask, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getM10HostTimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
				//服务器30分钟采集
				if(!ShareData.getM30HostTimerMap().containsKey(nodeid)){
					docollectHash = M30HostTaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						M30HostTaskTest m30HostTask = new M30HostTaskTest(); 
						m30HostTask.setNodeid(nodeid);
						delay = 1000L;
						period = 30*60*1000;
						try {
							timer.schedule(m30HostTask, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getM30HostTimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
				//服务器1小时采集
				if(!ShareData.getH1HostTimerMap().containsKey(nodeid)){
					docollectHash = H1HostTaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						H1HostTaskTest h1HostTask = new H1HostTaskTest(); 
						h1HostTask.setNodeid(nodeid);
						delay = 1000L;
						period = 60*60*1000;
						try {
							timer.schedule(h1HostTask, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getH1HostTimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
				//服务器1天采集
				if(!ShareData.getD1HostTimerMap().containsKey(nodeid)){
					docollectHash = D1HostTaskTest.getDocollcetHash();
					if(docollectHash.containsKey(nodeid)){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						D1HostTaskTest d1HostTask = new D1HostTaskTest(); 
						d1HostTask.setNodeid(nodeid);
						delay = 1000L;
						period = 60*60*1000;
						try {
							timer.schedule(d1HostTask, delay, period);// 调度monitorTask 为 M5Task
						} catch (IllegalStateException  e) {
							SysLogger.info("nodeid为："+nodeid+" 所在的Timer定时器出现异常，自动重启Timer");
							List<String> nodeids = new ArrayList<String>();
							nodeids.add(nodeid);
							removeTask(nodeids);//移除
							addTask(nodeids);//重新添加
						}
						ShareData.getD1HostTimerMap().put(nodeid, timer);
					}
					docollectHash = null;
				}
			}
		//}
	}
	
	
	/**
	 * 增加采集定时任务
	 */
	public void addLinkTask(String linkid,Hashtable hash){
		if(linkid == null){
			return;
		}
		long delay = 1000L;
		long period = 2*60*1000;
		//for(String nodeid:nodeidList){
			//判断是网络设备还是服务器
				if(!ShareData.getLinkTimerMap().containsKey(linkid)){//网络设备5分钟采集
						//List dolist = (ArrayList)docollectHash.get(nodeid);
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						CheckLinkTaskTest linkTask = new CheckLinkTaskTest(); 
						linkTask.setLinkid(linkid);
						delay = 1000L;
						//period = 80*1000;
						try {
							period = getInterval(Float.parseFloat((String)hash.get("interval")), (String)hash.get("unit"));
							timer.schedule(linkTask, delay, period);// 调度monitorTask 为 M5Task
							ShareData.getLinkTimerMap().put(linkid, timer);
						} catch (IllegalStateException  e) {
							SysLogger.info("linkid："+linkid+" 所在的Timer定时器出现异常，自动重启Timer");
						}
						
				}
	}
	
	/**
	 * 增加设备更新定时任务
	 */
	public void addPollTask(Host host,Hashtable hash){
		if(host == null){
			return;
		}
		long delay = 1000L;
		long period = 5*60*1000;
				if(!ShareData.getPollTimerMap().containsKey(host.getId()+"")){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						PollTaskTest pollTask = new PollTaskTest(); 
						pollTask.setHost(host);
						delay = 1000L;
						period = 80*1000;
						try {							
							//pollTask.setInterval(Float.parseFloat((String)hash.get("interval")), (String)hash.get("unit"));
							period = getInterval(Float.parseFloat((String)hash.get("interval")), (String)hash.get("unit"));
							timer.schedule(pollTask, delay, period);// 调度monitorTask 为 M5Task
							ShareData.getPollTimerMap().put(host.getId()+"", timer);
						} catch (IllegalStateException  e) {
							SysLogger.info("hostid："+host.getId()+" 所在的Timer定时器出现异常，自动重启Timer");
						}
						
				}
	}
	
	/**
	 * 增加设备更新定时任务
	 */
	public void addXmlTask(ManageXml xml,XmlOperator xmlOpr,Hashtable hash){
		if(xml == null){
			return;
		}
		long delay = 1000L;
		long period = 5*60*1000;
				if(!ShareData.getXmlTimerMap().containsKey(xml.getId()+"")){
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						UpdateXmlTaskTest xmlTask = new UpdateXmlTaskTest(); 
						xmlTask.setManageXml(xml);
						delay = 1000L;
						period = 80*1000;
						try {							
							//pollTask.setInterval(Float.parseFloat((String)hash.get("interval")), (String)hash.get("unit"));
							period = getInterval(Float.parseFloat((String)hash.get("interval")), (String)hash.get("unit"));
							timer.schedule(xmlTask, delay, period);// 调度monitorTask 为 M5Task
							ShareData.getXmlTimerMap().put(xml.getId()+"", timer);
						} catch (IllegalStateException  e) {
							SysLogger.info("xmlid："+xml.getId()+" 所在的Timer定时器出现异常，自动重启Timer");
						}
						
				}
	}
	
	/**
	 * 增加采集定时任务
	 */
	public void addPanelTask(IpaddressPanel ippanel,Hashtable hash){
		if(ippanel == null){
			return;
		}
		long delay = 1000L;
		long period = 5*60*1000;
		//for(String nodeid:nodeidList){
			//判断是网络设备还是服务器
				if(!ShareData.getPanelTimerMap().containsKey(ippanel.getId()+"")){//网络设备5分钟采集
						//List dolist = (ArrayList)docollectHash.get(nodeid);
						MonitorTimer timer = new MonitorTimer(true); //启动Timer
						UpdatePanelTaskTest panelTask = new UpdatePanelTaskTest(); 
						
						
						panelTask.setIpaddressPanel(ippanel);
						delay = 1000L;
						period = 80*1000;
						try {
							//panelTask.setInterval(Float.parseFloat((String)hash.get("interval")), (String)hash.get("unit"));
							period = getInterval(Float.parseFloat((String)hash.get("interval")), (String)hash.get("unit"));
							timer.schedule(panelTask, delay, period);// 调度monitorTask 为 M5Task
							ShareData.getPanelTimerMap().put(ippanel.getId()+"", timer);
						} catch (IllegalStateException  e) {
							SysLogger.info("panelid："+ippanel.getId()+" 所在的Timer定时器出现异常，自动重启Timer");
						}
						
				}
	}
	
	/**
	 * 取消采集定时任务
	 * @param nodeidList
	 */
	public void removeTask(List<String> nodeidList){
		HashMap m5TimerMap = ShareData.getM5TimerMap();
		HashMap m10TimerMap = ShareData.getM10TimerMap();
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = null;
		HostNode host = null;
		HostNodeDao dao = new HostNodeDao();
		try {
			for(String nodeid:nodeidList){
				//判断是网络设备还是服务器
				host = (HostNode)dao.findByID(nodeid);
		        nodedto = nodeUtil.creatNodeDTOByNode(host); 
				if(Constant.TYPE_NET.equals(nodedto.getType())){//网络设备
					if(m5TimerMap.containsKey(nodeid)){//网络设备5分钟采集
						MonitorTimer timer = (MonitorTimer)m5TimerMap.get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getM5Alldata().remove(nodeid);
						m5TimerMap.remove(nodeid);//移除内存中的该Timer信息
					}
					//网络设备10分钟采集
					if(m10TimerMap.containsKey(nodeid)){//网络设备10分钟采集
						MonitorTimer timer = (MonitorTimer)m10TimerMap.get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getM10Alldata().remove(nodeid);
						m10TimerMap.remove(nodeid);//移除内存中的该Timer信息
					}
					//网络设备30分钟采集
					if(ShareData.getM30TimerMap().containsKey(nodeid)){//网络设备30分钟采集
						MonitorTimer timer = (MonitorTimer)ShareData.getM30TimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getM30Alldata().remove(nodeid);
						ShareData.getM30TimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//网络设备1小时采集
					if(ShareData.getH1TimerMap().containsKey(nodeid)){//网络设备1小时采集
						MonitorTimer timer = (MonitorTimer)ShareData.getH1TimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getH1Alldata().remove(nodeid);
						ShareData.getH1TimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//网络设备1采集天
					if(ShareData.getD1TimerMap().containsKey(nodeid)){//网络设备1天采集
						MonitorTimer timer = (MonitorTimer)ShareData.getD1TimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getD1Alldata().remove(nodeid);
						ShareData.getD1TimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
				}
				if(Constant.TYPE_HOST.equals(nodedto.getType())){//服务器
					//服务器5分钟采集
					if(ShareData.getM5HostTimerMap().containsKey(nodeid)){
						MonitorTimer timer = (MonitorTimer)ShareData.getM5HostTimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getM5HostAlldata().remove(nodeid);
						ShareData.getM5HostTimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//服务器10分钟采集
					if(ShareData.getM10HostTimerMap().containsKey(nodeid)){
						MonitorTimer timer = (MonitorTimer)ShareData.getM10HostTimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getM10HostAlldata().remove(nodeid);
						ShareData.getM10HostTimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//服务器30分钟采集
					if(ShareData.getM30HostTimerMap().containsKey(nodeid)){
						MonitorTimer timer = (MonitorTimer)ShareData.getM30HostTimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getM30HostAlldata().remove(nodeid);
						ShareData.getM30HostTimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//服务器1小时采集
					if(ShareData.getH1HostTimerMap().containsKey(nodeid)){
						MonitorTimer timer = (MonitorTimer)ShareData.getH1HostTimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getH1HostAlldata().remove(nodeid);
						ShareData.getH1HostTimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//服务器1天采集
					if(ShareData.getD1HostTimerMap().containsKey(nodeid)){
						MonitorTimer timer = (MonitorTimer)ShareData.getD1HostTimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getD1HostAlldata().remove(nodeid);
						ShareData.getD1HostTimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		
	}
	
	/**
	 * 取消采集定时任务
	 * @param nodeidList
	 */
	public void removeLinkTask(List<String> linkidList){
		try {
			for(String linkid:linkidList){
					if(ShareData.getLinkTimerMap().containsKey(linkid)){//网络设备5分钟采集
						MonitorTimer timer = (MonitorTimer)ShareData.getLinkTimerMap().get(linkid);
						timer.canclethis(true);//取消Timer
						ShareData.getLinkAlldata().remove(linkid);
						ShareData.getLinkTimerMap().remove(linkid);//移除内存中的该Timer信息
					}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 取消采集定时任务
	 * @param nodeidList
	 */
	public void removeTask(String nodeid){
		//HashMap m5TimerMap = ShareData.getM5TimerMap();
		//HashMap m10TimerMap = ShareData.getM10TimerMap();
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = null;
		HostNode host = null;
		HostNodeDao dao = new HostNodeDao();
		try {
			//for(String nodeid:nodeidList){
				//判断是网络设备还是服务器
				host = (HostNode)dao.findByID(nodeid);
		        nodedto = nodeUtil.creatNodeDTOByNode(host); 
				if(Constant.TYPE_NET.equals(nodedto.getType())){//网络设备
					
					//网络设备5分钟采集
					if(ShareData.getM5TimerMap().containsKey(nodeid)){
						MonitorTimer timer = (MonitorTimer)ShareData.getM5TimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getM5Alldata().remove(nodeid);
						ShareData.getM5TimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//网络设备10分钟采集
					if(ShareData.getM10TimerMap().containsKey(nodeid)){//网络设备10分钟采集
						MonitorTimer timer = (MonitorTimer)ShareData.getM10TimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getM10Alldata().remove(nodeid);
						ShareData.getM10TimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//网络设备30分钟采集
					if(ShareData.getM30TimerMap().containsKey(nodeid)){//网络设备30分钟采集
						MonitorTimer timer = (MonitorTimer)ShareData.getM30TimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getM30Alldata().remove(nodeid);
						ShareData.getM30TimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//网络设备1小时采集
					if(ShareData.getH1TimerMap().containsKey(nodeid)){//网络设备1小时采集
						MonitorTimer timer = (MonitorTimer)ShareData.getH1TimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getH1Alldata().remove(nodeid);
						ShareData.getH1TimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//网络设备1采集天
					if(ShareData.getD1TimerMap().containsKey(nodeid)){//网络设备1天采集
						MonitorTimer timer = (MonitorTimer)ShareData.getD1TimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getD1Alldata().remove(nodeid);
						ShareData.getD1TimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
				}
				if(Constant.TYPE_HOST.equals(nodedto.getType())){//服务器
					//服务器5分钟采集
					if(ShareData.getM5HostTimerMap().containsKey(nodeid)){
						MonitorTimer timer = (MonitorTimer)ShareData.getM5HostTimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getM5HostAlldata().remove(nodeid);
						ShareData.getM5HostTimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//服务器10分钟采集
					if(ShareData.getM10HostTimerMap().containsKey(nodeid)){
						MonitorTimer timer = (MonitorTimer)ShareData.getM10HostTimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getM10HostAlldata().remove(nodeid);
						ShareData.getM10HostTimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//服务器30分钟采集
					if(ShareData.getM30HostTimerMap().containsKey(nodeid)){
						MonitorTimer timer = (MonitorTimer)ShareData.getM30HostTimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getM30HostAlldata().remove(nodeid);
						ShareData.getM30HostTimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//服务器1小时采集
					if(ShareData.getH1HostTimerMap().containsKey(nodeid)){
						MonitorTimer timer = (MonitorTimer)ShareData.getH1HostTimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getH1HostAlldata().remove(nodeid);
						ShareData.getH1HostTimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
					//服务器1天采集
					if(ShareData.getD1HostTimerMap().containsKey(nodeid)){
						MonitorTimer timer = (MonitorTimer)ShareData.getD1HostTimerMap().get(nodeid);
						timer.canclethis(true);//取消Timer
						ShareData.getD1HostAlldata().remove(nodeid);
						ShareData.getD1HostTimerMap().remove(nodeid);//移除内存中的该Timer信息
					}
				}
			//}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		
	}
	
	/**
	 * 取消采集定时任务
	 * @param nodeidList
	 */
	public void removeLinkTask(String linkid){
		try {					
					//删除链路采集
					if(ShareData.getLinkTimerMap().containsKey(linkid)){
						MonitorTimer timer = (MonitorTimer)ShareData.getLinkTimerMap().get(linkid);
						timer.canclethis(true);//取消Timer
						ShareData.getLinkAlldata().remove(linkid);
						ShareData.getLinkTimerMap().remove(linkid);//移除内存中的该Timer信息
					}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取某网络设备节点5分钟采集的指标
	 * @param nodeid
	 * @return
	 */
	public List getGatherIndicatorsList(String nodeid, String interval ,String unit,int enabled,String type ){
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		try {
			// 获取被启用的所有被监视指标
			monitorItemList = indicatorsdao.getByIntervalAndType(nodeid,interval, unit, enabled,type);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsdao.close();
		}
		if (monitorItemList == null)
			monitorItemList = new ArrayList<NodeGatherIndicators>();
		return monitorItemList;
	}
	
	public void startHostLogAgentTimers(int interval, String type) {
		List<String> nodeidList = new ArrayList<String>();
		if(type.equals("host")){//服务器
			HostNodeDao nodedao = new HostNodeDao();
			List nodelist = new ArrayList();
			try {
				nodelist = nodedao.loadMonitorByMonCategory(1,4);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodedao.close();
			}
			
			if (nodelist != null && nodelist.size() > 0) {
				for (int i = 0; i < nodelist.size(); i++) {
					HostNode node = (HostNode) nodelist.get(i);
					nodeidList.add(String.valueOf(node.getId()));
				}
			}
			addHostLogAgentTask(nodelist);
		}
		
	}
	
	public void startTimers(int interval, String type) {
		List<String> nodeidList = new ArrayList<String>();
		if(type.equals("net")){//网络设备
			HostNodeDao nodedao = new HostNodeDao();
			List nodelist = new ArrayList();
			try {
				nodelist = nodedao.loadMonitorNet();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodedao.close();
			}
			
			if (nodelist != null && nodelist.size() > 0) {
				for (int i = 0; i < nodelist.size(); i++) {
					HostNode node = (HostNode) nodelist.get(i);
					nodeidList.add(String.valueOf(node.getId()));
				}
			}
		}
		if(type.equals("host")){//服务器
			HostNodeDao nodedao = new HostNodeDao();
			List nodelist = new ArrayList();
			try {
				nodelist = nodedao.loadMonitorByMonCategory(1,4);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodedao.close();
			}
			
			if (nodelist != null && nodelist.size() > 0) {
				for (int i = 0; i < nodelist.size(); i++) {
					HostNode node = (HostNode) nodelist.get(i);
					nodeidList.add(String.valueOf(node.getId()));
				}
			}
		}
		addTask(nodeidList);
	}
	
	public void startLinkTimers(int interval, String type) {
		if(type.equals("link")){//网络设备链路
			//获取需要检查的链路
			//List linkList = PollingEngine.getInstance().getLinkList();
			List dolist = new ArrayList();
			if(PollingEngine.getInstance().getLinkList() != null && PollingEngine.getInstance().getLinkList().size()>0){
				for(int i=0;i<PollingEngine.getInstance().getLinkList().size();i++){
					LinkRoad lk = (LinkRoad)PollingEngine.getInstance().getLinkList().get(i);
					dolist.add(lk.getId()+"");
				}
			}
			addLinkTask(interval,dolist);
			dolist = null;
		}
	}
	
	public void startPanelTimers(int interval, String type) {
			//获取需要更新的面板
			List panelList = PollingEngine.getInstance().getPanelList();
			if(panelList != null && panelList.size()>0){
				addPanelTask(interval,panelList);
			}
			panelList = null;
	}
	
	public  void startPollTimers(int interval, String type) {
			//获取需要更新的设备
			//List hostList = PollingEngine.getInstance().getNodeList();
			if(PollingEngine.getInstance().getNodeList() != null && PollingEngine.getInstance().getNodeList().size()>0){
				addPollTask(interval,PollingEngine.getInstance().getNodeList());
			}
			//hostList =null;
	}
	
	public  void startXmlTimers(int interval, String type) {
		//获取需要更新的XML
		//List xmlList = PollingEngine.getInstance().getXmlList();
		if(PollingEngine.getInstance().getXmlList() != null && PollingEngine.getInstance().getXmlList().size()>0){
			addXmlTask(interval,PollingEngine.getInstance().getXmlList());
		}
}
	
	public  Hashtable getInterval(String name){
		Hashtable returnhash = new Hashtable();
		Hashtable task_ht = taskNum();
		int num = task_ht.size();
		TaskFactory taskF = new TaskFactory();
		for (int i = 0; i < num; i++) 
		{
			
			String taskinfo = task_ht.get(String.valueOf(i)).toString();
			String[] tmp = taskinfo.split(":");
			String taskname = tmp[0];
			String unit = tmp[2];
			String interval = tmp[1];
			if(taskname.equalsIgnoreCase(name)){
				returnhash.put("interval", interval);
				returnhash.put("unit", unit);
				//return interval;
				SysLogger.info(
						"interval is -- "
							+ interval
							+ "  unit is  -- "
							+ unit
							+ "taskname is -- "
							+ taskname);
			}

		}
		return returnhash;
	}
	
	public  Hashtable taskNum() {
		//Integer returnVal=new Integer(0);
		//SysLogger.info("开始执行到此4###################");
		Hashtable ht = new Hashtable();
		int index = 0;
		List list = new ArrayList();
		try {
			TaskXml taskxml = new TaskXml();
			list = taskxml.ListXml();
			for (int i = 0; i < list.size(); i++) {
				//SysLogger.info("开始执行到此###################"+i);
				Task task = new Task();
				BeanUtils.copyProperties(task, list.get(i));
				String sign = task.getStartsign();
				if ("1".equals(sign)) {
					if (task.getTaskname().equals("linktrust"))continue;
					String taskname = task.getTaskname();
					Float interval = task.getPolltime();
					String polltimeunit = task.getPolltimeunit();
					ht.put(String.valueOf(index),taskname + ":" + interval + ":" + polltimeunit);
					index++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//SysLogger.info("开始执行到此5###################");
		return ht;
	}
	
	public  int getInterval(float d,String t)
	   {
		int interval = 0;
		  if(t.equals("d"))
			 interval =(int) d*24*60*60*1000; //天数
		  else if(t.equals("h"))
			 interval =(int) d*60*60*1000;    //小时
		  else if(t.equals("m"))
			 interval = (int)d*60*1000;       //分钟
		else if(t.equals("s"))
					 interval =(int) d*1000;       //秒
		  return interval;
	   }
	
}
