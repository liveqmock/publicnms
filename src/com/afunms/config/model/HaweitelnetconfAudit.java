package com.afunms.config.model;

import java.sql.Timestamp;
import java.util.Date;

import com.afunms.common.base.BaseVo;

public class HaweitelnetconfAudit extends BaseVo {
    
    private int id;//id
    private String ip;//ip地址
    private String username;
    private int userid;
    private String oldpassword;//旧用户密码
    private String newpassword;//新用户密码
    private Timestamp dotime;//时间
    private String bak;//
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getOldpassword() {
        return oldpassword;
    }
    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }
    public String getNewpassword() {
        return newpassword;
    }
    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }
    public String getBak() {
        return bak;
    }
    public void setBak(String bak) {
        this.bak = bak;
    }
    public Timestamp getDotime() {
        return dotime;
    }
    public void setDotime(Timestamp dotime) {
        this.dotime = dotime;
    }
    public int getUserid() {
        return userid;
    }
    public void setUserid(int userid) {
        this.userid = userid;
    }
    
}
