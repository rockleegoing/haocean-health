package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysLegalBasisContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysLegalBasisContentMapper {

    /**
     * 查询定性依据内容
     */
    SysLegalBasisContent selectSysLegalBasisContentById(Long contentId);

    /**
     * 查询定性依据内容列表
     */
    List<SysLegalBasisContent> selectSysLegalBasisContentList(SysLegalBasisContent sysLegalBasisContent);

    /**
     * 查询某定性依据的所有内容
     */
    List<SysLegalBasisContent> selectSysLegalBasisContentByBasisId(Long basisId);

    /**
     * 新增定性依据内容
     */
    int insertSysLegalBasisContent(SysLegalBasisContent sysLegalBasisContent);

    /**
     * 批量新增定性依据内容
     */
    int batchInsertSysLegalBasisContent(@Param("list") List<SysLegalBasisContent> list);

    /**
     * 修改定性依据内容
     */
    int updateSysLegalBasisContent(SysLegalBasisContent sysLegalBasisContent);

    /**
     * 删除定性依据内容
     */
    int deleteSysLegalBasisContentById(Long contentId);

    /**
     * 删除某定性依据的所有内容
     */
    int deleteSysLegalBasisContentByBasisId(Long basisId);

    /**
     * 批量删除定性依据内容
     */
    int deleteSysLegalBasisContentByIds(Long[] contentIds);
}
