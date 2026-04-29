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
import com.ruoyi.system.domain.NormativeLanguage;
import com.ruoyi.system.service.INormativeLanguageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 规范用语Controller
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@RestController
@RequestMapping("/system/language")
public class NormativeLanguageController extends BaseController
{
    @Autowired
    private INormativeLanguageService normativeLanguageService;

    /**
     * 查询规范用语列表
     */
    @PreAuthorize("@ss.hasPermi('system:language:list')")
    @GetMapping("/list")
    public TableDataInfo list(NormativeLanguage normativeLanguage)
    {
        startPage();
        List<NormativeLanguage> list = normativeLanguageService.selectNormativeLanguageList(normativeLanguage);
        return getDataTable(list);
    }

    /**
     * 导出规范用语列表
     */
    @PreAuthorize("@ss.hasPermi('system:language:export')")
    @Log(title = "规范用语", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NormativeLanguage normativeLanguage)
    {
        List<NormativeLanguage> list = normativeLanguageService.selectNormativeLanguageList(normativeLanguage);
        ExcelUtil<NormativeLanguage> util = new ExcelUtil<NormativeLanguage>(NormativeLanguage.class);
        util.exportExcel(response, list, "规范用语数据");
    }

    /**
     * 获取规范用语详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:language:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(normativeLanguageService.selectNormativeLanguageById(id));
    }

    /**
     * 新增规范用语
     */
    @PreAuthorize("@ss.hasPermi('system:language:add')")
    @Log(title = "规范用语", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NormativeLanguage normativeLanguage)
    {
        return toAjax(normativeLanguageService.insertNormativeLanguage(normativeLanguage));
    }

    /**
     * 修改规范用语
     */
    @PreAuthorize("@ss.hasPermi('system:language:edit')")
    @Log(title = "规范用语", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NormativeLanguage normativeLanguage)
    {
        return toAjax(normativeLanguageService.updateNormativeLanguage(normativeLanguage));
    }

    /**
     * 删除规范用语
     */
    @PreAuthorize("@ss.hasPermi('system:language:remove')")
    @Log(title = "规范用语", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(normativeLanguageService.deleteNormativeLanguageByIds(ids));
    }
}
