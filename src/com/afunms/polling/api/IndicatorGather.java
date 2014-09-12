/*
 * @(#)IndicatorGather.java     v1.01, Jan 4, 2013
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.polling.api;

import com.afunms.common.base.BaseVo;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Result;

/**
 * ClassName:   IndicatorGather.java
 * <p>{@link IndicatorGather} 指标采集接口
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Jan 4, 2013 9:01:02 AM
 */
public interface IndicatorGather {

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
     */
    Result getValue(BaseVo node, NodeGatherIndicators nodeGatherIndicators);
}

