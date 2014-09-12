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
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.gatherdb.GathersqlListManager;

public class NetHostMemoryRtsql {
	
	
	/**
	 * 把结果生成sql
	 * @param dataresult 采集结果
	 * @param node 网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node,String Subentity) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {//是否启动分离模式

			if(dataresult != null && dataresult.size()>0){
				
			
				String hendsql="insert into nms_memory_data_temp(nodeid,ip,`type`,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values(";
				String endsql=")";
				
				
				//memory
				Vector memvector=(Vector) dataresult.get("memory");
			    if(null!=memvector && memvector.size()>0)
			    {
			    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    	NodeUtil nodeUtil = new NodeUtil();			
		 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
					String deleteSql = "delete from nms_memory_data_temp where nodeid='" +node.getId() + "'";
				
					
					if(memvector != null && memvector.size()>0){
						
						Vector list=new Vector();
						String tag="";
						
						for(int i=0;i<memvector.size();i++){
							Memorycollectdata vo = (Memorycollectdata) memvector.elementAt(i);
							tag=vo.getSubentity();
							
							
							if(vo.getSubentity().trim().equals(Subentity))
							{
//							if(vo.getSubentity().trim().equals("PhysicalMemory"))
//							{
//								tag="PhysicalMemory";
//							}
//							
//							if(vo.getSubentity().trim().equals("VirtualMemory"))
//							{
//								
//								tag="VirtualMemory";
//							}
//							
//							
//							if(vo.getSubentity().trim().equals("SwapMemory"))
//							{
//								
//								tag="SwapMemory";
//							}
							
							
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
//							sbuffer.append("'").append(time).append("',");
							if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("'").append(time).append("',");
							}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
							}
							sbuffer.append("'").append(vo.getUnit()).append("',");
							sbuffer.append("'").append(vo.getBak()).append("'");
							sbuffer.append(endsql);
							
							list.add(sbuffer.toString());
							//System.out.println(sbuffer.toString());
							sbuffer=null;
							vo=null;
							}
							
						}
						
						deleteSql=deleteSql+" and sindex='"+Subentity+"'";
						
						  System.out.println("=====&&&***&&&&======"+deleteSql);
						 GathersqlListManager.AdddateTempsql(deleteSql, list);
						 list=null;
					}
			    }
			    hendsql=null;
			    endsql=null;
				
			}
			
			
		}
	}

	
	/**
	 * 把结果生成sql
	 * @param dataresult 采集结果
	 * @param node 网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {//是否启动分离模式

			if(dataresult != null && dataresult.size()>0){
				
			
				String hendsql="insert into nms_memory_data_temp(nodeid,ip,`type`,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values(";
				String endsql=")";
				
				
				//memory
				Vector memvector=(Vector) dataresult.get("memory");
			    if(null!=memvector && memvector.size()>0)
			    {
			    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    	NodeUtil nodeUtil = new NodeUtil();			
		 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
					String deleteSql = "delete from nms_memory_data_temp where nodeid='" +node.getId() + "'";
				
					
					if(memvector != null && memvector.size()>0){
						
						Vector list=new Vector();
						String tag="";
						
						for(int i=0;i<memvector.size();i++){
							Memorycollectdata vo = (Memorycollectdata) memvector.elementAt(i);
							tag=vo.getSubentity();

							
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
//							sbuffer.append("'").append(time).append("',");
							if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("'").append(time).append("',");
							}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
							}
							sbuffer.append("'").append(vo.getUnit()).append("',");
							sbuffer.append("'").append(vo.getBak()).append("'");
							sbuffer.append(endsql);
							
							list.add(sbuffer.toString());
							//System.out.println(sbuffer.toString());
							sbuffer=null;
							vo=null;
							}
							
						
						
						//deleteSql=deleteSql+" and sindex='"+Subentity+"'";
						 GathersqlListManager.AdddateTempsql(deleteSql, list);
						 list=null;
					}
			    }
			    hendsql=null;
			    endsql=null;
				
			}
			
			
		}
	}

	
	
	

}
