/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class Oracle_sessiondata implements Serializable {

    /** identifier field */
    private Integer data_id;
    
    private String dbname;
	/**
	 * @return Returns the dbname.
	 */
	public String getDbname() {
		return dbname;
	}
	/**
	 * @param dbname The dbname to set.
	 */
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
    /** nullable persistent field */
    private String machine;

    /** nullable persistent field */
    private String username;

    /** nullable persistent field */
    private String program;

    /** nullable persistent field */
    private String status;

    /** nullable persistent field */
    private String sessiontype;

    /** nullable persistent field */
    private String command;

    /** nullable persistent field */
    private java.util.Date logontime;

    /** nullable persistent field */
    private java.util.Date mon_time;
    private String serverip;

    /** full constructor */
    public Oracle_sessiondata(String serverip,String dbname,java.lang.String machine, java.lang.String username, java.lang.String program, java.lang.String status, java.lang.String sessiontype, java.lang.String command, java.util.Date logontime, java.util.Date mon_time) {
    	this.serverip = serverip;
    	this.dbname = dbname;
        this.machine = machine;
        this.username = username;
        this.program = program;
        this.status = status;
        this.sessiontype = sessiontype;
        this.command = command;
        this.logontime = logontime;
        this.mon_time = mon_time;
    }

    /** default constructor */
    public Oracle_sessiondata() {
    }

    public Integer getData_id() {
        return this.data_id;
    }

	public void setData_id(Integer data_id) {
		this.data_id = data_id;
	}

    public java.lang.String getMachine() {
        return this.machine;
    }

	public void setMachine(java.lang.String machine) {
		this.machine = machine;
	}

    public java.lang.String getUsername() {
        return this.username;
    }

	public void setUsername(java.lang.String username) {
		this.username = username;
	}

    public java.lang.String getProgram() {
        return this.program;
    }

	public void setProgram(java.lang.String program) {
		this.program = program;
	}

    public java.lang.String getStatus() {
        return this.status;
    }

	public void setStatus(java.lang.String status) {
		this.status = status;
	}

    public java.lang.String getSessiontype() {
        return this.sessiontype;
    }

	public void setSessiontype(java.lang.String sessiontype) {
		this.sessiontype = sessiontype;
	}

    public java.lang.String getCommand() {
        return this.command;
    }

	public void setCommand(java.lang.String command) {
		this.command = command;
	}

    public java.util.Date getLogontime() {
        return this.logontime;
    }

	public void setLogontime(java.util.Date logontime) {
		this.logontime = logontime;
	}

    public java.util.Date getMon_time() {
        return this.mon_time;
    }

	public void setMon_time(java.util.Date mon_time) {
		this.mon_time = mon_time;
	}

	/**
	 * @return Returns the serverip.
	 */
	public String getServerip() {
		return serverip;
	}
	/**
	 * @param serverip The serverip to set.
	 */
	public void setServerip(String serverip) {
		this.serverip = serverip;
	}
}
