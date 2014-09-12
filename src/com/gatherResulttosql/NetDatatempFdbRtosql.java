package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.IpMac;
import com.gatherdb.GathersqlListManager;
import com.gatherdb.GathersqlRun;

public class NetDatatempFdbRtosql {
	
	/**
	 * 把结果生成sql
	 * 
	 * @param dataresult
	 *            采集结果
	 * @param node
	 *            网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {
	
	if(dataresult != null && dataresult.size()>0){
		NodeDTO nodeDTO = null;
		String ip = null;
		IpMac vo = null;
		Calendar tempCal = null;
		Date cc = null;
		String time = null;
		
		Vector fdbVector = null;
    	
		SimpleDateFormat sdf = new SimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss");
        NodeUtil nodeUtil = new NodeUtil();
		
 			nodeDTO = nodeUtil.creatNodeDTOByNode(node);
 			fdbVector = (Vector)dataresult.get("fdb");
			
			String deleteSql = "delete from nms_fdb_data_temp where nodeid='" +node.getId() + "'";
			
			
			if(fdbVector != null && fdbVector.size()>0){
				
				 tempCal=Calendar.getInstance();
				 time = sdf.format(tempCal.getTime());
				 Vector list=new Vector();
				for(int i=0;i<fdbVector.size();i++){
					vo = (IpMac) fdbVector.elementAt(i);
					String mac = vo.getMac();
					if(mac != null && !mac.contains(":")){// 排除mac为乱码的情况
						mac = "--";
					}
					
					    StringBuffer sql = new StringBuffer(200);
					    sql.append("insert into nms_fdb_data_temp(nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak)values('");
					    sql.append(node.getId());
					    sql.append("','");
					    sql.append(node.getIpAddress());
					    sql.append("','");
					    sql.append(nodeDTO.getType());
					    sql.append("','");
					    sql.append(nodeDTO.getSubtype());
					    sql.append("','");
					    sql.append(vo.getIfindex());
					    sql.append("','");
					    sql.append(vo.getIpaddress());
					    sql.append("','");
					    sql.append(mac);
					    sql.append("','");
					    sql.append(vo.getIfband());
					    sql.append("','");
					    sql.append(vo.getIfsms());
					    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					    	 sql.append("','");
							 sql.append(time);
							 sql.append("','"+vo.getBak()+"')");
					    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					    	 sql.append("',");
							 sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
							 sql.append(",'"+vo.getBak()+"')");
					    }
					    list.add(sql.toString());
					    sql=null;
					    vo=null;
					    
				}
				
				GathersqlListManager.AdddateTempsql(deleteSql, list);
				list=null;
				
			}
		}
	}
	
	/**
	 * 把结果生成sql
	 * 
	 * @param dataresult
	 *            采集结果
	 * @param node
	 *            网元节点
	 */
	public void CreateResultTosql1(Hashtable dataresult, Host node) {
		if ("0".equals(PollingEngine.getCollectwebflag())) {// 是否启动分离模式
			// 处理IPMAC信息入库
			if (dataresult != null && dataresult.size() > 0) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Vector fdbVector = (Vector) dataresult.get("fdb");
				String deleteSql = "delete from ipmac where relateipaddr='" + node.getIpAddress() + "'";
				Vector list = new Vector();
				DBManager dbm=new DBManager();
				dbm.addBatch(deleteSql);
				dbm.executeBatch();
				if (fdbVector != null && fdbVector.size() > 0) {
					for (int i = 0; i < fdbVector.size(); i++) {
						try {
							IpMac ipmac = (IpMac) fdbVector.elementAt(i);
							String mac = ipmac.getMac();
							if (mac == null) {
								mac = "";
							}
							mac = CommonUtil.removeIllegalStr(mac);
							String sqll = "";
							String time = sdf.format(ipmac.getCollecttime().getTime());
							sqll = "insert into ipmac(relateipaddr,ifindex,ipaddress,mac,collecttime,ifband,ifsms,bak)values('";
							sqll = sqll + ipmac.getRelateipaddr() + "','" + ipmac.getIfindex() + "','" + ipmac.getIpaddress() + "','";
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sqll = sqll + new String(mac.getBytes(), "UTF-8") + "','" + time + "','" + ipmac.getIfband() + "','" + ipmac.getIfsms() + "','" + ipmac.getIfband() + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sqll = sqll + new String(mac.getBytes(), "UTF-8") + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'),'" + ipmac.getIfband() + "','" + ipmac.getIfsms() + "','" + ipmac.getIfband() + "')";
							}
							
							dbm.addBatch(sqll);
							mac = null;
							ipmac = null;

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
				try{
					dbm.executeBatch();
					dbm.commit();
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					dbm.close();
				}
			}
		}
	}
}
