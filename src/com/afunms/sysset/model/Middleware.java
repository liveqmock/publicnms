package com.afunms.sysset.model;

import com.afunms.common.base.BaseVo;

public class Middleware extends BaseVo
{
	public Middleware()
	{
	}
	
	
	private int id;
	private String name;
	private String text;
	private int father_id;
	private String table_name;
	private String category;
	private String node_tag;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getFather_id() {
		return father_id;
	}
	public void setFather_id(int father_id) {
		this.father_id = father_id;
	}
	public String getTable_name() 
	{
		if( null==table_name || "null".equals(table_name) || "".equals(table_name) )
			return "";
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public String getCategory() 
	{
		if( null==category || "null".equals(category) || "".equals(category) )
			return "";
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getNode_tag() {
		return node_tag;
	}
	public void setNode_tag(String node_tag) {
		this.node_tag = node_tag;
	}
	
	
}
