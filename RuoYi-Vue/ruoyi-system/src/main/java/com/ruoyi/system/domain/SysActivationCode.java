package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 激活码对象 sys_activation_code
 */
public class SysActivationCode extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    private Long codeId;

    /** 激活码值 */
    private String codeValue;

    /** 状态（0 未使用 1 已使用 2 已过期 3 已禁用） */
    private String status;

    /** 有效期 */
    private Date expireTime;

    /** 绑定设备 ID */
    private String bindDeviceId;

    /** 绑定用户 ID */
    private Long bindUserId;

    /** 最大允许设备数 */
    private Integer maxDeviceCount;

    /** 已激活设备数 */
    private Integer activatedCount;

    /** 备注 */
    private String remark;

    // Getters and Setters
    public Long getCodeId() { return codeId; }
    public void setCodeId(Long codeId) { this.codeId = codeId; }

    public String getCodeValue() { return codeValue; }
    public void setCodeValue(String codeValue) { this.codeValue = codeValue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }

    public String getBindDeviceId() { return bindDeviceId; }
    public void setBindDeviceId(String bindDeviceId) { this.bindDeviceId = bindDeviceId; }

    public Long getBindUserId() { return bindUserId; }
    public void setBindUserId(Long bindUserId) { this.bindUserId = bindUserId; }

    public Integer getMaxDeviceCount() { return maxDeviceCount; }
    public void setMaxDeviceCount(Integer maxDeviceCount) { this.maxDeviceCount = maxDeviceCount; }

    public Integer getActivatedCount() { return activatedCount; }
    public void setActivatedCount(Integer activatedCount) { this.activatedCount = activatedCount; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    /** 状态文本（用于前端显示） */
    public String getStatusText() {
        if (this.status == null) {
            return "未知";
        }
        switch (this.status) {
            case "0": return "未使用";
            case "1": return "已使用";
            case "2": return "已过期";
            case "3": return "已禁用";
            default: return "未知";
        }
    }
}
