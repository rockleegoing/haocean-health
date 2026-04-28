package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysLegalBasisMapper;
import com.ruoyi.system.domain.SysLegalBasis;
import com.ruoyi.system.domain.SysLegalBasisContent;
import com.ruoyi.system.service.ISysLegalBasisService;
import com.ruoyi.system.service.ISysLegalBasisContentService;

/**
 * 定性依据Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-25
 */
@Service
public class SysLegalBasisServiceImpl implements ISysLegalBasisService {
    @Autowired
    private SysLegalBasisMapper sysLegalBasisMapper;

    @Autowired
    private ISysLegalBasisContentService sysLegalBasisContentService;

    /**
     * 查询定性依据
     *
     * @param basisId 定性依据主键
     * @return 定性依据
     */
    @Override
    public SysLegalBasis selectSysLegalBasisById(Long basisId) {
        return sysLegalBasisMapper.selectSysLegalBasisById(basisId);
    }

    /**
     * 查询定性依据列表
     *
     * @param sysLegalBasis 定性依据
     * @return 定性依据
     */
    @Override
    public List<SysLegalBasis> selectSysLegalBasisList(SysLegalBasis sysLegalBasis) {
        return sysLegalBasisMapper.selectSysLegalBasisList(sysLegalBasis);
    }

    /**
     * 查询某法律法规关联的定性依据列表
     */
    @Override
    public List<SysLegalBasis> selectSysLegalBasisListByRegulationId(Long regulationId) {
        return sysLegalBasisMapper.selectSysLegalBasisListByRegulationId(regulationId);
    }

    /**
     * 新增定性依据
     *
     * @param sysLegalBasis 定性依据
     * @return 结果
     */
    @Override
    public int insertSysLegalBasis(SysLegalBasis sysLegalBasis) {
        sysLegalBasis.setCreateTime(DateUtils.getNowDate());
        sysLegalBasis.setDelFlag("0");
        return sysLegalBasisMapper.insertSysLegalBasis(sysLegalBasis);
    }

    /**
     * 修改定性依据
     *
     * @param sysLegalBasis 定性依据
     * @return 结果
     */
    @Override
    public int updateSysLegalBasis(SysLegalBasis sysLegalBasis) {
        sysLegalBasis.setUpdateTime(DateUtils.getNowDate());
        return sysLegalBasisMapper.updateSysLegalBasis(sysLegalBasis);
    }

    /**
     * 批量删除定性依据
     *
     * @param basisIds 需要删除的定性依据主键
     * @return 结果
     */
    @Override
    public int deleteSysLegalBasisByIds(Long[] basisIds) {
        return sysLegalBasisMapper.deleteSysLegalBasisByIds(basisIds);
    }

    /**
     * 删除定性依据信息
     *
     * @param basisId 定性依据主键
     * @return 结果
     */
    @Override
    public int deleteSysLegalBasisById(Long basisId) {
        return sysLegalBasisMapper.deleteSysLegalBasisById(basisId);
    }

    /**
     * 查询定性依据详情（含内容列表）
     */
    @Override
    public Map<String, Object> selectLegalBasisDetail(Long basisId) {
        SysLegalBasis legalBasis = sysLegalBasisMapper.selectSysLegalBasisById(basisId);
        List<SysLegalBasisContent> contents = sysLegalBasisContentService.selectSysLegalBasisContentByBasisId(basisId);
        Map<String, Object> result = new HashMap<>();
        result.put("basis", legalBasis);
        result.put("contents", contents);
        return result;
    }
}
