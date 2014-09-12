/**
 * <p>Description:tomcat jvm stat report</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-20
 */

package com.afunms.application.util;

import com.afunms.polling.node.Tomcat;
import com.afunms.polling.PollingEngine;
import com.afunms.inform.dao.NewDataDao;
import com.afunms.report.base.ImplementorReport;
import com.afunms.report.jfree.ChartCreator;

public class TomcatJvmReport extends ImplementorReport
{
   private int nodeId;
   private String queryDate;
   
   public TomcatJvmReport()
   {
   }
   
   public void setQueryDate(String queryDate)
   {
	   this.queryDate = queryDate;
   }
   
   public void setNodeId(int nodeId)
   {
	   this.nodeId = nodeId;
   }

   //创建表格
   public void createReport()
   {
	   Tomcat host = (Tomcat)PollingEngine.getInstance().getNodeByID(nodeId);
       setHead(host.getAlias() + "  JVM可用内存报表");
   	   setNote("数据来源：东华网管软件");
      
	   NewDataDao dao = new NewDataDao();
	   double[][] dataSet = dao.multiStat(queryDate,nodeId,"051002",true,3);
	   if(dataSet==null)
	   {
		   setTable(null);
		   setChartKey(null);
	       return;
	   }
	   
	   /**
	    * 图与表的col与row正好相反
	    * dao中col指是图的横坐标，row是图的纵坐标
	    */
	   String[] rowKeys = dao.getRowKeys();
	   String[] colKeys = dao.getColKeys();
      
       setTableHead(new String[]{"序号","时间","JVM可用内存"});
       setColWidth(new int[]{2,2,3});
   	   setTable(new String[colKeys.length][tableHead.length]); 
	   for(int i=0;i<colKeys.length;i++)
	   {
	       table[i][0] = String.valueOf(i+1);  //序号
	       table[i][1] = String.valueOf(colKeys[i]);  //时间
	       table[i][2] = String.valueOf(dataSet[0][i]).replace(".0", "");
	   }

       //--------画图---------
       chartKey = ChartCreator.createLineChart(dataSet,rowKeys,colKeys,"时间","可用内存(M)","",650,400);
   }
}
