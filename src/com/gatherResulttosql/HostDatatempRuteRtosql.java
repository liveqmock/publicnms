package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.gatherdb.GathersqlListManager;

public class HostDatatempRuteRtosql {

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

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);

			List routeList = (ArrayList) dataresult.get("routelist");
			String deleteSql = "delete from nms_route_data_temp where nodeid='"
					+ node.getId() + "'";

			if (routeList != null && routeList.size() > 0) {
				Calendar tempCal = Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);

				Vector list = new Vector();

				for (int i = 0; i < routeList.size(); i++) {
					try {
						//								if(vo.getName() == null)vo.setName("");
						String routeValue = String.valueOf(routeList.get(i));
						StringBuffer sql = new StringBuffer(200);
						sql
								.append("insert into nms_route_data_temp(nodeid,ip,type,subtype,ifindex,rtype,collecttime)values('");
						sql.append(nodeDTO.getId());
						sql.append("','");
						sql.append(node.getIpAddress());
						sql.append("','");
						sql.append(nodeDTO.getType());//type
						sql.append("','");
						sql.append(nodeDTO.getSubtype());//subtype
						sql.append("','");
						sql.append(i + 1);//ifindex
						sql.append("','");
						sql.append(routeValue);//rtype  注意：将路由信息存到rtype中
						
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
						sql = null;
						routeValue=null;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				GathersqlListManager.AdddateTempsql(deleteSql, list);
				 list=null;
				
			}
		}
	}

}
