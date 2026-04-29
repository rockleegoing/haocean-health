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
import com.ruoyi.system.domain.NormativeTermBind;
import com.ruoyi.system.service.INormativeTermBindService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 规范用语法律条款关联Controller
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@RestController
@RequestMapping("/system/normativetermbind")
public class NormativeTermBindController extends BaseController
{
    @Autowired
    private INormativeTermBindService normativeTermBindService;

    /**
     * 查询规范用语法律条款关联列表
     */
    @PreAuthorize("@ss.hasPermi('system:normativetermbind:list')")
    @GetMapping("/list")
    public TableDataInfo list(NormativeTermBind normativeTermBind)
    {
        startPage();
        List<NormativeTermBind> list = normativeTermBindService.selectNormativeTermBindList(normativeTermBind);
        return getDataTable(list);
    }

    /**
     * 导出规范用语法律条款关联列表
     */
    @PreAuthorize("@ss.hasPermi('system:normativetermbind:export')")
    @Log(title = "规范用语法律条款关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NormativeTermBind normativeTermBind)
    {
        List<NormativeTermBind> list = normativeTermBindService.selectNormativeTermBindList(normativeTermBind);
        ExcelUtil<NormativeTermBind> util = new ExcelUtil<NormativeTermBind>(NormativeTermBind.class);
        util.exportExcel(response, list, "规范用语法律条款关联数据");
    }

    /**
     * 获取规范用语法律条款关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:normativetermbind:query')")
    @GetMapping(value = "/{legalTermId}")
    public AjaxResult getInfo(@PathVariable("legalTermId") Long legalTermId)
    {
        return success(normativeTermBindService.selectNormativeTermBindByLegalTermId(legalTermId));
    }

    /**
     * 新增规范用语法律条款关联
     */
    @PreAuthorize("@ss.hasPermi('system:normativetermbind:add')")
    @Log(title = "规范用语法律条款关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NormativeTermBind normativeTermBind)
    {
        return toAjax(normativeTermBindService.insertNormativeTermBind(normativeTermBind));
    }

    /**
     * 修改规范用语法律条款关联
     */
    @PreAuthorize("@ss.hasPermi('system:normativetermbind:edit')")
    @Log(title = "规范用语法律条款关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NormativeTermBind normativeTermBind)
    {
        return toAjax(normativeTermBindService.updateNormativeTermBind(normativeTermBind));
    }

    /**
     * 删除规范用语法律条款关联
     */
    @PreAuthorize("@ss.hasPermi('system:normativetermbind:remove')")
    @Log(title = "规范用语法律条款关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{legalTermIds}")
    public AjaxResult remove(@PathVariable Long[] legalTermIds)
    {
        return toAjax(normativeTermBindService.deleteNormativeTermBindByLegalTermIds(legalTermIds));
    }

    /**
     * 解绑（根据法律条款ID和规范用语ID）
     */
    @PreAuthorize("@ss.hasPermi('system:normativetermbind:remove')")
    @Log(title = "规范用语法律条款关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/unbind")
    public AjaxResult unbind(Long legalTermId, Long normativeLanguageId)
    {
        return toAjax(normativeTermBindService.deleteNormativeTermBindByBothIds(legalTermId, normativeLanguageId));
    }
}
