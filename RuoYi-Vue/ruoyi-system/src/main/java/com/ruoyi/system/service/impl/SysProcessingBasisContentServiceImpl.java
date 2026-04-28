package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysProcessingBasisContent;
import com.ruoyi.system.mapper.SysProcessingBasisContentMapper;
import com.ruoyi.system.service.ISysProcessingBasisContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysProcessingBasisContentServiceImpl implements ISysProcessingBasisContentService {

    @Autowired
    private SysProcessingBasisContentMapper sysProcessingBasisContentMapper;

    @Override
    public SysProcessingBasisContent selectSysProcessingBasisContentById(Long contentId) {
        return sysProcessingBasisContentMapper.selectSysProcessingBasisContentById(contentId);
    }

    @Override
    public List<SysProcessingBasisContent> selectSysProcessingBasisContentList(SysProcessingBasisContent sysProcessingBasisContent) {
        return sysProcessingBasisContentMapper.selectSysProcessingBasisContentList(sysProcessingBasisContent);
    }

    @Override
    public List<SysProcessingBasisContent> selectSysProcessingBasisContentByBasisId(Long basisId) {
        return sysProcessingBasisContentMapper.selectSysProcessingBasisContentByBasisId(basisId);
    }

    @Override
    public int insertSysProcessingBasisContent(SysProcessingBasisContent sysProcessingBasisContent) {
        return sysProcessingBasisContentMapper.insertSysProcessingBasisContent(sysProcessingBasisContent);
    }

    @Override
    @Transactional
    public int batchInsertSysProcessingBasisContent(Long basisId, List<SysProcessingBasisContent> contents) {
        if (contents == null || contents.isEmpty()) {
            return 0;
        }
        // 设置 basisId
        for (SysProcessingBasisContent content : contents) {
            content.setBasisId(basisId);
        }
        return sysProcessingBasisContentMapper.batchInsertSysProcessingBasisContent(contents);
    }

    @Override
    public int updateSysProcessingBasisContent(SysProcessingBasisContent sysProcessingBasisContent) {
        return sysProcessingBasisContentMapper.updateSysProcessingBasisContent(sysProcessingBasisContent);
    }

    @Override
    public int deleteSysProcessingBasisContentById(Long contentId) {
        return sysProcessingBasisContentMapper.deleteSysProcessingBasisContentById(contentId);
    }

    @Override
    public int deleteSysProcessingBasisContentByBasisId(Long basisId) {
        return sysProcessingBasisContentMapper.deleteSysProcessingBasisContentByBasisId(basisId);
    }

    @Override
    public int deleteSysProcessingBasisContentByIds(Long[] contentIds) {
        return sysProcessingBasisContentMapper.deleteSysProcessingBasisContentByIds(contentIds);
    }
}
