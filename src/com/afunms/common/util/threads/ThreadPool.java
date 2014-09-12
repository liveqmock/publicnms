/*
 *  该线程池为采集器提供采集线程的线程池，按照最初的想法，采集的采集线程共用一个线程池。
 *  当采集器启动成时将采集线程加入到线程池中。
 *  如有添加或修改，请在此注明：
 *  
 */

package com.afunms.common.util.threads;

import com.afunms.common.util.logging.Log;
import com.afunms.common.util.logging.LogFactory;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;




/**
 * 
 * 模仿 Tomcat 的线程池
 * 
 * @author <a href=nielin@dhcc.com.cn>聂林</a> 
 * @version 1.0
 */
public class ThreadPool {
	
	private static Log log = LogFactory.getLog(ThreadPool.class);
	
	private static boolean logfull = true;
	
	/**
	 * 	默认的最大线程数  默认值
	 */
	public static final int MAX_THREADS = 200;
	
	/**
	 * 	默认的最小线程数  默认值
	 */
	public static final int MAX_THREADS_MIN = 10;
	
	/**
	 * 	默认的最大的空闲线程数  默认值
	 */
	public static final int MAX_SPARE_THREADS = 50;
	
	/**
	 * 默认的最小的空闲线程数  默认值
	 */
	public static final int MIN_SPARE_THREADS = 5;
	
	/**
	 * 	默认的最大工作等待时间(即回收空闲线程间隔时间)  默认值
	 */
	public static final long WORK_WAIT_TIMEOUT = 60 * 1000;
	
	/**
	 * 	默认的线程池的线程优先级  默认值
	 */
	public static final int THREAD_PRIORITIY = Thread.NORM_PRIORITY;
	
	/**
	 * 	默认的线程池的线程优先级  默认值
	 */
	public static final String THREAD_NAME = "TP";


	/**
	 * 	线程池的最大线程数
	 */
	protected int maxThreads;

	/**
	 * 	线程池最小空闲线程数
	 */
	protected int minSpareThreads;

	/**
	 * 	线程池最大空闲线程数
	 */
	protected int maxSpareThreads;

	/**
	 * 当前线程池当中的线程数
	 */
	protected int currentThreadCount;

	/**
	 * 	当前工作的线程数
	 */
	protected int currentThreadsBusy;

	/**
	 * 	是否暂停线程池(即暂停线程池当中的所有线程数)
	 */
	protected boolean stopThePool;

	/* Flag to control if the main thread is 'daemon' */
	protected boolean isDaemon = true;

	/**
	 * 	线程池名称
	 */
	protected String name;
	
	/**
	 * 线程值的属性
	 */
	protected Properties properties = null;
	
	/**
	 * 所有执行线程数组
	 */
	protected ControlRunnable[] pool = null;
	
	/**
	 * 用于监控和清理的线程
	 */
	protected MonitorRunnable monitorRunnable = null;
	
	/**
	 * 用于将缓冲区的 <code>ThreadPoolRunnable</code>
	 * 加入到线程池中运行
	 */
	protected BufferRunnable bufferRunnable = null;
	
	/**
	 * <code>ThreadPoolRunnable</code> 缓冲区
	 */
	protected Vector<ThreadPoolRunnable> threadPoolBuffer = new Vector<ThreadPoolRunnable>();
	
	
	/**
	 * <p>用于存放打开的线程。</p>
	 * <p>键是：<code>Thread</code> ，值是：<code>ControlRunnable</code></p>
	 */
	protected Hashtable<Thread, ControlRunnable> threads = new Hashtable<Thread, ControlRunnable>();
	
	/**
	 * 线程池监听集合
	 */
	protected Vector<ThreadPoolListener> listeners = new Vector<ThreadPoolListener>();

	/**
	 * 
	 */
	protected int sequence = 1;

	/**
	 *	线程优先级
	 */
	protected int threadPriority;
	
	/**
	 * 工作等待的超时时间，即：空闲线程回收的等待时间
	 */
	protected long workWaitTimeout;
	
	/**
	 * 使用默认的参数来构造一个线程池
	 */
	public ThreadPool() {
		initialize();
	}
	
	/**
	 * 使用默认的参数来构造一个线程池
	 */
	public ThreadPool(Properties properties) {
		setProperties(properties);
		initialize();
	}
	
	/**
	 * <p>创建一个线程池实例。</p>
	 * <p>因参数 <code>jmx</code> 暂未使用，该方法等同于返回
	 * 一个默认属性的线程池。</p>
	 * @param jmx
	 *            暂未使用(UNUSED)
	 * @return 线程池<code>ThreadPool</code> 的实例. If JMX support is requested, you need to
	 *         call register() in order to set a name.
	 */
	public static ThreadPool createThreadPool(boolean jmx) {
		return new ThreadPool();
	}
	
	protected void initialize(){
		if(properties == null){
			if(log.isDebugEnabled()){
				log.debug("线程池属性集为空，使用默认设置初始化");
			}
			properties = new Properties();
		}
		currentThreadCount = 0;
		currentThreadsBusy = 0;
		stopThePool = false;
		
		name = THREAD_NAME;
		maxThreads = MAX_THREADS;
		maxSpareThreads = MAX_SPARE_THREADS;
		minSpareThreads = MIN_SPARE_THREADS;
		threadPriority = THREAD_PRIORITIY;
		workWaitTimeout = WORK_WAIT_TIMEOUT;
		
		String _name = properties.getProperty(ThreadPoolProperitesConstant.MAX_THREADS);
		if(_name != null && _name.trim().length() > 0) {
			name = _name.trim();
		} else {
			if(log.isDebugEnabled()){
				log.debug("线程池名称未设置或者是设置出错，使用默认设置：" + name);
			}
		}
		
		Pattern pattern = Pattern.compile("[0-9]+");
		
		String _maxThreads = properties.getProperty(ThreadPoolProperitesConstant.MAX_THREADS);
		if(_maxThreads != null && pattern.matcher(_maxThreads).matches()) {
			if(Integer.valueOf(_maxThreads) >= MAX_THREADS_MIN) {
				maxThreads = Integer.valueOf(_maxThreads);
			} else {
				if(log.isDebugEnabled()){
					log.debug("线程池最大线程数不能小于:" + maxThreads + "，使用默认设置：" + maxThreads);
				}
			}
		} else {
			if(log.isDebugEnabled()){
				log.debug("线程池最大线程数没有设置或者是设置出错，使用默认设置：" + maxThreads);
			}
		}
		
		String _maxSpareThreads = properties.getProperty(ThreadPoolProperitesConstant.MAX_SPARE_THREADS);
		if(_maxSpareThreads != null && pattern.matcher(_maxSpareThreads).matches()) {
			maxSpareThreads = Integer.valueOf(_maxSpareThreads);
		} else {
			if(log.isDebugEnabled()){
				log.debug("线程池最大空闲数没有设置或者是设置出错，使用默认设置：" + maxSpareThreads);
			}
		}
		
		String _minSpareThreads = properties.getProperty(ThreadPoolProperitesConstant.MIN_SPARE_THREADS);
		if(_minSpareThreads != null && pattern.matcher(_minSpareThreads).matches()) {
			minSpareThreads = Integer.valueOf(_minSpareThreads);
		} else {
			if(log.isDebugEnabled()){
				log.debug("线程池最小空闲数没有设置或者是设置出错，使用默认设置：" + minSpareThreads);
			}
		}
		
		String _threadPriority = properties.getProperty(ThreadPoolProperitesConstant.THREAD_PRIORITIY);
		if(_threadPriority != null && pattern.matcher(_threadPriority).matches()) {
			if(Integer.valueOf(_threadPriority) >= Thread.MIN_PRIORITY && Integer.valueOf(_threadPriority) <= Thread.MAX_PRIORITY) {
				threadPriority = Integer.valueOf(_threadPriority);
			} else {
				if(log.isDebugEnabled()){
					log.debug("线程池优先级设定不在1~10范围内，使用默认设置：" + threadPriority);
				}
			}
		}
		
		String _workWaitTimeout = properties.getProperty(ThreadPoolProperitesConstant.WORK_WAIT_TIMEOUT);
		if(_workWaitTimeout != null && pattern.matcher(_workWaitTimeout).matches()) {
			workWaitTimeout = Long.valueOf(_workWaitTimeout);
		} else {
			if(log.isDebugEnabled()){
				log.debug("线程池中线程的工作等待时间（即：空闲线程被回收的时间间隔）没有设置或者是设置出错，使用默认设置（单位毫秒）：" + workWaitTimeout);
			}
		}
	}
	
	/**
	 * <p>启动线程池</p>
	 * <p>线程池</p>
	 * @return
	 */
	public synchronized boolean start() {
		stopThePool = false;
		currentThreadCount = 0;
		currentThreadsBusy = 0;

		adjustLimits();

		pool = new ControlRunnable[maxThreads];

		openThreads(minSpareThreads);
		if (maxSpareThreads < maxThreads) {
			monitorRunnable = new MonitorRunnable(this);
			bufferRunnable = new BufferRunnable(this);
		}
		
		return false;
	}
	
	/**
	 * 创建未启动的线程
	 * 
	 * @param toOpen
	 *            需要打开的线程数
	 */
	protected void openThreads(int toOpen) {

		if (toOpen > maxThreads) {
			toOpen = maxThreads;
		}

		for (int i = currentThreadCount; i < toOpen; i++) {
			pool[i - currentThreadsBusy] = new ControlRunnable(this);
		}

		currentThreadCount = toOpen;
	}
	
	/**
	 * 由监控线程来检查和回收空闲线程
	 */
	protected synchronized void checkSpareControllers() {

		if (stopThePool) {
			return;
		}

		if ((currentThreadCount - currentThreadsBusy) > maxSpareThreads) {
			int toFree = currentThreadCount - currentThreadsBusy
					- maxSpareThreads;

			for (int i = 0; i < toFree; i++) {
				ControlRunnable controlRunnable = pool[currentThreadCount
						- currentThreadsBusy - 1];
				controlRunnable.terminate();
				pool[currentThreadCount - currentThreadsBusy - 1] = null;
				currentThreadCount--;
			}

		}

	}
	
	/**
	 * 检查配置问题，并解决。此修复程序提供了默认的设置
	 */
	protected void adjustLimits() {
		if (maxThreads <= 0) {
			maxThreads = MAX_THREADS;
		} else if (maxThreads < MAX_THREADS_MIN) {
			maxThreads = MAX_THREADS_MIN;
			log.warn("线程池最大线程数不能小于:" + maxThreads + "，当前设置：" + maxThreads);
		}

		if (maxSpareThreads >= maxThreads) {
			maxSpareThreads = maxThreads;
			log.warn("线程池最大空闲线程数不能大于最大线程数，当前设置：" + maxSpareThreads);
		}

		if (maxSpareThreads <= 0) {
			if (1 == maxThreads) {
				maxSpareThreads = 1;
			} else {
				maxSpareThreads = maxThreads / 2;
			}
			log.warn("线程池最大空闲线程数不能小于0，当前设置：" + maxSpareThreads);
		}

		if (minSpareThreads > maxSpareThreads) {
			minSpareThreads = maxSpareThreads;
			log.warn("线程池最小空闲线程数不能大于最大空闲线程数，当前设置：" + minSpareThreads);
		}

		if (minSpareThreads <= 0) {
			if (1 == maxSpareThreads) {
				minSpareThreads = 1;
			} else {
				minSpareThreads = maxSpareThreads / 2;
			}
			log.warn("线程池最小空闲线程数不能小于0，当前设置：" + maxSpareThreads);
		}
	}

	
	/**
	 * 停止线程池
	 */
	public synchronized void shutdown() {
		if (!stopThePool) {
			stopThePool = true;
			if (monitorRunnable != null) {
				// 停止监控线程
				monitorRunnable.terminate();
				monitorRunnable = null;
			}
			for (int i = 0; i < currentThreadCount - currentThreadsBusy; i++) {
				try {
					pool[i].terminate();
				} catch (Throwable t) {
					// 不做任何事...继续运行，停止线程池，并不会停止其他的运行
					log.error("Ignored exception while shutting down thread pool",
									t);
				}
			}
			currentThreadsBusy = currentThreadCount = 0;
			pool = null;
			notifyAll();
		}
	}
	
	/**
	 * 向线程池的缓冲区中添加一个需要运行的 <code>ThreadPoolRunnable</code> ,
	 * 一旦加入到缓存区中，如果线程池已经启动，则会加入到线程池中自动运行。
	 * 如果线程池的所有线程都在忙，则会一直等待一个空闲线程来运行。
	 * @param r
	 * 			需要运行的 ThreadPoolRunnable
	 */
	public void add(ThreadPoolRunnable r) {
		if (null == r) {
			throw new NullPointerException();
		}
		
		threadPoolBuffer.add(r);
		
		bufferRunnable.runIt();
	}
	
	/**
	 * 运行给定的 ThreadPoolRunnable
	 * @param r
	 * 			给定的 ThreadPoolRunnable
	 */
	public void runIt(ThreadPoolRunnable r) {
		if (null == r) {
			throw new NullPointerException();
		}

		ControlRunnable c = findControlRunnable();
		c.runIt(r);
	}
	
	
	private ControlRunnable findControlRunnable() {
		ControlRunnable c = null;

		if (stopThePool) {
			throw new IllegalStateException();
		}

		// 从线程池中获取一个空线程
		synchronized (this) {

			while (currentThreadsBusy == currentThreadCount) {
				// 所有线程都在忙
				if (currentThreadCount < maxThreads) {
					// 如果所有的线程都是打开的，则打开一组新的空线程线程，该线程数位最大空闲数
					int toOpen = currentThreadCount + minSpareThreads;
					openThreads(toOpen);
				} else {
					logFull(log, currentThreadCount, maxThreads);
					// 等待线程到达空闲
					try {
						this.wait();
					}
					// 这里仅仅是用于捕获 Throwable; 因为不可能会由 wait() 抛出其他异常;
					// 因此我这里只是用于捕获一下异常; 因为没有在任何地方使用线程中断，所以
					// 这个异常永远不会发生。
					catch (InterruptedException e) {
						log.error("等待空闲线程出现异常！！！Unexpected exception", e);
					}
					if (log.isDebugEnabled()) {
						log.debug("Finished waiting: CTC=" + currentThreadCount
								+ ", CTB=" + currentThreadsBusy);
					}
					// 如果停止线程池，则退出
					if (stopThePool) {
						break;
					}
				}
			}
			// 线程池已经停止，抛出异常
			if (0 == currentThreadCount || stopThePool) {
				throw new IllegalStateException();
			}

			// 如果到达这里，则说明有一个空闲线程，则拿去使用。
			int pos = currentThreadCount - currentThreadsBusy - 1;
			c = pool[pos];
			pool[pos] = null;
			currentThreadsBusy++;

		}
		return c;
	}
	
	/**
	 * 
	 * 通知线程池，指定的线程已经完成。
	 * 
	 * 当 ControlRunnable.run() 执行中抛出异常时来调用。
	 * 
	 */
	protected synchronized void notifyThreadEnd(ControlRunnable c) {
		currentThreadsBusy--;
		currentThreadCount--;
		notify();
	}
	
	/**
	 * 将线程返回到线程中。
	 * 由于这个线程返变成空闲线程，则由线程池来分配调用。
	 */
	protected synchronized void returnController(ControlRunnable c) {

		if (0 == currentThreadCount || stopThePool) {
			c.terminate();
			return;
		}

		// atomic
		currentThreadsBusy--;

		pool[currentThreadCount - currentThreadsBusy - 1] = c;
		notify();
	}
	
	/**
	 * 向线程中添加一个线程
	 * @param thread 需要添加的线程
	 * @param cr     线程所属的控制线程
	 */
	public void addThread(Thread thread, ControlRunnable cr) {
//		threads.put(t, cr);
		for (int i = 0; i < listeners.size(); i++) {
			ThreadPoolListener threadPoolListener = (ThreadPoolListener) listeners
						.elementAt(i);
			threadPoolListener.threadEnd(this, thread);
		}
	}
	
	/**
	 * 移除指定的线程
	 * @param thread
	 * 			需要移除的线程
	 */
	public void removeThread(Thread thread) {
//		threads.remove(t);
		for (int i = 0; i < listeners.size(); i++) {
			ThreadPoolListener threadPoolListener = (ThreadPoolListener) listeners
					.elementAt(i);
			threadPoolListener.threadEnd(this, thread);
		}
	}
	
	private static void logFull(Log loghelper, int currentThreadCount,
			int maxThreads) {
		if (logfull) {
//			log.error(sm.getString("threadpool.busy", new Integer(
//					currentThreadCount), new Integer(maxThreads)));
//			logfull = false;
		} else if (log.isDebugEnabled()) {
			log.debug("All threads are busy " + currentThreadCount + " "
					+ maxThreads);
		}
	}


	// --------------------------------------------------------- 获取和设置属性方法
	

	/**
	 * 返回线程池的属性集
	 * @return the properties 线程池的属性集
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * 设置线程池的属性集
	 * @param properties 线程池的属性集
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}


	
	
	/**
	 * 获取线程池名称。
	 * @return 返回线程池的名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置线程池名称。
	 * @param name
	 * 			线程池名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取线程池里线程设置默认的优先级。
	 * @return 线程池给线程默认的优先级
	 */
	public int getThreadPriority() {
		return threadPriority;
	}

	/**
	 * 设置线程池给线程设置默认的优先级。
	 * @param threadPriority
	 * 			线程池给线程默认设置的优先级
	 */
	public void setThreadPriority(int threadPriority) {
		if (log.isDebugEnabled())
			log.debug(getClass().getName() + ": setPriority(" + threadPriority
					+ "): here.");

		if (threadPriority < Thread.MIN_PRIORITY) {
			throw new IllegalArgumentException("new priority < MIN_PRIORITY");
		} else if (threadPriority > Thread.MAX_PRIORITY) {
			throw new IllegalArgumentException("new priority > MAX_PRIORITY");
		}

		this.threadPriority = threadPriority;

		Enumeration<Thread> currentThreads = getThreads();
		Thread t = null;
		while (currentThreads.hasMoreElements()) {
			// 设置当前所有线程的优先级
			t = (Thread) currentThreads.nextElement();
			t.setPriority(threadPriority);
		}
	}
	
	
	/**
	 * 设置最大线程数。
	 * @param maxThreads
	 * 			最大线程数
	 */
	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	/**
	 * 返回最大线程数。
	 * @return 最大线程数
	 */
	public int getMaxThreads() {
		return maxThreads;
	}

	/**
	 * 设置最小空闲线程数
	 * @param minSpareThreads
	 * 			最小空闲线程数
	 */
	public void setMinSpareThreads(int minSpareThreads) {
		this.minSpareThreads = minSpareThreads;
	}

	/**
	 * 返回最小空闲线程数。
	 * @return 最小空闲线程数
	 */
	public int getMinSpareThreads() {
		return minSpareThreads;
	}

	/**
	 * 设置最大空闲线程数。
	 * @param maxSpareThreads
	 * 			最大空闲线程数
	 */
	public void setMaxSpareThreads(int maxSpareThreads) {
		this.maxSpareThreads = maxSpareThreads;
	}

	/**
	 * 返回最大空闲线程数。
	 * @return 最大空闲线程数
	 */
	public int getMaxSpareThreads() {
		return maxSpareThreads;
	}
	
	/**
	 * 返回线程池中缓冲区中需要执行的 <code>ThreadPoolRunnable</code> 的列表
	 * @return the threadPoolBuffer
	 * 			缓冲区中需要执行的 <code>ThreadPoolRunnable</code> 的列表
	 */
	public Vector<ThreadPoolRunnable> getThreadPoolBuffer() {
		return threadPoolBuffer;
	}

	/**
	 * 设置线程池缓冲区中需要执行的 <code>ThreadPoolRunnable</code> 的列表
	 * @param threadPoolBuffer the threadPoolBuffer to set
	 * 		需要执行的 <code>ThreadPoolRunnable</code> 的列表
	 */
	public void setThreadPoolBuffer(Vector<ThreadPoolRunnable> threadPoolBuffer) {
		this.threadPoolBuffer = threadPoolBuffer;
	}
	
	/**
	 * 返回工作等待的超时时间，即：空闲线程回收的等待时间。单位：毫秒
	 * @return the workWaitTimeout
	 * 			工作等待的超时时间，即：空闲线程回收的等待时间。单位：毫秒
	 */
	public long getWorkWaitTimeout() {
		return workWaitTimeout;
	}

	/**
	 * 设置工作等待的超时时间，即：空闲线程回收的等待时间。单位：毫秒
	 * @param workWaitTimeout the workWaitTimeout to set
	 * 			需要设置的工作等待的超时时间，即：空闲线程回收的等待时间。单位：毫秒
	 */
	public void setWorkWaitTimeout(long workWaitTimeout) {
		this.workWaitTimeout = workWaitTimeout;
	}
	
	
	// --------------------------------------------------------- 获取只读属性的方法
	
	/**
	 * 
	 */
	public Enumeration<Thread> getThreads() {
		return threads.keys();
	}

	/**
	 * 获取当前线程数。
	 * @return 当前线程数
	 */
	public int getCurrentThreadCount() {
		return currentThreadCount;
	}

	/**
	 * 获取当前繁忙(工作)的线程数
	 * @return 当前繁忙(工作)的线程数
	 */
	public int getCurrentThreadsBusy() {
		return currentThreadsBusy;
	}

	/**
	 * 获取是否为守护线程。
	 * @return 是否为守护线程
	 */
	public boolean isDaemon() {
		return isDaemon;
	}

	public static int getDebug() {
		return 0;
	}
	
}
