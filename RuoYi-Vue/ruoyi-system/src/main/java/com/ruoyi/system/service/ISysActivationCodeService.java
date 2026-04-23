package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysActivationCode;
import com.ruoyi.system.domain.vo.ActivationApiResponse;
import java.util.List;

/**
 * 激活码 Service 接口
 */
public interface ISysActivationCodeService {

    /**
     * 根据 ID 查询激活码信息
     *
     * @param codeId 激活码 ID
     * @return 激活码信息
     */
    SysActivationCode selectSysActivationCodeById(Long codeId);

    /**
     * 根据码值查询激活码信息
     *
     * @param codeValue 激活码值
     * @return 激活码信息
     */
    SysActivationCode selectSysActivationCodeByValue(String codeValue);

    /**
     * 查询激活码列表
     *
     * @param sysActivationCode 激活码信息
     * @return 激活码集合
     */
    List<SysActivationCode> selectSysActivationCodeList(SysActivationCode sysActivationCode);

    /**
     * 新增激活码
     *
     * @param sysActivationCode 激活码信息
     * @return 结果
     */
    int insertSysActivationCode(SysActivationCode sysActivationCode);

    /**
     * 批量生成激活码
     *
     * @param count 生成数量（1-50）
     * @param expireDays 有效期天数
     * @param remark 备注
     * @param createBy 创建人
     * @return 生成的激活码列表
     */
    List<SysActivationCode> batchGenerateCodes(int count, int expireDays, String remark, String createBy);

    /**
     * 批量插入激活码
     *
     * @param sysActivationCodeList 激活码列表
     * @return 结果
     */
    int batchInsertSysActivationCode(List<SysActivationCode> sysActivationCodeList);

    /**
     * 修改激活码
     *
     * @param sysActivationCode 激活码信息
     * @return 结果
     */
    int updateSysActivationCode(SysActivationCode sysActivationCode);

    /**
     * 批量删除激活码
     *
     * @param codeIds 需要删除的激活码 ID
     * @return 结果
     */
    int deleteSysActivationCodeByIds(Long[] codeIds);

    /**
     * 根据 ID 删除激活码
     *
     * @param codeId 激活码 ID
     * @return 结果
     */
    int deleteSysActivationCodeById(Long codeId);

    /**
     * 验证激活码
     *
     * @param codeValue 激活码值
     * @param deviceUuid 设备 UUID
     * @param deviceName 设备名称（可选）
     * @param deviceModel 设备型号（可选）
     * @param deviceOs 操作系统（可选）
     * @param appVersion App 版本（可选）
     * @return 验证结果
     */
    ActivationApiResponse validateCode(String codeValue, String deviceUuid,
        String deviceName, String deviceModel, String deviceOs, String appVersion);
}
