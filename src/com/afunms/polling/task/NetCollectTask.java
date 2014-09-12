package com.afunms.polling.task;

import java.util.*;

import com.afunms.common.util.SnmpUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.discovery.IpAddress;
import com.afunms.discovery.IpRouter;
import com.afunms.polling.base.*;
import com.afunms.polling.node.*;
import com.afunms.polling.PollingEngine;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.monitor.executor.*;

public class NetCollectTask extends BaseTask
{
    public void executeTask()
    {
    	//网络设备数据采集
    	HostNodeDao nodeDao = new HostNodeDao();    	
    	List nodeList = nodeDao.loadOrderByIP();      	
    	for(int i=0;i<nodeList.size();i++)
    	{    		
    		HostNode node = (HostNode)nodeList.get(i);
    		//SysLogger.info(node.getIpAddress()+" "+node.getCategory()+" "+node.isManaged());
    		if(node.getCategory()==5 || !node.isManaged()) continue; //暂时不对打印机进行监控
    		if(node.getSysOid().indexOf("1.3.6.1.4.1.25506")>=0){
    			//H3C设备
    			HuaweiSnmp h3cSnmp = new HuaweiSnmp();
    			h3cSnmp.collectData(node);
    		}
    		
    		//loadOne(node);    		
    	}
    }
	
	public boolean timeRestricted()
	{
		return true;
	}
	
	private boolean linkExist(LinkRoad link)
	{
		//需要判断是管理IP还是IP别名
	    Host host1 = (Host)PollingEngine.getInstance().getNodeByID(link.getStartId());
	    Host host2 = (Host)PollingEngine.getInstance().getNodeByID(link.getEndId());
	    IfEntity if1 = host1.getIfEntityByIndex(link.getStartIndex());
	    IfEntity if2 = host2.getIfEntityByIndex(link.getEndIndex());
	    SysLogger.info(host1.getIpAddress()+"/"+if1.getOperStatus()+"<--->"+host2.getIpAddress()+"/"+if2.getOperStatus());
	    if(if1.getOperStatus()==2 || if2.getOperStatus()==2)
	    	return false;
	    if(host1.getCategory()==4 || host2.getCategory()==4) //有一端是服务器的则只检查交换机端口是否down
	    {
	        if(host1.getCategory()==4 && if2.getOperStatus()==2)
	           return false;
	        else if(host2.getCategory()==4 && if1.getOperStatus()==2)
	           return false;
	        else
	           return true;	
	    }
	    return true;	    	
	}
				
}