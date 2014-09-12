/**
 * <p>Description:Gate record report</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project 齐鲁石化
 * @date 2007-01-19
 */

package com.afunms.security.util;

import java.util.List;

import com.afunms.common.util.SysUtil;
import com.afunms.security.model.GateRecord;
import com.afunms.report.base.ImplementorReport;
import com.afunms.security.dao.GateRecordDao;

public class GateRecordReport extends ImplementorReport
{
	private boolean[] itemSwitchs;
	private String startTime;
	private String endTime;
	private String person;
	private String io;
	private String event;
	public GateRecordReport()
    {    	
    }
    
    public void setParameter(boolean[] itemSwitchs,String startTime,String endTime,String person,String io,String event)
    {
    	this.itemSwitchs = itemSwitchs;
    	this.startTime = startTime;
    	this.endTime = endTime;
    	this.person = person;
    	this.io = io;
    	this.event = event;
    }
    
	public void createReport()
    {    	
		setHead("");    	
    	setNote("");   
    	setTimeStamp(SysUtil.getCurrentDate());
    	setTableHead(new String[]{"序号","人员","事件","发生时间","进/出"});    	
    	setColWidth(new int[]{2,5,5,6,4});
    			 
		GateRecordDao dao = new GateRecordDao();
	    List list = dao.combinQuery(itemSwitchs, startTime, endTime,person,io,event);
	    if(list==null) return;
	    
    	table = new String[list.size()][tableHead.length];
    	for(int i=0;i<list.size();i++)
    	{
    		GateRecord vo = (GateRecord)list.get(i);
 	        table[i][0] = String.valueOf(i+1);  //序号 	        
		    table[i][1] = vo.getPerson();
		    table[i][2] = vo.getEvent();
			table[i][3] = vo.getLogTime();
			table[i][4] = vo.getIo();
    	}
    }       
}