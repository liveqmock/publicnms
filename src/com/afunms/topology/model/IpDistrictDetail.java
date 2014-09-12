/**
 * <p>Description:mapping table NMS_TOPO_NODE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

/**
 * 此类为封装ip所属区域与当前区域匹配的业务model类 没有存储到对应的数据库当中
 * @author nielin
 * @date 2010-05-12
 *
 */
public class IpDistrictDetail extends BaseVo{
	
	/**
	 * 默认id (以备后来持久化存入数据库中使用 ， 暂时没有使用)
	 */
	private int id; 
	
	/**
	 * 网络设备ip
	 */
	private String district;
	
	/**
	 * 分配ip总数
	 */
	private String ipTotal;
	
	/**
	 * 已使用
	 */
	private String usedTotal;
	
	/**
	 * 未使用
	 */
	private String unusedTotal;
	
	/**
	 * 在线总数
	 */
	private String isOnlineTotal;
	
	/**
	 * 离线总数
	 */
	private String unOnlineToatl;

	/**
	 * 
	 */
	public IpDistrictDetail() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param district
	 * @param ipTotal
	 * @param usedTotal
	 * @param unusedTotal
	 * @param isOnlineTotal
	 * @param unOnlineToatl
	 */
	public IpDistrictDetail(int id, String district, String ipTotal,
			String usedTotal, String unusedTotal, String isOnlineTotal,
			String unOnlineToatl) {
		this.id = id;
		this.district = district;
		this.ipTotal = ipTotal;
		this.usedTotal = usedTotal;
		this.unusedTotal = unusedTotal;
		this.isOnlineTotal = isOnlineTotal;
		this.unOnlineToatl = unOnlineToatl;
	}

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
	 * @return the district
	 */
	public String getDistrict() {
		return district;
	}

	/**
	 * @param district the district to set
	 */
	public void setDistrict(String district) {
		this.district = district;
	}

	/**
	 * @return the ipTotal
	 */
	public String getIpTotal() {
		return ipTotal;
	}

	/**
	 * @param ipTotal the ipTotal to set
	 */
	public void setIpTotal(String ipTotal) {
		this.ipTotal = ipTotal;
	}

	/**
	 * @return the usedTotal
	 */
	public String getUsedTotal() {
		return usedTotal;
	}

	/**
	 * @param usedTotal the usedTotal to set
	 */
	public void setUsedTotal(String usedTotal) {
		this.usedTotal = usedTotal;
	}

	/**
	 * @return the unusedTotal
	 */
	public String getUnusedTotal() {
		return unusedTotal;
	}

	/**
	 * @param unusedTotal the unusedTotal to set
	 */
	public void setUnusedTotal(String unusedTotal) {
		this.unusedTotal = unusedTotal;
	}

	/**
	 * @return the isOnlineTotal
	 */
	public String getIsOnlineTotal() {
		return isOnlineTotal;
	}

	/**
	 * @param isOnlineTotal the isOnlineTotal to set
	 */
	public void setIsOnlineTotal(String isOnlineTotal) {
		this.isOnlineTotal = isOnlineTotal;
	}

	/**
	 * @return the unOnlineToatl
	 */
	public String getUnOnlineToatl() {
		return unOnlineToatl;
	}

	/**
	 * @param unOnlineToatl the unOnlineToatl to set
	 */
	public void setUnOnlineToatl(String unOnlineToatl) {
		this.unOnlineToatl = unOnlineToatl;
	}

	
	

	
	
}