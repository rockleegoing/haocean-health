package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.mapper.SysDeviceMapper;
import com.ruoyi.system.service.ISysDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 设备 Service 实现类
 */
@Service
public class SysDeviceServiceImpl implements ISysDeviceService {

    @Autowired
    private SysDeviceMapper deviceMapper;

    @Override
    public SysDevice selectSysDeviceById(Long deviceId) {
        return deviceMapper.selectSysDeviceById(deviceId);
    }

    @Override
    public SysDevice selectSysDeviceByUuid(String deviceUuid) {
        return deviceMapper.selectSysDeviceByUuid(deviceUuid);
    }

    @Override
    public List<SysDevice> selectSysDeviceList(SysDevice sysDevice) {
        return deviceMapper.selectSysDeviceList(sysDevice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysDevice(SysDevice sysDevice) {
        sysDevice.setCreateTime(DateUtils.getNowDate());
        return deviceMapper.insertSysDevice(sysDevice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysDevice(SysDevice sysDevice) {
        sysDevice.setUpdateTime(DateUtils.getNowDate());
        return deviceMapper.updateSysDevice(sysDevice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysDeviceByIds(Long[] deviceIds) {
        return deviceMapper.deleteSysDeviceByIds(deviceIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysDeviceById(Long deviceId) {
        return deviceMapper.deleteSysDeviceById(deviceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unbindDevice(Long deviceId) {
        SysDevice device = deviceMapper.selectSysDeviceById(deviceId);
        if (device == null) {
            throw new IllegalArgumentException("设备不存在");
        }

        // 解绑设备：清除绑定信息
        device.setCurrentUserId(null);
        device.setCurrentUserName(null);
        device.setActivationCodeId(null);
        device.setUpdateTime(DateUtils.getNowDate());

        return deviceMapper.updateSysDevice(device);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDeviceCurrentUser(String deviceUuid, Long currentUserId, String currentUserName) {
        return deviceMapper.updateDeviceCurrentUser(deviceUuid, currentUserId, currentUserName);
    }
}
