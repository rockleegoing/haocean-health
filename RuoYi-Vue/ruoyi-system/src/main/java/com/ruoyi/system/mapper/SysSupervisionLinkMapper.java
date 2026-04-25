package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysSupervisionLanguageLink;
import com.ruoyi.system.domain.SysSupervisionRegulationLink;

/**
 * 监管事项关联Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysSupervisionLinkMapper
{
    /**
     * 查询监管事项关联的规范用语
     *
     * @param itemId 监管事项ID
     * @return 关联列表
     */
    public List<SysSupervisionLanguageLink> selectLanguageLinksByItemId(Long itemId);

    /**
     * 查询监管事项关联的法律法规
     *
     * @param itemId 监管事项ID
     * @return 关联列表
     */
    public List<SysSupervisionRegulationLink> selectRegulationLinksByItemId(Long itemId);

    /**
     * 添加规范用语关联
     *
     * @param link 关联
     * @return 结果
     */
    public int insertLanguageLink(SysSupervisionLanguageLink link);

    /**
     * 添加法律法规关联
     *
     * @param link 关联
     * @return 结果
     */
    public int insertRegulationLink(SysSupervisionRegulationLink link);

    /**
     * 删除规范用语关联
     *
     * @param linkId 关联ID
     * @return 结果
     */
    public int deleteLanguageLinkById(Long linkId);

    /**
     * 删除法律法规关联
     *
     * @param linkId 关联ID
     * @return 结果
     */
    public int deleteRegulationLinkById(Long linkId);

    /**
     * 删除事项的所有关联
     *
     * @param itemId 事项ID
     * @return 结果
     */
    public int deleteLinksByItemId(Long itemId);
}
