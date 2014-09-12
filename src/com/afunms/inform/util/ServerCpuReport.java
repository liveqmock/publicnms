/**
 * <p>Description:server cpu stat report</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2006-11-20
 */

package com.afunms.inform.util;
import java.text.DateFormat;
import java.util.Date;

import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;

import com.afunms.polling.node.Host;
import com.afunms.polling.PollingEngine;
import com.afunms.inform.dao.NodeDataDao;
import com.afunms.initialize.ResourceCenter;
import com.afunms.report.base.ImplementorReport;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.report.jfree.JFreeChartBrother;

public class ServerCpuReport extends ImplementorReport
{
   private int nodeId;
   private String moid;
   
   public ServerCpuReport()
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

   //创建表格
   public void createReport()
   {
	  Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeId);
      head = host.getAlias() + "  CPU利用率报表";
   	  note = "数据来源：东华网管软件";
      
      NodeDataDao dao = new NodeDataDao();
      String[][] tmpTable = dao.multiStat(timeStamp,nodeId,moid);
      
      if(tmpTable == null)
	  {
    	  table = null;
	      chart = null;
	      return;
	  }
      int row = tmpTable.length;
      int col = tmpTable[0].length;
      tableHead = new String[row+2];
      colWidth = new int[row+2];
      colWidth[0] = 2;
      colWidth[1] = 5;
      tableHead[0] = "序号";
      tableHead[1] = "时间";
      table = new String[col-1][tableHead.length];
      for(int i=0; i<row; i++)
      {
    	  tableHead[i+2] = "CPU("+tmpTable[i][24]+")的值(%)";
    	  colWidth[i+2] = 3;
      }
      
      for(int j=0; j<col-1; j++)
      {
    	  table[j][0] = String.valueOf(j+1);
    	  table[j][1] = j + ":00";
    	  for(int k=0; k<row;k++)
    	  {
    		  table[j][k+2] = tmpTable[k][j];
    	  }
      }

      //--------画图---------
      TimeSeries[] series = new TimeSeries[row]; 
	  TimeSeries series1 = null;
	  Date curDate = new Date();
	  try
	  {
	      curDate = DateFormat.getDateInstance().parse(timeStamp);
	  }
	  catch(Exception e){}
	  
	  String tmpData = "";
	  for(int j=0; j<row; j++)
	  {
		  series1 = new TimeSeries("CPU("+(j+1)+")",Hour.class);
		  for(int i=0;i<col-1;i++)
		  {
			  tmpData = tmpTable[j][i];
			  if(tmpData=="")
				  tmpData = "0";
			  series1.add(new Hour(i,new Day(curDate)), Double.parseDouble(tmpData));
		  }
		  series[j] = series1;
	  }	
	  chartKey = ChartCreator.createMultiTimeSeriesChart(series,"HOUR","X-时间(h)","Y-CPU利用率(%)",host.getAlias()+"-CPU利用率报表",500,350);
	  chart = (JFreeChartBrother)ResourceCenter.getInstance().getChartStorage().get(chartKey);
   }
}
