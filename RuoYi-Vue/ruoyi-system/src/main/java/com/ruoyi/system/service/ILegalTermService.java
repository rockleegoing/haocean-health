package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.LegalTerm;

/**
 * 法律目录Service接口
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public interface ILegalTermService 
{
    /**
     * 查询法律目录
     * 
     * @param id 法律目录主键
     * @return 法律目录
     */
    public LegalTerm selectLegalTermById(Long id);

    /**
     * 查询法律目录列表
     * 
     * @param legalTerm 法律目录
     * @return 法律目录集合
     */
    public List<LegalTerm> selectLegalTermList(LegalTerm legalTerm);

    /**
     * 新增法律目录
     * 
     * @param legalTerm 法律目录
     * @return 结果
     */
    public int insertLegalTerm(LegalTerm legalTerm);

    /**
     * 修改法律目录
     * 
     * @param legalTerm 法律目录
     * @return 结果
     */
    public int updateLegalTerm(LegalTerm legalTerm);

    /**
     * 批量删除法律目录
     * 
     * @param ids 需要删除的法律目录主键集合
     * @return 结果
     */
    public int deleteLegalTermByIds(Long[] ids);

    /**
     * 删除法律目录信息
     * 
     * @param id 法律目录主键
     * @return 结果
     */
    public int deleteLegalTermById(Long id);
}
