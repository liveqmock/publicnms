package com.gatherdb;


import java.util.TimerTask;
import org.apache.log4j.Logger;
import com.gatherdb.GathersqlListManager;






/**
 * 
 * 
 * 定时把队列里的数据插入数据库
 * @author Administrator
 *
 */
public class GathersqlRun extends TimerTask{
	
 
	public  Logger logger = Logger.getLogger(GathersqlRun.class);
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//System.out.println("===######=====开始定时数据入库=="+GathersqlListManager.queue.size());
//		if(GathersqlListManager.queue.size()>0)
//		{
//		 DBManager pollmg = new DBManager();// 数据库管理对象
//		 pollmg.excuteBatchSql(GathersqlListManager.queue);
//		 pollmg.close();
//		}
		
		if(!GathersqlListManager.idbstatus)
		{
		   //System.out.println(GathersqlListManager.idbstatus);
		   GathersqlListManager.Addsql("DHCC-DB");
		}
		//System.out.println("=idbstatus=="+GathersqlListManager.idbstatus);
		
		//System.out.println("===######=====数据定时入库结束==");
	}

}
