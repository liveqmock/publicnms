package com.afunms.system.manage;

import java.util.UUID;


import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.system.dao.NodeDao;
import com.afunms.system.model.Node;

public class BpmNodeManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		if (action.equals("update")) {
		   return update();
		}
		else if (action.equals("list")) {
			return "/system/bpmtype/bpmtype.jsp";
		}
		else if (action.equals("save")) {
			return save();
		}
		else if (action.equals("delete")) {
			String key = getParaValue("keytext");
			String target = null;
			if(!"1".equals(key)) {
				NodeDao dao = new NodeDao();
				if (dao.delete(key))
					 target = "/system/bpmtype/bpmtype.jsp";
			}else {
				target = "/system/bpmtype/bpmtype.jsp";
			}
			
			return target;
		}
		return null;
	}
	
   
	
	private String update() {
		Node vo = new Node();
		vo.setID(getParaValue("keytext"));
		vo.setName(getParaValue("nametext"));
		vo.setDesc(getParaValue("desctext"));
		NodeDao dao = new NodeDao();
		String target = null;
		if (dao.update(vo))
		    target = "/system/bpmtype/bpmtype.jsp";
		return target;
	    }
	
	private String save() {
		Node vo = new Node();
		vo.setID(UUID.randomUUID().toString().replaceAll("-", ""));
		vo.setName(getParaValue("nametext"));
		vo.setDesc(getParaValue("desctext"));
		vo.setPid(getParaValue("keytext"));
		NodeDao dao = new NodeDao();
		String target = null;
		if (dao.save(vo))
			 target = "/system/bpmtype/bpmtype.jsp";
		return target;
	    }
	
}
