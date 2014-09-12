package com.afunms.polling.snmp;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.application.model.WeblogicConfig;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.config.model.Procs;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.om.Usercollectdata;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LoadAixFile_bak {
	/**
	 * @param hostname
	 */
	 private String ipaddress;
	 private ProcsDao procsManager=new ProcsDao();
	 private Hashtable sendeddata = ShareData.getProcsendeddata();
	 
	 java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public LoadAixFile_bak(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
	public Hashtable getTelnetMonitorDetail()
    {		
		Hashtable returnHash = new Hashtable();
		StringBuffer fileContent = new StringBuffer();
		Vector cpuVector=new Vector();
		Vector systemVector=new Vector();
		Vector userVector=new Vector();
		Vector diskVector=new Vector();
		Vector processVector=new Vector();
		
		CPUcollectdata cpudata=null;
		Systemcollectdata systemdata=null;
		Usercollectdata userdata=null;
		Processcollectdata processdata=null;
		
    	try 
		{
    			//String filename = CommonAppUtil.getAppName() + "unixserver" + File.separator + this.getTheOwnerNode().getIpAddress()+ "-" + getMaxNum(this.getTheOwnerNode().getIpAddress()) +".log";
    		//if (getMaxNum(ipaddress) != null){
				String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+ipaddress+".log";					
				File file=new File(filename);
				if(!file.exists()){
					//文件不存在,则产生告警
					try{
						createFileNotExistSMS(ipaddress);
					}catch(Exception e){
						e.printStackTrace();
					}
					return null;
				}
				file = null;
				FileInputStream fis = new FileInputStream(filename);
				InputStreamReader isr=new InputStreamReader(fis);
				BufferedReader br=new BufferedReader(isr);
				String strLine = null;
	    		//读入文件内容
	    		while((strLine=br.readLine())!=null)
	    		{
	    			fileContent.append(strLine + "\n");
	    		}
	    		isr.close();
	    		fis.close();
	    		br.close();
	    		try{
	    			copyFile(ipaddress,getMaxNum(ipaddress));
	    			//deleteFile(ipaddress);
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
    		//}
		} 
    	catch (Exception e)
		{
			e.printStackTrace();
		}

    	Pattern tmpPt = null;
    	Matcher mr = null;
    	Calendar date = Calendar.getInstance();
    	
		//　----------------解析uptime内容--创建监控项---------------------    
    	try{
		String uptimeContent = "";
		tmpPt = Pattern.compile("(cmdbegin:uptime)(.*)(cmdbegin:end)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			uptimeContent = mr.group(2);
		} 
		//String[] uptimeLineArr = uptimeContent.split("\n");
		//String[] date_tmpData = null;
		if (uptimeContent != null && uptimeContent.length()>0){
			systemdata=new Systemcollectdata();
			systemdata.setIpaddress(ipaddress);
			systemdata.setCollecttime(date);
			systemdata.setCategory("System");
			systemdata.setEntity("SysUptime");
			systemdata.setSubentity("SysUptime");
			systemdata.setRestype("static");
			systemdata.setUnit(" ");
			systemdata.setThevalue(uptimeContent.trim());
			systemVector.addElement(systemdata);
		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
		//　----------------解析date内容--创建监控项--------------------- 
    	try{
		String dateContent = "";
		tmpPt = Pattern.compile("(cmdbegin:date)(.*)(cmdbegin:uptime)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			dateContent = mr.group(2);
		} 
		//String[] dateLineArr = dateContent.split("\n");
		//String[] date_tmpData = null;
		if (dateContent != null && dateContent.length()>0){
			systemdata=new Systemcollectdata();
			systemdata.setIpaddress(ipaddress);
			systemdata.setCollecttime(date);
			systemdata.setCategory("System");
			systemdata.setEntity("Systime");
			systemdata.setSubentity("Systime");
			systemdata.setRestype("static");
			systemdata.setUnit(" ");
			systemdata.setThevalue(dateContent.trim());
			systemVector.addElement(systemdata);

		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}

    	
		//　----------------解析user内容--创建监控项---------------------     
    	try{
		String userContent = "";
		tmpPt = Pattern.compile("(cmdbegin:user)(.*)(cmdbegin:date)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			userContent = mr.group(2);
		} 
		String[] userLineArr = userContent.split("\n");
		String[] user_tmpData = null;
		for(int i=0; i<userLineArr.length;i++){
			//user_tmpData = userLineArr[i].split("\\s++");	
			//String[] result = userLineArr[i].trim().split(":");
			String result = userLineArr[i].trim();
			//if (result.length>0){
				userdata=new Usercollectdata();
				userdata.setIpaddress(ipaddress);
				userdata.setCollecttime(date);
				userdata.setCategory("User");
				userdata.setEntity("Sysuser");
				userdata.setSubentity(result);
				userdata.setRestype("static");
				userdata.setUnit(" ");
				userdata.setThevalue(result);
				userVector.addElement(userdata);
				//continue;								
			//}
			
		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}

		//　----------------解析uname内容--创建监控项---------------------     
    	try{
		String unameContent = "";
		tmpPt = Pattern.compile("(cmdbegin:uname)(.*)(cmdbegin:user)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			unameContent = mr.group(2);
		} 
		String[] unameLineArr = unameContent.split("\n");
		String[] uname_tmpData = null;
		for(int i=0; i<unameLineArr.length;i++){
			uname_tmpData = unameLineArr[i].split("\\s++");				
			if (uname_tmpData.length==2){	
				  systemdata=new Systemcollectdata();
				  systemdata.setIpaddress(ipaddress);
				  systemdata.setCollecttime(date);
				  systemdata.setCategory("System");
				  systemdata.setEntity("operatSystem");
				  systemdata.setSubentity("operatSystem");
				  systemdata.setRestype("static");
				  systemdata.setUnit(" ");
				  systemdata.setThevalue(uname_tmpData[0]);
				  systemVector.addElement(systemdata);
				  
					systemdata=new Systemcollectdata();
					systemdata.setIpaddress(ipaddress);
					systemdata.setCollecttime(date);
					systemdata.setCategory("System");
					systemdata.setEntity("SysName");
					systemdata.setSubentity("SysName");
					systemdata.setRestype("static");
					systemdata.setUnit(" ");
					systemdata.setThevalue(uname_tmpData[1]);
				  systemVector.addElement(systemdata);								
				
			}				
		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}

    	
		//　----------------解析mac内容--创建监控项---------------------    
    	try{
		String macContent = "";
		tmpPt = Pattern.compile("(cmdbegin:mac)(.*)(cmdbegin:uname)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			macContent = mr.group(2);
		} 
		String[] macLineArr = macContent.split("\n");
		String[] mac_tmpData = null;
		String MAC = "";
		for(int i=0; i<macLineArr.length;i++){
			mac_tmpData = macLineArr[i].trim().split("\\s++");			
			if (mac_tmpData.length==9){	
				if(mac_tmpData[2].indexOf("link")>=0 && mac_tmpData[3] != null && mac_tmpData[3].trim().length()>0){
					if (i==0){
						MAC=MAC+mac_tmpData[3]+",";
					}else if (i==macLineArr.length-1){
						MAC=MAC+mac_tmpData[3];
					}else
						MAC=MAC+mac_tmpData[3]+",";
				}
			}				
		}
		  systemdata=new Systemcollectdata();
		  systemdata.setIpaddress(ipaddress);
		  systemdata.setCollecttime(date);
		  systemdata.setCategory("System");
		  systemdata.setEntity("MacAddr");
		  systemdata.setSubentity("MacAddr");
		  systemdata.setRestype("static");
		  systemdata.setUnit(" ");			  
		  systemdata.setThevalue(MAC);
		  systemVector.addElement(systemdata);
    	}catch(Exception e){
    		e.printStackTrace();
    	}

			//　----------------解析cpu内容--创建监控项--------------------- 
    	
		  Hashtable proHash = new Hashtable();
		 try{
			String cpuContent = "";
			tmpPt = Pattern.compile("(cmdbegin:proc)(.*)(cmdbegin:memory)",Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if(mr.find())
			{
				cpuContent = mr.group(2);
			} 
			String[] cpuLineArr = cpuContent.split("\n");
			String[] cpu_tmpData = null;
			for(int i=0; i<cpuLineArr.length;i++)
			{    			
				cpu_tmpData = cpuLineArr[i].trim().split("\\s++");				
				//0:PID 1:CMD 2:TIME 3:MEM
				if((cpu_tmpData != null) && (cpu_tmpData.length == 4)){					
					String[] _cpudata = new String[4];
					if (cpu_tmpData[0].equalsIgnoreCase("PID"))continue;
					_cpudata[0] = cpu_tmpData[0];//PID
					_cpudata[1] = cpu_tmpData[1];//CMD
					_cpudata[2] = cpu_tmpData[2];//TIME	
					_cpudata[3] = cpu_tmpData[3];//MEM
					proHash.put(_cpudata[0], _cpudata);
				}				
			}		  
 
		  
		  
		
		//　----------------解析cpu内容--创建监控项---------------------        	
		cpuContent = "";
		List procslist = new ArrayList();
		try{
			procslist = procsManager.loadByIp(ipaddress);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		List procs_list = new ArrayList();
		Hashtable procshash = new Hashtable();
		Vector procsV = new Vector();
		if (procslist != null && procslist.size()>0){
			for(int i=0;i<procslist.size();i++){
				Procs procs = (Procs)procslist.get(i);
				procshash.put(procs.getProcname(),procs);
				procsV.add(procs.getProcname());
			}
		}
		/*
		float cpuusage = 0.0f;
		if(cpuusage>100.0f)cpuusage=(float)(97f+Math.random()*3.0);		
	   		cpudata=new CPUcollectdata();
	   		cpudata.setIpaddress(ipaddress);
	   		cpudata.setCollecttime(date);
	   		cpudata.setCategory("CPU");
	   		cpudata.setEntity("Utilization");
	   		cpudata.setSubentity("Utilization");
	   		cpudata.setRestype("dynamic");
	   		cpudata.setUnit("%");
	   		cpudata.setThevalue(Float.toString(cpuusage));
	   		cpuVector.addElement(cpudata);	  	
    	*/
		systemdata=new Systemcollectdata();
		systemdata.setIpaddress(ipaddress);
		systemdata.setCollecttime(date);
		systemdata.setCategory("System");
		systemdata.setEntity("ProcessCount");
		systemdata.setSubentity("ProcessCount");
		systemdata.setRestype("static");
		systemdata.setUnit(" ");
		systemdata.setThevalue(cpuLineArr.length-1+"");
		systemVector.addElement(systemdata);		
		if (proHash != null&& proHash.size()>0){			
			Iterator keys=proHash.keySet().iterator();
	 		  String key="";
			 while(keys.hasNext()){
				key=keys.next().toString();
				String[] pro = (String[])proHash.get(key);
				String vbstring0=key;//pid
				String vbstring1=pro[1];//command
				String vbstring2="应用程序";
				String vbstring3="正在运行";
				String vbstring4="";//memsize
				String vbstring5=pro[2];//cputime
				String vbstring6=pro[3];//%mem
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("MemoryUtilization");
				processdata.setSubentity(vbstring0);
				processdata.setRestype("dynamic");
				processdata.setUnit("%");
				processdata.setThevalue("");
				processVector.addElement(processdata);	
		
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Memory");
				processdata.setSubentity(vbstring0);
				processdata.setRestype("static");
				processdata.setUnit("K");
				processdata.setThevalue(pro[3]);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Type");
				processdata.setSubentity(vbstring0);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring2);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Status");
				processdata.setSubentity(vbstring0);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring3);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Name");
				processdata.setSubentity(vbstring0);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring1);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("CpuTime");
				processdata.setSubentity(vbstring0);
				processdata.setRestype("static");
				processdata.setUnit("秒");
				processdata.setThevalue(vbstring5);
				processVector.addElement(processdata);
				/*
				//判断是否有需要监视的进程，若取得的列表里包含监视进程，则从Vector里去掉
				if (procshash !=null && procshash.size()>0){
					if (procshash.containsKey(vbstring1)){
						procshash.remove(vbstring1);
						procsV.remove(vbstring1);
					}
				}
				*/
				
				//判断是否有需要监视的进程，若取得的列表里包含监视进程，则从Vector里去掉
				if (procsV !=null && procsV.size()>0){					
					if (procsV.contains(vbstring1)){
						//procshash.remove(vbstring1);						
						procsV.remove(vbstring1);
						//判断已经发送的进程短信列表里是否有该进程,若有,则从已发送列表里去掉该短信信息
						if(sendeddata.containsKey(ipaddress+":"+vbstring1)){
							sendeddata.remove(ipaddress+":"+vbstring1);
						}
						//判断进程丢失列表里是否有该进程,若有,则从该列表里去掉该信息
			     		Hashtable iplostprocdata = (Hashtable)ShareData.getLostprocdata(ipaddress);
			     		if(iplostprocdata == null)iplostprocdata = new Hashtable();
			     		if (iplostprocdata.containsKey(vbstring1)){
			     			iplostprocdata.remove(vbstring1);
			     			ShareData.setLostprocdata(ipaddress, iplostprocdata);
			     		}
			     		
						
					}
				}
				
				
			  }
			 
			 /*
			 //判断ProcsV里还有没有需要监视的进程，若有，则说明当前没有启动该进程，则用命令重新启动该进程，同时写入事件
		     Vector eventtmpV = new Vector();
		     if (procsV !=null && procsV.size()>0){
		    	try{
		     	for(int i=0;i<procsV.size();i++){
		     		Procs procs = (Procs)procshash.get((String)procsV.get(i));		     	
					  Eventlist event = new Eventlist();
					  Monitoriplist monitoriplist = (Monitoriplist)monitormanager.getByIpaddress(ipaddress);
					  
					  event.setEventtype("host");
					  event.setEventlocation(ipaddress);
					  event.setManagesign(new Integer(0));
					  event.setReportman("monitorpc");
					  event.setNetlocation(equipmentManager.getByip(monitoriplist.getIpaddress()).getNetlocation());
					  event.setRecordtime(date);					  
					  event.setLevel1(new Integer(1));
					  event.setEquipment(equipmentManager.getByip(monitoriplist.getIpaddress()));
					  String time = sdf.format(date.getTime());
					  event.setContent(monitoriplist.getEquipname()+"&"+ipaddress+"&"+time+"进程"+procsV.get(i)+"不存在!&level=1");	
					  eventtmpV.add(event);
		     	}
		     	if (eventtmpV != null && eventtmpV.size()>0)
		     	eventmanager.createEventlist(eventtmpV);
	     		}catch(Exception ex){
	     			ex.printStackTrace();
	     		}

		     }
		 */
			
			 //判断ProcsV里还有没有需要监视的进程，若有，则说明当前没有启动该进程，则用命令重新启动该进程，同时写入事件
		     Vector eventtmpV = new Vector();
		     if (procsV !=null && procsV.size()>0){
		     	for(int i=0;i<procsV.size();i++){		     		
		     		Procs procs = (Procs)procshash.get((String)procsV.get(i));	
		     		Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
		     		try{
			     		Hashtable iplostprocdata = (Hashtable)ShareData.getLostprocdata(ipaddress);
			     		if(iplostprocdata == null)iplostprocdata = new Hashtable();
			     		iplostprocdata.put(procs.getProcname(), procs);
			     		ShareData.setLostprocdata(ipaddress, iplostprocdata);
			    		EventList eventlist = new EventList();
			    		eventlist.setEventtype("poll");
			    		eventlist.setEventlocation(host.getSysLocation());
			    		eventlist.setContent(procs.getProcname()+"进程丢失");
			    		eventlist.setLevel1(1);
			    		eventlist.setManagesign(0);
			    		eventlist.setBak("");
			    		eventlist.setRecordtime(Calendar.getInstance());
			    		eventlist.setReportman("系统轮询");
			    		NodeToBusinessDao ntbdao = new NodeToBusinessDao();
			    		List ntblist = ntbdao.loadByNodeAndEtype(host.getId(), "equipment");
			    		String bids = ",";
			    		if(ntblist != null && ntblist.size()>0){
			    			
			    			for(int k=0;k<ntblist.size();k++){
			    				NodeToBusiness ntb = (NodeToBusiness)ntblist.get(k);
			    				bids=bids+ntb.getBusinessid()+",";
			    			}
			    		}
			    		eventlist.setBusinessid(bids);
			    		eventlist.setNodeid(host.getId());
			    		eventlist.setOid(0);
			    		eventlist.setSubtype("host");
			    		eventlist.setSubentity("proc");
			    		EventListDao eventlistdao = new EventListDao();
			    		eventlistdao.save(eventlist);	
			    		eventtmpV.add(eventlist);
						//发送手机短信并写事件和声音告警
						createSMS(procs);		     			
		     		}catch(Exception e){
		     			e.printStackTrace();
		     		}
		     	}
		     }
			 
			
		}
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}

		//　----------------解析memory内容--创建监控项---------------------        	
		String memory_Content = "";
		tmpPt = Pattern.compile("(cmdbegin:memory)(.*)(cmdbegin:vmstat)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			try{
			memory_Content = mr.group(2);
			}catch(Exception e){
				e.printStackTrace();
			}
		} 
		String[] memory_LineArr = memory_Content.split("\n");
		String[] memory_tmpData = null;
		//Vector memoryVector=new Vector();
		//Memorycollectdata memorydata=null;
		int allPhysicalMemory =0;
		try{
		for(int i=1; i<memory_LineArr.length;i++)
		{    			
			memory_tmpData = memory_LineArr[i].split("\\s++");
			if((memory_tmpData != null))
			{				
				if (memory_tmpData[0]!=null && memory_tmpData[0].equalsIgnoreCase("Good")){
					//Memory
					allPhysicalMemory = Integer.parseInt(memory_tmpData[3]);
					for(int j=0;j<memory_tmpData.length;j++){
						//System.out.println(memory_tmpData[j]+"===============");
					}				
				}
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		//　----------------解析vmstat内容--创建监控项---------------------        	
		String vmstat_Content = "";
		tmpPt = Pattern.compile("(cmdbegin:vmstat)(.*)(cmdbegin:lsps)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			try{
			vmstat_Content = mr.group(2);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		} 
		String[] vmstat_LineArr = null;
		String[] vmstat_tmpData = null;
		int freePhysicalMemory =0;
		//Vector memoryVector=new Vector();
		//Memorycollectdata memorydata=null;
		try{
		vmstat_LineArr = vmstat_Content.split("\n");
		
		for(int i=1; i<vmstat_LineArr.length;i++)
		{    			
			vmstat_tmpData = vmstat_LineArr[i].trim().split("\\s++");			
			if((vmstat_tmpData != null && vmstat_tmpData.length==17))
			{							
				if (vmstat_tmpData[0]!=null && !vmstat_tmpData[0].equalsIgnoreCase("r")){
					//freeMemory
					freePhysicalMemory = Integer.parseInt(vmstat_tmpData[3])*4/1024;					
				}

			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
//System.out.println(ipaddress+"============================memory");		
		//　----------------解析lsps内容--创建监控项---------------------        	
		String lsps_Content = "";
		tmpPt = Pattern.compile("(cmdbegin:lsps)(.*)(cmdbegin:mac)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			lsps_Content = mr.group(2);
		} 
		String[] lsps_LineArr = lsps_Content.split("\n");
		String[] lsps_tmpData = null;
		int allSwapMemory =0;
		int usedPercent = 0;
		for(int i=1; i<lsps_LineArr.length;i++)
		{    			
			lsps_tmpData = lsps_LineArr[i].trim().split("\\s++");			
			if((lsps_tmpData != null && lsps_tmpData.length==2))
			{			
				String allSwapStr =  lsps_tmpData[0];
				String swapUsedPer = lsps_tmpData[1];	
				usedPercent = Integer.parseInt(swapUsedPer.substring(0,swapUsedPer.length()-1));
				allSwapMemory = Integer.parseInt(allSwapStr.substring(0,allSwapStr.length()-2));				
			}
		}	
		Vector memoryVector=new Vector();
		Memorycollectdata memorydata=null;
		float PhysicalMemUtilization =
			Float.parseFloat(Integer.toString(allPhysicalMemory-freePhysicalMemory))
					* 100
					/ Float.parseFloat(Integer.toString(allPhysicalMemory));
			memorydata=new Memorycollectdata();
			memorydata.setIpaddress(ipaddress);
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("Capability");
			memorydata.setSubentity("PhysicalMemory");
			memorydata.setRestype("static");
			memorydata.setUnit("M");
			memorydata.setThevalue(
				Float.toString(allPhysicalMemory));
			memoryVector.addElement(memorydata);
		
  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("UsedSize");
  			memorydata.setSubentity("PhysicalMemory");
  			memorydata.setRestype("static");
  			memorydata.setUnit("M");//(allPhysicalMemory-freePhysicalMemory)
  			memorydata.setThevalue( 
					Integer.toString(allPhysicalMemory-freePhysicalMemory));
  			memoryVector.addElement(memorydata);

  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("Utilization");
  			memorydata.setSubentity("PhysicalMemory");
  			memorydata.setRestype("dynamic");
  			memorydata.setUnit("%");
  			memorydata.setThevalue(
					Float.toString(PhysicalMemUtilization));
  			memoryVector.addElement(memorydata);
		
  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("Capability");
  			memorydata.setSubentity("SwapMemory");
  			memorydata.setRestype("static");
  			memorydata.setUnit("M");
  			memorydata.setThevalue(
					Integer.toString(allSwapMemory));
  			memoryVector.addElement(memorydata);
		
  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("UsdeSize");
  			memorydata.setSubentity("SwapMemory");
  			memorydata.setRestype("static");
  			memorydata.setUnit("M");
  			memorydata.setThevalue(
					Integer.toString(usedPercent*allSwapMemory/100));
  			memoryVector.addElement(memorydata);
		
  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("Utilization");
  			memorydata.setSubentity("SwapMemory");
  			memorydata.setRestype("dynamic");
  			memorydata.setUnit("%");
  			memorydata.setThevalue(Float.toString(usedPercent));
  			memoryVector.addElement(memorydata);
						
		//　----------------解析disk内容--创建监控项---------------------
//System.out.println(ipaddress+"----------------------disk");  		
		String diskContent = "";
		String diskLabel;
		tmpPt = Pattern.compile("(cmdbegin:disk)(.*)(cmdbegin:cpu)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			diskContent = mr.group(2);
		}		
		String[] diskLineArr = diskContent.split("\n");		
		String[] tmpData = null;
		//tmpPt = Pattern.compile("(^/[\\w/#]*)(\\s++)(\\d++)(\\s++)((\\d++)(\\s++)){2}+(\\d++)%");
		Diskcollectdata diskdata=null;		
		for(int i=1; i<diskLineArr.length;i++)
		{		
			try{
			
			tmpData = diskLineArr[i].split("\\s++");			
			if((tmpData != null) && (tmpData.length == 7))
			{
				diskLabel = tmpData[5];
				diskdata=new Diskcollectdata();
				diskdata.setIpaddress(ipaddress);
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("Utilization");//利用百分比
				diskdata.setSubentity(tmpData[6]);
				diskdata.setRestype("static");
				diskdata.setUnit("%");	
				if(tmpData[3].equals("-")){
					diskdata.setThevalue("0");
				}else
				diskdata.setThevalue(
						Float.toString(
								Float.parseFloat(
								tmpData[3].substring(
								0,
								tmpData[3].indexOf("%")))));
				diskVector.addElement(diskdata);

				diskdata=new Diskcollectdata();
				diskdata.setIpaddress(ipaddress);
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("AllSize");//总空间
				diskdata.setSubentity(tmpData[6]);
				diskdata.setRestype("static");

				float allblocksize=0;
				if(!tmpData[1].equals("-"))
				allblocksize=Float.parseFloat(tmpData[1]);
				float allsize=0.0f;
				allsize=allblocksize*1.0f/1024;
				if(allsize>=1024.0f){
					allsize=allsize/1024;
					diskdata.setUnit("G");
				}
				else{
					diskdata.setUnit("M");
				}

				diskdata.setThevalue(Float.toString(allsize));
				diskVector.addElement(diskdata);

				diskdata=new Diskcollectdata();
				diskdata.setIpaddress(ipaddress);
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("UsedSize");//使用大小
				diskdata.setSubentity(tmpData[6]);
				diskdata.setRestype("static");

				float UsedintSize=0;
				if(!tmpData[2].equals("-"))
				UsedintSize=Float.parseFloat(tmpData[1])-Float.parseFloat(tmpData[2]);
				float usedfloatsize=0.0f;
				usedfloatsize=UsedintSize*1.0f/1024;
				if(usedfloatsize>=1024.0f){
					usedfloatsize=usedfloatsize/1024;
					diskdata.setUnit("G");
				}
				else{
					diskdata.setUnit("M");
				}
				diskdata.setThevalue(Float.toString(usedfloatsize));
				diskVector.addElement(diskdata);
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}   		
//System.out.println(ipaddress+"----------------cpu");		
		//　----------------解析cpu内容--创建监控项---------------------
		try{
		String cpu_Content = "";
		tmpPt = Pattern.compile("(cmdbegin:cpu)(.*)(cmdbegin:proc)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			cpu_Content = mr.group(2);
		}		
		String[] cpu_LineArr = cpu_Content.split("\n");		
		String[] tmpcpuData = null;
		//tmpPt = Pattern.compile("(^/[\\w/#]*)(\\s++)(\\d++)(\\s++)((\\d++)(\\s++)){2}+(\\d++)%");
		//Diskcollectdata diskdata=null;		
		for(int i=1; i<cpu_LineArr.length;i++)
		{			
			tmpData = cpu_LineArr[i].split("\\s++");
			if((tmpData != null) && ((tmpData.length == 5)||(tmpData.length == 6)))
			{
				if(tmpData[0] != null && tmpData[0].equalsIgnoreCase("Average")){
//System.out.println(tmpData[0]+":"+tmpData[4]+"----------------");					
			   		cpudata=new CPUcollectdata();
			   		cpudata.setIpaddress(ipaddress);
			   		cpudata.setCollecttime(date);
			   		cpudata.setCategory("CPU");
			   		cpudata.setEntity("Utilization");
			   		cpudata.setSubentity("Utilization");
			   		cpudata.setRestype("dynamic");
			   		cpudata.setUnit("%");
			   		cpudata.setThevalue(Float.toString(100-Float.parseFloat(tmpData[4])));
			   		cpuVector.addElement(cpudata);	  	
				
				}
			}
		}    	    		    		    	    		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		//　----------------解析用户登陆历史内容--创建监控项---------------------  		
				String syslogContent = "";
				//String diskLabel;
				tmpPt = Pattern.compile("(cmdbegin:end)(.*)(cmdbegin:syslog)",Pattern.DOTALL);
				mr = tmpPt.matcher(fileContent.toString());
				if(mr.find())
				{
					syslogContent = mr.group(2);
				}		
				String[] syslogLineArr = syslogContent.split("\n");		
				String[] syslogData = null;
				//tmpPt = Pattern.compile("(^/[\\w/#]*)(\\s++)(\\d++)(\\s++)((\\d++)(\\s++)){2}+(\\d++)%");
				//Diskcollectdata syslogdata=null;
				List allSyslogList = new ArrayList();
				for(int i=1; i<syslogLineArr.length;i++)
				{							
					try{					
					tmpData = syslogLineArr[i].split("\\s++");			
					if((tmpData != null) )
					{
						List sysloglist = new ArrayList();					
						if(tmpData.length == 5){
							//系统重新启动
							String username = tmpData[0];
							String tools = tmpData[1];
							String timedetail = tmpData[2]+tmpData[3]+tmpData[4];	
							sysloglist.add(username);
							sysloglist.add(tools);
							sysloglist.add("");
							sysloglist.add(timedetail);
							allSyslogList.add(sysloglist);
						}else if (tmpData.length == 4){
							//系统重新启动
							String username = tmpData[0];
							String tools = tmpData[1];
							String timedetail = tmpData[2]+tmpData[3];	
							sysloglist.add(username);
							sysloglist.add(tools);
							sysloglist.add("");
							sysloglist.add(timedetail);
							allSyslogList.add(sysloglist);
						}else if (tmpData.length > 7){
							//正常处理
							String username = tmpData[0];
							String tools = tmpData[1];
							String ip = tmpData[2];
							String timedetail = "";
							for(int k=3;k<tmpData.length;k++){
								timedetail = timedetail+" "+tmpData[k];
							}
							sysloglist.add(username);
							sysloglist.add(tools);
							sysloglist.add(ip);
							sysloglist.add(timedetail);	
							allSyslogList.add(sysloglist);
						}
					}
					}catch(Exception e){
						e.printStackTrace();
					}
				}   		
		
		
		
		try{
			deleteFile(ipaddress);	
		}catch(Exception e){
			e.printStackTrace();
		}
		if (diskVector != null && diskVector.size()>0)returnHash.put("disk",diskVector);
		if (cpuVector != null && cpuVector.size()>0)returnHash.put("cpu",cpuVector);
		if (memoryVector != null && memoryVector.size()>0)returnHash.put("memory",memoryVector);
		if (userVector != null && userVector.size()>0)returnHash.put("user",userVector);
		if (processVector != null && processVector.size()>0)returnHash.put("process",processVector);	
		if (systemVector != null && systemVector.size()>0)returnHash.put("system",systemVector);
		if (allSyslogList != null && allSyslogList.size()>0)returnHash.put("syslog",allSyslogList);
		return returnHash;
    }	
	
    public String getMaxNum(String ipAddress){
    	String maxStr = null;
		File logFolder = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/");
   		String[] fileList = logFolder.list();
   		
   		for(int i=0;i<fileList.length;i++) //找一个最新的文件
   		{
   			if(!fileList[i].startsWith(ipAddress)) continue;
   			
   			return ipAddress;
   		}
   		return maxStr;
    }
	
    public void deleteFile(String ipAddress){

   			try
   			{
   				File delFile = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/" + ipAddress + ".log");
				System.out.println("###开始删除文件："+delFile);
				delFile.delete();
				System.out.println("###成功删除文件："+delFile);
   			}
   			catch(Exception e)		
   			{}
    }
    public void copyFile(String ipAddress,String max){
    	try   { 
    		String currenttime = SysUtil.getCurrentTime();
    		currenttime = currenttime.replaceAll("-", "");
    		currenttime = currenttime.replaceAll(" ", "");
    		currenttime = currenttime.replaceAll(":", "");
    		String ipdir = ipAddress.replaceAll("\\.", "-");
    		String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver_bak/"+ipdir;
    		File file=new File(filename);
    		if(!file.exists())file.mkdir();
            String cmd   =   "cmd   /c   copy   "+ResourceCenter.getInstance().getSysPath() + "linuxserver\\" + ipAddress + ".log"+" "+ResourceCenter.getInstance().getSysPath() + "linuxserver_bak\\" +ipdir+"\\"+ ipAddress+"-" +currenttime+ ".log";             
            //SysLogger.info(cmd);
            Process   child   =   Runtime.getRuntime().exec(cmd);   
          }catch (IOException e){    
            e.printStackTrace();
        }   
    	
    }
    
	 public void createSMS(Procs procs){
		 	Procs lastprocs = null;
		 	//建立短信	
		 	procs.setCollecttime(Calendar.getInstance());
		 	//从已经发送的短信列表里获得当前该PROC已经发送的短信
		 	lastprocs = (Procs)sendeddata.get(procs.getIpaddress()+":"+procs.getProcname());	
		 	/*
		 	try{		 				 		
		 		if (lastprocs==null){
		 			//内存中不存在	,表明没发过短信,则发短信
		 			Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
		 			Smscontent smscontent = new Smscontent();
		 			String time = sdf.format(procs.getCollecttime().getTime());
		 			smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&进程丢失&level=2");
		 			//发送短信
		 			Vector tosend = new Vector();
		 			tosend.add(smscontent);		 			
		 			smsmanager.sendSmscontent(tosend);
		 			//把该进程信息添加到已经发送的进程短信列表里,以IP:进程名作为key
		 			sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);		 						 				
		 		}else{
		 			//若已经发送的短信列表存在这个IP的PROC进程
		 			//若在，则从已发送短信列表里判断是否已经发送当天的短信		 				
		 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 			Date last = null;
		 			Date current = null;
		 			Calendar sendcalen = (Calendar)lastprocs.getCollecttime();
		 			Date cc = sendcalen.getTime();
		 			String tempsenddate = formatter.format(cc);
		 			
		 			Calendar currentcalen = (Calendar)procs.getCollecttime();
		 			cc = currentcalen.getTime();
		 			last = formatter.parse(tempsenddate);
		 			String currentsenddate = formatter.format(cc);
		 			current = formatter.parse(currentsenddate);
		 			
		 			long subvalue = current.getTime()-last.getTime();			 			
		 			
		 			if (subvalue/(1000*60*60*24)>=1){
		 				//超过一天，则再发信息
			 			Smscontent smscontent = new Smscontent();
			 			String time = sdf.format(procs.getCollecttime().getTime());
			 			Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
			 			if (equipment == null){
			 				return;
			 			}else
			 				smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&进程丢失&level=2");
			 			
			 			//发送短信
			 			Vector tosend = new Vector();
			 			tosend.add(smscontent);		 			
			 			smsmanager.sendSmscontent(tosend);
			 			//把该进程信息添加到已经发送的进程短信列表里,以IP:进程名作为key
			 			sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);		 						 				
			 		}else{
			 			//没超过一天,则只写事件
			 			Vector eventtmpV = new Vector();
						EventList event = new EventList();
						  Monitoriplist monitoriplist = (Monitoriplist)monitormanager.getByIpaddress(procs.getIpaddress());
						  event.setEventtype("host");
						  event.setEventlocation(procs.getIpaddress());
						  event.setManagesign(new Integer(0));
						  event.setReportman("monitorpc");
						  event.setRecordtime(Calendar.getInstance());
						  event.setLevel1(new Integer(1));
						  event.setEquipment(equipmentManager.getByip(monitoriplist.getIpaddress()));
						  event.setNetlocation(equipmentManager.getByip(monitoriplist.getIpaddress()).getNetlocation());
						  String time = sdf.format(Calendar.getInstance().getTime());
						  event.setContent(monitoriplist.getEquipname()+"&"+monitoriplist.getIpaddress()+"&"+time+"进程"+procs.getProcname()+"丢失&level=1");
						  eventtmpV.add(event);
						  try{
							  eventmanager.createEventlist(eventtmpV);
						  }catch(Exception e){
							  e.printStackTrace();
						  }						  
			 		}
		 		}
		 	}catch(Exception e){
		 		e.printStackTrace();
		 	}
		 	*/
		 }
	 
		public void createFileNotExistSMS(String ipaddress){
		 	//建立短信		 	
		 	//从内存里获得当前这个IP的PING的值
				Calendar date=Calendar.getInstance();
				try{
					Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
					if(host == null)return;
					
					if (!sendeddata.containsKey(ipaddress+":file:"+host.getId())){
						//若不在，则建立短信，并且添加到发送列表里
						Smscontent smscontent = new Smscontent();
						String time = sdf.format(date.getTime());
						smscontent.setLevel("3");
						smscontent.setObjid(host.getId()+"");
						smscontent.setMessage(host.getAlias()+" ("+host.getIpAddress()+")"+"的日志文件无法正确上传到网管服务器");
						smscontent.setRecordtime(time);
						smscontent.setSubtype("host");
						smscontent.setSubentity("ftp");
						smscontent.setIp(host.getIpAddress());//发送短信
//						SmscontentDao smsmanager=new SmscontentDao();
//						smsmanager.sendURLSmscontent(smscontent);	
						sendeddata.put(ipaddress+":file"+host.getId(),date);		 					 				
					}else{
						//若在，则从已发送短信列表里判断是否已经发送当天的短信
						Calendar formerdate =(Calendar)sendeddata.get(ipaddress+":file:"+host.getId());		 				
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						Date last = null;
						Date current = null;
						Calendar sendcalen = formerdate;
						Date cc = sendcalen.getTime();
						String tempsenddate = formatter.format(cc);
		 			
						Calendar currentcalen = date;
						cc = currentcalen.getTime();
						last = formatter.parse(tempsenddate);
						String currentsenddate = formatter.format(cc);
						current = formatter.parse(currentsenddate);
		 			
						long subvalue = current.getTime()-last.getTime();			 			
						if (subvalue/(1000*60*60*24)>=1){
							//超过一天，则再发信息
							Smscontent smscontent = new Smscontent();
							String time = sdf.format(date.getTime());
							smscontent.setLevel("3");
							smscontent.setObjid(host.getId()+"");
							smscontent.setMessage(host.getAlias()+" ("+host.getIpAddress()+")"+"的日志文件无法正确上传到网管服务器");
							smscontent.setRecordtime(time);
							smscontent.setSubtype("host");
							smscontent.setSubentity("ftp");
							smscontent.setIp(host.getIpAddress());//发送短信
//							SmscontentDao smsmanager=new SmscontentDao();
//							smsmanager.sendURLSmscontent(smscontent);
							//修改已经发送的短信记录	
							sendeddata.put(ipaddress+":file:"+host.getId(),date);	
						}	
					}	 			 			 			 			 	
				}catch(Exception e){
					e.printStackTrace();
				}
		 	}
    
}






