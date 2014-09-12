/*
 * Created on 2010-06-24
 *
 */
package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.om.Task;
import com.afunms.polling.snmp.db.MySqlDataCollector;
import com.afunms.polling.snmp.db.SQLServerDataCollector;


public class M5MySqlTask extends MonitorTask {
	
	public void run() {
		SysLogger.info("#### 开始执行MYSQL的5分钟采集任务 ####");
		try{
			NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
	    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 	
	    	Hashtable<String,Hashtable<String,NodeGatherIndicators>> sqlserverHash = new Hashtable<String,Hashtable<String,NodeGatherIndicators>>();//存放需要监视的DB2指标  <dbid:Hashtable<name:NodeGatherIndicators>>
	    	Hashtable dbhash = new Hashtable();
			DBDao dbdao = null;
			try{	
				DBTypeVo db2typevo = null;
				DBTypeDao typedao = null;
				try {
					typedao = new DBTypeDao();
					db2typevo = (DBTypeVo) typedao.findByDbtype("mysql");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (typedao != null)
						typedao.close();
				}
				List db2list = new ArrayList();
				try {
					dbdao = new DBDao();
					db2list = dbdao.getDbByTypeMonFlag(db2typevo.getId(), 1);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (dbdao != null)
						dbdao.close();
				}
				//取得db2采集
				if (db2list != null) {
					for (int i = 0; i < db2list.size(); i++) {
						DBVo dbmonitorlist = (DBVo)db2list.get(i);
						dbhash.put(dbmonitorlist.getId()+"", dbmonitorlist);
					}
				}
			}catch(Exception e){
				
			}
	    	try{
	    		//获取被启用的MYSQL所有被监视指标
	    		monitorItemList = indicatorsdao.getByInterval("5", "m",1,"db","mysql");
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		indicatorsdao.close();
	    	}
	    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
	    	for(int i=0;i<monitorItemList.size();i++){
	    		NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
	    		//判断该数据库是否被监视
	    		if(!dbhash.containsKey(nodeGatherIndicators.getNodeid()))continue;
	    		//MYSQL数据库
    			if(sqlserverHash.containsKey(nodeGatherIndicators.getNodeid())){
    				//若dbid已经存在,则获取原来的,再把新的添加进去
    				Hashtable gatherHash = (Hashtable)sqlserverHash.get(nodeGatherIndicators.getNodeid());
    				gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
    				sqlserverHash.put(nodeGatherIndicators.getNodeid(), gatherHash);
    			}else{
    				//若dbid不存在,则把新的添加进去
    				Hashtable gatherHash = new Hashtable();
    				gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
    				sqlserverHash.put(nodeGatherIndicators.getNodeid(), gatherHash);
    			}
	    	}
        		int numThreads = 200;        		
        		try {
        			List numList = new ArrayList();
        			TaskXml taskxml = new TaskXml();
        			numList = taskxml.ListXml();
        			for (int k = 0; k < numList.size(); k++) {
        				Task task = new Task();
        				BeanUtils.copyProperties(task, numList.get(k));
        				if (task.getTaskname().equals("netthreadnum")){
        					numThreads = task.getPolltime().intValue();
        				}
        			}

        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        		
        		//生成MYSQL采集线程
        		if(sqlserverHash != null && sqlserverHash.size()>0){
        			// 生成线程池
            		ThreadPool dbthreadPool = new ThreadPool(sqlserverHash.size());
        			//存在需要采集的MYSQL指标
        			for(Enumeration enumeration = sqlserverHash.keys(); enumeration.hasMoreElements();){
						String dbid = (String)enumeration.nextElement();
						Hashtable<String,NodeGatherIndicators> gatherHash = (Hashtable<String,NodeGatherIndicators>)sqlserverHash.get(dbid);
						dbthreadPool.runTask(createMySqlTask(dbid,gatherHash));
					}
        			// 关闭线程池并等待所有任务完成
            		dbthreadPool.join();
            		dbthreadPool.close();
            		dbthreadPool = null;
        		}
	    								
		}catch(Exception e){					 	
			e.printStackTrace();
		}finally{
			SysLogger.info("#### M5MYSQLTask Thread Count : "+Thread.activeCount()+" ####");
		}
	}
	
	/**
    创建SQLSERVER采集任务
	 */	
	private static Runnable createMySqlTask(final String dbid,final Hashtable gatherHash) {
    return new Runnable() {
        public void run() {
            try {
//            	MySqlDataCollector mysqlcollector = new MySqlDataCollector();
//            	SysLogger.info("##############################");
//            	SysLogger.info("### 开始采集ID为"+dbid+"的MYSQL数据 ");
//            	SysLogger.info("##############################");
//            	mysqlcollector.collect_data(dbid, gatherHash);
            }catch(Exception exc){
            	
            }
        }
    };
	}
	
}
