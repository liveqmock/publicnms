/**
 * <p>Description:node category</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-5
 */

package com.afunms.polling.base;

public class PanelNodeCategory
{
    private int id;
    private String cnName;
    private String enName;
    private String upUpImage;
    private String downUpImage;
    private String upDownImage;
    private String downDownImage;
    private String lostImage;
    
	public String getLostImage() {
		return lostImage;
	}
	public void setLostImage(String lostImage) {
		this.lostImage = lostImage;
	}

	public String getCnName() {
		return cnName;
	}
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUpUpImage() {
		return upUpImage;
	}
	public void setUpUpImage(String upUpImage) {
		this.upUpImage = upUpImage;
	}
	public String getDownUpImage() {
		return downUpImage;
	}
	public void setDownUpImage(String downUpImage) {
		this.downUpImage = downUpImage;
	}
	public String getUpDownImage() {
		return upDownImage;
	}
	public void setUpDownImage(String upDownImage) {
		this.upDownImage = upDownImage;
	}
	public String getDownDownImage() {
		return downDownImage;
	}
	public void setDownDownImage(String downDownImage) {
		this.downDownImage = downDownImage;
	}
}