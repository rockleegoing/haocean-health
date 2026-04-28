package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysProcessingBasisContent;
import java.util.List;

public interface ISysProcessingBasisContentService {

    /**
     * 查询处理依据内容
     */
    SysProcessingBasisContent selectSysProcessingBasisContentById(Long contentId);

    /**
     * 查询处理依据内容列表
     */
    List<SysProcessingBasisContent> selectSysProcessingBasisContentList(SysProcessingBasisContent sysProcessingBasisContent);

    /**
     * 查询某处理依据的所有内容
     */
    List<SysProcessingBasisContent> selectSysProcessingBasisContentByBasisId(Long basisId);

    /**
     * 新增处理依据内容
     */
    int insertSysProcessingBasisContent(SysProcessingBasisContent sysProcessingBasisContent);

    /**
     * 批量新增处理依据内容
     */
    int batchInsertSysProcessingBasisContent(Long basisId, List<SysProcessingBasisContent> contents);

    /**
     * 修改处理依据内容
     */
    int updateSysProcessingBasisContent(SysProcessingBasisContent sysProcessingBasisContent);

    /**
     * 删除处理依据内容
     */
    int deleteSysProcessingBasisContentById(Long contentId);

    /**
     * 删除某处理依据的所有内容
     */
    int deleteSysProcessingBasisContentByBasisId(Long basisId);

    /**
     * 批量删除处理依据内容
     */
    int deleteSysProcessingBasisContentByIds(Long[] contentIds);
}
