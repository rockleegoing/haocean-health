package com.ruoyi.web.controller.system;

import java.util.List;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysPhraseItem;
import com.ruoyi.system.service.ISysPhraseItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 规范用语项管理
 */
@RestController
@RequestMapping("/system/phrase/item")
public class SysPhraseItemController extends BaseController
{
    @Autowired
    private ISysPhraseItemService phraseItemService;

    /**
     * 获取项列表
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list(SysPhraseItem phraseItem)
    {
        List<SysPhraseItem> list = phraseItemService.selectSysPhraseItemList(phraseItem);
        return AjaxResult.success(list);
    }

    /**
     * 按书本获取项列表
     */
    @Anonymous
    @GetMapping("/listByBook/{bookId}")
    public AjaxResult listByBook(@PathVariable("bookId") Long bookId,
                                 @RequestParam(required = false) String phaseType)
    {
        List<SysPhraseItem> list;
        if (phaseType != null && !phaseType.isEmpty())
        {
            list = phraseItemService.selectSysPhraseItemByBookIdAndPhase(bookId, phaseType);
        }
        else
        {
            list = phraseItemService.selectSysPhraseItemByBookId(bookId);
        }
        return AjaxResult.success(list);
    }

    /**
     * 获取项详情
     */
    @Anonymous
    @GetMapping("/{itemId}")
    public AjaxResult getInfo(@PathVariable("itemId") Long itemId)
    {
        return AjaxResult.success(phraseItemService.selectSysPhraseItemByItemId(itemId));
    }

    /**
     * 新增项
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody SysPhraseItem phraseItem)
    {
        return AjaxResult.success(phraseItemService.insertSysPhraseItem(phraseItem));
    }

    /**
     * 修改项
     */
    @Anonymous
    @PutMapping
    public AjaxResult edit(@RequestBody SysPhraseItem phraseItem)
    {
        return AjaxResult.success(phraseItemService.updateSysPhraseItem(phraseItem));
    }

    /**
     * 删除项
     */
    @Anonymous
    @DeleteMapping("/{itemIds}")
    public AjaxResult remove(@PathVariable Long[] itemIds)
    {
        return AjaxResult.success(phraseItemService.deleteSysPhraseItemByItemIds(itemIds));
    }
}
