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
import com.afunms.polling.om.Interfacecollectdata;
import com.gatherdb.GathersqlListManager;

public class NetDatatempfanRtosql {

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

			// 处理风扇信息入库
			if (dataresult != null && dataresult.size() > 0) {
				NodeDTO nodeDTO = null;
				String ip = null;
				Interfacecollectdata vo = null;
				Calendar tempCal = null;
				Date cc = null;
				String time = null;
				Vector fanVector = null;

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				NodeUtil nodeUtil = new NodeUtil();
				nodeDTO = nodeUtil.creatNodeDTOByNode(node);
				fanVector = (Vector) dataresult.get("fan");

			
				String hendsql = "insert into nms_envir_data_temp (nodeid,ip,`type`,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak) values(";
				String endsql = "')";
				String deleteSql = "delete from nms_envir_data_temp where nodeid='"+ node.getId() + "' and entity='fan'";
				
				// 判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
			
				if (fanVector != null && fanVector.size() > 0) {
					tempCal = Calendar.getInstance();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					Vector list =new Vector();
					
					for (int i = 0; i < fanVector.size(); i++) {
						vo = (Interfacecollectdata) fanVector.elementAt(i);
						
							StringBuffer sbuffer = new StringBuffer(150);
							sbuffer.append(hendsql);
							sbuffer.append("'").append(node.getId()).append("',");
							sbuffer.append("'").append(node.getIpAddress()).append(
									"',");
							sbuffer.append("'").append(nodeDTO.getType()).append(
									"',");
							sbuffer.append("'").append(nodeDTO.getSubtype())
									.append("',");
							sbuffer.append("'").append(vo.getCategory()).append(
									"',");
							sbuffer.append("'").append(vo.getEntity()).append("',");
							sbuffer.append("'").append(vo.getSubentity()).append(
									"',");
							sbuffer.append("'").append(vo.getThevalue()).append(
									"',");
							sbuffer.append("'").append(vo.getChname()).append("',");
							sbuffer.append("'").append(vo.getRestype())
									.append("',");
							if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("'").append(time).append("',");
							}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
							}
							
							sbuffer.append("'").append(vo.getUnit()).append("',");
							sbuffer.append("'").append(vo.getBak());
							sbuffer.append(endsql);
							list.add(sbuffer.toString());

							sbuffer = null;
							vo=null;
					}
					GathersqlListManager.AdddateTempsql(deleteSql, list);
					list=null;
				}

			}
		}
	}

}
