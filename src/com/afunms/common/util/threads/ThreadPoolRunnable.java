package com.afunms.common.util.threads;



/** 
 * 如果想将程序放入到线程池中运行，则必须实现该接口。
 * 
 * @author 聂林
 * @version 1.0
 */
public interface ThreadPoolRunnable {
    // XXX use notes or a hashtable-like
    // Important: ThreadData in JDK1.2 is implemented as a Hashtable( Thread -> object ),
    // expensive.
    
	/**
	 * @return ThreadPoolRunnableAttributes
	 *			线程的属性
	 */
	public ThreadPoolRunnableAttributes getThreadPoolRunnableAttributes();

    /**
     * 线程池会分配一个线程来执行这个方法。
     * 如果执行完成后，则会将线程归还给线程池。
     */
    public void runIt(ThreadPoolRunnableAttributes threadPoolRunnableAttributes);

}
