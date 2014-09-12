/*
 * Created on 2005-4-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import com.afunms.common.util.SysLogger;
import com.afunms.polling.api.I_HostCollectDataDay;
import com.afunms.polling.impl.HostCollectDataDayManager;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HostCollectDayTask extends MonitorTask {

	/**
	 * 
	 */
	public HostCollectDayTask() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		I_HostCollectDataDay hostdataManager = new HostCollectDataDayManager();
		try {
			hostdataManager.schemeTask();
//			System.out.println(
//				"start delete " + Calendar.getInstance().getTime());
//			
//			System.out.println(
//				"end delete " + Calendar.getInstance().getTime());
		} catch (Exception e) {
			//System.out.println(e);
			SysLogger.info(e.getMessage());

		} finally {
		}
		// TODO Auto-generated method stub

	}

}
