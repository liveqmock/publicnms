/**
 * <p>Description:被监视指标(telnet)</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-26
 */

package com.afunms.monitor.item;

import java.util.Hashtable;

import com.afunms.common.util.SysLogger;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.topology.dao.NodeMonitorDao;
import com.afunms.topology.model.NodeMonitor;
import com.afunms.topology.model.TelnetConfig;

public class TelnetItem extends MonitoredItem
{
	private static final long serialVersionUID = 710493505125L;
	
	private static Hashtable telnetHash;	
	static
	{
		NodeMonitorDao nmDao = new NodeMonitorDao();  
		telnetHash = nmDao.loadTelnetConfig();	
		nmDao.close();
	}
	
	//---------------------------------------------------------
	private String user;  //用户名
	private String password; //密码
	private String prompt; //命令提示符

	public TelnetItem()
	{	
	}
	
	public void loadSelf(NodeMonitor nm)
	{
		loadCommon(nm);
    	if(telnetHash.get(String.valueOf(nm.getNodeID()))!=null)
    	{		
    	    TelnetConfig tc = (TelnetConfig)telnetHash.get(String.valueOf(nm.getNodeID()));                	
    	    setUser(tc.getUser());
    	    setPassword(tc.getPassword());
    	    setPrompt(tc.getPrompt());
    	}   
    	else
    	   SysLogger.error("Server " + nm.getNodeID() + " has not config telnet!!"); 			
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}	
}
