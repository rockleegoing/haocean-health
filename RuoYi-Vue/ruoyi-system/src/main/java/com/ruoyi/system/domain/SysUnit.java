package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 执法单位对象 sys_unit
 * 
 * @author ruoyi
 * @date 2026-04-24
 */
public class SysUnit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 单位ID */
    private Long unitId;

    /** 单位名称 */
    @Excel(name = "单位名称")
    private String unitName;

    /** 行业分类ID */
    @Excel(name = "行业分类ID")
    private Long industryCategoryId;

    /** 行业分类名称（不映射数据库列，通过JOIN查询获取） */
    @Excel(name = "行业分类名称")
    private String industryCategoryName;

    /** 区域 */
    @Excel(name = "区域")
    private String region;

    /** 监管类型 */
    @Excel(name = "监管类型")
    private String supervisionType;

    /** 统一社会信用代码 */
    @Excel(name = "统一社会信用代码")
    private String creditCode;

    /** 法定代表人 */
    @Excel(name = "法定代表人")
    private String legalPerson;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 经营地址 */
    @Excel(name = "经营地址")
    private String businessAddress;

    /** 纬度 */
    @Excel(name = "纬度")
    private BigDecimal latitude;

    /** 经度 */
    @Excel(name = "经度")
    private BigDecimal longitude;

    /** 状态:0正常,1停用 */
    @Excel(name = "状态:0正常,1停用")
    private String status;

    /** 删除标志:0存在,1删除 */
    private String delFlag;

    public void setUnitId(Long unitId) 
    {
        this.unitId = unitId;
    }

    public Long getUnitId() 
    {
        return unitId;
    }

    public void setUnitName(String unitName) 
    {
        this.unitName = unitName;
    }

    public String getUnitName() 
    {
        return unitName;
    }

    public void setIndustryCategoryId(Long industryCategoryId) 
    {
        this.industryCategoryId = industryCategoryId;
    }

    public Long getIndustryCategoryId()
    {
        return industryCategoryId;
    }

    public void setIndustryCategoryName(String industryCategoryName)
    {
        this.industryCategoryName = industryCategoryName;
    }

    public String getIndustryCategoryName()
    {
        return industryCategoryName;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getRegion() 
    {
        return region;
    }

    public void setSupervisionType(String supervisionType) 
    {
        this.supervisionType = supervisionType;
    }

    public String getSupervisionType() 
    {
        return supervisionType;
    }

    public void setCreditCode(String creditCode) 
    {
        this.creditCode = creditCode;
    }

    public String getCreditCode() 
    {
        return creditCode;
    }

    public void setLegalPerson(String legalPerson) 
    {
        this.legalPerson = legalPerson;
    }

    public String getLegalPerson() 
    {
        return legalPerson;
    }

    public void setContactPhone(String contactPhone) 
    {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone() 
    {
        return contactPhone;
    }

    public void setBusinessAddress(String businessAddress) 
    {
        this.businessAddress = businessAddress;
    }

    public String getBusinessAddress() 
    {
        return businessAddress;
    }

    public void setLatitude(BigDecimal latitude) 
    {
        this.latitude = latitude;
    }

    public BigDecimal getLatitude() 
    {
        return latitude;
    }

    public void setLongitude(BigDecimal longitude) 
    {
        this.longitude = longitude;
    }

    public BigDecimal getLongitude() 
    {
        return longitude;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("unitId", getUnitId())
            .append("unitName", getUnitName())
            .append("industryCategoryId", getIndustryCategoryId())
            .append("industryCategoryName", getIndustryCategoryName())
            .append("region", getRegion())
            .append("supervisionType", getSupervisionType())
            .append("creditCode", getCreditCode())
            .append("legalPerson", getLegalPerson())
            .append("contactPhone", getContactPhone())
            .append("businessAddress", getBusinessAddress())
            .append("latitude", getLatitude())
            .append("longitude", getLongitude())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
