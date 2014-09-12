package com.afunms.initialize;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import com.afunms.common.util.SysLogger;
import com.afunms.polling.task.MonitorTask;
import com.afunms.polling.task.MonitorTimer;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Jul 14, 2011 7:15:43 PM
 * 类说明
 */
public class TimerListener extends Thread{
	
	private MonitorTimer timer;//监听的timer
	
	private TimerTask task;//监听调度的task
	
	private long delay; //延迟时间  
	
	private long period;//调度task的时间间隔
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	 public TimerListener() {
		super();
	}

	 /**
	 * @param task      该timer调度的task任务
	 * @param delay     启动task时的延迟时间
	 * @param period    Task启动的间隔时间 单位：毫秒 
	 * */
	public TimerListener(TimerTask task,long delay, long period) {
		this.task = task;
		this.delay = delay;
		this.period = period;
	}

	/**
	 * @param timer  被监听的timer
	 */
	public void addTimerListener(MonitorTimer timer){
		this.timer = timer;
		this.start();
	}
	
	/**
	 * 是否是需要监听的task
	 * @param task
	 * @return
	 */
	public boolean isMonitorTask(TimerTask task){
		boolean flag = false;
		if(task instanceof MonitorTask){
			String taskName = task.getClass().getName();
			if(taskName.contains("M5Task")){
				flag = true;
			}else if(taskName.contains("M10Task")){
				flag = true;
			}//其他Task情况...
		}
		return flag;
	}

	public void run() {
		Date lastStartTime = null;
		Calendar calendar = null;
		boolean isMonitorTask = isMonitorTask(task);//该task是否为需要监听的task
		while(true){
			calendar = Calendar.getInstance();
			if(isMonitorTask){//只监听父类型为MonitorTask类型的task
				//过滤掉没有设定recentlyStartTime的task
				lastStartTime = ((MonitorTask)task).getRecentlyStartTime();//最近一次启动的日期
				if(lastStartTime == null){
					continue;
				}
//				SysLogger.info("----开始判断task是正常轮训:"+task);
				SysLogger.info("----最近一次启动"+task+"的日期--"+sdf.format(lastStartTime));
				long timeInterval = calendar.getTime().getTime() - lastStartTime.getTime();//实际时间间隔
				long maxTimeInterval = period*2 - 10*1000;//最大的可接受的时间间隔 = 实际Timer启动间隔*2  - 10秒（10秒为自设定的时间延迟范围） 
				SysLogger.info("----比较"+task+"启动的间隔时间----实际时间间隔:" + timeInterval+"  最大的可接受的时间间隔 :" + maxTimeInterval);
				if(timeInterval > maxTimeInterval){//间隔时间超过设定的task轮询时间间隔 *2 - 10秒  ，新建timer
					SysLogger.info("----"+timer+"已经停止运行 ，重启Timer");
					task.cancel();
					timer.purge();//移除所有已经暂停的任务
					timer.canclethis(true);
					try {
						task = (TimerTask)Class.forName(task.getClass().getName()).newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					timer = new MonitorTimer();//重建timer
					timer.schedule(task, 0, period);//新建Timer 并调度Task任务 并增加监听
					break;//退出此while-true循环体  
				}
				try {
					this.sleep(period);//停滞一段时间之后 再做Task是否正常轮训判断
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				break;
			}
		}
	}
}
