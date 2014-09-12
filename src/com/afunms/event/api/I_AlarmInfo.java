/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.api;

import java.util.List;
import java.util.Vector;
import java.util.Hashtable;

import com.afunms.event.model.AlarmInfo;
//import com.afunms.event.om.AllIplimenconf;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface I_AlarmInfo {
	public boolean createAlarmInfo(AlarmInfo alarminfo)throws Exception;
	//É¾³ý
	public boolean deleteIplimenconf(Integer[] allIplimenconfid) throws Exception;
	//²éÑ¯bysearchkey
	public List getBySearch(String searchfield,String searchkeyword) throws Exception;
	//²éÑ¯
	public List getAll() throws Exception;	
	public List getByTime(String starttime,String totime) throws Exception;
	}