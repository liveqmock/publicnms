package com.afunms.alarm.send;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.application.dao.FTPConfigDao;
import com.afunms.application.model.FTPConfig;
import com.afunms.common.util.FtpTool;
import com.afunms.common.util.SendMailManager;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.NodeconfigDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Ftp;
import com.afunms.polling.node.Host;
import com.afunms.polling.task.FtpUtil;
import com.afunms.system.dao.AlertEmailDao;
import com.afunms.system.dao.FtpTransConfigDao;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.AlertEmail;
import com.afunms.system.model.FtpTransConfig;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.cn.dhcc.util.license.DateUtil;
import com.dhcc.pdm.doc;
import com.dhcc.upload.Request;
import com.informix.util.dateUtil;

public class SendMailAlarm implements SendAlarm{

	public void sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail) {
		// TODO Auto-generated method stub
		SysLogger.info("==============发送邮件告警==================");
		AlertEmail em = null;
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
			em = (AlertEmail)list.get(0);
		}
//		if(em == null)return ;
		
		String userids = alarmWayDetail.getUserIds();
		UserDao userDao = new UserDao();
		List userList = new ArrayList();
		try{
			userList = userDao.findbyIDs(userids);
		}catch(Exception e){
			
		}finally{
			userDao.close();
		}
		if(userList != null && userList.size()>0){
			java.text.SimpleDateFormat _sdf1 = new java.text.SimpleDateFormat("MM-dd HH:mm");
			for(int i =0;i<userList.size();i++)
			{
				User vo = (User)userList.get(i);
				String mailAddressOfReceiver = vo.getEmail();
				try{
					Date cc = eventList.getRecordtime().getTime();
		  			String recordtime = _sdf1.format(cc);
		  			if(em != null){
		  				sendEmail(em.getMailAddress(),mailAddressOfReceiver,recordtime+" "+eventList.getContent());
//		  				System.out.println(eventList.getContent());
		  			}
					//#####################  深圳海关定制 AIX告警监控
					//存储告警信息到XML文件中
//					BuildEventXMLDoc(eventList); 
					//#####################   深圳海关定制
				}catch(Exception e){
					e.printStackTrace();
				}
				//sendEmail(em.getMailAddress(),mailAddressOfReceiver,"temp/network_and_host_report.xls");
			}
		}
	}
	
	
	
	/**
	 * 生成XML文件
	 * @param eventList
	 */
//	public void BuildEventXMLDoc(EventList eventList) {
//		//符合下列条件的生成告警信息xml文件  
//		if(eventList == null || eventList.getNodeid() == 0){
//			return ;
//		}
//		FtpTransConfigDao ftpTransConfigDao = new FtpTransConfigDao();
//		FtpTransConfig ftpTransConfig = ftpTransConfigDao.getFtpTransConfig();
//		ftpTransConfigDao.close();
//		if(ftpTransConfig.getFlag() != 1){//flag = 0： 不发送
//			return;
//		}
//		String subentity = eventList.getSubentity();
//		if(subentity != null &&(subentity.trim().equalsIgnoreCase("cpu") || subentity.trim().equalsIgnoreCase("physicalmemory") 
//				|| subentity.trim().equalsIgnoreCase("pagingusage") || subentity.trim().equalsIgnoreCase("iowait") 
//				|| subentity.trim().contains("errptlog"))){
//			Element root = new Element("root");
//			// 根节点添加到文档中；
//			Document Doc = new Document(root);
//			// 创建日期
//			Element datetime = new Element("datetime");
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			datetime.addContent(sdf.format(eventList.getRecordtime().getTime()));
//			// 创建节点 value;
//			Element content = new Element("value");
//			content.addContent(eventList.getContent());
//			root.addContent(datetime);
//			root.addContent(content);
//			XMLOutputter XMLOut = new XMLOutputter();
//			// 输出 xml 文件；
//			HostNodeDao dao = new HostNodeDao();
//			try {
//				HostNode host = (HostNode) dao.findByID(eventList.getNodeid() + "");
//				NodeUtil nodeUtil = new NodeUtil();
//				NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host); 
//				//不是aix的情况   
//				if(!nodedto.getSubtype().equalsIgnoreCase("aix")){
//					dao.close();
//					return;
//				}
//				//设置文件名、文件夹路径
//				String filename = host.getAlias() + "_" + eventList.getSubentity() + ".xml";
//				String filepath = ResourceCenter.getInstance().getSysPath() +"/ftpupload/";
//				System.out.println("$$$$$$$$$$$$生成告警xml文件的地址为--"+filepath+filename);
//				//生成xml格式的文件
//				XMLOut.output(Doc, new FileOutputStream(filepath+filename));
//				//上传xml文件
//				Boolean ftpFlag = ftpEventXml(ftpTransConfig,filepath, filename);
//				System.out.println("$$$$$$$$$$$$FTP--xml文件:"+filepath+filename+"--"+ftpFlag);
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				if(dao != null){
//					dao.close();
//				}
//			}
//		}
//	}
	
	/**
	 * 生成doc格式的文件
	 * @author HONGLI
	 */
	public void BuildEventXMLDoc(EventList eventList) {
		//符合下列条件的生成告警信息xml文件  
		if(eventList == null || eventList.getNodeid() == 0){
			return ;
		}
		FtpTransConfigDao ftpTransConfigDao = null;
		FtpTransConfig ftpTransConfig = null;
		try {
			ftpTransConfigDao = new FtpTransConfigDao();
			ftpTransConfig = ftpTransConfigDao.getFtpTransConfig();
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		} finally{
			if(ftpTransConfigDao != null){
				ftpTransConfigDao.close();
			}
		}
		
		String subentity = eventList.getSubentity();
		if(subentity != null &&(subentity.trim().equalsIgnoreCase("cpu") || subentity.trim().equalsIgnoreCase("physicalmemory") 
				|| subentity.trim().equalsIgnoreCase("pagingusage") || subentity.trim().equalsIgnoreCase("iowait") 
				|| subentity.trim().contains("errptlog")|| subentity.trim().equalsIgnoreCase("swapmemory"))){
			Element root = new Element("root");
			// 根节点添加到文档中；
			Document Doc = new Document(root);
			// 创建日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HostNodeDao dao = null;
			PrintWriter out = null;
			try {
				dao = new HostNodeDao();
				HostNode host = (HostNode) dao.findByID(eventList.getNodeid() + "");
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host); 
				//不是aix的情况   
				if(!nodedto.getSubtype().equalsIgnoreCase("aix")){
					return;
				}
				//设置文件名、文件夹路径
				String filename = host.getAlias() + "_" + eventList.getSubentity() + ".txt";
				String filepath = ResourceCenter.getInstance().getSysPath() +"ftpupload/";
				//System.out.println("$$$$$$$$$$$$生成告警doc文件的地址为--"+filepath+filename);
				//生成doc格式的文件
				File file = new File(filepath+filename);
				file.createNewFile();
				out = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath())));
				out.println("time=" + sdf.format(eventList.getRecordtime().getTime()));
//				out.println("value=" + eventList.getContent());
				out.println("value=2");  
				out.flush();
				//上传doc文件
				Boolean ftpFlag = false;
				if(ftpTransConfig != null && ftpTransConfig.getFlag() == 1){//flag = 0： 不发送
					ftpFlag = ftpEventXml(ftpTransConfig,filepath, filename);
				}
				//System.out.println("$$$$$$$$$$$$FTP传送告警doc文件:"+filepath+filename+"--"+ftpFlag);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(dao != null){
					dao.close();
				}
				if(out != null){
					out.close();
				}
			}
		}
	}
	
	/**
	 * 
	 * @param filepath  要传送的文件的所属文件夹路径
	 * @param filename  要传送的文件的文件名
	 * @return
	 */
	public Boolean ftpEventXml(FtpTransConfig ftpTransConfig, String filepath, String filename){
		Boolean retflag = false;
		FtpTool ftpTool = new FtpTool();
		ftpTool.setIp(ftpTransConfig.getIp());
		ftpTool.setPort(21);//端口
		ftpTool.setUser(ftpTransConfig.getUsername());
		ftpTool.setPwd(ftpTransConfig.getPassword());
		ftpTool.setRemotePath("/");//ftp文件夹
		try {
			ftpTool.uploadFile(ftpTool.getRemotePath(),filepath, filename);
			retflag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return retflag;
	}
	

	public static void main(String[] args) {   
	       try {   
	           SendMailAlarm j2x = new SendMailAlarm();   
	           System.out.println("生成 mxl 文件..."); 
	           EventList eventList = new EventList();
	           eventList.setContent("aix服务器(IP: 10.204.7.20) 物理内存利用率超过阀值 当前值:99 % 阀值:90 %");
	           j2x.BuildEventXMLDoc(eventList);   
	       } catch (Exception e) {   
	           e.printStackTrace();   
	       }   
	    }  


	public void sendAlarm(EventList eventList,String userids) {
		// TODO Auto-generated method stub
		SysLogger.info("==============发送邮件告警==================");
		AlertEmail em = null;
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
			em = (AlertEmail)list.get(0);
		}
		if(em == null)return ;
		
		//String userids = alarmWayDetail.getUserIds();
		UserDao userDao = new UserDao();
		List userList = new ArrayList();
		try{
			userList = userDao.findbyIDs(userids);
		}catch(Exception e){
			
		}finally{
			userDao.close();
		}
		if(userList != null && userList.size()>0){
			for(int i =0;i<userList.size();i++)
			{
				User vo = (User)userList.get(i);
				String mailAddressOfReceiver = vo.getEmail();
				try{
					sendEmail(em.getMailAddress(),mailAddressOfReceiver,eventList.getContent());
				}catch(Exception e){
					e.printStackTrace();
				}
				//sendEmail(em.getMailAddress(),mailAddressOfReceiver,"temp/network_and_host_report.xls");
			}
		}
		
		
		
	}
	
	private void sendEmail(String fromAddress,String mailAddressOfReceiver,String body)
	{
		SendMailManager mailManager = new SendMailManager();
		//String fileName = ResourceCenter.getInstance().getSysPath() + fileNa;
		mailManager.SendMailNoFile(fromAddress,mailAddressOfReceiver, body);
	}

}
