/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DominoMail extends BaseVo{
	private String mailDead = "";//无法邮递的邮件数目
	private String mailWaiting = "";//等待传送给其他服务器的邮件数目
	private String mailWaitingRecipients = "";//等待传送给其他服务器的邮件数目
	private String mailDeliverRate = "";//当前邮递速率（Bytes/s）
	private String mailTransferRate = "";//当前传送速率（Bytes/s）
	private String mailDeliverThreadsMax = "";//最大邮递线程数
	private String mailDeliverThreadsTotal = "";//邮递线程总数
	private String mailTransferThreadsMax = "";//最大传送线程数
	private String mailTransferThreadsTotal = "";//传送线程总数
	private String mailAvgSize = "";//邮件平均大小
	private String mailAvgTime = "";//邮件平均分发时间
	
	public String getMailDead() {
		return mailDead;
	}
	public void setMailDead(String mailDead) {
		this.mailDead = mailDead;
	}
	public String getMailDeliverRate() {
		return mailDeliverRate;
	}
	public void setMailDeliverRate(String mailDeliverRate) {
		this.mailDeliverRate = mailDeliverRate;
	}
	public String getMailDeliverThreadsMax() {
		return mailDeliverThreadsMax;
	}
	public void setMailDeliverThreadsMax(String mailDeliverThreadsMax) {
		this.mailDeliverThreadsMax = mailDeliverThreadsMax;
	}
	public String getMailDeliverThreadsTotal() {
		return mailDeliverThreadsTotal;
	}
	public void setMailDeliverThreadsTotal(String mailDeliverThreadsTotal) {
		this.mailDeliverThreadsTotal = mailDeliverThreadsTotal;
	}
	public String getMailTransferRate() {
		return mailTransferRate;
	}
	public void setMailTransferRate(String mailTransferRate) {
		this.mailTransferRate = mailTransferRate;
	}
	public String getMailTransferThreadsMax() {
		return mailTransferThreadsMax;
	}
	public void setMailTransferThreadsMax(String mailTransferThreadsMax) {
		this.mailTransferThreadsMax = mailTransferThreadsMax;
	}
	public String getMailTransferThreadsTotal() {
		return mailTransferThreadsTotal;
	}
	public void setMailTransferThreadsTotal(String mailTransferThreadsTotal) {
		this.mailTransferThreadsTotal = mailTransferThreadsTotal;
	}
	public String getMailWaiting() {
		return mailWaiting;
	}
	public void setMailWaiting(String mailWaiting) {
		this.mailWaiting = mailWaiting;
	}
	public String getMailWaitingRecipients() {
		return mailWaitingRecipients;
	}
	public void setMailWaitingRecipients(String mailWaitingRecipients) {
		this.mailWaitingRecipients = mailWaitingRecipients;
	}
	public String getMailAvgSize() {
		return mailAvgSize;
	}
	public void setMailAvgSize(String mailAvgSize) {
		this.mailAvgSize = mailAvgSize;
	}
	public String getMailAvgTime() {
		return mailAvgTime;
	}
	public void setMailAvgTime(String mailAvgTime) {
		this.mailAvgTime = mailAvgTime;
	}

}