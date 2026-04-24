package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysPhraseItem;

/**
 * 规范用语项Service接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface ISysPhraseItemService
{
    /**
     * 查询规范用语项
     *
     * @param itemId 项主键
     * @return 规范用语项
     */
    public SysPhraseItem selectSysPhraseItemByItemId(Long itemId);

    /**
     * 查询规范用语项列表
     *
     * @param phraseItem 规范用语项
     * @return 规范用语项集合
     */
    public List<SysPhraseItem> selectSysPhraseItemList(SysPhraseItem phraseItem);

    /**
     * 按书本查询规范用语项
     *
     * @param bookId 书本ID
     * @return 规范用语项列表
     */
    public List<SysPhraseItem> selectSysPhraseItemByBookId(Long bookId);

    /**
     * 按书本和环节查询规范用语项
     *
     * @param bookId 书本ID
     * @param phaseType 环节类型
     * @return 规范用语项列表
     */
    public List<SysPhraseItem> selectSysPhraseItemByBookIdAndPhase(Long bookId, String phaseType);

    /**
     * 查询所有项（用于App同步）
     *
     * @return 项列表
     */
    public List<SysPhraseItem> selectAllSysPhraseItems();

    /**
     * 查询变更的项（用于增量同步）
     *
     * @param version 当前版本号
     * @return 变更的项列表
     */
    public List<SysPhraseItem> selectChangedSysPhraseItems(Integer version);

    /**
     * 新增规范用语项
     *
     * @param phraseItem 规范用语项
     * @return 结果
     */
    public int insertSysPhraseItem(SysPhraseItem phraseItem);

    /**
     * 修改规范用语项
     *
     * @param phraseItem 规范用语项
     * @return 结果
     */
    public int updateSysPhraseItem(SysPhraseItem phraseItem);

    /**
     * 批量删除规范用语项
     *
     * @param itemIds 需要删除的项主键集合
     * @return 结果
     */
    public int deleteSysPhraseItemByItemIds(Long[] itemIds);

    /**
     * 删除规范用语项信息
     *
     * @param itemId 项主键
     * @return 结果
     */
    public int deleteSysPhraseItemByItemId(Long itemId);

    /**
     * 获取最大版本号
     *
     * @return 最大版本号
     */
    public Integer selectMaxVersion();

    /**
     * 校验项编码唯一性
     *
     * @param phraseItem 项
     * @return 结果
     */
    public boolean checkItemCodeUnique(SysPhraseItem phraseItem);
}
