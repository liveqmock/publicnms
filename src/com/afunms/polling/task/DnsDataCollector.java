package com.afunms.polling.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.model.DnsConfig;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.impl.ProcessDnsData;
import com.afunms.polling.om.Pingcollectdata;

/**
 * 
 * @descrition DNS 采集类
 * @author wangxiangyong
 * @date Apr 2, 2013 4:48:24 PM
 */
public class DnsDataCollector {
	public DnsDataCollector() {

	}

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void collect_Data(NodeGatherIndicators dnsIndicators) {
		String id = dnsIndicators.getNodeid();
		this.collectDnsData(id);
	}

	/**
	 * 
	 * @description 采集数据
	 * @author wangxiangyong
	 * @date Apr 2, 2013 5:22:12 PM
	 * @return Hashtable
	 * @param id
	 * @return
	 */
	public void collectDnsData(String id) {
		Hashtable dnsDataHash = new Hashtable();

		List dnsList = ShareData.getDnslist();
		DnsConfig dc = null;
		if (dnsList != null && dnsList.size() > 0) {
			for (int i = 0; i < dnsList.size(); i++) {
				dc = (DnsConfig) dnsList.get(i);
				if (dc.getFlag() == 0)
					continue;
				if (dc.getId() == Integer.parseInt(id))
					break;
			}
		}
		if (dc != null) {
			int status = collectDnsStatus(dnsDataHash, dc.getHostip());
			if (status == 100) {
				this.collectDnsHost(dnsDataHash, dc);
				this.collectDnsData(dnsDataHash, dc);
				this.collectDnsAddr(dnsDataHash, dc);
				this.collectDnsHinfo(dnsDataHash, dc);
				this.collectDnsMx(dnsDataHash, dc);
				this.collectDnsNs(dnsDataHash, dc);
				this.collectDnsCache(dnsDataHash, dc);
			}
			// 存入内存
			ShareData.getAllDnsData().put(dc.getId(), dnsDataHash);
			// 入库
			this.processDnsData(dc, status);
		}
	}

	/**
	 * 
	 * @description 入库操作
	 * @author wangxiangyong
	 * @date Apr 3, 2013 9:42:56 AM
	 * @return void
	 * @param dc
	 * @param status
	 */
	public void processDnsData(DnsConfig dc, int status) {
		Calendar date = Calendar.getInstance();
		Pingcollectdata hostdata = new Pingcollectdata();
		hostdata.setIpaddress(dc.getHostip());
		hostdata.setCollecttime(date);
		hostdata.setCategory("DnsPing");
		hostdata.setEntity("Utilization");
		hostdata.setSubentity("ConnectUtilization");
		hostdata.setRestype("dynamic");
		hostdata.setUnit("%");
		hostdata.setThevalue(status + "");
		try {
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dc);
			// 判断是否存在此告警指标
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list1 = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
			CheckEventUtil checkEventUtil = new CheckEventUtil();
			for (int i = 0; i < list1.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list1.get(i);
				if ("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, status+"");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ProcessDnsData processDnsData = new ProcessDnsData();
		processDnsData.saveDnsData(dc, ShareData.getAllDnsData());
		processDnsData.createHostData(hostdata);
	}

	public int collectDnsStatus(Hashtable dnsDataHash, String hostip) {
		String str1 = "";
		String str2 = "";
		int status = 0;
		Process process = null;
		BufferedReader bf = null;
		try {
			process= Runtime.getRuntime().exec("cmd /c nslookup " + hostip + " " + hostip);
			 bf = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((str1 = bf.readLine()) != null) {
				str2 += str1;
			}
			
			if (!str2.contains(hostip)||str2.contains("UnKnownAddress")||str2.contains("找不到") || str2.contains("Non-existent domain") || str2.contains("No response from server")) {
				status = 0;
			} else{
				status = 100;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf != null) {
				try {
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process != null) {
				process.destroy();
			}
		}
		dnsDataHash.put("status", status);
		return status;
	}

	/**
	 * 
	 * @description 得到服务器名 以及ip地址
	 * @author wangxiangyong
	 * @date Apr 2, 2013 4:57:33 PM
	 * @return void
	 * @param dnsDataHash
	 * @param dc
	 */
	public void collectDnsHost(Hashtable dnsDataHash, DnsConfig dc) {
		List array = new ArrayList();
		String str = "";
		String defaultStr = "";
		String hostip = "";
		int zhuangtai = 0;
		Process process = null;
		BufferedReader bf = null;
		try {
			process = Runtime.getRuntime().exec("cmd /c nslookup " + dc.getHostip() + " " + dc.getHostip());
			bf = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((str = bf.readLine()) != null) {
				array.add(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf != null) {
				try {
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process != null) {
				process.destroy();
			}
		}
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i).toString().contains("Name:")) {
				defaultStr = array.get(i).toString().substring(6);
				hostip = dc.getHostip();
				zhuangtai = 1;
			}
		}
		dnsDataHash.put("default", defaultStr);
		dnsDataHash.put("hostip", hostip);
		dnsDataHash.put("zhuangtai", zhuangtai);
	}

	/**
	 * 
	 * @description 得到域名所对应的地址
	 * @author wangxiangyong
	 * @date Apr 2, 2013 4:57:33 PM
	 * @return void
	 * @param dnsDataHash
	 * @param dc
	 */
	public void collectDnsData(Hashtable dnsDataHash, DnsConfig dc) {// 得到域名所对应的地址
		List array1 = new ArrayList();
		List arr1 = new ArrayList();
		String str1 = null;
		String time = "";
		String dnsip = "";
		String dns = "";
		long lasting = System.currentTimeMillis();
		Process process1 = null;
		BufferedReader bf1 = null;
		try {
			process1 = Runtime.getRuntime().exec("cmd /c nslookup " + dc.getDns() + " " + dc.getDnsip());
			bf1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
			while ((str1 = bf1.readLine()) != null) {
				array1.add(str1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf1 != null) {
				try {
					bf1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process1 != null) {
				process1.destroy();
			}
		}
		for (int j = 0; j < array1.size(); j++) {
			if (array1.get(j).toString().contains("Addresses:")) {
				dnsip = array1.get(j).toString().substring(10);
				dns = dc.getDns();
			}
			if (array1.get(j).toString().contains("Address:")) {
				arr1.add(array1.get(j));
				if (arr1.size() == 2) {
					dnsip = array1.get(j).toString().substring(8);
					dns = dc.getDns();
				}
			}
		}
		time = "响应时间：" + (System.currentTimeMillis() - lasting) + "ms";
		dnsDataHash.put("time", time);
		dnsDataHash.put("dnsip", dnsip);
		// dnsDataHash.put("dns", dns);
	}

	/**
	 * 
	 * @description 得到域名所对应的地址记录
	 * @author wangxiangyong
	 * @date Apr 2, 2013 4:57:33 PM
	 * @return void
	 * @param dnsDataHash
	 * @param dc
	 */
	public void collectDnsAddr(Hashtable dnsDataHash, DnsConfig dc) {
		// A记录
		String dns = "";
		String addr = "";
		List array2 = new ArrayList();
		List arr2 = new ArrayList();
		Process process2 = null;
		BufferedReader bf2 = null;
		String str2 = null;
		if (dc.getDns().contains("www")) {
			dns = dc.getDns().substring(4);
		} else {
			dns = dc.getDns();
		}
		try {
			process2 = Runtime.getRuntime().exec("cmd /c nslookup -qt=a " + dns + " " + dc.getHostip() + "");
			bf2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
			while ((str2 = bf2.readLine()) != null) {
				array2.add(str2);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf2 != null) {
				try {
					bf2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process2 != null) {
				process2.destroy();
			}
		}
		for (int j = 0; j < array2.size(); j++) {
			if (array2.get(j).toString().contains("Addresses:")) {
				addr = array2.get(j).toString().substring(10);
			}
			if (array2.get(j).toString().contains("Address:")) {
				arr2.add(array2.get(j));
				if (arr2.size() == 2) {
					addr = array2.get(j).toString().substring(8);
				}
			}
		}
		dnsDataHash.put("addr", addr);
		dnsDataHash.put("dns", dns);
	}

	/**
	 * 
	 * @description HINFO硬件配置信息
	 * @author wangxiangyong
	 * @date Apr 2, 2013 4:57:33 PM
	 * @return void
	 * @param dnsDataHash
	 * @param dc
	 */
	public void collectDnsHinfo(Hashtable dnsDataHash, DnsConfig dc) {
		List array3 = new ArrayList();
		String str3 = null;
		Process process3 = null;
		BufferedReader bf3 = null;
		String primary = "";
		String responsible = "";
		String serial = "";
		String refresh = "";
		String retry = "";
		String expire = "";
		String dfault = "";
		String dns = (String) dnsDataHash.get("dns");
		try {
			process3 = Runtime.getRuntime().exec("cmd /c nslookup -qt=hinfo " + dns + " " + dc.getHostip() + "");
			bf3 = new BufferedReader(new InputStreamReader(process3.getInputStream()));
			while ((str3 = bf3.readLine()) != null) {
				array3.add(str3);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf3 != null) {
				try {
					bf3.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process3 != null) {
				process3.destroy();
			}
		}
		for (int j = 0; j < array3.size(); j++) {
			if (array3.get(j).toString().contains("primary")) {
				primary = "主要名字服务器:" + array3.get(j).toString().substring(22);
			}
			if (array3.get(j).toString().contains("responsible")) {
				responsible = "邮件地址:" + array3.get(j).toString().substring(24);
			}
			if (array3.get(j).toString().contains("serial")) {
				serial = "文件版本:" + array3.get(j).toString().substring(10);
			}
			if (array3.get(j).toString().contains("refresh")) {
				refresh = "重刷新时间:" + array3.get(j).toString().substring(10);
			}
			if (array3.get(j).toString().contains("retry")) {
				retry = "重试时间:" + array3.get(j).toString().substring(10);
			}
			if (array3.get(j).toString().contains("expire")) {
				expire = "有效时间:" + array3.get(j).toString().substring(10);
			}
			if (array3.get(j).toString().contains("default")) {
				dfault = "TTL设置:" + array3.get(j).toString().substring(14);
			}
		}
		dnsDataHash.put("primary", primary);
		dnsDataHash.put("responsible", responsible);
		dnsDataHash.put("serial", serial);
		dnsDataHash.put("refresh", refresh);
		dnsDataHash.put("retry", retry);
		dnsDataHash.put("expire", expire);
		dnsDataHash.put("dfault", dfault);
	}

	/**
	 * 
	 * @description MX信息
	 * @author wangxiangyong
	 * @date Apr 2, 2013 4:57:33 PM
	 * @return void
	 * @param dnsDataHash
	 * @param dc
	 */
	public void collectDnsMx(Hashtable dnsDataHash, DnsConfig dc) {
		// MX记录
		List array4 = new ArrayList();
		String str4 = null;
		BufferedReader bf4 = null;
		List mx = new ArrayList();
		String dns = "";
		Process process4 = null;
		if (dc.getDns().contains("www")) {
			dns = dc.getDns().substring(4);
		} else {
			dns = dc.getDns();
		}
		try {
			process4 = Runtime.getRuntime().exec("cmd /c nslookup -qt=mx " + dns + " " + dc.getHostip() + "");
			bf4 = new BufferedReader(new InputStreamReader(process4.getInputStream()));
			while ((str4 = bf4.readLine()) != null) {
				array4.add(str4);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf4 != null) {
				try {
					bf4.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process4 != null) {
				process4.destroy();
			}
		}
		List array5 = new ArrayList();
		for (int j = 0; j < array4.size(); j++) {
			if (array4.get(j).toString().contains(dns)) {
				array5.add(array4.get(j).toString());
				mx = array5;
			}
		}

		dnsDataHash.put("mx", mx);
	}

	/**
	 * 
	 * @description NS信息
	 * @author wangxiangyong
	 * @date Apr 2, 2013 4:57:33 PM
	 * @return void
	 * @param dnsDataHash
	 * @param dc
	 */
	public void collectDnsNs(Hashtable dnsDataHash, DnsConfig dc) {
		// NS记录
		String dns = "";
		List ns = new ArrayList();
		List array6 = new ArrayList();
		String str6 = null;
		Process process6 = null;
		BufferedReader bf6 = null;
		if (dc.getDns().contains("www")) {
			dns = dc.getDns().substring(6);
		} else {
			dns = dc.getDns();
		}
		try {
			process6 = Runtime.getRuntime().exec("cmd /c nslookup -qt=ns " + dns + " " + dc.getHostip() + "");
			bf6 = new BufferedReader(new InputStreamReader(process6.getInputStream()));
			while ((str6 = bf6.readLine()) != null) {
				array6.add(str6);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf6 != null) {
				try {
					bf6.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process6 != null) {
				process6.destroy();
			}
		}
		List array7 = new ArrayList();
		for (int j = 0; j < array6.size(); j++) {
			if (array6.get(j).toString().contains(dns)) {
				array7.add(array6.get(j).toString());
				ns = array7;
			}
		}

		dnsDataHash.put("ns", ns);
	}

	/**
	 * 
	 * @description 緩存信息
	 * @author wangxiangyong
	 * @date Apr 2, 2013 4:57:33 PM
	 * @return void
	 * @param dnsDataHash
	 * @param dc
	 */
	public void collectDnsCache(Hashtable dnsDataHash, DnsConfig dc) {
		// 缓存记录
		// NS记录
		String dns = "";
		List cache = null;
		List array8 = new ArrayList();
		String str8 = null;
		if (dc.getDns().contains("www")) {
			dns = dc.getDns().substring(8);
		} else {
			dns = dc.getDns();
		}
		Process process8 = null;
		BufferedReader bf8 = null;
		try {
			process8 = Runtime.getRuntime().exec("cmd /c nslookup -d3 " + dc.getHostip() + " " + dc.getHostip());
			bf8 = new BufferedReader(new InputStreamReader(process8.getInputStream()));
			while ((str8 = bf8.readLine()) != null) {
				array8.add(str8);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf8 != null) {
				try {
					bf8.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process8 != null) {
				process8.destroy();
			}
		}
		for (int k = 0; k < array8.size(); k++) {
			if (array8.get(k).toString().contains("opcode")) {
			}
		}
		cache = array8;

		dnsDataHash.put("cache", cache);
	}

}
