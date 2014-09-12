package com.bpm.system.utils;

public enum ProcessEnum {
	none("0","未知"),
	CLAIMTASK("1","已签收未处理任务"),
	UNCLAIMTASK("2","未签收任务"),
	FINISHEDPRO("3","本周已完成流程"),
	UNFINISHEDPRO("4","本周已办未完成流程");
	public String key;
	public String decp;
	private ProcessEnum(String _key , String _decp)
	{
		key=_key;
		decp=_decp;
	}
}
