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


import java.util.Properties;
import java.util.logging.LogManager;



/**
 * Modified LogFactory: removed all discovery, hardcode a specific implementation
 * If you like a different logging implementation - use either the discovery-based
 * commons-logging, or better - another implementation hardcoded to your favourite
 * logging impl.
 * 
 * Why ? Each application and deployment can choose a logging implementation - 
 * that involves configuration, installing the logger jar and optional plugins, etc.
 * As part of this process - they can as well install the commons-logging implementation
 * that corresponds to their logger of choice. This completely avoids any discovery
 * problem, while still allowing the user to switch. 
 * 
 * Note that this implementation is not just a wrapper arround JDK logging ( like
 * the original commons-logging impl ). It adds 2 features - a simpler configuration
 * ( which is in fact a subset of log4j.properties ) and a formatter that is 
 * less ugly.   
 * 
 * The removal of 'abstract' preserves binary backward compatibility. It is possible
 * to preserve the abstract - and introduce another ( hardcoded ) factory - but I 
 * see no benefit. 
 * 
 * Since this class is not intended to be extended - and provides
 * no plugin for other LogFactory implementation - all protected methods are removed.
 * This can be changed - but again, there is little value in keeping dead code.
 * Just take a quick look at the removed code ( and it's complexity)  
 * 
 * --------------
 * 
 * Original comment:
 * <p>Factory for creating {@link Log} instances, with discovery and
 * configuration features similar to that employed by standard Java APIs
 * such as JAXP.</p>
 * 
 * <p><strong>IMPLEMENTATION NOTE</strong> - This implementation is heavily
 * based on the SAXParserFactory and DocumentBuilderFactory implementations
 * (corresponding to the JAXP pluggability APIs) found in Apache Xerces.</p>
 * 
 *
 * @author 聂林
 * @version $Revision: 1.0 $ $Date: 2011-03-01 11:39:25 +0100 (Tue, 01 Mar 2011) $
 */
public /* abstract */ class LogFactory {

    // ----------------------------------------------------- Manifest Constants

    /**
     * 该属性用于标识 LogFactory 实现类的名称
     */
    public static final String FACTORY_PROPERTY =
        "org.apache.commons.logging.LogFactory";

    /**
     * 默认的 LogFactory 类的实现类，如果没有找到指定的类，则使用该类。
     */
    public static final String FACTORY_DEFAULT =
        "org.apache.commons.logging.impl.LogFactoryImpl";

    /**
     * 配置文件名称
     */
    public static final String FACTORY_PROPERTIES =
        "commons-logging.properties";
    
    /**
     * <p>Setting this system property value allows the <code>Hashtable</code> used to store
     * classloaders to be substituted by an alternative implementation.
     * </p>
     * <p>
     * <strong>Note:</strong> <code>LogFactory</code> will print:
     * <code><pre>
     * [ERROR] LogFactory: Load of custom hashtable failed</em>
     * </code></pre>
     * to system error and then continue using a standard Hashtable.
     * </p>
     * <p>
     * <strong>Usage:</strong> Set this property when Java is invoked
     * and <code>LogFactory</code> will attempt to load a new instance 
     * of the given implementation class.
     * For example, running the following ant scriplet:
     * <code><pre>
     *  &lt;java classname="${test.runner}" fork="yes" failonerror="${test.failonerror}"&gt;
     *     ...
     *     &lt;sysproperty 
     *        key="org.apache.commons.logging.LogFactory.HashtableImpl"
     *        value="org.apache.commons.logging.AltHashtable"/&gt;
     *  &lt;/java&gt;
     * </pre></code>
     * will mean that <code>LogFactory</code> will load an instance of
     * <code>org.apache.commons.logging.AltHashtable</code>.
     * </p>
     * <p>
     * A typical use case is to allow a custom
     * Hashtable implementation using weak references to be substituted.
     * This will allow classloaders to be garbage collected without
     * the need to release them (on 1.3+ JVMs only, of course ;)
     * </p>
     */
    public static final String HASHTABLE_IMPLEMENTATION_PROPERTY =
        "org.apache.commons.logging.LogFactory.HashtableImpl";
    
    private static LogFactory singleton = new LogFactory();

    Properties logConfig;
    
    // ----------------------------------------------------------- Constructors


    /**
     * 私有的构造方法用于单例模式 (Singleton)
     */
    private LogFactory() {
        logConfig = new Properties();
    }
    
    // hook for syserr logger - class level
    void setLogConfig( Properties p ) {
        this.logConfig=p;
    }
    // --------------------------------------------------------- Public Methods

    // 使用不同的记录器就只需要改变上述两个方法就可以了。
    
    /**
     * <p>使用当前日志工厂的属性配置，来构造(如有需要)并返回一个日志 <code>Log</code>
     * 的实例。</p>
     *
     * <p><strong>注意</strong> - 根据你使用的日志工厂 <code>LogFactory</code>
     * 的实现, 你返回的日志 <code>Log</code> 实例可能是也可能不是在当前应用程序中, 
     * 并且可能会也可能不会在随后再次调用中使用同一个相同名称 <code>name</code> 参数。</p>
     *
     * @param name 需要返回日志 <code>Log</code> 实例的逻辑名称(这就意味着：
     * 该名称被底层日志的实现所识别)，该名称为日志 <code>Log</code> 的完全限定名。
     *
     * @exception LogConfigurationException 如果没有合适的日志 <code>Log</code>
     * 实例返回则抛出该异常
     */
    public Log getInstance(String name) throws LogConfigurationException {
        return DirectJDKLog.getInstance(name);
    }


    /**
     * 释放资源
     * 释放掉前期由这个工厂返回的日志 {@link Log} 所创建的任何内部的引用。
     * 这个在被实现的应用程序丢弃后，又被类加载器 <code>ClassLoader</code> 
     * 重新加载的环境中，是非常有用的，例如在 <code>servlet</code> 容器中。
     */
    public void release() {
        DirectJDKLog.release();
    }

    /**
     * 根据制定的名称 <code>name</code> 来返回配置的属性，如果没有这样的属性
     * 则返回 <code>null</code> .
     *
     * @param name Name of the attribute to return
     */
    public Object getAttribute(String name) {
        return logConfig.get(name);
    }


    /**
     * 返回一个数组，该数组包含所有当前定义配置的属性名称。 
     * 如果没有属性，则返回一个长度为零的数组。
     */
    public String[] getAttributeNames() {
        String result[] = new String[logConfig.size()];
        return logConfig.keySet().toArray(result);
    }

    /**
     * 删除制定名称 <code>name</code> 的配置属性。
     * 如果没有改属性，则不发生所有的操作。
     *
     * @param name 需要删除属性的名称
     */
    public void removeAttribute(String name) {
        logConfig.remove(name);
     }   


    /**
     * 
     * 设置指定名称的配置属性。  调用该方法的值如果为空 <code>null</code>，
     * 则相当于调用 <code>removeAttribute(name)</code> 方法。
     *  
     * @param name 需要设置属性的名称
     * @param value 需要设置属性的值，如果值为空 <code>null</code>
     * 则删除这个属性的任何设置。
     */
    public void setAttribute(String name, Object value) {
        logConfig.put(name, value);
    }


    /**
     * 一个方便的同名方法。通过指定的类 <code>Class</code> 类派生出其名称，并通过
     * 调用 <code>getInstance(String)</code> 方法来得到日志 <code>Log</code>
     * 实例。
     *
     * @param clazz 用于一个合适的获取日志 <code>Log</code> 名称的类
     * <code>Class</code>
     *
     * @exception LogConfigurationException 如果不能返回一个合适的
     * 日志 <code>Log</code> 的实例时抛出
     */
    public Log getInstance(Class clazz) throws LogConfigurationException {
        return getInstance( clazz.getName());
    }


    


    // ------------------------------------------------------- 静态变量



    // --------------------------------------------------------- 静态方法


    /**
     * <p>构造并返回一个日志工厂 <code>LogFactory</code> 的实例，使用查找程序
     * 确定以下实现类的名字来加载。</p>
     * <ul>
     * <li>系统配置文件中的 <code>org.apache.commons.logging.LogFactory</code> </li>
     * <li>JDK 1.3 后支持</li>
     * <li>如果发现配置文件 <code>commons-logging.properties</code> 的路径，则使用
     *	   该配置文件。该配置文件是按照标准的 <code>java.util.Properties</code> 的格
     *     式，并在系统配置中定义了键和其对应实现类的完全限定名。
     * <li>如果没有则使用默认的实现类
     *     (<code>org.apache.commons.logging.impl.LogFactoryImpl</code>).</li>
     * </ul>
     *
     * <p><em>注意</em> - 如果日志工厂 <code>LogFactory</code> 的实现类可以利用定义
     * 配置文件的方法，则所有在属性文件中定义的属性与相应的日志工厂 <code>LogFactory</code> 
     * 实例都应可以设置成配置的属性。</p>
     *
     * @exception LogConfigurationException 如果实现类不可用或者是不能实例化抛出
     */
    public static LogFactory getFactory() throws LogConfigurationException {
        return singleton;
    }


    /**
     * 一个方便的返回日志 <code>Log</code> 的同名方法，该方法可以无需关心
     * 工厂是如何应用的。
     *
     * @param clazz 用于获取日志名称的类 <code>Class</code>
     *
     * @exception LogConfigurationException 如果不能返回一个合适的日志 
     * <code>Log</code> 的实例。
     */
    public static Log getLog(Class clazz) throws LogConfigurationException {
        return (getFactory().getInstance(clazz));
    }


    /**
     * 一个方便的返回日志 <code>Log</code> 的同名方法，该方法可以无需关心
     * 工厂是如何应用的。
     *
     * @param name 需要返回日志 <code>Log</code> 实例的逻辑名称(这就意味着：
     * 该名称被底层日志的实现所识别)，该名称为日志 <code>Log</code> 的完全限定名。
     *
     * @exception LogConfigurationException 如果不能返回一个合适的日志 
     * <code>Log</code> 的实例。
     */
    public static Log getLog(String name) throws LogConfigurationException {
        return (getFactory().getInstance(name));
    }


    /**
     * 释放掉前期由指定类的加载器产生的日志工厂 {@link LogFactory} 所创建的任何内部接口，
     * 并调用每个实例的 <code>release()</code> 方法。
     *
     * @param classLoader 需要释放的日志工厂 <code>LogFactory</code> 的 {@link ClassLoader}
     */
    public static void release(
            @SuppressWarnings("unused") ClassLoader classLoader) {
        // JULI's log manager looks at the current classLoader
        LogManager.getLogManager().reset();
    }


    /**
     * 释放掉前期有日志工厂 {@link LogFactory} 实例来创建的任何内部接口，然后调用每个实例
     * 的 <code>release()</code> 方法。这个在被实现的应用程序丢弃后，
     * 又被类加载器 <code>ClassLoader</code> 重新加载的环境中，是非常有用的，
     * 例如在 <code>servlet</code> 容器中。
     */
    public static void releaseAll() {
        singleton.release();
    }

    /**
     * 返回一个字符串，该字符串是指定对象的唯一标示。
     * 
     * <p>返回的字符串的格式为："classname@hashcode" ，即和 <code>Object.toString()</code>
     * 方法返回的值一样，但是在工程中指定的类甚至可能重写 <code>toString</code> 方法</p>
     * 
     * @param o 指定的对象，可能为空 <code>null<code>.
     * @return string 一个格式为 <code>classname@hashcode</code> 的字符串, 
     * 如果指定的对象为空，则返回空 <code>null<code>.
     */
    public static String objectId(Object o) {
        if (o == null) {
            return "null";
        } else {
            return o.getClass().getName() + "@" + System.identityHashCode(o);
        }
    }
}
