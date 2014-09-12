/*
 * Created on 2005-4-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.api;
import java.util.List;
import java.util.Hashtable;
import java.util.Vector;
import com.afunms.polling.snmp.Hostlastcollectdata;
/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface I_HostLastCollectData {
              //资源取得方式类型增加
			  public boolean createHostData(Hostlastcollectdata hostdata) throws Exception;
              //资源取得方式类型增加
			  public boolean createHostData(Vector hostdatavec) throws Exception;
			  public boolean createHostData(String ip,Vector hostdatavec) throws Exception;
			  //资源取得方式类型删除
			  public boolean deleteHostData(String deletetime) throws Exception;
			  //资源取得方式类型查询byid
			  public Hostlastcollectdata getByHostdataid(Integer hostdataid) throws Exception;
			  public Hostlastcollectdata getByipandsubentity(String ipaddress,String index) throws Exception;
			  //资源取得方式类型查询byname
			  public List getByIpaddress(String ipaddress) throws Exception;
			  public List getBysubentity(String thevalue) throws Exception;

              public Hashtable getStatic(String ipaddress,String[] Category) throws Exception;

			  public List getByIpCategory(String ipaddress,String restype,String Category) throws Exception;

	          //资源取得方式类型查询byname
			  public List getByIpCategoryEntity(String ipaddress,String restype,String Category,String entity) throws Exception;
			  //资源取得方式类别查询bysearchkey
			  public List getBySearch(String searchfield,String searchkeyword) throws Exception;
			  //资源取得方式类型查询
			  public List getHostcollectdata(String time) throws Exception;			  			 
              //资源取得方式类型查询
			  public List getHostcollectdata() throws Exception;
              //取各网络主机的cpu,连通率，内存，磁盘利用率
              public List[]    getParameter(String[] ip)throws Exception;
	          public Hashtable getIfOctets(String ip) throws Exception;
	          public Hashtable getLastPacks(String ip) throws Exception;
	          public Hashtable getLastDiscards(String ip) throws Exception;	
	          public Hashtable getLastErrors(String ip) throws Exception;
	          public Hashtable getIfOctets_share(String ip) throws Exception;
	//--------------------------------------------
	          public Hashtable getCategory(String ip,String category,String starttime,String endtime)throws Exception;
	          public Vector getAllUtilHdxInterface(String ip,String starttime,String endtime)throws Exception;
              public Vector getInterface(String ip,String[] InterfaceItem,String order,String starttime,String endtime)throws Exception;
              public Vector getInterface_share(String ip,String[] InterfaceItem,String order,String starttime,String endtime)throws Exception;
              public Vector getIpMac(String ip)throws Exception;
	          public Hashtable getIfdetail(String ip,String index,String[] key,String starttime,String endtime)throws Exception;
	          public Hashtable getIfdetail_share(String ip,String index,String[] key,String starttime,String endtime)throws Exception;
              public Hashtable getBand(String ip,String index,String starttime,String endtime)throws Exception;
              public Hashtable getBand_share(String ip,String index,String starttime,String endtime)throws Exception;
             // public Hashtable getbyCategories(String ip,String[] category,String starttime,String endtime)throws Exception;
              public Hashtable getbyCategories_share(String ip,String[] category,String starttime,String endtime)throws Exception;
	          public Hashtable getMemory(String ip,String category,String starttime,String endtime)throws Exception;
	          public Hashtable getMemory_share(String ip,String category,String starttime,String endtime)throws Exception;
	          public Hashtable getDisk(String ip,String category,String starttime,String endtime)throws Exception;
	          public Hashtable getDisk_share(String ip,String category,String starttime,String endtime)throws Exception;
	          public Hashtable getProcess(String ip,String category,String order,String starttime,String endtime)throws Exception;
	          public Hashtable getProcess_share(String ip,String category,String order,String starttime,String endtime)throws Exception;
		      public Hashtable getIfStatus(String ip,String starttime,String endtime)throws Exception;
	//监视任务里取得所有被监视ip的被监视项
	          public Hashtable getAllParam()throws Exception;
	          public Hashtable getAllParam_share()throws Exception;
	          
	          //得到所有Ping的列表
			  public List getAllPingdata(String thevalue) throws Exception;
			  /**
			   * 
			   * <p>集中采集所有链路节点的数据集合</p>
			   * @author HONGLI  2011-04-13
			   * @param ipList          链路的节点ip集合
			   * @param netInterfaceItem  列名
			   * @param orderflag         排序标志位
			   * @param starttime		  
			   * @param endtime
			   * @return
			   */
			  public Hashtable getInterfaces(List<String> ipList,String[] netInterfaceItem, String orderflag,String starttime,String endtime);
	          
}
