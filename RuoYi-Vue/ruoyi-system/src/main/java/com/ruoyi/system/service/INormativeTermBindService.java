package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.NormativeTermBind;

/**
 * 规范用语法律条款关联Service接口
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public interface INormativeTermBindService 
{
    /**
     * 查询规范用语法律条款关联
     * 
     * @param legalTermId 规范用语法律条款关联主键
     * @return 规范用语法律条款关联
     */
    public NormativeTermBind selectNormativeTermBindByLegalTermId(Long legalTermId);

    /**
     * 查询规范用语法律条款关联列表
     * 
     * @param normativeTermBind 规范用语法律条款关联
     * @return 规范用语法律条款关联集合
     */
    public List<NormativeTermBind> selectNormativeTermBindList(NormativeTermBind normativeTermBind);

    /**
     * 新增规范用语法律条款关联
     * 
     * @param normativeTermBind 规范用语法律条款关联
     * @return 结果
     */
    public int insertNormativeTermBind(NormativeTermBind normativeTermBind);

    /**
     * 修改规范用语法律条款关联
     * 
     * @param normativeTermBind 规范用语法律条款关联
     * @return 结果
     */
    public int updateNormativeTermBind(NormativeTermBind normativeTermBind);

    /**
     * 批量删除规范用语法律条款关联
     * 
     * @param legalTermIds 需要删除的规范用语法律条款关联主键集合
     * @return 结果
     */
    public int deleteNormativeTermBindByLegalTermIds(Long[] legalTermIds);

    /**
     * 删除规范用语法律条款关联信息
     *
     * @param legalTermId 规范用语法律条款关联主键
     * @return 结果
     */
    public int deleteNormativeTermBindByLegalTermId(Long legalTermId);

    /**
     * 根据法律条款ID和规范用语ID删除绑定关系
     *
     * @param legalTermId 法律条款ID
     * @param normativeLanguageId 规范用语ID
     * @return 结果
     */
    public int deleteNormativeTermBindByBothIds(Long legalTermId, Long normativeLanguageId);
}
