/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.model;

import com.afunms.common.base.BaseVo;

public class EventReport extends BaseVo
{
    private Integer id;
    private Integer eventid;
    private String report_content;
    private java.util.Calendar deal_time;
    private java.util.Calendar report_time;
    private String report_man;

    
    public Integer getId() {
        return this.id;
    }

	public void setId(Integer id) {
		this.id = id;
	}

    public Integer getEventid() {
        return this.eventid;
    }

	public void setEventid(Integer eventid) {
		this.eventid = eventid;
	}

    public java.lang.String getReport_man() {
        return this.report_man;
    }

	public void setReport_man(java.lang.String report_man) {
		this.report_man = report_man;
	}

    public java.lang.String getReport_content() {
        return this.report_content;
    }

	public void setReport_content(java.lang.String report_content) {
		this.report_content = report_content;
	}

    public java.util.Calendar getDeal_time() {
        return this.deal_time;
    }

	public void setDeal_time(java.util.Calendar deal_time) {
		this.deal_time = deal_time;
	}

    public java.util.Calendar getReport_time() {
        return this.report_time;
    }

	public void setReport_time(java.util.Calendar report_time) {
		this.report_time = report_time;
	}

}
