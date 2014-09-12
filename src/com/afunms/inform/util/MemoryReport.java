/**
 * <p>Description:host memroy report</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project dhcnms
 * @date 2006-10-19
 */

package com.afunms.inform.util;

import java.text.DateFormat;
import java.util.Date;

import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;

import com.afunms.polling.node.Host;
import com.afunms.polling.PollingEngine;
import com.afunms.report.base.ImplementorReport;
import com.afunms.inform.dao.NodeDataDao;
import com.afunms.initialize.ResourceCenter;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.report.jfree.JFreeChartBrother;

public class MemoryReport extends ImplementorReport
{
   private int nodeId;
   private String moid;   
   
   public MemoryReport()
   {	  
   }

   public void setNodeId(int nodeId)
   {
	   this.nodeId = nodeId;
   }

   public void setMoid(String moid)
   {
       this.moid = moid;
   }

   public void createReport()
   {
	    Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeId);
		head = host.getAlias() + "  内存利用率报表";
	   	note = "数据来源：东华网管软件";
	   	tableHead = new String[]{"序号","时间","值(%)"};
	   	colWidth = new int[]{2,5,4};
	    NodeDataDao dao = new NodeDataDao();
	    float[] tmpTable = dao.singleStat(timeStamp,nodeId,moid);
	    if(tmpTable == null)
	    {
	    	table = null;
	    	chart = null;
	    	return;
	    }    	
	   	table = new String[tmpTable.length][tableHead.length];
	   	for(int i=0;i<24;i++)
	   	{
		        table[i][0] = String.valueOf(i+1);  
		        table[i][1] = i + ":00";
			    table[i][2] = String.valueOf(tmpTable[i]);
	   	}
		
		//------画图-------------
	   	TimeSeries[] series = new TimeSeries[1]; 
		TimeSeries series1 = new TimeSeries("内存利用率",Hour.class);
		Date curDate = new Date();
		try
		{
		    curDate = DateFormat.getDateInstance().parse(timeStamp);
		}
		catch(Exception e){}
		
		for(int i=0;i<tmpTable.length;i++)
		{
			series1.add(new Hour(i,new Day(curDate)), tmpTable[i]);
		}
		series[0] = series1;
		
		chartKey = ChartCreator.createMultiTimeSeriesChart(series,"HOUR","X-时间(h)","Y-内存利用率(%)",host.getAlias()+"内存利用率",500,350);
		chart = (JFreeChartBrother)ResourceCenter.getInstance().getChartStorage().get(chartKey);
   }
}
