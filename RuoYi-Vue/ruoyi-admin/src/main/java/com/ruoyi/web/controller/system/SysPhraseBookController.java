package com.ruoyi.web.controller.system;

import java.util.List;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysPhraseBook;
import com.ruoyi.system.service.ISysPhraseBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 规范用语书本管理
 */
@RestController
@RequestMapping("/system/phrase/book")
public class SysPhraseBookController extends BaseController
{
    @Autowired
    private ISysPhraseBookService phraseBookService;

    /**
     * 获取书本列表
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list(SysPhraseBook phraseBook)
    {
        List<SysPhraseBook> list = phraseBookService.selectSysPhraseBookList(phraseBook);
        return AjaxResult.success(list);
    }

    /**
     * 获取书本详情
     */
    @Anonymous
    @GetMapping("/{bookId}")
    public AjaxResult getInfo(@PathVariable("bookId") Long bookId)
    {
        return AjaxResult.success(phraseBookService.selectSysPhraseBookByBookId(bookId));
    }

    /**
     * 新增书本
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody SysPhraseBook phraseBook)
    {
        return AjaxResult.success(phraseBookService.insertSysPhraseBook(phraseBook));
    }

    /**
     * 修改书本
     */
    @Anonymous
    @PutMapping
    public AjaxResult edit(@RequestBody SysPhraseBook phraseBook)
    {
        return AjaxResult.success(phraseBookService.updateSysPhraseBook(phraseBook));
    }

    /**
     * 删除书本
     */
    @Anonymous
    @DeleteMapping("/{bookIds}")
    public AjaxResult remove(@PathVariable Long[] bookIds)
    {
        return AjaxResult.success(phraseBookService.deleteSysPhraseBookByBookIds(bookIds));
    }
}
