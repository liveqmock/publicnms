package com.afunms.common.util.threads;

import java.util.Vector;

import com.afunms.common.util.logging.Log;
import com.afunms.common.util.logging.LogFactory;



/**
 * 用于处理线程池缓冲区中需要执行的操作，所有需要处理的操作先加入到
 * 缓冲区中，然后由该线程每次将缓冲区的操作加入到线程池中运行。
 * 
 */
public class BufferRunnable implements Runnable {
	
	private static Log log = LogFactory.getLog(BufferRunnable.class);
	
	/**
	 * 所属的线程组
	 */
	protected ThreadPool threadPool;
	
	/**
	 * 所属线程
	 */
	protected Thread thread;
	
	/**
	 * 是否中断
	 */
	protected boolean isTerminate; 
	
	/**
	 * 名称
	 */
	protected String name;
	
	/**
	 * <code>ThreadPoolRunnable</code> 缓冲区
	 */
	protected Vector<ThreadPoolRunnable> threadPoolRunnableBuffer = null;
	
	/**
	 * 当前执行的 <code>ThreadPoolRunnable</code> 
	 */
	protected ThreadPoolRunnable threadPoolRunnable = null;

	/**
	 * 构造方法
	 * @param threadPool
	 * 			需要添加缓冲线程的线程池
	 */
	public BufferRunnable(ThreadPool threadPool) {
		this.setThreadPool(threadPool);
		this.start();
	}
	
	public void start() {
		this.isTerminate = false;
		this.thread = new Thread(this);
		this.thread.setDaemon(threadPool.isDaemon());
		setName(this.threadPool.getName() + "-Buffer");
		setThreadPoolRunnableBuffer(threadPool.threadPoolBuffer);
		if(log.isDebugEnabled()) {
			log.debug("启动线程池缓冲区线程");
		}
		this.thread.start();
	}

	public void run() {
		while (true) {
			try {
				// 如果缓冲区内没有任何需要执行的 ThreadPoolRunnable 
				synchronized (this) {
					while (threadPoolRunnableBuffer == null || threadPoolRunnableBuffer.size() == 0) {
						this.wait();
						if(isTerminate){
							threadPoolRunnableBuffer = null;
						}
					}
					if(isTerminate){
						break;
					}
					threadPoolRunnable = threadPoolRunnableBuffer.remove(0);
				}
				if(log.isDebugEnabled()) {
					log.debug("从缓冲区中获取一个需要执行的 ThreadPoolRunnable");
				}
				// 执行
				this.threadPool.runIt(threadPoolRunnable);
			} catch (InterruptedException e) {
				log.error("线程池缓冲线程:" + getName() +  " 出现异常！！！ Unexpected exception", e);
			}
		}
	}
	
	/**
	 * 停止监控线程，与 <code>terminate()</code> 效果一样
	 * @see #terminate()
	 */
	public void stop() {
		this.terminate();
	}
	
	/**
	 * 执行缓冲线程
	 */
	public synchronized void runIt() {
		this.isTerminate = false;
		this.notify();
	}

	/**
	 * 中断缓冲线程
	 */
	public synchronized void terminate() {
		this.isTerminate = true;
		this.notify();
	}

	/**
	 * @return the threadPool
	 */
	public ThreadPool getThreadPool() {
		return threadPool;
	}

	/**
	 * @param threadPool the threadPool to set
	 */
	public void setThreadPool(ThreadPool threadPool) {
		this.threadPool = threadPool;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		name = thread.getName();
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
		this.thread.setName(name);
	}

	/**
	 * @return the threadPoolRunnableBuffer
	 */
	public Vector<ThreadPoolRunnable> getThreadPoolRunnableBuffer() {
		return threadPoolRunnableBuffer;
	}

	/**
	 * @param threadPoolRunnableBuffer the threadPoolRunnableBuffer to set
	 */
	public void setThreadPoolRunnableBuffer(
			Vector<ThreadPoolRunnable> threadPoolRunnableBuffer) {
		this.threadPoolRunnableBuffer = threadPoolRunnableBuffer;
	}

	

}
