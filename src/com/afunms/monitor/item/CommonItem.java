package com.afunms.monitor.item;

import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.topology.model.NodeMonitor;

public class CommonItem extends MonitoredItem
{
	public CommonItem()
	{	
	}
	
	public void loadSelf(NodeMonitor nm)
	{
		loadCommon(nm);
	}	
}
