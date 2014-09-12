/*
 * Created on 2005-3-31
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import java.util.Calendar;
import java.util.Timer;
import java.util.Date;
import java.util.TimerTask;

import com.afunms.common.util.SysLogger;
import com.afunms.initialize.TimerListener;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MonitorTimer extends Timer {

	public MonitorTimer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param isDaemon
	 */
	public MonitorTimer(boolean isDaemon) {
		super(isDaemon);
		// TODO Auto-generated constructor stub
	}
	public void canclethis(boolean b){
		if(b==true){
			super.cancel();
//			Thread.currentThread().destroy(); 
		}
	}
	
	 public void schedule(TimerTask task, long delay, long period) {
		 super.scheduleAtFixedRate(task, delay, period);
		 new TimerListener(task, delay, period).addTimerListener(this);//Ôö¼Ó¼àÌý
	 }
}
