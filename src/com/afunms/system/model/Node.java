package com.afunms.system.model;

import java.util.List;

import com.afunms.common.base.BaseVo;

public class Node extends BaseVo
{ 
    public String ID;
 
    public String Name;
 
    public String Desc; 
 
    public List<Node> Children; 
 
    public Node Parent;
    
    public String Pid;

	public String getID() {
		return ID;
	}

	public void setID(String id) {
		ID = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}


	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public List<Node> getChildren() {
		return Children;
	}

	public void setChildren(List<Node> children) {
		Children = children;
	}

	public Node getParent() {
		return Parent;
	}

	public void setParent(Node parent) {
		Parent = parent;
	}

	public String getPid() {
		return Pid;
	}

	public void setPid(String pid) {
		Pid = pid;
	}
    
    
    
} 