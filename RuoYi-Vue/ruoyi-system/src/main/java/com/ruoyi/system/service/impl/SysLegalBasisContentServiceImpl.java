package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysLegalBasisContent;
import com.ruoyi.system.mapper.SysLegalBasisContentMapper;
import com.ruoyi.system.service.ISysLegalBasisContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysLegalBasisContentServiceImpl implements ISysLegalBasisContentService {

    @Autowired
    private SysLegalBasisContentMapper sysLegalBasisContentMapper;

    @Override
    public SysLegalBasisContent selectSysLegalBasisContentById(Long contentId) {
        return sysLegalBasisContentMapper.selectSysLegalBasisContentById(contentId);
    }

    @Override
    public List<SysLegalBasisContent> selectSysLegalBasisContentList(SysLegalBasisContent sysLegalBasisContent) {
        return sysLegalBasisContentMapper.selectSysLegalBasisContentList(sysLegalBasisContent);
    }

    @Override
    public List<SysLegalBasisContent> selectSysLegalBasisContentByBasisId(Long basisId) {
        return sysLegalBasisContentMapper.selectSysLegalBasisContentByBasisId(basisId);
    }

    @Override
    public int insertSysLegalBasisContent(SysLegalBasisContent sysLegalBasisContent) {
        return sysLegalBasisContentMapper.insertSysLegalBasisContent(sysLegalBasisContent);
    }

    @Override
    @Transactional
    public int batchInsertSysLegalBasisContent(Long basisId, List<SysLegalBasisContent> contents) {
        if (contents == null || contents.isEmpty()) {
            return 0;
        }
        // 设置 basisId
        for (SysLegalBasisContent content : contents) {
            content.setBasisId(basisId);
        }
        return sysLegalBasisContentMapper.batchInsertSysLegalBasisContent(contents);
    }

    @Override
    public int updateSysLegalBasisContent(SysLegalBasisContent sysLegalBasisContent) {
        return sysLegalBasisContentMapper.updateSysLegalBasisContent(sysLegalBasisContent);
    }

    @Override
    public int deleteSysLegalBasisContentById(Long contentId) {
        return sysLegalBasisContentMapper.deleteSysLegalBasisContentById(contentId);
    }

    @Override
    public int deleteSysLegalBasisContentByBasisId(Long basisId) {
        return sysLegalBasisContentMapper.deleteSysLegalBasisContentByBasisId(basisId);
    }

    @Override
    public int deleteSysLegalBasisContentByIds(Long[] contentIds) {
        return sysLegalBasisContentMapper.deleteSysLegalBasisContentByIds(contentIds);
    }
}
