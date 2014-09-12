/*
 * @(#)Column.java     v1.01, Dec 21, 2012
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.node.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/**
 * ClassName:   Column.java
 * <p> {@link Column} 设备类型中表中的列
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Dec 21, 2012 6:37:02 PM
 */
public class Column extends BaseVo implements Serializable {

    /**
     * serialVersionUID:
     * <p>序列化 Id
     *
     * @since   v1.01
     */
    private static final long serialVersionUID = -6505868871572525679L;

    /**
     * id:
     * <p>id
     *
     * @since   v1.01
     */
    private int id;

    /**
     * name:
     * <p>名称
     *
     * @since   v1.01
     */
    private String name;

    /**
     * type:
     * <p>字段类型
     *
     * @since   v1.01
     */
    private String type;

    /**
     * length:
     * <p>长度
     *
     * @since   v1.01
     */
    private int length;

    /**
     * notNull:
     * <p>不为空
     *
     * @since   v1.01
     */
    private boolean notNull;

    /**
     * autoIncrement:
     * <p>自增长
     *
     * @since   v1.01
     */
    private boolean autoIncrement;

    /**
     * getId:
     * <p>获取 id
     *
     * @return  {@link Integer}
     *          - id
     * @since   v1.01
     */
    public int getId() {
        return id;
    }

    /**
     * setId:
     * <p>设置 id
     *
     * @param   id
     *          - id
     * @since   v1.01
     */
    public void setId(int id) {
        this.id = id;
    }

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
     * <p>设置名称
     *
     * @param   name
     *          - 名称
     * @since   v1.01
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getType:
     * <p>获取类型
     *
     * @return  {@link String}
     *          - 类型
     * @since   v1.01
     */
    public String getType() {
        return type;
    }

    /**
     * setType:
     * <p>设置类型
     *
     * @param   type
     *          - 类型
     * @since   v1.01
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * getLength:
     * <p>获取长度
     *
     * @return  {@link Integer}
     *          - 长度
     * @since   v1.01
     */
    public int getLength() {
        return length;
    }

    /**
     * setLength:
     * <p>设置长度
     *
     * @param   length
     *          - 长度
     * @since   v1.01
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * isNotNull:
     * <p>如果不可以为空，则返回为 <code>true</code> ，否则返回为 <code>false</code>
     *
     * @return  {@link Boolean}
     *          - 如果不可以为空，则返回为 <code>true</code> ，否则返回为 <code>false</code>
     * @since   v1.01
     */
    public boolean isNotNull() {
        return notNull;
    }

    /**
     * setNotNull:
     * <p>如果不可以为空，则设置为 <code>true</code> ，否则设置为 <code>false</code>
     *
     * @param   notNull
     *          - 如果不可以为空，则设置为 <code>true</code> ，否则设置为 <code>false</code>
     * @since   v1.01
     */
    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    /**
     * isAutoIncrement:
     * <p>如果为自增长，则返回为 <code>true</code> ，否则返回为 <code>false</code>
     *
     * @return  {@link Boolean}
     *          - 如果为自增长，则返回为 <code>true</code> ，否则返回为 <code>false</code>
     * @since   v1.01
     */
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    /**
     * setAutoIncrement:
     * <p>如果为自增长，则设置为 <code>true</code> ，否则设置为 <code>false</code>
     *
     * @param   autoIncrement
     *          - 如果为自增长，则设置为 <code>true</code> ，否则设置为 <code>false</code>
     * @since   v1.01
     */
    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

}

