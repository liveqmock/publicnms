package com.afunms.polling.om;

import java.io.Serializable;


import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.afunms.common.base.*;


/** @author Hibernate CodeGenerator */
public class Policydata extends BaseVo implements Serializable {

	   /** identifier field */
    private Long id;
    private String ipaddress;
    private String pid;
    private int trustflag;//0:trust-->untrust    1:untrust-->trust
    private String source;
    private String destination;
    private String services;


	private String bak;

    /** full constructor */
    public Policydata(String ipaddress,String pid, int trustflag,String source, String destination, String services) {
        this.ipaddress = ipaddress;
        this.pid = pid;
        this.trustflag = trustflag;
        this.source = source;
        this.destination = destination;
        this.services = services;
    }

    /** default constructor */
    public Policydata() {
    }



    public java.lang.String getIpaddress() {
        return this.ipaddress;
    }

	public void setIpaddress(java.lang.String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
    public java.lang.String getPid() {
        return this.pid;
    }

	public void setPid(java.lang.String pid) {
		this.pid = pid;
	}

    public int getTrustflag() {
        return this.trustflag;
    }

	public void setTrustflag(int trustflag) {
		this.trustflag = trustflag;
	}

    public java.lang.String getSource() {
        return this.source;
    }

	public void setSource(java.lang.String source) {
		this.source = source;
	}

    public java.lang.String getDestination() {
        return this.destination;
    }

	public void setDestination(java.lang.String destination) {
		this.destination = destination;
	}


    public java.lang.String getServices() {
        return this.services;
    }

	public void setServices(java.lang.String services) {
		this.services = services;
	}


    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( !(other instanceof Policydata) ) return false;
        Policydata castOther = (Policydata) other;
        return new EqualsBuilder()
            .append(this.getId(), castOther.getId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getId())
            .toHashCode();
    }


	/**
	 * @return
	 */
	public Long getId() {
		return id;
	}


	/**
	 * @param integer
	 */
	public void setId(Long l) {
		id = l;
	}
}
