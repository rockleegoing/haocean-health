package com.ruoyi.system.domain.vo;

import java.io.Serializable;

/**
 * App 激活码验证响应 DTO
 */
public class ActivationApiResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 验证结果是否成功 */
    private Boolean success;

    /** 消息 */
    private String message;

    /** 激活码 ID */
    private Long codeId;

    /** 已激活设备数 */
    private Integer activatedCount;

    /** 最大设备数 */
    private Integer maxDeviceCount;

    /** 过期时间（时间戳） */
    private Long expiryTime;

    /** 激活时间（时间戳） */
    private Long activateTime;

    /** 激活设备型号 */
    private String activateDevice;

    /** 有效期天数 */
    private Integer validDays;

    public ActivationApiResponse() {
    }

    public ActivationApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ActivationApiResponse(Boolean success, String message, Long codeId,
            Integer activatedCount, Integer maxDeviceCount, Long expiryTime,
            Long activateTime, String activateDevice, Integer validDays) {
        this.success = success;
        this.message = message;
        this.codeId = codeId;
        this.activatedCount = activatedCount;
        this.maxDeviceCount = maxDeviceCount;
        this.expiryTime = expiryTime;
        this.activateTime = activateTime;
        this.activateDevice = activateDevice;
        this.validDays = validDays;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCodeId() {
        return codeId;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public Integer getActivatedCount() {
        return activatedCount;
    }

    public void setActivatedCount(Integer activatedCount) {
        this.activatedCount = activatedCount;
    }

    public Integer getMaxDeviceCount() {
        return maxDeviceCount;
    }

    public void setMaxDeviceCount(Integer maxDeviceCount) {
        this.maxDeviceCount = maxDeviceCount;
    }

    public Long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public Long getActivateTime() {
        return activateTime;
    }

    public void setActivateTime(Long activateTime) {
        this.activateTime = activateTime;
    }

    public String getActivateDevice() {
        return activateDevice;
    }

    public void setActivateDevice(String activateDevice) {
        this.activateDevice = activateDevice;
    }

    public Integer getValidDays() {
        return validDays;
    }

    public void setValidDays(Integer validDays) {
        this.validDays = validDays;
    }
}
