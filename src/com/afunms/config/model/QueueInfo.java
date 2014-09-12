package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class QueueInfo extends BaseVo{
	private int id;
	private String entity;
	private int inputSize;
	private int inputMax;
	private int inputDrops;
	private int inputFlushes;
	private int outputSize;
	private int outputMax;
	private int outputDrops;
	private int outputFlushes;
	private int outputThreshold;
	private int availBandwidth;
   private String collecttime;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public int getInputSize() {
		return inputSize;
	}

	public void setInputSize(int inputSize) {
		this.inputSize = inputSize;
	}

	public int getInputMax() {
		return inputMax;
	}

	public void setInputMax(int inputMax) {
		this.inputMax = inputMax;
	}

	public int getInputDrops() {
		return inputDrops;
	}

	public void setInputDrops(int intputDrops) {
		this.inputDrops = intputDrops;
	}

	public int getInputFlushes() {
		return inputFlushes;
	}

	public void setInputFlushes(int inputFlushes) {
		this.inputFlushes = inputFlushes;
	}

	public int getOutputSize() {
		return outputSize;
	}

	public void setOutputSize(int outputSize) {
		this.outputSize = outputSize;
	}

	public int getOutputMax() {
		return outputMax;
	}

	public void setOutputMax(int outputMax) {
		this.outputMax = outputMax;
	}

	public int getOutputDrops() {
		return outputDrops;
	}

	public void setOutputDrops(int outputDrops) {
		this.outputDrops = outputDrops;
	}

	public int getOutputFlushes() {
		return outputFlushes;
	}

	public void setOutputFlushes(int outputFlushes) {
		this.outputFlushes = outputFlushes;
	}

	public int getOutputThreshold() {
		return outputThreshold;
	}

	public void setOutputThreshold(int outputThreshold) {
		this.outputThreshold = outputThreshold;
	}

	public int getAvailBandwidth() {
		return availBandwidth;
	}

	public void setAvailBandwidth(int availBandwidth) {
		this.availBandwidth = availBandwidth;
	}

	public String getCollecttime() {
		return collecttime;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

}
