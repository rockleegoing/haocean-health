package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.LegalTermMapper;
import com.ruoyi.system.domain.LegalTerm;
import com.ruoyi.system.service.ILegalTermService;

/**
 * 法律条款Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@Service
public class LegalTermServiceImpl implements ILegalTermService 
{
    @Autowired
    private LegalTermMapper legalTermMapper;

    /**
     * 查询法律条款
     * 
     * @param id 法律条款主键
     * @return 法律条款
     */
    @Override
    public LegalTerm selectLegalTermById(Long id)
    {
        return legalTermMapper.selectLegalTermById(id);
    }

    /**
     * 查询法律条款列表
     * 
     * @param legalTerm 法律条款
     * @return 法律条款
     */
    @Override
    public List<LegalTerm> selectLegalTermList(LegalTerm legalTerm)
    {
        return legalTermMapper.selectLegalTermList(legalTerm);
    }

    /**
     * 新增法律条款
     * 
     * @param legalTerm 法律条款
     * @return 结果
     */
    @Override
    public int insertLegalTerm(LegalTerm legalTerm)
    {
        return legalTermMapper.insertLegalTerm(legalTerm);
    }

    /**
     * 修改法律条款
     * 
     * @param legalTerm 法律条款
     * @return 结果
     */
    @Override
    public int updateLegalTerm(LegalTerm legalTerm)
    {
        legalTerm.setUpdateTime(DateUtils.getNowDate());
        return legalTermMapper.updateLegalTerm(legalTerm);
    }

    /**
     * 批量删除法律条款
     * 
     * @param ids 需要删除的法律条款主键
     * @return 结果
     */
    @Override
    public int deleteLegalTermByIds(Long[] ids)
    {
        return legalTermMapper.deleteLegalTermByIds(ids);
    }

    /**
     * 删除法律条款信息
     * 
     * @param id 法律条款主键
     * @return 结果
     */
    @Override
    public int deleteLegalTermById(Long id)
    {
        return legalTermMapper.deleteLegalTermById(id);
    }
}
