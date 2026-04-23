package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysDevice;
import java.util.List;

/**
 * 设备 Service 接口
 */
public interface ISysDeviceService {

    /**
     * 根据 ID 查询设备信息
     *
     * @param deviceId 设备 ID
     * @return 设备信息
     */
    SysDevice selectSysDeviceById(Long deviceId);

    /**
     * 根据 UUID 查询设备信息
     *
     * @param deviceUuid 设备 UUID
     * @return 设备信息
     */
    SysDevice selectSysDeviceByUuid(String deviceUuid);

    /**
     * 查询设备列表
     *
     * @param sysDevice 设备信息
     * @return 设备集合
     */
    List<SysDevice> selectSysDeviceList(SysDevice sysDevice);

    /**
     * 新增设备
     *
     * @param sysDevice 设备信息
     * @return 结果
     */
    int insertSysDevice(SysDevice sysDevice);

    /**
     * 修改设备
     *
     * @param sysDevice 设备信息
     * @return 结果
     */
    int updateSysDevice(SysDevice sysDevice);

    /**
     * 批量删除设备
     *
     * @param deviceIds 需要删除的设备 ID
     * @return 结果
     */
    int deleteSysDeviceByIds(Long[] deviceIds);

    /**
     * 根据 ID 删除设备
     *
     * @param deviceId 设备 ID
     * @return 结果
     */
    int deleteSysDeviceById(Long deviceId);

    /**
     * 解绑设备
     *
     * @param deviceId 设备 ID
     * @return 结果
     */
    int unbindDevice(Long deviceId);

    /**
     * 更新设备当前用户
     *
     * @param deviceUuid 设备 UUID
     * @param currentUserId 当前用户 ID
     * @param currentUserName 当前用户名
     * @return 结果
     */
    int updateDeviceCurrentUser(String deviceUuid, Long currentUserId, String currentUserName);

    /**
     * 更新设备心跳
     *
     * @param deviceUuid 设备 UUID
     * @param status 状态 (0 离线 1 在线)
     */
    void updateHeartbeat(String deviceUuid, String status);

    /**
     * 检测并更新离线设备
     */
    void checkOfflineDevices();

    /**
     * 创建远程清除指令
     *
     * @param deviceUuid 设备 UUID
     */
    void createRemoteWipeCommand(String deviceUuid);
}
