package com.afunms.topology.manage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ManagerInterface;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.temp.dao.FdbDao;
import com.afunms.temp.dao.FdbTempDao;

public class FdbManager  extends BaseManager implements ManagerInterface
{

	public String execute(String action) 
	{
		// TODO Auto-generated method stub
		if(action.equals("list"))
		{
			return list();
		}
		if(action.equals("downloadfdbreport"))
		{
			return downloadfdbreport();
		}
		if(action.equals("downloadfdbreportall"))
		{
			return downloadfdbreportall();
		}
		if(action.equals("delete"))
		{
			DaoInterface dao = new FdbTempDao();
    	    setTarget("/fdb.do?action=list");
            return delete(dao);
		}
		if(action.equals("deleteall"))
		{
			return deleteall();
		}
		if(action.equals("find"))
		{
			return find();
		}
		if(action.equals("refresh"))
		{
			return refresh();
		}
		return null;
	}
	public String find()
	{
		String key = getParaValue("key");
		String value = getParaValue("value");	 
		FdbDao dao = new FdbDao();
		request.setAttribute("key", key);
		request.setAttribute("value", value);
		setTarget("/config/fdb/findlist.jsp");
	    return list(dao," where "+key+" = '"+value+"' order by ip");
		//setTarget("/config/ipmacbase/findlist.jsp");
	}
	public String deleteall()
	{
		FdbTempDao dao = new FdbTempDao();
		dao.saveOrUpdate("delete from nms_fdb_table");
		dao.close();
		dao = new FdbTempDao();
		setTarget("/config/fdb/list.jsp");
        return list(dao);
	}
	public String downloadfdbreportall()
	{
		FdbTempDao dao = new FdbTempDao();
		List list = dao.findByCriteria("select * from nms_fdb_data_temp");
		dao.close();
		int startRow = 0;
		Hashtable reporthash = new Hashtable();
		if (list!=null) {
			reporthash.put("list", list);
		}
		else {
			list = new ArrayList();
		}
		reporthash.put("startRow", startRow);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),reporthash);
		
		report.createReport_fdb("/temp/fdb_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/alarm/syslog/download.jsp";
	}
	public String downloadfdbreport()
	{
		List list = (List) session.getAttribute("list");
		int startRow = ((Integer)session.getAttribute("startRow")).intValue();
		Hashtable reporthash = new Hashtable();
		if (list!=null) {
			reporthash.put("list", list);
		}
		else {
			list = new ArrayList();
		}
		reporthash.put("startRow", startRow);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),reporthash);
		
		report.createReport_fdb("/temp/fdb_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/alarm/syslog/download.jsp";
	}
	public String list()
	{
		//FdbTempDao dao = new FdbTempDao();
		FdbDao dao = new FdbDao();
		setTarget("/config/fdb/list.jsp");
        return list(dao,"order by ip");
	}
	
	public String refresh()
	{
		FdbTempDao dao = new FdbTempDao();
		dao.refresh();
		dao.close();
		return list();
	}
}
