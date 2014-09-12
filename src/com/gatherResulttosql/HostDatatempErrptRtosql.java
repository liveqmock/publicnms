package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Errptlog;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.gatherdb.GathersqlListManager;

public class HostDatatempErrptRtosql {

	/**
	 * 把结果生成sql
	 * 
	 * @param dataresult
	 *            采集结果
	 * @param node
	 *            网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

//		if ("1".equals(PollingEngine.getCollectwebflag())) {// 是否启动分离模式

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
			Vector errptVector = (Vector) dataresult.get("errptlog");
			String deleteSql = "delete from nms_errptlog where hostid='"
					+ node.getId() + "'";
			if (errptVector != null && errptVector.size() > 0) {
				Calendar tempCal = Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				
				Vector list=new Vector();
				
				for (int i = 0; i < errptVector.size(); i++) {
					Errptlog vo = (Errptlog) errptVector.get(i);
					try {
						
						StringBuffer sql = new StringBuffer(200);
						sql.append("insert into nms_errptlog(labels,identifier,collettime,seqnumber,nodeid,machineid,errptclass,errpttype,resourcename,resourceclass,resourcetype,locations,vpd,descriptions,hostid) values('");
						sql.append(vo.getLabels());
						sql.append("','");
						sql.append(vo.getIdentifier());
						sql.append("','");
						sql.append(time);
						sql.append("','");
						sql.append(vo.getSeqnumber());
						sql.append("','");
						sql.append(vo.getNodeid());
						sql.append("','");
						sql.append(vo.getMachineid());
						sql.append("','");
						sql.append(vo.getErrptclass());
						sql.append("','");
						sql.append(vo.getErrpttype());
						sql.append("','");
						sql.append(vo.getResourcename());
						sql.append("','");
						sql.append(vo.getResourceclass());
						sql.append("','");
						sql.append(vo.getRescourcetype());
						sql.append("','");
						sql.append(vo.getLocations());
						sql.append("','");
						sql.append(vo.getVpd());
						sql.append("','");
						sql.append(vo.getDescriptions().replaceAll("'", ""));
						sql.append("','");
						sql.append(vo.getHostid());
						sql.append("')");
						SysLogger.info(sql.toString());
						list.add(sql.toString());
						sql=null;
						vo=null;
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				
				 GathersqlListManager.AdddateTempsql(deleteSql, list);
				 list=null;
			}
//		}
	}

}
