/*
 * Created on 2005-4-22
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.polling.PollingEngine;
import com.afunms.polling.snmp.Hostlastcollectdata;
import com.afunms.polling.snmp.*;
import com.afunms.polling.snmp.ping.PingSnmp;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.om.*;
import com.afunms.common.util.*;

import com.afunms.polling.node.Host;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;


/**
 * @author hukelei@dhcc.com.cn
 *
 */
public class FirewallCollectDataTask extends MonitorTask {
		
	/**
	 * 
	 */
	public FirewallCollectDataTask() {
		super();
	}

	public void run() {
		try{
	    	HostNodeDao nodeDao = new HostNodeDao(); 
	    	//得到被监视的防火墙设备
	    	List nodeList = nodeDao.loadNetwork(8);   	    	
	    	for(int i=0;i<nodeList.size();i++)
	    	{    		
	    		HostNode node = (HostNode)nodeList.get(i);	
	    	}
			Vector vector=null;							
        		int numTasks = nodeList.size();
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
        		

        		// 生成线程池
        		ThreadPool threadPool = null;
        		if(nodeList != null && nodeList.size()>0){
        			threadPool = new ThreadPool(nodeList.size());
        			// 运行任务
            		for (int i=0; i<nodeList.size(); i++) {
                			threadPool.runTask(createTask((HostNode)nodeList.get(i)));
            		}
            		// 关闭线程池并等待所有任务完成
            		threadPool.join();
            		threadPool.close();
        		}
        		threadPool = null;
        		  		        		
											
		}catch(Exception e){					 	
			e.printStackTrace();
		}
			finally{
			SysLogger.info("********Firewall Thread Count : "+Thread.activeCount());
		}
				
	}
	
    /**
        创建任务
    */	
    private static Runnable createTask(final HostNode node) {
        return new Runnable() {
            public void run() {
                try {                	
                	Vector vector=null;
                	Hashtable hashv = null;
                	I_HostCollectData hostdataManager=new HostCollectDataManager();
                	//SysLogger.info("##########开始采集地址为"+node.getIpAddress()+"防火墙信息##########");
                	if(node.getSysOid().startsWith("1.3.6.1.4.1.3224.") || node.getSysOid().startsWith("1.3.6.1.4.1.2636.")){//HONGLI ADD 凤凰传媒集团（Juniper Networks, Inc. srx3400 internet route）
                		SysLogger.info("##########开始采集地址为"+node.getIpAddress()+"防火墙信息##########");
                		//Netscreen
                		NetscreenSnmp netscreen=new NetscreenSnmp();
                		try{
                			hashv=netscreen.collect_Data(node);
                			hostdataManager.createHostData(node.getIpAddress(),hashv);
                		}catch(Exception ex){
                			ex.printStackTrace();
                		}
                		netscreen=null;
                		vector=null;
                		SysLogger.info("##########结束采集地址为"+node.getIpAddress()+"防火墙信息##########");
                	}else if(node.getSysOid().startsWith("1.3.6.1.4.1.14331.")){
                		SysLogger.info("##########开始采集地址为"+node.getIpAddress()+"防火墙信息##########");
                		//TOES
//                		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
//                		if(node != null){
//                			PingSnmp pingsnmp = null;
//                			try{
//                				pingsnmp = (PingSnmp)Class.forName("com.afunms.polling.snmp.ping.PingSnmp").newInstance();
//                    			returnHash = pingsnmp.collect_Data(alarmIndicatorsNode);
//                    			//在采集过程中已经存入数据库
//                    			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"utilhdx");
//                    		}catch(Exception e){
//                    			e.printStackTrace();
//                    		}
//                		}
                		TopsecSnmp tos=new TopsecSnmp();
    					try{
    						hashv=tos.collect_Data(node);
    						hostdataManager.createHostData(node.getIpAddress(),hashv);
                		}catch(Exception ex){
                			ex.printStackTrace();
                		}
                		tos=null;
    					vector=null;
    					SysLogger.info("##########结束采集地址为"+node.getIpAddress()+"防火墙信息##########");
                	}else if(node.getSysOid().startsWith("1.3.6.1.4.1.3375.")){
                		SysLogger.info("##########开始采集地址为"+node.getIpAddress()+"F5负载均衡信息##########");
                		
                		F5ServerSnmp f5s=new F5ServerSnmp();
    					try{
    						hashv=f5s.collect_Data(node);
    						hostdataManager.createHostData(node.getIpAddress(),hashv);
                		}catch(Exception ex){
                			ex.printStackTrace();
                		}
                		f5s=null;
    					vector=null;
    					SysLogger.info("##########结束采集地址为"+node.getIpAddress()+"F5负载均衡信息##########");
                	}else if(node.getSysOid().startsWith("1.3.6.1.4.1.31648.")){//迪普防火墙 DPtech FW1000-GS
                		SysLogger.info("##########开始采集地址为"+node.getIpAddress()+"防火墙信息##########");
                		//TOES
//                		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
//                		if(node != null){
//                			PingSnmp pingsnmp = null;
//                			try{
//                				pingsnmp = (PingSnmp)Class.forName("com.afunms.polling.snmp.ping.PingSnmp").newInstance();
//                    			returnHash = pingsnmp.collect_Data(alarmIndicatorsNode);
//                    			//在采集过程中已经存入数据库
//                    			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"utilhdx");
//                    		}catch(Exception e){
//                    			e.printStackTrace();
//                    		}
//                		}
                		DPtechSnmp tos=new DPtechSnmp();
    					try{
    						hashv=tos.collect_Data(node);
    						hostdataManager.createHostData(node.getIpAddress(),hashv);
                		}catch(Exception ex){
                			ex.printStackTrace();
                		}
                		tos=null;
    					vector=null;
    					SysLogger.info("##########结束采集地址为"+node.getIpAddress()+"防火墙信息##########");
                	}
				
				
				/*
				if(monitorIp.getOssource().getOsname().toLowerCase().indexOf("enterasys")>=0 && monitorIp.getMethoddic().getMethodname().indexOf("snmp")>=0){
					EnterasysSnmp sCisco=new EnterasysSnmp(monitorIp.getIpaddress(),monitorIp.getCommunity(),monitorIp.getPort());
					hashv=sCisco.collectData();
					//if(vector!=null){					
						hostdataManager.createHostData(monitorIp.getIpaddress(),hashv);
						//hostlastdataManager.createHostData(monitorIp.getIpaddress(),vector);
						//ShareData.setSharedata(monitorIp.getIpaddress(),vector);
					//}
					sCisco=null;
					vector=null;
				}

				if(monitorIp.getOssource().getOsname().toLowerCase().indexOf("fundry")>=0 && monitorIp.getMethoddic().getMethodname().indexOf("snmp")>=0){
					FundrySnmp sCisco=new FundrySnmp(monitorIp.getIpaddress(),monitorIp.getCommunity(),monitorIp.getPort());
					hashv=sCisco.collectData();
					//if(vector!=null){					
						hostdataManager.createHostData(monitorIp.getIpaddress(),hashv);
						//hostlastdataManager.createHostData(monitorIp.getIpaddress(),vector);
						//ShareData.setSharedata(monitorIp.getIpaddress(),vector);
					//}
					sCisco=null;
					vector=null;
				}
				
				if(monitorIp.getOssource().getOsname().toLowerCase().indexOf("alcatel")>=0 && monitorIp.getMethoddic().getMethodname().indexOf("snmp")>=0){
					AlcatelSnmp sCisco=new AlcatelSnmp(monitorIp.getIpaddress(),monitorIp.getCommunity(),monitorIp.getPort());
					hashv=sCisco.collectData();
					//if(vector!=null){					
						hostdataManager.createHostData(monitorIp.getIpaddress(),hashv);
						//hostlastdataManager.createHostData(monitorIp.getIpaddress(),vector);
						//ShareData.setSharedata(monitorIp.getIpaddress(),vector);
					//}
					sCisco=null;
					vector=null;
				}                    
				if(monitorIp.getOssource().getOsname().toLowerCase().indexOf("radware")>=0 && monitorIp.getMethoddic().getMethodname().indexOf("snmp")>=0){
					RadwareSnmp sCisco=new RadwareSnmp(monitorIp.getIpaddress(),monitorIp.getCommunity(),monitorIp.getPort());
					hashv=sCisco.collectData();
					//if(vector!=null){					
						hostdataManager.createHostData(monitorIp.getIpaddress(),hashv);
						//hostlastdataManager.createHostData(monitorIp.getIpaddress(),vector);
						//ShareData.setSharedata(monitorIp.getIpaddress(),vector);
					//}
					sCisco=null;
					vector=null;
				}                    
				
				if(monitorIp.getOssource().getOsname().toLowerCase().indexOf("hirschmann")>=0 && monitorIp.getMethoddic().getMethodname().indexOf("snmp")>=0){
					HirschmannSnmp sCisco=new HirschmannSnmp(monitorIp.getIpaddress(),monitorIp.getCommunity(),monitorIp.getPort());
					hashv=sCisco.collectData();
					//if(vector!=null){					
						hostdataManager.createHostData(monitorIp.getIpaddress(),hashv);
						//hostlastdataManager.createHostData(monitorIp.getIpaddress(),vector);
						//ShareData.setSharedata(monitorIp.getIpaddress(),vector);
					//}
					sCisco=null;
					vector=null;
				}                    
				*/
                }catch(Exception exc){
                	
                }

                //System.out.println("Task " + taskID + ": end");
            }
        };
    }
	
}
