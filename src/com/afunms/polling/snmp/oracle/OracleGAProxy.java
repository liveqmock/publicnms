package com.afunms.polling.snmp.oracle;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * Oracle PGA信息 采集 使用JDBC采集
 * 
 * @author wupinlong 2012/09/17
 * 
 */
public class OracleGAProxy extends SnmpMonitor {

	public OracleGAProxy() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		String htKey = "ga_hash";
		List dbmonitorlists = new ArrayList();
		dbmonitorlists = ShareData.getDBList();
		DBVo dbmonitorlist = new DBVo();
		if (dbmonitorlists != null && dbmonitorlists.size() > 0) {
			for (int i = 0; i < dbmonitorlists.size(); i++) {
				DBVo vo = (DBVo) dbmonitorlists.get(i);
				if (vo.getId() == Integer.parseInt(nodeGatherIndicators
						.getNodeid())) {
					dbmonitorlist = vo;
					break;
				}
			}
		}
		if (dbmonitorlist != null) {
			if (dbmonitorlist.getManaged() == 0) {
				// 如果未被管理，不采集，user信息为空
				return returndata;
			}
			String serverip = dbmonitorlist.getIpAddress();
			String[] args = new String[]{"aggregate_pga_auto_target",
										"total_pga_used_for_manual_workareas",
										"total_pga_inuse",
										"maximum_pga_allocated",
										"cache_hit_percentage",
										"recycle_buffer_cache",
										"keep_buffer_cache",
										"process_count",
										"total_pga_used_for_auto_workareas",
										"asm_buffer_cache",
										"over_allocation_count",
										"bytes_processed",
										"java_pool",
										"maximum_pga_used_for_manual_workareas",
										"streams_pool",
										"default_2k_buffer_cache",
										"max_processes_count",
										"total_pga_allocated",
										"default_4k_buffer_cache",
										"shared_pool",
										"default_32k_buffer_cache",
										"default_buffer_cache",
										"large_pool",
										"aggregate_pga_target_parameter",
										"default_16k_buffer_cache",
										"global_memory_bound",
										"default_8k_buffer_cache",
										"extra_bytes_read_written",
										"pga_memory_freed_back_to_os",
										"total_freeable_pga_memory",
										"recompute_count_total",
										"maximum_pga_used_for_auto_workareas",
										"mon_time"};
			returndata = LogParser.parse(this, dbmonitorlist, htKey, args);

			// 更新内存
			if (!ShareData.getSharedata().containsKey(
					dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getId())) {
				ShareData.getSharedata().put(
						dbmonitorlist.getIpAddress() + ":"
								+ dbmonitorlist.getId(), returndata);
			} else {
				Hashtable oracleHash = (Hashtable) ShareData.getSharedata()
						.get(
								dbmonitorlist.getIpAddress() + ":"
										+ dbmonitorlist.getId());
				oracleHash.put("memValue", returndata.get(htKey));
			}

			// ----------------------------------保存到数据库及告警 start
			Hashtable gahash = null;
			Vector extent_v = (Vector) returndata.get(htKey);
			if (extent_v != null && !extent_v.isEmpty()) {
				gahash = (Hashtable) extent_v.get(0);
			}
			
			if (gahash != null && !gahash.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbmonitorlist
						.getIpAddress());
				serverip = hex + ":" + dbmonitorlist.getId();
				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_oramemvalue where serverip='"
							+ serverip + "'";
					GathersqlListManager.Addsql(deletesql);

					StringBuffer sBuffer = new StringBuffer();
					String aggregate_pga_auto_target = String.valueOf(gahash
							.get("aggregate_pga_auto_target"));
					String total_pga_used_for_manual_workareas = String
							.valueOf(gahash
									.get("total_pga_used_for_manual_workareas"));
					String total_pga_inuse = String.valueOf(gahash
							.get("total_pga_inuse"));
					String maximum_pga_allocated = String.valueOf(gahash
							.get("maximum_pga_allocated"));
					String cache_hit_percentage = String.valueOf(gahash
							.get("cache_hit_percentage"));
					String recycle_buffer_cache = String.valueOf(gahash
							.get("recycle_buffer_cache"));
					String keep_buffer_cache = String.valueOf(gahash
							.get("keep_buffer_cache"));
					String process_count = String.valueOf(gahash
							.get("process_count"));
					String total_pga_used_for_auto_workareas = String
							.valueOf(gahash
									.get("total_pga_used_for_auto_workareas"));
					String asm_buffer_cache = String.valueOf(gahash
							.get("asm_buffer_cache"));
					String over_allocation_count = String.valueOf(gahash
							.get("over_allocation_count"));
					String bytes_processed = String.valueOf(gahash
							.get("bytes_processed"));
					String java_pool = String.valueOf(gahash.get("java_pool"));
					String maximum_pga_used_for_manual_workareas = String
							.valueOf(gahash
									.get("maximum_pga_used_for_manual_workareas"));
					String streams_pool = String.valueOf(gahash
							.get("streams_pool"));
					String default_2k_buffer_cache = String.valueOf(gahash
							.get("DEFAULT_2K_buffer_cache".toLowerCase()));
					String max_processes_count = String.valueOf(gahash
							.get("max_processes_count"));
					String total_pga_allocated = String.valueOf(gahash
							.get("total_PGA_allocated".toLowerCase()));
					String default_4k_buffer_cache = String.valueOf(gahash
							.get("DEFAULT_4K_buffer_cache".toLowerCase()));
					String shared_pool = String.valueOf(gahash
							.get("shared_pool"));
					String default_32k_buffer_cache = String.valueOf(gahash
							.get("DEFAULT_32K_buffer_cache".toLowerCase()));
					String default_buffer_cache = String.valueOf(gahash
							.get("DEFAULT_buffer_cache".toLowerCase()));
					String large_pool = String
							.valueOf(gahash.get("large_pool"));
					String aggregate_pga_target_parameter = String
							.valueOf(gahash
									.get("aggregate_PGA_target_parameter".toLowerCase()));
					String default_16k_buffer_cache = String.valueOf(gahash
							.get("DEFAULT_16K_buffer_cache".toLowerCase()));
					String global_memory_bound = String.valueOf(gahash
							.get("global_memory_bound"));
					String default_8k_buffer_cache = String.valueOf(gahash
							.get("DEFAULT_8K_buffer_cache".toLowerCase()));
					String extra_bytes_read_written = String.valueOf(gahash
							.get("extra_bytes_read_written"));
					String pga_memory_freed_back_to_os = String.valueOf(gahash
							.get("PGA_memory_freed_back_to_OS".toLowerCase()));
					String total_freeable_pga_memory = String.valueOf(gahash
							.get("total_freeable_PGA_memory".toLowerCase()));
					String recompute_count_total = String.valueOf(gahash
							.get("recompute_count_(total)"));
					String maximum_pga_used_for_auto_workareas = String
							.valueOf(gahash
									.get("maximum_PGA_used_for_auto_workareas".toLowerCase()));

					sBuffer
							.append("insert into nms_oramemvalue(serverip, agg_pga_auto_target, tpga_used_manu_workareas, total_pga_inuse, "
									+ "maximum_pga_allocated,cache_hit_percentage,recycle_buffer_cache,keep_buffer_cache,process_count ,tpga_used_auto_workareas,"
									+ "asm_buffer_cache,over_allocation_count,bytes_processed,java_pool,maxpga_used_manu_workareas,streams_pool,"
									+ "default_2k_buffer_cache,max_processes_count,total_pga_allocated,default_4k_buffer_cache,shared_pool,"
									+ "default_32k_buffer_cache,default_buffer_cache,large_pool,agg_pga_target_parameter ,default_16k_buffer_cache,"
									+ "global_memory_bound,default_8k_buffer_cache,extra_bytes_read_written,pga_mem_freed_back_os,tot_free_pga_memory,recom_count_total,"
									+ "maxpga_used_auto_workareas,mon_time) ");
					sBuffer.append(" values('");
					sBuffer.append(serverip);
					sBuffer.append("','");
					sBuffer.append(aggregate_pga_auto_target);
					sBuffer.append("','");
					sBuffer.append(total_pga_used_for_manual_workareas);
					sBuffer.append("','");
					sBuffer.append(total_pga_inuse);
					sBuffer.append("','");
					sBuffer.append(maximum_pga_allocated);
					sBuffer.append("','");
					sBuffer.append(cache_hit_percentage);
					sBuffer.append("','");
					sBuffer.append(recycle_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(keep_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(process_count);
					sBuffer.append("','");
					sBuffer.append(total_pga_used_for_auto_workareas);
					sBuffer.append("','");
					sBuffer.append(asm_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(over_allocation_count);
					sBuffer.append("','");
					sBuffer.append(bytes_processed);
					sBuffer.append("','");
					sBuffer.append(java_pool);
					sBuffer.append("','");
					sBuffer.append(maximum_pga_used_for_manual_workareas);
					sBuffer.append("','");
					sBuffer.append(streams_pool);
					sBuffer.append("','");
					sBuffer.append(default_2k_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(max_processes_count);
					sBuffer.append("','");
					sBuffer.append(total_pga_allocated);
					sBuffer.append("','");
					sBuffer.append(default_4k_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(shared_pool);
					sBuffer.append("','");
					sBuffer.append(default_32k_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(default_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(large_pool);
					sBuffer.append("','");
					sBuffer.append(aggregate_pga_target_parameter);
					sBuffer.append("','");
					sBuffer.append(default_16k_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(global_memory_bound);
					sBuffer.append("','");
					sBuffer.append(default_8k_buffer_cache);
					sBuffer.append("','");
					sBuffer.append(extra_bytes_read_written);
					sBuffer.append("','");
					sBuffer.append(pga_memory_freed_back_to_os);
					sBuffer.append("','");
					sBuffer.append(total_freeable_pga_memory);
					sBuffer.append("','");
					sBuffer.append(recompute_count_total);
					sBuffer.append("','");
					sBuffer.append(maximum_pga_used_for_auto_workareas);
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("','");
						sBuffer.append(montime);
						sBuffer.append("')");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sBuffer.append("',");
						sBuffer.append("to_date('" + montime
								+ "','YYYY-MM-DD HH24:MI:SS')");
						sBuffer.append(")");
					}
					GathersqlListManager.Addsql(sBuffer.toString());
					// ---------------------------------SGA及PGA不需告警
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// ----------------------------------保存到数据库及告警 end

		}
		return returndata;
	}

}
