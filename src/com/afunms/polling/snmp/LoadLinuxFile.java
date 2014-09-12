package com.afunms.polling.snmp;

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
import com.afunms.common.util.Arith;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.model.Nodeconfig;
import com.afunms.config.model.Nodecpuconfig;
import com.afunms.config.model.Procs;
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
import com.afunms.system.util.TimeGratherConfigUtil;
import com.gatherResulttosql.HostDatatempAllutilhdxRtosql;
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
import com.gatherResulttosql.HostDatatempserciceRttosql;
import com.gatherResulttosql.HostDatatemputilhdxRtosql;
import com.gatherResulttosql.HostPhysicalMemoryResulttosql;
import com.gatherResulttosql.HostcpuResultTosql;
import com.gatherResulttosql.HostdiskResultosql;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;
import com.gatherResulttosql.NetHostMemoryRtsql;

public class LoadLinuxFile {
    private String ipaddress;

    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LoadLinuxFile(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public LoadLinuxFile() {

    }

    public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
        Hashtable returnHash = new Hashtable();
        Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));

        if (host == null)
            return returnHash;
        // 判断是否在采集时间段内
        if (ShareData.getTimegatherhash() != null) {
            if (ShareData.getTimegatherhash().containsKey(host.getId() + ":equipment")) {
                TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
                int _result = 0;
                _result = timeconfig.isBetween((List) ShareData.getTimegatherhash().get(host.getId() + ":equipment"));
                if (_result == 1) {
                    // SysLogger.info("########时间段内: 开始采集
                    // "+node.getIpAddress()+" PING数据信息##########");
                } else if (_result == 2) {
                    // SysLogger.info("########全天: 开始采集 "+node.getIpAddress()+"
                    // PING数据信息##########");
                } else {
                    SysLogger.info("######## " + host.getIpAddress() + " 不在采集LINUX时间段内,退出##########");
                    // 清除之前内存中产生的告警信息
                    try {
                        // 清除之前内存中产生的内存告警信息
                        NodeDTO nodedto = null;
                        NodeUtil nodeUtil = new NodeUtil();
                        nodedto = nodeUtil.creatNodeDTOByHost(host);
                        CheckEventUtil checkutil = new CheckEventUtil();
                        checkutil.deleteEvent(host.getId() + "", nodedto.getType(), nodedto.getSubtype(), "iowait", null);
                        checkutil.deleteEvent(host.getId() + "", nodedto.getType(), nodedto.getSubtype(), "diskperc", null);
                        checkutil.deleteEvent(host.getId() + "", nodedto.getType(), nodedto.getSubtype(), "cpu", null);
                        checkutil.deleteEvent(host.getId() + "", nodedto.getType(), nodedto.getSubtype(), "physicalmemory", null);
                        checkutil.deleteEvent(host.getId() + "", nodedto.getType(), nodedto.getSubtype(), "swapmemory", null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return returnHash;
                }

            }
        }

        ipaddress = host.getIpAddress();
        Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ipaddress);
        if (ipAllData == null)
            ipAllData = new Hashtable();
        StringBuffer fileContent = new StringBuffer();
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
        if (host == null)
            return null;
        nodeconfig.setNodeid(host.getId());
        nodeconfig.setHostname(host.getAlias());
        try {

            String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + ipaddress + ".log";
            File file = new File(filename);
            if (!file.exists()) {
                filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + ipaddress + "_date";
            }
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
            try {
                copyFile(ipaddress, getMaxNum(ipaddress));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Pattern tmpPt = null;
        Matcher mr = null;
        Calendar date = Calendar.getInstance();

        // ----------------解析数据采集时间内容--创建监控项---------------------
        tmpPt = Pattern.compile("(cmdbegin:collecttime)(.*)(cmdbegin:version)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            collecttime = mr.group(2);
        }
        if (collecttime != null && collecttime.length() > 0) {
            collecttime = collecttime.trim();
        }

        // ----------------解析version内容--创建监控项---------------------
        String versionContent = "";
        tmpPt = Pattern.compile("(cmdbegin:version)(.*)(cmdbegin:cpuconfig)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            versionContent = mr.group(2);
        }
        if (versionContent != null && versionContent.length() > 0) {
            nodeconfig.setCSDVersion(versionContent.trim());
        }
        // ----------------解析cpuconfig内容--创建监控项---------------------
        String cpuconfigContent = "";
        tmpPt = Pattern.compile("(cmdbegin:cpuconfig)(.*)(cmdbegin:disk)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            cpuconfigContent = mr.group(2);

        }
        String[] cpuconfigLineArr = cpuconfigContent.split("\n");
        List<Nodecpuconfig> cpuconfiglist = new ArrayList<Nodecpuconfig>();
        Nodecpuconfig nodecpuconfig = new Nodecpuconfig();
        String procesors = "";
        if (cpuconfigLineArr != null) {
            for (int i = 0; i < cpuconfigLineArr.length; i++) {
                String[] result = cpuconfigLineArr[i].trim().split(":");
                if (result.length > 0) {
                    if (result[0].trim().equalsIgnoreCase("processor")) {
                        nodecpuconfig.setNodeid(host.getId());
                        nodecpuconfig.setProcessorId(result[1].trim());
                        procesors = result[1].trim();
                    } else if (result[0].trim().equalsIgnoreCase("model name")) {
                        nodecpuconfig.setName(result[1].trim());
                    } else if (result[0].trim().equalsIgnoreCase("cpu MHz")) {
                        nodecpuconfig.setProcessorSpeed(result[1].trim());
                    } else if (result[0].trim().equalsIgnoreCase("cache size")) {
                        nodecpuconfig.setL2CacheSize(result[1].trim());
                        cpuconfiglist.add(nodecpuconfig);
                        nodecpuconfig = new Nodecpuconfig();
                    }
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
        // disk数据集合，变化时进行告警检测
        Hashtable<String, Object> diskInfoHash = new Hashtable<String, Object>();
        // 磁盘大小
        float diskSize = 0;
        // 磁盘名称集合
        List<String> diskNameList = new ArrayList<String>();

        String diskContent = "";
        String diskLabel;
        List disklist = new ArrayList();
        tmpPt = Pattern.compile("(cmdbegin:disk)(.*)(cmdbegin:diskperf)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            diskContent = mr.group(2);
        }
        String[] diskLineArr = diskContent.split("\n");
        String[] tmpData = null;
        Diskcollectdata diskdata = null;
        int diskflag = 0;
        if (diskLineArr != null) {
            for (int i = 1; i < diskLineArr.length; i++) {

                tmpData = diskLineArr[i].split("\\s++");
                if ((tmpData != null) && (tmpData.length == 6)) {
                    diskLabel = tmpData[5];
                    diskdata = new Diskcollectdata();
                    diskdata.setIpaddress(host.getIpAddress());
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
                    diskdata.setIpaddress(host.getIpAddress());
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
                    try {
                        String diskinc = "0.0";
                        float pastutil = 0.0f;
                        Vector disk_v = (Vector) ipAllData.get("disk");
                        if (disk_v != null && disk_v.size() > 0) {
                            for (int si = 0; si < disk_v.size(); si++) {
                                Diskcollectdata disk_data = (Diskcollectdata) disk_v.elementAt(si);
                                if ((tmpData[5]).equals(disk_data.getSubentity()) && "Utilization".equals(disk_data.getEntity())) {
                                    pastutil = Float.parseFloat(disk_data.getThevalue());
                                }
                            }
                        } else {
                            pastutil = Float.parseFloat(tmpData[4].substring(0, tmpData[4].indexOf("%")));
                        }
                        if (pastutil == 0) {
                            pastutil = Float.parseFloat(tmpData[4].substring(0, tmpData[4].indexOf("%")));
                        }
                        if (Float.parseFloat(tmpData[4].substring(0, tmpData[4].indexOf("%"))) - pastutil > 0) {
                            diskinc = (Float.parseFloat(tmpData[4].substring(0, tmpData[4].indexOf("%"))) - pastutil) + "";
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

                    diskdata = new Diskcollectdata();
                    diskdata.setIpaddress(host.getIpAddress());
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
        }
        // 进行磁盘告警检测
        try {
            diskSize = diskSize / 1024;
            diskInfoHash.put("diskSize", diskSize + "G");
            diskInfoHash.put("diskNameList", diskNameList);
            CheckEventUtil checkutil = new CheckEventUtil();
            checkutil.hardwareInfo(host, "disk", diskInfoHash);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ----------------解析diskperf内容--创建监控项---------------------
        String diskperfContent = "";
        tmpPt = Pattern.compile("(cmdbegin:diskperf\n)(.*)(cmdbegin:cpu\n)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            diskperfContent = mr.group(2);
        }
        String[] diskperfLineArr = diskperfContent.split("\n");
        String[] diskperf_tmpData = null;
        List<Hashtable> alldiskperf = new ArrayList<Hashtable>();
        Hashtable<String, String> diskperfhash = new Hashtable<String, String>();
        int flag = 0;
        if (diskperfLineArr != null) {
            for (int i = 0; i < diskperfLineArr.length; i++) {
                diskperf_tmpData = diskperfLineArr[i].trim().split("\\s++");
                if (diskperf_tmpData != null && diskperf_tmpData.length == 10) {
                    if (diskperf_tmpData[0].trim().equalsIgnoreCase("Average:")) {

                        if (diskperf_tmpData[1].trim().equalsIgnoreCase("DEV")) {
                            continue;
                        } else {
                            diskperfhash.put("tps", diskperf_tmpData[2].trim());
                            diskperfhash.put("rd_sec/s", diskperf_tmpData[3].trim());
                            diskperfhash.put("wr_sec/s", diskperf_tmpData[4].trim());
                            diskperfhash.put("avgrq-sz", diskperf_tmpData[5].trim());
                            diskperfhash.put("avgqu-sz", diskperf_tmpData[6].trim());
                            diskperfhash.put("await", diskperf_tmpData[7].trim());
                            diskperfhash.put("svctm", diskperf_tmpData[8].trim());
                            diskperfhash.put("%util", diskperf_tmpData[9].trim());
                            diskperfhash.put("%busy", Math.round(Float.parseFloat(diskperf_tmpData[8].trim()) * 100 / (Float.parseFloat(diskperf_tmpData[7].trim()) + Float.parseFloat(diskperf_tmpData[8].trim()))) + "");
                            diskperfhash.put("disklebel", diskperf_tmpData[1].trim());

                            alldiskperf.add(diskperfhash);
                            flag = flag + 1;
                            diskperfhash = new Hashtable();

                        }
                    }
                }
            }
        }
        // ----------------解析cpu内容--创建监控项---------------------
        String cpuperfContent = "";
        tmpPt = Pattern.compile("(cmdbegin:cpu\n)(.*)(cmdbegin:memory)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            cpuperfContent = mr.group(2);

        }
        String[] cpuperfLineArr = cpuperfContent.split("\n");

        List cpuperflist = new ArrayList();
        Hashtable<String, String> cpuperfhash = new Hashtable<String, String>();
        if (cpuperfLineArr != null) {
            for (int i = 0; i < cpuperfLineArr.length; i++) {
                diskperf_tmpData = cpuperfLineArr[i].trim().split("\\s++");
                if (diskperf_tmpData != null && diskperf_tmpData.length >= 7) {
                    if (diskperf_tmpData[0].trim().equalsIgnoreCase("Average:")) {
                        cpuperfhash.put("%user", diskperf_tmpData[2].trim());
                        cpuperfhash.put("%nice", diskperf_tmpData[3].trim());
                        cpuperfhash.put("%system", diskperf_tmpData[4].trim());
                        cpuperfhash.put("%iowait", diskperf_tmpData[5].trim());
                        if (diskperf_tmpData.length == 7) {
                            cpuperfhash.put("%idle", diskperf_tmpData[6].trim());
                        }
                        if (diskperf_tmpData.length == 8) {
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
                }
            }
        }
        // ----------------解析memory内容--创建监控项---------------------
        String memperfContent = "";
        tmpPt = Pattern.compile("(cmdbegin:memory)(.*)(cmdbegin:process)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            memperfContent = mr.group(2);
        }
        String[] memperfLineArr = memperfContent.split("\n");
        List memperflist = new ArrayList();
        Vector memoryVector = new Vector();
        Memorycollectdata memorydata = null;
        Hashtable<String, String> memperfhash = new Hashtable<String, String>();
        if (memperfLineArr != null) {
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
                        // Memory 内存利用率
                        float PhysicalMemUtilization = (Float.parseFloat(diskperf_tmpData[2]) - Float.parseFloat(diskperf_tmpData[5]) - Float.parseFloat(diskperf_tmpData[6])) * 100 / (Float.parseFloat(diskperf_tmpData[1]) + Float.parseFloat(diskperf_tmpData[5]) + Float.parseFloat(diskperf_tmpData[6]));

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
        }
        // ----------------解析process内容--创建监控项---------------------
        String processContent = "";
        tmpPt = Pattern.compile("(cmdbegin:process)(.*)(cmdbegin:mac)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            processContent = mr.group(2);
        }
        List procslist = new ArrayList();
        ProcsDao procsdaor = new ProcsDao();
        try {
            procslist = procsdaor.loadByIp(ipaddress);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            procsdaor.close();
        }
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
        String[] processtmpData = null;
        if (cpu_LineArr != null) {
            for (int i = 1; i < cpu_LineArr.length; i++) {
                processtmpData = cpu_LineArr[i].trim().split("\\s++");
                if ((processtmpData != null) && (processtmpData.length == 12)) {
                    String USER = processtmpData[0];// USER
                    if ("USER".equalsIgnoreCase(USER))
                        continue;
                    String pid = processtmpData[1];// pid
                    String vbstring1 = processtmpData[10];// command
                    String vbstring2 = "应用程序";
                    String vbstring3 = "";
                    String vbstring4 = processtmpData[5];// memsize
                    if (vbstring4 == null)
                        vbstring4 = "0";
                    String vbstring5 = processtmpData[9];// cputime
                    String vbstring6 = processtmpData[3];// %mem
                    String vbstring7 = processtmpData[7];// STAT
                    String vbstring8 = processtmpData[8];// STIME
                    String vbstring9 = processtmpData[2];// %CPU
                    if ("Z".equals(vbstring7)) {
                        vbstring3 = "僵尸进程";
                    } else {
                        vbstring3 = "正在运行";
                    }
                    processdata = new Processcollectdata();
                    processdata.setIpaddress(ipaddress);
                    processdata.setCollecttime(date);
                    processdata.setCategory("Process");
                    processdata.setEntity("process_id");
                    processdata.setSubentity(pid);
                    processdata.setRestype("dynamic");
                    processdata.setUnit(" ");
                    processdata.setThevalue(pid);
                    processVector.addElement(processdata);

                    processdata = new Processcollectdata();
                    processdata.setIpaddress(ipaddress);
                    processdata.setCollecttime(date);
                    processdata.setCategory("Process");
                    processdata.setEntity("USER");
                    processdata.setSubentity(pid);
                    processdata.setRestype("dynamic");
                    processdata.setUnit(" ");
                    processdata.setThevalue(USER);
                    processVector.addElement(processdata);

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

                    processdata = new Processcollectdata();
                    processdata.setIpaddress(ipaddress);
                    processdata.setCollecttime(date);
                    processdata.setCategory("Process");
                    processdata.setEntity("StartTime");
                    processdata.setSubentity(pid);
                    processdata.setRestype("static");
                    processdata.setUnit(" ");
                    processdata.setThevalue(vbstring8);
                    processVector.addElement(processdata);

                    processdata = new Processcollectdata();
                    processdata.setIpaddress(ipaddress);
                    processdata.setCollecttime(date);
                    processdata.setCategory("Process");
                    processdata.setEntity("CpuUtilization");
                    processdata.setSubentity(pid);
                    processdata.setRestype("dynamic");
                    processdata.setUnit("%");
                    processdata.setThevalue(vbstring9);
                    processVector.addElement(processdata);

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
        tmpPt = Pattern.compile("(cmdbegin:mac)(.*)(cmdbegin:interface)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            macContent = mr.group(2);
        }
        String[] macLineArr = macContent.split("\n");
        String[] macTempArr = null;
        String MAC = "";
        Hashtable macTempHashtable = new Hashtable();
        if (macLineArr != null) {
            for (int i = 0; i < macLineArr.length; i++) {
                macTempArr = macLineArr[i].trim().split("\\s++");
                if (macTempArr.length == 4) {
                    if (macTempArr[0].equalsIgnoreCase("link/ether") && macTempArr[2].equalsIgnoreCase("brd")) {
                        MAC = macTempArr[1];
                        if (MAC.equalsIgnoreCase("00:00:00:00:00:00")) {
                            continue;
                        }
                        if (macTempHashtable.containsKey(MAC))
                            continue;
                        macTempHashtable.put(MAC, MAC);
                        String macTemp = nodeconfig.getMac();
                        if (macTemp != null && macTemp.trim().length() > 0) {
                            macTemp = macTemp + "," + MAC;
                            nodeconfig.setMac(macTemp);
                        } else {
                            nodeconfig.setMac(MAC);
                        }
                    }
                }
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
        tmpPt = Pattern.compile("(cmdbegin:interface)(.*)(cmdbegin:uname)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            interfaceContent = mr.group(2);
        }
        String[] ifLineArr = interfaceContent.split("\n");
        String[] ifTempArr = null;
        Long allInHdx = 0l;
        Long allOutHdx = 0l;
        int count = 0;
        
        ArrayList ifList = new ArrayList();
        Hashtable ifHashtable = new Hashtable();
        Interfacecollectdata interfacedata = null;
        if (ifLineArr != null) {
            for (int i = 0; i < ifLineArr.length; i++) {
                ifTempArr = ifLineArr[i].trim().split("\\s++");
                if (ifTempArr != null && ifTempArr.length == 9) {
                    if (ifLineArr[i].contains("Average:")) {
                        if (ifTempArr[1].trim().equalsIgnoreCase("IFACE"))
                            continue;
                        ifHashtable.put("IFACE", ifTempArr[1]);
                        ifHashtable.put("rxpck/s", ifTempArr[2]);
                        ifHashtable.put("txpck/s", ifTempArr[3]);
                        ifHashtable.put("rxbyt/s", ifTempArr[4]);
                        ifHashtable.put("txbyt/s", ifTempArr[5]);
                        ifHashtable.put("rxcmp/s", ifTempArr[6]);
                        ifHashtable.put("txcmp/s", ifTempArr[7]);
                        ifHashtable.put("rxmcst/s", ifTempArr[8]);

                        // 端口索引
                        interfacedata = new Interfacecollectdata();
                        interfacedata.setIpaddress(ipaddress);
                        interfacedata.setCollecttime(date);
                        interfacedata.setCategory("Interface");
                        interfacedata.setEntity("index");
                        interfacedata.setSubentity(i + "");
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
                        interfacedata.setRestype("static");
                        interfacedata.setUnit("");
                        interfacedata.setThevalue(ifTempArr[1]);
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
                        utilhdx.setEntity("InBandwidthUtilHdx");
                        utilhdx.setThevalue(Long.toString(Math.round(Float.parseFloat(ifTempArr[4])) * 8));
                        utilhdx.setSubentity(i + "");
                        utilhdx.setRestype("dynamic");
                        utilhdx.setUnit("Kb/秒");
                        utilhdx.setChname(i + "端口入口" + "流速");
                        utilhdxVector.addElement(utilhdx);
                        allInHdx += Math.round(Float.parseFloat(ifTempArr[4])) * 8;
                        // 端口出口流速
                        utilhdx = new UtilHdx();
                        utilhdx.setIpaddress(ipaddress);
                        utilhdx.setCollecttime(date);
                        utilhdx.setCategory("Interface");
                        utilhdx.setEntity("OutBandwidthUtilHdx");
                        utilhdx.setThevalue(Long.toString(Math.round(Float.parseFloat(ifTempArr[5])) * 8));
                        utilhdx.setSubentity(i + "");
                        utilhdx.setRestype("dynamic");
                        utilhdx.setUnit("Kb/秒");
                        utilhdx.setChname(i + "端口出口" + "流速");
                        utilhdxVector.addElement(utilhdx);
                        allOutHdx += Math.round(Float.parseFloat(ifTempArr[5])) * 8;
                        ifList.add(ifHashtable);
                        ifHashtable = new Hashtable();
                        count++;
                    }

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
        allutilhdx.setThevalue(Long.toString(allInHdx + allOutHdx));
        allutilhdxVector.addElement(allutilhdx);

        returnHash.put("interfaceNumber", count);

        // ----------------解析uname内容--创建监控项---------------------
        String unameContent = "";
        tmpPt = Pattern.compile("(cmdbegin:uname)(.*)(cmdbegin:usergroup)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            unameContent = mr.group(2);
        }
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
        
        

        // ----------------解析usergroup内容--创建监控项---------------------
        Hashtable usergrouphash = new Hashtable();
        String usergroupContent = "";
        tmpPt = Pattern.compile("(cmdbegin:usergroup)(.*)(cmdbegin:user)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            usergroupContent = mr.group(2);
        }
        String[] usergroupLineArr = usergroupContent.split("\n");
        String[] usergroup_tmpData = null;
        if (usergroupLineArr != null) {
            for (int i = 0; i < usergroupLineArr.length; i++) {
                usergroup_tmpData = usergroupLineArr[i].split(":");
                if (usergroup_tmpData.length >= 3) {
                    usergrouphash.put((String) usergroup_tmpData[2], usergroup_tmpData[0]);
                }
            }
        }
        // ----------------解析user内容--创建监控项---------------------
        String userContent = "";
        tmpPt = Pattern.compile("(cmdbegin:user)(.*)(cmdbegin:date)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            userContent = mr.group(2);
        }
        String[] userLineArr = userContent.split("\n");
        if (userLineArr != null) {
            for (int i = 0; i < userLineArr.length; i++) {
                String[] result = userLineArr[i].trim().split(":");
                if (result.length > 4) {
                    int userid = 0;
                    if (result[2].length() < 6) {
                        userid = Integer.parseInt(result[2]);
                    }
                    // 小于500的为系统级用户,过滤
                    if (userid < 500)
                        continue;

                    int usergroupid = Integer.parseInt(result[3]);
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
        }
        // ----------------解析date内容--创建监控项---------------------
        String dateContent = "";
        tmpPt = Pattern.compile("(cmdbegin:date)(.*)(cmdbegin:uptime)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            dateContent = mr.group(2);
        }
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
        tmpPt = Pattern.compile("(cmdbegin:uptime)(.*)(cmdbegin:service)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            uptimeContent = mr.group(2);
        }
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

        // ----------------解析service内容--创建监控项---------------------
        List servicelist = new ArrayList();
        Hashtable service = new Hashtable();
        String serviceContent = "";
        tmpPt = Pattern.compile("(cmdbegin:service)(.*)(cmdbegin:end)", Pattern.DOTALL);
        mr = tmpPt.matcher(fileContent.toString());
        if (mr.find()) {
            serviceContent = mr.group(2);
        }
        String[] serviceLineArr = serviceContent.split("\n");
        String[] result = null;
        if (serviceLineArr != null) {
            for (int i = 0; i < serviceLineArr.length; i++) {
                result = serviceLineArr[i].trim().split("\\s++");
                if (result.length == 8) {
                    try {
                        service.put("name", result[0]);
                        String servicestatus = result[4];
                        if (servicestatus.indexOf("on") >= 0 || servicestatus.indexOf("启用") >= 0) {
                            service.put("status", "启用");
                        } else {
                            service.put("status", "未启用");
                        }

                        servicelist.add(service);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    service = new Hashtable();
                }
            }
        }
        try {
            deleteFile(ipaddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
        if (diskVector != null && diskVector.size() > 0) {
            returnHash.put("disk", diskVector);
            // 把采集结果生成sql
            HostdiskResultosql tosql = new HostdiskResultosql();
            tosql.CreateResultTosql(returnHash, host.getIpAddress());
            if (!"0".equals(runmodel)) {
                HostDatatempDiskRttosql temptosql = new HostDatatempDiskRttosql();
                temptosql.CreateResultTosql(returnHash, host);
                temptosql = null;
            }

            tosql = null;

        }
        if (cpuVector != null && cpuVector.size() > 0) {
            returnHash.put("cpu", cpuVector);
            if (!"0".equals(runmodel)) {
                NetHostDatatempCpuRTosql totempsql = new NetHostDatatempCpuRTosql();
                totempsql.CreateResultTosql(returnHash, host);
                totempsql = null;
            }

        }
        if (memoryVector != null && memoryVector.size() > 0) {
            returnHash.put("memory", memoryVector);
            HostPhysicalMemoryResulttosql tosql = new HostPhysicalMemoryResulttosql();
            tosql.CreateResultTosql(returnHash, host.getIpAddress());
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
        if (ifList != null && ifList.size() > 0) {
            returnHash.put("iflist", ifList);
            if (!"0".equals(runmodel)) {
                HostDatatempiflistRtosql tosql = new HostDatatempiflistRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }

        }
        if (utilhdxVector != null && utilhdxVector.size() > 0) {
            returnHash.put("utilhdx", utilhdxVector);
            if (!"0".equals(runmodel)) {
                HostDatatemputilhdxRtosql tosql = new HostDatatemputilhdxRtosql();
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

        if (interfaceVector != null && interfaceVector.size() > 0) {
            returnHash.put("interface", interfaceVector);
            if (!"0".equals(runmodel)) {
                HostDatatempinterfaceRtosql tosql = new HostDatatempinterfaceRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }

        }
        if (alldiskperf != null && alldiskperf.size() > 0) {
            returnHash.put("alldiskperf", alldiskperf);
            if (!"0".equals(runmodel)) {
                HostDatatempnDiskperfRtosql tosql = new HostDatatempnDiskperfRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }

        }
        if (servicelist != null && servicelist.size() > 0) {
            returnHash.put("servicelist", servicelist);
            if (!"0".equals(runmodel)) {
                HostDatatempserciceRttosql totempsql = new HostDatatempserciceRttosql();
                totempsql.CreateResultLinuxTosql(returnHash, host);
            }

        }
        if (cpuconfiglist != null && cpuconfiglist.size() > 0) {
            returnHash.put("cpuconfiglist", cpuconfiglist);
            if (!"0".equals(runmodel)) {
                HostDatatempCpuconfiRtosql tosql = new HostDatatempCpuconfiRtosql();
                tosql.CreateResultTosql(returnHash, host);
            }

        }
        if (cpuperflist != null && cpuperflist.size() > 0) {
            returnHash.put("cpuperflist", cpuperflist);

            HostcpuResultTosql rtosql = new HostcpuResultTosql();
            rtosql.CreateLinuxResultTosql(returnHash, host.getIpAddress());
            if (!"0".equals(runmodel)) {
                HostDatatempCpuperRtosql tmptosql = new HostDatatempCpuperRtosql();
                tmptosql.CreateResultTosql(returnHash, host);
            }

        }

        returnHash.put("collecttime", collecttime);
        if (!"0".equals(runmodel)) {
            HostDatatempCollecttimeRtosql tosql = new HostDatatempCollecttimeRtosql();
            tosql.CreateResultTosql(returnHash, host);
        }

        NodeUtil nodeUtil = new NodeUtil();
        NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
        try {
            updateLinuxData(nodeDTO, returnHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ShareData.getSharedata().put(host.getIpAddress(), returnHash);
        return returnHash;
    }

    public void updateLinuxData(NodeDTO nodeDTO, Hashtable hashtable) {
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
                        long size = file.length();
                        Date date = new Date(lasttime);
                        Date date2 = new Date();
                        long btmes = (date2.getTime() - date.getTime()) / 1000;
                        if (file.exists()) {
                            checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, btmes + "");
                        } else {
                            checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, "999999");
                        }
                    }
                } else if ("cpu".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (hashtable != null && hashtable.size() > 0) {
                        Vector cpuVector = (Vector) hashtable.get("cpu");
                        if (cpuVector != null) {
                            for (int k = 0; k < cpuVector.size(); k++) {
                                CPUcollectdata cpudata = (CPUcollectdata) cpuVector.get(k);
                                if ("Utilization".equalsIgnoreCase(cpudata.getEntity()) && "Utilization".equalsIgnoreCase(cpudata.getSubentity())) {
                                    checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, cpudata.getThevalue());
                                }
                            }
                        }
                    }
                } else if ("physicalmemory".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (hashtable != null && hashtable.size() > 0) {
                        Vector memoryVector = (Vector) hashtable.get("memory");
                        if (memoryVector != null) {
                            for (int k = 0; k < memoryVector.size(); k++) {
                                Memorycollectdata memorydata = (Memorycollectdata) memoryVector.get(k);
                                ;
                                if ("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
                                    checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, memorydata.getThevalue());
                                }
                            }
                        }
                    }
                } else if ("swapmemory".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (hashtable != null && hashtable.size() > 0) {
                        Vector memoryVector = (Vector) hashtable.get("memory");
                        if (memoryVector != null) {
                            for (int k = 0; k < memoryVector.size(); k++) {
                                Memorycollectdata memorydata = (Memorycollectdata) memoryVector.get(k);
                                ;
                                if ("SwapMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
                                    checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, memorydata.getThevalue());
                                }
                            }
                        }
                    }
                } else if ("AllInBandwidthUtilHdx".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (hashtable != null && hashtable.size() > 0) {
                        Vector inVector = (Vector) hashtable.get("utilhdx");
                        int inutil = 0;
                        if (inVector != null) {
                            for (int k = 0; k < inVector.size(); k++) {
                                UtilHdx indata = (UtilHdx) inVector.get(k);
                                ;
                                if ("InBandwidthUtilHdx".equalsIgnoreCase(indata.getEntity())) {
                                    inutil = inutil + Integer.parseInt(indata.getThevalue());
                                }
                            }
                            checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, inutil + "");
                        }
                    }
                } else if ("AllOutBandwidthUtilHdx".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (hashtable != null && hashtable.size() > 0) {
                        Vector outVector = (Vector) hashtable.get("utilhdx");
                        if (outVector != null) {
                            int oututil = 0;
                            for (int k = 0; k < outVector.size(); k++) {
                                UtilHdx outdata = (UtilHdx) outVector.get(k);
                                ;
                                if ("OutBandwidthUtilHdx".equalsIgnoreCase(outdata.getEntity())) {
                                    oututil = oututil + Integer.parseInt(outdata.getThevalue());
                                }
                            }
                            checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, oututil + "");
                        }
                    }
                } else if ("diskperc".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (hashtable != null && hashtable.size() > 0) {
                        Vector diskVector = (Vector) hashtable.get("disk");
                        if (diskVector != null) {
                            checkEventUtil.checkDisk(host, diskVector, alarmIndicatorsNode);
                        }
                    }
                } else if ("diskinc".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (hashtable != null && hashtable.size() > 0) {
                        Vector diskVector = (Vector) hashtable.get("disk");
                        if (diskVector != null) {
                            checkEventUtil.checkDisk(host, diskVector, alarmIndicatorsNode);
                        }
                    }
                } else if ("process".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
                    if (hashtable != null && hashtable.size() > 0) {
                        Vector processVector = (Vector) hashtable.get("process");
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

    public String getMaxNum(String ipAddress) {
        String maxStr = null;
        File logFolder = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/");
        String[] fileList = logFolder.list();

        for (int i = 0; i < fileList.length; i++) // 找一个最新的文件
        {
            if (!fileList[i].startsWith(ipAddress))
                continue;

            return ipAddress;
        }
        return maxStr;
    }

    public void deleteFile(String ipAddress) {

        try {
            File delFile = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/" + ipAddress + ".log");
            // delFile.delete();
        } catch (Exception e) {
        }
    }

    public void copyFile(String ipAddress, String max) {
        try {
            String currenttime = SysUtil.getCurrentTime();
            currenttime = currenttime.replaceAll("-", "");
            currenttime = currenttime.replaceAll(" ", "");
            currenttime = currenttime.replaceAll(":", "");
            String ipdir = ipAddress.replaceAll("\\.", "-");
            String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver_bak/" + ipdir;
            File file = new File(filename);
            if (!file.exists())
                file.mkdir();
            String cmd = "cmd   /c   copy   " + ResourceCenter.getInstance().getSysPath() + "linuxserver\\" + ipAddress + ".log" + " " + ResourceCenter.getInstance().getSysPath() + "linuxserver_bak\\" + ipdir + "\\" + ipAddress + "-" + currenttime + ".log";
            Process child = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
