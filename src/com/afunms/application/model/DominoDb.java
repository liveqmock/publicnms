/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DominoDb extends BaseVo{
	private String dbBuffPoolMax = "";//缓冲池的最大值（MB）
	private String dbBuffPoolPeak = "";//缓冲池的高峰值（MB）
	private String dbBuffPoolReads = "";//缓冲池读取次数
	private String dbBuffPoolWrites = "";//缓冲池写入次数
	private String dbBuffPoolReadHit = "";//缓冲池读请求命中率（％）
	private String dbCacheEntry = "";//高速缓存中的项目数
	private String dbCacheWaterMark = "";//高速缓存的高水印
	private String dbCacheHit = "";//高速缓存命中次数
	private String dbCacheDbOpen = "";//高速缓存完成的数据库打开操作次数
	private String dbNifPoolPeak = "";//NIF池峰值数目
	private String dbNifPoolUse = "";//NIF池已使用数目
	private String dbNsfPoolPeak = "";//NSF池峰值数目
	private String dbNsfPoolUse = "";//NSF池已使用数目
	
	public DominoDb(){
		dbBuffPoolMax = "";//缓冲池的最大值（MB）
		dbBuffPoolPeak = "";//缓冲池的高峰值（MB）
		dbBuffPoolReads = "";//缓冲池读取次数
		dbBuffPoolWrites = "";//缓冲池写入次数
		dbBuffPoolReadHit = "";//缓冲池读请求命中率（％）
		dbCacheEntry = "";//高速缓存中的项目数
		dbCacheWaterMark = "";//高速缓存的高水印
		dbCacheHit = "";//高速缓存命中次数
		dbCacheDbOpen = "";//高速缓存完成的数据库打开操作次数
		dbNifPoolPeak = "";//NIF池峰值数目
		dbNifPoolUse = "";//NIF池已使用数目
		dbNsfPoolPeak = "";//NSF池峰值数目
		dbNsfPoolUse = "";//NSF池已使用数目		
	}
	public String getDbBuffPoolMax() {
		return dbBuffPoolMax;
	}
	public void setDbBuffPoolMax(String dbBuffPoolMax) {
		this.dbBuffPoolMax = dbBuffPoolMax;
	}
	public String getDbBuffPoolPeak() {
		return dbBuffPoolPeak;
	}
	public void setDbBuffPoolPeak(String dbBuffPoolPeak) {
		this.dbBuffPoolPeak = dbBuffPoolPeak;
	}
	public String getDbBuffPoolReadHit() {
		return dbBuffPoolReadHit;
	}
	public void setDbBuffPoolReadHit(String dbBuffPoolReadHit) {
		this.dbBuffPoolReadHit = dbBuffPoolReadHit;
	}
	public String getDbBuffPoolReads() {
		return dbBuffPoolReads;
	}
	public void setDbBuffPoolReads(String dbBuffPoolReads) {
		this.dbBuffPoolReads = dbBuffPoolReads;
	}
	public String getDbBuffPoolWrites() {
		return dbBuffPoolWrites;
	}
	public void setDbBuffPoolWrites(String dbBuffPoolWrites) {
		this.dbBuffPoolWrites = dbBuffPoolWrites;
	}
	public String getDbCacheDbOpen() {
		return dbCacheDbOpen;
	}
	public void setDbCacheDbOpen(String dbCacheDbOpen) {
		this.dbCacheDbOpen = dbCacheDbOpen;
	}
	public String getDbCacheEntry() {
		return dbCacheEntry;
	}
	public void setDbCacheEntry(String dbCacheEntry) {
		this.dbCacheEntry = dbCacheEntry;
	}
	public String getDbCacheHit() {
		return dbCacheHit;
	}
	public void setDbCacheHit(String dbCacheHit) {
		this.dbCacheHit = dbCacheHit;
	}
	public String getDbCacheWaterMark() {
		return dbCacheWaterMark;
	}
	public void setDbCacheWaterMark(String dbCacheWaterMark) {
		this.dbCacheWaterMark = dbCacheWaterMark;
	}
	public String getDbNifPoolPeak() {
		return dbNifPoolPeak;
	}
	public void setDbNifPoolPeak(String dbNifPoolPeak) {
		this.dbNifPoolPeak = dbNifPoolPeak;
	}
	public String getDbNifPoolUse() {
		return dbNifPoolUse;
	}
	public void setDbNifPoolUse(String dbNifPoolUse) {
		this.dbNifPoolUse = dbNifPoolUse;
	}
	public String getDbNsfPoolPeak() {
		return dbNsfPoolPeak;
	}
	public void setDbNsfPoolPeak(String dbNsfPoolPeak) {
		this.dbNsfPoolPeak = dbNsfPoolPeak;
	}
	public String getDbNsfPoolUse() {
		return dbNsfPoolUse;
	}
	public void setDbNsfPoolUse(String dbNsfPoolUse) {
		this.dbNsfPoolUse = dbNsfPoolUse;
	}

}