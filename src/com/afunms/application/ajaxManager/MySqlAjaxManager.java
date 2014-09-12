package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.SysUtil;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;

public class MySqlAjaxManager  extends AjaxBaseManager implements AjaxManagerInterface
{

	public void execute(String action) 
	{
		if(action.equals("ajaxUpdate_availability"))
		{
			ajaxUpdate_availability();
		}
	}
	private void ajaxUpdate_availability()
	{
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try{
			vo = (DBVo)dao.findByID(getParaValue("id"));
		 }catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());
		String newip=SysUtil.doip(vo.getIpAddress());						
	
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		Hashtable ConnectUtilizationhash = new Hashtable();
		I_HostCollectData hostmanager=new HostCollectDataManager();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","MYPing","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		String pingconavg ="";
		if (ConnectUtilizationhash.get("avgpingcon")!=null)
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
		if(pingconavg != null){
			pingconavg = pingconavg.replace("%", "");
		}
	}
}
