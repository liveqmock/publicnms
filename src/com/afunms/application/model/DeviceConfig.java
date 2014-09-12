package com.afunms.application.model;

import java.sql.Timestamp;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class DeviceConfig extends BaseVo
{
	int id;
	int nodeid;
	String sn;
	String name;
	String devCode;
	String room;
	String brand;
	String model;
	String useFor;
	String version;
	String maintainer;
	String director;
	String users;
	Timestamp expiryDate;
	Timestamp reviewDate;
	int status;
	String incidence;
	String place;
	String supplier;
	String assetNumber;
	String description;
	Timestamp installedDate;
	String category;
	int updateStatus;
	String followupCalls;
	String userDepartment;
	String afterServiceProvider;
	String maintenanceDepartment;
	Timestamp updatedOn;
	String updatedBy;
	Timestamp createdOn;
	String createdBy;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNodeid() {
		return nodeid;
	}
	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDevCode() {
		return devCode;
	}
	public void setDevCode(String devCode) {
		this.devCode = devCode;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getUseFor() {
		return useFor;
	}
	public void setUseFor(String useFor) {
		this.useFor = useFor;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMaintainer() {
		return maintainer;
	}
	public void setMaintainer(String maintainer) {
		this.maintainer = maintainer;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getUsers() {
		return users;
	}
	public void setUsers(String users) {
		this.users = users;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getIncidence() {
		return incidence;
	}
	public void setIncidence(String incidence) {
		this.incidence = incidence;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getAssetNumber() {
		return assetNumber;
	}
	public void setAssetNumber(String assetNumber) {
		this.assetNumber = assetNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getUpdateStatus() {
		return updateStatus;
	}
	public void setUpdateStatus(int updateStatus) {
		this.updateStatus = updateStatus;
	}
	public String getFollowupCalls() {
		return followupCalls;
	}
	public void setFollowupCalls(String followupCalls) {
		this.followupCalls = followupCalls;
	}
	public String getUserDepartment() {
		return userDepartment;
	}
	public void setUserDepartment(String userDepartment) {
		this.userDepartment = userDepartment;
	}
	public String getAfterServiceProvider() {
		return afterServiceProvider;
	}
	public void setAfterServiceProvider(String afterServiceProvider) {
		this.afterServiceProvider = afterServiceProvider;
	}
	public String getMaintenanceDepartment() {
		return maintenanceDepartment;
	}
	public void setMaintenanceDepartment(String maintenanceDepartment) {
		this.maintenanceDepartment = maintenanceDepartment;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Timestamp getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Timestamp expiryDate) {
		this.expiryDate = expiryDate;
	}
	public Timestamp getReviewDate() {
		return reviewDate;
	}
	public void setReviewDate(Timestamp reviewDate) {
		this.reviewDate = reviewDate;
	}
	public Timestamp getInstalledDate() {
		return installedDate;
	}
	public void setInstalledDate(Timestamp installedDate) {
		this.installedDate = installedDate;
	}
	public Timestamp getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}
	public Timestamp getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}
	
}
