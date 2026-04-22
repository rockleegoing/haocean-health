package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysDevice;
import java.util.List;

/**
 * 设备 Mapper 数据层
 */
public interface SysDeviceMapper {

    SysDevice selectSysDeviceById(Long deviceId);

    SysDevice selectSysDeviceByUuid(String deviceUuid);

    List<SysDevice> selectSysDeviceList(SysDevice sysDevice);

    int insertSysDevice(SysDevice sysDevice);

    int updateSysDevice(SysDevice sysDevice);

    int deleteSysDeviceById(Long deviceId);

    int deleteSysDeviceByIds(Long[] deviceIds);

    int updateDeviceCurrentUser(String deviceUuid, Long currentUserId, String currentUserName);
}
