/*
 * @(#)JBossPingIndicatorGather.java     v1.01, Feb 25, 2013
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.polling.snmp.jboss;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.jbossmonitor.HttpClientJBoss;
import com.afunms.application.model.JBossConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.api.IndicatorGather;
import com.afunms.polling.node.Result;
import com.afunms.polling.om.Pingcollectdata;
import com.gatherResulttosql.JBossPingResultTosql;

/**
 * ClassName:   JBossPingIndicatorGather.java
 * <p>{@link JBossPingIndicatorGather} {@link JBossConfig} 的 Ping 指标采集类
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Feb 25, 2013 11:52:54 AM
 */
public class JBossPingIndicatorGather extends SnmpMonitor implements
        IndicatorGather {

    /**
     * collect_Data:
     * <p>采集方法
     *
     * @return
     *
     * @since   v1.01
     */
    public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
        Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
        JBossConfig node = null;
        JBossConfigDao dao = new JBossConfigDao();
        try {
            node = (JBossConfig) dao.findByID(nodeGatherIndicators.getNodeid());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dao.close();
        }
        Result result = getValue(node, nodeGatherIndicators);
        Pingcollectdata pingcollectdata = (Pingcollectdata) result.getResult();
        Vector<Pingcollectdata> vector = new Vector<Pingcollectdata>();
        vector.add(pingcollectdata);
        String value = pingcollectdata.getThevalue();
        
        NodeDTO nodeDTO = new NodeUtil().conversionToNodeDTO(node);
        String nodeid = nodeDTO.getNodeid();
        String type = nodeDTO.getType();
        String subtype = nodeDTO.getSubtype();
        AlarmIndicatorsUtil util = new AlarmIndicatorsUtil();
        List<AlarmIndicatorsNode> list = util.getAlarmIndicatorsForNode(nodeid, type, subtype);
        try {
            CheckEventUtil checkEventUtil = new CheckEventUtil();
            if (list != null) {
                for (AlarmIndicatorsNode alarmIndicatorsNode : list) {
                    if ("ping".equals(alarmIndicatorsNode.getName())) {
                        checkEventUtil.checkEvent(node, alarmIndicatorsNode, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Hashtable<String, Vector<Pingcollectdata>> ipdata = new Hashtable<String, Vector<Pingcollectdata>>();
        ipdata.put("ping", vector);
        try {
            JBossPingResultTosql resultTosql = new JBossPingResultTosql();
            resultTosql.CreateResultTosql(ipdata, nodeid);
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ipdata;
    }

    /**
     * getValue:
     * <p>获取结果
     *
     * @param   node
     *          - 设备
     * @param   nodeGatherIndicators
     *          - 采集指标
     * @return  {@link Result}
     *          - 返回采集结果
     *
     * @since   v1.01
     * @see com.afunms.polling.api.IndicatorGather#getValue(com.afunms.polling.base.Node, com.afunms.indicators.model.NodeGatherIndicators)
     */
    public Result getValue(BaseVo node, NodeGatherIndicators nodeGatherIndicators) {
        JBossConfig jbossConfig = (JBossConfig) node;
        String ipaddress = jbossConfig.getIpaddress();
        HttpClientJBoss jboss = new HttpClientJBoss();
        String src = null;
        try {
            src = jboss.getGetResponseWithHttpClient("http://" + ipaddress + ":"+jbossConfig.getPort()+"/web-console/ServerInfo.jsp", "GBK");
        } catch (Exception e) {
        }
        Calendar date = Calendar.getInstance();
        Pingcollectdata pingcollectdata = new Pingcollectdata();
        pingcollectdata.setIpaddress(ipaddress);
        pingcollectdata.setCollecttime(date);
        pingcollectdata.setCategory("Ping");
        pingcollectdata.setEntity("Utilization");
        pingcollectdata.setSubentity("ConnectUtilization");
        pingcollectdata.setRestype("dynamic");
        pingcollectdata.setUnit("%");
        if (src != null && src.contains("Version")) {
            pingcollectdata.setThevalue("100");
        } else {
            pingcollectdata.setThevalue("0");
        }
        Result result = new Result();
        result.setCollectTime(date.getTime());
        result.setErrorCode(1);
        result.setErrorInfo("");
        result.setResult(pingcollectdata);
        return result;
    }
}

