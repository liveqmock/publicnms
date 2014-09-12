/**
 * <p>Description:collect interface operStatus</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-16
 */

package com.afunms.monitor.executor;

import java.util.Hashtable;

import com.afunms.common.util.*;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.SnmpItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.inform.model.Alarm;
import com.afunms.polling.node.*;
import com.afunms.polling.base.*;
import com.afunms.topology.model.HostNode;

public class TrafficOperStatus extends SnmpMonitor implements MonitorInterface
{   
   public TrafficOperStatus()
   {
   }
   public void collectData(HostNode node){
	   
   }
   public Hashtable collect_Data(HostNode node){
	   return null;
   }
   public void collectData(Node node,MonitoredItem monitoredItem)
   {
	  Host host = (Host)node;
	  if(host.getInterfaceHash()==null||host.getInterfaceHash().size()==0) return;
	  
   	  SnmpItem item = (SnmpItem)monitoredItem;
   	  String[] oids = new String[]{"1.3.6.1.2.1.2.2.1.1","1.3.6.1.2.1.2.2.1.8","1.3.6.1.2.1.2.2.1.5"};
   	  String[][] valueArray = null;
   	  try
   	  {
   		 valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
   	  }
   	  catch(Exception e)
   	  {
   		 valueArray = null;
   		 SysLogger.error(host.getIpAddress() + "_TrafficOperStatus");
   	  }   	
   	  if(valueArray==null||valueArray.length==0) return;
	  item.setMultiResults(null); //这些值不用插入数据库  
	     		  
   	  int tempStatus = 0;
   	  for(int i=0;i<valueArray.length;i++)
   	  {
    	 if(valueArray[i][0]==null) continue;  
    	 
   		 if(host.getInterfaceHash().get(valueArray[i][0])!=null)
   		 {   			 
   			 if("1".equals(valueArray[i][1])) 
   				tempStatus = 1;
   			 else
   				tempStatus = 2;
   			 
   			 IfEntity ifEntity = (IfEntity)host.getInterfaceHash().get(valueArray[i][0]);
   			 ifEntity.setOperStatus(tempStatus);
   			 ifEntity.setSpeed(Long.parseLong(SysUtil.ifNull(valueArray[i][2],"0")));

   			 if(ifEntity.getOperStatus()!=tempStatus)
   			 {     				
   				Alarm vo = new Alarm();
   				if(tempStatus==1)
   				   vo.setMessage("端口[" + valueArray[i][0] + "]状态改变:从down变为up");
   				else
   				   vo.setMessage("端口[" + valueArray[i][0] + "]状态改变:从up变为down");				
				vo.setIpAddress(host.getIpAddress());
				vo.setLevel(item.getAlarmLevel());
				vo.setLogTime(SysUtil.getCurrentTime());
				vo.setCategory(host.getCategory());
				host.getAlarmMessage().add(vo);
   			 }
   		 }   		
   	  }
   }
   
   public void analyseData(Host host,MonitoredItem item) //覆盖BaseMonitor
   {
   }
}   