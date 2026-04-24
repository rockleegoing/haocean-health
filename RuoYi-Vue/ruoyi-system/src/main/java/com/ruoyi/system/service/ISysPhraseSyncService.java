package com.ruoyi.system.service;

import java.util.Map;
import com.ruoyi.system.domain.vo.PhraseSyncVO;

/**
 * 规范用语同步Service接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface ISysPhraseSyncService
{
    /**
     * 全量同步 - 获取所有规范用语数据
     *
     * @return 同步数据
     */
    public PhraseSyncVO syncFullData();

    /**
     * 增量同步 - 根据版本号获取变更数据
     *
     * @param bookVersion 书本版本号
     * @param itemVersion 项版本号
     * @param detailVersion 明细版本号
     * @return 同步数据
     */
    public PhraseSyncVO syncIncrementalData(Integer bookVersion, Integer itemVersion, Integer detailVersion);

    /**
     * 获取同步版本信息
     *
     * @return 版本信息
     */
    public Map<String, Integer> getSyncVersions();

    /**
     * 获取书本列表（App端使用）
     *
     * @param industryCode 行业编码
     * @return 书本列表
     */
    public Object getBookList(String industryCode);

    /**
     * 获取项列表（App端使用）
     *
     * @param bookId 书本ID
     * @param phaseType 环节类型
     * @return 项列表
     */
    public Object getItemList(Long bookId, String phaseType);

    /**
     * 获取明细列表（App端使用）
     *
     * @param itemId 项ID
     * @return 明细列表
     */
    public Object getDetailList(Long itemId);
}
