package com.ruoyi.web.controller.app;

import java.util.Map;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.vo.PhraseSyncVO;
import com.ruoyi.system.service.ISysPhraseSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 规范用语同步Controller（App端使用）
 */
@RestController
@RequestMapping("/app/phrase")
public class SysPhraseSyncController extends BaseController
{
    @Autowired
    private ISysPhraseSyncService phraseSyncService;

    /**
     * 全量同步 - 获取所有规范用语数据
     */
    @Anonymous
    @GetMapping("/sync/full")
    public AjaxResult syncFull()
    {
        PhraseSyncVO syncData = phraseSyncService.syncFullData();
        return AjaxResult.success(syncData);
    }

    /**
     * 增量同步 - 根据版本号获取变更数据
     */
    @Anonymous
    @GetMapping("/sync/incremental")
    public AjaxResult syncIncremental(@RequestParam(required = false) Integer bookVersion,
                                     @RequestParam(required = false) Integer itemVersion,
                                     @RequestParam(required = false) Integer detailVersion)
    {
        PhraseSyncVO syncData = phraseSyncService.syncIncrementalData(bookVersion, itemVersion, detailVersion);
        return AjaxResult.success(syncData);
    }

    /**
     * 获取同步版本信息
     */
    @Anonymous
    @GetMapping("/sync/version")
    public AjaxResult getSyncVersion()
    {
        Map<String, Integer> versions = phraseSyncService.getSyncVersions();
        return AjaxResult.success(versions);
    }

    /**
     * 获取书本列表（App端使用）
     */
    @Anonymous
    @GetMapping("/book/list")
    public AjaxResult getBookList(@RequestParam(required = false) String industryCode)
    {
        return AjaxResult.success(phraseSyncService.getBookList(industryCode));
    }

    /**
     * 获取项列表（App端使用）
     */
    @Anonymous
    @GetMapping("/item/list/{bookId}")
    public AjaxResult getItemList(@PathVariable Long bookId,
                                  @RequestParam(required = false) String phaseType)
    {
        return AjaxResult.success(phraseSyncService.getItemList(bookId, phaseType));
    }

    /**
     * 获取明细列表（App端使用）
     */
    @Anonymous
    @GetMapping("/detail/list/{itemId}")
    public AjaxResult getDetailList(@PathVariable Long itemId)
    {
        return AjaxResult.success(phraseSyncService.getDetailList(itemId));
    }
}
