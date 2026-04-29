package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.LawTypeBindMapper;
import com.ruoyi.system.domain.LawTypeBind;
import com.ruoyi.system.service.ILawTypeBindService;

/**
 * 法律类型绑定Service实现
 *
 * @author ruoyi
 * @date 2026-04-30
 */
@Service
public class LawTypeBindServiceImpl implements ILawTypeBindService
{
    @Autowired
    private LawTypeBindMapper lawTypeBindMapper;

    @Override
    public List<LawTypeBind> selectByLawId(Long lawId)
    {
        return lawTypeBindMapper.selectByLawId(lawId);
    }

    @Override
    public List<LawTypeBind> selectByTypeId(Long typeId)
    {
        return lawTypeBindMapper.selectByTypeId(typeId);
    }

    @Override
    public int insertBatch(List<LawTypeBind> binds)
    {
        if (binds == null || binds.isEmpty()) {
            return 0;
        }
        for (LawTypeBind bind : binds) {
            bind.setCreateTime(DateUtils.getNowDate());
        }
        return lawTypeBindMapper.insertBatch(binds);
    }

    @Override
    public int deleteByLawId(Long lawId)
    {
        return lawTypeBindMapper.deleteByLawId(lawId);
    }

    @Override
    public int deleteByLawIdAndTypeId(Long lawId, Long typeId)
    {
        return lawTypeBindMapper.deleteByLawIdAndTypeId(lawId, typeId);
    }

    @Override
    public List<LawTypeBind> selectAll()
    {
        return lawTypeBindMapper.selectAll();
    }
}