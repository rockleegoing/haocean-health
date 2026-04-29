package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Law;
import com.ruoyi.system.domain.LegalTerm;

/**
 * 法律目录Service接口
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public interface ILawService 
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
     * 批量删除法律目录
     * 
     * @param ids 需要删除的法律目录主键集合
     * @return 结果
     */
    public int deleteLawByIds(Long[] ids);

    /**
     * 删除法律目录信息
     *
     * @param id 法律目录主键
     * @return 结果
     */
    public int deleteLawById(Long id);

    /**
     * 查询法律条款列表
     *
     * @param legalTerm 法律条款
     * @return 法律条款集合
     */
    public List<LegalTerm> selectLegalTermList(LegalTerm legalTerm);

    /**
     * 查询法律条款详情
     *
     * @param id 法律条款主键
     * @return 法律条款
     */
    public LegalTerm selectLegalTermById(Long id);
}
