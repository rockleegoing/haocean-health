package com.ruoyi.web.controller.system;

import java.util.List;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysDocumentTemplateIndustry;
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
     */
    @Anonymous
    @GetMapping("/sync")
    public AjaxResult syncTemplateIndustry() {
        // 查询所有模板的行业关联关系
        // 通过查询所有模板，然后获取每个模板关联的行业分类
        // 这里直接返回空列表，因为关联数据通过模板本身携带
        // 实际业务中可以通过中间表查询
        return AjaxResult.success();
    }
}
