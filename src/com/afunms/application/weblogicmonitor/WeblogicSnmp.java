package com.afunms.application.weblogicmonitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.afunms.common.util.SysLogger;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WeblogicSnmp extends AbstractSnmp {
	private boolean debug=true;
	private  String nethost="1.1.1.1";
	private static Hashtable Enterasys_if_ifStatus = null;
	//private Hashtable allportdata = ShareData.getAllportdata();
	private java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//private static I_HostCollectData hostdataManager =
		//new HostCollectDataManager();	
		static {
			Enterasys_if_ifStatus = new Hashtable();
			Enterasys_if_ifStatus.put("1", "up");
			Enterasys_if_ifStatus.put("2", "down");
			Enterasys_if_ifStatus.put("3", "testing");
			Enterasys_if_ifStatus.put("5", "unknow");
			Enterasys_if_ifStatus.put("7", "unknow");
		};
	/**
	 * @param community
	 * @param port
	 * @param timeout
	 */
	public WeblogicSnmp(String host,String community,Integer port) {
			super(community,port,1600);
			//System.out.println("Start collect data as ip "+nethost+"   "+community+"    "+port);
			this.nethost=host;
		}
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public void run(){
		//while(true){		
	//}
	}
	
	public  Hashtable collectData(){
		Hashtable rValue = new Hashtable();
		List normalValue = collectNormalData();
		List queueValue = collectQueueData();
		List jdbcValue = collectJdbcData();
		List webappValue = collectWebAppData();
		List heapValue = collectHeapData();
		List serverValue = collectServerData();
		List jobValue = collectJobData();
		List servletValue = collectServletData();
		List logValue = collectLogData();
		List transValue = collectTransData();
		rValue.put("normalValue", normalValue);
		rValue.put("queueValue", queueValue);
		rValue.put("jdbcValue", jdbcValue);
		rValue.put("webappValue", webappValue);
		rValue.put("heapValue", heapValue);
		rValue.put("serverValue", serverValue);
		rValue.put("servletValue", servletValue);
		rValue.put("logValue", logValue);
		rValue.put("transValue", transValue);
		return rValue;		
	}
	
	public  Hashtable collectData(Hashtable gatherhash){
		Hashtable rValue = new Hashtable();
		List normalValue = new ArrayList();
		if(gatherhash.containsKey("domain")){
			//采集域
			try{
				normalValue = collectNormalData();
			}catch(Exception e){
				
			}
		}

		List queueValue = new ArrayList();
		if(gatherhash.containsKey("queue")){
			//队列
			queueValue = collectQueueData();
		}
		
		List jdbcValue =  new ArrayList();
		if(gatherhash.containsKey("jdbc")){
			jdbcValue = collectJdbcData();
		}
		
		List webappValue = new ArrayList();
		if(gatherhash.containsKey("webapp")){
			webappValue = collectWebAppData();
		}
		
		List heapValue = new ArrayList();
		if(gatherhash.containsKey("heap")){
			heapValue = collectHeapData();
		}
		
		List serverValue = new ArrayList();
		if(gatherhash.containsKey("server")){
			serverValue = collectServerData();
		}
		
		//List jobValue = collectJobData();
		List servletValue = new ArrayList();
		if(gatherhash.containsKey("servlet")){
			servletValue = collectServletData();
		}
		
		List logValue = new ArrayList();
		if(gatherhash.containsKey("log")){
			logValue = collectLogData();
		}
		
		List transValue = new ArrayList();
		if(gatherhash.containsKey("transaction")){
			transValue = collectTransData();
		}
		
		rValue.put("normalValue", normalValue);
		rValue.put("queueValue", queueValue);
		rValue.put("jdbcValue", jdbcValue);
		rValue.put("webappValue", webappValue);
		rValue.put("heapValue", heapValue);
		rValue.put("serverValue", serverValue);
		rValue.put("servletValue", servletValue);
		rValue.put("logValue", logValue);
		rValue.put("transValue", transValue);
		return rValue;		
	}
	
	public  List collectNormalData(){
		String EntreaSysModle = "";
		List normalList = new ArrayList();
		WeblogicNormal normal = new WeblogicNormal();
		
		try {
					try{					
							  String[] oids =                
								  new String[] {  
									".1.3.6.1.4.1.140.625.530.1.15",   //活动域名
								    ".1.3.6.1.4.1.140.625.530.1.30" , //活动域状态
									".1.3.6.1.4.1.140.625.530.1.51" ,  //端口
									".1.3.6.1.4.1.140.625.530.1.53"    //版本
									
									  };
					this.setVariableBindings(oids);
					List list=this.table(this.getDefault_community(),nethost);					
					for(int i=0;i<list.size();i++){
						  
						TableEvent tbevent=(TableEvent)list.get(i);						
						VariableBinding[] vb=tbevent.getColumns();						
				if (vb != null){
					EntreaSysModle = vb[0].toString().substring(vb[0].toString().indexOf("=")+1,vb[0].toString().length()).trim();				
					String vbString=vb[0].toString();

					for(int j=0;j<vb.length;j++){
						if(vb[j]!=null){
							vbString=vb[j].toString();
							String sValue=vbString.substring(vbString.lastIndexOf("=")+1,vbString.length()).trim();		
							//SysLogger.info(j+"==========="+sValue);
							if(j==0){
								normal.setDomainName(sValue);
							}else if (j==1){
								normal.setDomainActive(sValue);
							}else if (j==2){
								normal.setDomainAdministrationPort(sValue);
							}else if (j==3){
								normal.setDomainConfigurationVersion(sValue);
							}							
						}																																																																																																				
					}
					normalList.add(normal);
					normal = new WeblogicNormal();
				}
			}
			 }
		     catch(Exception e){e.printStackTrace();}	  			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//returnHas.put("flag",flag);
		return normalList;
	}
	public  List collectTransData(){
		String EntreaSysModle = "";
		List transList = new ArrayList();
		WeblogicTrans trans = new WeblogicTrans();
		
		try {
			try{					
					  String[] oids =                
						  new String[] {  
							".1.3.6.1.4.1.140.625.420.1.45",   //
						    ".1.3.6.1.4.1.140.625.420.1.25" , //
							".1.3.6.1.4.1.140.625.420.1.30" ,  //
							".1.3.6.1.4.1.140.625.420.1.35",    //
							".1.3.6.1.4.1.140.625.420.1.40",   //
						    ".1.3.6.1.4.1.140.625.420.1.50" , //
							".1.3.6.1.4.1.140.625.420.1.55" ,  //
							".1.3.6.1.4.1.140.625.420.1.60",    //
							".1.3.6.1.4.1.140.625.420.1.65"  //
							  };
			this.setVariableBindings(oids);
			List list=this.table(this.getDefault_community(),nethost);					
			for(int i=0;i<list.size();i++){
				  
				TableEvent tbevent=(TableEvent)list.get(i);						
				VariableBinding[] vb=tbevent.getColumns();						
		if (vb != null){
			EntreaSysModle = vb[0].toString().substring(vb[0].toString().indexOf("=")+1,vb[0].toString().length()).trim();				
			String vbString=vb[0].toString();

			for(int j=0;j<vb.length;j++){
				if(vb[j]!=null){
					vbString=vb[j].toString();
					String sValue=vbString.substring(vbString.lastIndexOf("=")+1,vbString.length()).trim();		
					if(j==0){
						trans.setTransactionResourceRuntimeResourceName(sValue);
					}else if (j==1){
						trans.setTransactionResourceRuntimeTransactionTotalCount(sValue);
					}else if (j==2){
						trans.setTransactionResourceRuntimeTransactionCommittedTotalCount(sValue);
					}else if (j==3){
						trans.setTransactionResourceRuntimeTransactionRolledBackTotalCount(sValue);
					}else if (j==4){
						trans.setTransactionResourceRuntimeTransactionHeuristicsTotalCount(sValue);
					}else if (j==5){
						trans.setTransactionResourceRuntimeTransactionHeuristicCommitTotalCount(sValue);
					}else if (j==6){
						trans.setTransactionResourceRuntimeTransactionHeuristicRollbackTotalCount(sValue);
					}else if (j==7){
						trans.setTransactionResourceRuntimeTransactionHeuristicMixedTotalCount(sValue);
					}else if (j==8){
						trans.setTransactionResourceRuntimeTransactionHeuristicHazardTotalCount(sValue);
					}											
				}																																																																																																				
			}
			transList.add(trans);
			trans = new WeblogicTrans();
		}
			}
		}
				catch(Exception e){e.printStackTrace();}	  			
			}
					catch(Exception e){
						e.printStackTrace();
					}
					return transList;
		}
	public  List collectJobData(){
		String EntreaSysModle = "";
		List normalList = new ArrayList();
		WeblogicNormal normal = new WeblogicNormal();
		
		try {
					try{					
							  String[] oids =                
								  new String[] {  
									".1.3.6.1.4.1.140.625.1250.1.1",   //
								    ".1.3.6.1.4.1.140.625.1250.1.2" , //
									".1.3.6.1.4.1.140.625.1250.1.3" ,  //
									".1.3.6.1.4.1.140.625.1250.1.4",    //
									".1.3.6.1.4.1.140.625.1250.1.5",   //
								    ".1.3.6.1.4.1.140.625.1250.1.6" , //
									".1.3.6.1.4.1.140.625.1250.1.7" ,  //
									".1.3.6.1.4.1.140.625.1250.1.8",    //
									".1.3.6.1.4.1.140.625.1250.1.9",   //
								    ".1.3.6.1.4.1.140.625.1250.1.10"  //
									  };
					this.setVariableBindings(oids);
					List list=this.table(this.getDefault_community(),nethost);					
					for(int i=0;i<list.size();i++){
						  
						TableEvent tbevent=(TableEvent)list.get(i);						
						VariableBinding[] vb=tbevent.getColumns();						
				if (vb != null){
					EntreaSysModle = vb[0].toString().substring(vb[0].toString().indexOf("=")+1,vb[0].toString().length()).trim();				
					String vbString=vb[0].toString();

					for(int j=0;j<vb.length;j++){
						if(vb[j]!=null){
							vbString=vb[j].toString();
							String sValue=vbString.substring(vbString.lastIndexOf("=")+1,vbString.length()).trim();		
							//SysLogger.info(j+"==========="+sValue);
							if(j==0){
								normal.setDomainName(sValue);
							}else if (j==1){
								normal.setDomainActive(sValue);
							}else if (j==2){
								normal.setDomainAdministrationPort(sValue);
							}else if (j==3){
								normal.setDomainConfigurationVersion(sValue);
							}							
						}																																																																																																				
					}
//					normalList.add(normal);
//					normal = new WeblogicNormal();
				}
			}
			 }
		     catch(Exception e){e.printStackTrace();}	  			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//returnHas.put("flag",flag);
		return normalList;
	}
	
	public  List collectServletData(){
		String EntreaSysModle = "";
		List servletList = new ArrayList();
		WeblogicNormal normal = new WeblogicNormal();
		
		try {
					try{					
							  String[] oids =                
								  new String[] {  
									//".1.3.6.1.4.1.140.625.380.1.1",   //servletRuntimeIndex
								    ".1.3.6.1.4.1.140.625.380.1.10" , //servletRuntimeType
									".1.3.6.1.4.1.140.625.380.1.15" ,  //servletRuntimeName
									//".1.3.6.1.4.1.140.625.380.1.20",    //servletRuntimeParent
									".1.3.6.1.4.1.140.625.380.1.25",   //servletRuntimeServletName
								    ".1.3.6.1.4.1.140.625.380.1.30" , //servletRuntimeReloadTotalCount
									".1.3.6.1.4.1.140.625.380.1.35" ,  //servletRuntimeInvocationTotalCount
									".1.3.6.1.4.1.140.625.380.1.40",    //servletRuntimePoolMaxCapacity
									".1.3.6.1.4.1.140.625.380.1.45",   //servletRuntimeExecutionTimeTotal
									//".1.3.6.1.4.1.140.625.380.1.5",   //servletRuntimeObjectName
								    ".1.3.6.1.4.1.140.625.380.1.50",  //servletRuntimeExecutionTimeHigh
								    ".1.3.6.1.4.1.140.625.380.1.55",   //servletRuntimeExecutionTimeLow
								    ".1.3.6.1.4.1.140.625.380.1.60" , //servletRuntimeExecutionTimeAverage
									//".1.3.6.1.4.1.140.625.380.1.65" ,  //servletRuntimeServletPath
									//".1.3.6.1.4.1.140.625.380.1.70",    //servletRuntimeContextPath
									".1.3.6.1.4.1.140.625.380.1.75"   //servletRuntimeURL
									//".1.3.6.1.4.1.140.625.380.1.76"   //servletRuntimeServletClassName
									  };
					this.setVariableBindings(oids);
					List list=this.table(this.getDefault_community(),nethost);					
					for(int i=0;i<list.size();i++){
						WeblogicServlet servlet = new WeblogicServlet();
						TableEvent tbevent=(TableEvent)list.get(i);						
						VariableBinding[] vb=tbevent.getColumns();						
						if (vb != null){
							EntreaSysModle = vb[0].toString().substring(vb[0].toString().indexOf("=")+1,vb[0].toString().length()).trim();				
							String vbString=vb[0].toString();
							for(int j=0;j<vb.length;j++){
								String sValue = "";
								if(vb[j]!=null){
									vbString=vb[j].toString();
									sValue=vbString.substring(vbString.lastIndexOf("=")+1,vbString.length()).trim();	
								}
								if(j==0){
									servlet.setServletRuntimeType(sValue);
								}else if (j==1){
									servlet.setServletRuntimeName(sValue);
								}else if (j==2){
									servlet.setServletRuntimeServletName(sValue);
								}else if (j==3){
									servlet.setServletRuntimeReloadTotalCount(sValue);
								}else if (j==4){
									servlet.setServletRuntimeInvocationTotalCount(sValue);
								}else if (j==5){
									servlet.setServletRuntimePoolMaxCapacity(sValue);
								}else if (j==6){
									servlet.setServletRuntimeExecutionTimeTotal(sValue);
								}else if (j==7){
									servlet.setServletRuntimeExecutionTimeHigh(sValue);
								}else if (j==8){
									servlet.setServletRuntimeExecutionTimeLow(sValue);
								}else if (j==9){
									servlet.setServletRuntimeExecutionTimeAverage(sValue);
								}else if (j==10){
									servlet.setServletRuntimeURL(sValue);
								}
							}
							servletList.add(servlet);
						}
					}
			 }
		     catch(Exception e){e.printStackTrace();}	  			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//returnHas.put("flag",flag);
		return servletList;
	}
	
	public  List collectLogData(){
		String EntreaSysModle = "";
		List normalList = new ArrayList();
		WeblogicNormal normal = new WeblogicNormal();
		
		try {
					try{					
							  String[] oids =                
								  new String[] {  
									".1.3.6.1.4.1.140.625.700.1.15",   //
								    ".1.3.6.1.4.1.140.625.700.1.25" , //
									".1.3.6.1.4.1.140.625.700.1.50" ,  //
									".1.3.6.1.4.1.140.625.700.1.54",    //
									".1.3.6.1.4.1.140.625.700.1.61"
									  };
					this.setVariableBindings(oids);
					List list=this.table(this.getDefault_community(),nethost);					
					for(int i=0;i<list.size();i++){
						  
						TableEvent tbevent=(TableEvent)list.get(i);						
						VariableBinding[] vb=tbevent.getColumns();						
				if (vb != null){
					EntreaSysModle = vb[0].toString().substring(vb[0].toString().indexOf("=")+1,vb[0].toString().length()).trim();				
					String vbString=vb[0].toString();

					for(int j=0;j<vb.length;j++){
						if(vb[j]!=null){
							vbString=vb[j].toString();
							String sValue=vbString.substring(vbString.lastIndexOf("=")+1,vbString.length()).trim();		
							SysLogger.info(j+"==========="+sValue);
							if(j==0){
								normal.setDomainName(sValue);
							}else if (j==1){
								normal.setDomainActive(sValue);
							}else if (j==2){
								normal.setDomainAdministrationPort(sValue);
							}else if (j==3){
								normal.setDomainConfigurationVersion(sValue);
							}							
						}																																																																																																				
					}
//					normalList.add(normal);
//					normal = new WeblogicNormal();
				}
			}
			 }
		     catch(Exception e){e.printStackTrace();}	  			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//returnHas.put("flag",flag);
		return normalList;
	}
	
	public  List collectWebLogData(){
		String EntreaSysModle = "";
		List normalList = new ArrayList();
		WeblogicNormal normal = new WeblogicNormal();
		
		try {
					try{					
							  String[] oids =                
								  new String[] {  
									".1.3.6.1.4.1.140.625.1160.1.18",
									".1.3.6.1.4.1.140.625.1160.1.13",
									".1.3.6.1.4.1.140.625.1160.1.14"
									  };
					this.setVariableBindings(oids);
					List list=this.table(this.getDefault_community(),nethost);					
					for(int i=0;i<list.size();i++){
						  
						TableEvent tbevent=(TableEvent)list.get(i);						
						VariableBinding[] vb=tbevent.getColumns();						
				if (vb != null){
					EntreaSysModle = vb[0].toString().substring(vb[0].toString().indexOf("=")+1,vb[0].toString().length()).trim();				
					String vbString=vb[0].toString();

					for(int j=0;j<vb.length;j++){
						if(vb[j]!=null){
							vbString=vb[j].toString();
							String sValue=vbString.substring(vbString.lastIndexOf("=")+1,vbString.length()).trim();		
							SysLogger.info(j+"==========="+sValue);
							if(j==0){
								normal.setDomainName(sValue);
							}else if (j==1){
								normal.setDomainActive(sValue);
							}else if (j==2){
								normal.setDomainAdministrationPort(sValue);
							}else if (j==3){
								normal.setDomainConfigurationVersion(sValue);
							}							
						}																																																																																																				
					}
//					normalList.add(normal);
//					normal = new WeblogicNormal();
				}
			}
			 }
		     catch(Exception e){e.printStackTrace();}	  			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//returnHas.put("flag",flag);
		return normalList;
	}
    
	//线程队列
	public  List collectQueueData(){
		
		String EntreaSysModle = "";
		List queueList = new ArrayList();
		WeblogicQueue queue = new WeblogicQueue();
		String[] oids =                
			  new String[] {
				".1.3.6.1.4.1.140.625.180.1.15",
				".1.3.6.1.4.1.140.625.180.1.25", 
			    ".1.3.6.1.4.1.140.625.180.1.30",
			    ".1.3.6.1.4.1.140.625.180.1.35",
			    ".1.3.6.1.4.1.140.625.180.1.40"
		};
				this.setVariableBindings(oids);
				try {
					List list = this.table(this.getDefault_community(),nethost);
				for(int i=0;i<list.size();i++){
					TableEvent tbevent=(TableEvent)list.get(i);
					//System.out.println("tbevent  ex   "+tbevent.getException());
					
					VariableBinding[] vb=tbevent.getColumns();
					
			if (vb != null){
				EntreaSysModle = vb[0].toString().substring(vb[0].toString().indexOf("=")+1,vb[0].toString().length()).trim();				
				String vbString=vb[0].toString();

				for(int j=0;j<vb.length;j++){
					if(vb[j]!=null){
						vbString=vb[j].toString();
						String sValue=vbString.substring(vbString.lastIndexOf("=")+1,vbString.length()).trim();								
						if(j==0){
							queue.setExecuteQueueRuntimeName(sValue);
						}else if (j==1){
							queue.setThreadPoolRuntimeExecuteThreadIdleCount(sValue);
						}else if (j==2){
							queue.setExecuteQueueRuntimePendingRequestOldestTime(sValue);
						}else if (j==3){
							queue.setExecuteQueueRuntimePendingRequestCurrentCount(sValue);
						}else if (j==4){
							queue.setExecuteQueueRuntimePendingRequestTotalCount(sValue);
						}
						
					}
				}
				queueList.add(queue);
				queue = new WeblogicQueue();
			}
				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		return queueList;
		
	}
    
	//Jdbc连接池
	public  List collectJdbcData(){
		String EntreaSysModle = "";
		Hashtable returnHas = new Hashtable();
		WeblogicJdbc jdbc = new WeblogicJdbc();
		List jdbcList = new ArrayList();
		String[] oids =                
			  new String[] {  
				".1.3.6.1.4.1.140.625.190.1.15",   //名称
			    ".1.3.6.1.4.1.140.625.190.1.25" , //当前连接数
			    ".1.3.6.1.4.1.140.625.190.1.35" ,   //驱动器版本
				".1.3.6.1.4.1.140.625.190.1.60" ,  //最大容量
				".1.3.6.1.4.1.140.625.190.1.61" ,   //平均连接数
				".1.3.6.1.4.1.140.625.190.1.66" ,  //最高可活动连接数
				".1.3.6.1.4.1.140.625.190.1.68" ,  //泄漏数
				".1.3.6.1.4.1.140.625.190.1.30" , //等待数
				".1.3.6.1.4.1.140.625.190.1.50" ,//最长等待时间
			
		};
				this.setVariableBindings(oids);
				
				try {
					List list = this.table(this.getDefault_community(),nethost);
				
				for(int i=0;i<list.size();i++){
					  
					TableEvent tbevent=(TableEvent)list.get(i);
					//System.out.println("tbevent  ex   "+tbevent.getException());
					VariableBinding[] vb=tbevent.getColumns();
					//System.out.println(vb[0].toString());
			if (vb != null){
				EntreaSysModle = vb[0].toString().substring(vb[0].toString().indexOf("=")+1,vb[0].toString().length()).trim();				
				String vbString=vb[0].toString();

				for(int j=0;j<vb.length;j++){
					if(vb[j]!=null){
						vbString=vb[j].toString();
						String sValue=vbString.substring(vbString.lastIndexOf("=")+1,vbString.length()).trim();								
						if(j==0){
							jdbc.setJdbcConnectionPoolName(sValue);
						}else if (j==1){
							jdbc.setJdbcConnectionPoolRuntimeActiveConnectionsCurrentCount(sValue);
						}else if (j==2){
							jdbc.setJdbcConnectionPoolRuntimeVersionJDBCDriver(sValue);
						}else if (j==3){
							jdbc.setJdbcConnectionPoolRuntimeMaxCapacity(sValue);
						}else if (j==4){
							jdbc.setJdbcConnectionPoolRuntimeActiveConnectionsAverageCount(sValue);
						}else if (j==5){
							jdbc.setJdbcConnectionPoolRuntimeHighestNumAvailable(sValue);
						}else if (j==6){
							jdbc.setJdbcLeaked(sValue);
						}else if (j==7){
							jdbc.setJdbcWaitCurrent(sValue);
						}else if (j==8){
							jdbc.setJdbcWaitMaxTime(sValue);
						}
						
					}
			}
			jdbcList.add(jdbc);
			jdbc = new WeblogicJdbc();
				
//System.out.println(jdbc.getJdbcConnectionPoolRuntimeVersionJDBCDriver());
				
			}
				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		return jdbcList;
		
	}
	
	//WebApp
	public  List collectWebAppData(){
		String EntreaSysModle = "";
		Hashtable returnHas = new Hashtable();
		List webappList = new ArrayList();
		WeblogicWeb web= new WeblogicWeb();
		String[] oids =                
			  new String[] {  
				".1.3.6.1.4.1.140.625.430.1.15",   //名称
			    ".1.3.6.1.4.1.140.625.430.1.30" , //状态
			    ".1.3.6.1.4.1.140.625.430.1.50" ,   //当前会话数
				".1.3.6.1.4.1.140.625.430.1.55" ,  //最大会话数
				".1.3.6.1.4.1.140.625.430.1.60"  //总会话数
		};
				this.setVariableBindings(oids);
				
				try {
					List list = this.table(this.getDefault_community(),nethost);
				
				for(int i=0;i<list.size();i++){
					  
					TableEvent tbevent=(TableEvent)list.get(i);
					//System.out.println("tbevent  ex   "+tbevent.getException());
					VariableBinding[] vb=tbevent.getColumns();
					//System.out.println(vb[0].toString());
			if (vb != null){
				EntreaSysModle = vb[0].toString().substring(vb[0].toString().indexOf("=")+1,vb[0].toString().length()).trim();				
				String vbString=vb[0].toString();

				for(int j=0;j<vb.length;j++){
					if(vb[j]!=null){
						vbString=vb[j].toString();
						String sValue=vbString.substring(vbString.lastIndexOf("=")+1,vbString.length()).trim();								
						if(j==0){
							web.setWebAppComponentRuntimeComponentName(sValue);
						}else if (j==1){
							web.setWebAppComponentRuntimeStatus(sValue);
						}else if (j==2){
							web.setWebAppComponentRuntimeOpenSessionsCurrentCount(sValue);
						}else if (j==3){
							web.setWebAppComponentRuntimeOpenSessionsHighCount(sValue);
						}else if (j==4){
							web.setWebAppComponentRuntimeSessionsOpenedTotalCount(sValue);
						}
						
					}
			}
				webappList.add(web);
				web= new WeblogicWeb();
				//System.out.println(web.getWebAppComponentRuntimeComponentName());
				
			}
				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		return webappList;
		
	}
	
	//jvm 堆 heap
	public  List collectHeapData(){
		String EntreaSysModle = "";
		List webappList = new ArrayList();
		WeblogicHeap heap= new WeblogicHeap();
		String[] oids =                
			  new String[] {  
				".1.3.6.1.4.1.140.625.340.1.15",   //名称
			    ".1.3.6.1.4.1.140.625.340.1.25" , //当前堆大小
			    ".1.3.6.1.4.1.140.625.340.1.30"   //堆总大小
		};
				this.setVariableBindings(oids);
				
				try {
					List list = this.table(this.getDefault_community(),nethost);
				
				for(int i=0;i<list.size();i++){
					  
					TableEvent tbevent=(TableEvent)list.get(i);
					//System.out.println("tbevent  ex   "+tbevent.getException());
					VariableBinding[] vb=tbevent.getColumns();
					//System.out.println(vb[0].toString());
			if (vb != null){
				EntreaSysModle = vb[0].toString().substring(vb[0].toString().indexOf("=")+1,vb[0].toString().length()).trim();				
				String vbString=vb[0].toString();

				for(int j=0;j<vb.length;j++){
					if(vb[j]!=null){
						vbString=vb[j].toString();
						String sValue=vbString.substring(vbString.lastIndexOf("=")+1,vbString.length()).trim();								
						if(j==0){
							heap.setJvmRuntimeName(sValue);
						}else if (j==1){
							heap.setJvmRuntimeHeapFreeCurrent(sValue);
						}else if (j==2){
							heap.setJvmRuntimeHeapSizeCurrent(sValue);
						}
						
					}
				}
				webappList.add(heap);
				heap= new WeblogicHeap();
			
				//System.out.println(heap.getJvmRuntimeName());
				
			}
				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		return webappList;
		
	}
	
	//Server服务
	public  List collectServerData(){
		String EntreaSysModle = "";
		List serverList = new ArrayList();
		WeblogicServer server= new WeblogicServer();
		String[] oids =                
			  new String[] {  
				".1.3.6.1.4.1.140.625.360.1.15",	//服务名
				".1.3.6.1.4.1.140.625.360.1.30",	//服务监听地址
				".1.3.6.1.4.1.140.625.360.1.35", 	//服务监听端口
				".1.3.6.1.4.1.140.625.360.1.50", 	//当前Socket数
				".1.3.6.1.4.1.140.625.360.1.60" 	//服务器当前运行状态
		};
				this.setVariableBindings(oids);
				
				try {
					List list = this.table(this.getDefault_community(),nethost);
				
				for(int i=0;i<list.size();i++){
					  
					TableEvent tbevent=(TableEvent)list.get(i);
					//System.out.println("tbevent  ex   "+tbevent.getException());
					VariableBinding[] vb=tbevent.getColumns();
					//System.out.println(vb[0].toString());
			if (vb != null){
				EntreaSysModle = vb[0].toString().substring(vb[0].toString().indexOf("=")+1,vb[0].toString().length()).trim();				
				String vbString=vb[0].toString();

				for(int j=0;j<vb.length;j++){
					if(vb[j]!=null){
						vbString=vb[j].toString();
						String sValue=vbString.substring(vbString.lastIndexOf("=")+1,vbString.length()).trim();								
						if(j==0){
							server.setServerRuntimeName(sValue);
						}else if (j==1){
							server.setServerRuntimeListenAddress(sValue);
						}else if (j==2){
							server.setServerRuntimeListenPort(sValue);
						}else if (j==3){
							server.setServerRuntimeOpenSocketsCurrentCount(sValue);
						}else if (j==4){
							server.setServerRuntimeState(sValue);
						}
					}
				}
				serverList.add(server);
				server= new WeblogicServer();
				
			}
				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		return serverList;
		
	}
	
	
	
	public int getInterval(float d,String t)
			   {
				int interval=0;
				  if(t.equals("d"))
					 interval =(int) d*24*60*60; //天数
				  else if(t.equals("h"))
					 interval =(int) d*60*60;    //小时
				  else if(t.equals("m"))
					 interval = (int)d*60;       //分钟
				else if(t.equals("s"))
							 interval =(int) d;       //秒
				return interval;
			   }
		  
//   public static void main(String [] args){
//	   WeblogicSnmp dd = new WeblogicSnmp("172.16.2.10","public",new Integer(163));
//	   dd.collectServerData();
//   }
}




