package com.afunms.alarm.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.base.Node;

/**
 * ClassName:   NodeAlarmService.java
 * <p>{@link NodeAlarmService} 用于获取设备当前告警信息的服务类
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Feb 19, 2013 5:47:36 PM
 */
public class NodeAlarmService {

    /**
     * getMaxAlarmLevel:
     * <p>获取列表中设备的最高告警等级
     *
     * @param   list
     *          - 设备列表
     * @return  {@link Integer}
     *          - 列表中设备的最高告警等级
     *
     * @since   v1.01
     */
    public int getMaxAlarmLevel(List<NodeDTO> list) {
        int maxAlarmLevel = 0;
        if (list == null || list.size() == 0) {
            return maxAlarmLevel;
        }
        Hashtable<String, Integer> checkEventHashtable = ShareData.getCheckEventHash();
        Set<String> keySet = checkEventHashtable.keySet();
        for (NodeDTO nodeDTO : list) {
            String nodeKey = nodeDTO.getNodeid() + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
            for (String key : keySet) {
                if (key.contains(nodeKey)) {
                    int alarmLevel = checkEventHashtable.get(key);
                    if (alarmLevel > maxAlarmLevel) {
                        maxAlarmLevel = alarmLevel;
                    }
                }
            }
            
        }
        return maxAlarmLevel;
    }

    /**
     * getMaxAlarmLevel:
     * <p>获取设备的最高告警等级
     *
     * @param   nodeDTO
     *          - 设备
     * @return  {@link Integer}
     *          - 列表中设备的最高告警等级
     *
     * @since   v1.01
     */
    public int getMaxAlarmLevel(NodeDTO nodeDTO) {
        List<NodeDTO> list = new ArrayList<NodeDTO>();
        list.add(nodeDTO);
        return getMaxAlarmLevel(list);
    }

    /**
     * getMaxAlarmLevel:
     * <p>获取设备的最高告警等级
     *
     * @param   vo
     *          - 设备
     * @return  {@link Integer}
     *          - 列表中设备的最高告警等级
     *
     * @since   v1.01
     */
    public int getMaxAlarmLevel(BaseVo vo) {
        NodeUtil util = new NodeUtil();
        NodeDTO node = util.conversionToNodeDTO(vo);
        return getMaxAlarmLevel(node);
    }

    /**
     * getMaxAlarmLevel:
     * <p>获取设备的最高告警等级
     *
     * @param   vo
     *          - 设备
     * @return  {@link Integer}
     *          - 列表中设备的最高告警等级
     *
     * @since   v1.01
     */
    public int getMaxAlarmLevel(Node vo) {
        NodeUtil util = new NodeUtil();
        NodeDTO node = util.conversionToNodeDTO(vo);
        return getMaxAlarmLevel(node);
    } 
    /**
     * @param args
     */
    public static void main(String[] args) {
    }

}
