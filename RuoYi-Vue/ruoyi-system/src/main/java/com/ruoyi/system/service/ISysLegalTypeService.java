package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.dict.SysLegalType;

/**
 * 法律类型Service接口
 */
public interface ISysLegalTypeService {
    /**
     * 查询法律类型
     */
    public SysLegalType selectLegalTypeById(Long typeId);

    /**
     * 查询法律类型列表
     */
    public List<SysLegalType> selectLegalTypeList(SysLegalType legalType);

    /**
     * 查询所有正常状态的法律类型
     */
    public List<SysLegalType> selectLegalTypeAll();

    /**
     * 新增法律类型
     */
    public int insertLegalType(SysLegalType legalType);

    /**
     * 修改法律类型
     */
    public int updateLegalType(SysLegalType legalType);

    /**
     * 删除法律类型
     */
    public int deleteLegalTypeById(Long typeId);

    /**
     * 批量删除法律类型
     */
    public int deleteLegalTypeByIds(Long[] typeIds);
}