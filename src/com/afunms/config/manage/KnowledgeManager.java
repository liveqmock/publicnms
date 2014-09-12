package com.afunms.config.manage;


import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.KnowledgeDAO;
import com.afunms.config.model.Knowledge;


public class KnowledgeManager extends BaseManager implements ManagerInterface {

	public String list() {
		KnowledgeDAO dao = new KnowledgeDAO();
		setTarget("/config/knowledges/list.jsp");
		return list(dao);
	}

	public String hostfind() {
		String eventid = (String) session.getAttribute("idforknowledge");
		KnowledgeDAO dao = new KnowledgeDAO();
		String attachfiles_event = dao.findforevent(eventid);
		request.setAttribute("attachfiles_event", attachfiles_event);
		return "/config/knowledges/event_show.jsp";
	}

	public String update() {
		Knowledge vo = new Knowledge();
		vo.setId(getParaIntValue("id"));
		vo.setCategory(getParaValue("category"));
		vo.setEntity(getParaValue("entity"));
		vo.setSubentity(getParaValue("subentity"));
		vo.setAttachfiles(getParaValue("attachfiles"));
		vo.setBak(getParaValue("bak"));

		KnowledgeDAO dao = new KnowledgeDAO();
		String target = null;
		if (dao.update(vo))
			target = "/knowledge.do?action=list";
		return target;
	}

	public String add() {

		Knowledge vo = new Knowledge();
		KnowledgeDAO dao = new KnowledgeDAO();
		String fname = (String) session.getAttribute("fname");
		
		vo.setCategory(getParaValue("category"));
		vo.setEntity(getParaValue("entity"));
		vo.setSubentity(getParaValue("subentity"));
		vo.setAttachfiles(fname);
		vo.setBak(getParaValue("bak"));

		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/knowledge.do?action=list";
	}

	private String readyAdd() {
		return "/config/knowledges/add.jsp";
	}

	private String upload() {
		return "/config/knowledges/upload.jsp";
	}

	public String execute(String action) {

		if (action.equals("list")) {
			return list();
		}
		if (action.equals("ready_add")) {
			KnowledgeDAO dao=new KnowledgeDAO();
			String select=dao.selectcontent();
			session.setAttribute("select", select);
			return readyAdd();
		}
		if (action.equals("add")) {
			return add();
		}
		if (action.equals("delete")) {

			DaoInterface dao = new KnowledgeDAO();
			setTarget("/knowledge.do?action=list&jp=1");
			return delete(dao);
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("read")) {
			DaoInterface dao = new KnowledgeDAO();
			setTarget("/config/knowledges/read.jsp");
			return readyEdit(dao);
		}
		if (action.equals("ready_edit")) {
			KnowledgeDAO dao2=new KnowledgeDAO();
			String select=dao2.selectcontent();
			session.setAttribute("select", select);
			DaoInterface dao = new KnowledgeDAO();
			setTarget("/config/knowledges/edit.jsp");
			return readyEdit(dao);
		}
		if (action.equals("upload")) {
			return upload();
		}
		if (action.equals("hostfind")) {
			return hostfind();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

}
