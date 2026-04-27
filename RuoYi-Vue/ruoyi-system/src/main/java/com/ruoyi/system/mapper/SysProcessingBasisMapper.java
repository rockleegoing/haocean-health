package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysProcessingBasis;

/**
 * 处理依据Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-28
 */
public interface SysProcessingBasisMapper {
    /**
     * 查询处理依据
     *
     * @param basisId 处理依据主键
     * @return 处理依据
     */
    SysProcessingBasis selectSysProcessingBasisById(Long basisId);

    /**
     * 查询处理依据列表
     *
     * @param sysProcessingBasis 处理依据
     * @return 处理依据集合
     */
    List<SysProcessingBasis> selectSysProcessingBasisList(SysProcessingBasis sysProcessingBasis);

    /**
     * 查询某法律法规关联的处理依据列表
     *
     * @param regulationId 法规ID
     * @return 处理依据列表
     */
    List<SysProcessingBasis> selectSysProcessingBasisListByRegulationId(Long regulationId);

    /**
     * 新增处理依据
     *
     * @param sysProcessingBasis 处理依据
     * @return 结果
     */
    int insertSysProcessingBasis(SysProcessingBasis sysProcessingBasis);

    /**
     * 修改处理依据
     *
     * @param sysProcessingBasis 处理依据
     * @return 结果
     */
    int updateSysProcessingBasis(SysProcessingBasis sysProcessingBasis);

    /**
     * 删除处理依据（逻辑删除）
     *
     * @param basisId 处理依据主键
     * @return 结果
     */
    int deleteSysProcessingBasisById(Long basisId);

    /**
     * 批量删除处理依据（逻辑删除）
     *
     * @param basisIds 需要删除的处理依据主键
     * @return 结果
     */
    int deleteSysProcessingBasisByIds(Long[] basisIds);
}
