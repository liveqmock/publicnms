/**
 * <p>Description:network performance report,subclass of implementor report</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-18
 */

package com.afunms.inform.util;

import java.util.List;

import com.afunms.report.base.ImplementorReport;
import com.afunms.inform.dao.InformDao;
import com.afunms.inform.model.*;

public class NetworkPerformanceReport extends ImplementorReport
{
	private String orderField; //排序字段
	
	public void setOrderField(String orderField)
	{
	   	this.orderField = orderField;
	}
	
    public void createReport()
    {
    	head = "网络设备性能数据报表";    	
    	note = "数据来源：东华网管软件";    	
    	tableHead = new String[]{"序号","设备名","IP地址","CPU利用率","内存利用率","接口利用率"};    	
    	colWidth = new int[]{2,5,4,3,3,3};
    	
    	InformDao dao = new InformDao();
    	List list = dao.queryNetworkPerformance(timeStamp,orderField);
    	table = new String[list.size()][tableHead.length];
    	for(int i=0;i<list.size();i++)
    	{
    		NetworkPerformance vo = (NetworkPerformance)list.get(i);
 	        table[i][0] = String.valueOf(i+1);  //序号 
	        table[i][1] = vo.getAlias();
		    table[i][2] = vo.getIpAddress();
			table[i][3] = vo.getCpuValue() + "%";
			table[i][4] = vo.getMemValue() + "%";
			table[i][5] = vo.getIfUtil() + "%";
    	}
    }       
}