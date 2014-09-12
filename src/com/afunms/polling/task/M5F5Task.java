/*
 * Created on 2011-07-9
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
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Task;
import com.afunms.polling.snmp.f5.F5UserloginSnmp;
import com.afunms.polling.snmp.f5.F5VlansSnmp;
import com.afunms.polling.snmp.interfaces.InterfaceSnmp;
import com.afunms.polling.snmp.interfaces.PackageSnmp;
import com.afunms.polling.snmp.ping.PingSnmp;
import com.afunms.polling.snmp.system.SystemSnmp;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
/**
 * @author yangjun     
 * @date 2011-07-9 
 * 对负载均衡设备进行采集    
 *   
 */
public class M5F5Task extends MonitorTask {   
	    
	public void run() {
		try{
			NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
	    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
	    	try{
	    		monitorItemList = indicatorsdao.getByIntervalAndType("5", "m",1,"f5");
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
    			nodelist = nodedao.loadMonitorF5();
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
        			//过滤掉不监视的设备
        			if(!nodehash.containsKey(nodeGatherIndicators.getNodeid()))continue;
    				List tempList = (List)docollcetHash.get(nodeGatherIndicators.getNodeid());
    				tempList.add(nodeGatherIndicators);
    				docollcetHash.put(nodeGatherIndicators.getNodeid(), tempList);
    			}else{
        			//过滤掉不监视的设备
        			if(!nodehash.containsKey(nodeGatherIndicators.getNodeid()))continue;
    				List tempList = new ArrayList();
    				tempList.add(nodeGatherIndicators);
    				docollcetHash.put(nodeGatherIndicators.getNodeid(), tempList);
    			}
    		}
    		if(docollcetHash != null){
    			numThreads = docollcetHash.size();
    			if(numThreads > 200){
    				numThreads = 200;
    			}
    		}
//    		 生成线程池,数目依据被监视的设备多少
    		ThreadPool threadPool = null;													
    		// 运行任务
    		if(docollcetHash != null && docollcetHash.size()>0){
    			threadPool = new ThreadPool(docollcetHash.size());	
    			Enumeration newProEnu = docollcetHash.keys();
    			while(newProEnu.hasMoreElements())
    			{
    				String nodeid = (String)newProEnu.nextElement();
    				List dolist = (List)docollcetHash.get(nodeid);
    				threadPool.runTask(createTask(nodeid,dolist,alldata));
    	
    			}
    			// 关闭线程池并等待所有任务完成
        		threadPool.join();             		
        		threadPool.close();
        		//threadPool.destroy();
    		}
    		threadPool = null;
    		
    		Date _enddate = new Date();
    		SysLogger.info("##############################");
			SysLogger.info("### 所有F5设备采集时间 "+(_enddate.getTime()-_startdate.getTime())+"####");
			SysLogger.info("##############################");
    		
    		HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
    		Date startdate = new Date();
    		hostdataManager.createHostItemData(alldata, "net");
    		
    		Date enddate = new Date();
    		SysLogger.info("##############################");
			SysLogger.info("### 所有F5设备入库时间 "+(enddate.getTime()-startdate.getTime())+"####");
			SysLogger.info("##############################");
			alldata.clear();
		}catch(Exception e){					 	
			e.printStackTrace();
		}finally{
			SysLogger.info("********M5F5Task Thread Count : "+Thread.activeCount());
		}
	}
	
	/**
    创建任务
	 */	
	private static Runnable createTask(final String nodeid,final List list,final Hashtable alldata) {
	    return new Runnable() {
	        public void run() {
	        	try {                	
	            	Hashtable returnHash = new Hashtable();
	            	AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
	            	NodeGatherIndicators alarmIndicatorsNode = null;
	            	Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid));
	            	
	            	if(list != null && list.size()>0){
	            		for(int k=0;k<list.size();k++){
	            			alarmIndicatorsNode = (NodeGatherIndicators)list.get(k);            			
	            			if(alarmIndicatorsNode.getName().equalsIgnoreCase("systemgroup")){
	                    		if(node != null){
	                    			SystemSnmp systemsnmp = null;
	                    			try{
	                    				Date startdate1 = new Date();
	                    				systemsnmp = (SystemSnmp)Class.forName("com.afunms.polling.snmp.system.SystemSnmp").newInstance();
	                        			returnHash = systemsnmp.collect_Data(alarmIndicatorsNode);
	                        			Date enddate1 = new Date();
	                            		SysLogger.info("##############################");
	                    				SysLogger.info("### "+node.getIpAddress()+" F5设备SystemGroup采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
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
	                        		}catch(Exception e){
	                        			e.printStackTrace();
	                        		}
	                    		}
	                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("ping")){
	                    		if(node != null){
	                    			PingSnmp pingsnmp = null;
	                    			try{
	                    				Date startdate1 = new Date();
	                    				pingsnmp = (PingSnmp)Class.forName("com.afunms.polling.snmp.ping.PingSnmp").newInstance();
	                        			returnHash = pingsnmp.collect_Data(alarmIndicatorsNode);
	                        			Date enddate1 = new Date();
	                             		SysLogger.info("##############################");
	                     				SysLogger.info("### "+node.getIpAddress()+" F5设备Ping采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
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
	                        				
	                        			}else{
	                        				Hashtable ipdata = new Hashtable();
	                        				ipdata.put("ping", returnHash);
	                        				alldata.put(node.getIpAddress(), ipdata);
	                        				
	                        			}
	                        			
	                        			//对PING值进行告警检测
	                        			if(returnHash != null && returnHash.size()>0){
	                        				Vector pingvector = (Vector)returnHash.get("ping");
	                        				if(pingvector != null){
	                        					for (int i = 0; i < pingvector.size(); i++) {
	                                				Pingcollectdata pingdata = (Pingcollectdata) pingvector.elementAt(i);
	                                				if(pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")){
	                            						//连通率进行判断               						
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
	                        		}catch(Exception e){
	                        			e.printStackTrace();
	                        		}
	                    		}
	                    	} else if(alarmIndicatorsNode.getName().equalsIgnoreCase("interface")){
	                    		if(node != null){
	                    			InterfaceSnmp interfacesnmp = null;
	                    			try{
//	                    				SysLogger.info("############################################################");
//	                    				SysLogger.info("##########开始采集"+node.getIpAddress()+" 接口信息...##########");
//	                    				SysLogger.info("############################################################");
	                    				Date startdate1 = new Date();
	                    				interfacesnmp = (InterfaceSnmp)Class.forName("com.afunms.polling.snmp.interfaces.InterfaceSnmp").newInstance();
                    					returnHash = interfacesnmp.collect_Data(alarmIndicatorsNode);
	                        			Date enddate1 = new Date();
	                            		SysLogger.info("##############################");
	                    				SysLogger.info("### "+node.getIpAddress()+" F5设备Interface采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
	                    				SysLogger.info("##############################");
//	                    				对出入总流量值进行告警检测
                            		    try{
                            				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_F5, "f5");
                            				for(int i = 0 ; i < list.size() ; i ++){
                            					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
                            					if(alarmIndicatorsnode.getName().equalsIgnoreCase("AllInBandwidthUtilHdx") || alarmIndicatorsnode.getName().equalsIgnoreCase("AllOutBandwidthUtilHdx")
                            							|| alarmIndicatorsnode.getName().equalsIgnoreCase("utilhdx")){
                            						//对总出入口流量值进行告警检测
//                                					CheckEventUtil checkutil = new CheckEventUtil();
//                                					checkutil.updateData(node,returnHash,"f5","f5",alarmIndicatorsnode);
                            					}
                            				}
                            		    }catch(Exception e){
                            		    	e.printStackTrace();
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
	                        				
	                        			}else{
	                        				Hashtable ipdata = new Hashtable();
	                        				ipdata.put("interface", returnHash);
	                        				alldata.put(node.getIpAddress(), ipdata);
	                        				
	                        			}
	                        		}catch(Exception e){
	                        			e.printStackTrace();
	                        		}
	                    		}
	                    	}else if(alarmIndicatorsNode.getName().equalsIgnoreCase("packs")){
	                    		if(node != null){
	                    			PackageSnmp packagesnmp = null;
	                    			try{
	                    				Date startdate1 = new Date();
	                    				packagesnmp = (PackageSnmp)Class.forName("com.afunms.polling.snmp.interfaces.PackageSnmp").newInstance();
	                        			returnHash = packagesnmp.collect_Data(alarmIndicatorsNode);
	                        			Date enddate1 = new Date();
	                             		SysLogger.info("##############################");
	                     				SysLogger.info("### "+node.getIpAddress()+" F5设备Pack采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
	                     				SysLogger.info("##############################");
	                        			if(alldata.containsKey(node.getIpAddress())){
	                        				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
	                        				if(ipdata != null){
	                        					ipdata.put("packs", returnHash);
	                        				}else{
	                        					ipdata = new Hashtable();
	                        					ipdata.put("packs", returnHash);
	                        				}
	                        				alldata.put(node.getIpAddress(), ipdata);
	                        				
	                        			}else{
	                        				Hashtable ipdata = new Hashtable();
	                        				ipdata.put("packs", returnHash);
	                        				alldata.put(node.getIpAddress(), ipdata);
	                        				
	                        			}
	                        		}catch(Exception e){
	                        			e.printStackTrace();
	                        		}
	                    		}
	                    	} else if(alarmIndicatorsNode.getName().equalsIgnoreCase("userlogin")){
	                    		if(node != null){
	                    			F5UserloginSnmp f5snmp = null;
	                    			try{
	                    				Date startdate1 = new Date();
	                    				f5snmp = (F5UserloginSnmp)Class.forName("com.afunms.polling.snmp.f5.F5UserloginSnmp").newInstance();
	                        			returnHash = f5snmp.collect_Data(alarmIndicatorsNode);
	                        			Date enddate1 = new Date();
	                             		SysLogger.info("##############################");
	                     				SysLogger.info("### "+node.getIpAddress()+" F5设备userlogin采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
	                     				SysLogger.info("##############################");
	                        			if(alldata.containsKey(node.getIpAddress())){
	                        				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
	                        				if(ipdata != null){
	                        					ipdata.put("userlogin", returnHash);
	                        				}else{
	                        					ipdata = new Hashtable();
	                        					ipdata.put("userlogin", returnHash);
	                        				}
	                        				alldata.put(node.getIpAddress(), ipdata);
	                        				
	                        			}else{
	                        				Hashtable ipdata = new Hashtable();
	                        				ipdata.put("userlogin", returnHash);
	                        				alldata.put(node.getIpAddress(), ipdata);
	                        				
	                        			}
	                        		}catch(Exception e){
	                        			e.printStackTrace();
	                        		}
	                    		}
	                    	} else if(alarmIndicatorsNode.getName().equalsIgnoreCase("vlans")){
	                    		if(node != null){
	                    			F5VlansSnmp vlansnmp = null;
	                    			try{
	                    				Date startdate1 = new Date();
	                    				vlansnmp = (F5VlansSnmp)Class.forName("com.afunms.polling.snmp.f5.F5VlansSnmp").newInstance();
	                        			returnHash = vlansnmp.collect_Data(alarmIndicatorsNode);
	                        			Date enddate1 = new Date();
	                             		SysLogger.info("##############################");
	                     				SysLogger.info("### "+node.getIpAddress()+" F5设备vlans采集时间 "+(enddate1.getTime()-startdate1.getTime())+"####");
	                     				SysLogger.info("##############################");
	                        			if(alldata.containsKey(node.getIpAddress())){
	                        				Hashtable ipdata = (Hashtable)alldata.get(node.getIpAddress());
	                        				if(ipdata != null){
	                        					ipdata.put("vlans", returnHash);
	                        				}else{
	                        					ipdata = new Hashtable();
	                        					ipdata.put("vlans", returnHash);
	                        				}
	                        				alldata.put(node.getIpAddress(), ipdata);
	                        				
	                        			}else{
	                        				Hashtable ipdata = new Hashtable();
	                        				ipdata.put("vlans", returnHash);
	                        				alldata.put(node.getIpAddress(), ipdata);
	                        				
	                        			}
	                        		}catch(Exception e){
	                        			e.printStackTrace();
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
