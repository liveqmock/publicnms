/**
 * <p>Description:collect jvm garbage</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-06
 */

package com.afunms.polling.task;

import java.util.List;


import com.afunms.common.util.SysUtil;
import com.afunms.polling.*;
import com.afunms.polling.base.*;
import com.afunms.polling.node.*;
import com.afunms.inform.dao.PerformanceDao;

public class GarbageCollect extends BaseTask
{
    public GarbageCollect()
    {
    }

    public void executeTask()
    {
    	
		if(!PollingEngine.getInstance().getCurrentDate().equals(SysUtil.getCurrentDate())) //过了一天		
		{
			 PollingEngine.getInstance().setCurrentDate(SysUtil.getCurrentDate());
			 List nodeList = PollingEngine.getInstance().getNodeList();
			 for(int i=0;i<nodeList.size();i++)
			 {
				 Node node = (Host)nodeList.get(i);
				 node.setFailTimes(0);  //failTimes和normalTimes用于计算今天的可用率
				 node.setNormalTimes(0);				 
			 }				 
			 PerformanceDao dao = new PerformanceDao();
			 try{
				 dao.deleteData();
			 }catch(Exception e){
				 e.printStackTrace();
			 }finally{
				 dao.close();
			 }
		}
    }
}
