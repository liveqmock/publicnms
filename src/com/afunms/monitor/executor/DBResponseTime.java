/**
 * <p>Description:数据库响应时间</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-8
 */

package com.afunms.monitor.executor;

import java.sql.*;
import java.util.Hashtable;

import com.afunms.monitor.executor.base.BaseMonitor;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.item.CommonItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.topology.model.HostNode;
import com.afunms.application.util.DBPool;

public class DBResponseTime extends BaseMonitor implements MonitorInterface
{
	public DBResponseTime()
	{		
	}
	   public void collectData(HostNode node){
		   
	   }
	   public Hashtable collect_Data(HostNode node){
		   return null;
	   }
	public void collectData(Node node,MonitoredItem monitoredItem)
	{
		CommonItem item = (CommonItem)monitoredItem;
		DBPool.getInstance().removeConnect(node.getId());
						
        int result = 0;        
        long startTime = System.currentTimeMillis();        
        Connection conn = DBPool.getInstance().getConnection(node.getId());                       
        if(conn==null)
        {
            result = -1;
            node.setFailTimes(node.getFailTimes() + 1);
        }
        else   
        {
            result = (int)(System.currentTimeMillis() - startTime);
            node.setNormalTimes(node.getNormalTimes() + 1);     
        }
        item.setSingleResult(result);
	}	
	
	public void analyseData(Node node,MonitoredItem item)
	{		
	}
}