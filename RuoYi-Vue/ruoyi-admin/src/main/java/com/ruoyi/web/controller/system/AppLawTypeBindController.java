package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.LawTypeBind;
import com.ruoyi.system.service.ILawTypeBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Android 端法律类型绑定接口（匿名可访问）
 */
@RestController
@RequestMapping("/app/lawtype/bind")
public class AppLawTypeBindController extends BaseController {

    @Autowired
    private ILawTypeBindService lawTypeBindService;

    /**
     * 获取所有绑定关系
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list() {
        List<LawTypeBind> list = lawTypeBindService.selectAll();
        return AjaxResult.success(list);
    }

    /**
     * 查询某法律关联的类型列表
     */
    @Anonymous
    @GetMapping("/{lawId}")
    public AjaxResult getByLawId(@PathVariable Long lawId) {
        List<LawTypeBind> list = lawTypeBindService.selectByLawId(lawId);
        return AjaxResult.success(list);
    }

    /**
     * 绑定（可批量）
     */
    @Anonymous
    @PostMapping("/{lawId}")
    public AjaxResult bind(@PathVariable Long lawId, @RequestBody List<Long> typeIds) {
        if (typeIds == null || typeIds.isEmpty()) {
            return AjaxResult.error("typeIds不能为空");
        }
        // 先删除旧绑定
        lawTypeBindService.deleteByLawId(lawId);
        // 批量插入新绑定
        List<LawTypeBind> binds = typeIds.stream().map(typeId -> {
            LawTypeBind bind = new LawTypeBind();
            bind.setLawId(lawId);
            bind.setTypeId(typeId);
            return bind;
        }).toList();
        lawTypeBindService.insertBatch(binds);
        return AjaxResult.success();
    }

    /**
     * 删除某法律的全部绑定
     */
    @Anonymous
    @DeleteMapping("/{lawId}")
    public AjaxResult deleteByLawId(@PathVariable Long lawId) {
        lawTypeBindService.deleteByLawId(lawId);
        return AjaxResult.success();
    }

    /**
     * 删除单个绑定
     */
    @Anonymous
    @DeleteMapping("/{lawId}/{typeId}")
    public AjaxResult delete(@PathVariable Long lawId, @PathVariable Long typeId) {
        lawTypeBindService.deleteByLawIdAndTypeId(lawId, typeId);
        return AjaxResult.success();
    }
}
