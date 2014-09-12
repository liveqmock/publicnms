/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DBTypeVo extends BaseVo
{
	private int id;
	private String dbtype;
	private String dbdesc;
	
	public String getDbtype() {
		return dbtype;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDbdesc() {
		return dbdesc;
	}
	public void setDbdesc(String dbdesc) {
		this.dbdesc = dbdesc;
	}
}