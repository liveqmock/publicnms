package com.afunms.common.util.threads;


/**
 * <p>线程池的监听器。</p>
 * 
 * <p>用于监听线程池创建线程和停止线程。
 * @author 聂林
 * @version 1.0 $Date: 2011-03-13 00:41:40 +0100 (Sun, Mar 13, 2011) $
 *
 */
public interface ThreadPoolListener {
	/**
	 * Interface to allow applications to be notified when a threads are created
	 * and stopped.
	 */
	public void threadStart(ThreadPool threadPool, Thread thread);

	public void threadEnd(ThreadPool threadPool, Thread thread);
}
