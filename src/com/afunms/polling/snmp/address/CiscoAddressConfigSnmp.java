package com.afunms.polling.snmp.address;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CMTSaddresscollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.gatherResulttosql.NetcmtsaddressResultTosql;


/**
 * 获取cmts中的地址的相关数据
 * @author wangzhenlong
 *
 */
public class CiscoAddressConfigSnmp extends SnmpMonitor {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 采集数据方法
	 * @param alarmIndicatorsNode
	 * @return
	 */
	public Hashtable collect_data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector addressVector = new Vector();
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (host == null)
			return returnHash;

		// 判断是否在采集时间段内
		if (ShareData.getTimegatherhash() != null) {
			if (ShareData.getTimegatherhash().containsKey(
					host.getId() + ":equipment")) {
				TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
				int _result = 0;
				_result = timeconfig.isBetween((List) ShareData
						.getTimegatherhash().get(host.getId() + ":equipment"));
				if (_result == 1) {
					// SysLogger.info("########时间段内: 开始采集
					// "+node.getIpAddress()+" PING数据信息##########");
				} else if (_result == 2) {
					// SysLogger.info("########全天: 开始采集 "+node.getIpAddress()+"
					// PING数据信息##########");
				} else {
					SysLogger.info("######## " + host.getIpAddress()
							+ " 不在采集flash时间段内,退出##########");
					// //清除之前内存中产生的告警信息
					// try{
					// //清除之前内存中产生的内存告警信息
					// CheckEventUtil checkutil = new CheckEventUtil();
					// checkutil.deleteEvent(node.getId()+":host:diskperc");
					// checkutil.deleteEvent(node.getId()+":host:diskinc");
					// }catch(Exception e){
					// e.printStackTrace();
					// }
					return returnHash;
				}
			}
		}

		try {
			Calendar date = Calendar.getInstance();

			try {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Node snmpNode = (Node) PollingEngine.getInstance().getNodeByIP(
						host.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpNode.setLastTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {

				String[] oids = new String[] { "1.3.6.1.2.1.10.127.1.3.3.1.3",// IP地址信息
						"1.3.6.1.2.1.10.127.1.3.3.1.21",// MAC地址信息
						"1.3.6.1.2.1.10.127.1.3.3.1.9" };// 地址状态信息

				String[][] valueArray = null;
				valueArray = snmp.getTableData(host.getIpAddress(), host
						.getCommunity(), oids);

				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						if (valueArray[i] == null)
							continue;
						CMTSaddresscollectdata cmtsaddresscollectdata = new CMTSaddresscollectdata();
						cmtsaddresscollectdata.setCollecttime(sdf
								.format(new Date()));// 设置采集时间
						cmtsaddresscollectdata.setIpAddress(valueArray[i][0]);//获取IP地址信息
						cmtsaddresscollectdata.setMacAddress(valueArray[i][1]);//获取mac地址信息
						cmtsaddresscollectdata
								.setStatusAddress(valueArray[i][2]);//获取地址状态信息
						addressVector.addElement(cmtsaddresscollectdata);
					}
				}
				
				returnHash.put("cmtsaddress", addressVector);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 信息存入内存
		if (!(ShareData.getSharedata().containsKey(host.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null)
				ipAllData = new Hashtable();
			if (addressVector != null && addressVector.size() > 0)
				ipAllData.put("cmtsaddress", addressVector);
			ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		} else {
			if (addressVector != null && addressVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(host.getIpAddress()))
						.put("cmtsaddress", addressVector);
		}
		
		
		NetcmtsaddressResultTosql tosql = new NetcmtsaddressResultTosql();
		tosql.CreateResultTosql(returnHash, host);

		addressVector=null;
		
		return returnHash;
	}
}
