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
import com.ruoyi.system.domain.NormativeMatterBind;
import com.ruoyi.system.service.INormativeMatterBindService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 规范用语与监管事项绑定关系Controller
 *
 * @author ruoyi
 * @date 2026-04-29
 */
@RestController
@RequestMapping("/system/normativematterbind")
public class NormativeMatterBindController extends BaseController
{
    @Autowired
    private INormativeMatterBindService normativeMatterBindService;

    /**
     * 查询规范用语与监管事项绑定关系列表
     */
    @PreAuthorize("@ss.hasPermi('system:normativematterbind:list')")
    @GetMapping("/list")
    public TableDataInfo list(NormativeMatterBind normativeMatterBind)
    {
        startPage();
        List<NormativeMatterBind> list = normativeMatterBindService.selectNormativeMatterBindList(normativeMatterBind);
        return getDataTable(list);
    }

    /**
     * 导出规范用语与监管事项绑定关系列表
     */
    @PreAuthorize("@ss.hasPermi('system:normativematterbind:export')")
    @Log(title = "规范用语与监管事项绑定关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NormativeMatterBind normativeMatterBind)
    {
        List<NormativeMatterBind> list = normativeMatterBindService.selectNormativeMatterBindList(normativeMatterBind);
        ExcelUtil<NormativeMatterBind> util = new ExcelUtil<NormativeMatterBind>(NormativeMatterBind.class);
        util.exportExcel(response, list, "规范用语与监管事项绑定关系数据");
    }

    /**
     * 获取规范用语与监管事项绑定关系详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:normativematterbind:query')")
    @GetMapping(value = "/{matterId}/{normativeId}")
    public AjaxResult getInfo(@PathVariable("matterId") Long matterId, @PathVariable("normativeId") Long normativeId)
    {
        NormativeMatterBind bind = new NormativeMatterBind();
        bind.setMatterId(matterId);
        bind.setNormativeId(normativeId);
        return success(normativeMatterBindService.selectNormativeMatterBindList(bind));
    }

    /**
     * 新增规范用语与监管事项绑定关系
     */
    @PreAuthorize("@ss.hasPermi('system:normativematterbind:add')")
    @Log(title = "规范用语与监管事项绑定关系", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NormativeMatterBind normativeMatterBind)
    {
        return toAjax(normativeMatterBindService.insertNormativeMatterBind(normativeMatterBind));
    }

    /**
     * 删除规范用语与监管事项绑定关系
     */
    @PreAuthorize("@ss.hasPermi('system:normativematterbind:remove')")
    @Log(title = "规范用语与监管事项绑定关系", businessType = BusinessType.DELETE)
    @DeleteMapping
    public AjaxResult remove(NormativeMatterBind bind)
    {
        return toAjax(normativeMatterBindService.deleteNormativeMatterBindByMatterIdAndNormativeId(bind.getMatterId(), bind.getNormativeId()));
    }

    /**
     * 获取所有已绑定的事项列表（用于按事项视图，不受分页限制）
     */
    @PreAuthorize("@ss.hasPermi('system:normativematterbind:list')")
    @GetMapping("/boundMatterList")
    public AjaxResult listBoundMatter()
    {
        List<NormativeMatterBind> list = normativeMatterBindService.selectBoundMatterList();
        return success(list);
    }

    /**
     * 获取所有已绑定的规范用语列表（用于按规范用语视图，不受分页限制）
     */
    @PreAuthorize("@ss.hasPermi('system:normativematterbind:list')")
    @GetMapping("/boundNormativeList")
    public AjaxResult listBoundNormative()
    {
        List<NormativeMatterBind> list = normativeMatterBindService.selectBoundNormativeList();
        return success(list);
    }
}
