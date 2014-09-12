/*
 * @(#)PerformanceInfoService.java     v1.01, Jan 11, 2013
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.node.service;

import java.util.List;

import com.afunms.node.dao.PerformaceInfoTableDao;
import com.afunms.node.model.PerformanceInfo;

/**
 * ClassName:   PerformanceInfoService.java
 * <p>
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Jan 11, 2013 5:42:45 PM
 */
public class PerformanceInfoService {

    /**
     * getPerformance:
     * <p>获取性能信息
     *
     * @param table
     * @param startTime
     * @param endTime
     * @return
     *
     * @since   v1.01
     */
    public List<PerformanceInfo> getPerformance(String table, String startTime, String endTime) {
        List<PerformanceInfo> list = null;
        PerformaceInfoTableDao dao = new PerformaceInfoTableDao(table);
        try {
            list = dao.findByCollectTime(startTime, endTime);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            dao.close();
        }
        return list;
    }

    /**
     * save:
     * <p>保存性能信息
     *
     * @param   table
     *          - 性能信息表
     * @param   info
     *          - 性能信息
     * @return  {@link Boolean}
     *          - 如果保存成功，则返回 <code>true</code> , 否则返回 <code>false</code>
     *
     * @since   v1.01
     */
    public boolean save(String table, PerformanceInfo info) {
        boolean result = false;
        PerformaceInfoTableDao dao = new PerformaceInfoTableDao(table);
        try {
            result = dao.save(info);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            dao.close();
        }
        return result;
    }
}

