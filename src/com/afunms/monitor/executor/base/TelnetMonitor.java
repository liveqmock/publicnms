/**
 * <p>Description:monitor with telnet</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-18
 */

package com.afunms.monitor.executor.base;

import cn.org.xone.telnet.TelnetWrapper;

public class TelnetMonitor
{
    protected static TelnetWrapper telnet = new TelnetWrapper(); 
    
	public TelnetMonitor()
    {
    }               
}