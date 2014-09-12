package com.afunms.polling.snmp.upsoutput;

/*
 * @author yangjun@dhcc.com.cn
 *
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
public class EmsOutputSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */    
	public EmsOutputSnmp() {   
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
		Vector outputVector=new Vector();
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
		    final String[] desc=SnmpMibConstants.UpsMibOutputDesc;
			final String[] chname=SnmpMibConstants.UpsMibOutputChname;
			final String[] unit=SnmpMibConstants.UpsMibOutputUnit;
//			String[] oids = new String[] {
//					"1.3.6.1.4.1.13400.2.5.2.4.1.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.2.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.3.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.4.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.5.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.6.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.7.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.8.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.9.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.10.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.11.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.12.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.13.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.14.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.15.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.16.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.17.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.18.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.19.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.20.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.21.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.22.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.23.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.24.0",
//					"1.3.6.1.4.1.13400.2.5.2.4.25.0"
//					};
					  
			String[] valueArray = new String[22];  		  
//			for(int j=0;j<oids.length;j++){
//				try {
//					valueArray[j] = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[j]);
//				} catch(Exception e){
//					valueArray = null;
//					e.printStackTrace();
//				}
//			}
			if(node.getSysOid().startsWith("1.3.6.1.4.1.13400.2.1")){//
				/**
				 * 	public static final String[] UpsMibOutputChname={"输出线电压A","输出线电压B","输出线电压C",
				 * "A相输出电流","B相输出电流","C相输出电流",
				 * "输出频率",
				 * "A相输出功率因数","B相输出功率因数","C相输出功率因数",
       			 * 	"A相输出有功功率","B相输出有功功率","C相输出有功功率",
       			 * 	"A相输出视在功率","B相输出视在功率","C相输出视在功率",
        		 * 	"A相输出无功功率","B相输出无功功率","C相输出无功功率",
         		 * 	"A相输出负载百分比","B相输出负载百分比","C相输出负载百分比",
				 * 	"A相输出峰值比","B相输出峰值比","C相输出峰值比"};
				 * 
				 */
				String[] oids = new String[] {
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.1.0",//输出A相电压
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.2.0",//输出B相电压
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.3.0",//输出C相电压
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.4.0",//A相输出电流
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.5.0",//B相输出电流
						".1.3.6.1.4.1.13400.2.1.3.3.3.3.6.0",//C相输出电流
						
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.7.0",//输出频率
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.8.0",//A相输出功率因数
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.9.0",//B相输出功率因数
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.10.0",//C相输出功率因数
						
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.11.0",//A相输出有功功率
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.12.0",//B相输出有功功率
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.13.0",//C相输出有功功率
						
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.14.0",//A相输出视在功率
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.15.0",//B相输出视在功率
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.16.0",//C相输出视在功率
						
						//"1.3.6.1.4.1.13400.2.20.2.4.29.0",//A相输出无功功率
						//"1.3.6.1.4.1.13400.2.20.2.4.30.0",
						//"1.3.6.1.4.1.13400.2.20.2.4.31.0",
						
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.17.0",//A相输出负载百分比
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.18.0",//B相输出负载百分比
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.19.0",//C相输出负载百分比
						
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.20.0",//A相输出峰值比
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.21.0",//B相输出峰值比
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.22.0"//C相输出峰值比
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
					systemdata.setCategory("Output");
					systemdata.setEntity(desc[i]);
					systemdata.setSubentity(desc[i]);
					systemdata.setChname(chname[i]);
					systemdata.setRestype("dynamic");
					systemdata.setUnit(unit[i]);
					String value = valueArray[i];
					System.out.println("EmsOutputSnmp:value====="+value);
					if(value!=null && !value.equals("noSuchObject")){
						if(desc[i].equals("AXSCGLYS")||desc[i].equals("BXSCGLYS")||desc[i].equals("CXSCGLYS")){
							systemdata.setThevalue((Float.parseFloat(value)/100)+"");
						}else{
						  systemdata.setThevalue((Float.parseFloat(value)/10)+"");
						}
					} else {
						systemdata.setThevalue("0");
					}
					outputVector.addElement(systemdata);
			    }
		    }
		} catch(Exception e){
			  e.printStackTrace();
		}
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
		if(ipAllData == null)ipAllData = new Hashtable();
		ipAllData.put("output",outputVector);
	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    returnHash.put("output", outputVector);
	    
	    Hashtable ipdata = new Hashtable();
	    ipdata.put("output", returnHash);
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