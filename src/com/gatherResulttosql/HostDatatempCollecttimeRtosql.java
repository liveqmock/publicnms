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
import com.gatherdb.GathersqlListManager;

public class HostDatatempCollecttimeRtosql {

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
			String collecttime = (String) dataresult.get("collecttime");

			if (null != collecttime) {
				String deleteSql = "delete from nms_other_data_temp where nodeid='"
						+ node.getId() + "' and entity = 'collecttime'";
				Vector list = new Vector();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					StringBuffer sql = new StringBuffer(500);
					sql
							.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,thevalue,collecttime)values('");
					sql.append(nodeDTO.getId());
					sql.append("','");
					sql.append(node.getIpAddress());
					sql.append("','");
					sql.append(nodeDTO.getType());// type
					sql.append("','");
					sql.append(nodeDTO.getSubtype());// subtype
					sql.append("','collecttime','");// entity
					sql.append(collecttime);// thevalue数据采集时间
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						sql.append("','");
						sql.append(time);// collecttime数据保存时间
						sql.append("')");
					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
						sql.append("',");
						sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");// collecttime数据保存时间
						sql.append(")");
					}
					
					list.add(sql.toString());
					sql = null;

				} catch (Exception e) {
					e.printStackTrace();
				}

				GathersqlListManager.AdddateTempsql(deleteSql, list);
				list = null;

			}

		}

	}

}
