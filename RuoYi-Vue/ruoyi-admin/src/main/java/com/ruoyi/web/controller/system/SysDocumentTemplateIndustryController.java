package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.stream.Collectors;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysDocumentTemplateIndustry;
import com.ruoyi.system.mapper.SysDocumentTemplateIndustryMapper;
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
    private SysDocumentTemplateIndustryMapper templateIndustryMapper;

    /**
     * 同步模板行业关联数据
     * 返回格式：[{templateId: 1, industryCategoryId: 1}, ...]
     */
    @Anonymous
    @GetMapping("/sync")
    public AjaxResult syncTemplateIndustry() {
        // 从中间表获取所有关联数据
        List<SysDocumentTemplateIndustry> relations = templateIndustryMapper.selectAll();

        // 转换为API响应格式
        List<Object> result = relations.stream()
            .map(r -> {
                return new java.util.HashMap<String, Object>() {{
                    put("templateId", r.getTemplateId());
                    put("industryCategoryId", r.getIndustryCategoryId());
                }};
            })
            .collect(Collectors.toList());

        return AjaxResult.success(result);
    }
}
