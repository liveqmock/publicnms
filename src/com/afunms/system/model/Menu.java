/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class Menu extends BaseVo
{
    private String id;
    private String title;
    private String url;
    private int sort;
    
    public void setId(String id)
    {
       this.id = id;
    }

    public String getId()
    {
       return id;
    }

    public void setTitle(String title)
    {
       this.title = title;
    }

    public String getTitle()
    {
       return title;
    }

    public void setUrl(String url)
    {
       this.url = url;
    }

    public String getUrl()
    {
       return url;
    }

    public void setSort(int sort)
    {
       this.sort = sort;
    }

    public int getSort()
    {
       return sort;
    }
}
