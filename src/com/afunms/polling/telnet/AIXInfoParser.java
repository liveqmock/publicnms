package com.afunms.polling.telnet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.Arith;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ReadErrptlog;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.model.Errptlog;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.config.model.Nodeconfig;
import com.afunms.config.model.Nodecpuconfig;
import com.afunms.config.model.Procs;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.om.Usercollectdata;
import com.afunms.polling.om.UtilHdx;
import com.gatherResulttosql.HostDatatempCpuconfiRtosql;
import com.gatherResulttosql.HostDatatempCpuperRtosql;
import com.gatherResulttosql.HostDatatempDiskPeriofRtosql;
import com.gatherResulttosql.HostDatatempDiskRttosql;
import com.gatherResulttosql.HostDatatempErrptRtosql;
import com.gatherResulttosql.HostDatatempNodeconfRtosql;
import com.gatherResulttosql.HostDatatempPageRtosql;
import com.gatherResulttosql.HostDatatempPagingRtosql;
import com.gatherResulttosql.HostDatatempProcessRtTosql;
import com.gatherResulttosql.HostDatatempRuteRtosql;
import com.gatherResulttosql.HostDatatempUserRtosql;
import com.gatherResulttosql.HostDatatempVolumeRtosql;
import com.gatherResulttosql.HostDatatempiflistRtosql;
import com.gatherResulttosql.HostDatatempinterfaceRtosql;
import com.gatherResulttosql.HostDatatempnDiskperfRtosql;
import com.gatherResulttosql.HostDatatempserciceRttosql;
import com.gatherResulttosql.HostDatatemputilhdxRtosql;
import com.gatherResulttosql.HostPagingResultTosql;
import com.gatherResulttosql.HostPhysicalMemoryResulttosql;
import com.gatherResulttosql.HostcpuResultTosql;
import com.gatherResulttosql.HostdiskResultosql;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;
import com.gatherResulttosql.NetHostMemoryRtsql;

public class AIXInfoParser
{

	public static void main(String[] args)
	{
		
		String cpuperfContent  = "AIX appserv 3 5 0006B30FD600    04/13/10\n"+

"System configuration: lcpu=8  mode=Capped \n\n" +


"12:54:29    %usr    %sys    %wio   %idle   physc\n"+

"12:54:30       0       0       0     100    4.01\n" +
"12:54:31       0       0       0     100    4.00\n"+
"12:54:32       0       0       0     100    4.00\n"+

"\nAverage        0       0       0     100    4.00";
		
		
		String[] cpuperfLineArr = cpuperfContent.split("\n");
		
		String[] diskperf_tmpData = null;
		
		for (int i = 0; i < cpuperfLineArr.length; i++)
		{
			diskperf_tmpData = cpuperfLineArr[i].trim().split("\\s++");
			if (diskperf_tmpData != null && diskperf_tmpData.length == 6)
			{

				Hashtable cpuperfhash = new Hashtable();
				if (diskperf_tmpData[0].trim().equalsIgnoreCase("Average"))
				{
					cpuperfhash.put("%usr", diskperf_tmpData[1].trim());
					cpuperfhash.put("%sys", diskperf_tmpData[2].trim());
					cpuperfhash.put("%wio", diskperf_tmpData[3].trim());
					cpuperfhash.put("%idle", diskperf_tmpData[4].trim());
					cpuperfhash.put("physc", diskperf_tmpData[5].trim());
					
					System.out.println(cpuperfhash);
				}
			}
		}	
	}
	
	
	
	
	public static Hashtable getTelnetMonitorDetail(Wrapper telnet)
	{		
		//yangjun
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(telnet.getHost());
		if(ipAllData == null)ipAllData = new Hashtable();
		
		Hashtable returnHash = new Hashtable();
		StringBuffer fileContent = new StringBuffer();
		Vector cpuVector = new Vector();
		Vector systemVector = new Vector();
		Vector userVector = new Vector();
		Vector diskVector = new Vector();
		Vector processVector = new Vector();
		Nodeconfig nodeconfig = new Nodeconfig();
		Vector interfaceVector = new Vector();
		Vector utilhdxVector = new Vector();
		Vector errptlogVector = new Vector();
		Vector volumeVector=new Vector();
		List routeList=new ArrayList();
		Vector memoryVector = new Vector();
		List iflist = new ArrayList();
		List alldiskperf = new ArrayList();
		List<Nodecpuconfig> cpuconfiglist = new ArrayList<Nodecpuconfig>();
		List netmedialist = new ArrayList();
		List servicelist = new ArrayList();
		List cpuperflist = new ArrayList();

		String ipaddress = telnet.getHost();
		List monitorItemList = telnet.getMonitorItemList();

		CPUcollectdata cpudata = null;
		Systemcollectdata systemdata = null;
		Usercollectdata userdata = null;
		Processcollectdata processdata = null;
		Host host = (Host) PollingEngine.getInstance().getNodeByIP(
				telnet.getHost());
		if (host == null)
			return null;
		nodeconfig.setNodeid(host.getId());
		nodeconfig.setHostname(host.getAlias());
		float PhysicalMemCap = 0;
		float freePhysicalMemory = 0;
		float allPhyPagesSize = 0;
		float usedPhyPagesSize = 0;
		float SwapMemCap = 0;
		float freeSwapMemory = 0;
		float usedSwapMemory = 0;
		Hashtable pagehash = new Hashtable();
		Hashtable paginghash = new Hashtable();

		Hashtable networkconfig = new Hashtable();

		Calendar date = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");							
		Date cc = date.getTime();
		String collecttime = sdf1.format(cc);
		
		Hashtable gatherHash = new Hashtable();
		if(monitorItemList != null && monitorItemList.size()>0){
			for(int i=0;i<monitorItemList.size();i++){
				NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
				gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators.getName());
			}
		}
		if(gatherHash == null)return null;
		
		if(gatherHash.containsKey("pageingspace")){
			//----------------解析Paging Space内容--创建监控项---------------------        	
			String Paging_Content = "";
			try
			{
				Paging_Content = telnet.send("lsps -s");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			String[] Paging_LineArr = null;
			String[] Paging_tmpData = null;
			
			try{
				Paging_LineArr = Paging_Content.split("\n");
				if(Paging_LineArr!=null&&Paging_LineArr.length>1)
				{    	
					for(int i=0;i<Paging_LineArr.length;i++){
						String tempstr = Paging_LineArr[i];
						if(tempstr != null){
							if(tempstr.contains("%")){
								Paging_tmpData = tempstr.trim().split("\\s++");          			
								if(Paging_tmpData != null)
								{							
									String Total_Paging_Space = Paging_tmpData[0];
									String Percent_Used = Paging_tmpData[1];
									paginghash.put("Total_Paging_Space", Total_Paging_Space);
									paginghash.put("Percent_Used", Percent_Used);
									
									//SysLogger.info("Total_Paging_Space:"+Total_Paging_Space+"====Percent_Used:"+Percent_Used);
									Hashtable collectHash = new Hashtable();
									collectHash.put("pagingusage", paginghash);
								    try{
										AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
										List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix","pagingusage");
										for(int k = 0 ; k < list.size() ; k ++){
											AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(k);
											//对换页率进行告警检测
											CheckEventUtil checkutil = new CheckEventUtil();
											checkutil.updateData(host,collectHash,"host","aix",alarmIndicatorsnode);
											//}
										}
								    }catch(Exception e){
								    	e.printStackTrace();
								    }
								    
									//总换页
									try
									{
										Total_Paging_Space = Total_Paging_Space.replaceAll("MB", "");
										allPhyPagesSize = Float.parseFloat(Total_Paging_Space);
									} catch (Exception e)
									{
										e.printStackTrace();
									}
									//已使用率
									try
									{
										Percent_Used = Percent_Used.replaceAll("%", "");
										usedPhyPagesSize = Float.parseFloat(Percent_Used);
									} catch (Exception e)
									{
										e.printStackTrace();
									}
								}
							}
						}
						
					}

				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		

		if(gatherHash.containsKey("swapmemory")){
			// ----------------解析swap内容--创建监控项---------------------
			String swap_Content = "";
			try
			{
				swap_Content = telnet.send("swap -l");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			String[] swap_LineArr = null;
			String[] swap_tmpData = null;
			try
			{
				swap_LineArr = swap_Content.trim().split("\n");
				if (swap_LineArr != null && swap_LineArr.length > 0)
				{
					for(int i=0;i<swap_LineArr.length;i++){
						String temp = swap_LineArr[i].trim();
						if(temp.contains("/")){
							swap_tmpData = temp.trim().split("\\s++");
							if (swap_tmpData != null && swap_tmpData.length == 5)
							{
								try
								{
									// System.out.println("==============================解析成功swap");
									SwapMemCap = Float.parseFloat(swap_tmpData[3].replace("MB", ""));
									freeSwapMemory = Float.parseFloat(swap_tmpData[4].replace("MB", ""));
									usedSwapMemory = SwapMemCap-freeSwapMemory;
									//SysLogger.info(SwapMemCap+"===="+freeSwapMemory+"===="+usedSwapMemory);
								} catch (Exception e)
								{
									e.printStackTrace();
								}
							}
							break;
						}
					}

				}
				// 将memory数据写进去	
				if (SwapMemCap > 0)
				{
					// Swap
					Memorycollectdata memorydata = null;
					memorydata = new Memorycollectdata();
					memorydata.setIpaddress(ipaddress);
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("Capability");
					memorydata.setSubentity("SwapMemory");
					memorydata.setRestype("static");
					memorydata.setUnit("M");
					// 一个BLOCK是512byte
					// 交换分区使用大小
					memorydata.setThevalue(Math.round(SwapMemCap) + "");
					memoryVector.addElement(memorydata);
					memorydata = new Memorycollectdata();
					memorydata.setIpaddress(ipaddress);
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("UsedSize");
					memorydata.setSubentity("SwapMemory");
					memorydata.setRestype("static");
					memorydata.setUnit("M");
					memorydata.setThevalue(Math.round(usedSwapMemory ) + "");
					memoryVector.addElement(memorydata);
					// 交换分区使用率
					memorydata = new Memorycollectdata();
					memorydata.setIpaddress(ipaddress);
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("Utilization");
					memorydata.setSubentity("SwapMemory");
					memorydata.setRestype("dynamic");
					memorydata.setUnit("%");
					memorydata.setThevalue(Math
							.round(usedSwapMemory * 100 / SwapMemCap)
							+ "");
//					System.out.println("使用大小=" + usedSwapMemory);
//					System.out.println("总大小=" + SwapMemCap);
//					System.out.println("交换分区使用率  "+ Math.round(usedSwapMemory * 100 / SwapMemCap) + "");
					memoryVector.addElement(memorydata);
					
		  			Vector swapmemV = new Vector();
		  			swapmemV.add(memorydata);				
				    Hashtable collectHash = new Hashtable();
					collectHash.put("swapmem", swapmemV);				
					//对交换内存值进行告警检测
				    try{
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix","swapmemory");
						for(int i = 0 ; i < list.size() ; i ++){
							AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
							//对交换内存值进行告警检测
							CheckEventUtil checkutil = new CheckEventUtil();
							checkutil.updateData(host,collectHash,"host","aix",alarmIndicatorsnode);
							//}
						}
				    }catch(Exception e){
				    	e.printStackTrace();
				    }
				}


			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		// ----------------解析cpuconfig内容--创建监控项---------------------
		String cpuconfigContent = "";

		try
		{
			cpuconfigContent = telnet.send("prtconf");
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}

		String[] cpuconfigLineArr = cpuconfigContent.split("\n");
		String[] cpuconfig_tmpData = null;
		
		Nodecpuconfig nodecpuconfig = new Nodecpuconfig();
		String procesors = "";
		String processorType = "";
		String processorSpeed = "";
		for (int i = 0; i < cpuconfigLineArr.length; i++)
		{
			String[] result = cpuconfigLineArr[i].trim().split(":");
			if (result.length > 0)
			{
				if (result[0].trim().equalsIgnoreCase("Number Of Processors"))
				{
					// 处理器个数
					// 设置节点的CPU配置个数
					nodeconfig.setNumberOfProcessors(result[1].trim() + "");
				} else if (result[0].trim().equalsIgnoreCase("CPU Type"))
				{
					// CPU数据位
					if (nodeconfig.getNumberOfProcessors() != null
							&& nodeconfig.getNumberOfProcessors().trim()
									.length() > 0)
					{
						int pnum = Integer.parseInt(nodeconfig
								.getNumberOfProcessors());
						for (int k = 0; k < pnum; k++)
						{
							nodecpuconfig.setDataWidth(result[1].trim() + "");
							nodecpuconfig.setProcessorId(k + "");
							nodecpuconfig.setName("");
							nodecpuconfig.setNodeid(host.getId());
							nodecpuconfig.setL2CacheSize("");
							nodecpuconfig.setL2CacheSpeed("");
							nodecpuconfig.setProcessorType(processorType);
							nodecpuconfig.setProcessorSpeed(processorSpeed);
							cpuconfiglist.add(nodecpuconfig);
							nodecpuconfig = new Nodecpuconfig();
						}
					}
				}else if(result[0].trim().equalsIgnoreCase("Processor Type")){
					//CPU类型
					processorType = result[1].trim()+"";
				}else if(result[0].trim().equalsIgnoreCase("Processor Clock Speed")){
					//CPU内核主频
					processorSpeed = result[1].trim()+"";
				} else if (result[0].trim()
						.equalsIgnoreCase("Good Memory Size"))
				{
					String allphy = result[1].trim().trim();
					try
					{
						allphy = allphy.replaceAll("MB", "");
						PhysicalMemCap = Float.parseFloat(allphy);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					// nodecpuconfig.setDataWidth(result[1].trim()+"");
				} else if (result[0].trim().equalsIgnoreCase("IP Address"))
				{
					// IP地址
					networkconfig.put("IP", result[1].trim() + "");
				} else if (result[0].trim().equalsIgnoreCase("Sub Netmask"))
				{
					// 子网掩码
					networkconfig.put("NETMASK", result[1].trim() + "");
				} else if (result[0].trim().equalsIgnoreCase("Gateway"))
				{
					// 网关
					networkconfig.put("GATEWAY", result[1].trim() + "");
				}

			}

		}
		nodecpuconfig = null;
		
		if(gatherHash.containsKey("systemconfig")){			
			// ----------------解析uname内容--创建监控项---------------------

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
			for (int i = 0; i < unameLineArr.length; i++)
			{
				uname_tmpData = unameLineArr[i].split("\\s++");
				if (uname_tmpData.length == 2)
				{
					systemdata = new Systemcollectdata();
					systemdata.setIpaddress(ipaddress);
					systemdata.setCollecttime(date);
					systemdata.setCategory("System");
					systemdata.setEntity("operatSystem");
					systemdata.setSubentity("operatSystem");
					systemdata.setRestype("static");
					systemdata.setUnit(" ");
					systemdata.setThevalue(uname_tmpData[0]);
					systemVector.addElement(systemdata);

					systemdata = new Systemcollectdata();
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
			
			// ----------------解析usergroup内容--创建监控项---------------------
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
			for (int i = 0; i < usergroupLineArr.length; i++)
			{
				usergroup_tmpData = usergroupLineArr[i].split(":");
				if (usergroup_tmpData.length >= 3)
				{
					usergrouphash.put((String) usergroup_tmpData[2],
							usergroup_tmpData[0]);
				}
			}

			// ----------------解析user内容--创建监控项---------------------
			String userContent = "";

			try
			{
				userContent = telnet.send("lsuser ALL");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}

			String[] userLineArr = userContent.split("\n");
			String[] user_tmpData = null;
			for (int i = 0; i < userLineArr.length; i++)
			{
				String[] result = userLineArr[i].trim().split("\\s++");
				if (result.length >= 4)
				{
					String userName = result[0];
					String groupStr = result[3];
					String[] groups = groupStr.split("=");
					String group = "";
					if (groups != null && groups.length == 2)
					{
						group = groups[1];
					}
					// String userid = result[1];
					// int usergroupid = Integer.parseInt(result[3]);
					// 小于500的为系统级用户,过滤
					// if(userid < 500)continue;

					userdata = new Usercollectdata();
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

			// ----------------解析date内容--创建监控项---------------------
			String dateContent = "";

			try
			{
				dateContent = telnet.send("date");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			if (dateContent != null && dateContent.length() > 0)
			{
				systemdata = new Systemcollectdata();
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

			// ----------------解析uptime内容--创建监控项---------------------
			String uptimeContent = "";
			try
			{
				uptimeContent = telnet.send("uptime");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			if (uptimeContent != null && uptimeContent.length() > 0)
			{
				//SysLogger.info("===uptime==="+uptimeContent);
				systemdata = new Systemcollectdata();
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
			
			// ----------------解析version内容--创建监控项---------------------
			String versionContent = "";
			try
			{
				versionContent = telnet.send("oslevel -q");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			
			System.out.println("-------------------------------------------------"+versionContent);
			
			String[] oslevel_LineArr = null;
			if (versionContent != null && versionContent.length() > 0)
			{
				oslevel_LineArr = versionContent.trim().split("\n");
				//SysLogger.info(oslevel_LineArr[1]+"====version");
				for(int i=0;i<oslevel_LineArr.length;i++){
					String temp = oslevel_LineArr[i];
					if(temp.contains(".")){
						nodeconfig.setCSDVersion(temp);
						break;
					}
				}
				
			}
		}


		if(gatherHash.containsKey("disk")){
			// ----------------解析disk内容--创建监控项---------------------
			String diskContent = "";
			String diskLabel;
			List disklist = new ArrayList();
			try
			{
				diskContent = telnet.send("df -m");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			String[] diskLineArr = diskContent.split("\n");
			String[] tmpData = null;
			Diskcollectdata diskdata = null;
			int diskflag = 0;
			for (int i = 1; i < diskLineArr.length; i++)
			{

				tmpData = diskLineArr[i].split("\\s++");
				if ((tmpData != null) && (tmpData.length == 7))
				{
					diskLabel = tmpData[6];

					diskdata = new Diskcollectdata();
					diskdata.setIpaddress(ipaddress);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("Utilization");// 利用百分比
					diskdata.setSubentity(tmpData[6]);
					diskdata.setRestype("static");
					diskdata.setUnit("%");
					try
					{
						diskdata.setThevalue(Float.toString(Float
								.parseFloat(tmpData[3].substring(0, tmpData[3]
										.indexOf("%")))));
					} catch (Exception ex)
					{
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
								if((tmpData[6]).equals(disk_data.getSubentity())&&"Utilization".equals(disk_data.getEntity())){
									pastutil = Float.parseFloat(disk_data.getThevalue());
								}
							}
						} else {
							pastutil = Float.parseFloat(tmpData[3].substring(0,tmpData[3].indexOf("%")));
						}
						if (pastutil == 0) {
							pastutil = Float.parseFloat(
									tmpData[3].substring(
											0,
											tmpData[3].indexOf("%")));
						}
						if(Float.parseFloat(
										tmpData[3].substring(
										0,
										tmpData[3].indexOf("%")))-pastutil>0){
							diskinc = (Float.parseFloat(
											tmpData[3].substring(
											0,
											tmpData[3].indexOf("%")))-pastutil)+"";
						}
						diskdata = new Diskcollectdata();
						diskdata.setIpaddress(host.getIpAddress());
						diskdata.setCollecttime(date);
						diskdata.setCategory("Disk");
						diskdata.setEntity("UtilizationInc");// 利用增长率百分比
						diskdata.setSubentity(tmpData[6]);
						diskdata.setRestype("dynamic");
						diskdata.setUnit("%");
						diskdata.setThevalue(diskinc);
						diskVector.addElement(diskdata);
					} catch (Exception e) {
						e.printStackTrace();
					}
					//
					
					
					
					diskdata = new Diskcollectdata();
					diskdata.setIpaddress(ipaddress);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("AllSize");// 总空间
					diskdata.setSubentity(tmpData[6]);
					diskdata.setRestype("static");

					float allblocksize = 0;
					allblocksize = Float.parseFloat(tmpData[1]);
					float allsize = 0.0f;
					allsize = allblocksize;
					if (allsize >= 1024.0f)
					{
						allsize = allsize / 1024;
						diskdata.setUnit("G");
					} else
					{
						diskdata.setUnit("M");
					}

					diskdata.setThevalue(Float.toString(allsize));
					diskVector.addElement(diskdata);

					diskdata = new Diskcollectdata();
					diskdata.setIpaddress(ipaddress);
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("UsedSize");// 使用大小
					diskdata.setSubentity(tmpData[6]);
					diskdata.setRestype("static");

					float FreeintSize = 0;
					FreeintSize = Float.parseFloat(tmpData[2]);

					float usedfloatsize = 0.0f;
					usedfloatsize = allblocksize - FreeintSize;
					if (usedfloatsize >= 1024.0f)
					{
						usedfloatsize = usedfloatsize / 1024;
						diskdata.setUnit("G");
					} else
					{
						diskdata.setUnit("M");
					}
					diskdata.setThevalue(Float.toString(usedfloatsize));
					diskVector.addElement(diskdata);
					disklist.add(diskflag, diskLabel);
					diskflag = diskflag + 1;
				}
			}
			
		    //进行磁盘告警检测
		    //SysLogger.info("### 开始运行检测磁盘是否告警### ... ###");
		    try{
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix");
				for(int i = 0 ; i < list.size() ; i ++){
					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
					//SysLogger.info("alarmIndicatorsnode name ======"+alarmIndicatorsnode.getName());
					if(alarmIndicatorsnode.getName().equalsIgnoreCase("diskperc")){
						CheckEventUtil checkutil = new CheckEventUtil();
					    checkutil.checkDisk(host,diskVector,alarmIndicatorsnode);
					    break;
					}
				}
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		}
		
		if(gatherHash.containsKey("diskio")){
			// ----------------解析diskperf内容--创建监控项---------------------

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
			
			Hashtable<String, String> diskperfhash = new Hashtable<String, String>();
			int flag = 0;
			for (int i = 0; i < diskperfLineArr.length; i++)
			{
				diskperf_tmpData = diskperfLineArr[i].trim().split("\\s++");
				if (diskperf_tmpData != null
						&& (diskperf_tmpData.length == 7 || diskperf_tmpData.length == 8))
				{
					if (diskperf_tmpData[0].trim().equalsIgnoreCase("Average"))
					{
						flag = 1;
						diskperfhash.put("%busy", diskperf_tmpData[2].trim());
						diskperfhash.put("avque", diskperf_tmpData[3].trim());
						diskperfhash.put("r+w/s", diskperf_tmpData[4].trim());
						diskperfhash.put("Kbs/s", diskperf_tmpData[5].trim());
						diskperfhash.put("avwait", diskperf_tmpData[6].trim());
						diskperfhash.put("avserv", diskperf_tmpData[7].trim());
						diskperfhash.put("disklebel", diskperf_tmpData[1].trim());
						alldiskperf.add(diskperfhash);
					} else if (flag == 1)
					{
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
		}
		

		if(gatherHash.containsKey("netperf")){
			// ----------------解析netperf内容--创建监控项---------------------
			String netperfContent = "";

			try
			{
				netperfContent = telnet.send("netstat -ian");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			String[] netperfLineArr = netperfContent.split("\n");
			String[] netperf_tmpData = null;
			List netperf = new ArrayList();
			// Hashtable<String,String> netnamehash = new
			// Hashtable<String,String>();
			// int flag = 0;
			for (int i = 0; i < netperfLineArr.length; i++)
			{
				netperf_tmpData = netperfLineArr[i].trim().split("\\s++");
				// System.out.println("=============================长度==="+netperf_tmpData.length);
				if (netperf_tmpData != null && netperf_tmpData.length == 9)
				{
					if (netperf_tmpData[0].trim().indexOf("en") >= 0
							&& netperf_tmpData[2].trim().indexOf("link") >= 0)
					{
						netperf.add(netperf_tmpData[0].trim());
					}
				} else if (netperf_tmpData != null && netperf_tmpData.length == 10)
				{
					if (netperf_tmpData[0].trim().indexOf("en") >= 0
							&& netperf_tmpData[2].trim().indexOf("link") >= 0)
					{
						netperf.add(netperf_tmpData[0].trim());
					}
				}

			}

			
			// ----------------解析netallperf内容--创建监控项---------------------
			
			List oldiflist = new ArrayList();
			
			Hashtable netmediahash = new Hashtable();
			String netallperfContent = "";

			try
			{
				netallperfContent = "start-en0\n";
				netallperfContent = netallperfContent +telnet.send("entstat -d en0 |egrep 'Hardware Address|Link Status|Media Speed Running|Packets|Bytes'");
				//netallperfContent = netallperfContent +telnet.send("entstat -d en0 |egrep 'Bytes'");
				netallperfContent = netallperfContent +"\nend-en0\n";
				netallperfContent = netallperfContent +"start-en1\n";
				netallperfContent = netallperfContent +telnet.send("entstat -d en1 |egrep 'Hardware Address|Link Status|Media Speed Running|Packets|Bytes'");
				netallperfContent = netallperfContent +"\nend-en1\n";
				netallperfContent = netallperfContent +"start-en2\n";
				netallperfContent = netallperfContent +telnet.send("entstat -d en2 |egrep 'Hardware Address|Link Status|Media Speed Running|Packets|Bytes'");
				netallperfContent = netallperfContent +"\nend-en2\n";
				netallperfContent = netallperfContent +"start-en3\n";
				netallperfContent = netallperfContent +telnet.send("entstat -d en3 |egrep 'Hardware Address|Link Status|Media Speed Running|Packets|Bytes'");
				netallperfContent = netallperfContent +"\nend-en3\n";
				//netallperfContent = telnet.send("entstat -d en0 |egrep 'Bytes|Hardware Address|Link Status|Media Speed Running|Packets|Bytes|Interrupts' |egrep -v 'XOFF|XON';entstat -d en2 |egrep 'Bytes|Hardware Address|Link Status|Media Speed Running|Packets|Bytes|Interrupts' |egrep -v 'XOFF|XON';entstat -d en3 |egrep 'Bytes|Hardware Address|Link Status|Media Speed Running|Packets|Bytes|Interrupts' |egrep -v 'XOFF|XON'");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			//SysLogger.info(netallperfContent);
			String[] netallperfLineArr = netallperfContent.trim().split("\n");
			String[] netallperf_tmpData = null;
			List netalldiskperf = new ArrayList();
			Hashtable<String,String> netallperfhash = new Hashtable<String,String>();
			int macflag = 0;
			String MAC = "";
			//Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			
			if(ipAllData != null){
				oldiflist = (List)ipAllData.get("iflist");
			}
			
			if(netperf != null && netperf.size()>0){
				//System.out.println("-------------------进入解析-----------------------------");
				Interfacecollectdata interfacedata = null;
				
				//开始循环网络接口
				for(int k=0;k<netperf.size();k++){
					
					
					Hashtable ifhash = new Hashtable();
					Hashtable oldifhash = new Hashtable();//用来保存上次采集结果
					if(oldiflist != null && oldiflist.size()>0){
						oldifhash = (Hashtable)oldiflist.get(k);
					}
					
					
					String portDesc = (String)netperf.get(k);//en1
					//SysLogger.info("==========="+portDesc+"===========");
					//int index=0;//用来定位
					
					//通过
					  
					 /**
					  * 由于aix 同一个命令采集的数据出现不同的数据格式需要在做下面的判断
					  * 第一行数据有下面的模式
					  * 模式一 
					  * ETHERNET STATISTICS (en1
					  * netflg="neten"
					  * 模式二：
					  * Hardware Address:
					  * netflg="netmac"
					  * 使用一个 netflg 判断是什么模式
					  * 
					  */
					
					 String netflg="";
					 
//					 if(netallperfContent.indexOf("ETHERNET STATISTICS ("+portDesc)>0){
//						 netflg="neten";
//						 
//					 }
					 int beginindex = 0;
					 for(int i=0;i<netallperfLineArr.length;i++)
					 { 
						 int tempid = i;
						 //SysLogger.info(tempid+"======="+netallperfLineArr[tempid]);
						 if(netallperfLineArr[tempid].indexOf("Hardware Address:")>=0)
						 {
							 netflg="netmac";
							// beginindex = tempid;
							 //index=k*13;
							 break;	 
						 }
						 
					 }
					
					//IDC版本后去mac地址
					 
					 String mideaspeed = "";//网卡带宽
					 String status = "";//网卡状态
					 String Bytes = "";//网卡输入输出字节数
					 String Packets = "";//输入输出数据包
					 String LinkStatus = "";//状态
					 //ETHERNET STATISTICS (en1 模式
					 Pattern tmpPt = null;
				     Matcher mr = null;
					//if(netflg.equals("neten")){
						
						//System.out.println("=====neten=====================neten=====neten=="+portDesc);
						
						tmpPt = Pattern.compile("(start-"+portDesc+")(.*)(end-"+portDesc+")",Pattern.DOTALL);
						mr = tmpPt.matcher(netallperfContent.toString());
						String netenContent="";
						if(mr.find())
						{
							netenContent = mr.group(2);

						} 
						String [] netLineArr=null;
						netLineArr=netenContent.trim().split("\n");
						//SysLogger.info("---"+netenContent.trim());
//						System.out.println("&&&&&&&"+netLineArr[3]);
//				        MAC = netLineArr[3].trim().substring(netLineArr[3].trim().indexOf("Hardware Address:"));  
					try{
						
						for(int i=0;i<netLineArr.length;i++){
						String tempStr = netLineArr[i];
						//SysLogger.info("====="+tempStr);
						if(tempStr.indexOf("Hardware Address:")>=0)
						 {
							 beginindex = i;
							 break;	 
						 }
//						if(tempStr != null && tempStr.length()>0){
//							if(tempStr.indexOf("Hardware Address:")>=0){
//								MAC = tempStr.trim().substring(tempStr.trim().indexOf("Hardware Address:")+17);
//							}else if(tempStr.indexOf("Media Speed Running:")>=0){
//								mideaspeed = tempStr.trim().substring(tempStr.trim().indexOf("Media Speed Running:")+20);
//							}else if(tempStr.indexOf("Link Status :")>=0){
//								status = tempStr.trim().substring(tempStr.trim().indexOf("Link Status :"));
//								LinkStatus=status;
//							}else if(tempStr.startsWith("Packets:")){
//								Packets = tempStr.trim();
//							}else if(tempStr.startsWith("Bytes:")){
//								Bytes = tempStr.trim();
//								//SysLogger.info("---------------"+Bytes);
//							}
//						}
					}
						
						
						//SysLogger.info("===beginindex===="+beginindex);
						MAC = netLineArr[beginindex].trim().substring(netLineArr[beginindex].trim().indexOf("Hardware Address:")+17);
						Packets = netLineArr[beginindex+1].trim();
						Bytes = netLineArr[beginindex+2].trim();
						status = netLineArr[beginindex+9].trim().substring(netLineArr[beginindex+9].trim().indexOf(":"));
						LinkStatus=status;
						mideaspeed = netLineArr[beginindex+10].trim().substring(netLineArr[beginindex+10].trim().indexOf(":"));
						
//						for(int i=0;i<netLineArr.length;i++){
//							String tempStr = netLineArr[i];
//							SysLogger.info("==="+tempStr);
//							if(tempStr != null && tempStr.length()>0){
//								if(tempStr.indexOf("Hardware Address:")>=0){
//									MAC = tempStr.trim().substring(tempStr.trim().indexOf("Hardware Address:")+17);
//								}else if(tempStr.indexOf("Media Speed Running:")>=0){
//									mideaspeed = tempStr.trim().substring(tempStr.trim().indexOf("Media Speed Running:")+20);
//								}else if(tempStr.indexOf("Link Status :")>=0){
//									status = tempStr.trim().substring(tempStr.trim().indexOf("Link Status :"));
//									LinkStatus=status;
//								}else if(tempStr.startsWith("Packets:")){
//									Packets = tempStr.trim();
//								}else if(tempStr.startsWith("Bytes:")){
//									Bytes = tempStr.trim();
//									//SysLogger.info("---------------"+Bytes);
//								}
//							}
//						}
//						mideaspeed = netLineArr[45].trim().substring(netLineArr[45].trim().indexOf("Media Speed Running:")); 
//						status = netLineArr[43].trim().substring(netLineArr[43].trim().indexOf("Link Status :")); 						
//						Packets = netLineArr[8].trim(); 
//						LinkStatus=status;
//						Bytes = netLineArr[9].trim(); 

					 
					}catch(Exception e){
						e.printStackTrace();
					}	
//					SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$MAC==="+MAC);
//					SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$status==="+status);
//					SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$mideaspeed==="+mideaspeed);
					String mac_ = nodeconfig.getMac();
					if(mac_ != null && mac_.trim().length()>0){
						
						//由于网卡过多，这个数值过把界面弄大了
						if(k<3)
						{
							mac_=mac_+","+MAC;
						}
						
						nodeconfig.setMac(mac_);
					}else{
						nodeconfig.setMac(MAC);
					}
				
					
					
					status=	status.replaceAll("Link Status :", "").trim();
					netmediahash.put("desc", portDesc);//描述
					netmediahash.put("speed", mideaspeed);//带宽
					netmediahash.put("mac", MAC);
					netmediahash.put("status", status);//连接状体
					netmedialist.add(netmediahash);
					netmediahash = new Hashtable();	
					
					
					//=================解析数据包==================
					String outPackets ="0";
					String inPackets ="0";
					if(Packets.indexOf("Packets:")>=0)
					{
					String[] packsperf_tmpData = Packets.split("\\s++");
					
					outPackets = packsperf_tmpData[1];//发送的数据包
					inPackets = packsperf_tmpData[3];//接受的数据包
					}
					String oldOutPackets = "0";
					String oldInPackets = "0";
					String endOutPackets = "0";
					String endInPackets = "0";
					if(oldifhash != null && oldifhash.size()>0){
						if(oldifhash.containsKey("outPackets")){
							oldOutPackets = (String)oldifhash.get("outPackets");
						}
						try{
							endOutPackets = (Long.parseLong(outPackets)-Long.parseLong(oldOutPackets))+"";
						}catch(Exception e){
							e.printStackTrace();
						}
						if(oldifhash.containsKey("inPackets")){
							oldInPackets = (String)oldifhash.get("inPackets");
						}
						try{
							endInPackets = (Long.parseLong(inPackets)-Long.parseLong(oldInPackets))+"";
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					String outBytes ="0";
					String inBytes ="0";
					if(Bytes.indexOf("Bytes:")>=0)
					{
					String[] bytes_tmpData = Bytes.split("\\s++");
					outBytes = bytes_tmpData[1];//发送的字节
					inBytes = bytes_tmpData[3];//接受的字节
					
					//===解析字节数=====================
					
					String oldOutBytes = "0";
					String oldInBytes = "0";
					String endOutBytes = "0";
					String endInBytes = "0";
					
					if(oldifhash != null && oldifhash.size()>0){
						if(oldifhash.containsKey("outBytes")){
							oldOutBytes = (String)oldifhash.get("outBytes");
						}
						try{
							endOutBytes = (Long.parseLong(outBytes)-Long.parseLong(oldOutBytes))*8/1024/300+"";
						}catch(Exception e){
							e.printStackTrace();
						}
						if(oldifhash.containsKey("inBytes")){
							oldInBytes = (String)oldifhash.get("inBytes");
						}
						try{
							endInBytes = (Long.parseLong(inBytes)-Long.parseLong(oldInBytes))*8/1024/300+"";
						}catch(Exception e){
							e.printStackTrace();
						}
					}				
					String linkstatus ="";
					//System.out.println("&&&&&&&&&&**************************"+LinkStatus);
					linkstatus=LinkStatus.replaceAll("Link Status :", "").trim();
				    if (linkstatus.equals("Up"))
				    {
				    	//System.out.println("---------- Up");
				    	linkstatus="1";
				    }else if(linkstatus.equals("Down"))
				     {
				    	//System.out.println("---------- Down");
				    	linkstatus="2";
				     }
					//System.out.println("7788877=="+linkstatus);
					//============带宽===============
					String MediaSpeedRunning =mideaspeed;
					String speedunit = "";
					String speedstr = "";
					String mspeed ="0";
					if(MediaSpeedRunning.indexOf(":")>=0)
					{
					String[] speed_tmpData = MediaSpeedRunning.split(":");
					
					
				    mspeed = speed_tmpData[1].trim();
					String[] speed = mspeed.split("\\s++");
					
					if(speed.length>0){
					speedstr = speed[0];
					}else{
					speedstr = "0";
					}
					
					if(speed.length>1){
					speedunit = speed[1];
					}else{
					speedunit = "Mbps";
					}
					}
					
					
					ifhash.put("outPackets", outPackets);
					ifhash.put("inPackets", inPackets);
					ifhash.put("outBytes", outBytes);
					ifhash.put("inBytes", inBytes);
					
					
				   	//端口索引
					interfacedata=new Interfacecollectdata();
					interfacedata.setIpaddress(ipaddress);
					interfacedata.setCollecttime(date);
					interfacedata.setCategory("Interface");
					interfacedata.setEntity("index");
					interfacedata.setSubentity(k+1+"");
					//端口状态不保存，只作为静态数据放到临时表里
					interfacedata.setRestype("static");
					interfacedata.setUnit("");
					interfacedata.setThevalue(k+1+"");
					interfacedata.setChname("端口索引");
					interfaceVector.addElement(interfacedata);
					//端口描述
					interfacedata=new Interfacecollectdata();
					interfacedata.setIpaddress(ipaddress);
					interfacedata.setCollecttime(date);
					interfacedata.setCategory("Interface");
					interfacedata.setEntity("ifDescr");
					interfacedata.setSubentity(k+1+"");
					//端口状态不保存，只作为静态数据放到临时表里
					interfacedata.setRestype("static");
					interfacedata.setUnit("");
					interfacedata.setThevalue(portDesc);
					interfacedata.setChname("端口描述2");
					interfaceVector.addElement(interfacedata);
					//端口带宽
					interfacedata=new Interfacecollectdata();
					interfacedata.setIpaddress(ipaddress);
					interfacedata.setCollecttime(date);
					interfacedata.setCategory("Interface");
					interfacedata.setEntity("ifSpeed");
					interfacedata.setSubentity(k+1+"");
					interfacedata.setRestype("static");
					interfacedata.setUnit(speedunit);
					interfacedata.setThevalue(speedstr);
					interfacedata.setChname("");
					interfaceVector.addElement(interfacedata);
					//当前状态
					interfacedata=new Interfacecollectdata();
					interfacedata.setIpaddress(ipaddress);
					interfacedata.setCollecttime(date);
					interfacedata.setCategory("Interface");
					interfacedata.setEntity("ifOperStatus");
					interfacedata.setSubentity(k+1+"");
					interfacedata.setRestype("static");
					interfacedata.setUnit("");
					interfacedata.setThevalue(linkstatus);
					interfacedata.setChname("当前状态");
					interfaceVector.addElement(interfacedata);
					//当前状态
					interfacedata=new Interfacecollectdata();
					interfacedata.setIpaddress(ipaddress);
					interfacedata.setCollecttime(date);
					interfacedata.setCategory("Interface");
					interfacedata.setEntity("ifOperStatus");
					interfacedata.setSubentity(k+1+"");
					interfacedata.setRestype("static");
					interfacedata.setUnit("");
					interfacedata.setThevalue(1+"");
					interfacedata.setChname("当前状态");
					interfaceVector.addElement(interfacedata);
					//端口入口流速
					UtilHdx utilhdx=new UtilHdx();
					utilhdx.setIpaddress(ipaddress);
					utilhdx.setCollecttime(date);
					utilhdx.setCategory("Interface");
					String chnameBand="";
					utilhdx.setEntity("InBandwidthUtilHdx");
					utilhdx.setThevalue(endInBytes);
					utilhdx.setSubentity(k+1+"");
					utilhdx.setRestype("dynamic");
					utilhdx.setUnit("Kb/秒");	
					utilhdx.setChname(k+1+"端口入口"+"流速");
					utilhdxVector.addElement(utilhdx);
					//端口出口流速
					utilhdx=new UtilHdx();
					utilhdx.setIpaddress(ipaddress);
					utilhdx.setCollecttime(date);
					utilhdx.setCategory("Interface");
					utilhdx.setEntity("OutBandwidthUtilHdx");
					utilhdx.setThevalue(endOutBytes);
					utilhdx.setSubentity(k+1+"");
					utilhdx.setRestype("dynamic");
					utilhdx.setUnit("Kb/秒");	
					utilhdx.setChname(k+1+"端口出口"+"流速");
					utilhdxVector.addElement(utilhdx);
					
				   iflist.add(ifhash);
				   ifhash = new Hashtable();
					
					
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
		}	

		}
		
		if(gatherHash.containsKey("physicalmemory")){
			// 物理内存计算存在一点问题
			// ----------------解析vmstat内容--创建监控项---------------------
			String vmstat_Content = "";
			try
			{
				vmstat_Content = telnet.send("vmstat");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			String[] vmstat_LineArr = null;
			String[] vmstat_tmpData = null;

			try
			{
				vmstat_LineArr = vmstat_Content.split("\n");

				for (int i = 1; i < vmstat_LineArr.length; i++)
				{
					vmstat_tmpData = vmstat_LineArr[i].trim().split("\\s++");
					if ((vmstat_tmpData != null && vmstat_tmpData.length == 17))
					{
						if (vmstat_tmpData[0] != null
								&& !vmstat_tmpData[0].equalsIgnoreCase("r"))
						{
							// freeMemory
							freePhysicalMemory = Integer.parseInt(vmstat_tmpData[3]) * 4 / 1024;
							String re = vmstat_tmpData[4];
							String pi = vmstat_tmpData[5];
							String po = vmstat_tmpData[6];
							String fr = vmstat_tmpData[7];
							String sr = vmstat_tmpData[8];
							String cy = vmstat_tmpData[9];
							pagehash.put("re", re);
							pagehash.put("pi", pi);
							pagehash.put("po", po);
							pagehash.put("fr", fr);
							pagehash.put("sr", sr);
							pagehash.put("cy", cy);
						}

					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
			Memorycollectdata memorydata = null;
			if (PhysicalMemCap > 0)
			{
				// usedPhyPagesSize这个是内存使用率
				// freePhysicalMemory 是在虚拟内存中有，物理内存中没有
				// 计算内存使用率
				//System.out.println("============空闲物理内存========================"+ freePhysicalMemory);
				float PhysicalMemUtilization = (PhysicalMemCap - freePhysicalMemory)
						* 100 / PhysicalMemCap;
				//System.out.println("============使用率=2======================="+ PhysicalMemUtilization);

				// 物理总内存大小
				memorydata = new Memorycollectdata();
				memorydata.setIpaddress(ipaddress);
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("Capability");
				memorydata.setSubentity("PhysicalMemory");
				memorydata.setRestype("static");
				memorydata.setUnit("M");
				memorydata.setThevalue(Float.toString(PhysicalMemCap));
				memoryVector.addElement(memorydata);
				// 已经用的物理内存
				memorydata = new Memorycollectdata();
				memorydata.setIpaddress(ipaddress);
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("UsedSize");
				memorydata.setSubentity("PhysicalMemory");
				memorydata.setRestype("static");
				memorydata.setUnit("M");
				memorydata.setThevalue(
				// Float.toString(PhysicalMemCap*(1-usedPhyPagesSize/100)));
						Float.toString(PhysicalMemCap - freePhysicalMemory));
				memoryVector.addElement(memorydata);
				// 内存使用率
				memorydata = new Memorycollectdata();
				memorydata.setIpaddress(ipaddress);
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("Utilization");
				memorydata.setSubentity("PhysicalMemory");
				memorydata.setRestype("dynamic");
				memorydata.setUnit("%");
				memorydata.setThevalue(Math.round(PhysicalMemUtilization) + "");
				// memorydata.setThevalue(Math.round(usedPhyPagesSize)+"");
				memoryVector.addElement(memorydata);
				
				Vector phymemV = new Vector();
				phymemV.add(memorydata);				
			    Hashtable collectHash = new Hashtable();
				collectHash.put("physicalmem", phymemV);				
				//对物理内存值进行告警检测
			    try{
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix","physicalmemory");
					for(int i = 0 ; i < list.size() ; i ++){
						AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
						//对物理内存值进行告警检测
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.updateData(host,collectHash,"host","aix",alarmIndicatorsnode);
						//}
					}
			    }catch(Exception e){
			    	e.printStackTrace();
			    }
			}
		}
		
			
			

		
		if(gatherHash.containsKey("cpudetail")){
			// ----------------解析cpu内容--创建监控项---------------------
			String cpuperfContent = "";		
			try
			{
				cpuperfContent = telnet.send("sar -u 1 3");
			} catch (Exception e1)
			{
				telnet.log("error = " + e1.toString());
				e1.printStackTrace();
			}
			//SysLogger.info(cpuperfContent);
			String[] cpuperfLineArr = cpuperfContent.split("\n");
			String[] cpuperf_tmpData = null;
			
			Hashtable<String,String> cpuperfhash = new Hashtable<String,String>();
			for(int i=0; i<cpuperfLineArr.length;i++){
				cpuperf_tmpData = cpuperfLineArr[i].trim().split("\\s++");
				if(cpuperf_tmpData != null && cpuperf_tmpData.length ==5 || cpuperf_tmpData.length==6 || cpuperf_tmpData.length==7){
					
					
					if(cpuperf_tmpData[0].trim().equalsIgnoreCase("Average")){
							cpuperfhash.put("%usr", cpuperf_tmpData[1].trim());
							cpuperfhash.put("%sys", cpuperf_tmpData[2].trim());
							cpuperfhash.put("%wio", cpuperf_tmpData[3].trim());
							cpuperfhash.put("%idle", cpuperf_tmpData[4].trim());
							if(cpuperf_tmpData.length==6||cpuperf_tmpData.length==7)
							{
								cpuperfhash.put("physc", cpuperf_tmpData[5].trim());
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
					   		cpudata.setThevalue(Arith.round((100.0-Double.parseDouble(cpuperf_tmpData[4].trim())),0)+"");
					   		cpuVector.addElement(cpudata);
					   		
							//对CPU值进行告警检测
					   		Hashtable collectHash = new Hashtable();
							collectHash.put("cpu", cpuVector);
						    try{
								AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
								List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix","cpu");
								for(int k = 0 ; k < list.size() ; k ++){
									AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(k);
									//对CPU值进行告警检测
									CheckEventUtil checkutil = new CheckEventUtil();
									checkutil.updateData(host,collectHash,"host","aix",alarmIndicatorsnode);
									//}
								}
						    }catch(Exception e){
						    	e.printStackTrace();
						    }
					}
				}				
			}
		}

		if(gatherHash.containsKey("process")){
			// ----------------解析process内容--创建监控项---------------------
			String processContent = "";
			try
			{
				processContent = telnet.send("ps guc | egrep -v \"RSS\" | sort +6b -7 -n -r");
						//.send("ps gv | head -n 1; ps gv | egrep -v \"RSS\" | sort +6b -7 -n -r");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			List procslist = new ArrayList();
			ProcsDao procsdaor = new ProcsDao();
			try
			{
				procslist = procsdaor.loadByIp(ipaddress);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			} finally
			{
				procsdaor.close();
			}
			List procs_list = new ArrayList();
			Hashtable procshash = new Hashtable();
			Vector procsV = new Vector();
			if (procslist != null && procslist.size() > 0)
			{
				for (int i = 0; i < procslist.size(); i++)
				{
					Procs procs = (Procs) procslist.get(i);
					procshash.put(procs.getProcname(), procs);
					procsV.add(procs.getProcname());
				}
			}
			String[] process_LineArr = processContent.split("\n");
			String[] processtmpData = null;
			float cpuusage = 0.0f;
			for (int i = 1; i < process_LineArr.length; i++)
			{
				processtmpData = process_LineArr[i].trim().split("\\s++");

				if((processtmpData != null) && (processtmpData.length >= 11)){
					
					//SysLogger.info(processtmpData[0]+"-----------------");
					String USER=processtmpData[0];//USER
					String pid=processtmpData[1];//pid
					if("USER".equalsIgnoreCase(USER))continue;
					String cmd = processtmpData[10];
					String vbstring8 = processtmpData[8];
					String vbstring5=processtmpData[9];//cputime
					if(processtmpData.length > 11){
						cmd = processtmpData[11];
						vbstring8 = processtmpData[8]+processtmpData[9];//STIME
						vbstring5=processtmpData[10];//cputime
					}
					String vbstring2="应用程序";
					String vbstring3="";
					String vbstring4=processtmpData[4];//memsize
					if (vbstring4 == null)vbstring4="0";
					String vbstring6=processtmpData[3];//%mem
					String vbstring7=processtmpData[2];//%CPU
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
						//createSMS(procs);		     			
		     		}catch(Exception e){
		     			e.printStackTrace();
		     		}
		     	}
		     }

			systemdata = new Systemcollectdata();
			systemdata.setIpaddress(ipaddress);
			systemdata.setCollecttime(date);
			systemdata.setCategory("System");
			systemdata.setEntity("ProcessCount");
			systemdata.setSubentity("ProcessCount");
			systemdata.setRestype("static");
			systemdata.setUnit(" ");
			systemdata.setThevalue(process_LineArr + "");
			systemVector.addElement(systemdata);
		}
		
		if(gatherHash.containsKey("service")){
			// ----------------解析service内容--创建监控项---------------------
			
			Hashtable service = new Hashtable();
			String serviceContent = "";

			try
			{
				serviceContent = telnet.send("lssrc -a");
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			String[] serviceLineArr = serviceContent.split("\n");
			String[] service_tmpData = null;
			for (int i = 0; i < serviceLineArr.length; i++)
			{
				service_tmpData = serviceLineArr[i].trim().split("\\s++");
				if (service_tmpData != null && service_tmpData.length >= 3)
				{
					if ("Subsystem".equalsIgnoreCase(service_tmpData[0]))
						continue;
					if (service_tmpData.length == 4)
					{
						// 启动的情况下,有PID
						try
						{
							service.put("name", service_tmpData[0]);
							service.put("group", service_tmpData[1]);
							service.put("pid", service_tmpData[2]);
							service.put("status", service_tmpData[3]);
							servicelist.add(service);
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						service = new Hashtable();
					} else
					{
						// 未启动情况下没有PID
						try
						{
							service.put("name", service_tmpData[0]);
							service.put("group", service_tmpData[1]);
							service.put("status", service_tmpData[2]);
							service.put("pid", "");
							servicelist.add(service);
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						service = new Hashtable();
					}

				}
				if (service_tmpData.length == 2)
				{
					systemdata = new Systemcollectdata();
					systemdata.setIpaddress(ipaddress);
					systemdata.setCollecttime(date);
					systemdata.setCategory("System");
					systemdata.setEntity("operatSystem");
					systemdata.setSubentity("operatSystem");
					systemdata.setRestype("static");
					systemdata.setUnit(" ");
					systemdata.setThevalue(service_tmpData[0]);
					systemVector.addElement(systemdata);

					systemdata = new Systemcollectdata();
					systemdata.setIpaddress(ipaddress);
					systemdata.setCollecttime(date);
					systemdata.setCategory("System");
					systemdata.setEntity("SysName");
					systemdata.setSubentity("SysName");
					systemdata.setRestype("static");
					systemdata.setUnit(" ");
					systemdata.setThevalue(service_tmpData[1]);
					systemVector.addElement(systemdata);

				}
			}
		}
		

		
		if(gatherHash.containsKey("errpt")){
			// ----------------解析errpt内容--创建监控项---------------------
			Calendar nowtime = Calendar.getInstance();
			Date nowdate = nowtime.getTime();
			long beforetime = nowdate.getTime()-5*60*1000;
			nowdate.setTime(beforetime);
			nowtime.setTimeInMillis(beforetime);
			String lastdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nowdate);
			String[] datestr = lastdate.split(" ");
			String newdate = datestr[0];
			String newtime = datestr[1];
			
			String[] newdatestr = newdate.split("-");
			String[] newtimestr = newtime.split(":");
			String errptdate = newdatestr[1]+newdatestr[2]+newtimestr[0]+newtimestr[1]+newdatestr[0].substring(2);
			String errptlogContent = "";
			try
			{
				errptlogContent = telnet.send("errpt -a -s "+errptdate);
				//errptlogContent = telnet.send("errpt -a -s 0101121010");
				ReadErrptlog readErrptlog = new ReadErrptlog();
				List list = null;
				try {
					list = readErrptlog.praseErrptlog(errptlogContent);
					if(list == null){
						list = new ArrayList();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int i = 0 ; i < list.size() ; i++){
					Errptlog errptlog = (Errptlog)list.get(i);
					errptlog.setHostid(host.getId()+"");
					errptlogVector.add(list.get(i));
				}
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		
		if(gatherHash.containsKey("volume")){
			//　----------------解析volume内容--创建监控项---------------------
			String volumeContent = "";
			try{
				volumeContent = telnet.send("lspv");		
				String[] volumeLineArr = volumeContent.split("\n");
				String[] volumetmpData = null;
				for(int i=1; i<volumeLineArr.length;i++)
				{			
					volumetmpData = volumeLineArr[i].split("\\s++");
					if((volumetmpData != null) && (volumetmpData.length == 4 || volumetmpData.length == 3))
					{
						Hashtable volumeHash = new Hashtable();
						volumeHash.put("disk", volumetmpData[0]);
						volumeHash.put("pvid", volumetmpData[1]);
						volumeHash.put("vg", volumetmpData[2]);
						if(volumetmpData.length == 4){
							volumeHash.put("status", volumetmpData[3]);
						}else{
							volumeHash.put("status", "-");
						}
						
						volumeVector.addElement(volumeHash);
					}
				}
			}catch(Exception e){
				
			}
		}
		
		if(gatherHash.containsKey("route")){
			//　----------------解析路由内容--创建监控项---------------------
			String routeContent = "";
			try{
				routeContent = telnet.send("netstat -rn");		
				String[] routeLineArr = routeContent.split("\n");
				String[] routetmpData = null;
				for(int i=1; i<routeLineArr.length;i++)
				{		
					routeList.add(routeLineArr[i]);
				}
			}catch(Exception e){
				
			}
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
			
			 //HostcpuResultTosql restosql=new HostcpuResultTosql();
			 //restosql.CreateResultTosql(returnHash, host.getIpAddress());
			  //把结果转换成sql
			   
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
			{//系统信息
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
		if (iflist != null && iflist.size()>0)
			{
			returnHash.put("iflist",iflist);
			HostDatatempiflistRtosql tosql=new HostDatatempiflistRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
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
		if (netmedialist != null && netmedialist.size()>0)
			{
			returnHash.put("netmedialist",netmedialist);
			}
		if (servicelist != null && servicelist.size()>0)
			{
			returnHash.put("servicelist",servicelist);
			   //把sql生成sql
			HostDatatempserciceRttosql totempsql=new HostDatatempserciceRttosql();
			totempsql.CreateResultLinuxTosql(returnHash, host);
			
			}
		if (cpuperflist != null && cpuperflist.size()>0)
			{
			returnHash.put("cpuperflist",cpuperflist);
			
			HostcpuResultTosql rtosql=new HostcpuResultTosql();
			rtosql.CreateLinuxResultTosql(returnHash, host.getIpAddress());
			
			HostDatatempCpuperRtosql tmptosql=new HostDatatempCpuperRtosql();
			tmptosql.CreateResultTosql(returnHash, host);
			
			}
		if (pagehash != null && pagehash.size()>0)
			{
			returnHash.put("pagehash",pagehash);
			
			HostDatatempPageRtosql tosql=new HostDatatempPageRtosql();
			tosql.CreateResultTosql(returnHash, host);
			}
		if (paginghash != null && paginghash.size()>0)
			{
			
			returnHash.put("paginghash",paginghash);
			
			HostDatatempPagingRtosql tosql=new HostDatatempPagingRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			HostPagingResultTosql Rtosql=new HostPagingResultTosql();
			Rtosql.CreateResultTosql(returnHash, host.getIpAddress());
			
			
			}
		if (errptlogVector != null && errptlogVector.size()>0)
			{
			
			returnHash.put("errptlog",errptlogVector);
			HostDatatempErrptRtosql tosql=new HostDatatempErrptRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
		if (volumeVector != null && volumeVector.size()>0)
			{
			returnHash.put("volume",volumeVector);
			
			HostDatatempVolumeRtosql tosql=new HostDatatempVolumeRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
		if (routeList != null && routeList.size()>0)
			{
			returnHash.put("routelist",routeList);
			
			HostDatatempRuteRtosql tosql =new HostDatatempRuteRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
		returnHash.put("collecttime",collecttime);
		SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		SysLogger.info("&&&&&  结束采集 IP:"+host.getIpAddress()+"&&&&&&");
		SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		return returnHash;

	}
	
	public void createSMS(Procs procs){
	 	Procs lastprocs = null;
	 	//建立短信	
	 	procs.setCollecttime(Calendar.getInstance());
	 	//从已经发送的短信列表里获得当前该PROC已经发送的短信
	 	//lastprocs = (Procs)sendeddata.get(procs.getIpaddress()+":"+procs.getProcname());	
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

}
