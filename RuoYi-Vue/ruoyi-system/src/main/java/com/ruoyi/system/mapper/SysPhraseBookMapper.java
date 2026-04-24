package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysPhraseBook;

/**
 * 规范用语书本Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysPhraseBookMapper
{
    /**
     * 查询规范用语书本
     *
     * @param bookId 书本主键
     * @return 规范用语书本
     */
    public SysPhraseBook selectSysPhraseBookByBookId(Long bookId);

    /**
     * 查询规范用语书本列表
     *
     * @param phraseBook 规范用语书本
     * @return 规范用语书本集合
     */
    public List<SysPhraseBook> selectSysPhraseBookList(SysPhraseBook phraseBook);

    /**
     * 查询所有书本（用于App同步）
     *
     * @return 书本列表
     */
    public List<SysPhraseBook> selectAllSysPhraseBooks();

    /**
     * 查询变更的书本（用于增量同步）
     *
     * @param version 当前版本号
     * @return 变更的书本列表
     */
    public List<SysPhraseBook> selectChangedSysPhraseBooks(Integer version);

    /**
     * 新增规范用语书本
     *
     * @param phraseBook 规范用语书本
     * @return 结果
     */
    public int insertSysPhraseBook(SysPhraseBook phraseBook);

    /**
     * 修改规范用语书本
     *
     * @param phraseBook 规范用语书本
     * @return 结果
     */
    public int updateSysPhraseBook(SysPhraseBook phraseBook);

    /**
     * 删除规范用语书本
     *
     * @param bookId 书本主键
     * @return 结果
     */
    public int deleteSysPhraseBookByBookId(Long bookId);

    /**
     * 批量删除规范用语书本
     *
     * @param bookIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysPhraseBookByBookIds(Long[] bookIds);

    /**
     * 获取最大版本号
     *
     * @return 最大版本号
     */
    public Integer selectMaxVersion();
}
