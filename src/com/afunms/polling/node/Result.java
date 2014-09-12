/*
 * @(#)Result.java     v1.01, Dec 28, 2012
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.polling.node;

import java.util.Date;

import com.afunms.common.base.BaseVo;

/**
 * ClassName:   Result.java
 * <p> {@link Result} 采集结果
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Dec 28, 2012 11:34:30 AM
 */
public class Result extends BaseVo {

    /**
     * result:
     * <p>结果
     *
     * @since   v1.01
     */
    private Object result;

    /**
     * errorCode:
     * <p>编码，1为成功，未成功小于0
     *
     * @since   v1.01
     */
    private int errorCode;

    /**
     * errorInfo:
     * <p>错误信息
     *
     * @since   v1.01
     */
    private String errorInfo;

    /**
     * collectTime:
     * <p>采集时间
     *
     * @since   v1.01
     */
    private Date collectTime;

    /**
     * getResult:
     * <p>获取结果
     *
     * @return  {@link Object}
     *          - 结果
     * @since   v1.01
     */
    public Object getResult() {
        return result;
    }

    /**
     * setResult:
     * <p>设置结果
     *
     * @param   result
     *          - 结果
     * @since   v1.01
     */
    public void setResult(Object result) {
        this.result = result;
    }

    
    /**
     * getErrorCode:
     * <p>获取编码
     *
     * @return  {@link Integer}
     *          - 编码
     * @since   v1.01
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * setErrorCode:
     * <p>设置编码
     *
     * @param   code
     *          - 编码
     * @since   v1.01
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * getErrorInfo:
     * <p>获取错误信息
     *
     * @return  {@link String}
     *          - 错误信息
     * @since   v1.01
     */
    public String getErrorInfo() {
        return errorInfo;
    }

    /**
     * setErrorInfo:
     * <p>设置错误信息
     *
     * @param   reason
     *          - 错误信息
     * @since   v1.01
     */
    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    /**
     * getCollectTime:
     * <p>获取采集时间
     *
     * @return  {@link Date}
     *          - 采集时间
     * @since   v1.01
     */
    public Date getCollectTime() {
        return collectTime;
    }

    /**
     * setCollectTime:
     * <p>设置采集时间
     *
     * @param   collectTime
     *          - 采集时间
     * @since   v1.01
     */
    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

}

