package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 设备对象 sys_device
 */
public class SysDevice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    private Long deviceId;

    /** 设备唯一标识 */
    private String deviceUuid;

    /** 设备名称 */
    private String deviceName;

    /** 设备型号 */
    private String deviceModel;

    /** 操作系统 */
    private String deviceOs;

    /** App 版本 */
    private String appVersion;

    /** 当前登录用户 ID */
    private Long currentUserId;

    /** 当前登录用户名（冗余字段，便于查询） */
    private String currentUserName;

    /** 激活码 ID */
    private Long activationCodeId;

    /** 最后同步时间 */
    private Date lastSyncTime;

    /** 最后登录时间 */
    private Date lastLoginTime;

    /** 最后登录 IP */
    private String lastLoginIp;

    /** 状态（0 离线 1 在线） */
    private String status;

    /** 备注 */
    private String remark;

    // Getters and Setters
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    public String getDeviceUuid() { return deviceUuid; }
    public void setDeviceUuid(String deviceUuid) { this.deviceUuid = deviceUuid; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getDeviceOs() { return deviceOs; }
    public void setDeviceOs(String deviceOs) { this.deviceOs = deviceOs; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }

    public Long getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(Long currentUserId) { this.currentUserId = currentUserId; }

    public String getCurrentUserName() { return currentUserName; }
    public void setCurrentUserName(String currentUserName) { this.currentUserName = currentUserName; }

    public Long getActivationCodeId() { return activationCodeId; }
    public void setActivationCodeId(Long activationCodeId) { this.activationCodeId = activationCodeId; }

    public Date getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(Date lastSyncTime) { this.lastSyncTime = lastSyncTime; }

    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }

    public String getLastLoginIp() { return lastLoginIp; }
    public void setLastLoginIp(String lastLoginIp) { this.lastLoginIp = lastLoginIp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    /** 状态文本（用于前端显示） */
    public String getStatusText() {
        if (this.status == null) {
            return "未知";
        }
        switch (this.status) {
            case "0": return "离线";
            case "1": return "在线";
            default: return "未知";
        }
    }
}
