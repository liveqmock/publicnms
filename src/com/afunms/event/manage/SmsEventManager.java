/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.event.manage;

import com.afunms.common.base.*;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.polling.*;
import com.afunms.polling.node.*;
import com.afunms.system.model.User;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.util.*;
import com.afunms.common.util.DBManager;
import com.afunms.config.model.*;
import com.afunms.config.dao.*;
import com.afunms.event.dao.*;
import com.afunms.event.model.EventList;
import com.afunms.event.model.EventReport;
import com.afunms.common.util.*;

import java.text.SimpleDateFormat;
import java.util.*;

import montnets.SmsDao;

import com.afunms.polling.loader.HostLoader;

public class SmsEventManager extends BaseManager implements ManagerInterface {
   /**
    * sql语句
    * @return
    */
	private String getSQL(){
		int status = 99;
		int level1 = 99;
		int bid = 0;

		String subtype = "";
		String b_time = "";
		String t_time = "";
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");

		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;

		request.setAttribute("status", status);
		request.setAttribute("level1", level1);
		bid = getParaIntValue("businessid");
		request.setAttribute("businessid", bid);
		BusinessDao bdao = new BusinessDao();
		List businesslist = bdao.loadAll();
		request.setAttribute("businesslist", businesslist);
		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		subtype = getParaValue("subtype");
		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";
		String sql = "";
		try {
			User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			StringBuffer s = new StringBuffer();
			s.append("where eventtime>= '" + starttime1 + "' "
					+ "and eventtime<='" + totime1 + "'");
			
			int flag = 0;
			sql = s.toString();
			sql = sql + " order by id desc";

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return sql;
	}
	/**
	 * 查询所有的方法
	 * @return
	 */
	private String list() {
		
		SmsDao dao = new SmsDao();
		setTarget("/alarm/event/sendsmslist.jsp");
		
        return list(dao,getSQL());
	}
	public String execute(String action) {
		// TODO Auto-generated method stub
		if(action.equals("list"))
		{
			return list();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
	



	
}
