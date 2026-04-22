package com.ruoyi.web.controller.device;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysActivationCode;
import com.ruoyi.system.service.ISysActivationCodeService;

/**
 * 激活码 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/device/activationCode")
public class SysActivationCodeController extends BaseController
{
    @Autowired
    private ISysActivationCodeService activationCodeService;

    /**
     * 获取激活码列表
     */
    @PreAuthorize("@ss.hasPermi('device:activationCode:list')")
    @PostMapping("/list")
    public TableDataInfo list(SysActivationCode activationCode)
    {
        startPage();
        List<SysActivationCode> list = activationCodeService.selectSysActivationCodeList(activationCode);
        return getDataTable(list);
    }

    @Log(title = "激活码管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('device:activationCode:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysActivationCode activationCode)
    {
        List<SysActivationCode> list = activationCodeService.selectSysActivationCodeList(activationCode);
        ExcelUtil<SysActivationCode> util = new ExcelUtil<SysActivationCode>(SysActivationCode.class);
        util.exportExcel(response, list, "激活码数据");
    }

    /**
     * 根据激活码 ID 获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:activationCode:query')")
    @GetMapping(value = "/{codeId}")
    public AjaxResult getInfo(@PathVariable("codeId") Long codeId)
    {
        return success(activationCodeService.selectSysActivationCodeById(codeId));
    }

    /**
     * 生成激活码
     */
    @PreAuthorize("@ss.hasPermi('device:activationCode:add')")
    @Log(title = "激活码管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysActivationCode activationCode)
    {
        activationCode.setCreateBy(getUsername());
        return toAjax(activationCodeService.insertSysActivationCode(activationCode));
    }

    /**
     * 批量生成激活码
     */
    @PreAuthorize("@ss.hasPermi('device:activationCode:add')")
    @Log(title = "激活码管理", businessType = BusinessType.INSERT)
    @PostMapping("/batchGenerate")
    public AjaxResult batchGenerate(@RequestBody Map<String, Object> params)
    {
        int count = params.get("count") != null ? ((Number) params.get("count")).intValue() : 1;
        int expireDays = params.get("expireDays") != null ? ((Number) params.get("expireDays")).intValue() : 365;
        String remark = params.get("remark") != null ? params.get("remark").toString() : "";

        List<SysActivationCode> codes = activationCodeService.batchGenerateCodes(count, expireDays, remark, getUsername());
        return AjaxResult.success("成功生成 " + codes.size() + " 个激活码", codes);
    }

    /**
     * 修改激活码
     */
    @PreAuthorize("@ss.hasPermi('device:activationCode:edit')")
    @Log(title = "激活码管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysActivationCode activationCode)
    {
        activationCode.setUpdateBy(getUsername());
        return toAjax(activationCodeService.updateSysActivationCode(activationCode));
    }

    /**
     * 删除激活码
     */
    @PreAuthorize("@ss.hasPermi('device:activationCode:remove')")
    @Log(title = "激活码管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{codeIds}")
    public AjaxResult remove(@PathVariable Long[] codeIds)
    {
        return toAjax(activationCodeService.deleteSysActivationCodeByIds(codeIds));
    }

    /**
     * 验证激活码（App 调用）
     */
    @Anonymous
    @Log(title = "激活码验证", businessType = BusinessType.OTHER)
    @PostMapping("/validate")
    public AjaxResult validate(@RequestBody Map<String, String> params)
    {
        String codeValue = params.get("codeValue");
        String deviceUuid = params.get("deviceUuid");

        if (codeValue == null || codeValue.isEmpty())
        {
            return error("激活码不能为空");
        }
        if (deviceUuid == null || deviceUuid.isEmpty())
        {
            return error("设备标识不能为空");
        }

        Map<String, Object> result = activationCodeService.validateCode(codeValue, deviceUuid);
        return success(result);
    }
}
