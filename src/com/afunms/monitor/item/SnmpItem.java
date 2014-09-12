/**
 * <p>Description:被监视指标(snmp)</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-26
 */

package com.afunms.monitor.item;

import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.topology.model.NodeMonitor;

public class SnmpItem extends MonitoredItem
{
	private static final long serialVersionUID = 318159759136L;
	
	public SnmpItem()
	{	
	}
	
	public void loadSelf(NodeMonitor nm)
	{
		loadCommon(nm);
	}	
}
