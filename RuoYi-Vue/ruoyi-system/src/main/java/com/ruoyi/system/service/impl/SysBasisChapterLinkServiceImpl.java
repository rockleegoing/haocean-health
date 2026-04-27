package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysBasisChapterLinkMapper;
import com.ruoyi.system.domain.SysBasisChapterLink;
import com.ruoyi.system.service.ISysBasisChapterLinkService;

/**
 * 依据章节关联Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-28
 */
@Service
public class SysBasisChapterLinkServiceImpl implements ISysBasisChapterLinkService {
    @Autowired
    private SysBasisChapterLinkMapper sysBasisChapterLinkMapper;

    /**
     * 查询关联列表
     *
     * @param sysBasisChapterLink 关联
     * @return 关联
     */
    @Override
    public List<SysBasisChapterLink> selectBasisChapterLinkList(SysBasisChapterLink sysBasisChapterLink) {
        return sysBasisChapterLinkMapper.selectBasisChapterLinkList(sysBasisChapterLink);
    }

    /**
     * 根据条款ID查询关联的定性依据
     *
     * @param articleId 条款ID
     * @return 关联列表
     */
    @Override
    public List<Map<String, Object>> selectLegalBasisByArticle(Long articleId) {
        return sysBasisChapterLinkMapper.selectLegalBasisByArticle(articleId);
    }

    /**
     * 根据条款ID查询关联的处理依据
     *
     * @param articleId 条款ID
     * @return 关联列表
     */
    @Override
    public List<Map<String, Object>> selectProcessingBasisByArticle(Long articleId) {
        return sysBasisChapterLinkMapper.selectProcessingBasisByArticle(articleId);
    }

    /**
     * 新增关联
     *
     * @param sysBasisChapterLink 关联
     * @return 结果
     */
    @Override
    public int insertBasisChapterLink(SysBasisChapterLink sysBasisChapterLink) {
        return sysBasisChapterLinkMapper.insertBasisChapterLink(sysBasisChapterLink);
    }

    /**
     * 删除关联
     *
     * @param linkId 关联主键
     * @return 结果
     */
    @Override
    public int deleteBasisChapterLinkById(Long linkId) {
        return sysBasisChapterLinkMapper.deleteBasisChapterLinkById(linkId);
    }

    /**
     * 批量删除关联
     *
     * @param linkIds 需要删除的关联主键
     * @return 结果
     */
    @Override
    public int deleteBasisChapterLinkByIds(Long[] linkIds) {
        return sysBasisChapterLinkMapper.deleteBasisChapterLinkByIds(linkIds);
    }

    /**
     * 获取章节下条目的关联依据统计
     *
     * @param regulationId 法规ID
     * @return 统计结果
     */
    @Override
    public List<Map<String, Object>> selectArticleBasisCountByRegulation(Long regulationId) {
        return sysBasisChapterLinkMapper.selectArticleBasisCountByRegulation(regulationId);
    }
}
