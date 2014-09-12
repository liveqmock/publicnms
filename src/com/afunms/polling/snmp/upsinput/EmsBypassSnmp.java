package com.afunms.polling.snmp.upsinput;

/*
 * @author yangjun@dhcc.com.cn
 * 艾默生UPS旁路信息组
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.UPSNode;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.snmp.SnmpMibConstants;
import com.afunms.security.dao.MgeUpsDao;
import com.afunms.security.model.MgeUps;
import com.afunms.topology.model.HostNode;


/**   
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */  
public class EmsBypassSnmp extends SnmpMonitor {
	  java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  /**
	   * 
	  */    
	   public EmsBypassSnmp() {   
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
		Vector passVector=new Vector();
		//UPSNode node = (UPSNode)PollingEngine.getInstance().getUpsByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
//		MgeUpsDao mgeUpsDao = new MgeUpsDao();
//		MgeUps mgeUps = null;
//		try{
//			mgeUps = (MgeUps)mgeUpsDao.findByID(node.getId()+"");
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			mgeUpsDao.close();
//		}
		Systemcollectdata systemdata=null;
		Calendar date=Calendar.getInstance();
//		try{
//	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getUpsByIP(node.getIpAddress());
//			Date cc = date.getTime();
//			String time = sdf.format(cc);
//			snmpnode.setLastTime(time);
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
		try{
		    final String[] desc=SnmpMibConstants.UpsMibBypassDesc;
			final String[] chname=SnmpMibConstants.UpsMibBypassChname;
			final String[] unit=SnmpMibConstants.UpsMibBypassUnit;
//			String[] oids = new String[] {
//					"1.3.6.1.4.1.13400.2.5.2.3.1.0",
//					"1.3.6.1.4.1.13400.2.5.2.3.2.0",
//					"1.3.6.1.4.1.13400.2.5.2.3.3.0",
//					"1.3.6.1.4.1.13400.2.5.2.3.4.0"
//					};					  
			String[] valueArray = new String[4];   	   
//			for(int j=0;j<oids.length;j++){
//				try {
//					valueArray[j] = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[j]);
//				} catch(Exception e){
//					valueArray = null;
//					e.printStackTrace();
//				}
//			}
			/**
			 * public static final String[] UpsMibBypassChname={"A相旁路电压","B相旁路电压","C相旁路电压","旁路频率"};
			 */
			if(node.getSysOid().startsWith("1.3.6.1.4.1.13400.2.1")){//
				String[] oids = new String[] {
						".1.3.6.1.4.1.13400.2.1.3.3.3.3.1.0",//A相旁路电压
						".1.3.6.1.4.1.13400.2.1.3.3.3.3.2.0",//B相旁路电压
						".1.3.6.1.4.1.13400.2.1.3.3.3.3.3.0",//C相旁路电压
						".1.3.6.1.4.1.13400.2.1.3.3.3.3.7.0"//旁路频率
						};
						  
				for(int j=0;j<oids.length;j++){
					try {
						valueArray[j] = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[j]);
					} catch(Exception e){
						valueArray = null;
						e.printStackTrace();
					}
				}
			}
			if(valueArray != null&&valueArray.length>0){
			    for(int i=0;i<valueArray.length;i++) {
			    	systemdata=new Systemcollectdata();
					systemdata.setIpaddress(node.getIpAddress());
					systemdata.setCollecttime(date);
					systemdata.setCategory("Bypass");
					systemdata.setEntity(desc[i]);
					systemdata.setSubentity(desc[i]);
					systemdata.setChname(chname[i]);
					systemdata.setRestype("dynamic");
					systemdata.setUnit(unit[i]);
					String value = valueArray[i];
					System.out.println("EmsBypassSnmp:value====="+value);
					if(value!=null && !value.equals("noSuchObject")){
						systemdata.setThevalue((Float.parseFloat(value)/10)+"");
					} else {
						systemdata.setThevalue("");
					}
					passVector.addElement(systemdata);
			    }
		    }
		} catch(Exception e){
			  e.printStackTrace();
		}
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
		if(ipAllData == null)ipAllData = new Hashtable();
		ipAllData.put("bypass",passVector);
	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    returnHash.put("bypass", passVector);
	    
	    Hashtable ipdata = new Hashtable();
	    ipdata.put("bypass", returnHash);
	    Hashtable alldata = new Hashtable();
	    alldata.put(node.getIpAddress(), ipdata);
	    HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
		try {
			hostdataManager.createHostItemData(alldata, "ups");
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return returnHash;
	}
}