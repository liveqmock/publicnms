package com.afunms.polling.snmp.dhcp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.afunms.application.dao.DHCPConfigDao;
import com.afunms.application.model.DHCPConfig;
import com.afunms.application.weblogicmonitor.AbstractSnmp;
import com.afunms.application.weblogicmonitor.WeblogicTrans;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.SysLogger;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DhcpScopeSnmp extends AbstractSnmp {
	private  String nethost="1.1.1.1";
	private java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * @param community
	 * @param port
	 * @param timeout
	 */
	public DhcpScopeSnmp(String host,String community,Integer port) {
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
		List dhcpscopeValue = collectDHCPScopeData();
		List dhcpparValue = collectTransData();
		rValue.put("dhcpscope", dhcpscopeValue);
		rValue.put("dhcppar", dhcpparValue);
		return rValue;		
	}
	
	public  Hashtable collectData(Hashtable gatherhash,DHCPConfig dhcpconf){
		Hashtable rValue = new Hashtable();
		List dhcpscopeValue = new ArrayList();
		Vector dhcppingvector= new Vector();
		if(gatherhash.containsKey("dhcpscope")){
			//采集域
			try{
				dhcpscopeValue = collectDHCPScopeData();
			}catch(Exception e){
				
			}
		}
		
		if(gatherhash.containsKey("ping")){
			//连通率
			PingUtil pingU=new PingUtil(nethost);		
			try {
				Integer[] packet=pingU.ping();
				dhcppingvector = pingU.addhis(packet); 
				if(dhcppingvector!=null){
					DHCPConfigDao dhcpconfigdao = new DHCPConfigDao();
					try{
						dhcpconfigdao.createHostData(dhcppingvector,dhcpconf);
					}catch(Exception e){
						
					}finally{
						dhcpconfigdao.close();
					}
				}
				//vector=null; 			
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			try{
				dhcpscopeValue = collectDHCPScopeData();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

//		List queueValue = new ArrayList();
//		if(gatherhash.containsKey("dhcppar")){
//			//队列
//			queueValue = collectTransData();
//		}		
		
		rValue.put("dhcpscopeValue", dhcpscopeValue);
		//rValue.put("queueValue", queueValue);
		rValue.put("dhcpping", dhcppingvector);
		return rValue;		
	}
	public  List collectDHCPScopeData(){
		String EntreaSysModle = "";
		List dhcpscopeList = new ArrayList();
		//WeblogicNormal normal = new WeblogicNormal();
		Hashtable scope = new Hashtable();
		
		try {
					try{					
							  String[] oids =                
								  new String[] {  
									".1.3.6.1.4.1.311.1.3.2.1.1.1",   //子网地址
								    ".1.3.6.1.4.1.311.1.3.2.1.1.2" , //in use
									".1.3.6.1.4.1.311.1.3.2.1.1.3" ,  //in free
									".1.3.6.1.4.1.311.1.3.2.1.1.4"    //PendingOffers
									
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
								scope.put("netadd", sValue);
								SysLogger.info("==========netadd:"+sValue);
							}else if (j==1){
								scope.put("inuse", sValue);
								SysLogger.info("==========inuse:"+sValue);
							}else if (j==2){
								scope.put("free", sValue);
								SysLogger.info("==========free:"+sValue);
							}else if (j==3){
								scope.put("pendingoffers", sValue);
								SysLogger.info("==========pendingoffers:"+sValue);
							}							
						}																																																																																																				
					}
					dhcpscopeList.add(scope);
					scope = new Hashtable();
				}
			}
			 }
		     catch(Exception e){e.printStackTrace();}	  			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//returnHas.put("flag",flag);
		return dhcpscopeList;
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




