package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentCategory;

public interface ISysDocumentCategoryService {
    public SysDocumentCategory selectSysDocumentCategoryById(Long categoryId);
    public List<SysDocumentCategory> selectSysDocumentCategoryList(SysDocumentCategory category);
    public List<SysDocumentCategory> selectAllSysDocumentCategories();
    public int insertSysDocumentCategory(SysDocumentCategory category);
    public int updateSysDocumentCategory(SysDocumentCategory category);
    public int deleteSysDocumentCategoryById(Long categoryId);
    public int deleteSysDocumentCategoryByIds(Long[] categoryIds);
}
