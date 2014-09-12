package com.gatherdb;


import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.common.util.DBManager;


/**
 * 
 * 采集数据sql内存数据管理对象列表与方法
 * @author konglq
 *
 */
public class GathersqlListManager {
	
	public static  Queue<String> queue = new LinkedList<String>();//定时数据入库
	public static  Queue<String> queue2 = new LinkedList<String>();//定时数据入库
	public static  Hashtable<String,Vector> datatemplist = new Hashtable();
	public static  Hashtable<String,Vector> datatemplist2 = new Hashtable();
	public static Logger logger = Logger.getLogger(GathersqlListManager.class);
	public static boolean qflg=true; //轮询队列状体
	public static boolean idbstatus=false;//是否处于入库状态
	public static boolean datatempflg=true;//轮询队列状体
	public static boolean idbdatatempstatus=false;//是否处于入库状态
	
	//告警数据处理队列
	public static  Queue<String> queue_alarm = new LinkedList<String>();//告警数据定时入库
	public static  Queue<String> queue2_alarm= new LinkedList<String>();//告警数据定时入库
	
	public static boolean qflg_alarm=true; //轮询队列状体
	public static boolean idbstatus_alarm=false;//是否处于入库状态
	public static boolean datatempflg_alarm=true;//轮询队列状体
	public static boolean idbdatatempstatus_alarm=false;//是否处于入库状态
	
	
	/**
	 * 
	 * 把sql放入到内存队列，如果传递参数为DHCC-DB 表示数据入口
	 * @param sql 字符参数有2个方式，一个是sql，一个是表示是入口（DHCC-DB）
	 */
	public  static  void Addsql(String sql){

		
		if(sql.equals("DHCC-DB")){		
			if(qflg==true){
				//System.out.println("=====入库队列1start=======");
				//System.out.println("=**=入库队列1=="+GathersqlListManager.queue.size());	
				if(queue.size()>1){
					qflg=!qflg;
					idbstatus=true;
	     
					DBManager pollmg = new DBManager();// 数据库管理对象
					 try{
						 pollmg.excuteBatchSql(queue);
					 }catch(Exception e){
						 
					 }finally{
						 pollmg.close(); 
					 }		 
					 pollmg=null;
		 
		 
					 idbstatus=false;
					 //System.out.println("=====入库队列1end=======");
				}	 
		 }else if(qflg==false){
			 //System.out.println("=====入库队列2start=======");
			 if(queue2.size()>1){
				 //System.out.println("=**=入库队列2=="+GathersqlListManager.queue2.size());	
				 idbstatus=true;
				 qflg=!qflg;
			 
				 DBManager pollmg = new DBManager();// 数据库管理对象
				 try{
					 pollmg.excuteBatchSql(queue2);
				 }catch(Exception e){
					 
				 }finally{
					 pollmg.close();
				 }
				 
				 pollmg=null;
			 
				 idbstatus=false;
			 
			 }
			// System.out.println("=====入库队列2end=======");
			 
		 }
		 
		}else{
			if(qflg){
				
				synchronized (queue){
					//System.out.println("==放入队列1");	
					queue.offer(sql);
				}
			}else{
				synchronized (queue2){
					//System.out.println("==放入队列2");
					queue2.offer(sql);
				}
			}
		 }
	}
	
	
	
	/**
	 * 数据分离模式sql入口
	 * 分为两种情况，当key为 DHCC-DB 表示调用数据入库接口，当key值为sql的删除语句时
	 * 表示把结果生成sql放入到内存列表中,这样做的目的是为了保证线程的安全和数据的完整性
	 * @param sql
	 */
	public  static void AdddateTempsql(String key,Vector sql)
	{
		if(key.equals("DHCC-DB")){
			 //System.out.println("===临时入库队列="+datatempflg);			
			 if(datatempflg==true){
				 if(datatemplist.size()>0){
					 datatempflg=!datatempflg;
					 idbdatatempstatus=true;
					 //System.out.println("=========================99999999====开始临时数据入库=========="+GathersqlListManager.datatemplist.size());
				
				 if(datatemplist.size()>0)
				 {
					//Vector list=new Vector();
					 Iterator it = datatemplist.keySet().iterator();
					 DBManager dbm=new DBManager();  
					  while (it.hasNext()){   
					       String keys; 
					       
					       
					       keys=(String)it.next();
//					      System.out.println("======*****==key===="+keys);
					       //list= GathersqlListManager.datatemplist.get(keys);
					       if(null!=datatemplist.get(keys))
					       {
					    	   //logger.info(keys);
					    	   dbm.addBatch(keys);
					    	   for(int i=0;i<datatemplist.get(keys).size();i++)
					    	   {
					    		   //logger.info(GathersqlListManager.datatemplist.get(keys).get(i).toString());
					    		   dbm.addBatch((String)datatemplist.get(keys).get(i).toString());
					    	   }
					    	   
					       }
					       //GathersqlListManager.datatemplist.remove(keys);
					       
					   } 
					  try{
						  dbm.executeBatch();
					  }catch(Exception e){
						  
					  }finally{
						  dbm.close();
					  }
					  
					  dbm=null;
				  it=null;
				 }
				  datatemplist.clear();
				  //System.out.println("=999999999====开始临时数据入库结束=========="+GathersqlListManager.datatemplist.size());
	
				  idbdatatempstatus=false;
			 }
			 
			
			
			 }else if(datatempflg==false)
			 { 
				 if(GathersqlListManager.datatemplist2.size()>0)
					{
						//System.out.println("=========================777777====开始临时数据入库=========="+GathersqlListManager.datatemplist.size());
					     datatempflg=!datatempflg;
					     idbdatatempstatus=true;
						 if(GathersqlListManager.datatemplist2.size()>0)
						 {
							//Vector list=new Vector();	
						   Iterator it = GathersqlListManager.datatemplist2.keySet().iterator();  
						   DBManager dbm=new DBManager();
						  while (it.hasNext()){   
						       String keys; 
						       
						       
						       keys=(String)it.next();
//						       System.out.println("======*****==key===="+keys);
						       //list= GathersqlListManager.datatemplist.get(keys);
						       if(null!=GathersqlListManager.datatemplist2.get(keys))
						       {
						    	   //logger.info(keys);
						    	   dbm.addBatch(keys);
						    	   for(int i=0;i<GathersqlListManager.datatemplist2.get(keys).size();i++)
						    	   {
						    		   //logger.info(GathersqlListManager.datatemplist2.get(keys).get(i).toString());
						    		   dbm.addBatch((String)GathersqlListManager.datatemplist2.get(keys).get(i).toString());
						    	   }
						    	   
						       }
						       //GathersqlListManager.datatemplist.remove(keys);
						       
						   } 
						  try{
							  dbm.executeBatch();
						  }catch(Exception e){
							  e.printStackTrace();
						  }finally{
							  dbm.close();
						  }
						  
						  dbm=null;
						  it=null;
						 }
						  GathersqlListManager.datatemplist2.clear();
						  //System.out.println("=777777====开始临时数据入库结束=========="+GathersqlListManager.datatemplist.size());
			
						  idbdatatempstatus=false;
			 }
			
			
			
		}
			 
		}else if(key.startsWith("delete") || key.startsWith("DELETE"))
		 {		
			if(datatempflg){				
			//System.out.println("==放入临时队列1");	
			datatemplist.put(key, sql);	
			}else{
			 //System.out.println("==放入临时队列2");
			 datatemplist2.put(key, sql);	
			}
			
		 }
		
	}
	
	
	
	/**
	 * 
	 * 把sql放入到内存队列，如果传递参数为DHCC-DB 表示数据入口
	 * @param sql 字符参数有2个方式，一个是sql，一个是表示是入口（DHCC-DB）
	 */
	public  static  void Addsql_alarm(String sql)
	{
			
		
//		System.out.println("==Addsql_alarm=="+sql);
		if(sql.equals("DHCC-DB"))
		{
			
		
		 if(qflg_alarm==true)
		 {
		 //System.out.println("=====入库队列1start=======");
	     //System.out.println("=**=入库队列1=="+GathersqlListManager.queue.size());	
		 if(GathersqlListManager.queue_alarm.size()>1)
		 {
	     qflg_alarm=!qflg_alarm;
	     idbstatus_alarm=true;
		 DBManager pollmg = new DBManager();// 数据库管理对象
		 pollmg.excuteBatchSql(GathersqlListManager.queue_alarm);
		 pollmg.close();
		 pollmg=null;
		 idbstatus_alarm=false;
		 //System.out.println("=====入库队列1end=======");
		 }
		
		 
		 
		 }else if(qflg_alarm==false)
		 {
			// System.out.println("=====入库队列2start=======");
			 if(GathersqlListManager.queue2_alarm.size()>1)
			 {
			 //System.out.println("=**=入库队列2=="+GathersqlListManager.queue2.size());	
			 idbstatus_alarm=true;
			 qflg_alarm=!qflg_alarm;
			 DBManager pollmg = new DBManager();// 数据库管理对象
			 pollmg.excuteBatchSql(GathersqlListManager.queue2_alarm);
			 pollmg.close();
			 pollmg=null;
			 
			 idbstatus_alarm=false;
			 
			 }
			 //System.out.println("=====入库队列2end=======");
			 
		 }
		 
		}else
		 {
			
			if(qflg_alarm)
			{
				
				synchronized (queue_alarm){
			//System.out.println("==放入队列1");	
			queue_alarm.offer(sql);
				}
			}else
			{
				synchronized (queue2_alarm){
			 //System.out.println("==放入队列2");
			 queue2_alarm.offer(sql);
			  }
			}
		 }
	}
	
	
	
	
	public static void main(String [] arg)
	{
		
		//GathersqlListManager.Runsql();
		
		
		//gm.StopRunsql();
	}
	

}
