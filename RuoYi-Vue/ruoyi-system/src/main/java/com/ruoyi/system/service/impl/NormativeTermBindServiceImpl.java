package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.NormativeTermBindMapper;
import com.ruoyi.system.domain.NormativeTermBind;
import com.ruoyi.system.service.INormativeTermBindService;

/**
 * 规范用语法律条款关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@Service
public class NormativeTermBindServiceImpl implements INormativeTermBindService 
{
    @Autowired
    private NormativeTermBindMapper normativeTermBindMapper;

    /**
     * 查询规范用语法律条款关联
     * 
     * @param legalTermId 规范用语法律条款关联主键
     * @return 规范用语法律条款关联
     */
    @Override
    public NormativeTermBind selectNormativeTermBindByLegalTermId(Long legalTermId)
    {
        return normativeTermBindMapper.selectNormativeTermBindByLegalTermId(legalTermId);
    }

    /**
     * 查询规范用语法律条款关联列表
     * 
     * @param normativeTermBind 规范用语法律条款关联
     * @return 规范用语法律条款关联
     */
    @Override
    public List<NormativeTermBind> selectNormativeTermBindList(NormativeTermBind normativeTermBind)
    {
        return normativeTermBindMapper.selectNormativeTermBindList(normativeTermBind);
    }

    /**
     * 新增规范用语法律条款关联
     * 
     * @param normativeTermBind 规范用语法律条款关联
     * @return 结果
     */
    @Override
    public int insertNormativeTermBind(NormativeTermBind normativeTermBind)
    {
        return normativeTermBindMapper.insertNormativeTermBind(normativeTermBind);
    }

    /**
     * 修改规范用语法律条款关联
     * 
     * @param normativeTermBind 规范用语法律条款关联
     * @return 结果
     */
    @Override
    public int updateNormativeTermBind(NormativeTermBind normativeTermBind)
    {
        return normativeTermBindMapper.updateNormativeTermBind(normativeTermBind);
    }

    /**
     * 批量删除规范用语法律条款关联
     * 
     * @param legalTermIds 需要删除的规范用语法律条款关联主键
     * @return 结果
     */
    @Override
    public int deleteNormativeTermBindByLegalTermIds(Long[] legalTermIds)
    {
        return normativeTermBindMapper.deleteNormativeTermBindByLegalTermIds(legalTermIds);
    }

    /**
     * 删除规范用语法律条款关联信息
     *
     * @param legalTermId 规范用语法律条款关联主键
     * @return 结果
     */
    @Override
    public int deleteNormativeTermBindByLegalTermId(Long legalTermId)
    {
        return normativeTermBindMapper.deleteNormativeTermBindByLegalTermId(legalTermId);
    }

    /**
     * 根据法律条款ID和规范用语ID删除绑定关系
     *
     * @param legalTermId 法律条款ID
     * @param normativeLanguageId 规范用语ID
     * @return 结果
     */
    @Override
    public int deleteNormativeTermBindByBothIds(Long legalTermId, Long normativeLanguageId)
    {
        return normativeTermBindMapper.deleteNormativeTermBindByBothIds(legalTermId, normativeLanguageId);
    }
}
