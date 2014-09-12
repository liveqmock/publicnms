/*
 * Created on 2005-4-8
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.api;
import java.util.Hashtable;

import com.afunms.system.model.User;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface I_HostCollectDataHour {
	public boolean schemeTask() ;
	///////////////////////////////////////////////以下为展现层用到的函数
	//连通率的日报表
	public String[] gethourHis(String ip,String category,String entity,String subentity,String starttime, String totime) throws Exception;
	public Hashtable gethourHis1(String ip,String category,String entity,String subentity,String starttime, String totime) throws Exception;
    public Hashtable getmultiHis(String ip,String category,String starttime, String totime) throws Exception;
	public Hashtable getmultiHis(String ip,String category,String subenity,String[] bandkey,String[] bandch,String starttime, String totime) throws Exception;
	public Hashtable getmultiHis(String ip,String category,String subenity,String[] bandkey,String[] bandch,String starttime, String totime,String tablename) throws Exception;
	public Hashtable[] getMemory_month(String ip,String category,String starttime,String endtime)throws Exception;
	public boolean hostreportAll(User user);
	public boolean netreportAll(User user);
}
