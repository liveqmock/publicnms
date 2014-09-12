/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class IpAlias extends BaseVo
{
    private String id;
    private String ipaddress;
    private String aliasip;
    private String indexs;
    private String descr;
    private String speed;
    private String types;
    
    public void setId(String id)
    {
       this.id = id;
    }

    public String getId()
    {
       return id;
    }

    public void setIpaddress(String ipaddress)
    {
       this.ipaddress = ipaddress;
    }

    public String getIpaddress()
    {
       return ipaddress;
    }
    
    public void setAliasip(String aliasip)
    {
       this.aliasip = aliasip;
    }

    public String getAliasip()
    {
       return aliasip;
    }

    public void setIndexs(String indexs)
    {
       this.indexs = indexs;
    }

    public String getIndexs()
    {
       return indexs;
    }
    
    public void setDescr(String descr)
    {
       this.descr = descr;
    }

    public String getDescr()
    {
       return descr;
    }
    
    public void setSpeed(String speed)
    {
       this.speed = speed;
    }

    public String getSpeed()
    {
       return speed;
    }
    
    public void setTypes(String types)
    {
       this.types = types;
    }

    public String getTypes()
    {
       return types;
    }
}
