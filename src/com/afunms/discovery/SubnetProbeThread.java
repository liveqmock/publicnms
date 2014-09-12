/**
 * <p>Description:probe subnet</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project chongqing electric power
 * @date 2006-12-18
 */

package com.afunms.discovery;

import java.util.*;

import com.afunms.common.util.*;

public class SubnetProbeThread extends BaseThread
{
	private List ipList;
    public SubnetProbeThread(List ipList)
    {
    	this.ipList = ipList;    	
    }
    
	public void run()
    {
		int deviceType = 0;
		for(int i=0;i<ipList.size();i++)
	    {		  
		   String curAddress = (String)ipList.get(i);
		   String tmpCommunity = SnmpUtil.getInstance().getCommunity(curAddress);
		   if(tmpCommunity==null) continue;
		  
		   String tmpSysOid = SnmpUtil.getInstance().getSysOid(curAddress,tmpCommunity);
		   if(tmpSysOid==null) continue;
		   
		   deviceType = SnmpUtil.getInstance().checkDevice(curAddress,tmpCommunity,tmpSysOid);
		   if(deviceType>0)
		   {		  
		      Host host = new Host();   	  
		      host.setCategory(deviceType);
		      host.setIpAddress(curAddress);
		      host.setCommunity(tmpCommunity);
		      host.setSysOid(tmpSysOid);
		      host.setSuperNode(0);
		      host.setLayer(1);
		      host.setLocalNet(-1);   	     	  
		      DiscoverEngine.getInstance().addHost(host,null);	    	 
		   }
	    }				
		setCompleted(true);
    }	
}