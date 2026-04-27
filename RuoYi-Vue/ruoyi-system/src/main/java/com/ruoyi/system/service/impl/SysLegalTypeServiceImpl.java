package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysLegalTypeMapper;
import com.ruoyi.system.domain.dict.SysLegalType;
import com.ruoyi.system.service.ISysLegalTypeService;

/**
 * 法律类型Service实现
 */
@Service
public class SysLegalTypeServiceImpl implements ISysLegalTypeService {

    @Autowired
    private SysLegalTypeMapper legalTypeMapper;

    @Override
    public SysLegalType selectLegalTypeById(Long typeId) {
        return legalTypeMapper.selectLegalTypeById(typeId);
    }

    @Override
    public List<SysLegalType> selectLegalTypeList(SysLegalType legalType) {
        return legalTypeMapper.selectLegalTypeList(legalType);
    }

    @Override
    public List<SysLegalType> selectLegalTypeAll() {
        return legalTypeMapper.selectLegalTypeAll();
    }

    @Override
    public int insertLegalType(SysLegalType legalType) {
        return legalTypeMapper.insertLegalType(legalType);
    }

    @Override
    public int updateLegalType(SysLegalType legalType) {
        return legalTypeMapper.updateLegalType(legalType);
    }

    @Override
    public int deleteLegalTypeById(Long typeId) {
        return legalTypeMapper.deleteLegalTypeById(typeId);
    }

    @Override
    public int deleteLegalTypeByIds(Long[] typeIds) {
        return legalTypeMapper.deleteLegalTypeByIds(typeIds);
    }
}