package com.bpm.system.utils;

public enum ExportExcelEnum {
	none("none","未知"),
	process_statistical("process_statistical","流程报表统计");
	public String key;
	public String decp;
	private ExportExcelEnum(String _key , String _decp)
	{
		key=_key;
		decp=_decp;
	}

}
