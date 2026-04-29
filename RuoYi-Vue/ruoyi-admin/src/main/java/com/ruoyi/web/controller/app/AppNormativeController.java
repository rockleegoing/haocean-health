package com.ruoyi.web.controller.app;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.NormativeCategory;
import com.ruoyi.system.domain.NormativeLanguage;
import com.ruoyi.system.domain.NormativeMatterBind;
import com.ruoyi.system.domain.NormativeTermBind;
import com.ruoyi.system.service.INormativeCategoryService;
import com.ruoyi.system.service.INormativeLanguageService;
import com.ruoyi.system.service.INormativeMatterBindService;
import com.ruoyi.system.service.INormativeTermBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * App规范用语Controller
 *
 * @author ruoyi
 * @date 2026-04-29
 */
@RestController
@RequestMapping("/app/normative")
public class AppNormativeController extends BaseController {

    @Autowired
    private INormativeCategoryService categoryService;

    @Autowired
    private INormativeLanguageService languageService;

    @Autowired
    private INormativeMatterBindService matterBindService;

    @Autowired
    private INormativeTermBindService termBindService;

    /**
     * 规范用语分类列表
     */
    @Anonymous
    @GetMapping("/category/list")
    public AjaxResult listCategory() {
        return AjaxResult.success(categoryService.selectNormativeCategoryList(new NormativeCategory()));
    }

    /**
     * 规范用语列表（支持 categoryId 参数过滤）
     */
    @Anonymous
    @GetMapping("/language/list")
    public AjaxResult listLanguage(@RequestParam(required = false) Long categoryId) {
        NormativeLanguage language = new NormativeLanguage();
        if (categoryId != null) {
            // categoryId 对应 primaryCategory（一级分类）
            language.setPrimaryCategory(categoryId);
        }
        return AjaxResult.success(languageService.selectNormativeLanguageList(language));
    }

    /**
     * 规范用语监管事项关联列表
     */
    @Anonymous
    @GetMapping("/matterbind/list")
    public AjaxResult listMatterBind() {
        return AjaxResult.success(matterBindService.selectNormativeMatterBindList(new NormativeMatterBind()));
    }

    /**
     * 规范用语法律条款关联列表（支持 legalTermId 参数过滤）
     */
    @Anonymous
    @GetMapping("/termbind/list")
    public AjaxResult listTermBind(@RequestParam(required = false) Long legalTermId) {
        NormativeTermBind bind = new NormativeTermBind();
        if (legalTermId != null) {
            bind.setLegalTermId(legalTermId);
        }
        return AjaxResult.success(termBindService.selectNormativeTermBindList(bind));
    }
}
