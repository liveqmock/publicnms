package com.afunms.polling.snmp.statue;

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
public class EmsStatueSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */    
	public EmsStatueSnmp() {   
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
		Vector statuVector=new Vector();
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
		    final String[] desc=SnmpMibConstants.UpsMibStatueDesc;
			final String[] chname=SnmpMibConstants.UpsMibStatueChname;
			final String[] unit=SnmpMibConstants.UpsMibStatueUnit;
//			String[] oids = new String[] {
//					"1.3.6.1.4.1.13400.2.5.2.6.1.0",//
//					"1.3.6.1.4.1.13400.2.5.2.6.2.0",//
//					"1.3.6.1.4.1.13400.2.5.2.6.3.0",//
//					"1.3.6.1.4.1.13400.2.5.2.6.4.0",//
//					"1.3.6.1.4.1.13400.2.5.2.6.5.0",//
//					"1.3.6.1.4.1.13400.2.5.2.6.6.0",//
//					"1.3.6.1.4.1.13400.2.5.2.6.7.0",//
//					"1.3.6.1.4.1.13400.2.5.2.6.8.0",//
//					"1.3.6.1.4.1.13400.2.5.2.6.9.0",//
//					"1.3.6.1.4.1.13400.2.5.2.6.10.0",//
//					"1.3.6.1.4.1.13400.2.5.2.6.11.0"
//					};
//					  
//			String[] valueArray = new String[11];   	   
//			for(int j=0;j<oids.length;j++){
//				try {
//					valueArray[j] = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[j]);
//				} catch(Exception e){
//					valueArray = null;
//					e.printStackTrace();
//				}
//			}
			String[] valueArray = new String[8];
			if(node.getSysOid().startsWith("1.3.6.1.4.1.13400.2.1")){//
/**
 * 	public static final String[] UpsMibStatueChname={"并机设置总台数","并机设置冗余台数",
 * "并机系统A相输出总有功功率","并机系统B相输出总有功功率","并机系统C相输出总有功功率",
        "并机系统A相输出总视在功率","并机系统A相输出总视在功率","并机系统A相输出总视在功率",
        "并机系统A相输出总无功功率","并机系统A相输出总无功功率","并机系统A相输出总无功功率"};
 */
				
				String[] oids = new String[] {
						".1.3.6.1.4.1.13400.2.1.3.3.10.1.3.0",//并机设置总台数
						".1.3.6.1.4.1.13400.2.1.3.3.10.1.2.0",//并机设置冗余台数
						".1.3.6.1.4.1.13400.2.1.3.3.10.2.1.0",//并机系统A相输出总有功功率
						".1.3.6.1.4.1.13400.2.1.3.3.10.2.2.0",//并机系统B相输出总有功功率
						".1.3.6.1.4.1.13400.2.1.3.3.10.2.3.0",//并机系统C相输出总有功功率
						
						".1.3.6.1.4.1.13400.2.1.3.3.10.2.4.0",//并机系统A相输出总视在功率
						".1.3.6.1.4.1.13400.2.1.3.3.10.2.5.0",//并机系统B相输出总视在功率
						".1.3.6.1.4.1.13400.2.1.3.3.10.2.6.0",//并机系统C相输出总视在功率
						
						//"1.3.6.1.4.1.13400.2.20.2.2.7.0",//并机系统A相输出总无功功率
						//"1.3.6.1.4.1.13400.2.20.2.2.8.0",//
						//"1.3.6.1.4.1.13400.2.20.2.2.9.0"
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
					systemdata.setCategory("Statue");
					systemdata.setEntity(desc[i]);
					systemdata.setSubentity(desc[i]);
					systemdata.setChname(chname[i]);
					systemdata.setRestype("dynamic");
					systemdata.setUnit(unit[i]);
					String value = valueArray[i];
					System.out.println("EmsStatueSnmp:value====="+value);
					if(value!=null && !value.equals("noSuchObject")){
						if(desc[i].equals("JXRL")||desc[i].equals("BJTH")){
							systemdata.setThevalue(value+"");
						}else{
							systemdata.setThevalue((Float.parseFloat(value)/10)+"");
						}						
					} else {
						systemdata.setThevalue("");
					}
					statuVector.addElement(systemdata);
			    }
		    }
		} catch(Exception e){
			  e.printStackTrace();
		}
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
		if(ipAllData == null)ipAllData = new Hashtable();
		ipAllData.put("statue",statuVector);
	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    returnHash.put("statue", statuVector);
	    
	    Hashtable ipdata = new Hashtable();
	    ipdata.put("statue", returnHash);
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