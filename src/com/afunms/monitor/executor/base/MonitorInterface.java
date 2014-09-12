/**
 * <p>Description:Monitor interface</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-17
 */

package com.afunms.monitor.executor.base;

import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.topology.model.HostNode;
import com.afunms.polling.node.Host;
import java.util.*;

public interface MonitorInterface
{
   public void collectData(Node node,MonitoredItem item);
   public Hashtable collect_Data(HostNode node);
   public void collectData(HostNode node);
   
   public void analyseData(Node node,MonitoredItem item);
}