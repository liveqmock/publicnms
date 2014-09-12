package com.afunms.polling.snmp.voltage;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetDatatempvoltageRtosql;
import com.gatherResulttosql.NetvoltageResultTosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MaipuVoltageSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 *     
	 */
	public MaipuVoltageSnmp() {
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
		Vector voltageVector=new Vector();
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
			Interfacecollectdata interfacedata = new Interfacecollectdata();
			Calendar date=Calendar.getInstance();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			//-------------------------------------------------------------------------------------------电压 start
		   	  try {
		   		  if(node.getSysOid().startsWith("1.3.6.1.4.1.5651.")){
		   			String[][] valueArray = null;
		   			String[] oids =                
						  new String[] {       
		   					//"1.3.6.1.4.1.43.45.1.10.2.6.1.1.1.1.12"
							"1.3.6.1.4.1.5651.3.600.1.1"//电压
		   			};
		   			//valueArray = SnmpUtils.getCpuTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
		   			valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
		   			int allvalue=0;
		   			int flag = 0;
					if(valueArray != null){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {
					   		String _value = valueArray[i][0];
					   		String index = valueArray[i][1];
					   		int value=0;
					   		try{
					   			value=Integer.parseInt(_value);
					   			allvalue = allvalue+Integer.parseInt(_value);
					   		}catch(Exception e){
					   			
					   		}
							if(value >0){
								flag = flag +1;
						   		List alist = new ArrayList();
						   		alist.add(index);
						   		alist.add(_value);
						   		//温度
						   		//voltageList.add(alist);				   		
						   		  interfacedata = new Interfacecollectdata();
						   		  interfacedata.setIpaddress(node.getIpAddress());
						   		  interfacedata.setCollecttime(date);
						   		  interfacedata.setCategory("Voltage");
						   		  interfacedata.setEntity(index);
						   		  interfacedata.setSubentity(index);
						   		  interfacedata.setRestype("dynamic");
						   		  interfacedata.setUnit("V");		
						   		  interfacedata.setThevalue(value+"");
								  SysLogger.info(node.getIpAddress()+" 电压： "+value);
								  voltageVector.addElement(interfacedata);		   		
							}
					   		//SysLogger.info(host.getIpAddress()+"  "+index+"   value="+value);
					   	  }
					}
		   		  } 
				  //cpuVector.add(3, temperatureList);
		   	  }
		   	  catch(Exception e)
		   	  {
		   	  }	   	  
		   	  //-------------------------------------------------------------------------------------------电压 end
			}catch(Exception e){
			}
			if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
			{
				Hashtable ipAllData = new Hashtable();
				if(ipAllData == null)ipAllData = new Hashtable();
				if(voltageVector != null && voltageVector.size()>0)ipAllData.put("voltage",voltageVector);
			    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
			}else
			 {
				if(voltageVector != null && voltageVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("voltage",voltageVector);
			 }
			
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("voltage",voltageVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    returnHash.put("voltage", voltageVector);
	    try{
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "maipu","voltage");
			
			for(int i = 0 ; i < list.size() ; i ++){
				 
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
				//对风扇进行告警检测
				CheckEventUtil checkutil = new CheckEventUtil();
				if (voltageVector.size()>0) {
					for (int j = 0; j < voltageVector.size(); j++) {
						Interfacecollectdata data=(Interfacecollectdata)voltageVector.get(j);
						if (data!=null) {
							checkutil.checkEvent(node,alarmIndicatorsnode,data.getThevalue(),data.getSubentity());
						}
						
					}
					
				}
				
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    //ipAllData=null;
	    voltageVector=null;
	    
	    //把采集结果生成sql
	    NetvoltageResultTosql tosql=new NetvoltageResultTosql();
	    tosql.CreateResultTosql(returnHash, node.getIpAddress());
	    String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	    if(!"0".equals(runmodel)){
			//采集与访问是分离模式,则不需要将监视数据写入临时表格
	    	NetDatatempvoltageRtosql totempsql=new NetDatatempvoltageRtosql();
		    totempsql.CreateResultTosql(returnHash, node);
	    }
	    
	    return returnHash;
	}
}





