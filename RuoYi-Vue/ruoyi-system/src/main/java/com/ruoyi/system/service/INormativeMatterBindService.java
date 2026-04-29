package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.NormativeMatterBind;

/**
 * 规范用语与监管事项绑定关系Service接口
 *
 * @author ruoyi
 * @date 2026-04-29
 */
public interface INormativeMatterBindService
{
    /**
     * 查询规范用语与监管事项绑定关系
     *
     * @param matterId 监管事项ID
     * @return 规范用语与监管事项绑定关系
     */
    public NormativeMatterBind selectNormativeMatterBindByMatterId(Long matterId);

    /**
     * 查询规范用语与监管事项绑定关系列表
     *
     * @param normativeMatterBind 规范用语与监管事项绑定关系
     * @return 规范用语与监管事项绑定关系集合
     */
    public List<NormativeMatterBind> selectNormativeMatterBindList(NormativeMatterBind normativeMatterBind);

    /**
     * 新增规范用语与监管事项绑定关系
     *
     * @param normativeMatterBind 规范用语与监管事项绑定关系
     * @return 结果
     */
    public int insertNormativeMatterBind(NormativeMatterBind normativeMatterBind);

    /**
     * 修改规范用语与监管事项绑定关系
     *
     * @param normativeMatterBind 规范用语与监管事项绑定关系
     * @return 结果
     */
    public int updateNormativeMatterBind(NormativeMatterBind normativeMatterBind);

    /**
     * 批量删除规范用语与监管事项绑定关系
     *
     * @param matterIds 需要删除的监管事项ID集合
     * @return 结果
     */
    public int deleteNormativeMatterBindByMatterIds(Long[] matterIds);

    /**
     * 删除规范用语与监管事项绑定关系信息
     *
     * @param matterId 监管事项ID
     * @return 结果
     */
    public int deleteNormativeMatterBindByMatterId(Long matterId);

    /**
     * 根据事项ID和规范用语ID删除绑定关系
     *
     * @param matterId 监管事项ID
     * @param normativeId 规范用语ID
     * @return 结果
     */
    public int deleteNormativeMatterBindByMatterIdAndNormativeId(Long matterId, Long normativeId);

    /**
     * 查询所有已绑定的事项列表（去重）
     *
     * @return 已绑定事项集合
     */
    public List<NormativeMatterBind> selectBoundMatterList();

    /**
     * 查询所有已绑定的规范用语列表（去重）
     *
     * @return 已绑定规范用语集合
     */
    public List<NormativeMatterBind> selectBoundNormativeList();
}
