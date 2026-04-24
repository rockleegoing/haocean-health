package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.SysPhraseItemMapper;
import com.ruoyi.system.domain.SysPhraseItem;
import com.ruoyi.system.service.ISysPhraseItemService;

/**
 * 规范用语项Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-25
 */
@Service
public class SysPhraseItemServiceImpl implements ISysPhraseItemService
{
    @Autowired
    private SysPhraseItemMapper phraseItemMapper;

    /**
     * 查询规范用语项
     *
     * @param itemId 项主键
     * @return 规范用语项
     */
    @Override
    public SysPhraseItem selectSysPhraseItemByItemId(Long itemId)
    {
        return phraseItemMapper.selectSysPhraseItemByItemId(itemId);
    }

    /**
     * 查询规范用语项列表
     *
     * @param phraseItem 规范用语项
     * @return 规范用语项
     */
    @Override
    public List<SysPhraseItem> selectSysPhraseItemList(SysPhraseItem phraseItem)
    {
        return phraseItemMapper.selectSysPhraseItemList(phraseItem);
    }

    /**
     * 按书本查询规范用语项
     *
     * @param bookId 书本ID
     * @return 规范用语项列表
     */
    @Override
    public List<SysPhraseItem> selectSysPhraseItemByBookId(Long bookId)
    {
        return phraseItemMapper.selectSysPhraseItemByBookId(bookId);
    }

    /**
     * 按书本和环节查询规范用语项
     *
     * @param bookId 书本ID
     * @param phaseType 环节类型
     * @return 规范用语项列表
     */
    @Override
    public List<SysPhraseItem> selectSysPhraseItemByBookIdAndPhase(Long bookId, String phaseType)
    {
        return phraseItemMapper.selectSysPhraseItemByBookIdAndPhase(bookId, phaseType);
    }

    /**
     * 查询所有项（用于App同步）
     *
     * @return 项列表
     */
    @Override
    public List<SysPhraseItem> selectAllSysPhraseItems()
    {
        return phraseItemMapper.selectAllSysPhraseItems();
    }

    /**
     * 查询变更的项（用于增量同步）
     *
     * @param version 当前版本号
     * @return 变更的项列表
     */
    @Override
    public List<SysPhraseItem> selectChangedSysPhraseItems(Integer version)
    {
        return phraseItemMapper.selectChangedSysPhraseItems(version);
    }

    /**
     * 新增规范用语项
     *
     * @param phraseItem 规范用语项
     * @return 结果
     */
    @Override
    @Transactional
    public int insertSysPhraseItem(SysPhraseItem phraseItem)
    {
        if (!checkItemCodeUnique(phraseItem))
        {
            throw new ServiceException("项编码已存在");
        }
        phraseItem.setCreateTime(DateUtils.getNowDate());
        phraseItem.setVersion(1);
        phraseItem.setDelFlag("0");
        return phraseItemMapper.insertSysPhraseItem(phraseItem);
    }

    /**
     * 修改规范用语项
     *
     * @param phraseItem 规范用语项
     * @return 结果
     */
    @Override
    @Transactional
    public int updateSysPhraseItem(SysPhraseItem phraseItem)
    {
        if (!checkItemCodeUnique(phraseItem))
        {
            throw new ServiceException("项编码已存在");
        }
        phraseItem.setUpdateTime(DateUtils.getNowDate());
        // 版本号+1
        phraseItem.setVersion(phraseItem.getVersion() + 1);
        return phraseItemMapper.updateSysPhraseItem(phraseItem);
    }

    /**
     * 批量删除规范用语项
     *
     * @param itemIds 需要删除的项主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteSysPhraseItemByItemIds(Long[] itemIds)
    {
        return phraseItemMapper.deleteSysPhraseItemByItemIds(itemIds);
    }

    /**
     * 删除规范用语项信息
     *
     * @param itemId 项主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteSysPhraseItemByItemId(Long itemId)
    {
        return phraseItemMapper.deleteSysPhraseItemByItemId(itemId);
    }

    /**
     * 获取最大版本号
     *
     * @return 最大版本号
     */
    @Override
    public Integer selectMaxVersion()
    {
        Integer version = phraseItemMapper.selectMaxVersion();
        return version != null ? version : 0;
    }

    /**
     * 校验项编码唯一性
     *
     * @param phraseItem 项
     * @return 结果
     */
    @Override
    public boolean checkItemCodeUnique(SysPhraseItem phraseItem)
    {
        SysPhraseItem unique = new SysPhraseItem();
        unique.setItemCode(phraseItem.getItemCode());
        List<SysPhraseItem> list = phraseItemMapper.selectSysPhraseItemList(unique);
        if (list != null && !list.isEmpty())
        {
            // 如果是更新操作，且ID相同，则忽略
            if (phraseItem.getItemId() != null && list.get(0).getItemId().equals(phraseItem.getItemId()))
            {
                return true;
            }
            return false;
        }
        return true;
    }
}
