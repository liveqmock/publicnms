package com.afunms.polling.snmp.memory;

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
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetHostMemoryRtsql;
import com.gatherResulttosql.NetmemoryResultTosql;

/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class H3CMemorySnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 
	 */
	public H3CMemorySnmp() {
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
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		// yangjun
		Hashtable returnHash = new Hashtable();
		Vector memoryVector = new Vector();
		List memoryList = new ArrayList();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if (node == null) {
			return returnHash;
		}
		// 判断是否在采集时间段内
		if (ShareData.getTimegatherhash() != null) {
			if (ShareData.getTimegatherhash().containsKey(node.getId() + ":equipment")) {
				TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
				int _result = 0;
				_result = timeconfig.isBetween((List) ShareData.getTimegatherhash().get(node.getId() + ":equipment"));
				if (_result == 1) {
					// SysLogger.info("########时间段内: 开始采集
					// "+node.getIpAddress()+" PING数据信息##########");
				} else if (_result == 2) {
					// SysLogger.info("########全天: 开始采集 "+node.getIpAddress()+"
					// PING数据信息##########");
				} else {
					SysLogger.info("######## " + node.getIpAddress() + " 不在采集内存时间段内,退出##########");
					// 清除之前内存中产生的告警信息
					try {
						// 清除之前内存中产生的内存告警信息
						NodeDTO nodedto = null;
						NodeUtil nodeUtil = new NodeUtil();
						nodedto = nodeUtil.creatNodeDTOByHost(node);
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.deleteEvent(node.getId() + "", nodedto.getType(), nodedto.getSubtype(), "memory", null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return returnHash;
				}

			}
		}
		try {
			Calendar date = Calendar.getInstance();

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {

			}
			// -------------------------------------------------------------------------------------------内存
			// start
			try {
				String temp = "0";
				int usedvalueperc = 0;
				String memtype = "";// 有两中类型 memp 代表百分比，memsize 代表大小
				if (node.getSysOid().startsWith("1.3.6.1.4.1.2011.") || node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.25506.1.151") || node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.25506.1.149")) {
					String[][] valueArray = null;
					String[] oids = new String[] { "1.3.6.1.4.1.2011.6.1.2.1.1.2",// hwMemSize
							"1.3.6.1.4.1.2011.6.1.2.1.1.3"// hwMemFree
					};
				
					memtype = "memsize";
					
					// add at 2012-06-05 by hp  1.3.6.1.4.1.2011.2.23.147:s5700
					if (node.getSysOid().trim().startsWith("1.3.6.1.4.1.2011.2.62.2")|| node.getSysOid().equals("1.3.6.1.4.1.2011.2.23.147")) {
						oids = new String[] { "1.3.6.1.4.1.2011.6.3.5.1.1.2",// 总共的内存
								"1.3.6.1.4.1.2011.6.3.5.1.1.3"// 空闲内存
						};
						memtype = "memsize";
					}
					//1.3.6.1.4.1.2011.1.1.1.12831 R2831
					if (node.getSysOid().equals("1.3.6.1.4.1.2011.1.1.1.12831")|| node.getSysOid().equals("1.3.6.1.4.1.2011.1.1.1.12811") || node.getSysOid().equals("1.3.6.1.4.1.2011.10.1.89")) {
						oids = new String[] { "1.3.6.1.4.1.2011.2.2.5.1",// 已用内存
								"1.3.6.1.4.1.2011.2.2.5.2"// 空闲内存
						};
						memtype = "memsize";
					}
					if (node.getSysOid().equals("1.3.6.1.4.1.2011.2.170.2") || node.getSysOid().equals("1.3.6.1.4.1.2011.2.170.3")) {
						// Huawei S9306 1.3.6.1.4.1.2011.2.170.2
						// Huawei S9312 1.3.6.1.4.1.2011.2.170.3
						oids = new String[] { "1.3.6.1.4.1.2011.6.3.5.1.1.2",// 已用内存
								"1.3.6.1.4.1.2011.6.3.5.1.1.3"// 空闲内存
						};
						memtype = "memsize";
					}
					// 1.3.6.1.4.1.25506.1.354 R6604 wxy add
					if (node.getSysOid().equals("1.3.6.1.4.1.25506.1.354")||node.getSysOid().equals("1.3.6.1.4.1.2011.2.45") || node.getSysOid().equals("1.3.6.1.4.1.2011.10.1.211")) {
						// Quidway AR46-40 1.3.6.1.4.1.2011.2.45
						// Quidway S3552P EA 1.3.6.1.4.1.2011.10.1.211
						oids = new String[] { "1.3.6.1.4.1.2011.10.2.6.1.1.1.1.8"// 内存利用率
						};
						memtype = "memp";
					}
					if (node.getSysOid().equals("1.3.6.1.4.1.2011.2.31")) {
						// NE 20s 1.3.6.1.4.1.2011.2.31
						oids = new String[] { "1.3.6.1.4.1.2011.2.17.6.9.1.2"// 内存利用率
						};
						memtype = "memp";
					}
					if (node.getSysOid().equals("1.3.6.1.4.1.2011.2.49")||node.getSysOid().equals("1.3.6.1.4.1.2011.2.62.2.5") || node.getSysOid().equals("1.3.6.1.4.1.2011.2.88.2") || node.getSysOid().equals("1.3.6.1.4.1.2011.2.62.2.9") || node.getSysOid().equals("1.3.6.1.4.1.2011.2.62.2.3")
							|| node.getSysOid().equals("1.3.6.1.4.1.2011.2.62.2.8")) {
						// NE40E-3 1.3.6.1.4.1.2011.2.62.2.8
						// NE40E 1.3.6.1.4.1.2011.2.62.2.5
						// NE 5000E 1.3.6.1.4.1.2011.2.62.2.3
						// NE 20E-8 1.3.6.1.4.1.2011.2.88.2
						//NE 20E 1.3.6.1.4.1.2011.2.49
						oids = new String[] { "1.3.6.1.4.1.2011.5.25.31.1.1.1.1.7"// 内存利用率
						};
						memtype = "memp";
					}
					if (node.getSysOid().equals("1.3.6.1.4.1.2011.2.23.97") || node.getSysOid().equals("1.3.6.1.4.1.2011.2.23.95")) {
						oids = new String[] { "1.3.6.1.4.1.2011.5.25.31.1.1.1.1.7"// 内存利用率
						};
						memtype = "memp";

					}

					// valueArray =
					// snmp.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids);
					valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(),
							node.getPrivacyPassphrase(), 3, 1000 * 30);
					int allvalue = 0;

					int flag = 0;
					if (valueArray != null && valueArray.length > 0) {
						// 根据不同的类型来判断
						if (memtype.equals("memp")) {
							for (int i = 0; i < valueArray.length; i++) {
								String _value = valueArray[i][0];
								String index = valueArray[i][1];
								int value = 0;
								try {
									value = Integer.parseInt(_value);
								} catch (Exception e) {
								}
								try {
									allvalue = allvalue + value;
									// SysLogger.info(host.getIpAddress()+" 内存：
									// "+Integer.parseInt(value+"")+"
									// 总内存:"+allvalue);
								} catch (Exception e) {

								}
								if (value > 0) {
									flag = flag + 1;
									List alist = new ArrayList();
									alist.add(index);
									alist.add(_value);
									// 内存
									memoryList.add(alist);
									Memorycollectdata memorycollectdata = new Memorycollectdata();
									memorycollectdata.setIpaddress(node.getIpAddress());
									memorycollectdata.setCollecttime(date);
									memorycollectdata.setCategory("Memory");
									memorycollectdata.setEntity("Utilization");
									memorycollectdata.setSubentity(index);
									memorycollectdata.setRestype("dynamic");
									memorycollectdata.setUnit("%");
									memorycollectdata.setThevalue(_value + "");
									// SysLogger.info(host.getIpAddress()+"
									// 索引："+index+" 内存：
									// "+Integer.parseInt(_value+""));
									memoryVector.addElement(memorycollectdata);
								}
							}

						}

						if (memtype.equals("memsize")) {
							for (int i = 0; i < valueArray.length; i++) {
								String sizevalue = valueArray[i][0];
								String freevalue = valueArray[i][1];
								String index = valueArray[i][2];
								float value = 0.0f;
								String usedperc = "0";
								if (Long.parseLong(sizevalue) > 0)
									value = (Long.parseLong(sizevalue) - Long.parseLong(freevalue)) * 100 / (Long.parseLong(sizevalue));

								if (value > 0) {
									int intvalue = Math.round(value);
									allvalue = allvalue + intvalue;
									// SysLogger.info(host.getIpAddress()+" 内存：
									// "+Integer.parseInt(intvalue+"")+"
									// 总内存:"+allvalue);
									flag = flag + 1;
									List alist = new ArrayList();
									alist.add("");
									alist.add(usedperc);
									// 内存
									memoryList.add(alist);
									Memorycollectdata memorycollectdata = new Memorycollectdata();
									memorycollectdata.setIpaddress(node.getIpAddress());
									memorycollectdata.setCollecttime(date);
									memorycollectdata.setCategory("Memory");
									memorycollectdata.setEntity("Utilization");
									memorycollectdata.setSubentity(index);
									memorycollectdata.setRestype("dynamic");
									memorycollectdata.setUnit("%");
									memorycollectdata.setThevalue(intvalue + "");
									// SysLogger.info(host.getIpAddress()+" 内存：
									// "+Integer.parseInt(intvalue+""));
									memoryVector.addElement(memorycollectdata);
								}
							}
						}

						if (node.getSysOid().equals("1.3.6.1.4.1.2011.1.1.1.12831")||node.getSysOid().equals("1.3.6.1.4.1.2011.1.1.1.12811") || node.getSysOid().equals("1.3.6.1.4.1.2011.10.1.89")) {
							for (int i = 0; i < valueArray.length; i++) {
								String sizevalue = valueArray[i][0];
								String freevalue = valueArray[i][1];
								String index = valueArray[i][2];
								float value = 0.0f;
								String usedperc = "0";
								if (Long.parseLong(sizevalue) > 0)
									value = (Long.parseLong(sizevalue)) * 100 / (Long.parseLong(sizevalue) + Long.parseLong(freevalue));

								if (value > 0) {
									int intvalue = Math.round(value);
									allvalue = allvalue + intvalue;
									// SysLogger.info(host.getIpAddress()+" 内存：
									// "+Integer.parseInt(intvalue+"")+"
									// 总内存:"+allvalue);
									flag = flag + 1;
									List alist = new ArrayList();
									alist.add("");
									alist.add(usedperc);
									// 内存
									memoryList.add(alist);
									Memorycollectdata memorycollectdata = new Memorycollectdata();
									memorycollectdata.setIpaddress(node.getIpAddress());
									memorycollectdata.setCollecttime(date);
									memorycollectdata.setCategory("Memory");
									memorycollectdata.setEntity("Utilization");
									memorycollectdata.setSubentity(index);
									memorycollectdata.setRestype("dynamic");
									memorycollectdata.setUnit("%");
									memorycollectdata.setThevalue(intvalue + "");
									// SysLogger.info(host.getIpAddress()+" 内存：
									// "+Integer.parseInt(intvalue+""));
									memoryVector.addElement(memorycollectdata);
								}
							}
						}

						if (flag > 0)
							usedvalueperc = allvalue / flag;
						// //内存
						// Memorycollectdata memorycollectdata = new
						// Memorycollectdata();
						// memorycollectdata.setIpaddress(node.getIpAddress());
						// memorycollectdata.setCollecttime(date);
						// memorycollectdata.setCategory("Memory");
						// memorycollectdata.setEntity("Utilization");
						// memorycollectdata.setSubentity("Utilization");
						// memorycollectdata.setRestype("dynamic");
						// memorycollectdata.setUnit("");
						// memorycollectdata.setThevalue(usedvalueperc+"");
						// //SysLogger.info(host.getIpAddress()+" 内存：
						// "+Integer.parseInt(usedvalueperc+""));
						// memoryVector.addElement(memorycollectdata);
					} else {
						//没有采集到数据
//						Memorycollectdata memorycollectdata = new Memorycollectdata();
//						memorycollectdata.setIpaddress(node.getIpAddress());
//						memorycollectdata.setCollecttime(date);
//						memorycollectdata.setCategory("Memory");
//						memorycollectdata.setEntity("Utilization");
//						memorycollectdata.setSubentity("unknown");
//						memorycollectdata.setRestype("dynamic");
//						memorycollectdata.setUnit("%");
//						memorycollectdata.setThevalue("0");
//						// SysLogger.info(host.getIpAddress()+" 内存：
//						// "+Integer.parseInt(intvalue+""));
//						memoryVector.addElement(memorycollectdata);
					}
					
					
					
				} else if (node.getSysOid().startsWith("1.3.6.1.4.1.25506.")) {
					String[][] valueArray = null;
					String[] oids = new String[] { "1.3.6.1.4.1.2011.10.2.6.1.1.1.1.8"// 内存利用率
					};
//                      Vector<String> oidsVec=new Vector<String>();
//                      oidsVec.add("1.3.6.1.4.1.25506.1.230");//MSR20-11
					if (node.getSysOid().equals("1.3.6.1.4.1.25506.1.194") || node.getSysOid().equals("1.3.6.1.4.1.25506.1.76") || node.getSysOid().equals("1.3.6.1.4.1.25506.1.73") || node.getSysOid().equals("1.3.6.1.4.1.25506.1.151")
							|| node.getSysOid().equals("1.3.6.1.4.1.25506.1.462")) {
						oids = new String[] { "1.3.6.1.4.1.25506.2.6.1.1.1.1.8"// 内存利用率
						};

					}
					if (node.getSysOid().equals("1.3.6.1.4.1.25506.1.390") || node.getSysOid().equals("1.3.6.1.4.1.25506.1.9")) {
						oids = new String[] { "1.3.6.1.4.1.25506.2.6.1.1.1.1.8"// 内存利用率
						};
					}
					if (node.getSysOid().equals("1.3.6.1.4.1.25506.1.2")||node.getSysOid().equals("1.3.6.1.4.1.25506.1.1")) {
						oids = new String[] { "1.3.6.1.4.1.25506.2.6.1.1.1.1.8"// 内存利用率
						};

					}

					if (node.getSysOid().equals("1.3.6.1.4.1.25506.1.515")||node.getSysOid().equals("1.3.6.1.4.1.25506.1.619")||node.getSysOid().equals("1.3.6.1.4.1.25506.1.518")|| node.getSysOid().equals("1.3.6.1.4.1.25506.1.620")) {
						oids = new String[] { "1.3.6.1.4.1.25506.2.6.1.1.1.1.8"// 内存利用率
						};

					}
					if (node.getSysOid().equals("1.3.6.1.4.1.25506.1.230")) {
						oids = new String[] { "1.3.6.1.4.1.25506.2.6.1.1.1.1.8"// 内存利用率
						};

					}
					// .1.3.6.1.4.1.2011.2.17.6.9.1.2.1

					// valueArray =
					// snmp.getCpuTableData(node.getIpAddress(),node.getCommunity(),oids);
					valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(),
							node.getPrivacyPassphrase(), 1, 1000 * 10);
					int allvalue = 0;
					int flag = 0;
					if (valueArray != null && valueArray.length > 0) {
						for (int i = 0; i < valueArray.length; i++) {
							String _value = valueArray[i][0];
							String index = valueArray[i][1];
							int value = 0;
							if(_value!=null){
							try {
								value = Integer.parseInt(_value);
								allvalue = allvalue + value;
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							if (value > 0) {
								flag = flag + 1;
								List alist = new ArrayList();
								alist.add(index);
								alist.add(_value);
								// 内存
								memoryList.add(alist);
								Memorycollectdata memorycollectdata = new Memorycollectdata();
								memorycollectdata.setIpaddress(node.getIpAddress());
								memorycollectdata.setCollecttime(date);
								memorycollectdata.setCategory("Memory");
								memorycollectdata.setEntity("Utilization");
								memorycollectdata.setSubentity(index);
								memorycollectdata.setRestype("dynamic");
								memorycollectdata.setUnit("%");
								memorycollectdata.setThevalue(_value + "");
								// SysLogger.info(host.getIpAddress()+"
								// 索引："+index+" 内存：
								// "+Integer.parseInt(_value+""));
								memoryVector.addElement(memorycollectdata);
							} 
							}
						}
						if (flag > 0)
							usedvalueperc = allvalue / flag;
					}
				}
				
			} catch (Exception e) {
			}
			if (memoryVector.size() == 0) {
				//采集不到数据
				Memorycollectdata memorycollectdata = new Memorycollectdata();
				memorycollectdata.setIpaddress(node.getIpAddress());
				memorycollectdata.setCollecttime(date);
				memorycollectdata.setCategory("Memory");
				memorycollectdata.setEntity("Utilization");
				memorycollectdata.setSubentity("unknown");
				memorycollectdata.setRestype("dynamic");
				memorycollectdata.setUnit("%");
				memorycollectdata.setThevalue("0");
				memoryVector.addElement(memorycollectdata);
			}
			// -------------------------------------------------------------------------------------------内存
			// end
		} catch (Exception e) {
			// returnHash=null;
			// e.printStackTrace();
			// return null;
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			
			if (memoryVector != null && memoryVector.size() > 0)
				ipAllData.put("memory", memoryVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (memoryVector != null && memoryVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("memory", memoryVector);

		}
		returnHash.put("memory", memoryVector);

		Hashtable collectHash = new Hashtable();
		collectHash.put("memory", memoryVector);

		try {
			if (memoryVector != null && memoryVector.size() > 0) {
				int thevalue = 0;
				for (int i = 0; i < memoryVector.size(); i++) {
					Memorycollectdata memorycollectdata = (Memorycollectdata) memoryVector.get(i);
					if ("Utilization".equals(memorycollectdata.getEntity())) {
						if (Integer.parseInt(memorycollectdata.getThevalue()) > thevalue) {
							thevalue = Integer.parseInt(memorycollectdata.getThevalue());
						}
					}
				}
				CheckEventUtil checkutil = new CheckEventUtil();
				checkutil.updateData(node, nodeGatherIndicators, thevalue + "", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error("h3c 内存 告警出错", e);
		}
		memoryVector = null;
		// 把采集结果生成sql
		NetmemoryResultTosql tosql = new NetmemoryResultTosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			// 采集与访问是分离模式,则不需要将监视数据写入临时表格
			NetHostMemoryRtsql totempsql = new NetHostMemoryRtsql();
			totempsql.CreateResultTosql(returnHash, node);
		}
		return returnHash;
	}
}
