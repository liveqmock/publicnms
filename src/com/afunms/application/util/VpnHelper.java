package com.afunms.application.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.detail.net.service.NetService;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.temp.model.NodeTemp;

/**
 * 
 * @description 获取VPN数据信息
 * @author wangxiangyong
 * @date Feb 12, 2012 2:06:59 PM
 */
public class VpnHelper {

	public String getPingValue(Host host) {
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		String pingvalue = "0";
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
			if (pingData != null && pingData.size() > 0) {
				Pingcollectdata pingdata = (Pingcollectdata) pingData.get(0);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				pingvalue = pingdata.getThevalue();
				pingdata = (Pingcollectdata) pingData.get(1);
			}
		} else {
			List pingList = new ArrayList();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
			pingList = new NetService(host.getId() + "", nodedto.getType(), nodedto.getSubtype()).getCurrPingInfo();
			// ----ping值和响应时间end
			if (pingList != null && pingList.size() > 0) {
				for (int i = 0; i < pingList.size(); i++) {
					NodeTemp nodetemp = (NodeTemp) pingList.get(i);
					if ("ConnectUtilization".equals(nodetemp.getSindex())) {
						pingvalue = nodetemp.getThevalue();
					}
				}
			}
		}
		return pingvalue;
	}

	public Hashtable getInterface(Host host) {
		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		String id="";
		if (host != null) {
             id=host.getId()+"";
			Vector ifvector = new Vector();
			String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
			String orderflag = "index";
			String[] netInterfaceItem = { "index", "ifDescr","ifSpeed", "ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };
			I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
			if ("0".equals(runmodel)) {
				// 采集与访问是集成模式
				try {
					ifvector = hostlastmanager.getInterface_share(host.getIpAddress(), netInterfaceItem, orderflag, null, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					ifvector = hostlastmanager.getInterface(host.getIpAddress(), netInterfaceItem, orderflag, null, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (ifvector != null) {
				for (int i = 0; i < ifvector.size(); i++) {
					String[] strs = (String[]) ifvector.get(i);
					if (strs != null) {
						//端口流速
						int speed = (Integer.parseInt(strs[5].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "")) + Integer
								.parseInt(strs[4].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", ""))) / 2;

						hashtable.put(id+"_"+strs[0] + "_" + netInterfaceItem[2], speed+"");
						hashtable.put(id+"_"+strs[0] + "_" + netInterfaceItem[3], strs[3]);
//						hashtable.put(id+"_"+strs[0] + "_" + netInterfaceItem[4], strs[4]);
//						hashtable.put(id+"_"+strs[0] + "_" + netInterfaceItem[5], strs[5]);
//						System.out.println(speed+"---"+id+"_"+strs[0] + "_" + netInterfaceItem[2]+":"+strs[3]+":"+strs[4]+":"+strs[5]+":"+strs[1]);
					}
				}
			}

		}
		return hashtable;
	}
}

