/**
 * <p>Description:utility class,includes some methods which are often used</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import com.afunms.system.dao.*;
import com.afunms.system.model.*;

public class SendMailManager 
{
	private String test;
    private MimeMessage mimeMessage = null;
    private String saveAttachPath = "";//附件下载后的存放目录
    private StringBuffer bodytext = new StringBuffer(); //存放邮件内容的StringBuffer对象
	private String dateformat = "yy-MM-ddHH:mm"; //默认的日前显示格式
	
	public boolean SendMail(String receivemailaddr,String body)
	{
		String reporttime = ProjectProperties.getDayReporTime();
		AlertEmail vo = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try{
			list = emaildao.getByFlage(1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			emaildao.close();
		}
		if(list != null && list.size()>0)
		{
			vo = (AlertEmail)list.get(0);
		}
		if(vo == null)return false;
		String mailaddr = vo.getUsername();
		String mailpassword = vo.getPassword();
		String mailsmtp = vo.getSmtp();
		SysLogger.info(mailaddr+"==="+mailpassword+"==="+mailsmtp);
		boolean flag = false;
		try{
			Address[] ccAddress = {new InternetAddress("hukelei@dhcc.com.cn"),new InternetAddress("rhythm333@163.com")};
			//String fromAddr = "supergzm@sina.com";
			String fromAddr="";
			SendMail sendmail = new SendMail(mailsmtp,mailaddr,
					mailpassword,receivemailaddr, "网管服务告警邮件", body,fromAddr,ccAddress);
			
			try{
				flag = sendmail.sendmail();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return flag;
	}
	public boolean SendMailWithFile(String fromAddress,String receivemailaddr,String body,String fileName)
	{
		String reporttime = ProjectProperties.getDayReporTime();
		AlertEmail vo = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try{
			list = emaildao.getByFlage(1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			emaildao.close();
		}
		if(list != null && list.size()>0)
		{
			vo = (AlertEmail)list.get(0);
		}
		if(vo == null)return false;
		String mailaddr = vo.getUsername();
		String mailpassword = vo.getPassword();
		String mailsmtp = vo.getSmtp();
		SysLogger.info(mailaddr+"==="+mailpassword+"==="+mailsmtp);
		boolean flag = false;
		try{
			Address[] ccAddress = {new InternetAddress("hukelei@dhcc.com.cn"),new InternetAddress("rhythm333@163.com")};
			String fromAddr = fromAddress;
			//String fromAddr="";
			SendMail sendmail = new SendMail(mailsmtp,mailaddr,
					mailpassword,receivemailaddr, "网管服务告警邮件", body,fromAddr,ccAddress);
			
			try{
				flag = sendmail.sendmailWithFile(fileName);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return flag;
	}
	
	public boolean SendMailNoFile(String fromAddress,String receivemailaddr,String body)
	{
		String reporttime = ProjectProperties.getDayReporTime();
		AlertEmail vo = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try{
			list = emaildao.getByFlage(1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			emaildao.close();
		}
		if(list != null && list.size()>0)
		{
			vo = (AlertEmail)list.get(0);
		}
		if(vo == null)return false;
		String mailaddr = vo.getUsername();
		String mailpassword = vo.getPassword();
		String mailsmtp = vo.getSmtp();
		SysLogger.info(mailaddr+"==="+mailpassword+"==="+mailsmtp);
		boolean flag = false;
		try{
			Address[] ccAddress = {new InternetAddress("hukelei@dhcc.com.cn"),new InternetAddress("rhythm333@163.com")};
			String fromAddr = fromAddress;
			//String fromAddr="";
//			System.out.println("in the send Mail manager page get the body is:"+body);
			SendMail sendmail = new SendMail(mailsmtp,mailaddr,
					mailpassword,receivemailaddr, "网管服务告警邮件", body,fromAddr,ccAddress);
			
			try{
				flag = sendmail.sendmailNoFile();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return flag;
	}
	
	public boolean SendMyMail(String receivemailaddr,String body)
	{
		String reporttime = ProjectProperties.getDayReporTime();
		AlertEmail vo = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try{
			list = emaildao.getByFlage(1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			emaildao.close();
		}
		if(list != null && list.size()>0)
		{
			vo = (AlertEmail)list.get(0);
		}
		if(vo == null)return false;
		String mailaddr = vo.getUsername();
		String mailpassword = vo.getPassword();
		String mailsmtp = vo.getSmtp();
		SysLogger.info(mailaddr+"==="+mailpassword+"==="+mailsmtp);
		boolean flag = false;
		try{
			Address[] ccAddress = {new InternetAddress("hukelei@dhcc.com.cn"),new InternetAddress("rhythm333@163.com")};
			String fromAddr = "";
			SendMail sendmail = new SendMail(mailsmtp,mailaddr,
					mailpassword,receivemailaddr, "网管服务告警邮件", body,fromAddr,ccAddress);
			
			try{
				flag = sendmail.sendmail();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return flag;
	}
	
	
	
	/**
	*构造函数
	*/
	public SendMailManager(){}

}