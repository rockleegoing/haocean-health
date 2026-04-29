package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.RegulatoryMatterItemMapper;
import com.ruoyi.system.domain.RegulatoryMatterItem;
import com.ruoyi.system.service.IRegulatoryMatterItemService;

/**
 * 监管事项详情Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@Service
public class RegulatoryMatterItemServiceImpl implements IRegulatoryMatterItemService 
{
    @Autowired
    private RegulatoryMatterItemMapper regulatoryMatterItemMapper;

    /**
     * 查询监管事项详情
     * 
     * @param id 监管事项详情主键
     * @return 监管事项详情
     */
    @Override
    public RegulatoryMatterItem selectRegulatoryMatterItemById(Long id)
    {
        return regulatoryMatterItemMapper.selectRegulatoryMatterItemById(id);
    }

    /**
     * 查询监管事项详情列表
     * 
     * @param regulatoryMatterItem 监管事项详情
     * @return 监管事项详情
     */
    @Override
    public List<RegulatoryMatterItem> selectRegulatoryMatterItemList(RegulatoryMatterItem regulatoryMatterItem)
    {
        return regulatoryMatterItemMapper.selectRegulatoryMatterItemList(regulatoryMatterItem);
    }

    /**
     * 新增监管事项详情
     * 
     * @param regulatoryMatterItem 监管事项详情
     * @return 结果
     */
    @Override
    public int insertRegulatoryMatterItem(RegulatoryMatterItem regulatoryMatterItem)
    {
        regulatoryMatterItem.setCreateTime(DateUtils.getNowDate());
        return regulatoryMatterItemMapper.insertRegulatoryMatterItem(regulatoryMatterItem);
    }

    /**
     * 修改监管事项详情
     * 
     * @param regulatoryMatterItem 监管事项详情
     * @return 结果
     */
    @Override
    public int updateRegulatoryMatterItem(RegulatoryMatterItem regulatoryMatterItem)
    {
        regulatoryMatterItem.setUpdateTime(DateUtils.getNowDate());
        return regulatoryMatterItemMapper.updateRegulatoryMatterItem(regulatoryMatterItem);
    }

    /**
     * 批量删除监管事项详情
     * 
     * @param ids 需要删除的监管事项详情主键
     * @return 结果
     */
    @Override
    public int deleteRegulatoryMatterItemByIds(Long[] ids)
    {
        return regulatoryMatterItemMapper.deleteRegulatoryMatterItemByIds(ids);
    }

    /**
     * 删除监管事项详情信息
     * 
     * @param id 监管事项详情主键
     * @return 结果
     */
    @Override
    public int deleteRegulatoryMatterItemById(Long id)
    {
        return regulatoryMatterItemMapper.deleteRegulatoryMatterItemById(id);
    }
}
