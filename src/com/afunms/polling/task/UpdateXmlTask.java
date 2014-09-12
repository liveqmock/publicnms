/*
 * Created on 2005-4-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.dao.IndicatorsTopoRelationDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.IndicatorsTopoRelation;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.NetworkDao;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.IpaddressPanelDao;
import com.afunms.config.model.IpaddressPanel;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.HintNodeDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.dao.RelationDao;
import com.afunms.topology.model.HintNode;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.model.Relation;
import com.afunms.topology.util.PanelXmlOperator;
import com.afunms.topology.util.XmlOperator;

         
    
/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UpdateXmlTask extends MonitorTask {

	/**          
	 *     
	 */
	public UpdateXmlTask() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
//		SysLogger.info("######开始更新XML######");
		
		IpaddressPanelDao paneldao = new IpaddressPanelDao();
		List panellist = null;
		try{
			panellist = paneldao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			paneldao.close();
		}
		int numThreads = 200;		
//		try {
//			List numList = new ArrayList();
//			TaskXml taskxml = new TaskXml();
//			numList = taskxml.ListXml();
//			for (int i = 0; i < numList.size(); i++) {
//				Task task = new Task();
//				BeanUtils.copyProperties(task, numList.get(i));
//				if (task.getTaskname().equals("updatexmlthreadnum")){
//					numThreads = task.getPolltime().intValue();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
		if (panellist != null && panellist.size() > 0) {

    		ThreadPool threadPool = new ThreadPool(numThreads);														
    		for (int i=0; i<panellist.size(); i++) {
        			threadPool.runTask(createUpdatePanelTask((IpaddressPanel)panellist.get(i)));
    		}
    		threadPool.join(); 

		}
		
		// 如果子图列表有子图的情况 yangjun add 2009-10-14
//		ManageXmlDao subMapDao = new ManageXmlDao();
//		List subfileList = null;
//		try{
//			subfileList = subMapDao.loadAll();
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			subMapDao.close();
//		}
		
		NodeDependDao nodeDependDao = new NodeDependDao();
		Hashtable nodedependhash = new Hashtable();
		try{
			List list = nodeDependDao.loadAll();
			if(list != null && list.size()>0){
				for(int i=0;i<list.size();i++){
					NodeDepend vo = (NodeDepend)list.get(i);
					if(nodedependhash.containsKey(vo.getXmlfile())){
						((List)nodedependhash.get(vo.getXmlfile())).add(vo);
					}else{
						List templist = new ArrayList();
						templist.add(vo);
						nodedependhash.put(vo.getXmlfile(), templist);
					}
					
				}
			}
//		    List list = nodeDependDao.findByXml(xml.getXmlName());
//		    ChartXml chartxml;
//			chartxml = new ChartXml("NetworkMonitor","/"+xml.getXmlName().replace("jsp", "xml"));
//			chartxml.addBussinessXML(xml.getTopoName(),list);
//			ChartXml chartxmlList;
//			chartxmlList = new ChartXml("NetworkMonitor","/"+xml.getXmlName().replace("jsp", "xml").replace("businessmap", "list"));
//			chartxmlList.addListXML(xml.getTopoName(),list);
		}catch(Exception e){
		    e.printStackTrace();   	
		}finally{
			nodeDependDao.close();
        }
		ShareData.setAllnodedepend(nodedependhash);
		
		
		IndicatorsTopoRelationDao indicatorsTopoRelationDao = new IndicatorsTopoRelationDao();
		Hashtable tophash = new Hashtable();
		try{
			List list = indicatorsTopoRelationDao.loadAll();
			if(list != null && list.size()>0){
				for(int i=0;i<list.size();i++){
					IndicatorsTopoRelation relation = (IndicatorsTopoRelation)list.get(i);
					if(tophash.containsKey(relation.getTopoId()+":"+relation.getNodeid())){
						((List)tophash.get(relation.getTopoId()+":"+relation.getNodeid())).add(relation);
					}else{
						List tlist = new ArrayList();
						tlist.add(relation);
						tophash.put(relation.getTopoId()+":"+relation.getNodeid(), tlist);
					}
					
				}
			}
		}catch(Exception e){
			
		}finally{
			indicatorsTopoRelationDao.close();
		}
		ShareData.setToprelation(tophash);
		
		AlarmIndicatorsNodeDao alarmdao = new AlarmIndicatorsNodeDao();
		Hashtable alarmnodehash = new Hashtable();
		try{
			List list = alarmdao.loadAll();
			if(list != null && list.size()>0){
				for(int i=0;i<list.size();i++){
					AlarmIndicatorsNode alarmindi = (AlarmIndicatorsNode)list.get(i);
					alarmnodehash.put(alarmindi.getId()+":"+alarmindi.getNodeid(), alarmindi);
				}
			}
		}catch(Exception e){
			
		}finally{
			alarmdao.close();
		}
		ShareData.setAllalarmindicators(alarmnodehash);
		
		HintNodeDao hintNodeDao = new HintNodeDao();
		Hashtable hinthash = new Hashtable();
		try{
			List hintlist = hintNodeDao.loadAll();
			if(hintlist != null && hintlist.size()>0){
				for(int i=0;i<hintlist.size();i++){
					HintNode hintnode = (HintNode)hintlist.get(i);
					hinthash.put(hintnode.getNodeId()+":"+hintnode.getXmlfile(), hintnode);
				}
			}
		}catch(Exception e){
			
		}finally{
			hintNodeDao.close();
		}
		ShareData.setAllhintlinks(hinthash);
		
		// ----------------更新链路(2007.2.27)-------------------
		ManageXmlDao manageXmlDao = new ManageXmlDao();
		Hashtable managexmlhash = new Hashtable();
		Hashtable managexmlhashtable = new Hashtable();
		List managexmllist = null;
		try{
			managexmllist = manageXmlDao.loadAll();
			if(managexmllist != null && managexmllist.size()>0){
				for(int i=0;i<managexmllist.size();i++){
					ManageXml vo = (ManageXml)managexmllist.get(i);
					managexmlhash.put(vo.getXmlName(), vo);
					managexmlhashtable.put(vo.getId(), vo);
				}
			}
			//mvo = (ManageXml) manageXmlDao.findByXml(xmlName);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			manageXmlDao.close();
		}
		ShareData.setManagexmlhashtable(managexmlhashtable);
		ShareData.setManagexmlhash(managexmlhash);
		
		RelationDao relationDao = new RelationDao();
		Hashtable relationhash = new Hashtable();
		Hashtable relationhashtable = new Hashtable();
		try{
			List list = relationDao.loadAll();
			if(list != null && list.size()>0){
				for(int i=0;i<list.size();i++){
					Relation relation = (Relation)list.get(i);
					if(relationhash.containsKey(relation.getMapId()+"")){
						((List)relationhash.get(relation.getMapId()+"")).add(relation);
					}else{
						List tlist = new ArrayList();
						tlist.add(relation);
						relationhash.put(relation.getMapId()+"", tlist);
					}
					relationhashtable.put(relation.getNodeId()+":"+relation.getXmlName(), relation.getMapId());
				}
			}
		}catch(Exception e){
			
		}finally{
			relationDao.close();
		}
		ShareData.setRelationhashtable(relationhashtable);
		ShareData.setRelationhash(relationhash);
		
		
//		NodeEquipDao nodeEquipDao = new NodeEquipDao();// yangjun add
//		Hashtable nodeequiphash = new Hashtable();
//		try{
//			List nodeequiplist = nodeEquipDao.loadAll();
//			if(nodeequiplist != null && nodeequiplist.size()>0){
//				for(int i=0;i<nodeequiplist.size();i++){
//					NodeEquip nodeequip = (NodeEquip)nodeequiplist.get(i);
//					nodeequiphash.put(nodeequip.getNodeId()+":"+nodeequip.getXmlName(), nodeequip);
//				}
//			}
//		}catch(Exception e){
//			
//		}finally{
//			nodeEquipDao.close();
//		}
//		ShareData.setAllnodeequps(nodeequiphash);
		
		List nodelist = PollingEngine.getInstance().getNodeList();
		if(nodelist != null && nodelist.size()>0){ 
			collectData(nodelist);
		}
//		if(managexmllist != null && managexmllist.size()>0){
//			XmlOperator xmlOpr = new XmlOperator();
//			for (int j = 0; j < managexmllist.size(); j++) {
//				if(nodelist != null && nodelist.size()>0){   
//					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
                    //生成线程池
//		    		ThreadPool threadPool = new ThreadPool(nodelist.size());	
		    		//先采集节点数据CPU、内存等
		    		// 运行任务
//		    		for (int i=0; i<nodelist.size(); i++) {
		    			//IndicatorsTopoRelationDao indicatorsTopoRelationDao = new IndicatorsTopoRelationDao();
//		    			String nodeid = ((Host)nodelist.get(i)).getId()+"";
						//List<IndicatorsTopoRelation> list1 = indicatorsTopoRelationDao.findByTopoId(((ManageXml) subfileList.get(j)).getId()+"",nodeid);
//		    			List<IndicatorsTopoRelation> list1 = new ArrayList();
//		    			if(tophash.containsKey(((ManageXml) subfileList.get(j)).getId()+":"+nodeid)){
//		    				list1 = (List)tophash.get(((ManageXml) subfileList.get(j)).getId()+":"+nodeid);
//		    			}
						//indicatorsTopoRelationDao.close(); 
//						List moidList = new ArrayList();
//						for(int k=0;k<list1.size();k++){
//							IndicatorsTopoRelation nm = (IndicatorsTopoRelation)list1.get(k);
//							if(nm!=null){
//								//AlarmIndicatorsNode alarmIndicatorsNode = alarmIndicatorsUtil.getAlarmInicatorsNodes(nm.getIndicatorsId(),nodeid);
//								AlarmIndicatorsNode alarmIndicatorsNode = null;
//								if(alarmnodehash.containsKey(nm.getIndicatorsId()+":"+nodeid)){
//									alarmIndicatorsNode = (AlarmIndicatorsNode)alarmnodehash.get(nm.getIndicatorsId()+":"+nodeid);
//									if(alarmIndicatorsNode != null)moidList.add(alarmIndicatorsNode);
//								}
//								
//							}
//						}
//		        	    threadPool.runTask(createPollTask((Host)nodelist.get(i),moidList));
//		    		}
		    		// 关闭线程池并等待所有任务完成
//		    		threadPool.join(); 	
//		    		threadPool.close();
//		    		threadPool = null;
//		    		createSubViewTask((ManageXml) managexmllist.get(j));
//				}	
//			}
//			xmlOpr.alarmNode(xmlOpr.getAlarmMapList());
//		}
		
		//更新所有节点的状态
//		if(nodelist != null && nodelist.size()>0){   
//            //生成线程池
//    		ThreadPool threadPool = new ThreadPool(nodelist.size());
//    		Hashtable checkEventHash = new Hashtable();
//    		CheckEventDao checkeventdao = new CheckEventDao();
//    		List list = new ArrayList();
//    		try{
//    			list = checkeventdao.loadAll();
//    		}catch(Exception e){
//    			
//    		}finally{
//    			checkeventdao.close();
//    		}
//    		if(list != null && list.size()>0){
//    			for(int i=0;i<list.size();i++){
//    				CheckEvent vo = (CheckEvent)list.get(i);
//    				checkEventHash.put(vo.getName(), vo.getAlarmlevel());
//    			}
//    		}
//    		ShareData.setCheckEventHash(checkEventHash);
//    		// 运行任务
//    		for (int i=0; i<nodelist.size(); i++) {
//        	    threadPool.runTask(createPollRefreshStateTask((Host)nodelist.get(i)));
//    		}
//    		// 关闭线程池并等待所有任务完成
//    		threadPool.join(); 	
//    		threadPool.close();
//    		threadPool = null;
//		}	
		
		// 更新拓扑图信息
//		try {
//			XmlOperator xmlOpr = new XmlOperator();
//			xmlOpr.setFile("server.jsp");
//			xmlOpr.init4updateXml();
//			xmlOpr.updateInfo(false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		SysLogger.info("######结束更新XML######");
	}
	
	/**
	 * <P>先更新节点数据(CPU、内存等)</p>
	 * HONGLI
	 * @param monitornodelist
	 */
	public void collectData(List nodeList){
		String runmodel = PollingEngine.getCollectwebflag(); 
		if("1".equals(runmodel)){
	       	//采集与访问是分离模式
			NetworkDao networkDao = new NetworkDao();
			try{
				networkDao.collectAllNetworkData(nodeList);
			}catch(Exception e){
				
			}finally{
			
			}
			SysLogger.info("######采集与访问是分离模式 更新内存######");
		}
	}
	
	
    /**
     *	创建轮询面板的任务
     */	
	private Runnable createUpdatePanelTask(final IpaddressPanel ippanel) {
    return new Runnable() {
        public void run() {
			try {
				String ipaddress = ippanel.getIpaddress();
				Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
				if(host == null)return;
				//过滤只通过PING采集数据的设备
				if(host.getCollecttype()==SystemConstant.COLLECTTYPE_PING || host.getCollecttype()==SystemConstant.COLLECTTYPE_SSHCONNECT
						|| host.getCollecttype()==SystemConstant.COLLECTTYPE_TELNETCONNECT || "as400".equals(host.getSysOid()))return;
				String oid = host.getSysOid();
				oid = oid.replaceAll("\\.", "-");
				PanelXmlOperator panelxmlOpr = new PanelXmlOperator();
				String filename = SysUtil.doip(host.getIpAddress())+ ".jsp";
				panelxmlOpr.setFile(filename, 2);
				panelxmlOpr.setOid(oid);
				panelxmlOpr.setImageType(ippanel.getImageType());   // nielin add at 2010-01-12
				panelxmlOpr.setIpaddress(host.getIpAddress());
				// 写XML
				panelxmlOpr.init4createXml();
				panelxmlOpr.createXml(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    };
	}
	
    /**
     *	创建轮询节点的任务
     */	
	private Runnable createPollTask(final Host host,final List list) {
    return new Runnable() {
        public void run() {
			try{
				//Host host = (Host)nodelist.get(i);
				host.setMoidList(list);
				host.getAlarmMessage().clear();
				host.doPoll();
			}catch(Exception e){
				e.printStackTrace();
			}
        }
    };
	}
	
	/**
     *	创建轮询改变节点的状态任务
     */	
//	private Runnable createPollRefreshStateTask(final Host host) {
//    return new Runnable() {
//        public void run() {
//			try{
//				host.refreshNodeState();
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//        }
//    };
//	}
	
//    /**
//     *	创建轮询子视图的任务
//     */	
//	private Runnable createChildViewTask(final CustomXml xml) {
//    return new Runnable() {
//        public void run() {
//        	XmlOperator xmlOpr = new XmlOperator();
//			try {
//				xmlOpr.setFile(xml.getXmlName());
//				xmlOpr.init4updateXml();
//				xmlOpr.updateInfo(true);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//        }
//    };
//	}
	
    /**
     *	创建轮询子视图的任务
     */	
	private void createSubViewTask(ManageXml xml) {
		try {
		    XmlOperator xmlOpr = new XmlOperator();
			xmlOpr.setFile(xml.getXmlName());
			xmlOpr.init4updateXml();
			xmlOpr.updateInfo(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(xml.getTopoType()==1){//业务视图，需要更新业务视图的flex-xml文件
			SysLogger.info("--开始更新业务视图的flex-xml文件--");
			//NodeDependDao nodeDependDao = new NodeDependDao();
			try{
				List list = new ArrayList();
				if(ShareData.getAllnodedepend() != null ){
					if(((Hashtable)ShareData.getAllnodedepend()).containsKey(xml.getXmlName())){
						list = (List)((Hashtable)ShareData.getAllnodedepend()).get(xml.getXmlName());
					}
				}
			    //List list = nodeDependDao.findByXml(xml.getXmlName());
			    ChartXml chartxml;
				chartxml = new ChartXml("NetworkMonitor","/"+xml.getXmlName().replace("jsp", "xml"));
				chartxml.addBussinessXML(xml.getTopoName(),list);
				ChartXml chartxmlList;
				chartxmlList = new ChartXml("NetworkMonitor","/"+xml.getXmlName().replace("jsp", "xml").replace("businessmap", "list"));
				chartxmlList.addListXML(xml.getTopoName(),list);
			}catch(Exception e){
			    e.printStackTrace();   	
			}finally{
				//nodeDependDao.close();
            }
		}
	}

}
