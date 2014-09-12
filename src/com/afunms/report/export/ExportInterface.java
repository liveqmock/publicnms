/**
 * @author sunqichang/孙启昌
 * Created on Jun 1, 2011 4:34:24 PM
 */
package com.afunms.report.export;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.lowagie.text.DocumentException;

/**
 * @author sunqichang/孙启昌
 * 
 * 导出接口
 */
public interface ExportInterface {

	/**
	 * 插入标题
	 * 
	 * @param title
	 *            标题
	 * @param cols
	 *            列数
	 * @param timefromto
	 *            时间
	 */
	public void insertTitle(String title, int cols, String timefromto) throws Exception;

	/**
	 * 插入表格
	 * 
	 * @param tableal
	 *            表格ArrayList<String[]>
	 * @throws IOException
	 */
	public void insertTable(ArrayList<String[]> tableal) throws Exception;

	/**
	 * 插入图片
	 * 
	 * @param path
	 *            图片路径
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void insertChart(String path) throws Exception;

	/**
	 * 保存文件
	 * 
	 * @throws Exception
	 */
	public void save() throws Exception;

}
