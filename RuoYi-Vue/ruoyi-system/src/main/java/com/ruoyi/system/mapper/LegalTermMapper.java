package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.LegalTerm;

/**
 * 法律条款Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public interface LegalTermMapper 
{
    /**
     * 查询法律条款
     * 
     * @param id 法律条款主键
     * @return 法律条款
     */
    public LegalTerm selectLegalTermById(Long id);

    /**
     * 查询法律条款列表
     * 
     * @param legalTerm 法律条款
     * @return 法律条款集合
     */
    public List<LegalTerm> selectLegalTermList(LegalTerm legalTerm);

    /**
     * 新增法律条款
     * 
     * @param legalTerm 法律条款
     * @return 结果
     */
    public int insertLegalTerm(LegalTerm legalTerm);

    /**
     * 修改法律条款
     * 
     * @param legalTerm 法律条款
     * @return 结果
     */
    public int updateLegalTerm(LegalTerm legalTerm);

    /**
     * 删除法律条款
     * 
     * @param id 法律条款主键
     * @return 结果
     */
    public int deleteLegalTermById(Long id);

    /**
     * 批量删除法律条款
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLegalTermByIds(Long[] ids);
}
