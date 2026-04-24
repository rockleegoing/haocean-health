package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.SysPhraseDetailMapper;
import com.ruoyi.system.domain.SysPhraseDetail;
import com.ruoyi.system.service.ISysPhraseDetailService;

/**
 * 规范用语项明细Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-25
 */
@Service
public class SysPhraseDetailServiceImpl implements ISysPhraseDetailService
{
    @Autowired
    private SysPhraseDetailMapper phraseDetailMapper;

    /**
     * 查询规范用语项明细
     *
     * @param detailId 明细主键
     * @return 规范用语项明细
     */
    @Override
    public SysPhraseDetail selectSysPhraseDetailByDetailId(Long detailId)
    {
        return phraseDetailMapper.selectSysPhraseDetailByDetailId(detailId);
    }

    /**
     * 查询规范用语项明细列表
     *
     * @param phraseDetail 规范用语项明细
     * @return 规范用语项明细
     */
    @Override
    public List<SysPhraseDetail> selectSysPhraseDetailList(SysPhraseDetail phraseDetail)
    {
        return phraseDetailMapper.selectSysPhraseDetailList(phraseDetail);
    }

    /**
     * 按项ID查询规范用语项明细
     *
     * @param itemId 项ID
     * @return 明细列表
     */
    @Override
    public List<SysPhraseDetail> selectSysPhraseDetailByItemId(Long itemId)
    {
        return phraseDetailMapper.selectSysPhraseDetailByItemId(itemId);
    }

    /**
     * 查询所有明细（用于App同步）
     *
     * @return 明细列表
     */
    @Override
    public List<SysPhraseDetail> selectAllSysPhraseDetails()
    {
        return phraseDetailMapper.selectAllSysPhraseDetails();
    }

    /**
     * 查询变更的明细（用于增量同步）
     *
     * @param version 当前版本号
     * @return 变更的明细列表
     */
    @Override
    public List<SysPhraseDetail> selectChangedSysPhraseDetails(Integer version)
    {
        return phraseDetailMapper.selectChangedSysPhraseDetails(version);
    }

    /**
     * 新增规范用语项明细
     *
     * @param phraseDetail 规范用语项明细
     * @return 结果
     */
    @Override
    @Transactional
    public int insertSysPhraseDetail(SysPhraseDetail phraseDetail)
    {
        phraseDetail.setCreateTime(DateUtils.getNowDate());
        phraseDetail.setVersion(1);
        phraseDetail.setDelFlag("0");
        return phraseDetailMapper.insertSysPhraseDetail(phraseDetail);
    }

    /**
     * 修改规范用语项明细
     *
     * @param phraseDetail 规范用语项明细
     * @return 结果
     */
    @Override
    @Transactional
    public int updateSysPhraseDetail(SysPhraseDetail phraseDetail)
    {
        phraseDetail.setUpdateTime(DateUtils.getNowDate());
        // 版本号+1
        phraseDetail.setVersion(phraseDetail.getVersion() + 1);
        return phraseDetailMapper.updateSysPhraseDetail(phraseDetail);
    }

    /**
     * 批量删除规范用语项明细
     *
     * @param detailIds 需要删除的明细主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteSysPhraseDetailByDetailIds(Long[] detailIds)
    {
        return phraseDetailMapper.deleteSysPhraseDetailByDetailIds(detailIds);
    }

    /**
     * 删除规范用语项明细信息
     *
     * @param detailId 明细主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteSysPhraseDetailByDetailId(Long detailId)
    {
        return phraseDetailMapper.deleteSysPhraseDetailByDetailId(detailId);
    }

    /**
     * 获取最大版本号
     *
     * @return 最大版本号
     */
    @Override
    public Integer selectMaxVersion()
    {
        Integer version = phraseDetailMapper.selectMaxVersion();
        return version != null ? version : 0;
    }
}
