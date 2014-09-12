package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import net.sf.json.JSONObject;

import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.SysUtil;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;

public class DB2AjaxManager  extends AjaxBaseManager implements AjaxManagerInterface 
{

	public void execute(String action) 
	{
		// TODO Auto-generated method stub
		if(action.equals("ajaxUpdate_availability"))
		{
			ajaxUpdate_availability();
		}
	}
	private void ajaxUpdate_availability()
	{
		double avgpingcon = 0;
		DBVo vo = new DBVo();
		String id = getParaValue("id");
		request.setAttribute("id", id);
		DBDao dao = new DBDao();
		try{
			vo = (DBVo)dao.findByID(id);
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
			ConnectUtilizationhash = hostmanager.getCategory(vo.getId()+"","DB2Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		String pingconavg = "";
		if (ConnectUtilizationhash.get("avgpingcon")!=null)
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
		if(pingconavg != null){
			pingconavg = pingconavg.replace("%", "");
		}
		avgpingcon = new Double(pingconavg+"").doubleValue();
		int percent1 = Double.valueOf(avgpingcon).intValue();
		int percent2 = 100-percent1;  
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("percent1", percent1);
		map.put("percent2", percent2);
		//System.out.println("percent1:"+percent1);
		//System.out.println("percent2:"+percent2);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

}
