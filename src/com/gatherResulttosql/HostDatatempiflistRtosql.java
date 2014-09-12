package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.gatherdb.GathersqlListManager;

public class HostDatatempiflistRtosql {
	
	
	/**
	 * 把结果生成sql
	 * @param dataresult 采集结果
	 * @param node 网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {//是否启动分离模式

			
					String ip = node.getIpAddress();
					NodeUtil nodeUtil = new NodeUtil();		
			 		NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
			 			
			 			
					List ifList = (ArrayList)dataresult.get("iflist");
					String deleteSql = "delete from nms_other_data_temp where nodeid='" +node.getId() + "' and entity = 'iflist'";
					Vector list=new Vector();
					
					if(ifList != null && ifList.size()>0){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Calendar tempCal=Calendar.getInstance();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						
						for(int i=0;i<ifList.size();i++){
							Hashtable ifItemHash = (Hashtable) ifList.get(i);	
							Enumeration tempEnumeration2 = ifItemHash.keys();
							while(tempEnumeration2.hasMoreElements()){
								String key = (String)tempEnumeration2.nextElement();
								String value = (String)ifItemHash.get(key);
								try {									
									StringBuffer sql = new StringBuffer(200);
								    sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,collecttime)values('");
								    sql.append(nodeDTO.getId());
								    sql.append("','");
								    sql.append(ip);
								    sql.append("','");
								    sql.append(nodeDTO.getType());//type
								    sql.append("','");
								    sql.append(nodeDTO.getSubtype());//subtype
								    sql.append("','");
								    sql.append("iflist");//entity
								    sql.append("','");
								    sql.append(key);//subentity
								    sql.append("','");
								    sql.append(i+1);//sindex
								    sql.append("','");
								    sql.append(value);//thevalue
								    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								    	sql.append("','");
									    sql.append(time);//collecttime
									    sql.append("')");
								    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								    	sql.append("',");
									    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");//collecttime
									    sql.append(")");
								    }
								    
								    
								    list.add(sql.toString());
								    sql=null;
								    
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					
					 GathersqlListManager.AdddateTempsql(deleteSql, list);
					 list=null;
					 ifList=null;
					
				}
			}
		
			

			
	

}
