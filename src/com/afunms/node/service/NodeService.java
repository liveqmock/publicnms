/*
 * @(#)NodeService.java     v1.01, Dec 22, 2012
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.node.service;

import com.afunms.common.base.BaseVo;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.node.model.Category;

/**
 * ClassName:   NodeService.java
 * <p>
 *
 * @author      ƒÙ¡÷
 * @version     v1.01
 * @since       v1.01
 * @Date        Dec 22, 2012 6:04:34 PM
 */
public class NodeService {

    public void addNode(BaseVo baseVo) {
        NodeUtil nodeUtil = new NodeUtil();
        NodeDTO node = nodeUtil.conversionToNodeDTO(baseVo);
        addNode(node);
    }

    public void addNode(NodeDTO node) {
        CategoryService categoryService = new CategoryService();
        Category category = categoryService.getCategory(node);
        
        System.out.println(category.getName());
        ConfigInfoService service = new ConfigInfoService();
        service.addNodeConfigInfo(node, category);
    }

    public void deleteNode(BaseVo baseVo) {
        NodeUtil nodeUtil = new NodeUtil();
        NodeDTO node = nodeUtil.conversionToNodeDTO(baseVo);
        deleteNode(node);
    }

    public void deleteNode(NodeDTO node) {
        CategoryService categoryService = new CategoryService();
        Category category = categoryService.getCategory(node);
        
        ConfigInfoService service = new ConfigInfoService();
        service.deleteNodeConfigInfo(node, category);
    }
}

