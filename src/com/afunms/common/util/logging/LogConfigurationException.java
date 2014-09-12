package com.afunms.common.util.logging;


/**
 * 当无法通过日志工厂 <code>LogFactory</code> 的相应的工厂方法来创建一个日志 
 * <code>Log</code> 的实例时，抛出该异常</p>
 *
 * @author 聂林
 * @version $Revision: 1.0 $ $Date: 2011-03-02 09:11:11 +0200 (Wed, 02 Mar 2011) $
 */

public class LogConfigurationException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6976186569111431357L;


	/**
     * 构造方法
     * 构造一个详细信息为 <code>null</code> 的异常
     */
    public LogConfigurationException() {
        super();
    }


    /**
     * 构造方法
     * 构造一个指定详细信息为 <code>String</code> 类型的异常
     *
     * @param message The detail message
     */
    public LogConfigurationException(String message) {
        super(message);
    }


    /**
     * 构造方法
     * 通过指定指定错误为 <code>Throwable</code> 类型的 
     * <code>cause</code> 并通过器派生出来的详细信息来构造一个 
     * <code>LogConfigurationException</code> 异常
     *
     * @param cause The underlying cause
     */
    public LogConfigurationException(Throwable cause) {
        this( ((cause == null) ? null : cause.toString()), cause);
    }


    /**
     * 构造方法
     * 通过指定详细信息为 <code>String</code> 类型的 <code>message</code> 
     * 和指定错误为 <code>Throwable</code> 类型的 <code>cause</code>
     * 来构造一个 <code>LogConfigurationException</code> 异常
     *
     * @param message The detail message
     * @param cause The underlying cause
     */
    public LogConfigurationException(String message, Throwable cause) {

        super(message);
        this.cause = cause; // Two-argument version requires JDK 1.4 or later

    }


    /**
     * 异常的底层原因 {@link Throwable}
     */
    protected Throwable cause = null;


    /**
     * 返回这个异常的底层原因 {@link Throwable}
     */
    public Throwable getCause() {

        return (this.cause);

    }


}
