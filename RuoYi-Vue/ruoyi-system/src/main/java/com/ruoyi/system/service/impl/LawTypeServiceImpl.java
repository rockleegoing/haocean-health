package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.LawTypeMapper;
import com.ruoyi.system.domain.LawType;
import com.ruoyi.system.service.ILawTypeService;

/**
 * 法律法规类型Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
@Service
public class LawTypeServiceImpl implements ILawTypeService 
{
    @Autowired
    private LawTypeMapper lawTypeMapper;

    /**
     * 查询法律法规类型
     * 
     * @param id 法律法规类型主键
     * @return 法律法规类型
     */
    @Override
    public LawType selectLawTypeById(Long id)
    {
        return lawTypeMapper.selectLawTypeById(id);
    }

    /**
     * 查询法律法规类型列表
     * 
     * @param lawType 法律法规类型
     * @return 法律法规类型
     */
    @Override
    public List<LawType> selectLawTypeList(LawType lawType)
    {
        return lawTypeMapper.selectLawTypeList(lawType);
    }

    /**
     * 新增法律法规类型
     *
     * @param lawType 法律法规类型
     * @return 结果
     */
    @Override
    public int insertLawType(LawType lawType)
    {
        lawType.setCreateTime(DateUtils.getNowDate());
        // 自动计算 ancestors
        if (lawType.getParentId() == null || lawType.getParentId() == 0) {
            lawType.setAncestors("0");
        } else {
            // 查询父节点的 ancestors
            LawType parent = lawTypeMapper.selectLawTypeById(lawType.getParentId());
            if (parent != null) {
                lawType.setAncestors(parent.getAncestors() + "," + parent.getId());
            } else {
                lawType.setAncestors("0");
            }
        }
        return lawTypeMapper.insertLawType(lawType);
    }

    /**
     * 修改法律法规类型
     *
     * @param lawType 法律法规类型
     * @return 结果
     */
    @Override
    public int updateLawType(LawType lawType)
    {
        lawType.setUpdateTime(DateUtils.getNowDate());
        LawType old = lawTypeMapper.selectLawTypeById(lawType.getId());
        String oldAncestors = old.getAncestors();
        String newAncestors;

        if (lawType.getParentId() == null || lawType.getParentId() == 0) {
            newAncestors = "0";
        } else {
            LawType parent = lawTypeMapper.selectLawTypeById(lawType.getParentId());
            if (parent != null) {
                newAncestors = parent.getAncestors() + "," + parent.getId();
            } else {
                newAncestors = "0";
            }
        }
        lawType.setAncestors(newAncestors);

        int result = lawTypeMapper.updateLawType(lawType);

        // 如果 ancestors 发生变化，需要更新所有子节点的 ancestors
        if (result > 0 && !oldAncestors.equals(newAncestors)) {
            // 更新子节点的 ancestors（将旧前缀替换为新前缀）
            List<LawType> children = lawTypeMapper.selectChildrenByAncestorsLike(oldAncestors + "," + lawType.getId());
            for (LawType child : children) {
                LawType updateChild = new LawType();
                updateChild.setId(child.getId());
                updateChild.setAncestors(newAncestors + child.getAncestors().substring(oldAncestors.length()));
                lawTypeMapper.updateLawType(updateChild);
            }
        }

        return result;
    }

    /**
     * 批量删除法律法规类型
     * 
     * @param ids 需要删除的法律法规类型主键
     * @return 结果
     */
    @Override
    public int deleteLawTypeByIds(Long[] ids)
    {
        return lawTypeMapper.deleteLawTypeByIds(ids);
    }

    /**
     * 删除法律法规类型信息
     * 
     * @param id 法律法规类型主键
     * @return 结果
     */
    @Override
    public int deleteLawTypeById(Long id)
    {
        return lawTypeMapper.deleteLawTypeById(id);
    }
}
