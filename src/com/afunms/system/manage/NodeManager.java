package com.afunms.system.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.system.dao.NodeDao;
import com.afunms.system.model.Node;

public class NodeManager  extends AjaxBaseManager implements AjaxManagerInterface {

	public void execute(String action) {
		if (action.equals("list")) {
			list();
		}
	}
	
	private void list() {
		List<Node> list = new NodeDao().getNodes();
		Map<String, Node> map = new HashMap<String, Node>();
		for(Node node:list) {
			map.put(node.getID(), node);
		}
		Set<String> keyset = map.keySet();
		Iterator<String> itr = keyset.iterator();
		Node node = null;
		Node pnode = null;
		Node root = null;
		String pid = "";
		while(itr.hasNext()) {
			node = map.get(itr.next());
			pid = node.getPid();
			if(!"0".equals(pid)) {
				pnode = map.get(pid);
				node.setParent(pnode);
				if(null==pnode.getChildren()) {
					pnode.setChildren(new ArrayList<Node>());
				}
				pnode.getChildren().add(node);
			}else {
				root = node;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"title\":");
		sb.append("\"");
		sb.append(root.getName());
		sb.append("\"");
		sb.append(",\"isFolder\":true,\"key\":");
		sb.append("\"");
		sb.append(root.getID());
		sb.append("\"");
		sb.append(",\"tooltip\":");
		sb.append("\"");
		sb.append(root.getDesc());
		sb.append("\"");
		sb.append(",\"expand\":true");
		if(null!=root.getChildren()&&root.getChildren().size()>0) {
			sb.append(",\"children\":[");
			sb.append(getChildren(root));
			sb.append("]");
		}
		sb.append("}");
		String str = sb.toString().replaceAll("\\[,", "\\[");
		response.setContentType("application/json");
		out.print(str);
		out.flush();
	}
	
	private String getChildren(Node node) {
		StringBuilder sb = new StringBuilder();
		for(Node n :node.getChildren()) {
			sb.append(",{");
			sb.append("\"title\":");
			sb.append("\"");
			sb.append(n.getName());
			sb.append("\"");
			sb.append(",\"tooltip\":");
			sb.append("\"");
			sb.append(n.getDesc());
			sb.append("\"");
			sb.append(",\"key\":");
			sb.append("\"");
			sb.append(n.getID());
			sb.append("\"");
			if(null!=n.getChildren()&&n.getChildren().size()>0) {
				sb.append(",\"isFolder\":true,\"expand\":false");
				sb.append(",\"children\":[");
				sb.append(getChildren(n));
				sb.append("]");
			}
			sb.append("}");
		}
		return sb.toString();
	}
	
	
}
