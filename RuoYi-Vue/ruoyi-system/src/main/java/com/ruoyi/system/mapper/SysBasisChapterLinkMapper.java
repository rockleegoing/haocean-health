package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysBasisChapterLink;

/**
 * 依据章节关联Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-28
 */
public interface SysBasisChapterLinkMapper {
    /**
     * 查询关联列表
     *
     * @param sysBasisChapterLink 关联
     * @return 关联集合
     */
    List<SysBasisChapterLink> selectBasisChapterLinkList(SysBasisChapterLink sysBasisChapterLink);

    /**
     * 根据条款ID查询关联的定性依据
     *
     * @param articleId 条款ID
     * @return 关联列表
     */
    List<Map<String, Object>> selectLegalBasisByArticle(Long articleId);

    /**
     * 根据条款ID查询关联的处理依据
     *
     * @param articleId 条款ID
     * @return 关联列表
     */
    List<Map<String, Object>> selectProcessingBasisByArticle(Long articleId);

    /**
     * 新增关联
     *
     * @param sysBasisChapterLink 关联
     * @return 结果
     */
    int insertBasisChapterLink(SysBasisChapterLink sysBasisChapterLink);

    /**
     * 删除关联
     *
     * @param linkId 关联主键
     * @return 结果
     */
    int deleteBasisChapterLinkById(Long linkId);

    /**
     * 批量删除关联
     *
     * @param linkIds 需要删除的关联主键
     * @return 结果
     */
    int deleteBasisChapterLinkByIds(Long[] linkIds);

    /**
     * 获取章节下条目的关联依据统计
     *
     * @param regulationId 法规ID
     * @return 统计结果
     */
    List<Map<String, Object>> selectArticleBasisCountByRegulation(Long regulationId);
}
