package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Law;

/**
 * 法律目录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public interface LawMapper 
{
    /**
     * 查询法律目录
     * 
     * @param id 法律目录主键
     * @return 法律目录
     */
    public Law selectLawById(Long id);

    /**
     * 查询法律目录列表
     * 
     * @param law 法律目录
     * @return 法律目录集合
     */
    public List<Law> selectLawList(Law law);

    /**
     * 新增法律目录
     * 
     * @param law 法律目录
     * @return 结果
     */
    public int insertLaw(Law law);

    /**
     * 修改法律目录
     * 
     * @param law 法律目录
     * @return 结果
     */
    public int updateLaw(Law law);

    /**
     * 删除法律目录
     * 
     * @param id 法律目录主键
     * @return 结果
     */
    public int deleteLawById(Long id);

    /**
     * 批量删除法律目录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLawByIds(Long[] ids);
}
