package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.config.model.Nodeconfig;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Systemcollectdata;
import com.gatherdb.GathersqlListManager;
import java.util.Vector;

public class HostDatatempNodeconfRtosql {
	
	
	/**
	 * 把结果生成sql
	 * @param dataresult 采集结果
	 * @param node 网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {//是否启动分离模式
			//处理nodeconfig信息入库
				
					  NodeUtil nodeUtil = new NodeUtil();		
			 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
			 			Nodeconfig nodeconfig = (Nodeconfig)dataresult.get("nodeconfig");
					String deleteSql = "delete from nms_nodeconfig where nodeid='" +node.getId() + "'";
					
					Vector list=new Vector();
					if(nodeconfig != null){
						try {									
							StringBuffer sql = new StringBuffer(200);
						    sql.append("insert into nms_nodeconfig(nodeid,hostname,sysname,serialNumber,cSDVersion,numberOfProcessors,mac)values('");
						    sql.append(nodeDTO.getId());
						    sql.append("','");
						    sql.append(nodeconfig.getHostname());//hostname
						    sql.append("','");
						    sql.append(nodeconfig.getSysname());//sysname
						    sql.append("','");
						    sql.append(nodeconfig.getSerialNumber());//serialNumber
						    sql.append("','");
						    sql.append(nodeconfig.getCSDVersion());//cSDVersion
						    sql.append("','");
						    sql.append(nodeconfig.getNumberOfProcessors());//numberOfProcessors
						    sql.append("','");
						    sql.append(nodeconfig.getMac());//mac
						    sql.append("')");
//						    System.out.println(sql.toString());
						    list.add(sql.toString());
						    
						    GathersqlListManager.AdddateTempsql(deleteSql, list);
						    deleteSql=null;
						    sql=null;
						    nodeconfig=null;
						    nodeDTO=null;
						    
						    
						    
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			
				
	
	
}



