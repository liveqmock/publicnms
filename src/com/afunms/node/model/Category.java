/*
 * @(#)Category.java     v1.01, Dec 21, 2012
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.node.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseVo;

/**
 * ClassName:   Category.java
 * <p> {@link Category} 设备类型
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Dec 21, 2012 5:43:11 PM
 */
public class Category extends BaseVo implements Serializable {

    /**
     * serialVersionUID:
     * <p>序列化 Id
     *
     * @since   v1.01
     */
    private static final long serialVersionUID = 5904029678714154986L;

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
     * isLeaf:
     * <p>是否为叶节点
     *
     * @since   v1.01
     */
    private boolean isLeaf;

    /**
     * tableList:
     * <p>表
     *
     * @since   v1.01
     */
    private List<Table> tableList;

    /**
     * father:
     * <p>父节点
     *
     * @since   v1.01
     */
    private Category father;

    /**
     * children:
     * <p>子节点
     *
     * @since   v1.01
     */
    private List<Category> children;

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
     * isLeaf:
     * <p>是否为叶节点
     *
     * @return  boolean
     *          - 如果为叶节点则返回 <code>true</code> ，否者返回 <code>false</code>
     * @since   v1.01
     */
    public boolean isLeaf() {
        return isLeaf;
    }

    /**
     * setLeaf:
     * <p>设置为叶节点
     *
     * @param   isLeaf
     *          - 是否为叶节点
     * @since   v1.01
     */
    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    /**
     * getTableList:
     * <p>获取表
     *
     * @return  {@link List<Table>}
     *          - 表
     * @since   v1.01
     */
    public List<Table> getTableList() {
        return tableList;
    }

    /**
     * setTableList:
     * <p>设置表
     *
     * @param   tableList
     *          - 表
     * @since   v1.01
     */
    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }

    /**
     * getFather:
     * <p>获取父节点
     *
     * @return  {@link Category}
     *          - 父节点
     * @since   v1.01
     */
    public Category getFather() {
        return father;
    }

    /**
     * setFather:
     * <p>设置父节点
     *
     * @param   father
     *          - 父节点
     * @since   v1.01
     */
    public void setFather(Category father) {
        this.father = father;
    }

    /**
     * getChildren:
     * <p>获取子节点
     *
     * @return  {@link List<Category>}
     *          - 子节点
     * @since   v1.01
     */
    public List<Category> getChildren() {
        return children;
    }

    /**
     * setChildren:
     * <p>设置子节点
     *
     * @param   children
     *          - 子节点
     * @since   v1.01
     */
    public void setChildren(List<Category> children) {
        this.children = children;
    }

    /**
     * addChild:
     * <p>添加子节点
     *
     * @param   child
     *          - 子节点
     *
     * @since   v1.01
     */
    public void addChild(Category child) {
        if (this.children == null) {
            this.children = new ArrayList<Category>();
        }
        this.children.add(child);
    }

    /**
     * removeChild:
     * <p>删除一个子节点
     *
     * @param   index
     *          - 需要删除子节点的索引
     * @return  {@link Category}
     *          - 子节点
     *
     * @since   v1.01
     */
    public Category removeChild(int index) {
        List<Category> children = getChildren();
        if (children != null && children.size() > index) {
            return children.remove(index);
        }
        return null;
    }

    /**
     * getChild:
     * <p>获取子节点
     *
     * @param   index
     *          - 子节点的索引
     * @return  {@link Category}
     *          - 子节点
     *
     * @since   v1.01
     */
    public Category getChild(int index) {
        List<Category> children = getChildren();
        if (children != null && children.size() > index) {
            return children.get(index);
        }
        return null;
    }

    /**
     * getChild:
     * <p>根据名称获取子节点
     *
     * @param   name
     *          - 子节点名称
     * @return  {@link Category}
     *          - 子节点
     *
     * @since   v1.01
     */
    public Category getChild(String name) {
        List<Category> children = getChildren();
        if (children != null) {
            for (Category child : children) {
                if (name.equals(child.getName())) {
                    return child;
                }
            }
        }
        return null;
    }
}
