package com.afunms.polling.snmp.system;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.application.dao.NetAppDataOperator;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Systemcollectdata;

//NetApp 产品信息采集类
public class NetAppProductInfoSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String[] NetAppProductDesc = { //
	"productType", //
			"productVersion", //
			"productId", //
			"productVendor", //
			"productModel", //
			"productFirmwareVersion", //
			"productGuiUrl", //
			"productApiUrl", //
			"productSerialNum", //
			"productPartnerSerialNum", //
			"productCPUArch", //
			"productTrapData", //
			"productMachineType", //
	};
	public static final String[] NetAppProductDescCh = { //
	"产品类型", //
			"产品版本", //
			"产品ID", //
			"产品供应商", //
			"产品模块", //
			"软件版本",//
			"Gui路径",//
			"Api路径",//
			"产品序列号",//
			"合作伙伴ID",//
			"Cpu构建",//
			"Trap信息",//
			"机器类型",//
	};

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector systemVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			Systemcollectdata systemdata = null;
			Calendar date = Calendar.getInstance();

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {

			}
			try {
				final String[] desc = NetAppProductDesc;
				final String[] chname = NetAppProductDescCh;
				String[] oids = new String[] { ".1.3.6.1.4.1.789.1.1.1.0", // productType
						".1.3.6.1.4.1.789.1.1.2.0", // productVersion
						".1.3.6.1.4.1.789.1.1.3.0", // productId
						".1.3.6.1.4.1.789.1.1.4.0", // productVendor
						".1.3.6.1.4.1.789.1.1.5.0", // productModel
						".1.3.6.1.4.1.789.1.1.6.0", // productFirmwareVersion
						".1.3.6.1.4.1.789.1.1.7.0", // productGuiUrl
						".1.3.6.1.4.1.789.1.1.8.0", // productApiUrl
						".1.3.6.1.4.1.789.1.1.9.0", // productSerialNum
						".1.3.6.1.4.1.789.1.1.10.0", // productPartnerSerialNum
						".1.3.6.1.4.1.789.1.1.11.0", // productCPUArch
						".1.3.6.1.4.1.789.1.1.12.0", // productTrapData
						".1.3.6.1.4.1.789.1.1.13.0" // productMachineType
				};

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getList(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						for (int j = 0; j < 13; j++) {
							systemdata = new Systemcollectdata();
							systemdata.setIpaddress(node.getIpAddress());
							systemdata.setCollecttime(date);
							systemdata.setCategory("NetApp");
							systemdata.setEntity("ProductInfo");
							systemdata.setSubentity(desc[j]);
							systemdata.setChname(chname[j]);
							systemdata.setRestype("static");
							systemdata.setUnit("");
							String value = valueArray[i][j];
							if (j == 0) {
								systemdata.setThevalue(value);
							} else
								systemdata.setThevalue(value);
							systemVector.addElement(systemdata);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null)
				ipAllData = new Hashtable();
			if (systemVector != null && systemVector.size() > 0)
				ipAllData.put("productInfo", systemVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (systemVector != null && systemVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("productInfo", systemVector);
		}

		returnHash.put("productInfo", systemVector);

		systemVector = null;

		NetAppDataOperator op=new NetAppDataOperator();
		op.CreateResultTosql(returnHash, node.getIpAddress());

		return returnHash;
	}
}
