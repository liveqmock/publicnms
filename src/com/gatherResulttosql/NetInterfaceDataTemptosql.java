package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SystemConstant;
import com.afunms.discovery.Node;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.DiscardsPerc;
import com.afunms.polling.om.ErrorsPerc;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.Packs;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.topology.model.HostNode;
import com.gatherdb.GathersqlListManager;


public class NetInterfaceDataTemptosql {
	
	
	
	public void CreateResultTosql(Hashtable interfacehash,Host node)
	{
	
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodeDTO = null;
		nodeDTO = nodeUtil.creatNodeDTOByNode(node);
		Vector list =new Vector();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String deleteSql = "delete from nms_interface_data_temp where nodeid='" +node.getId() + "'";
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_interface_data_temp");
		sql.append("(nodeid,ip,`type`,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak) ");
		sql.append("values(");
		String hendsql=sql.toString();
		String endsql=")";
		
		Calendar tempCal = null;
		Date cc = null;
		String time = null;
		sql = null;

			
			// 开始设置综合流速
			if (interfacehash.get("allutilhdx") != null && ((Vector<AllUtilHdx>)interfacehash.get("allutilhdx")).size() > 0) {
				AllUtilHdx allutilhdx = null;
				try{ 
					for (int i = 0; i < ((Vector<AllUtilHdx>)interfacehash.get("allutilhdx")).size(); i++) {
							allutilhdx = (AllUtilHdx) ((Vector<AllUtilHdx>)interfacehash.get("allutilhdx")).elementAt(i);
							tempCal = (Calendar) allutilhdx.getCollecttime();
							cc = tempCal.getTime();
							time = sdf.format(cc);
							// list = new ArrayList();
							 sql = new StringBuffer(200);
							sql.append(hendsql);
							sql.append("'").append(node.getId()).append("',");
							sql.append("'").append(node.getIpAddress()).append("',");
							sql.append("'").append(nodeDTO.getType()).append("',");
							sql.append("'").append(nodeDTO.getSubtype()).append("',");
							sql.append("'").append(allutilhdx.getCategory()).append("',");
							sql.append("'").append(allutilhdx.getEntity()).append("',");
							sql.append("'").append(allutilhdx.getSubentity()).append("',");
							sql.append("'").append(allutilhdx.getThevalue()).append("',");
							sql.append("'").append(allutilhdx.getChname()).append("',");
							sql.append("'").append(allutilhdx.getRestype()).append("',");
//							sql.append("'").append(time).append("',");
							if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								sql.append("'").append(time).append("',");
							}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
							}
							sql.append("'").append(allutilhdx.getUnit()).append("',");
							sql.append("'").append(allutilhdx.getBak()).append("'");
							sql.append(endsql);
							list.add(sql.toString());
							sql=null;
							allutilhdx=null;
							time=null;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			// 结束设置综合流速
			
			// 开始设置带宽利用率
			// Vector utilhdxpercVector = (Vector)
			// interfacehash.get("utilhdxperc");
			if (interfacehash.get("utilhdxperc") != null && ((Vector) interfacehash.get("utilhdxperc")).size() > 0) {
				UtilHdxPerc utilhdxperc = null;
				try{
					for (int i = 0; i < ((Vector) interfacehash.get("utilhdxperc")).size(); i++) {
							utilhdxperc = (UtilHdxPerc) ((Vector) interfacehash.get("utilhdxperc")).elementAt(i);
							tempCal = (Calendar) utilhdxperc.getCollecttime();
							cc = tempCal.getTime();
							time = sdf.format(cc);							
							 sql = new StringBuffer(200);
							sql.append(hendsql);
								sql.append("'").append(node.getId()).append("',");
								sql.append("'").append(node.getIpAddress()).append("',");
								sql.append("'").append(nodeDTO.getType()).append("',");
								sql.append("'").append(nodeDTO.getSubtype()).append("',");
								sql.append("'").append(utilhdxperc.getCategory()).append("',");
								sql.append("'").append(utilhdxperc.getEntity()).append("',");
								sql.append("'").append(utilhdxperc.getSubentity()).append("',");
								sql.append("'").append(utilhdxperc.getThevalue()).append("',");
								sql.append("'").append(utilhdxperc.getChname()).append("',");
								sql.append("'").append(utilhdxperc.getRestype()).append("',");
//								sql.append("'").append(time).append("',");
								if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
									sql.append("'").append(time).append("',");
								}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
									sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
								}
								sql.append("'").append(utilhdxperc.getUnit()).append("',");
								sql.append("'").append(utilhdxperc.getBak()).append("'");
								sql.append(endsql);
								list.add(sql.toString());
								sql=null;
								utilhdxperc=null;
								
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			// 结束设置带宽利用率
			
			// 开始设置流速
			// Vector utilhdxVector = (Vector) interfacehash.get("utilhdx");
			if (interfacehash.get("utilhdx") != null && ((Vector)interfacehash.get("utilhdx")).size() > 0) {
				try{
					UtilHdx utilhdx = null;
					for (int i = 0; i < ((Vector) interfacehash.get("utilhdx")).size(); i++) {
							utilhdx = (UtilHdx) ((Vector) interfacehash.get("utilhdx")).elementAt(i);
							tempCal = (Calendar) utilhdx.getCollecttime();
							cc = tempCal.getTime();
							time = sdf.format(cc);
							
							 sql = new StringBuffer(200);
								sql.append(hendsql);
									sql.append("'").append(node.getId()).append("',");
									sql.append("'").append(node.getIpAddress()).append("',");
									sql.append("'").append(nodeDTO.getType()).append("',");
									sql.append("'").append(nodeDTO.getSubtype()).append("',");
									sql.append("'").append(utilhdx.getCategory()).append("',");
									sql.append("'").append(utilhdx.getEntity()).append("',");
									sql.append("'").append(utilhdx.getSubentity()).append("',");
									sql.append("'").append(utilhdx.getThevalue()).append("',");
									sql.append("'").append(utilhdx.getChname()).append("',");
									sql.append("'").append(utilhdx.getRestype()).append("',");
//									sql.append("'").append(time).append("',");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sql.append("'").append(time).append("',");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
									}
									sql.append("'").append(utilhdx.getUnit()).append("',");
									sql.append("'").append(utilhdx.getBak()).append("'");
									sql.append(endsql);
									list.add(sql.toString());
									sql=null;
									utilhdx=null;
					}
					
				}catch(Exception e){	
					e.printStackTrace();
				}
			}
			// 结束设置流速
			
			// 开始设置流速
			// Vector interfaceVector = (Vector) interfacehash.get("interface");
			if ((Vector) interfacehash.get("interface") != null && ((Vector) interfacehash.get("interface")).size() > 0) {
				try{
					Interfacecollectdata interfacedata = null;
					for (int i = 0; i < ((Vector) interfacehash.get("interface")).size(); i++) {
							interfacedata = (Interfacecollectdata) ((Vector) interfacehash.get("interface")).elementAt(i);
							tempCal = (Calendar) interfacedata.getCollecttime();
							cc = tempCal.getTime();
							time = sdf.format(cc);							
							 sql = new StringBuffer(200);
								sql.append(hendsql);
									sql.append("'").append(node.getId()).append("',");
									sql.append("'").append(node.getIpAddress()).append("',");
									sql.append("'").append(nodeDTO.getType()).append("',");
									sql.append("'").append(nodeDTO.getSubtype()).append("',");
									sql.append("'").append(interfacedata.getCategory()).append("',");
									sql.append("'").append(interfacedata.getEntity()).append("',");
									sql.append("'").append(interfacedata.getSubentity()).append("',");
									sql.append("'").append(interfacedata.getThevalue()).append("',");
									sql.append("'").append(interfacedata.getChname()).append("',");
									sql.append("'").append(interfacedata.getRestype()).append("',");
//									sql.append("'").append(time).append("',");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sql.append("'").append(time).append("',");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
									}
									sql.append("'").append(interfacedata.getUnit()).append("',");
									sql.append("'").append(interfacedata.getBak()).append("'");
									sql.append(endsql);
									list.add(sql.toString());
									sql=null;
									interfacedata=null;
					}
					
				}catch(Exception e){	
					e.printStackTrace();
				}
			}
			// 结束设置流速
			
			// 开始设置丢包率
			// Vector discardspercVector = (Vector)
			// interfacehash.get("discardsperc");
			if ((Vector) interfacehash.get("discardsperc") != null && ((Vector) interfacehash.get("discardsperc")).size() > 0) {
				try{
					DiscardsPerc discardsperc = null;
					for (int i = 0; i < ((Vector) interfacehash.get("discardsperc")).size(); i++) {
							discardsperc = (DiscardsPerc) ((Vector) interfacehash.get("discardsperc")).elementAt(i);
							tempCal = (Calendar) discardsperc.getCollecttime();
							cc = tempCal.getTime();
							time = sdf.format(cc);

							 sql = new StringBuffer(200);
								sql.append(hendsql);
									sql.append("'").append(node.getId()).append("',");
									sql.append("'").append(node.getIpAddress()).append("',");
									sql.append("'").append(nodeDTO.getType()).append("',");
									sql.append("'").append(nodeDTO.getSubtype()).append("',");
									sql.append("'").append(discardsperc.getCategory()).append("',");
									sql.append("'").append(discardsperc.getEntity()).append("',");
									sql.append("'").append(discardsperc.getSubentity()).append("',");
									sql.append("'").append(discardsperc.getThevalue()).append("',");
									sql.append("'").append(discardsperc.getChname()).append("',");
									sql.append("'").append(discardsperc.getRestype()).append("',");
//									sql.append("'").append(time).append("',");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sql.append("'").append(time).append("',");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
									}
									sql.append("'").append(discardsperc.getUnit()).append("',");
									sql.append("'").append(discardsperc.getBak()).append("'");
									sql.append(endsql);
									list.add(sql.toString());
									sql=null;
									discardsperc=null;

					}
				}catch(Exception e){	
					e.printStackTrace();
				}
			}
			// 结束设置丢包率
			
			// 开始设置错误率
			// Vector errorspercVector = (Vector)
			// interfacehash.get("errorsperc");
			if ((Vector) interfacehash.get("errorsperc") != null && ((Vector) interfacehash.get("errorsperc")).size() > 0) {
				try{
					ErrorsPerc errorsperc = null;
					for (int i = 0; i < ((Vector) interfacehash.get("errorsperc")).size(); i++) {
							errorsperc = (ErrorsPerc) ((Vector) interfacehash.get("errorsperc")).elementAt(i);
							tempCal = (Calendar) errorsperc.getCollecttime();
							cc = tempCal.getTime();
							time = sdf.format(cc);
							
							 sql = new StringBuffer(200);
								sql.append(hendsql);
									sql.append("'").append(node.getId()).append("',");
									sql.append("'").append(node.getIpAddress()).append("',");
									sql.append("'").append(nodeDTO.getType()).append("',");
									sql.append("'").append(nodeDTO.getSubtype()).append("',");
									sql.append("'").append(errorsperc.getCategory()).append("',");
									sql.append("'").append(errorsperc.getEntity()).append("',");
									sql.append("'").append(errorsperc.getSubentity()).append("',");
									sql.append("'").append(errorsperc.getThevalue()).append("',");
									sql.append("'").append(errorsperc.getChname()).append("',");
									sql.append("'").append(errorsperc.getRestype()).append("',");
//									sql.append("'").append(time).append("',");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sql.append("'").append(time).append("',");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
									}
									sql.append("'").append(errorsperc.getUnit()).append("',");
									sql.append("'").append(errorsperc.getBak()).append("'");
									sql.append(endsql);
									list.add(sql.toString());
									sql=null;
									errorsperc=null;
							
					}
				}catch(Exception e){	
					e.printStackTrace();
				}
			}
			// 结束设置错误率
			
			// 开始设置数据包
			// Vector packsVector = (Vector) interfacehash.get("packs");
			if ((Vector) interfacehash.get("packs") != null && ((Vector) interfacehash.get("packs")).size() > 0) {
				try{
					Packs packs = null;
					for (int i = 0; i < ((Vector) interfacehash.get("packs")).size(); i++) {
							packs = (Packs) ((Vector) interfacehash.get("packs")).elementAt(i);
							tempCal = (Calendar) packs.getCollecttime();
							cc = tempCal.getTime();
							time = sdf.format(cc);							
							 sql = new StringBuffer();
								sql.append(hendsql);
									sql.append("'").append(node.getId()).append("',");
									sql.append("'").append(node.getIpAddress()).append("',");
									sql.append("'").append(nodeDTO.getType()).append("',");
									sql.append("'").append(nodeDTO.getSubtype()).append("',");
									sql.append("'").append(packs.getCategory()).append("',");
									sql.append("'").append(packs.getEntity()).append("',");
									sql.append("'").append(packs.getSubentity()).append("',");
									sql.append("'").append(packs.getThevalue()).append("',");
									sql.append("'").append(packs.getChname()).append("',");
									sql.append("'").append(packs.getRestype()).append("',");
//									sql.append("'").append(time).append("',");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sql.append("'").append(time).append("',");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
									}
									sql.append("'").append(packs.getUnit()).append("',");
									sql.append("'").append(packs.getBak()).append("'");
									sql.append(endsql);
									list.add(sql.toString());
									sql=null;
									packs=null;
							
					}
				}catch(Exception e){	
					e.printStackTrace();
				}
			}
			// 结束设置数据包
			
			// 开始设置入口广播和多播数据包
			// Vector inpacksVector = (Vector) interfacehash.get("inpacks");
			if ((Vector) interfacehash.get("inpacks") != null && ((Vector) interfacehash.get("inpacks")).size() > 0) {
				try{
					InPkts packs = null;
					for (int i = 0; i < ((Vector) interfacehash.get("inpacks")).size(); i++) {
							packs = (InPkts) ((Vector) interfacehash.get("inpacks")).elementAt(i);
							tempCal = (Calendar) packs.getCollecttime();
							cc = tempCal.getTime();
							time = sdf.format(cc);
							sql = new StringBuffer(200);
							sql.append(hendsql);
								sql.append("'").append(node.getId()).append("',");
								sql.append("'").append(node.getIpAddress()).append("',");
								sql.append("'").append(nodeDTO.getType()).append("',");
								sql.append("'").append(nodeDTO.getSubtype()).append("',");
								sql.append("'").append(packs.getCategory()).append("',");
								sql.append("'").append(packs.getEntity()).append("',");
								sql.append("'").append(packs.getSubentity()).append("',");
								sql.append("'").append(packs.getThevalue()).append("',");
								sql.append("'").append(packs.getChname()).append("',");
								sql.append("'").append(packs.getRestype()).append("',");
//								sql.append("'").append(time).append("',");
								if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
									sql.append("'").append(time).append("',");
								}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
									sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
								}
								sql.append("'").append(packs.getUnit()).append("',");
								sql.append("'").append(packs.getBak()).append("'");
								sql.append(endsql);
								list.add(sql.toString());
								sql=null;

							packs = null;
					}
				}catch(Exception e){	
					e.printStackTrace();
				}
			}
			// 结束设置入口广播和多播数据包
			
			// 开始设置出口广播和多播数据包
			// Vector outpacksVector = (Vector) interfacehash.get("outpacks");
			if ((Vector) interfacehash.get("outpacks") != null && ((Vector) interfacehash.get("outpacks")).size() > 0) {
				try{
					OutPkts packs = null;
					for (int i = 0; i < ((Vector) interfacehash.get("outpacks")).size(); i++) {
							packs = (OutPkts) ((Vector) interfacehash.get("outpacks")).elementAt(i);
							tempCal = (Calendar) packs.getCollecttime();
							cc = tempCal.getTime();
							time = sdf.format(cc);
							sql = new StringBuffer(200);
							sql.append(hendsql);
								sql.append("'").append(node.getId()).append("',");
								sql.append("'").append(node.getIpAddress()).append("',");
								sql.append("'").append(nodeDTO.getType()).append("',");
								sql.append("'").append(nodeDTO.getSubtype()).append("',");
								sql.append("'").append(packs.getCategory()).append("',");
								sql.append("'").append(packs.getEntity()).append("',");
								sql.append("'").append(packs.getSubentity()).append("',");
								sql.append("'").append(packs.getThevalue()).append("',");
								sql.append("'").append(packs.getChname()).append("',");
								sql.append("'").append(packs.getRestype()).append("',");
//								sql.append("'").append(time).append("',");
								if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
									sql.append("'").append(time).append("',");
								}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
									sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
								}
								sql.append("'").append(packs.getUnit()).append("',");
								sql.append("'").append(packs.getBak()).append("'");
								sql.append(endsql);
								list.add(sql.toString());
								sql=null;
							    packs = null;
					}
				}catch(Exception e){	
					e.printStackTrace();
				}
			}
			// 结束设置出口广播和多播数据包
			//把sql放入到队里中
			GathersqlListManager.AdddateTempsql(deleteSql, list);
			list=null;
			
			
	}	
	

}
