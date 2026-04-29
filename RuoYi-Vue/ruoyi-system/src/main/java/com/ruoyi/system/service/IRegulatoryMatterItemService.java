package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.RegulatoryMatterItem;

/**
 * 监管事项详情Service接口
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public interface IRegulatoryMatterItemService 
{
    /**
     * 查询监管事项详情
     * 
     * @param id 监管事项详情主键
     * @return 监管事项详情
     */
    public RegulatoryMatterItem selectRegulatoryMatterItemById(Long id);

    /**
     * 查询监管事项详情列表
     * 
     * @param regulatoryMatterItem 监管事项详情
     * @return 监管事项详情集合
     */
    public List<RegulatoryMatterItem> selectRegulatoryMatterItemList(RegulatoryMatterItem regulatoryMatterItem);

    /**
     * 新增监管事项详情
     * 
     * @param regulatoryMatterItem 监管事项详情
     * @return 结果
     */
    public int insertRegulatoryMatterItem(RegulatoryMatterItem regulatoryMatterItem);

    /**
     * 修改监管事项详情
     * 
     * @param regulatoryMatterItem 监管事项详情
     * @return 结果
     */
    public int updateRegulatoryMatterItem(RegulatoryMatterItem regulatoryMatterItem);

    /**
     * 批量删除监管事项详情
     * 
     * @param ids 需要删除的监管事项详情主键集合
     * @return 结果
     */
    public int deleteRegulatoryMatterItemByIds(Long[] ids);

    /**
     * 删除监管事项详情信息
     * 
     * @param id 监管事项详情主键
     * @return 结果
     */
    public int deleteRegulatoryMatterItemById(Long id);
}
