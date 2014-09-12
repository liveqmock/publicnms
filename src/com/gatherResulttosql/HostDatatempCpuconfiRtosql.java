package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.config.model.Nodecpuconfig;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.gatherdb.GathersqlListManager;

public class HostDatatempCpuconfiRtosql {

	
	

	/**
	 * 把结果生成sql
	 * @param dataresult 采集结果
	 * @param node 网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {//是否启动分离模式
			

					NodeUtil nodeUtil = new NodeUtil();		
			 		NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);

					List cpuconfiglist = (ArrayList)dataresult.get("cpuconfiglist");
					String deleteSql = "delete from nms_nodecpuconfig where nodeid='" +node.getId() + "'";
					Vector list=new Vector();
					
					//得到CPU平均
					int index = 0;
					if (cpuconfiglist != null && cpuconfiglist.size() > 0) {
						for (int i = 0; i < cpuconfiglist.size(); i++){
							Nodecpuconfig cpuconfig = (Nodecpuconfig) cpuconfiglist.get(i);
							if(cpuconfig.getProcessorSpeed() == null){
								cpuconfig.setProcessorSpeed("");
							}
							if(cpuconfig.getProcessorType() == null){
								cpuconfig.setProcessorType("");
							}
							try {
							    StringBuffer sql = new StringBuffer(200);
							    sql.append("insert into nms_nodecpuconfig(nodeid,dataWidth,processorId,name,l2CacheSize,l2CacheSpeed,descrOfProcessors,processorType,processorSpeed)values('");
							    sql.append(nodeDTO.getId());
							    sql.append("','");
							    sql.append(cpuconfig.getDataWidth());//dataWidth
							    sql.append("','");
							    sql.append(cpuconfig.getProcessorId());//processorId
							    sql.append("','");
							    sql.append(cpuconfig.getName());//name
							    sql.append("','");
							    sql.append(cpuconfig.getL2CacheSize());//l2CacheSize
							    sql.append("','");
							    sql.append(cpuconfig.getL2CacheSpeed());//l2CacheSpeed
							    sql.append("','");
							    sql.append(cpuconfig.getDescrOfProcessors());//descrOfProcessors
							    sql.append("','");
							    sql.append(cpuconfig.getProcessorType());//processorType
							    sql.append("','");
							    sql.append(cpuconfig.getProcessorSpeed());//processorSpeed
							    sql.append("')");//unit
							    list.add(sql.toString());
							    sql=null;
							    cpuconfig=null;
							    
							    
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					 GathersqlListManager.AdddateTempsql(deleteSql, list);
					 list=null;
					 cpuconfiglist=null;
					
					
				}
			}
			
			
			
	

	
}
