/**
 * <p>Description:discovery process monitor</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-13
 */

package com.afunms.discovery;

/**
 * 监视拓扑发现,看其进度如何
 */
public interface DiscoverMonitorInterface
{	
	/**
	 * 开始时间
	 */
	public String getStartTime();	

	/**
	 * 结束时间
	 */
	public String getEndTime();
	
	/**
	 * 到当前,总耗时
	 */
	public String getElapseTime();
	
	/**
	 * 子网的总数
	 */
	public int getSubNetTotal();
	
	/**
	 * 设备的总数
	 */
	public int getHostTotal();

	/**
	 * 已发现完设备的总数
	 */
	public int getDiscoveredNodeTotal();
			
	/**
	 * 当前发现结果的统计表
	 */
	public String getResultTable();			
	
	/**
	 * 发现完成了吗?
	 */
	public boolean isCompleted();
}