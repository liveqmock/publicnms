package com.afunms.polling.telnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.Arith;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.config.model.Nodeconfig;
import com.afunms.config.model.Nodecpuconfig;
import com.afunms.config.model.Procs;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.om.Usercollectdata;
import com.gatherResulttosql.HostDatatempCpuconfiRtosql;
import com.gatherResulttosql.HostDatatempCpuperRtosql;
import com.gatherResulttosql.HostDatatempDiskRttosql;
import com.gatherResulttosql.HostDatatempNodeconfRtosql;
import com.gatherResulttosql.HostDatatempProcessRtTosql;
import com.gatherResulttosql.HostDatatempUserRtosql;
import com.gatherResulttosql.HostDatatempinterfaceRtosql;
import com.gatherResulttosql.HostDatatempnDiskperfRtosql;
import com.gatherResulttosql.HostDatatemputilhdxRtosql;
import com.gatherResulttosql.HostPhysicalMemoryResulttosql;
import com.gatherResulttosql.HostcpuResultTosql;
import com.gatherResulttosql.HostdiskResultosql;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;
import com.gatherResulttosql.NetHostMemoryRtsql;

public class SolarisInfoParser
{

	private static boolean debug = true;
	
	private static void debug(String info)
	{
		if(debug)
		{
			System.out.println(info);
		}
	}
	
	
	
	public static Hashtable getTelnetMonitorDetail(Wrapper telnet)
	{
		Hashtable returnHash = new Hashtable();
		
		String ipaddress = telnet.getHost();
		
		Vector cpuVector=new Vector();
		Vector systemVector=new Vector();
		Vector userVector=new Vector();
		Vector diskVector=new Vector();
		Vector processVector=new Vector();
		Nodeconfig nodeconfig = new Nodeconfig();
		Vector interfaceVector = new Vector();
		Vector utilhdxVector = new Vector();
		
		CPUcollectdata cpudata=null;
		Systemcollectdata systemdata=null;
		Usercollectdata userdata=null;
		Processcollectdata processdata=null;
		Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
		if(host == null)return null;
		nodeconfig.setNodeid(host.getId());
		nodeconfig.setHostname(host.getAlias());
		float PhysicalMemCap = 0;
		float allPhyPagesSize = 0;
		float freePhysicalMemory =0; 
		float usedPhyPagesSize = 0;
		Hashtable networkconfig = new Hashtable();
		float SwapMemCap = 0;
		float freeSwapMemory =0;
		float usedSwapMemory =0;
		
		

    	Calendar date = Calendar.getInstance();
    	
    	
	     //----------------解析version内容--创建监控项---------------------        	
		String versionContent = "";
		
		try
		{
			versionContent = telnet.send("uname -snrvmapi");
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		
		if (versionContent != null && versionContent.length()>0){
			nodeconfig.setCSDVersion(versionContent.trim());
		}
		
		//----------------解析cpuconfig内容--创建监控项---------------------        	
		String cpuconfigContent = "";
		try
		{
			cpuconfigContent = telnet.send("psrinfo -vp");
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		System.out.println("cpuconfigContent = " + cpuconfigContent);
		
		// TODO 需要根据中英文 来区别解析
		
		boolean isChinese = false;
		if(cpuconfigContent != null && cpuconfigContent.length() > 0 && cpuconfigContent.contains("物理处理器"))
		{
			isChinese = true;
		}
		
		
			
		String[] cpuconfigLineArr = cpuconfigContent.split("\n");
		
		System.out.println("cpuconfigLineArr length = " + cpuconfigLineArr.length);
		
		String[] cpuconfig_tmpData = null;
		List<Nodecpuconfig> cpuconfiglist = new ArrayList<Nodecpuconfig>();
		Nodecpuconfig nodecpuconfig = new Nodecpuconfig();
		//String procesors = "";
		//处理器描述
		if(isChinese)
		{
			if(cpuconfigLineArr.length >= 3)
			{
				nodeconfig.setDescrOfProcessors(cpuconfigLineArr[2].trim());
			}
			else
			{
				nodeconfig.setDescrOfProcessors(cpuconfigContent);
			}
			String[] processorsresult = cpuconfigLineArr[0].trim().split("\\s++");
			//处理器个数
			nodeconfig.setNumberOfProcessors(processorsresult[1].trim());
			debug("has " + processorsresult[1].trim() + " physical processor");
		}
		else
		{
			nodeconfig.setDescrOfProcessors(cpuconfigLineArr[3].trim());
			String[] processorsresult = cpuconfigLineArr[1].trim().split("\\s++");
			//处理器个数
			nodeconfig.setNumberOfProcessors(processorsresult[4].trim());
		}
		
		cpuconfiglist.add(nodecpuconfig);
		
		
		//　----------------解析disk内容--创建监控项---------------------
		String diskContent = "";
		String diskLabel;
		List disklist = new ArrayList();
		
		try
		{
			diskContent = telnet.send("df -k");
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		String[] diskLineArr = diskContent.split("\n");
		String[] tmpData = null;
		Diskcollectdata diskdata=null;
		int diskflag = 0;
		for(int i=1; i<diskLineArr.length;i++)
		{
			
			tmpData = diskLineArr[i].split("\\s++");
			if((tmpData != null) && (tmpData.length == 6))
			{
				diskLabel = tmpData[5];
				
				diskdata=new Diskcollectdata();
				diskdata.setIpaddress(ipaddress);
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("Utilization");//利用百分比
				diskdata.setSubentity(tmpData[5]);
				diskdata.setRestype("static");
				diskdata.setUnit("%");
				try{
				diskdata.setThevalue(
						Float.toString(
								Float.parseFloat(
								tmpData[4].substring(
								0,
								tmpData[4].indexOf("%")))));
				}catch(Exception ex){
					continue;
				}
				diskVector.addElement(diskdata);

				diskdata=new Diskcollectdata();
				diskdata.setIpaddress(ipaddress);
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("AllSize");//总空间
				diskdata.setSubentity(tmpData[5]);
				diskdata.setRestype("static");

				float allblocksize=0;
				allblocksize=Float.parseFloat(tmpData[1]);
				float allsize=0.0f;
				allsize=allblocksize;
				if(allsize>=1024.0f&&allsize<1048576.0f){
					allsize=allsize/1024;
					diskdata.setUnit("M");
				}
				else if(allsize>=1048576.0f){
					allsize=allsize/1048576;
					diskdata.setUnit("G");
				}else{
					diskdata.setUnit("KB");
				}

				diskdata.setThevalue(Float.toString(allsize));
				diskVector.addElement(diskdata);

				diskdata=new Diskcollectdata();
				diskdata.setIpaddress(ipaddress);
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("UsedSize");//使用大小
				diskdata.setSubentity(tmpData[5]);
				diskdata.setRestype("static");

				
				float usedfloatsize=0.0f;
				usedfloatsize = Float.parseFloat(tmpData[2]);
				if(usedfloatsize>=1024.0f&&allsize<1048576.0f){
					usedfloatsize=usedfloatsize/1024;
					diskdata.setUnit("M");
				}
				else if(usedfloatsize>=1048576.0f){
					usedfloatsize=usedfloatsize/1048576;
					diskdata.setUnit("G");
				}else{
					diskdata.setUnit("KB");
				}
				diskdata.setThevalue(Float.toString(usedfloatsize));
				diskVector.addElement(diskdata);
				disklist.add(diskflag,diskLabel);
				diskflag = diskflag +1;
			}
		}
		//----------------解析diskperf内容--创建监控项---------------------        	
		String diskperfContent = "";
		String average = "";
		
		try
		{
			diskperfContent = telnet.send("sar -d 1 2");
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		String[] diskperfLineArr = diskperfContent.split("\n");
		String[] diskperf_tmpData = null;
		List alldiskperf = new ArrayList();
		Hashtable<String,String> diskperfhash = new Hashtable<String,String>();
		int flag = 0;
		for(int i=0; i<diskperfLineArr.length;i++){
			diskperf_tmpData = diskperfLineArr[i].trim().split("\\s++");
			if(diskperf_tmpData != null && (diskperf_tmpData.length==7 || diskperf_tmpData.length==8)){
				if(diskperf_tmpData[0].trim().equalsIgnoreCase("Average")){
					flag = 1;
					diskperfhash.put("%busy", diskperf_tmpData[2].trim());
					diskperfhash.put("avque", diskperf_tmpData[3].trim());
					diskperfhash.put("r+w/s", diskperf_tmpData[4].trim());
					diskperfhash.put("Kbs/s", diskperf_tmpData[5].trim());
					diskperfhash.put("avwait", diskperf_tmpData[6].trim());
					diskperfhash.put("avserv", diskperf_tmpData[7].trim());
					diskperfhash.put("disklebel", diskperf_tmpData[1].trim());
					alldiskperf.add(diskperfhash);
				}else if(flag == 1){
					diskperfhash.put("%busy", diskperf_tmpData[1].trim());
					diskperfhash.put("avque", diskperf_tmpData[2].trim());
					diskperfhash.put("r+w/s", diskperf_tmpData[3].trim());
					diskperfhash.put("Kbs/s", diskperf_tmpData[4].trim());
					diskperfhash.put("avwait", diskperf_tmpData[5].trim());
					diskperfhash.put("avserv", diskperf_tmpData[6].trim());
					diskperfhash.put("disklebel", diskperf_tmpData[0].trim());
					alldiskperf.add(diskperfhash);
				}		
				diskperfhash = new Hashtable();
			}				
		}
		
		
		
	
		
			
			
						
		
		//----------------解析cpu内容--创建监控项---------------------        	
		String cpuperfContent = "";
		//String average = "";
		
		try {
			cpuperfContent = telnet.send("sar -u 1 3");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		String[] cpuperfLineArr = cpuperfContent.split("\n");
		List cpuperflist = new ArrayList();
		Hashtable<String,String> cpuperfhash = new Hashtable<String,String>();
		for(int i=0; i<cpuperfLineArr.length;i++){
			diskperf_tmpData = cpuperfLineArr[i].trim().split("\\s++");
			if(diskperf_tmpData != null && diskperf_tmpData.length==5){
				if(diskperf_tmpData[0].trim().equalsIgnoreCase("Average")){
					
						cpuperfhash.put("%usr", diskperf_tmpData[1].trim());
						cpuperfhash.put("%sys", diskperf_tmpData[2].trim());
						cpuperfhash.put("%wio", diskperf_tmpData[3].trim());
						cpuperfhash.put("%idle", diskperf_tmpData[4].trim());
						cpuperflist.add(cpuperfhash);
						
						cpudata=new CPUcollectdata();
				   		cpudata.setIpaddress(ipaddress);
				   		cpudata.setCollecttime(date);
				   		cpudata.setCategory("CPU");
				   		cpudata.setEntity("Utilization");
				   		cpudata.setSubentity("Utilization");
				   		cpudata.setRestype("dynamic");
				   		cpudata.setUnit("%");
				   		cpudata.setThevalue(Arith.round((100.0-Double.parseDouble(diskperf_tmpData[4].trim())),0)+"");
				   		cpuVector.addElement(cpudata);
					
				}
			}				
		}
		
		//----------------解析memory内容--创建监控项---------------------        	
		String memperfContent = "";
		
		try
		{
			memperfContent = telnet.send("vmstat");
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		
		String[] memperfLineArr = memperfContent.split("\n");
		List memperflist = new ArrayList();
		Vector memoryVector=new Vector();
		String[] memory_tempData = null;
		Memorycollectdata memorydata=null;
		Hashtable<String,String> memperfhash = new Hashtable<String,String>();
		for(int i=0; i<memperfLineArr.length;i++){
			
			
			int j = memperfLineArr.length;
			diskperf_tmpData = memperfLineArr[j-1].trim().split("\\s++");
			if(diskperf_tmpData != null && diskperf_tmpData.length>=22){
//				if(memperfLineArr[i].trim().contains(":")){
//					memory_tempData = memperfLineArr[i].trim().split(":");
//					
//						if(memory_tempData[0].trim().equalsIgnoreCase("Memory size")){
//							String allphy = memory_tempData[1].trim().trim();
//							try{
//								allphy = allphy.replaceAll("Megabytes", "");
//								PhysicalMemCap = Float.parseFloat(allphy);
//							}catch(Exception e){
//								e.printStackTrace();
//							}
//						}
//					}
				if (diskperf_tmpData[0]!=null && !diskperf_tmpData[0].equalsIgnoreCase("r")){
					//freeMemory
					freePhysicalMemory = Integer.parseInt(diskperf_tmpData[4])*4/1024;	
					
				}
					memperfhash.put("free",String.valueOf(freePhysicalMemory));
					memperflist.add(memperfhash);
					memperfhash = new Hashtable();
					//Memory
					
				
			}
		}
		
		String mem_Content = "";
		try {
			mem_Content = telnet.send("prtconf | head -2");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		String[] mem_fLineArr = mem_Content.split("\n");
		List mem_list = new ArrayList();
		Vector mem_Vector=new Vector();
		String[] mem_tempData = null;
		Memorycollectdata mem_data=null;
		Hashtable<String,String> mem_fhash = new Hashtable<String,String>();
		for(int i=0; i<mem_fLineArr.length;i++){
			
			
				if(mem_fLineArr[i].trim().contains(":")){
					memory_tempData = mem_fLineArr[i].trim().split(":");
					
						if(memory_tempData[0].trim().equalsIgnoreCase("Memory size")){
							String allphy = memory_tempData[1].trim().trim();
							try{
								allphy = allphy.replaceAll("Megabytes", "");
								PhysicalMemCap = Float.parseFloat(allphy);
								//System.out.println("==================PhysicalMemCap====================="+PhysicalMemCap);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
				
			}
		if(PhysicalMemCap > 0){            
		float PhysicalMemUtilization =100- (PhysicalMemCap-freePhysicalMemory)* 100/ PhysicalMemCap;
					
					memorydata=new Memorycollectdata();
		  			memorydata.setIpaddress(ipaddress);
		  			memorydata.setCollecttime(date);
		  			memorydata.setCategory("Memory");
		  			memorydata.setEntity("Capability");
		  			memorydata.setSubentity("PhysicalMemory");
		  			memorydata.setRestype("static");
		  			memorydata.setUnit("M");
		  			memorydata.setThevalue(
		  					Float.toString(PhysicalMemCap));
		  			memoryVector.addElement(memorydata);
		  			
		  			memorydata=new Memorycollectdata();
		  			memorydata.setIpaddress(ipaddress);
		  			memorydata.setCollecttime(date);
		  			memorydata.setCategory("Memory");
		  			memorydata.setEntity("UsedSize");
		  			memorydata.setSubentity("PhysicalMemory");
		  			memorydata.setRestype("static");
		  			memorydata.setUnit("M");
		  			memorydata.setThevalue(
		  					Float.toString(PhysicalMemCap-freePhysicalMemory));
		  			memoryVector.addElement(memorydata);
		  			
		  			memorydata=new Memorycollectdata();
		  			memorydata.setIpaddress(ipaddress);
		  			memorydata.setCollecttime(date);
		  			memorydata.setCategory("Memory");
		  			memorydata.setEntity("Utilization");
		  			memorydata.setSubentity("PhysicalMemory");
		  			memorydata.setRestype("dynamic");
		  			memorydata.setUnit("%");
		  			memorydata.setThevalue(Math.round(PhysicalMemUtilization)+"");
		  			memoryVector.addElement(memorydata);
		}
			
		  		//　----------------解析swap内容--创建监控项---------------------        	
		  			String swap_Content = "";
		  			try
					{
						swap_Content = telnet.send("swap -s");
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
		  			
		  			String[] swap_LineArr = null;
		  			String[] swap_tmpData = null;
		  			
		  			try{
		  				swap_LineArr = swap_Content.trim().split("\n");
		  				for(int i=0; i<swap_LineArr.length;i++)
		  				{    			
		  					
		  					swap_tmpData = swap_LineArr[0].trim().split("\\s++");
		  					if(swap_tmpData != null && swap_tmpData.length==12){
		  						try{
		  							String swap1=swap_tmpData[10].replaceAll("k", "");
		  							String swap2=swap_tmpData[8].replaceAll("k", "");
		  							
		  							freeSwapMemory = Float.parseFloat(swap1.trim());
		  							usedSwapMemory = Float.parseFloat(swap2.trim());
		  							SwapMemCap = freeSwapMemory + usedSwapMemory;
		  							//SysLogger.info(SwapMemCap+"===="+freeSwapMemory+"===="+usedSwapMemory);
		  						}catch(Exception e){
		  							e.printStackTrace();
		  						}
		  					}
		  				}
		  				if(SwapMemCap > 0){		
			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("Capability");
  			memorydata.setSubentity("SwapMemory");
  			memorydata.setRestype("static");
  			memorydata.setUnit("M");
  			memorydata.setThevalue(SwapMemCap/1024+"");
  			memoryVector.addElement(memorydata);
  			
  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("UsedSize");
  			memorydata.setSubentity("SwapMemory");
  			memorydata.setRestype("static");
  			memorydata.setUnit("M");
  			memorydata.setThevalue(usedSwapMemory/1024+"");
  			memoryVector.addElement(memorydata);
			
  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("Utilization");
  			memorydata.setSubentity("SwapMemory");
  			memorydata.setRestype("dynamic");
  			memorydata.setUnit("%");
  			memorydata.setThevalue(Math.round(usedSwapMemory*100/SwapMemCap)+"");
  			memoryVector.addElement(memorydata);
		  				}
	
		  			}catch(Exception e){
	  					e.printStackTrace();
	  				}	
		//　----------------解析process内容--创建监控项---------------------        	
		String processContent = "";
		
		try
		{
			processContent = telnet.send("ps -eo pid,user,pcpu,pmem,time,rss,comm");
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		
		List procslist = new ArrayList();
		ProcsDao procsdaor=new ProcsDao();
		try{
			procslist = procsdaor.loadByIp(ipaddress);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			procsdaor.close();
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
		String[] process_LineArr = processContent.split("\n");
		String[] processtmpData = null;
		float cpuusage = 0.0f;
		for(int i=1; i<process_LineArr.length;i++)
		{    			
			processtmpData = process_LineArr[i].trim().split("\\s++");
			
			if((processtmpData != null) && (processtmpData.length >= 7)){
				String pid=processtmpData[0];//pid
				if("pid".equalsIgnoreCase(pid))continue;
				String cmd = "";
				
				for(int k=6;k<processtmpData.length;k++)
				{
					if(k == processtmpData.length - 1)
					{
						cmd += processtmpData[k] + " ";
					}
					else
					{
						cmd += processtmpData[k];
					}
				}
				
				cmd = cmd.trim();//command
				
				System.out.println("cmd = " + cmd + " , real cmd = " + processtmpData[6]);
				
				String vbstring1=processtmpData[6];//command
				String vbstring2="应用程序";
				String vbstring3="正在运行";
				String vbstring4=processtmpData[5];//memsize
				if (vbstring4 == null)vbstring4="0";
				String vbstring5=processtmpData[4];//cputime
				if(vbstring5.contains("-")){
					vbstring5 = "0.0";
				}
				String vbstring6=processtmpData[3];//%mem
				if(vbstring6.contains("-")){
					vbstring6 = "0.0";
				}
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("MemoryUtilization");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit("%");
				processdata.setThevalue(vbstring6);
				processVector.addElement(processdata);	
		
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Memory");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit("K");
				processdata.setThevalue(vbstring4);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Type");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring2);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Status");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring3);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Name");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(cmd);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("CpuTime");
				processdata.setSubentity(pid);
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
				
				
			}	
		}
		//判断ProcsV里还有没有需要监视的进程，若有，则说明当前没有启动该进程，则用命令重新启动该进程，同时写入事件
	     Vector eventtmpV = new Vector();
	     if (procsV !=null && procsV.size()>0){
	     	for(int i=0;i<procsV.size();i++){		     		
	     		Procs procs = (Procs)procshash.get((String)procsV.get(i));	
	     		//Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
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
//					createSMS(procs);		     			
	     		}catch(Exception e){
	     			e.printStackTrace();
	     		}
	     	}
	
    	
		systemdata=new Systemcollectdata();
		systemdata.setIpaddress(ipaddress);
		systemdata.setCollecttime(date);
		systemdata.setCategory("System");
		systemdata.setEntity("ProcessCount");
		systemdata.setSubentity("ProcessCount");
		systemdata.setRestype("static");
		systemdata.setUnit(" ");
		systemdata.setThevalue(process_LineArr+"");
		systemVector.addElement(systemdata);	
	     }
		
		//　----------------解析uname内容--创建监控项---------------------        	
		String unameContent = "";
		try
		{
			unameContent = telnet.send("uname -sn");
		} catch (IOException e1)
		{
			e1.printStackTrace();
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
		
		//　----------------解析usergroup内容--创建监控项--------------------- 
		Hashtable usergrouphash = new Hashtable();
		String usergroupContent = "";
		
		try
		{
			usergroupContent = telnet.send("cat /etc/group");
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		String[] usergroupLineArr = usergroupContent.split("\n");
		String[] usergroup_tmpData = null;
		for(int i=0; i<usergroupLineArr.length;i++){
			usergroup_tmpData = usergroupLineArr[i].split(":");				
			if (usergroup_tmpData.length>=3){	
				usergrouphash.put((String)usergroup_tmpData[2], usergroup_tmpData[0]);
			}				
		}
		
		//　----------------解析user内容--创建监控项---------------------        	
		String userContent = "";
		try
		{
			userContent = telnet.send("cat /etc/passwd");
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		String[] userLineArr = userContent.split("\n");
		String[] user_tmpData = null;
		for(int i=0; i<userLineArr.length;i++){
			String[] result = userLineArr[i].trim().split(":x:");
			if(result.length>=2){
				String userName = result[0];
	
				String group = result[1];
//				String[] groups = groupStr.split("=");
//				String group ="";
//				if(groups != null && groups.length==2){
//					group = groups[1];
//				}
				//String userid = result[1];
				//int usergroupid = Integer.parseInt(result[3]);
				//小于500的为系统级用户,过滤
				//if(userid < 500)continue;
				
				userdata=new Usercollectdata();
				userdata.setIpaddress(ipaddress);
				userdata.setCollecttime(date);
				userdata.setCategory("User");
				userdata.setEntity("Sysuser");
				userdata.setSubentity(group);
				userdata.setRestype("static");
				userdata.setUnit(" ");
				userdata.setThevalue(userName);
				userVector.addElement(userdata);								
			}
		}
		
		//　----------------解析date内容--创建监控项---------------------        	
		String dateContent = "";
		try
		{
			dateContent = telnet.send("date");
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
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

		//　----------------解析uptime内容--创建监控项---------------------        	
		String uptimeContent = "";
		try
		{
			uptimeContent = telnet.send("uptime");
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
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
		
		if (diskVector != null && diskVector.size()>0)
		{
		returnHash.put("disk",diskVector);
		
		 //把采集结果生成sql
	    HostdiskResultosql tosql=new HostdiskResultosql();
	    tosql.CreateResultTosql(returnHash, host.getIpAddress());

	    HostDatatempDiskRttosql temptosql=new HostDatatempDiskRttosql();
	    temptosql.CreateResultTosql(returnHash, host);
	    tosql=null;
	    temptosql=null;
		}
	if (cpuVector != null && cpuVector.size()>0)
		{
		returnHash.put("cpu",cpuVector);
		
		NetHostDatatempCpuRTosql totempsql=new NetHostDatatempCpuRTosql();
		 totempsql.CreateResultTosql(returnHash, host);
		 totempsql=null;
		}
	if (memoryVector != null && memoryVector.size()>0)
		{
		returnHash.put("memory",memoryVector);
		
		//把采集结果生成sql
	    HostPhysicalMemoryResulttosql  tosql=new HostPhysicalMemoryResulttosql();
	    tosql.CreateResultTosql(returnHash, host.getIpAddress());
	    NetHostMemoryRtsql  totempsql=new NetHostMemoryRtsql();
	    totempsql.CreateResultTosql(returnHash, host);
		}
	if (userVector != null && userVector.size()>0)
		{
		returnHash.put("user",userVector);
		
		HostDatatempUserRtosql tosql=new HostDatatempUserRtosql();
		tosql.CreateResultTosql(returnHash, host);
		}
	if (processVector != null && processVector.size()>0)
		{
		returnHash.put("process",processVector);
		
		//把结果生成sql
		HostDatatempProcessRtTosql temptosql=new HostDatatempProcessRtTosql();
		temptosql.CreateResultTosql(returnHash, host);
		}
	if (systemVector != null && systemVector.size()>0)
		{
		returnHash.put("system",systemVector);
		
		NetHostDatatempSystemRttosql tosql=new NetHostDatatempSystemRttosql();
		tosql.CreateResultTosql(returnHash, host);
		}
	if (nodeconfig != null)
		{
		returnHash.put("nodeconfig",nodeconfig);
		
		HostDatatempNodeconfRtosql tosql=new HostDatatempNodeconfRtosql();
		tosql.CreateResultTosql(returnHash, host);
		}
	//if (iflist != null && iflist.size()>0)returnHash.put("iflist",iflist);
	if (utilhdxVector != null && utilhdxVector.size()>0)
		{
		returnHash.put("utilhdx",utilhdxVector);
		
		HostDatatemputilhdxRtosql tosql=new HostDatatemputilhdxRtosql();
		tosql.CreateResultTosql(returnHash, host);
		}
	if (interfaceVector != null && interfaceVector.size()>0)
		{
		returnHash.put("interface",interfaceVector);
		
		HostDatatempinterfaceRtosql tosql=new HostDatatempinterfaceRtosql();
		tosql.CreateResultTosql(returnHash, host);
		}
	if (alldiskperf != null && alldiskperf.size()>0)
		{
		returnHash.put("alldiskperf",alldiskperf);
		
		HostDatatempnDiskperfRtosql tosql=new HostDatatempnDiskperfRtosql();
		tosql.CreateResultTosql(returnHash, host);
		}
	if (cpuconfiglist != null && cpuconfiglist.size()>0)
		{
		returnHash.put("cpuconfiglist",cpuconfiglist);
		
		HostDatatempCpuconfiRtosql tosql=new HostDatatempCpuconfiRtosql();
		tosql.CreateResultTosql(returnHash, host);
		}
	if (cpuperflist != null && cpuperflist.size()>0)
		{
		returnHash.put("cpuperflist",cpuperflist);
		
		HostcpuResultTosql rtosql=new HostcpuResultTosql();
		rtosql.CreateLinuxResultTosql(returnHash, host.getIpAddress());
		
		HostDatatempCpuperRtosql tmptosql=new HostDatatempCpuperRtosql();
		tmptosql.CreateResultTosql(returnHash, host);
		}
//	if (netmedialist != null && netmedialist.size()>0)
//		{
//		returnHash.put("netmedialist",netmedialist);
//		}
//	if (netconflist != null && netconflist.size()>0)
//		{
//		returnHash.put("netconflist",netconflist);
//		
//		
//		}
//	if (netflowlist != null && netflowlist.size()>0)
//	{
//	    returnHash.put("netflowlist",netflowlist);
//	}
	if (interfaceVector != null && interfaceVector.size()>0)
	{
	returnHash.put("interface",interfaceVector);
	HostDatatempinterfaceRtosql tosql=new HostDatatempinterfaceRtosql();
	tosql.CreateResultTosql(returnHash, host);
	
	}
		
		

//		if (diskVector != null && diskVector.size()>0)returnHash.put("disk",diskVector);
//		if (cpuVector != null && cpuVector.size()>0)returnHash.put("cpu",cpuVector);
//		if (memoryVector != null && memoryVector.size()>0)returnHash.put("memory",memoryVector);
//		if (userVector != null && userVector.size()>0)returnHash.put("user",userVector);
//		if (processVector != null && processVector.size()>0)returnHash.put("process",processVector);	
//		if (systemVector != null && systemVector.size()>0)returnHash.put("system",systemVector);	
//		if (nodeconfig != null)returnHash.put("nodeconfig",nodeconfig);
//		//if (iflist != null && iflist.size()>0)returnHash.put("iflist",iflist);
//		if (utilhdxVector != null && utilhdxVector.size()>0)returnHash.put("utilhdx",utilhdxVector);
//		if (interfaceVector != null && interfaceVector.size()>0)returnHash.put("interface",interfaceVector);
//		if (alldiskperf != null && alldiskperf.size()>0)returnHash.put("alldiskperf",alldiskperf);
//		if (cpuconfiglist != null && cpuconfiglist.size()>0)returnHash.put("cpuconfiglist",cpuconfiglist);
//		if (cpuperflist != null && cpuperflist.size()>0)returnHash.put("cpuperflist",cpuperflist);
		return returnHash;
		
		
		
	}
	
	
	
	
	public static void main(String[] args)
	{
		
		TelnetWrapper wrapper = new TelnetWrapper();
		try
		{
			wrapper.connect("172.25.25.5", 23);
			wrapper.login("itims", "itims", "login:", "assword:", "$");
			
			Hashtable params = wrapper.getTelnetMonitorDetail();
			System.out.println("values = " + params);
			
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
		finally
		{
			try
			{
				wrapper.disconnect();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	
	
	
	

}
