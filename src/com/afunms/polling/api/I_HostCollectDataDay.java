/*
 * Created on 2005-4-8
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.api;

import java.util.Hashtable;
/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface I_HostCollectDataDay {
	public boolean schemeTask() throws Exception;
	//public int deleteTask() throws Exception;
	//public int deleteHourTask() throws Exception;
	//public int deleteDayTask() throws Exception;
	public String[][] getdayHis(String ip,String category,String entity,String subentity,String year, String month) throws Exception;
	public Hashtable getdayHis1(String ip,String category,String entity,String subentity,String year, String month) throws Exception;
	public Hashtable getmultiHis(String ip,String category,String starttime, String totime) throws Exception;
	public Hashtable getmultiHis(String ip,String category,String subentity,String[] bandkey,String[] bandch,String starttime, String totime) throws Exception;
	public Hashtable getmultiHis(String ip,String category,String subentity,String[] bandkey,String[] bandch,String starttime, String totime,String tablename) throws Exception;
	//public Hashtable getmultiHisMonth(String ip,String category,String subentity,String[] bandkey,String[] bandch,String starttime, String totime,String tablename) throws Exception;
	public Hashtable getmultiHis(String ip,String category,String subentity,String[] bandkey,String[] bandch,String startyear) throws Exception;
	public Hashtable getmultiHisHdx(String ip,String category,String subentity,String[] bandkey,String[] bandch,String startyear,String tablename) throws Exception;
	public Hashtable getmultiHisHdxMonth(String ip,String category,String subentity,String[] bandkey,String[] bandch,String year,String month,String tablename) throws Exception;
	public Hashtable getmultiHisHdx(String ip,String category,String subentity,String[] bandkey,String[] bandch,String starttime, String totime,String tablename) throws Exception;
	public Hashtable getmultiHisPerc(String ip,String category,String subentity,String[] bandkey,String[] bandch,String startyear,String tablename) throws Exception;
	//yangjun add
	public Hashtable getmultiHisHdx(
			String ip,
			String subentity,
			String entity,
			String starttime,
			String totime,
			String time) throws Exception;
	public Hashtable getAllAvgAndMaxHisHdx(
			String ip,
			String starttime,
			String endtime)
			throws Exception ;
}
