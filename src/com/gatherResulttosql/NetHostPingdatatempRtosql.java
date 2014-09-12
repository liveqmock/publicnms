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
import com.afunms.polling.om.Pingcollectdata;
import com.gatherdb.GathersqlListManager;

public class NetHostPingdatatempRtosql {

	/**
	 * 把采集结果生成sql
	 * 
	 * @param dataresult
	 *            数据结果列表
	 * @param node
	 *            节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {

			if (dataresult != null && dataresult.size() > 0) {
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = null;
				nodeDTO = nodeUtil.creatNodeDTOByNode(node);

				Vector pingVector = new Vector();

				nodeDTO = nodeUtil.creatNodeDTOByNode(node);
				pingVector = (Vector) dataresult.get("ping");
				if (pingVector != null && pingVector.size() > 0) {

					String deleteSql = "delete from nms_ping_data_temp where nodeid='"
							+ node.getId() + "'";
					String hendsql = "insert into nms_ping_data_temp"
							+ "(nodeid,ip,`type`,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak) "
							+ "values(";
					String endsql = ")";

					
					Vector list = new Vector();
					// dbmanager.addBatch(deleteSql);
					for (int i = 0; i < pingVector.size(); i++) {
						Pingcollectdata vo = (Pingcollectdata) pingVector
								.elementAt(i);
						try {

							Calendar tempCal = (Calendar) vo.getCollecttime();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);

							StringBuffer sbuffer = new StringBuffer();
							sbuffer.append(hendsql);
							sbuffer.append("'").append(node.getId()).append(
									"',");
							sbuffer.append("'").append(node.getIpAddress())
									.append("',");
							sbuffer.append("'").append(nodeDTO.getType()).append(
									"',");
							sbuffer.append("'").append(nodeDTO.getSubtype())
									.append("',");
							sbuffer.append("'").append(vo.getCategory())
									.append("',");
							sbuffer.append("'").append(vo.getEntity()).append(
									"',");
							sbuffer.append("'").append(vo.getSubentity())
									.append("',");
							sbuffer.append("'").append(vo.getThevalue())
									.append("',");
							sbuffer.append("'").append(vo.getChname()).append(
									"',");
							sbuffer.append("'").append(vo.getRestype()).append(
									"',");
//							sbuffer.append("'").append(time).append("',");
							if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("'").append(time).append("',");
							}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
							}
							sbuffer.append("'").append(vo.getUnit()).append(
									"',");
							sbuffer.append("'").append(vo.getBak()).append("'");
							sbuffer.append(endsql);
							list.add(sbuffer.toString());
							sbuffer=null;

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					GathersqlListManager.AdddateTempsql(deleteSql, list);
					list=null;
					deleteSql=null;
					endsql=null;
					
				}

			}
		}
	}

}
