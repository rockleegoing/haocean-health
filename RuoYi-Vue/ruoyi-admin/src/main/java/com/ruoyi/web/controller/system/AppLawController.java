package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.Law;
import com.ruoyi.system.domain.LegalTerm;
import com.ruoyi.system.service.ILawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Android 端法律接口（匿名可访问）
 */
@RestController
@RequestMapping("/app/law")
public class AppLawController extends BaseController {

    @Autowired
    private ILawService lawService;

    /**
     * 获取法律法规列表
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success(lawService.selectLawList(new Law()));
    }

    /**
     * 获取法律法规详情
     */
    @Anonymous
    @GetMapping("/{lawId}")
    public AjaxResult getInfo(@PathVariable Long lawId) {
        return AjaxResult.success(lawService.selectLawById(lawId));
    }

    /**
     * 获取法律条款列表
     */
    @Anonymous
    @GetMapping("/{lawId}/term/list")
    public AjaxResult listTerm(@PathVariable Long lawId) {
        LegalTerm term = new LegalTerm();
        term.setLawId(lawId);
        return AjaxResult.success(lawService.selectLegalTermList(term));
    }

    /**
     * 获取条款详情
     */
    @Anonymous
    @GetMapping("/term/{termId}")
    public AjaxResult getTermInfo(@PathVariable Long termId) {
        return AjaxResult.success(lawService.selectLegalTermById(termId));
    }
}