package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Diskcollectdata;
import com.gatherdb.GathersqlListManager;

public class HostDatatempDiskRttosql {
	

	/**
	 * 把结果生成sql
	 * @param dataresult 采集结果
	 * @param node 网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {//是否启动分离模式

			if(dataresult != null && dataresult.size()>0){
				
				//Enumeration diskhash = alldiskhash.keys();
				String hendsql="insert into nms_disk_data_temp(nodeid,ip,`type`,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values(";
				String endsql=")";
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				NodeUtil nodeUtil = new NodeUtil();
		 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
					Vector diskVector = (Vector)dataresult.get("disk");
					String deleteSql = "delete from nms_disk_data_temp where nodeid='" +node.getId() + "'";
					if(diskVector != null && diskVector.size()>0){
						Vector list=new Vector();
						for(int i=0;i<diskVector.size();i++){
							Diskcollectdata vo = (Diskcollectdata) diskVector.elementAt(i);
						
							Calendar tempCal = (Calendar) vo.getCollecttime();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);								
								
							
							StringBuffer sbuffer = new StringBuffer(200);
							sbuffer.append(hendsql);
							sbuffer.append("'").append(node.getId()).append("',");
							sbuffer.append("'").append(node.getIpAddress()).append("',");
							sbuffer.append("'").append(nodeDTO.getType()).append("',");
							sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
							sbuffer.append("'").append(vo.getCategory()).append("',");
							sbuffer.append("'").append(vo.getEntity()).append("',");
							sbuffer.append("'").append(vo.getSubentity()).append("',");
							sbuffer.append("'").append(vo.getThevalue()).append("',");
							sbuffer.append("'").append(vo.getChname()).append("',");
							sbuffer.append("'").append(vo.getRestype()).append("',");
							if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("'").append(time).append("',");
							}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
							}
							
							sbuffer.append("'").append(vo.getUnit()).append("',");
							sbuffer.append("'").append(vo.getBak()).append("'");
							sbuffer.append(endsql);
							list.add(sbuffer.toString());
							sbuffer=null;
							vo=null;
							//time=null;
							///tempCal=null;
							//cc=null;
    
						}
						GathersqlListManager.AdddateTempsql(deleteSql, list);
					}
					
					diskVector=null;
					
					
				}
				
			

			
		}
	}

	
	
	

}
