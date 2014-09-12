/**
 * <p>Description:snmp tool</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.common.util;

import java.io.IOException;
import java.util.*;

import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableListener;
import org.snmp4j.util.TableUtils;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.PDU;

import com.afunms.discovery.*;



public class LinkSnmpUtil
{
  private static LinkSnmpService snmp;
  private static LinkSnmpUtil instance = new LinkSnmpUtil();
  
  public static synchronized LinkSnmpUtil getInstance()
  {
     return instance;
  }

  private LinkSnmpUtil() 
  {  	 
  	 snmp = new LinkSnmpService();
  }
  
  public LinkSnmpUtil(int version) 
  {  	 
  	 snmp = new LinkSnmpService(version);
  }
  

  /**
   * 得到H3C的结果表
   */
  public List getH3cConfigResultTable(String address,String community)
  {
     List tableValues = null;
     try
     {
        String[] oids = new String[]
                    {"1.3.6.1.4.1.2011.10.2.4.1.2.5.1.2",   //1.resultOptIndex
                     "1.3.6.1.4.1.2011.10.2.4.1.2.5.1.4"};  //OperateState

        //SysLogger.info("getH3cConfigResultTable(),ip=" + address + ",community=" + community);
       String[][] ipArray = snmp.getTableData(address,community,oids);
       if(ipArray==null) return null;

       tableValues = new ArrayList();
       IpAddress ipAddress = null;
       for (int i = 0; i < ipArray.length; i++)
       {   
    	   List alist = new ArrayList();
    	   //SysLogger.info(ipArray[i][0]+"======="+ipArray[i][1]);
    	   if(ipArray[i][0] != null && ipArray[i][0].trim().length()>0 && ipArray[i][1] != null && ipArray[i][1].trim().length()>0){
    		   alist.add(0,ipArray[i][0]);
    		   alist.add(1,ipArray[i][1]);
    	   }       	   
           tableValues.add(alist);
       }
    }
    catch (Exception e)
    {
    	SysLogger.error("getH3cConfigResultTable(),ip=" + address + ",community=" + community,e);
        tableValues = null;
    }
    return tableValues;
  }
  
  /**
   * 被监视对象本身mac
   */
  public String getHostBridgeAddress(String address,String community)
  {
	  String  value = null;
	  String hostmac = "";
	  try{
		  
		  String[] oids =                
			  new String[] {                
				  "1.3.6.1.2.1.2.2.1.6"
				  };
		  String[][] valueArray = null;
			try {
				valueArray = snmp.getTableData(address,community,oids);
			} catch(Exception e){
				valueArray = null;
			}	
			if(valueArray != null){
				for(int i=0;i<valueArray.length;i++){
					value=valueArray[i][0];
					
					if (value == null || value.length()==0)continue;
					if(hostmac==null){
						hostmac = value;
					}else{
					hostmac = hostmac +","+value;
					
					}
					
				}
				hostmac = hostmac.substring(1,hostmac.length());
			}				
		}
		catch(Exception e){
		}
	return hostmac;
	
  }

  
  /**
   * 得到CISCO的结果表
   */
  public List getCiscoConfigResultTable(String address,String community)
  {
     List tableValues = null;
     try
     {
        String[] oids = new String[]
                    {"1.3.6.1.4.1.9.9.96.1.1.1.1.10"   //ccCopyState 1:waiting 2:running 3:successful 4:failed
                     };

        //SysLogger.info("getH3cConfigResultTable(),ip=" + address + ",community=" + community);
       String[][] ipArray = snmp.getCpuTableData(address,community,oids);
       if(ipArray==null) return null;

       tableValues = new ArrayList();
       IpAddress ipAddress = null;
       for(int i = 0; i < ipArray.length; i++)
       {   
    	   List alist = new ArrayList();
    	   //SysLogger.info(ipArray[i][0]+"======="+ipArray[i][1]);
    	   if(ipArray[i][0] != null && ipArray[i][0].trim().length()>0 && ipArray[i][1] != null && ipArray[i][1].trim().length()>0){
    		   alist.add(0,ipArray[i][0]);
    		   alist.add(1,ipArray[i][1]);
    	   }       	   
           tableValues.add(alist);
       }
    }
    catch (Exception e)
    {
    	SysLogger.error("getH3cConfigResultTable(),ip=" + address + ",community=" + community,e);
        tableValues = null;
    }
    return tableValues;
  }
  
  /**
   * 得到所有IpNetToMedia,即直接与该设备连接的ip
   */
  public List getIpNetToMediaTable(String address,String community)
  {
     List tableValues = null;
     try
     {
        String[] oids = new String[]
                    {"1.3.6.1.2.1.4.22.1.1",   //1.ifIndex
        		     "1.3.6.1.2.1.4.22.1.2",   //2.mac
                     "1.3.6.1.2.1.4.22.1.3",   //3.ip
                     "1.3.6.1.2.1.4.22.1.4"};  //4.type

       String[][] ipArray = snmp.getTableData(address,community,oids);
       if(ipArray==null) return null;

       tableValues = new ArrayList();
       IpAddress ipAddress = null;
       for (int i = 0; i < ipArray.length; i++)
       {       	  
       	   if(!"3".equals(ipArray[i][3])) continue;    	   
       	   if(ipArray[i][1].length()!=17) continue;
       	   //SysLogger.info(address+" ifIndex:"+ipArray[i][0]+" mac: "+ipArray[i][1]);
       	   ipAddress = new IpAddress();
       	   ipAddress.setIfIndex(ipArray[i][0]);
       	   ipAddress.setPhysAddress(ipArray[i][1]);
       	   ipAddress.setIpAddress(ipArray[i][2]);       	   
           tableValues.add(ipAddress);
       }
    }
    catch (Exception e)
    {
    	SysLogger.error("getIpNetToMediaTable(),ip=" + address + ",community=" + community,e);
        tableValues = null;
    }
    return tableValues;
  }
  
  /**
   * 得到所有IpNetToMedia,即直接与该设备连接的ip
   */
  public List getAtInterfaceTable(String address,String community)
  {
     List tableValues = null;
     try
     {
        String[] oids = new String[]
                    {"1.3.6.1.2.1.4.22.1.1",   //1.ifIndex
        		     "1.3.6.1.2.1.4.22.1.2",   //2.mac
                     "1.3.6.1.2.1.4.22.1.3",   //3.ip
                     "1.3.6.1.2.1.4.22.1.4"};  //4.type

       String[][] ipArray = snmp.getTableData(address,community,oids);
       if(ipArray==null) return null;

       tableValues = new ArrayList();
       IpAddress ipAddress = null;
       for (int i = 0; i < ipArray.length; i++)
       {       	  
       	   if(!"3".equals(ipArray[i][3])) continue;    	   
       	   if(ipArray[i][1].length()!=17) continue;
       	   AtInterface at = new AtInterface();
		   at.setIfindex(Integer.parseInt(ipArray[i][0]));
		   at.setMacAddress(ipArray[i][1]);
		   at.setIpAddress(ipArray[i][2]);
           tableValues.add(at);
       }
    }
    catch (Exception e)
    {
    	SysLogger.error("getIpNetToMediaTable(),ip=" + address + ",community=" + community,e);
        tableValues = null;
    }
    return tableValues;
  }
  /**
   * 得到Huawei的NDP信息
   */
  public Hashtable getNDPTable(String address,String community)
  {
	  Hashtable tableValues = null;
     try
     {
        String[] oids = new String[]
                    {"1.3.6.1.4.1.2011.6.7.5.6.1.1",   //1.hwNDPPortNbDeviceId
        		     "1.3.6.1.4.1.2011.6.7.5.6.1.2",   //2.hwNDPPortNbPortName
                     "1.3.6.1.4.1.2011.6.7.5.6.1.3",   //3.hwNDPPortNbDeviceName
                     "1.3.6.1.4.1.2011.6.7.5.6.1.4"};  //4.hwNDPPortNbPortMode

       String[][] ipArray = snmp.getTableData(address,community,oids);
       if(ipArray==null) return null;


       tableValues = new Hashtable();
       IpAddress ipAddress = null;
       for (int i = 0; i < ipArray.length; i++)
       {       	  
       	   //if(!"3".equals(ipArray[i][3])) continue;    	   
       	   //if(ipArray[i][1].length()!=17) continue;
    	   SysLogger.info(address+" DeviceId:"+ipArray[i][0]+" PortName:"+ipArray[i][1]+" DeviceName"+ipArray[i][2]);
    	   if(ipArray[i][0] == null || ipArray[i][1]==null)continue;
    	   tableValues.put(ipArray[i][0], ipArray[i][1]);
    	   
    	   /*
       	   ipAddress = new IpAddress();
       	   ipAddress.setIfIndex(ipArray[i][0]);
       	   ipAddress.setPhysAddress(ipArray[i][1]);
       	   ipAddress.setIpAddress(ipArray[i][2]);       	   
           tableValues.add(ipAddress);
           */
       }
       
    }
    catch (Exception e)
    {
    	SysLogger.error("getNDPTable(),ip=" + address + ",community=" + community,e);
        tableValues = null;
    }
    return tableValues;
  }
  
  /**
   * 得到Huawei的NDP信息
   */
  public boolean setSysGroup(String address,String community,int version,Hashtable mibvalues)
  {
	  Hashtable tableValues = null;
     try
     {
        String[] oids = new String[]
                    {"1.3.6.1.2.1.1.4.0",   //4.sysContact
        		     "1.3.6.1.2.1.1.5.0",   //5.sysName
                     "1.3.6.1.2.1.1.6.0"};  //6.sysLocation
        String[] _mibvalue = new String[3];
        _mibvalue[0] = (String)mibvalues.get("sysContact");
        _mibvalue[1] = (String)mibvalues.get("sysName");
        _mibvalue[2] = (String)mibvalues.get("sysLocation");

       snmp.setMibValues(address, community,version, oids, _mibvalue);
       return true;
    }
    catch (Exception e)
    {
    	e.printStackTrace();
    	return false;
    }
  }

  /**
   * 得到端口号
   */
  public String getPort(String address,String community,String mac)
  {
	  if(mac==null) return null;
	  
      return snmp.getMibValue(address,community,NetworkUtil.getTheFdbOid(mac));
  }
  
  /**
   * 得到system oid
   */
  public String getSysOid(String address,String community)
  {
  	 return snmp.getMibValue(address,community,"1.3.6.1.2.1.1.2.0");
  }

  /**
   * 得到系统描述
   */
  public String getSysDescr(String address,String community)
  {
	  return snmp.getMibValue(address,community,"1.3.6.1.2.1.1.1.0");
  }

  /**
   * 得到系统描述
   */
  public int getSysServices(String address,String community)
  {
	  int result = 0;
	  String temp = snmp.getMibValue(address,community,"1.3.6.1.2.1.1.7.0");
	  if(temp!=null)
		 result = Integer.parseInt(temp);
	  return result;
  }
  
  /**
   * 得到系统名字
   */
  public String getSysName(String address,String community)
  {
	  String sysname = null;
	  try{
		  sysname = snmp.getMibValue(address,community,"1.3.6.1.2.1.1.5.0");
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	  return sysname;
  	 //return snmp.getMibValue(address,community,"1.3.6.1.2.1.1.5.0");
  }
  
  /**
   * get subnet from router table
   */
  public Hashtable getSysGroup(String address,String community)
  {
	  Hashtable tableValues = new Hashtable();
     try
     {        
         String[] oids = new String[]
                {"1.3.6.1.2.1.1.1",       //0.sysDescr
            	 "1.3.6.1.2.1.1.2",       //1.objectId        		       
                 "1.3.6.1.2.1.1.3",       //2.sysUpTime
                 "1.3.6.1.2.1.1.4",       //3.sysContact
                 "1.3.6.1.2.1.1.5",       //4.sysName
                 "1.3.6.1.2.1.1.6",       //5.sysLocation
                 "1.3.6.1.2.1.1.7"};     //6.sysService				                      
        
        String[][] ipArray = snmp.getTableData(address,community,oids);
        if(ipArray==null) return null;
        if(ipArray[0][0] != null){
    		tableValues.put("sysDescr", ipArray[0][0]);
    	}else{
    		tableValues.put("sysDescr", "");
    	}
        if(ipArray[0][1] != null){
    		tableValues.put("objectId", ipArray[0][1]);
    	}else{
    		tableValues.put("objectId", "");
    	}
        if(ipArray[0][2] != null){
    		tableValues.put("sysUpTime", ipArray[0][2]);
    	}else{
    		tableValues.put("sysUpTime", "");
    	}
        if(ipArray[0][3] != null){
    		tableValues.put("sysContact", ipArray[0][3]);
    	}else{
    		tableValues.put("sysContact", "");
    	}
        if(ipArray[0][4] != null){
    		tableValues.put("sysName", ipArray[0][4]);
    	}else{
    		tableValues.put("sysName", "");
    	}
        if(ipArray[0][5] != null){
    		tableValues.put("sysLocation", ipArray[0][5]);
    	}else{
    		tableValues.put("sysLocation", "");
    	}
        if(ipArray[0][6] != null){
    		tableValues.put("sysService", ipArray[0][6]);
    	}else{
    		tableValues.put("sysService", "");
    	}	
     }
     catch (Exception e)
     {
    	 e.printStackTrace();
    	 //SysLogger.error("getSysGroup(),ip=" + address + ",community=" + community);
         tableValues = null;
     }
     return tableValues;
  }
  
  /**
   * 交换机本身mac
   */
  public String getBridgeAddress(String address,String community)
  {
	  String bridge = null;
	  try{
		  bridge = snmp.getMibValue(address,community,"1.3.6.1.2.1.17.1.1.0");
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	  return bridge;
  	 //return snmp.getMibValue(address,community,"1.3.6.1.2.1.17.1.1.0");
  }
  
  /**
   * 从ip router table中得到与该设备相连的路由器
   */
  public List getRouterList(String address,String community)
  {
      String[] oids = new String[]
                    {"1.3.6.1.2.1.4.21.1.2",       //0.if index
                	 "1.3.6.1.2.1.4.21.1.1",       //1.ipRouterDest        		       
                     "1.3.6.1.2.1.4.21.1.7",       //7.ipRouterNextHop
                     "1.3.6.1.2.1.4.21.1.8",       //8.ipRouterType
                     "1.3.6.1.2.1.4.21.1.9",       //9.ipRouterProto
                     "1.3.6.1.2.1.4.21.1.11",
                     "1.3.6.1.2.1.4.21.1.3"};     //11.ipRouterMetric1				                      
 
      List tableValues = new ArrayList();
      try
      {
	      String[][] ipArray = snmp.getTableData(address,community,oids);
	      if(ipArray==null) return null;
	      
	      Hashtable macHash = null;
	      String[] oids2 = new String[]
	                       {"1.3.6.1.2.1.4.22.1.3",   //1.ip
	                		"1.3.6.1.2.1.4.22.1.2"};   //2.mac
	      String[][] macArray = snmp.getTableData(address,community,oids2);
	      macHash = new Hashtable();
	      for(int i = 0; i < macArray.length; i++)
	      	 macHash.put(macArray[i][0],macArray[i][1]);
	            	      
	      for (int i = 0; i < ipArray.length; i++)
	      {
	    	  //ipArray[i][5].equals("255.255.255.255") 不能把mask为255的去掉
		      if(ipArray[i][5].equals("0.0.0.0") 
		    	 ||ipArray[i][1].equals("0.0.0.0")||ipArray[i][1].startsWith("127.0")) 
		    	 continue; 
		      if(Integer.parseInt(ipArray[i][6])==-1)continue;
		      //SysLogger.info(address+" NextHop:"+ipArray[i][2]+" Dest:"+ipArray[i][1]+" Type:"+Integer.parseInt(ipArray[i][3]));
	          IpRouter ipr = new IpRouter();
	          ipr.setIfIndex(ipArray[i][0]);
	          ipr.setDest(ipArray[i][1]);
	          ipr.setNextHop(ipArray[i][2]);
	          ipr.setType(Integer.parseInt(ipArray[i][3]));
	          ipr.setProto(Integer.parseInt(ipArray[i][4]));	          
	          ipr.setMask(ipArray[i][5]);
	          ipr.setMetric(Integer.parseInt(ipArray[i][6]));
	  	      if(!tableValues.contains(ipr)) //不存在则加入
	  	      {      	      
	  	           if(macHash.get(ipArray[i][2])!=null)
	  	        	  ipr.setPhysAddress((String)macHash.get(ipArray[i][2]));
	  	    	   tableValues.add(ipr);
	  	      }   
	  	  }	      
      }
	  catch (Exception e)
	  {
	       SysLogger.error("getRouterList(),ip=" + address + ",community=" + community);
	       tableValues = null;
	  }	      
      return tableValues;
  }
  
  /**
   * get subnet from router table
   */
  public List getSubNetList(String address,String community)
  {
     List tableValues = new ArrayList();
     try
     {        
         String[] oids = new String[]
                {"1.3.6.1.2.1.4.21.1.2",       //0.if index
            	 "1.3.6.1.2.1.4.21.1.1",       //1.ipRouterDest        		       
                 "1.3.6.1.2.1.4.21.1.7",       //7.ipRouterNextHop
                 "1.3.6.1.2.1.4.21.1.8",       //8.ipRouterType
                 "1.3.6.1.2.1.4.21.1.11"};     //11.ipRouterMask				                      
        
        String[][] ipArray = snmp.getTableData(address,community,oids);
        if(ipArray==null) return null;
        
        for (int i = 0; i < ipArray.length; i++)
        {
            if(!"3".equals(ipArray[i][3])) continue; //不是direct的是不处理
                        
        	if(ipArray[i][4].equals("255.255.255.255")||ipArray[i][4].equals("0.0.0.0") 
  	    	   ||ipArray[i][2].startsWith("127.0")||ipArray[i][1].startsWith("127.0")||ipArray[i][2].equals("0.0.0.0")) 
  	    		continue;
      	            	
        	if(NetworkUtil.isNetAddress(ipArray[i][1],ipArray[i][4]))
        	{
      	        SubNet subNet = new SubNet();    
      	        subNet.setIfIndex(ipArray[i][0]);
      	        subNet.setNetAddress(ipArray[i][1]);
      	        subNet.setIpAddress(ipArray[i][2]);
      	        subNet.setNetMask(ipArray[i][4]);
      	        if(!tableValues.contains(subNet))
                   tableValues.add(subNet);   
      	    }   
        }	
     }
     catch (Exception e)
     {
    	 SysLogger.error("getSubNetList(),ip=" + address + ",community=" + community);
         tableValues = null;
     }
     return tableValues;
  }

  /**
   * get cdp from router/switch table
   */
  public List getCiscoCDPList(String address,String community)
  {
	  //现在还没判断是否需要增加Portstate状态的判断
     List tableValues = new ArrayList();
     String[][] cdpArray1=null;
     try
     {        	
        String[] oids1 = new String[]
               {"1.3.6.1.4.1.9.9.23.1.2.1.1.4",       //1.cdpCacheAddress
                //"1.3.6.1.4.1.9.9.23.1.2.1.1.5",       //3.cdpCacheVersion       		       
                //"1.3.6.1.4.1.9.9.23.1.2.1.1.6",       //8.cdpCacheDeviceId
                "1.3.6.1.4.1.9.9.23.1.2.1.1.7"       //9.cdpCacheDevicePort
                //"1.3.6.1.4.1.9.9.23.1.2.1.1.1"		  //cdpCacheIfIndex
                };     				                      
                           
       cdpArray1 = snmp.getTableData(address,community,oids1);
       if(cdpArray1==null) return null;
       
       for (int i = 0; i < cdpArray1.length; i++)
       {
    	//SysLogger.info(address+" CDP:------cdpIP:"+cdpArray1[i][0]+"("+ciscoIP2IP(cdpArray1[i][0])+")"+"---Port:"+cdpArray1[i][1]);        	
		CdpCachEntryInterface cdp = new CdpCachEntryInterface();
		if(cdpArray1[i][0] == null)continue;
		cdp.setIp(ciscoIP2IP(cdpArray1[i][0]));
		cdp.setPortdesc(cdpArray1[i][1]);
		//cdp.setIfindex(cdpArray1[i][2]);
		SysLogger.info(address+" remoteIp:"+cdp.getIp()+"==="+cdpArray1[i][0]+"=== 远程端口描述:"+cdp.getPortdesc());
       	tableValues.add(cdp);
       }
       /*
       Hashtable<String,String[]> m_VlanIPAndCommunity=new Hashtable<String, String[]>();
       m_VlanIPAndCommunity = getVLanIPAndReadCommunity(address,community);
       if(m_VlanIPAndCommunity != null && m_VlanIPAndCommunity.size()>0){
    	   Iterator iter = m_VlanIPAndCommunity.keySet().iterator();
			while (iter.hasNext()) {
			
				String[] tmp = m_VlanIPAndCommunity.get(iter.next());
System.out.println(tmp[0]+"======"+tmp[1]+"====="+tmp[2]);				
				//snmputil.setPara(tmp[0], tmp[1], new Integer(m_TimeOut).toString());
			}
       }
       */
     }
     catch (Exception e)
     {
    	 cdpArray1 = null;
    	 e.printStackTrace();
    	 SysLogger.error("getCiscoCDPList(),ip=" + address + ",community=" + community);
         tableValues = null;
     }
     if(cdpArray1 != null)cdpArray1 = null;
     return tableValues;
  }
  
  /**
   * get cisco vlan from router/switch table
   */
  public Hashtable<Long, Long> getCiscoIIDVlanIdValue(String address,String community)
  {
	  /*
		 * 应该在各个设备的类中实现本函数。 cisco设备 先从CISCO-VLAN-MEMBERSHIP-MIB的
		 * .iso.org.dod.internet.private.enterprises.cisco.ciscoMgmt.ciscoVlanMembershipMIB.ciscoVlanMembershipMIBObjects.vmMembership.vmMembershipTable.vmMembershipEntry.vmVlan
		 * .1.3.6.1.4.1.9.9.68.1.2.2.1.2 节点获取端口id与vlan
		 * id的映射关系。如果成功获取则直接返回，否则执行下一步： 从CISCO-VLAN-IFTABLE-RELATIONSHIP-MIB
		 * .iso.org.dod.internet.private.enterprises.cisco.ciscoMgmt.ciscoVlanIfTableRelationshipMIB.cviMIBObjects.cviGlobals.cviVlanInterfaceIndexTable.cviVlanInterfaceIndexEntry.cviRoutedVlanIfIndex
		 * .1.3.6.1.4.1.9.9.128.1.1.1.1.3 节点获取vlan
		 * id与端口id的映射关系（返回之前要调换位置！！）。如果成功获取则直接返回,失败返回空
		 * 
		 * 返回节点获取端口id与vlan id的映射关系。
		 * 
		 * 其他设备参见SNMP-Info-1.04\SNMP-Info-1.04 下的i_vlan函数的实现。
		 */
	 Hashtable<Long, Long> result = new Hashtable<Long, Long>();
     try
     {        	       
       String[] oids1 = new String[]
             {"1.3.6.1.4.1.9.9.68.1.2.2.1.2"       //vmVlan
       };     				                      
                                               
       String[][] vlanArray1 = null;
       try{
    	   vlanArray1 = snmp.getCiscoVlanTableData(address,community,oids1);
           if(vlanArray1==null) return null;                               
           for (int i = 0; i < vlanArray1.length; i++)
           {
        	   result.put(new Long(vlanArray1[i][0]), new Long(vlanArray1[i][1]));//端口ID:VLANID
 System.out.println("VLAN:------VLANID:"+vlanArray1[i][0]+"---端口ID:"+vlanArray1[i][1]);        	
           }    	   
       }catch(Exception ex){
    	   ex.printStackTrace();
       }

       if(result != null && result.size()>0){
    	   return result;
       }
       oids1 = new String[]
                          {"1.3.6.1.4.1.9.9.128.1.1.1.1.3"       //cviRoutedVlanIfIndex
                    };     				                      
       try{
           vlanArray1 = snmp.getCiscoVlanTableData(address,community,oids1);
           if(vlanArray1==null) return null;
                               
           for (int i = 0; i < vlanArray1.length; i++)
           {
        	   result.put(new Long(vlanArray1[i][0]), new Long(vlanArray1[i][1]));//端口ID:VLANID
System.out.println("VLAN:------VLAN:"+vlanArray1[i][0]+"---VLANID:"+vlanArray1[i][1]);        	
           } 
       }catch(Exception ex){
    	   
       }     
     }
     catch (Exception e)
     {
    	 e.printStackTrace();
    	 SysLogger.error("getCiscoIIDVlanIdValue(),ip=" + address + ",community=" + community);
    	 result = null;
     }
     return result;
  }
  
	private Hashtable<String,String[]> getVLanIPAndReadCommunity(String address,String community){

		/*
		 * String[]中数组依次为IP地址，端口
		 * .iso.org.dod.internet.mgmt.mib-2.entityMIB.entityMIBObjects.entityLogical.entLogicalTable.entLogicalEntry.entLogicalTAddress
		 * .1.3.6.1.2.1.47.1.2.1.1.5 
		 * snmp读团体
		 * .iso.org.dod.internet.mgmt.mib-2.entityMIB.entityMIBObjects.entityLogical.entLogicalTable.entLogicalEntry.entLogicalCommunity
		 * .1.3.6.1.2.1.47.1.2.1.1.4
		 */
		Hashtable<String,String[]> m_VlanIPAndCommunity=new Hashtable<String, String[]>();
		try {

			String[] columnoids = { ".1.3.6.1.2.1.47.1.2.1.1.5",
					".1.3.6.1.2.1.47.1.2.1.1.4" };
			String[][] result = null;
			result = snmp.getTableData(address, community, columnoids);

			for (int i = 0; i < result.length; ++i) {
				String[] tmp = CommonUtil.IPPort2String(result[i][0],4 );
				String[] ip_port_comm = new String[3];
				ip_port_comm[0] = tmp[0];
				ip_port_comm[1] = tmp[1];
				ip_port_comm[2] = result[i][1];
				
				if( null == m_VlanIPAndCommunity.put(ip_port_comm[0]+ip_port_comm[1]+ip_port_comm[2] ,ip_port_comm ) ){
					
					System.out.println(address+" getVLanIPAndReadCommunity:"+result[i][0]+" "+result[i][1]+" to "+ip_port_comm[0]+" "+ip_port_comm[1]+" "+ip_port_comm[2]);
					
				}else{
					
					System.out.println(address+" already getVLanIPAndReadCommunity:"+result[i][0]+" "+result[i][1]+" to "+ip_port_comm[0]+" "+ip_port_comm[1]+" "+ip_port_comm[2]);
					
				}
			}
			
			//获取vlan数据,使用不同的目标IP得到的数据不一样
//			Vector<String[]> alias = getAlias();
//			for( int pos = 0; pos<alias.size(); ++pos ) {
//				
//				System.out.println(getIP()+" getVLanIPAndReadCommunity"+(String)(alias.get(pos)[0]));
//				
//				String str_alias = (String)(alias.get(pos)[0]);
//				snmputil.setPara( str_alias, getSnmpPort(), new Integer(m_TimeOut).toString());
//				
//				result = snmputil.getTable(column, null, null);
//
//				System.out.println(getIP()+" getVLanIPAndReadCommunity "+result.length);
//				for (int i = 0; i < result.length; ++i) {
//					String[] tmp = CommonUtil.IPPort2String(result[i][0],4 );
//					String[] ip_port_comm = new String[3];
//					ip_port_comm[0] = tmp[0];
//					ip_port_comm[1] = tmp[1];
//					ip_port_comm[2] = result[i][1];
//					
//					if( null == m_VlanIPAndCommunity.put( ip_port_comm[0]+ip_port_comm[1]+ip_port_comm[2],ip_port_comm ) ){
//						
//						System.out.println(str_alias+ " getVLanIPAndReadCommunity:"+result[i][0]+" "+result[i][1]+" to "+ip_port_comm[0]+" "+ip_port_comm[1]+" "+ip_port_comm[2]);
//						
//					}else{
//						System.out.println(str_alias+ " already getVLanIPAndReadCommunity:"+result[i][0]+" "+result[i][1]+" to "+ip_port_comm[0]+" "+ip_port_comm[1]+" "+ip_port_comm[2]);
//						
//					}
//				}
//				
//			}
//			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//snmputil.destory();
		}

		return m_VlanIPAndCommunity;

	}  
  
  /**
   * get stp from router table
   */
  public List getBridgeStpList(String address,String community)
  {
	  //现在还没判断是否需要增加Portstate状态的判断
     List tableValues = new ArrayList();
     Hashtable portToIfIndex = new Hashtable();
     String[][] stpArray = null;
     try
     {   
    	 
         String[] oids = new String[]
                         {"1.3.6.1.2.1.17.1.4.1.1",     //1.dot1dBasePort
                          "1.3.6.1.2.1.17.1.4.1.2"      //2.dot1dBasePortIfIndex     		       
                          };     				                      
                            
         stpArray = snmp.getTableData(address,community,oids);
         if(stpArray==null) return null;
                            
         for (int i = 0; i < stpArray.length; i++)
         {
        	 if(stpArray[i][0] == null)continue;
        	 //SysLogger.info("获取端口和索引的关系#######"+address+"  port:"+stpArray[i][0]+"    ifindex:"+stpArray[i][1]);
        	 portToIfIndex.put(stpArray[i][0], stpArray[i][1]);
        }    	 
    	 
         oids = new String[]
                {"1.3.6.1.2.1.17.2.15.1.1",       //1.dot1dStpPort
            	 "1.3.6.1.2.1.17.2.15.1.3",       //3.dot1dStpPortState       		       
                 "1.3.6.1.2.1.17.2.15.1.8",       //8.dot1dStpPortDesignatedBridge
                 "1.3.6.1.2.1.17.2.15.1.9"       //9.dot1dStpPortDesignatedPort
                 };     				                      
        
        stpArray = snmp.getTableData(address,community,oids);
        if(stpArray==null) return null;
        
        for (int i = 0; i < stpArray.length; i++)
        {
        	if(stpArray[i][0] == null)continue;
        	if(stpArray[i][2].equalsIgnoreCase("00:00:00:00:00:00:00:00"))continue;
        	if(stpArray[i][3].equalsIgnoreCase("00:00"))continue;
        	//SysLogger.info(address+" STP:"+"----ifIndex:"+(String)portToIfIndex.get(stpArray[i][0])+"------Port:"+stpArray[i][0]+"---Bridge:"+stpArray[i][2]+"---BridgePort:"+stpArray[i][3]);        	
        	
			BridgeStpInterface bstp = new BridgeStpInterface();
        	bstp.setPort((String)portToIfIndex.get(stpArray[i][0]));
        	bstp.setBridge(stpArray[i][2]);
        	bstp.setBridgeport(stpArray[i][3]);
        	bstp.setIfindex((String)portToIfIndex.get(stpArray[i][0]));
        	tableValues.add(bstp);
        }	 
     }
     catch (Exception e)
     {
    	 stpArray = null;
    	 e.printStackTrace();
    	 SysLogger.error("getBridgeStpList(),ip=" + address + ",community=" + community);
         tableValues = null;
     }
     if(stpArray != null)stpArray = null;
     return tableValues;
  }
  
	//c0:a8:01:f7  ->  192.168.1.247
	public String ciscoIP2IP(String ciscoip){
		
		String[] s = ciscoip.split(":");
		if( 4 == s.length ){
			return ""+Integer.parseInt(s[0], 16)+"."+Integer.parseInt(s[1], 16)+"."+Integer.parseInt(s[2], 16)+"."+Integer.parseInt(s[3], 16);
		}
		
		return "";
	}
  /**
   * identify a device is router or switch
   * 0=unknown,1=router, 2=route_switch,3=switch,4=server,5=printer,20=other  
   */
  public int checkDevice(String address,String community,String sysOid)
  {
     String switchtemp = null;
     String forwardtemp = null;
     int deviceType = 0;
     
     if(sysOid == null)return deviceType;
     
//     try //从现在源资中判断设备的类型
//     {
//    	 SysLogger.info("IP---"+address+"---sysOid---"+sysOid);
//    	 if(DiscoverResource.getInstance().getDeviceType().get(sysOid) != null)
//    		 deviceType = ((Integer)DiscoverResource.getInstance().getDeviceType().get(sysOid)).intValue();   
//    	 SysLogger.info("设备"+address+"类型为:"+deviceType+" sysOid:"+sysOid);    	 
//     }
//     catch(Exception e)
//     {
//    	 e.printStackTrace();
//    	 deviceType = 0;	 
//     }     
//     if(deviceType!=0) return deviceType;
     
  	 try
	 {    		 
  		//temp = snmp.getMibValue(address,community,"1.3.6.1.2.1.43.5.1.1.1.1"); //Printer-MIB
  		//SysLogger.info("设备"+address+" Printer-MIB:"+temp);
   		//if(temp!=null && !temp.equalsIgnoreCase("noSuchObject")) return 5; 
   		SysLogger.info("设备"+address+" getIfNumber:"+getIfNumber(address,community));
  		if(getIfNumber(address,community)<2) return 0; //未知	  		
  	    String[][] ipArray = snmp.getTableData(address,community,new String[]{"1.3.6.1.2.1.4.20.1.1"});
  	    //||ipArray.length==1 路由器addr表中也可能只有一项，所以..(2007.3.6)
  	    //SysLogger.info("设备"+address+" ipArray:"+ipArray.toString());
  	    if(ipArray==null) return 0; 

        int isSwitch = -1,ipForward = -1;        
        switchtemp = snmp.getMibValue(address,community,"1.3.6.1.2.1.17.1.2.0"); //Bridge－Mib 
  	    if(switchtemp == null )
  	    	switchtemp = snmp.getMibValue(address,community,"1.3.6.1.2.1.17.1.2"); //Bridge－Mib
  	    SysLogger.info("设备"+address+" Bridge－Mib:"+switchtemp);
  	    if(switchtemp!=null){
  	    	if(switchtemp.equalsIgnoreCase("noSuchObject")){
  	    		isSwitch = 0;
  	    	}else
  	    		isSwitch = Integer.parseInt(switchtemp);
  	    }
  	      	  
  	  forwardtemp = snmp.getMibValue(address,community,"1.3.6.1.2.1.4.1.0");   //ipForwording 
  	    if(forwardtemp == null )
  	    	forwardtemp = snmp.getMibValue(address,community,"1.3.6.1.2.1.4.1");   //ipForwording 
  	    SysLogger.info("设备"+address+" ipForwording:"+forwardtemp);
  	    if(forwardtemp!=null) ipForward = Integer.parseInt(forwardtemp);
  	    
  	    SysLogger.info("设备"+address+" ipforward:"+ipForward+" isSwitch:"+isSwitch);
  	    if(ipForward==1&&isSwitch==0)
        	return 1; //路由 	  
        if(ipForward==1&&isSwitch>0)
        	return 2; //路由交换;
        if(ipForward!=1&&isSwitch>0)
        	return 3; //二层交换;	                  	    
	 }  
  	 catch(Exception e)
	 {  	
  		 e.printStackTrace();
  	 	 deviceType = 0;
	 }
     return deviceType; 
  }
  
  /**
   *得到一个设备接口数 
   */
  public int getIfNumber(String address,String community)
  {
  	 int ifNumber = 0;
     try
     {
    	ifNumber = Integer.parseInt(snmp.getMibValue(address,community,"1.3.6.1.2.1.2.1.0"));
     }
     catch (Exception e)
     {
    	 ifNumber = 0;       
     }
     return ifNumber;
  }

  /**
   * 确定community
   */
  public String getCommunity(String address)
  {
	  SysLogger.info("开始获取设备"+address+"的团体名称");
	  String community = null;
	  String sysOid = null;
	  try //确定是否有特定的community
	  {
		  community = (String)DiscoverResource.getInstance().getSpecifiedCommunity().get(address);
	  }
	  catch(Exception e)
	  {
		  community = null;
	  }
	  if(community!=null) return community;
	  SysLogger.info("设备"+address+"不存在特定团体名称");
	  Iterator communityList = DiscoverResource.getInstance().getCommunitySet().iterator();	  
 	  while(communityList.hasNext())
 	  {	 		  
 		  community = (String)communityList.next();
 		 SysLogger.info("开始获取设备"+address+"的sysOid,用"+community+"团体名称");
 	      sysOid = getSysOid(address,community);
 	     SysLogger.info("结束获取设备"+address+"的sysOid,用"+community+"团体名称");
          if(sysOid!=null) break;
      }           
 	  SysLogger.info("设备"+address+"的sysOid为"+sysOid);
 	  if(sysOid==null) 
 	 	 return DiscoverResource.getInstance().getCommunity();//用缺省的团体名称
 	  else
 	 	 return community;//返回能取到值的团体名称
  }  
  
  /**
   * 得到所有接口的相关信息(2006.06.30)
   * 增加category,如果是路由器，if_index=if_port(2007.01.16)
   */
  public List getIfEntityList(String address,String community,int category)
  {
  	 List tableValues = new ArrayList(50);
     String[] oids1 = new String[]
             		  {"1.3.6.1.2.1.2.2.1.1",  //index
		               "1.3.6.1.2.1.2.2.1.2",  //descr
		               "1.3.6.1.2.1.2.2.1.5",  //speed
			           "1.3.6.1.2.1.2.2.1.6",  //mac
			           "1.3.6.1.2.1.2.2.1.8",  //operstatus
			           "1.3.6.1.2.1.2.2.1.3"}; //type 
     
     String[] oids2 = new String[]
			          {"1.3.6.1.2.1.17.1.4.1.2",  //1.index
			           "1.3.6.1.2.1.17.1.4.1.1"}; //2.port
     
     String[] oids3 = new String[]
			          {"1.3.6.1.2.1.4.20.1.2",    //1.index
			           "1.3.6.1.2.1.4.20.1.1"};   //2.ip     
     try
	 {
        String[][] ipArray = snmp.getTableData(address,community,oids1);    
     	if(ipArray==null) return null;
     	     	
     	Hashtable ifHash = new Hashtable();
        for (int i = 0; i < ipArray.length; i++)
        {   
           //SysLogger.info(address+"-------index:"+ipArray[i][0]);   	
           if(ipArray[i][0]==null) continue; //(2006.08.30)
           
           IfEntity ifEntity = new IfEntity(); 
           if(ipArray[i][0]==null)
        	  ifEntity.setIndex("");     	      
           else
        	  ifEntity.setIndex(ipArray[i][0]); 
           if(ipArray[i][1].length()<50)        	   
     	      ifEntity.setDescr(ipArray[i][1]);
           else //为防止descr过长，插入数据库时出错
        	  ifEntity.setDescr(ipArray[i][1].substring(0,50)); 
           //依据DESCR处理设备面板
           if(ifEntity.getDescr() != null){
           	String descr = ifEntity.getDescr();            	
           	if(descr.indexOf("GigabitEthernet")>=0){
           		String allchassis = descr.substring(descr.lastIndexOf("t")+1);
           	        String[] chassis = allchassis.split("/");
           		if(chassis.length==3){
           			String str_chassis = chassis[0];
           			String slot = chassis[1];
           			String uport = chassis[2];
           			try{
           				ifEntity.setChassis(Integer.parseInt(str_chassis));
           			}catch(Exception chassex){
           				ifEntity.setChassis(-1);
           			}
           			try{
           				ifEntity.setSlot(Integer.parseInt(slot));
           			}catch(Exception chassex){
           				ifEntity.setSlot(-1);
           			}
           			try{
           				ifEntity.setUport(Integer.parseInt(uport));
           			}catch(Exception chassex){
           				ifEntity.setUport(-1);
           			}
           		} 
           	}else if(descr.indexOf("Ethernet")==0){
           		String allchassis = descr.substring(descr.lastIndexOf("t")+1);
       	        String[] chassis = allchassis.split("/");
       	        if(chassis.length==3){
           			String str_chassis = chassis[0];
           			String slot = chassis[1];
           			String uport = chassis[2];
           			try{
           				ifEntity.setChassis(Integer.parseInt(str_chassis));
           			}catch(Exception chassex){
           				ifEntity.setChassis(-1);
           			}
           			try{
           				ifEntity.setSlot(Integer.parseInt(slot));
           			}catch(Exception chassex){
           				ifEntity.setSlot(-1);
           			}
           			try{
           				ifEntity.setUport(Integer.parseInt(uport));
           			}catch(Exception chassex){
           				ifEntity.setUport(-1);
           			}
       	        }           		
           	}
         }
     	   ifEntity.setSpeed(ipArray[i][2]);
     	   ifEntity.setPhysAddress(ipArray[i][3]);
     	   if("1".equals(ipArray[i][4]))
     	      ifEntity.setOperStatus(1);
     	   else
     		  ifEntity.setOperStatus(2);     		   
     	   ifEntity.setIpAddress("");
     	   ifEntity.setIpList("");
     	   if(category==1)
     	      ifEntity.setPort(ifEntity.getIndex());
     	   else
     		  ifEntity.setPort(""); 
     	   ifEntity.setType(Integer.parseInt(ipArray[i][5]));
     	   tableValues.add(ifEntity);
     	   //SysLogger.info(address+"存在"+ipArray[i][0]+"号端口");
     	   ifHash.put(ipArray[i][0],ifEntity);
        }
        
        //2.==========找端口==========
        if(category!=1)
        {
            String[][] ipArray2 = null;
            try{
            	ipArray2 = snmp.getTableData(address,community,oids2);
            }catch(Exception e){
            	
            }
	        if(ipArray2==null) return null;
        
            for(int i = 0; i < ipArray2.length; i++)
            {  
            	if(ipArray2[i][0] == null)continue;
            	//SysLogger.info(address+"-------index:"+ipArray2[i][0]+"---port:"+ipArray2[i][1]);  
            	
        	   if(ifHash.get(ipArray2[i][0])==null) continue;
        	   IfEntity ifEntity = (IfEntity)ifHash.get(ipArray2[i][0]);        	         	
        	   ifEntity.setPort(ipArray2[i][1]);
            }
        }
        //3.==========找IP=============
        String[][] ipArray3 = null;
        try{
        	ipArray3 = snmp.getTableData(address,community,oids3);
        }catch(Exception e){
        	
        }
	    if(ipArray3==null) return null;
        
        for (int i = 0; i < ipArray3.length; i++)
        {   
        	if(ipArray3[i][0] == null)continue;
//System.out.println(address+"-------index:"+ipArray3[i][0]+"---ip:"+ipArray3[i][1]);
        	if(ifHash.get(ipArray3[i][0])==null) continue;
        	IfEntity ifEntity = (IfEntity)ifHash.get(ipArray3[i][0]);
        	if(ipArray3[i][1].startsWith("127"))continue;  //过滤掉本地IP      	
        	if(ifEntity.getIpAddress().equals(""))  //解决一个接口多个IP的问题
        	{	
        	   ifEntity.setIpAddress(ipArray3[i][1]);
        	   ifEntity.setIpList(ipArray3[i][1]);
        	}   
        	else
        	   ifEntity.setIpList(ifEntity.getIpList() + "," + ipArray3[i][1]);	
        }
        for(int i=0;i<tableValues.size();i++)
        {
        	IfEntity tmp = (IfEntity)tableValues.get(i); 
        	//为什么需要把00:00:00:00:00:00去掉????山西森林公安
        	/*
        	if(("00:00:00:00:00:00".equals(tmp.getPhysAddress())&&tmp.getType()!=6&&tmp.getType()!=23&&tmp.getType()!=24)
        			||tmp.getIpAddress().startsWith("127"))
        		tableValues.remove(i);
        	*/
        }
	 }
     catch(Exception e)
	 {
    	 e.printStackTrace();
    	 SysLogger.error("getIfEntityList(),IP=" + address + ",community=" + community,e);
         tableValues = null;
	 }
     return tableValues;
  }

  public List getIfEntityList(String address,List<String> vlanCommunities)
  {
	  List allIfs = new ArrayList();	  
	  for(String temp:vlanCommunities)
	  {		  
		  List ifs = SnmpUtil.getInstance().getIfEntityList("10.10.10.252",temp,2);
		  if(ifs!=null) allIfs.addAll(ifs);		  
	  }
	  return allIfs;
  }
  
  /**
   * 得到ip router table,用于Tool
   */
  public List getIPRouterTable(String address,String community)
  {
     List tableValues = null;
     try
     {
         String[] oids = new String[]
                {"1.3.6.1.2.1.4.21.1.1",     //1.ipRouterDest        		       
                 "1.3.6.1.2.1.4.21.1.7",     //7.ipRouterNextHop
                 "1.3.6.1.2.1.4.21.1.8",     //8.ipRouterType
                 "1.3.6.1.2.1.4.21.1.11",    //11.ipRouterMask
				 "1.3.6.1.2.1.4.21.1.2"};    //0.if index                 
 
        String[][] ipArray = snmp.getTableData(address,community,oids);
        if(ipArray==null) return null;

        tableValues = new ArrayList();
        IpRouter ipRouter = null;
        for (int i = 0; i < ipArray.length; i++)
        {
           int ipType = 0;
           try
		   {
        	   ipType = Integer.parseInt(ipArray[i][2]);
		   }
           catch(NumberFormatException e)
		   {
        	   ipType = 0;	
		   }
           ipRouter = new IpRouter();
           ipRouter.setDest(ipArray[i][0]);
           ipRouter.setNextHop(ipArray[i][1]);
           ipRouter.setType(ipType);
           ipRouter.setMask(ipArray[i][3]);
           ipRouter.setIfIndex(ipArray[i][4]);
           tableValues.add(ipRouter);
        }
     }
     catch(Exception e)
     {
    	 SysLogger.info("getIPRouterTable(),ip=" + address + ",community=" + community);
         tableValues = null;
     }
     return tableValues;
   }
  

  
   public List getFdbTable(String address,String community)
   {
      String[] oids1 = new String[]
                     {"1.3.6.1.2.1.17.4.3.1.1",  //1.mac   		       
                      "1.3.6.1.2.1.17.4.3.1.2",  //2.port
                      "1.3.6.1.2.1.17.4.3.1.3"}; //3.type
      
      String[] oids2 = new String[]
            		 {"1.3.6.1.2.1.17.1.4.1.2",  //1.index
            		  "1.3.6.1.2.1.17.1.4.1.1"}; //2.port
                       
      String[][] ipArray1 = null;
      String[][] ipArray2 = null;
      List tableValues = new ArrayList(30);
      try
      {         
    	  HashMap portMap = new HashMap();
    	  ipArray2 = snmp.getTableData(address,community,oids2);
    	  for(int i=0;i<ipArray2.length;i++){
    		  //SysLogger.info(address+" FDB port:"+ipArray2[i][1]+"   index:"+ipArray2[i][0]);
    	     portMap.put(ipArray2[i][1], ipArray2[i][0]);
    	  }
    	      	  
    	  ipArray1 = snmp.getTableData(address,community,oids1);
    	  for(int i=0;i<ipArray1.length;i++)
    	  {
    		  //SysLogger.info("FDB====>ip : "+address+" type========="+ipArray1[i][2]+" port "+ipArray1[i][1]+" MAC : "+ipArray1[i][0]);
              if(!"3".equals(ipArray1[i][2])) continue; //only type=learned 
    		  if(portMap.get(ipArray1[i][1])==null) continue;
    		  
   		      String ifIndex = (String)portMap.get(ipArray1[i][1]);     	       	      
   		      String[] item = new String[2];
   		      item[0] = ifIndex;
   		      item[1] = ipArray1[i][0];
   		      //SysLogger.info(address+" FDB "+" ifIndex : "+ifIndex+" MAC : "+ipArray1[i][0]) ;  		      
     	      tableValues.add(item);     	      
    	  }    	  
      }
      catch(Exception e)
      {
    	  e.printStackTrace();
    	  SysLogger.info("getFdbTable(),ip=" + address + ",community=" + community);         
      }
      return tableValues;
   }
   
   public HashMap getDtpFdbTable(String address,String community)
   {
      String[] oids1 = new String[]
                     {"1.3.6.1.2.1.17.4.3.1.1",  //1.mac   		       
                      "1.3.6.1.2.1.17.4.3.1.2",  //2.port
                      "1.3.6.1.2.1.17.4.3.1.3"}; //3.type
                       
      String[][] ipArray1 = null;
	  HashMap<Integer,Set<String>> portMacs = new HashMap<Integer,Set<String>>();
      try
      {        
    	  //SysLogger.info("getDtpFdbTable#################################################");
    	  //HashMap portMap = new HashMap();
    	  Set<String> macs = new HashSet<String>();

    	  ipArray1 = snmp.getTableData(address,community,oids1);
    	  for(int i=0;i<ipArray1.length;i++)
    	  {
    		  //SysLogger.info(address+" PORT: "+ipArray1[i][1]+" MAC: "+ipArray1[i][0]+ " TYPE: "+ipArray1[i][2]);
    		  if(ipArray1[i][0] == null)continue;
    		  if(ipArray1[i][1] == null)continue;
    		  
              if(!"3".equals(ipArray1[i][2])) continue; //only type=learned 
    		  //if(portMap.get(ipArray1[i][1])==null) continue;
              if (portMacs.containsKey(new Integer(ipArray1[i][1]))) {
      			macs = portMacs.get(new Integer(ipArray1[i][1]));
      		  }
      			macs.add(ipArray1[i][0]);
      			//SysLogger.info(address+" final PORT: "+ipArray1[i][1]+" MAC: "+ipArray1[i][0]);
      			portMacs.put(new Integer(ipArray1[i][1]), macs);
      			//macsVlan.put(macAddress, vlan);
    	  }    	  
      }
      catch(Exception e)
      {
    	  e.printStackTrace();
    	  SysLogger.info("getDtpFdbTable(),ip=" + address + ",community=" + community);         
      }
      return portMacs;
   }
   
   public boolean macInFdbTable(String mac,String address,String community)
   {
      String[] oids = new String[]
                     {"1.3.6.1.2.1.17.4.3.1.1",  //1.mac   		       
                      "1.3.6.1.2.1.17.4.3.1.2",  //2.port
                      "1.3.6.1.2.1.17.4.3.1.3"}; //3.type
                       
      boolean result = false;
      try
      {         
    	  String[][] ipArray = snmp.getTableData(address,community,oids);
    	  for(int i=0;i<ipArray.length;i++)
    	  {            
   		      if(mac.equals(ipArray[i][0]))
   		      {
   		    	  result = true;
   		    	  break;
   		      }
    	  }    	  
      }
      catch(Exception e)
      {
    	  SysLogger.info("getFdbTable(),ip=" + address + ",community=" + community);         
      }
      return result;
   }   

   public String[][] getMacIPTable(String address,String community)
   {    
      String[] oids = new String[]
                     {"1.3.6.1.2.1.4.22.1.2",  //mac
                      "1.3.6.1.2.1.4.22.1.3"}; //ip

      String[][] ipArray = null;
      try
      {
         ipArray = snmp.getTableData(address, community, oids);
      }
      catch(Exception e)
      {
   	     SysLogger.info("getMacIPTable(),ip=" + address + ",community=" + community);         
      }    
      return ipArray;
   }
         
   /**
    * find all possible links between two switches,or router and switch
    * if them are router and switch,we should put router first. 
    */
   public List findLinks(int id1,String ip1,String community1,int id2,String ip2,String community2)
   {
	   //System.out.println("找" + ip1 + "与" + ip2 + "之间的链路");
	   String[] fdbOids = new String[]
                  {"1.3.6.1.2.1.17.4.3.1.1",  //1.mac   		       
                   "1.3.6.1.2.1.17.4.3.1.2",  //2.port
                   "1.3.6.1.2.1.17.4.3.1.3"}; //3.type
       String[] ifOids = new String[]
          		  {"1.3.6.1.2.1.2.2.1.1",  //index
		           "1.3.6.1.2.1.2.2.1.6"};  //mac   
       String[] portOids = new String[]
          		  {"1.3.6.1.2.1.17.1.4.1.2",  //1.index
          		   "1.3.6.1.2.1.17.1.4.1.1"}; //2.port
       
       List links = new ArrayList();
       try
       {         
     	  String[][] ifTable1 = snmp.getTableData(ip1,community1,ifOids);     	       	 
     	  String[][] fdbTable2 = snmp.getTableData(ip2,community2,fdbOids);
     	  String[][] port2 = snmp.getTableData(ip2,community2,portOids);
     	  
     	  if(ifTable1==null||fdbTable2==null||port2==null) return null;
     	  
     	  HashMap portMap = new HashMap();
     	  for(int i=0;i<port2.length;i++)
     		 portMap.put(port2[i][1], port2[i][0]);
     	   
     	  HashMap fdbMap = new HashMap();
     	  for(int j=0;j<fdbTable2.length;j++)
     	  {
     		  if("3".equals(fdbTable2[j][2]))
     		     fdbMap.put(fdbTable2[j][0], fdbTable2[j][1]);
     	  }	
     	  for(int i=0;i<ifTable1.length;i++)
     	  {
      		  if(fdbMap.get(ifTable1[i][1])!=null)
      		  {
      			  String port = (String)fdbMap.get(ifTable1[i][1]);
      			  String index = (String)portMap.get(port);
      			  TemporaryLink newLink = null;
                  if(id1>id2)
                 	 newLink = new TemporaryLink(id2,index,id1,ifTable1[i][0]);
                   else
                 	 newLink = new TemporaryLink(id1,ifTable1[i][0],id2,index); 
      			  if(!links.contains(newLink))
      			  {
      			      links.add(newLink);
      			      System.out.println(newLink.toString());
      			  }
      		  }
     	  }	
     	  if(links.size()==0) return null;
       }
       catch(Exception e)
       {
     	   SysLogger.error("getLinks()",e);  
     	   links = null;
       }
       return links;
   }
   
   public List<String> getVlanCommunities(String ip,String communtiy)
   {
	   String[] vlanOids = new String[]{"1.3.6.1.4.1.9.9.68.1.2.2.1"}; 
	   List<String>  vlanCommunities = new ArrayList<String>();
	   try
	   {
	       String[][] vlan = snmp.getTableData(ip,communtiy,vlanOids);
	       for(int i=0;i<vlan.length;i++)
	       {
	           String temp = communtiy + "@" + vlan[i][0];
	    	   if(!vlanCommunities.contains(temp))
	    		   vlanCommunities.add(temp);
	       }
	   }
       catch(Exception e)
       {
     	   SysLogger.error("getLinks()",e);  
       }	   
	   return vlanCommunities;
   }

   
   public static void main(String[] args)
   {
	  SnmpUtil.getInstance().findLinks(2,"192.168.209.253", "public",1,"192.168.204.254", "public");	   
//	  for(String temp:vlans)
//	  {
//		  System.out.println("vlan=" + temp);  
//		  List ifs = SnmpUtil.getInstance().getIfEntityList("10.10.10.252",temp,2);
//		  if(ifs==null) continue;
//		  
//		  for(int i=0;i<ifs.size();i++)
//		  {
//			  IfEntity ifObj = (IfEntity)ifs.get(i);
//			  System.out.println(ifObj.getIpAddress() + "," + ifObj.getPhysAddress());
//		  }
//	  }
   }   
}
