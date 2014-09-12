package com.afunms.polling.snmp.cpu;

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



/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HPNetCpuSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public HPNetCpuSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {	
		Hashtable returnHash=new Hashtable();
		Vector cpuVector=new Vector();
		List cpuList = new ArrayList();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
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
				  
			  }
			  try {
				  //-------------------------------------------------------------------------------------------cpu start
		   		  String temp = "0";
		   		  //if(node.getSysOid().startsWith("1.3.6.1.4.1.2011.")){
		   			//temp = snmp.getMibValue(host.getIpAddress(),host.getCommunity(),"1.3.6.1.4.1.2011.6.1.1.1.4.0");
		   			String[][] valueArray = null;
		   			String[] oids =  new String[] { "1.3.6.1.4.1.11.2.14.11.5.1.9.6.1"};	   			
		   			
		   			//valueArray = snmp.getCpuTableData(node.getIpAddress(),node.getCommunity(),oids);
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
		   		  //}
		   		  
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
//				-------------------------------------------------------------------------------------------cpu end
		}
		catch(Exception e){
			//returnHash=null;
			//e.printStackTrace();
			//return null;
		}
		
//		Hashtable ipAllData = new Hashtable();
//		try{
//			ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		}catch(Exception e){
//			
//		}
//		if(ipAllData == null)ipAllData = new Hashtable();
//		if(cpuVector != null && cpuVector.size()>0)ipAllData.put("cpu",cpuVector);
//		if(cpuList != null && cpuList.size()>0)ipAllData.put("cpulist",cpuList);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
//	    returnHash.put("cpu", cpuVector);
	    
	    
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





