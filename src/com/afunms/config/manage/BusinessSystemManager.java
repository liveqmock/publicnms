package com.afunms.config.manage;

import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.BusinessSystemDao;
import com.afunms.config.model.BusinessSystem;


public class BusinessSystemManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		if (action.equals("list"))
        {
            return list();
        }
		if (action.equals("ready_add")) {
			return readyAdd();
		}
		if (action.equals("add")) {
			return add();
		}
		if (action.equals("delete")) {
			DaoInterface dao = new BusinessSystemDao();
			setTarget("/businesssystem.do?action=list&jp=1");
			return delete(dao);
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("read")) {
			DaoInterface dao = new BusinessSystemDao();
			setTarget("/config/businesssystem/read.jsp");
			return readyEdit(dao);
		}
		if (action.equals("ready_edit")) {
			DaoInterface dao = new BusinessSystemDao();
			setTarget("/config/businesssystem/edit.jsp");
			return readyEdit(dao);
		}
		if(action.equals("find")){
			return find();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	/**
	*根据条件查询
	*/
	public String find(){
		String con1=getParaValue("namechk");
		request.setAttribute("namechk",con1 );
		String con2=getParaValue("contactnamechk");
		request.setAttribute("contactnamechk",con2 );
		String key=getParaValue("wordkey");
		request.setAttribute("wordkey",key );
		BusinessSystemDao dao = new BusinessSystemDao();
		if(key.equals("")){
			StringBuffer sql = new StringBuffer();
			sql.append("where 1 = 1 ");
			if(con1 != null && !"".equals(con1)){
				if(!con1.equals("全部")){
					sql.append("and name = '" + con1 + "' ");
				}				
			}
			if(con2 != null && !"".equals(con2)){
				if(!con2.equals("全部")){
					sql.append("and contactname = '" + con2 + "'");
				}				
			}
			sql.append(";");
			Vector<List> vector = dao.getConditionList();
			List nameList = vector.get(0);
			List contactNameList = vector.get(1);
			request.setAttribute("nameList", nameList);
			request.setAttribute("contactNameList", contactNameList);
			dao = new BusinessSystemDao();
			return list(dao,sql.toString());
		}else{
			Vector<List> vector = dao.getConditionList();
			List nameList = vector.get(0);
			List contactNameList = vector.get(1);
			request.setAttribute("nameList", nameList);
			request.setAttribute("contactNameList", contactNameList);
			dao = new BusinessSystemDao();
			String sqlStr=dao.getByKeySql(key);
			return list(dao,sqlStr);
		}	
	}

	private String update() {
		BusinessSystem vo = new BusinessSystem();
		vo.setId(Integer.parseInt(getParaValue("id")));
		vo.setName(getParaValue("name"));
		vo.setDescr(getParaValue("descr"));
		vo.setContactname(getParaValue("contactname"));
		vo.setContactphone(getParaValue("contactphone"));
		vo.setContactemail(getParaValue("contactemail"));
		BusinessSystemDao dao = new BusinessSystemDao();
		String target = null;
		if(dao.update(vo))
	    	   target = "/businesssystem.do?action=list";
		return target;
	}

	private String add() {
		BusinessSystem vo = new BusinessSystem();
		BusinessSystemDao dao = new BusinessSystemDao();		
		vo.setName(getParaValue("name"));
		vo.setDescr(getParaValue("descr"));
		vo.setContactname(getParaValue("contactname"));
		vo.setContactphone(getParaValue("contactphone"));
		vo.setContactemail(getParaValue("contactemail"));		
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dao.close();
		}
		return "/businesssystem.do?action=list";
	}

	private String readyAdd() {
		return "/config/businesssystem/add.jsp";
	}

	private String list() {
		List list = null;
		BusinessSystemDao dao = new BusinessSystemDao();
		Vector<List> vector = dao.getConditionList();
		List nameList = vector.get(0);
		List contactNameList = vector.get(1);
		request.setAttribute("nameList", nameList);
		request.setAttribute("contactNameList", contactNameList);
		setTarget("/config/businesssystem/list.jsp");
		dao = new BusinessSystemDao();
		return list(dao);
	}
}
