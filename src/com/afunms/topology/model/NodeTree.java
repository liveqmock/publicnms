package com.afunms.topology.model;

import java.util.ArrayList;
import java.util.List;

public class NodeTree {

	
	String name;
	
	List<NodeModel> nodeList=new ArrayList<NodeModel>();
   List<LinkModel> linkList=new ArrayList<LinkModel>();
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NodeModel> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<NodeModel> nodeList) {
		this.nodeList = nodeList;
	}

	public List<LinkModel> getLinkList() {
		return linkList;
	}

	public void setLinkList(List<LinkModel> linkList) {
		this.linkList = linkList;
	}


}
