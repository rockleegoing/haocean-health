package com.ruoyi.web.controller.system;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.NormativeCategory;
import com.ruoyi.system.service.INormativeCategoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 规范用语类别Controller
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@RestController
@RequestMapping("/system/normativecategory")
public class NormativeCategoryController extends BaseController
{
    @Autowired
    private INormativeCategoryService normativeCategoryService;

    /**
     * 查询规范用语类别列表
     */
    @PreAuthorize("@ss.hasPermi('system:normativecategory:list')")
    @GetMapping("/list")
    public AjaxResult list(NormativeCategory normativeCategory)
    {
        List<NormativeCategory> list = normativeCategoryService.selectNormativeCategoryList(normativeCategory);
        return success(list);
    }

    /**
     * 导出规范用语类别列表
     */
    @PreAuthorize("@ss.hasPermi('system:normativecategory:export')")
    @Log(title = "规范用语类别", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NormativeCategory normativeCategory)
    {
        List<NormativeCategory> list = normativeCategoryService.selectNormativeCategoryList(normativeCategory);
        ExcelUtil<NormativeCategory> util = new ExcelUtil<NormativeCategory>(NormativeCategory.class);
        util.exportExcel(response, list, "规范用语类别数据");
    }

    /**
     * 获取规范用语类别详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:normativecategory:query')")
    @GetMapping(value = "/{code}")
    public AjaxResult getInfo(@PathVariable("code") Long code)
    {
        return success(normativeCategoryService.selectNormativeCategoryByCode(code));
    }

    /**
     * 新增规范用语类别
     */
    @PreAuthorize("@ss.hasPermi('system:normativecategory:add')")
    @Log(title = "规范用语类别", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NormativeCategory normativeCategory)
    {
        return toAjax(normativeCategoryService.insertNormativeCategory(normativeCategory));
    }

    /**
     * 修改规范用语类别
     */
    @PreAuthorize("@ss.hasPermi('system:normativecategory:edit')")
    @Log(title = "规范用语类别", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NormativeCategory normativeCategory)
    {
        return toAjax(normativeCategoryService.updateNormativeCategory(normativeCategory));
    }

    /**
     * 删除规范用语类别
     */
    @PreAuthorize("@ss.hasPermi('system:normativecategory:remove')")
    @Log(title = "规范用语类别", businessType = BusinessType.DELETE)
	@DeleteMapping("/{codes}")
    public AjaxResult remove(@PathVariable Long[] codes)
    {
        return toAjax(normativeCategoryService.deleteNormativeCategoryByCodes(codes));
    }
}
