package com.afunms.polling.snmp.ping;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.PingInfoParser;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.model.IpAlias;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.UPSNode;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.telnet.SSHWrapper;
import com.afunms.polling.telnet.TelnetWrapper;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.RemotePingHostDao;
import com.afunms.topology.dao.RemotePingNodeDao;
import com.afunms.topology.model.ConnectTypeConfig;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.RemotePingHost;
import com.afunms.topology.model.RemotePingNode;
import com.gatherResulttosql.HostnetPingResultTosql;
import com.gatherResulttosql.NetHostPingdatatempRtosql;
import com.gatherResulttosql.StoragePingResultTosql;



/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PingSnmp {
	//private Hashtable sendeddata = ShareData.getProcsendeddata();
	private  Hashtable connectConfigHashtable = new Hashtable();
	 
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public PingSnmp() {
	}

	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {		
       
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));

		
		Hashtable returnhash = new Hashtable();
		HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
    	Vector vector=null;
    	
    	if(node == null)
    		return returnhash;
    	
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
//    				SysLogger.info("######## "+node.getIpAddress()+" 不在采集PING时间段内,退出##########");
    				return returnhash;
    			}
    		}
    	}
//    	TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
//    	int _result = 0;
//    	_result = timeconfig.isBetween(node.getId()+"", "equipment");
//		//if(result != 1 && result != 2)return null;
//		if(_result ==1 ){
//			//SysLogger.info("########时间段内: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
//		}else if(_result == 2){
//			//SysLogger.info("########全天: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
//		}else {
//			//SysLogger.info("######## "+node.getIpAddress()+" 不在采集PING时间段内,退出##########");
//			return null;
//		}
    	//初始化连通性检测方式数据
//		ConnectTypeConfigDao connectTypeConfigDao = new ConnectTypeConfigDao();
//		List configList = new ArrayList();
//		try{
//			configList = connectTypeConfigDao.loadAll();
//		}catch(Exception e){
//			
//		}finally{
//			connectTypeConfigDao.close();
//			connectTypeConfigDao = null;
//		}
//		if(configList != null && configList.size()>0){
//			for(int i=0;i<configList.size();i++){
//				ConnectTypeConfig connectTypeConfig = (ConnectTypeConfig)configList.get(i);
//				connectConfigHashtable.put(connectTypeConfig.getNode_id(), connectTypeConfig);
//			}
//		}
		connectConfigHashtable=(Hashtable)ShareData.getConnectConfigHashtable().get("connectConfigHashtable");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		Node host = (Host)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
		Calendar _date=Calendar.getInstance();
		Date ccc = _date.getTime();
		String times = sdf.format(ccc);
		host.setLastTime(times);
    	
        try {    
        	/*
        	 * 先判断是否为remoteping的主服务器,若是,则除了进行PING之外,还要对下联的子节点进行REMOTEPING
        	 */
        	if(node.getEndpoint()==1){
    			PingUtil pingU=new PingUtil(node.getIpAddress());
    			Integer[] packet=pingU.ping();
    			vector=pingU.addhis(packet); 
    			if(vector!=null){				
//    				hostdataManager.createHostData(vector);		
    				hostdataManager.createHostData(vector,alarmIndicatorsNode);	
    				ShareData.setPingdata(node.getIpAddress(),vector);
    				returnhash.put("ping", vector);
    			}
    			vector=null;
    			
    			if(node.getCategory() < 4 ){
    				//网络设备则需要PING关联的节点地址
    				IpAliasDao ipdao = new IpAliasDao();
    			     List iplist = new ArrayList();
    			     try{
    			    	 iplist = ipdao.loadByIpaddress(node.getIpAddress());
    			     }catch(Exception e){
    			    	 e.printStackTrace();
    			     }finally{
    			    	 ipdao.close();
    			     }
    			     if(iplist!=null && iplist.size()>0){
	    			     try{
	    			    	 int numThreads = iplist.size();
	    			     
//	    			    	 // 生成线程池
//	    			    	 ThreadPool threadPool = new ThreadPool(numThreads);														
//	    			    	 // 运行任务
//	    			    	 for (int i=0; i<iplist.size(); i++) {
//	    			    		 //threadPool.runTask(createTask2((IpAlias)iplist.get(i)));
//	    			    	 }    					
//	    			    	 // 关闭线程池并等待所有任务完成
//	    			    	 threadPool.join();
//	    			    	 threadPool.close();
//	    			    	 threadPool = null;
	    			     }catch(Exception e){
	    			    	 SysLogger.error("error in ExecutePing!"+e.getMessage());
	    			     }finally{
	    			     }
    			     }
    			}			
        		//需要对其下属的子节点进行PING操作			
    			List list = null ;   			
    			RemotePingNodeDao remotePingNodeDao = new RemotePingNodeDao();
    			try {
    				list = remotePingNodeDao.findByNodeId(String.valueOf(node.getId()));
    			} catch (RuntimeException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} finally{
    				remotePingNodeDao.close();
    				remotePingNodeDao = null;
    			} 
    			HostNodeDao hostNodeDao = new HostNodeDao();   			
    			try{
    				RemotePingHost remotePingHost = null ;
    				RemotePingHostDao remotePingHostDao = new RemotePingHostDao();
    				try {
						remotePingHost =  remotePingHostDao.findByNodeId(String.valueOf(node.getId()));
					} catch (RuntimeException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally{
						remotePingHostDao.close();
						remotePingHostDao = null;
					}
    				TelnetWrapper telnet = new TelnetWrapper();
    				try {
						telnet.connect(node.getIpAddress(), 23);
						telnet.login(remotePingHost.getUsername(), remotePingHost.getPassword(),
								remotePingHost.getLoginPrompt(), remotePingHost.getPasswordPrompt() , 
								remotePingHost.getShellPrompt());						
						// 运行任务
						for (int i=0; i<list.size(); i++) {
							String result = "";
							RemotePingNode remotePingNode = (RemotePingNode)list.get(i);
							HostNode hostNodeTemp = (HostNode)hostNodeDao.findByID(remotePingNode.getChildNodeId());
			    			result = telnet.send("ping " + hostNodeTemp.getIpAddress());
			    			setData(result , hostNodeTemp,alarmIndicatorsNode);
						}
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						telnet.disconnect();
					}
    			}catch(Exception e){
    				SysLogger.error("error in ExecutePing!"+e.getMessage());
    		  	}
    			finally{
    				hostNodeDao.close();
    				hostNodeDao = null;
    				
    			}       		
        	}else if(node.getEndpoint()==2){
        		//若为REMOTEPING子节点，则返回
        		//return;
        	}else{
        		//正常的设备
        		if(connectConfigHashtable.containsKey(node.getId()+"")){
        			//若存在连通性检测配置,为只通过TELNET或SSH检测连通性
        			ConnectTypeConfig connectTypeConfig = (ConnectTypeConfig)connectConfigHashtable.get(node.getId()+"");
        			if("telnet".equalsIgnoreCase(connectTypeConfig.getConnecttype())){
        				//用TELNET方式进行检测
        				Pingcollectdata hostdata=null;
    					Calendar date=Calendar.getInstance();
    					hostdata=new Pingcollectdata();
    					hostdata.setIpaddress(node.getIpAddress());
    					hostdata.setCollecttime(date);
    					hostdata.setCategory("Ping");
    					hostdata.setEntity("Utilization");
    					hostdata.setSubentity("ConnectUtilization");
    					hostdata.setRestype("dynamic");
    					hostdata.setUnit("%");
        				int flag = 0;
        				Vector<Pingcollectdata> _vector = new Vector<Pingcollectdata>();
        				TelnetWrapper telnet = new TelnetWrapper();
        				long starttime = 0;
        				long endtime = 0;
        				long condelay = 0;
        				try
						{
        					starttime = System.currentTimeMillis();
							telnet.connect(node.getIpAddress(), 23);
							telnet.login(connectTypeConfig.getUsername(), EncryptUtil.decode(connectTypeConfig.getPassword()), connectTypeConfig.getLoginPrompt(), connectTypeConfig.getPasswordPrompt(), connectTypeConfig.getShellPrompt());
							endtime = System.currentTimeMillis();
						} catch (Exception e){
							endtime = System.currentTimeMillis();
							e.printStackTrace();
							flag = 1;
						}finally{
							try{
								telnet.disconnect();
							} catch (Exception e){
								e.printStackTrace();
							}
						}
						condelay = endtime - starttime;
						if(flag == 0){
							hostdata.setThevalue("100");
						}else{
							hostdata.setThevalue("0");
						}
						_vector.add(0, hostdata);
						
						//响应时间
						hostdata=new Pingcollectdata();
						hostdata.setIpaddress(node.getIpAddress());
						hostdata.setCollecttime(date);
						hostdata.setCategory("Ping");
						hostdata.setEntity("ResponseTime");
						hostdata.setSubentity("ResponseTime");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("毫秒");
						hostdata.setThevalue(condelay+"");
						_vector.add(1, hostdata);
            			if(_vector!=null){				
//            				hostdataManager.createHostData(_vector);	
            				hostdataManager.createHostData(vector,alarmIndicatorsNode);	
            				ShareData.setPingdata(node.getIpAddress(),_vector);
            				returnhash.put("ping", _vector);
            			}            			
            			vector=null;            			
        			}else if("ssh".equalsIgnoreCase(connectTypeConfig.getConnecttype())){
        				//用SSH方式进行检测
        				Pingcollectdata hostdata=null;
    					Calendar date=Calendar.getInstance();
    					hostdata=new Pingcollectdata();
    					hostdata.setIpaddress(node.getIpAddress());
    					hostdata.setCollecttime(date);
    					hostdata.setCategory("Ping");
    					hostdata.setEntity("Utilization");
    					hostdata.setSubentity("ConnectUtilization");
    					hostdata.setRestype("dynamic");
    					hostdata.setUnit("%");
    					
    					int nodeId = node.getId();
            			
            			RemotePingHostDao hostDao = new RemotePingHostDao();
            			RemotePingHost params = hostDao.findByNodeId(String.valueOf(nodeId)); 
            			hostDao.close();
    					
        				int flag = 0;
        				Vector<Pingcollectdata> _vector = new Vector<Pingcollectdata>();
        				SSHWrapper ssh = new SSHWrapper();
        				long starttime = 0;
        				long endtime = 0;
        				long condelay = 0;
        				try
						{
        					starttime = System.currentTimeMillis();
        					//SysLogger.info("======="+connectTypeConfig.getPassword()+"---------pwd");
        					ssh.connect(node.getIpAddress(), 22 , params.getUsername() , params.getPassword());
							endtime = System.currentTimeMillis();
						} catch (Exception e){
							endtime = System.currentTimeMillis();
							e.printStackTrace();
							flag = 1;
						}finally{
							try{
								ssh.disconnect();
							} catch (Exception e){
								e.printStackTrace();
							}
						}
						condelay = endtime - starttime;
						if(flag == 0){
							hostdata.setThevalue("100");
						}else{
							hostdata.setThevalue("0");
						}
						_vector.add(0, hostdata);
						
						//响应时间
						hostdata=new Pingcollectdata();
						hostdata.setIpaddress(node.getIpAddress());
						hostdata.setCollecttime(date);
						hostdata.setCategory("Ping");
						hostdata.setEntity("ResponseTime");
						hostdata.setSubentity("ResponseTime");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("毫秒");
						hostdata.setThevalue(condelay+"");
						_vector.add(1, hostdata);
            			if(_vector!=null){				
            				//hostdataManager.createHostData(_vector);		
            				hostdataManager.createHostData(_vector,alarmIndicatorsNode);	
            				ShareData.setPingdata(node.getIpAddress(),_vector);
            				returnhash.put("ping", _vector);
            			}
            			_vector=null;
        			}else{
        				//其他方式可以在这里扩展
        			}
        		}else{
        			//通过PING操作进行连通性检测        			
    				PingUtil pingU=new PingUtil(node.getIpAddress());
        			Integer[] packet=pingU.ping();
        			try{
        				//if(packet != null)
        					vector=pingU.addhis(packet); 
        			}catch(Exception e){
        				SysLogger.error(node.getIpAddress()+"===ping数据为空");
        				e.printStackTrace();
        			}
        			if(vector!=null){				
        				//hostdataManager.createHostData(vector,alarmIndicatorsNode);					
        				ShareData.setPingdata(node.getIpAddress(),vector);
        				returnhash.put("ping", vector);
        			}
        			
        			vector=null;
//        			if(node.getCategory() < 4 ){
//        				//网络设备则需要PING关联的节点地址
//        				IpAliasDao ipdao = new IpAliasDao();
//        			     List iplist = new ArrayList();
//        			     try{
//        			    	 iplist = ipdao.loadByIpaddress(node.getIpAddress());
//        			     }catch(Exception e){
//        			    	 e.printStackTrace();
//        			     }finally{
//        			    	 ipdao.close();
//        			     }
//        			     if(iplist!=null && iplist.size()>0){
//    	    			     try{
//    	    			    	 int numThreads = iplist.size();  	    			     
//    	    			    	 // 生成线程池
//    	    			    	 ThreadPool threadPool = new ThreadPool(numThreads);														
//    	    			    	 // 运行任务
//    	    			    	 for (int i=0; i<iplist.size(); i++) {
//    	    			    		 threadPool.runTask(createTask2((IpAlias)iplist.get(i)));
//    	    			    	 }      	    					
//    	    			    	 // 关闭线程池并等待所有任务完成
//    	    			    	 threadPool.join();
//    	    			     }catch(Exception e){
//    	    			    	 SysLogger.info("error in ExecutePing!"+e.getMessage());
//    	    			     }finally{
//    	    			     }
//        			     }    
//        			}
        		}
    			
        	}
        }catch(Exception exc){
        	
        }
        
        
        //把数据转换成sql  HP存储执行普通ping操作
        if(host.getCategory() == 14 && host.getOstype() != 44){
        	//存储
        	StoragePingResultTosql tosql=new StoragePingResultTosql();
            tosql.CreateResultTosql(returnhash, node.getIpAddress());      	
        }else{        	
            HostnetPingResultTosql tosql=new HostnetPingResultTosql();
            tosql.CreateResultTosql(returnhash, node.getIpAddress());
        }

        String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	    if(!"0".equals(runmodel)){
			//采集与访问是分离模式,则不需要将监视数据写入临时表格
	    	NetHostPingdatatempRtosql totempsql=new NetHostPingdatatempRtosql();
	        totempsql.CreateResultTosql(returnhash, node);
	    }
        
//		SysLogger.info("====================================");
//		SysLogger.info("==============   开始对PING值进行告警检测 "+node.getIpAddress()+"PING数据     ===============");
//		SysLogger.info("====================================");  
//	    SysLogger.info("=============111111111111======================="+ alarmIndicatorsNode.getName()+"=="+ alarmIndicatorsNode.getType());
	   
      //对PING值进行告警检测
		if(returnhash != null && returnhash.size()>0){
			Vector pingvector = (Vector)returnhash.get("ping");
			if(pingvector != null){
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				for (int i = 0; i < pingvector.size(); i++) {
    				Pingcollectdata pingdata = (Pingcollectdata) pingvector.elementAt(i);
    				if(pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")){
						//连通率进行判断               						
						List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), alarmIndicatorsNode.getType(), "");
						for(int m = 0 ; m < list.size() ; m ++){
							AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(m);
							if("1".equals(_alarmIndicatorsNode.getEnabled())){
								if(_alarmIndicatorsNode.getName().equalsIgnoreCase("ping")){
									CheckEventUtil checkeventutil = new CheckEventUtil();
//									SysLogger.info(_alarmIndicatorsNode.getName()+"=====_alarmIndicatorsNode.getName()=========");
									checkeventutil.checkEvent(node, _alarmIndicatorsNode, pingdata.getThevalue());
								}
							}  
						}
						
					}
    			}
			}
			
			pingvector=null;
		}
	
        
        
        
	    return returnhash;
	}
	private static Runnable createTask2(final IpAlias vo){
		return new Runnable(){
			public void run(){
		    	PingUtil pingU=new PingUtil(vo.getAliasip());
				Integer[] packet=pingU.ping();
				Vector vector=pingU.addhis(packet); 
				Vector endV = new Vector();
				if(vector!=null&&vector.size()>0){
					for(int j=0;j<vector.size();j++){
						Pingcollectdata hostdata = (Pingcollectdata)vector.get(j);
						if(hostdata.getEntity().equals("Utilization")){
							endV.add(hostdata);
							break;
						}
					}							
					ShareData.setRelateippingdata(vo.getAliasip(),endV);

   					}
   				vector=null;
			}
		};
	}
	
	private static void setData(String result ,HostNode hostnode,NodeGatherIndicators alarmIndicatorsNode){
		Vector vector = null ;
		int[] pingresult;
		if(result != null && result.length()>0){
			HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
			pingresult = PingInfoParser.parsePingInfo(result);
			if(pingresult!=null){
				Integer[] integer = new Integer[pingresult.length];
				for(int i = 0 ; i < pingresult.length ; i ++){
					integer[i] = pingresult[i];
				}
				
				PingUtil pingU=new PingUtil(hostnode.getIpAddress());
				vector=pingU.addhis(integer); 
				if(vector!=null){				
					try {
						hostdataManager.createHostData(vector,alarmIndicatorsNode);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ShareData.setPingdata(hostnode.getIpAddress(),vector);
				}
				vector=null;
			}
		}
	}
}