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
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Pingcollectdata;
import com.gatherdb.GathersqlListManager;


/**
 * 
 * 把cpu的采集结果生成temp的sql
 * 
 * @author konglq
 *
 */
public class NetHostDatatempCpuRTosql {
	
	/**
	 * 把结果生成sql
	 * @param dataresult 采集结果
	 * @param node 网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {//是否启动分离模式

			if (dataresult != null && dataresult.size() > 0) {
				
				Vector cpuVector = (Vector)dataresult.get("cpu");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = null;
				nodeDTO = nodeUtil.creatNodeDTOByNode(node);
				if(null!=cpuVector && cpuVector.size()>0)
				{
				CPUcollectdata vo = (CPUcollectdata) cpuVector.elementAt(0);

				String deleteSql = "delete from nms_cpu_data_temp where nodeid='" +node.getId() + "'";
				
				String hendsql="insert into nms_cpu_data_temp(nodeid,ip,`type`,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values(";
				String endsql=")";
				
				Calendar tempCal = (Calendar) vo.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				Vector list=new Vector();
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
//				sbuffer.append("'").append(time).append("',");
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					sbuffer.append("'").append(time).append("',");
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
				}
				sbuffer.append("'").append(vo.getUnit()).append("',");
				sbuffer.append("'").append(vo.getBak()).append("'");
				sbuffer.append(endsql);
				
				list.add(sbuffer.toString());
			    GathersqlListManager.AdddateTempsql(deleteSql, list);
			    list=null;
			    sbuffer=null;
			    vo=null;
			    //time=null;
			    //cc=null;
			    //tempCal=null;

					
			   }
			}


			
		}
	}

	
	

}
