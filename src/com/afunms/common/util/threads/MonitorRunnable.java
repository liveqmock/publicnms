package com.afunms.common.util.threads;

import com.afunms.common.util.logging.Log;
import com.afunms.common.util.logging.LogFactory;

/**
 * 用于监控的线程，定期执行清理线程池的动作
 */
public class MonitorRunnable implements Runnable {
	
	private static Log log = LogFactory.getLog(MonitorRunnable.class);
	
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
	 * 监控执行的间隔
	 */
	protected long interval;
	
	/**
	 * 名称
	 */
	protected String name;

	/**
	 * 构造方法
	 * @param threadPool
	 * 			需要添加监控线程的线程池
	 */
	public MonitorRunnable(ThreadPool threadPool) {
		this.setThreadPool(threadPool);
		if(log.isDebugEnabled()) {
			log.debug("启动线程池监控线程");
		}
		
		this.start();
	}
	
	public void start() {
		this.isTerminate = false;
		this.thread = new Thread(this);
		this.thread.setDaemon(threadPool.isDaemon());
		setName(this.threadPool.getName() + "-Monitor");
		setInterval(threadPool.getWorkWaitTimeout());
		this.thread.start();
	}

	public void run() {
		while (true) {
			try {
				// 等待时间间隔 进行睡眠
				synchronized (this) {
					this.wait(interval);
				}
				
				if(isTerminate){
					break;
				}
				
				// 检查和回收空闲线程
				this.threadPool.checkSpareControllers();
			} catch (InterruptedException e) {
				log.error("线程池监控线程:" + getName() +  " 出现异常！！！ Unexpected exception", e);
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
	 * 中断监控线程
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
	 * @return the interval
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(long interval) {
		this.interval = interval;
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
	
	

}
