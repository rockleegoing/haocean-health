package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysActivationCode;
import java.util.List;

/**
 * 激活码 Mapper 数据层
 */
public interface SysActivationCodeMapper {

    SysActivationCode selectSysActivationCodeById(Long codeId);

    SysActivationCode selectSysActivationCodeByValue(String codeValue);

    List<SysActivationCode> selectSysActivationCodeList(SysActivationCode sysActivationCode);

    int insertSysActivationCode(SysActivationCode sysActivationCode);

    int batchInsertSysActivationCode(List<SysActivationCode> sysActivationCodeList);

    int updateSysActivationCode(SysActivationCode sysActivationCode);

    int deleteSysActivationCodeById(Long codeId);

    int deleteSysActivationCodeByIds(Long[] codeIds);
}
