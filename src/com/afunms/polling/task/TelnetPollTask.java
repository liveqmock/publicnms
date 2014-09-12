package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.om.Task;
import com.afunms.polling.snmp.AS400Collection;
import com.afunms.polling.telnet.TelnetWrapper;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.RemotePingHostDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.RemotePingHost;

public class TelnetPollTask extends MonitorTask 
{
	
	public TelnetPollTask()
	{
		super();
	}
	

	@Override
	public void run()
	{
		
//		HostNodeDao nodeDao = new HostNodeDao(); 
//    	//得到被监视的主机服务器设备
//    	List nodeList = new ArrayList();
//    	try{
//    		nodeList = nodeDao.loadHostByFlag(1);   	    	
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}finally{
//    		nodeDao.close();
//    	}
//    	if(nodeList != null)
//    	{
//	    	for(int i = nodeList.size() - 1 ; i >= 0 ; i--)
//	    	{    		
//	    		HostNode node = (HostNode)nodeList.get(i);	
//	    		// 3 代表为 用 telnet 协议进行采集
//	    		if(node.getEndpoint() != 3)
//	    		{
//	    			nodeList.remove(i);
//	    		}
//	    	}
//    	}
//    	
//    	
//    	if(nodeList != null && nodeList.size() > 0)
//    	{
//    		
//    		System.out.println("there have " + nodeList.size() + " node to collect by telnet");
//    		
//    		int numTasks = nodeList.size();
//    		int numThreads = 200;
//    		
//    		try {
//    			List numList = new ArrayList();
//    			TaskXml taskxml = new TaskXml();
//    			numList = taskxml.ListXml();
//    			for (int i = 0; i < numList.size(); i++) {
//    				Task task = new Task();
//    				BeanUtils.copyProperties(task, numList.get(i));
//    				if (task.getTaskname().equals("netthreadnum")){
//    					numThreads = task.getPolltime().intValue();
//    				}
//    			}
//
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//    		
//
//    		// 生成线程池
//    		ThreadPool threadPool = new ThreadPool(numThreads);														
//    		// 运行任务
//    		for (int i=0; i<numTasks; i++) {
//    			HostNode node = (HostNode)nodeList.get(i);
//    			
//        		threadPool.runTask(createTask(node));
//    		}
//    		// 关闭线程池并等待所有任务完成
//    		threadPool.join();  		        		
//										
//    	}
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
            	
            	I_HostLastCollectData hostlastdataManager=new HostLastCollectDataManager();
            	if(node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET){
            		//Telnet 获取方式      
            		
            		if(node.getOstype() == 15){
                		System.out.println("采集: 开始用TELNET方式采集IP地址为"+node.getIpAddress()+"类型为AS400主机服务器的数据");
	            		//AS400下的TELNET采集方式
	            		SysLogger.info("采集: 开始用TELNET方式采集IP地址为"+node.getIpAddress()+"类型为AS400主机服务器的数据");
	            		RemotePingHostDao remotePingHostDao = new RemotePingHostDao();
	            		try{
	    					RemotePingHost remotePingHost = (RemotePingHost) remotePingHostDao.findByNodeId(String.valueOf(node.getId()));
	    					
	    					String ipaddress = node.getIpAddress();
//	    					if("10.10.1.1".equals(node.getIpAddress())){
//	    						ipaddress = "iSeriesD.DFW.IBM.COM";
//	    					}
	    					
	    					AS400Collection collection  = new AS400Collection(ipaddress, remotePingHost.getUsername() , remotePingHost.getPassword(),remotePingHost.getLoginPrompt(),remotePingHost.getPasswordPrompt(),remotePingHost.getShellPrompt());
//	    					hashv=collection.execute();
//	    					hostdataManager.createHostDataForAS400(node.getIpAddress(),hashv);
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
    								telnet.connect(node.getIpAddress(), 23);
    								telnet.login(params.getUsername(), params.getPassword(), params.getLoginPrompt(), params.getPasswordPrompt(), params.getShellPrompt());
    							
    								telnet.log("begin to collect telnet info at time " + new Date().toLocaleString());
    								hashv = telnet.getTelnetMonitorDetail();
    								
//    								telnet.log(hashv.toString());
    								
    								telnet.log("end of collect telnet info at time " + new Date().toLocaleString() + "\n\n\n\n\n");

    								if(hashv != null)
    								{
    									hostdataManager.createHostData(node.getIpAddress() , hashv);
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
    };
}
	
	
	
	
	

}
