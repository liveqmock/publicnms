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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.Arith;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.model.Nodeconfig;
import com.afunms.config.model.Procs;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Systemcollectdata;
//import com.gatherResulttosql.HostCpuperfResultTosql;
import com.gatherResulttosql.HostDatatempDiskRttosql;
import com.gatherResulttosql.HostDatatempNodeconfRtosql;
import com.gatherResulttosql.HostDatatempProcessRtTosql;
import com.gatherResulttosql.HostDatatempnDiskperfRtosql;
import com.gatherResulttosql.HostPhysicalMemoryResulttosql;
import com.gatherResulttosql.HostcpuResultTosql;
import com.gatherResulttosql.HostdiskResultosql;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;
import com.gatherResulttosql.NetHostMemoryRtsql;

/**
 * @author Administrator   
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LoadScoUnixWareFile {       
	/**     
	 * @param hostname         
	 */
	private String ipaddress;
//	private java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public LoadScoUnixWareFile(String ipaddress) {
		this.ipaddress = ipaddress;  
	}
    public LoadScoUnixWareFile() {
		
	}
	
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode)
    {
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		ipaddress=host.getIpAddress();
		//yangjun
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ipaddress);
		if(ipAllData == null)ipAllData = new Hashtable();
		
		Hashtable returnHash = new Hashtable();
		StringBuffer fileContent = new StringBuffer();
		Vector cpuVector=new Vector();
		Vector systemVector=new Vector();
		Vector diskVector=new Vector();
		Vector processVector=new Vector();
		Nodeconfig nodeconfig = new Nodeconfig();
		String collecttime = "";
		
		
		CPUcollectdata cpudata=null;
		Systemcollectdata systemdata=null;
		Processcollectdata processdata=null;
		if(host == null)return null;
		nodeconfig.setNodeid(host.getId());
		nodeconfig.setHostname(host.getAlias());
		float PhysicalMemCap = 0;
		float freePhysicalMemory =0; 
		float SwapMemCap = 0;
		float freeSwapMemory =0;
		float usedSwapMemory =0;
		
    	try 
		{
			String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+ipaddress+".log";				
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);
			String strLine = null;
    		//读入文件内容
    		while((strLine=br.readLine())!=null)
    		{
    			fileContent.append(strLine + "\n");
    			//SysLogger.info(strLine);
    		}
    		isr.close();
    		fis.close();
    		br.close();
    		try{
//    			copyFile(ipaddress,getMaxNum(ipaddress));
    		}catch(Exception e){
    			e.printStackTrace();
    		}
		} 
    	catch (Exception e)
		{
			e.printStackTrace();
		}

    	Pattern tmpPt = null;
    	Matcher mr = null;
    	Calendar date = Calendar.getInstance();
    	
	    //----------------解析version内容--创建监控项---------------------        	
		String versionContent = "";
		tmpPt = Pattern.compile("(cmdbegin:version)(.*)(cmdbegin:swap)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			versionContent = mr.group(2);
//			System.out.println("================================version=====");
//			System.out.println(versionContent);
//			System.out.println("================================version===end==");
		}
		String[] versionLineArr = versionContent.split("\n");
		if (versionLineArr != null && versionLineArr.length >0){
			for(int i=1; i<versionLineArr.length;i++){
				String[] vstr = versionLineArr[i].split("=");
				if(vstr[0]!=null&&"version".equalsIgnoreCase(vstr[0])){
					nodeconfig.setCSDVersion(vstr[1]);
				}
				if(vstr[0]!=null&&"hostname".equalsIgnoreCase(vstr[0])){
					nodeconfig.setHostname(vstr[1]);
				}
				if(vstr[0]!=null&&"sysname".equalsIgnoreCase(vstr[0])){
					nodeconfig.setSysname(vstr[1]);
				}
				if(vstr[0]!=null&&"hw_serial".equalsIgnoreCase(vstr[0])){
					nodeconfig.setSerialNumber(vstr[1]);
				}
			}
		}
		else
		{
			nodeconfig.setCSDVersion("");
			nodeconfig.setHostname("");
			nodeconfig.setSysname("");
			nodeconfig.setSerialNumber("");
		}
		nodeconfig.setNumberOfProcessors("1");
		nodeconfig.setMac("");
		//　----------------解析swap内容--创建监控项---------------------        	
		String swap_Content = "";
		tmpPt = Pattern.compile("(cmdbegin:swap)(.*)(cmdbegin:process)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			try{
				swap_Content = mr.group(2);
				
//				System.out.println("================================swap=====");
//				System.out.println(swap_Content);
//				System.out.println("================================swap===end==");
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		} 
		String[] swap_LineArr = null;
		String[] swap_tmpData = null;
		try{
			swap_LineArr = swap_Content.trim().split("\n");
			if(swap_LineArr != null && swap_LineArr.length>0){
				swap_tmpData = swap_LineArr[0].trim().split("\\s++");	
				if(swap_tmpData != null && swap_tmpData.length==12){
					
					try{
//						System.out.println("==============================解析成功swap");
						SwapMemCap = Float.parseFloat(swap_tmpData[4]);
						freeSwapMemory = Float.parseFloat(swap_tmpData[10]);
						usedSwapMemory = Float.parseFloat(swap_tmpData[7]);
						SysLogger.info(SwapMemCap+"===="+freeSwapMemory+"===="+usedSwapMemory);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//　----------------解析disk内容--创建监控项---------------------
		String diskContent = "";
		String diskLabel;
		List disklist = new ArrayList();
		tmpPt = Pattern.compile("(cmdbegin:disk\n)(.*)(cmdbegin:diskperf)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			diskContent = mr.group(2);
//			System.out.println("==================(cmdbegin:disk\n)(.*)(cmdbegin:diskperf)======================");
//			System.out.println(diskContent);
//			System.out.println("==================(cmdbegin:disk\n)(.*)(cmdbegin:diskperf)======================");
			
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

				//yangjun 
				try {
					String diskinc = "0.0";
					float pastutil = 0.0f;
					Vector disk_v = (Vector)ipAllData.get("disk");
					if (disk_v != null && disk_v.size() > 0) {
						for (int si = 0; si < disk_v.size(); si++) {
							Diskcollectdata disk_data = (Diskcollectdata) disk_v.elementAt(si);
							if((tmpData[5]).equals(disk_data.getSubentity())&&"Utilization".equals(disk_data.getEntity())){
								pastutil = Float.parseFloat(disk_data.getThevalue());
							}
						}
					} else {
						pastutil = Float.parseFloat(tmpData[4].substring(0,tmpData[4].indexOf("%")));
					}
					if (pastutil == 0) {
						pastutil = Float.parseFloat(
								tmpData[4].substring(
										0,
										tmpData[4].indexOf("%")));
					}
					if(Float.parseFloat(
									tmpData[4].substring(
									0,
									tmpData[4].indexOf("%")))-pastutil>0){
						diskinc = (Float.parseFloat(
										tmpData[4].substring(
										0,
										tmpData[4].indexOf("%")))-pastutil)+"";
					}
					diskdata = new Diskcollectdata();
					diskdata.setIpaddress(host.getIpAddress());
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("UtilizationInc");// 利用增长率百分比
					diskdata.setSubentity(tmpData[5]);
					diskdata.setRestype("dynamic");
					diskdata.setUnit("%");
					diskdata.setThevalue(diskinc);
					diskVector.addElement(diskdata);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//
				
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
				if(allsize>=1024.0f){
					allsize=allsize/(1024*1024);
					diskdata.setUnit("G");
				}
				else{
					allsize=allsize/1024;
					diskdata.setUnit("M");
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

				float FreeintSize=0;
				FreeintSize=Float.parseFloat(tmpData[3]);
				
				
				float usedfloatsize=0.0f;
				usedfloatsize = allblocksize - FreeintSize;
				if(usedfloatsize>=1024.0f){
					usedfloatsize=usedfloatsize/(1024*1024);
					diskdata.setUnit("G");
				}
				else{
					usedfloatsize=usedfloatsize/1024;
					diskdata.setUnit("M");
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
		tmpPt = Pattern.compile("(cmdbegin:diskperf)(.*)(cmdbegin:netperf)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			diskperfContent = mr.group(2);
			
		} 
		String[] diskperfLineArr = diskperfContent.split("\n");
		String[] diskperf_tmpData = null;
		List alldiskperf = new ArrayList();
		Hashtable<String,String> diskperfhash = new Hashtable<String,String>();
		int flag = 0;
		for(int i=0; i<diskperfLineArr.length;i++){
			diskperf_tmpData = diskperfLineArr[i].trim().split("\\s++");
			if(diskperf_tmpData != null && (diskperf_tmpData.length==8 || diskperf_tmpData.length==9)){
				if(diskperf_tmpData[0].trim().equalsIgnoreCase("Average")){
					flag = 1;
					diskperfhash.put("%busy", diskperf_tmpData[3].trim());
					diskperfhash.put("avque", diskperf_tmpData[4].trim());
					diskperfhash.put("r+w/s", diskperf_tmpData[5].trim());
					diskperfhash.put("Kbs/s", diskperf_tmpData[6].trim());
					diskperfhash.put("avwait", diskperf_tmpData[7].trim());
					diskperfhash.put("avserv", diskperf_tmpData[8].trim());
					diskperfhash.put("disklebel", diskperf_tmpData[1].trim());
					alldiskperf.add(diskperfhash);
				}else if(flag == 1){
					diskperfhash.put("%busy", diskperf_tmpData[2].trim());
					diskperfhash.put("avque", diskperf_tmpData[3].trim());
					diskperfhash.put("r+w/s", diskperf_tmpData[4].trim());
					diskperfhash.put("Kbs/s", diskperf_tmpData[5].trim());
					diskperfhash.put("avwait", diskperf_tmpData[6].trim());
					diskperfhash.put("avserv", diskperf_tmpData[7].trim());
					diskperfhash.put("disklebel", diskperf_tmpData[0].trim());
					alldiskperf.add(diskperfhash);
				}
				
				diskperfhash = new Hashtable();
			}				
		}
		//----------------解析netperf内容--创建监控项---------------------        	
		String netperfContent = "";
		tmpPt = Pattern.compile("(cmdbegin:netperf)(.*)(cmdbegin:netallperf)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			netperfContent = mr.group(2);
			
//			System.out.println("==================(cmdbegin:netperf)(.*)(cmdbegin:netallperf)==============");
//			System.out.println(netperfContent);
//			System.out.println("==================(cmdbegin:netperf)(.*)(cmdbegin:netallperf)==============");
		} 
		String[] netperfLineArr = netperfContent.split("\n");
		String[] netperf_tmpData = null;
		List netperf = new ArrayList();
		//Hashtable<String,String> netnamehash = new Hashtable<String,String>();
		//int flag = 0;
		for(int i=0; i<netperfLineArr.length;i++){
			netperf_tmpData = netperfLineArr[i].trim().split("\\s++");
			//System.out.println("=============================长度==="+netperf_tmpData.length);
			if(netperf_tmpData != null && (netperf_tmpData.length==9||netperf_tmpData.length==10)){
				if(netperf_tmpData[0].trim().indexOf("net")>=0){
					
					System.out.println("="+netperf_tmpData[0].trim());
					netperf.add(netperf_tmpData[0].trim());
				}
			}
		}

		//----------------解析cpu内容--创建监控项---------------------        	
		String cpuperfContent = "";
		//String average = "";
		tmpPt = Pattern.compile("(cmdbegin:cpu)(.*)(cmdbegin:allconfig)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			cpuperfContent = mr.group(2);
//			System.out.println("===============cpu=================");
//			System.out.println(cpuperfContent);
//			System.out.println("===============cpu=================");
			
			
		} 
		String[] cpuperfLineArr = cpuperfContent.split("\n");
		List cpuperflist = new ArrayList();
		Hashtable<String,String> cpuperfhash = new Hashtable<String,String>();
		for(int i=0; i<cpuperfLineArr.length;i++){
			diskperf_tmpData = cpuperfLineArr[i].trim().split("\\s++");
			if(diskperf_tmpData != null && (diskperf_tmpData.length ==5 || diskperf_tmpData.length==6 || diskperf_tmpData.length==7)){
				
				
				if(diskperf_tmpData[0].trim().equalsIgnoreCase("Average")){
						cpuperfhash.put("%usr", diskperf_tmpData[1].trim());
						cpuperfhash.put("%sys", diskperf_tmpData[2].trim());
						cpuperfhash.put("%wio", diskperf_tmpData[3].trim());
						cpuperfhash.put("%idle", diskperf_tmpData[4].trim());
						if(diskperf_tmpData.length==6||diskperf_tmpData.length==7)
						{
						cpuperfhash.put("%intr", diskperf_tmpData[5].trim());
						}
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
		//将memory数据写进去
		//物理内存计算存在一点问题
		Vector memoryVector=new Vector();
		Memorycollectdata memorydata=null;
		if(PhysicalMemCap > 0){
			//usedPhyPagesSize这个是内存使用率
			//freePhysicalMemory 是在虚拟内存中有，物理内存中没有
			//计算内存使用率
			//System.out.println("============空闲物理内存========================"+freePhysicalMemory);
			float PhysicalMemUtilization =(PhysicalMemCap-freePhysicalMemory)* 100/ PhysicalMemCap;
			//System.out.println("============使用率=2======================="+PhysicalMemUtilization);
			
			//物理总内存大小
			memorydata=new Memorycollectdata();
				memorydata.setIpaddress(ipaddress);
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("Capability");
				memorydata.setSubentity("PhysicalMemory");
				memorydata.setRestype("static");
				memorydata.setUnit("M");
				memorydata.setThevalue(Float.toString(PhysicalMemCap));
				memoryVector.addElement(memorydata);
				//已经用的物理内存
				memorydata=new Memorycollectdata();
				memorydata.setIpaddress(ipaddress);
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("UsedSize");
				memorydata.setSubentity("PhysicalMemory");
				memorydata.setRestype("static");
				memorydata.setUnit("M");
				memorydata.setThevalue(
					//Float.toString(PhysicalMemCap*(1-usedPhyPagesSize/100)));
					Float.toString(PhysicalMemCap-freePhysicalMemory));
				memoryVector.addElement(memorydata);
				//内存使用率
				memorydata=new Memorycollectdata();
				memorydata.setIpaddress(ipaddress);
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("Utilization");
				memorydata.setSubentity("PhysicalMemory");
				memorydata.setRestype("dynamic");
				memorydata.setUnit("%");
				memorydata.setThevalue(Math.round(PhysicalMemUtilization)+"");
				//memorydata.setThevalue(Math.round(usedPhyPagesSize)+"");
				memoryVector.addElement(memorydata);
		}
		if(SwapMemCap > 0){
			//Swap
  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("Capability");
  			memorydata.setSubentity("SwapMemory");
  			memorydata.setRestype("static");
  			memorydata.setUnit("M");
  			//一个BLOCK是512byte
  			//交换分区使用大小
  			memorydata.setThevalue(Math.round(SwapMemCap*4/1024)+"");
  			memoryVector.addElement(memorydata);
  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("UsedSize");
  			memorydata.setSubentity("SwapMemory");
  			memorydata.setRestype("static");
  			memorydata.setUnit("M");
  			memorydata.setThevalue(Math.round(usedSwapMemory*4/1024)+"");
  			memoryVector.addElement(memorydata);
			//交换分区使用率
  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("Utilization");
  			memorydata.setSubentity("SwapMemory");
  			memorydata.setRestype("dynamic");
  			memorydata.setUnit("%");
  			memorydata.setThevalue(Math.round(usedSwapMemory*100/SwapMemCap)+"");
  			//System.out.println("使用大小="+usedSwapMemory);
  			//System.out.println("总大小="+SwapMemCap);
  			//System.out.println("交换分区使用率  "+Math.round(usedSwapMemory*100/SwapMemCap)+"");
  			memoryVector.addElement(memorydata);
		}
		
		//　----------------解析process内容--创建监控项---------------------        	
		String processContent = "";
		tmpPt = Pattern.compile("(cmdbegin:process)(.*)(cmdbegin:cpu)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			processContent = mr.group(2);
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
			//System.out.println("processtmpData.length==="+processtmpData.length);
			if((processtmpData != null) && (processtmpData.length >= 10)){
				
				//SysLogger.info(processtmpData[0]+"-----------------");
				String USER=processtmpData[0];//USER
				String pid=processtmpData[1];//pid
				if("UID".equalsIgnoreCase(USER))continue;
				String cmd = processtmpData[9];
				String vbstring8 = processtmpData[6];
				String vbstring5=processtmpData[8];//cputime
				if(processtmpData.length >= 11){
					if(processtmpData[9].indexOf(":")!=-1){
						cmd = processtmpData[10];
						vbstring8 = processtmpData[6]+processtmpData[7];//STIME
						vbstring5=processtmpData[9];//cputime
					}
				}
				String vbstring2="应用程序";
				String vbstring3="";
				String vbstring4=processtmpData[4];//memsize
				if (vbstring4 == null)vbstring4="0";
				String vbstring6=processtmpData[5];//%mem
				String vbstring7=processtmpData[5];//%CPU
				String vbstring9=processtmpData[7];//STAT
				if("Z".equals(vbstring9)){
					vbstring3="僵尸进程";
				} else {
					vbstring3="正在运行";
				}
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("process_id");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit(" ");
				processdata.setThevalue(pid);
				processVector.addElement(processdata);	
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("USER");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit(" ");
				processdata.setThevalue(USER);
				processVector.addElement(processdata);	
				
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
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("StartTime");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring8);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("CpuUtilization");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit("%");
				processdata.setThevalue(vbstring7);
				processVector.addElement(processdata);
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
		systemdata.setThevalue(process_LineArr.length+"");
		systemVector.addElement(systemdata);	
		
		//　----------------解析uname内容--创建监控项---------------------        	
		String unameContent = "";
		tmpPt = Pattern.compile("(cmdbegin:uname)(.*)(cmdbegin:usergroup)",Pattern.DOTALL);
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
		
		//　----------------解析usergroup内容--创建监控项--------------------- 
		Hashtable usergrouphash = new Hashtable();
		String usergroupContent = "";
		tmpPt = Pattern.compile("(cmdbegin:usergroup)(.*)(cmdbegin:date)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			usergroupContent = mr.group(2);
		} 
		String[] usergroupLineArr = usergroupContent.split("\n");
		String[] usergroup_tmpData = null;
		for(int i=0; i<usergroupLineArr.length;i++){
			usergroup_tmpData = usergroupLineArr[i].split(":");				
			if (usergroup_tmpData.length>=3){	
				usergrouphash.put((String)usergroup_tmpData[2], usergroup_tmpData[0]);
			}				
		}
		
		
		//　----------------解析date内容--创建监控项---------------------        	
		String dateContent = "";
		tmpPt = Pattern.compile("(cmdbegin:date)(.*)(cmdbegin:uptime)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			dateContent = mr.group(2);
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
		tmpPt = Pattern.compile("(cmdbegin:uptime)(.*)(cmdbegin:end)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			uptimeContent = mr.group(2);
		}
		if (uptimeContent != null && uptimeContent.length()>0){
			systemdata=new Systemcollectdata();
			systemdata.setIpaddress(ipaddress);
			systemdata.setCollecttime(date);
			systemdata.setCategory("System");
			systemdata.setEntity("sysUpTime");
			systemdata.setSubentity("sysUpTime");
			systemdata.setRestype("static");
			systemdata.setUnit(" ");
			systemdata.setThevalue(uptimeContent.trim());
			systemVector.addElement(systemdata);
		}

		try{
			deleteFile(ipaddress);
		}catch(Exception e){
			e.printStackTrace();
		}
		if (diskVector != null && diskVector.size()>0)
		{//磁盘使用率
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
		{//cpu
		 returnHash.put("cpu",cpuVector);
		
		 HostcpuResultTosql restosql=new HostcpuResultTosql();
		 restosql.CreateResultTosql(returnHash, host.getIpAddress());
		 NetHostDatatempCpuRTosql totempsql=new NetHostDatatempCpuRTosql();
		 totempsql.CreateResultTosql(returnHash, host);
		 totempsql=null;
		 restosql=null;
		    
		
		}
	    if (memoryVector != null && memoryVector.size()>0)
		{
		returnHash.put("memory",memoryVector);
		//把采集结果生成sql
	    HostPhysicalMemoryResulttosql  tosql=new HostPhysicalMemoryResulttosql();
	    tosql.CreateResultTosql(returnHash, host.getIpAddress());
	    NetHostMemoryRtsql  totempsql=new NetHostMemoryRtsql();
	    totempsql.CreateResultTosql(returnHash, host);
	    totempsql=null;
	    tosql=null;
		
		}
	    if (processVector != null && processVector.size()>0)
		{
		returnHash.put("process",processVector);
		
		//把结果生成sql
		HostDatatempProcessRtTosql temptosql=new HostDatatempProcessRtTosql();
		temptosql.CreateResultTosql(returnHash, host);
		temptosql=null;
		}
	    if (systemVector != null && systemVector.size()>0)
		{//系统信息
		returnHash.put("system",systemVector);
		NetHostDatatempSystemRttosql tosql=new NetHostDatatempSystemRttosql();
		tosql.CreateResultTosql(returnHash, host);
		tosql=null;
		}	
	    if (nodeconfig != null)
		{
		returnHash.put("nodeconfig",nodeconfig);
		
		HostDatatempNodeconfRtosql tosql=new HostDatatempNodeconfRtosql();
		tosql.CreateResultTosql(returnHash, host);
		tosql=null;
		}
	    if (alldiskperf != null && alldiskperf.size()>0)
		{
		returnHash.put("alldiskperf",alldiskperf);
		HostDatatempnDiskperfRtosql tosql=new HostDatatempnDiskperfRtosql();
		tosql.CreateResultTosql(returnHash, host);
		tosql=null;
		
		}
	    if (cpuperflist != null && cpuperflist.size()>0)
		{
		returnHash.put("cpuperflist",cpuperflist);
		
//		HostCpuperfResultTosql tmptosql=new HostCpuperfResultTosql();
//		tmptosql.CreateResultTosql(returnHash, host.getIpAddress());
		
		}
		returnHash.put("collecttime",collecttime);
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
//		Date date2 = new Date();
		if(returnHash!=null&&returnHash.size()>0){
			try {
				updateScoUnixData(nodeDTO, returnHash);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//if (! "1".equals(PollingEngine.getCollectwebflag())) {
		 ShareData.getSharedata().put(host.getIpAddress(), returnHash);
		return returnHash;
    }	
	public void updateScoUnixData(NodeDTO nodeDTO, Hashtable hashtable){
    	Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeDTO.getId());
    	AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
    	List list = alarmIndicatorsUtil.getAlarmIndicatorsForNode(nodeDTO.getId()+"", nodeDTO.getType(), nodeDTO.getSubtype());
    	if(list == null || list.size() ==0){
    		SysLogger.info("无告警指标 不告警=======================");
    		return;
    	} 
    	CheckEventUtil checkEventUtil = new CheckEventUtil();
    	for(int i = 0 ; i < list.size(); i++){
    		
    		try {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
				if("file".equalsIgnoreCase(alarmIndicatorsNode.getName())){
					String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+host.getIpAddress()+".log";
					if(filename!=null){
						File file=new File(filename);
						long lasttime = file.lastModified();
						long size = file.length();
						Date date = new Date(lasttime);
						Date date2 = new Date();
						long btmes = (date2.getTime()-date.getTime())/1000;
//						if(!file.exists()||btmes > 3600){
//							
//						}
						if(file.exists()){
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, btmes+"");
						} else {
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, "999999");
						}
					}
				} else if("cpu".equalsIgnoreCase(alarmIndicatorsNode.getName())){
					Vector cpuVector = (Vector)hashtable.get("cpu");
					if(cpuVector!=null){
						for(int k = 0 ; k < cpuVector.size();k++){
							CPUcollectdata cpudata= (CPUcollectdata)cpuVector.get(k);
							if("Utilization".equalsIgnoreCase(cpudata.getEntity())&&"Utilization".equals(cpudata.getSubentity())){
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, cpudata.getThevalue());
							}
						}
					}
				} else if ("diskperc".equalsIgnoreCase(alarmIndicatorsNode.getName())){
					Vector diskVector = (Vector)hashtable.get("disk");
					if(diskVector!=null){
						checkEventUtil.checkDisk(host, diskVector, alarmIndicatorsNode);
					}
				} else if ("diskinc".equalsIgnoreCase(alarmIndicatorsNode.getName())){
         			Vector diskVector = (Vector)hashtable.get("disk");
         			if(diskVector!=null){
         				checkEventUtil.checkDisk(host, diskVector, alarmIndicatorsNode);
         			}
				} else if ("process".equalsIgnoreCase(alarmIndicatorsNode.getName())){
					Vector processVector = (Vector)hashtable.get("process");
					if(processVector!=null){
						checkEventUtil.createProcessGroupEventList(nodeDTO.getIpaddress(), processVector, alarmIndicatorsNode);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
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
			//delFile.delete();
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
	 
	 public static void main(String[] args){
		 java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 String filename = "E:/MyWork/Tomcat5.0/webapps/afunms/linuxserver/200.1.1.218.log";				
		File file=new File(filename);
		long lasttime = file.lastModified();
		long size = file.length();
		Date date = new Date(lasttime);
		Date date2 = new Date();
		String times = sdf.format(date);
		long btmes = (date2.getTime()-date.getTime())/1000;
		System.out.println(btmes);
		System.out.println(size/1024);
	 }
}






