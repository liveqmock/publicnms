package com.afunms.polling.snmp.interfaces;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.topology.model.HostNode;

public class UtilHdxRealtimeSnmp extends SnmpMonitor {
    public UtilHdxRealtimeSnmp() {
    }

    public void collectData(Node node, MonitoredItem item) {

    }

    public void collectData(HostNode node) {
    }

    public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
        Hashtable returnHash = new Hashtable();
        Vector interfaceVector = new Vector();
        Hashtable interfaceSpeedHash = new Hashtable();
        Vector utilhdxVector = new Vector();
        Vector allutilhdxVector = new Vector();
        Vector utilhdxpercVector = new Vector();
        AllUtilHdx allutilhdx = new AllUtilHdx();
        Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
        Hashtable formerOctetsHastable = ShareData.getRealOctetsdata(host.getIpAddress());
        long allInOctetsSpeed = 0;
        long allOutOctetsSpeed = 0;
        long allOctetsSpeed = 0;
        Calendar date = Calendar.getInstance();
        try {
            Interfacecollectdata interfacedata = null;
            UtilHdx utilhdx = new UtilHdx();
            UtilHdxPerc utilhdxperc = new UtilHdxPerc();
            try {

                // 取得轮询间隔时间
                Hashtable<String, Comparable> realoctetsHash = new Hashtable();
                if (formerOctetsHastable == null)
                    formerOctetsHastable = new Hashtable();
                String[] oids = new String[] { "1.3.6.1.2.1.2.2.1.1", // index
                        "1.3.6.1.2.1.2.2.1.10", // ifInOctets 1
                        "1.3.6.1.2.1.2.2.1.16", // ifOutOctets 2
                        "1.3.6.1.2.1.2.2.1.5"// ifSpeed
                };
                String[] highOids = new String[] { "1.3.6.1.2.1.2.2.1.1", // index
                        "1.3.6.1.2.1.31.1.1.1.6", // ifInOctets 1
                        "1.3.6.1.2.1.31.1.1.1.10", // ifOutOctets 2
                        "1.3.6.1.2.1.31.1.1.1.15"// ifSpeed
                };
                final String[] desc = { "index", "ifInOctets", "ifOutOctets", "ifSpeed" };
                String[][] valueArray = null;
                try {
                    if (host.getSnmpversion() == 0) {
                        valueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), oids, host.getSnmpversion(), host.getSecuritylevel(), host.getSecurityName(), host.getV3_ap(), host.getAuthpassphrase(), host.getV3_privacy(), host.getPrivacyPassphrase(), 3, 1000 * 30);
                    } else if (host.getSnmpversion() == 1) {
                        valueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), highOids, host.getSnmpversion(), host.getSecuritylevel(), host.getSecurityName(), host.getV3_ap(), host.getAuthpassphrase(), host.getV3_privacy(), host.getPrivacyPassphrase(), 3, 1000 * 30);
                    } else if (host.getSnmpversion() == 3) {
                        valueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), highOids, host.getSnmpversion(), host.getSecuritylevel(), host.getSecurityName(), host.getV3_ap(), host.getAuthpassphrase(), host.getV3_privacy(), host.getPrivacyPassphrase(), 3, 1000 * 30);
                    }

                } catch (Exception e) {
                }
                if (valueArray != null) {
                    for (int i = 0; i < valueArray.length; i++) {
                        if (valueArray[i][0] == null)
                            continue;
                        String ifIndex = valueArray[i][4].toString();
                        for (int j = 0; j < 4; j++) {
                            if (valueArray[i][j] != null) {
                                String ifValue = valueArray[i][j];
                                interfacedata = new Interfacecollectdata();
                                interfacedata.setIpaddress(host.getIpAddress());
                                interfacedata.setCollecttime(date);
                                interfacedata.setCategory("Interface");
                                interfacedata.setEntity(desc[j]);
                                interfacedata.setSubentity(ifIndex);
                                interfacedata.setThevalue(ifValue);
                                if (j == 3) {
                                    long longValue = 0L;
                                    if (host.getCategory() == 4 || host.getCategory() == 8) {// 当为服务器时候,取出来的数据为原始大小
                                        longValue = Long.parseLong(ifValue);
                                    } else {
                                        if (host.getSnmpversion() == 0) {
                                            longValue = Long.parseLong(ifValue);
                                        } else {
                                            longValue = Long.parseLong(ifValue) * 1000000;
                                        }
                                    }
                                    ifValue = Long.toString(longValue);
                                    interfaceSpeedHash.put(ifIndex, ifValue);
                                
                                }
                                interfaceVector.addElement(interfacedata);
                            }
                        }
                    }

                    BigInteger longInterval = new BigInteger("0");
                    Calendar cal = (Calendar) formerOctetsHastable.get("collecttime");// 上次采集时间
                    long timeInMillis = 0;
                    if (cal != null) {
                        timeInMillis = cal.getTimeInMillis();
                        longInterval = new BigInteger(((date.getTimeInMillis() - timeInMillis) / 1000) + "");// 时间差
                    }

                    DecimalFormat df = new DecimalFormat("#.##");
                    Enumeration<Interfacecollectdata> ifE = interfaceVector.elements();
                    String key = null;
                    String ifIndex = null;
                    BigInteger formerOctets = new BigInteger("0");
                    BigInteger CurrentOctets = new BigInteger("0");
                    BigInteger octetsBetween = new BigInteger("0");
                    BigInteger flowSpeed = new BigInteger("0");
                    double interfaceBandwidthUsedPercent = 0.0;

                    while (ifE.hasMoreElements()) {
                        interfacedata = ifE.nextElement();
                        ifIndex = interfacedata.getSubentity();
                        if (interfacedata.getEntity().equals("ifInOctets")) {
                            key = "ifInOctets" + ":" + ifIndex;
                            // 上一次采集的入口字节数
                            if (null != formerOctetsHastable.get(key)) {
                                formerOctets = new BigInteger(formerOctetsHastable.get(key).toString());
                            }
                            // 本次采集的入口字节数
                            CurrentOctets = new BigInteger(interfacedata.getThevalue().toString());
                            // 计时器反转处理
                            if (CurrentOctets.compareTo(formerOctets) < 0) {
                                if (host.getSnmpversion() == 0) {
                                    // 32位计数
                                    CurrentOctets = CurrentOctets.add(new BigInteger("4294967296"));
                                } else if (host.getSnmpversion() == 1 || host.getSnmpversion() == 2) {
                                    // 64位计数
                                    CurrentOctets = CurrentOctets.add(new BigInteger("18446744073709551615"));
                                }
                            }
                            if (longInterval.compareTo(new BigInteger("0")) != 0) {
                                octetsBetween = CurrentOctets.subtract(formerOctets);
                                flowSpeed = octetsBetween.divide(longInterval);// 除以时间差得到速率(B/s)
                            }

                            utilhdx = new UtilHdx();
                            utilhdx.setIpaddress(host.getIpAddress());
                            utilhdx.setCollecttime(date);
                            utilhdx.setCategory("Interface");
                            utilhdx.setSubentity(ifIndex);
                            utilhdx.setRestype("dynamic");
                            utilhdx.setUnit("kb/s");
                            utilhdx.setEntity("InBandwidthUtilHdx");
                            utilhdx.setChname(ifIndex + "端口入口流速");
                            utilhdx.setThevalue(df.format(flowSpeed.longValue() * 8 / 1000));
                            allInOctetsSpeed = allInOctetsSpeed + Long.parseLong(utilhdx.getThevalue());// 入口综合流速
                            utilhdxVector.addElement(utilhdx);
                            realoctetsHash.put(key, interfacedata.getThevalue());

                            utilhdxperc = new UtilHdxPerc();
                            utilhdxperc.setIpaddress(host.getIpAddress());
                            utilhdxperc.setCollecttime(date);
                            utilhdxperc.setCategory("Interface");
                            utilhdxperc.setSubentity(ifIndex);
                            utilhdxperc.setRestype("dynamic");
                            utilhdxperc.setUnit("%");
                            utilhdxperc.setEntity("InBandwidthUtilHdxPerc");
                            utilhdxperc.setChname(ifIndex + "端口入口带宽利用率");
                            double ifSpeed = 0.0;
                            String ifSpeedString = "0";
                            if (interfaceSpeedHash.get(ifIndex) != null) {
                                ifSpeedString = interfaceSpeedHash.get(ifIndex).toString();
                            }
                            if (!"0".equalsIgnoreCase(ifSpeedString)) {
                                ifSpeed = Double.parseDouble(ifSpeedString);
                                interfaceBandwidthUsedPercent = flowSpeed.longValue() * 8 / ifSpeed * 100;
                            } else {
                                interfaceBandwidthUsedPercent = 0;
                            }
                            utilhdxperc.setThevalue(df.format(interfaceBandwidthUsedPercent));
                            utilhdxpercVector.addElement(utilhdxperc);

                        } else if (interfacedata.getEntity().equals("ifOutOctets")) {
                            key = "ifOutOctets" + ":" + ifIndex;
                            // 上一次采集的入口字节数
                            if (null != formerOctetsHastable.get(key)) {
                                formerOctets = new BigInteger(formerOctetsHastable.get(key).toString());
                            }
                            // 本次采集的入口字节数
                            CurrentOctets = new BigInteger(interfacedata.getThevalue().toString());
                            // 计时器反转处理
                            if (CurrentOctets.compareTo(formerOctets) < 0) {
                                if (host.getSnmpversion() == 0) {
                                    // 32位计数
                                    CurrentOctets = CurrentOctets.add(new BigInteger("4294967296"));
                                } else if (host.getSnmpversion() == 1 || host.getSnmpversion() == 2) {
                                    // 64位计数
                                    CurrentOctets = CurrentOctets.add(new BigInteger("18446744073709551615"));
                                }
                            }
                            if (longInterval.compareTo(new BigInteger("0")) != 0) {
                                octetsBetween = CurrentOctets.subtract(formerOctets);
                                flowSpeed = octetsBetween.divide(longInterval);// 除以时间差得到速率(B/s)
                            }

                            utilhdx = new UtilHdx();
                            utilhdx.setIpaddress(host.getIpAddress());
                            utilhdx.setCollecttime(date);
                            utilhdx.setCategory("Interface");
                            utilhdx.setSubentity(ifIndex);
                            utilhdx.setRestype("dynamic");
                            utilhdx.setUnit("kb/s");
                            utilhdx.setEntity("OutBandwidthUtilHdx");
                            utilhdx.setChname(ifIndex + "端口出口流速");
                            utilhdx.setThevalue(df.format(flowSpeed.longValue() * 8 / 1000));
                            allOutOctetsSpeed = allOutOctetsSpeed + Long.parseLong(utilhdx.getThevalue());// 出口综合流速
                            utilhdxVector.addElement(utilhdx);
                            realoctetsHash.put(key, interfacedata.getThevalue());

                            utilhdxperc = new UtilHdxPerc();
                            utilhdxperc.setIpaddress(host.getIpAddress());
                            utilhdxperc.setCollecttime(date);
                            utilhdxperc.setCategory("Interface");
                            utilhdxperc.setSubentity(ifIndex);
                            utilhdxperc.setRestype("dynamic");
                            utilhdxperc.setUnit("%");
                            utilhdxperc.setEntity("OutBandwidthUtilHdxPerc");
                            utilhdxperc.setChname(ifIndex + "端口出口带宽利用率");
                            double ifSpeed = 0.0;
                            String ifSpeedString = "0";
                            if (interfaceSpeedHash.get(ifIndex) != null) {
                                ifSpeedString = interfaceSpeedHash.get(ifIndex).toString();
                            }
                            if (!"0".equalsIgnoreCase(ifSpeedString)) {
                                ifSpeed = Double.parseDouble(ifSpeedString);
                                interfaceBandwidthUsedPercent = flowSpeed.longValue() * 8 / ifSpeed * 100;
                            } else {
                                interfaceBandwidthUsedPercent = 0;
                            }
                            utilhdxperc.setThevalue(df.format(interfaceBandwidthUsedPercent));
                            utilhdxpercVector.addElement(utilhdxperc);

                        }
                    }

                    allOctetsSpeed = allInOctetsSpeed + allOutOctetsSpeed;// 综合流速

                }
                realoctetsHash.put("collecttime", date);
                ShareData.setRealOctetsdata(host.getIpAddress(), realoctetsHash);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
        }
        AllUtilHdx allInutilhdx = new AllUtilHdx();
        allInutilhdx = new AllUtilHdx();
        allInutilhdx.setIpaddress(host.getIpAddress());
        allInutilhdx.setCollecttime(date);
        allInutilhdx.setCategory("Interface");
        allInutilhdx.setEntity("AllInBandwidthUtilHdx");
        allInutilhdx.setSubentity("AllInBandwidthUtilHdx");
        allInutilhdx.setRestype("dynamic");
        allInutilhdx.setUnit("kb/秒");
        allInutilhdx.setChname("入口流速");
        allInutilhdx.setThevalue(Long.toString(allInOctetsSpeed * 8));
        allutilhdxVector.addElement(allInutilhdx);

        AllUtilHdx alloututilhdx = new AllUtilHdx();
        alloututilhdx = new AllUtilHdx();
        alloututilhdx.setIpaddress(host.getIpAddress());
        alloututilhdx.setCollecttime(date);
        alloututilhdx.setCategory("Interface");
        alloututilhdx.setEntity("AllOutBandwidthUtilHdx");
        alloututilhdx.setSubentity("AllOutBandwidthUtilHdx");
        alloututilhdx.setRestype("dynamic");
        alloututilhdx.setUnit("kb/秒");
        alloututilhdx.setChname("出口流速");
        alloututilhdx.setThevalue(Long.toString(allOutOctetsSpeed * 8));
        allutilhdxVector.addElement(alloututilhdx);

        allutilhdx = new AllUtilHdx();
        allutilhdx.setIpaddress(host.getIpAddress());
        allutilhdx.setCollecttime(date);
        allutilhdx.setCategory("Interface");
        allutilhdx.setEntity("AllBandwidthUtilHdx");
        allutilhdx.setSubentity("AllBandwidthUtilHdx");
        allutilhdx.setRestype("dynamic");
        allutilhdx.setUnit("kb/秒");
        allutilhdx.setChname("综合流速");
        allutilhdx.setThevalue(Long.toString(allOctetsSpeed * 8));
        allutilhdxVector.addElement(allutilhdx);
        returnHash.put("allutilhdx", allutilhdxVector);
        returnHash.put("utilhdxperc", utilhdxpercVector);
        returnHash.put("utilhdx", utilhdxVector);

        return returnHash;
    }

    public int getInterval(float d, String t) {
        int interval = 0;
        if (t.equals("d"))
            interval = (int) d * 24 * 60 * 60; // 天数
        else if (t.equals("h"))
            interval = (int) d * 60 * 60; // 小时
        else if (t.equals("m"))
            interval = (int) d * 60; // 分钟
        else if (t.equals("s"))
            interval = (int) d; // 秒
        return interval;
    }
}
