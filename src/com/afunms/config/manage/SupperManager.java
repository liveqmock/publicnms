package com.afunms.config.manage;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;

public class SupperManager extends BaseManager implements ManagerInterface {

	public String list() {
		SupperDao dao = new SupperDao();
		setTarget("/config/supper/list.jsp");
		return list(dao);
	}

	private String update() {
		Supper vo = new Supper();
		vo.setSu_id(getParaValue("id"));
		vo.setSu_name(getParaValue("su_name"));
		vo.setSu_class(getParaValue("su_class"));
		vo.setSu_area(getParaValue("su_area"));
		vo.setSu_person(getParaValue("su_person"));
		vo.setSu_email(getParaValue("su_email"));
		vo.setSu_phone(getParaValue("su_phone"));
		vo.setSu_address(getParaValue("su_address"));
		vo.setSu_desc(getParaValue("su_desc"));
		vo.setSu_dept(getParaValue("su_dept"));
		vo.setSu_url(getParaValue("su_url"));
		SupperDao dao = new SupperDao();
		String target = null;
		if(dao.update(vo))
	    	   target = "/supper.do?action=list";
		return target;
	}
	
	private String save() {
		Supper vo = new Supper();
		vo.setSu_name(getParaValue("su_name"));
		vo.setSu_class(getParaValue("su_class"));
		vo.setSu_area(getParaValue("su_area"));
		vo.setSu_person(getParaValue("su_person"));
		vo.setSu_email(getParaValue("su_email"));
		vo.setSu_phone(getParaValue("su_phone"));
		vo.setSu_address(getParaValue("su_address"));
		vo.setSu_desc(getParaValue("su_desc"));
		vo.setSu_dept(getParaValue("su_dept"));
		vo.setSu_url(getParaValue("su_url"));
		SupperDao dao = new SupperDao();
		int result = dao.save(vo);

		String target = null;
		if (result == 0) {
			target = null;
			setErrorCode(ErrorMessage.USER_EXIST);
		} else if (result == 1)
			target = "/supper.do?action=list&jp=1";
		else
			target = null;
		return target;
	}

	public String execute(String action) {
		if (action.equals("list"))
			return list();

		if (action.equals("add"))
			return save();

		if (action.equals("delete")) {
			DaoInterface dao = new SupperDao();
			setTarget("/supper.do?action=list&jp=1");
			return delete(dao);
		}

		if (action.equals("read")) {
			DaoInterface dao = new SupperDao();
			setTarget("/config/supper/read.jsp");
			return readyEdit(dao);
		}

		if (action.equals("ready_add")) {
			return "/config/supper/add.jsp";
		}
		
		if (action.equals("ready_edit")) {
			DaoInterface dao = new SupperDao();
		    setTarget("/config/supper/edit.jsp");
	        return readyEdit(dao);
		}
		if (action.equals("update")) {
			 return update();
		}
		return null;
	}

}
