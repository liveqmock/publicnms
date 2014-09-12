/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DominoDisk extends BaseVo{
	private String diskname = "";//名称
	private String disksize = "";//大小
	private String diskfree = "";//空闲
	private String diskusedpctutil = "";//使用率
	private String disktype = "";//类型
	
	
	public DominoDisk(){
		diskname = "";
		disksize = "";
		diskfree = "";
		diskusedpctutil = "";	
		disktype = "";
	}


	public String getDiskname() {
		return diskname;
	}


	public void setDiskname(String diskname) {
		this.diskname = diskname;
	}


	public String getDisksize() {
		return disksize;
	}


	public void setDisksize(String disksize) {
		this.disksize = disksize;
	}


	public String getDiskfree() {
		return diskfree;
	}


	public void setDiskfree(String diskfree) {
		this.diskfree = diskfree;
	}


	public String getDiskusedpctutil() {
		return diskusedpctutil;
	}


	public void setDiskusedpctutil(String diskusedpctutil) {
		this.diskusedpctutil = diskusedpctutil;
	}


	public String getDisktype() {
		return disktype;
	}


	public void setDisktype(String disktype) {
		this.disktype = disktype;
	}
	

}