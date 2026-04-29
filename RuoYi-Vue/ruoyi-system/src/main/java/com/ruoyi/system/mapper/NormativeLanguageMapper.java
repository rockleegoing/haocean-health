package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.NormativeLanguage;

/**
 * 规范用语Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public interface NormativeLanguageMapper 
{
    /**
     * 查询规范用语
     * 
     * @param id 规范用语主键
     * @return 规范用语
     */
    public NormativeLanguage selectNormativeLanguageById(Long id);

    /**
     * 查询规范用语列表
     * 
     * @param normativeLanguage 规范用语
     * @return 规范用语集合
     */
    public List<NormativeLanguage> selectNormativeLanguageList(NormativeLanguage normativeLanguage);

    /**
     * 新增规范用语
     * 
     * @param normativeLanguage 规范用语
     * @return 结果
     */
    public int insertNormativeLanguage(NormativeLanguage normativeLanguage);

    /**
     * 修改规范用语
     * 
     * @param normativeLanguage 规范用语
     * @return 结果
     */
    public int updateNormativeLanguage(NormativeLanguage normativeLanguage);

    /**
     * 删除规范用语
     * 
     * @param id 规范用语主键
     * @return 结果
     */
    public int deleteNormativeLanguageById(Long id);

    /**
     * 批量删除规范用语
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNormativeLanguageByIds(Long[] ids);
}
