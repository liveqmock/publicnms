package com.afunms.polling.snmp;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import com.afunms.alarm.send.SendAlarmUtil;
import com.afunms.alarm.send.SendMailAlarm;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.Arith;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ErrptlogUtil;
import com.afunms.common.util.ReadErrptlog;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.ErrptconfigDao;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.model.Errptconfig;
import com.afunms.config.model.Errptlog;
import com.afunms.config.model.Nodeconfig;
import com.afunms.config.model.Nodecpuconfig;
import com.afunms.config.model.Procs;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
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
import com.gatherResulttosql.HostDatatempAllutilhdxRtosql;
import com.gatherResulttosql.HostDatatempCollecttimeRtosql;
import com.gatherResulttosql.HostDatatempCpuconfiRtosql;
import com.gatherResulttosql.HostDatatempCpuperRtosql;
import com.gatherResulttosql.HostDatatempDiskPeriofRtosql;
import com.gatherResulttosql.HostDatatempDiskRttosql;
import com.gatherResulttosql.HostDatatempErrptRtosql;
import com.gatherResulttosql.HostDatatempNodeconfRtosql;
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
import com.gatherResulttosql.HostvirtualmemoryResultTosql;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;
import com.gatherResulttosql.NetHostMemoryRtsql;
import com.gatherResulttosql.NetinterfaceResultTosql;

public class LoadAixFile {

    Host host;

    String ipaddress;

    Pattern pt = null;

    Matcher mc = null;

    Calendar date = Calendar.getInstance();

    Nodeconfig nodeconfig = new Nodeconfig();

    Processcollectdata processData = new Processcollectdata();

    Vector processVector = new Vector();

    Systemcollectdata systemdata = new Systemcollectdata();

    Memorycollectdata memorydata = new Memorycollectdata();

    Vector memoryVector = new Vector();

    Vector systemVector = new Vector();

    Vector diskVector = new Vector();

    Vector interfaceVector = new Vector();

    Vector utilhdxVector = new Vector();
    
    Vector errptlogVector = new Vector();

    Hashtable returnHash = new Hashtable();

    float physicalMenmoryTotalSize = 0;

    float physicalMenmoryFreeSize = 0;

    float allSwapSpace = 0;

    float freeSwapSpace = 0;

    float usedSwapSpace = 0;

    float pageSpaceUsedPercent = 0;

    Hashtable pagingHash = new Hashtable();

    Usercollectdata userdata = null;

    Vector userVector = new Vector();

    CPUcollectdata cpuData = new CPUcollectdata();

    Vector cpuVector = new Vector();

    Vector volumeVector = new Vector();

    List routeList = new ArrayList();

    CheckEventUtil checkUtil = new CheckEventUtil();

    AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();

    Hashtable ipAllData = new Hashtable();

    List iflist = new ArrayList();

    List allDiskPerfList = new ArrayList();

    List allDiskIOList = new ArrayList();

    List netmedialist = new ArrayList();

    List servicelist = new ArrayList();

    List cpuPerfListt = new ArrayList();

    Hashtable pagePerformanceHashtable = new Hashtable();

    List<Nodecpuconfig> cpuConfigList = new ArrayList<Nodecpuconfig>();

    Vector allutilhdxVector = new Vector();

    public LoadAixFile() {

    }

    // Edit by chen

    public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
        StringBuffer fileContent = new StringBuffer();
        host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
        if (host == null)
            return returnHash;
        ipaddress = host.getIpAddress();

        nodeconfig.setNodeid(host.getId());
        nodeconfig.setHostname(host.getAlias());

        ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
        if (ipAllData == null)
            ipAllData = new Hashtable();
        try {
            String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + host.getIpAddress() + ".log";
            FileInputStream fis = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String strLine = null;
            while ((strLine = br.readLine()) != null) {
                fileContent.append(strLine + "\n");
            }
            isr.close();
            fis.close();
            br.close();
        } catch (Exception e) {
            SysLogger.info(" LoadAixFile 无法找到指定文件 " + host.getIpAddress());
        }

        this.handleCollectTime(fileContent);
        this.handleVersion(fileContent);
        this.handleVmstat(fileContent);
        this.handlePageSpace(fileContent);
        this.handleSwap(fileContent);
        this.handleProcess(fileContent);
        this.handleCpu(fileContent);
        this.handleProcessor(fileContent);
        this.handleDisk(fileContent);
        this.handleDiskPerf(fileContent);
        this.handleDiskIO(fileContent);
        this.handleNet(fileContent);
        this.handleUnName(fileContent);
        this.handleService(fileContent);
        this.handleUserGroup(fileContent);
        this.handleUser(fileContent);
        this.handleDate(fileContent);
        this.handleUpTime(fileContent);
        this.handleErrpt(fileContent);
        this.handleVolume(fileContent);
        this.handleRoute(fileContent);
        this.processData();

        return returnHash;

    }

    // 处理采集时间
    public void handleCollectTime(StringBuffer fileContent) {
        String collectTimePart = null;
        pt = Pattern.compile("(cmdbegin:collecttime)(.*)(cmdbegin:version)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            collectTimePart = mc.group(2);
        }
        if (collectTimePart != null && collectTimePart.length() > 0) {
            collectTimePart = collectTimePart.trim();
        }
        returnHash.put("collecttime", collectTimePart);
    }

    // 处理系统版本
    public void handleVersion(StringBuffer fileContent) {
        String versionPart = "";
        pt = Pattern.compile("(cmdbegin:version)(.*)(cmdbegin:vmstat)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            versionPart = mc.group(2);
        }
        if (versionPart != null && versionPart.length() > 0 && versionPart.length() < 50) {
            nodeconfig.setCSDVersion(versionPart.trim());
            DBManager dbconn = new DBManager();
            String sql = "update topo_host_node set sys_descr='" + versionPart.trim() + "' where ip_address='" + host.getIpAddress() + "'";
            try {
                dbconn.executeUpdate(sql);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dbconn.close();
            }
        } else {
            nodeconfig.setCSDVersion("");
        }
    }

    // 处理vmstat
    public void handleVmstat(StringBuffer fileContent) {
        String vmstatPart = "";
        pt = Pattern.compile("(cmdbegin:vmstat)(.*)(cmdbegin:lsps)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            vmstatPart = mc.group(2);
        }
        String[] lineArray = null;
        String[] tempArray = null;
        try {
            lineArray = vmstatPart.split("\n");
            if (lineArray != null && lineArray.length > 0) {
                for (int i = 1; i < lineArray.length; i++) {
                    if (lineArray[i].trim().indexOf("System configuration:") >= 0) {
                        String memoryString = (lineArray[i].trim().substring(lineArray[i].trim().indexOf("mem="), lineArray[i].trim().length() - 2));
                        physicalMenmoryTotalSize = Float.parseFloat(memoryString.substring(memoryString.indexOf("=") + 1));
                        continue;
                    }
                    tempArray = lineArray[i].trim().split("\\s++");
                    if ((tempArray != null && tempArray.length == 17 || tempArray.length == 19)) {
                        if (tempArray[0] != null && !tempArray[0].equalsIgnoreCase("r")) {
                            physicalMenmoryFreeSize = Float.parseFloat(tempArray[3]) * 4 / 1024;
                            String re = tempArray[4];
                            String pi = tempArray[5];
                            String po = tempArray[6];
                            String fr = tempArray[7];
                            String sr = tempArray[8];
                            String cy = tempArray[9];
                            String iw = tempArray[16];
                            pagePerformanceHashtable.put("re", re);
                            pagePerformanceHashtable.put("pi", pi);
                            pagePerformanceHashtable.put("po", po);
                            pagePerformanceHashtable.put("fr", fr);
                            pagePerformanceHashtable.put("sr", sr);
                            pagePerformanceHashtable.put("cy", cy);
                            pagePerformanceHashtable.put("cy", cy);
                            pagePerformanceHashtable.put("iw", iw);
                        }
                    }
                }

                if (physicalMenmoryTotalSize > 0) {
                    float PhysicalMemUtilization = (physicalMenmoryTotalSize - physicalMenmoryFreeSize) * 100 / physicalMenmoryTotalSize;
                    // 物理总内存大小
                    memorydata = new Memorycollectdata();
                    memorydata.setCollecttime(date);
                    memorydata.setCategory("Memory");
                    memorydata.setEntity("Capability");
                    memorydata.setSubentity("PhysicalMemory");
                    memorydata.setRestype("static");
                    memorydata.setUnit("M");
                    memorydata.setThevalue(Float.toString(physicalMenmoryTotalSize));
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
                    memorydata.setThevalue(Float.toString(physicalMenmoryTotalSize - physicalMenmoryFreeSize));
                    memoryVector.addElement(memorydata);
                    // 物理内存使用率
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Hashtable checkHash = new Hashtable();
        checkHash.put("vmstat", pagePerformanceHashtable);
        try {
            List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix", "iowait");
            for (int k = 0; k < list.size(); k++) {
                AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(k);
                checkUtil.updateData(host, checkHash, "host", "aix", alarmIndicatorsnode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 处理换页空间
    public void handlePageSpace(StringBuffer fileContent) {
        String pageSpacePart = "";
        pt = Pattern.compile("(cmdbegin:lsps)(.*)(cmdbegin:swap)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            pageSpacePart = mc.group(2);
        }
        String[] lineArray = null;
        String[] temparray = null;
        String totalPageSpaceSizeStr = null;
        String pageSpaceUsedPercentStr = null;
        try {
            lineArray = pageSpacePart.split("\n");
            if (lineArray != null && lineArray.length >= 0) {
                temparray = lineArray[2].trim().split("\\s++");
                if (temparray != null && temparray.length >= 0) {
                    totalPageSpaceSizeStr = temparray[0].trim();
                    pageSpaceUsedPercentStr = temparray[1].trim();
                    pageSpaceUsedPercentStr = pageSpaceUsedPercentStr.replaceAll("%", "");
                    pagingHash.put("Total_Paging_Space", totalPageSpaceSizeStr);
                    pagingHash.put("Percent_Used", pageSpaceUsedPercentStr);
                    try {
                        // 对换页值进行告警检测
                        List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix", "pagingusage");
                        for (int i = 0; i < list.size(); i++) {
                            AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(i);
                            checkUtil.checkEvent(host, alarmIndicatorsnode, pageSpaceUsedPercentStr, "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 处理swap空间
    public void handleSwap(StringBuffer fileContent) {
        String swapPart = "";
        pt = Pattern.compile("(cmdbegin:swap)(.*)(cmdbegin:process\n)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            swapPart = mc.group(2);
        }
        String[] swapLineArr = null;
        String[] swapTmpArr = null;
        try {
            swapLineArr = swapPart.trim().split("\n");
            if (swapLineArr != null && swapLineArr.length > 0) {
                swapTmpArr = swapLineArr[0].trim().split("\\s++");
                if (swapTmpArr != null && swapTmpArr.length == 12) {
                    try {
                        allSwapSpace = Float.parseFloat(swapTmpArr[2].trim());
                        freeSwapSpace = Float.parseFloat(swapTmpArr[10].trim());
                        usedSwapSpace = Float.parseFloat(swapTmpArr[6].trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (allSwapSpace > 0) {
                    // Swap
                    memorydata = new Memorycollectdata();
                    memorydata.setIpaddress(ipaddress);
                    memorydata.setCollecttime(date);
                    memorydata.setCategory("Memory");
                    memorydata.setEntity("Capability");
                    memorydata.setSubentity("SwapMemory");
                    memorydata.setRestype("static");
                    memorydata.setUnit("M");
                    // 一个BLOCK是512byte
                    memorydata.setThevalue(Math.round(allSwapSpace / 1024) + "");
                    memoryVector.addElement(memorydata);

                    // 交换分区使用大小
                    memorydata = new Memorycollectdata();
                    memorydata.setIpaddress(ipaddress);
                    memorydata.setCollecttime(date);
                    memorydata.setCategory("Memory");
                    memorydata.setEntity("UsedSize");
                    memorydata.setSubentity("SwapMemory");
                    memorydata.setRestype("static");
                    memorydata.setUnit("M");
                    memorydata.setThevalue(Math.round(usedSwapSpace / 1024) + "");
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
                    memorydata.setThevalue(usedSwapSpace / allSwapSpace + "");
                    memoryVector.addElement(memorydata);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 处理process
    public void handleProcess(StringBuffer fileContent) {
        String processPart = "";
        pt = Pattern.compile("(cmdbegin:process)(.*)(cmdbegin:cpu)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            processPart = mc.group(2);
        }
        List formerProcsList = new ArrayList();
        ProcsDao procsDao = new ProcsDao();
        try {
            formerProcsList = procsDao.loadByIp(ipaddress);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            procsDao.close();
        }
        Hashtable formerProcsHashtable = new Hashtable();
        Vector formerProcsVector = new Vector();
        if (formerProcsList != null && formerProcsList.size() > 0) {
            for (int i = 0; i < formerProcsList.size(); i++) {
                Procs procs = (Procs) formerProcsList.get(i);
                formerProcsHashtable.put(procs.getProcname(), procs);
                formerProcsVector.add(procs.getProcname());
            }
        }
        String[] processLineArr = processPart.split("\n");
        String[] processTmpArr = null;
        for (int i = 1; i < processLineArr.length; i++) {
            processTmpArr = processLineArr[i].trim().split("\\s++");
            if ((processTmpArr != null) && (processTmpArr.length >= 11)) {
                String user = processTmpArr[0];// USER
                String pid = processTmpArr[1];// PID
                if ("USER".equalsIgnoreCase(user))
                    continue;
                String command = processTmpArr[10];// COMMAND
                String sTime = processTmpArr[8];// STIME
                String time = processTmpArr[9];// TIME
                if (processTmpArr.length > 11) {
                    command = processTmpArr[11];
                    sTime = processTmpArr[8] + processTmpArr[9];// STIME
                    time = processTmpArr[10];// cputime
                }
                String processType = "应用程序";
                String processCnState = "";
                String memorySize = processTmpArr[4];// memsize
                if (memorySize == null)
                    memorySize = "0";
                String memoryPercent = processTmpArr[3];// %mem
                String cpuPercent = processTmpArr[2];// %CPU
                String state = processTmpArr[7];// STAT
                if ("Z".equals(state)) {
                    processCnState = "僵尸进程";
                } else {
                    processCnState = "正在运行";
                }
                processData = new Processcollectdata();
                processData.setIpaddress(ipaddress);
                processData.setCollecttime(date);
                processData.setCategory("Process");
                processData.setEntity("process_id");
                processData.setSubentity(pid);
                processData.setRestype("dynamic");
                processData.setUnit(" ");
                processData.setThevalue(pid);
                processVector.addElement(processData);

                processData = new Processcollectdata();
                processData.setIpaddress(ipaddress);
                processData.setCollecttime(date);
                processData.setCategory("Process");
                processData.setEntity("USER");
                processData.setSubentity(pid);
                processData.setRestype("dynamic");
                processData.setUnit(" ");
                processData.setThevalue(user);
                processVector.addElement(processData);

                processData = new Processcollectdata();
                processData.setIpaddress(ipaddress);
                processData.setCollecttime(date);
                processData.setCategory("Process");
                processData.setEntity("MemoryUtilization");
                processData.setSubentity(pid);
                processData.setRestype("dynamic");
                processData.setUnit("%");
                processData.setThevalue(memoryPercent);
                processVector.addElement(processData);

                processData = new Processcollectdata();
                processData.setIpaddress(ipaddress);
                processData.setCollecttime(date);
                processData.setCategory("Process");
                processData.setEntity("Memory");
                processData.setSubentity(pid);
                processData.setRestype("static");
                processData.setUnit("K");
                processData.setThevalue(memorySize);
                processVector.addElement(processData);

                processData = new Processcollectdata();
                processData.setIpaddress(ipaddress);
                processData.setCollecttime(date);
                processData.setCategory("Process");
                processData.setEntity("Type");
                processData.setSubentity(pid);
                processData.setRestype("static");
                processData.setUnit(" ");
                processData.setThevalue(processType);
                processVector.addElement(processData);

                processData = new Processcollectdata();
                processData.setIpaddress(ipaddress);
                processData.setCollecttime(date);
                processData.setCategory("Process");
                processData.setEntity("Status");
                processData.setSubentity(pid);
                processData.setRestype("static");
                processData.setUnit(" ");
                processData.setThevalue(processCnState);
                processVector.addElement(processData);

                processData = new Processcollectdata();
                processData.setIpaddress(ipaddress);
                processData.setCollecttime(date);
                processData.setCategory("Process");
                processData.setEntity("Name");
                processData.setSubentity(pid);
                processData.setRestype("static");
                processData.setUnit(" ");
                processData.setThevalue(command);
                processVector.addElement(processData);

                processData = new Processcollectdata();
                processData.setIpaddress(ipaddress);
                processData.setCollecttime(date);
                processData.setCategory("Process");
                processData.setEntity("CpuTime");
                processData.setSubentity(pid);
                processData.setRestype("static");
                processData.setUnit("秒");
                processData.setThevalue(time);
                processVector.addElement(processData);

                processData = new Processcollectdata();
                processData.setIpaddress(ipaddress);
                processData.setCollecttime(date);
                processData.setCategory("Process");
                processData.setEntity("StartTime");
                processData.setSubentity(pid);
                processData.setRestype("static");
                processData.setUnit(" ");
                processData.setThevalue(sTime);
                processVector.addElement(processData);

                processData = new Processcollectdata();
                processData.setIpaddress(ipaddress);
                processData.setCollecttime(date);
                processData.setCategory("Process");
                processData.setEntity("CpuUtilization");
                processData.setSubentity(pid);
                processData.setRestype("dynamic");
                processData.setUnit("%");
                processData.setThevalue(cpuPercent);
                processVector.addElement(processData);
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
        systemdata.setThevalue(processLineArr.length + "");
        systemVector.addElement(systemdata);
    }

    public void handleCpu(StringBuffer fileContent) {
        String cpuPart = "";
        pt = Pattern.compile("(cmdbegin:cpu)(.*)(cmdbegin:allconfig)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            cpuPart = mc.group(2);
        }
        String[] lineArr = cpuPart.split("\n");
        String[] cpuTempArr = null;
        Hashtable<String, String> cpuperfhash = new Hashtable<String, String>();
        if (lineArr != null) {
            for (int i = 0; i < lineArr.length; i++) {
                cpuTempArr = lineArr[i].trim().split("\\s++");
                if (cpuTempArr != null && cpuTempArr.length == 5 || cpuTempArr.length == 6 || cpuTempArr.length == 7) {

                    if (cpuTempArr[0].trim().equalsIgnoreCase("Average") || "平均值".equals(cpuTempArr[0].trim())) {
                        cpuperfhash.put("%usr", cpuTempArr[1].trim());
                        cpuperfhash.put("%sys", cpuTempArr[2].trim());
                        cpuperfhash.put("%wio", cpuTempArr[3].trim());
                        cpuperfhash.put("%idle", cpuTempArr[4].trim());
                        if (cpuTempArr.length == 6 || cpuTempArr.length == 7) {
                            cpuperfhash.put("physc", cpuTempArr[5].trim());
                        }
                        cpuPerfListt.add(cpuperfhash);
                        cpuData = new CPUcollectdata();
                        cpuData.setIpaddress(ipaddress);
                        cpuData.setCollecttime(date);
                        cpuData.setCategory("CPU");
                        cpuData.setEntity("Utilization");
                        cpuData.setSubentity("Utilization");
                        cpuData.setRestype("dynamic");
                        cpuData.setUnit("%");
                        cpuData.setThevalue(Arith.round((100.0 - Double.parseDouble(cpuTempArr[4].trim())), 0) + "");
                        cpuVector.addElement(cpuData);
                    }
                }
            }
        }
    }

    // 处理processor
    public void handleProcessor(StringBuffer fileContent) {
        String processorconfigContent = "";
        List cpuNamesList = new ArrayList();
        pt = Pattern.compile("(cmdbegin:processor\n)(.*)(cmdbegin:cpuconfig\n)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            processorconfigContent = mc.group(2);
        }
        String[] processorconfigLineArr = processorconfigContent.trim().split("\n");
        if (processorconfigLineArr != null && processorconfigLineArr.length > 0) {
            nodeconfig.setNumberOfProcessors(processorconfigLineArr.length + "");// 设置节点的CPU配置个数
        }
        for (int i = 0; i < processorconfigLineArr.length; i++) {
            String processorLineToStringArr[] = processorconfigLineArr[i].split("\\s++");
            if(processorLineToStringArr == null || processorLineToStringArr.length < 3)continue;
            String temp = processorLineToStringArr[0] + ";" + processorLineToStringArr[2];
            cpuNamesList.add(temp);
        }
        pt = Pattern.compile("(cmdbegin:cpuconfig\n)(.*)(cmdbegin:allconfig\n)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            processorconfigContent = mc.group(2);
        }
        String[] cpuconfigLineArr = processorconfigContent.split("\n");
        Nodecpuconfig nodeCpuConfig = new Nodecpuconfig();
        String processorType = "";
        String processorSpeed = "";
        String cputype = "";
        for (int i = 0; i < cpuconfigLineArr.length; i++) {
            String[] result = null;
            if (cpuconfigLineArr[i].trim().contains("：")) {
                result = cpuconfigLineArr[i].trim().split("：");// 中文版的情形
            } else {
                result = cpuconfigLineArr[i].trim().split(":");// 英文版aix的情形
            }
            if (result.length > 0) {
                if (result[0].trim().equalsIgnoreCase("CPU Type") || "CPU 类型".equals(result[0].trim())) {
                    // CPU数据位
                    cputype = result[1].trim() + "";
                } else if (result[0].trim().equalsIgnoreCase("Processor Type") || "内核类型".equals(result[0].trim()) || "Kernel Type".equals(result[0].trim())) {
                    // CPU类型
                    processorType = result[1].trim() + "";
                } else if (result[0].trim().equalsIgnoreCase("Processor Clock Speed") || "处理器时钟速度".equals(result[0].trim())) {
                    // CPU内核主频
                    processorSpeed = result[1].trim() + "";
                    if (nodeconfig.getNumberOfProcessors() != null && nodeconfig.getNumberOfProcessors().trim().length() > 0) {
                        int pnum = Integer.parseInt(nodeconfig.getNumberOfProcessors());
                        for (int k = 0; k < pnum; k++) {
                            String temp[] = ((String) cpuNamesList.get(k)).split(";");
                            nodeCpuConfig.setDataWidth(cputype);
                            nodeCpuConfig.setProcessorId(temp[1]);
                            nodeCpuConfig.setName(temp[0]);
                            nodeCpuConfig.setNodeid(host.getId());
                            nodeCpuConfig.setL2CacheSize("");
                            nodeCpuConfig.setL2CacheSpeed("");
                            nodeCpuConfig.setProcessorType(processorType);
                            nodeCpuConfig.setProcessorSpeed(processorSpeed);
                            cpuConfigList.add(nodeCpuConfig);
                            nodeCpuConfig = new Nodecpuconfig();
                        }
                    }
                } else if (result[0].trim().equalsIgnoreCase("Memory Size") || "内存大小".equals(result[0].trim())) {
                    String allphy = result[1].trim() + "";
                    try {
                        allphy = allphy.replaceAll("MB", "");
                        if (physicalMenmoryTotalSize == 0) {
                            physicalMenmoryTotalSize = Float.parseFloat(allphy);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void handleDisk(StringBuffer fileContent) {
        List<String> diskNameList = new ArrayList<String>();// 磁盘名称集合
        String diskContent = "";
        pt = Pattern.compile("(cmdbegin:disk\n)(.*)(cmdbegin:diskperf)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            diskContent = mc.group(2);
        }
        String[] diskLineArr = diskContent.split("\n");
        String[] tmpData = null;
        Diskcollectdata diskdata = null;
        for (int i = 1; i < diskLineArr.length; i++) {
            tmpData = diskLineArr[i].split("\\s++");
            if ((tmpData != null) && (tmpData.length == 7)) {
                diskdata = new Diskcollectdata();
                diskdata.setIpaddress(host.getIpAddress());
                diskdata.setCollecttime(date);
                diskdata.setCategory("Disk");
                diskdata.setEntity("Utilization");// 利用百分比 %Used array[3]
                diskdata.setSubentity(tmpData[6]);
                diskdata.setRestype("static");
                diskdata.setUnit("%");
                try {
                    diskdata.setThevalue(Float.toString(Float.parseFloat(tmpData[3].substring(0, tmpData[3].indexOf("%")))));
                } catch (Exception ex) {
                    continue;
                }
                diskVector.addElement(diskdata);

                try {
                    String diskinc = "0.0";
                    float pastutil = 0.0f;
                    Vector disk_v = (Vector) ipAllData.get("disk");
                    if (disk_v != null && disk_v.size() > 0) {
                        for (int si = 0; si < disk_v.size(); si++) {
                            Diskcollectdata disk_data = (Diskcollectdata) disk_v.elementAt(si);
                            if ((tmpData[6]).equals(disk_data.getSubentity()) && "Utilization".equals(disk_data.getEntity())) {
                                pastutil = Float.parseFloat(disk_data.getThevalue());
                            }
                        }
                    } else {
                        pastutil = Float.parseFloat(tmpData[3].substring(0, tmpData[3].indexOf("%")));
                    }
                    if (pastutil == 0) {
                        pastutil = Float.parseFloat(tmpData[3].substring(0, tmpData[3].indexOf("%")));
                    }
                    if (Float.parseFloat(tmpData[3].substring(0, tmpData[3].indexOf("%"))) - pastutil > 0) {
                        diskinc = (Float.parseFloat(tmpData[3].substring(0, tmpData[3].indexOf("%"))) - pastutil) + "";
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

                diskdata = new Diskcollectdata();
                diskdata.setIpaddress(host.getIpAddress());
                diskdata.setCollecttime(date);
                diskdata.setCategory("Disk");
                diskdata.setEntity("AllSize");// 总空间 MB blocks arry[1]
                diskdata.setSubentity(tmpData[6]);
                diskdata.setRestype("static");
                float allblocksize = Float.parseFloat(tmpData[1]);
                float allSizeResult = 0.0f;
                if (!diskdata.getSubentity().equals("")) {
                    diskNameList.add(diskdata.getSubentity());
                }
                if (allblocksize >= 1024.0f) {
                    allSizeResult = allblocksize / 1024;
                    diskdata.setUnit("G");
                } else {
                    allSizeResult = allblocksize;
                    diskdata.setUnit("M");
                }
                diskdata.setThevalue(Float.toString(allSizeResult));
                diskVector.addElement(diskdata);

                diskdata = new Diskcollectdata();
                diskdata.setIpaddress(host.getIpAddress());
                diskdata.setCollecttime(date);
                diskdata.setCategory("Disk");
                diskdata.setEntity("UsedSize");// 使用大小
                diskdata.setSubentity(tmpData[6]);
                diskdata.setRestype("static");
                float FreeintSize = Float.parseFloat(tmpData[2]);
                float usedfloatsize = allblocksize - FreeintSize;
                if (usedfloatsize >= 1024.0f) {
                    usedfloatsize = usedfloatsize / 1024;
                    diskdata.setUnit("G");
                } else {
                    diskdata.setUnit("M");
                }
                diskdata.setThevalue(Float.toString(usedfloatsize));
                diskVector.addElement(diskdata);
            }

            try {
                CheckEventUtil checkutil = new CheckEventUtil();
                AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
                AlarmIndicatorsNode alarmIndicatorsnode = new AlarmIndicatorsNode();
                // 进行磁盘利用率告警检测
                List diskpercList = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix", "diskperc");
                for (int n = 0; n < diskpercList.size(); n++) {
                    alarmIndicatorsnode = (AlarmIndicatorsNode) diskpercList.get(n);
                    checkutil.checkDisk(host, diskVector, alarmIndicatorsnode);
                }
                // 进行磁盘利用增长率告警检测
                List diskincList = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix", "diskinc");
                for (int m = 0; m < diskincList.size(); m++) {
                    alarmIndicatorsnode = (AlarmIndicatorsNode) diskincList.get(m);
                    checkutil.checkDisk(host, diskVector, alarmIndicatorsnode);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 磁盘性能
    public void handleDiskPerf(StringBuffer fileContent) {
        String diskPerffPart = "";
        pt = Pattern.compile("(cmdbegin:diskperf)(.*)(cmdbegin:diskiostat)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            diskPerffPart = mc.group(2);
        }
        String[] diskperfLineArr = diskPerffPart.split("\n");
        String[] diskperf_tmpData = null;

        Hashtable<String, String> diskPerfHash = new Hashtable<String, String>();
        int flag = 0;
        for (int i = 0; i < diskperfLineArr.length; i++) {
            diskperf_tmpData = diskperfLineArr[i].trim().split("\\s++");
            if (diskperf_tmpData != null && (diskperf_tmpData.length == 7 || diskperf_tmpData.length == 8)) {
                if (diskperf_tmpData[0].trim().equalsIgnoreCase("Average")) {
                    flag = 1;
                    diskPerfHash.put("%busy", diskperf_tmpData[2].trim());
                    diskPerfHash.put("avque", diskperf_tmpData[3].trim());
                    diskPerfHash.put("r+w/s", diskperf_tmpData[4].trim());
                    diskPerfHash.put("Kbs/s", diskperf_tmpData[5].trim());
                    diskPerfHash.put("avwait", diskperf_tmpData[6].trim());
                    diskPerfHash.put("avserv", diskperf_tmpData[7].trim());
                    diskPerfHash.put("disklebel", diskperf_tmpData[1].trim());
                    allDiskPerfList.add(diskPerfHash);
                } else if (flag == 1) {
                    diskPerfHash.put("%busy", diskperf_tmpData[1].trim());
                    diskPerfHash.put("avque", diskperf_tmpData[2].trim());
                    diskPerfHash.put("r+w/s", diskperf_tmpData[3].trim());
                    diskPerfHash.put("Kbs/s", diskperf_tmpData[4].trim());
                    diskPerfHash.put("avwait", diskperf_tmpData[5].trim());
                    diskPerfHash.put("avserv", diskperf_tmpData[6].trim());
                    diskPerfHash.put("disklebel", diskperf_tmpData[0].trim());
                    allDiskPerfList.add(diskPerfHash);
                }
                diskPerfHash = new Hashtable();
            }
        }
    }

    public void handleDiskIO(StringBuffer fileContent) {
        String diskIOPart = "";
        pt = Pattern.compile("(cmdbegin:diskiostat)(.*)(cmdbegin:netperf)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            diskIOPart = mc.group(2);
        }
        String[] diskioLineArr = diskIOPart.split("\n");
        String[] diskio_tmpData = null;
        Hashtable<String, String> diskpiohash = new Hashtable<String, String>();
        int flags = 0;
        for (int i = 0; i < diskioLineArr.length; i++) {
            diskio_tmpData = diskioLineArr[i].trim().split("\\s++");
            if (diskio_tmpData != null) {
                if (diskio_tmpData[0].trim().equalsIgnoreCase("Disks:") || "磁盘：".equals(diskio_tmpData[0].trim())) {
                    flags = 1;
                    continue;
                }
                if (flags == 1) {
                    diskpiohash.put("Disks", diskio_tmpData[0].trim());
                    diskpiohash.put("%tm_act", diskio_tmpData[1].trim());
                    diskpiohash.put("Kbps", diskio_tmpData[2].trim());
                    diskpiohash.put("tps", diskio_tmpData[3].trim());
                    diskpiohash.put("kb_read", diskio_tmpData[4].trim());
                    diskpiohash.put("kb_wrtn", diskio_tmpData[5].trim());
                    allDiskIOList.add(diskpiohash);
                }
                diskpiohash = new Hashtable();
            }
        }
    }

    // 处理网路
    public void handleNet(StringBuffer fileContent) {
        // 取得工作网卡的实例
        String netperfContent = "";
        pt = Pattern.compile("(cmdbegin:netperf)(.*)(cmdbegin:netallperf)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            netperfContent = mc.group(2);
        }
        String[] netPerfLineArr = netperfContent.split("\n");
        String[] netPerfTempArr = null;
        List ifNameList = new ArrayList();

        for (int i = 0; i < netPerfLineArr.length; i++) {
            netPerfTempArr = netPerfLineArr[i].trim().split("\\s++");
            if (netPerfTempArr != null && netPerfTempArr.length == 9) {
                if (netPerfTempArr[0].trim().indexOf("en") >= 0 && netPerfTempArr[2].trim().indexOf("link") >= 0) {
                    ifNameList.add(netPerfTempArr[0].trim());
                }
            } else if (netPerfTempArr != null && netPerfTempArr.length == 10) {
                if (netPerfTempArr[0].trim().indexOf("en") >= 0 && netPerfTempArr[2].trim().indexOf("link") >= 0) {
                    ifNameList.add(netPerfTempArr[0].trim());
                }
            }

        }

        List formerIfList = new ArrayList();
        Hashtable netmediahash = new Hashtable();
        String netAllPerfContent = "";
        pt = Pattern.compile("(cmdbegin:netallperf)(.*)(cmdbegin:uname)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            netAllPerfContent = mc.group(2);
        }
        String[] netallperfLineArr = netAllPerfContent.trim().split("\n");
        String MAC = "";
        if (ipAllData != null) {
            formerIfList = (List) ipAllData.get("iflist");
        }
        Long allInHdx = 0l;
        Long allOutHdx = 0l;
        int count = 0;

        if (ifNameList != null && ifNameList.size() > 0) {
            Interfacecollectdata interfacedata = null;
            for (int k = 0; k < ifNameList.size(); k++) {
                Hashtable currentIfHashtable = new Hashtable();
                Hashtable formerIfHashtable = new Hashtable();// 用来保存上次采集结果
                if (formerIfList != null && formerIfList.size() > 0) {
                    formerIfHashtable = (Hashtable) formerIfList.get(k);
                }
                String portDesc = (String) ifNameList.get(k);// 网口描述
                String mediaSpeed = "";// 网卡带宽
                String LinkStatus = "";// 网卡状态
                String Bytes = "";// 网卡输入输出字节数
                String Packets = "";// 输入输出数据包

                // 模式一： ETHERNET STATISTICS： netflg="neten"
                // 模式二： Hardware Address: netflg="netmac"
                String netflg = "";
                if (netAllPerfContent.indexOf("ETHERNET STATISTICS (" + portDesc) > 0) {
                    netflg = "neten";
                }
                for (int i = 0; i < netallperfLineArr.length; i++) {
                    if (i == 1 && netallperfLineArr[i].indexOf("Hardware Address:") >= 0) {
                        netflg = "netmac";
                        break;
                    }
                }

                if (netflg.equals("neten")) {
                    pt = Pattern.compile("(start-" + portDesc + ")(.*)(end-" + portDesc + ")", Pattern.DOTALL);
                    mc = pt.matcher(fileContent.toString());
                    String netenContent = "";
                    if (mc.find()) {
                        netenContent = mc.group(2);
                    }

                    String[] netLineArr = null;
                    netLineArr = netenContent.trim().split("\n");
                    MAC = netLineArr[3].trim().substring(netLineArr[3].trim().indexOf("Hardware Address:"));
                    MAC = MAC.replaceAll("Hardware Address:", "").trim();
                    try {
                        mediaSpeed = "1000 Mbps Full Duplex";
                        LinkStatus = "Up";
                        Packets = netLineArr[8].trim();
                        Bytes = netLineArr[9].trim();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (netflg.equals("netmac")) {
                    pt = Pattern.compile("(start-" + portDesc + ")(.*)(end-" + portDesc + ")", Pattern.DOTALL);
                    mc = pt.matcher(fileContent.toString());
                    String netenContent = "";
                    if (mc.find()) {
                        netenContent = mc.group(2);
                    }
                    String[] netLineArr = null;
                    netLineArr = netenContent.trim().split("\n");

                    if (netLineArr.length == 13) {
                        try {
                            if (netLineArr[0].indexOf("Hardware Address:") >= 0) {
                                MAC = netLineArr[0].replaceAll("Hardware Address:", "").trim();
                            }
                            if (netLineArr[11].indexOf("Media Speed Running:") >= 0) {
                                mediaSpeed = netLineArr[11].replaceAll("Media Speed Running:", "").trim();
                            }
                            if (netLineArr[10].indexOf("Link Status :") >= 0) {
                                LinkStatus = netLineArr[10].replaceAll("Link Status :", "").trim();
                            }
                            Packets = netLineArr[1].trim();
                            Bytes = netLineArr[2].trim();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                String tempMac = nodeconfig.getMac();
                if (tempMac != null && tempMac.trim().length() > 0) {
                    // 由于网卡过多，这个数值过把界面弄大了
                    if (k < 3) {
                        tempMac = tempMac + "," + MAC;
                    }
                    nodeconfig.setMac(tempMac);
                } else {
                    nodeconfig.setMac(MAC);
                }

                DBManager dbconn = new DBManager();
                String sql = "update topo_host_node set bridge_address='" + nodeconfig.getMac() + "' where ip_address='" + host.getIpAddress() + "'";
                try {
                    dbconn.executeUpdate(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dbconn.close();
                }

                netmediahash.put("desc", portDesc);
                netmediahash.put("speed", mediaSpeed);
                netmediahash.put("mac", MAC);
                netmediahash.put("status", LinkStatus);
                netmedialist.add(netmediahash);
                netmediahash = new Hashtable();

                String currentOutPackets = "0";
                String currentInPackets = "0";
                if (Packets.indexOf("Packets:") >= 0) {
                    String[] packsTempArr = Packets.split("\\s++");
                    currentOutPackets = packsTempArr[1];// 发送的数据包
                    currentInPackets = packsTempArr[3];// 接受的数据包
                }
                String formerOutPackets = "0";
                String formerInPackets = "0";
                String betweenOutPackets = "0";
                String betweenInPackets = "0";
                if (formerIfHashtable != null && formerIfHashtable.size() > 0) {
                    if (formerIfHashtable.containsKey("outPackets")) {
                        formerOutPackets = (String) formerIfHashtable.get("outPackets");
                    }
                    try {
                        betweenOutPackets = (Long.parseLong(currentOutPackets) - Long.parseLong(formerOutPackets)) + "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (formerIfHashtable.containsKey("inPackets")) {
                        formerInPackets = (String) formerIfHashtable.get("inPackets");
                    }
                    try {
                        betweenInPackets = (Long.parseLong(currentInPackets) - Long.parseLong(formerInPackets)) + "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                String currentOutBytes = "0";
                String currentInBytes = "0";
                if (Bytes.indexOf("Bytes:") >= 0) {
                    String[] bytesTempArr = Bytes.split("\\s++");
                    currentOutBytes = bytesTempArr[1];// 发送的字节
                    currentInBytes = bytesTempArr[3];// 接受的字节
                }

                String formerOutBytes = "0";
                String formerInBytes = "0";
                String betweenOutBytes = "0";
                String betweenInBytes = "0";
                if (formerIfHashtable != null && formerIfHashtable.size() > 0) {
                    if (formerIfHashtable.containsKey("outBytes")) {
                        formerOutBytes = (String) formerIfHashtable.get("outBytes");
                    }
                    try {
                        betweenOutBytes = (Long.parseLong(currentOutBytes) - Long.parseLong(formerOutBytes)) * 8 / 1024 / 300 + "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (formerIfHashtable.containsKey("inBytes")) {
                        formerInBytes = (String) formerIfHashtable.get("inBytes");
                    }
                    try {
                        betweenInBytes = (Long.parseLong(currentInBytes) - Long.parseLong(formerInBytes)) * 8 / 1024 / 300 + "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (LinkStatus.equals("Up")) {
                    LinkStatus = "1";
                } else if (LinkStatus.equals("Down")) {
                    LinkStatus = "2";
                }

                String speedUnit = "";
                String speedValue = "";
                String[] speedTempArr = mediaSpeed.split("\\s++");
                try {
                    speedValue = speedTempArr[0].trim();
                    speedUnit = speedTempArr[1].trim();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentIfHashtable.put("outPackets", currentOutPackets);
                currentIfHashtable.put("inPackets", currentInPackets);
                currentIfHashtable.put("outBytes", currentOutBytes);
                currentIfHashtable.put("inBytes", currentInBytes);

                // 端口索引
                interfacedata = new Interfacecollectdata();
                interfacedata.setIpaddress(ipaddress);
                interfacedata.setCollecttime(date);
                interfacedata.setCategory("Interface");
                interfacedata.setEntity("index");
                interfacedata.setSubentity(k + 1 + "");
                interfacedata.setRestype("static");
                interfacedata.setUnit("");
                interfacedata.setThevalue(k + 1 + "");
                interfacedata.setChname("端口索引");
                interfaceVector.addElement(interfacedata);
                // 端口描述
                interfacedata = new Interfacecollectdata();
                interfacedata.setIpaddress(ipaddress);
                interfacedata.setCollecttime(date);
                interfacedata.setCategory("Interface");
                interfacedata.setEntity("ifDescr");
                interfacedata.setSubentity(k + 1 + "");
                interfacedata.setRestype("static");
                interfacedata.setUnit("");
                interfacedata.setThevalue(portDesc);
                interfacedata.setChname("端口描述2");
                interfaceVector.addElement(interfacedata);
                // 端口带宽
                interfacedata = new Interfacecollectdata();
                interfacedata.setIpaddress(ipaddress);
                interfacedata.setCollecttime(date);
                interfacedata.setCategory("Interface");
                interfacedata.setEntity("ifSpeed");
                interfacedata.setSubentity(k + 1 + "");
                interfacedata.setRestype("static");
                interfacedata.setUnit(speedUnit);
                interfacedata.setThevalue(speedValue);
                interfacedata.setChname("");
                interfaceVector.addElement(interfacedata);
                // 当前状态
                interfacedata = new Interfacecollectdata();
                interfacedata.setIpaddress(ipaddress);
                interfacedata.setCollecttime(date);
                interfacedata.setCategory("Interface");
                interfacedata.setEntity("ifOperStatus");
                interfacedata.setSubentity(k + 1 + "");
                interfacedata.setRestype("static");
                interfacedata.setUnit("");
                interfacedata.setThevalue(LinkStatus);
                interfacedata.setChname("当前状态");
                interfaceVector.addElement(interfacedata);
                // 当前状态
                interfacedata = new Interfacecollectdata();
                interfacedata.setIpaddress(ipaddress);
                interfacedata.setCollecttime(date);
                interfacedata.setCategory("Interface");
                interfacedata.setEntity("ifOperStatus");
                interfacedata.setSubentity(k + 1 + "");
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
                utilhdx.setEntity("InBandwidthUtilHdx");
                utilhdx.setThevalue(betweenInBytes);
                utilhdx.setSubentity(k + 1 + "");
                utilhdx.setRestype("dynamic");
                utilhdx.setUnit("Kb/秒");
                utilhdx.setChname(k + 1 + "端口入口" + "流速");
                allInHdx = allInHdx + Long.parseLong(betweenInBytes);
                utilhdxVector.addElement(utilhdx);
                // 端口出口流速
                utilhdx = new UtilHdx();
                utilhdx.setIpaddress(ipaddress);
                utilhdx.setCollecttime(date);
                utilhdx.setCategory("Interface");
                utilhdx.setEntity("OutBandwidthUtilHdx");
                utilhdx.setThevalue(betweenOutBytes);
                utilhdx.setSubentity(k + 1 + "");
                utilhdx.setRestype("dynamic");
                utilhdx.setUnit("Kb/秒");
                utilhdx.setChname(k + 1 + "端口出口" + "流速");
                allOutHdx = allOutHdx + Long.parseLong(betweenOutBytes);
                utilhdxVector.addElement(utilhdx);
                iflist.add(currentIfHashtable);
                currentIfHashtable = new Hashtable();
                count++;

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
        allutilhdx.setThevalue(Long.toString(allInHdx + allOutHdx));
        allutilhdxVector.addElement(allutilhdx);

        returnHash.put("interfaceNumber", count);
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
    }

    public void handleUnName(StringBuffer fileContent) {
        String unamePart = "";
        pt = Pattern.compile("(cmdbegin:uname)(.*)(cmdbegin:service)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            unamePart = mc.group(2);
        }
        String[] unameLineArr = unamePart.split("\n");
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
                
                DBManager dbconn = new DBManager();
                String sql = "update topo_host_node set sys_name='" + uname_tmpData[1] + "' where ip_address='" + host.getIpAddress() + "'";
                try {
                    dbconn.executeUpdate(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dbconn.close();
                }

            }
        }
    }

    public void handleService(StringBuffer fileContent) {
        Hashtable service = new Hashtable();
        String serviceContent = "";
        pt = Pattern.compile("(cmdbegin:service)(.*)(cmdbegin:usergroup)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            serviceContent = mc.group(2);
        }
        String[] serviceLineArr = serviceContent.split("\n");
        String[] service_tmpData = null;
        for (int i = 0; i < serviceLineArr.length; i++) {
            service_tmpData = serviceLineArr[i].trim().split("\\s++");
            if (service_tmpData != null && service_tmpData.length >= 3) {
                if ("Subsystem".equalsIgnoreCase(service_tmpData[0]))
                    continue;
                if (service_tmpData.length == 4) {
                    // 启动的情况下,有PID
                    try {
                        service.put("DisplayName", service_tmpData[0]);
                        service.put("groupstr", service_tmpData[1]);
                        service.put("pid", service_tmpData[2]);
                        service.put("State", service_tmpData[3]);
                        servicelist.add(service);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    service = new Hashtable();
                } else {
                    // 未启动情况下没有PID
                    try {
                        service.put("DisplayName", service_tmpData[0]);
                        service.put("groupstr", service_tmpData[1]);
                        service.put("State", service_tmpData[2]);
                        service.put("pid", "");
                        servicelist.add(service);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    service = new Hashtable();
                }

            } else if (service_tmpData != null && service_tmpData.length == 2) {
                // 启动的情况下,有PID
                try {
                    service.put("DisplayName", service_tmpData[0]);
                    service.put("groupstr", "");
                    service.put("pid", "");
                    service.put("State", service_tmpData[1]);
                    servicelist.add(service);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                service = new Hashtable();
            }
        }
    }

    public void handleUserGroup(StringBuffer fileContent) {
        Hashtable usergroupHash = new Hashtable();
        String userGroupPart = "";
        pt = Pattern.compile("(cmdbegin:usergroup)(.*)(cmdbegin:user\n)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            userGroupPart = mc.group(2);
        }
        String[] usergroupLineArr = userGroupPart.split("\n");
        String[] usergroup_tmpData = null;
        for (int i = 0; i < usergroupLineArr.length; i++) {
            usergroup_tmpData = usergroupLineArr[i].split(":");
            if (usergroup_tmpData.length >= 3) {
                usergroupHash.put((String) usergroup_tmpData[2], usergroup_tmpData[0]);
            }
        }
    }

    public void handleUser(StringBuffer fileContent) {
        String userPart = "";
        pt = Pattern.compile("(cmdbegin:user\n)(.*)(cmdbegin:date)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            userPart = mc.group(2);
        }
        String[] userLineArr = userPart.split("\n");
        for (int i = 0; i < userLineArr.length; i++) {
            String[] result = userLineArr[i].trim().split("\\s++");
            if (result.length >= 4) {
                String userName = result[0];
                String groupStr = result[3];
                String[] groups = groupStr.split("=");
                String group = "";
                if (groups != null && groups.length == 2) {
                    group = groups[1];
                }
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
    }

    public void handleDate(StringBuffer fileContent) {
        String datePart = "";
        pt = Pattern.compile("(cmdbegin:date)(.*)(cmdbegin:uptime)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            datePart = mc.group(2);
        }
        if (datePart != null && datePart.length() > 0) {
            systemdata = new Systemcollectdata();
            systemdata.setIpaddress(ipaddress);
            systemdata.setCollecttime(date);
            systemdata.setCategory("System");
            systemdata.setEntity("Systime");
            systemdata.setSubentity("Systime");
            systemdata.setRestype("static");
            systemdata.setUnit(" ");
            systemdata.setThevalue(datePart.trim());
            systemVector.addElement(systemdata);

        }
    }

    public void handleUpTime(StringBuffer fileContent) {
        String uptimePart = "";
        pt = Pattern.compile("(cmdbegin:uptime)(.*)(cmdbegin:errpt)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            uptimePart = mc.group(2);
        }
        if (uptimePart != null && uptimePart.length() > 0) {
            systemdata = new Systemcollectdata();
            systemdata.setIpaddress(ipaddress);
            systemdata.setCollecttime(date);
            systemdata.setCategory("System");
            systemdata.setEntity("SysUptime");
            systemdata.setSubentity("SysUptime");
            systemdata.setRestype("static");
            systemdata.setUnit(" ");
            systemdata.setThevalue(uptimePart.trim());
            systemVector.addElement(systemdata);
        }
    }
    
    public void handleErrpt(StringBuffer fileContent) {
		String errptlogContent = "";
		pt = Pattern.compile("(cmdbegin:errpt)(.*)(cmdbegin:volume)",Pattern.DOTALL);
		mc = pt.matcher(fileContent.toString());
		if(mc.find()){
			errptlogContent = mc.group(2);
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
			ErrptconfigDao dao = new ErrptconfigDao();
			Errptconfig errptconfig = new Errptconfig();
			try{
				errptconfig = dao.loadErrptconfigByNodeid(host.getId());
			}catch(Exception e){
				
			}finally{
				dao.close();
			}
			SendMailAlarm sendMailAlarm = new SendMailAlarm();
			//int index = 0;
			try{
				for(int i = 0 ; i < list.size() ; i++){
					Errptlog errptlog = (Errptlog)list.get(i);
					errptlog.setHostid(host.getId()+"");
					errptlogVector.add(list.get(i));
					//进行告警判断
					//errptlog 满足告警条件
					if(errptconfig != null){
						if(errptconfig.getErrpttype().contains(errptlog.getErrpttype().toLowerCase()) || errptconfig.getErrptclass().contains(errptlog.getErrptclass().toLowerCase())){
							//index++;
							//if(index == 1){
								//System.out.println("errptlog.getErrpttype().toLowerCase()"+errptlog.getErrpttype().toLowerCase());
								EventList eventlist = new EventList();
					    		eventlist.setEventtype("poll");
					    		eventlist.setEventlocation(host.getSysLocation());
					    		eventlist.setContent("设备IP："+host.getIpAddress()+" 设备名称："+host.getAlias()+"， errpt告警信息    级别："+ErrptlogUtil.getTypename(errptlog.getErrpttype())+" 种类:"+ErrptlogUtil.getClassname(errptlog.getErrptclass()));
					    		//eventlist.setContent("errpt告警信息    级别："+ErrptlogUtil.getTypename(errptlog.getErrpttype())+" 种类:"+ErrptlogUtil.getClassname(errptlog.getErrptclass()));
					    		eventlist.setLevel1(1);
					    		eventlist.setManagesign(0);
					    		eventlist.setBak("");
					    		eventlist.setRecordtime(Calendar.getInstance());
					    		eventlist.setReportman("系统轮询");
					    		eventlist.setBusinessid(host.getBid());
					    		eventlist.setNodeid(host.getId());
					    		eventlist.setOid(0);
					    		eventlist.setSubtype("host");
					    		eventlist.setSubentity("errptlog");
					    		EventListDao eventlistdao = null;
					    		try {
					    			eventlistdao = new EventListDao();
									eventlistdao.save(eventlist);
								} catch (Exception e) {
									e.printStackTrace();
								} finally{
									if(eventlistdao != null){
										eventlistdao.close();
									}
								}
					    		//生成格式为xml的errptlog告警文件
					    		//sendMailAlarm.BuildEventXMLDoc(eventlist);  
					    		SendAlarmUtil sendAlarmUtil = new SendAlarmUtil();
					    		//发送告警
					    		sendAlarmUtil.sendAlarmNoIndicator(errptconfig.getAlarmwayid(), eventlist);
							//}
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
		}
    }

    public void handleVolume(StringBuffer fileContent) {
        String volumeContent = "";
        pt = Pattern.compile("(cmdbegin:volume)(.*)(cmdbegin:route)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            volumeContent = mc.group(2);
        }
        String[] volumeLineArr = volumeContent.split("\n");
        String[] volumetmpData = null;
        for (int i = 1; i < volumeLineArr.length; i++) {

            volumetmpData = volumeLineArr[i].split("\\s++");
            if ((volumetmpData != null) && (volumetmpData.length == 4 || volumetmpData.length == 3)) {
                Hashtable volumeHash = new Hashtable();
                volumeHash.put("disk", volumetmpData[0]);
                volumeHash.put("pvid", volumetmpData[1]);
                volumeHash.put("vg", volumetmpData[2]);
                if (volumetmpData.length == 4) {
                    volumeHash.put("status", volumetmpData[3]);
                } else {
                    volumeHash.put("status", "-");
                }
                volumeVector.addElement(volumeHash);
            }
        }
    }

    public void handleRoute(StringBuffer fileContent) {
        String routePart = "";
        pt = Pattern.compile("(cmdbegin:route)(.*)(cmdbegin:end)", Pattern.DOTALL);
        mc = pt.matcher(fileContent.toString());
        if (mc.find()) {
            routePart = mc.group(2);
        }
        String[] routeLineArr = routePart.split("\n");
        String[] routetmpData = null;
        for (int i = 1; i < routeLineArr.length; i++) {
            routeList.add(routeLineArr[i]);
            routetmpData = routeLineArr[i].split("\\s++");
            if ((routetmpData != null) && (routetmpData.length == 4 || routetmpData.length == 3)) {
                Hashtable volumeHash = new Hashtable();
                volumeHash.put("disk", routetmpData[0]);
                volumeHash.put("pvid", routetmpData[1]);
                volumeHash.put("vg", routetmpData[2]);
                if (routetmpData.length == 4) {
                    volumeHash.put("status", routetmpData[3]);
                } else {
                    volumeHash.put("status", "-");
                }
                volumeVector.addElement(volumeHash);
            }
        }
    }

    public void processData() {
        String runmodel = PollingEngine.getCollectwebflag();
        if (diskVector != null && diskVector.size() > 0) {
            returnHash.put("disk", diskVector);
            HostdiskResultosql tosql = new HostdiskResultosql();
            tosql.CreateResultTosql(returnHash, host.getIpAddress());
            if (!"0".equals(runmodel)) {
                HostDatatempDiskRttosql temptosql = new HostDatatempDiskRttosql();
                temptosql.CreateResultTosql(returnHash, host);
            }
        }
        if (cpuVector != null && cpuVector.size() > 0) {
            returnHash.put("cpu", cpuVector);
            HostcpuResultTosql restosql = new HostcpuResultTosql();
            restosql.CreateResultTosql(returnHash, host.getIpAddress());
            if (!"0".equals(runmodel)) {
                NetHostDatatempCpuRTosql totempsql = new NetHostDatatempCpuRTosql();
                totempsql.CreateResultTosql(returnHash, host);
            }
        }

        if (memoryVector != null && memoryVector.size() > 0) {
            returnHash.put("memory", memoryVector);
            HostPhysicalMemoryResulttosql tosql = new HostPhysicalMemoryResulttosql();
            tosql.CreateResultTosql(returnHash, host.getIpAddress());
            HostvirtualmemoryResultTosql virtualmemorySql = new HostvirtualmemoryResultTosql();
            virtualmemorySql.CreateSwapMemoryResultTosql(returnHash, host.getIpAddress());
            if (!"0".equals(runmodel)) {
                NetHostMemoryRtsql totempsql = new NetHostMemoryRtsql();
                totempsql.CreateResultTosql(returnHash, host);
            }
        }
        if (userVector != null && userVector.size() > 0) {
            returnHash.put("user", userVector);
            if (!"0".equals(runmodel)) {
                HostDatatempUserRtosql tosql = new HostDatatempUserRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }

        }
        if (processVector != null && processVector.size() > 0) {
            returnHash.put("process", processVector);
            if (!"0".equals(runmodel)) {
                HostDatatempProcessRtTosql temptosql = new HostDatatempProcessRtTosql();
                temptosql.CreateResultTosql(returnHash, host);
            }
        }
        if (systemVector != null && systemVector.size() > 0) {
            returnHash.put("system", systemVector);
            if (!"0".equals(runmodel)) {
                NetHostDatatempSystemRttosql tosql = new NetHostDatatempSystemRttosql();
                tosql.CreateResultTosql(returnHash, host);
            }

        }
        if (nodeconfig != null) {
            returnHash.put("nodeconfig", nodeconfig);
            if (!"0".equals(runmodel)) {
                HostDatatempNodeconfRtosql tosql = new HostDatatempNodeconfRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }

        }

        if (iflist != null && iflist.size() > 0) {
            returnHash.put("iflist", iflist);
            if (!"0".equals(runmodel)) {
                HostDatatempiflistRtosql tosql = new HostDatatempiflistRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }
        }

        if (utilhdxVector != null && utilhdxVector.size() > 0) {
            returnHash.put("utilhdx", utilhdxVector);
            NetinterfaceResultTosql tosql_ = new NetinterfaceResultTosql();
            tosql_.CreateResultTosql(returnHash, host.getIpAddress());
            if (!"0".equals(runmodel)) {
                HostDatatemputilhdxRtosql tosql = new HostDatatemputilhdxRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }
        }
        
        if (errptlogVector != null && errptlogVector.size() > 0) {
            returnHash.put("errptlog", errptlogVector);
            SysLogger.info("#############################1");
            SysLogger.info("#############################1");
            SysLogger.info("#############################1");
            SysLogger.info("#############################1");
//            if (!"0".equals(runmodel)) {
            	HostDatatempErrptRtosql tosql = new HostDatatempErrptRtosql();
                tosql.CreateResultTosql(returnHash, host);
//            }
                SysLogger.info("#############################1");
                SysLogger.info("#############################1");
                SysLogger.info("#############################1");
                SysLogger.info("#############################1");
        }

        if (interfaceVector != null && interfaceVector.size() > 0) {
            returnHash.put("interface", interfaceVector);
            NetinterfaceResultTosql tosql_ = new NetinterfaceResultTosql();
            tosql_.CreateResultTosql(returnHash, host.getIpAddress());
            if (!"0".equals(runmodel)) {
                HostDatatempinterfaceRtosql tosql = new HostDatatempinterfaceRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }

        }
        if (allutilhdxVector != null && allutilhdxVector.size() > 0) {
            returnHash.put("allutilhdx", allutilhdxVector);
            if (!"0".equals(runmodel)) {
                HostDatatempAllutilhdxRtosql tosql = new HostDatatempAllutilhdxRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }
        }
        if (allDiskPerfList != null && allDiskPerfList.size() > 0) {
            returnHash.put("alldiskperf", allDiskPerfList);
            if (!"0".equals(runmodel)) {
                HostDatatempnDiskperfRtosql tosql = new HostDatatempnDiskperfRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }

        }
        if (allDiskIOList != null && allDiskIOList.size() > 0) {
            returnHash.put("alldiskio", allDiskIOList);
            if (!"0".equals(runmodel)) {
                HostDatatempDiskPeriofRtosql tosql = new HostDatatempDiskPeriofRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }

        }
        if (cpuConfigList != null && cpuConfigList.size() > 0) {
            returnHash.put("cpuconfiglist", cpuConfigList);
            if (!"0".equals(runmodel)) {
                HostDatatempCpuconfiRtosql tosql = new HostDatatempCpuconfiRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }

        }
        if (netmedialist != null && netmedialist.size() > 0) {
            returnHash.put("netmedialist", netmedialist);
        }
        if (servicelist != null && servicelist.size() > 0) {
            returnHash.put("servicelist", servicelist);
            if (!"0".equals(runmodel)) {
                HostDatatempserciceRttosql totempsql = new HostDatatempserciceRttosql();
                totempsql.CreateResultLinuxTosql(returnHash, host);
            }

        }
        if (cpuPerfListt != null && cpuPerfListt.size() > 0) {
            returnHash.put("cpuperflist", cpuPerfListt);
            HostcpuResultTosql rtosql = new HostcpuResultTosql();
            rtosql.CreateLinuxResultTosql(returnHash, host.getIpAddress());
            if (!"0".equals(runmodel)) {
                HostDatatempCpuperRtosql tmptosql = new HostDatatempCpuperRtosql();
                tmptosql.CreateResultTosql(returnHash, host);
            }

        }
        if (pagePerformanceHashtable != null && pagePerformanceHashtable.size() > 0) {
            returnHash.put("paginghash", pagePerformanceHashtable);
            HostPagingResultTosql Rtosql = new HostPagingResultTosql();
            Rtosql.CreateResultTosql(returnHash, host.getIpAddress());
            if (!"0".equals(runmodel)) {
                HostDatatempPagingRtosql tosql = new HostDatatempPagingRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }
        }

        if (pagingHash != null && pagingHash.size() > 0) {
            returnHash.put("paginghash", pagingHash);
            HostPagingResultTosql Rtosql = new HostPagingResultTosql();
            Rtosql.CreateResultTosql(returnHash, host.getIpAddress());
            if (!"0".equals(runmodel)) {
                HostDatatempPagingRtosql tosql = new HostDatatempPagingRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }
        }

        if (volumeVector != null && volumeVector.size() > 0) {
            returnHash.put("volume", volumeVector);
            if (!"0".equals(runmodel)) {
                HostDatatempVolumeRtosql tosql = new HostDatatempVolumeRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }

        }
        if (routeList != null && routeList.size() > 0) {
            returnHash.put("routelist", routeList);
            if (!"0".equals(runmodel)) {
                HostDatatempRuteRtosql tosql = new HostDatatempRuteRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }
        }

        if (!"0".equals(runmodel)) {
            HostDatatempCollecttimeRtosql tosql = new HostDatatempCollecttimeRtosql();
            tosql.CreateResultTosql(returnHash, host);
        }

        NodeUtil nodeUtil = new NodeUtil();
        NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
        try {
            checkAixEvent(nodeDTO, returnHash);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 更新内存
        ShareData.getSharedata().put(host.getIpAddress(), returnHash);
    }

    @SuppressWarnings("unused")
    private void checkAixEvent(NodeDTO nodeDTO, Hashtable resultHash) {
        Host host = (Host) PollingEngine.getInstance().getNodeByID(nodeDTO.getId());
        AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
        List list = alarmIndicatorsUtil.getAlarmIndicatorsForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
        if (list == null || list.size() == 0) {
            return;
        }
        CheckEventUtil checkEventUtil = new CheckEventUtil();
        for (int i = 0; i < list.size(); i++) {
            try {
                AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
                if ("file".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + host.getIpAddress() + ".log";
                    if (filename != null) {
                        File file = new File(filename);
                        long lasttime = file.lastModified();
                        Date date = new Date(lasttime);
                        java.util.Date date2 = new java.util.Date();
                        long btmes = (date2.getTime() - date.getTime()) / 1000;
                        if (file.exists()) {
                            checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, btmes + "");
                        } else {
                            checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, "999999");
                        }
                    }
                } else if ("cpu".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (resultHash != null && resultHash.size() > 0) {
                        Vector cpuVector = (Vector) resultHash.get("cpu");
                        if (cpuVector != null) {
                            for (int k = 0; k < cpuVector.size(); k++) {
                                CPUcollectdata cpudata = (CPUcollectdata) cpuVector.get(k);
                                if ("Utilization".equalsIgnoreCase(cpudata.getEntity()) && "Utilization".equals(cpudata.getSubentity())) {
                                    checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, cpudata.getThevalue());
                                }
                            }
                        }
                    }
                } else if ("physicalmemory".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (resultHash != null && resultHash.size() > 0) {
                        Vector memoryVector = (Vector) resultHash.get("memory");
                        if (memoryVector != null) {
                            for (int k = 0; k < memoryVector.size(); k++) {
                                Memorycollectdata memorydata = (Memorycollectdata) memoryVector.get(k);
                                if ("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equals(memorydata.getEntity())) {
                                    checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, memorydata.getThevalue());
                                }
                            }
                        }
                    }
                } else if ("swapmemory".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (resultHash != null && resultHash.size() > 0) {
                        Vector memoryVector = (Vector) resultHash.get("memory");
                        if (memoryVector != null) {
                            for (int k = 0; k < memoryVector.size(); k++) {
                                Memorycollectdata memorydata = (Memorycollectdata) memoryVector.get(k);
                                if ("SwapMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equals(memorydata.getEntity())) {
                                    checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, memorydata.getThevalue());
                                }
                            }
                        }
                    }
                } else if ("AllInBandwidthUtilHdx".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (resultHash != null && resultHash.size() > 0) {
                        Vector inVector = (Vector) resultHash.get("utilhdx");
                        if (inVector != null) {
                            int inutil = 0;
                            for (int k = 0; k < inVector.size(); k++) {
                                UtilHdx indata = (UtilHdx) inVector.get(k);
                                if ("InBandwidthUtilHdx".equalsIgnoreCase(indata.getEntity())) {
                                    inutil = inutil + Integer.parseInt(indata.getThevalue());
                                }
                            }
                            checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, inutil + "");
                        }
                    }
                } else if ("AllOutBandwidthUtilHdx".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (resultHash != null && resultHash.size() > 0) {
                        Vector outVector = (Vector) resultHash.get("utilhdx");
                        if (outVector != null) {
                            int oututil = 0;
                            for (int k = 0; k < outVector.size(); k++) {
                                UtilHdx outdata = (UtilHdx) outVector.get(k);
                                if ("OutBandwidthUtilHdx".equalsIgnoreCase(outdata.getEntity())) {
                                    oututil = oututil + Integer.parseInt(outdata.getThevalue());
                                }
                            }
                            checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, oututil + "");
                        }
                    }
                } else if ("diskperc".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (resultHash != null && resultHash.size() > 0) {
                        Vector diskVector = (Vector) resultHash.get("disk");
                        if (diskVector != null) {
                            checkEventUtil.checkDisk(host, diskVector, alarmIndicatorsNode);
                        }
                    }
                } else if ("diskinc".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (resultHash != null && resultHash.size() > 0) {
                        Vector diskVector = (Vector) resultHash.get("disk");
                        if (diskVector != null) {
                            checkEventUtil.checkDisk(host, diskVector, alarmIndicatorsNode);
                        }
                    }
                } else if ("process".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (resultHash != null && resultHash.size() > 0) {
                        Vector processVector = (Vector) resultHash.get("process");
                        if (processVector != null) {
                            checkEventUtil.createProcessGroupEventList(nodeDTO.getIpaddress(), processVector, alarmIndicatorsNode);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
