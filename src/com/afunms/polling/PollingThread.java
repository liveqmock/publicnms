/**
 * <p>Description:polling resource</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-28
 */

package com.afunms.polling;

import java.util.List;
import java.util.ArrayList;

import com.afunms.polling.base.*;
import com.afunms.monitor.executor.base.*;
import com.afunms.initialize.ResourceCenter;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.inform.model.Alarm;
import com.afunms.common.util.*;
import com.afunms.monitor.item.base.MoidConstants;
import com.afunms.monitor.item.ServiceItem;

public class PollingThread extends Thread
{
	private static final int THREAD_INTERVAL = ResourceCenter.getInstance().getPollingThreadInterval();
	private List nodeList;
	
	public PollingThread()
	{
		nodeList = new ArrayList(20);
	}
	
	public List getNodeList()
	{
	    return nodeList;	
	}
	
	public void run()
	{	   	
		/*
	   long startTime = 0;
	   while(true) //无限循环
	   {
		  //开始时把状态复位，以后每个moid测试这个状态，如果为0就不再继续
		  startTime = SysUtil.getCurrentLongTime();		  	  
		  for(int i=0;i<nodeList.size();i++)
		  {			
			 Node host = (Node)nodeList.get(i); 			 			
			 if(!host.isManaged()) continue;
			 
			 host.setAlarm(false);
			 host.getAlarmMessage().clear();			 			 
			 List moidList = host.getMoidList();			 
			 for(int j=0;j<moidList.size();j++)
			 {				
				 MonitoredItem item = (MonitoredItem)moidList.get(j);	
				 if(SysUtil.getCurrentLongTime() < item.getNextTime()) continue;
				 if(j != 0 && host.getStatus() != 1 ) continue; //如果状态不正常，则不要采集其他指标				 
				 MonitorInterface monitor = MonitorFactory.getMonitor(item.getMoid());
				 monitor.collectData(host,item); //1.收集数据								     
				 monitor.analyseData(host,item); //2.分析数据
				 item.setLastTime(SysUtil.getCurrentLongTime()); //一定放在collectData后面
				 PollingDao.getInstance().addItemInfo(host.getId(),item); //3.保存数据
				 if(j==0) //第一个是用来确定这个节点是否可用的
				 { 		
					 host.setLastTime(SysUtil.longToTime(item.getLastTime()));
					 host.setNextTime(SysUtil.longToTime(item.getNextTime()));
					 if(item.getSingleResult()==-1 && host.getCategory()==4 ) //如果是服务器，当服务器不可用时，所有服务清0 
					 {
					     ServiceItem si = (ServiceItem)host.getItemByMoid(MoidConstants.TEST_SERVICE);
					     for(int k=0;k<si.getServicesStatus().length;k++)
					    	 si.getServicesStatus()[k] = 0;
					 }		             
				 }					
			 }    			 
		     if(host.getAlarmMessage().size()>0)
		     {	 
		         Alarm alarmInfo = (Alarm)host.getAlarmMessage().get(host.getAlarmMessage().size()-1); //最后一条报警			 
		         host.setLastAlarm(alarmInfo.getMessage());
		         PollingDao.getInstance().addAlarmInfo(host.getAlarmMessage());
		     }
		     else
		        host.setLastAlarm("");		     		     		 
		  }//end_for_i
	      try
	      {     	   
        	  if(SysUtil.getCurrentLongTime() - startTime < THREAD_INTERVAL) 
                  Thread.sleep(THREAD_INTERVAL - (SysUtil.getCurrentLongTime() - startTime)); 
	      }
	      catch(InterruptedException ie)
	      {}	   		  
	   }//end_while
	   */
	}
}