package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.RegulatoryCategoryBind;

/**
 * 监管事项执法分类绑定关系Service接口
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public interface IRegulatoryCategoryBindService 
{
    /**
     * 查询监管事项执法分类绑定关系
     * 
     * @param matterId 监管事项执法分类绑定关系主键
     * @return 监管事项执法分类绑定关系
     */
    public RegulatoryCategoryBind selectRegulatoryCategoryBindByMatterId(Long matterId);

    /**
     * 查询监管事项执法分类绑定关系列表
     * 
     * @param regulatoryCategoryBind 监管事项执法分类绑定关系
     * @return 监管事项执法分类绑定关系集合
     */
    public List<RegulatoryCategoryBind> selectRegulatoryCategoryBindList(RegulatoryCategoryBind regulatoryCategoryBind);

    /**
     * 新增监管事项执法分类绑定关系
     * 
     * @param regulatoryCategoryBind 监管事项执法分类绑定关系
     * @return 结果
     */
    public int insertRegulatoryCategoryBind(RegulatoryCategoryBind regulatoryCategoryBind);

    /**
     * 修改监管事项执法分类绑定关系
     * 
     * @param regulatoryCategoryBind 监管事项执法分类绑定关系
     * @return 结果
     */
    public int updateRegulatoryCategoryBind(RegulatoryCategoryBind regulatoryCategoryBind);

    /**
     * 批量删除监管事项执法分类绑定关系
     * 
     * @param matterIds 需要删除的监管事项执法分类绑定关系主键集合
     * @return 结果
     */
    public int deleteRegulatoryCategoryBindByMatterIds(Long[] matterIds);

    /**
     * 删除监管事项执法分类绑定关系信息
     *
     * @param matterId 监管事项执法分类绑定关系主键
     * @return 结果
     */
    public int deleteRegulatoryCategoryBindByMatterId(Long matterId);

    /**
     * 根据事项ID和分类ID删除绑定关系
     *
     * @param matterId 监管事项ID
     * @param categoryId 分类ID
     * @return 结果
     */
    public int deleteRegulatoryCategoryBindByMatterIdAndCategoryId(Long matterId, Long categoryId);

    /**
     * 查询所有已绑定的事项列表（去重）
     *
     * @return 已绑定事项集合
     */
    public List<RegulatoryCategoryBind> selectBoundMatterList();
}
