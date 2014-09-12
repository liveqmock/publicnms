package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class TreeNode extends BaseVo{
	
	private int id;

	private String name;

	private String text;

	private int fatherId;

	private String tableName;
	
	private String category;
	
	private String nodeTag;
	
	private String url;
	
	private String deceiveNum;		// 该字段不存入数据库 默认为 0 
	
	private String imgUrl;			// 图片 url

	private String isHaveChild;		// 是否还有子节点 1 为 有 0 为否
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getFatherId() {
		return fatherId;
	}

	public void setFatherId(int fatherId) {
		this.fatherId = fatherId;
	}

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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getNodeTag() {
		return nodeTag;
	}

	public void setNodeTag(String nodeTag) {
		this.nodeTag = nodeTag;
	}

	/**
	 * @return the deceiveNum
	 */
	public String getDeceiveNum() {
		return deceiveNum;
	}

	/**
	 * @param deceiveNum the deceiveNum to set
	 */
	public void setDeceiveNum(String deceiveNum) {
		this.deceiveNum = deceiveNum;
	}

	/**
	 * @return the imgUrl
	 */
	public String getImgUrl() {
		return imgUrl;
	}

	/**
	 * @param imgUrl the imgUrl to set
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	/**
	 * @return the isHaveChild
	 */
	public String getIsHaveChild() {
		return isHaveChild;
	}

	/**
	 * @param isHaveChild the isHaveChild to set
	 */
	public void setIsHaveChild(String isHaveChild) {
		this.isHaveChild = isHaveChild;
	}
	
}
