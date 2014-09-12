/*
 * @(#)CategoryXMLService.java     v1.01, Dec 21, 2012
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.node.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.afunms.initialize.ResourceCenter;
import com.afunms.node.model.Category;
import com.afunms.node.model.Column;
import com.afunms.node.model.Table;

/**
 * ClassName:   CategoryXMLService.java
 * <p>
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Dec 21, 2012 11:22:13 PM
 */
public class CategoryXMLService {

    /**
     * CategoryXML:
     * <p>设备类型配置文件
     *
     * @since   v1.01
     */
    public static final String CategoryXML = ResourceCenter.getInstance().getSysPath() + "WEB-INF/classes/node-category-config.xml";

    /**
     * rootCategory:
     * <p>根节点
     *
     * @since   v1.01
     */
    private static Category rootCategory = null;

    /**
     * getRootCategory:
     * <p>获取根节点
     *
     * @return
     *
     * @since   v1.01
     */
    public static Category getRootCategory() {
        if (rootCategory == null) {
            rootCategory = new Category();
            rootCategory.setId(0);
            parseXML();
        }
        return rootCategory;
    }

    /**
     * parseXML:
     * <p>解析XML
     *
     * @since   v1.01
     */
    @SuppressWarnings("unchecked")
    public static void parseXML() {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new File(CategoryXML));
            List list = doc.getRootElement().getChildren("category");
            for (Object object : list) {
                Element element = (Element) object;
                createCategory(rootCategory, element);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  
    }

    /**
     * 
     * createCategory:
     * <p>通过 XML 节点创建 {@link Category} ，并放入至父节点中
     *
     * @param   father
     *          - 父节点
     * @param   element
     *          - XML 节点
     *
     * @since   v1.01
     */
    @SuppressWarnings("unchecked")
    public static void createCategory(Category father, Element element) {
        List categorylist = element.getChildren("category");
        int id = Integer.valueOf(element.getAttributeValue("id"));
        String name = element.getAttributeValue("name");

        boolean isLeaf = false;
        if (categorylist == null || categorylist.size() == 0) {
            isLeaf = true;
        }

        Category category = new Category();
        category.setFather(father);
        category.setId(id);
        category.setLeaf(isLeaf);
        category.setName(name);

        father.addChild(category);

        System.out.println(isLeaf + "=========================" + category.getName());
        if (!isLeaf) {
            for (Object object : categorylist) {
                Element childElement = (Element) object;
                createCategory(category, childElement);
            }
        }
        
        List<Table> tableList = createTable(element);
        System.out.println(tableList.size() + "=========================" + category.getName());
        category.setTableList(tableList);
    }

    /**
     * createTable:
     * <p>通过 {@link Category} 节点创建表
     *
     * @param   element
     *          - {@link Category} 节点
     * @return  {@link List<Table>}
     *          - 表
     *
     * @since   v1.01
     */
    @SuppressWarnings("unchecked")
    private static List<Table> createTable(Element element) {
        List<Table> tableList = new ArrayList<Table>();
        Element tablesElement = element.getChild("tables");
        if (tablesElement == null) {
            return tableList;
        }
        List<Element> tableElementList = tablesElement.getChildren("table");
        if (tableElementList != null && tableElementList.size() > 0) {
            System.out.println(tableElementList.size());
            for (Object object : tableElementList) {
                Element tableElement = (Element) object;

                List columnElementList = tableElement.getChildren("column");

                List<Column> columnList = new ArrayList<Column>();
                if (columnElementList != null && columnElementList.size() > 0) {
                    for (Object object2 : columnElementList) {
                        Element columnElement = (Element) object2;

                        String idStr = columnElement.getAttributeValue("id");
                        String columnName = columnElement.getAttributeValue("name");
                        String type = columnElement.getAttributeValue("type");
                        String lengthStr = columnElement.getAttributeValue("length");
                        String notNullStr = columnElement.getAttributeValue("not-null");
                        String autoIncrementStr = columnElement.getAttributeValue("auto-increment");

                        int id = -1;
                        if (idStr != null && idStr.trim().length() > 0) {
                            id = Integer.valueOf(idStr);
                        }

                        boolean notNull = false;
                        if (notNullStr != null && notNullStr.trim().length() > 0) {
                            notNull = Boolean.valueOf(notNullStr);
                        }

                        boolean autoIncrement = false;
                        if (autoIncrementStr != null && autoIncrementStr.trim().length() > 0) {
                            autoIncrement = Boolean.valueOf(autoIncrementStr);
                        }

                        int length = -1;
                        if (lengthStr != null && lengthStr.trim().length() > 0) {
                            length = Integer.valueOf(lengthStr);
                        }

                        Column column = new Column();
                        column.setName(columnName);
                        column.setAutoIncrement(autoIncrement);
                        column.setId(id);
                        column.setLength(length);
                        column.setNotNull(notNull);
                        column.setType(type);

                        columnList.add(column);
                    }
                }

                String tableName = tableElement.getAttributeValue("name");
                String engine = tableElement.getAttributeValue("engine");
                String charset = tableElement.getAttributeValue("charset");
                String primaryKey = tableElement.getAttributeValue("primary-key");

                Table table = new Table();
                table.setName(tableName);
                table.setColumnList(columnList);
                table.setCharset(charset);
                table.setEngine(engine);
                table.setPrimaryKey(primaryKey);

                tableList.add(table);
            }
        }
        return tableList;
    }
}

