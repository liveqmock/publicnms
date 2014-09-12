/**
 * <p>Description:monitor object id constants</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-26
 */

package com.afunms.monitor.item.base;

public class MoidConstants
{
	private MoidConstants()
	{		
	}
	public static final String WINDOWS_CPU = "001001";
	public static final String WINDOWS_MEMORY = "001002";
	public static final String WINDOWS_DISK = "001003";
	public static final String CISCO_CPU = "002001";
	public static final String CISCO_MEMORY = "002002";
	public static final String OPERATION_STATUS = "003001";
	public static final String RX_UTILIZATION = "003002";
	public static final String TX_UTILIZATION = "003003";
	public static final String ERRORS = "003004";
	public static final String DISCARDS = "003005";
	public static final String HOST_PING = "999001";
	public static final String TEST_SERVICE = "999002";
	public static final String TOMCAT_PING = "051001";
	public static final String TOMCAT_JVM = "051002";	

	public static final String DB_PING = "052001";
	public static final String MYSQL_SESSION = "052002";	
	
	public static final String TRU64_CPU = "004001";
	public static final String TRU64_MEMORY = "004002";
	public static final String TRU64_FILE_SYSTEM = "004003";
	public static final String TRU64_DIR = "004004";
}