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
import com.gatherdb.GathersqlListManager;

public class HostDatatempPageRtosql {
	
	

	/**
	 * 把结果生成sql
	 * @param dataresult 采集结果
	 * @param node 网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {//是否启动分离模式
			

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					NodeUtil nodeUtil = new NodeUtil();
			 		NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
					
		 			Hashtable pageHashtable = (Hashtable)dataresult.get("pagehash");
					String deleteSql = "delete from nms_other_data_temp where nodeid='" +node.getId() + "' and entity = 'pagehash'";
					Enumeration en = pageHashtable.keys();
					int sindex = 0;
					Vector list=new Vector();
					
					while(en.hasMoreElements()){
						sindex ++;
						String key = (String) en.nextElement();
						String value = (String) pageHashtable.get(key);
						Calendar tempCal=Calendar.getInstance();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						try {									
							StringBuffer sql = new StringBuffer(500);
							sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
						    sql.append(nodeDTO.getId());
						    sql.append("','");
						    sql.append(node.getIpAddress());
						    sql.append("','");
						    sql.append(nodeDTO.getType());
						    sql.append("','");
						    sql.append(nodeDTO.getSubtype());
						    sql.append("','pagehash','");//entity
						    sql.append(key);//subentity
						    sql.append("','");
						    sql.append(sindex);//sindex
						    sql.append("','");
						    sql.append(value);//thevalue
						    sql.append("','");
						    sql.append(key);//chname
						    sql.append("','static");//restype
//						    sql.append("','");
//						    sql.append(time);//collecttime
//						    sql.append("',' ',' ')");//unit
						    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						    	sql.append("','");
							    sql.append(time);//collecttime
							    sql.append("',' ',' ')");//unit
						    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
						    	sql.append("',");
							    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
							    sql.append(",' ',' ')");//unit
						    }
						    
						    list.add(sql.toString());
						    sql=null;
						    
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					 GathersqlListManager.AdddateTempsql(deleteSql, list);
					 list=null;
					
					
					
				}
			}

			
			
			
	
	

}
