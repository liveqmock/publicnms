package com.gathertask;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.Hashtable;
import com.gathertask.dao.Taskdao;
import com.gatherdb.nmsmemorydate;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.gathertask.TaskManager;



/**
 * 
 * 维护采集任务
 * 5分钟检查一次采集任务，检查已经在跑的任务与被停止的
 * @author konglq
 *
 */
public class MaintainTask extends TimerTask{

	@Override
	public void run() {
		// TODO Auto-generated method stub
//		System.out.println("================定时开始维护定时任务====================");
		Taskdao taskdao=new Taskdao();
		
		Hashtable nlist=taskdao.GetRunTaskList();
		TaskManager tkmanager=new TaskManager();
		
		
		//开始对内存队列的信息进行比较
		if(null!= nlist && nlist.size()>0)
		{
			//数据库的列表中比较内存列表
			
			NodeGatherIndicators gathertask;
			NodeGatherIndicators gathertask2;
			//Iterator it= nlist.entrySet().iterator();
			
			Enumeration it1=nlist.elements(); 
			 
			  
			
			while(it1.hasMoreElements()){
				gathertask=(NodeGatherIndicators) it1.nextElement();
			    
				//内存队列中包含有对应的定时任务
				if(nmsmemorydate.RunGatherLinst.containsKey(gathertask.getId()+""))
				{
					
					//System.out.println("==============1==========================");
					String itime=gathertask.getPoll_interval();
					String itype=gathertask.getInterval_unit();
					gathertask2=(NodeGatherIndicators) nmsmemorydate.RunGatherLinst.get(gathertask.getId()+"");
					//System.out.println("=====1===="+itime);
					//System.out.println("=====1==type=="+itype);
					//System.out.println("====2====="+gathertask2.getPoll_interval());
					//System.out.println("=====2===="+gathertask2.getInterval_unit());
				
					
					if(itime.equals(gathertask2.getPoll_interval()) && itype.equals(gathertask2.getInterval_unit()))
					{//重新建立一个定时任务
						//tkmanager.createOneTask(gathertask);
						//System.out.println("==========正常运行任务============"+gathertask.getId());
					}else 
					  {
						//注销任务
//						System.out.println("==任务配置被修改重启任务============="+gathertask.getId()+"");
						//tkmanager.cancelTask(gathertask.getId()+"");
						tkmanager.createOneTask(gathertask);
					  }
					
				}else
				 {//内存中没有对应的定时任务
//					System.out.println("===========启动新定时任务============="+gathertask.getId());
					tkmanager.createOneTask(gathertask);
				 }
			}
			
			
//			 System.out.println("=====开始迭代内存列表====");
			
		 //迭代内存列表查找当前定时的任务
			 if(nmsmemorydate.RunGatherLinst.size()>0)
			 {
			 it1=nmsmemorydate.RunGatherLinst.elements();
			 
			 while(it1.hasMoreElements()){
					gathertask2=(NodeGatherIndicators) it1.nextElement();
					//内存队列中包含有对应的定时任务
					
					//System.out.println("=======%%%%===="+nlist.size());
					if(!nlist.containsKey(gathertask2.getId()+""))
					{
//					  System.out.println("=====任务已经停止===="+gathertask2.getId());
					  tkmanager.cancelTask(gathertask2.getId()+"");

					}
			
			
		       }
			 }
	  }else
	    {
		  tkmanager.canceAlllTask();
	    }
		
		
		
//		System.out.println("================维护定时任务结束==========任务数======="+nmsmemorydate.TaskList.size());
	
	
	
	}
	
	
	
	
}
	
	
	  

