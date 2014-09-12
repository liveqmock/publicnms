package com.afunms.topology.manage;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;

public class ConfigureManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		if (action.equals("configtree")) {
			return "/configTree/panel.jsp";
		}
		if (action.equals("treeData")) {
			String DID;
			DID = request.getParameter("DID").toString();
			request.setAttribute("DID", DID);
			// System.out.println("================="+DID);
			return "/configTree/treeData.jsp";
		}
		if (action.equals("iframe")) {
			String DID;
			DID = request.getParameter("DID").toString();
			request.setAttribute("DID", DID);
			// System.out.println("================="+DID);
			return "/configTree/iframe.jsp";
		}
		if (action.equals("iframeData")) {
			String DID;
			DID = request.getParameter("DID").toString();
			request.setAttribute("DID", DID);
			return "/configTree/iframeData.jsp";
		}
		if (action.equals("homepage")) {
			return "/configTree/homepage.jsp";
		}
		if (action.equals("gridData")) {
			String start = request.getParameter("start");
			String limit = request.getParameter("limit");
			request.setAttribute("start", start);
			request.setAttribute("limit", limit);
			return "/configTree/gridData.jsp";
		}
		if (action.equals("add_father")) {
			return "/configTree/add_father.jsp";
		}
		if (action.equals("add_fatherData")) {
			String text = request.getParameter("text");
			String descn = request.getParameter("descn");
			request.setAttribute("text", text);
			request.setAttribute("descn", descn);
			return "/configTree/add_fatherData.jsp";
		}
		if (action.equals("treeDataList")) {
			return "/configTree/treeDataList.jsp";
		}
		if (action.equals("add_childnode")) {
			return "/configTree/add_childnode.jsp";
		}
		if (action.equals("add_childnodeData")) {
			String text = request.getParameter("text");
			String descn = request.getParameter("descn");
			String fathernode = request.getParameter("fatherid");
			request.setAttribute("text", text);
			request.setAttribute("descn", descn);
			request.setAttribute("fathernode", fathernode);
			return "/configTree/add_childnodeData.jsp";
		}
		if (action.equals("delenode")) {
			return "/configTree/delenode.jsp";
		}
		if (action.equals("delenodeData")) {
			String nodeid = request.getParameter("nodeid");
			request.setAttribute("nodeid", nodeid);
			return "/configTree/delenodeData.jsp";
		}
		if (action.equals("modifynode")) {
			String nodeid;
			nodeid = request.getParameter("nodeid").toString();
			request.setAttribute("nodeid", nodeid);
			// System.out.println("======================"+nodeid);
			return "/configTree/modifynode.jsp";
		}
		if (action.equals("modifynodeData")) {

			String text = request.getParameter("text");
			String descn = request.getParameter("descn");
			String nodeid = request.getParameter("nodeid");
			request.setAttribute("text", text);
			request.setAttribute("descn", descn);
			request.setAttribute("nodeid", nodeid);
			System.out.println("===========s===========" + nodeid);
			return "/configTree/modifynodeData.jsp";
		}
		return null;
	}
}
