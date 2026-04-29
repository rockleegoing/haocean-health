package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.NormativeMatterBindMapper;
import com.ruoyi.system.domain.NormativeMatterBind;
import com.ruoyi.system.service.INormativeMatterBindService;

/**
 * 规范用语与监管事项绑定关系Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-29
 */
@Service
public class NormativeMatterBindServiceImpl implements INormativeMatterBindService
{
    @Autowired
    private NormativeMatterBindMapper normativeMatterBindMapper;

    /**
     * 查询规范用语与监管事项绑定关系
     *
     * @param matterId 监管事项ID
     * @return 规范用语与监管事项绑定关系
     */
    @Override
    public NormativeMatterBind selectNormativeMatterBindByMatterId(Long matterId)
    {
        return normativeMatterBindMapper.selectNormativeMatterBindByMatterId(matterId);
    }

    /**
     * 查询规范用语与监管事项绑定关系列表
     *
     * @param normativeMatterBind 规范用语与监管事项绑定关系
     * @return 规范用语与监管事项绑定关系
     */
    @Override
    public List<NormativeMatterBind> selectNormativeMatterBindList(NormativeMatterBind normativeMatterBind)
    {
        return normativeMatterBindMapper.selectNormativeMatterBindList(normativeMatterBind);
    }

    /**
     * 新增规范用语与监管事项绑定关系
     *
     * @param normativeMatterBind 规范用语与监管事项绑定关系
     * @return 结果
     */
    @Override
    public int insertNormativeMatterBind(NormativeMatterBind normativeMatterBind)
    {
        return normativeMatterBindMapper.insertNormativeMatterBind(normativeMatterBind);
    }

    /**
     * 修改规范用语与监管事项绑定关系
     *
     * @param normativeMatterBind 规范用语与监管事项绑定关系
     * @return 结果
     */
    @Override
    public int updateNormativeMatterBind(NormativeMatterBind normativeMatterBind)
    {
        return normativeMatterBindMapper.updateNormativeMatterBind(normativeMatterBind);
    }

    /**
     * 批量删除规范用语与监管事项绑定关系
     *
     * @param matterIds 需要删除的监管事项ID
     * @return 结果
     */
    @Override
    public int deleteNormativeMatterBindByMatterIds(Long[] matterIds)
    {
        return normativeMatterBindMapper.deleteNormativeMatterBindByMatterIds(matterIds);
    }

    /**
     * 删除规范用语与监管事项绑定关系信息
     *
     * @param matterId 监管事项ID
     * @return 结果
     */
    @Override
    public int deleteNormativeMatterBindByMatterId(Long matterId)
    {
        return normativeMatterBindMapper.deleteNormativeMatterBindByMatterId(matterId);
    }

    /**
     * 根据事项ID和规范用语ID删除绑定关系
     *
     * @param matterId 监管事项ID
     * @param normativeId 规范用语ID
     * @return 结果
     */
    @Override
    public int deleteNormativeMatterBindByMatterIdAndNormativeId(Long matterId, Long normativeId)
    {
        return normativeMatterBindMapper.deleteNormativeMatterBindByMatterIdAndNormativeId(matterId, normativeId);
    }

    /**
     * 查询所有已绑定的事项列表（去重）
     *
     * @return 已绑定事项集合
     */
    @Override
    public List<NormativeMatterBind> selectBoundMatterList()
    {
        return normativeMatterBindMapper.selectBoundMatterList();
    }

    /**
     * 查询所有已绑定的规范用语列表（去重）
     *
     * @return 已绑定规范用语集合
     */
    @Override
    public List<NormativeMatterBind> selectBoundNormativeList()
    {
        return normativeMatterBindMapper.selectBoundNormativeList();
    }
}
