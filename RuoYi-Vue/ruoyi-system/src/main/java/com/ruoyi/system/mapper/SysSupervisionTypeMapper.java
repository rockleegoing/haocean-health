package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.dict.SysSupervisionType;

/**
 * 监管类型Mapper接口
 */
public interface SysSupervisionTypeMapper {
    /**
     * 查询监管类型
     */
    public SysSupervisionType selectSupervisionTypeById(Long typeId);

    /**
     * 查询监管类型列表
     */
    public List<SysSupervisionType> selectSupervisionTypeList(SysSupervisionType supervisionType);

    /**
     * 查询所有正常状态的监管类型
     */
    public List<SysSupervisionType> selectSupervisionTypeAll();

    /**
     * 新增监管类型
     */
    public int insertSupervisionType(SysSupervisionType supervisionType);

    /**
     * 修改监管类型
     */
    public int updateSupervisionType(SysSupervisionType supervisionType);

    /**
     * 删除监管类型
     */
    public int deleteSupervisionTypeById(Long typeId);

    /**
     * 批量删除监管类型
     */
    public int deleteSupervisionTypeByIds(Long[] typeIds);
}