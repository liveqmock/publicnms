/**
 * <p>Description:base monitor</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-28
 */

package com.afunms.monitor.executor.base;

import java.util.List;

import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.monitor.item.base.MonitorResult;
import com.afunms.inform.model.Alarm;
import com.afunms.common.util.SysUtil;
import com.afunms.polling.base.*;

public class BaseMonitor
{         
	public BaseMonitor()
    {
    }
	
	public void analyseData(Node node,MonitoredItem item)
	{		
		item.setAlarm(false);
		if(!item.isEnabled()) return; //不启用阀值的不分析,
				
		if(item.getResultType()==1)  //单值
		{							
			if(item.getSingleResult()<0) return; //不正常的数据不分析,也不入库	
			double result = item.getSingleResult();
			if(item.getCompare()==1&&result>item.getThreshold())     //>
			   item.setViolateTimes(item.getViolateTimes() + 1);
			else if(item.getCompare()==2&&result==item.getThreshold()) //=
			   item.setViolateTimes(item.getViolateTimes() + 1);		
			else if(item.getCompare()==3&&result<item.getThreshold()) //<
			   item.setViolateTimes(item.getViolateTimes() + 1);
			else
			   item.setViolateTimes(0);			

			if(item.getViolateTimes() >= item.getUpperTimes())
			{
				Alarm vo = new Alarm();
				vo.setIpAddress(node.getIpAddress());
				vo.setLevel(item.getAlarmLevel());
				vo.setMessage(item.getAlarmInfo());
				vo.setLogTime(SysUtil.getCurrentTime());
				vo.setCategory(node.getCategory());
				node.getAlarmMessage().add(vo);	
				item.setAlarm(true);
			} 
		}
		else //多值
		{
			List resultList = item.getMultiResults();
			if(resultList==null||resultList.size()==0) return;
			
			int violateTimes = 0;
			for(int i=0;i<resultList.size();i++)
			{
				MonitorResult mr = (MonitorResult)resultList.get(i);

				if(mr.getPercentage() < 0 || mr.getValue() < 0 ) continue; //不正常的数据不分析,也不入库				
				if(item.getCompare()==1 && item.getCompareType()==1 && mr.getPercentage()>item.getThreshold()) 	
				   item.setViolateTimes(item.getViolateTimes() + 1);
				else if(item.getCompare()==1 && item.getCompareType()==2 && mr.getValue()>item.getThreshold())
				   item.setViolateTimes(item.getViolateTimes() + 1);	
			    else if(item.getCompare()==2 && item.getCompareType()==1 && mr.getPercentage()==item.getThreshold())
				   item.setViolateTimes(item.getViolateTimes() + 1);		
			    else if(item.getCompare()==2 && item.getCompareType()==2 && mr.getValue()==item.getThreshold())
				   item.setViolateTimes(item.getViolateTimes() + 1);						
			    else if(item.getCompare()==3 && item.getCompareType()==1 && mr.getPercentage()<item.getThreshold())
				   item.setViolateTimes(item.getViolateTimes() + 1);
			    else if(item.getCompare()==3 && item.getCompareType()==1 && mr.getValue()<item.getThreshold())
				   item.setViolateTimes(item.getViolateTimes() + 1);
					
				if(item.getViolateTimes() >= item.getUpperTimes())
				   node.setStatus(item.getAlarmLevel()); //状态为报警级别
				
				if(item.getViolateTimes() >= item.getUpperTimes()&&violateTimes==0) //同样的报警信息不要多次
				{
					violateTimes ++;
					Alarm vo = new Alarm();
					vo.setIpAddress(node.getIpAddress());
					vo.setLevel(item.getAlarmLevel());
					vo.setMessage(item.getAlarmInfo());
					vo.setLogTime(SysUtil.getCurrentTime());
					vo.setCategory(node.getCategory());
					node.getAlarmMessage().add(vo);	
					item.setAlarm(true);
				} 			
			}//end_for
			if(violateTimes==0) item.setViolateTimes(0);	
		}//end_else	
	}	
}