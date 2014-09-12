/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import java.io.Serializable;
import java.util.Date;


/** @author Hibernate CodeGenerator */
public class Sqlserver_processdata implements Serializable {

    /** identifier field */
    private Integer data_id;

    /** nullable persistent field */
    private String spid;

    /** nullable persistent field */
    private String dbname;

    /** nullable persistent field */
    private String username;

    /** nullable persistent field */
    private int cpu;

    /** nullable persistent field */
    private long physical_io;

    /** nullable persistent field */
    private int memusage;

    /** nullable persistent field */
    private String status;

    /** nullable persistent field */
    private String hostname;

    /** nullable persistent field */
    private String program_name;
    private java.util.Date login_time;
    /** nullable persistent field */
    private java.util.Date mon_time;
    
    private String serverip;
    /** full constructor */
    public Sqlserver_processdata(String serverip,java.lang.String spid, java.lang.String dbname, java.lang.String username, int cpu, long physical_io, int memusage, java.lang.String status, java.lang.String hostname, java.lang.String program_name,Date login_time, java.util.Date mon_time) {
    	this.serverip = serverip;
        this.spid = spid;
        this.dbname = dbname;
        this.username = username;
        this.cpu = cpu;
        this.physical_io = physical_io;
        this.memusage = memusage;
        this.status = status;
        this.hostname = hostname;
        this.program_name = program_name;
        this.login_time = login_time;
        this.mon_time = mon_time;
    }

    /** default constructor */
    public Sqlserver_processdata() {
    }

    public Integer getData_id() {
        return this.data_id;
    }

	public void setData_id(Integer data_id) {
		this.data_id = data_id;
	}

    public java.lang.String getSpid() {
        return this.spid;
    }

	public void setSpid(java.lang.String spid) {
		this.spid = spid;
	}

    public java.lang.String getDbname() {
        return this.dbname;
    }

	public void setDbname(java.lang.String dbname) {
		this.dbname = dbname;
	}

    public java.lang.String getUsername() {
        return this.username;
    }

	public void setUsername(java.lang.String username) {
		this.username = username;
	}

    public int getCpu() {
        return this.cpu;
    }

	public void setCpu(int cpu) {
		this.cpu = cpu;
	}

    public long getPhysical_io() {
        return this.physical_io;
    }

	public void setPhysical_io(long physical_io) {
		this.physical_io = physical_io;
	}

    public int getMemusage() {
        return this.memusage;
    }

	public void setMemusage(int memusage) {
		this.memusage = memusage;
	}

    public java.lang.String getStatus() {
        return this.status;
    }

	public void setStatus(java.lang.String status) {
		this.status = status;
	}

    public java.lang.String getHostname() {
        return this.hostname;
    }

	public void setHostname(java.lang.String hostname) {
		this.hostname = hostname;
	}

    public java.lang.String getProgram_name() {
        return this.program_name;
    }

	public void setProgram_name(java.lang.String program_name) {
		this.program_name = program_name;
	}

    public java.util.Date getMon_time() {
        return this.mon_time;
    }

	public void setMon_time(java.util.Date mon_time) {
		this.mon_time = mon_time;
	}

	/**
	 * @return Returns the logon_time.
	 */
	public java.util.Date getLogin_time() {
		return login_time;
	}
	/**
	 * @param logon_time The logon_time to set.
	 */
	public void setLogin_time(java.util.Date login_time) {
		this.login_time = login_time;
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
