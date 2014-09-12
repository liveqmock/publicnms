package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.application.dao.SlaNodeConfigDao;
import com.afunms.application.model.SlaNodeConfig;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.om.Task;
import com.afunms.polling.snmp.sla.ICMPSnmp;

public class M5SLATelnetTask extends MonitorTask 
{
	
	public M5SLATelnetTask()
	{
		super();
	}
	
	public void run()
	{
		try{
			SlaNodeConfigDao configdao = new SlaNodeConfigDao();
			//得到被监视的SLA列表
			List nodeSnmpList = new ArrayList();
			List nodeTelnetList = new ArrayList();
			Hashtable nodeHash = new Hashtable();
			Hashtable telnetHash = new Hashtable();
			
	    	try{
	    		nodeSnmpList = configdao.getConfigByIntervalAndUnitAndFlagAndColltype(5,"m",1,"snmp");	    	
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		configdao.close();
	    	}
	    	configdao = new SlaNodeConfigDao();
	    	try{
	    		nodeTelnetList = configdao.getConfigByIntervalAndUnitAndFlagAndColltype(5,"m",1,"telnet");	    	
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		configdao.close();
	    	}
	    	
			HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
			List telnetlist = null;
			try {
				telnetlist = haweitelnetconfDao.getAllTelnetConfig();
				if(telnetlist != null && telnetlist.size()>0){
					for(int i=0;i<telnetlist.size();i++){
						Huaweitelnetconf vo = (Huaweitelnetconf)telnetlist.get(i);
						//SysLogger.info("&&&&&&&&&&put---"+vo.getId());
						telnetHash.put(vo.getId(), vo);
					}
				}
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				haweitelnetconfDao.close();
			}
	    	
	    	
	    	SysLogger.info("nodeList size======="+nodeTelnetList.size());
	    	if(nodeTelnetList != null){
		    	for(int i=0;i<nodeTelnetList.size();i++){    		
		    		SlaNodeConfig nodeconfig = (SlaNodeConfig)nodeTelnetList.get(i);	
		    		if(nodeHash.containsKey(nodeconfig.getTelnetconfig_id()+"")){
		    			List entrylist = (List)nodeHash.get(nodeconfig.getTelnetconfig_id()+"");
		    			entrylist.add(nodeconfig);
		    			SysLogger.info("put config==="+nodeconfig.getTelnetconfig_id()+"");
		    			nodeHash.put(nodeconfig.getTelnetconfig_id()+"", entrylist);
		    		}else{
		    			List entrylist = new ArrayList();
		    			entrylist.add(nodeconfig);
		    			nodeHash.put(nodeconfig.getTelnetconfig_id()+"", entrylist);
		    			SysLogger.info("put config=###=="+nodeconfig.getTelnetconfig_id()+"");
		    		}
		    	}
	    	}
    	
    	
    	
    	//if(nodeSnmpList != null && nodeSnmpList.size() > 0){
    		int numTasks = nodeSnmpList.size();
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
    		ThreadPool threadPoolTelnet = null;
    		final Hashtable alldata = new Hashtable();
    		// 运行任务
    		
    		//采集TELNET数据
    		if(nodeHash != null && nodeHash.size()>0){    			
    			threadPoolTelnet = new ThreadPool(nodeHash.size());	
    			Enumeration newProEnu = nodeHash.keys();
    			while(newProEnu.hasMoreElements())
    			{
    				String telnetconfig_id = (String)newProEnu.nextElement();
    				List nodelist = (List)nodeHash.get(telnetconfig_id);
		    	   Huaweitelnetconf telconf = new Huaweitelnetconf();
		    	   try{
		    		   SysLogger.info("get id ========="+telnetconfig_id);
		    		   telconf = (Huaweitelnetconf)telnetHash.get(Integer.parseInt(telnetconfig_id));
		    	   }catch(Exception e){
		    		   e.printStackTrace();
		    	   }
		    	   threadPoolTelnet.runTask(createTask(telconf,nodelist,alldata));
    	
    			}
    			// 关闭线程池并等待所有任务完成
    			threadPoolTelnet.join();             		
    			threadPoolTelnet.close();
        		HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
        		try{
        			SysLogger.info("######插入数据库 ########################");
        			hostdataManager.createAllSLAData(alldata); 
        		}catch(Exception e){
        			
        		}
        		hostdataManager = null;
        		alldata.clear();
    		}
    		threadPoolTelnet = null;
    		
    		
    		if(nodeSnmpList != null && nodeSnmpList.size()>0){    			
    			threadPool = new ThreadPool(nodeSnmpList.size());	
    			//Enumeration newProEnu = nodeHash.keys();
//    			while(newProEnu.hasMoreElements())
//    			{
    			for(int i=0;i<nodeSnmpList.size();i++){
    				SlaNodeConfig nodeconfig = (SlaNodeConfig)nodeSnmpList.get(i);
    				Huaweitelnetconf telconf = new Huaweitelnetconf();
    				try{
 		    		   telconf = (Huaweitelnetconf)telnetHash.get(nodeconfig.getTelnetconfig_id());
 		    		   //telconf = (Huaweitelnetconf)haweitelnetconfDao.findByID(telnetconfig_id);
 		    	   }catch(Exception e){
 		    		   e.printStackTrace();
 		    	   }finally{
 		    		   //haweitelnetconfDao.close();
 		    	   }
    				
    				//List dolist = (List)newProEnu.nextElement();;
    				threadPool.runTask(createTask(nodeconfig,telconf,alldata));
    	
    			}
    			// 关闭线程池并等待所有任务完成
        		threadPool.join();             		
        		threadPool.close();
        		HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
        		try{
        			//SysLogger.info("alldata size============"+alldata.size());
        			hostdataManager.createSLAData(alldata); 
        		}catch(Exception e){
        			
        		}
        		hostdataManager = null;
        		alldata.clear();
    		}
    		threadPool = null;
    		  		        		
										
    	//}
		}catch(Exception e){					 	
			e.printStackTrace();
		}finally{
			SysLogger.info("********M5SLATelnet Thread Count : "+Thread.activeCount());
		}
	}
	
	
	
	
	 /**
    创建任务
	  */	
	private static Runnable createTask(final SlaNodeConfig nodeconfig,final Huaweitelnetconf telconf,final Hashtable alldata) {
		return new Runnable() {
        public void run() {
            try {  
    	    	SLATelnetDataCollector telnetdatacollector = new SLATelnetDataCollector();
    	    	try{
    	    		//if(nodelist.size()>0){
    	    		ICMPSnmp icmpsnmp = new ICMPSnmp();
    	    		alldata.put(nodeconfig.getId()+"", icmpsnmp.collect_Data(nodeconfig, telconf));
    	    		//}
    	    	}catch(Exception e){
    	    		//e.printStackTrace();
    	    	}

            }catch(Exception exc){
            	
            }
            
        }
    };
}
	
	 /**
    创建任务
	  */	
	private static Runnable createTask(final Huaweitelnetconf telconf,final List nodelist,final Hashtable alldata) {
		return new Runnable() {
        public void run() {
            try {  
    	    	SLATelnetDataCollector telnetdatacollector = new SLATelnetDataCollector();
    	    	try{
    	    		if(nodelist.size()>0){
    	    			SysLogger.info("telconf====="+telconf.getId());
    	    			alldata.put(telconf.getId()+"", telnetdatacollector.collect_data(telconf, nodelist));
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
