/**
 * <p>Description: active_server_alarm</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project 衡水信用社
 * @date 2007-3-23
 */

package com.afunms.alarm.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.alarm.model.IndicatorsTopoRelation;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

public class IndicatorsTopoRelationDao extends BaseDao implements DaoInterface{
    public IndicatorsTopoRelationDao(){
    	super("nms_indicators_topo_relation");
    }

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		IndicatorsTopoRelation relation = new IndicatorsTopoRelation();
		try {
			relation.setIndicatorsId(rs.getString("indicators_id"));
			relation.setSIndex(rs.getString("sindex"));
			relation.setTopoId(rs.getString("topo_id"));
			relation.setNodeid(rs.getString("nodeid"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return relation;
	}
	
	public boolean save(BaseVo vo) {
		
		IndicatorsTopoRelation relation = (IndicatorsTopoRelation)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_indicators_topo_relation(indicators_id,sindex,topo_id" +
				") values('");
		sql.append(relation.getIndicatorsId());
		sql.append("','");
		sql.append(relation.getSIndex());
		sql.append("','");
		sql.append(relation.getTopoId());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean deleteByTopoId(String topoId){
		StringBuffer sql = new StringBuffer();
		sql.append("delete from nms_indicators_topo_relation where topo_id='" + topoId + "'");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean deleteByIndicatorsId(String indicatorsId){
		StringBuffer sql = new StringBuffer();
		sql.append("delete from nms_indicators_topo_relation where indicators_id='" + indicatorsId + "'");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean deleteByIndicatorsIdAndSindex(String indicatorsId, String sindex) {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from nms_indicators_topo_relation where indicators_id='" + indicatorsId + "' and sindex='" + sindex + "'");
		return saveOrUpdate(sql.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<IndicatorsTopoRelation> findByTopoId(String topoId,String nodeid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_indicators_topo_relation where topo_id='" + topoId + "' and nodeid='" + nodeid + "'");
		List<IndicatorsTopoRelation> findByCriteria = findByCriteria(sql.toString());
		return findByCriteria;
	}
	
	public boolean deleteByTopoIdAndNodeId(String topoId,String nodeid) {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from nms_indicators_topo_relation where topo_id='" + topoId + "' and nodeid='" + nodeid + "'");
		return saveOrUpdate(sql.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<IndicatorsTopoRelation> findByIndicatorsId(String indicatorsId) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_indicators_topo_relation where indicators_id='" + indicatorsId + "'");
		List<IndicatorsTopoRelation> findByCriteria = findByCriteria(sql.toString());
		return findByCriteria;
	}
	
	@SuppressWarnings("unchecked")
	public List<IndicatorsTopoRelation> findByIndicatorsIdAndSindex(String indicatorsId, String sindex) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_indicators_topo_relation where indicators_id='" + indicatorsId + "' and sindex='" + sindex + "'");
		List<IndicatorsTopoRelation> findByCriteria = findByCriteria(sql.toString());
		return findByCriteria;
	}
	
	public List<IndicatorsTopoRelation> findByTopoIndicatorsIdAndSindex(String topoId,String indicatorsId, String sindex) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_indicators_topo_relation where topo_id='" + topoId + "' and indicators_id='" + indicatorsId + "' and sindex='" + sindex + "'");
		List<IndicatorsTopoRelation> findByCriteria = findByCriteria(sql.toString());
		return findByCriteria;
	}
	
	public List<IndicatorsTopoRelation> findByTopoAndIndicatorsId(String topoId,String indicatorsId) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_indicators_topo_relation where topo_id='" + topoId + "' and indicators_id='" + indicatorsId + "'");
		List<IndicatorsTopoRelation> findByCriteria = findByCriteria(sql.toString());
		return findByCriteria;
	}
	public List<IndicatorsTopoRelation> findByTopoAndNodeId(String topoId,String nodeid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_indicators_topo_relation where topo_id='" + topoId + "' and nodeid='" + nodeid + "'");
		List<IndicatorsTopoRelation> findByCriteria = findByCriteria(sql.toString());
		return findByCriteria;
	}
	
	public boolean save(List<IndicatorsTopoRelation> list) {
		boolean result = false;
		try {
			for(int i = 0 ; i < list.size(); i++){
				IndicatorsTopoRelation relation = list.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append("insert into nms_indicators_topo_relation(indicators_id,sindex,topo_id,nodeid" +
						") values('");
				sql.append(relation.getIndicatorsId());
				sql.append("','");
				sql.append(relation.getSIndex());
				sql.append("','");
				sql.append(relation.getTopoId());
				sql.append("','");
				sql.append(relation.getNodeid());
				sql.append("')");
				conn.addBatch(sql.toString());
			}
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	public boolean update(BaseVo vo) {
		return false;
	}
	
   
}   