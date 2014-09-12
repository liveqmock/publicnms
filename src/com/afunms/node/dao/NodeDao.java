/*
 * @(#)NodeDao.java     v1.01, Dec 22, 2012
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.node.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.node.model.Column;
import com.afunms.node.model.Table;

/**
 * ClassName:   NodeDao.java
 * <p>
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Dec 22, 2012 6:53:12 PM
 */
public class NodeDao extends BaseDao implements DaoInterface {

    /**
     * loadFromRS:
     * <p>通过结果集创建 {@link BaseVo}
     *
     * @param   rs
     *          - 结果集
     * @return  {@link BaseVo}
     *          - baseVo
     *
     * @since   v1.01
     * @see     com.afunms.common.base.BaseDao#loadFromRS(java.sql.ResultSet)
     */
    @Override
    public BaseVo loadFromRS(ResultSet rs) {
        return null;
    }

    /**
     * save:
     * <p>保存 {@link BaseVo} 至数据库(未实现)
     *
     * @param   vo
     *          - {@link BaseVo}
     * @return  {@link Boolean}
     *          - 如果保存成功，则返回 <code>true</code> ，否则返回 <code>false</code>
     *
     * @since   v1.01
     * @see     com.afunms.common.base.DaoInterface#save(com.afunms.common.base.BaseVo)
     */
    public boolean save(BaseVo vo) {
        return false;
    }

    /**
     * update:
     * <p>修改 {@link BaseVo} 至数据库(未实现)
     *
     * @param   vo
     *          - {@link BaseVo}
     * @return  {@link Boolean}
     *          - 如果修改成功，则返回 <code>true</code> ，否则返回 <code>false</code>
     *
     * @since   v1.01
     * @see com.afunms.common.base.DaoInterface#update(com.afunms.common.base.BaseVo)
     */
    public boolean update(BaseVo vo) {
        return false;
    }

    /**
     * createTable:
     * <p>创建表
     *
     * @param   node
     *          - 设备
     * @param   list
     *          - 表
     * @return  {@link Boolean}
     *          - 如果创建成功则返回 <code>true</code> ，否则返回 <code>false</code>
     *
     * @since   v1.01
     */
    @SuppressWarnings("static-access")
    public boolean createTable(NodeDTO node, List<Table> list) {
        boolean result = false;
        if (list == null || list.size() == 0) {
            return result;
        }
        for (Table table : list) {
            List<Column> columnList = table.getColumnList();
            StringBuffer tableStringBuffer = new StringBuffer(200);
            tableStringBuffer.append("create table if not exists " + table.getName() + node.getNodeid());
            tableStringBuffer.append("(");
            for (Column column : columnList) {
                tableStringBuffer.append(column.getName());
                tableStringBuffer.append(" ");
                tableStringBuffer.append(column.getType());
                if (column.getLength() > 0) {
                    tableStringBuffer.append(" ");
                    tableStringBuffer.append("(" + column.getLength() + ")");
                }
                if (column.isNotNull()) {
                    tableStringBuffer.append(" ");
                    tableStringBuffer.append("not null");
                }
                if (column.isAutoIncrement()) {
                    tableStringBuffer.append(" ");
                    tableStringBuffer.append("auto_increment");
                }
                tableStringBuffer.append(",");
            }
            tableStringBuffer.append(" PRIMARY KEY (" + table.getPrimaryKey() + ")");
            tableStringBuffer.append(")");
            tableStringBuffer.append(" ENGINE=" + table.getEngine());
            tableStringBuffer.append(" DEFAULT CHARSET=" + table.getCharset());
            String tableSql = tableStringBuffer.toString();
            conn.addBatch(tableSql);
        }
        conn.executeBatch();
        return result;
    }

    /**
     * dropTable:
     * <p>删除表
     *
     * @param   node
     *          - 设备
     * @param   list
     *          - 表
     * @return  {@link Boolean}
     *          - 如果创建成功则返回 <code>true</code> ，否则返回 <code>false</code>
     * @since   v1.01
     */
    public boolean dropTable(NodeDTO node, List<Table> list) {
        boolean result = false;
        if (list == null || list.size() == 0) {
            return true;
        }
        for (Table table : list) {
            String sql = "drop table if exists " + table.getName() + node.getNodeid();
            conn.addBatch(sql);
        }
        conn.executeBatch();
        result = true;
        return result;
    }
}

