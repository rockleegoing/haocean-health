package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysSupervisionTypeMapper;
import com.ruoyi.system.domain.dict.SysSupervisionType;
import com.ruoyi.system.service.ISysSupervisionTypeService;

/**
 * 监管类型Service实现
 */
@Service
public class SysSupervisionTypeServiceImpl implements ISysSupervisionTypeService {

    @Autowired
    private SysSupervisionTypeMapper supervisionTypeMapper;

    @Override
    public SysSupervisionType selectSupervisionTypeById(Long typeId) {
        return supervisionTypeMapper.selectSupervisionTypeById(typeId);
    }

    @Override
    public List<SysSupervisionType> selectSupervisionTypeList(SysSupervisionType supervisionType) {
        return supervisionTypeMapper.selectSupervisionTypeList(supervisionType);
    }

    @Override
    public List<SysSupervisionType> selectSupervisionTypeAll() {
        return supervisionTypeMapper.selectSupervisionTypeAll();
    }

    @Override
    public int insertSupervisionType(SysSupervisionType supervisionType) {
        return supervisionTypeMapper.insertSupervisionType(supervisionType);
    }

    @Override
    public int updateSupervisionType(SysSupervisionType supervisionType) {
        return supervisionTypeMapper.updateSupervisionType(supervisionType);
    }

    @Override
    public int deleteSupervisionTypeById(Long typeId) {
        return supervisionTypeMapper.deleteSupervisionTypeById(typeId);
    }

    @Override
    public int deleteSupervisionTypeByIds(Long[] typeIds) {
        return supervisionTypeMapper.deleteSupervisionTypeByIds(typeIds);
    }
}