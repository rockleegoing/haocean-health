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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.service.ISysDeviceService;

/**
 * 设备 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/device/device")
public class SysDeviceController extends BaseController
{
    @Autowired
    private ISysDeviceService deviceService;

    /**
     * 获取设备列表
     */
    @PreAuthorize("@ss.hasPermi('device:device:list')")
    @PostMapping("/list")
    public TableDataInfo list(SysDevice device)
    {
        startPage();
        List<SysDevice> list = deviceService.selectSysDeviceList(device);
        return getDataTable(list);
    }

    @Log(title = "设备管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('device:device:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDevice device)
    {
        List<SysDevice> list = deviceService.selectSysDeviceList(device);
        ExcelUtil<SysDevice> util = new ExcelUtil<SysDevice>(SysDevice.class);
        util.exportExcel(response, list, "设备数据");
    }

    /**
     * 根据设备 ID 获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:device:query')")
    @GetMapping(value = "/{deviceId}")
    public AjaxResult getInfo(@PathVariable("deviceId") Long deviceId)
    {
        return success(deviceService.selectSysDeviceById(deviceId));
    }

    /**
     * 新增设备
     */
    @PreAuthorize("@ss.hasPermi('device:device:add')")
    @Log(title = "设备管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDevice device)
    {
        device.setCreateBy(getUsername());
        return toAjax(deviceService.insertSysDevice(device));
    }

    /**
     * 修改设备
     */
    @PreAuthorize("@ss.hasPermi('device:device:edit')")
    @Log(title = "设备管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDevice device)
    {
        device.setUpdateBy(getUsername());
        return toAjax(deviceService.updateSysDevice(device));
    }

    /**
     * 删除设备
     */
    @PreAuthorize("@ss.hasPermi('device:device:remove')")
    @Log(title = "设备管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deviceIds}")
    public AjaxResult remove(@PathVariable Long[] deviceIds)
    {
        return toAjax(deviceService.deleteSysDeviceByIds(deviceIds));
    }

    /**
     * 设备解绑
     */
    @PreAuthorize("@ss.hasPermi('device:device:unbind')")
    @Log(title = "设备管理", businessType = BusinessType.OTHER)
    @PutMapping("/unbind")
    public AjaxResult unbind(@RequestBody Map<String, Long> params)
    {
        Long deviceId = params.get("deviceId");
        if (deviceId == null)
        {
            return error("设备 ID 不能为空");
        }
        return toAjax(deviceService.unbindDevice(deviceId));
    }

    /**
     * 远程清除设备数据
     */
    @PreAuthorize("@ss.hasPermi('device:device:clearData')")
    @Log(title = "设备管理", businessType = BusinessType.CLEAN)
    @PostMapping("/clearData")
    public AjaxResult clearData(@RequestBody Map<String, String> params)
    {
        String deviceUuid = params.get("deviceUuid");
        if (deviceUuid == null || deviceUuid.isEmpty())
        {
            return error("设备标识不能为空");
        }

        SysDevice device = deviceService.selectSysDeviceByUuid(deviceUuid);
        if (device == null)
        {
            return error("设备不存在");
        }

        // 清除设备数据逻辑（可根据需求扩展）
        // 这里仅做记录，实际清除操作由客户端执行
        return success("已发送清除设备数据指令");
    }
}
