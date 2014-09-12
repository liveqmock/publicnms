/*
 * Created on 2010-06-24
 *
 */
package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.Huawei3comtelnetUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Task;
import com.afunms.polling.snmp.cpu.BDComCpuSnmp;
import com.afunms.polling.snmp.cpu.CiscoCpuSnmp;
import com.afunms.polling.snmp.cpu.DLinkCpuSnmp;
import com.afunms.polling.snmp.cpu.EnterasysCpuSnmp;
import com.afunms.polling.snmp.cpu.H3CCpuSnmp;
import com.afunms.polling.snmp.cpu.MaipuCpuSnmp;
import com.afunms.polling.snmp.cpu.NortelCpuSnmp;
import com.afunms.polling.snmp.cpu.RadwareCpuSnmp;
import com.afunms.polling.snmp.cpu.RedGiantCpuSnmp;
import com.afunms.polling.snmp.cpu.ZTECpuSnmp;
import com.afunms.polling.snmp.fan.CiscoFanSnmp;
import com.afunms.polling.snmp.fan.H3CFanSnmp;
import com.afunms.polling.snmp.flash.BDComFlashSnmp;
import com.afunms.polling.snmp.flash.CiscoFlashSnmp;
import com.afunms.polling.snmp.flash.H3CFlashSnmp;
import com.afunms.polling.snmp.interfaces.ArpSnmp;
import com.afunms.polling.snmp.interfaces.FdbSnmp;
import com.afunms.polling.snmp.interfaces.InterfaceSnmp;
import com.afunms.polling.snmp.interfaces.PackageSnmp;
import com.afunms.polling.snmp.interfaces.RouterSnmp;
import com.afunms.polling.snmp.memory.BDComMemorySnmp;
import com.afunms.polling.snmp.memory.CiscoMemorySnmp;
import com.afunms.polling.snmp.memory.DLinkMemorySnmp;
import com.afunms.polling.snmp.memory.EnterasysMemorySnmp;
import com.afunms.polling.snmp.memory.H3CMemorySnmp;
import com.afunms.polling.snmp.memory.MaipuMemorySnmp;
import com.afunms.polling.snmp.memory.NortelMemorySnmp;
import com.afunms.polling.snmp.memory.RedGiantMemorySnmp;
import com.afunms.polling.snmp.ping.PingSnmp;
import com.afunms.polling.snmp.power.CiscoPowerSnmp;
import com.afunms.polling.snmp.power.H3CPowerSnmp;
import com.afunms.polling.snmp.system.SystemSnmp;
import com.afunms.polling.snmp.temperature.BDComTemperatureSnmp;
import com.afunms.polling.snmp.temperature.CiscoTemperatureSnmp;
import com.afunms.polling.snmp.temperature.H3CTemperatureSnmp;
import com.afunms.polling.snmp.voltage.CiscoVoltageSnmp;
import com.afunms.polling.snmp.voltage.H3CVoltageSnmp;
import com.afunms.topology.dao.ConnectTypeConfigDao;
import com.afunms.topology.dao.DiscoverConfigDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.ConnectTypeConfig;
import com.afunms.topology.model.HostNode;





/**
 * @author nielin
 * @date 2010-06-24 
 * 
 * 对网络设备进行采集
 *
 */
public class M3Task extends MonitorTask {
	
	public void run() {
		try{
			
			
			//SysLogger.info("######### M5Task Thread Count : "+Thread.activeCount());
			//在run方法里面加入，Huawei3comtelnetUtil.inittelnetlist();  主要目的是定时更新内存列表中需要vpn采集的列表 较少对数据库的连接
			//5分钟实例化一次vpn的配置列表-----konglq
			//System.out.println("=====&&&&&&&=====初始花telnet 采集===");
			//Huawei3comtelnetUtil.inittelnetlist();//加入的内容
			//-------konglq---------
			
			
			///pingsnmp类定时查询数据库配置 并把数据更新到内存中 pingsnmpConnectTypeConfig  (key)
			
//	        ConnectTypeConfigDao connectTypeConfigDao = new ConnectTypeConfigDao();
//	        Hashtable connectConfigHashtable = new Hashtable();
//			List configList = new ArrayList();
//			try{
//				configList = connectTypeConfigDao.loadAll();
//			}catch(Exception e){
//				
//			}finally{
//				connectTypeConfigDao.close();
//				connectTypeConfigDao = null;
//			}
//			if(configList != null && configList.size()>0){
//				for(int i=0;i<configList.size();i++){
//					ConnectTypeConfig connectTypeConfig = (ConnectTypeConfig)configList.get(i);
//					connectConfigHashtable.put(connectTypeConfig.getNode_id(), connectTypeConfig);
//				}
//			}
//			
//			ShareData.getConnectConfigHashtable().put("connectConfigHashtable", connectConfigHashtable);
//
//			//装载告警历史
//			Hashtable checkEventHashtable = new Hashtable();
//			CheckEventDao checkeventdao = new CheckEventDao();
//			List eventlist = new ArrayList();
//			try{
//				eventlist = checkeventdao.loadAll();
//			}catch(Exception e){
//				
//			}finally{
//				checkeventdao.close();
//			}
//			if(eventlist != null && eventlist.size()>0){
//				CheckEvent vo = null;
//				for(int i=0;i<eventlist.size();i++){
//					vo = (CheckEvent)eventlist.get(i);
//					checkEventHashtable.put(vo.getName(), vo.getAlarmlevel());
//				}
//			}
//			ShareData.setCheckEventHash(checkEventHashtable);
//			
//			List portconfiglist = new ArrayList();
//			PortconfigDao configdao = new PortconfigDao(); 			
//			Portconfig portconfig = null;
//			Hashtable portconfigHash = new Hashtable();
//			try {
//				portconfiglist = configdao.getAllBySms();
//			} catch (RuntimeException e) {
//				e.printStackTrace();
//			} finally {
//				configdao.close();
//			}
//			if(portconfiglist != null && portconfiglist.size()>0){
//				for(int i=0;i<portconfiglist.size();i++){
//					portconfig = (Portconfig)portconfiglist.get(i);
//					if(portconfigHash.containsKey(portconfig.getIpaddress())){
//						List portlist = (List)portconfigHash.get(portconfig.getIpaddress());
//						portlist.add(portconfig);
//						portconfigHash.put(portconfig.getIpaddress(), portlist);
//					}else{
//						List portlist = new ArrayList();
//						portlist.add(portconfig);
//						portconfigHash.put(portconfig.getIpaddress(), portlist);
//					}
//				}
//			} 
//			ShareData.setPortConfigHash(portconfigHash);
			
			//--------pingsnmp----------end-------
			
			
			
			NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
	    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
	    	try{
	    		//获取被启用的所有被监视指标
	    		monitorItemList = indicatorsdao.getByIntervalAndType("3", "m",1,"net");
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		indicatorsdao.close();
	    	}
	    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();

        		int numThreads = 200;        		
        		try {
        			List numList = new ArrayList();
        			TaskXml taskxml = new TaskXml();
        			numList = taskxml.ListXml();
        			for (int i = 0; i < numList.size(); i++) {
        				Task task = new Task();
        				BeanUtils.copyProperties(task, numList.get(i));
        				if (task.getTaskname().equals("netthreadnum")){
        					numThreads = task.getPolltime().intValue();
        				}
        			}

        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        		HostNodeDao nodedao = new HostNodeDao();
        		List nodelist = new ArrayList();
        		try{
        			nodelist = nodedao.loadMonitorNet();
        		}catch(Exception e){
        			
        		}finally{
        			nodedao.close();
        		}
        		Hashtable nodehash = new Hashtable();
        		if(nodelist != null && nodelist.size()>0){
        			for(int i=0;i<nodelist.size();i++){
        				HostNode node = (HostNode)nodelist.get(i);
        				nodehash.put(node.getId()+"", node.getId());
        			}
        		}        		
        		final Hashtable alldata = new Hashtable();
        		
        		
        		
        		
        		Date _startdate = new Date();
        		Hashtable docollcetHash = new Hashtable();
        		for (int i=0; i<monitorItemList.size(); i++) {
        			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
        			if(docollcetHash.containsKey(nodeGatherIndicators.getNodeid())){
            			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
            			//过滤掉不监视的设备
            			if(!nodehash.containsKey(nodeGatherIndicators.getNodeid()))continue;
        				List tempList = (List)docollcetHash.get(nodeGatherIndicators.getNodeid());
        				tempList.add(nodeGatherIndicators);
        				docollcetHash.put(nodeGatherIndicators.getNodeid(), tempList);
        			}else{
        				Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
            			//过滤掉不监视的设备
            			if(!nodehash.containsKey(nodeGatherIndicators.getNodeid()))continue;
        				List tempList = new ArrayList();
        				tempList.add(nodeGatherIndicators);
        				docollcetHash.put(nodeGatherIndicators.getNodeid(), tempList);
        			}
        			
        			
//        			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
//        			//过滤掉不监视的设备
//        			if(!nodehash.containsKey(nodeGatherIndicators.getNodeid()))continue;
//        			threadPool.runTask(createTask(nodeGatherIndicators,alldata));           		
        		}
        		
        		if(docollcetHash != null){
        			numThreads = docollcetHash.size();
        			if(numThreads > 200){
        				numThreads = 200;
        			}
        		}
        		// 生成线程池,数目依据被监视的设备多少
        		ThreadPool threadPool = null;													
        		// 运行任务
        		if(docollcetHash != null && docollcetHash.size()>0){
        			threadPool = new ThreadPool(docollcetHash.size());	
        			Enumeration newProEnu = docollcetHash.keys();
        			while(newProEnu.hasMoreElements())
        			{
        				String nodeid = (String)newProEnu.nextElement();
        				List dolist = (List)docollcetHash.get(nodeid);
        				threadPool.runTask(createTask(dolist,alldata));
        	
        			}
        			// 关闭线程池并等待所有任务完成
            		threadPool.join();             		
            		threadPool.close();
            		//threadPool.destroy();
        		}
        		threadPool = null;
        		
        		
        		Date _enddate = new Date();
        		SysLogger.info("##############################");
				SysLogger.info("### 所有网络设备采集时间 "+(_enddate.getTime()-_startdate.getTime())+"####");
				SysLogger.info("##############################");
        		
        		
        		
        		HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
        		Date startdate = new Date();
        		hostdataManager.createHostItemData(alldata, "net");
        		Date enddate = new Date();
        		SysLogger.info("##############################");
				SysLogger.info("### 所有网络设备入库时间 "+(enddate.getTime()-startdate.getTime())+"####");
				SysLogger.info("##############################");
				alldata.clear();
				//alldata = null;
        		
		}catch(Exception e){
			SysLogger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			SysLogger.info(e.getMessage());
			SysLogger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			e.printStackTrace();
			//若出现例外，则需要重新调用启动定时任务的方法
		}finally{
			//Runtime.getRuntime().gc();
			SysLogger.info("********M3Task Thread Count : "+Thread.activeCount());
		}
	}
	
	/**
    创建任务
	 */	
	private static Runnable createTask(final List list,final Hashtable alldata) {
    return new Runnable() {
        public void run() {
        	try {                	
            	Vector vector=null;
            	Hashtable hashv = new Hashtable();
            	Hashtable returnHash = new Hashtable();
            	HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
            	NodeGatherIndicators alarmIndicatorsNode = null;
            	if(list != null && list.size()>0){
            		for(int k=0;k<list.size();k++){
            			alarmIndicatorsNode = (NodeGatherIndicators)list.get(k);
            			if(alarmIndicatorsNode.getName().equalsIgnoreCase("cpu")){
                    		//CPU的采集
                    		if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("cisco")){
                    			//CISCO的CPU
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				CiscoCpuSnmp ciscocpusnmp = null;
                        			try{
                        				ciscocpusnmp = (CiscoCpuSnmp)Class.forName("com.afunms.polling.snmp.cpu.CiscoCpuSnmp").newInstance();
                            			returnHash = ciscocpusnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("cpu", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("cpu", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("cpu", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","cisco","cpu");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("h3c")){
                    			//H3C的CPU
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				H3CCpuSnmp h3ccpusnmp = null;
                        			try{
                        				Date startdate1 = new Date();
                        				h3ccpusnmp = (H3CCpuSnmp)Class.forName("com.afunms.polling.snmp.cpu.H3CCpuSnmp").newInstance();
                            			returnHash = h3ccpusnmp.collect_Data(alarmIndicatorsNode);
                            			Date enddate1 = new Date();
                                		//SysLogger.info("##############################");
                        				//SysLogger.info("### 所有网络设备CPU采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                        				//SysLogger.info("##############################");
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("cpu", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("cpu", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("cpu", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","h3c","cpu");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("zte")){
                    			//中兴的CPU
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				ZTECpuSnmp ztecpusnmp = null;
                        			try{
                        				ztecpusnmp = (ZTECpuSnmp)Class.forName("com.afunms.polling.snmp.cpu.ZTECpuSnmp").newInstance();
                            			returnHash = ztecpusnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("cpu", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("cpu", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("cpu", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","h3c","cpu");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("entrasys")){
                    			//凯创的CPU
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				EnterasysCpuSnmp enterasyscpusnmp = null;
                        			try{
                        				enterasyscpusnmp = (EnterasysCpuSnmp)Class.forName("com.afunms.polling.snmp.cpu.EnterasysCpuSnmp").newInstance();
                            			returnHash = enterasyscpusnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("cpu", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("cpu", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("cpu", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","enterasys","cpu");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("radware")){
                    			//radware的CPU
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				RadwareCpuSnmp radwarecpusnmp = null;
                        			try{
                        				radwarecpusnmp = (RadwareCpuSnmp)Class.forName("com.afunms.polling.snmp.cpu.RadwareCpuSnmp").newInstance();
                            			returnHash = radwarecpusnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("cpu", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("cpu", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("cpu", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","radware","cpu");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("maipu")){
                    			//maipu的CPU
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				MaipuCpuSnmp maipucpusnmp = null;
                        			try{
                        				maipucpusnmp = (MaipuCpuSnmp)Class.forName("com.afunms.polling.snmp.cpu.MaipuCpuSnmp").newInstance();
                            			returnHash = maipucpusnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("cpu", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("cpu", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("cpu", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","maipu","cpu");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("redgiant")){
                    			//redgiant的CPU
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				RedGiantCpuSnmp redgiantcpusnmp = null;
                        			try{
                        				redgiantcpusnmp = (RedGiantCpuSnmp)Class.forName("com.afunms.polling.snmp.cpu.RedGiantCpuSnmp").newInstance();
                            			returnHash = redgiantcpusnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("cpu", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("cpu", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("cpu", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","redgiant","cpu");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("northtel")){
                    			//北电的CPU
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				NortelCpuSnmp nortelcpusnmp = null;
                        			try{
                        				nortelcpusnmp = (NortelCpuSnmp)Class.forName("com.afunms.polling.snmp.cpu.NortelCpuSnmp").newInstance();
                            			returnHash = nortelcpusnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("cpu", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("cpu", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("cpu", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","northtel","cpu");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("dlink")){
                    			//dlink的CPU
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				DLinkCpuSnmp dlinkcpusnmp = null;
                        			try{
                        				dlinkcpusnmp = (DLinkCpuSnmp)Class.forName("com.afunms.polling.snmp.cpu.DLinkCpuSnmp").newInstance();
                            			returnHash = dlinkcpusnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("cpu", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("cpu", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("cpu", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","dlink","cpu");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("bdcom")){
                    			//博达的CPU
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				BDComCpuSnmp bdcomcpusnmp = null;
                        			try{
                        				bdcomcpusnmp = (BDComCpuSnmp)Class.forName("com.afunms.polling.snmp.cpu.BDComCpuSnmp").newInstance();
                            			returnHash = bdcomcpusnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("cpu", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("cpu", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("cpu", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","bdcom","cpu");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("memory")){
                    		//内存的采集
                    		if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("cisco")){
                        		//CISCO的MEMORY
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				CiscoMemorySnmp ciscomemorysnmp = null;
                        			try{
                        				ciscomemorysnmp = (CiscoMemorySnmp)Class.forName("com.afunms.polling.snmp.memory.CiscoMemorySnmp").newInstance();
                            			returnHash = ciscomemorysnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("memory", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("memory", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("memory", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","cisco","memory");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("h3c")){ 
                    			//h3c的MEMORY
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				H3CMemorySnmp h3cmemorysnmp = null;
                        			try{
                        				Date startdate1 = new Date();
                        				h3cmemorysnmp = (H3CMemorySnmp)Class.forName("com.afunms.polling.snmp.memory.H3CMemorySnmp").newInstance();
                            			returnHash = h3cmemorysnmp.collect_Data(alarmIndicatorsNode);
                            			Date enddate1 = new Date();
                                		SysLogger.info("##############################");
                        				SysLogger.info("### "+node.getIpAddress()+" 网络设备Memory采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                        				SysLogger.info("##############################");
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("memory", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("memory", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("memory", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","h3c","memory");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("entrasys")){ 
                    			//entrasys的MEMORY
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				EnterasysMemorySnmp enterasysmemorysnmp = null;
                        			try{
                        				enterasysmemorysnmp = (EnterasysMemorySnmp)Class.forName("com.afunms.polling.snmp.memory.EnterasysMemorySnmp").newInstance();
                            			returnHash = enterasysmemorysnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("memory", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("memory", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("memory", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","entrasys","memory");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("radware")){ 
                    			//radware的MEMORY
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				EnterasysMemorySnmp enterasysmemorysnmp = null;
                        			try{
                        				enterasysmemorysnmp = (EnterasysMemorySnmp)Class.forName("com.afunms.polling.snmp.memory.EnterasysMemorySnmp").newInstance();
                            			returnHash = enterasysmemorysnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("memory", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("memory", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("memory", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","entrasys","memory");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("maipu")){ 
                    			//maipu的MEMORY
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				MaipuMemorySnmp maipumemorysnmp = null;
                        			try{
                        				maipumemorysnmp = (MaipuMemorySnmp)Class.forName("com.afunms.polling.snmp.memory.MaipuMemorySnmp").newInstance();
                            			returnHash = maipumemorysnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("memory", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("memory", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("memory", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","maipu","memory");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("redgiant")){ 
                    			//redgiant的MEMORY
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				RedGiantMemorySnmp redmemorysnmp = null;
                        			try{
                        				redmemorysnmp = (RedGiantMemorySnmp)Class.forName("com.afunms.polling.snmp.memory.RedGiantMemorySnmp").newInstance();
                            			returnHash = redmemorysnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("memory", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("memory", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("memory", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","redgiant","memory");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("northtel")){ 
                    			//northtel的MEMORY
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				NortelMemorySnmp nortelmemorysnmp = null;
                        			try{
                        				nortelmemorysnmp = (NortelMemorySnmp)Class.forName("com.afunms.polling.snmp.memory.NortelMemorySnmp").newInstance();
                            			returnHash = nortelmemorysnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("memory", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("memory", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("memory", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","northtel","memory");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("dlink")){ 
                    			//dlink的MEMORY
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				DLinkMemorySnmp dlinkmemorysnmp = null;
                        			try{
                        				dlinkmemorysnmp = (DLinkMemorySnmp)Class.forName("com.afunms.polling.snmp.memory.DLinkMemorySnmp").newInstance();
                            			returnHash = dlinkmemorysnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("memory", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("memory", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("memory", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","dlink","memory");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("bdcom")){ 
                    			//bdcom的MEMORY
                    			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    			if(node != null){
                    				BDComMemorySnmp bdcommemorysnmp = null;
                        			try{
                        				bdcommemorysnmp = (BDComMemorySnmp)Class.forName("com.afunms.polling.snmp.memory.BDComMemorySnmp").newInstance();
                            			returnHash = bdcommemorysnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("memory", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("memory", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("memory", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,"net","bdcom","memory");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("systemgroup")){
                    		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    		if(node != null){
                    			SystemSnmp systemsnmp = null;
                    			try{
                    				Date startdate1 = new Date();
                    				systemsnmp = (SystemSnmp)Class.forName("com.afunms.polling.snmp.system.SystemSnmp").newInstance();
                        			returnHash = systemsnmp.collect_Data(alarmIndicatorsNode);
                        			Date enddate1 = new Date();
                            		SysLogger.info("##############################");
                    				SysLogger.info("### "+node.getIpAddress()+" 网络设备SystemGroup采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                    				SysLogger.info("##############################");
                        			if(alldata.containsKey(node.getIpAddress())){
                        				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                        				if(ipdata != null){
                        					ipdata.put("systemgroup", returnHash);
                        				}else{
                        					ipdata = new Hashtable();
                        					ipdata.put("systemgroup", returnHash);
                        				}
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}else{
                        				Hashtable ipdata = new Hashtable();
                        				ipdata.put("systemgroup", returnHash);
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}
                        			//IP-MAC不存入数据库
                        			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"ipmac");
                        		}catch(Exception e){
                        			e.printStackTrace();
                        		}
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("interface")){
                    		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    		if(node != null){
                    			InterfaceSnmp interfacesnmp = null;
                    			try{
//                    				SysLogger.info("############################################################");
//                    				SysLogger.info("##########开始采集"+node.getIpAddress()+" 接口信息...##########");
//                    				SysLogger.info("############################################################");
                    				Date startdate1 = new Date();
                    				interfacesnmp = (InterfaceSnmp)Class.forName("com.afunms.polling.snmp.interfaces.InterfaceSnmp").newInstance();
                        			returnHash = interfacesnmp.collect_Data(alarmIndicatorsNode);
                        			Date enddate1 = new Date();
                            		SysLogger.info("##############################");
                    				SysLogger.info("### "+node.getIpAddress()+" 网络设备Interface采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                    				SysLogger.info("##############################");
                        			
                        			if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("h3c")){
                        				//对出入总流量值进行告警检测
                            		    try{
                            				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
                            				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "h3c");
                            				for(int i = 0 ; i < list.size() ; i ++){
                            					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
                            					if(alarmIndicatorsnode.getName().equalsIgnoreCase("AllInBandwidthUtilHdx") || alarmIndicatorsnode.getName().equalsIgnoreCase("AllOutBandwidthUtilHdx")
                            							|| alarmIndicatorsnode.getName().equalsIgnoreCase("utilhdx")){
                            						//对总出入口流量值进行告警检测
                            						
                            						//System.out.println("+==========777777======="+alarmIndicatorsnode.getName());
                                					CheckEventUtil checkutil = new CheckEventUtil();
                                					checkutil.updateData(node,returnHash,"net","h3c",alarmIndicatorsnode);
                            					}
                            				}
                            		    }catch(Exception e){
                            		    	e.printStackTrace();
                            		    }
                        			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("cisco")){
                        				//对出入总流量值进行告警检测
                            		    try{
                            				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
                            				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "cisco");
                            				for(int i = 0 ; i < list.size() ; i ++){
                            					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
                            					if(alarmIndicatorsnode.getName().equalsIgnoreCase("AllInBandwidthUtilHdx") || alarmIndicatorsnode.getName().equalsIgnoreCase("AllOutBandwidthUtilHdx")
                            							|| alarmIndicatorsnode.getName().equalsIgnoreCase("utilhdx")){
                            						//对总出入口流量值进行告警检测
                                					CheckEventUtil checkutil = new CheckEventUtil();
                                					checkutil.updateData(node,returnHash,"net","cisco",alarmIndicatorsnode);
                            					}
                            				}
                            		    }catch(Exception e){
                            		    	e.printStackTrace();
                            		    }
                        			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("zte")){
                        				//对出入总流量值进行告警检测
                            		    try{
                            				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
                            				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "zte");
                            				for(int i = 0 ; i < list.size() ; i ++){
                            					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
                            					if(alarmIndicatorsnode.getName().equalsIgnoreCase("AllInBandwidthUtilHdx") || alarmIndicatorsnode.getName().equalsIgnoreCase("AllOutBandwidthUtilHdx")
                            							|| alarmIndicatorsnode.getName().equalsIgnoreCase("utilhdx")){
                            						//对总出入口流量值进行告警检测
                                					CheckEventUtil checkutil = new CheckEventUtil();
                                					checkutil.updateData(node,returnHash,"net","zte",alarmIndicatorsnode);
                            					}
                            				}
                            		    }catch(Exception e){
                            		    	e.printStackTrace();
                            		    }
                        			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("entrasys")){
                        				//对出入总流量值进行告警检测
                            		    try{
                            				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
                            				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "zte");
                            				for(int i = 0 ; i < list.size() ; i ++){
                            					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
                            					if(alarmIndicatorsnode.getName().equalsIgnoreCase("AllInBandwidthUtilHdx") || alarmIndicatorsnode.getName().equalsIgnoreCase("AllOutBandwidthUtilHdx")
                            							|| alarmIndicatorsnode.getName().equalsIgnoreCase("utilhdx")){
                            						//对总出入口流量值进行告警检测
                                					CheckEventUtil checkutil = new CheckEventUtil();
                                					checkutil.updateData(node,returnHash,"net","entrasys",alarmIndicatorsnode);
                            					}
                            				}
                            		    }catch(Exception e){
                            		    	e.printStackTrace();
                            		    } 
                        			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("radware")){
                        				//对出入总流量值进行告警检测
                            		    try{
                            				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
                            				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "zte");
                            				for(int i = 0 ; i < list.size() ; i ++){
                            					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
                            					if(alarmIndicatorsnode.getName().equalsIgnoreCase("AllInBandwidthUtilHdx") || alarmIndicatorsnode.getName().equalsIgnoreCase("AllOutBandwidthUtilHdx")
                            							|| alarmIndicatorsnode.getName().equalsIgnoreCase("utilhdx")){
                            						//对总出入口流量值进行告警检测
                                					CheckEventUtil checkutil = new CheckEventUtil();
                                					checkutil.updateData(node,returnHash,"net","radware",alarmIndicatorsnode);
                            					}
                            				}
                            		    }catch(Exception e){
                            		    	e.printStackTrace();
                            		    }
                        			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("maipu")){
                        				//对出入总流量值进行告警检测
                            		    try{
                            				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
                            				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "zte");
                            				for(int i = 0 ; i < list.size() ; i ++){
                            					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
                            					if(alarmIndicatorsnode.getName().equalsIgnoreCase("AllInBandwidthUtilHdx") || alarmIndicatorsnode.getName().equalsIgnoreCase("AllOutBandwidthUtilHdx")
                            							|| alarmIndicatorsnode.getName().equalsIgnoreCase("utilhdx")){
                            						//对总出入口流量值进行告警检测
                                					CheckEventUtil checkutil = new CheckEventUtil();
                                					checkutil.updateData(node,returnHash,"net","maipu",alarmIndicatorsnode);
                            					}
                            				}
                            		    }catch(Exception e){
                            		    	e.printStackTrace();
                            		    }
                        			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("redgiant")){
                        				//对出入总流量值进行告警检测
                            		    try{
                            				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
                            				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "zte");
                            				for(int i = 0 ; i < list.size() ; i ++){
                            					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
                            					if(alarmIndicatorsnode.getName().equalsIgnoreCase("AllInBandwidthUtilHdx") || alarmIndicatorsnode.getName().equalsIgnoreCase("AllOutBandwidthUtilHdx")
                            							|| alarmIndicatorsnode.getName().equalsIgnoreCase("utilhdx")){
                            						//对总出入口流量值进行告警检测
                                					CheckEventUtil checkutil = new CheckEventUtil();
                                					checkutil.updateData(node,returnHash,"net","redgiant",alarmIndicatorsnode);
                            					}
                            				}
                            		    }catch(Exception e){
                            		    	e.printStackTrace();
                            		    }
                        			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("northtel")){
                        				//对出入总流量值进行告警检测
                            		    try{
                            				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
                            				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "zte");
                            				for(int i = 0 ; i < list.size() ; i ++){
                            					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
                            					if(alarmIndicatorsnode.getName().equalsIgnoreCase("AllInBandwidthUtilHdx") || alarmIndicatorsnode.getName().equalsIgnoreCase("AllOutBandwidthUtilHdx")
                            							|| alarmIndicatorsnode.getName().equalsIgnoreCase("utilhdx")){
                            						//对总出入口流量值进行告警检测
                                					CheckEventUtil checkutil = new CheckEventUtil();
                                					checkutil.updateData(node,returnHash,"net","northtel",alarmIndicatorsnode);
                            					}
                            				}
                            		    }catch(Exception e){
                            		    	e.printStackTrace();
                            		    }
                        			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("dlink")){
                        				//对出入总流量值进行告警检测
                            		    try{
                            				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
                            				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "zte");
                            				for(int i = 0 ; i < list.size() ; i ++){
                            					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
                            					if(alarmIndicatorsnode.getName().equalsIgnoreCase("AllInBandwidthUtilHdx") || alarmIndicatorsnode.getName().equalsIgnoreCase("AllOutBandwidthUtilHdx")
                            							|| alarmIndicatorsnode.getName().equalsIgnoreCase("utilhdx")){
                            						//对总出入口流量值进行告警检测
                                					CheckEventUtil checkutil = new CheckEventUtil();
                                					checkutil.updateData(node,returnHash,"net","dlink",alarmIndicatorsnode);
                            					}
                            				}
                            		    }catch(Exception e){
                            		    	e.printStackTrace();
                            		    }
                        			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("bdcom")){
                        				//对出入总流量值进行告警检测
                            		    try{
                            				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
                            				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "zte");
                            				for(int i = 0 ; i < list.size() ; i ++){
                            					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
                            					if(alarmIndicatorsnode.getName().equalsIgnoreCase("AllInBandwidthUtilHdx") || alarmIndicatorsnode.getName().equalsIgnoreCase("AllOutBandwidthUtilHdx")
                            							|| alarmIndicatorsnode.getName().equalsIgnoreCase("utilhdx")){
                            						//对总出入口流量值进行告警检测
                                					CheckEventUtil checkutil = new CheckEventUtil();
                                					checkutil.updateData(node,returnHash,"net","bdcom",alarmIndicatorsnode);
                            					}
                            				}
                            		    }catch(Exception e){
                            		    	e.printStackTrace();
                            		    }
                        			}
                        			//存入数据库
                        			if(alldata.containsKey(node.getIpAddress())){
                        				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                        				if(ipdata != null){
                        					ipdata.put("interface", returnHash);
                        				}else{
                        					ipdata = new Hashtable();
                        					ipdata.put("interface", returnHash);
                        				}
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}else{
                        				Hashtable ipdata = new Hashtable();
                        				ipdata.put("interface", returnHash);
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}
                        			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"interface");
                        		}catch(Exception e){
                        			e.printStackTrace();
                        		}
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("packs")){
                    		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    		if(node != null){
                    			PackageSnmp packagesnmp = null;
                    			try{
                    				Date startdate1 = new Date();
                    				packagesnmp = (PackageSnmp)Class.forName("com.afunms.polling.snmp.interfaces.PackageSnmp").newInstance();
                        			returnHash = packagesnmp.collect_Data(alarmIndicatorsNode);
                        			Date enddate1 = new Date();
                             		SysLogger.info("##############################");
                     				SysLogger.info("### "+node.getIpAddress()+" 网络设备Pack采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                     				SysLogger.info("##############################");
                        			//存入数据库
                        			if(alldata.containsKey(node.getIpAddress())){
                        				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                        				if(ipdata != null){
                        					ipdata.put("packs", returnHash);
                        				}else{
                        					ipdata = new Hashtable();
                        					ipdata.put("packs", returnHash);
                        				}
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}else{
                        				Hashtable ipdata = new Hashtable();
                        				ipdata.put("packs", returnHash);
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}
                        			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"packs");
                        		}catch(Exception e){
                        			e.printStackTrace();
                        		}
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("ping")){
                    		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    		if(node != null){
                    			PingSnmp pingsnmp = null;
                    			try{
                    				Date startdate1 = new Date();
                    				pingsnmp = (PingSnmp)Class.forName("com.afunms.polling.snmp.ping.PingSnmp").newInstance();
                        			returnHash = pingsnmp.collect_Data(alarmIndicatorsNode);
                        			Date enddate1 = new Date();
                             		SysLogger.info("##############################");
                     				SysLogger.info("### "+node.getIpAddress()+" 网络设备Ping采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                     				SysLogger.info("##############################");
                        			if(alldata.containsKey(node.getIpAddress())){
                        				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                        				if(ipdata != null){
                        					ipdata.put("ping", returnHash);
                        				}else{
                        					ipdata = new Hashtable();
                        					ipdata.put("ping", returnHash);
                        				}
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}else{
                        				Hashtable ipdata = new Hashtable();
                        				ipdata.put("ping", returnHash);
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}
                        			
                        			//对PING值进行告警检测
                        			if(returnHash != null && returnHash.size()>0){
                        				Vector pingvector = (Vector)returnHash.get("ping");
                        				if(pingvector != null){
                        					for (int i = 0; i < pingvector.size(); i++) {
                                				Pingcollectdata pingdata = (Pingcollectdata) pingvector.elementAt(i);
                                				if(pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")){
                            						//连通率进行判断
                            						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();                						
                            						List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), alarmIndicatorsNode.getType(), "");
                            						for(int m = 0 ; m < list.size() ; m ++){
                            							AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(m);
                            							if("1".equals(_alarmIndicatorsNode.getEnabled())){
                            								if(_alarmIndicatorsNode.getName().equalsIgnoreCase("ping")){
                            									CheckEventUtil checkeventutil = new CheckEventUtil();
                            									//SysLogger.info(_alarmIndicatorsNode.getName()+"=====_alarmIndicatorsNode.getName()=========");
                            									checkeventutil.checkEvent(node, _alarmIndicatorsNode, pingdata.getThevalue());
                            								}
                            							}  
                            						}
                            						
                            					}
                                			}
                        				}
                        			}
                        			//在采集过程中已经存入数据库
                        			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"utilhdx");
                        		}catch(Exception e){
                        			e.printStackTrace();
                        		}
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("ipmac")){
                    		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    		if(node != null){
                    			ArpSnmp arpsnmp = null;
                    			try{
                    				Date startdate1 = new Date();
                    				arpsnmp = (ArpSnmp)Class.forName("com.afunms.polling.snmp.interfaces.ArpSnmp").newInstance();
                        			returnHash = arpsnmp.collect_Data(alarmIndicatorsNode);
                        			Date enddate1 = new Date();
                             		SysLogger.info("##############################");
                     				SysLogger.info("### "+node.getIpAddress()+" 网络设备IPMAC采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                     				SysLogger.info("##############################");
                        			if(alldata.containsKey(node.getIpAddress())){
                        				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                        				if(ipdata != null){
                        					ipdata.put("ipmac", returnHash);
                        				}else{
                        					ipdata = new Hashtable();
                        					ipdata.put("ipmac", returnHash);
                        				}
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}else{
                        				Hashtable ipdata = new Hashtable();
                        				ipdata.put("ipmac", returnHash);
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}
                        			//IP-MAC不存入数据库
                        			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"ipmac");
                        		}catch(Exception e){
                        			e.printStackTrace();
                        		}
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("router")){
                    		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    		if(node != null){
                    			RouterSnmp routersnmp = null;
                    			try{
                    				Date startdate1 = new Date();
                    				routersnmp = (RouterSnmp)Class.forName("com.afunms.polling.snmp.interfaces.RouterSnmp").newInstance();
                        			returnHash = routersnmp.collect_Data(alarmIndicatorsNode);
                        			Date enddate1 = new Date();
                             		SysLogger.info("##############################");
                     				SysLogger.info("### "+node.getIpAddress()+" 网络设备ROUTER采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                     				SysLogger.info("##############################");
                        			if(alldata.containsKey(node.getIpAddress())){
                        				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                        				if(ipdata != null){
                        					ipdata.put("iprouter", returnHash);
                        				}else{
                        					ipdata = new Hashtable();
                        					ipdata.put("iprouter", returnHash);
                        				}
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}else{
                        				Hashtable ipdata = new Hashtable();
                        				ipdata.put("iprouter", returnHash);
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}
                        			//路由表不存入数据库
                        			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"ipmac");
                        		}catch(Exception e){
                        			e.printStackTrace();
                        		}
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("fdb")){
                    		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    		if(node != null){
                    			FdbSnmp fdbsnmp = null;
                    			try{
                    				//SysLogger.info("开始采集FDB表################");
                    				Date startdate1 = new Date();
                    				fdbsnmp = (FdbSnmp)Class.forName("com.afunms.polling.snmp.interfaces.FdbSnmp").newInstance();
                        			returnHash = fdbsnmp.collect_Data(alarmIndicatorsNode);
                        			Date enddate1 = new Date();
                             		SysLogger.info("##############################");
                     				SysLogger.info("### "+node.getIpAddress()+" 网络设备FDB采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                     				SysLogger.info("##############################");
                        			if(alldata.containsKey(node.getIpAddress())){
                        				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                        				if(ipdata != null){
                        					ipdata.put("fdb", returnHash);
                        				}else{
                        					ipdata = new Hashtable();
                        					ipdata.put("fdb", returnHash);
                        				}
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}else{
                        				Hashtable ipdata = new Hashtable();
                        				ipdata.put("fdb", returnHash);
                        				alldata.put(node.getIpAddress(), ipdata);
                        				ipdata = null;
                        			}
                        			//FDB表不存入数据库
                        			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"ipmac");
                        		}catch(Exception e){
                        			e.printStackTrace();
                        		}
                    		}
                    		
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("flash")){
                    		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    		if(node != null){
                    			if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("cisco")){
                    				//CISCO闪存
                    				CiscoFlashSnmp flashsnmp = null;
                        			try{
                        				//SysLogger.info("开始采集FLASH信息################");
                        				flashsnmp = (CiscoFlashSnmp)Class.forName("com.afunms.polling.snmp.flash.CiscoFlashSnmp").newInstance();
                            			returnHash = flashsnmp.collect_Data(alarmIndicatorsNode);
                            			//flash表不存入数据库
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("flash", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("flash", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("flash", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"flash");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("h3c")){
                    				//h3c闪存
                    				H3CFlashSnmp flashsnmp = null;
                        			try{
                        				//SysLogger.info("开始采集h3c的FLASH信息################");
                        				Date startdate1 = new Date();
                        				flashsnmp = (H3CFlashSnmp)Class.forName("com.afunms.polling.snmp.flash.H3CFlashSnmp").newInstance();
                            			returnHash = flashsnmp.collect_Data(alarmIndicatorsNode);
                            			Date enddate1 = new Date();
                                 		SysLogger.info("##############################");
                         				SysLogger.info("### "+node.getIpAddress()+" 网络设备FLASH采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                         				SysLogger.info("##############################");
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("flash", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("flash", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("flash", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"flash");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("bdcom")){
                    				//BDCOM闪存
                    				BDComFlashSnmp flashsnmp = null;
                        			try{
                        				//SysLogger.info("开始采集h3c的FLASH信息################");
                        				flashsnmp = (BDComFlashSnmp)Class.forName("com.afunms.polling.snmp.flash.BDComFlashSnmp").newInstance();
                            			returnHash = flashsnmp.collect_Data(alarmIndicatorsNode);
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("flash", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("flash", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("flash", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"flash");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    			
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("temperature")){
                    		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    		if(node != null){
                    			if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("cisco")){
                    				//CISCO温度
                    				CiscoTemperatureSnmp tempersnmp = null;
                        			try{
                        				//SysLogger.info("开始采集温度信息################");
                        				tempersnmp = (CiscoTemperatureSnmp)Class.forName("com.afunms.polling.snmp.temperature.CiscoTemperatureSnmp").newInstance();
                            			returnHash = tempersnmp.collect_Data(alarmIndicatorsNode);
                            			//FDB表不存入数据库
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("temperature", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("temperature", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("temperature", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"temperature");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("h3c")){
                    				//h3c温度
                    				H3CTemperatureSnmp tempersnmp = null;
                        			try{
                        				//SysLogger.info("开始采集H3C温度信息################");
                        				Date startdate1 = new Date();
                        				tempersnmp = (H3CTemperatureSnmp)Class.forName("com.afunms.polling.snmp.temperature.H3CTemperatureSnmp").newInstance();
                            			returnHash = tempersnmp.collect_Data(alarmIndicatorsNode);
                            			Date enddate1 = new Date();
                                 		SysLogger.info("##############################");
                         				SysLogger.info("### "+node.getIpAddress()+" 网络设备温度采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                         				SysLogger.info("##############################");
                            			//FDB表不存入数据库
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("temperature", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("temperature", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"temperature");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("bdcom")){
                    				//bdcom温度
                    				BDComTemperatureSnmp tempersnmp = null;
                        			try{
                        				//SysLogger.info("开始采集BDCOM温度信息################");
                        				tempersnmp = (BDComTemperatureSnmp)Class.forName("com.afunms.polling.snmp.temperature.BDComTemperatureSnmp").newInstance();
                            			returnHash = tempersnmp.collect_Data(alarmIndicatorsNode);
                            			//FDB表不存入数据库
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("temperature", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("temperature", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("temperature", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"temperature");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    			
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("fan")){
                    		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    		if(node != null){
                    			if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("cisco")){
                    				//CISCO风扇
                    				CiscoFanSnmp fansnmp = null;
                        			try{
                        				//SysLogger.info("开始采集温度信息################");
                        				fansnmp = (CiscoFanSnmp)Class.forName("com.afunms.polling.snmp.fan.CiscoFanSnmp").newInstance();
                            			returnHash = fansnmp.collect_Data(alarmIndicatorsNode);
                            			//FDB表不存入数据库
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("fan", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("fan", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("fan", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"fan");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("h3c")){
                    				//H3C风扇
                    				H3CFanSnmp fansnmp = null;
                        			try{
                        				//SysLogger.info("开始采集温度信息################");
                        				Date startdate1 = new Date();
                        				fansnmp = (H3CFanSnmp)Class.forName("com.afunms.polling.snmp.fan.H3CFanSnmp").newInstance();
                            			returnHash = fansnmp.collect_Data(alarmIndicatorsNode);
                            			Date enddate1 = new Date();
                                 		SysLogger.info("##############################");
                         				SysLogger.info("### "+node.getIpAddress()+" 网络设备FAN采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                         				SysLogger.info("##############################");
                            			//FDB表不存入数据库
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("fan", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("fan", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("fan", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"fan");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("power")){
                    		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    		if(node != null){
                    			if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("cisco")){
                    				//CISCO电源
                    				CiscoPowerSnmp powersnmp = null;
                        			try{
                        				//SysLogger.info("开始采集CISCO电源信息################");
                        				powersnmp = (CiscoPowerSnmp)Class.forName("com.afunms.polling.snmp.power.CiscoPowerSnmp").newInstance();
                            			returnHash = powersnmp.collect_Data(alarmIndicatorsNode);
                            			//存入数据库
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("power", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("power", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("power", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"power");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("h3c")){
                    				//H3C电源
                    				H3CPowerSnmp powersnmp = null;
                        			try{
                        				//SysLogger.info("开始采集H3C电源信息################");
                        				Date startdate1 = new Date();
                        				powersnmp = (H3CPowerSnmp)Class.forName("com.afunms.polling.snmp.power.H3CPowerSnmp").newInstance();
                            			returnHash = powersnmp.collect_Data(alarmIndicatorsNode);
                            			Date enddate1 = new Date();
                                 		SysLogger.info("##############################");
                         				SysLogger.info("### "+node.getIpAddress()+" 网络设备POWER采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                         				SysLogger.info("##############################");
                            			//存入数据库
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("power", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("power", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("power", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"power");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("voltage")){
                    		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
                    		if(node != null){
                    			if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("cisco")){
                    				//CISCO电压
                    				CiscoVoltageSnmp voltagesnmp = null;
                        			try{
                        				//SysLogger.info("开始采集CISCO电压信息################");
                        				voltagesnmp = (CiscoVoltageSnmp)Class.forName("com.afunms.polling.snmp.voltage.CiscoVoltageSnmp").newInstance();
                            			returnHash = voltagesnmp.collect_Data(alarmIndicatorsNode);
                            			//存入数据库
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("voltage", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("voltage", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("voltage", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"voltage");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}else if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("h3c")){
                    				//H3C电压
                    				H3CVoltageSnmp voltagesnmp = null;
                        			try{
                        				//SysLogger.info("开始采集H3C电压信息################");
                        				Date startdate1 = new Date();
                        				voltagesnmp = (H3CVoltageSnmp)Class.forName("com.afunms.polling.snmp.voltage.H3CVoltageSnmp").newInstance();
                            			returnHash = voltagesnmp.collect_Data(alarmIndicatorsNode);
                            			Date enddate1 = new Date();
                                 		SysLogger.info("##############################");
                         				SysLogger.info("### "+node.getIpAddress()+" 网络设备电压采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
                         				SysLogger.info("##############################");
                            			//存入数据库
                            			if(alldata.containsKey(node.getIpAddress())){
                            				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
                            				if(ipdata != null){
                            					ipdata.put("voltage", returnHash);
                            				}else{
                            					ipdata = new Hashtable();
                            					ipdata.put("voltage", returnHash);
                            				}
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}else{
                            				Hashtable ipdata = new Hashtable();
                            				ipdata.put("voltage", returnHash);
                            				alldata.put(node.getIpAddress(), ipdata);
                            				ipdata = null;
                            			}
                            			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"voltage");
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}
                    	}
            		}
            	} 
            	

        		
            }catch(Exception exc){
            	
            }
        }
    };
	}
}
