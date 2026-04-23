package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysActivationCode;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.mapper.SysActivationCodeMapper;
import com.ruoyi.system.mapper.SysDeviceMapper;
import com.ruoyi.system.service.ISysActivationCodeService;
import com.ruoyi.system.service.ISysDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import com.ruoyi.system.domain.vo.ActivationApiResponse;

/**
 * 激活码 Service 实现类
 */
@Service
public class SysActivationCodeServiceImpl implements ISysActivationCodeService {

    @Autowired
    private SysActivationCodeMapper activationCodeMapper;

    @Autowired
    private ISysDeviceService deviceService;

    @Autowired
    private SysDeviceMapper deviceMapper;

    /**
     * 激活码字符集（排除 I/O/0/1 等易混淆字符）
     */
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    /**
     * 激活码长度
     */
    private static final int CODE_LENGTH = 8;

    @Override
    public SysActivationCode selectSysActivationCodeById(Long codeId) {
        return activationCodeMapper.selectSysActivationCodeById(codeId);
    }

    @Override
    public SysActivationCode selectSysActivationCodeByValue(String codeValue) {
        return activationCodeMapper.selectSysActivationCodeByValue(codeValue);
    }

    @Override
    public List<SysActivationCode> selectSysActivationCodeList(SysActivationCode sysActivationCode) {
        return activationCodeMapper.selectSysActivationCodeList(sysActivationCode);
    }

    @Override
    public int insertSysActivationCode(SysActivationCode sysActivationCode) {
        sysActivationCode.setCreateTime(DateUtils.getNowDate());
        return activationCodeMapper.insertSysActivationCode(sysActivationCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SysActivationCode> batchGenerateCodes(int count, int expireDays, String remark, String createBy) {
        if (count < 1 || count > 50) {
            throw new IllegalArgumentException("批量生成数量必须在 1-50 之间");
        }

        List<SysActivationCode> codeList = new ArrayList<>();
        Date now = DateUtils.getNowDate();

        for (int i = 0; i < count; i++) {
            SysActivationCode code = new SysActivationCode();
            code.setCodeValue(generateCode());
            code.setStatus("0"); // 未使用
            code.setValidDays(expireDays); // 存储有效期天数，不计算绝对过期时间
            code.setRemark(remark);
            code.setCreateBy(createBy);
            code.setCreateTime(now);
            codeList.add(code);
        }

        batchInsertSysActivationCode(codeList);
        return codeList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSysActivationCode(List<SysActivationCode> sysActivationCodeList) {
        return activationCodeMapper.batchInsertSysActivationCode(sysActivationCodeList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysActivationCode(SysActivationCode sysActivationCode) {
        sysActivationCode.setUpdateTime(DateUtils.getNowDate());
        return activationCodeMapper.updateSysActivationCode(sysActivationCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysActivationCodeByIds(Long[] codeIds) {
        return activationCodeMapper.deleteSysActivationCodeByIds(codeIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysActivationCodeById(Long codeId) {
        return activationCodeMapper.deleteSysActivationCodeById(codeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivationApiResponse validateCode(String codeValue, String deviceUuid,
        String deviceName, String deviceModel, String deviceOs, String appVersion) {
        // 查询激活码
        SysActivationCode code = activationCodeMapper.selectSysActivationCodeByValue(codeValue);
        if (code == null) {
            return new ActivationApiResponse(false, "激活码不存在");
        }

        // 检查状态
        if ("1".equals(code.getStatus())) {
            return new ActivationApiResponse(false, "激活码已被使用");
        }

        if ("2".equals(code.getStatus())) {
            return new ActivationApiResponse(false, "激活码已过期");
        }

        if ("3".equals(code.getStatus())) {
            return new ActivationApiResponse(false, "激活码已被禁用");
        }

        // 检查是否已达到最大设备数
        if (code.getMaxDeviceCount() != null && code.getActivatedCount() >= code.getMaxDeviceCount()) {
            return new ActivationApiResponse(false, "该激活码已达到最大设备数限制");
        }

        // 检查有效期
        if (code.getExpireTime() != null && code.getExpireTime().before(new Date())) {
            code.setStatus("2"); // 标记为过期
            activationCodeMapper.updateSysActivationCode(code);
            return new ActivationApiResponse(false, "激活码已过期");
        }

        // 更新激活码状态为已使用
        code.setStatus("1");
        code.setBindDeviceId(deviceUuid);

        // 增加已激活设备数
        if (code.getActivatedCount() == null) {
            code.setActivatedCount(0);
        }
        code.setActivatedCount(code.getActivatedCount() + 1);

        // 记录激活时间和激活设备
        Date activateTime = DateUtils.getNowDate();
        code.setActivateTime(activateTime);
        code.setActivateDevice(deviceModel);

        // 计算实际的过期时间 = 激活时间 + 有效期天数
        if (code.getValidDays() != null && code.getValidDays() > 0) {
            code.setExpireTime(DateUtils.addDays(activateTime, code.getValidDays()));
        }

        code.setUpdateTime(DateUtils.getNowDate());
        activationCodeMapper.updateSysActivationCode(code);

        // 自动创建设备记录（如果不存在）
        SysDevice device = deviceMapper.selectSysDeviceByUuid(deviceUuid);
        if (device == null) {
            device = new SysDevice();
            device.setDeviceUuid(deviceUuid);
            device.setDeviceName(deviceName);
            device.setDeviceModel(deviceModel);
            device.setDeviceOs(deviceOs);
            device.setAppVersion(appVersion);
            device.setActivationCodeId(code.getCodeId());
            device.setStatus("0"); // 离线
            device.setCreateTime(DateUtils.getNowDate());
            deviceService.insertSysDevice(device);
        } else {
            // 设备已存在，更新关联信息
            device.setActivationCodeId(code.getCodeId());
            deviceMapper.updateSysDevice(device);
        }

        return new ActivationApiResponse(
            true,
            "激活码验证成功",
            code.getCodeId(),
            code.getActivatedCount(),
            code.getMaxDeviceCount(),
            code.getExpireTime() != null ? code.getExpireTime().getTime() : null,
            code.getActivateTime() != null ? code.getActivateTime().getTime() : null,
            code.getActivateDevice(),
            code.getValidDays()
        );
    }

    /**
     * 生成随机激活码
     *
     * @return 激活码
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        Random random = new Random();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CODE_CHARS.length());
            code.append(CODE_CHARS.charAt(index));
        }

        // 确保生成的激活码唯一
        if (activationCodeMapper.selectSysActivationCodeByValue(code.toString()) != null) {
            return generateCode();
        }

        return code.toString();
    }
}
