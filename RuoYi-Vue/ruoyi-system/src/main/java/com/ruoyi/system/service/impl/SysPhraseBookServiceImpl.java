package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.SysPhraseBookMapper;
import com.ruoyi.system.domain.SysPhraseBook;
import com.ruoyi.system.service.ISysPhraseBookService;

/**
 * 规范用语书本Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-25
 */
@Service
public class SysPhraseBookServiceImpl implements ISysPhraseBookService
{
    @Autowired
    private SysPhraseBookMapper phraseBookMapper;

    /**
     * 查询规范用语书本
     *
     * @param bookId 书本主键
     * @return 规范用语书本
     */
    @Override
    public SysPhraseBook selectSysPhraseBookByBookId(Long bookId)
    {
        return phraseBookMapper.selectSysPhraseBookByBookId(bookId);
    }

    /**
     * 查询规范用语书本列表
     *
     * @param phraseBook 规范用语书本
     * @return 规范用语书本
     */
    @Override
    public List<SysPhraseBook> selectSysPhraseBookList(SysPhraseBook phraseBook)
    {
        return phraseBookMapper.selectSysPhraseBookList(phraseBook);
    }

    /**
     * 查询所有书本（用于App同步）
     *
     * @return 书本列表
     */
    @Override
    public List<SysPhraseBook> selectAllSysPhraseBooks()
    {
        return phraseBookMapper.selectAllSysPhraseBooks();
    }

    /**
     * 查询变更的书本（用于增量同步）
     *
     * @param version 当前版本号
     * @return 变更的书本列表
     */
    @Override
    public List<SysPhraseBook> selectChangedSysPhraseBooks(Integer version)
    {
        return phraseBookMapper.selectChangedSysPhraseBooks(version);
    }

    /**
     * 新增规范用语书本
     *
     * @param phraseBook 规范用语书本
     * @return 结果
     */
    @Override
    @Transactional
    public int insertSysPhraseBook(SysPhraseBook phraseBook)
    {
        if (!checkBookCodeUnique(phraseBook))
        {
            throw new ServiceException("书本编码已存在");
        }
        phraseBook.setCreateTime(DateUtils.getNowDate());
        phraseBook.setVersion(1);
        phraseBook.setDelFlag("0");
        return phraseBookMapper.insertSysPhraseBook(phraseBook);
    }

    /**
     * 修改规范用语书本
     *
     * @param phraseBook 规范用语书本
     * @return 结果
     */
    @Override
    @Transactional
    public int updateSysPhraseBook(SysPhraseBook phraseBook)
    {
        if (!checkBookCodeUnique(phraseBook))
        {
            throw new ServiceException("书本编码已存在");
        }
        phraseBook.setUpdateTime(DateUtils.getNowDate());
        // 版本号+1
        phraseBook.setVersion(phraseBook.getVersion() + 1);
        return phraseBookMapper.updateSysPhraseBook(phraseBook);
    }

    /**
     * 批量删除规范用语书本
     *
     * @param bookIds 需要删除的书本主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteSysPhraseBookByBookIds(Long[] bookIds)
    {
        return phraseBookMapper.deleteSysPhraseBookByBookIds(bookIds);
    }

    /**
     * 删除规范用语书本信息
     *
     * @param bookId 书本主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteSysPhraseBookByBookId(Long bookId)
    {
        return phraseBookMapper.deleteSysPhraseBookByBookId(bookId);
    }

    /**
     * 获取最大版本号
     *
     * @return 最大版本号
     */
    @Override
    public Integer selectMaxVersion()
    {
        Integer version = phraseBookMapper.selectMaxVersion();
        return version != null ? version : 0;
    }

    /**
     * 校验书本编码唯一性
     *
     * @param phraseBook 书本
     * @return 结果
     */
    @Override
    public boolean checkBookCodeUnique(SysPhraseBook phraseBook)
    {
        SysPhraseBook unique = new SysPhraseBook();
        unique.setBookCode(phraseBook.getBookCode());
        List<SysPhraseBook> list = phraseBookMapper.selectSysPhraseBookList(unique);
        if (list != null && !list.isEmpty())
        {
            // 如果是更新操作，且ID相同，则忽略
            if (phraseBook.getBookId() != null && list.get(0).getBookId().equals(phraseBook.getBookId()))
            {
                return true;
            }
            return false;
        }
        return true;
    }
}
