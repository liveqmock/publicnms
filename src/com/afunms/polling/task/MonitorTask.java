/*
 * Created on 2005-3-28
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import org.apache.log4j.Logger;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class MonitorTask extends TimerTask {
	private int interval = 1000;
	private Context ctx;
	private SessionFactory sessionFactory;
	private Logger logger=Logger.getLogger(MonitorTask.class);

	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	protected Date recentlyStartTime;//最近启动的日期
	
	
	public Date getRecentlyStartTime() {
		return recentlyStartTime;
	}

	public SimpleDateFormat getSdf() {
		return sdf;
	}

	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}

	public void setRecentlyStartTime(Date recentlyStartTime) {
		this.recentlyStartTime = recentlyStartTime;
	}
	/**
	 * 
	 */
	public MonitorTask() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public abstract void run();
	public void destroy() {
//		
////		TODO Auto-generated method stub
//				 if(ctx!=null){
//					 try{
//						 ctx.unbind("HibernateSessionFactory");
//					 }
//					 catch(NamingException e){
//						 e.printStackTrace();
//						 throw new RuntimeException("unbind sessionFactory from JNDI Exception "+e.getMessage());
//					 }
//				 }
//				 if(sessionFactory!=null){
//						 try{
//							 sessionFactory.close();
//						 }
//						 catch(HibernateException e){
//							 throw new RuntimeException("Close SessionFactory Exception "+ e.getMessage());
//						 }
//				 }
//		// Just puts "destroy" string in log
//		// Put your code here
	}
	public boolean isGetSessionFactory(){
		boolean b=false;
		try{
	
			int i=0;

			Context inttex = new InitialContext();
			try {
			//从JNDI中取得SessionFactory的实例，如果出错返回false
				SessionFactory sessionFactory =
				(SessionFactory) inttex.lookup("HibernateSessionFactory");
				b=true;
			} 
			catch (NamingException e) {
				//e.printStackTrace();
				//System.out.println("get jndi name failed");
				b=false;
				return b;
			}
		}		
		catch (Exception e) {
			e.printStackTrace();
		}
		return b;
}

//public void initJNDI() {
////	TODO Auto-generated method stub
//			 try{
//				 System.out.println("----333-------------JNDI bind success-----====-----");
//				 sessionFactory=new Configuration().configure().buildSessionFactory();
//				 System.out.println("-----------------JNDI bind success-----====-----");
//			 }
//			 catch(HibernateException e){
//				 e.printStackTrace();
//				 throw new RuntimeException("Can not build SessionFactory "+e.getMessage());}
//			 try{
//				 ctx=new InitialContext();
//				 ctx.bind("HibernateSessionFactory",sessionFactory);
//		
//				 System.out.println("-----------------JNDI bind success----------");
//		
//				 //this.info("JNDI bind success");
//			 }catch(NamingException e){
//				 //logger.error("JNDI bind failed"+e);
//				 e.printStackTrace();
//				 throw new RuntimeException("bind sessionFactory to JNDI Exception "+e.getMessage());}
//				 catch(Exception e){
//					 e.printStackTrace();
//				 }
//	// Put your code here
//}

	public void setInterval(float d,String t)
	   {
		  if(t.equals("d"))
			 interval =(int) d*24*60*60*1000; //天数
		  else if(t.equals("h"))
			 interval =(int) d*60*60*1000;    //小时
		  else if(t.equals("m"))
			 interval = (int)d*60*1000;       //分钟
		else if(t.equals("s"))
					 interval =(int) d*1000;       //秒
	   }
	

	/**
	 * @return
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * @param i
	 */
	

	/**
	 * @return
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @param logger
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

}
