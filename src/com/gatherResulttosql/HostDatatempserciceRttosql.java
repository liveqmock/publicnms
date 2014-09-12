package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Servicecollectdata;
import com.afunms.temp.model.ServiceNodeTemp;
import com.gatherdb.GathersqlListManager;

public class HostDatatempserciceRttosql {
	
	
	
	/**
	 * 把结果生成sql
	 * @param dataresult 采集结果
	 * @param node 网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {//是否启动分离模式

			// 处理服务信息入库
			if(dataresult != null && dataresult.size()>0){
				
				
				String hendsql="insert into nms_sercice_data_temp(nodeid,ip,`type`,subtype,name,instate,opstate,paused,uninst,collecttime) values(";
				String endsql=")";
				
				
			
				    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    NodeUtil nodeUtil = new NodeUtil();					
		 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
					Vector serviceVector = (Vector)dataresult.get("winservice");
				
					String deleteSql = "delete from nms_sercice_data_temp where nodeid='" +node.getId() + "'";

					
					if(serviceVector != null && serviceVector.size()>0){
						Calendar tempCal=Calendar.getInstance();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						Vector list=new Vector();
						
						
						for(int i=0;i<serviceVector.size();i++){
							Servicecollectdata vo = (Servicecollectdata) serviceVector.elementAt(i);								
							
							try {
							
							
							StringBuffer sbuffer = new StringBuffer(200);
							sbuffer.append(hendsql);
							sbuffer.append("'").append(node.getId()).append("',");
							sbuffer.append("'").append(node.getIpAddress()).append("',");
							sbuffer.append("'").append(nodeDTO.getType()).append("',");
							sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
							sbuffer.append("'").append(new String(vo.getName().getBytes(),"UTF-8")).append("',");
							sbuffer.append("'").append(vo.getInstate()).append("',");
							sbuffer.append("'").append(vo.getOpstate()).append("',");
							sbuffer.append("'").append(vo.getPaused()).append("',");
							sbuffer.append("'").append(vo.getUninst()).append("',");
							if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("'").append(time).append("'");
							}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
							}
							
							sbuffer.append(endsql);
							list.add(sbuffer.toString());	
							sbuffer=null;
							vo=null;
							
						}catch(Exception e)
						{
							
						}
						}
						
						GathersqlListManager.AdddateTempsql(deleteSql, list);
						list=null;
					}
					
					serviceVector=null;
					nodeDTO=null;
					
				}
				
			}
			

			
		}
	
	
	

	/**
	 * 把结果生成sql
	 * @param dataresult 采集结果
	 * @param node 网元节点
	 */
	public void CreateResultLinuxTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {//是否启动分离模式
			
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    NodeUtil nodeUtil = new NodeUtil();					
	 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
		 			
					List serviceList = (ArrayList)dataresult.get("servicelist");
					String deleteSql = "delete from nms_sercice_data_temp where nodeid='" +node.getId() + "'";
					
					
					if(serviceList != null && serviceList.size()>0){
						Calendar tempCal=Calendar.getInstance();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						
						Vector list=new Vector();
						
						for(int i=0;i<serviceList.size();i++){
							Hashtable serviceItemHash = (Hashtable) serviceList.get(i);	
							Enumeration tempEnumeration2 = serviceItemHash.keys();
							ServiceNodeTemp serviceNodeTemp = null;
							serviceNodeTemp = getServiceNodeTempByHashtable(serviceItemHash);
							if(serviceNodeTemp.getPathName() != null && serviceNodeTemp.getPathName().trim().length()>0){
								serviceNodeTemp.setPathName(serviceNodeTemp.getPathName().replaceAll("\"", ""));
							}
							try {									
								StringBuffer sql = new StringBuffer(200);
							    sql.append("insert into nms_sercice_data_temp(nodeid,ip,type,subtype,name,instate,opstate,paused,uninst,collecttime,startMode,pathName,description,serviceType,pid,groupstr)values('");
							    sql.append(nodeDTO.getId());
							    sql.append("','");
							    sql.append(node.getIpAddress());
							    sql.append("','");
							    sql.append(nodeDTO.getType());//type
							    sql.append("','");
							    sql.append(nodeDTO.getSubtype());//subtype
							    sql.append("','");
							    sql.append(serviceNodeTemp.getName());//name
							    sql.append("','");
							    sql.append(serviceNodeTemp.getInstate());//instate
							    sql.append("','");
							    sql.append(serviceNodeTemp.getOpstate());//opstate
							    sql.append("','");
							    sql.append(serviceNodeTemp.getPaused());//paused
							    sql.append("','");
							    sql.append(serviceNodeTemp.getUninst());//uninst
							    sql.append("','");
							    sql.append(time);//collecttime
							    sql.append("','");
							    sql.append(serviceNodeTemp.getStartMode());//startMode
							    sql.append("',\"");
							    sql.append(serviceNodeTemp.getPathName());//pathName
							    sql.append("\",'");
							    sql.append(serviceNodeTemp.getDescription());//description
							    sql.append("','");
							    sql.append(serviceNodeTemp.getServiceType());//serviceType
							    sql.append("','");
							    sql.append(serviceNodeTemp.getPid());//pid
							    sql.append("','");
							    sql.append(serviceNodeTemp.getGroupstr());//groupstr 
							    sql.append("')");
							    
							    list.add(sql.toString());
							    sql=null;
							    serviceNodeTemp=null;
							    
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						
						GathersqlListManager.AdddateTempsql(deleteSql, list);
						list=null;
						
					}
				}
			}
	
	
	
	/**
	 * 根据service的Hashtable得到ServiceNodeTemp对象
	 * @param serviceItemHash
	 * @return
	 */
	public ServiceNodeTemp getServiceNodeTempByHashtable(
				Hashtable serviceItemHash) {
		ServiceNodeTemp serviceNodeTemp = new ServiceNodeTemp();
		Iterator iterator = serviceItemHash.keySet().iterator();
		while(iterator.hasNext()){
			String key = String.valueOf(iterator.next());
			String value = String.valueOf(serviceItemHash.get(key));
			if(value != null && value.indexOf("\\") != -1){
				value = value.replaceAll("\\\\", "/");
			}
			if("DisplayName".equalsIgnoreCase(key) || "name".equalsIgnoreCase(key)){
				serviceNodeTemp.setName(value);
			}
			if("instate".equalsIgnoreCase(key) || "State".equalsIgnoreCase(key) || "status".equalsIgnoreCase(key)){
				serviceNodeTemp.setInstate(value);
			}
			if("opstate".equalsIgnoreCase(key)){
				serviceNodeTemp.setOpstate(value);
			}
			if("uninst".equalsIgnoreCase(key)){
				serviceNodeTemp.setUninst(value);
			}
			if("paused".equalsIgnoreCase(key)){
				serviceNodeTemp.setPaused(value);
			}
			if("StartMode".equalsIgnoreCase(key)){
				serviceNodeTemp.setStartMode(value);
			}
			if("PathName".equalsIgnoreCase(key)){
				serviceNodeTemp.setPathName(value);
			}
			if("Description".equalsIgnoreCase(key)){
				serviceNodeTemp.setDescription(value);
			}
			if("ServiceType".equalsIgnoreCase(key)){
				serviceNodeTemp.setServiceType(value);
			}
			if("pid".equalsIgnoreCase(key)){
				serviceNodeTemp.setPid(value);
			}
			if("groupstr".equalsIgnoreCase(key) || "group".equalsIgnoreCase(key)){
				serviceNodeTemp.setGroupstr(value);
			}
		}
		return serviceNodeTemp;
	}

	
	
	
	}

	

