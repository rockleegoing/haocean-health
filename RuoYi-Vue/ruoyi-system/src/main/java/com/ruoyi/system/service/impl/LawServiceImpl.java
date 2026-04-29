package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.LawMapper;
import com.ruoyi.system.domain.Law;
import com.ruoyi.system.service.ILawService;

/**
 * 法律条款Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@Service
public class LawServiceImpl implements ILawService 
{
    @Autowired
    private LawMapper lawMapper;

    /**
     * 查询法律条款
     * 
     * @param id 法律条款主键
     * @return 法律条款
     */
    @Override
    public Law selectLawById(Long id)
    {
        return lawMapper.selectLawById(id);
    }

    /**
     * 查询法律条款列表
     * 
     * @param law 法律条款
     * @return 法律条款
     */
    @Override
    public List<Law> selectLawList(Law law)
    {
        return lawMapper.selectLawList(law);
    }

    /**
     * 新增法律条款
     * 
     * @param law 法律条款
     * @return 结果
     */
    @Override
    public int insertLaw(Law law)
    {
        return lawMapper.insertLaw(law);
    }

    /**
     * 修改法律条款
     * 
     * @param law 法律条款
     * @return 结果
     */
    @Override
    public int updateLaw(Law law)
    {
        return lawMapper.updateLaw(law);
    }

    /**
     * 批量删除法律条款
     * 
     * @param ids 需要删除的法律条款主键
     * @return 结果
     */
    @Override
    public int deleteLawByIds(Long[] ids)
    {
        return lawMapper.deleteLawByIds(ids);
    }

    /**
     * 删除法律条款信息
     * 
     * @param id 法律条款主键
     * @return 结果
     */
    @Override
    public int deleteLawById(Long id)
    {
        return lawMapper.deleteLawById(id);
    }
}
