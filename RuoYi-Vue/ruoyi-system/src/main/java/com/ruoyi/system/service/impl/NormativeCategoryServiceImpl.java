package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.NormativeCategoryMapper;
import com.ruoyi.system.domain.NormativeCategory;
import com.ruoyi.system.service.INormativeCategoryService;

/**
 * 规范用语类别Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@Service
public class NormativeCategoryServiceImpl implements INormativeCategoryService 
{
    @Autowired
    private NormativeCategoryMapper normativeCategoryMapper;

    /**
     * 查询规范用语类别
     * 
     * @param code 规范用语类别主键
     * @return 规范用语类别
     */
    @Override
    public NormativeCategory selectNormativeCategoryByCode(Long code)
    {
        return normativeCategoryMapper.selectNormativeCategoryByCode(code);
    }

    /**
     * 查询规范用语类别列表
     * 
     * @param normativeCategory 规范用语类别
     * @return 规范用语类别
     */
    @Override
    public List<NormativeCategory> selectNormativeCategoryList(NormativeCategory normativeCategory)
    {
        return normativeCategoryMapper.selectNormativeCategoryList(normativeCategory);
    }

    /**
     * 新增规范用语类别
     * 
     * @param normativeCategory 规范用语类别
     * @return 结果
     */
    @Override
    public int insertNormativeCategory(NormativeCategory normativeCategory)
    {
        return normativeCategoryMapper.insertNormativeCategory(normativeCategory);
    }

    /**
     * 修改规范用语类别
     * 
     * @param normativeCategory 规范用语类别
     * @return 结果
     */
    @Override
    public int updateNormativeCategory(NormativeCategory normativeCategory)
    {
        return normativeCategoryMapper.updateNormativeCategory(normativeCategory);
    }

    /**
     * 批量删除规范用语类别
     * 
     * @param codes 需要删除的规范用语类别主键
     * @return 结果
     */
    @Override
    public int deleteNormativeCategoryByCodes(Long[] codes)
    {
        return normativeCategoryMapper.deleteNormativeCategoryByCodes(codes);
    }

    /**
     * 删除规范用语类别信息
     * 
     * @param code 规范用语类别主键
     * @return 结果
     */
    @Override
    public int deleteNormativeCategoryByCode(Long code)
    {
        return normativeCategoryMapper.deleteNormativeCategoryByCode(code);
    }
}
