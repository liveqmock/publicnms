/*
 * @(#)ConfigInfoService.java     v1.01, Dec 13, 2012
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.node.service;

import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.base.BaseVo;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.node.model.Category;

/**
 * ClassName:   ConfigInfoService.java
 * <p> {@link ConfigInfoService} 添加设备服务，
 * 用于在添加完设备后，添加相关的其他信息
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Dec 13, 2012 4:18:51 PM
 */
public class ConfigInfoService {

    /**
     * addNodeConfigInfo:
     * <p>添加设备的配置信息
     *
     * @param   baseVo
     *          - 设备
     * @param   category
     *          - 类型
     *
     * @since   v1.01
     */
    public void addNodeConfigInfo(BaseVo baseVo, Category category) {
        NodeUtil nodeUtil = new NodeUtil();
        NodeDTO node = nodeUtil.conversionToNodeDTO(baseVo);
        addNodeConfigInfo(node, category);
    }

    /**
     * addNodeConfigInfo:
     * <p>添加设备的配置信息
     *
     * @param   node
     *          - 设备
     * @param   category
     *          - 类型
     *
     * @since   v1.01
     */
    public void addNodeConfigInfo(NodeDTO node, Category category) {
        createTable(node, category);
        addGatherIndicators(node, category);
        addAlarmIndicators(node, category);
    }

    /**
     * deleteNodeConfigInfo:
     * <p>删除设备的配置信息
     *
     * @param   baseVo
     *          - 设备
     * @param   category
     *          - 类型
     *
     * @since   v1.01
     */
    public void deleteNodeConfigInfo(BaseVo baseVo, Category category) {
        NodeUtil nodeUtil = new NodeUtil();
        NodeDTO node = nodeUtil.conversionToNodeDTO(baseVo);
        deleteNodeConfigInfo(node, category);
    }

    /**
     * deleteNodeConfigInfo:
     * <p>删除设备的配置信息
     *
     * @param   node
     *          - 设备
     * @param   category
     *          - 类型
     *
     * @since   v1.01
     */
    public void deleteNodeConfigInfo(NodeDTO node, Category category) {
        dropTable(node, category);
        deleteTemp(node, category);
        deleteGatherIndicators(node, category);
        deleteAlarmIndicators(node, category);
    }

    /**
     * createTable:
     * <p>创建表
     *
     * @param   node
     *          - 设备
     * @param   category
     *          - 类型
     *
     * @since   v1.01
     */
    private void createTable(NodeDTO node, Category category) {
        TableService service = new TableService();
        service.createTable(node, category);
    }

    /**
     * dropTable:
     * <p>创建表
     *
     * @param   node
     *          - 设备
     * @param   category
     *          - 类型
     *
     * @since   v1.01
     */
    private void dropTable(NodeDTO node, Category category) {
        TableService service = new TableService();
        service.dropTable(node, category);
    }

    /**
     * addGatherIndicators:
     * <p>添加采集指标
     *
     * @param   node
     *          - 设备
     * @param   category
     *          - 类型
     *
     * @since   v1.01
     */
    private void addGatherIndicators(NodeDTO node, Category category) {
        NodeGatherIndicatorsUtil gatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
        gatherIndicatorsUtil.addGatherIndicatorsForNode(node.getNodeid(), node.getType(), node.getSubtype());
    }

    /**
     * deleteGatherIndicators:
     * <p>删除采集指标
     *
     * @param   node
     *          - 设备
     * @param   category
     *          - 类型
     *
     * @since   v1.01
     */
    private void deleteGatherIndicators(NodeDTO node, Category category) {
        NodeGatherIndicatorsUtil gatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
        System.out.println(node.getNodeid() + "===" + node.getType() + "===" + node.getSubtype());
        gatherIndicatorsUtil.deleteAllGatherIndicatorsForNode(node.getNodeid(), node.getType(), node.getSubtype());
    }

    /**
     * addGatherIndicators:
     * <p>添加告警指标
     *
     * @param   node
     *          - 设备
     * @param   category
     *          - 类型
     *
     * @since   v1.01
     */
    private void addAlarmIndicators(NodeDTO node, Category category) {
        AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
        alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getNodeid(), node.getType(), node.getSubtype());
    }

    /**
     * deleteAlarmIndicators:
     * <p>删除告警指标
     *
     * @param   node
     *          - 设备
     * @param   category
     *          - 类型
     *
     * @since   v1.01
     */
    private void deleteAlarmIndicators(NodeDTO node, Category category) {
        AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
        alarmIndicatorsUtil.deleteAlarmInicatorsThresholdForNode(node.getNodeid(), node.getType(), node.getSubtype());
    }

    /**
     * deleteAlarmIndicators:
     * <p>删除告警指标
     *
     * @param   node
     *          - 设备
     * @param   category
     *          - 类型
     *
     * @since   v1.01
     */
    private void deleteTemp(NodeDTO node, Category category) {
        
    }
}

