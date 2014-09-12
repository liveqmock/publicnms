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
import com.afunms.polling.om.Devicecollectdata;
import com.gatherdb.GathersqlListManager;

public class HostDatatempDeviceRttosql {

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

			// 处理硬件信息入库
			if (dataresult != null && dataresult.size() > 0) {

				String hendsql = "insert into nms_device_data_temp(nodeid,ip,`type`,subtype,name,deviceindex,dtype,status,collecttime)values(";
				String endsql = ")";

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
				Vector hardwareVector = (Vector) dataresult.get("device");
				String deleteSql = "delete from nms_device_data_temp where nodeid='"
						+ node.getId() + "'";
				if (hardwareVector != null && hardwareVector.size() > 0) {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					Vector list = new Vector();
					Devicecollectdata vo = null;
					for (int i = 0; i < hardwareVector.size(); i++) {
						vo = (Devicecollectdata) hardwareVector.elementAt(i);
						String name = vo.getName();
						if (name != null) {
							name = name.replaceAll("\\\\", "/").replaceAll("'",
									"");
						}

						StringBuffer sbuffer = new StringBuffer(200);
						sbuffer.append(hendsql);
						sbuffer.append("'").append(node.getId()).append("',");
						sbuffer.append("'").append(node.getIpAddress()).append(
								"',");
						sbuffer.append("'").append(nodeDTO.getType()).append("',");
						sbuffer.append("'").append(nodeDTO.getSubtype())
								.append("',");
						sbuffer.append("'").append(name).append("',");
						sbuffer.append("'").append(vo.getDeviceindex()).append(
								"',");
						sbuffer.append("'").append(vo.getType()).append("',");
						sbuffer.append("'").append(vo.getStatus()).append("',");
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							sbuffer.append("'").append(time).append("'");
						}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
						}
						
						
						sbuffer.append(endsql);
						list.add(sbuffer.toString());
						sbuffer=null;
						//time=null;
						//vo=null;
					}
					GathersqlListManager.AdddateTempsql(deleteSql, list);
					list=null;
				}
				
				hardwareVector=null;

			}

		}
	}

}
