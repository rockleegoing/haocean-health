package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysBasisChapterLinkMapper;
import com.ruoyi.system.domain.SysBasisChapterLink;
import com.ruoyi.system.service.ISysBasisChapterLinkService;

/**
 * 章节依据关联Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-28
 */
@Service
public class SysBasisChapterLinkServiceImpl implements ISysBasisChapterLinkService {
    @Autowired
    private SysBasisChapterLinkMapper sysBasisChapterLinkMapper;

    /**
     * 查询章节依据关联
     *
     * @param linkId 关联ID
     * @return 章节依据关联
     */
    @Override
    public SysBasisChapterLink selectSysBasisChapterLinkById(Long linkId) {
        return sysBasisChapterLinkMapper.selectSysBasisChapterLinkById(linkId);
    }

    /**
     * 查询章节依据关联列表
     *
     * @param sysBasisChapterLink 章节依据关联
     * @return 章节依据关联
     */
    @Override
    public List<SysBasisChapterLink> selectSysBasisChapterLinkList(SysBasisChapterLink sysBasisChapterLink) {
        return sysBasisChapterLinkMapper.selectSysBasisChapterLinkList(sysBasisChapterLink);
    }

    /**
     * 根据章节ID查询关联列表
     */
    @Override
    public List<SysBasisChapterLink> selectSysBasisChapterLinkByChapterId(Long chapterId) {
        return sysBasisChapterLinkMapper.selectSysBasisChapterLinkByChapterId(chapterId);
    }

    /**
     * 根据章节ID和依据类型查询关联列表
     */
    @Override
    public List<SysBasisChapterLink> selectSysBasisChapterLinkByChapterIdAndType(Long chapterId, String basisType) {
        return sysBasisChapterLinkMapper.selectSysBasisChapterLinkByChapterIdAndType(chapterId, basisType);
    }

    /**
     * 新增章节依据关联
     *
     * @param sysBasisChapterLink 章节依据关联
     * @return 结果
     */
    @Override
    public int insertSysBasisChapterLink(SysBasisChapterLink sysBasisChapterLink) {
        sysBasisChapterLink.setCreateTime(DateUtils.getNowDate());
        return sysBasisChapterLinkMapper.insertSysBasisChapterLink(sysBasisChapterLink);
    }

    /**
     * 修改章节依据关联
     *
     * @param sysBasisChapterLink 章节依据关联
     * @return 结果
     */
    @Override
    public int updateSysBasisChapterLink(SysBasisChapterLink sysBasisChapterLink) {
        sysBasisChapterLink.setUpdateTime(DateUtils.getNowDate());
        return sysBasisChapterLinkMapper.updateSysBasisChapterLink(sysBasisChapterLink);
    }

    /**
     * 批量删除章节依据关联
     *
     * @param linkIds 需要删除的关联主键
     * @return 结果
     */
    @Override
    public int deleteSysBasisChapterLinkByIds(Long[] linkIds) {
        return sysBasisChapterLinkMapper.deleteSysBasisChapterLinkByIds(linkIds);
    }

    /**
     * 删除章节依据关联信息
     *
     * @param linkId 关联ID
     * @return 结果
     */
    @Override
    public int deleteSysBasisChapterLinkById(Long linkId) {
        return sysBasisChapterLinkMapper.deleteSysBasisChapterLinkById(linkId);
    }

    /**
     * 根据章节ID删除所有关联
     */
    @Override
    public int deleteSysBasisChapterLinkByChapterId(Long chapterId) {
        return sysBasisChapterLinkMapper.deleteSysBasisChapterLinkByChapterId(chapterId);
    }

    /**
     * 根据章节ID和依据类型删除关联
     */
    @Override
    public int deleteSysBasisChapterLinkByChapterIdAndType(Long chapterId, String basisType) {
        return sysBasisChapterLinkMapper.deleteSysBasisChapterLinkByChapterIdAndType(chapterId, basisType);
    }
}
