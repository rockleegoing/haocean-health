package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysProcessingBasisMapper;
import com.ruoyi.system.domain.SysProcessingBasis;
import com.ruoyi.system.domain.SysProcessingBasisContent;
import com.ruoyi.system.service.ISysProcessingBasisService;
import com.ruoyi.system.service.ISysProcessingBasisContentService;

/**
 * 处理依据Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-28
 */
@Service
public class SysProcessingBasisServiceImpl implements ISysProcessingBasisService {
    @Autowired
    private SysProcessingBasisMapper sysProcessingBasisMapper;

    @Autowired
    private ISysProcessingBasisContentService sysProcessingBasisContentService;

    /**
     * 查询处理依据
     *
     * @param basisId 处理依据主键
     * @return 处理依据
     */
    @Override
    public SysProcessingBasis selectSysProcessingBasisById(Long basisId) {
        return sysProcessingBasisMapper.selectSysProcessingBasisById(basisId);
    }

    /**
     * 查询处理依据列表
     *
     * @param sysProcessingBasis 处理依据
     * @return 处理依据
     */
    @Override
    public List<SysProcessingBasis> selectSysProcessingBasisList(SysProcessingBasis sysProcessingBasis) {
        return sysProcessingBasisMapper.selectSysProcessingBasisList(sysProcessingBasis);
    }

    /**
     * 查询某法律法规关联的处理依据列表
     */
    @Override
    public List<SysProcessingBasis> selectSysProcessingBasisListByRegulationId(Long regulationId) {
        return sysProcessingBasisMapper.selectSysProcessingBasisListByRegulationId(regulationId);
    }

    /**
     * 新增处理依据
     *
     * @param sysProcessingBasis 处理依据
     * @return 结果
     */
    @Override
    public int insertSysProcessingBasis(SysProcessingBasis sysProcessingBasis) {
        sysProcessingBasis.setCreateTime(DateUtils.getNowDate());
        sysProcessingBasis.setDelFlag("0");
        return sysProcessingBasisMapper.insertSysProcessingBasis(sysProcessingBasis);
    }

    /**
     * 修改处理依据
     *
     * @param sysProcessingBasis 处理依据
     * @return 结果
     */
    @Override
    public int updateSysProcessingBasis(SysProcessingBasis sysProcessingBasis) {
        sysProcessingBasis.setUpdateTime(DateUtils.getNowDate());
        return sysProcessingBasisMapper.updateSysProcessingBasis(sysProcessingBasis);
    }

    /**
     * 批量删除处理依据
     *
     * @param basisIds 需要删除的处理依据主键
     * @return 结果
     */
    @Override
    public int deleteSysProcessingBasisByIds(Long[] basisIds) {
        return sysProcessingBasisMapper.deleteSysProcessingBasisByIds(basisIds);
    }

    /**
     * 删除处理依据信息
     *
     * @param basisId 处理依据主键
     * @return 结果
     */
    @Override
    public int deleteSysProcessingBasisById(Long basisId) {
        return sysProcessingBasisMapper.deleteSysProcessingBasisById(basisId);
    }

    /**
     * 查询处理依据详情（含内容列表）
     */
    @Override
    public Map<String, Object> selectProcessingBasisDetail(Long basisId) {
        SysProcessingBasis processingBasis = sysProcessingBasisMapper.selectSysProcessingBasisById(basisId);
        List<SysProcessingBasisContent> contents = sysProcessingBasisContentService.selectSysProcessingBasisContentByBasisId(basisId);
        Map<String, Object> result = new HashMap<>();
        result.put("basis", processingBasis);
        result.put("contents", contents);
        return result;
    }
}
