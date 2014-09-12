package com.afunms.polling.snmp.hdc;

import java.io.Serializable;

public class HdcDFMessage implements Serializable {
	private String dfLUNSerialNumber;
	private String dfLUNPortID;
	private String dfLUNLUN;
	private String dfLUNWWNSecurity;
	private String dfLUNWWNGroupSecurity;
	private String dfLUNControlStatus;
	private int nodeid;
	private String dfLUNGroupSerialNumber;
	private String dfLUNGroupPortID;
	private String dfLUNGroupControlIndex;
	private String dfLUNGroupID;
	private String dfLUNGroupNickname;
	private String dfLUNGroupedLUNs;
	private String dfLUNGroupWWNSecurity;
	private String dfLUNGroupWWNGroupSecurity;
	private String dfLUNGroupControlStatus;
	private String dfSwitchSerialNumber;
	private String dfSwitchPortID;
	private String dfSwitchOnOff;
	private String dfSwitchControlStatus;
	private String dfWWNSerialNumber;
	private String dfWWNPortID;
	private String dfWWNControlIndex;
	private String dfWWNWWN;
	private String dfWWNID;
	private String dfWWNNickname;
	private String dfWWNUseNickname;
	private String dfWWNControlStatus;
	private String dfWWNGroupSerialNumber;
	private String dfWWNGroupPortID;
	private String dfWWNGroupControlIndex;
	private String dfWWNGroupID;
	private String dfWWNGroupNickname;
	private String dfWWNGroupedWWNs;
	private String dfWWNGroupControlStatus;
	private String dfSystemProductName;
	private String dfSystemMicroRevision;
	
	private String dfPortID;
	private String dfSystemSerialNumber;
	private String dfPortSerialNumber;
	private String dfPortKind;
	private String dfPortHostMode;
	private String dfPortFibreAddress;
	private String dfPortFibreTopology;
	private String dfPortControlStatus;
	private String dfPortDisplayName;
	private String dfPortWWN;
	public String getDfPortSerialNumber() {
		return dfPortSerialNumber;
	}

	public void setDfPortSerialNumber(String dfPortSerialNumber) {
		this.dfPortSerialNumber = dfPortSerialNumber;
	}

	public String getDfPortKind() {
		return dfPortKind;
	}

	public void setDfPortKind(String dfPortKind) {
		this.dfPortKind = dfPortKind;
	}

	public String getDfPortHostMode() {
		return dfPortHostMode;
	}

	public void setDfPortHostMode(String dfPortHostMode) {
		this.dfPortHostMode = dfPortHostMode;
	}

	public String getDfPortFibreAddress() {
		return dfPortFibreAddress;
	}

	public void setDfPortFibreAddress(String dfPortFibreAddress) {
		this.dfPortFibreAddress = dfPortFibreAddress;
	}

	public String getDfPortFibreTopology() {
		return dfPortFibreTopology;
	}

	public void setDfPortFibreTopology(String dfPortFibreTopology) {
		this.dfPortFibreTopology = dfPortFibreTopology;
	}

	public String getDfPortControlStatus() {
		return dfPortControlStatus;
	}

	public void setDfPortControlStatus(String dfPortControlStatus) {
		this.dfPortControlStatus = dfPortControlStatus;
	}

	public String getDfPortDisplayName() {
		return dfPortDisplayName;
	}

	public void setDfPortDisplayName(String dfPortDisplayName) {
		this.dfPortDisplayName = dfPortDisplayName;
	}

	public String getDfPortWWN() {
		return dfPortWWN;
	}

	public void setDfPortWWN(String dfPortWWN) {
		this.dfPortWWN = dfPortWWN;
	}

	public String getDfSystemProductName() {
		return dfSystemProductName;
	}

	public void setDfSystemProductName(String dfSystemProductName) {
		this.dfSystemProductName = dfSystemProductName;
	}

	public String getDfSystemMicroRevision() {
		return dfSystemMicroRevision;
	}

	public void setDfSystemMicroRevision(String dfSystemMicroRevision) {
		this.dfSystemMicroRevision = dfSystemMicroRevision;
	}

	public String getDfSystemSerialNumber() {
		return dfSystemSerialNumber;
	}

	public void setDfSystemSerialNumber(String dfSystemSerialNumber) {
		this.dfSystemSerialNumber = dfSystemSerialNumber;
	}

	public String getDfWWNSerialNumber() {
		return dfWWNSerialNumber;
	}

	public void setDfWWNSerialNumber(String dfWWNSerialNumber) {
		this.dfWWNSerialNumber = dfWWNSerialNumber;
	}

	public String getDfWWNPortID() {
		return dfWWNPortID;
	}

	public void setDfWWNPortID(String dfWWNPortID) {
		this.dfWWNPortID = dfWWNPortID;
	}

	public String getDfWWNControlIndex() {
		return dfWWNControlIndex;
	}

	public void setDfWWNControlIndex(String dfWWNControlIndex) {
		this.dfWWNControlIndex = dfWWNControlIndex;
	}

	public String getDfWWNWWN() {
		return dfWWNWWN;
	}

	public void setDfWWNWWN(String dfWWNWWN) {
		this.dfWWNWWN = dfWWNWWN;
	}

	public String getDfWWNID() {
		return dfWWNID;
	}

	public void setDfWWNID(String dfWWNID) {
		this.dfWWNID = dfWWNID;
	}

	public String getDfWWNNickname() {
		return dfWWNNickname;
	}

	public void setDfWWNNickname(String dfWWNNickname) {
		this.dfWWNNickname = dfWWNNickname;
	}

	public String getDfWWNUseNickname() {
		return dfWWNUseNickname;
	}

	public void setDfWWNUseNickname(String dfWWNUseNickname) {
		this.dfWWNUseNickname = dfWWNUseNickname;
	}

	public String getDfWWNControlStatus() {
		return dfWWNControlStatus;
	}

	public void setDfWWNControlStatus(String dfWWNControlStatus) {
		this.dfWWNControlStatus = dfWWNControlStatus;
	}

	public String getDfSwitchSerialNumber() {
		return dfSwitchSerialNumber;
	}

	public void setDfSwitchSerialNumber(String dfSwitchSerialNumber) {
		this.dfSwitchSerialNumber = dfSwitchSerialNumber;
	}

	public String getDfSwitchPortID() {
		return dfSwitchPortID;
	}

	public void setDfSwitchPortID(String dfSwitchPortID) {
		this.dfSwitchPortID = dfSwitchPortID;
	}

	public String getDfSwitchOnOff() {
		return dfSwitchOnOff;
	}

	public void setDfSwitchOnOff(String dfSwitchOnOff) {
		this.dfSwitchOnOff = dfSwitchOnOff;
	}

	public String getDfSwitchControlStatus() {
		return dfSwitchControlStatus;
	}

	public void setDfSwitchControlStatus(String dfSwitchControlStatus) {
		this.dfSwitchControlStatus = dfSwitchControlStatus;
	}

	public String getDfLUNGroupControlStatus() {
		return dfLUNGroupControlStatus;
	}

	public void setDfLUNGroupControlStatus(String dfLUNGroupControlStatus) {
		this.dfLUNGroupControlStatus = dfLUNGroupControlStatus;
	}

	public String getDfLUNSerialNumber() {
		return dfLUNSerialNumber;
	}

	public void setDfLUNSerialNumber(String dfLUNSerialNumber) {
		this.dfLUNSerialNumber = dfLUNSerialNumber;
	}

	public String getDfLUNPortID() {
		return dfLUNPortID;
	}

	public void setDfLUNPortID(String dfLUNPortID) {
		this.dfLUNPortID = dfLUNPortID;
	}

	public String getDfLUNLUN() {
		return dfLUNLUN;
	}

	public void setDfLUNLUN(String dfLUNLUN) {
		this.dfLUNLUN = dfLUNLUN;
	}

	public String getDfLUNWWNSecurity() {
		return dfLUNWWNSecurity;
	}

	public void setDfLUNWWNSecurity(String dfLUNWWNSecurity) {
		this.dfLUNWWNSecurity = dfLUNWWNSecurity;
	}

	public String getDfLUNWWNGroupSecurity() {
		return dfLUNWWNGroupSecurity;
	}

	public void setDfLUNWWNGroupSecurity(String dfLUNWWNGroupSecurity) {
		this.dfLUNWWNGroupSecurity = dfLUNWWNGroupSecurity;
	}

	public String getDfLUNControlStatus() {
		return dfLUNControlStatus;
	}

	public void setDfLUNControlStatus(String dfLUNControlStatus) {
		this.dfLUNControlStatus = dfLUNControlStatus;
	}

	public int getNodeid() {
		return nodeid;
	}

	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}

	public String getDfLUNGroupSerialNumber() {
		return dfLUNGroupSerialNumber;
	}

	public void setDfLUNGroupSerialNumber(String dfLUNGroupSerialNumber) {
		this.dfLUNGroupSerialNumber = dfLUNGroupSerialNumber;
	}

	public String getDfLUNGroupPortID() {
		return dfLUNGroupPortID;
	}

	public void setDfLUNGroupPortID(String dfLUNGroupPortID) {
		this.dfLUNGroupPortID = dfLUNGroupPortID;
	}

	public String getDfLUNGroupControlIndex() {
		return dfLUNGroupControlIndex;
	}

	public void setDfLUNGroupControlIndex(String dfLUNGroupControlIndex) {
		this.dfLUNGroupControlIndex = dfLUNGroupControlIndex;
	}

	public String getDfLUNGroupID() {
		return dfLUNGroupID;
	}

	public void setDfLUNGroupID(String dfLUNGroupID) {
		this.dfLUNGroupID = dfLUNGroupID;
	}

	public String getDfLUNGroupNickname() {
		return dfLUNGroupNickname;
	}

	public void setDfLUNGroupNickname(String dfLUNGroupNickname) {
		this.dfLUNGroupNickname = dfLUNGroupNickname;
	}

	public String getDfLUNGroupedLUNs() {
		return dfLUNGroupedLUNs;
	}

	public void setDfLUNGroupedLUNs(String dfLUNGroupedLUNs) {
		this.dfLUNGroupedLUNs = dfLUNGroupedLUNs;
	}

	public String getDfLUNGroupWWNSecurity() {
		return dfLUNGroupWWNSecurity;
	}

	public void setDfLUNGroupWWNSecurity(String dfLUNGroupWWNSecurity) {
		this.dfLUNGroupWWNSecurity = dfLUNGroupWWNSecurity;
	}

	public String getDfLUNGroupWWNGroupSecurity() {
		return dfLUNGroupWWNGroupSecurity;
	}

	public void setDfLUNGroupWWNGroupSecurity(String dfLUNGroupWWNGroupSecurity) {
		this.dfLUNGroupWWNGroupSecurity = dfLUNGroupWWNGroupSecurity;
	}

	public String getDfWWNGroupSerialNumber() {
		return dfWWNGroupSerialNumber;
	}

	public void setDfWWNGroupSerialNumber(String dfWWNGroupSerialNumber) {
		this.dfWWNGroupSerialNumber = dfWWNGroupSerialNumber;
	}

	public String getDfWWNGroupPortID() {
		return dfWWNGroupPortID;
	}

	public void setDfWWNGroupPortID(String dfWWNGroupPortID) {
		this.dfWWNGroupPortID = dfWWNGroupPortID;
	}

	public String getDfWWNGroupControlIndex() {
		return dfWWNGroupControlIndex;
	}

	public void setDfWWNGroupControlIndex(String dfWWNGroupControlIndex) {
		this.dfWWNGroupControlIndex = dfWWNGroupControlIndex;
	}

	public String getDfWWNGroupID() {
		return dfWWNGroupID;
	}

	public void setDfWWNGroupID(String dfWWNGroupID) {
		this.dfWWNGroupID = dfWWNGroupID;
	}

	public String getDfWWNGroupNickname() {
		return dfWWNGroupNickname;
	}

	public void setDfWWNGroupNickname(String dfWWNGroupNickname) {
		this.dfWWNGroupNickname = dfWWNGroupNickname;
	}

	public String getDfWWNGroupedWWNs() {
		return dfWWNGroupedWWNs;
	}

	public void setDfWWNGroupedWWNs(String dfWWNGroupedWWNs) {
		this.dfWWNGroupedWWNs = dfWWNGroupedWWNs;
	}

	public String getDfWWNGroupControlStatus() {
		return dfWWNGroupControlStatus;
	}

	public void setDfWWNGroupControlStatus(String dfWWNGroupControlStatus) {
		this.dfWWNGroupControlStatus = dfWWNGroupControlStatus;
	}

	public String getDfPortID() {
		return dfPortID;
	}

	public void setDfPortID(String dfPortID) {
		this.dfPortID = dfPortID;
	}
}
