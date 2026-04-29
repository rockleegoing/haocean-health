package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.NormativeLanguageMapper;
import com.ruoyi.system.domain.NormativeLanguage;
import com.ruoyi.system.service.INormativeLanguageService;

/**
 * 规范用语Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@Service
public class NormativeLanguageServiceImpl implements INormativeLanguageService 
{
    @Autowired
    private NormativeLanguageMapper normativeLanguageMapper;

    /**
     * 查询规范用语
     * 
     * @param id 规范用语主键
     * @return 规范用语
     */
    @Override
    public NormativeLanguage selectNormativeLanguageById(Long id)
    {
        return normativeLanguageMapper.selectNormativeLanguageById(id);
    }

    /**
     * 查询规范用语列表
     * 
     * @param normativeLanguage 规范用语
     * @return 规范用语
     */
    @Override
    public List<NormativeLanguage> selectNormativeLanguageList(NormativeLanguage normativeLanguage)
    {
        return normativeLanguageMapper.selectNormativeLanguageList(normativeLanguage);
    }

    /**
     * 新增规范用语
     * 
     * @param normativeLanguage 规范用语
     * @return 结果
     */
    @Override
    public int insertNormativeLanguage(NormativeLanguage normativeLanguage)
    {
        normativeLanguage.setCreateTime(DateUtils.getNowDate());
        return normativeLanguageMapper.insertNormativeLanguage(normativeLanguage);
    }

    /**
     * 修改规范用语
     * 
     * @param normativeLanguage 规范用语
     * @return 结果
     */
    @Override
    public int updateNormativeLanguage(NormativeLanguage normativeLanguage)
    {
        return normativeLanguageMapper.updateNormativeLanguage(normativeLanguage);
    }

    /**
     * 批量删除规范用语
     * 
     * @param ids 需要删除的规范用语主键
     * @return 结果
     */
    @Override
    public int deleteNormativeLanguageByIds(Long[] ids)
    {
        return normativeLanguageMapper.deleteNormativeLanguageByIds(ids);
    }

    /**
     * 删除规范用语信息
     * 
     * @param id 规范用语主键
     * @return 结果
     */
    @Override
    public int deleteNormativeLanguageById(Long id)
    {
        return normativeLanguageMapper.deleteNormativeLanguageById(id);
    }
}
