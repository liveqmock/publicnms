/**
 * <p>Description:主要用于UPS和空调</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project 齐鲁石化
 * @date 2007-1-25
 */

package com.afunms.security.util;

public class MachineProtectHelper
{
	/**
	 * 节点状态描述
	 */
    public static String getUPSStatusDescr(int status)
    {
        String descr = null; 
    	if(status==1)
    	   descr = "正常";
		else if(status==2)	//0,1,2,3都与一般的节点保持一致		
		   descr = "设备忙";
		else if(status==3)			
		   descr = "Ping不通";  
		else if(status==4)
		   descr = "UPS有故障";
		else if(status==5)
		   descr = "市电断电,电池启用";
		else //0
		   descr = "不被管理";
    	return descr;
    }
    
	/**
	 * 节点状态标志
	 */
    public static String getStatusImage(int status)
    {
        String image = null; 
    	if(status==1)
    	   image = "status5.png";
		else if(status==2)
		   image = "status2.png";     	
		else if(status==3)
		   image = "status4.png"; 
		else if(status==4)
		   image = "status1.png";
		else if(status==5)
		   image = "status3.png";
		else
		   image = "status6.png";	
    	return "image/topo/" + image;
    }
}