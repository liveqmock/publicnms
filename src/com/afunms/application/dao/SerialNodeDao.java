
/*
 *	此类为 serialNode 类的dao类 对表 system_serial_node 进行操作
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.application.model.SerialNode;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;



public class SerialNodeDao extends BaseDao implements DaoInterface {
	
	public SerialNodeDao() {
		super("system_serial_node");
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		SerialNode serialNode = new SerialNode();
		try {
			serialNode.setId(rs.getInt("id"));
			serialNode.setAddress(rs.getString("address"));
			serialNode.setName(rs.getString("name"));
			serialNode.setDescription(rs.getString("description"));
			serialNode.setMonflag(rs.getString("monflag"));
			serialNode.setSerialPortId(rs.getString("serialPortId"));
			serialNode.setBaudRate(rs.getString("baudRate"));
			serialNode.setDatabits(rs.getString("databits"));
			serialNode.setStopbits(rs.getString("stopbits"));
			serialNode.setParity(rs.getString("parity"));
			serialNode.setBid(rs.getString("bid"));
			serialNode.setSendMail(rs.getString("sendmail"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return serialNode;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		SerialNode serialNode = (SerialNode)vo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_serial_node(id,name,address,description,monflag,serialportid,baudrate,databits,stopbits,parity,bid,sendmail)values(");
		sql.append("'");
		sql.append(serialNode.getId());
		sql.append("','");
		sql.append(serialNode.getName());	
		sql.append("','");
		sql.append(serialNode.getAddress());
		sql.append("','");
		sql.append(serialNode.getDescription());
		sql.append("','");
		sql.append(serialNode.getMonflag());
		sql.append("','");
		sql.append(serialNode.getSerialPortId());
		sql.append("','");
		sql.append(serialNode.getBaudRate());
		sql.append("','");
		sql.append(serialNode.getDatabits());
		sql.append("','");
		sql.append(serialNode.getStopbits());
		sql.append("','");
		sql.append(serialNode.getParity());
		sql.append("','");
		sql.append(serialNode.getBid());
		sql.append("','");
		sql.append(serialNode.getSendMail());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public List findByMonflag(String monflag){
		String sql = "select * from system_serial_node where monflag = " + monflag;
		return findByCriteria(sql);
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		if(vo == null){
			return false;
		}
		SerialNode serialNode = (SerialNode)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update system_serial_node set address='");
		sql.append(serialNode.getAddress());
		sql.append("',name='");
		sql.append(serialNode.getName());
		sql.append("',description='");
		sql.append(serialNode.getDescription());
		sql.append("',monflag='");
		sql.append(serialNode.getMonflag());
	   	sql.append("',serialportid='");
	   	sql.append(serialNode.getSerialPortId());
	   	sql.append("',baudrate='");
	   	sql.append(serialNode.getBaudRate());
	   	sql.append("',databits='");
	   	sql.append(serialNode.getDatabits());
	   	sql.append("',stopbits='");
	   	sql.append(serialNode.getStopbits());
	   	sql.append("',parity='");
	   	sql.append(serialNode.getParity());
	   	sql.append("',bid='");
	   	sql.append(serialNode.getBid());
	   	sql.append("',sendmail='");
	   	sql.append(serialNode.getSendMail());
	   	sql.append("' where id=");
	   	sql.append(serialNode.getId());
	   	return saveOrUpdate(sql.toString());
	}

}
