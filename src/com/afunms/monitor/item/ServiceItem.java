/**
 * <p>Description:被监视指标(db)</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-06
 */

package com.afunms.monitor.item;

import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.topology.model.NodeMonitor;
import com.afunms.initialize.ResourceCenter;

public class ServiceItem extends MonitoredItem
{
	private static final long serialVersionUID = 827391995595L;	
	private int[] servicesStatus; //每个服务的状态1=正常,0=不正常
	
	public ServiceItem()
	{	
		int len = ResourceCenter.getInstance().getServiceList().size();
		servicesStatus = new int[len];
	}
	
	public void loadSelf(NodeMonitor nm)
	{
		loadCommon(nm);
 	}
	
	public int[] getServicesStatus() {
		return servicesStatus;
	}

	public void setServicesStatus(int[] servicesStatus) {
		this.servicesStatus = servicesStatus;
	}	
}	