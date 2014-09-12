package com.afunms.polling.snmp.interfaces;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import com.afunms.polling.om.IpMac;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetDatatempFdbRtosql;

public class FdbSnmp extends SnmpMonitor {
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public FdbSnmp() {
    }

    public void collectData(Node node, MonitoredItem item) {

    }

    public void collectData(HostNode node) {

    }

    public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
        Hashtable returnHash = new Hashtable();
        Vector fdbVector = new Vector();
        Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
        if (node == null)
            return returnHash;
        try {
            Calendar date = Calendar.getInstance();
            Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
            if (ipAllData == null)
                ipAllData = new Hashtable();
            Vector ipmacVector = new Vector();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
                Date cc = date.getTime();
                String time = sdf.format(cc);
                snmpnode.setLastTime(time);
                ipmacVector = (Vector) ipAllData.get("ipmac");
            } catch (Exception e) {

            }
            Hashtable MACVSIP = new Hashtable();
            if (ipmacVector != null && ipmacVector.size() > 0) {
                for (int i = 0; i < ipmacVector.size(); i++) {
                    IpMac ipmac = (IpMac) ipmacVector.get(i);
                    if (ipmac != null && ipmac.getMac() != null && ipmac.getIpaddress() != null && !ipmac.getIfindex().equals("unknown"))
                        MACVSIP.put(ipmac.getMac(), ipmac.getIpaddress());
                }
            }

            try {
                String[][] valueArray = null;
                valueArray = SnmpUtils.getFdbTable(node.getIpAddress(), node.getCommunity(), node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
                if (valueArray != null && valueArray.length > 0) {
                    for (int i = 0; i < valueArray.length; i++) {
                        if (valueArray[i][0] == null || valueArray[i][3] == null) {
                            continue;
                        }
//                        System.out.println(node.getIpAddress() + " > " + valueArray[i][0] + " " + valueArray[i][1] + " " + valueArray[i][2] + " " + valueArray[i][3] + " " + valueArray[i][4]);
                        IpMac ipmac = new IpMac();
                        ipmac.setIfindex(valueArray[i][1]);
                        ipmac.setMac(valueArray[i][0]);
                        ipmac.setIfband("0");
                        ipmac.setIfsms("0");
                        ipmac.setBak(valueArray[i][3]);
                        ipmac.setCollecttime(new GregorianCalendar());
                        ipmac.setRelateipaddr(node.getIpAddress());
                        ipmac.setIpaddress(valueArray[i][4]);
                        ipmac.setRelateipaddr(node.getIpAddress());// 设置交换机IP
                        fdbVector.add(ipmac);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if(fdbVector.size() == 0) {
            	//没有采集到数据
            	IpMac ipmac = new IpMac();
                ipmac.setIfindex("unknown");
                ipmac.setMac("unknown");
                ipmac.setIfband("0");
                ipmac.setIfsms("0");
                ipmac.setBak("unknown");
                ipmac.setCollecttime(new GregorianCalendar());
                ipmac.setRelateipaddr(node.getIpAddress());
                ipmac.setIpaddress("unknown");
                ipmac.setRelateipaddr(node.getIpAddress());// 设置交换机IP
                fdbVector.add(ipmac);
            }
            // 得到所有FDBend
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
            Hashtable ipAllData = new Hashtable();
            if (ipAllData == null)
                ipAllData = new Hashtable();
            if (fdbVector != null && fdbVector.size() > 0)
                ipAllData.put("fdb", fdbVector);
            ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
        } else {
            if (fdbVector != null && fdbVector.size() > 0)
                ((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("fdb", fdbVector);

        }

        returnHash.put("fdb", fdbVector);
        fdbVector = null;

     
        NetDatatempFdbRtosql totempsql = new NetDatatempFdbRtosql();
        totempsql.CreateResultTosql(returnHash, node);
      
        return returnHash;
    }

    public Vector gethh3cFdb(Host host) {

        Vector tempVector = new Vector();
        String[][] interfaceValueArray = null;
        String[][] fdbValueArray = null;
        String[][] arpValueArray = null;

        String[] interfaceTableOids = new String[] { "1.3.6.1.2.1.2.2.1.1", // ifIndex
                "1.3.6.1.2.1.2.2.1.2" // ifDescr
        };

        String[] fdbTableOids = new String[] { "1.3.6.1.4.1.25506.8.35.3.1.1.1", // mac
                "1.3.6.1.4.1.25506.8.35.3.1.1.2", // vlanid
                "1.3.6.1.4.1.25506.8.35.3.1.1.3"// ifindex
        };

        String[] arpTableOids = new String[] { "1.3.6.1.2.1.4.22.1.2", // mac
                "1.3.6.1.2.1.4.22.1.3", // ip
        };

        try {
            interfaceValueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), interfaceTableOids, host.getSnmpversion(), 3, 1000 * 30);
            fdbValueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), fdbTableOids, host.getSnmpversion(), 3, 1000 * 30);
            arpValueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), arpTableOids, host.getSnmpversion(), 3, 1000 * 30);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < fdbValueArray.length; i++) {
            String ifIndex = fdbValueArray[i][2];
            for (int j = 0; j < interfaceValueArray.length; j++) {
                String index = interfaceValueArray[j][0];
                if (ifIndex.equals(index)) {
                    fdbValueArray[i][2] = interfaceValueArray[j][1];
                }
            }
        }
        for (int i = 0; i < fdbValueArray.length; i++) {
            String ifMac = fdbValueArray[i][0];
            for (int j = 0; j < arpValueArray.length; j++) {
                String mac = arpValueArray[j][0];
                if (ifMac.equals(mac)) {
                    fdbValueArray[i][3] = arpValueArray[j][1];
                }
            }
        }

        if (fdbValueArray != null) {
            for (int i = 0; i < fdbValueArray.length; i++) {
                IpMac ipmac = new IpMac();
                ipmac.setIfindex(fdbValueArray[i][1]);
                ipmac.setMac(fdbValueArray[i][0]);
                ipmac.setIfband("0");
                ipmac.setIfsms("0");
                ipmac.setBak(fdbValueArray[i][2]);
                ipmac.setCollecttime(new GregorianCalendar());
                ipmac.setRelateipaddr(host.getIpAddress());
                ipmac.setIpaddress(fdbValueArray[i][3]);
                tempVector.add(ipmac);
            }
        }

        return tempVector;
    }

}
