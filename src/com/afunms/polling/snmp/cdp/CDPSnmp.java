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
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.NDP;
import com.afunms.polling.snmp.ndp.NDPSingleSnmp;
import com.afunms.polling.task.ThreadPool;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.dao.HostInterfaceDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.dao.NDPDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.util.TopoHelper;
import com.afunms.topology.util.XmlOperator;
import com.gatherResulttosql.NetHostNDPRttosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CDPSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public CDPSnmp() {
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
		if(node == null)return returnHash;
		SysLogger.info("######## "+node.getIpAddress()+" 开始采集CDP信息##########");
		//判断是否在采集时间段内
    	if(ShareData.getTimegatherhash() != null){
    		if(ShareData.getTimegatherhash().containsKey(node.getId()+":equipment")){
    			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
    			int _result = 0;
    			_result = timeconfig.isBetween((List)ShareData.getTimegatherhash().get(node.getId()+":equipment"));
    			if(_result ==1 ){
    				//SysLogger.info("########时间段内: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
    			}else if(_result == 2){
    				//SysLogger.info("########全天: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
    			}else {
    				SysLogger.info("######## "+node.getIpAddress()+" 不在采集NDP时间段内,退出##########");
//    				//清除之前内存中产生的告警信息
//    			    try{
//    			    	//清除之前内存中产生的内存告警信息
//						CheckEventUtil checkutil = new CheckEventUtil();
//						checkutil.deleteEvent(node.getId()+":host:diskperc");
//						checkutil.deleteEvent(node.getId()+":host:diskinc");
//    			    }catch(Exception e){
//    			    	e.printStackTrace();
//    			    }
    				return returnHash;
    			}
    			
    		}
    	}
    	
    	
    	Hashtable formerCDP = new Hashtable();
    	Hashtable ipmacHash = new Hashtable();
    	
    	//List nodeIpAliasList = new ArrayList();
    	
    	IpAliasDao nodeipaliasdao = new IpAliasDao();
		Hashtable nodeipaliasHash = new Hashtable();
		try{
			List aliasList = nodeipaliasdao.loadByIpaddress(node.getIpAddress());
			if(aliasList != null && aliasList.size()>0){
				for(int k=0;k<aliasList.size();k++){
					IpAlias vo = (IpAlias)aliasList.get(k);
					nodeipaliasHash.put(vo.getAliasip(), vo.getIpaddress());
				}
				nodeipaliasHash.put(node.getIpAddress(), node.getIpAddress());
			}
		}catch(Exception e){
			
		}finally{
			nodeipaliasdao.close();
		}
		
        Hashtable nodelistHash = new Hashtable();
        try {
        	List hostlist = PollingEngine.getInstance().getNodeList();
            if (hostlist != null && hostlist.size() > 0) {
                for (int i = 0; i < hostlist.size(); i++) {
                	Host _node = (Host) hostlist.get(i);
                    //SysLogger.info("====put "+_node.getIpAddress()+" to nodelistHash ====");
                    nodelistHash.put(_node.getIpAddress(), _node.getIpAddress());

                }
            }
        } catch (Exception e) {

        } finally {
//            hostNodeDao.close();
        }
    	
//    	if(node.getMac().contains("|")){
//			
//			
//			String[] macs = node.getMac().split("|");
//			for(int k=0;k<macs.length;k++){
//				SysLogger.info(macs[k]+"===="+node.getIpAddress());
//				nodeMacList.add(macs[k]);
//			}
//		}else{
//			SysLogger.info(node.getMac()+"===="+node.getIpAddress());
//			nodeMacList.add(node.getMac());
//		}
    	
    	
		try {
			Calendar date=Calendar.getInstance();
			if(ShareData.getSharedata().get(node.getIpAddress()) != null){
				if(((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).containsKey("cdp")){
					Vector former_cdpVector = (Vector)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).get("cdp");
					if(former_cdpVector != null && former_cdpVector.size()>0){
						CdpCachEntryInterface cdp = null;
						for(int i=0;i<former_cdpVector.size();i++){
							cdp = (CdpCachEntryInterface)former_cdpVector.get(i);
							formerCDP.put(cdp.getIp()+"|"+cdp.getPortdesc(), cdp);
						}
					}
					Vector ipmacVector = (Vector)((Hashtable)ShareData.getSharedata().get(node.getIpAddress())).get("ipmac");
					if(ipmacVector != null && ipmacVector.size()>0){
						IpMac ipmac = null;
						for(int i=0;i<ipmacVector.size();i++){
							ipmac = (IpMac)ipmacVector.get(i);
							ipmacHash.put(ipmac.getMac(), ipmac.getIpaddress());
						}
					}
				}
			}
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
			if(ipAllData == null)ipAllData = new Hashtable();
			
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
	    
		HostNodeDao hostNodeDao = new HostNodeDao();
		Hashtable nodehash = new Hashtable();
		try{
			List hostlist = hostNodeDao.loadall();
			if(hostlist != null && hostlist.size()>0){
				for(int i=0;i<hostlist.size();i++){
					HostNode _node = (HostNode)hostlist.get(i);
					if(_node.getBridgeAddress() != null && _node.getBridgeAddress().trim().length()>0){
						//SysLogger.info(_node.getBridgeAddress()+"======");
						
						if(_node.getBridgeAddress().contains("|")){
							
							
							String[] macs = _node.getBridgeAddress().split("|");
							for(int k=0;k<macs.length;k++){
								//SysLogger.info(macs[k]+"===="+_node.getIpAddress());
								nodehash.put(macs[k], _node.getIpAddress());
							}
						}else{
							//SysLogger.info(_node.getBridgeAddress()+"===="+_node.getIpAddress());
							nodehash.put(_node.getBridgeAddress(), _node.getIpAddress());
						}
					}
					
				}
			}
		}catch(Exception e){
			
		}finally{
			hostNodeDao.close();
		}	    
	    
	    
	    Vector newCDP = new Vector();
	    if(cdpVector != null && cdpVector.size()>0){
	    	NDPDao cdpdao = new NDPDao();
	    	List ndplistdb = new ArrayList();
	    	Hashtable cdpFromDbHash = new Hashtable();
	    	try{
	    		ndplistdb = cdpdao.getbynodeid(Long.parseLong(node.getId()+""));
	    		if(ndplistdb != null && ndplistdb.size()>0){
	    			NDP ndp = null;
	    			for(int i=0;i<ndplistdb.size();i++){
	    				ndp = (NDP) ndplistdb.get(i);
	    				cdpFromDbHash.put(ndp.getDeviceId()+"|"+ndp.getPortName(), ndp);
	    			}
	    		}
	    	}catch(Exception e){
	    		
	    	}finally{
	    		cdpdao.close();
	    	}
	    	
	    	IpAliasDao ipaliasdao = new IpAliasDao();
    		Hashtable ipaliasHash = new Hashtable();
    		try{
    			List aliasList = ipaliasdao.loadAll();
    			if(aliasList != null && aliasList.size()>0){
    				for(int k=0;k<aliasList.size();k++){
    					IpAlias vo = (IpAlias)aliasList.get(k);
    					ipaliasHash.put(vo.getAliasip(), vo.getIpaddress());
    				}
    			}
    		}catch(Exception e){
    			
    		}finally{
    			ipaliasdao.close();
    		}
	    	
    		CdpCachEntryInterface cdp = null;
	    	for(int i=0;i<cdpVector.size();i++){
	    		cdp = (CdpCachEntryInterface)cdpVector.get(i);
	    		
	    		SysLogger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	    		SysLogger.info("ip:"+node.getIpAddress()+" ### "+cdp.getIp()+"===="+cdp.getPortdesc());
	    		SysLogger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	    		
	    		if(!formerCDP.containsKey(cdp.getIp()+"|"+cdp.getPortdesc())){
	    			SysLogger.info(" ### 发现新的CDP信息："+cdp.getIp()+"  "+cdp.getPortdesc()+ " ###1");
	    			//找到新的NDP信息，设备有可能是新加的
	    			if(ipaliasHash.containsKey(cdp.getIp()) || nodelistHash.containsKey(cdp.getIp())){
	    				//已经在系统存在该设备,只更改NDP表
	    				newCDP.add(cdp);
	    				//需要重新计算链路关系
                        SysLogger.info("==========================================");
                        SysLogger.info("=======开始计算链路关系 "+cdp.getIp()+"   "+cdp.getPortdesc()+" =======");
                        SysLogger.info("==========================================");
                        //com.afunms.discovery.Host host = null;
                        
                        String ip = cdp.getIp();
                        if(ipaliasHash.containsKey(cdp.getIp()))ip = (String)ipaliasHash.get(cdp.getIp());
                        if(nodelistHash.containsKey(cdp.getIp()))ip = (String)nodelistHash.get(cdp.getIp());
                        Host _host = (Host) PollingEngine.getInstance().getNodeByIp(ip);
                        if(_host == null)continue;
                        Hashtable portDescHash = new Hashtable();
                        
                        Vector _cdpVector = null;
                        if(ShareData.getSharedata() != null && ShareData.getSharedata().containsKey(ip)){
                        	if(((Hashtable)ShareData.getSharedata().get(ip)).containsKey("cdp")){
                        		_cdpVector=(Vector)((Hashtable)ShareData.getSharedata().get(ip)).get("cdp");
                        	}
                        	Vector interfaceVector = null;
                        	if (((Hashtable) ShareData.getSharedata().get(ip)).containsKey("interface")) {
                                interfaceVector = (Vector) ((Hashtable) ShareData.getSharedata().get(ip)).get("interface");
                                if(interfaceVector != null && interfaceVector.size()>0){
                                	Interfacecollectdata interfaceCollectData = null;
                                	for(int m=0;m<interfaceVector.size();m++){
                                		interfaceCollectData = (Interfacecollectdata) interfaceVector.get(m);
                                		if("ifDescr".equalsIgnoreCase(interfaceCollectData.getEntity())){
                                			//端口描述:端口索引
                                			portDescHash.put(interfaceCollectData.getThevalue(), interfaceCollectData.getSubentity());
                                		}
                                	}
                                }
                        	}
                        }
                        
                        if(_cdpVector != null && _cdpVector.size()>0){
                        	com.afunms.polling.node.IfEntity endIfEntity = null;
                        	IfEntity startIfEntity = null;                       	
                        	if(portDescHash.containsKey(cdp.getPortdesc())){
                        		//存在改端口描述
                        		endIfEntity = _host.getIfEntityByIndex((String)portDescHash.get(cdp.getPortdesc()));
                        	}
                        	if(endIfEntity != null){
                            	//若存在对端接口
                        		CdpCachEntryInterface _cdp = null;
                				for(int k=0;k<_cdpVector.size();k++){
                					_cdp = (CdpCachEntryInterface)_cdpVector.get(k);
                					//SysLogger.info(_host.getIpAddress()+"   ---    "+_cdp.getIp()+"  ---  "+_cdp.getPortdesc());
    		            			for(Enumeration enumeration = nodeipaliasHash.keys(); enumeration.hasMoreElements();){
    		    						String ipalias = (String)enumeration.nextElement();
    		    						String ipaddress = (String)nodeipaliasHash.get(ipalias);
    		    						//SysLogger.info("aliasip:"+ipalias+"   ipaddress:"+ipaddress);
    		    					}
                						 if(nodeipaliasHash.containsKey(_cdp.getIp())){
                							 HostInterfaceDao ifdao = new HostInterfaceDao();
                							 try{
                								 startIfEntity = (IfEntity)ifdao.loadInterfacesByNodeIDAndDesc(node.getId(), _cdp.getPortdesc());
                							 }catch(Exception e){
                								 e.printStackTrace();
                							 }finally{
                								 ifdao.close();
                							 }
                							 if(startIfEntity != null && endIfEntity != null){
                								 Link link = new Link();
                								 link.setStartId(node.getId());
                								 link.setStartIndex(startIfEntity.getIndex());
                								 link.setStartIp(startIfEntity.getIpAddress()==null?"":startIfEntity.getIpAddress());
                								 link.setStartDescr(startIfEntity.getDescr());
                								 
                								 link.setEndId(_host.getId());
                								 link.setEndIndex(endIfEntity.getIndex());
                								 link.setEndIp(endIfEntity.getIpAddress()==null?"":endIfEntity.getIpAddress());
                								 link.setEndDescr(endIfEntity.getDescr());
                								 
                								 link.setMaxSpeed("200000");
                								 link.setMaxPer("50");
                								 link.setLinktype(1);
                								 link.setType(1);
                								 link.setFindtype(1);
                								 link.setLinkName(node.getIpAddress()+"/"+link.getStartDescr()+"-"+_host.getIpAddress()+"/"+link.getEndDescr());
                								 
                								 
                								 List linklist = PollingEngine.getInstance().getLinkList();
                		                            Hashtable existLinkHash = new Hashtable();
                		                            Hashtable existEndLinkHash = new Hashtable();
                		                            if(linklist != null && linklist.size()>0){
                		                            	LinkRoad lr = null;
                		                            	
                		                            	for(int p=0;p<linklist.size();p++){
                		                            		lr = (LinkRoad)linklist.get(p);
                		                            		existLinkHash.put(lr.getStartId()+":"+lr.getStartIndex()+":"+lr.getEndId()+":"+lr.getEndIndex(), lr);
                		                            		existEndLinkHash.put(lr.getEndId()+":"+lr.getEndIndex()+":"+lr.getStartId()+":"+lr.getStartIndex(), lr);
                		                            		
                		                            	}
                		                            }
                		                            
                		                            if(!existLinkHash.containsKey(link.getStartId()+":"+link.getStartIndex()+":"+link.getEndId()+":"+link.getEndIndex())
                                                     		&& !existLinkHash.containsKey(link.getEndId()+":"+link.getEndIndex()+":"+link.getStartId()+":"+link.getStartIndex())
                                                     		&& !existEndLinkHash.containsKey(link.getStartId()+":"+link.getStartIndex()+":"+link.getEndId()+":"+link.getEndIndex())
                                                     		&& !existEndLinkHash.containsKey(link.getEndId()+":"+link.getEndIndex()+":"+link.getStartId()+":"+link.getStartIndex())){
                		                            	if(link.getStartId() == link.getEndId())continue;
                		                            	LinkDao linkdao = new LinkDao();
                		                            	try{
                       									 linkdao.save(link);
                       									 
                       									 XmlOperator xopr = new XmlOperator();
                           								 xopr.setFile("network.jsp");
                           								 xopr.init4updateXml();
                           								 xopr.addLine(link.getLinkName(),String.valueOf(link.getId()),"net"+String.valueOf(link.getStartId()),"net"+String.valueOf(link.getEndId()));
                           								 xopr.writeXml();
                           								 
                       									//链路信息实时更新
                        					    			LinkRoad lr = new LinkRoad();
                        					    			lr.setId(link.getId());
                        					    			lr.setLinkName(link.getLinkName());
                        					    			lr.setLinkName(link.getLinkName());//yangjun add
                        					    			lr.setMaxSpeed(link.getMaxSpeed());//yangjun add
                        					    			lr.setMaxPer(link.getMaxPer());//yangjun add
                        					    			lr.setStartId(link.getStartId());
                        					    			lr.setStartIp(link.getStartIp());		
                        					    			lr.setStartIndex(link.getStartIndex());
                        					    			lr.setStartDescr(link.getStartDescr());
                        					    			lr.setEndIp(link.getEndIp());				
                        					    			lr.setEndId(link.getEndId());
                        					    			lr.setEndIndex(link.getEndIndex());
                        					    			lr.setEndDescr(link.getEndDescr());
                        					    			lr.setAssistant(link.getAssistant());
                        					    			lr.setType(link.getType());
                        					    			lr.setShowinterf(link.getShowinterf());
                        					    			PollingEngine.getInstance().getLinkList().add(lr);
                       								 }catch(Exception e){
                       									 e.printStackTrace();
                       								 }finally{
                       									 linkdao.close();
                       								 }
                       								 break;
                		                            }
                								 
                								 
                							 }
                							 	
                						 }
                					}
                        	}			
                        }                 
	    			}else{
	    				//系统不存在该设备
	    				SysLogger.info("cdp.getIP()======"+cdp.getIp());
		    			String ip = cdp.getIp();
		    			//对这个IP地址进行SNMP添加
		    			SysLogger.info(ip+"     对这个IP地址进行SNMP添加1");
		    			try{
                            if(ipaliasHash.containsKey(ip)){
                            	//已经存在该设备
                            	String nodeIp = "";
                                if(nodelistHash.containsKey(ip))nodeIp = (String)nodelistHash.get(ip);
                                if(nodeIp == null || nodeIp.trim().length()==0){
                                	if(ipaliasHash.containsKey(ip))nodeIp = (String)ipaliasHash.get(ip);
                                }
                                Host _host = null;
                                if(nodeIp != null && nodeIp.trim().length()>0){
                                	_host = (Host) PollingEngine.getInstance().getNodeByIp(nodeIp);
                                	if(_host != null){
                                		//只计算链路关系
                                		continue;
                                	}
                                }
                            }
                            
		    				TopoHelper helper = new TopoHelper(); //包括更新数据库和更新内存
		    				int addResult = 0;
		    				addResult = helper.addHost(node.getAssetid(),node.getLocation(),ip,ip,node.getSnmpversion(),node.getCommunity(),node.getWritecommunity(),
		    							node.getTransfer(),2,node.getOstype(),
		    			    			1,node.getBid(),node.getSendmobiles(),node.getSendemail(),node.getSendphone(),node.getSupperid(),true,
		    			    			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase());
		    					
		    				
                        	List linklist = PollingEngine.getInstance().getLinkList();
                            Hashtable existLinkHash = new Hashtable();
                            Hashtable existEndLinkHash = new Hashtable();
                            if(linklist != null && linklist.size()>0){
                            	LinkRoad lr = null;
                            	
                            	for(int p=0;p<linklist.size();p++){
                            		lr = (LinkRoad)linklist.get(p);
                            		existLinkHash.put(lr.getStartId()+":"+lr.getStartIndex()+":"+lr.getEndId()+":"+lr.getEndIndex(), lr);
                            		existEndLinkHash.put(lr.getEndId()+":"+lr.getEndIndex()+":"+lr.getStartId()+":"+lr.getStartIndex(), lr);
                            		
                            	}
                            }
		    				//生成链路
		    				CDPSingleSnmp cdpsnmp = null;
		    				com.afunms.discovery.Host host = null;
		    				NodeGatherIndicators _alarmIndicatorsNode = new NodeGatherIndicators();
		    				try{
		    					host = helper.getHost();	
		    					if(host == null)continue;
		    					HostNodeDao nodedao = new HostNodeDao();
		    					try{
		    						nodedao.addNodeByNDP(host, addResult);
		    						//网络设备
		    					    XmlOperator opr = new XmlOperator();
		    					    opr.setFile("network.jsp");
		    					    opr.init4updateXml();
		    					    opr.addNode(helper.getHost());   
		    				        opr.writeXml();
		    					}catch(Exception e){
		    						
		    					}finally{
		    						nodedao.close();
		    					}
		    						
		    						
		    					_alarmIndicatorsNode.setNodeid(host.getId()+"");
		    					cdpsnmp = (CDPSingleSnmp)Class.forName("com.afunms.polling.snmp.cdp.CDPSingleSnmp").newInstance();
                        		returnHash = cdpsnmp.collect_Data(_alarmIndicatorsNode);
                        		IfEntity endIfEntity = host.getIfEntityByDesc(cdp.getPortdesc());
                        		IfEntity startIfEntity = null;
                        		if(returnHash != null && returnHash.containsKey("cdp")){
                        			Vector _cdpVector=(Vector)returnHash.get("cdp");
                        			if(_cdpVector != null && _cdpVector.size()>0){
                        				CdpCachEntryInterface _cdp = null;
                        				for(int k=0;k<_cdpVector.size();k++){
                        					_cdp = (CdpCachEntryInterface)_cdpVector.get(k);
                        					//SysLogger.info(host.getIpAddress()+"   ---    "+_cdp.getIp()+"  ---  "+_cdp.getPortdesc());
                        					
            		            			for(Enumeration enumeration = nodeipaliasHash.keys(); enumeration.hasMoreElements();){
            		    						String ipalias = (String)enumeration.nextElement();
            		    						String ipaddress = (String)nodeipaliasHash.get(ipalias);
            		    						//SysLogger.info("aliasip:"+ipalias+"   ipaddress:"+ipaddress);
            		    					}
                        						 if(nodeipaliasHash.containsKey(_cdp.getIp())){
                        							 HostInterfaceDao ifdao = new HostInterfaceDao();
                        							 try{
                        								 startIfEntity = (IfEntity)ifdao.loadInterfacesByNodeIDAndDesc(node.getId(), _cdp.getPortdesc());
                        							 }catch(Exception e){
                        								 e.printStackTrace();
                        							 }finally{
                        								 ifdao.close();
                        							 }
                        							 if(startIfEntity != null && endIfEntity != null){
                        								 Link link = new Link();
                        								 link.setStartId(node.getId());
                        								 link.setStartIndex(startIfEntity.getIndex());
                        								 link.setStartIp(startIfEntity.getIpAddress()==null?"":startIfEntity.getIpAddress());
                        								 link.setStartDescr(startIfEntity.getDescr());
                        								 
                        								 link.setEndId(host.getId());
                        								 link.setEndIndex(endIfEntity.getIndex());
                        								 link.setEndIp(endIfEntity.getIpAddress()==null?"":endIfEntity.getIpAddress());
                        								 link.setEndDescr(endIfEntity.getDescr());
                        								 
                        								 link.setMaxSpeed("200000");
                        								 link.setMaxPer("50");
                        								 link.setLinktype(1);
                        								 link.setType(1);
                        								 link.setFindtype(1);
                        								 link.setLinkName(node.getIpAddress()+"/"+link.getStartDescr()+"-"+host.getIpAddress()+"/"+link.getEndDescr());
            								 
                        								 if(!existLinkHash.containsKey(link.getStartId()+":"+link.getStartIndex()+":"+link.getEndId()+":"+link.getEndIndex())
                                                         		&& !existLinkHash.containsKey(link.getEndId()+":"+link.getEndIndex()+":"+link.getStartId()+":"+link.getStartIndex())
                                                         		&& !existEndLinkHash.containsKey(link.getStartId()+":"+link.getStartIndex()+":"+link.getEndId()+":"+link.getEndIndex())
                                                         		&& !existEndLinkHash.containsKey(link.getEndId()+":"+link.getEndIndex()+":"+link.getStartId()+":"+link.getStartIndex())){
                        									 if(link.getStartId() == link.getEndId())continue;
                        									 LinkDao linkdao = new LinkDao();
                            								 try{
                            									 linkdao.save(link);
                            									 
                            									 XmlOperator xopr = new XmlOperator();
                                								 xopr.setFile("network.jsp");
                                								 xopr.init4updateXml();
                                								 xopr.addLine(link.getLinkName(),String.valueOf(link.getId()),"net"+String.valueOf(link.getStartId()),"net"+String.valueOf(link.getEndId()));
                                								 xopr.writeXml();
                                								 
                            									//链路信息实时更新
                             					    			LinkRoad lr = new LinkRoad();
                             					    			lr.setId(link.getId());
                             					    			lr.setLinkName(link.getLinkName());
                             					    			lr.setLinkName(link.getLinkName());//yangjun add
                             					    			lr.setMaxSpeed(link.getMaxSpeed());//yangjun add
                             					    			lr.setMaxPer(link.getMaxPer());//yangjun add
                             					    			lr.setStartId(link.getStartId());
                             					    			lr.setStartIp(link.getStartIp());		
                             					    			lr.setStartIndex(link.getStartIndex());
                             					    			lr.setStartDescr(link.getStartDescr());
                             					    			lr.setEndIp(link.getEndIp());				
                             					    			lr.setEndId(link.getEndId());
                             					    			lr.setEndIndex(link.getEndIndex());
                             					    			lr.setEndDescr(link.getEndDescr());
                             					    			lr.setAssistant(link.getAssistant());
                             					    			lr.setType(link.getType());
                             					    			lr.setShowinterf(link.getShowinterf());
                             					    			PollingEngine.getInstance().getLinkList().add(lr);
                            								 }catch(Exception e){
                            									 e.printStackTrace();
                            								 }finally{
                            									 linkdao.close();
                            								 }
                            								 break;
                        								 }
                        								 
                        							 }
                        							 	
                        						 }
                        					}
                        				}
                        			}
                        			
                        			
		    					}catch(Exception e){
		    						e.printStackTrace();
		    					}finally{
		    						
		    					}
		    					
		    					newCDP.add(cdp);
		    				}catch(Exception e){
		    					e.printStackTrace();
		    				}
	    			}	    			
	    		}else{
	    			//若在上一次采集中有该CDP信息，则需要判断数据库中是否有，若数据库中没有，
	    			//则说明上次没有对该CDP信息进行发现
	    			if(!cdpFromDbHash.containsKey(cdp.getIp()+"|"+cdp.getPortdesc())){
	    				SysLogger.info(" $$$ 发现新的CDP信息："+cdp.getIp()+"  "+cdp.getPortdesc()+ " $$$2");
	    				//找到新的CDP信息，设备有可能是新加的
		    			if(ipaliasHash.containsKey(cdp.getIp()) || nodelistHash.containsKey(cdp.getIp()) ){
		    				//已经在系统存在该设备,只更改CDP表
		    				newCDP.add(cdp);
		    				//需要重新计算链路关系
                            SysLogger.info("==========================================");
                            SysLogger.info("=======开始计算链路关系 "+cdp.getIp()+"   "+cdp.getPortdesc()+" =======");
                            SysLogger.info("==========================================");
                            
                            //com.afunms.discovery.Host host = null;
                            
                            String ip = "";
                            if(ipaliasHash.containsKey(cdp.getIp()))ip = (String)ipaliasHash.get(cdp.getIp());
                            if(nodelistHash.containsKey(cdp.getIp()))ip = (String)nodelistHash.get(cdp.getIp());
                            Host _host = (Host) PollingEngine.getInstance().getNodeByIp(ip);
                            if(_host == null)continue;
                            Hashtable portDescHash = new Hashtable();
                            
                            Vector _cdpVector = null;
                            if(ShareData.getSharedata() != null && ShareData.getSharedata().containsKey(ip)){
                            	if(((Hashtable)ShareData.getSharedata().get(ip)).containsKey("cdp")){
                            		_cdpVector=(Vector)((Hashtable)ShareData.getSharedata().get(ip)).get("cdp");
                            	}
                            	Vector interfaceVector = null;
                            	if (((Hashtable) ShareData.getSharedata().get(ip)).containsKey("interface")) {
                                    interfaceVector = (Vector) ((Hashtable) ShareData.getSharedata().get(ip)).get("interface");
                                    if(interfaceVector != null && interfaceVector.size()>0){
                                    	Interfacecollectdata interfaceCollectData = null;
                                    	for(int m=0;m<interfaceVector.size();m++){
                                    		interfaceCollectData = (Interfacecollectdata) interfaceVector.get(m);
                                    		if("ifDescr".equalsIgnoreCase(interfaceCollectData.getEntity())){
                                    			//端口描述:端口索引
                                    			SysLogger.info(ip+"  "+interfaceCollectData.getThevalue()+"  "+interfaceCollectData.getSubentity());
                                    			portDescHash.put(interfaceCollectData.getThevalue(), interfaceCollectData.getSubentity());
                                    		}
                                    	}
                                    }
                            	}
                            }
                            if(_cdpVector != null && _cdpVector.size()>0){
                            	SysLogger.info("_cdpVector size ##############"+_cdpVector.size());
                            	com.afunms.polling.node.IfEntity endIfEntity = null;
                            	IfEntity startIfEntity = null;    
   							 
   							     //SysLogger.info(ip+" cdp.getPortdesc():"+cdp.getPortdesc());
                            	if(portDescHash.containsKey(cdp.getPortdesc())){
                            		//存在改端口描述
                            		endIfEntity = _host.getIfEntityByIndex((String)portDescHash.get(cdp.getPortdesc()));
                            	}
                            	if(endIfEntity != null){
                                	//若存在对端接口
                            		CdpCachEntryInterface _cdp = null;
                    				for(int k=0;k<_cdpVector.size();k++){
                    					_cdp = (CdpCachEntryInterface)_cdpVector.get(k);
                    					SysLogger.info(_host.getIpAddress()+"   ---    "+_cdp.getIp()+"  ---  "+_cdp.getPortdesc());
        		            			for(Enumeration enumeration = nodeipaliasHash.keys(); enumeration.hasMoreElements();){
        		    						String ipalias = (String)enumeration.nextElement();
        		    						String ipaddress = (String)nodeipaliasHash.get(ipalias);
        		    						//SysLogger.info("aliasip:"+ipalias+"   ipaddress:"+ipaddress);
        		    					}
                    						 if(nodeipaliasHash.containsKey(_cdp.getIp())){
                    							 HostInterfaceDao ifdao = new HostInterfaceDao();
                    							 try{
                    								 startIfEntity = (IfEntity)ifdao.loadInterfacesByNodeIDAndDesc(node.getId(), _cdp.getPortdesc());
                    							 }catch(Exception e){
                    								 e.printStackTrace();
                    							 }finally{
                    								 ifdao.close();
                    							 }
                    							 if(startIfEntity != null && endIfEntity != null){
                    								 Link link = new Link();
                    								 link.setStartId(node.getId());
                    								 link.setStartIndex(startIfEntity.getIndex());
                    								 link.setStartIp(startIfEntity.getIpAddress()==null?"":startIfEntity.getIpAddress());
                    								 link.setStartDescr(startIfEntity.getDescr());
                    								 
                    								 link.setEndId(_host.getId());
                    								 link.setEndIndex(endIfEntity.getIndex());
                    								 link.setEndIp(endIfEntity.getIpAddress()==null?"":endIfEntity.getIpAddress());
                    								 link.setEndDescr(endIfEntity.getDescr());
                    								 
                    								 link.setMaxSpeed("200000");
                    								 link.setMaxPer("50");
                    								 link.setLinktype(1);
                    								 link.setType(1);
                    								 link.setFindtype(1);
                    								 link.setLinkName(node.getIpAddress()+"/"+link.getStartDescr()+"-"+_host.getIpAddress()+"/"+link.getEndDescr());
                    								 
                    								 
                    								 List linklist = PollingEngine.getInstance().getLinkList();
                    		                            Hashtable existLinkHash = new Hashtable();
                    		                            Hashtable existEndLinkHash = new Hashtable();
                    		                            if(linklist != null && linklist.size()>0){
                    		                            	LinkRoad lr = null;
                    		                            	
                    		                            	for(int p=0;p<linklist.size();p++){
                    		                            		lr = (LinkRoad)linklist.get(p);
                    		                            		existLinkHash.put(lr.getStartId()+":"+lr.getStartIndex()+":"+lr.getEndId()+":"+lr.getEndIndex(), lr);
                    		                            		existEndLinkHash.put(lr.getEndId()+":"+lr.getEndIndex()+":"+lr.getStartId()+":"+lr.getStartIndex(), lr);
                    		                            		
                    		                            	}
                    		                            }
                    		                            //SysLogger.info("link======"+link.getStartId()+":"+link.getStartIndex()+":"+link.getEndId()+":"+link.getEndIndex());
                    		                            if(!existLinkHash.containsKey(link.getStartId()+":"+link.getStartIndex()+":"+link.getEndId()+":"+link.getEndIndex())
                                                         		&& !existLinkHash.containsKey(link.getEndId()+":"+link.getEndIndex()+":"+link.getStartId()+":"+link.getStartIndex())
                                                         		&& !existEndLinkHash.containsKey(link.getStartId()+":"+link.getStartIndex()+":"+link.getEndId()+":"+link.getEndIndex())
                                                         		&& !existEndLinkHash.containsKey(link.getEndId()+":"+link.getEndIndex()+":"+link.getStartId()+":"+link.getStartIndex())){
                    		                            	if(link.getStartId() == link.getEndId())continue;
                    		                            	LinkDao linkdao = new LinkDao();
                    		                            	try{
                           									 linkdao.save(link);
                           									 
                           									 XmlOperator xopr = new XmlOperator();
                               								 xopr.setFile("network.jsp");
                               								 xopr.init4updateXml();
                               								 xopr.addLine(link.getLinkName(),String.valueOf(link.getId()),"net"+String.valueOf(link.getStartId()),"net"+String.valueOf(link.getEndId()));
                               								 xopr.writeXml();
                               								 
                           									//链路信息实时更新
                            					    			LinkRoad lr = new LinkRoad();
                            					    			lr.setId(link.getId());
                            					    			lr.setLinkName(link.getLinkName());
                            					    			lr.setLinkName(link.getLinkName());//yangjun add
                            					    			lr.setMaxSpeed(link.getMaxSpeed());//yangjun add
                            					    			lr.setMaxPer(link.getMaxPer());//yangjun add
                            					    			lr.setStartId(link.getStartId());
                            					    			lr.setStartIp(link.getStartIp());		
                            					    			lr.setStartIndex(link.getStartIndex());
                            					    			lr.setStartDescr(link.getStartDescr());
                            					    			lr.setEndIp(link.getEndIp());				
                            					    			lr.setEndId(link.getEndId());
                            					    			lr.setEndIndex(link.getEndIndex());
                            					    			lr.setEndDescr(link.getEndDescr());
                            					    			lr.setAssistant(link.getAssistant());
                            					    			lr.setType(link.getType());
                            					    			lr.setShowinterf(link.getShowinterf());
                            					    			PollingEngine.getInstance().getLinkList().add(lr);
                           								 }catch(Exception e){
                           									 e.printStackTrace();
                           								 }finally{
                           									 linkdao.close();
                           								 }
                           								 break;
                    		                            }else{
                    		                            	SysLogger.info("Exist link==="+link.getStartId()+":"+link.getStartIndex()+":"+link.getEndId()+":"+link.getEndIndex());
                    		                            }
                    								 
                    								 
                    							 }
                    							 	
                    						 }
                    					}
                            	}			
                            }
                            
		    			}else{
		    				//系统不存在该设备
		    				SysLogger.info("cdp.getDeviceId() #####  "+cdp.getIp());
			    			//找到对应的IP
			    			String ip = cdp.getIp();			    			
			    			//判断该IP是否已经在系统中存在
			    			com.afunms.discovery.Host host = null;
			    			//对这个IP地址进行SNMP添加
			    			try{
			    				SysLogger.info(ip+"   ###  对这个IP地址进行SNMP添加3");
			    				
			    				if(ipaliasHash.containsKey(ip)){
                                	//已经存在该设备
                                	String nodeIp = "";
                                    if(nodelistHash.containsKey(ip))nodeIp = (String)nodelistHash.get(ip);
                                    if(nodeIp == null || nodeIp.trim().length()==0){
                                    	if(ipaliasHash.containsKey(ip))nodeIp = (String)ipaliasHash.get(ip);
                                    }
                                    Host _host = null;
                                    if(nodeIp != null && nodeIp.trim().length()>0){
                                    	_host = (Host) PollingEngine.getInstance().getNodeByIp(nodeIp);
                                    	if(_host != null){
                                    		//只计算链路关系
                                    		continue;
                                    	}
                                    }
                                }
			    				
			    				
		    					TopoHelper helper = new TopoHelper(); //包括更新数据库和更新内存
		    				    int addResult = 0;
		    					addResult = helper.addHost(node.getAssetid(),node.getLocation(),ip,ip,node.getSnmpversion(),node.getCommunity(),node.getWritecommunity(),
		    							node.getTransfer(),2,node.getOstype(),
		    			    			1,node.getBid(),node.getSendmobiles(),node.getSendemail(),node.getSendphone(),node.getSupperid(),true,
		    			    			node.getSecuritylevel(),node.getSecurityName(),node.getV3_ap(),node.getAuthpassphrase(),node.getV3_privacy(),node.getPrivacyPassphrase());
		    					
		    					//生成链路		
		    					CDPSingleSnmp cdpsnmp = null;
		    					NodeGatherIndicators _alarmIndicatorsNode = new NodeGatherIndicators();
			    				try{
		    						host = helper.getHost();
		    						if(host == null)continue;
		    						HostNodeDao nodedao = new HostNodeDao();
			    					try{
			    						nodedao.addNodeByNDP(host, addResult);
			    						//网络设备
			    					    XmlOperator opr = new XmlOperator();
			    					    opr.setFile("network.jsp");
			    					    opr.init4updateXml();
			    					    opr.addNode(helper.getHost());   
			    				        opr.writeXml();
			    					}catch(Exception e){
			    						
			    					}finally{
			    						nodedao.close();
			    					}
			    					
		    						_alarmIndicatorsNode.setNodeid(host.getId()+"");
		    						cdpsnmp = (CDPSingleSnmp)Class.forName("com.afunms.polling.snmp.cdp.CDPSingleSnmp").newInstance();
                        			returnHash = cdpsnmp.collect_Data(_alarmIndicatorsNode);
                        			
                        			IfEntity endIfEntity = host.getIfEntityByDesc(cdp.getPortdesc());
                        			IfEntity startIfEntity = null;
                        			if(returnHash != null && returnHash.containsKey("cdp")){
                        				Vector _cdpVector=(Vector)returnHash.get("cdp");
                        				if(_cdpVector != null && _cdpVector.size()>0){

                            				CdpCachEntryInterface _cdp = null;
                            				for(int k=0;k<_cdpVector.size();k++){
                            					_cdp = (CdpCachEntryInterface)_cdpVector.get(k);
                            					//SysLogger.info(host.getIpAddress()+"   ---    "+_cdp.getIp()+"  ---  "+_cdp.getPortdesc());
                            						 if(nodeipaliasHash.containsKey(_cdp.getIp())){
                            							 HostInterfaceDao ifdao = new HostInterfaceDao();
                            							 try{
                            								 startIfEntity = (IfEntity)ifdao.loadInterfacesByNodeIDAndDesc(node.getId(), _cdp.getPortdesc());
                            							 }catch(Exception e){
                            								 e.printStackTrace();
                            							 }finally{
                            								 ifdao.close();
                            							 }
                            							 if(startIfEntity != null && endIfEntity != null){
                            								 Link link = new Link();
                            								 link.setStartId(node.getId());
                            								 link.setStartIndex(startIfEntity.getIndex());
                            								 link.setStartIp(startIfEntity.getIpAddress()==null?"":startIfEntity.getIpAddress());
                            								 link.setStartDescr(startIfEntity.getDescr());
                            								 
                            								 link.setEndId(host.getId());
                            								 link.setEndIndex(endIfEntity.getIndex());
                            								 link.setEndIp(endIfEntity.getIpAddress()==null?"":endIfEntity.getIpAddress());
                            								 link.setEndDescr(endIfEntity.getDescr());
                            								 
                            								 link.setMaxSpeed("200000");
                            								 link.setMaxPer("50");
                            								 link.setLinktype(1);
                            								 link.setType(1);
                            								 link.setFindtype(1);
                            								 link.setLinkName(node.getIpAddress()+"/"+link.getStartDescr()+"-"+host.getIpAddress()+"/"+link.getEndDescr());
                            								 
                            								 List linklist = PollingEngine.getInstance().getLinkList();
                         		                            Hashtable existLinkHash = new Hashtable();
                         		                            Hashtable existEndLinkHash = new Hashtable();
                         		                            if(linklist != null && linklist.size()>0){
                         		                            	LinkRoad lr = null;
                         		                            	
                         		                            	for(int p=0;p<linklist.size();p++){
                         		                            		lr = (LinkRoad)linklist.get(p);
                         		                            		existLinkHash.put(lr.getStartId()+":"+lr.getStartIndex()+":"+lr.getEndId()+":"+lr.getEndIndex(), lr);
                         		                            		existEndLinkHash.put(lr.getEndId()+":"+lr.getEndIndex()+":"+lr.getStartId()+":"+lr.getStartIndex(), lr);
                         		                            		
                         		                            	}
                         		                            }
                         		                           if(!existLinkHash.containsKey(link.getStartId()+":"+link.getStartIndex()+":"+link.getEndId()+":"+link.getEndIndex())
                                                            		&& !existLinkHash.containsKey(link.getEndId()+":"+link.getEndIndex()+":"+link.getStartId()+":"+link.getStartIndex())
                                                            		&& !existEndLinkHash.containsKey(link.getStartId()+":"+link.getStartIndex()+":"+link.getEndId()+":"+link.getEndIndex())
                                                            		&& !existEndLinkHash.containsKey(link.getEndId()+":"+link.getEndIndex()+":"+link.getStartId()+":"+link.getStartIndex())){
                         		                        	  if(link.getStartId() == link.getEndId())continue;
                         		                        	   LinkDao linkdao = new LinkDao();
                             								 try{
                             									 linkdao.save(link);
                             									 
                             									 XmlOperator xopr = new XmlOperator();
                                 								 xopr.setFile("network.jsp");
                                 								 xopr.init4updateXml();
                                 								 xopr.addLine(link.getLinkName(),String.valueOf(link.getId()),"net"+String.valueOf(link.getStartId()),"net"+String.valueOf(link.getEndId()));
                                 								 xopr.writeXml();
                                 								 
                             									//链路信息实时更新
                              					    			LinkRoad lr = new LinkRoad();
                              					    			lr.setId(link.getId());
                              					    			lr.setLinkName(link.getLinkName());
                              					    			lr.setLinkName(link.getLinkName());//yangjun add
                              					    			lr.setMaxSpeed(link.getMaxSpeed());//yangjun add
                              					    			lr.setMaxPer(link.getMaxPer());//yangjun add
                              					    			lr.setStartId(link.getStartId());
                              					    			lr.setStartIp(link.getStartIp());		
                              					    			lr.setStartIndex(link.getStartIndex());
                              					    			lr.setStartDescr(link.getStartDescr());
                              					    			lr.setEndIp(link.getEndIp());				
                              					    			lr.setEndId(link.getEndId());
                              					    			lr.setEndIndex(link.getEndIndex());
                              					    			lr.setEndDescr(link.getEndDescr());
                              					    			lr.setAssistant(link.getAssistant());
                              					    			lr.setType(link.getType());
                              					    			lr.setShowinterf(link.getShowinterf());
                              					    			PollingEngine.getInstance().getLinkList().add(lr);
                             								 }catch(Exception e){
                             									 e.printStackTrace();
                             								 }finally{
                             									 linkdao.close();
                             								 }
                             								 break;
                         		                           }
                            								 
                            								 
                            								 
                            							 }
                            							 	
                            						 }
                            					}
                            				
                        				}
                        			}
                        			
                        			
		    					
			    				}catch(Exception e){
			    						e.printStackTrace();
			    					}finally{
			    						
			    					}
			    					newCDP.add(cdp);
			    				}catch(Exception e){
			    					e.printStackTrace();
			    				}
			    				
			    			//}
		    			}
	    				
	    			}
	    			
	    		}
	    	}
	    }
	    //把采集结果生成sql
	    NetHostNDPRttosql ndptosql=new NetHostNDPRttosql();
	    ndptosql.CreateResultTosql(newCDP, node);
	    
	    cdpVector=null;
	    return returnHash;
	}
	
	/**
    创建URL采集任务
	 */	
	private static Runnable createMACTask(final String ip,final Host node) {
    return new Runnable() {
        public void run() {
        	String sysOid = "";
  	      	int deviceType = 0;
            try { 
            	sysOid = SnmpUtil.getInstance().getSysOid(ip,node.getCommunity());
  	    	  	deviceType = SnmpUtil.getInstance().checkDevice(ip,node.getCommunity(),sysOid);
  	    	  	if(deviceType != 0 && deviceType<4){
  	    	  		//network equipment
  	    	  		List ipaliasList = new ArrayList();
  	    	  		List macList = new ArrayList();
  	    	  		List ifEntityList = SnmpUtil.getInstance().getIfEntityList(ip,node.getCommunity(),deviceType);
  	    	  		if(ifEntityList != null && ifEntityList.size()>0){
  	    	  		IfEntity ifEntity = new IfEntity(); 
  	    	  			for(int i=0;i<ifEntityList.size();i++){
  	    	  				ifEntity = (IfEntity)ifEntityList.get(i); 
  	    	  				if(ifEntity.getIpAddress() != null && ifEntity.getIpAddress().trim().length()>0){
  	    	  					if(!ipaliasList.contains(ifEntity.getIpAddress()))ipaliasList.add(ifEntity.getIpAddress());
  	    	  				}
  	    	  				if(ifEntity.getPhysAddress() != null && ifEntity.getPhysAddress().trim().length()>0){
  	    	  				if(!macList.contains(ifEntity.getPhysAddress()))macList.add(ifEntity.getPhysAddress());
  	    	  				}
  	    	  			}
  	    	  			ShareData.setMacNDP(ip+":"+node.getIpAddress(), macList);
  	    	  			ShareData.setIpaliasNDP(ip+":"+node.getIpAddress(), ipaliasList);
//  	    	  			gatherHash.put("ipalias", ipaliasList);
//  	    	  			gatherHash.put("maclist", macList);
  	    	  		}
  	    	  		ShareData.setIfEntityNDP(ip+":"+node.getIpAddress(), ifEntityList);
  	    	  		
  	    	  	}
            }catch(Exception exc){
            	exc.printStackTrace();
            }
        }
    };
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





