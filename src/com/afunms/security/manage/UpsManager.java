package com.afunms.security.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.event.dao.EventListDao;
import com.afunms.security.dao.UpsDao;
import com.afunms.system.model.User;
import com.afunms.topology.model.HostNode;

public class UpsManager extends BaseManager implements

ManagerInterface {

	public String execute(String action) {
		if (action.equals("list"))
			return list();
		if ("toDetail".equals(action))
			return toDetail();
		if ("current".equals(action))
			return toCurrent();
		if ("event".equals(action))
			return event();
		if ("tosysinfo".equals(action))
			return toSysinfo();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String toSysinfo() {
		String id = getParaValue("id");
		UpsDao UpsDao = new UpsDao();
		HostNode upsvo = null;
		try {
			upsvo = (HostNode) UpsDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			UpsDao.close();
		}
		request.setAttribute("vo", upsvo);
		return "/ups/ups_sysinfo.jsp";
	}

	private String list() {

		String category = getParaValue("category");

		User current_user = (User) session.getAttribute

		(SessionConstant.CURRENT_USER);

		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids =

				current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int

					i = 0; i < bids.length; i++) {
						if (bids[i].trim

						().length() > 0) {
							if

							(_flag == 0) {

								s.append(" and ( bid like '%," + bids[i].trim()
										+ ",%' ");

								_flag = 1;
							} else {

								// flag = 1;

								s.append(" or bid like '%," + bids[i].trim()
										+ ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		request.setAttribute("actionlist", "list");
		setTarget("/ups/list.jsp");
		UpsDao upsdao = new UpsDao();
		if (category != null) {
			if (category.equals("emerson")) {
				if (current_user.getRole() == 0) {
					return list(upsdao,
							"where 1=1 and ostype=48 and category=17");
				} else {
					return list(upsdao,
							"where 1=1 and ostype=48 and category=17 " + s);
				}
			}

		}
		if (current_user.getRole() == 0) {
			return list(upsdao, "where 1=1 and category=17");
		} else {
			return list(upsdao, "where 1=1 and category=17 " + s);
		}
	}

	private String toDetail() {

		String id = getParaValue("id");
		UpsDao UpsDao = new UpsDao();
		HostNode upsvo = null;
		try {
			upsvo = (HostNode) UpsDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			UpsDao.close();
		}
		request.setAttribute("vo", upsvo);
		return "/ups/ups_detail.jsp";
	}

	private String toCurrent() {

		String id = getParaValue("id");
		UpsDao UpsDao = new UpsDao();
		HostNode upsvo = null;
		try {
			upsvo = (HostNode) UpsDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			UpsDao.close();
		}
		request.setAttribute("vo", upsvo);
		return "/ups/ups_current_detail.jsp";
	}

	private String event() {

		String id = getParaValue("id");
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		List list = new ArrayList();
		try {
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
			if (b_time == null) {
				SimpleDateFormat sdf = new

				SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new

				SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventListDao dao = new EventListDao();
		try {
			String starttime2 = b_time + " 00:00:00";
			String totime2 = t_time + " 23:59:59";
			User vo = (User) session.getAttribute

			(SessionConstant.CURRENT_USER);
			list = dao.getQuery

			(starttime2, totime2, status + "", level1 + "", vo.getBusinessids

			(), Integer.parseInt(id));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dao.close();
		}
		UpsDao UpsDao = new UpsDao();
		HostNode vo = null;
		try {
			vo = (HostNode) UpsDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			UpsDao.close();
		}
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("vo", vo);
		return "/ups/ups_event.jsp";
	}
}
