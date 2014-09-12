package com.gathertask;

import java.util.Hashtable;

import com.afunms.indicators.model.NodeGatherIndicators;


/**
 * 
 * 定时任务线程接口
 * @author konglq
 *
 */
public interface BaskRunerInterface {
	/**
	 * 
	 * 所有定时任务的方法必须实现此方法
	 * 在此方法里实现所需要做的事情
	 * 
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode);
	
	
	

}
