/*
 * Created on 2011-08-2
 *
 */
package com.afunms.polling.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.ProcessNetData;
import com.afunms.polling.impl.ProcessVPNData;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.DiscardsPerc;
import com.afunms.polling.om.ErrorsPerc;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.Packs;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Task;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.polling.snmp.cpu.ArrayNetworkCpuSnmp;
import com.afunms.polling.snmp.interfaces.InterfaceSnmp;
import com.afunms.polling.snmp.ping.PingSnmp;
import com.afunms.polling.snmp.system.SystemSnmp;
import com.afunms.topology.dao.ConnectTypeConfigDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.ConnectTypeConfig;
import com.afunms.topology.model.HostNode;
import com.afunms.util.DataGate;

public class M5VPNTask extends MonitorTask {
	
	
	public void run() {
		try{
			//每次重新调度task时，刷新其日期属性
			super.recentlyStartTime = new Date();
			//SysLogger.info("---- 启动VPNtask-"+this.getClass().getName()+" 时间： "+super.sdf.format(recentlyStartTime));
			//SysLogger.info("wwwwwwwwwwwwwwwwwwwwww   M5VPNTask   wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww: "+Thread.activeCount());

			ConnectTypeConfigDao connectTypeConfigDao = new ConnectTypeConfigDao();
	        Hashtable connectConfigHashtable = new Hashtable();
			List configList = new ArrayList();
			try{
				configList = connectTypeConfigDao.loadAll();
			}catch(Exception e){
				
			}finally{
				connectTypeConfigDao.close();
				connectTypeConfigDao = null;
			}
			if(configList != null && configList.size()>0){
				for(int i=0;i<configList.size();i++){
					ConnectTypeConfig connectTypeConfig = (ConnectTypeConfig)configList.get(i);
					connectConfigHashtable.put(connectTypeConfig.getNode_id(), connectTypeConfig);
				}
			}
			
			ShareData.getConnectConfigHashtable().put("connectConfigHashtable", connectConfigHashtable);

			//装载告警历史
			Hashtable checkEventHashtable = new Hashtable();
			CheckEventDao checkeventdao = new CheckEventDao();
			List eventlist = new ArrayList();
			try{
				eventlist = checkeventdao.loadAll();
			}catch(Exception e){
				
			}finally{
				checkeventdao.close();
			}
			if(eventlist != null && eventlist.size()>0){
				CheckEvent checkEvent = null;
				for(int i=0;i<eventlist.size();i++){
					checkEvent = (CheckEvent)eventlist.get(i);
					String name = checkEvent.getNodeid()+":"+checkEvent.getType()+":"+checkEvent.getSubtype()+":"+checkEvent.getIndicatorsName();
					if (checkEvent.getSindex() != null && checkEvent.getSindex().trim().length() > 0) {
						name = name + ":" + checkEvent.getSindex();
					}
					checkEventHashtable.put(name, checkEvent.getAlarmlevel());
				}
			}
			ShareData.setCheckEventHash(checkEventHashtable);
			
			List portconfiglist = new ArrayList();
			PortconfigDao configdao = new PortconfigDao(); 			
			Portconfig portconfig = null;
			Hashtable portconfigHash = new Hashtable();
			try {
				portconfiglist = configdao.getAllBySms();
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally { 
				configdao.close();
			}
			if(portconfiglist != null && portconfiglist.size()>0){
				for(int i=0;i<portconfiglist.size();i++){
					portconfig = (Portconfig)portconfiglist.get(i);
					if(portconfigHash.containsKey(portconfig.getIpaddress())){
						List portlist = (List)portconfigHash.get(portconfig.getIpaddress());
						portlist.add(portconfig);
						portconfigHash.put(portconfig.getIpaddress(), portlist);
					}else{
						List portlist = new ArrayList();
						portlist.add(portconfig);
						portconfigHash.put(portconfig.getIpaddress(), portlist);
					}
				}
			} 
			ShareData.setPortConfigHash(portconfigHash);
			
			NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
	    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
	    	try{
	    		//获取被启用的所有被监视指标
	    		monitorItemList = indicatorsdao.getByIntervalAndType("5", "m",1,"vpn");
//	    		SysLogger.info("^^^^  %%%%%%%   ^^^^"+monitorItemList.size()+"");
	    		
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
        				 ////////////////////////////////VPNTask///////////////////////////////
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
        			for(int i= 0;i<nodelist.size();i++){
        			
        			}
        		}catch(Exception e){
        			e.printStackTrace();
        		}finally{
        			nodedao.close();
        		}
        		Hashtable nodehash = new Hashtable();
//        		System.out.println("%%%%%        %%%%+nodehash.size():"+nodelist.size()+"&&&&&&&");
				
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
        			
//        			SysLogger.info("%%%$$$$$$$$nodeGatherIndicators information"+nodeGatherIndicators.getName()+nodeGatherIndicators.getSubtype());
        			
        			if(docollcetHash.containsKey(nodeGatherIndicators.getNodeid())){
//        				SysLogger.info("%%%enter if language ! $$: nodeGatherIndicators is "+nodeGatherIndicators
//        						+""+nodeGatherIndicators.getName());
        				
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
//        				SysLogger.info("%%%%%%%%%%$$$$$$$$$$: nodeGatherIndicators is "+nodeGatherIndicators
//        						+""+nodeGatherIndicators.getName());
        				docollcetHash.put(nodeGatherIndicators.getNodeid(), tempList);
        			}
        		}
//        		SysLogger.info("&%%&%&%&%&&&&&&&docollcetHash is "+docollcetHash
//        				+"size is "+docollcetHash.size());

        		// 生成线程池,数目依据被监视的设备多少
        		ThreadPool threadPool = null;													
        		// 运行任务
        		if(docollcetHash != null && docollcetHash.size()>0){
        			threadPool = new ThreadPool(docollcetHash.size());	
        			Enumeration newProEnu = docollcetHash.keys();
        			while(newProEnu.hasMoreElements())
        			{
        				String nodeid = (String)newProEnu.nextElement();///////////////////提取加载了的nodeId,
        				List dolist = (List)docollcetHash.get(nodeid);////根据nodeid获得要提取的指标
        				threadPool.runTask(createTask(nodeid,dolist,alldata));
        	
        			}
        			// 关闭线程池并等待所有任务完成
            		threadPool.join();             		
            		threadPool.close();
        		}  
        		threadPool = null;
        		

        		
		}catch(Exception e){
			SysLogger.info(e.getMessage());
			e.printStackTrace();
			//若出现例外，则需要重新调用启动定时任务的方法
		}finally{
			Runtime.getRuntime().gc();
//			SysLogger.info("********M5VPNTask Thread Count : "+Thread.activeCount());
		}
	}
	
	/**
    创建任务
	 */	
	private static Runnable createTask(final String nodeid,final List list,final Hashtable alldata) {
    return new Runnable() {
        public void run() {
        	try {             
        		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	Hashtable hashv = new Hashtable();
            	Hashtable returnHash = new Hashtable();
            	AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
            	NodeGatherIndicators alarmIndicatorsNode = null;
            	Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid));
            	Connection conn = null;
				PreparedStatement pstmt = null;
				PreparedStatement deletePstmt = null;
				Hashtable allprocesshash = new Hashtable();
				Enumeration processhash = allprocesshash.keys();
//				SysLogger.info("&&&&&&&%%%%%%%%%$$$$list is :"+list);
            	if(list != null && list.size()>0){
            		for(int k=0;k<list.size();k++){
//            			SysLogger.info("^^^^%%$$$$$$%%^^^^"+""+"");
            			alarmIndicatorsNode = (NodeGatherIndicators)list.get(k); 
            			ProcessVPNData processVPNData = new ProcessVPNData();
            			if(alarmIndicatorsNode.getName().equalsIgnoreCase("cpu")){
                    		//CPU的采集
//            				SysLogger.info("^^^^%%$$$$$$%%^^^^"+alarmIndicatorsNode.getSubtype()+"");
                    		if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("arraynetworks")){
                    			if(node != null){
                    				ArrayNetworkCpuSnmp arrayNetworkCpuSnmp = null;
                        			try{
                        				arrayNetworkCpuSnmp = new ArrayNetworkCpuSnmp();
                    					Hashtable returnHashVPNCpu = arrayNetworkCpuSnmp.collect_Data(alarmIndicatorsNode);
                    					processVPNData.saveVPNCpuData(returnHashVPNCpu);
                    					
                    					//将数据插入对应IP的历史表中
                    					String ip = node.getIpAddress();
                    					String allipstr = SysUtil.doip(ip); 
                    					Vector vector =(Vector)returnHashVPNCpu.get("cpu");
//                    					SysLogger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%the size of vector is :"+vector.size());
                    					for (int i = 0; i < vector.size(); i++) {
                    						CPUcollectdata cpudata = new CPUcollectdata();
                    						cpudata =(CPUcollectdata)vector.get(i);
	                    					Calendar tempCal = (Calendar) cpudata.getCollecttime();
		    								Date cc = tempCal.getTime();
		    								String time = sdf.format(cc);
	                    					String tablename = "cpu" + allipstr;
	                    					String sql = "insert into "+tablename
				                    					+"(ipAddress,entity,subentity,thevalue,chname,restype,collecttime,unit,bak,count,category)" +
												  		"values(?,?,?,?,?,?,?,?,?,?,?);";
	                    					
//	                    					SysLogger.info("%%%%%" +
//	                    							"execute this sql sentence :"+sql+"%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
	                    					conn = DataGate.getCon();
	            							conn.setAutoCommit(false);
	            							pstmt = conn.prepareStatement(sql);
	            							pstmt.setString(1, ip);
	            							pstmt.setString(2, cpudata.getEntity());
	            							pstmt.setString(3, cpudata.getSubentity());
	            							pstmt.setString(4, cpudata.getThevalue());
	            							pstmt.setString(5, cpudata.getChname());
	            							pstmt.setString(6, cpudata.getRestype());
	            							pstmt.setString(7, time);
	            							pstmt.setString(8, cpudata.getUnit());
	            							pstmt.setString(9, cpudata.getBak());
	            							pstmt.setLong(10, 0);//cpudata.getCount()
	            							pstmt.setString(11, "cpu");//cpudata.getCategory()
	            							try{
		            							pstmt.execute();
		            							conn.commit();
	            							}catch(Exception e){
	            								e.printStackTrace();
	            							}
                    					}
                            		}catch(Exception e){
                            			e.printStackTrace();
                            		}
                    			}
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("systemgroup")){
                    		//Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
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
                        				
                        			}else{
                        				Hashtable ipdata = new Hashtable();
                        				ipdata.put("systemgroup", returnHash);
                        				alldata.put(node.getIpAddress(), ipdata);
                        				
                        			}
                        			//IP-MAC不存入数据库
                        			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"ipmac");
                        		}catch(Exception e){
                        			e.printStackTrace();
                        		}
                    		}
                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("interface")){
                    		StringBuffer sBuffer = null;
                    		DBManager dbmanager = new DBManager();
                    		if(node != null){
                    			InterfaceSnmp interfacesnmp = null;
                    			try{
                					interfacesnmp = (InterfaceSnmp)Class.forName("com.afunms.polling.snmp.interfaces.InterfaceSnmp").newInstance();
                					returnHash = interfacesnmp.collect_Data(alarmIndicatorsNode);
                        			
                        			if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("arraynetworks")){
                        				//对出入总流量值进行告警检测
                            		    try{
                            				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "h3c");
                            				for(int i = 0 ; i < list.size() ; i ++){
                            					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
                            					if(alarmIndicatorsnode.getName().equalsIgnoreCase("AllInBandwidthUtilHdx") || alarmIndicatorsnode.getName().equalsIgnoreCase("AllOutBandwidthUtilHdx")
                            							|| alarmIndicatorsnode.getName().equalsIgnoreCase("utilhdx")){
                            						//对总出入口流量值进行告警检测
                                					CheckEventUtil checkutil = new CheckEventUtil();
                                					checkutil.updateData(node,returnHash,"vpn","arraynetworks",alarmIndicatorsnode);
                            					}
                            				}
                            		    }catch(Exception e){
                            		    	e.printStackTrace();
                            		    }
                        			}
                        			//存入数据库
                        			Hashtable interfacehash = returnHash ;
                        			String ip = node.getIpAddress();
                					String allipstr = SysUtil.doip(ip); 
    	    						//将当前采集到的接口数据入库
                					ProcessNetData processnetdata = new ProcessNetData();
    	    						try{
    	    							processnetdata.processInterfaceData(node.getId()+"", ip, "vpn", "arraynetworks", interfacehash);
    	    						}catch(Exception e){
    	    							e.printStackTrace();
    	    						}
    	    						processnetdata = null;
    	    						
    	    						//端口状态信息入库
    	    						Vector interfaceVector = (Vector) interfacehash.get("interface");
    	    						Calendar tempCal = null;
    								Date cc = null;
    								String time = null;
    	    						if (interfaceVector != null && interfaceVector.size() > 0) {	    							
    	    							String tablename = "portstatus"+allipstr;
    	    							try{
    	    								Interfacecollectdata interfacedata = null;
//    	    								SysLogger.info("the interface is :"+interfaceVector+" %%%%%%%%%%%%%%%%%%%\n the size is"+interfaceVector.size());
    	    								for (int i = 0; i < interfaceVector.size(); i++) {
    	    									interfacedata = (Interfacecollectdata) interfaceVector.elementAt(i);
//    	    									if (((List)ShareData.getPortConfigHash().get(ip)).contains(interfacedata.getSubentity())&&interfacedata.getEntity().equals("ifOperStatus")) {
    	    										tempCal = (Calendar) interfacedata.getCollecttime();
    	    										cc = tempCal.getTime();
    	    										time = sdf.format(cc);
    	    										tempCal = null;
    	    										cc = null;
    		    										long count = 0;
    			    									if(interfacedata.getCount() != null){
    			    										count = interfacedata.getCount();
    			    									}
    												    sBuffer = new StringBuffer();
    												    sBuffer.append("insert into ");
    												    sBuffer.append(tablename);
    												    sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
    												    sBuffer.append("values('");
    												    sBuffer.append(ip);
    												    sBuffer.append("','");
    												    sBuffer.append(interfacedata.getRestype());
    												    sBuffer.append("','");
    												    sBuffer.append(interfacedata.getCategory());
    												    sBuffer.append("','");
    												    sBuffer.append(interfacedata.getEntity());
    												    sBuffer.append("','");
    												    sBuffer.append(interfacedata.getSubentity());
    												    sBuffer.append("','");
    												    sBuffer.append(interfacedata.getUnit());
    												    sBuffer.append("','");
    												    sBuffer.append(interfacedata.getChname());
    												    sBuffer.append("','");
    												    sBuffer.append(interfacedata.getBak());
    												    sBuffer.append("','");
    												    sBuffer.append(count);
    												    sBuffer.append("','");
    												    sBuffer.append(interfacedata.getThevalue());
    												    sBuffer.append("','");
    												    sBuffer.append(time);
    												    sBuffer.append("')");
    												    try {
    				    									dbmanager.addBatch(sBuffer.toString());
    				    									dbmanager.executeBatch();
        		    										dbmanager.commit();
//        		    										SysLogger.info(sBuffer.toString());
    				    								} catch (Exception ex) {
    				    									ex.printStackTrace();
    				    								} finally {
    				    									time = null;
    				    									sBuffer = null;
    				    								}				    								
    											}
    	    									interfacedata = null;	
//    	    								}
    	    							}catch(Exception e){	
    	    								e.printStackTrace();
    	    							}
    	    							}
    	    						interfaceVector = null;
    	    						
    	    						//
    	    						Vector allutilhdxVector = (Vector) interfacehash.get("allutilhdx");
    	    						if (allutilhdxVector != null && allutilhdxVector.size() > 0) {
    	    							AllUtilHdx allutilhdx = null;
    	    							String tablename = "allutilhdx" + allipstr;
    	    							for (int si = 0; si < allutilhdxVector.size(); si++) {
    	    								try{
    		    								allutilhdx = (AllUtilHdx) allutilhdxVector.elementAt(si);
    		    								if (allutilhdx.getRestype().equals("dynamic")) {
    		    									if (allutilhdx.getThevalue().equals("0"))
    		    										continue;
    		    									tempCal = (Calendar) allutilhdx.getCollecttime();
    		    									cc = tempCal.getTime();
    		    									time = sdf.format(cc);
    		    									
    		    									long count = 0;
    		    									if(allutilhdx.getCount() != null){
    		    										count = allutilhdx.getCount();
    		    									}
    		    									sBuffer = new StringBuffer();
    		    									sBuffer.append("insert into ");
    		    									sBuffer.append(tablename);
    		    									sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
    		    									sBuffer.append("values('");
    		    									sBuffer.append(ip);
    		    									sBuffer.append("','");
    		    									sBuffer.append(allutilhdx.getRestype());
    		    									sBuffer.append("','");
    		    									sBuffer.append(allutilhdx.getCategory());
    		    									sBuffer.append("','");
    		    									sBuffer.append(allutilhdx.getEntity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(allutilhdx.getSubentity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(allutilhdx.getUnit());
    		    									sBuffer.append("','");
    		    									sBuffer.append(allutilhdx.getChname());
    		    									sBuffer.append("','");
    		    									sBuffer.append(allutilhdx.getBak());
    		    									sBuffer.append("','");
    		    									sBuffer.append(count);
    		    									sBuffer.append("','");
    		    									sBuffer.append(allutilhdx.getThevalue());
    		    									sBuffer.append("','");
    		    									sBuffer.append(time);
    		    									sBuffer.append("')");
    		    									try {
    		    										dbmanager.addBatch(sBuffer.toString());
    		    										dbmanager.executeBatch();
    		    										dbmanager.commit();
//    		    										SysLogger.info(sBuffer.toString());
    		    									} catch (Exception ex) {
    		    										ex.printStackTrace();
    		    									}
    		    									sBuffer = null;
    		    									time = null;
    		    								}
    		    								allutilhdx = null;
    	    								}catch(Exception e){	
    		    								e.printStackTrace();
    		    							}
    	    							}
    	    							tablename = null;
    	    						
    	    						}
    	    						allutilhdxVector = null;

    	    						//UtilHdxPerc
    	    						Vector utilhdxpercVector = (Vector) interfacehash.get("utilhdxperc");
    	    						if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
    	    							String tablename = "utilhdxperc" + allipstr;
    	    							UtilHdxPerc utilhdxperc = null;
    	    							for (int si = 0; si < utilhdxpercVector.size(); si++) {
    	    								try{
    	    								utilhdxperc = (UtilHdxPerc) utilhdxpercVector.elementAt(si);
    	    								if (utilhdxperc.getRestype().equals("dynamic")) {
    	    									if (utilhdxperc.getThevalue().equals("0")|| utilhdxperc.getThevalue().equals("0.0"))
    	    										continue;
    	    									tempCal = (Calendar) utilhdxperc.getCollecttime();
    	    									cc = tempCal.getTime();
    	    									time = sdf.format(cc);
    	    									long count = 0;
    	    									if(utilhdxperc.getCount() != null){
    	    										count = utilhdxperc.getCount();
    	    									}
    	    									sBuffer = new StringBuffer();
    	    									sBuffer.append("insert into ");
    	    									sBuffer.append(tablename);
    	    									sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
    	    									sBuffer.append("values('");
    	    									sBuffer.append(ip);
    	    									sBuffer.append("','");
    	    									sBuffer.append(utilhdxperc.getRestype());
    	    									sBuffer.append("','");
    	    									sBuffer.append(utilhdxperc.getCategory());
    	    									sBuffer.append("','");
    	    									sBuffer.append(utilhdxperc.getEntity());
    	    									sBuffer.append("','");
    	    									sBuffer.append(utilhdxperc.getSubentity());
    	    									sBuffer.append("','");
    	    									sBuffer.append(utilhdxperc.getUnit());
    	    									sBuffer.append("','");
    	    									sBuffer.append(utilhdxperc.getChname());
    	    									sBuffer.append("','");
    	    									sBuffer.append(utilhdxperc.getBak());
    	    									sBuffer.append("','");
    	    									sBuffer.append(count);
    	    									sBuffer.append("','");
    	    									sBuffer.append(utilhdxperc.getThevalue());
    	    									sBuffer.append("','");
    	    									sBuffer.append(time);
    	    									sBuffer.append("')");
    	    									try {
    	    										dbmanager.addBatch(sBuffer.toString());
    	    										dbmanager.executeBatch();
		    										dbmanager.commit();
//		    										SysLogger.info(sBuffer.toString());
    	    									} catch (Exception ex) {
    	    										ex.printStackTrace();
    	    									}
    	    									sBuffer = null;
    	    									time = null;					
    	    								}
    	    								utilhdxperc = null;
    	    								}catch(Exception e){	
    		    								e.printStackTrace();
    		    							}	    								
    	    							}
    	    							tablename = null;
    	    						}
    	    						utilhdxpercVector = null;

    	    						//UtilHdx
    	    						Vector utilhdxVector = (Vector) interfacehash.get("utilhdx");
    	    						if (utilhdxVector != null && utilhdxVector.size() > 0) {
    	    							String tablename = "utilhdx" + allipstr;
    	    							UtilHdx utilhdx = null;
    	    							for (int si = 0; si < utilhdxVector.size(); si++) {
    	    								try{
    	    									utilhdx = (UtilHdx) utilhdxVector.elementAt(si);
    		    								if (utilhdx.getRestype().equals("dynamic")) {
    		    									if (utilhdx.getThevalue().equals("0"))
    		    										continue;
    		    									tempCal = (Calendar) utilhdx.getCollecttime();
    		    									cc = tempCal.getTime();
    		    									time = sdf.format(cc);
    		    									long count = 0;
    		    									if(utilhdx.getCount() != null){
    		    										count = utilhdx.getCount();
    		    									}
    		    									sBuffer = new StringBuffer();
    		    									sBuffer.append("insert into ");
    		    									sBuffer.append(tablename);
    		    									sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
    		    									sBuffer.append("values('");
    		    									sBuffer.append(ip);
    		    									sBuffer.append("','");
    		    									sBuffer.append(utilhdx.getRestype()); 
    		    									sBuffer.append("','");
    		    									sBuffer.append(utilhdx.getCategory());
    		    									sBuffer.append("','");
    		    									sBuffer.append(utilhdx.getEntity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(utilhdx.getSubentity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(utilhdx.getUnit());
    		    									sBuffer.append("','");
    		    									sBuffer.append(utilhdx.getChname());
    		    									sBuffer.append("','");
    		    									sBuffer.append(utilhdx.getBak());
    		    									sBuffer.append("','");
    		    									sBuffer.append(count);
    		    									sBuffer.append("','");
    		    									sBuffer.append(utilhdx.getThevalue());
    		    									sBuffer.append("','");
    		    									sBuffer.append(time);
    		    									sBuffer.append("')");
    		    									try {
    		    										dbmanager.addBatch(sBuffer.toString());
    		    										dbmanager.executeBatch();
    		    										dbmanager.commit();
//    		    										SysLogger.info(sBuffer.toString());
    		    									} catch (Exception ex) {
    		    										ex.printStackTrace();
    		    									}				
    		    								}
    	    								}catch(Exception e){	
    		    								e.printStackTrace();
    		    							}
    	    								sBuffer = null;
        									time = null;
        									utilhdx = null;    								
    	    							}
    	    							tablename = null;
    	    						}
    	    						utilhdxVector = null;
    	    						
    	    						//discardsperc
    	    						Vector discardspercVector = (Vector) interfacehash.get("discardsperc");
    	    						if (discardspercVector != null && discardspercVector.size() > 0) {
    	    							DiscardsPerc discardsperc = null;
    	    							String tablename = "discardsperc" + allipstr;
    	    							for (int si = 0; si < discardspercVector.size(); si++) {
    	    								try{
    	    									discardsperc = (DiscardsPerc) discardspercVector.elementAt(si);
    		    								if (discardsperc.getRestype().equals("dynamic")) { 
//    		    									if (discardsperc.getThevalue()=="0.0"))
//    		    										continue;
    		    									tempCal = (Calendar) discardsperc.getCollecttime();
    		    									cc = tempCal.getTime();
    		    									time = sdf.format(cc);
    		    									long count = 0;
    			    								if(discardsperc.getCount() != null){
    			    									count = discardsperc.getCount();
    			    								}
    		    									sBuffer = new StringBuffer();
    		    									sBuffer.append("insert into ");
    		    									sBuffer.append(tablename);
    		    									sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
    		    									sBuffer.append("values('");
    		    									sBuffer.append(ip);
    		    									sBuffer.append("','");
    		    									sBuffer.append(discardsperc.getRestype());
    		    									sBuffer.append("','");
    		    									sBuffer.append(discardsperc.getCategory());
    		    									sBuffer.append("','");
    		    									sBuffer.append(discardsperc.getEntity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(discardsperc.getSubentity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(discardsperc.getUnit());
    		    									sBuffer.append("','");
    		    									sBuffer.append(discardsperc.getChname());
    		    									sBuffer.append("','");
    		    									sBuffer.append(discardsperc.getBak());
    		    									sBuffer.append("','");
    		    									sBuffer.append(count);
    		    									sBuffer.append("','");
    		    									sBuffer.append(discardsperc.getThevalue());
    		    									sBuffer.append("','");
    		    									sBuffer.append(time);
    		    									sBuffer.append("')");
    		    									try {
    		    										dbmanager.addBatch(sBuffer.toString());
    		    										dbmanager.executeBatch();
    		    										dbmanager.commit();
    		    										
    		    										//dbmanager.executeUpdate(sBuffer.toString());
    		    									} catch (Exception ex) {
    		    										ex.printStackTrace();
    		    									}				
    		    								}
    	    								} catch (Exception ex) {
        										ex.printStackTrace();
        									}
    	    								
    		    							sBuffer = null;
    		    							time = null;
    		    							discardsperc = null;
    	    							}
    	    							tablename = null;
    	    						}
    	    						discardspercVector = null;

    	    						//errorsperc
    	    						Vector errorspercVector = (Vector) interfacehash.get("errorsperc");
    	    						if (errorspercVector != null && errorspercVector.size() > 0) {
    	    							ErrorsPerc errorsperc = null;
    	    							String tablename = "errorsperc" + allipstr;
    	    							for (int si = 0; si < errorspercVector.size(); si++) {
    	    								try{
    	    									errorsperc = (ErrorsPerc) errorspercVector.elementAt(si);
    		    								if (errorsperc.getRestype().equals("dynamic")) {
//    		    									if (errorsperc.getThevalue().equals("0.0"))
//    		    										continue;
    		    									tempCal = (Calendar) errorsperc.getCollecttime();
    		    									cc = tempCal.getTime();
    		    									time = sdf.format(cc);
    		    									long count = 0;
    			    								if(errorsperc.getCount() != null){
    			    									count = errorsperc.getCount();
    			    								}
    		    									sBuffer = new StringBuffer();
    		    									sBuffer.append("insert into ");
    		    									sBuffer.append(tablename);
    		    									sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
    		    									sBuffer.append("values('");
    		    									sBuffer.append(ip);
    		    									sBuffer.append("','");
    		    									sBuffer.append(errorsperc.getRestype());
    		    									sBuffer.append("','");
    		    									sBuffer.append(errorsperc.getCategory());
    		    									sBuffer.append("','");
    		    									sBuffer.append(errorsperc.getEntity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(errorsperc.getSubentity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(errorsperc.getUnit());
    		    									sBuffer.append("','");
    		    									sBuffer.append(errorsperc.getChname());
    		    									sBuffer.append("','");
    		    									sBuffer.append(errorsperc.getBak());
    		    									sBuffer.append("','");
    		    									sBuffer.append(count);
    		    									sBuffer.append("','");
    		    									sBuffer.append(errorsperc.getThevalue());
    		    									sBuffer.append("','");
    		    									sBuffer.append(time);
    		    									sBuffer.append("')");
    		    									try {
    		    										dbmanager.addBatch(sBuffer.toString());
    		    										dbmanager.executeBatch();
    		    										dbmanager.commit();
//    		    										SysLogger.info(sBuffer.toString());
    		    									} catch (Exception ex) {
    		    										ex.printStackTrace();
    		    									}				
    		    									
    		    								}
    	    								} catch (Exception ex) {
        										ex.printStackTrace();
        									}
    		    							sBuffer = null;
    		    							time = null;
    		    							errorsperc = null;
    	    							}
    	    							tablename = null;
    	    						}
    	    						errorspercVector = null;
    	    						
    	    						//packs
    	    						Vector packsVector = (Vector) interfacehash.get("packs");
    	    						if (packsVector != null && packsVector.size() > 0) {
    	    							String tablename = "packs" + allipstr;
    	    							Packs packs = null;
    	    							for (int si = 0; si < packsVector.size(); si++) {
    	    								try{
    	    									packs = (Packs) packsVector.elementAt(si);
    		    								if (packs.getRestype().equals("dynamic")) {
//    		    									if (packs.getThevalue().equals("0"))
//    		    										continue;
    		    									tempCal = (Calendar) packs.getCollecttime();
    		    									cc = tempCal.getTime();
    		    									time = sdf.format(cc);
    		    									long count = 0;
    			    								if(packs.getCount() != null){
    			    									count = packs.getCount();
    			    								}
    		    									sBuffer = new StringBuffer();
    		    									sBuffer.append("insert into ");
    		    									sBuffer.append(tablename);
    		    									sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
    		    									sBuffer.append("values('");
    		    									sBuffer.append(ip);
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getRestype());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getCategory());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getEntity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getSubentity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getUnit());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getChname());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getBak());
    		    									sBuffer.append("','");
    		    									sBuffer.append(count);
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getThevalue());
    		    									sBuffer.append("','");
    		    									sBuffer.append(time);
    		    									sBuffer.append("')");
    		    									try {
    		    										dbmanager.addBatch(sBuffer.toString());
    		    										dbmanager.executeBatch();
    		    										dbmanager.commit();
//    		    										SysLogger.info(sBuffer.toString());
    		    									} catch (Exception ex) {
    		    										ex.printStackTrace();
    		    									}
    		    									//conn.executeUpdate(sql);															
    		    								}
    	    								} catch (Exception ex) {
        										ex.printStackTrace();
        									}
    		    							sBuffer = null;
    		    							time = null;
    		    							packs = null;
    	    							}
    	    							tablename = null;
    	    						}
    	    						packsVector = null;
    	    						
    	    						//inpacks
    	    						Vector inpacksVector = (Vector) interfacehash.get("inpacks");
//    	    						SysLogger.info("the inpacksVector is :"+inpacksVector+
//											" \n %%%%%%%%%%%%%%%%%the size is  "+inpacksVector.size());
    	    						if (inpacksVector != null && inpacksVector.size() > 0) {
    	    							String tablename = "inpacks" + allipstr;
    	    							InPkts packs = null;
    	    							for (int si = 0; si < inpacksVector.size(); si++) {
    	    								try{    
    	    									packs = (InPkts) inpacksVector.elementAt(si);
    		    								if (packs.getRestype().equals("dynamic")) {
    		    									tempCal = (Calendar) packs.getCollecttime();
    		    									cc = tempCal.getTime();
    		    									time = sdf.format(cc);
    		    									
    		    									long count = 0;
    			    								if(packs.getCount() != null){
    			    									count = packs.getCount();
    			    								}
    		    									sBuffer = new StringBuffer();
    		    									sBuffer.append("insert into ");
    		    									sBuffer.append(tablename);
    		    									sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
    		    									sBuffer.append("values('");
    		    									sBuffer.append(ip);
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getRestype());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getCategory());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getEntity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getSubentity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getUnit());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getChname());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getBak());
    		    									sBuffer.append("','");
    		    									sBuffer.append(count);
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getThevalue());
    		    									sBuffer.append("','");
    		    									sBuffer.append(time);
    		    									sBuffer.append("')");
    		    									try {
    		    										dbmanager.addBatch(sBuffer.toString());
    		    										dbmanager.executeBatch();
    		    										dbmanager.commit();
    		    										//dbmanager.executeUpdate(sBuffer.toString());
    		    									} catch (Exception ex) {
    		    										ex.printStackTrace();
    		    									}														
    		    								}
    	    								} catch (Exception ex) {
        										ex.printStackTrace();
        									}
    		    							sBuffer = null;
    		    							time = null;
    		    							packs = null;
    	    							}
    	    							tablename = null;
    	    						}
    	    						inpacksVector = null;
    	    						
    	    						//outpacks
    	    						Vector outpacksVector = (Vector) interfacehash.get("outpacks");
//    	    						SysLogger.info("the outpacksVector is :"+outpacksVector+
//											" \n %%%%%%%%%%%%%%%%%the size is  "+outpacksVector.size());
    	    						if (outpacksVector != null && outpacksVector.size() > 0) {
    	    							String tablename = "outpacks" + allipstr;
    	    							OutPkts packs = null;
    	    							for (int si = 0; si < outpacksVector.size(); si++) {
    	    								try{
    	    									packs = (OutPkts) outpacksVector.elementAt(si);
    		    								if (packs.getRestype().equals("dynamic")) {
    		    									tempCal = (Calendar) packs.getCollecttime();
    		    									cc = tempCal.getTime();
    		    									time = sdf.format(cc);
    		    									long count = 0;
    			    								if(packs.getCount() != null){
    			    									count = packs.getCount();
    			    								}
    		    									sBuffer = new StringBuffer();
    		    									sBuffer.append("insert into ");
    		    									sBuffer.append(tablename);
    		    									sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
    		    									sBuffer.append("values('");
    		    									sBuffer.append(ip);
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getRestype());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getCategory());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getEntity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getSubentity());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getUnit());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getChname());
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getBak());
    		    									sBuffer.append("','");
    		    									sBuffer.append(count);
    		    									sBuffer.append("','");
    		    									sBuffer.append(packs.getThevalue());
    		    									sBuffer.append("','");
    		    									sBuffer.append(time);
    		    									sBuffer.append("')");
    		    									try {
    		    										dbmanager.addBatch(sBuffer.toString());
    		    										dbmanager.executeBatch();
    		    										dbmanager.commit();
    		    									} catch (Exception ex) {
    		    										ex.printStackTrace();
    		    									}															
    		    								}
    	    								} catch (Exception ex) {
        										ex.printStackTrace();
        									}
    	    								
    		    							sBuffer = null;
    		    							time = null;
    		    							packs = null;
    	    							}
    	    							tablename = null;
    	    						}
    	    						outpacksVector = null;
    	    						
    	    						
                        		}catch(Exception e){
                        			e.printStackTrace();
                        }
                    }
	            }else if(alarmIndicatorsNode.getName().equalsIgnoreCase("ping")){
            		//ping的采集
            		if(alarmIndicatorsNode.getSubtype().equalsIgnoreCase("arraynetworks")){
            			if(node != null){
            				PingSnmp pingsnmp = null;
            				Hashtable returnHashping =null;
                			try{
                				pingsnmp = (PingSnmp)Class.forName("com.afunms.polling.snmp.ping.PingSnmp").newInstance();
//                				SysLogger.info("%%%%%%%%%%$&&&&$&&the alarmIndicatorsNode is "+alarmIndicatorsNode.getNodeid()+alarmIndicatorsNode.getCategory()+alarmIndicatorsNode.getInterval_unit());
                				returnHashping = pingsnmp.collect_Data(alarmIndicatorsNode);
            					
            					//将数据插入对应IP的历史表中
            					String ip = node.getIpAddress();
            					String allipstr = SysUtil.doip(ip); 
//            					SysLogger.info("%%%%%%%%%%$&&&&$&&the returnHashping is "+returnHashping);
            					Vector vector =(Vector)returnHashping.get("ping");
            					Pingcollectdata pingdata = null;
            					DBManager dbmanager = new DBManager();
            					for (int i = 0; i < vector.size(); i++) {
    								pingdata = (Pingcollectdata) vector.elementAt(i);
	    							if (pingdata.getRestype().equals("dynamic")) {
	    								Calendar tempCal = (Calendar) pingdata.getCollecttime();
	    								Date cc = tempCal.getTime();
	    								String time = sdf.format(cc);
	    								String tablename = "ping" + allipstr;
	    								String sql = "insert into " + tablename
	    								+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
	    								+ "values('" + ip + "','" + pingdata.getRestype() + "','" + pingdata.getCategory() + "','"
	    								+ pingdata.getEntity() + "','" + pingdata.getSubentity() + "','" + pingdata.getUnit() + "','"
	    								+ pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'"
	    								+ pingdata.getThevalue() + "','" + time + "')";
	    								try {
	    									dbmanager.addBatch(sql);
	    									dbmanager.executeBatch();
	    									dbmanager.commit();
	    								} catch (Exception ex) {
	    									ex.printStackTrace();
	    								}														
	    							}
	    							pingdata = null;
            					}
                    		}catch(Exception e){
                    			e.printStackTrace();
                    		}
            			}
            		}else if(true){
                		//add new method at here!
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
