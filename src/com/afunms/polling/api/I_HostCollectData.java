/*
 * Created on 2005-4-8
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.api;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.DBManager;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.impl.*;
//import com.dhcc.webnms.util.InitCoordinate;
/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface I_HostCollectData {
//	资源取得方式类型增加
			  public boolean createHostData(Hostcollectdata hostdata) throws Exception;
//	资源取得方式类型增加
			  //public boolean createHostData(Vector hostdatavec) throws Exception;
			  public boolean createHostData(Vector hostdatavec,NodeGatherIndicators alarmIndicatorsNode) throws Exception;
			  public boolean refreshCollectLastTable() throws Exception;
			  //public boolean createHostData(String ip,Vector hostdatavec) throws Exception;
			  public boolean createHostData(String ip,Hashtable datahash);
			  //资源取得方式类型删除
			  public boolean deleteHostData(String deletetime) throws Exception;
			  //资源取得方式类型查询byid
			  public Hostcollectdata getByHostdataid(Integer hostdataid) throws Exception;
			  //资源取得方式类型查询byname
			  public List getByIpaddress(String ipaddress,String time) throws Exception;
			  //资源取得方式类型查询byname
			  public List getByIpCategory(String ipaddress,String Category,String time) throws Exception;
			  //	资源取得方式类型查询byname
			  public List getByIpCategoryEntity(String ipaddress,String Category,String entity,String time) throws Exception;
			  //资源取得方式类别查询bysearchkey
			  public List getBySearch(String searchfield,String searchkeyword) throws Exception;
			  //资源取得方式类型查询
			  public List getHostcollectdata(String time) throws Exception;
			  //	资源取得方式类型查询
			  public List getHostcollectdata() throws Exception;
			  // as400 入库
			  public boolean createHostDataForAS400(String ip,Hashtable datahash);//nielin add

	//--------------------------------------------------------------------
  public Hashtable[] getIfBand(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime) throws Exception;
  public Hashtable[] getIfBand_UtilHdxPerc(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime) throws Exception;
  public Hashtable[] getDiscardsPerc(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime) throws Exception;
  public Hashtable[] getErrorsPerc(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime) throws Exception;
  public Hashtable[] getIfBand_UtilHdx(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime) throws Exception;
  public Hashtable[] getIfBand_Packs(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime) throws Exception;
  public Hashtable[] getIfBand_InPacks(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime) throws Exception;
  public Hashtable[] getIfBand_OutPacks(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime) throws Exception;
  public Vector[] getIfStatus(String ipaddress,String index,String starttime,String endtime) throws Exception;
  public Hashtable getCategory(String ip,String category,String subentity,String starttime,String endtime)throws Exception;
  public Hashtable getCategory1(String ip,String category,String subentity,String starttime,String endtime)throws Exception;
  public Hashtable getCpuHistroy(String ip,String category,String subentity,String starttime,String endtime)throws Exception;
  public Hashtable getOraSpaceHistroy(String ip,String category, String starttime, String endtime)throws Exception;
  public Hashtable[] getMemory(String ip,String category,String starttime,String endtime)throws Exception;  
  public Hashtable getFlash(String ip,String category,String starttime,String endtime,String time)throws Exception;
  
  public Hashtable[] getCategoryDayAndMonth(String ip,String category,String starttime,String endtime,String tablename)throws Exception;
  public Hashtable[] getBand(String ipaddress,String[] bandkey,String[] bandch,String starttime,String endtime) throws Exception;
  public Hashtable[] getBand_AllUtilHdxPerc(String ipaddress,String[] bandkey,String[] bandch,String starttime,String endtime) throws Exception;
  public Hashtable[] getBand_AllUtilHdx(String ipaddress,String[] bandkey,String[] bandch,String starttime,String endtime) throws Exception;
  public String[] getAvailability(String ipaddress,String starttime,String endtime) throws Exception;
  public Hashtable getbandutil(String ip,String orderflag,String starttime,String endtime)throws Exception;
  public Hashtable getbandutil_sub(String ip,String orderflag,String starttime,String endtime)throws Exception;
  public Hashtable getAllutilhdx(String ip,String subentity,
			String starttime,
			String endtime,
			String need) throws Exception;
	public Hashtable getAllutilhdx(String ip,String subentity,
				String starttime,
				String endtime,
				String need,
				String time) throws Exception;
	public Hashtable getUtilhdx(String ip,String sindex,String starttime,String endtime) throws Exception;
	public Hashtable getUtilhdxper(String ip,String sindex,String starttime,String endtime,String time) throws Exception;
	public Hashtable getNetMemeory(String ip, String category, String starttime, String endtime)throws Exception;
	 public Hashtable  getCurByCategory(String ip,String category,String subentity)throws Exception;
	public Hashtable getDiskHistroy(String ip,String category, String starttime, String endtime) throws Exception;
		
	public Hashtable getPingData(String ip,String subentity,
				String starttime,
				String endtime,
				String need,
				String time) throws Exception;
	public Hashtable getMemory(String ip,String category,String starttime,String endtime,String time)throws Exception;
	public Hashtable[] getIfBand_Packs(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime,String time) throws Exception;
	public Hashtable[] getIfBand_InPacks(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime,String time) throws Exception;
	public Hashtable[] getIfBand_OutPacks(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime,String time) throws Exception;
	public Hashtable[] getDiscardsPerc(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime,String table) throws Exception;
	public Hashtable[] getErrorsPerc(String ipaddress,String index,String[] bandkey,String[] bandch,String starttime,String endtime,String table) throws Exception;
	public Hashtable getCategory(String ip,String category,String subentity,String starttime,String endtime,String time)throws Exception;
	public Hashtable getBusCategory(
			String id,
			String bid,
			String starttime,
			String endtime,
			String time)throws Exception;
	public Hashtable getDisk(String ip,String category,String starttime,String endtime,String time)throws Exception;
	public Hashtable getEnviroment(String ip,String category,String starttime,String endtime,String time,String table)throws Exception;
	public Hashtable getLkuhdxp(String id,String starttime,String endtime,String time)throws Exception;
	public Hashtable getLkPing(String id,String starttime,String endtime,String time)throws Exception;
	public Hashtable getLkuhdx(String id,String entity,String starttime,String endtime,String time)throws Exception;
	public boolean createGSNData(String key,Hashtable datahash);
	public boolean createHostItemData(Hashtable alldata, String string);
	public Hashtable getCpuDetail(String ip,String starttime, String endtime) throws Exception ;
	public Hashtable getPageingDetail(String ip,String starttime, String endtime) throws Exception;
	public Hashtable getCpuDetails(DBManager dbmanager,String ip,String category, String starttime, String endtime, String time) throws Exception;
	public Hashtable getPgusedData(String ip, String starttime, String endtime, String need, String time);
	//根据表明得到subenity列表
	public List<String> getSubentitysByTableName(String tablename);
	public Hashtable getUpsData(String ip, String category, String starttime, String endtime, String time, String table) throws Exception;
	public Hashtable getDataHash(String tablename, String starttime, String endtime)throws Exception;
	public Hashtable getDataHash(String tablename, String starttime, String endtime,String category)throws Exception;
}
