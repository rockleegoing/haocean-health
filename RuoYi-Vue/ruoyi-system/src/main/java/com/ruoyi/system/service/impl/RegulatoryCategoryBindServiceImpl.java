package com.ruoyi.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.RegulatoryCategoryBindMapper;
import com.ruoyi.system.domain.RegulatoryCategoryBind;
import com.ruoyi.system.service.IRegulatoryCategoryBindService;

/**
 * 监管事项执法分类绑定关系Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@Service
public class RegulatoryCategoryBindServiceImpl implements IRegulatoryCategoryBindService 
{
    @Autowired
    private RegulatoryCategoryBindMapper regulatoryCategoryBindMapper;

    /**
     * 查询监管事项执法分类绑定关系
     * 
     * @param matterId 监管事项执法分类绑定关系主键
     * @return 监管事项执法分类绑定关系
     */
    @Override
    public RegulatoryCategoryBind selectRegulatoryCategoryBindByMatterId(Long matterId)
    {
        return regulatoryCategoryBindMapper.selectRegulatoryCategoryBindByMatterId(matterId);
    }

    /**
     * 查询监管事项执法分类绑定关系列表
     * 
     * @param regulatoryCategoryBind 监管事项执法分类绑定关系
     * @return 监管事项执法分类绑定关系
     */
    @Override
    public List<RegulatoryCategoryBind> selectRegulatoryCategoryBindList(RegulatoryCategoryBind regulatoryCategoryBind)
    {
        return regulatoryCategoryBindMapper.selectRegulatoryCategoryBindList(regulatoryCategoryBind);
    }

    /**
     * 新增监管事项执法分类绑定关系
     * 
     * @param regulatoryCategoryBind 监管事项执法分类绑定关系
     * @return 结果
     */
    @Override
    public int insertRegulatoryCategoryBind(RegulatoryCategoryBind regulatoryCategoryBind)
    {
        regulatoryCategoryBind.setCreateTime(DateUtils.getNowDate());
        return regulatoryCategoryBindMapper.insertRegulatoryCategoryBind(regulatoryCategoryBind);
    }

    /**
     * 修改监管事项执法分类绑定关系
     * 
     * @param regulatoryCategoryBind 监管事项执法分类绑定关系
     * @return 结果
     */
    @Override
    public int updateRegulatoryCategoryBind(RegulatoryCategoryBind regulatoryCategoryBind)
    {
        return regulatoryCategoryBindMapper.updateRegulatoryCategoryBind(regulatoryCategoryBind);
    }

    /**
     * 批量删除监管事项执法分类绑定关系
     * 
     * @param matterIds 需要删除的监管事项执法分类绑定关系主键
     * @return 结果
     */
    @Override
    public int deleteRegulatoryCategoryBindByMatterIds(Long[] matterIds)
    {
        return regulatoryCategoryBindMapper.deleteRegulatoryCategoryBindByMatterIds(matterIds);
    }

    /**
     * 删除监管事项执法分类绑定关系信息
     *
     * @param matterId 监管事项执法分类绑定关系主键
     * @return 结果
     */
    @Override
    public int deleteRegulatoryCategoryBindByMatterId(Long matterId)
    {
        return regulatoryCategoryBindMapper.deleteRegulatoryCategoryBindByMatterId(matterId);
    }

    /**
     * 根据事项ID和分类ID删除绑定关系
     *
     * @param matterId 监管事项ID
     * @param categoryId 分类ID
     * @return 结果
     */
    @Override
    public int deleteRegulatoryCategoryBindByMatterIdAndCategoryId(Long matterId, Long categoryId)
    {
        return regulatoryCategoryBindMapper.deleteRegulatoryCategoryBindByMatterIdAndCategoryId(matterId, categoryId);
    }

    /**
     * 查询所有已绑定的事项列表（去重）
     *
     * @return 已绑定事项集合
     */
    @Override
    public List<RegulatoryCategoryBind> selectBoundMatterList()
    {
        return regulatoryCategoryBindMapper.selectBoundMatterList();
    }
}
