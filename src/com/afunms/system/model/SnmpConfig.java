/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class SnmpConfig extends BaseVo
{
    private String id;
    private String name;
    private int snmpversion;//v1:0 v2c:1
    private String readcommunity;
    private String writecommunity;   
    private int timeout;
    private int trytime;
    
    public void setId(String id)
    {
       this.id = id;
    }

    public String getId()
    {
       return id;
    }

    public void setName(String name)
    {
       this.name = name;
    }

    public String getName()
    {
       return name;
    }
    public void setSnmpversion(int snmpversion)
    {
       this.snmpversion = snmpversion;
    }

    public int getSnmpversion()
    {
       return snmpversion;
    }
    public void setReadcommunity(String readcommunity)
    {
       this.readcommunity = readcommunity;
    }

    public String getReadcommunity()
    {
       return readcommunity;
    }
    
    public void setWritecommunity(String writecommunity)
    {
       this.writecommunity = writecommunity;
    }

    public String getWritecommunity()
    {
       return writecommunity;
    }

    public void setTimeout(int timeout)
    {
       this.timeout = timeout;
    }

    public int getTimeout()
    {
       return timeout;
    }
    public void setTrytime(int trytime)
    {
       this.trytime = trytime;
    }

    public int getTrytime()
    {
       return trytime;
    }
}
