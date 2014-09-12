package com.afunms.polling.snmp.sqlserver;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.SqldbconfigDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Sqldbconfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.JdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.gatherdb.GathersqlListManager;

public class collect_db {
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable dbHashValue = new Hashtable(); //存放db信息
		String sql = "";
		Hashtable sqlserverDataHash = ShareData.getSqlserverdata();
		
		JdbcUtil util = null;
		ResultSet rs = null;
		DBVo dbmonitorlist = null; 
		DBDao dbdao = new DBDao();
		try{
			String dbid = nodeGatherIndicators.getNodeid();
			dbmonitorlist = (DBVo)dbdao.findByID(dbid);
		}catch(Exception e){
			
		}finally{
			dbdao.close();
		}
		if(dbmonitorlist == null)return null;
		if(dbmonitorlist.getManaged() == 0)return null;
		DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
		String serverip = dbnode.getIpAddress();
		String username = dbnode.getUser();
		String port = dbnode.getPort();
		String dburl = "jdbc:jtds:sqlserver://" + serverip + ":" + port + ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
		String passwords;
		try {
			passwords = EncryptUtil.decode(dbnode.getPassword());
			util = new JdbcUtil(dburl, username, passwords);
			util.jdbc();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(serverip);
		String hexip = hex + ":" + dbmonitorlist.getAlias();
		
		if(sqlserverDataHash.get(serverip) == null){
			sqlserverDataHash.put(serverip, new Hashtable());
		}
		Hashtable sqlserverdata = (Hashtable)sqlserverDataHash.get(serverip);

		// 获取SQLSERVER的DB和日志文件信息
		try {
			sql = "select counter_name,instance_name,cntr_value from master.dbo.sysperfinfo where object_name ='SQLServer:Databases' and (counter_name='Data File(s) Size (KB)' or counter_name='Log File(s) Size (KB)') order by instance_name ";
			Hashtable retdatabase = new Hashtable();
			Hashtable retlogfile = new Hashtable();
			Vector names = new Vector();
			
			rs = util.stmt.executeQuery(sql);
			//ResultSetMetaData rsmd = rs.getMetaData();
			Hashtable alldatabase = new Hashtable();
			Hashtable alllogfile = new Hashtable();
			Hashtable database = new Hashtable();
			Hashtable logfile = new Hashtable();

			while (rs.next()) {
				database = new Hashtable();
				logfile = new Hashtable();
				Hashtable return_value = new Hashtable();
				String counter_name = rs.getString("counter_name")
						.toString();
				String instance_name = rs.getString("instance_name")
						.toString().trim();

				int cntr_value = rs.getInt("cntr_value");
				if (counter_name.trim()
						.equals("Data File(s) Size (KB)")) {
					// 数据文件大小
					/*--------------------------modify 2010-5-10--------------*/
					if (alldatabase.get(instance_name) == null) {
						database.put("dbname", instance_name);
						database.put("size", cntr_value / 1024);
						System.out.println("--------****-------"
								+ instance_name
								+ "--------****--------");
						System.out.println("--------**-------"
								+ cntr_value / 1024
								+ "--------**--------");
						// SysLogger.info("DB :
						// "+instance_name+"==="+cntr_value);
						alldatabase.put(instance_name, database);
						names.add(instance_name);
					} else {
						Hashtable preValue = (Hashtable) alldatabase
								.get(instance_name);
						int presize = (Integer) preValue.get("size");
						preValue.put("size", presize + cntr_value
								/ 1024);
					}
					/*--------------------------------------------------------*/

				} else if (counter_name.trim().equals(
						"Log File(s) Size (KB)")) {
					// LOG文件大小
					if (alllogfile.get(instance_name) == null) {
						logfile.put("logname", instance_name);
						logfile.put("size", cntr_value / 1024);
						// SysLogger.info("LOG :
						// "+instance_name+"==="+cntr_value);
						alllogfile.put(instance_name, logfile);
					} else {
						Hashtable preVal = (Hashtable) alllogfile
								.get(instance_name);
						int presize = (Integer) preVal.get("size");
						preVal.put("size", presize + cntr_value / 1024);
					}

				}
			}
			sql = "select B.name as dbName,A.name as logicName,A.fileid,size from master.dbo.sysaltfiles A,master.dbo.sysdatabases B where A.dbid=B.dbid order by B.name";
			try {
				rs = util.stmt.executeQuery(sql);
				//rsmd = rs.getMetaData();
				while (rs.next()) {
					String dbName = rs.getString("dbName").toString()
							.trim();
					// String logicName = rs.getString(1).toString();
					String field = rs.getString("fileid").toString();
					int usedsize = rs.getInt("size");
					float f_usedsize = new Float(usedsize);
					if ("1".equals(field)) {
						// 数据文件
						if (alldatabase.get(dbName) != null) {
							Hashtable _database = (Hashtable) alldatabase
									.get(dbName);
							int preused = 0;
							if (_database.get("usedsize") != null) {
								preused = Integer
										.parseInt((String) _database
												.get("usedsize"));
							}
							_database
									.put("usedsize", String
											.valueOf(usedsize / 1024
													+ preused));
							float allsize = new Float(_database.get(
									"size").toString());
							f_usedsize = usedsize / 1024 + preused;
							float f_usedperc = 100 * (f_usedsize / allsize);
							int usedperc = new Float(f_usedperc)
									.intValue();
							_database.put("usedperc", usedperc + "");
							// SysLogger.info("DB : usedperc :
							// "+usedperc);
							retdatabase.put(dbName, _database);

						}

					} else {
						// 日志文件
						if (alllogfile.get(dbName) != null) {
							Hashtable _logfile = (Hashtable) alllogfile
									.get(dbName);
							int preuse = 0;
							// //System.out.println(dbName);
							if (_logfile.get("usedsize") != null)
								preuse = Integer
										.parseInt((String) _logfile
												.get("usedsize"));

							_logfile.put("usedsize", String
									.valueOf(usedsize / 1024 + preuse));
							// //System.out.println(_logfile.get("usedsize"));
							f_usedsize = usedsize / 1024 + preuse;
							float allsize = new Float(_logfile.get(
									"size").toString());
							float f_usedperc = (100 * f_usedsize / (allsize));
							int usedperc = new Float(f_usedperc)
									.intValue();
							_logfile.put("usedperc", usedperc + "");
							// SysLogger.info("LOG : usedperc :
							// "+usedperc);
							retlogfile.put(dbName, _logfile);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			dbHashValue.put("database", retdatabase);
			dbHashValue.put("logfile", retlogfile);
			dbHashValue.put("names", names);
			//写入内存
			sqlserverdata.put("dbValue", dbHashValue);
			ShareData.setSqldbdata(serverip, dbHashValue);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			util.closeStmt();
			util.closeConn();
		}
		
		//存入内存
		//ShareData.setSqlserverdata(serverip, sqlserverDataHash);
		
		//存入数据库
		try {
			String deletesql = "delete from nms_sqlserverdbvalue where serverip = '" + hexip + "'";
			GathersqlListManager.Addsql(deletesql);
			Hashtable logfile = (Hashtable)dbHashValue.get("logfile");
			Hashtable database = (Hashtable)dbHashValue.get("database");
			Vector names = (Vector)dbHashValue.get("names");
			Iterator iter = logfile.entrySet().iterator(); 
			String label = "0";
			String insertsql = "";
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    String key = String.valueOf(entry.getKey()); 
			    Hashtable val = (Hashtable)entry.getValue(); 
			    insertsql = addSqlserver_nmsdbvalue(hexip,val,"",label);
			    GathersqlListManager.Addsql(insertsql);
			} 
			label = "1";
			iter = database.entrySet().iterator(); 
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    String key = String.valueOf(entry.getKey()); 
			    Hashtable val = (Hashtable)entry.getValue(); 
			    insertsql = addSqlserver_nmsdbvalue(hexip,val,"",label);
			    GathersqlListManager.Addsql(insertsql);
			} 
			label = "2";
			for(int i=0;i<names.size();i++){
				insertsql = addSqlserver_nmsdbvalue(hexip, null, String.valueOf(names.get(i)), label);
				GathersqlListManager.Addsql(insertsql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//告警
		NodeUtil nodeUtil = new NodeUtil();
	    NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dbmonitorlist);
		checkToAlarm(nodeDTO,dbHashValue);
		
		return sqlserverDataHash;
	}
	
	public void checkToAlarm(NodeDTO nodeDTO, Hashtable dbValue){
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
    	List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId()+"", nodeDTO.getType(), nodeDTO.getSubtype());
		CheckEventUtil checkEventUtil = new CheckEventUtil();
    	for(int i = 0 ; i < list.size(); i++){
    		AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
    		if ("tablespace".equalsIgnoreCase(alarmIndicatorsNode.getName())){

				if (dbValue != null && dbValue.size() > 0) {
					SqldbconfigDao sqldbconfigManager = new SqldbconfigDao();
					Hashtable alarmdbs = sqldbconfigManager.getByAlarmflag(1);
					sqldbconfigManager.close();
					Hashtable database = (Hashtable) dbValue.get("database");
					Hashtable logfile = (Hashtable) dbValue.get("logfile");
					Vector names = (Vector) dbValue.get("names");
					if (alarmdbs == null)
						alarmdbs = new Hashtable();
					if (database == null)
						database = new Hashtable();
					if (logfile == null)
						logfile = new Hashtable();
					if (names != null && names.size() > 0) {
						for (int k = 0; k < names.size(); k++) {
							String dbname = (String) names.get(k);
							if (database.get(dbname) != null) {
								Hashtable db = (Hashtable) database.get(dbname);
								String usedperc = (String) db.get("usedperc");
								if (alarmdbs.containsKey(nodeDTO.getIpaddress() + ":" + dbname + ":0")) {
									Sqldbconfig sqldbconfig = (Sqldbconfig) alarmdbs.get(nodeDTO.getIpaddress() + ":" + dbname + ":0");
									if (sqldbconfig == null)
										continue;
									if (usedperc == null)
										continue;
									alarmIndicatorsNode.setLimenvalue0(sqldbconfig.getAlarmvalue() + "");
									alarmIndicatorsNode.setLimenvalue1(sqldbconfig.getAlarmvalue() + "");
									alarmIndicatorsNode.setLimenvalue2(sqldbconfig.getAlarmvalue() + "");
									checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, usedperc+"",sqldbconfig.getDbname());
								}
							}
							if (logfile.get(dbname) != null) {
								Hashtable db = (Hashtable) logfile.get(dbname);
								String usedperc = (String) db.get("usedperc");
								if (alarmdbs.containsKey(nodeDTO.getIpaddress() + ":" + dbname + ":0")) {
									Sqldbconfig sqldbconfig = (Sqldbconfig) alarmdbs.get(nodeDTO.getIpaddress() + ":" + dbname + ":1");
									if (sqldbconfig == null)
										continue;
									alarmIndicatorsNode.setLimenvalue0(sqldbconfig.getAlarmvalue() + "");
									alarmIndicatorsNode.setLimenvalue1(sqldbconfig.getAlarmvalue() + "");
									alarmIndicatorsNode.setLimenvalue2(sqldbconfig.getAlarmvalue() + "");
									checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, usedperc+"",sqldbconfig.getDbname());
//									if (sqldbconfig.getAlarmvalue() < new Integer(usedperc)) {
//										// 告警
//										SysLogger.info("$$$ 开始告警 $$$");
//										dbnode = (DBNode) PollingEngine.getInstance()
//												.getDbByID(dbmonitorlist.getId());
//										dbnode.setAlarm(true);
//										dbnode.setStatus(3);
//										List alarmList = dbnode.getAlarmMessage();
//										if (alarmList == null)
//											alarmList = new ArrayList();
//										dbnode.getAlarmMessage().add(
//												sqldbconfig.getDbname() + "表空间超过阀值" + sqldbconfig.getAlarmvalue());
//									}
								}
							}
						}
					}
				}
			
    		}
    	}
	}
	
	public String addSqlserver_nmsdbvalue(String serverip, Hashtable scans, String instance_name, String label) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sBuffer = new StringBuffer();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);
			
			if (!"2".equals(label)) {
				sBuffer
						.append("insert into nms_sqlserverdbvalue(serverip, usedperc, usedsize, "
								+ "sizes, logname, dbname,label,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("usedperc")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("usedsize")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("size")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("logname")));
				sBuffer.append("','");
				sBuffer.append(String.valueOf(scans.get("dbname")));
				sBuffer.append("','");
				sBuffer.append(label);
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
			} else {
				sBuffer = new StringBuffer();
				sBuffer
						.append("insert into nms_sqlserverdbvalue(serverip,instance_name,label,mon_time)");
				sBuffer.append(" values('");
				sBuffer.append(serverip);
				sBuffer.append("','");
				sBuffer.append(instance_name);
				sBuffer.append("','");
				sBuffer.append(label);
				// sBuffer.append("','");
				// sBuffer.append(montime);
				// sBuffer.append("')");
				if (SystemConstant.DBType.equals("mysql")) {
					sBuffer.append("','");
					sBuffer.append(montime);
					sBuffer.append("')");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sBuffer.append("',to_date('" + montime
							+ "','yyyy-mm-dd hh24:mi:ss'))");
				}
			}
			// System.out.println(sBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sBuffer.toString();
	}
}




















