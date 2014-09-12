package com.afunms.system.model;

import com.afunms.common.base.BaseVo;


public class Function extends BaseVo{
	private int id;
    private String func_desc;	
    private String ch_desc;
    /**
	 *  level_desc 菜单级别：1,2,3... ;
	 */
    private int level_desc;
    /**
	 *  father_node 父菜单的id;
	 */
    private int father_node;
    /**
	 *  url 为function的链接 ;
	 */
    private String url;
    /**
	 *  img_url 为function的图片链接 ;
	 */
    private String img_url;
    /**
	 *  isCurrentWindow 是否打开新窗口：1为true ; 0为false ;
	 */
    private int isCurrentWindow;
    
    /**
	 *  width 窗口宽度
	 */
    private String width;
    /**
	 *  height 窗口高度
	 */
    private String height;
	/**
	 * 	clientX 窗口位置 左边
	 */
    private String clientX;
    /**
	 *  clientY 窗口位置 上边
	 */
    private String clientY;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the func_desc
	 */
	public String getFunc_desc() {
		return func_desc;
	}
	/**
	 * @param func_desc the func_desc to set
	 */
	public void setFunc_desc(String func_desc) {
		this.func_desc = func_desc;
	}
	/**
	 * @return the ch_desc
	 */
	public String getCh_desc() {
		return ch_desc;
	}
	/**
	 * @param ch_desc the ch_desc to set
	 */
	public void setCh_desc(String ch_desc) {
		this.ch_desc = ch_desc;
	}
	/**
	 * @return the level_desc
	 */
	public int getLevel_desc() {
		return level_desc;
	}
	/**
	 * @param level_desc the level_desc to set
	 */
	public void setLevel_desc(int level_desc) {
		this.level_desc = level_desc;
	}
	/**
	 * @return the father_node
	 */
	public int getFather_node() {
		return father_node;
	}
	/**
	 * @param father_node the father_node to set
	 */
	public void setFather_node(int father_node) {
		this.father_node = father_node;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the img_url
	 */
	public String getImg_url() {
		return img_url;
	}
	/**
	 * @param img_url the img_url to set
	 */
	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}
	/**
	 * @return the isCurrentWindow
	 */
	public int getIsCurrentWindow() {
		return isCurrentWindow;
	}
	/**
	 * @param isCurrentWindow the isCurrentWindow to set
	 */
	public void setIsCurrentWindow(int isCurrentWindow) {
		this.isCurrentWindow = isCurrentWindow;
	}
	/**
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}
	/**
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(String height) {
		this.height = height;
	}
	/**
	 * @return the clientX
	 */
	public String getClientX() {
		return clientX;
	}
	/**
	 * @param clientX the clientX to set
	 */
	public void setClientX(String clientX) {
		this.clientX = clientX;
	}
	/**
	 * @return the clientY
	 */
	public String getClientY() {
		return clientY;
	}
	/**
	 * @param clientY the clientY to set
	 */
	public void setClientY(String clientY) {
		this.clientY = clientY;
	}
	
    
    
    
}
