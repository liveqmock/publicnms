package com.afunms.application.model;
/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：May 27, 2011 6:00:48 PM
 * 类说明 :排序最多的前10条SQL
 */
public class OracleTopSqlSort {
	private String id;
	
	private String sqltext;
	
	private String sorts;
	
	private String executions;
	
	private String sortsexec;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSqltext() {
		return sqltext;
	}

	public void setSqltext(String sqltext) {
		this.sqltext = sqltext;
	}

	public String getSorts() {
		return sorts;
	}

	public void setSorts(String sorts) {
		this.sorts = sorts;
	}

	public String getExecutions() {
		return executions;
	}

	public void setExecutions(String executions) {
		this.executions = executions;
	}

	public String getSortsexec() {
		return sortsexec;
	}

	public void setSortsexec(String sortsexec) {
		this.sortsexec = sortsexec;
	}
}
