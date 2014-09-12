package montnets;

import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class SmsConfig extends BaseVo{
	
	private int id;
	private String name;//用户名
	private String mobilenum;//手机号码
	private String eventlist;//告警信息
	private Calendar eventtime;//时间
	public String getEventlist() {
		return eventlist;
	}
	public void setEventlist(String eventlist) {
		this.eventlist = eventlist;
	}

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMobilenum() {
		return mobilenum;
	}
	public void setMobilenum(String mobilenum) {
		this.mobilenum = mobilenum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Calendar getEventtime() {
		return eventtime;
	}
	public void setEventtime(Calendar eventtime) {
		this.eventtime = eventtime;
	}

}
