/**
 * <p>Description:device response time</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.monitor.item;

import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.topology.model.NodeMonitor;

public class ResponseTimeItem extends MonitoredItem
{
	public ResponseTimeItem()
	{
	}
	
	public void loadSelf(NodeMonitor nm)
	{
		loadCommon(nm);
	}		
}