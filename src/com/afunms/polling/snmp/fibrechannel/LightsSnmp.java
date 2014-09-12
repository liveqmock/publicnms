package com.afunms.polling.snmp.fibrechannel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Lightcollectdata;
import com.afunms.topology.model.HostNode;
//ÐÅºÅµÆ×´Ì¬ÐÅÏ¢
public class LightsSnmp extends SnmpMonitor {
	private static Hashtable lightEntity_lightStatus = null;
	static {
		lightEntity_lightStatus = new Hashtable();
		lightEntity_lightStatus.put("1", "off");
		lightEntity_lightStatus.put("2", "green");
		lightEntity_lightStatus.put("3", "amber");
		lightEntity_lightStatus.put("4", "red");
	};
	private static Hashtable lightEntity_lightType = null;
	static {
		lightEntity_lightType = new Hashtable();
		lightEntity_lightType.put("1", "led");
		lightEntity_lightType.put("2", "alphanumeric");
	};
	private static Hashtable lightEntity_lightState = null;
	static {
		lightEntity_lightState = new Hashtable();
		lightEntity_lightState.put("1", "unknown");
		lightEntity_lightState.put("2", "off");
		lightEntity_lightState.put("3", "on");
		lightEntity_lightState.put("4", "blinking");
	};
	private static Hashtable lightEntity_lightColor = null;
	static {
		lightEntity_lightColor = new Hashtable();
		lightEntity_lightColor.put("1", "unknown");
		lightEntity_lightColor.put("2", "white");
		lightEntity_lightColor.put("3", "red");
		lightEntity_lightColor.put("4", "green");
		lightEntity_lightColor.put("5", "yellow");
		lightEntity_lightColor.put("6", "amber");
		lightEntity_lightColor.put("7", "blue");
	};
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * yangjun
	 */
	public LightsSnmp() {
	}
    public void collectData(Node node,MonitoredItem item){
	   
    }
    public void collectData(HostNode node){
	   
    }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash=new Hashtable();
		Hashtable lightHash=new Hashtable();
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		
		try {
			Lightcollectdata lightdata=new Lightcollectdata();
			Calendar date=Calendar.getInstance();
            try {
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(host.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch(Exception e) {
				SysLogger.error(host.getIpAddress() + "_CiscoLightsSnmp");
			    e.printStackTrace();	  
			}
			//-------------------------------------------------------------------------------------------process start			
			try{
				String[] oids =                
					new String[] {      
						"1.3.6.1.4.1.9.9.195.1.3.1.2",      //   color
						"1.3.6.1.4.1.9.9.344.1.1.1.2" ,     //
						"1.3.6.1.4.1.9.9.344.1.1.1.3",      //
						"1.3.6.1.4.1.9.9.344.1.1.1.4" ,
						"1.3.6.1.4.1.9.9.344.1.1.1.5"
						};

				String[][] valueArray = null;
				try {
					//valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
					valueArray = SnmpUtils.getTableData(host.getIpAddress(),host.getCommunity(),oids,host.getSnmpversion(),
		   		  			host.getSecuritylevel(),host.getSecurityName(),host.getV3_ap(),host.getAuthpassphrase(),host.getV3_privacy(),host.getPrivacyPassphrase(),3,1000*30);
				} catch(Exception e){
					valueArray = null;
					SysLogger.error(host.getIpAddress() + "_CiscoLightsSnmp");
				}
				if(valueArray != null&&valueArray.length > 0){
					for(int i=0;i<valueArray.length;i++){
						Vector lightVector=new Vector();
						String vbstring0=valueArray[i][0];
						String vbstring1=valueArray[i][1];
						String vbstring2=valueArray[i][2];
						String vbstring3=valueArray[i][3];
						String vbstring4=valueArray[i][4];
						
						lightdata=new Lightcollectdata();
						lightdata.setIpaddress(host.getIpAddress());
						lightdata.setCollecttime(date);
						lightdata.setCategory("Light");
						lightdata.setEntity("color");
						lightdata.setSubentity(i+"");
						lightdata.setRestype("dynamic");
						lightdata.setUnit(" ");
						if (vbstring0!=null&&lightEntity_lightStatus.get(vbstring0) != null){
							lightdata.setThevalue(lightEntity_lightStatus.get(vbstring0).toString());
                           
						} else {
							lightdata.setThevalue(" ");
						}
						lightdata.setChname(vbstring2);
						lightVector.addElement(lightdata);
						
						lightdata=new Lightcollectdata();
						lightdata.setIpaddress(host.getIpAddress());
						lightdata.setCollecttime(date);
						lightdata.setCategory("Light");
						lightdata.setEntity("type");
						lightdata.setSubentity(i+"");
						lightdata.setRestype("static");
						lightdata.setUnit(" ");
						if (vbstring1!=null&&lightEntity_lightType.get(vbstring1) != null){
							lightdata.setThevalue(lightEntity_lightType.get(vbstring1).toString());
						} else {
							lightdata.setThevalue(" ");
						}
						lightdata.setChname(vbstring2);
						lightVector.addElement(lightdata);	
						
						lightdata=new Lightcollectdata();
						lightdata.setIpaddress(host.getIpAddress());
						lightdata.setCollecttime(date);
						lightdata.setCategory("Light");
						lightdata.setEntity("descr");
						lightdata.setSubentity(i+"");
						lightdata.setRestype("dynamic");
						lightdata.setUnit(" ");
						lightdata.setThevalue(vbstring2);
						lightdata.setChname(vbstring2);
						lightVector.addElement(lightdata);	
						
						lightdata=new Lightcollectdata();
						lightdata.setIpaddress(host.getIpAddress());
						lightdata.setCollecttime(date);
						lightdata.setCategory("Light");
						lightdata.setEntity("state");
						lightdata.setSubentity(i+"");
						lightdata.setRestype("dynamic");
						lightdata.setUnit(" ");
						if (vbstring3!=null&&lightEntity_lightState.get(vbstring3) != null){
							lightdata.setThevalue(lightEntity_lightState.get(vbstring3).toString());
						} else {
							lightdata.setThevalue(" ");
						}
						lightdata.setChname(vbstring2);
						lightVector.addElement(lightdata);
						
						lightdata=new Lightcollectdata();
						lightdata.setIpaddress(host.getIpAddress());
						lightdata.setCollecttime(date);
						lightdata.setCategory("Light");
						lightdata.setEntity("displycolor");
						lightdata.setSubentity(i+"");
						lightdata.setRestype("dynamic");
						lightdata.setUnit(" ");
						if (vbstring4!=null&&lightEntity_lightColor.get(vbstring4) != null){
							lightdata.setThevalue(lightEntity_lightColor.get(vbstring4).toString());
						} else {
							lightdata.setThevalue(" ");
						}
						lightdata.setChname(vbstring2);
						lightVector.addElement(lightdata);
						lightHash.put(i, lightVector);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			//-------------------------------------------------------------------------------------------lights end
		}catch(Exception e){
			e.printStackTrace();
		}finally{
//			System.gc();
		}
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("light",lightHash);
//	    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
//	    returnHash.put("light", lightHash);
	    
	    if(!(ShareData.getSharedata().containsKey(host.getIpAddress())))
		{
			Hashtable ipAllData = new Hashtable();
			if(ipAllData == null)ipAllData = new Hashtable();
			if(lightHash != null && lightHash.size()>0)ipAllData.put("light",lightHash);
		    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		}else
		 {
			if(lightHash != null && lightHash.size()>0)((Hashtable)ShareData.getSharedata().get(host.getIpAddress())).put("light",lightHash);
		   
		 }
	    returnHash.put("light", lightHash);
	    
	    
	    
	    return returnHash;
	}
}
