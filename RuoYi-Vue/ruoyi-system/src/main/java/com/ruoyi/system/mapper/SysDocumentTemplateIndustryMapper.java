package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysDocumentTemplateIndustry;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 文书模板与行业分类关联Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-26
 */
public interface SysDocumentTemplateIndustryMapper {

    int insertBatch(@Param("list") List<SysDocumentTemplateIndustry> list);

    int deleteByTemplateId(Long templateId);

    List<Long> selectByIndustryCategoryId(Long industryCategoryId);

    List<Long> selectIndustryCategoryIdsByTemplateId(Long templateId);

    List<SysDocumentTemplateIndustry> selectAll();
}
