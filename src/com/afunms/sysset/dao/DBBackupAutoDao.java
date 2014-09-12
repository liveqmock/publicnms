package com.afunms.sysset.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;
import com.afunms.sysset.model.DBBackup;
import com.afunms.sysset.model.DBBackupAuto;

/**
 * 自动备份数据库文件
 * @author HONGLI  Sep 8, 2011
 */
public class DBBackupAutoDao extends BaseDao implements DaoInterface
{
	private String username = "root";   // 备份时用的数据库帐号,默认是 root
	private String password = "root";   // 备份时用的密码,默认是 root
	private String database = "afunms"; // 需要备份的数据库名
	private String filepath = ResourceCenter.getInstance().getSysPath() + "WEB-INF/dbbackup/"; // 备份的路径地址
	private String filename = null;			//备份的文件名
	
	
	public DBBackupAutoDao() {
		super("nms_dbbackup_auto");
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * @param database the database to set
	 */
	public void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * @return the filepath
	 */
	public String getFilepath() {
		return filepath;
	}

	/**
	 * @param filepath the filepath to set
	 */
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	// 查询数据库中所有的表名
	public List<String> findByCriteria(String sql) 
	{
		List<String> list = new ArrayList<String>();
		try 
		{
			rs = conn.executeQuery(sql);
			if (rs == null)
				return null;
			while (rs.next()) 
			{
				list.add(rs.getString(1));
			}
		} 
		catch (Exception e)
		{
			list = null;
			SysLogger.error("DBBackupDao.findByCriteria()", e);
		} 
		finally 
		{
			conn.close();
		}
		return list;
	}
	/**
	 * 数据库备份
	 * @param tables    需要备份的表数组
	 * @param radio     备份的类型
	 * @param filename  文件名
	 * @return
	 */ 
	public boolean backup(String[] tables, int radio, String filename) {
		getUserAndPass();
		String option = null; // 备份数据库所需的参数
		boolean result = false;
		if (radio == 0){ // 只备份表结构
			option = " --opt -d";
		} else if (radio == 1){ // 只备份表数据
			option = " --opt -t";
		} else{ // 备份表结构和数据
			option = " --opt";
		}
		// 注意mysqldump是调用mysql数据库的一个组件，在未在系统变量中声明的话，要在这里写mysqldump的完整路径
		int count = tables.length / 10;
		FileOutputStream fout = null;
		OutputStreamWriter writer = null;
		try {
			File file = new File(filepath); // 判断路径是否存在，若不存在则创建
			if (!file.exists()) {
				file.mkdir();
			}
			fout = new FileOutputStream(filepath + filename + ".sql", true);
			writer = new OutputStreamWriter(fout, "utf8");
			for (int j = 0; j <= count; j++) {
				// 如果表太多 可能造成 mysqldump 命令语句过长 导致 mysqldump 不能正确执行 故每十个表执行一次
				StringBuffer sb = new StringBuffer();
				sb.append("mysqldump ");
				sb.append(" --add-drop-table ");
				sb.append(database);
				boolean flag = false;//是否表名称为空  
				for (int i = j * 10; i < tables.length && i < j * 10 + 10; i++) {
					sb.append(" ");
					if(tables[i] == null || tables[i].equals("")){
						continue;
					}
					flag = true;
					sb.append(tables[i]);
				}
				if(!flag){//过滤掉表明数组中，一部分值为空的字符串或者为空的情形产生的bug 
					continue;
				}
				sb.append(" -u ").append(username).append(" -p").append(password).append(option);
//				System.out.println(sb.toString());
				Process child = Runtime.getRuntime().exec(sb.toString());
				InputStream in = null;
				InputStreamReader xx = null;
				BufferedReader br = null;
				try {
					String inStr;
					String outStr;
					in = child.getInputStream();// 控制台的输出信息作为输入流
					xx = new InputStreamReader(in, "utf8");// 设置输出流编码为utf8。这里必须是utf8，否则从流中读入的是乱码
					// 组合控制台输出信息字符串
					br = new BufferedReader(xx);
					while ((inStr = br.readLine()) != null) {
						StringBuffer sbsql = new StringBuffer("");
						sbsql.append(inStr + "\r\n");
						outStr = sbsql.toString();// 备份出来的内容是一个字条串
						writer.write(outStr);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(writer != null){
						writer.flush();
					}
					if(in != null){
						in.close();
					}
					if(xx != null){
						xx.close();
					}
					if(br != null){
						br.close();
					}
					if(child != null){
						child.destroy();
					}
				}
			}
			result = true;
		} catch (IOException e) {
			SysLogger.error("DBBackupDao.backup()", e);
			result = false;
		} finally {
			conn.close();
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fout != null){
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	// 数据库文件导入
	public boolean load(String filename) { 
		boolean result = false;
        try {  
        	if(filename == null){
        		throw new IOException("文件路径为空");
        	}
        	File file = new File(filename);
        	if(!file.exists()){
        		throw new IOException("文件不存在");
        			
        	} 
            Runtime rt = Runtime.getRuntime();  
            System.out.println(filename);
            // 调用 mysql 的 cmd:  
            Process child = rt.exec("cmd /c mysql -u root -proot afunms " );  
           
            OutputStream out = child.getOutputStream();//控制台的输入信息作为输出流  
            String inStr;  
            
            String outStr;  
            BufferedReader br = new BufferedReader(new InputStreamReader(  
                    new FileInputStream(file), "utf8"));
            OutputStreamWriter writer = new OutputStreamWriter(out, "utf8");  
            while ((inStr = br.readLine()) != null) {  
            	StringBuffer sb = new StringBuffer();  
                sb.append(inStr + "\r\n");  
                outStr = sb.toString(); 
                writer.write(outStr); 
                 
            }  
            // 注：这里如果用缓冲方式写入文件的话，会导致中文乱码，用flush()方法则可以避免  
            writer.flush(); 
            // 别忘记关闭输入输出流  
            writer.close();  
            out.close();  
            br.close();
            
            System.out.println("/* Load OK! */");  
            result = true;
        } catch (Exception e) { 
        	result = false;
            e.printStackTrace();  
        }finally{
			conn.close();
		} 
        return result;
    } 
	
	//读取配置文件afunms.xml中的用户名和密码
	private void getUserAndPass()
	{
		try 
		{
			File file = new File( ResourceCenter.getInstance().getSysPath() + "WEB-INF/classes/afunms.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			NodeList nl = doc.getElementsByTagName("parameter");
			for (int i = 0; i < nl.getLength(); i++) 
			{
				if ("username".equals(doc.getElementsByTagName("name").item(i).getFirstChild().getNodeValue()))
				{
					username = doc.getElementsByTagName("value").item(i).getFirstChild().getNodeValue();
					continue;
				}
				if ("password".equals(doc.getElementsByTagName("name").item(i).getFirstChild().getNodeValue())) 
				{
					password = doc.getElementsByTagName("value").item(i).getFirstChild().getNodeValue();
				}
			}
		} 
		catch (Exception e) 
		{
			SysLogger.error("DBBackupDao.getUserAndPass()", e);
		}
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		DBBackupAuto dbBackupAuto = new DBBackupAuto();		
		try {
			dbBackupAuto.setId(rs.getInt("id"));
			dbBackupAuto.setFilename(rs.getString("filename"));
			dbBackupAuto.setTime(rs.getString("time"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dbBackupAuto;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		DBBackupAuto DBBackupAuto = (DBBackupAuto)vo;
		
        StringBuffer sb = new StringBuffer();
        sb.append("insert into nms_DBBackup_auto(filename,time)values(");
        sb.append("'");
        sb.append(DBBackupAuto.getFilename());
        sb.append("','");
        sb.append(DBBackupAuto.getTime());   
        sb.append("')");	        
        return saveOrUpdate(sb.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean delete(String id){
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_DBBackup_auto where id=" + id);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally{
			conn.close();
		}
		return result;
	}
}
