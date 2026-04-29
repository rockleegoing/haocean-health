package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.RegulatoryMatterMapper;
import com.ruoyi.system.domain.RegulatoryMatter;
import com.ruoyi.system.service.IRegulatoryMatterService;

/**
 * 监管事项Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@Service
public class RegulatoryMatterServiceImpl implements IRegulatoryMatterService 
{
    @Autowired
    private RegulatoryMatterMapper regulatoryMatterMapper;

    /**
     * 查询监管事项
     * 
     * @param id 监管事项主键
     * @return 监管事项
     */
    @Override
    public RegulatoryMatter selectRegulatoryMatterById(Long id)
    {
        return regulatoryMatterMapper.selectRegulatoryMatterById(id);
    }

    /**
     * 查询监管事项列表
     * 
     * @param regulatoryMatter 监管事项
     * @return 监管事项
     */
    @Override
    public List<RegulatoryMatter> selectRegulatoryMatterList(RegulatoryMatter regulatoryMatter)
    {
        return regulatoryMatterMapper.selectRegulatoryMatterList(regulatoryMatter);
    }

    /**
     * 新增监管事项
     * 
     * @param regulatoryMatter 监管事项
     * @return 结果
     */
    @Override
    public int insertRegulatoryMatter(RegulatoryMatter regulatoryMatter)
    {
        regulatoryMatter.setCreateTime(DateUtils.getNowDate());
        return regulatoryMatterMapper.insertRegulatoryMatter(regulatoryMatter);
    }

    /**
     * 修改监管事项
     * 
     * @param regulatoryMatter 监管事项
     * @return 结果
     */
    @Override
    public int updateRegulatoryMatter(RegulatoryMatter regulatoryMatter)
    {
        regulatoryMatter.setUpdateTime(DateUtils.getNowDate());
        return regulatoryMatterMapper.updateRegulatoryMatter(regulatoryMatter);
    }

    /**
     * 批量删除监管事项
     * 
     * @param ids 需要删除的监管事项主键
     * @return 结果
     */
    @Override
    public int deleteRegulatoryMatterByIds(Long[] ids)
    {
        return regulatoryMatterMapper.deleteRegulatoryMatterByIds(ids);
    }

    /**
     * 删除监管事项信息
     * 
     * @param id 监管事项主键
     * @return 结果
     */
    @Override
    public int deleteRegulatoryMatterById(Long id)
    {
        return regulatoryMatterMapper.deleteRegulatoryMatterById(id);
    }
}
