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
import com.afunms.polling.om.Usercollectdata;
import com.gatherdb.GathersqlListManager;


/**
 * 
 * linux 用户信息转换成sql
 * 
 * @author konglq
 *
 */
public class HostDatatempUserRtosql {
	
	
	/**
	 * 把结果生成sql
	 * 
	 * @param dataresult
	 *            采集结果
	 * @param node
	 *            网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {// 是否启动分离模式

			        
			        Vector userVector=(Vector)dataresult.get("user");
					
					//dbmanager.addBatch(deleteSql);
					if(userVector != null && userVector.size()>0){
						
						String ip = (String)node.getIpAddress();
	    										
	    				NodeUtil nodeUtil = new NodeUtil();
	    				NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
						String deleteSql = "delete from nms_user_data_temp where nodeid='" +node.getId() + "'";
						
						SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
						Calendar tempCal = Calendar.getInstance();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						Vector list = new Vector();
						
						
						for(int i=0;i<userVector.size();i++){
							Usercollectdata vo = (Usercollectdata) userVector.elementAt(i);		
							try {									
//								if(vo.getName() == null)vo.setName("");
								StringBuffer sql = new StringBuffer(200);
							    sql.append("insert into nms_user_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
							    sql.append(nodeDTO.getId());
							    sql.append("','");
							    sql.append(ip);
							    sql.append("','");
							    sql.append(nodeDTO.getType());
							    sql.append("','");
							    sql.append(nodeDTO.getSubtype());
							    sql.append("','");
							    sql.append(vo.getCategory());//entity
							    sql.append("','");
							    sql.append(vo.getEntity());//subentity
							    sql.append("','");
							    sql.append(vo.getSubentity().trim());//sindex
							    sql.append("','");
							    sql.append(vo.getThevalue());//thevalue
							    sql.append("','");
							    sql.append(vo.getChname());
							    sql.append("','");
							    sql.append(vo.getRestype());
							    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							    	sql.append("','");
								    sql.append(time);
								    sql.append("','");
							    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							    	sql.append("',");
								    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
								    sql.append(",'");
							    }
							    
							    sql.append(vo.getUnit());
							    sql.append("','");
							    sql.append(vo.getBak());
							    sql.append("')");
//							    SysLogger.info(sql.toString());
							    //dbmanager.addBatch(sql.toString());	
							    list.add(sql.toString());
							    sql=null;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						GathersqlListManager.AdddateTempsql(deleteSql, list);
						list=null;
						time=null;
						cc=null;
						tempCal=null;
						nodeDTO=null;
						nodeUtil=null;
						userVector=null;
						
					}
				}
		
	}

}
