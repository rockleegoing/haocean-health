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
import com.ruoyi.system.domain.RegulatoryCategoryBind;
import com.ruoyi.system.service.IRegulatoryCategoryBindService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 监管事项执法分类绑定关系Controller
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@RestController
@RequestMapping("/system/regulatorycategorybind")
public class RegulatoryCategoryBindController extends BaseController
{
    @Autowired
    private IRegulatoryCategoryBindService regulatoryCategoryBindService;

    /**
     * 查询监管事项执法分类绑定关系列表
     */
    @PreAuthorize("@ss.hasPermi('system:regulatorycategorybind:list')")
    @GetMapping("/list")
    public TableDataInfo list(RegulatoryCategoryBind regulatoryCategoryBind)
    {
        startPage();
        List<RegulatoryCategoryBind> list = regulatoryCategoryBindService.selectRegulatoryCategoryBindList(regulatoryCategoryBind);
        return getDataTable(list);
    }

    /**
     * 导出监管事项执法分类绑定关系列表
     */
    @PreAuthorize("@ss.hasPermi('system:regulatorycategorybind:export')")
    @Log(title = "监管事项执法分类绑定关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RegulatoryCategoryBind regulatoryCategoryBind)
    {
        List<RegulatoryCategoryBind> list = regulatoryCategoryBindService.selectRegulatoryCategoryBindList(regulatoryCategoryBind);
        ExcelUtil<RegulatoryCategoryBind> util = new ExcelUtil<RegulatoryCategoryBind>(RegulatoryCategoryBind.class);
        util.exportExcel(response, list, "监管事项执法分类绑定关系数据");
    }

    /**
     * 获取监管事项执法分类绑定关系详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:regulatorycategorybind:query')")
    @GetMapping(value = "/{matterId}")
    public AjaxResult getInfo(@PathVariable("matterId") Long matterId)
    {
        return success(regulatoryCategoryBindService.selectRegulatoryCategoryBindByMatterId(matterId));
    }

    /**
     * 新增监管事项执法分类绑定关系
     */
    @PreAuthorize("@ss.hasPermi('system:regulatorycategorybind:add')")
    @Log(title = "监管事项执法分类绑定关系", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RegulatoryCategoryBind regulatoryCategoryBind)
    {
        return toAjax(regulatoryCategoryBindService.insertRegulatoryCategoryBind(regulatoryCategoryBind));
    }

    /**
     * 修改监管事项执法分类绑定关系
     */
    @PreAuthorize("@ss.hasPermi('system:regulatorycategorybind:edit')")
    @Log(title = "监管事项执法分类绑定关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RegulatoryCategoryBind regulatoryCategoryBind)
    {
        return toAjax(regulatoryCategoryBindService.updateRegulatoryCategoryBind(regulatoryCategoryBind));
    }

    /**
     * 删除监管事项执法分类绑定关系
     */
    @PreAuthorize("@ss.hasPermi('system:regulatorycategorybind:remove')")
    @Log(title = "监管事项执法分类绑定关系", businessType = BusinessType.DELETE)
	@DeleteMapping("/{matterIds}")
    public AjaxResult remove(@PathVariable Long[] matterIds)
    {
        return toAjax(regulatoryCategoryBindService.deleteRegulatoryCategoryBindByMatterIds(matterIds));
    }
}
