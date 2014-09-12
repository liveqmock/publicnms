package com.afunms.polling.snmp.device;

/*
 * @author yangjun@dhcc.com.cn
 * ibm光纤交换机硬件信息采集
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Devicecollectdata;
import com.afunms.topology.model.HostNode;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class IbmDeviceSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public IbmDeviceSnmp() {
	}
	
	private static Hashtable device_Status = null;
	static {
		device_Status = new Hashtable();
		device_Status.put("1", "未知");
		device_Status.put("2", "运行");
		device_Status.put("3", "告警");
		device_Status.put("4", "测试");
		device_Status.put("5", "停止");
	};
	private static Hashtable device_Type = null;
	static {
		device_Type = new Hashtable();
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.1", "名称");
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.2", "未知");
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.3", "电源");
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.4", "风扇");
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.5", "风扇");
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.6", "风扇");
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.7", "其他");
	};

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash=new Hashtable();
		Vector deviceVector=new Vector();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		
		try {
			Devicecollectdata devicedata=null;
			Calendar date=Calendar.getInstance();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			//-------------------------------------------------------------------------------------------device start
			  try{
			  String[] oids =                
				  new String[] {               
					  "1.3.6.1.2.1.47.1.1.1.1.2.1",  //
					  "1.3.6.1.2.1.47.1.1.1.1.2.2",  //
					  "1.3.6.1.2.1.47.1.1.1.1.2.3",    //
					  "1.3.6.1.2.1.47.1.1.1.1.2.4" };   //

			  String[][] valueArray = null;   	  
				try {
					//valueArray = snmp.getTableData(node.getIpAddress(),node.getCommunity(),oids);
					valueArray = SnmpUtils.getTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
				} catch(Exception e){
					valueArray = null;
					//SysLogger.error(node.getIpAddress() + "_WindowsSnmp");
				}	
				for(int i=0;i<valueArray.length;i++){
					devicedata = new Devicecollectdata();
					String devindex = valueArray[i][0];
					String type = valueArray[i][1];
					String name = valueArray[i][2];
					String status = valueArray[i][3];
					if(status == null)status="";
					if(device_Status.containsKey(status))status = (String)device_Status.get(status);
					devicedata.setDeviceindex(devindex);
					devicedata.setIpaddress(node.getIpAddress());
					devicedata.setName(name);
					devicedata.setStatus(status);
					devicedata.setType((String)device_Type.get(type));
					deviceVector.addElement(devicedata);
					//SysLogger.info(name+"######"+devindex+"######"+(String)device_Type.get(type)+"######"+status);
				    
				}  
		   }catch(Exception e){
		   }			   
		   //-------------------------------------------------------------------------------------------device end
			}catch(Exception e){
			}finally{
//				System.gc();
			}
		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("device",deviceVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    
	    
	    if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
		{
			Hashtable ipAllData = new Hashtable();
			if(ipAllData == null)ipAllData = new Hashtable();
			if(deviceVector != null && deviceVector.size()>0)ipAllData.put("device",deviceVector);
			
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		}else
		 {
			if(deviceVector != null && deviceVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("device",deviceVector);
		 }
	    
	    
	    returnHash.put("device",deviceVector);
	    return returnHash;
	}
}





