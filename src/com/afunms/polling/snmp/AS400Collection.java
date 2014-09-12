package com.afunms.polling.snmp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.snmp.ping.PingSnmp;
import com.afunms.polling.telnet.AS400Telnet;
import com.afunms.polling.telnet.StringUtilForAS400;
import com.afunms.polling.telnet.TelnetWrapperForAS400;
import com.afunms.topology.model.JobForAS400;
import com.afunms.topology.model.SubsystemForAS400;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.JobList;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.RequestNotSupportedException;
import com.ibm.as400.access.Subsystem;
import com.ibm.as400.access.SystemPool;
import com.ibm.as400.access.SystemStatus;

public class AS400Collection {
	
	private String ip;
	
	private String username;
	
	private String password;
	
	private AS400 as400;
	
	private String loginPrompt;
	
	private String passwordPrompt;
	
	private String shellPrompt;
	
	private SystemStatus systemStatus;
	
	
	/**
	 * 
	 */
	public AS400Collection() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param ip
	 * @param username
	 * @param password
	 */
	public AS400Collection(String ip, String username, String password,String loginPrompt, String passwordPrompt, String shellPrompt) {
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.loginPrompt = loginPrompt;
		this.passwordPrompt = passwordPrompt;
		this.shellPrompt = shellPrompt;
		try {
			this.as400 = new AS400(this.ip , this.username , this.password);
		} catch (Exception e) {
			e.printStackTrace();  
		}
		try {
			this.systemStatus = new SystemStatus(as400);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(this.ip+"====="+as400.isConnected()+"==========as400.isConnected()==============");
	}
	
	

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	/**
	 * @return the loginPrompt
	 */
	public String getLoginPrompt() {
		return loginPrompt;
	}






	/**
	 * @param loginPrompt the loginPrompt to set
	 */
	public void setLoginPrompt(String loginPrompt) {
		this.loginPrompt = loginPrompt;
	}






	/**
	 * @return the passwordPrompt
	 */
	public String getPasswordPrompt() {
		return passwordPrompt;
	}






	/**
	 * @param passwordPrompt the passwordPrompt to set
	 */
	public void setPasswordPrompt(String passwordPrompt) {
		this.passwordPrompt = passwordPrompt;
	}






	/**
	 * @return the shellPrompt
	 */
	public String getShellPrompt() {
		return shellPrompt;
	}






	/**
	 * @param shellPrompt the shellPrompt to set
	 */
	public void setShellPrompt(String shellPrompt) {
		this.shellPrompt = shellPrompt;
	}

	/**
	 * @return the as400
	 */
	public AS400 getAs400() {
		return as400;
	}

	/**
	 * @param as400 the as400 to set
	 */
	public void setAs400(AS400 as400) {
		this.as400 = as400;
	}
	
	public boolean init(){
		this.as400 = new AS400(ip , username , password);
		this.systemStatus = new SystemStatus(as400);
		return true;
	}

	public boolean init(String ip, String username, String password,
			String loginPrompt, String passwordPrompt, String shellPrompt){
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.loginPrompt = loginPrompt;
		this.passwordPrompt = passwordPrompt;
		this.shellPrompt = shellPrompt;
		this.as400 = new AS400(this.ip , this.username , this.password);
		this.systemStatus = new SystemStatus(as400);
		return true;
	}
	
	public Hashtable execute(List monitorItemList){
		System.out.println("开始采集AS400 服务器");
		Hashtable hashtable = new Hashtable();
		Hashtable gatherhash = new Hashtable();
		if(monitorItemList != null && monitorItemList.size()>0){
			for(int i=0;i<monitorItemList.size();i++){
				NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
				gatherhash.put(nodeGatherIndicators.getName(), nodeGatherIndicators.getName());
			}
		}
		if(gatherhash == null)return null;
		try {
			if(gatherhash.containsKey("pool")){
				List systemPoolList = getSystemPool();
				SysLogger.info("============================开始采集AS400 服务器========pool====="+this.ip);
				SysLogger.info(systemPoolList.size()+"========systemPoolList==========="+this.ip);
				hashtable.put("SystemPool", systemPoolList);
			}
			if(gatherhash.containsKey("subsystem")){
				try {
					System.out.println("============================开始采集AS400 服务器========用户信息=====");
					List subSystem = getSubSystemList();
					hashtable.put("subSystem", subSystem);
					System.out.println(subSystem.size()+"========subSystem===========");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if(gatherhash.containsKey("job")){
				List jobList = getJobStatus();
				hashtable.put("Jobs", jobList);
				System.out.println(jobList.size()+"========jobList===========");
//				List jobForAS400EventList = new ArrayList();
//		        //if(category == 4){
//		    		
//		    		/*
//		    		 * nielin add 2010-08-18
//		    		 *
//		    		 * 创建作业组告警
//		    		 * 
//		    		 * start ===============================
//		    		 */
//		    		try{
//		    			if(jobList != null && jobList.size()>0){
//		    				CheckEventUtil checkutil = new CheckEventUtil();
//		    				jobForAS400EventList = checkutil.createJobForAS400GroupEventList(ip , jobList);
//		    			}
//		    		}catch(Exception e){
//		    			//e.printStackTrace();
//		    		}
//		    		/*
//		    		 * nielin add 2010-08-18
//		    		 *
//		    		 * 创建作业组告警
//		    		 * 
//		    		 * end ===============================
//		    		 */
//		        //}
//		        if(jobForAS400EventList != null && jobForAS400EventList.size()>0){
//		        	alarm = true;
//		        	//this.alarmlevel=3;
//		        }
//				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if(gatherhash.containsKey("ping")){
    			PingSnmp pingsnmp = null;
    			try{
    				pingsnmp = (PingSnmp)Class.forName("com.afunms.polling.snmp.ping.PingSnmp").newInstance();
        			pingsnmp.collect_Data((NodeGatherIndicators)gatherhash.get("ping"));
        			//在采集过程中已经存入数据库
        			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"utilhdx");
        		}catch(Exception e){
        			//e.printStackTrace();
        		}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Hashtable hashtable2 = null;
		try {
			hashtable2 = getDataByTelnet(gatherhash);
			if(hashtable2 == null){
				hashtable2 = new Hashtable();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(hashtable2.size()+"========hashtable2===========");
		if(gatherhash.containsKey("disk")){			
			try {
				Vector diskVector = (Vector)hashtable2.get("AS400disk");
				if(diskVector == null){
					diskVector = new Vector();
				}
				Hashtable systemStatushashtable = getSystemStatus();
				Vector alldiskVector = new Vector();
				Diskcollectdata diskdata = null;
				Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ip);
				if(ipAllData == null)ipAllData = new Hashtable();
				Calendar date=Calendar.getInstance();
				if(systemStatushashtable.get("SystemASP")!=null&&systemStatushashtable.get("PercentSystemASPUsed")!=null){
					diskdata=new Diskcollectdata();
					diskdata.setIpaddress(ip);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("Utilization");	
					diskdata.setRestype("static");
					diskdata.setUnit("%");
					diskdata.setSubentity("ASP");
					diskdata.setThevalue((String)systemStatushashtable.get("PercentSystemASPUsed"));
					alldiskVector.addElement(diskdata);
					
					diskdata=new Diskcollectdata();
					diskdata.setIpaddress(ip);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("AllSize");	
					diskdata.setRestype("static");
					diskdata.setUnit("G");
					diskdata.setSubentity("ASP");
					diskdata.setThevalue(Float.parseFloat((String)systemStatushashtable.get("SystemASP"))/1024+"");
					alldiskVector.addElement(diskdata);
					
					diskdata=new Diskcollectdata();
					diskdata.setIpaddress(ip);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("UsedSize");	
					diskdata.setRestype("static");
					diskdata.setUnit("G");
					diskdata.setSubentity("ASP");
					float used = (Float.parseFloat((String)systemStatushashtable.get("SystemASP"))/1024)*(Float.parseFloat((String)systemStatushashtable.get("PercentSystemASPUsed"))/100);				
					diskdata.setThevalue(used+"");
					alldiskVector.addElement(diskdata);
					
					String desc = "ASP";
					float value=0.0f;
					
					try {
						value = Float.parseFloat((String)systemStatushashtable.get("PercentSystemASPUsed"));
						String diskinc = "0.0";
						float pastutil = 0.0f;
						Vector disk_v = (Vector)ipAllData.get("disk");
						if (disk_v != null && disk_v.size() > 0) {
							for (int si = 0; si < disk_v.size(); si++) {
								Diskcollectdata disk_data = (Diskcollectdata) disk_v.elementAt(si);
								if((desc).equals(disk_data.getSubentity())&&"Utilization".equals(disk_data.getEntity())){
									pastutil = Float.parseFloat(disk_data.getThevalue());
								}
							}
						} else {
							pastutil = value;
						}
						if (pastutil == 0) {
							pastutil = value;
						}
						if(value-pastutil>0){
							diskinc = (value-pastutil)+"";
						}
						//System.out.println("diskinc------------------"+diskinc);
						diskdata = new Diskcollectdata();
						diskdata.setIpaddress(ip);
						diskdata.setCollecttime(date);
						diskdata.setCategory("Disk");
						diskdata.setEntity("UtilizationInc");// 利用增长率百分比
						diskdata.setSubentity(desc);
						diskdata.setRestype("dynamic");
						diskdata.setUnit("%");
						diskdata.setThevalue(diskinc);
						alldiskVector.addElement(diskdata);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				hashtable.put("AS400disk", diskVector);
				hashtable.put("disk", alldiskVector);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(gatherhash.containsKey("network")){
			Vector networkVector = (Vector)hashtable2.get("network");
			if(networkVector == null){
				networkVector = new Vector();
			}
			hashtable.put("network", networkVector);
		}
		if(gatherhash.containsKey("hardware")){
			Vector hardwareVector = (Vector)hashtable2.get("hardware");
			if(hardwareVector == null){
				hardwareVector = new Vector();
			}
			hashtable.put("hardware", hardwareVector);
		}
		if(gatherhash.containsKey("service")){
			Vector serviceVector = (Vector)hashtable2.get("service");
			if(serviceVector == null){
				serviceVector = new Vector();
			}
			hashtable.put("service", serviceVector);
		}
		if(gatherhash.containsKey("system")){
			try {  
				Hashtable systemStatushashtable = getSystemStatus();
				
				Hashtable systemStatushashtable2 = (Hashtable)hashtable2.get("SystemStatus");
				if(systemStatushashtable2 == null){
					systemStatushashtable2 = new Hashtable();
				}
				systemStatushashtable.putAll(systemStatushashtable2);
				systemStatushashtable.put("cpu", systemStatushashtable2.get("cpu"));
				Vector cpuVector = new Vector();
				Calendar date=Calendar.getInstance();
				CPUcollectdata cpudata=new CPUcollectdata();
				cpudata.setIpaddress(ip);
				cpudata.setCollecttime(date);
				cpudata.setCategory("CPU");
				cpudata.setEntity("Utilization");
				cpudata.setSubentity("Utilization");
				cpudata.setRestype("dynamic");
				cpudata.setUnit("%");
				cpudata.setThevalue((String)systemStatushashtable.get("cpu"));
				
				cpuVector.addElement(cpudata);
				hashtable.put("cpu", cpuVector);
				hashtable.put("SystemStatus", systemStatushashtable);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		SysLogger.info("as400采集结束"+this.ip);
		SysLogger.info("%%%%%%%%%hashtable.size()%%%%%%%%%"+hashtable.size()+this.ip);
		return hashtable;
	}
	
	public Hashtable execute(Hashtable gatherhash){
		Hashtable hashtable = new Hashtable();
		Hashtable returnHash = new Hashtable();
		if(gatherhash == null)return null;
		try {
			if(gatherhash.containsKey("pool")){
				List systemPoolList = getSystemPool();
				hashtable.put("SystemPool", systemPoolList);
			}
			if(gatherhash.containsKey("subsystem")){
				try {
					//System.out.println("============================开始采集AS400 服务器========用户信息=====");
					List subSystem = getSubSystemList();
					hashtable.put("subSystem", subSystem);
					//System.out.println(subSystem.size()+"========subSystem===========");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if(gatherhash.containsKey("job")){
				List jobList = getJobStatus();
				hashtable.put("Jobs", jobList);
				System.out.println(jobList.size()+"========jobList===========");
//				List jobForAS400EventList = new ArrayList();
//		        //if(category == 4){
//		    		
//		    		/*
//		    		 * nielin add 2010-08-18
//		    		 *
//		    		 * 创建作业组告警
//		    		 * 
//		    		 * start ===============================
//		    		 */
//		    		try{
//		    			if(jobList != null && jobList.size()>0){
//		    				CheckEventUtil checkutil = new CheckEventUtil();
//		    				jobForAS400EventList = checkutil.createJobForAS400GroupEventList(ip , jobList);
//		    			}
//		    		}catch(Exception e){
//		    			//e.printStackTrace();
//		    		}
//		    		/*
//		    		 * nielin add 2010-08-18
//		    		 *
//		    		 * 创建作业组告警
//		    		 * 
//		    		 * end ===============================
//		    		 */
//		        //}
//		        if(jobForAS400EventList != null && jobForAS400EventList.size()>0){
//		        	alarm = true;
//		        	//this.alarmlevel=3;
//		        }
//				
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		try {
			if(gatherhash.containsKey("ping")){
    			PingSnmp pingsnmp = null;
    			try{
    				pingsnmp = (PingSnmp)Class.forName("com.afunms.polling.snmp.ping.PingSnmp").newInstance();
        			returnHash = pingsnmp.collect_Data((NodeGatherIndicators)gatherhash.get("ping"));
        			//在采集过程中已经存入数据库
        			//hostdataManager.createHostItemData(node.getIpAddress(),returnHash,alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),"utilhdx");
        		}catch(Exception e){
        			//e.printStackTrace();
        		}
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Hashtable hashtable2 = null;
		try {
			hashtable2 = getDataByTelnet(gatherhash);
			if(hashtable2 == null){
				hashtable2 = new Hashtable();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		if(gatherhash.containsKey("disk")){			
			try {
				Vector diskVector = (Vector)hashtable2.get("AS400disk");
				if(diskVector == null){
					diskVector = new Vector();
				}
				Vector alldiskVector = new Vector();
				Diskcollectdata diskdata = null;
				Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ip);
				if(ipAllData == null)ipAllData = new Hashtable();
				Calendar date=Calendar.getInstance();
				for(int k=0;k<diskVector.size();k++){
					Hashtable valuehashtable = (Hashtable)diskVector.get(k);
					diskdata=new Diskcollectdata();
					diskdata.setIpaddress(ip);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("Utilization");	
					diskdata.setRestype("static");
					diskdata.setUnit("%");
					diskdata.setSubentity((String)valuehashtable.get("Unit"));
					diskdata.setThevalue((String)valuehashtable.get("%Used"));
					alldiskVector.addElement(diskdata);
					
					diskdata=new Diskcollectdata();
					diskdata.setIpaddress(ip);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("Utilization");	
					diskdata.setRestype("static");
					diskdata.setUnit("%");
					diskdata.setSubentity((String)valuehashtable.get("Unit"));
					diskdata.setThevalue((String)valuehashtable.get("%Used"));
					alldiskVector.addElement(diskdata);
					
					diskdata=new Diskcollectdata();
					diskdata.setIpaddress(ip);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("AllSize");	
					diskdata.setRestype("static");
					diskdata.setUnit("M");
					diskdata.setSubentity((String)valuehashtable.get("Unit"));
					diskdata.setThevalue((String)valuehashtable.get("Size(M)"));
					alldiskVector.addElement(diskdata);
					
					diskdata=new Diskcollectdata();
					diskdata.setIpaddress(ip);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("UsedSize");	
					diskdata.setRestype("static");
					diskdata.setUnit("M");
					diskdata.setSubentity((String)valuehashtable.get("Unit"));
					int used = Integer.parseInt((String)valuehashtable.get("Size(M)"))*Integer.parseInt((String)valuehashtable.get("%Used"))/100;				
					diskdata.setThevalue(used+"");
					alldiskVector.addElement(diskdata);
					
					String desc = (String)valuehashtable.get("Unit");
					float value=0.0f;
					
					try {
						value = Float.parseFloat((String)valuehashtable.get("%Used"));
						String diskinc = "0.0";
						float pastutil = 0.0f;
						Vector disk_v = (Vector)ipAllData.get("disk");
						if (disk_v != null && disk_v.size() > 0) {
							for (int si = 0; si < disk_v.size(); si++) {
								Diskcollectdata disk_data = (Diskcollectdata) disk_v.elementAt(si);
								if((desc).equals(disk_data.getSubentity())&&"Utilization".equals(disk_data.getEntity())){
									pastutil = Float.parseFloat(disk_data.getThevalue());
								}
							}
						} else {
							pastutil = value;
						}
						if (pastutil == 0) {
							pastutil = value;
						}
						if(value-pastutil>0){
							diskinc = (value-pastutil)+"";
						}
						//System.out.println("diskinc------------------"+diskinc);
						diskdata = new Diskcollectdata();
						diskdata.setIpaddress(ip);
						diskdata.setCollecttime(date);
						diskdata.setCategory("Disk");
						diskdata.setEntity("UtilizationInc");// 利用增长率百分比
						diskdata.setSubentity(desc);
						diskdata.setRestype("dynamic");
						diskdata.setUnit("%");
						diskdata.setThevalue(diskinc);
						alldiskVector.addElement(diskdata);
					} catch (Exception e) {
						e.printStackTrace();
					}
									
					diskVector.add(valuehashtable);	
				}
				
				//测试数据开始,正式环境需要注释以下语句
				for(int i=0;i<6;i++){
					Hashtable valuehashtable = new Hashtable();
					valuehashtable.put("Unit", i+"");
					valuehashtable.put("Type", "Type");

					valuehashtable.put("Size(M)",Math.round(Math.random()*100)+"");

					valuehashtable.put("%Used", Math.round(Math.random()*100)+"");

					valuehashtable.put("I/O Rqs", "100");

					valuehashtable.put("Request Size(K)", "100");

					valuehashtable.put("Read Rqs",  "100");

					valuehashtable.put("Write Rqs",  "100");

					valuehashtable.put("Read(K)",  "100");

					valuehashtable.put("Write(K)",  "100");

					valuehashtable.put("%Busy",  Math.round(Math.random()*100)+"");
					
					diskdata=new Diskcollectdata();
					diskdata.setIpaddress(ip);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("Utilization");	
					diskdata.setRestype("static");
					diskdata.setUnit("%");
					diskdata.setSubentity((String)valuehashtable.get("Unit"));
					diskdata.setThevalue((String)valuehashtable.get("%Used"));
					alldiskVector.addElement(diskdata);
					
					diskdata=new Diskcollectdata();
					diskdata.setIpaddress(ip);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("Utilization");	
					diskdata.setRestype("static");
					diskdata.setUnit("%");
					diskdata.setSubentity((String)valuehashtable.get("Unit"));
					diskdata.setThevalue((String)valuehashtable.get("%Used"));
					alldiskVector.addElement(diskdata);
					
					diskdata=new Diskcollectdata();
					diskdata.setIpaddress(ip);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("AllSize");	
					diskdata.setRestype("static");
					diskdata.setUnit("M");
					diskdata.setSubentity((String)valuehashtable.get("Unit"));
					diskdata.setThevalue((String)valuehashtable.get("Size(M)"));
					alldiskVector.addElement(diskdata);
					
					diskdata=new Diskcollectdata();
					diskdata.setIpaddress(ip);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("UsedSize");	
					diskdata.setRestype("static");
					diskdata.setUnit("M");
					diskdata.setSubentity((String)valuehashtable.get("Unit"));
					int used = Integer.parseInt((String)valuehashtable.get("Size(M)"))*Integer.parseInt((String)valuehashtable.get("%Used"))/100;				
					diskdata.setThevalue(used+"");
					alldiskVector.addElement(diskdata);
					
					String desc = (String)valuehashtable.get("Unit");
					float value=0.0f;
					
					try {
						value = Float.parseFloat((String)valuehashtable.get("%Used"));
						String diskinc = "0.0";
						float pastutil = 0.0f;
						Vector disk_v = (Vector)ipAllData.get("disk");
						if (disk_v != null && disk_v.size() > 0) {
							for (int si = 0; si < disk_v.size(); si++) {
								Diskcollectdata disk_data = (Diskcollectdata) disk_v.elementAt(si);
								if((desc).equals(disk_data.getSubentity())&&"Utilization".equals(disk_data.getEntity())){
									pastutil = Float.parseFloat(disk_data.getThevalue());
								}
							}
						} else {
							pastutil = value;
						}
						if (pastutil == 0) {
							pastutil = value;
						}
						if(value-pastutil>0){
							diskinc = (value-pastutil)+"";
						}
						//System.out.println("diskinc------------------"+diskinc);
						diskdata = new Diskcollectdata();
						diskdata.setIpaddress(ip);
						diskdata.setCollecttime(date);
						diskdata.setCategory("Disk");
						diskdata.setEntity("UtilizationInc");// 利用增长率百分比
						diskdata.setSubentity(desc);
						diskdata.setRestype("dynamic");
						diskdata.setUnit("%");
						diskdata.setThevalue(diskinc);
						alldiskVector.addElement(diskdata);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
					diskVector.add(valuehashtable);
				}
				//测试数据结束

				hashtable.put("AS400disk", diskVector);
				hashtable.put("disk", alldiskVector);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(gatherhash.containsKey("system")){
			try {
				Hashtable systemStatushashtable = getSystemStatus();
				
				Hashtable systemStatushashtable2 = (Hashtable)hashtable2.get("SystemStatus");
				if(systemStatushashtable2 == null){
					systemStatushashtable2 = new Hashtable();
				}
				systemStatushashtable.putAll(systemStatushashtable2);
				systemStatushashtable.put("cpu", "95");
				Vector cpuVector = new Vector();
				Calendar date=Calendar.getInstance();
				CPUcollectdata cpudata=new CPUcollectdata();
				cpudata.setIpaddress(ip);
				cpudata.setCollecttime(date);
				cpudata.setCategory("CPU");
				cpudata.setEntity("Utilization");
				cpudata.setSubentity("Utilization");
				cpudata.setRestype("dynamic");
				cpudata.setUnit("%");
				cpudata.setThevalue((String)systemStatushashtable.get("cpu"));
				
				cpuVector.addElement(cpudata);
				hashtable.put("cpu", cpuVector);
				hashtable.put("SystemStatus", systemStatushashtable);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return hashtable;
	}

	public Hashtable getSystemStatus(){
		Hashtable hashtable = new Hashtable();
		
		try {
			hashtable.put("JobsInSystem", String.valueOf(systemStatus.getJobsInSystem()));
		} catch (AS400SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ErrorCompletingRequestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ObjectDoesNotExistException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			hashtable.put("SystemASP", String.valueOf(systemStatus.getSystemASP()+""));
		} catch (AS400SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ErrorCompletingRequestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ObjectDoesNotExistException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			hashtable.put("PercentSystemASPUsed", String.valueOf(systemStatus.getPercentSystemASPUsed()));
		} catch (AS400SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ErrorCompletingRequestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ObjectDoesNotExistException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			hashtable.put("CurrentUnprotectedStorageUsed", String.valueOf(systemStatus.getCurrentUnprotectedStorageUsed()));
		} catch (AS400SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ErrorCompletingRequestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ObjectDoesNotExistException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			hashtable.put("MaximumUnprotectedStorageUsed", String.valueOf(systemStatus.getMaximumUnprotectedStorageUsed()));
		} catch (AS400SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ErrorCompletingRequestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {  
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ObjectDoesNotExistException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			hashtable.put("PercentPermanentAddresses", String.valueOf(systemStatus.getPercentPermanentAddresses()));
		} catch (AS400SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ErrorCompletingRequestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ObjectDoesNotExistException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			hashtable.put("PercentTemporaryAddresses", String.valueOf(systemStatus.getPercentTemporaryAddresses()));
		} catch (AS400SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorCompletingRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectDoesNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return hashtable;
		
	}
	
	public List getSystemPool(){
		List list = new ArrayList();
		try {
			Enumeration enumeration = systemStatus.getSystemPools();
			while(enumeration.hasMoreElements()){
				try {
					Hashtable hashtable = new Hashtable();
					SystemPool systemPool = (SystemPool)enumeration.nextElement();
					int id = systemPool.getIdentifier();
					String name = systemPool.getName();
					int size = systemPool.getSize();
					int reservedSize = systemPool.getReservedSize();
					int maximumActiveThreads = systemPool.getMaximumActiveThreads();
					
					hashtable.put("id", String.valueOf(id));
					hashtable.put("name", name);
					hashtable.put("size", String.valueOf(size));
					hashtable.put("reservedSize", String.valueOf(reservedSize));
					hashtable.put("maximumActiveThreads", String.valueOf(maximumActiveThreads));
					list.add(hashtable);
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
		} catch (AS400SecurityException e) {
			e.printStackTrace();
		} catch (ErrorCompletingRequestException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ObjectDoesNotExistException e) {
			e.printStackTrace();
		}
		
		
		return list;
	}
	
	public List getSubSystemList(){
		List list = new ArrayList();
		
		try {
			Subsystem[] Subsystemlist = Subsystem.listAllSubsystems(as400);
			if(Subsystemlist != null){
				for(int i = 0 ; i < Subsystemlist.length ; i++){
					Subsystem subsystem = Subsystemlist[i];
					SubsystemForAS400 subsystemForAS400 = new SubsystemForAS400();
					try {
						subsystemForAS400.setName(subsystem.getName());
						subsystemForAS400.setPath(subsystem.getPath());
						subsystemForAS400.setCurrentActiveJobs(subsystem.getCurrentActiveJobs()+"");
						//subsystemForAS400.setObjectDescription(subsystem.getObjectDescription());
//						System.out.println(subsystem.getObjectDescription().getName());
//						System.out.println(subsystem.getObjectDescription().getType());
//						System.out.println(subsystem.getObjectDescription().getStatus());
						if(subsystem.exists()){
							subsystemForAS400.setExists("1");
						}else{
							subsystemForAS400.setExists("0");
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					list.add(subsystemForAS400);
				}
			}
		} catch (AS400Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AS400SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorCompletingRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectDoesNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RequestNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return list;
	}
	
	public List getJobStatus(){
		List list = new ArrayList();
		
		
		JobList jobList = new JobList(as400); 
		
		
		try {
			Enumeration enumeration = jobList.getJobs();
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String time = simpleDateFormat.format(new Date());
			
			while(enumeration.hasMoreElements()){
				JobForAS400 jobForAS400 = new JobForAS400();
				try {
					Job job= (Job)enumeration.nextElement();
					if(!Job.JOB_STATUS_ACTIVE.equals(job.getStatus())){
						continue;
					}
					jobForAS400.setSubsystem(job.getSubsystem().trim());
					jobForAS400.setStatus(job.getStatus().trim());
					jobForAS400.setName(job.getName().trim());
					jobForAS400.setUser(job.getUser().trim());
					jobForAS400.setType(job.getType().trim());
					jobForAS400.setSubtype(job.getSubtype().trim());
					jobForAS400.setCPUUsedTime(job.getCPUUsed()+"");
					jobForAS400.setActiveStatus(((String)job.getValue(Job.ACTIVE_JOB_STATUS)).trim());
					jobForAS400.setCollectTime(time);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				list.add(jobForAS400);
				
			}
			
//		} catch (AS400SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ErrorCompletingRequestException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ObjectDoesNotExistException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		
		return list;
	}
	
	
	public Hashtable getDataByTelnet(Hashtable gatherhash){
		Hashtable hashtable = null;
		try {
			AS400Telnet telnet = new AS400Telnet(ip, username, password , loginPrompt , passwordPrompt , shellPrompt);
			telnet.connect();
			System.out.println("telnet.connect()===="+telnet.connect());
			System.out.println("telnet.login()===="+telnet.login());
			telnet.login();
			hashtable = telnet.execute(gatherhash);
		} catch (Exception e) {  
			e.printStackTrace();
		}
		return hashtable;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		 AS400 as400= new AS400("iSeriesD.DFW.IBM.COM" , "WUSER" , "DEMO2PWD");
//		AS400Telnet telnet = new AS400Telnet("192.168.9.40" , "B4WP" , "B4WP" , "2007." , "2007." , "===>");
		TelnetWrapperForAS400 telnetWrapperForAS400 = new TelnetWrapperForAS400();
		String user = "b4wp";
		String pwd = "b4wp";
		String ip = "192.168.9.40";
		try {
			telnetWrapperForAS400.connect(ip, 23);
			telnetWrapperForAS400.login(user , pwd , "2007." , "2007." , "===>");
			telnetWrapperForAS400.setPrompt("===>");
			telnetWrapperForAS400.send("");
			String result3 = new String(telnetWrapperForAS400.write("wrkdsksts"));
			String result4 = telnetWrapperForAS400.send(telnetWrapperForAS400.write((char)12));
			System.out.println(StringUtilForAS400.filterCode(result3));
			System.out.println(StringUtilForAS400.filterCode(result4));
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
