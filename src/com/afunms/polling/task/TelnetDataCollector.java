package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import montnets.SmsDao;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.alarm.util.AlarmResourceCenter;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.model.Diskconfig;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.snmp.AS400Collection;
import com.afunms.polling.telnet.TelnetWrapper;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.RemotePingHostDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.RemotePingHost;

public class TelnetDataCollector {
	
	public TelnetDataCollector()
	{
	}
	public void collect_data(String id,Hashtable gatherHash) {
		try {                	
        	Vector vector=null;
        	Hashtable hashv = null;
        	I_HostCollectData hostdataManager=new HostCollectDataManager();
        	//I_HostLastCollectData hostlastdataManager=new HostLastCollectDataManager();
        	
        	HostNodeDao nodeDao = new HostNodeDao(); 
        	//得到被监视的主机服务器设备
        	HostNode node = null;
        	try{
        		node = (HostNode)nodeDao.findByID(id);
        	}catch(Exception e){
        		
        	}finally{
        		nodeDao.close();
        	}
        	if(node == null)return;
        	if(!node.isManaged())return;
        	
			NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
	    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
	    	try{
	    		//获取被启用的所有被监视指标
	    		monitorItemList = indicatorsdao.getByIntervalAndType("5", "m",1,"host");
	    	}catch(Exception e){
	    		e.printStackTrace();   
	    	}finally{
	    		indicatorsdao.close();
	    	}
	    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
	    	NodeGatherIndicators pingnodeGatherIndicators = null;
    		for (int i=0; i<monitorItemList.size(); i++) {
    			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
    			//若该设备不是通过SNMP采集的方式,则过滤掉
    			if(nodeGatherIndicators.getName().equalsIgnoreCase("ping")){
    				pingnodeGatherIndicators = nodeGatherIndicators;
    			}       			          		
    		}
        	
        	if(node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET){
        		//Telnet 获取方式             		
        		if(node.getOstype() == 15){
            		//System.out.println("采集: 开始用TELNET方式采集IP地址为"+node.getIpAddress()+"类型为AS400主机服务器的数据");
            		//AS400下的TELNET采集方式
        			SysLogger.info("采集: 开始用TELNET方式采集IP地址为"+node.getIpAddress()+"类型为AS400主机服务器的数据");
            		RemotePingHostDao remotePingHostDao = new RemotePingHostDao();
            		try{
    					RemotePingHost remotePingHost = (RemotePingHost) remotePingHostDao.findByNodeId(String.valueOf(node.getId()));
    					
    					String ipaddress = node.getIpAddress();
    					
    					try {
							AS400Collection collection  = new AS400Collection(ipaddress, remotePingHost.getUsername() , remotePingHost.getPassword(),remotePingHost.getLoginPrompt(),remotePingHost.getPasswordPrompt(),remotePingHost.getShellPrompt());
							hashv=collection.execute(gatherHash);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
    					//需要填写CPU,磁盘利用率,任务数量等告警检测
    					Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
    					try {  
							updateData(host,hashv);
						} catch (Exception e) {
							e.printStackTrace();
						}
    					try {
//    						System.out.println("hashv==============="+hashv);
							hostdataManager.createHostDataForAS400(node.getIpAddress(),hashv);
						} catch (Exception e) {
							e.printStackTrace();
						}
    				}catch(Exception e){
    					e.printStackTrace();
    				} finally{
    					remotePingHostDao.close();
    					hashv=null;
    				}
    				
            	}else{
            		try{
            			//  首先获取协议信息
            			System.out.println("begin to collect info of " + node.getIpAddress() + " by telnet");
            			int nodeId = node.getId();           			
            			RemotePingHostDao hostDao = new RemotePingHostDao();
            			RemotePingHost params = hostDao.findByNodeId(String.valueOf(nodeId)); 
            			hostDao.close();
            			if(params != null)
            			{
            				TelnetWrapper telnet = new TelnetWrapper();
            				try
							{
            					Vector<Pingcollectdata> _vector = new Vector<Pingcollectdata>();
            					
            					Pingcollectdata hostdata=null;
		    					Calendar date=Calendar.getInstance();
		    					hostdata=new Pingcollectdata();
		    					hostdata.setIpaddress(node.getIpAddress());
		    					hostdata.setCollecttime(date);
		    					hostdata.setCategory("Ping");
		    					hostdata.setEntity("Utilization");
		    					hostdata.setSubentity("ConnectUtilization");
		    					hostdata.setRestype("dynamic");
		    					hostdata.setUnit("%");
		        				long starttime = 0;
		        				long endtime = 0;
		        				long condelay = 0;
		        				int flag = 0;
            					try{
            						starttime = System.currentTimeMillis();
            						telnet.connect(node.getIpAddress(), 23);
            						telnet.login(params.getUsername(), params.getPassword(), params.getLoginPrompt(), params.getPasswordPrompt(), params.getShellPrompt());
            						endtime = System.currentTimeMillis();
            					}catch(Exception e){
            						endtime = System.currentTimeMillis();
									e.printStackTrace();
									flag = 1;
            					}
            					condelay = endtime - starttime;
								if(flag == 0){
									hostdata.setThevalue("100");
								}else{
									hostdata.setThevalue("0");
								}
								_vector.add(0, hostdata);
								
								
								//响应时间
								hostdata=new Pingcollectdata();
								hostdata.setIpaddress(node.getIpAddress());
								hostdata.setCollecttime(date);
								hostdata.setCategory("Ping");
								hostdata.setEntity("ResponseTime");
								hostdata.setSubentity("ResponseTime");
								hostdata.setRestype("dynamic");
								hostdata.setUnit("毫秒");
								hostdata.setThevalue(condelay+"");
								_vector.add(1, hostdata);

								
								//telnet.log("begin to collect telnet info at time " + new Date().toLocaleString());
								hashv = telnet.getTelnetMonitorDetail();
								//telnet.log("end of collect telnet info at time " + new Date().toLocaleString() + "\n\n\n\n\n");

								if(hashv != null)
								{
			            			if(_vector!=null){
			            				if(pingnodeGatherIndicators != null){
			            					hostdataManager.createHostData(_vector,pingnodeGatherIndicators);	
				            				ShareData.setPingdata(node.getIpAddress(),_vector);
				            				hashv.put("ping", _vector);
			            				}
			            				//hostdataManager.createHostData(_vector);	
			            				
			            			}
									hostdataManager.createHostData(node.getIpAddress() , hashv);
			            			vector=null;
								}
								
							} catch (Exception e)
							{
								e.printStackTrace();
							}
							finally
							{
								try
								{
									telnet.disconnect();
								} catch (Exception e)
								{
									e.printStackTrace();
								}
							}
            				
            				
            			}
            			else
            			{
            				System.out.println("can not find the parameters of telnet protocol by nodeId " + nodeId);
            			}
            			

            		}catch(Exception e){
            			e.printStackTrace();
            		}
            	}
        		
				
				hashv=null;
        	}

        }catch(Exception exc){
        	
        }
	}
	
	public Hashtable collect_data(HostNode node,List dolist) {
    	Vector vector=null;
    	Hashtable hashv = null;
		try {                	
        	I_HostCollectData hostdataManager=new HostCollectDataManager();
	    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
	    	monitorItemList = dolist;
	    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
	    	NodeGatherIndicators pingnodeGatherIndicators = null;
    		for (int i=0; i<monitorItemList.size(); i++) {
    			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
    			//若该设备不是通过SNMP采集的方式,则过滤掉
    			if(nodeGatherIndicators.getName().equalsIgnoreCase("ping")){
    				pingnodeGatherIndicators = nodeGatherIndicators;
    			}       			          		
    		}
        	
        	if(node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET){
        		//Telnet 获取方式             		
        		if(node.getOstype() == 15){
            		//System.out.println("采集: 开始用TELNET方式采集IP地址为"+node.getIpAddress()+"类型为AS400主机服务器的数据");
            		//AS400下的TELNET采集方式
            		//SysLogger.info("采集: 开始用TELNET方式采集IP地址为"+node.getIpAddress()+"类型为AS400主机服务器的数据");
            		RemotePingHostDao remotePingHostDao = new RemotePingHostDao();
            		try{
    					RemotePingHost remotePingHost = (RemotePingHost) remotePingHostDao.findByNodeId(String.valueOf(node.getId()));
    					
    					String ipaddress = node.getIpAddress();
    					
    					AS400Collection collection  = new AS400Collection(ipaddress, remotePingHost.getUsername() , remotePingHost.getPassword(),remotePingHost.getLoginPrompt(),remotePingHost.getPasswordPrompt(),remotePingHost.getShellPrompt());
    					hashv=collection.execute(monitorItemList);
    					//需要填写CPU,磁盘利用率,任务数量等告警检测
    					//
    					//
    					Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
    					updateData(host,hashv);
    					hostdataManager.createHostDataForAS400(node.getIpAddress(),hashv);
    				}catch(Exception e){
    					//e.printStackTrace();
    				} finally{
    					remotePingHostDao.close();
    					hashv=null;
    				}
    				
            	} else {
            		try{
            			//  首先获取协议信息
            			SysLogger.info(" ######################## ");
            			SysLogger.info(" ### 开始采集服务器 " + node.getIpAddress() + " by telnet");
            			SysLogger.info(" ######################## ");
            			int nodeId = node.getId(); 
            			RemotePingHost params = null;
            			if(ShareData.getParamsHash().containsKey(nodeId+"")){
            				params = (RemotePingHost)ShareData.getParamsHash().get(nodeId+"");
            			}
            			if(params != null)
            			{
            				Hashtable gatherHash = new Hashtable();
            				if(monitorItemList != null && monitorItemList.size()>0){
            					for(int i=0;i<monitorItemList.size();i++){
            						NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
            						gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators.getName());
            					}
            				}
            				if(gatherHash == null)return null;
            				
            				TelnetWrapper telnet = new TelnetWrapper();
            				try
							{
            					Vector<Pingcollectdata> _vector = new Vector<Pingcollectdata>();
            					
            					Pingcollectdata hostdata=null;
		    					Calendar date=Calendar.getInstance();
		    					hostdata=new Pingcollectdata();
		    					hostdata.setIpaddress(node.getIpAddress());
		    					hostdata.setCollecttime(date);
		    					hostdata.setCategory("Ping");
		    					hostdata.setEntity("Utilization");
		    					hostdata.setSubentity("ConnectUtilization");
		    					hostdata.setRestype("dynamic");
		    					hostdata.setUnit("%");
		        				long starttime = 0;
		        				long endtime = 0;
		        				long condelay = 0;
		        				int flag = 0;
            					try{
            						starttime = System.currentTimeMillis();
            						telnet.connect(node.getIpAddress(), 23);
            						telnet.login(params.getUsername(), params.getPassword(), params.getLoginPrompt(), params.getPasswordPrompt(), params.getShellPrompt());
            						endtime = System.currentTimeMillis();
            					}catch(Exception e){
            						endtime = System.currentTimeMillis();
									e.printStackTrace();
									flag = 1;
            					}
            					condelay = endtime - starttime;
								if(flag == 0){
									hostdata.setThevalue("100");
								}else{
									hostdata.setThevalue("0");
								}
								_vector.add(0, hostdata);
								
								//响应时间
								hostdata=new Pingcollectdata();
								hostdata.setIpaddress(node.getIpAddress());
								hostdata.setCollecttime(date);
								hostdata.setCategory("Ping");
								hostdata.setEntity("ResponseTime");
								hostdata.setSubentity("ResponseTime");
								hostdata.setRestype("dynamic");
								hostdata.setUnit("毫秒");
								hostdata.setThevalue(condelay+"");
								_vector.add(1, hostdata);

								//telnet.log("begin to collect telnet info at time " + new Date().toLocaleString());
								telnet.setMonitorItemList(monitorItemList);
								hashv = telnet.getTelnetMonitorDetail();
								//telnet.log("end of collect telnet info at time " + new Date().toLocaleString() + "\n\n\n\n\n");
								if(hashv != null){
			            			if(_vector!=null){
			            				if(pingnodeGatherIndicators != null){
			            					hostdataManager.createHostData(_vector,pingnodeGatherIndicators);	
				            				ShareData.setPingdata(node.getIpAddress(),_vector);
				            				Hashtable pinghash = new Hashtable();
				            				pinghash.put("ping", _vector);
				            				hashv.put("ping", pinghash);
			            				}
			            			}
//									hostdataManager.createHostData(node.getIpAddress() , hashv);
//			            			vector=null;
								}
							} catch (Exception e)
							{
								e.printStackTrace();
							}
							finally
							{
								try
								{
									telnet.disconnect();
								} catch (Exception e)
								{
									e.printStackTrace();
								}
							}
            			}
            			else
            			{
            				System.out.println("can not find the parameters of telnet protocol by nodeId " + nodeId);
            			}
            			

            		}catch(Exception e){
            			e.printStackTrace();
            		}
            	}
        	}
        	
        }catch(Exception exc){
        	
        }
        return hashv;
	}
	
	/**
	 * 创建告警数据
	 * @author nielin
	 * @param vo
	 * @param collectingData
	 */
	public void updateData(Object vo , Object collectingData){
		Host node = (Host)vo;
//		Host host = (Host)PollingEngine.getInstance().getNodeByID(node.getId());
		Hashtable datahashtable = (Hashtable)collectingData;
		List jobList = (List)datahashtable.get("Jobs");
		List subSystemList = (List)datahashtable.get("subSystem");
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, "as400");
		for(int i = 0 ; i < list.size() ; i ++){
			try {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
				String indicators = alarmIndicatorsNode.getName();
				CheckEventUtil checkEventUtil = new CheckEventUtil();
				if("diskperc".equals(indicators)){
					//磁盘利用率
//				SysLogger.info("### 开始检测磁盘是否告警 ###");
					Vector diskVector = new Vector();
					if(datahashtable.get("disk") != null)diskVector = (Vector)datahashtable.get("disk");
					if(diskVector == null)diskVector = new Vector();
					checkEventUtil.checkDisk(node, diskVector, alarmIndicatorsNode);
				}else if("jobs".equals(indicators)){
					checkEventUtil.createJobForAS400GroupEventList(node.getIpAddress() , jobList , alarmIndicatorsNode);
				}else if("subsystem".equals(indicators)){
					checkEventUtil.createJobForAS400SubSystemEventList(node.getIpAddress() , jobList , subSystemList , alarmIndicatorsNode);
				}else if("jobnumber".equals(indicators)){
//					checkEventUtil.updateData(vo, collectingData, AlarmConstant.TYPE_HOST, "as400", alarmIndicatorsNode);
					List joblist = new ArrayList();
					String value = "0";  
					if(datahashtable.get("Jobs") != null)joblist = (List)datahashtable.get("Jobs");
					if(joblist == null)joblist = new ArrayList();
					value = joblist.size()+"";
					checkEventUtil.checkEvent(node, alarmIndicatorsNode, value, null);  
				}else if("cpu".equals(indicators)){
//					checkEventUtil.updateData(vo, collectingData, AlarmConstant.TYPE_HOST, "as400", alarmIndicatorsNode);
					String value = "0";
					Vector cpuVector = new Vector();
					if(datahashtable.get("cpu") != null)cpuVector = (Vector)datahashtable.get("cpu");
					if(cpuVector != null && cpuVector.size()>0){
						CPUcollectdata cpudata=(CPUcollectdata)cpuVector.get(0);
						value = cpudata.getThevalue();
					}
					checkEventUtil.checkEvent(node, alarmIndicatorsNode, value, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
