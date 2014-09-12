/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DominoCache extends BaseVo{
	private String cacheCommandCount = "";//存储器指令个数
	private String cacheCommandDisRate = "";//存储器指令转换率
	private String cacheCommandHitRate = "";//存储器指令成功率
	private String cacheCommandSize = "";//存储器指令大小
	private String cacheDbHitRate = "";//存储器数据库成功率
	private String cacheSessionCount = "";//存储器Session个数
	private String cacheSessionDisRate = "";//存储器Session转换率
	private String cacheSessionHitRate = "";//存储器Session成功率
	private String cacheSessionSize = "";//存储器Session大小
	private String cacheUserCount = "";//存储器用户个数
	private String cacheUserDisRate = "";//存储器用户转换率
	private String cacheUserHitRate = "";//存储器用户成功率
	private String cacheUserSize = "";//存储器用户大小
	public String getCacheCommandCount() {
		return cacheCommandCount;
	}
	public void setCacheCommandCount(String cacheCommandCount) {
		this.cacheCommandCount = cacheCommandCount;
	}
	public String getCacheCommandDisRate() {
		return cacheCommandDisRate;
	}
	public void setCacheCommandDisRate(String cacheCommandDisRate) {
		this.cacheCommandDisRate = cacheCommandDisRate;
	}
	public String getCacheCommandHitRate() {
		return cacheCommandHitRate;
	}
	public void setCacheCommandHitRate(String cacheCommandHitRate) {
		this.cacheCommandHitRate = cacheCommandHitRate;
	}
	public String getCacheCommandSize() {
		return cacheCommandSize;
	}
	public void setCacheCommandSize(String cacheCommandSize) {
		this.cacheCommandSize = cacheCommandSize;
	}
	public String getCacheDbHitRate() {
		return cacheDbHitRate;
	}
	public void setCacheDbHitRate(String cacheDbHitRate) {
		this.cacheDbHitRate = cacheDbHitRate;
	}
	public String getCacheSessionCount() {
		return cacheSessionCount;
	}
	public void setCacheSessionCount(String cacheSessionCount) {
		this.cacheSessionCount = cacheSessionCount;
	}
	public String getCacheSessionDisRate() {
		return cacheSessionDisRate;
	}
	public void setCacheSessionDisRate(String cacheSessionDisRate) {
		this.cacheSessionDisRate = cacheSessionDisRate;
	}
	public String getCacheSessionHitRate() {
		return cacheSessionHitRate;
	}
	public void setCacheSessionHitRate(String cacheSessionHitRate) {
		this.cacheSessionHitRate = cacheSessionHitRate;
	}
	public String getCacheSessionSize() {
		return cacheSessionSize;
	}
	public void setCacheSessionSize(String cacheSessionSize) {
		this.cacheSessionSize = cacheSessionSize;
	}
	public String getCacheUserCount() {
		return cacheUserCount;
	}
	public void setCacheUserCount(String cacheUserCount) {
		this.cacheUserCount = cacheUserCount;
	}
	public String getCacheUserDisRate() {
		return cacheUserDisRate;
	}
	public void setCacheUserDisRate(String cacheUserDisRate) {
		this.cacheUserDisRate = cacheUserDisRate;
	}
	public String getCacheUserHitRate() {
		return cacheUserHitRate;
	}
	public void setCacheUserHitRate(String cacheUserHitRate) {
		this.cacheUserHitRate = cacheUserHitRate;
	}
	public String getCacheUserSize() {
		return cacheUserSize;
	}
	public void setCacheUserSize(String cacheUserSize) {
		this.cacheUserSize = cacheUserSize;
	}
	

}