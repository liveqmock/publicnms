package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Storagecollectdata;
import com.gatherdb.GathersqlListManager;


/***
 * 
 * storage 采集信息生成sql
 * @author konglq
 *
 */
public class HostDatatempstorageRttosql {
	
	
	/**
	 * 把结果生成sql
	 * @param dataresult 采集结果
	 * @param node 网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {//是否启动分离模式

			if(dataresult != null && dataresult.size()>0){
				
				String hendsql="insert into nms_storage_data_temp(nodeid,ip,`type`,subtype,name,stype,cap,storageindex,collecttime)values(";
				String endsql=")";
				
				   
				    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    NodeUtil nodeUtil = new NodeUtil();
					Vector storageVector = (Vector)dataresult.get("storage");
					NodeDTO nodeDTO = null;
					nodeDTO = nodeUtil.creatNodeDTOByNode(node);
					
					String deleteSql = "delete from nms_storage_data_temp where nodeid='" +node.getId() + "'";
					if(storageVector != null && storageVector.size()>0){
						Calendar tempCal=Calendar.getInstance();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						
						
						Vector list=new Vector();
						for(int i=0;i<storageVector.size();i++){
							Storagecollectdata vo = (Storagecollectdata) storageVector.elementAt(i);
							StringBuffer sbuffer = new StringBuffer(100);
							sbuffer.append(hendsql);
							sbuffer.append("'").append(node.getId()).append("',");
							sbuffer.append("'").append(node.getIpAddress()).append("',");
							sbuffer.append("'").append(nodeDTO.getType()).append("',");
							sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
							sbuffer.append("'").append(vo.getName().replace("\\", "/")).append("',");
							sbuffer.append("'").append(vo.getType()).append("',");
							sbuffer.append("'").append(vo.getCap()).append("',");
							sbuffer.append("'").append(vo.getStorageindex()).append("',");
//							sbuffer.append("'").append(time).append("'");
							if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("'").append(time).append("'");
							}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
							}
							sbuffer.append(endsql);
							list.add(sbuffer.toString());
							vo=null;
							sbuffer=null;
							
						}
						
						GathersqlListManager.AdddateTempsql(deleteSql, list);
						list=null;

					}
					storageVector=null;
					
				}
			
			
			

			
		}
	}

	
	

}
