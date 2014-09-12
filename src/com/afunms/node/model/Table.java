/*
 * @(#)Table.java     v1.01, Dec 21, 2012
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.node.model;

import java.io.Serializable;
import java.util.List;

import com.afunms.common.base.BaseVo;

/**
 * ClassName:   Table.java
 * <p> {@link Table} 设备类型中的表
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Dec 21, 2012 6:15:34 PM
 */
public class Table extends BaseVo implements Serializable {

    /**
     * serialVersionUID:
     * <p>序列化 ID
     *
     * @since   v1.01
     */
    private static final long serialVersionUID = -3746972947336858996L;

    /**
     * name:
     * <p>名称
     *
     * @since   v1.01
     */
    private String name;

    /**
     * columnList:
     * <p>列
     *
     * @since   v1.01
     */
    private List<Column> columnList;

    /**
     * primaryKey:
     * <p>主键
     *
     * @since   v1.01
     */
    private String primaryKey;

    /**
     * engine:
     * <p>数据引擎
     *
     * @since   v1.01
     */
    private String engine;
    
    /**
     * charset:
     * <p>编码
     *
     * @since   v1.01
     */
    private String charset;

    /**
     * getName:
     * <p>获取名称
     *
     * @return  {@link String}
     *          - 名称
     * @since   v1.01
     */
    public String getName() {
        return name;
    }

    /**
     * setName:
     * <p>设子名称
     *
     * @param   name
     *          - 名称
     * @since   v1.01
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getColumnList:
     * <p>获取列
     *
     * @return  {@link List<Column>}
     *          - 列
     * @since   v1.01
     */
    public List<Column> getColumnList() {
        return columnList;
    }

    /**
     * setColumnList:
     * <p>设置列
     *
     * @param   columnList
     *          - 列
     * @since   v1.01
     */
    public void setColumnList(List<Column> columnList) {
        this.columnList = columnList;
    }

    /**
     * getPrimaryKey:
     * <p>获取主键
     *
     * @return  {@link String}
     *          - 主键
     * @since   v1.01
     */
    public String getPrimaryKey() {
        return primaryKey;
    }

    /**
     * setPrimaryKey:
     * <p>设置主键
     *
     * @param   primaryKey
     *          - 主键
     * @since   v1.01
     */
    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * getEngine:
     * <p>获取数据引擎
     *
     * @return  {@link String}
     *          - 数据引擎
     * @since   v1.01
     */
    public String getEngine() {
        return engine;
    }

    /**
     * setEngine:
     * <p>设置数据引擎
     *
     * @param   engine
     *          - 数据引擎
     * @since   v1.01
     */
    public void setEngine(String engine) {
        this.engine = engine;
    }

    /**
     * getCharset:
     * <p>获取编码
     *
     * @return  {@link String}
     *          - 编码
     * @since   v1.01
     */
    public String getCharset() {
        return charset;
    }

    /**
     * setCharset:
     * <p>设置编码
     *
     * @param   charset
     *          - 编码
     * @since   v1.01
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
}

