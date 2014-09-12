package com.afunms.common.util.threads;


/** 
 * 如果想将程序放入到线程池中运行，则必须实现该接口。
 * 
 * @author 聂林
 * @version 1.0
 */
public abstract class ThreadPoolRunnableAbstract implements ThreadPoolRunnable{
    // XXX use notes or a hashtable-like
    // Important: ThreadData in JDK1.2 is implemented as a Hashtable( Thread -> object ),
    // expensive.
	
	@SuppressWarnings("unused")
	protected ThreadPoolRunnableAttributes threadPoolRunnableAttributes;
    
	/**
	 * @return ThreadPoolRunnableAttributes
	 *			线程的属性
	 */
	public abstract ThreadPoolRunnableAttributes getThreadPoolRunnableAttributes();
	

	/**
     * 设置线程组的属性
	 * @param threadPoolRunnableAttributes the threadPoolRunnableAttributes to set
	 */
	public void setThreadPoolRunnableAttributes(
			ThreadPoolRunnableAttributes threadPoolRunnableAttributes) {
		this.threadPoolRunnableAttributes = threadPoolRunnableAttributes;
	}



	/**
     * 线程池会分配一个线程来执行这个方法。
     * 如果执行完成后，则会将线程归还给线程池。
     */
    public abstract void runIt(ThreadPoolRunnableAttributes threadPoolRunnableAttributes);

}
