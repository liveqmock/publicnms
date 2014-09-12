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
import com.afunms.polling.om.IpRouter;
import com.gatherdb.GathersqlListManager;

public class NetDatatempRouterRtosql {
	
	private static String[] iproutertype={"","","","direct(3)","indirect(4)"};
	private static String[] iprouterproto={"","other(1)","local(2)","netmgmt(3)","icmp(4)","egp(5)","ggp(6)","hello(7)","rip(8)","is-is(9)","es-is(10)","ciscoIgrp(11)","bbnSpfIgp(12)","ospf(13)","bgp(14)"};
	
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

			//处理路由信息
			if(dataresult != null && dataresult.size()>0){
				//Enumeration routerhash = allrouterhash.keys();
				NodeDTO nodeDTO = null;
				String ip = null;
				IpRouter iprouter = null;
				Calendar tempCal = null;
				Date cc = null;
				String time = null;
				Vector routerVector = null;
	    		

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				NodeUtil nodeUtil = new NodeUtil();
					
		 			nodeDTO = nodeUtil.creatNodeDTOByNode(node);
		 			routerVector = (Vector)dataresult.get("iprouter");
		 			if (routerVector != null && routerVector.size() > 0) {
		 			//删除原来的数据
					String deleteSql = "delete from nms_route_data_temp where nodeid='" +node.getId() + "'";
					//dbmanager.addBatch(deleteSql);
					 Vector list=new Vector();
				
						
						StringBuffer sql = null;
						for(int i=0;i<routerVector.size();i++){
							iprouter = (IpRouter) routerVector.elementAt(i);
							tempCal = (Calendar) iprouter.getCollecttime();
							cc = tempCal.getTime();
							time = sdf.format(cc);
							try {
							    sql = new StringBuffer(500);
							    sql.append("insert into nms_route_data_temp(nodeid,ip,type,subtype,ifindex,nexthop,proto,rtype,mask,collecttime,physaddress,dest)values('");
							    sql.append(node.getId());
							    sql.append("','");
							    sql.append(node.getIpAddress());
							    sql.append("','");
							    sql.append(nodeDTO.getType());
							    sql.append("','");
							    sql.append(nodeDTO.getSubtype());
							    sql.append("','");
							    sql.append(iprouter.getIfindex());
							    sql.append("','");
							    sql.append(iprouter.getNexthop());
							    sql.append("','");
							    sql.append(iprouterproto[Integer.parseInt(iprouter.getProto().longValue()+"")]);
							    sql.append("','");
							    sql.append(iproutertype[Integer.parseInt(iprouter.getType().longValue()+"")]);
							    sql.append("','");
							    sql.append(iprouter.getMask());
							    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							    	sql.append("','");
								    sql.append(time);
								    sql.append("','");
							    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							    	sql.append("',");
								    sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
								    sql.append(",'");
							    }
							    
							    sql.append(iprouter.getPhysaddress());
							    sql.append("','");
							    sql.append(iprouter.getDest());
							    sql.append("')");
							    list.add(sql.toString());
							   // dbmanager.addBatch(sql.toString());
							    sql = null;
							    
							} catch (Exception e1) {
								e1.printStackTrace();
							}
//					   
						}
						
						GathersqlListManager.AdddateTempsql(deleteSql, list);
					
		 			}
		 			
					routerVector = null;
					iprouter = null;
					
					
				}

		}
	}

}
