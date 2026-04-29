package com.ruoyi.web.controller.system;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import com.ruoyi.system.domain.LawType;
import com.ruoyi.system.service.ILawTypeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 法律法规类型Controller
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@RestController
@RequestMapping("/system/lawtype")
public class LawTypeController extends BaseController
{
    @Autowired
    private ILawTypeService lawTypeService;

    /**
     * 查询法律法规类型列表
     */
    @PreAuthorize("@ss.hasPermi('system:lawtype:list')")
    @GetMapping("/list")
    public TableDataInfo list(LawType lawType)
    {
        startPage();
        List<LawType> list = lawTypeService.selectLawTypeList(lawType);
        return getDataTable(list);
    }

    /**
     * 获取法律类型树结构
     */
    @PreAuthorize("@ss.hasPermi('system:lawtype:list')")
    @GetMapping("/treeList")
    public AjaxResult treeList(LawType lawType) {
        List<LawType> list = lawTypeService.selectLawTypeList(lawType);
        return success(handleTree(list));
    }

    /**
     * 处理树形结构
     */
    private List<LawType> handleTree(List<LawType> list) {
        List<LawType> returnList = new ArrayList<>();
        List<Long> tempList = list.stream().map(LawType::getId).collect(Collectors.toList());
        for (LawType lawType : list) {
            if (!tempList.contains(lawType.getParentId())) {
                recursionFn(list, lawType);
                returnList.add(lawType);
            }
        }
        if (returnList.isEmpty()) {
            returnList = list;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<LawType> list, LawType t) {
        List<LawType> childList = getChildList(list, t);
        t.setChildren(childList);
        for (LawType tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 获取子列表
     */
    private List<LawType> getChildList(List<LawType> list, LawType t) {
        List<LawType> tlist = new ArrayList<>();
        for (LawType n : list) {
            if (n.getParentId() != null && n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<LawType> list, LawType t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 导出法律法规类型列表
     */
    @PreAuthorize("@ss.hasPermi('system:lawtype:export')")
    @Log(title = "法律法规类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LawType lawType)
    {
        List<LawType> list = lawTypeService.selectLawTypeList(lawType);
        ExcelUtil<LawType> util = new ExcelUtil<LawType>(LawType.class);
        util.exportExcel(response, list, "法律法规类型数据");
    }

    /**
     * 获取法律法规类型详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:lawtype:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(lawTypeService.selectLawTypeById(id));
    }

    /**
     * 新增法律法规类型
     */
    @PreAuthorize("@ss.hasPermi('system:lawtype:add')")
    @Log(title = "法律法规类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LawType lawType)
    {
        return toAjax(lawTypeService.insertLawType(lawType));
    }

    /**
     * 修改法律法规类型
     */
    @PreAuthorize("@ss.hasPermi('system:lawtype:edit')")
    @Log(title = "法律法规类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LawType lawType)
    {
        return toAjax(lawTypeService.updateLawType(lawType));
    }

    /**
     * 删除法律法规类型
     */
    @PreAuthorize("@ss.hasPermi('system:lawtype:remove')")
    @Log(title = "法律法规类型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(lawTypeService.deleteLawTypeByIds(ids));
    }
}
