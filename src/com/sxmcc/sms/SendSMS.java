// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 
// Source File Name:   SendSMS.java

package com.sxmcc.sms;

import java.io.PrintStream;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.huilin.tinysoap.client.AlertClient;
import com.huilin.tinysoap.client.SendAlert;

// Referenced classes of package com.sxmcc.sms:
//            Config

public class SendSMS
{
	    static int do_flag = 0;
	    static Logger logger = Logger.getLogger("SMS");
	    static String soap_uri = "";
	    static String sp_id = "";
	  

	    public SendSMS()
	    {
	    	
	        do_flag = 1;
	        soap_uri = Config.getProp("soap_uri");
	        logger.debug("soap_uri:" + soap_uri);
	        sp_id = Config.getProp("sp_id");
	        logger.debug("sp_id:" + sp_id);
	      
	    }
	    
	    
	    public static void sendAlert(String misId[],String title,String content,String flowId[],String nodeId[]) 
	    {

           for(int i=0;i<misId.length;i++){
        	   sendAlert(misId[i], "", title, content, flowId[i], nodeId[i]);
           }


		}
	    
	    public static void sendSms(String content,String[] des) 
	    {

           for(int i=0;i<des.length;i++){
        	   sendSms(content,des[i]);
           }


		}

	public static void sendSms(String content,String phoneNo) {

		String appId =sp_id; // 由短信提醒平台统一分配的应用标识（相当于企业id）
	    // content = "工单内试容测试工单内容工单内容测试工单内容测试工单内容"; // 短信内容
		String soapUri =soap_uri;   //"http://10.204.4.38:8080/alert/services/HuilinAlertService";
		String out = null;
		// 发送短信接口调用
		out = "";
		//String phoneNo = "13834220402";
  	   try{
  		    //logger.debug("准备发送短信");
System.out.println("================准备发送短信");  	

	    	out = AlertClient.sendSms(soapUri, appId, content, phoneNo);
System.out.println(out);	    	
	    	//logger.debug("完成发送短信");
	      }catch(Exception e){
	    	  e.printStackTrace();
		     //logger.error("调用慧林短信平台发短信失败原因：" + e.toString());
		    
          }
		//logger.debug("手机号:" + phoneNo +"  内容： "+content+" \n短信接口返回值："+out);
	    //System.out.println("手机号:" + phoneNo +"  内容： "+content+" \n短信接口返回值："+out);

	}
	
	/**
	 * 慧林soap调用客户端,发送wap和短信
	 * 
	 * @param args
	 */
	public static void sendAlert(String misId,String uid,String title,String content,String flowId,String nodeId) {

		String appId =Config.getProp("sp_id");  // 由短信提醒平台统一分配的应用标识（相当于企业id）
		
		// title = "aslkdfalskdf"; // wap push 标题
		// content = "工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容试工单内容测试工单内容测试工单内容"; // 短信内容
		String wapUri = "211.138.109.68/wapoa/rds.jsp?flowId="+flowId.trim()+"&nodeId="+nodeId.trim(); // wap
		// push链接
		// String uid = "scwangli"; // 用户id,即登录poral的用户名
	    //	String misId = ""; // 员工编号, uid 与 misId 提供一个即可 

		String soapUri = Config.getProp("soap_uri");//"http://10.204.4.38:8080/alert/services/HuilinAlertService";
	
		String out = null;
		// 发送提醒接口调用
		try{
			logger.debug("准备发送WAP");
		   out = AlertClient.sendAlert(soapUri, appId, content, content, wapUri,uid, misId);
		   logger.debug("完成发送WAP");
		}catch(Exception e){
			logger.error("调用慧林短信平台发提醒失败原因：" + e.toString() );	
		}
		logger.debug("工号:" + misId+"  标题："+title +"  内容： "+content+"  \n push链接:"+wapUri+"  \n短信接口返回值："+out);
		// 返回值是一个xml格式的字符串，格式见《接口规范》
		//System.out.println("工号:" + misId+"  标题："+title +"  内容： "+content+"  \n push链接:"+wapUri+"  \n短信接口返回值："+out);
		System.out
				.println("-----------------------提醒接口返回值如下--------------------\n");
		System.out.println(out);

	}

	/**
	 * 慧林soap调用客户端,调用示例代码
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

	    String appId = "101012"; // 由短信提醒平台统一分配的应用标识（相当于企业id）
	    SendSMS ss=new SendSMS();
		String title = "aslkdfalskdf"; // wap push 标题
		String content = "工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容测试工单内容试工单内容测试工单内容测试工单内容"; // 短信内容
		String wapUri = "211.138.109.68/wapoa/rds.jsp?flowId=flow6018601892605466511570_12006444414117812&nodeId=node2042245782870_12006444965172465"; // wap
		// push链接
		String uid = "gaojie"; // 用户id,即登录poral的用户名
		String misId = ""; // 员工编号, uid 与 misId 提供一个即可

		String soapUri = "http://10.204.4.38:8080/alert/services/HuilinAlertService";

		String phoneNo = "13453112535";
		String flowId="232323";
		String nodeId="23232323";
		ss.sendSms("测试手机短信",phoneNo);
		//ss.sendAlert(misId, uid, title, content, flowId, nodeId);

	}
	
}
