package com.afunms.polling.snmp.cdp;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtil;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.model.IpAlias;
import com.afunms.discovery.CdpCachEntryInterface;
import com.afunms.discovery.IfEntity;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.NDP;
import com.afunms.polling.task.ThreadPool;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.NDPDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.util.TopoHelper;
import com.afunms.topology.util.XmlOperator;
import com.gatherResulttosql.NetHostNDPRttosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CDPSingleSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public CDPSingleSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash=new Hashtable();
		Vector cdpVector=new Vector();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		SysLogger.info("#################process#########");
		if(node == null)return returnHash;
		SysLogger.info("#################process1#########");
		try {
			Calendar date=Calendar.getInstance();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			//---------------------------------------------------得到所有NDP start
			     try
			     {
			    	 String[] oids = new String[]
			    	                            {"1.3.6.1.4.1.9.9.23.1.2.1.1.4",   //1.cdpCacheAddress
                		     					 "1.3.6.1.4.1.9.9.23.1.2.1.1.7",   //2.cdpCacheDevicePort
			    	                            };  
  
					String[][] valueArray = null;   	  
					try {
						valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
					} catch(Exception e){
						valueArray = null;
					}
					if(valueArray==null) return null;
				       CdpCachEntryInterface cdp = null;
				   	  for(int i=0;i<valueArray.length;i++)
				   	  {
				   		cdp = new CdpCachEntryInterface();
				   		if(valueArray[i][0] == null)continue;
						cdp.setIp(ciscoIP2IP(valueArray[i][0]));
						cdp.setPortdesc(valueArray[i][1]);
						cdpVector.addElement(cdp);
				   		SysLogger.info(node.getIpAddress()+"   deviceid:"+cdp.getIp()+"   portname:"+cdp.getPortdesc());
				   	  }	
				   	valueArray = null;
			    }catch (Exception e)
			    {
			        e.printStackTrace();
			    }
			  
			    //---------------------------------------------------得到NDP end	
			}catch(Exception e){
			}
	    returnHash.put("cdp", cdpVector);
	    if(!(ShareData.getSharedata().containsKey(node.getIpAddress())))
		{
			Hashtable ipAllData = new Hashtable();
			if(ipAllData == null)ipAllData = new Hashtable();
			if(cdpVector != null && cdpVector.size()>0)ipAllData.put("cdp",cdpVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		}else{
			if(cdpVector != null && cdpVector.size()>0)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).put("cdp",cdpVector);
		   
		}
	    	    
	    //把采集结果生成sql
	    NetHostNDPRttosql ndptosql=new NetHostNDPRttosql();
	    ndptosql.CreateResultTosql(cdpVector, node);
	    
	    cdpVector=null;
	    return returnHash;
	}
	
	//c0:a8:01:f7  ->  192.168.1.247
	public String ciscoIP2IP(String ciscoip){
		
		String[] s = ciscoip.split(":");
		if( 4 == s.length ){
			return ""+Integer.parseInt(s[0], 16)+"."+Integer.parseInt(s[1], 16)+"."+Integer.parseInt(s[2], 16)+"."+Integer.parseInt(s[3], 16);
		}
		
		return "";
	}

}





