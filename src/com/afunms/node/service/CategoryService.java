/*
 * @(#)CategoryService.java     v1.01, Dec 21, 2012
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.node.service;

import java.util.List;

import com.afunms.indicators.model.NodeDTO;
import com.afunms.node.model.Category;

/**
 * ClassName:   CategoryService.java
 * <p>{@link Category} 设备类型的服务
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Dec 21, 2012 7:01:43 PM
 */
public class CategoryService {

    public List<Category> getAllCategory() {
        return null;
    }

    public Category getCategory(NodeDTO node) {
        Category category = getRootCategory().getChild(node.getType());
        Category child = category.getChild(node.getSubtype());
        return child;
    }

    public static Category getRootCategory() {
        return CategoryXMLService.getRootCategory();
    }
}

