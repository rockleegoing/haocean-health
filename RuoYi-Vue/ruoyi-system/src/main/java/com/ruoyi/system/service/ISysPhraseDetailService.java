package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysPhraseDetail;

/**
 * 规范用语项明细Service接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface ISysPhraseDetailService
{
    /**
     * 查询规范用语项明细
     *
     * @param detailId 明细主键
     * @return 规范用语项明细
     */
    public SysPhraseDetail selectSysPhraseDetailByDetailId(Long detailId);

    /**
     * 查询规范用语项明细列表
     *
     * @param phraseDetail 规范用语项明细
     * @return 规范用语项明细集合
     */
    public List<SysPhraseDetail> selectSysPhraseDetailList(SysPhraseDetail phraseDetail);

    /**
     * 按项ID查询规范用语项明细
     *
     * @param itemId 项ID
     * @return 明细列表
     */
    public List<SysPhraseDetail> selectSysPhraseDetailByItemId(Long itemId);

    /**
     * 查询所有明细（用于App同步）
     *
     * @return 明细列表
     */
    public List<SysPhraseDetail> selectAllSysPhraseDetails();

    /**
     * 查询变更的明细（用于增量同步）
     *
     * @param version 当前版本号
     * @return 变更的明细列表
     */
    public List<SysPhraseDetail> selectChangedSysPhraseDetails(Integer version);

    /**
     * 新增规范用语项明细
     *
     * @param phraseDetail 规范用语项明细
     * @return 结果
     */
    public int insertSysPhraseDetail(SysPhraseDetail phraseDetail);

    /**
     * 修改规范用语项明细
     *
     * @param phraseDetail 规范用语项明细
     * @return 结果
     */
    public int updateSysPhraseDetail(SysPhraseDetail phraseDetail);

    /**
     * 批量删除规范用语项明细
     *
     * @param detailIds 需要删除的明细主键集合
     * @return 结果
     */
    public int deleteSysPhraseDetailByDetailIds(Long[] detailIds);

    /**
     * 删除规范用语项明细信息
     *
     * @param detailId 明细主键
     * @return 结果
     */
    public int deleteSysPhraseDetailByDetailId(Long detailId);

    /**
     * 获取最大版本号
     *
     * @return 最大版本号
     */
    public Integer selectMaxVersion();
}
