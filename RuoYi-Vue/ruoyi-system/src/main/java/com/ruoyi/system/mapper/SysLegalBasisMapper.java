package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysLegalBasis;

/**
 * 定性依据Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysLegalBasisMapper {
    /**
     * 查询定性依据
     *
     * @param basisId 定性依据主键
     * @return 定性依据
     */
    public SysLegalBasis selectSysLegalBasisById(Long basisId);

    /**
     * 查询定性依据列表
     *
     * @param sysLegalBasis 定性依据
     * @return 定性依据集合
     */
    public List<SysLegalBasis> selectSysLegalBasisList(SysLegalBasis sysLegalBasis);

    /**
     * 查询某法律法规关联的定性依据列表
     *
     * @param regulationId 法律法规ID
     * @return 定性依据列表
     */
    public List<SysLegalBasis> selectSysLegalBasisListByRegulationId(Long regulationId);

    /**
     * 新增定性依据
     *
     * @param sysLegalBasis 定性依据
     * @return 结果
     */
    public int insertSysLegalBasis(SysLegalBasis sysLegalBasis);

    /**
     * 修改定性依据
     *
     * @param sysLegalBasis 定性依据
     * @return 结果
     */
    public int updateSysLegalBasis(SysLegalBasis sysLegalBasis);

    /**
     * 删除定性依据
     *
     * @param basisId 定性依据主键
     * @return 结果
     */
    public int deleteSysLegalBasisById(Long basisId);

    /**
     * 批量删除定性依据
     *
     * @param basisIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysLegalBasisByIds(Long[] basisIds);
}
