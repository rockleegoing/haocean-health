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
import com.ruoyi.system.domain.RegulatoryMatter;
import com.ruoyi.system.service.IRegulatoryMatterService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 监管事项Controller
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@RestController
@RequestMapping("/system/matter")
public class RegulatoryMatterController extends BaseController
{
    @Autowired
    private IRegulatoryMatterService regulatoryMatterService;

    /**
     * 查询监管事项列表
     */
    @PreAuthorize("@ss.hasPermi('system:matter:list')")
    @GetMapping("/list")
    public TableDataInfo list(RegulatoryMatter regulatoryMatter)
    {
        startPage();
        List<RegulatoryMatter> list = regulatoryMatterService.selectRegulatoryMatterList(regulatoryMatter);
        return getDataTable(list);
    }

    /**
     * 导出监管事项列表
     */
    @PreAuthorize("@ss.hasPermi('system:matter:export')")
    @Log(title = "监管事项", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RegulatoryMatter regulatoryMatter)
    {
        List<RegulatoryMatter> list = regulatoryMatterService.selectRegulatoryMatterList(regulatoryMatter);
        ExcelUtil<RegulatoryMatter> util = new ExcelUtil<RegulatoryMatter>(RegulatoryMatter.class);
        util.exportExcel(response, list, "监管事项数据");
    }

    /**
     * 获取监管事项详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:matter:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(regulatoryMatterService.selectRegulatoryMatterById(id));
    }

    /**
     * 新增监管事项
     */
    @PreAuthorize("@ss.hasPermi('system:matter:add')")
    @Log(title = "监管事项", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RegulatoryMatter regulatoryMatter)
    {
        return toAjax(regulatoryMatterService.insertRegulatoryMatter(regulatoryMatter));
    }

    /**
     * 修改监管事项
     */
    @PreAuthorize("@ss.hasPermi('system:matter:edit')")
    @Log(title = "监管事项", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RegulatoryMatter regulatoryMatter)
    {
        return toAjax(regulatoryMatterService.updateRegulatoryMatter(regulatoryMatter));
    }

    /**
     * 删除监管事项
     */
    @PreAuthorize("@ss.hasPermi('system:matter:remove')")
    @Log(title = "监管事项", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(regulatoryMatterService.deleteRegulatoryMatterByIds(ids));
    }
}
