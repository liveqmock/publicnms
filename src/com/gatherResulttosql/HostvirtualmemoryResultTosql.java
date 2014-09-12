package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.Memorycollectdata;
import com.gatherdb.GathersqlListManager;

public class HostvirtualmemoryResultTosql {

    /**
     * 
     * 根据采集结果生成对应的sql放入到内存列表中
     */
    public void CreateResultTosql(Hashtable ipdata, String ip) {

        if (ipdata.containsKey("memory")) {

            // Hashtable physicalmemoryhash =
            // (Hashtable)ipdata.get("virtualmemory");
            Vector memoryVector = (Vector) ipdata.get("memory");

            String allipstr = SysUtil.doip(ip);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Memorycollectdata memorydata = null;

            if (memoryVector != null && memoryVector.size() > 0) {
                for (int si = 0; si < memoryVector.size(); si++) {
                    memorydata = (Memorycollectdata) memoryVector.elementAt(si);
                    if (memorydata.getSubentity().equalsIgnoreCase("VirtualMemory") || memorydata.getSubentity().equalsIgnoreCase("SwapMemory")) {
                        if (memorydata.getRestype().equals("dynamic")) {
                            Calendar tempCal = (Calendar) memorydata.getCollecttime();
                            Date cc = tempCal.getTime();
                            String time = sdf.format(cc);
                            String tablename = "memory" + allipstr;
                            // if
                            // (memorydata.getIpaddress().equals("10.217.255.253")||memorydata.getIpaddress().equals("10.217.255.64")||memorydata.getIpaddress().equals("10.216.2.35")){
                            String sql = "";
                            if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
                                sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "','" + time + "')";
                            } else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
                                sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
                            }

                            // System.out.println("====虚拟内存==="+sql);
                            GathersqlListManager.Addsql(sql);
                            sql = null;
                            tablename = null;
                            time = null;
                            cc = null;
                            tempCal = null;

                        }
                    }
                }
                memorydata = null;

            }
        }

    }

    /**
     * 作用：交换内存入库 根据采集结果生成对应的SQL放入到入库内存列表中
     */
    public void CreateSwapMemoryResultTosql(Hashtable ipdata, String ip) {

        if (ipdata.containsKey("memory")) {

            Vector memoryVector = (Vector) ipdata.get("memory");

            String allipstr = SysUtil.doip(ip);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Memorycollectdata memorydata = null;

            if (memoryVector != null && memoryVector.size() > 0) {
                for (int si = 0; si < memoryVector.size(); si++) {
                    memorydata = (Memorycollectdata) memoryVector.elementAt(si);
                    if (!memorydata.getSubentity().equalsIgnoreCase("SwapMemory"))
                        continue;
                    if (memorydata.getRestype().equals("dynamic")) {
                        Calendar tempCal = (Calendar) memorydata.getCollecttime();
                        Date cc = tempCal.getTime();
                        String time = sdf.format(cc);
                        String tablename = "memory" + allipstr;
                        String sql = "";
                        if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
                            sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "','" + time + "')";
                        } else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
                            sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
                        }
                        GathersqlListManager.Addsql(sql);
                        sql = null;
                        tablename = null;
                        time = null;
                        cc = null;
                        tempCal = null;

                    }
                }
                memorydata = null;

            }
        }

    }

}
