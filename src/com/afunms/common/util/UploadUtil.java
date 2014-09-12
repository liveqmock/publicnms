package com.afunms.common.util;

import java.io.File;

public class UploadUtil {
	public boolean isExist(String realPath) {
		boolean flag = false;
		File file = null;
		try {
			file = new File(realPath);
			if (file.exists()) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
}
