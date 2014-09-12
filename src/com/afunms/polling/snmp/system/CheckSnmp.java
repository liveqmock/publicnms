package com.afunms.polling.snmp.system;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.AlarmHelper;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.EnvConfig;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.snmp.SnmpMibConstants;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CheckSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public CheckSnmp() {
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
		Vector snmpVector=new Vector();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return returnHash;
		//判断是否在采集时间段内
    	if(ShareData.getTimegatherhash() != null){
    		if(ShareData.getTimegatherhash().containsKey(node.getId()+":equipment")){
    			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
    			int _result = 0;
    			_result = timeconfig.isBetween((List)ShareData.getTimegatherhash().get(node.getId()+":equipment"));
    			if(_result ==1 ){
    				//SysLogger.info("########时间段内: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
    			}else if(_result == 2){
    				//SysLogger.info("########全天: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
    			}else {
    				SysLogger.info("######## "+node.getIpAddress()+" 不在采集flash时间段内,退出##########");
//    				//清除之前内存中产生的告警信息
//    			    try{
//    			    	//清除之前内存中产生的内存告警信息
//						CheckEventUtil checkutil = new CheckEventUtil();
//						checkutil.deleteEvent(node.getId()+":host:diskperc");
//						checkutil.deleteEvent(node.getId()+":host:diskinc");
//    			    }catch(Exception e){
//    			    	e.printStackTrace();
//    			    }
    				return returnHash;
    			}
    			
    		}
    	}
    	
		try {
			Systemcollectdata systemdata=null;
			Calendar date=Calendar.getInstance();
//			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//			if(ipAllData == null)ipAllData = new Hashtable();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			//-------------------------------------------------------------------------------------------system start			
			  try{
					//final String[] desc={"sysDescr","sysUpTime","sysContact","sysName","sysLocation","sysServices","MacAddr"};
					//final String[] chname={"描述","运行时间","联系人","设备名称","设备位置","服务类型","MacAddr"};				
				final String[] desc=SnmpMibConstants.NetWorkMibSystemDesc;
				final String[] chname=SnmpMibConstants.NetWorkMibSystemChname;
						  String[] oids =                
							  new String[] {               
								"1.3.6.1.2.1.1.1" ,
								"1.3.6.1.2.1.1.3" ,
								"1.3.6.1.2.1.1.4" ,
								"1.3.6.1.2.1.1.5" ,
								"1.3.6.1.2.1.1.6" ,
								"1.3.6.1.2.1.1.7"
								//"1.3.6.1.2.1.2.2.1.6"
								  };
						  
				String[][] valueArray = null;   	  
				try {
					//valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
					valueArray = SnmpUtils.getTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
				} catch(Exception e){
					valueArray = null;
					//SysLogger.error(node.getIpAddress() + "_WindowsSnmp");
				}
				if(valueArray != null){
					//snmp服务开启
					Pingcollectdata hostdata=null;
					hostdata=new Pingcollectdata();
					hostdata.setIpaddress(node.getIpAddress());
					hostdata.setCollecttime(date);
					hostdata.setCategory("SNMPPing");
					hostdata.setEntity("Utilization");
					hostdata.setSubentity("ConnectUtilization");
					hostdata.setRestype("dynamic");
					hostdata.setUnit("%");
					hostdata.setThevalue("0");
					snmpVector.add(hostdata);
				}else{
					//snmp服务未开启
					Pingcollectdata hostdata=null;
					hostdata=new Pingcollectdata();
					hostdata.setIpaddress(node.getIpAddress());
					hostdata.setCollecttime(date);
					hostdata.setCategory("SNMPPing");
					hostdata.setEntity("Utilization");
					hostdata.setSubentity("ConnectUtilization");
					hostdata.setRestype("dynamic");
					hostdata.setUnit("%");
					hostdata.setThevalue("1");
					snmpVector.add(hostdata);
				}
			  }
			  catch(Exception e){
				  //e.printStackTrace();
			}
			  //-------------------------------------------------------------------------------------------system end
			  
			}catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
			}
			
			try{
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, "windows","snmp");
				for (int i = 0; i < snmpVector.size(); i++) {
    				Pingcollectdata pingdata = (Pingcollectdata) snmpVector.elementAt(i);
    				if(pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")){
						//连通率进行判断               						
						//List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), alarmIndicatorsNode.getType(), "");
						for(int m = 0 ; m < list.size() ; m ++){
							AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(m);
							if("1".equals(_alarmIndicatorsNode.getEnabled())){
								if(_alarmIndicatorsNode.getName().equalsIgnoreCase("snmp")){
									CheckEventUtil checkeventutil = new CheckEventUtil();
									//SysLogger.info(_alarmIndicatorsNode.getName()+"=====_alarmIndicatorsNode.getName()=========");
									checkeventutil.checkEvent(node, _alarmIndicatorsNode, pingdata.getThevalue());
								}
							}  
						}
						
					}
    			}
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
			
			
		
			if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
			{
				Hashtable ipAllData = new Hashtable();
				if(ipAllData == null)ipAllData = new Hashtable();
				if(snmpVector != null && snmpVector.size()>0)ipAllData.put("snmp",snmpVector);
			    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
			}else
			 {
				if(snmpVector != null && snmpVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("snmp",snmpVector);
			 }
			
	    returnHash.put("snmp", snmpVector);
	    snmpVector=null;
	    
	    String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	    if(!"0".equals(runmodel)){
			//采集与访问是分离模式,则不需要将监视数据写入临时表格
	    	NetHostDatatempSystemRttosql tosql=new NetHostDatatempSystemRttosql();
		    tosql.CreateResultTosql(returnHash, node);
	    }
	    
	    
	    
	    return returnHash;
	}
}





