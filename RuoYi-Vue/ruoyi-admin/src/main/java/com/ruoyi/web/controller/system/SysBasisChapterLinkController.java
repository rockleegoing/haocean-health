package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysBasisChapterLink;
import com.ruoyi.system.service.ISysBasisChapterLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 依据章节关联管理
 */
@RestController
@RequestMapping("/system/basisLink")
public class SysBasisChapterLinkController extends BaseController {

    @Autowired
    private ISysBasisChapterLinkService sysBasisChapterLinkService;

    /**
     * 获取关联列表
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list(SysBasisChapterLink sysBasisChapterLink) {
        List<SysBasisChapterLink> list = sysBasisChapterLinkService.selectBasisChapterLinkList(sysBasisChapterLink);
        return AjaxResult.success(list);
    }

    /**
     * 根据条款ID获取关联的定性依据
     */
    @Anonymous
    @GetMapping("/legalBasis/{articleId}")
    public AjaxResult getLegalBasisByArticle(@PathVariable("articleId") Long articleId) {
        List<Map<String, Object>> list = sysBasisChapterLinkService.selectLegalBasisByArticle(articleId);
        return AjaxResult.success(list);
    }

    /**
     * 根据条款ID获取关联的处理依据
     */
    @Anonymous
    @GetMapping("/processingBasis/{articleId}")
    public AjaxResult getProcessingBasisByArticle(@PathVariable("articleId") Long articleId) {
        List<Map<String, Object>> list = sysBasisChapterLinkService.selectProcessingBasisByArticle(articleId);
        return AjaxResult.success(list);
    }

    /**
     * 新增关联
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody SysBasisChapterLink sysBasisChapterLink) {
        return AjaxResult.success(sysBasisChapterLinkService.insertBasisChapterLink(sysBasisChapterLink));
    }

    /**
     * 删除关联
     */
    @Anonymous
    @DeleteMapping("/{linkIds}")
    public AjaxResult remove(@PathVariable Long[] linkIds) {
        return AjaxResult.success(sysBasisChapterLinkService.deleteBasisChapterLinkByIds(linkIds));
    }

    /**
     * 获取某法规的章节下条目关联依据统计
     */
    @Anonymous
    @GetMapping("/articleBasisCount/{regulationId}")
    public AjaxResult getArticleBasisCount(@PathVariable("regulationId") Long regulationId) {
        List<Map<String, Object>> result = sysBasisChapterLinkService.selectArticleBasisCountByRegulation(regulationId);
        return AjaxResult.success(result);
    }
}
