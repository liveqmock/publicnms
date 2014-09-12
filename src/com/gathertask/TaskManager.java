/**
 * 
 * 根据XML的TASK信息来建立任务对象
 * 并把任务对象保存在列表TaskList中
 * 
 * 一个建立定时任务对象。
 * 一个是可以从人物列表中把任务对象注销
 */
package com.gathertask;

import java.util.Enumeration;
import java.util.Timer;
import org.apache.log4j.Logger;


import com.afunms.indicators.model.NodeGatherIndicators;
import com.gatherdb.GatherDataAlarmsqlRun;
import com.gatherdb.GatherDatatempsqlRun;
import com.gatherdb.GathersqlRun;
import com.gatherdb.nmsmemorydate;
import java.util.Hashtable;
import com.gathertask.dao.Taskdao;
import com.gathertask.MaintainTask;





public class TaskManager {
	
	private Logger logger = Logger.getLogger(TaskManager.class);
	
	
	
/**
 * 建立所有的采集任务
 * @param taskinterval 任务的采集频率
 * @param taskid 任务的id
 * @param taskname 任务的名称
 * @param tasktype 任务的类型
 * @param tasksubtype
 */
	public  void createOneTask(NodeGatherIndicators nodeGatherIndicators)
	{
		Timer timer=null;
		BaskTask btask=null;
	     
		//根据Hashtable 中的参数来判断来判断启动采集的任务
		if(null!=nmsmemorydate.TaskList && nmsmemorydate.TaskList.size()>0&& nmsmemorydate.TaskList.containsKey(nodeGatherIndicators.getId()))
		{
            //停止原来的timer，列表并且从内存中删除对应的对象
			timer=(Timer)nmsmemorydate.TaskList.get(nodeGatherIndicators.getId());
			timer.cancel();
			nmsmemorydate.TaskList.remove(nodeGatherIndicators.getId());
			nmsmemorydate.RunGatherLinst.remove(nodeGatherIndicators.getId());
		}else
		  {
			//建立定时采集任务
			timer = new Timer();
			btask=new BaskTask();
			btask.setRunclasspath((String)nodeGatherIndicators.getClasspath());//运行类得路径
			btask.setTaskid(nodeGatherIndicators.getId()+"");
			btask.setNodeid((String)nodeGatherIndicators.getNodeid());
			btask.setTaskname(nodeGatherIndicators.getName());
			btask.setRunclasspath(nodeGatherIndicators.getClasspath());
			btask.setGather(nodeGatherIndicators);
			
			long intervaltime=Integer.parseInt(nodeGatherIndicators.getPoll_interval());
			if(nodeGatherIndicators.getInterval_unit().equals("s"))
			{		
				intervaltime=intervaltime*1000;
			}
			if(nodeGatherIndicators.getInterval_unit().equals("m"))
			{				
				intervaltime=intervaltime*1000*60;
			}
			if(nodeGatherIndicators.getInterval_unit().equals("h"))
			{
				intervaltime=intervaltime*1000*60*60;
			}
			if(nodeGatherIndicators.getInterval_unit().equals("d"))
			{
				intervaltime=intervaltime*1000*60*60*24;
			}
			
			long in=0;
			if(nmsmemorydate.TaskList.size()>300)
			{
				in=(nmsmemorydate.TaskList.size()/5)*200;
				
			}else
			{
				in=nmsmemorydate.TaskList.size()*200;
			}
			
			timer.schedule(btask, in, intervaltime);//按分钟执行定时任务
			nmsmemorydate.TaskList.put(nodeGatherIndicators.getId()+"", timer);//把TIMER对象到任务队里
			nmsmemorydate.RunGatherLinst.put(nodeGatherIndicators.getId()+"", nodeGatherIndicators);
		}
		
	}

	

	/**
	 * 建立一个维护进程
     * 5分钟定时检查一次timer是否需要运行，或是定时时间已经改变
	 */
		public void CreateMaintainTask()
		{
			if(!nmsmemorydate.MaintainTaskStatus)
			{
			Timer timer=null;
			MaintainTask btask=null;
			timer = new Timer();
			btask=new MaintainTask();
			timer.schedule(btask, 1000, 1*1000*60);//按分钟执行定时任务
			nmsmemorydate.MaintainTaskStatus=true;//设置标记为启动
			nmsmemorydate.MaintainTasktimer=timer;
			}
				
	   }
		
		
		/**
		 * 数据分离模式入库定时任务
	     * 1分钟入库一次
		 */
			public void CreateDataTempTask()
			{
				//if(null!=nmsmemorydate.GatherDatatempsqlTasktimer)
				//{
				Timer timer=null;
				GatherDatatempsqlRun btask=null;
				timer = new Timer();
				btask=new GatherDatatempsqlRun();
				timer.schedule(btask, 20000, 10*1000);//按分钟执行定时任务
				
				nmsmemorydate.GatherDatatempsqlTasktimer=timer;
				//}
					
		   }
			
			
			/**
			 * 
		     * 垃圾回收
			 */
				public void CreateGCTask()
				{
					//if(null!=nmsmemorydate.GatherDatatempsqlTasktimer)
					//{
					Timer timer=null;
					GcTask btask=null;
					timer = new Timer();
					btask=new GcTask();
					timer.schedule(btask, 20000, 5*1000*60);//按分钟执行定时任务
					
					//nmsmemorydate.GatherDatatempsqlTasktimer=timer;
					//}
						
			   }
			
		
		
		/**
		 * 建立一个维护进程
	     * 5分钟定时检查一次timer是否需要运行，或是定时时间已经改变
		 */
			public void CreateGahterSQLTask()
			{
				if(!nmsmemorydate.GathersqlTaskStatus)
				{
				Timer timer=null;
				GathersqlRun btask=null;
				timer = new Timer();
				btask=new GathersqlRun();
				timer.schedule(btask, 0, 5*1000);//5秒钟入库一次
				nmsmemorydate.GathersqlTaskStatus=true;//设置标记为启动
				nmsmemorydate.GathersqlTasktimer=timer;
				}
					
		   }
			
		

	
	
	/**
	 * 
	 * 根据数据库表的记录建立采集任务
	 * 
	 * 
	 */
    public void createAllTask()
		{
			Timer timer=null;
			
			BaskTask btask=null;
			Taskdao taskdao=new Taskdao();
			
			Hashtable runtask=taskdao.GetRunTaskList();
			
			logger.info("=采集任务个数=="+runtask.size());
			if(null!=runtask)
			{//如果不为空则循环

				 Enumeration allvalue=runtask.elements(); 
				 
				  while(allvalue.hasMoreElements())     
				  {     
					 
					     NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)allvalue.nextElement();     
				     
						//根据Hashtable 中的参数来判断来判断启动采集的任务
						if(null!=nmsmemorydate.TaskList && nmsmemorydate.TaskList.size()>0&& nmsmemorydate.TaskList.containsKey(nodeGatherIndicators.getId()))
						{
				            //停止原来的timer，列表并且从内存中删除对应的对象
							timer=(Timer)nmsmemorydate.TaskList.get(nodeGatherIndicators.getId());
							timer.cancel();
							nmsmemorydate.TaskList.remove(nodeGatherIndicators.getId());
							nmsmemorydate.RunGatherLinst.remove(nodeGatherIndicators.getId());
						}else
						  {//建立定时采集任务
							timer = new Timer();
							btask=new BaskTask();
							btask.setRunclasspath((String)nodeGatherIndicators.getClasspath());//运行类得路径
							btask.setTaskid(nodeGatherIndicators.getId()+"");
							btask.setNodeid((String)nodeGatherIndicators.getNodeid());
							btask.setTaskname(nodeGatherIndicators.getName());
							btask.setRunclasspath(nodeGatherIndicators.getClasspath());
							btask.setGather(nodeGatherIndicators);
							
							//SysLogger.info(nodeGatherIndicators.getClasspath()+"---------"+nodeGatherIndicators.getPoll_interval());
							long intervaltime=Integer.parseInt(nodeGatherIndicators.getPoll_interval());
							if(nodeGatherIndicators.getInterval_unit().equals("s"))
							{		
								intervaltime=intervaltime*1000;
							}
							if(nodeGatherIndicators.getInterval_unit().equals("m"))
							{
								
								intervaltime=intervaltime*1000*60;
							}
							if(nodeGatherIndicators.getInterval_unit().equals("h"))
							{
								intervaltime=intervaltime*1000*60*60;
							}
							if(nodeGatherIndicators.getInterval_unit().equals("d"))
							{
								intervaltime=intervaltime*1000*60*60*24;
							}
							
							long in=0;
							if(nmsmemorydate.TaskList.size()>300)
							{
								in=(nmsmemorydate.TaskList.size()/5)*200;
								
							}else
							{
								in=nmsmemorydate.TaskList.size()*200;
							}
							
							timer.schedule(btask, in+1000, intervaltime);//按分钟执行定时任务
							nmsmemorydate.TaskList.put(nodeGatherIndicators.getId()+"", timer);//把TIMER对象到任务队里
							//SysLogger.info(nodeGatherIndicators.getName()+"==="+nodeGatherIndicators.getClasspath()+"==="+nodeGatherIndicators.getType()+"==="+nodeGatherIndicators.getSubtype()+"==="+nodeGatherIndicators.getPoll_interval()+nodeGatherIndicators.getInterval_unit());
							nmsmemorydate.RunGatherLinst.put(nodeGatherIndicators.getId()+"", nodeGatherIndicators);
						}
					  
					  
				  } 
				 
					
				
					
				}
				
		}

	
	

	/**
	 * 
	 * 根据id把采集任务停止
	 * 
	 * @param id
	 */
    public synchronized void cancelTask(String id)
    {
    	System.out.println("====停止任务=="+id);
    	
    	if(null!=nmsmemorydate.TaskList.get(id) )
    	{
    	((Timer) nmsmemorydate.TaskList.get(id+"")).cancel();//注销该任务
    	nmsmemorydate.TaskList.remove(id+"");
    	nmsmemorydate.RunGatherLinst.remove(id+"");
    	
    	}
    	
    }
	
    
    
	/**
	 * 
	 * 取消所有的采集任务
	 * 
	 * @param id
	 */
    public  void canceAlllTask()
    {
    	if(nmsmemorydate.TaskList.size()>0)
    	{
    	 Enumeration allvalue=nmsmemorydate.TaskList.elements(); 
    	 Enumeration key=nmsmemorydate.TaskList.keys();
		  while(allvalue.hasMoreElements())     
		  {     
		      
    	     //(Timer) allvalue.nextElement(); 
    	      String id=(String)key.nextElement();
    	    if(null!=(Timer) allvalue.nextElement() )
    	    {
    	    ((Timer) nmsmemorydate.TaskList.get(id+"")).cancel();//注销该任务
    	     nmsmemorydate.TaskList.remove(id);
    	     nmsmemorydate.RunGatherLinst.remove(id);
    	    }
    	    
		}
		  
		  nmsmemorydate.TaskList.clone();
		  nmsmemorydate.RunGatherLinst.clone();
    	}
//	  logger.info("======完成注销采集任务=====");
    	
    }
    
	
	
    /**
     * 建立告警数据入库，告警数据删除
     * 
     */
    public void CreateGahterAlarmSQLTask()
    {
 	   
 		//if(null!=nmsmemorydate.GatherDatatempsqlTasktimer)
 		//{
 		Timer timer=null;
 		GatherDataAlarmsqlRun btask=null;
 		timer = new Timer();
 		btask=new GatherDataAlarmsqlRun();
 		timer.schedule(btask, 500, 1000*30);//按分钟执行定时任务
 		
 		//nmsmemorydate.GatherDatatempsqlTasktimer=timer;
 		//}
 			
 	   
 	   
    }
    
 
	
	public static void main(String [] arg)
	{
		System.out.println("----------------------");
		TaskManager manager=new TaskManager();
		
		manager.createAllTask();
		
		System.out.println("++++队列长度="+nmsmemorydate.TaskList.size());
		//manager.canceAlllTask();
		System.out.println("++++队列长度="+nmsmemorydate.TaskList.size());
		
	}
	
	
    
}
