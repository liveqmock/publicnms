package com.afunms.automation.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.automation.model.TimingBackupCondition;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

public class TimingBackupConditionDao extends BaseDao implements DaoInterface{
	  public  TimingBackupConditionDao() {
			super("sys_timingbackup_condition");
		}
			@Override
			public BaseVo loadFromRS(ResultSet rs) {
				TimingBackupCondition vo=new TimingBackupCondition();
				try {
					vo.setId(rs.getInt("id"));
					vo.setTimingId(rs.getInt("timingId"));
					vo.setIsContain(rs.getInt("isContain"));
					vo.setContent(rs.getString("content"));
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				return vo;
			}

			public boolean save(BaseVo vo) {
				StringBuffer sqlBuffer = new StringBuffer();
				TimingBackupCondition condition=(TimingBackupCondition)vo;
				sqlBuffer.append("insert into sys_timingbackup_condition(timingId,isContain,content)  values (");
				sqlBuffer.append(condition.getTimingId());
				sqlBuffer.append(",");
				sqlBuffer.append(condition.getIsContain());
				sqlBuffer.append(",'");
				sqlBuffer.append(condition.getContent());
				sqlBuffer.append("')");
				return saveOrUpdate(sqlBuffer.toString());
			}
		    public void addBatch(BaseVo vo,int id) {
		    	StringBuffer sqlBuffer = new StringBuffer();
		    	
		    	if(id==-2){
				TimingBackupCondition condition=(TimingBackupCondition)vo;
				sqlBuffer.append("insert into sys_timingbackup_condition(timingId,isContain,content)  values (");
				sqlBuffer.append(condition.getTimingId());
				sqlBuffer.append(",");
				sqlBuffer.append(condition.getIsContain());
				sqlBuffer.append(",'");
				sqlBuffer.append(condition.getContent());
				sqlBuffer.append("')");
		    	}else {
		        		sqlBuffer.append("delete from sys_timingbackup_condition where timingId="+id);
				}
		    	System.out.println("///////////"+sqlBuffer.toString());
				conn.addBatch(sqlBuffer.toString());
			}
		    public void executeBatch() {
		    	
		    	conn.executeBatch();
				
		    }
			public boolean update(BaseVo vo) {
				// TODO Auto-generated method stub
				return false;
			}

		}

