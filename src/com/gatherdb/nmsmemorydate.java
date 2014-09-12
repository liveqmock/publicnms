package com.gatherdb;

import java.util.Hashtable;
import java.util.Timer;

import com.afunms.indicators.model.NodeGatherIndicators;



/**
 * 此类为静态变量类所有需要在内存中的数据都必须在此类中定义
 * 并且定义的变量需要做详细的注释，例如key是什么，里面保存的是什么类型的变量
 * 
 * 
 * @author konglq
 *
 */
public class nmsmemorydate {
	
	//用来保存定时任务，任务的ID为KEY，VALUE是TIMER对象
	public static Hashtable <String,Timer> TaskList= new Hashtable();
	/** 
	 *运行的采集任务对象列表，key是nodeid，value 为nms_host_node 数据库表
	 *以hashtable的方式保存
	 */	
	
	/**
	 * 
	 * 内存中保持采集任务表的信息
	 * nms_gather_indicators_node
	 * id为key
	 * value 为数据库记录
	 * 
	 */
     public static Hashtable<String,NodeGatherIndicators> RunGatherLinst=new Hashtable();
     
     //定时维护任务标记
     public static boolean MaintainTaskStatus=false;
     //维护任务对象
     public static Timer MaintainTasktimer= null;
     
     //定时入库任务
     public static boolean GathersqlTaskStatus=false;
     //定时入库任务对象
     public static Timer GathersqlTasktimer= null;
     //数据分离模式数据入库
     public static Timer GatherDatatempsqlTasktimer= null;
     
     
     
    
     
}
