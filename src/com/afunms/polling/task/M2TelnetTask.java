package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Task;
import com.afunms.polling.snmp.AS400Collection;
import com.afunms.polling.telnet.TelnetWrapper;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.RemotePingHostDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.RemotePingHost;

public class M2TelnetTask extends MonitorTask 
{
	
	public M2TelnetTask()
	{
		super();
	}
	
	public void run()
	{
		try{
		HostNodeDao nodeDao = new HostNodeDao(); 
    	//得到被监视的主机服务器设备
    	List nodeList = new ArrayList();
    	try{
    		nodeList = nodeDao.loadHostByFlag(1);   	    	
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		nodeDao.close();
    	}
    	if(nodeList != null)
    	{
	    	for(int i = nodeList.size() - 1 ; i >= 0 ; i--)
	    	{    		
	    		HostNode node = (HostNode)nodeList.get(i);	
	    		if(!node.isManaged()){
	    			nodeList.remove(i);
	    			continue;
	    		}
	    		// 3 代表为 用 telnet 协议进行采集
	    		if(node.getEndpoint() != 3)
	    		{
	    			nodeList.remove(i);
	    		}
	    	}
    	}
    	
    	//存放用TELNET协议进行采集的集合
		Hashtable nodehash = new Hashtable();
		if(nodeList != null && nodeList.size()>0){
			for(int i=0;i<nodeList.size();i++){
				HostNode node = (HostNode)nodeList.get(i);
				nodehash.put(node.getId()+"", node);
			}
		}
    	
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
    	try{
    		//获取被启用的所有被监视指标
    		monitorItemList = indicatorsdao.getByIntervalAndType("2", "m",1,"host");
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		indicatorsdao.close();
    	}
    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
    	
    	Hashtable docollcetHash = new Hashtable();
		for (int i=0; i<monitorItemList.size(); i++) {
			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
			//SysLogger.info(nodeGatherIndicators.getName()+"==========");
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
		}
    	
    	
    	
    	if(nodeList != null && nodeList.size() > 0){
    		
    		//System.out.println("there have " + nodeList.size() + " node to collect by telnet");
    		
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
    		

//    		// 生成线程池
//    		ThreadPool threadPool = null;	
//    		if(nodeList != null && nodeList.size()>0){
//    			threadPool = new ThreadPool(nodeList.size());	
//    			// 运行任务
//        		for (int i=0; i<numTasks; i++) {
//        			HostNode node = (HostNode)nodeList.get(i);
//            		threadPool.runTask(createTask(node));
//        		}
//        		// 关闭线程池并等待所有任务完成
//        		threadPool.join();
//        		threadPool.close();
//        		threadPool = null;
//    		}
    		
    		
    		// 生成线程池,数目依据被监视的设备多少
    		ThreadPool threadPool = null;	
    		final Hashtable alldata = new Hashtable();
    		// 运行任务
    		if(docollcetHash != null && docollcetHash.size()>0){
    			Hashtable paramhash = new Hashtable();
    			RemotePingHostDao hostDao = new RemotePingHostDao(); 
    			List paramlist = null;
    			try{
    				paramlist = hostDao.loadAll(); 
    			}catch(Exception e){
    				
    			}finally{
    				hostDao.close();
    			}
    			if(paramlist != null && paramlist.size()>0){
    				for(int i=0;i<paramlist.size();i++){
    					RemotePingHost params = (RemotePingHost)paramlist.get(i);
    					paramhash.put(params.getNode_id(), params);
    				}
    				
    			}
    			if(paramhash != null && paramhash.size()>0)ShareData.setParamsHash(paramhash);
    			
    			threadPool = new ThreadPool(docollcetHash.size());	
    			Enumeration newProEnu = docollcetHash.keys();
    			while(newProEnu.hasMoreElements())
    			{
    				String nodeid = (String)newProEnu.nextElement();
    				HostNode node = (HostNode)nodehash.get(nodeid);
    				List dolist = (List)docollcetHash.get(nodeid);
    				threadPool.runTask(createTask(node,dolist,alldata));
    	
    			}
    			// 关闭线程池并等待所有任务完成
        		threadPool.join();             		
        		threadPool.close();
        		HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
        		try{
        			hostdataManager.createMultiHostData(alldata,"host"); 
        		}catch(Exception e){
        			
        		}
        		hostdataManager = null;
        		alldata.clear();
    		}
    		threadPool = null;
    		  		        		
										
    	}
		}catch(Exception e){					 	
			e.printStackTrace();
		}finally{
			SysLogger.info("********M2Telnet Thread Count : "+Thread.activeCount());
		}
	}
	
	
	
	
	 /**
    创建任务
*/	
private static Runnable createTask(final HostNode node,final List dolist,final Hashtable alldata) {
    return new Runnable() {
        public void run() {
            try {  
    	    	TelnetDataCollector telnetdatacollector = new TelnetDataCollector();
    	    	try{
    	    		if(dolist.size()>0){
    	    			alldata.put(node.getIpAddress(), telnetdatacollector.collect_data(node, dolist));
    	    		}
    	    	}catch(Exception e){
    	    		//e.printStackTrace();
    	    	}

            }catch(Exception exc){
            	
            }
            
        }
    };
}
	
	
	
	
	

}
