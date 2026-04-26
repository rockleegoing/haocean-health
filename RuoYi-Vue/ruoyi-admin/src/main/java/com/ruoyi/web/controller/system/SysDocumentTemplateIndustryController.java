package com.ruoyi.web.controller.system;

import java.util.List;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysDocumentTemplate;
import com.ruoyi.system.service.ISysDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文书模板与行业分类关联Controller
 *
 * @author ruoyi
 * @date 2026-04-26
 */
@RestController
@RequestMapping("/api/admin/document/template/industry")
public class SysDocumentTemplateIndustryController extends BaseController {

    @Autowired
    private ISysDocumentService sysDocumentService;

    /**
     * 同步模板行业关联数据
     * 返回格式：[{templateId: 1, industryCategoryId: 1}, ...]
     */
    @Anonymous
    @GetMapping("/sync")
    public AjaxResult syncTemplateIndustry() {
        // 获取所有启用的模板
        List<SysDocumentTemplate> templates = sysDocumentService.selectAllSysDocumentTemplates();

        // 返回模板的行业分类信息（通过模板的 industryCategoryId 字段）
        // 如果模板设置了行业分类，则返回关联
        List<Object> relations = templates.stream()
            .filter(t -> t.getIndustryCategoryId() != null && t.getIndustryCategoryId() > 0)
            .map(t -> {
                return new java.util.HashMap<String, Object>() {{
                    put("templateId", t.getId());
                    put("industryCategoryId", t.getIndustryCategoryId());
                }};
            })
            .collect(java.util.stream.Collectors.toList());

        return AjaxResult.success(relations);
    }
}
