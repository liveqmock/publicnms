/*
 * Created on 2010-06-24
 *
 */
package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.common.util.SysLogger;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Task;
import com.afunms.polling.snmp.cpu.BDComCpuSnmp;
import com.afunms.polling.snmp.cpu.CiscoCpuSnmp;
import com.afunms.polling.snmp.cpu.DLinkCpuSnmp;
import com.afunms.polling.snmp.cpu.EnterasysCpuSnmp;
import com.afunms.polling.snmp.cpu.H3CCpuSnmp;
import com.afunms.polling.snmp.cpu.LinuxCpuSnmp;
import com.afunms.polling.snmp.cpu.MaipuCpuSnmp;
import com.afunms.polling.snmp.cpu.NortelCpuSnmp;
import com.afunms.polling.snmp.cpu.RadwareCpuSnmp;
import com.afunms.polling.snmp.cpu.RedGiantCpuSnmp;
import com.afunms.polling.snmp.cpu.WindowsCpuSnmp;
import com.afunms.polling.snmp.db.DB2DataCollector;
import com.afunms.polling.snmp.db.InformixDataCollector;
import com.afunms.polling.snmp.db.OracleDataCollector;
import com.afunms.polling.snmp.db.SQLServerDataCollector;
import com.afunms.polling.snmp.db.SybaseDataCollector;
import com.afunms.polling.snmp.device.LinuxDeviceSnmp;
import com.afunms.polling.snmp.device.WindowsDeviceSnmp;
import com.afunms.polling.snmp.disk.LinuxDiskSnmp;
import com.afunms.polling.snmp.disk.WindowsDiskSnmp;
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
import com.afunms.polling.snmp.memory.LinuxPhysicalMemorySnmp;
import com.afunms.polling.snmp.memory.MaipuMemorySnmp;
import com.afunms.polling.snmp.memory.NortelMemorySnmp;
import com.afunms.polling.snmp.memory.RedGiantMemorySnmp;
import com.afunms.polling.snmp.memory.WindowsPhysicalMemorySnmp;
import com.afunms.polling.snmp.memory.WindowsVirtualMemorySnmp;
import com.afunms.polling.snmp.ping.PingSnmp;
import com.afunms.polling.snmp.power.CiscoPowerSnmp;
import com.afunms.polling.snmp.power.H3CPowerSnmp;
import com.afunms.polling.snmp.process.LinuxProcessSnmp;
import com.afunms.polling.snmp.process.WindowsProcessSnmp;
import com.afunms.polling.snmp.service.WindowsServiceSnmp;
import com.afunms.polling.snmp.software.LinuxSoftwareSnmp;
import com.afunms.polling.snmp.software.WindowsSoftwareSnmp;
import com.afunms.polling.snmp.storage.LinuxStorageSnmp;
import com.afunms.polling.snmp.storage.WindowsStorageSnmp;
import com.afunms.polling.snmp.system.SystemSnmp;
import com.afunms.polling.snmp.temperature.BDComTemperatureSnmp;
import com.afunms.polling.snmp.temperature.CiscoTemperatureSnmp;
import com.afunms.polling.snmp.temperature.H3CTemperatureSnmp;
import com.afunms.polling.snmp.voltage.CiscoVoltageSnmp;
import com.afunms.polling.snmp.voltage.H3CVoltageSnmp;


public class M10DB2Task extends MonitorTask {
	
	public void run() {
		SysLogger.info("#### 开始执行DB2的10分钟采集任务 ####");
		try{
			NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
	    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 	
	    	Hashtable<String,Hashtable<String,NodeGatherIndicators>> db2Hash = new Hashtable<String,Hashtable<String,NodeGatherIndicators>>();//存放需要监视的DB2指标  <dbid:Hashtable<name:NodeGatherIndicators>>

	    	try{
	    		//获取被启用的DB2所有被监视指标
	    		monitorItemList = indicatorsdao.getByInterval("10", "m",1,"db","db2");
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		indicatorsdao.close();
	    	}
	    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
	    	for(int i=0;i<monitorItemList.size();i++){
	    		NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);

	    		//DB2数据库
    			if(db2Hash.containsKey(nodeGatherIndicators.getNodeid())){
    				//若dbid已经存在,则获取原来的,再把新的添加进去
    				Hashtable gatherHash = (Hashtable)db2Hash.get(nodeGatherIndicators.getNodeid());
    				gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
    				db2Hash.put(nodeGatherIndicators.getNodeid(), gatherHash);
    			}else{
    				//若dbid不存在,则把新的添加进去
    				Hashtable gatherHash = new Hashtable();
    				gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
    				db2Hash.put(nodeGatherIndicators.getNodeid(), gatherHash);
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
        		
        		//生成DB2采集线程
        		if(db2Hash != null && db2Hash.size()>0){
        			// 生成线程池
            		ThreadPool dbthreadPool = new ThreadPool(db2Hash.size());
        			//存在需要采集的DB2指标
        			for(Enumeration enumeration = db2Hash.keys(); enumeration.hasMoreElements();){
						String dbid = (String)enumeration.nextElement();
						Hashtable<String,NodeGatherIndicators> gatherHash = (Hashtable<String,NodeGatherIndicators>)db2Hash.get(dbid);
						dbthreadPool.runTask(createDB2Task(dbid,gatherHash));
					}
        			// 关闭线程池并等待所有任务完成
            		dbthreadPool.join();
            		dbthreadPool.close();
            		dbthreadPool = null;
        		}
	    								
		}catch(Exception e){					 	
			e.printStackTrace();
		}finally{
			SysLogger.info("#### M10DB2Task Thread Count : "+Thread.activeCount()+" ####");
		}
	}
	
	/**
    创建DB2采集任务
	 */	
	private static Runnable createDB2Task(final String dbid,final Hashtable gatherHash) {
    return new Runnable() {
        public void run() {
            try {                	
//            	HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
//            	DB2DataCollector db2collector = new DB2DataCollector();
//            	SysLogger.info("##############################");
//            	SysLogger.info("### 开始采集ID为"+dbid+"的DB2数据 ");
//            	SysLogger.info("##############################");
//            	db2collector.collect_data(dbid, gatherHash);
            }catch(Exception exc){
            	
            }
        }
    };
	}
	
}
