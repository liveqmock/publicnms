package com.afunms.polling.snmp.memory;


/*
 * @author yangjun@dhcc.com.cn
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
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetHostMemoryRtsql;
import com.gatherResulttosql.NetmemoryResultTosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NetscreenMemorySnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public NetscreenMemorySnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {	
		//yangjun
		Hashtable returnHash=new Hashtable();
		Vector memoryVector=new Vector();
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
    				SysLogger.info("######## "+node.getIpAddress()+" 不在采集内存时间段内,退出##########");
    				//清除之前内存中产生的告警信息
    			    try{
    			    	//清除之前内存中产生的内存告警信息
    			    	NodeDTO nodedto = null;
    					NodeUtil nodeUtil = new NodeUtil();
    					nodedto = nodeUtil.creatNodeDTOByHost(node);
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.deleteEvent(node.getId()+"", nodedto.getType(), nodedto.getSubtype(), "memory", null);
    			    }catch(Exception e){
    			    	e.printStackTrace();
    			    }
    				return returnHash;
    			}
    			
    		}
    	}
    	
		try {
			Memorycollectdata memorydata=null;
			Calendar date=Calendar.getInstance();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			  try{
					
					String[] oids =                
						new String[] {               
							"1.3.6.1.4.1.3224.16.2.1",//All Memory Size
							"1.3.6.1.4.1.3224.16.2.2"}; //process Left Size
					String[] oids1 =                
						new String[] {               
							"1.3.6.1.4.1.3224.16.2.2" };
					String[][] valueArray = null; 
					int allMemorySize=0;
					float value=0.0f;
					int leftsize=0;
					int allUsedSize=0;
					
					try {
						if(node.getSnmpversion()==0||node.getSnmpversion()==1){
							valueArray=SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
							}else if(node.getSnmpversion()==3){
								valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(),node.getCommunity(),oids,node.getSnmpversion(),
							  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
					   				
							}
					} catch(Exception e){
						valueArray = null;
						SysLogger.error(node.getIpAddress() + "_NetscreenSnmp");
					}
					if(valueArray != null){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {
					   		  if(valueArray[i][0] != null && valueArray[i][1] != null){
					   			leftsize=Integer.parseInt(valueArray[i][0]);
					   			allMemorySize=Integer.parseInt(valueArray[i][1]); 
						   		break;
					   		  }
					   		 
					   	  }
					}
					if(allMemorySize!=0){
						value=(allMemorySize-leftsize)*100f/allMemorySize;
					}
					else{
						value = 0;
						//throw new Exception("allMemorySize is 0");
					}
					//##########HONGLI ADD  凤凰传媒集团juniper 防火墙
					String[] oids2 = new String[]{
						"1.3.6.1.4.1.2636.3.39.1.12.1.1.1.5"
					};
					if(valueArray == null || valueArray.length == 0){
						valueArray=SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
						//valueArray = snmp.getTableData(node.getIpAddress(),node.getCommunity(),oids2);
//						valueArray = SnmpUtils.getTableData(node.getIpAddress(),node.getCommunity(),oids2,node.getSnmpversion(),
//			   		  			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase(),3,1000*30);
					}
					int flag=0;
//					if(valueArray != null){
//						for(int i=0; i<valueArray.length;i++){
//							value = (Integer.parseInt(valueArray[0][0]) + Integer.parseInt(valueArray[0][1]))/2;
//							
//				   		  }
//					}
					System.out.println("memory value --->"+value);
					//##########END
					memorydata=new Memorycollectdata();
					memorydata.setIpaddress(node.getIpAddress());
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("Utilization");
					memorydata.setSubentity("PhysicalMemory");
					memorydata.setRestype("dynamic");
					memorydata.setUnit("%");
					memorydata.setThevalue((int)Math.rint(value/1)+"");
					memoryVector.addElement(memorydata);
					
//					memorydata=new Memorycollectdata();
//					memorydata.setIpaddress(node.getIpAddress());
//					memorydata.setCollecttime(date);
//					memorydata.setCategory("Memory");
//					memorydata.setEntity("Allocate");
//					memorydata.setRestype("static");
//					memorydata.setSubentity("PhysicalMemory");
//				
//					float size=0.0f;
//					size=allMemorySize*1.0f/1024;			
//					if(size>=1024.0f){
//						size=size/1024;
//						memorydata.setUnit("G");
//					}
//					else{
//						memorydata.setUnit("M");
//					}				
//					memorydata.setThevalue((int)Math.rint(size/1)+"");
//					memoryVector.addElement(memorydata);
//					memorydata=new Memorycollectdata();
//					memorydata.setIpaddress(node.getIpAddress());
//					memorydata.setCollecttime(date);
//					memorydata.setCategory("Memory");
//					memorydata.setEntity("UsedSize");
//					memorydata.setRestype("static");
//					memorydata.setSubentity("PhysicalMemory");
//							size=(allMemorySize-leftsize)*1.0f/1024;						
//							if(size>=1024.0f){
//								size=size/1024;
//								memorydata.setUnit("G");
//							}
//							else{
//								memorydata.setUnit("M");
//							}	
//							memorydata.setThevalue((int)Math.rint(size/1)+"");
//							memoryVector.addElement(memorydata);
					}
					catch(Exception e){
						e.printStackTrace();
					}
		   	  //-------------------------------------------------------------------------------------------内存 end	
		}
		catch(Exception e){
			//returnHash=null;
			//e.printStackTrace();
			//return null;
		}
		
		if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
		{
			Hashtable ipAllData = new Hashtable();
			if(ipAllData == null)ipAllData = new Hashtable();
			if(memoryVector != null && memoryVector.size()>0)ipAllData.put("memory",memoryVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		}else{
			if(memoryVector != null && memoryVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("memory",memoryVector);
		   
		 }
	    returnHash.put("memory",memoryVector);

	    Hashtable collectHash = new Hashtable();
		collectHash.put("memory", memoryVector);

		//对内存值进行告警检测
	    try {
	    	if(memoryVector != null && memoryVector.size() > 0){
				int thevalue = 0;
				for(int i = 0 ; i < memoryVector.size(); i++){
					Memorycollectdata memorycollectdata = (Memorycollectdata)memoryVector.get(i);
					if("Utilization".equals(memorycollectdata.getEntity())){
						if(Integer.parseInt(memorycollectdata.getThevalue())>thevalue){
							thevalue = Integer.parseInt(memorycollectdata.getThevalue());
						}
					}
				}
				CheckEventUtil checkutil = new CheckEventUtil();
				checkutil.updateData(node, nodeGatherIndicators, thevalue+"",null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//SysLogger.error("CISCO 内存 告警出错" , e);
		}
		memoryVector=null;
	    //把采集结果生成sql
	    NetmemoryResultTosql tosql=new NetmemoryResultTosql();
	    tosql.CreateResultTosql(returnHash, node.getIpAddress());
	    String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	    if(!"0".equals(runmodel)){
			//采集与访问是分离模式,则不需要将监视数据写入临时表格
	    	NetHostMemoryRtsql  totempsql=new NetHostMemoryRtsql();
		    totempsql.CreateResultTosql(returnHash, node);
	    }
	    
	    
	    return returnHash;
	}
}





