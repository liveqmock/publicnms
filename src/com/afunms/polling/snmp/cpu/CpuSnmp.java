package com.afunms.polling.snmp.cpu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetcpuResultTosql;

public class CpuSnmp  extends SnmpMonitor {

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public CpuSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {	
		Hashtable returnHash=new Hashtable();
		Vector cpuVector=new Vector();
		List cpuList = new ArrayList();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if(node == null) {
			return returnHash;
		}
		
    	//判断是否在采集时间段内
    	if(ShareData.getTimegatherhash() != null){
    		if(ShareData.getTimegatherhash().containsKey(node.getId()+":equipment")){
    			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
    			int _result = 0;
    			_result = timeconfig.isBetween((List)ShareData.getTimegatherhash().get(node.getId()+":equipment"));
    			if(_result ==1 ){
    			}else if(_result == 2){
    			}else {
    				SysLogger.info("######## "+node.getIpAddress()+" 不在采集CPU时间段内,退出##########");
    				//清除之前内存中产生的告警信息
    			    try{
    			    	//清除之前内存中产生的CPU告警信息
    			    	NodeDTO nodedto = null;
    					NodeUtil nodeUtil = new NodeUtil();
    					nodedto = nodeUtil.creatNodeDTOByHost(node);
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.deleteEvent(node.getId()+"", nodedto.getType(), nodedto.getSubtype(), "cpu", null);
    			    }catch(Exception e){
    			    	e.printStackTrace();
    			    }
    				return returnHash;
    			}
    			
    		}
    	}
		
		try {
			CPUcollectdata cpudata=null;
			int result = 0;
			Calendar date=Calendar.getInstance();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  e.printStackTrace();
			  }
			  try {
				  //-------------------------------------------------------------------------------------------cpu start
		   		  String temp = "0";
		   		 if(node.getSysOid().startsWith("1.3.6.1.4.1.35047")){
		   			String[][] valueArray = null;
		   			
		   			String[] oids = new String[] {"1.3.6.1.4.1.35047.1.3"};//CPU利用率
//		   			String[] oids = new String[] {"1.3.6.1.4.1.35047.1.6.1.0"};//1分钟平均CPU利用率
//		   			String[] oids = new String[] {"1.3.6.1.4.1.35047.1.6.2.0"};//5分钟平均CPU利用率
//		   			String[] oids = new String[] {"1.3.6.1.4.1.35047.1.6.3.0"};//10分钟平均CPU利用率
		   			valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
		   			int allvalue=0;
		   			int flag = 0;
					if(valueArray != null && valueArray.length > 0){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {
					   		String _value = valueArray[i][0];		   		
					   		String index = valueArray[i][1];
					   		int value=0;
					   		value=Integer.parseInt(_value);
							allvalue = allvalue+Integer.parseInt(_value);
							if(value >0){
								flag = flag +1;
						   		List alist = new ArrayList();
						   		alist.add(index);
						   		alist.add(_value);
						   		cpuList.add(alist);					   		
							}
					   	  }
					}
					
					if(flag >0){
						int intvalue = (allvalue/flag);
						temp = intvalue+"";
					}
		   		 }else if(node.getSysOid().startsWith("1.3.6.1.4.1.2011.")){
		   			String[][] valueArray = null;
		   			String[] oids =                
						  new String[] {               
							"1.3.6.1.4.1.2011.6.1.1.1.4"};
		   			String[] oids2 = 
		   				  new String[] {
		   					"1.3.6.1.4.1.2011.10.2.6.1.1.1.1.6"};
		   			
		   			//-----1.3.6.1.4.1.2011.2.26. heiweine08
		   		    if(node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.26.2")  //
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.19") //S6506
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.21") //s3526E
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.22") //s2026
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.24") //s2026E
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.27") //s3526E-FM
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.28") //s3526E-FS
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.29") //s3050C
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.30") //s6503
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.37") //s3552G
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.39") //s3528G
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.40") //s3528P
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.10.1.80") //
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.10.1.99")
		   		    	
		   		    	
		   		    	
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.19") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.42")	   		    	
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.55") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.149")
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.161") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.191")
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.219") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.246")
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.297") 
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.1") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.13")
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.188") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.515")
		   		    )
		   		    {		   		    	
		   		    	 oids =                
							 new String[] {               
								"1.3.6.1.4.1.2011.5.1.1.1.4"};
			   			oids2 = 
			   				new String[] {
			   					"1.3.6.1.4.1.2011.5.25.31.1.1.1.1.6"};
		   		    }
					//add at 2012-06-05 by hp
					if (node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.62.2.4") 
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.62.2.2")
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.62.2.8") 
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.6.2") //MA5200
					) {
				   		//NE40E-3 1.3.6.1.4.1.2011.2.62.2.8    昆山广电 以WALK的MIB文件验证过
				   		//NE80E 1.3.6.1.4.1.2011.2.62.2.2 昆山广电 以WALK的MIB文件验证过
						//NE5000EMulti 1.3.6.1.4.1.2011.2.62.2.4 
						oids = new String[] { "1.3.6.1.4.1.2011.6.3.4.1.4" }; //前5分钟利用率
						oids2 = new String[] { "1.3.6.1.4.1.2011.6.3.4.1.4" };
						// 
					}
		   		//-----1.3.6.1.4.1.2011.2.31 NE40
		   		    if(node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.31"))
		   		    {  		    	
		   		    	//System.out.println("======中调-----"+node.getIpAddress());
		   		    	 oids =                
							  new String[] {               
								"1.3.6.1.4.1.2011.2.17.4.4.1.7"};
			   			oids2 = 
			   				new String[] {"1.3.6.1.4.1.2011.2.17.4.4.1.7"};
			   			//System.out.println("======中调-----"+node.getIpAddress());
		   		    }
		   		    
		   		    if(node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12809") || node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12810")
		   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12811") || node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12830")
		   		    	||node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12831") || node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12840")
		   		    	||node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12880") )
		   		    {  		
		   		    	// ar28-09 1.3.6.1.4.1.2011.1.1.1.12809    
		   		    	// ar28-10 1.3.6.1.4.1.2011.1.1.1.12810
				   		// ar28-11 1.3.6.1.4.1.2011.1.1.1.12811
				   		// ar28-30 1.3.6.1.4.1.2011.1.1.1.12830
				   		// ar28-31 1.3.6.1.4.1.2011.1.1.1.12831
				   		// ar28-40 1.3.6.1.4.1.2011.1.1.1.12840
		   		    	// ar28-80 1.3.6.1.4.1.2011.1.1.1.12880
		   		    	oids =                
							new String[] {               
								"1.3.6.1.4.1.2011.2.2.4.12"};
			   			oids2 = 
			   				new String[] {
			   					"1.3.6.1.4.1.2011.2.2.4.12"};
		   		    }
		   		//1.3.6.1.4.1.2011.2.49 NE20 wxy add
		   		    if(node.getSysOid().equals("1.3.6.1.4.1.2011.2.49")||node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.62.2.5") || node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.88.2")
		   		    	||node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.62.2.3") || node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.62.2.9"))
		   		    {
		   		    	//NE40E 	1.3.6.1.4.1.2011.2.62.2.5 
		   		    	//NE 5000E	1.3.6.1.4.1.2011.2.62.2.3
		   		    	//System.out.println("======中调-----"+node.getIpAddress());
		   		    	 oids =                
							  new String[] {               
								"1.3.6.1.4.1.2011.5.25.31.1.1.1.1.5"};
			   			oids2 = new String[] {"1.3.6.1.4.1.2011.5.25.31.1.1.1.1.5"};
			   			//System.out.println("======中调-----"+node.getIpAddress());
		   		    }		   		    

		   		   if(node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.97") || node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.95")
		   				   || node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.91") || node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.170.1")
		   				   || node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.170.3") || node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.183.1")
		   		   )
		   		    {		   		    	
		   		    	 oids =                
							  new String[] {               
								"1.3.6.1.4.1.2011.5.25.31.1.1.1.1.5"};
			   			oids2 = new String[] {"1.3.6.1.4.1.2011.5.25.31.1.1.1.1.5"};
		   		    }
		   		
		   		    
		   		  if(node.getSysOid().trim().equals("1.3.6.1.4.1.2011.10.1.88"))
		   		    {
		   		    	
		   		    	 oids =                
							  new String[] {               
								"1.3.6.1.4.1.2011.5.12.2.1.1.1.1.5"};
			   			oids2 = new String[] {"1.3.6.1.4.1.2011.5.12.2.1.1.1.1.5"};
		   		    }
		   	        // wxy update Quidway AR46-40 1.3.6.1.4.1.2011.2.45
					if (node.getSysOid().equals("1.3.6.1.4.1.2011.2.45") || node.getSysOid().trim().equals("1.3.6.1.4.1.2011.10.1.88")) {
						oids = new String[] { "1.3.6.1.4.1.2011.5.12.2.1.1.1.1.5" };
						oids2 = new String[] { "1.3.6.1.4.1.2011.5.12.2.1.1.1.1.5" };
					}
		   		  if(node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.170.2") || node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.170.3"))
		   		    {
		   		    	
		   		    	 oids =                
							  new String[] {               
								"1.3.6.1.4.1.2011.6.3.4.1.2"};
			   			oids2 = new String[] {"1.3.6.1.4.1.2011.6.3.4.1.2"};
		   		    }
		   			
		   			
		   			
		   			 
		   		//valueArray = snmp.getCpuTableData(node.getIpAddress(),node.getCommunity(),oids);
		   		  	valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
		   			if(valueArray==null||valueArray.length==0){//yangjun add  
		   				//valueArray = snmp.getCpuTableData(node.getIpAddress(), node.getCommunity(), oids2);
		   				valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
			   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
		   			}
		   			int allvalue=0;
		   			int flag = 0;
					if(valueArray != null && valueArray.length > 0){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {
					   		
					   		String _value = valueArray[i][0];
					   		String index = valueArray[i][1];
					   		int value=0;
					   		value=Integer.parseInt(_value);
					   		allvalue = allvalue+Integer.parseInt(_value);
							if(value >0){
								flag = flag +1;
						   		List alist = new ArrayList();
						   		alist.add(index);
						   		alist.add(value+"");
						   		
						   		//SysLogger.info(host.getIpAddress()+" "+index+" CPU1 Value:"+_value);
						   		cpuList.add(alist);
							}
					   	  }
					}
					
					if(flag >0){
						int intvalue = (allvalue/flag);
						temp = intvalue+"";
						//SysLogger.info(host.getIpAddress()+" cpu "+allvalue/flag);
					}
		   		  }else if(node.getSysOid().startsWith("1.3.6.1.4.1.25506.")){
		   			  
		   			String[][] valueArray = null;
		   			String[] oids = new String[] {"1.3.6.1.4.1.2011.10.2.6.1.1.1.1.6"};//CPU利用率
		   			String[] oids2 = new String[] {"1.3.6.1.4.1.25506.2.6.1.1.1.1.6"};
		   			String[] oids3 = new String[]{"1.3.6.1.4.1.2011.6.1.1.1.4"};//HONGLI add
		   			if(node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.25506.1.149") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.297") 
			   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.19") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.42")	   		    	
			   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.55") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.149")
			   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.161") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.191")
			   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.219") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.246")   		    	
			   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.1") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.13")
			   		    	|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.188") || node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.515")	
		   			){
		   				oids = new String[]{"1.3.6.1.4.1.2011.6.1.1.1.4"};
		   			}
		   		//valueArray = snmp.getCpuTableData(node.getIpAddress(),node.getCommunity(),oids);
		   			valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
		   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
		   			if(valueArray==null||valueArray.length==0){//hukelei add  
		   				//valueArray = snmp.getCpuTableData(node.getIpAddress(), node.getCommunity(), oids2);
		   				valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids2,node.getSnmpversion(),
			   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
		   			}
		   			if(valueArray==null||valueArray.length==0){//HONGLI add  
		   				//valueArray = snmp.getCpuTableData(node.getIpAddress(), node.getCommunity(), oids3);
		   				valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids3,node.getSnmpversion(),
			   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
		   			}
		   			int allvalue=0;
		   			int flag = 0;
					if(valueArray != null && valueArray.length > 0){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {

					   		String _value = valueArray[i][0];		   		
					   		String index = valueArray[i][1];
					   		
					   		
					   		int value=0;
					   		value=Integer.parseInt(_value);
							allvalue = allvalue+Integer.parseInt(_value);
							if(value >0){
								flag = flag +1;
						   		List alist = new ArrayList();
						   		alist.add(index);
						   		alist.add(_value);
						   		cpuList.add(alist);					   		
						   		//SysLogger.info(host.getIpAddress()+" "+index+" CPU2 Value:"+_value);
							}
					   	  }
					}
					
					if(flag >0){
						int intvalue = (allvalue/flag);
						temp = intvalue+"";
						//SysLogger.info(host.getIpAddress()+" cpu "+allvalue/flag);
					}
		   		  } 
		   		  
		   		  if(temp == null){
		   			  result = 0;
		   		  }else{
		   			  try{
		   				  if(temp.equalsIgnoreCase("noSuchObject")){
		   					result = 0;
		   				  }else
		   					  result = Integer.parseInt(temp); 
		   			  }catch(Exception ex){
		   				  ex.printStackTrace();
		   				  result = 0;
		   			  }
		   		  }
		   		  
		   		  //SysLogger.info(host.getIpAddress() + "_H3CSnmp value="+result );
				  cpudata=new CPUcollectdata();
				  cpudata.setIpaddress(node.getIpAddress());
				  cpudata.setCollecttime(date);
				  cpudata.setCategory("CPU");
				  cpudata.setEntity("Utilization");
				  cpudata.setSubentity("Utilization");
				  cpudata.setRestype("dynamic");
				  cpudata.setUnit("%");		
				  cpudata.setThevalue(result+"");
				  SysLogger.info(node.getIpAddress()+" CPU "+result+"%");
					
				  //if (cpudata != null && !cpuusage.equalsIgnoreCase("noSuchObject"))
				  cpuVector.add(0, cpudata);
				  //if(cpuList != null && cpuList.size()>0){
					  cpuVector.add(1, cpuList);
				  //}
				  //cpuVector.addElement(cpudata);
		   	  }
			  
			  
		   	  catch(Exception e)
		   	  {
		   	  }	
		   	if (cpuVector.size() == 0) {
				//没有采集到数据
   				cpudata=new CPUcollectdata();
			    cpudata.setIpaddress(node.getIpAddress());
				cpudata.setCollecttime(date);
				cpudata.setCategory("CPU");
				cpudata.setEntity("Utilization");
				cpudata.setSubentity("unknown");
				cpudata.setRestype("dynamic");
				cpudata.setUnit("%");		
				cpudata.setThevalue("0");
				SysLogger.info(node.getIpAddress()+" CPU 没有采集到数据");
				cpuVector.add(cpudata);
			} 
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	    
	    if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
		{
			Hashtable ipAllData = new Hashtable();
			if(ipAllData == null)ipAllData = new Hashtable();
			if(cpuVector != null && cpuVector.size()>0)ipAllData.put("cpu",cpuVector);
			if(cpuList != null && cpuList.size()>0)ipAllData.put("cpulist",cpuList);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		}else
		 {
			if(cpuVector != null && cpuVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("cpu",cpuVector);
			if(cpuList != null && cpuList.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("cpulist",cpuList);
		   
		 }
	    returnHash.put("cpu", cpuVector);
	    
	    
	    //对CPU值进行告警检测
	    Hashtable collectHash = new Hashtable();
		collectHash.put("cpu", cpuVector);
		
		
		
	    try{
	    	if(cpuVector != null && cpuVector.size() > 0){
				for(int i = 0 ; i < cpuVector.size(); i++){
					CPUcollectdata cpucollectdata = (CPUcollectdata)cpuVector.get(0); 
					if("Utilization".equals(cpucollectdata.getEntity())){
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.updateData(node, nodeGatherIndicators, cpucollectdata.getThevalue());
					}
				}
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	  //把结果转换成sql
	    NetcpuResultTosql tosql=new NetcpuResultTosql();
	    tosql.CreateResultTosql(returnHash, node.getIpAddress());
	    String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	    if(!"0".equals(runmodel)){
			//采集与访问是分离模式,则不需要将监视数据写入临时表格
	    	NetHostDatatempCpuRTosql totempsql=new NetHostDatatempCpuRTosql();
		    totempsql.CreateResultTosql(returnHash, node);
	    }
	    
	    
	    cpuVector=null;
		cpuList=null;
	    
	    return returnHash;
	}

}
