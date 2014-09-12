/*
 * @(#)TableService.java     v1.01, Dec 13, 2012
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.node.service;

import java.util.List;

import com.afunms.indicators.model.NodeDTO;
import com.afunms.node.dao.NodeDao;
import com.afunms.node.model.Category;
import com.afunms.node.model.Table;

/**
 * ClassName:   TableService.java
 * <p>{@link TableService} 设备表服务类，
 * 用于创建，修改和删除设备的性能信息表。
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Dec 13, 2012 10:42:01 PM
 */
public class TableService {

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
    public void createTable(NodeDTO node, Category category) {
        List<Table> list = category.getTableList();
        NodeDao nodeDao = new NodeDao();
        try {
            nodeDao.createTable(node, list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            nodeDao.close();
        }
    }

    /**
     * dropTable:
     * <p>删除表
     *
     * @param   node
     *          - 设备
     * @param   category
     *          - 类型
     *
     * @since   v1.01
     */
    public void dropTable(NodeDTO node, Category category) {
        List<Table> list = category.getTableList();
        NodeDao nodeDao = new NodeDao();
        try {
            nodeDao.dropTable(node, list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            nodeDao.close();
        }
    }
}

