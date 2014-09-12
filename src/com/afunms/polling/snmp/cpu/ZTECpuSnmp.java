package com.afunms.polling.snmp.cpu;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetcpuResultTosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ZTECpuSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 
	 */
	public ZTECpuSnmp() {
	}

	public void collectData(Node node, MonitoredItem item) {

	}

	public void collectData(HostNode node) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		// yangjun
		Hashtable returnHash = new Hashtable();
		Vector cpuVector = new Vector();
		List cpuList = new ArrayList();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return null;
		// HostNode host = (HostNode)node;

		try {
			// System.out.println("Start collect data as ip "+host);
			CPUcollectdata cpudata = null;
			Calendar date = Calendar.getInstance();

			try {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node) PollingEngine
						.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {

			}
			// -------------------------------------------------------------------------------------------cpu
			// start
			int result = 0;
			String temp = "0";
			try {
				// String temp = "0";
				String[] oids = null;
				String[][] valueArray = null;
				
		if (node.getSysOid().startsWith(
				"1.3.6.1.4.1.3902.15.2.30")) {

					oids = new String[] { "1.3.6.1.4.1.3902.15.2.30.1.3"// ZTE 2928
			    };
//					valueArray = SnmpUtils.getCpuTableData(node.getIpAddress(),
//							node.getCommunity(), oids, node.getSnmpversion(),
//							3, 1000);
					valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
					int allvalue = 0;
					int flag = 0;

					if (valueArray != null) {
						for (int i = 0; i < valueArray.length; i++) {
							String _value = valueArray[i][0];
							String index = valueArray[i][1];
							int value = 0;
							//value = (int) Float.parseFloat(_value);
							value=Math.round(Float.parseFloat(_value));
							allvalue = allvalue + value;
							// if(value >0){
							flag = flag + 1;
							List alist = new ArrayList();
							alist.add(index);
							alist.add(_value);
							cpuList.add(alist);
							// }
							// SysLogger.info(host.getIpAddress()+" "+index+"
							// value="+value);
						}
					}

					if (flag > 0) {

						int intvalue = (allvalue / flag);
						temp = intvalue + "";
						// SysLogger.info(node.getIpAddress()+"获取的cpu===
						// "+allvalue/flag);
					}

					if (temp == null) {
						result = 0;
					} else {
						try {
							if (temp.equalsIgnoreCase("noSuchObject")) {
								result = 0;
							} else
								result = Integer.parseInt(temp);
						} catch (Exception ex) {
							ex.printStackTrace();
							result = 0;
						}
					}
					cpudata = new CPUcollectdata();
					cpudata.setIpaddress(node.getIpAddress());
					cpudata.setCollecttime(date);
					cpudata.setCategory("CPU");
					cpudata.setEntity("Utilization");
					cpudata.setSubentity("Utilization");
					cpudata.setRestype("dynamic");
					cpudata.setUnit("%");
					cpudata.setThevalue(result + "");

					cpuVector.addElement(cpudata);
					
					
					
		       }else{
				
				if (node.getSysOid().startsWith("1.3.6.1.4.1.3902.")) {
					
				
					
					// ZTE M6000
					if (node.getSysOid().startsWith(
							"1.3.6.1.4.1.3902.3.100.6002.2")) {
						oids = new String[] { "1.3.6.1.4.1.3902.3.6002.2.1.1.9"// CPU5分钟利用率
						};
					}

					// ZTE T600
					if (node.getSysOid()
							.startsWith("1.3.6.1.4.1.3902.3.100.27")) {
						oids = new String[] { "1.3.6.1.4.1.3902.3.3.1.1.12"// CPU5分钟利用率
						};
					}

					// ZTE 5928
					if (node.getSysOid()
							.startsWith("1.3.6.1.4.1.3902.3.100.40")) {

						oids = new String[] { "1.3.6.1.4.1.3902.3.3.1.1.12"// CPU5分钟利用率
						};
					}

					// ZTE 3884
					if (node.getSysOid().startsWith("1.3.6.1.4.1.3902.3.100.135")) {

						oids = new String[] { "1.3.6.1.4.1.3902.3.3.1.1.6"// CPU5秒钟利用率
						};
					}

					// ZTE 2928
					if (node.getSysOid().startsWith(
							"1.3.6.1.4.1.3902.15.2.11.2")) {

						oids = new String[] { "1.3.6.1.4.1.3902.15.2.11.1.3"// CPU2分钟利用率，2928没有5分钟的CPU利用率
						};
					}
					// ZTE 3800
					if (node.getSysOid().startsWith(
							"1.3.6.1.4.1.3902.3.100.55")) {

						oids = new String[] { "1.3.6.1.4.1.3902.15.2.10.1.3"
						};
					}
					// ZTE 5250
					if (node.getSysOid().startsWith(
							"1.3.6.1.4.1.3902.3.100.56")) {

						oids = new String[] { "1.3.6.1.4.1.3902.3.3.1.1.6"
						};
					}
					//ZTE 2609
					//5分钟   1.3.6.1.4.1.3902.15.2.2.1.1
					//30分钟  1.3.6.1.4.1.3902.15.2.2.1.2
					////2分钟  1.3.6.1.4.1.3902.15.2.2.1.3
					if (node.getSysOid().startsWith("1.3.6.1.4.1.3902.15.2.1.4")) {

				     oids = new String[] { "1.3.6.1.4.1.3902.15.2.2.1.3"
				    };
			          }
//					valueArray = SnmpUtils.getCpuTableData(node.getIpAddress(),
//							node.getCommunity(), oids, node.getSnmpversion(),
//							3, 1000);
					valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
					int allvalue = 0;
					int flag = 0;

					if (valueArray != null) {
						for (int i = 0; i < valueArray.length; i++) {
							String _value = valueArray[i][0];
							String index = valueArray[i][1];

							int value = 0;
							value = Integer.parseInt(_value);
							allvalue = allvalue + Integer.parseInt(_value);
							// if(value >0){
							flag = flag + 1;
							List alist = new ArrayList();
							alist.add(index);
							alist.add(_value);
							cpuList.add(alist);
							// }
							// SysLogger.info(host.getIpAddress()+" "+index+"
							// value="+value);
						}
					}

					if (flag > 0) {

						int intvalue = (allvalue / flag);
						temp = intvalue + "";
						// SysLogger.info(node.getIpAddress()+"获取的cpu===
						// "+allvalue/flag);
					}

					if (temp == null) {
						result = 0;
					} else {
						try {
							if (temp.equalsIgnoreCase("noSuchObject")) {
								result = 0;
							} else
								result = Integer.parseInt(temp);
						} catch (Exception ex) {
							ex.printStackTrace();
							result = 0;
						}
					}
					cpudata = new CPUcollectdata();
					cpudata.setIpaddress(node.getIpAddress());
					cpudata.setCollecttime(date);
					cpudata.setCategory("CPU");
					cpudata.setEntity("Utilization");
					cpudata.setSubentity("Utilization");
					cpudata.setRestype("dynamic");
					cpudata.setUnit("%");
					cpudata.setThevalue(result + "");

					cpuVector.addElement(cpudata);

				}
		       }
			} catch (Exception e) {
				// e.printStackTrace();
			}
			// -------------------------------------------------------------------------------------------cpu
			// end
		} catch (Exception e) {
			// returnHash=null;
			// e.printStackTrace();
			// return null;
		}
		Hashtable ipAllData = new Hashtable();
		try {
			ipAllData = (Hashtable) ShareData.getSharedata().get(
					node.getIpAddress());
		} catch (Exception e) {

		}
		if (ipAllData == null)
			ipAllData = new Hashtable();
		if (cpuVector != null && cpuVector.size() > 0)
			ipAllData.put("cpu", cpuVector);
		if (cpuList != null && cpuList.size() > 0)
			ipAllData.put("cpulist", cpuList);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("cpu", cpuVector);

		// 对CPU值进行告警检测
		Hashtable collectHash = new Hashtable();
		collectHash.put("cpu", cpuVector);
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(
					String.valueOf(node.getId()), AlarmConstant.TYPE_NET,
					"zte", "cpu");
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list
						.get(i);
				// 对CPU值进行告警检测
				CheckEventUtil checkutil = new CheckEventUtil();
				checkutil.updateData(node, collectHash, "net", "zte",
						alarmIndicatorsnode);
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 把结果转换成sql
		NetcpuResultTosql tosql = new NetcpuResultTosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());
		NetHostDatatempCpuRTosql totempsql = new NetHostDatatempCpuRTosql();
		totempsql.CreateResultTosql(returnHash, node);

		return returnHash;
	}
}





