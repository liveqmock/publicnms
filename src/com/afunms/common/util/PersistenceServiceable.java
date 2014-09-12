/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

//import com.cn.dhcc.ens.model.DTO;

public interface PersistenceServiceable
{
    public void addOneNetworkItem(
    		String idName,
    		String ipAddress,
    		String netMask,
    		String community,
    		String writeCommunity,
    		String commonNodes,
    		String subNetList,
    		String routerList,
    		String switchList,
    		String snmpRespIpAddrList,
    		String pingRespIpList,
    		String unResponseIpAddrList,
    		String haveAddIpAddrMap,
    		String discoverStatus,
    		String containNetId,
    		String isSeedNetwork,
    		String reserved1,
    		String reserved2,
    		String reserved3,
    		String reserved4,
    		String reserved5,
    		String reserved6,
    		Connection outConnection) throws PersistException;
    	  			
	public void addOneAlarmItem(
	        String alarmId,
	        String ipAddress,
	        String idName,
	        String alarmDate,
	        String alarmTime,
	        String severityoftheAlarm,
	        String sourceOftheAlarm,
	        String alarmDetailInfor,
	        String alarmDealDoneState,
	        String reserved1,
	        String reserved2,
	        String reserved3,
	        Connection outConnection) throws PersistException;
	
	public void addOneMobileAlarmItem(String sqlStr,
			Connection outConnection) throws PersistException;
	      	        			
	public void addOneMonitorItem(
	        String idname,
	        String monitorOid,
			String monitorGroup,
			String monitorCategory,
			String monitorDeviceType,
			String monitorDisplayName,
			String monitorValue,
			String ipAddress,
			String snmpPort,
			String community,
			String isSnmpMonitorItem,
			String thresholdEnable,
			String thresholdLimit,
			String thresholdCheck,
			String severityoftheAlarm,
			String consecutivetimes,
			String reserved1, String reserved2, String reserved3,
			String reserved4, Connection outConnection) throws PersistException;
	
	public void addOneMonitorItem(
	        String idname,
	        String monitorOid,
			String monitorGroup,
			String monitorCategory,
			String monitorDeviceType,
			String monitDisplayOldName,
			String monitDisplayNewName,
			String monitorValue,
			String ipAddress,
			String snmpPort,
			String community,
			String isSnmpMonitorItem,
			String thresholdEnable,
			String thresholdLimit,
			String thresholdCheck,
			String severityoftheAlarm,
			String consecutivetimes,
			String reserved1, String reserved2, String reserved3,
			String reserved4, Connection outConnection) throws PersistException;
			
	public void addOneNodeObjItem(
	         String idName,
	         String sysObjectIdValue,
	         String sysName,
	         String sysDescr,
	         String ipAddress,
	         String netMask,
	         String community,
			 String writeCommunity,
			 String snmpVersion,
			 String snmpPort,
			 String containNetId,
			 String snmpSupport,
			 String deviceCategory,
			 String deviceType,
			 String performDataSnmpTimeOut,
			 String reserved1,
			 String reserved2,
			 String reserved3,
			 String reserved4,
			 String reserved5,
			 String reserved6,
			 Connection outConnection) throws PersistException;
		   			
	public void addOneRouterItem(
	        String idName,
	        String sysObjectIdValue,
	        String sysName,
	        String sysDescr,
	        String ipAddress,
	        String netMask,
	        String community,
			String writeCommunity,
			String snmpSupport,
	        String snmpVersion,
			String snmpPort,
			String containNetId,
			String deviceCategory,
		    String deviceType,
		    String theLayer,
		    String performDataSnmpTimeOut,
		    String reserved1,
		    String reserved2,
		    String reserved3,
			String reserved4,
			String reserved5,
			String reserved6,
		    Connection outConnection) throws PersistException;
		  
	public void addOneSwitchItem(
	         String idName,
	         String sysObjectIdValue,
	         String sysName,
	         String sysDescr,
	         String ipAddress,
	         String netMask,
	         String community,
		     String writeCommunity,
		     String snmpVersion,
		     String snmpPort,
		     String containNetId,
		     String snmpSupport,
		     String deviceCategory,
		     String deviceType,
		     String performDataSnmpTimeOut,
		     String reserved1,
		     String reserved2,
		     String reserved3,
			 String reserved4,
			 String reserved5,
			 String reserved6,
		     Connection outConnection) throws PersistException;
		   
	public void addOneMonitorItemCollet(
	         String colletIpaddress, 
	         String monitorColletDisplayName, 
	         String monitorColletOid, 
	         String monitorColletValue, 
	         String monitorColletDate, 
	         String monitorColletTime, 
	         String reserved1, 
	         String reserved2, 
	         String reserved3, 
	         String reserved4,
	         Connection outConnection) throws PersistException;
	
	public void addOneDispatchNet(
			String nodeIp, 
			String insertTime,
			int timeForSum, 
			String type, 
			Connection outConnection) throws PersistException;
	       
	public void addOneProducerItem(
		     String producerName, 
		     String reserved1, 
		     String reserved2, 
		     String reserved3, 
		     String reserved4,
		     Connection outConnection) throws PersistException;
	       
	public void addOneDeviceTypeCatalogItem(
	         String objIdValue, 
	         String typeDesc, 
	         String picture, 
	         String category, 
	         String producer, 
	         String reserved1, 
	         String reserved2, 
	         String reserved3, 
	         String reserved4,
	         Connection outConnection) throws PersistException;
	       
	public void addOneSpecSnmpCommunItem(
	         String ipaddress, 
	         String community, 
	         String writeCommunity, 
	         String snmpPort, 
	         String reserved1, 
	         String reserved2, 
	         String reserved3, 
	         String reserved4, 
	         String reserved5, 
	         String reserved6,
	         Connection outConnection) throws PersistException;
	         
    public void addOneIfCollectItem(
	          String idName, 
	          String ifindex, 
	          String ifutiliRate, 
	          String ifinErrorsRate, 
	          String ifoutErrorsRate, 
	          String ifinDiscardsRate, 
	          String ifoutDiscardsRate, 
	          String reserved1, 
	          String reserved2, 
	          String reserved3, 
	          String reserved4,
	          Connection outConnection) throws PersistException;
    
    public void addOneEnsCoreParamInfor(
    		  String paramName,
    		  String paramValue,
    		  String paramType,
			  String reserved1,
			  String reserved2,
			  String reserved3,
			  String reserved4,
			  String reserved5,
			  Connection outConnection) throws PersistException;
    
    public void addOneIfEntryInfor(
    		String idname,
    		String ifindex,
			String iftype,
			String ifspeed,
			String ifdescr,
			String Reserved1,
    		String Reserved2,
    		String Reserved3,
    		String Reserved4,
			Connection outConnection) throws PersistException;
	  
    public void addOneNodeParamInfor(
    		String idname,
			String ipaddress,
			String protocolDescr,
			String port,
			String useName,
			String password,
			String prompt,
			String reserved1,
			String reserved2,
			String reserved3,
			String reserved4,
			String reserved5,
			Connection outConnection) throws PersistException;
    
    public void addOneIfCollectTmpItem(
	          String idName, 
	          String ifindex, 
	          String ifutiliRate, 
	          String ifinErrorsRate, 
	          String ifoutErrorsRate, 
	          String ifinDiscardsRate, 
	          String ifoutDiscardsRate, 
	          String reserved1, 
	          String reserved2, 
	          String reserved3, 
	          String reserved4,
	          Connection outConnection) throws PersistException;
    
    public void addOneMonitorTmpItemCollet(
	         String colletIpaddress, 
	         String monitorColletDisplayName, 
	         String monitorColletOid, 
	         String monitorColletValue, 
	         String monitorColletDate, 
	         String monitorColletTime, 
	         String reserved1, 
	         String reserved2, 
	         String reserved3, 
	         String reserved4,
	         Connection outConnection) throws PersistException;
    
    public void addOneApplicInfor(
             String applicName,
         	 String nodeId,
         	 String applicType,
         	 String applicIpAddress,
         	 String appliNetMask,
         	 String loginUser,
         	 String loginPassword,
         	 String applicPort,
         	 String applicVersion,
			 String dbOrServiceName,
         	 String isSSL,
         	 String reserved1,
         	 String reserved2,
         	 String reserved3,
         	 String reserved4,
         	 String reserved5,
			 Connection outConnection) throws PersistException;
    
    public void addOneNotifyMessageInfor(
             String id,
             String user,
             String message,
             String createTime,
             String endTime,
             Connection outConnection) throws PersistException;
    
	//List中为NetworkInfor	   
    public List  searchNetworkItem(
             String idName, Connection outConnection);
	 
	//List中为AlarmInfor       
	public List searchAlarmItem(
	         String alarmId, Connection outConnection);
	
	//List中为MonitorItemInfor       
	public List searchMonitorItem(
				 String idName, Connection outConnection);
	
	//List中为NodeObjInfor	
	public List searchNodeObjItem(
			     String idName, Connection outConnection);	  
	
	//List中为RouterInfor			   
	public List searchRouterItem(
				 String idName, Connection outConnection);	
	
	//List中为SwitchInfor				   
	public List searchSwitchItem(
				 String idName, Connection outConnection);	
			   
	//List中为MonitorItemColletInfor		   
	public List searchMonitorItemCollet(
	             String colletIpaddress, Connection outConnection);
	
	//List中为ProducerInfor          
	public List searchProducerItem(
	              String producerName, Connection outConnection); 
	            
    //List中为DeviceTypeCatalogInfor 
    public List searchDeviceTypeCatalogItem(
	              String objIdValue, Connection outConnection);	            
	            
	//List中为SpecSnmpCommunInfor
	public List searchSpecSnmpCommunItem(
	              String ipAddress, Connection outConnection);
	              
	//List中为IfCollectInfor
	public List searchIfCollectItem(
	              String idName,Connection outConnection); 
	
	//list中为EnsCoreParamInfor
	public List searchEnsCoreParamInfor(
	 		      String paramName,Connection outConnection);
	//list中为IfEntryInfor
	public List searchIfEntryInfor(
	 		      String idname,Connection outConnection);
	//list中为NodeParanInfor
	public List searchNodeParamInfor(
	 		      String idname,Connection outConnection);
	 
    //List中为MonitorItemColletTmpInfor		   
	public List searchMonitorItemColletTmpInfor(
		          String colletIpaddress, Connection outConnection);
		
    //List中为IfCollectTmpTable
	public List searchIfCollectTmpTable(
		          String idName,Connection outConnection); 

	//List 中为ApplicInfor
	public List searchApplicInfor(
			      String applicName,Connection outConnection);
	
	//List 中为NotifyMessageInfor
	public List searchNotifyMessageInfor(
	              String id,Connection outConnection);
	 
	public List networkInforList(Connection outConnection); 
	
	public List alarmInforList(Connection outConnection); 
	 
	public List monitorItemInforList(Connection outConnection); 
	
	public List nodeObjInforList(Connection outConnection); 
	
	public List routerInforList(Connection outConnection); 
	
	public List switchInforList(Connection outConnection); 
	
	public List monitorItemColletList(Connection outConnection);
	
	public List producerInforList(Connection outConnection);
	
	public List deviceTypeCatalogInforList(Connection outConnection);
	
	public List specSnmpCommunInforList(Connection outConnection);	
	
    public List ifCollectInforList(Connection outConnection);
    
    public List ensCoreParamInforList(Connection outConnection);
    
    public List ifEntryInforList(Connection outConnection);
    
    public List nodeParamInforList(Connection outConnection);
    
    public List ifCollectTmpTableList(Connection outConnection);
    
    public List monitotItemColletTmpInfor(Connection outConnection);
    
    public List applicInforList(Connection outConnection);
    
    public List notifyMessageInforList(Connection outConnection);
	
	public Connection getDbPoolConnection();
    
	public void closeDbPoolConnection(Connection connection);
		 
	/**
     * 执行一段 SQL 查询语句并返回结果。该方法不判断 SELECT 语句的合法性。<br>
     * 例如：设 String[][] queryResult 为查询返回的结果，可如下遍历出结果信息：<br>
     * for (int m = 0; m < queryResult.length; m++) {
     *      for (int n = 0; n < queryResult[0].length; n++) {
     *          System.out.print(queryResult[m][n] + "|");
     *      }
     *      System.out.println();
     *  }
     * @param query 指定输入的查询语句
     * @param outConnection 指定需要使用的外部连接，如果该参数为 null，则函数使用 Persistence 指定的内部JDBC连接
     * @return 查询结果[m][n]，m为行数，n为列数，注：返回的第 [0][m] 维数组为表头（Lable）
     */
    public String[][] executeQuery(String query, Connection outConnection);
        
    /**
     * 执行SQL插入、更新、删除记录操作，该方法不判断输入参数SQL语句的合法性
     * @param update 指定需要执行的SQL语句
     * @param outConnection 指定需要使用的外部连接，如果该参数为 null，则函数使用 Persistence 指定的内部JDBC连接
     */
    public boolean executeUpdate(String update, Connection outConnection);        
    
    /**
     * 备份当天的数据
     * @return
     */
    public boolean backupDayData(Connection outConnection);
    
    /**
     * 备份历史数据
     * @return
     */
    public boolean backupHistoryData(Connection outConnection);
        
    /**
     * 取得数据备份目录
     * @return
     */
    public String getBackUpDirectory();
    
    /**
     * 设置数据备份目录
     */
    public void setBackUpDirectory(String tmpStr);    
    
    /**
     * 返回告警表备份上限记录数
     * @return
     */
    public String getAlarmDataBkCount();
    
    /**
     * 设置告警表备份上限记录数
     * @param alarmRecordCnt
     */
    public void setAlarmDataBkCount(long alarmRecordCntParam);
    
    /**
     * 返回资源监控和接口历史表的备份上限记录数
     * @return
     */
    public String getHisDataBkCount();
    
    /**
     * 设置资源监控和接口历史表的备份上限记录数
     * @param hisDataBkCountParam
     */
    public void setHisDataBkCount(long hisDataBkCountParam);
    /**
     * 返回通知通告信息
     * @param pageNO
     * @param recentId
     * @return DTO
     */
    //public DTO loadMessages(int pageNO,String recentId);
    //********************kjchen 于重庆项目组**********************  开始
	public String[][] executeQueryOracle(String sql, Connection conn);
	 //********************kjchen 于重庆项目组**********************  结束
}

