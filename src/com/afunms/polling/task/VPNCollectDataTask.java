
package com.afunms.polling.task;
/*
 * created 7.26 2011
 */
import java.util.Hashtable;

import com.afunms.polling.impl.ProcessVPNData;
import com.afunms.polling.node.Host;
import com.afunms.polling.snmp.cpu.ArrayNetworkCpuSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNClusterSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNCountSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNFlowRateSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNInforSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNInterfaceSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNLogSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNSSLSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNSSLSysInforSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNSessionSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNSystemSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNTCPSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNTcsSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNVClientAppSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNVIPSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNVSSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNVirtualSiteSnmp;
import com.afunms.polling.snmp.vpn.ArrayVPNWebSnmp;


public class VPNCollectDataTask extends MonitorTask {
		

	public static void main(String [] args){
		VPNCollectDataTask vpnCollectDataTask = new VPNCollectDataTask();
		vpnCollectDataTask.run();
	}

	public void run() { 
		try{
			/*
			 * 配置节点信息
			 */
			Host node = new Host();
			node.setIpAddress("10.204.3.253");
			node.setCommunity("oavpn-1");
			node.setSnmpversion(1);
//			SysLogger.info("-------------------------------开始采集VPN信息" +
//					"-------------------------------");
			ProcessVPNData processVPNData = new ProcessVPNData();
					/*
					 * 对Cluster进行数据采集
					 */
//					ArrayVPNClusterSnmp arrayVPNClusterSnmp = new ArrayVPNClusterSnmp();
//					Hashtable returnHash = arrayVPNClusterSnmp.collect_Data(node);
//					processVPNData.saveVPNClusterData(returnHash);
//					/*
//					 * 对SSL进行数据采集
//					 */
//					ArrayVPNSSLSnmp arrayVPNSSLSnmp = new ArrayVPNSSLSnmp();
//					Hashtable returnHashSSL = arrayVPNSSLSnmp.collect_Data(node);
//					processVPNData.saveVPNSSLData(returnHashSSL);
//					/*
//					 * 对session进行数据采集
//					 */
//					ArrayVPNSessionSnmp arrayVPNSessionSnmp = new ArrayVPNSessionSnmp();
//					Hashtable returnHashSession = arrayVPNSessionSnmp.collect_Data(node);
//					processVPNData.saveVPNSessionData(returnHashSession);
//					/*
//					 * 对TCP进行数据采集 
//					 */
//					ArrayVPNTCPSnmp arrayVPNTCPSnmp = new ArrayVPNTCPSnmp();
//					Hashtable returnHashTCP = arrayVPNTCPSnmp.collect_Data(node);
//					processVPNData.saveVPNTCPData(returnHashTCP);
//					/*
//					 * 对Interface进行数据采集
//					 */
//					ArrayVPNInterfaceSnmp arrayVPNInterfaceSnmp = new ArrayVPNInterfaceSnmp();
//					Hashtable returnHashInterface = arrayVPNInterfaceSnmp.collect_Data(node);
//					processVPNData.saveVPNInterfaceData(returnHashInterface);
//					/*
//					 * 对FlowRate进行数据采集
//					 */
//					ArrayVPNFlowRateSnmp arrayVPNFlowRateSnmp = new ArrayVPNFlowRateSnmp();
//					Hashtable returnHashFlowRate = arrayVPNFlowRateSnmp.collect_Data(node);
//					processVPNData.saveVPNFlowRateData(returnHashFlowRate);
//					/*
//					 * 对VirtualSite进行数据采集
//					 */
//					ArrayVPNVirtualSiteSnmp arrayVPNVirtualSiteSnmp = new ArrayVPNVirtualSiteSnmp();
//					Hashtable returnHashVirtualSite = arrayVPNVirtualSiteSnmp.collect_Data(node);
//					processVPNData.saveVPNVirtualSiteData(returnHashVirtualSite);
//					/*
//					 * 对VPNInf进行数据采集
//					 */
//					ArrayVPNInforSnmp arrayVPNInforSnmp = new ArrayVPNInforSnmp();
//					Hashtable returnHashVPNInf = arrayVPNInforSnmp.collect_Data(node);
//					processVPNData.saveVPNInforData(returnHashVPNInf);
//					/*
//					 * 对VPNweb进行数据采集
//					 */
//					ArrayVPNWebSnmp arrayVPNWebSnmp = new ArrayVPNWebSnmp();
//					Hashtable returnHashVPNWeb= arrayVPNWebSnmp.collect_Data(node);
//					processVPNData.saveVPNWebData(returnHashVPNWeb);
//					/*
//					 * 对VPNVS进行数据采集
//					 */
//					ArrayVPNVSSnmp arrayVPNVSSnmp = new ArrayVPNVSSnmp();
//					Hashtable returnHashVPNVS= arrayVPNVSSnmp.collect_Data(node);
//					processVPNData.saveVPNVSData(returnHashVPNVS);
//					/*
//					 * 对VPNVS进行数据采集
//					 */
//					ArrayVPNVClientAppSnmp arrayVPNVClientAppSnmp = new ArrayVPNVClientAppSnmp();
//					Hashtable returnHashVPNVC= arrayVPNVClientAppSnmp.collect_Data(node);
//					processVPNData.saveVPNVCData(returnHashVPNVC);
//					/*
//					 * 对VPNVIP进行数据采集
//					 */
//					ArrayVPNVIPSnmp arrayVPNVIPSnmp = new ArrayVPNVIPSnmp();
//					Hashtable returnHashVPNVIP= arrayVPNVIPSnmp.collect_Data(node);
//					processVPNData.saveVPNVIPData(returnHashVPNVIP);
//					/*
//					 * 对VPNLog进行数据采集
//					 */ 
//					ArrayVPNLogSnmp arrayVPNLogSnmp = new ArrayVPNLogSnmp();
//					Hashtable returnHashVPNLog = arrayVPNLogSnmp.collect_Data(node);
//					processVPNData.saveVPNLogData(returnHashVPNLog);
//					/*
//					 * 对count进行数据采集
//					 */
//					ArrayVPNCountSnmp arrayVPNCountSnmp = new ArrayVPNCountSnmp();
//					Hashtable returnHashVPNCount = arrayVPNCountSnmp.collect_Data(node);
//					processVPNData.saveVPNCountData(returnHashVPNCount);
//					/*
//					 * 对TCS进行数据采集
//					 */
//					ArrayVPNTcsSnmp arrayVPNTcsSnmp = new ArrayVPNTcsSnmp();
//					Hashtable returnHashVPNTcs = arrayVPNTcsSnmp.collect_Data(node);
//					processVPNData.saveVPNTCSData(returnHashVPNTcs);
//					/*
//					 * 对System进行数据采集
//					 */
//					ArrayVPNSystemSnmp arrayVPNSystemSnmp = new ArrayVPNSystemSnmp();
//					Hashtable returnHashVPNSystem = arrayVPNSystemSnmp.collect_Data(node);
//					processVPNData.saveVPNSystemData(returnHashVPNSystem);
//					/*
//					 * 对SSLSysInfor进行数据采集
//					 */
//					ArrayVPNSSLSysInforSnmp arrayVPNSSLSysInforSnmp = new ArrayVPNSSLSysInforSnmp();
//					Hashtable returnHashVPNSSLSysInfor = arrayVPNSSLSysInforSnmp.collect_Data(node);
//					processVPNData.saveVPNSSLSysInforData(returnHashVPNSSLSysInfor);
//					/*
//					 * 对cpu进行数据采集
//					 */
//					ArrayNetworkCpuSnmp arrayNetworkCpuSnmp = new ArrayNetworkCpuSnmp();
//					Hashtable returnHashVPNCpu = arrayNetworkCpuSnmp.collect_Data(node);
//					processVPNData.saveVPNCpuData(returnHashVPNCpu);

					
			
			 
		}catch(Exception e){					 	
			e.printStackTrace();
		}
			finally{
//			SysLogger.info("-------------------------------VPN 采集数据已经入库 : " +
//					"-------------------------------"
//					+Thread.activeCount()
//					);
		}
				
	}
	
	

	
}
