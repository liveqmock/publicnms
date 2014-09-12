package com.afunms.polling.telnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.afunms.common.util.ShareData;
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
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.om.Usercollectdata;
import com.afunms.polling.om.UtilHdx;
import com.gatherResulttosql.HostDatatempCollecttimeRtosql;
import com.gatherResulttosql.HostDatatempCpuconfiRtosql;
import com.gatherResulttosql.HostDatatempCpuperRtosql;
import com.gatherResulttosql.HostDatatempDiskRttosql;
import com.gatherResulttosql.HostDatatempNodeconfRtosql;
import com.gatherResulttosql.HostDatatempProcessRtTosql;
import com.gatherResulttosql.HostDatatempUserRtosql;
import com.gatherResulttosql.HostDatatempiflistRtosql;
import com.gatherResulttosql.HostDatatempinterfaceRtosql;
import com.gatherResulttosql.HostDatatempnDiskperfRtosql;
import com.gatherResulttosql.HostDatatemputilhdxRtosql;
import com.gatherResulttosql.HostPhysicalMemoryResulttosql;
import com.gatherResulttosql.HostcpuResultTosql;
import com.gatherResulttosql.HostdiskResultosql;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;
import com.gatherResulttosql.NetHostMemoryRtsql;
import com.gatherResulttosql.HostDatatempAllutilhdxRtosql;

public class LinuxInfoParser {

	public static Hashtable getTelnetMonitorDetail(Wrapper telnet) {
		System.out.println("-------begin to get telnet monitor detail");

		String ipaddress = telnet.getHost();

		Hashtable returnHash = new Hashtable();

		Vector cpuVector = new Vector();
		Vector systemVector = new Vector();
		Vector userVector = new Vector();
		Vector diskVector = new Vector();
		Vector processVector = new Vector();
		Nodeconfig nodeconfig = new Nodeconfig();
		Vector interfaceVector = new Vector();
		Vector utilhdxVector = new Vector();
		Vector allutilhdxVector = new Vector();
		String collecttime = "";
		CPUcollectdata cpudata = null;
		Systemcollectdata systemdata = null;
		Usercollectdata userdata = null;
		Processcollectdata processdata = null;
		Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
		if (host == null)
			return null;
		nodeconfig.setNodeid(host.getId());
		nodeconfig.setHostname(host.getAlias());

		Pattern tmpPt = null;
		Matcher mr = null;
		Calendar date = Calendar.getInstance();

		// ----------------解析version内容--创建监控项---------------------
		String versionContent = "";

		try {
			versionContent = telnet.send("cat /proc/version");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (versionContent != null && versionContent.length() > 0) {
			nodeconfig.setCSDVersion(versionContent.trim());
		}

		// ----------------解析cpuconfig内容--创建监控项---------------------
		String cpuconfigContent = "";
		try {
			cpuconfigContent = telnet.send("cat /proc/cpuinfo | egrep \"model name|processor|cpu MHz|cache size\"");
		} catch (IOException e1) {
			// e1.printStackTrace();
		}

//		System.out.println("==========cpuconfigContent==============");
//		System.out.println("===" + cpuconfigContent);
//		System.out.println("==========1==============");

		String[] cpuconfigLineArr = cpuconfigContent.split("\n");
		// String[] cpuconfig_tmpData = null;
		List<Nodecpuconfig> cpuconfiglist = new ArrayList<Nodecpuconfig>();
		Nodecpuconfig nodecpuconfig = new Nodecpuconfig();
		String procesors = "";
		for (int i = 1; i < cpuconfigLineArr.length; i++) {
//			System.out.println("cpuconfigLineArr[" + i + "]=====" + cpuconfigLineArr[i]);
			String[] result = cpuconfigLineArr[i].trim().split(":");
			if (result.length > 0) {
				if (result[0].trim().equalsIgnoreCase("processor")) {
					nodecpuconfig.setNodeid(host.getId());
					nodecpuconfig.setProcessorId(result[1].trim());
					procesors = result[1].trim();
					System.out.println("procesors=" + procesors);
				} else if (result[0].trim().equalsIgnoreCase("model name")) {
					nodecpuconfig.setName(result[1].trim());
					System.out.println("model name=" + result[1].trim());
				} else if (result[0].trim().equalsIgnoreCase("cpu MHz")) {
					nodecpuconfig.setProcessorSpeed(result[1].trim());
					System.out.println("cpu MHz=" + result[1].trim());
				} else if (result[0].trim().equalsIgnoreCase("cache size")) {
					nodecpuconfig.setL2CacheSize(result[1].trim());
					cpuconfiglist.add(nodecpuconfig);
					System.out.println("cache size=" + result[1].trim());
					nodecpuconfig = new Nodecpuconfig();
				}
			}

		}
		nodecpuconfig = null;
		// 设置节点的CPU配置个数
		int procesorsnum = 0;
		if (procesors != null && procesors.trim().length() > 0) {
			try {
				procesorsnum = Integer.parseInt(procesors) + 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		nodeconfig.setNumberOfProcessors(procesorsnum + "");
		// cpu个数变更告警
		CheckEventUtil cEventUtil = new CheckEventUtil();
		cEventUtil.hardwareInfo(host, "cpu", procesorsnum + "");

		// ----------------解析disk内容--创建监控项---------------------
		String diskContent = "";
		String diskLabel;
		List disklist = new ArrayList();
		// disk数据集合，变化时进行告警检测
		Hashtable<String, Object> diskInfoHash = new Hashtable<String, Object>();
		// 磁盘大小
		float diskSize = 0;
		// 磁盘名称集合
		List<String> diskNameList = new ArrayList<String>();
		try {
			diskContent = telnet.send("df -k");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		System.out.println("==========df -k==============");
//		System.out.println("===" + diskContent);
//		System.out.println("==========2==============");

		String[] diskLineArr = diskContent.split("\n");
		String[] tmpData = null;
		// tmpPt =
		// Pattern.compile("(^/[\\w/#]*)(\\s++)(\\d++)(\\s++)((\\d++)(\\s++)){2}+(\\d++)%");
		Diskcollectdata diskdata = null;
		int diskflag = 0;
		for (int i = 1; i < diskLineArr.length; i++) {

			tmpData = diskLineArr[i].split("\\s++");
			if ((tmpData != null) && (tmpData.length == 6)) {
				diskLabel = tmpData[5];

				diskdata = new Diskcollectdata();
				diskdata.setIpaddress(ipaddress);
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("Utilization");// 利用百分比
				diskdata.setSubentity(tmpData[5]);
				diskdata.setRestype("static");
				diskdata.setUnit("%");
				try {
					diskdata.setThevalue(Float.toString(Float.parseFloat(tmpData[4].substring(0, tmpData[4].indexOf("%")))));
				} catch (Exception ex) {
					continue;
				}
				diskVector.addElement(diskdata);

				diskdata = new Diskcollectdata();
				diskdata.setIpaddress(ipaddress);
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("AllSize");// 总空间
				diskdata.setSubentity(tmpData[5]);
				diskdata.setRestype("static");

				float allblocksize = 0;
				allblocksize = Float.parseFloat(tmpData[1]);
				float allsize = 0.0f;
				allsize = allblocksize * 1.0f / 1024;
				// 磁盘总大小 单位为M
				diskSize = diskSize + allsize;
				// 磁盘名称放入集合
				if (!diskdata.getSubentity().equals("")) {
					diskNameList.add(diskdata.getSubentity());
				}
				if (allsize >= 1024.0f) {
					allsize = allsize / 1024;
					diskdata.setUnit("G");
				} else {
					diskdata.setUnit("M");
				}

				diskdata.setThevalue(Float.toString(allsize));
				diskVector.addElement(diskdata);

				diskdata = new Diskcollectdata();
				diskdata.setIpaddress(ipaddress);
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("UsedSize");// 使用大小
				diskdata.setSubentity(tmpData[5]);
				diskdata.setRestype("static");

				float UsedintSize = 0;
				UsedintSize = Float.parseFloat(tmpData[2]);
				float usedfloatsize = 0.0f;
				usedfloatsize = UsedintSize * 1.0f / 1024;
				if (usedfloatsize >= 1024.0f) {
					usedfloatsize = usedfloatsize / 1024;
					diskdata.setUnit("G");
				} else {
					diskdata.setUnit("M");
				}
				diskdata.setThevalue(Float.toString(usedfloatsize));
				diskVector.addElement(diskdata);
				disklist.add(diskflag, diskLabel);
				diskflag = diskflag + 1;
			}
		}
		// 进行磁盘告警检测
		// SysLogger.info("### 开始运行检测磁盘是否告警### ... ###");
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "linux");
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(i);
				// SysLogger.info("alarmIndicatorsnode name
				// ======"+alarmIndicatorsnode.getName());
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("diskperc")) {
					CheckEventUtil checkutil = new CheckEventUtil();
					checkutil.checkDisk(host, diskVector, alarmIndicatorsnode);
					break;
				}
			}
			// ##########总大小以及盘符信息变化，进行告警判断
			diskSize = diskSize / 1024;
			diskInfoHash.put("diskSize", diskSize + "G");
			diskInfoHash.put("diskNameList", diskNameList);
			CheckEventUtil checkutil = new CheckEventUtil();
			checkutil.hardwareInfo(host, "disk", diskInfoHash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// SysLogger.info("disklist size
		// ========================"+disklist.size());
		// ----------------解析diskperf内容--创建监控项---------------------
		String diskperfContent = "";
		String average = "";

		try {
			diskperfContent = telnet.send("sar -d 1 3");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//		System.out.println("==========sar -d 1 3==============");
//		System.out.println("===");
//		System.out.println("==========3==============");

		String[] diskperfLineArr = diskperfContent.split("\n");
		String[] diskperf_tmpData = null;
		// Hashtable<String,Hashtable> alldiskperf = new
		// Hashtable<String,Hashtable>();
		List<Hashtable> alldiskperf = new ArrayList<Hashtable>();
		Hashtable<String, String> diskperfhash = new Hashtable<String, String>();
		int flag = 0;
		for (int i = 0; i < diskperfLineArr.length; i++) {
			diskperf_tmpData = diskperfLineArr[i].trim().split("\\s++");
			if (diskperf_tmpData != null && diskperf_tmpData.length == 10) {
				if (diskperf_tmpData[0].trim().equalsIgnoreCase("Average:")) {
					if (diskperf_tmpData[1].trim().equalsIgnoreCase("DEV")) {
						// 处理第一行标题
						continue;
					} else {
						// if(flag >= disklist.size())break;
						diskperfhash.put("tps", diskperf_tmpData[2].trim());
						diskperfhash.put("rd_sec/s", diskperf_tmpData[3].trim());
						diskperfhash.put("wr_sec/s", diskperf_tmpData[4].trim());
						diskperfhash.put("avgrq-sz", diskperf_tmpData[5].trim());
						diskperfhash.put("avgqu-sz", diskperf_tmpData[6].trim());
						diskperfhash.put("await", diskperf_tmpData[7].trim());
						diskperfhash.put("svctm", diskperf_tmpData[8].trim());
						diskperfhash.put("%util", diskperf_tmpData[9].trim());
						diskperfhash.put("%busy", Math.round(Float.parseFloat(diskperf_tmpData[8].trim()) * 100
								/ (Float.parseFloat(diskperf_tmpData[7].trim()) + Float.parseFloat(diskperf_tmpData[8].trim())))
								+ "");
						diskperfhash.put("disklebel", diskperf_tmpData[1].trim());

						// //Diskcollectdata diskdata = null;
						// diskdata = new Diskcollectdata();
						// diskdata.setIpaddress(ipaddress);
						// diskdata.setCollecttime(date);
						// diskdata.setCategory("Disk");
						// diskdata.setEntity("Busy");// 利用百分比
						// diskdata.setSubentity((String)disklist.get(flag));
						// diskdata.setRestype("static");
						// diskdata.setUnit("%");
						// diskdata.setThevalue((String)diskperfhash.get("%busy"));
						// diskVector.addElement(diskdata);
						// //SysLogger.info("add
						// ===========busy:"+diskdata.getThevalue());
						// //读KBytes/s
						// diskdata = new Diskcollectdata();
						// diskdata.setIpaddress(ipaddress);
						// diskdata.setCollecttime(date);
						// diskdata.setCategory("Disk");
						// diskdata.setEntity("ReadBytesPersec");//读KBytes/s
						// diskdata.setSubentity((String)disklist.get(flag));
						// diskdata.setRestype("static");
						// diskdata.setUnit("");
						// diskdata.setThevalue(Math.round(Float.parseFloat(diskperf_tmpData[3].trim())*512)+"");
						// diskVector.addElement(diskdata);
						// //写KBytes/s
						// diskdata = new Diskcollectdata();
						// diskdata.setIpaddress(ipaddress);
						// diskdata.setCollecttime(date);
						// diskdata.setCategory("Disk");
						// diskdata.setEntity("WriteBytesPersec");//写KBytes/s
						// diskdata.setSubentity((String)disklist.get(flag));
						// diskdata.setRestype("static");
						// diskdata.setUnit("");
						// diskdata.setThevalue(Math.round(Float.parseFloat(diskperf_tmpData[4].trim())*512)+"");
						// diskVector.addElement(diskdata);

						alldiskperf.add(diskperfhash);
						flag = flag + 1;
						diskperfhash = new Hashtable();

					}
				}
			}
		}

		// ----------------解析cpu内容--创建监控项---------------------

		String cpuperfContent = "";
		try {
			cpuperfContent = telnet.send("sar -u 1 3");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		System.out.println("==========sar -u 1 3==============");
//		System.out.println("===");
//		System.out.println("==========4==============");
		// String average = "";

		String[] cpuperfLineArr = cpuperfContent.split("\n");
		List cpuperflist = new ArrayList();
		Hashtable<String, String> cpuperfhash = new Hashtable<String, String>();
		for (int i = 0; i < cpuperfLineArr.length; i++) {
			diskperf_tmpData = cpuperfLineArr[i].trim().split("\\s++");
			if (diskperf_tmpData != null && diskperf_tmpData.length == 8) {
				if (diskperf_tmpData[0].trim().equalsIgnoreCase("Average:")) {
					cpuperfhash.put("%user", diskperf_tmpData[2].trim());
					cpuperfhash.put("%nice", diskperf_tmpData[3].trim());
					cpuperfhash.put("%system", diskperf_tmpData[4].trim());
					cpuperfhash.put("%iowait", diskperf_tmpData[5].trim());
					// sar 的版本不一样需要做下面的判断，
					// 修改人：konglq

					if (diskperf_tmpData.length == 7) {
						// System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&格式=7"+diskperf_tmpData[6].trim());
						cpuperfhash.put("%idle", diskperf_tmpData[6].trim());
					}

					if (diskperf_tmpData.length == 8) {
						// System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&格式=8"+diskperf_tmpData[7].trim());
						cpuperfhash.put("%steal", diskperf_tmpData[6].trim());
						cpuperfhash.put("%idle", diskperf_tmpData[7].trim());
					}
					cpuperflist.add(cpuperfhash);

					cpudata = new CPUcollectdata();
					cpudata.setIpaddress(ipaddress);
					cpudata.setCollecttime(date);
					cpudata.setCategory("CPU");
					cpudata.setEntity("Utilization");
					cpudata.setSubentity("Utilization");
					cpudata.setRestype("dynamic");
					cpudata.setUnit("%");
					if (diskperf_tmpData.length == 8) {
						cpudata.setThevalue(Arith.round((100.0 - Double.parseDouble(diskperf_tmpData[7].trim())), 0) + "");
					}

					if (diskperf_tmpData.length == 7) {
						cpudata.setThevalue(Arith.round((100.0 - Double.parseDouble(diskperf_tmpData[6].trim())), 0) + "");
					}
					cpuVector.addElement(cpudata);

				}
			} else if (diskperf_tmpData != null && diskperf_tmpData.length == 7) {
				// 某些返回值 中 不包含 steal 这一列

				if (diskperf_tmpData[0].trim().equalsIgnoreCase("Average:")) {
					cpuperfhash.put("%user", diskperf_tmpData[2].trim());
					cpuperfhash.put("%nice", diskperf_tmpData[3].trim());
					cpuperfhash.put("%system", diskperf_tmpData[4].trim());
					cpuperfhash.put("%iowait", diskperf_tmpData[5].trim());
					cpuperfhash.put("%steal", "0");
					cpuperfhash.put("%idle", diskperf_tmpData[6].trim());
					cpuperflist.add(cpuperfhash);

					cpudata = new CPUcollectdata();
					cpudata.setIpaddress(ipaddress);
					cpudata.setCollecttime(date);
					cpudata.setCategory("CPU");
					cpudata.setEntity("Utilization");
					cpudata.setSubentity("Utilization");
					cpudata.setRestype("dynamic");
					cpudata.setUnit("%");
					cpudata.setThevalue(Arith.round((100.0 - Double.parseDouble(diskperf_tmpData[6].trim())), 0) + "");
					cpuVector.addElement(cpudata);

				}
			}

		}

		// ----------------解析memory内容--创建监控项---------------------
		String memperfContent = "";

		try {
			memperfContent = telnet.send("free");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		System.out.println("==========free==============");
//		System.out.println("==========5==============");

		String[] memperfLineArr = memperfContent.split("\n");
		List memperflist = new ArrayList();
		Vector memoryVector = new Vector();
		Memorycollectdata memorydata = null;
		Hashtable<String, String> memperfhash = new Hashtable<String, String>();
		for (int i = 0; i < memperfLineArr.length; i++) {
			diskperf_tmpData = memperfLineArr[i].trim().split("\\s++");
			if (diskperf_tmpData != null && diskperf_tmpData.length >= 4) {
				if (diskperf_tmpData[0].trim().equalsIgnoreCase("Mem:")) {
					memperfhash.put("total", diskperf_tmpData[1].trim());
					memperfhash.put("used", diskperf_tmpData[2].trim());
					memperfhash.put("free", diskperf_tmpData[3].trim());
					memperfhash.put("shared", diskperf_tmpData[4].trim());
					memperfhash.put("buffers", diskperf_tmpData[5].trim());
					memperfhash.put("cached", diskperf_tmpData[6].trim());
					memperflist.add(memperfhash);
					memperfhash = new Hashtable();
					// Memory
					float PhysicalMemUtilization = 100 - Float.parseFloat(diskperf_tmpData[3]) * 100 / Float.parseFloat(diskperf_tmpData[1]);
					memorydata = new Memorycollectdata();
					memorydata.setIpaddress(ipaddress);
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("Capability");
					memorydata.setSubentity("PhysicalMemory");
					memorydata.setRestype("static");
					memorydata.setUnit("M");
					memorydata.setThevalue(Integer.toString(Integer.parseInt(diskperf_tmpData[1]) / 1024));
					memoryVector.addElement(memorydata);

					memorydata = new Memorycollectdata();
					memorydata.setIpaddress(ipaddress);
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("UsedSize");
					memorydata.setSubentity("PhysicalMemory");
					memorydata.setRestype("static");
					memorydata.setUnit("M");
					memorydata.setThevalue(Integer.toString(Integer.parseInt(diskperf_tmpData[2]) / 1024));
					memoryVector.addElement(memorydata);

					memorydata = new Memorycollectdata();
					memorydata.setIpaddress(ipaddress);
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("Utilization");
					memorydata.setSubentity("PhysicalMemory");
					memorydata.setRestype("dynamic");
					memorydata.setUnit("%");
					memorydata.setThevalue(Math.round(PhysicalMemUtilization) + "");
					memoryVector.addElement(memorydata);

					Hashtable collectHash = new Hashtable();
					collectHash.put("physicalmem", memoryVector);

					// 对物理内存值进行告警检测
					try {
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "linux", "physicalmemory");
						for (int k = 0; k < list.size(); k++) {
							AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(k);
							// 对物理内存值进行告警检测
							CheckEventUtil checkutil = new CheckEventUtil();
							checkutil.updateData(host, collectHash, "host", "linux", alarmIndicatorsnode);
							// }
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 物理内存总大小变化告警检测
					CheckEventUtil checkutil = new CheckEventUtil();
					checkutil.hardwareInfo(host, "PhysicalMemory", Integer.toString(Integer.parseInt(diskperf_tmpData[1]) / 1024) + "M");
				} else if (diskperf_tmpData[0].trim().equalsIgnoreCase("Swap:")) {
					memperfhash.put("total", diskperf_tmpData[1].trim());
					memperfhash.put("used", diskperf_tmpData[2].trim());
					memperfhash.put("free", diskperf_tmpData[3].trim());
					memperflist.add(memperfhash);
					memperfhash = new Hashtable();
					// Swap
					memorydata = new Memorycollectdata();
					memorydata.setIpaddress(ipaddress);
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("Capability");
					memorydata.setSubentity("SwapMemory");
					memorydata.setRestype("static");
					memorydata.setUnit("M");
					memorydata.setThevalue(Integer.toString(Integer.parseInt(diskperf_tmpData[1]) / 1024));
					memoryVector.addElement(memorydata);
					memorydata = new Memorycollectdata();
					memorydata.setIpaddress(ipaddress);
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("UsedSize");
					memorydata.setSubentity("SwapMemory");
					memorydata.setRestype("static");
					memorydata.setUnit("M");
					memorydata.setThevalue(Integer.toString(Integer.parseInt(diskperf_tmpData[2]) / 1024));
					memoryVector.addElement(memorydata);
					float SwapMemUtilization = (Integer.parseInt(diskperf_tmpData[2])) * 100 / Integer.parseInt(diskperf_tmpData[1]);

					memorydata = new Memorycollectdata();
					memorydata.setIpaddress(ipaddress);
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("Utilization");
					memorydata.setSubentity("SwapMemory");
					memorydata.setRestype("dynamic");
					memorydata.setUnit("%");
					memorydata.setThevalue(Math.round(SwapMemUtilization) + "");
					memoryVector.addElement(memorydata);
				}
			}
		}

		// ----------------解析process内容--创建监控项---------------------
		String processContent = "";

		try {
			processContent = telnet.send("ps aux");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		System.out.println("==========ps aux==============");
//		System.out.println("==========6==============");

		List procslist = new ArrayList();
		ProcsDao procsdaor = new ProcsDao();
		try {
			procslist = procsdaor.loadByIp(ipaddress);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			procsdaor.close();
		}
		List procs_list = new ArrayList();
		Hashtable procshash = new Hashtable();
		Vector procsV = new Vector();
		if (procslist != null && procslist.size() > 0) {
			for (int i = 0; i < procslist.size(); i++) {
				Procs procs = (Procs) procslist.get(i);
				procshash.put(procs.getProcname(), procs);
				procsV.add(procs.getProcname());
			}
		}
		String[] cpu_LineArr = processContent.split("\n");
		String[] cputmpData = null;
		float cpuusage = 0.0f;
		for (int i = 1; i < cpu_LineArr.length; i++) {
			cputmpData = cpu_LineArr[i].trim().split("\\s++");

			if ((cputmpData != null) && (cputmpData.length >= 11)) {
				String pid = cputmpData[1];// pid

				if (pid.equalsIgnoreCase("pid"))
					continue;

				String cmd = "";

				for (int k = 10; k < cputmpData.length; k++) {
					if (k == cputmpData.length - 1) {
						cmd += cputmpData[k] + " ";
					} else {
						cmd += cputmpData[k];
					}
				}

				cmd = cmd.trim();// command

				String vbstring1 = cmd;// command
				String vbstring2 = "应用程序";
				String vbstring3 = "正在运行";
				String vbstring4 = cputmpData[5];// memsize
				if (vbstring4 == null)
					vbstring4 = "0";
				String vbstring5 = cputmpData[9];// cputime
				String vbstring6 = cputmpData[3];// %mem

				processdata = new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("MemoryUtilization");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit("%");
				processdata.setThevalue(vbstring6);
				processVector.addElement(processdata);

				processdata = new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Memory");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit("K");
				processdata.setThevalue(vbstring4);
				processVector.addElement(processdata);

				processdata = new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Type");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring2);
				processVector.addElement(processdata);

				processdata = new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Status");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring3);
				processVector.addElement(processdata);

				processdata = new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Name");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring1);
				processVector.addElement(processdata);

				processdata = new Processcollectdata();
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
				 * //判断是否有需要监视的进程，若取得的列表里包含监视进程，则从Vector里去掉 if (procshash !=null &&
				 * procshash.size()>0){ if (procshash.containsKey(vbstring1)){
				 * procshash.remove(vbstring1); procsV.remove(vbstring1); } }
				 */

			}
		}
		// 判断ProcsV里还有没有需要监视的进程，若有，则说明当前没有启动该进程，则用命令重新启动该进程，同时写入事件
		Vector eventtmpV = new Vector();
		if (procsV != null && procsV.size() > 0) {
			for (int i = 0; i < procsV.size(); i++) {
				Procs procs = (Procs) procshash.get((String) procsV.get(i));
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
				try {
					Hashtable iplostprocdata = (Hashtable) ShareData.getLostprocdata(ipaddress);
					if (iplostprocdata == null)
						iplostprocdata = new Hashtable();
					iplostprocdata.put(procs.getProcname(), procs);
					ShareData.setLostprocdata(ipaddress, iplostprocdata);
					EventList eventlist = new EventList();
					eventlist.setEventtype("poll");
					eventlist.setEventlocation(host.getSysLocation());
					eventlist.setContent(procs.getProcname() + "进程丢失");
					eventlist.setLevel1(1);
					eventlist.setManagesign(0);
					eventlist.setBak("");
					eventlist.setRecordtime(Calendar.getInstance());
					eventlist.setReportman("系统轮询");
					NodeToBusinessDao ntbdao = new NodeToBusinessDao();
					List ntblist = ntbdao.loadByNodeAndEtype(host.getId(), "equipment");
					String bids = ",";
					if (ntblist != null && ntblist.size() > 0) {

						for (int k = 0; k < ntblist.size(); k++) {
							NodeToBusiness ntb = (NodeToBusiness) ntblist.get(k);
							bids = bids + ntb.getBusinessid() + ",";
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
					// 发送手机短信并写事件和声音告警
					// createSMS(procs);
				} catch (Exception e) {
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
		systemdata.setThevalue(cpu_LineArr.length - 1 + "");
		systemVector.addElement(systemdata);

		// ----------------解析mac内容--创建监控项---------------------
		String macContent = "";
		try {
			macContent = telnet.send("/sbin/ip addr");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		System.out.println("==========/sbin/ip addr==============");
//		System.out.println("===");
//		System.out.println("==========7==============");

		String[] macLineArr = macContent.split("\n");
		String[] mac_tmpData = null;
		String MAC = "";
		for (int i = 0; i < macLineArr.length; i++) {
			mac_tmpData = macLineArr[i].trim().split("\\s++");
			if (mac_tmpData.length == 4) {
				if (mac_tmpData[0].equalsIgnoreCase("link/ether") && mac_tmpData[2].equalsIgnoreCase("brd")) {
					MAC = mac_tmpData[1];
					String mac_ = nodeconfig.getMac();
					if (mac_ != null && mac_.trim().length() > 0) {
						mac_ = mac_ + "," + MAC;
						nodeconfig.setMac(mac_);
					} else {
						nodeconfig.setMac(MAC);
					}
				}
			}
		}
		systemdata = new Systemcollectdata();
		systemdata.setIpaddress(ipaddress);
		systemdata.setCollecttime(date);
		systemdata.setCategory("System");
		systemdata.setEntity("MacAddr");
		systemdata.setSubentity("MacAddr");
		systemdata.setRestype("static");
		systemdata.setUnit(" ");
		systemdata.setThevalue(MAC);
		systemVector.addElement(systemdata);

		// ----------------解析interface内容--创建监控项---------------------
		String interfaceContent = "";
		try {
			interfaceContent = telnet.send("sar -n DEV 1 3");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		System.out.println("==========sar -n DEV 1 3==============");
//		System.out.println("==========8==============");

		String[] interfaceLineArr = interfaceContent.split("\n");
		String[] interface_tmpData = null;
		Long allInHdx = 0l;
		Long allOutHdx = 0l;
		int count=0;
		ArrayList iflist = new ArrayList();
		Hashtable ifhash = new Hashtable();
		for (int i = 0; i < interfaceLineArr.length; i++) {
			Interfacecollectdata interfacedata = null;
			interface_tmpData = interfaceLineArr[i].trim().split("\\s++");
			if (interface_tmpData != null && interface_tmpData.length == 9) {
				if (interfaceLineArr[i].contains("Average:")) {
					if (interface_tmpData[1].trim().equalsIgnoreCase("IFACE"))
						continue;
					ifhash.put("IFACE", interface_tmpData[1]);
					ifhash.put("rxpck/s", interface_tmpData[2]);
					ifhash.put("txpck/s", interface_tmpData[3]);
					ifhash.put("rxbyt/s", interface_tmpData[4]);
					ifhash.put("txbyt/s", interface_tmpData[5]);
					ifhash.put("rxcmp/s", interface_tmpData[6]);
					ifhash.put("txcmp/s", interface_tmpData[7]);
					ifhash.put("rxmcst/s", interface_tmpData[8]);

					// 端口索引
					interfacedata = new Interfacecollectdata();
					interfacedata.setIpaddress(ipaddress);
					interfacedata.setCollecttime(date);
					interfacedata.setCategory("Interface");
					interfacedata.setEntity("index");
					interfacedata.setSubentity(i + "");
					// 端口状态不保存，只作为静态数据放到临时表里
					interfacedata.setRestype("static");
					interfacedata.setUnit("");
					interfacedata.setThevalue(i + "");
					interfacedata.setChname("端口索引");
					interfaceVector.addElement(interfacedata);
					// 端口描述
					interfacedata = new Interfacecollectdata();
					interfacedata.setIpaddress(ipaddress);
					interfacedata.setCollecttime(date);
					interfacedata.setCategory("Interface");
					interfacedata.setEntity("ifDescr");
					interfacedata.setSubentity(i + "");
					// 端口状态不保存，只作为静态数据放到临时表里
					interfacedata.setRestype("static");
					interfacedata.setUnit("");
					interfacedata.setThevalue(interface_tmpData[1]);
					interfacedata.setChname("端口描述2");
					interfaceVector.addElement(interfacedata);
					// 端口带宽
					interfacedata = new Interfacecollectdata();
					interfacedata.setIpaddress(ipaddress);
					interfacedata.setCollecttime(date);
					interfacedata.setCategory("Interface");
					interfacedata.setEntity("ifSpeed");
					interfacedata.setSubentity(i + "");
					interfacedata.setRestype("static");
					interfacedata.setUnit("");
					interfacedata.setThevalue("");
					interfacedata.setChname("每秒字节数");
					interfaceVector.addElement(interfacedata);
					// 当前状态
					interfacedata = new Interfacecollectdata();
					interfacedata.setIpaddress(ipaddress);
					interfacedata.setCollecttime(date);
					interfacedata.setCategory("Interface");
					interfacedata.setEntity("ifOperStatus");
					interfacedata.setSubentity(i + "");
					interfacedata.setRestype("static");
					interfacedata.setUnit("");
					interfacedata.setThevalue("up");
					interfacedata.setChname("当前状态");
					interfaceVector.addElement(interfacedata);
					// 当前状态
					interfacedata = new Interfacecollectdata();
					interfacedata.setIpaddress(ipaddress);
					interfacedata.setCollecttime(date);
					interfacedata.setCategory("Interface");
					interfacedata.setEntity("ifOperStatus");
					interfacedata.setSubentity(i + "");
					interfacedata.setRestype("static");
					interfacedata.setUnit("");
					interfacedata.setThevalue(1 + "");
					interfacedata.setChname("当前状态");
					interfaceVector.addElement(interfacedata);
					// 端口入口流速
					UtilHdx utilhdx = new UtilHdx();
					utilhdx.setIpaddress(ipaddress);
					utilhdx.setCollecttime(date);
					utilhdx.setCategory("Interface");
					String chnameBand = "";
					utilhdx.setEntity("InBandwidthUtilHdx");
					utilhdx.setThevalue(Long.toString(Math.round(Float.parseFloat(interface_tmpData[4])) * 8));
					utilhdx.setSubentity(i + "");
					utilhdx.setRestype("dynamic");
					utilhdx.setUnit("Kb/秒");
					utilhdx.setChname(i + "端口入口" + "流速");
					utilhdxVector.addElement(utilhdx);
					allInHdx += Math.round(Float.parseFloat(interface_tmpData[4])) * 8;
					// 端口出口流速
					utilhdx = new UtilHdx();
					utilhdx.setIpaddress(ipaddress);
					utilhdx.setCollecttime(date);
					utilhdx.setCategory("Interface");
					utilhdx.setEntity("OutBandwidthUtilHdx");
					utilhdx.setThevalue(Long.toString(Math.round(Float.parseFloat(interface_tmpData[5])) * 8));
					utilhdx.setSubentity(i + "");
					utilhdx.setRestype("dynamic");
					utilhdx.setUnit("Kb/秒");
					utilhdx.setChname(i + "端口出口" + "流速");
					utilhdxVector.addElement(utilhdx);
					allOutHdx += Math.round(Float.parseFloat(interface_tmpData[5])) * 8;
					/*
					 * //丢弃的数据包 utilhdx=new UtilHdx();
					 * utilhdx.setIpaddress(ipaddress);
					 * utilhdx.setCollecttime(date);
					 * utilhdx.setCategory("Interface");
					 * utilhdx.setChname("入站被丢弃的数据包");
					 * utilhdx.setEntity("ifInDiscards");
					 * utilhdx.setThevalue((String)rValue.get("PacketsReceivedDiscarded"));
					 * utilhdx.setSubentity(i+"");
					 * utilhdx.setRestype("dynamic"); utilhdx.setUnit("个");
					 * utilhdxVector.addElement(utilhdx); //入站错误数据包 utilhdx=new
					 * UtilHdx(); utilhdx.setIpaddress(ipaddress);
					 * utilhdx.setCollecttime(date);
					 * utilhdx.setCategory("Interface");
					 * utilhdx.setChname("入站错误数据包");
					 * utilhdx.setEntity("ifInErrors");
					 * utilhdx.setThevalue((String)rValue.get("PacketsReceivedErrors"));
					 * utilhdx.setSubentity(k+"");
					 * utilhdx.setRestype("dynamic"); utilhdx.setUnit("个");
					 * utilhdxVector.addElement(utilhdx); //入口非单向传输数据包
					 * utilhdx=new UtilHdx(); utilhdx.setIpaddress(ipaddress);
					 * utilhdx.setCollecttime(date);
					 * utilhdx.setCategory("Interface");
					 * utilhdx.setChname("非单向传输数据包");
					 * utilhdx.setEntity("ifInNUcastPkts");
					 * utilhdx.setThevalue((String)rValue.get("PacketsReceivedNonUnicastPersec"));
					 * utilhdx.setSubentity(k+"");
					 * utilhdx.setRestype("dynamic"); utilhdx.setUnit("个");
					 * utilhdxVector.addElement(utilhdx); //入口单向传输数据包
					 * utilhdx=new UtilHdx(); utilhdx.setIpaddress(ipaddress);
					 * utilhdx.setCollecttime(date);
					 * utilhdx.setCategory("Interface");
					 * utilhdx.setChname("单向传输数据包");
					 * utilhdx.setEntity("ifInUcastPkts");
					 * utilhdx.setThevalue((String)rValue.get("PacketsReceivedUnicastPersec"));
					 * utilhdx.setSubentity(k+"");
					 * utilhdx.setRestype("dynamic"); utilhdx.setUnit("个");
					 * utilhdxVector.addElement(utilhdx); //出口非单向传输数据包
					 * utilhdx=new UtilHdx(); utilhdx.setIpaddress(ipaddress);
					 * utilhdx.setCollecttime(date);
					 * utilhdx.setCategory("Interface");
					 * utilhdx.setChname("非单向传输数据包");
					 * utilhdx.setEntity("ifOutNUcastPkts");
					 * utilhdx.setThevalue((String)rValue.get("PacketsSentNonUnicastPersec"));
					 * utilhdx.setSubentity(k+"");
					 * utilhdx.setRestype("dynamic"); utilhdx.setUnit("个");
					 * utilhdxVector.addElement(utilhdx); //出口单向传输数据包
					 * utilhdx=new UtilHdx(); utilhdx.setIpaddress(ipaddress);
					 * utilhdx.setCollecttime(date);
					 * utilhdx.setCategory("Interface");
					 * utilhdx.setChname("单向传输数据包");
					 * utilhdx.setEntity("ifOutUcastPkts");
					 * utilhdx.setThevalue((String)rValue.get("PacketsSentUnicastPersec"));
					 * utilhdx.setSubentity(k+"");
					 * utilhdx.setRestype("dynamic"); utilhdx.setUnit("个");
					 * utilhdxVector.addElement(utilhdx);
					 */
					iflist.add(ifhash);
					ifhash = new Hashtable();
					count++;
				}

			}
		}
		AllUtilHdx alloututilhdx = new AllUtilHdx();
		alloututilhdx = new AllUtilHdx();
		alloututilhdx.setIpaddress(host.getIpAddress());
		alloututilhdx.setCollecttime(date);
		alloututilhdx.setCategory("Interface");
		alloututilhdx.setEntity("AllOutBandwidthUtilHdx");
		alloututilhdx.setSubentity("AllOutBandwidthUtilHdx");
		alloututilhdx.setRestype("dynamic");
		alloututilhdx.setUnit("kb/s");
		alloututilhdx.setChname("出口流速");
		alloututilhdx.setThevalue(Long.toString(allOutHdx));
		allutilhdxVector.addElement(alloututilhdx);

		AllUtilHdx allInutilhdx = new AllUtilHdx();
		allInutilhdx = new AllUtilHdx();
		allInutilhdx.setIpaddress(host.getIpAddress());
		allInutilhdx.setCollecttime(date);
		allInutilhdx.setCategory("Interface");
		allInutilhdx.setEntity("AllInBandwidthUtilHdx");
		allInutilhdx.setSubentity("AllInBandwidthUtilHdx");
		allInutilhdx.setRestype("dynamic");
		allInutilhdx.setUnit("kb/s");
		allInutilhdx.setChname("入口流速");
		allInutilhdx.setThevalue(Long.toString(allInHdx));
		allutilhdxVector.addElement(allInutilhdx);
		
		AllUtilHdx allutilhdx = new AllUtilHdx();
		allutilhdx.setIpaddress(host.getIpAddress());
		allutilhdx.setCollecttime(date);
		allutilhdx.setCategory("Interface");
		allutilhdx.setEntity("AllBandwidthUtilHdx");
		allutilhdx.setSubentity("AllBandwidthUtilHdx");
		allutilhdx.setRestype("dynamic");
		allutilhdx.setUnit("kb/s");	
		allutilhdx.setChname("综合流速");
		allutilhdx.setThevalue(Long.toString(allInHdx+allOutHdx));	
		allutilhdxVector.addElement(allutilhdx);
		
		returnHash.put("interfaceNumber", count);
		
		// ----------------解析uname内容--创建监控项---------------------
		String unameContent = "";
		try {
			unameContent = telnet.send("uname -sn");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//
//		System.out.println("==========uname -sn==============");

		String[] unameLineArr = unameContent.split("\n");
		String[] uname_tmpData = null;
		for (int i = 0; i < unameLineArr.length; i++) {
			uname_tmpData = unameLineArr[i].split("\\s++");
			if (uname_tmpData.length == 2) {
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

		try {
			usergroupContent = telnet.send("cat /etc/group");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//
//		System.out.println("==========cat /etc/group==============");
//		System.out.println("==========10==============");

		String[] usergroupLineArr = usergroupContent.split("\n");
		String[] usergroup_tmpData = null;
		for (int i = 0; i < usergroupLineArr.length; i++) {
			usergroup_tmpData = usergroupLineArr[i].split(":");
			if (usergroup_tmpData.length >= 3) {
				usergrouphash.put((String) usergroup_tmpData[2], usergroup_tmpData[0]);
			}
		}

		// ----------------解析user内容--创建监控项---------------------
		String userContent = "";
		try {
			userContent = telnet.send("cat /etc/passwd");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		System.out.println("==========cat /etc/passwd==============");
//		System.out.println("==========11==============");

		String[] userLineArr = userContent.split("\n");
		String[] user_tmpData = null;
		for (int i = 0; i < userLineArr.length; i++) {
			// user_tmpData = userLineArr[i].split("\\s++");
			String[] result = userLineArr[i].trim().split(":");
			if (result.length > 4) {
				long userid = Long.parseLong(result[2]);
				long usergroupid = Long.parseLong(result[3]);
				// 小于500的为系统级用户,过滤
				if (userid < 500)
					continue;

				userdata = new Usercollectdata();
				userdata.setIpaddress(ipaddress);
				userdata.setCollecttime(date);
				userdata.setCategory("User");
				userdata.setEntity("Sysuser");
				String groupname = "";
				if (usergrouphash != null && usergrouphash.size() > 0) {
					if (usergrouphash.containsKey(usergroupid + "")) {
						groupname = (String) usergrouphash.get(usergroupid + "");
					}
				}
				userdata.setSubentity(groupname + "");
				userdata.setRestype("static");
				userdata.setUnit(" ");
				userdata.setThevalue(result[0]);
				userVector.addElement(userdata);
				continue;
			}

		}

		// ----------------解析date内容--创建监控项---------------------
		String dateContent = "";
		try {
			dateContent = telnet.send("date");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		System.out.println("==========date==============");
//		System.out.println("==========12==============");

		if (dateContent != null && dateContent.length() > 0) {
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
		try {
			uptimeContent = telnet.send("uptime");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (uptimeContent != null && uptimeContent.length() >= 29)
			uptimeContent=uptimeContent.substring(0, 28);
//		System.out.println("==========uptime==============");
//		System.out.println("==========13==============");

		if (uptimeContent != null && uptimeContent.length() > 0) {
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

		if (diskVector != null && diskVector.size() > 0) {
			returnHash.put("disk", diskVector);
			// 把采集结果生成sql
			HostdiskResultosql tosql = new HostdiskResultosql();
			tosql.CreateResultTosql(returnHash, host.getIpAddress());

			HostDatatempDiskRttosql temptosql = new HostDatatempDiskRttosql();
			temptosql.CreateResultTosql(returnHash, host);
			tosql = null;
			temptosql = null;
		}
		if (cpuVector != null && cpuVector.size() > 0) {
			returnHash.put("cpu", cpuVector);
			NetHostDatatempCpuRTosql totempsql = new NetHostDatatempCpuRTosql();
			totempsql.CreateResultTosql(returnHash, host);
			totempsql = null;
		}
		if (memoryVector != null && memoryVector.size() > 0) {
			returnHash.put("memory", memoryVector);
			// 把采集结果生成sql
			HostPhysicalMemoryResulttosql tosql = new HostPhysicalMemoryResulttosql();
			tosql.CreateResultTosql(returnHash, host.getIpAddress());
			NetHostMemoryRtsql totempsql = new NetHostMemoryRtsql();
			totempsql.CreateResultTosql(returnHash, host);
		}
		if (userVector != null && userVector.size() > 0) {
			returnHash.put("user", userVector);
			HostDatatempUserRtosql tosql = new HostDatatempUserRtosql();
			tosql.CreateResultTosql(returnHash, host);
		}
		if (processVector != null && processVector.size() > 0) {
			returnHash.put("process", processVector);
			// 把结果生成sql
			HostDatatempProcessRtTosql temptosql = new HostDatatempProcessRtTosql();
			temptosql.CreateResultTosql(returnHash, host);
		}
		if (systemVector != null && systemVector.size() > 0) {
			returnHash.put("system", systemVector);
			NetHostDatatempSystemRttosql tosql = new NetHostDatatempSystemRttosql();
			tosql.CreateResultTosql(returnHash, host);
		}
		if (nodeconfig != null) {
			returnHash.put("nodeconfig", nodeconfig);
			HostDatatempNodeconfRtosql tosql = new HostDatatempNodeconfRtosql();
			tosql.CreateResultTosql(returnHash, host);
		}
		if (iflist != null && iflist.size() > 0) {
			returnHash.put("iflist", iflist);
			HostDatatempiflistRtosql tosql = new HostDatatempiflistRtosql();
			tosql.CreateResultTosql(returnHash, host);
		}
		if (utilhdxVector != null && utilhdxVector.size() > 0) {
			returnHash.put("utilhdx", utilhdxVector);
			HostDatatemputilhdxRtosql tosql = new HostDatatemputilhdxRtosql();
			tosql.CreateResultTosql(returnHash, host);
		}
		if (allutilhdxVector != null && allutilhdxVector.size() > 0){
			returnHash.put("allutilhdx", allutilhdxVector);
			HostDatatempAllutilhdxRtosql tosql=new HostDatatempAllutilhdxRtosql();
			
			tosql.CreateResultTosql(returnHash, host);
		}
	
		if (interfaceVector != null && interfaceVector.size() > 0) {
			returnHash.put("interface", interfaceVector);
			HostDatatempinterfaceRtosql tosql = new HostDatatempinterfaceRtosql();
			tosql.CreateResultTosql(returnHash, host);
		}
		if (alldiskperf != null && alldiskperf.size() > 0) {
			returnHash.put("alldiskperf", alldiskperf);
			HostDatatempnDiskperfRtosql tosql = new HostDatatempnDiskperfRtosql();
			tosql.CreateResultTosql(returnHash, host);
		}
		if (cpuconfiglist != null && cpuconfiglist.size() > 0) {
			returnHash.put("cpuconfiglist", cpuconfiglist);
			HostDatatempCpuconfiRtosql tosql = new HostDatatempCpuconfiRtosql();
			tosql.CreateResultTosql(returnHash, host);
		}
		if (cpuperflist != null && cpuperflist.size() > 0) {
			returnHash.put("cpuperflist", cpuperflist);
			HostcpuResultTosql rtosql = new HostcpuResultTosql();
			rtosql.CreateLinuxResultTosql(returnHash, host.getIpAddress());

			HostDatatempCpuperRtosql tmptosql = new HostDatatempCpuperRtosql();
			tmptosql.CreateResultTosql(returnHash, host);
		}
		returnHash.put("collecttime", collecttime);

		HostDatatempCollecttimeRtosql tosql = new HostDatatempCollecttimeRtosql();
		tosql.CreateResultTosql(returnHash, host);
		System.out.println("end of get telnet monitor detail");

		return returnHash;
	}

	public static void main(String[] arg) {

		String cpuperfContent = "AIX appserv 3 5 0006B30FD600    04/13/10\n" +

		"System configuration: lcpu=8  mode=Capped " +

		"12:54:29    %usr    %sys    %wio   %idle   physc\n" +

		"12:54:30       0       0       0     100    4.01\n" + "12:54:31       0       0       0     100    4.00\n" + "12:54:32       0       0       0     100    4.00\n" +

		"\nAverage        0       0       0     100    4.00";
		System.out.println(cpuperfContent);

	}
}
