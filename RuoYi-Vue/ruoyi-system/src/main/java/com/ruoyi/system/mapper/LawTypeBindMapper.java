package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.LawTypeBind;

/**
 * 法律类型绑定Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-30
 */
public interface LawTypeBindMapper
{
    /**
     * 查询某法律关联的所有类型
     *
     * @param lawId 法律ID
     * @return 绑定列表
     */
    public List<LawTypeBind> selectByLawId(Long lawId);

    /**
     * 查询某类型关联的所有法律
     *
     * @param typeId 类型ID
     * @return 绑定列表
     */
    public List<LawTypeBind> selectByTypeId(Long typeId);

    /**
     * 批量插入绑定关系
     *
     * @param binds 绑定列表
     * @return 结果
     */
    public int insertBatch(List<LawTypeBind> binds);

    /**
     * 删除某法律的全部绑定
     *
     * @param lawId 法律ID
     * @return 结果
     */
    public int deleteByLawId(Long lawId);

    /**
     * 删除单个绑定
     *
     * @param lawId 法律ID
     * @param typeId 类型ID
     * @return 结果
     */
    public int deleteByLawIdAndTypeId(Long lawId, Long typeId);

    /**
     * 查询所有绑定列表
     *
     * @return 绑定列表
     */
    public List<LawTypeBind> selectAll();
}
