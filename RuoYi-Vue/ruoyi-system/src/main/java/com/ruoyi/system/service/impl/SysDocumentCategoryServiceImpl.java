package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysDocumentCategoryMapper;
import com.ruoyi.system.domain.SysDocumentCategory;
import com.ruoyi.system.service.ISysDocumentCategoryService;

@Service
public class SysDocumentCategoryServiceImpl implements ISysDocumentCategoryService {

    @Autowired
    private SysDocumentCategoryMapper categoryMapper;

    @Override
    public SysDocumentCategory selectSysDocumentCategoryById(Long categoryId) {
        return categoryMapper.selectSysDocumentCategoryById(categoryId);
    }

    @Override
    public List<SysDocumentCategory> selectSysDocumentCategoryList(SysDocumentCategory category) {
        return categoryMapper.selectSysDocumentCategoryList(category);
    }

    @Override
    public List<SysDocumentCategory> selectAllSysDocumentCategories() {
        return categoryMapper.selectAllSysDocumentCategories();
    }

    @Override
    public int insertSysDocumentCategory(SysDocumentCategory category) {
        return categoryMapper.insertSysDocumentCategory(category);
    }

    @Override
    public int updateSysDocumentCategory(SysDocumentCategory category) {
        return categoryMapper.updateSysDocumentCategory(category);
    }

    @Override
    public int deleteSysDocumentCategoryById(Long categoryId) {
        return categoryMapper.deleteSysDocumentCategoryById(categoryId);
    }

    @Override
    public int deleteSysDocumentCategoryByIds(Long[] categoryIds) {
        return categoryMapper.deleteSysDocumentCategoryByIds(categoryIds);
    }
}