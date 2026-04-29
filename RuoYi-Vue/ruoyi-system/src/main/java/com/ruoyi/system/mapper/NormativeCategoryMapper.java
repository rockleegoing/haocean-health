package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.NormativeCategory;

/**
 * 规范用语类别Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public interface NormativeCategoryMapper 
{
    /**
     * 查询规范用语类别
     * 
     * @param code 规范用语类别主键
     * @return 规范用语类别
     */
    public NormativeCategory selectNormativeCategoryByCode(Long code);

    /**
     * 查询规范用语类别列表
     * 
     * @param normativeCategory 规范用语类别
     * @return 规范用语类别集合
     */
    public List<NormativeCategory> selectNormativeCategoryList(NormativeCategory normativeCategory);

    /**
     * 新增规范用语类别
     * 
     * @param normativeCategory 规范用语类别
     * @return 结果
     */
    public int insertNormativeCategory(NormativeCategory normativeCategory);

    /**
     * 修改规范用语类别
     * 
     * @param normativeCategory 规范用语类别
     * @return 结果
     */
    public int updateNormativeCategory(NormativeCategory normativeCategory);

    /**
     * 删除规范用语类别
     * 
     * @param code 规范用语类别主键
     * @return 结果
     */
    public int deleteNormativeCategoryByCode(Long code);

    /**
     * 批量删除规范用语类别
     * 
     * @param codes 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNormativeCategoryByCodes(Long[] codes);
}
