package com.ruoyi.web.controller.system;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysRegulation;
import com.ruoyi.system.domain.SysRegulationChapter;
import com.ruoyi.system.domain.SysRegulationArticle;
import com.ruoyi.system.domain.vo.RegulationImportVo;
import com.ruoyi.system.service.ISysRegulationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 法律法规管理
 */
@RestController
@RequestMapping("/system/regulation")
public class SysRegulationController extends BaseController {

    @Autowired
    private ISysRegulationService sysRegulationService;

    /**
     * 获取法律法规列表
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list(SysRegulation sysRegulation) {
        List<SysRegulation> list = sysRegulationService.selectSysRegulationList(sysRegulation);
        return AjaxResult.success(list);
    }

    /**
     * 获取法律法规详情
     */
    @Anonymous
    @GetMapping("/{regulationId}")
    public AjaxResult getInfo(@PathVariable("regulationId") Long regulationId) {
        SysRegulation regulation = sysRegulationService.selectSysRegulationById(regulationId);
        return AjaxResult.success(regulation);
    }

    /**
     * 新增法律法规
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody SysRegulation sysRegulation) {
        return AjaxResult.success(sysRegulationService.insertSysRegulation(sysRegulation));
    }

    /**
     * 修改法律法规
     */
    @Anonymous
    @PutMapping
    public AjaxResult edit(@RequestBody SysRegulation sysRegulation) {
        return AjaxResult.success(sysRegulationService.updateSysRegulation(sysRegulation));
    }

    /**
     * 删除法律法规
     */
    @Anonymous
    @DeleteMapping("/{regulationIds}")
    public AjaxResult remove(@PathVariable Long[] regulationIds) {
        return AjaxResult.success(sysRegulationService.deleteSysRegulationByIds(regulationIds));
    }

    /**
     * 获取章节列表
     */
    @Anonymous
    @GetMapping("/chapters/{regulationId}")
    public TableDataInfo getChapterList(
        @PathVariable("regulationId") Long regulationId,
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize) {
        startPage();
        List<SysRegulationChapter> list = sysRegulationService.selectChapterListByRegulationId(regulationId);
        return getDataTable(list);
    }

    /**
     * 获取章节详情
     */
    @Anonymous
    @GetMapping("/chapter/{chapterId}")
    public AjaxResult getChapterInfo(@PathVariable("chapterId") Long chapterId) {
        SysRegulationChapter chapter = sysRegulationService.selectSysRegulationChapterById(chapterId);
        return AjaxResult.success(chapter);
    }

    /**
     * 获取条款列表
     */
    @Anonymous
    @GetMapping("/articles/{regulationId}")
    public TableDataInfo getArticleList(
        @PathVariable("regulationId") Long regulationId,
        @RequestParam(required = false) Long chapterId,
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize) {
        startPage();
        List<SysRegulationArticle> list = sysRegulationService.selectArticleListByRegulationId(regulationId, chapterId);
        return getDataTable(list);
    }

    /**
     * 获取条款详情
     */
    @Anonymous
    @GetMapping("/article/{articleId}")
    public AjaxResult getArticleInfo(@PathVariable("articleId") Long articleId) {
        SysRegulationArticle article = sysRegulationService.selectSysRegulationArticleById(articleId);
        return AjaxResult.success(article);
    }

    /**
     * 新增章节
     */
    @Anonymous
    @PostMapping("/chapter")
    public AjaxResult addChapter(@RequestBody SysRegulationChapter chapter) {
        return AjaxResult.success(sysRegulationService.insertSysRegulationChapter(chapter));
    }

    /**
     * 修改章节
     */
    @Anonymous
    @PutMapping("/chapter")
    public AjaxResult editChapter(@RequestBody SysRegulationChapter chapter) {
        return AjaxResult.success(sysRegulationService.updateSysRegulationChapter(chapter));
    }

    /**
     * 删除章节
     */
    @Anonymous
    @DeleteMapping("/chapter/{chapterIds}")
    public AjaxResult removeChapter(@PathVariable Long[] chapterIds) {
        return AjaxResult.success(sysRegulationService.deleteSysRegulationChapterByIds(chapterIds));
    }

    /**
     * 新增条款
     */
    @Anonymous
    @PostMapping("/article")
    public AjaxResult addArticle(@RequestBody SysRegulationArticle article) {
        return AjaxResult.success(sysRegulationService.insertSysRegulationArticle(article));
    }

    /**
     * 修改条款
     */
    @Anonymous
    @PutMapping("/article")
    public AjaxResult editArticle(@RequestBody SysRegulationArticle article) {
        return AjaxResult.success(sysRegulationService.updateSysRegulationArticle(article));
    }

    /**
     * 删除条款
     */
    @Anonymous
    @DeleteMapping("/article/{articleIds}")
    public AjaxResult removeArticle(@PathVariable Long[] articleIds) {
        return AjaxResult.success(sysRegulationService.deleteSysRegulationArticleByIds(articleIds));
    }

    /**
     * 导出法律法规 Excel
     */
    @Anonymous
    @GetMapping("/export")
    public void export(HttpServletResponse response, SysRegulation sysRegulation) {
        List<SysRegulation> list = sysRegulationService.selectSysRegulationList(sysRegulation);
        ExcelUtil.exportExcel(response, list, "法律法规数据", SysRegulation.class);
    }

    /**
     * 导入法律法规 Excel
     */
    @Anonymous
    @PostMapping("/import")
    public AjaxResult importExcel(MultipartFile file, boolean updateSupport) throws Exception {
        List<RegulationImportVo> voList = ExcelUtil.importExcel(file.getInputStream(), RegulationImportVo.class);
        String operName = getUsername();
        Map<String, Object> result = sysRegulationService.importRegulation(voList, updateSupport, operName);
        return AjaxResult.success("导入成功，成功" + result.get("success") + "条，失败" + result.get("fail") + "条", result.get("errors"));
    }

    /**
     * 导出法律法规 JSON
     */
    @Anonymous
    @GetMapping("/export/json")
    public void exportJson(HttpServletResponse response, SysRegulation sysRegulation) {
        List<SysRegulation> list = sysRegulationService.selectSysRegulationList(sysRegulation);
        // 构建带章节和条款的完整JSON结构
        List<RegulationImportVo> voList = list.stream().map(regulation -> {
            RegulationImportVo vo = new RegulationImportVo();
            vo.setTitle(regulation.getTitle());
            vo.setLegalType(regulation.getLegalType());
            vo.setSupervisionTypes(regulation.getSupervisionTypes());
            vo.setPublishDate(regulation.getPublishDate());
            vo.setEffectiveDate(regulation.getEffectiveDate());
            vo.setIssuingAuthority(regulation.getIssuingAuthority());
            vo.setContent(regulation.getContent());

            // 查询章节
            List<SysRegulationChapter> chapters = sysRegulationService.selectChapterListByRegulationId(regulation.getRegulationId());
            if (chapters != null && !chapters.isEmpty()) {
                List<ChapterImportVo> chapterVoList = chapters.stream().map(chapter -> {
                    ChapterImportVo chapterVo = new ChapterImportVo();
                    chapterVo.setChapterNo(chapter.getChapterNo());
                    chapterVo.setChapterTitle(chapter.getChapterTitle());
                    chapterVo.setSortOrder(chapter.getSortOrder());

                    // 查询条款
                    List<SysRegulationArticle> articles = sysRegulationService.selectArticleListByRegulationId(regulation.getRegulationId(), chapter.getChapterId());
                    if (articles != null && !articles.isEmpty()) {
                        List<ArticleImportVo> articleVoList = articles.stream().map(article -> {
                            ArticleImportVo articleVo = new ArticleImportVo();
                            articleVo.setArticleNo(article.getArticleNo());
                            articleVo.setContent(article.getContent());
                            articleVo.setSortOrder(article.getSortOrder());
                            return articleVo;
                        }).collect(Collectors.toList());
                        chapterVo.setArticles(articleVoList);
                    }
                    return chapterVo;
                }).collect(Collectors.toList());
                vo.setChapters(chapterVoList);
            }
            return vo;
        }).collect(Collectors.toList());

        try {
            response.setContentType("application/json;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(voList));
        } catch (IOException e) {
            throw new RuntimeException("导出JSON失败");
        }
    }

    /**
     * 导入法律法规 JSON
     */
    @Anonymous
    @PostMapping("/import/json")
    public AjaxResult importJson(@RequestBody List<RegulationImportVo> voList) {
        String operName = getUsername();
        Map<String, Object> result = sysRegulationService.importRegulation(voList, true, operName);
        return AjaxResult.success("导入成功，成功" + result.get("success") + "条，失败" + result.get("fail") + "条", result.get("errors"));
    }
}
