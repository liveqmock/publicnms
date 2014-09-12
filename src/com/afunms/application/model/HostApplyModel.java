package com.afunms.application.model;

import java.io.Serializable;
import com.afunms.common.base.BaseVo;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Oct 9, 2011 2:08:14 PM
 * 类说明 服务器应用模型类
 */
public class HostApplyModel extends BaseVo implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	private int id;

	/**
	 * userId:
	 * <p>用户 id
	 *
	 * @since   v1.01
	 */
	private int userId;

	/**
	 * 服务器的id
	 */
	private int nodeid;
	
	/**
	 * 应用类型
	 */
	private String type;
	
	/**
	 * 应用子类型
	 */
	private String subtype;

	/**
	 * 服务器IP地址
	 */
	private String ipaddres;
	
	/**
	 * 是否显示
	 */
	private boolean isShow;
	
	
	public int getNodeid() {
		return nodeid;
	}

	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}

	public String getIpaddres() {
		return ipaddres;
	}

	public void setIpaddres(String ipaddres) {
		this.ipaddres = ipaddres;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

    /**
     * getUserId:
     * <p>获取用户 Id
     *
     * @return  {@link Integer}
     *          - 用户 Id
     * @since   v1.01
     */
    public int getUserId() {
        return userId;
    }

    /**
     * setUserId:
     * <p>设置用户 Id
     *
     * @param   userId
     *          - 用户 Id
     * @since   v1.01
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

	
}
