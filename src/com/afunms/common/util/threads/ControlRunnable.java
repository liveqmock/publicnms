package com.afunms.common.util.threads;

import com.afunms.common.util.logging.Log;
import com.afunms.common.util.logging.LogFactory;


/**
 * 由线程池控制和用于执行各种操作(即 {@link ThreadPoolRunnable} )
 * 的一个线程对象。 
 * 
 * @author 聂林
 * @version 1.0 $Date: 2011-03-07 16:34:30 +0100 (Mon, Mar 7, 2011) $
 */
public class ControlRunnable implements Runnable {
	
	private Log log = LogFactory.getLog(ControlRunnable.class);
	
	/**
	 * 所属的线程组
	 */
	protected ThreadPool threadPool;
	
	/**
	 * 是否中断
	 */
	protected boolean shouldTerminate; 
	
	/**
	 * 用于激活执行线程
	 */
	protected boolean shouldRun;
	
	/**
	 * 该线程执行的方法：
	 */
	
	/**
	 * 1. Runnable 形式
	 */
	protected Runnable toRunRunnable;
	
	/**
	 * 2. ThreadPoolRunnable 的形式
	 */
	protected ThreadPoolRunnable toRun;
	
	/**
	 * 用于执行的线程
	 */
	protected Thread thread;
	
	/**
	 * 该线程的属性
	 */
	protected ThreadPoolRunnableAttributes runnableAttributes;


	/**
	 * 创建一个新线程
	 */
	protected ControlRunnable(ThreadPool threadPool) {
		this.threadPool = threadPool;
		toRun = null;
		shouldTerminate = false;
		shouldRun = false;
		thread = new Thread(this);
		thread.setDaemon(this.threadPool.isDaemon());
		//thread.setName(this.threadPool.getName() + "-Processor" + this.threadPool.incSequence());
		thread.setPriority(threadPool.getThreadPriority());
		this.threadPool.addThread(thread, this);
		thread.start();
	}

	public void run() {

		boolean _shouldRun = false;
		boolean _shouldTerminate = false;
		ThreadPoolRunnable _toRun = null;
		try {
			while (true) {
				try {
					/* Wait for work. */
					synchronized (this) {
						while (!shouldRun && !shouldTerminate) {
							this.wait();
						}
						_shouldRun = shouldRun;
						_shouldTerminate = shouldTerminate;
						_toRun = toRun;
					}

					if (_shouldTerminate) {
						if (log.isDebugEnabled())
							log.debug("Terminate");
						break;
					}

					/* 检查是否可以执行一个线程 */
					try {
						if (_toRun != null) {
							runnableAttributes = _toRun.getThreadPoolRunnableAttributes();
//							if (log.isDebugEnabled())
//								log.debug("Getting thread data");
						}
						if (_shouldRun) {
							if (_toRun != null) {
								_toRun.runIt(runnableAttributes);
							} else if (toRunRunnable != null) {
								toRunRunnable.run();
							} else {
								if (log.isDebugEnabled())
									log.debug("No toRun ???");
							}
						}
					} catch (Throwable t) {
//						log.error(sm.getString(
//								"threadpool.thread_error", t, toRun
//										.toString()));
						/*
						 * 
						 * 如果这个 runnable 抛出异常 (甚至可能是一个死线程)，这说明
						 * 这个线程已经死亡。
						 * 
						 * 也就意味着我们需要从线程池中释放这个线程
						 * 
						 */
						_shouldTerminate = true;
						_shouldRun = false;
						threadPool.notifyThreadEnd(this);
					} finally {
						if (_shouldRun) {
							shouldRun = false;
							/*
							 * 通知线程池，该线程现在处于空闲状态
							 */
							threadPool.returnController(this);
						}
					}

					/*
					 * 检查是否终止
					 */
					if (_shouldTerminate) {
						break;
					}
				} catch (InterruptedException ie) { 
					/*
					 * 为了等待一直能够持续下去，我们不会调用中断，
					 * 所以这个不会发生
					 */
					log.error("Unexpected exception", ie);
				}
			}
		} finally {
			threadPool.removeThread(Thread.currentThread());
		}
	}
	
	/**
	 * Run a task
	 * 
	 * @param toRun
	 */
	public synchronized void runIt(Runnable toRun) {
		this.toRunRunnable = toRun;
		// Do not re-init, the whole idea is to run init only once per
		// thread - the pool is supposed to run a single task, that is
		// initialized once.
		// noThData = true;
		//shouldRun = true;
		this.notify();
	}
	
	/**
	 * Run a task
	 * 
	 * @param toRun
	 */
	public synchronized void runIt(ThreadPoolRunnable toRun) {
		 this.toRun = toRun;
		// Do not re-init, the whole idea is to run init only once per
		// thread - the pool is supposed to run a single task, that is
		// initialized once.
		// noThData = true;
		shouldRun = true;
		this.notify();
	}
	
	public synchronized void terminate() {
		shouldTerminate = true;
		this.notify();
	}
}
