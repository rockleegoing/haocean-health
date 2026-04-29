package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.RegulatoryMatter;

/**
 * 监管事项Service接口
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public interface IRegulatoryMatterService 
{
    /**
     * 查询监管事项
     * 
     * @param id 监管事项主键
     * @return 监管事项
     */
    public RegulatoryMatter selectRegulatoryMatterById(Long id);

    /**
     * 查询监管事项列表
     * 
     * @param regulatoryMatter 监管事项
     * @return 监管事项集合
     */
    public List<RegulatoryMatter> selectRegulatoryMatterList(RegulatoryMatter regulatoryMatter);

    /**
     * 新增监管事项
     * 
     * @param regulatoryMatter 监管事项
     * @return 结果
     */
    public int insertRegulatoryMatter(RegulatoryMatter regulatoryMatter);

    /**
     * 修改监管事项
     * 
     * @param regulatoryMatter 监管事项
     * @return 结果
     */
    public int updateRegulatoryMatter(RegulatoryMatter regulatoryMatter);

    /**
     * 批量删除监管事项
     * 
     * @param ids 需要删除的监管事项主键集合
     * @return 结果
     */
    public int deleteRegulatoryMatterByIds(Long[] ids);

    /**
     * 删除监管事项信息
     * 
     * @param id 监管事项主键
     * @return 结果
     */
    public int deleteRegulatoryMatterById(Long id);
}
