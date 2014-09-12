/**
 * <p>Description:机群中存活主机监视</p>
 * <p>Company: dhcc.com</p>
 * @author afu
 * @project 衡水信用社
 * @date 2007-03-23
 */

package com.afunms.monitor.executor;

import com.afunms.application.dao.ActiveServerAlarmDao;
import com.afunms.application.model.ActiveServerAlarm;
import com.afunms.inform.model.*;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.item.CommonItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.common.util.SysUtil;
import com.afunms.polling.base.*;
import com.afunms.polling.node.*;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import java.util.*;

public class ActiveServer extends SnmpMonitor implements MonitorInterface
{
   
   public ActiveServer()
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
	   CommonItem item = (CommonItem)monitoredItem;
	   item.setSingleResult(-1);
	   item.setAlarm(false);
	   
       String currentAlias = snmp.getMibValue(host.getIpAddress(),host.getCommunity(),"1.3.6.1.2.1.1.5.0");
       
       /**
        * 如果机器相同，说明活动主机没有切换过
        */
       if(host.getAlias().equals(currentAlias)) 
       {
    	   item.setViolateTimes(0);
    	   return;
       }

       
       String alarmMsg = "活动主机切换，从" + host.getAlias() + "切换到" + currentAlias;
       item.setAlarm(true);       	   	   
       item.setAlarmInfo(alarmMsg);       
       host.setAlias(currentAlias);
       
       Alarm vo = new Alarm();
       vo.setIpAddress(node.getIpAddress());
       vo.setLevel(3);		  
	   vo.setMessage(alarmMsg);		  
	   vo.setCategory(host.getCategory());
	   vo.setLogTime(SysUtil.getCurrentTime());			
	   node.getAlarmMessage().add(vo);	

	   ActiveServerAlarm asa = new ActiveServerAlarm();
       asa.setIpAddress(node.getIpAddress());  
       asa.setEvent(alarmMsg);		  
       asa.setLogTime(SysUtil.getCurrentTime());
       ActiveServerAlarmDao asaDao = new ActiveServerAlarmDao();
       asaDao.save(asa);
       
       HostNodeDao hnDao = new HostNodeDao();
       HostNode hn = new HostNode();
       hn.setId(host.getId());
       hn.setAlias(host.getAlias());
       hnDao.update(hn);
   }
   
   public void analyseData(Node node,MonitoredItem monitoredItem)
   {
   }   
}