/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package com.afunms.common.util.logging;

/**
 * <p>一个简单的日志记录 API 的抽象接口.  为了能够让日志工厂 {@ LogFactory }
 * 来成功实例化日志, 则该类必须实现该接口，并且必须有带一个字符串 {@ String} 
 * 作为参数的构造方法，该字符串用于给这个日志的实例命名。</p>
 *
 * <p> 以下为 <code>Log</code> 的六个等级:
 * <ol>
 * <li>追踪 trace (the least serious)</li>
 * <li>调试 debug</li>
 * <li>信息 info</li>
 * <li>警告 warn</li>
 * <li>错误 error</li>
 * <li>致命 fatal (the most serious)</li>
 * </ol>
 * 这些日志等级的概念是否对应，需要依赖于底层日志系统的实现。
 * 虽然预期是这样排序的，但是还是需要实现来保证.</p>
 *
 * <p>性能往往是记录比较关注的.
 * 通过检查适当的属性, 一个组件往往能避免一些昂贵的操作(如果记录生产信息的话).</p>
 *
 * <p> 例如,
 * <code><pre>
 *    if (log.isDebugEnabled()) {
 *        ... do something expensive ...
 *        log.debug(theResult);
 *    }
 * </pre></code>
 * </p>
 *
 * <p>底层的日志系统一般都会做外部的日志 API 来进行配置, 然后通过某种机制来支持该系统</p>
 *
 * @author <a href="mailto:nielin@dhcc.com.cn">聂林</a>
 * @version $Revision: 1.0 $ $Date: 2011-03-01 15:48:25 +0100 (Tue, 01 Mar 2011) $
 */
public interface Log {


    // ----------------------------------------------------- Logging Properties


    /**
     * <p> 当前调试日志是否启用</p>
     *
     * <p> 当日志的级别超过调试级别，调用该方法可以减少一些不必要的操作。 </p>
     */
    public boolean isDebugEnabled();


    /**
     * <p> 当前错误日志是否启用</p>
     *
     * <p> 当日志的级别超过错误级别，调用该方法可以减少一些不必要的操作。 </p>
     */
    public boolean isErrorEnabled();


    /**
     * <p> 当前致命日志是否启用</p>
     *
     * <p> 当日志的级别超过致命级别，调用该方法可以减少一些不必要的操作。 </p>
     */
    public boolean isFatalEnabled();


    /**
     * <p> 当前信息日志是否启用</p>
     *
     * <p> 当日志的级别超过信息级别，调用该方法可以减少一些不必要的操作。 </p>
     */
    public boolean isInfoEnabled();


    /**
     * <p> 当前追踪日志是否启用</p>
     *
     * <p> 当日志的级别超过追踪级别，调用该方法可以减少一些不必要的操作。 </p>
     */
    public boolean isTraceEnabled();


    /**
     * <p> 当前警告日志是否启用</p>
     *
     * <p> 当日志的级别超过警告级别，调用该方法可以减少一些不必要的操作。 </p>
     */
    public boolean isWarnEnabled();


    // -------------------------------------------------------- Logging Methods


    /**
     * <p> 用日志的追踪等级来记录信息 </p>
     *
     * @param message log this message
     */
    public void trace(Object message);


    /**
     * <p> 用日志的追踪等级来记录错误 </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void trace(Object message, Throwable t);


    /**
     * <p> 用日志的调试等级来记录信息 </p>
     *
     * @param message log this message
     */
    public void debug(Object message);


    /**
     * <p> 用日志的调试等级来记录错误 </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void debug(Object message, Throwable t);


    /**
     * <p> 用日志的信息等级来记录信息 </p>
     *
     * @param message log this message
     */
    public void info(Object message);


    /**
     * <p> 用日志的信息等级来记录错误 </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void info(Object message, Throwable t);


    /**
     * <p> 用日志的告警等级来记录信息 </p>
     *
     * @param message log this message
     */
    public void warn(Object message);


    /**
     * <p> 用日志的告警等级来记录错误 </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void warn(Object message, Throwable t);


    /**
     * <p> 用日志的错误等级来记录信息 </p>
     *
     * @param message log this message
     */
    public void error(Object message);


    /**
     * <p> 用日志的错误等级来记录错误 </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void error(Object message, Throwable t);


    /**
     * <p> 用日志的致命等级来记录信息 </p>
     *
     * @param message log this message
     */
    public void fatal(Object message);


    /**
     * <p> 用日志的致命等级来记录错误 </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void fatal(Object message, Throwable t);


}
