package com.ruoyi.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.vo.PhraseSyncVO;
import com.ruoyi.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 规范用语同步Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-25
 */
@Service
public class SysPhraseSyncServiceImpl implements ISysPhraseSyncService
{
    @Autowired
    private ISysPhraseBookService phraseBookService;

    @Autowired
    private ISysPhraseItemService phraseItemService;

    @Autowired
    private ISysPhraseDetailService phraseDetailService;

    /**
     * 全量同步 - 获取所有规范用语数据
     *
     * @return 同步数据
     */
    @Override
    @Transactional(readOnly = true)
    public PhraseSyncVO syncFullData()
    {
        PhraseSyncVO syncVO = new PhraseSyncVO();

        // 获取所有数据
        syncVO.setBooks(phraseBookService.selectAllSysPhraseBooks());
        syncVO.setItems(phraseItemService.selectAllSysPhraseItems());
        syncVO.setDetails(phraseDetailService.selectAllSysPhraseDetails());

        // 获取最大版本号
        syncVO.setBookVersion(phraseBookService.selectMaxVersion());
        syncVO.setItemVersion(phraseItemService.selectMaxVersion());
        syncVO.setDetailVersion(phraseDetailService.selectMaxVersion());

        // 同步时间
        syncVO.setSyncTime(System.currentTimeMillis());

        return syncVO;
    }

    /**
     * 增量同步 - 根据版本号获取变更数据
     *
     * @param bookVersion 书本版本号
     * @param itemVersion 项版本号
     * @param detailVersion 明细版本号
     * @return 同步数据
     */
    @Override
    @Transactional(readOnly = true)
    public PhraseSyncVO syncIncrementalData(Integer bookVersion, Integer itemVersion, Integer detailVersion)
    {
        PhraseSyncVO syncVO = new PhraseSyncVO();

        // 处理版本号null情况
        int bookVer = bookVersion != null ? bookVersion : 0;
        int itemVer = itemVersion != null ? itemVersion : 0;
        int detailVer = detailVersion != null ? detailVersion : 0;

        // 获取变更的数据
        if (bookVer < phraseBookService.selectMaxVersion())
        {
            syncVO.setBooks(phraseBookService.selectChangedSysPhraseBooks(bookVer));
        }
        if (itemVer < phraseItemService.selectMaxVersion())
        {
            syncVO.setItems(phraseItemService.selectChangedSysPhraseItems(itemVer));
        }
        if (detailVer < phraseDetailService.selectMaxVersion())
        {
            syncVO.setDetails(phraseDetailService.selectChangedSysPhraseDetails(detailVer));
        }

        // 获取当前最大版本号
        syncVO.setBookVersion(phraseBookService.selectMaxVersion());
        syncVO.setItemVersion(phraseItemService.selectMaxVersion());
        syncVO.setDetailVersion(phraseDetailService.selectMaxVersion());

        // 同步时间
        syncVO.setSyncTime(System.currentTimeMillis());

        return syncVO;
    }

    /**
     * 获取同步版本信息
     *
     * @return 版本信息
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> getSyncVersions()
    {
        Map<String, Integer> versions = new HashMap<>();
        versions.put("bookVersion", phraseBookService.selectMaxVersion());
        versions.put("itemVersion", phraseItemService.selectMaxVersion());
        versions.put("detailVersion", phraseDetailService.selectMaxVersion());
        return versions;
    }

    /**
     * 获取书本列表（App端使用）
     *
     * @param industryCode 行业编码
     * @return 书本列表
     */
    @Override
    @Transactional(readOnly = true)
    public Object getBookList(String industryCode)
    {
        com.ruoyi.system.domain.SysPhraseBook phraseBook = new com.ruoyi.system.domain.SysPhraseBook();
        phraseBook.setIndustryCode(industryCode);
        return phraseBookService.selectSysPhraseBookList(phraseBook);
    }

    /**
     * 获取项列表（App端使用）
     *
     * @param bookId 书本ID
     * @param phaseType 环节类型
     * @return 项列表
     */
    @Override
    @Transactional(readOnly = true)
    public Object getItemList(Long bookId, String phaseType)
    {
        if (phaseType != null && !phaseType.isEmpty())
        {
            return phraseItemService.selectSysPhraseItemByBookIdAndPhase(bookId, phaseType);
        }
        else
        {
            return phraseItemService.selectSysPhraseItemByBookId(bookId);
        }
    }

    /**
     * 获取明细列表（App端使用）
     *
     * @param itemId 项ID
     * @return 明细列表
     */
    @Override
    @Transactional(readOnly = true)
    public Object getDetailList(Long itemId)
    {
        return phraseDetailService.selectSysPhraseDetailByItemId(itemId);
    }
}
