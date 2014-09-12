package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.Pingcollectdata;
import com.gatherdb.GathersqlListManager;

public class JBossPingResultTosql {

    /**
     * 
     * 根据采集结果生成对应的sql放入到内存列表中
     */
    public void CreateResultTosql(Hashtable ipdata, String nodeid) {
        if (ipdata.containsKey("ping")) {
            // ping
            Vector<Pingcollectdata> pingVector = (Vector<Pingcollectdata>) ipdata.get("ping");
            System.out.println("JBOSS 入库================================" + pingVector.size());
            if (pingVector != null && pingVector.size() > 0) {
                for (Pingcollectdata pingcollectdata : pingVector) {
                    Calendar tempCal = (Calendar) pingcollectdata.getCollecttime();
                    Date cc = tempCal.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    String time = sdf.format(cc);
                    String tablename = "jbossping" + nodeid;
                    String sql = "";
                    if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
                        sql = "insert into "
                                + tablename
                                + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
                                + "values('" + pingcollectdata.getIpaddress() + "','"
                                + pingcollectdata.getRestype() + "','"
                                + pingcollectdata.getCategory() + "','"
                                + pingcollectdata.getEntity() + "','"
                                + pingcollectdata.getSubentity() + "','"
                                + pingcollectdata.getUnit() + "','"
                                + pingcollectdata.getChname() + "','"
                                + pingcollectdata.getBak() + "',"
                                + pingcollectdata.getCount() + ",'"
                                + pingcollectdata.getThevalue() + "','" + time
                                + "')";
                    } else if ("oracle"
                            .equalsIgnoreCase(SystemConstant.DBType)) {
                        sql = "insert into "
                                + tablename
                                + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
                                + "values('" + pingcollectdata.getIpaddress() + "','"
                                + pingcollectdata.getRestype() + "','"
                                + pingcollectdata.getCategory() + "','"
                                + pingcollectdata.getEntity() + "','"
                                + pingcollectdata.getSubentity() + "','"
                                + pingcollectdata.getUnit() + "','"
                                + pingcollectdata.getChname() + "','"
                                + pingcollectdata.getBak() + "',"
                                + pingcollectdata.getCount() + ",'"
                                + pingcollectdata.getThevalue() + "',to_date('"
                                + time + "','YYYY-MM-DD HH24:MI:SS'))";
                    }
                    System.out.println(sql + "=================adfsadfsadfasdf=================");
                    GathersqlListManager.Addsql(sql);
                }
            }
        }

    }

}
