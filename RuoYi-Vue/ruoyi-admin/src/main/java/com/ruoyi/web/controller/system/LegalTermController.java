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
import com.ruoyi.system.domain.LegalTerm;
import com.ruoyi.system.service.ILegalTermService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 法律条款Controller
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@RestController
@RequestMapping("/system/legalterm")
public class LegalTermController extends BaseController
{
    @Autowired
    private ILegalTermService legalTermService;

    /**
     * 查询法律条款列表
     */
    @PreAuthorize("@ss.hasPermi('system:legalterm:list')")
    @GetMapping("/list")
    public TableDataInfo list(LegalTerm legalTerm)
    {
        startPage();
        List<LegalTerm> list = legalTermService.selectLegalTermList(legalTerm);
        return getDataTable(list);
    }

    /**
     * 导出法律条款列表
     */
    @PreAuthorize("@ss.hasPermi('system:legalterm:export')")
    @Log(title = "法律条款", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LegalTerm legalTerm)
    {
        List<LegalTerm> list = legalTermService.selectLegalTermList(legalTerm);
        ExcelUtil<LegalTerm> util = new ExcelUtil<LegalTerm>(LegalTerm.class);
        util.exportExcel(response, list, "法律条款数据");
    }

    /**
     * 获取法律条款详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:legalterm:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(legalTermService.selectLegalTermById(id));
    }

    /**
     * 新增法律条款
     */
    @PreAuthorize("@ss.hasPermi('system:legalterm:add')")
    @Log(title = "法律条款", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LegalTerm legalTerm)
    {
        return toAjax(legalTermService.insertLegalTerm(legalTerm));
    }

    /**
     * 修改法律条款
     */
    @PreAuthorize("@ss.hasPermi('system:legalterm:edit')")
    @Log(title = "法律条款", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LegalTerm legalTerm)
    {
        return toAjax(legalTermService.updateLegalTerm(legalTerm));
    }

    /**
     * 删除法律条款
     */
    @PreAuthorize("@ss.hasPermi('system:legalterm:remove')")
    @Log(title = "法律条款", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(legalTermService.deleteLegalTermByIds(ids));
    }
}
