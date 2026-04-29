package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.RegulatoryMatterItem;

/**
 * 监管事项详情Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public interface RegulatoryMatterItemMapper 
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
     * 删除监管事项详情
     * 
     * @param id 监管事项详情主键
     * @return 结果
     */
    public int deleteRegulatoryMatterItemById(Long id);

    /**
     * 批量删除监管事项详情
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRegulatoryMatterItemByIds(Long[] ids);
}
