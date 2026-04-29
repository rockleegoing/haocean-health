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
import com.ruoyi.system.domain.Law;
import com.ruoyi.system.service.ILawService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 法律目录Controller
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@RestController
@RequestMapping("/system/law")
public class LawController extends BaseController
{
    @Autowired
    private ILawService lawService;

    /**
     * 查询法律目录列表
     */
    @PreAuthorize("@ss.hasPermi('system:law:list')")
    @GetMapping("/list")
    public TableDataInfo list(Law law)
    {
        startPage();
        List<Law> list = lawService.selectLawList(law);
        return getDataTable(list);
    }

    /**
     * 导出法律目录列表
     */
    @PreAuthorize("@ss.hasPermi('system:law:export')")
    @Log(title = "法律目录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Law law)
    {
        List<Law> list = lawService.selectLawList(law);
        ExcelUtil<Law> util = new ExcelUtil<Law>(Law.class);
        util.exportExcel(response, list, "法律目录数据");
    }

    /**
     * 获取法律目录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:law:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(lawService.selectLawById(id));
    }

    /**
     * 新增法律目录
     */
    @PreAuthorize("@ss.hasPermi('system:law:add')")
    @Log(title = "法律目录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Law law)
    {
        return toAjax(lawService.insertLaw(law));
    }

    /**
     * 修改法律目录
     */
    @PreAuthorize("@ss.hasPermi('system:law:edit')")
    @Log(title = "法律目录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Law law)
    {
        return toAjax(lawService.updateLaw(law));
    }

    /**
     * 删除法律目录
     */
    @PreAuthorize("@ss.hasPermi('system:law:remove')")
    @Log(title = "法律目录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(lawService.deleteLawByIds(ids));
    }
}
