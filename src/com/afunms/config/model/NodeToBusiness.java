/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class NodeToBusiness extends BaseVo
{
    private int id;
    private String elementtype;
    private int nodeid;
    private int businessid;
    
    public void setId(int id)
    {
       this.id = id;
    }

    public int getId()
    {
       return id;
    }

    public void setElementtype(String elementtype)
    {
       this.elementtype = elementtype;
    }

    public String getElementtype()
    {
       return elementtype;
    }
    
    public void setNodeid(int nodeid)
    {
       this.nodeid = nodeid;
    }

    public int getNodeid()
    {
       return nodeid;
    }

    public void setBusinessid(int businessid)
    {
       this.businessid = businessid;
    }

    public int getBusinessid()
    {
       return businessid;
    }
}
