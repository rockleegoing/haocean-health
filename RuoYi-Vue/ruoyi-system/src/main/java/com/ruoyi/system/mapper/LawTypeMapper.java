package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.LawType;

/**
 * 法律法规类型Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public interface LawTypeMapper 
{
    /**
     * 查询法律法规类型
     * 
     * @param id 法律法规类型主键
     * @return 法律法规类型
     */
    public LawType selectLawTypeById(Long id);

    /**
     * 查询法律法规类型列表
     * 
     * @param lawType 法律法规类型
     * @return 法律法规类型集合
     */
    public List<LawType> selectLawTypeList(LawType lawType);

    /**
     * 新增法律法规类型
     * 
     * @param lawType 法律法规类型
     * @return 结果
     */
    public int insertLawType(LawType lawType);

    /**
     * 修改法律法规类型
     * 
     * @param lawType 法律法规类型
     * @return 结果
     */
    public int updateLawType(LawType lawType);

    /**
     * 删除法律法规类型
     * 
     * @param id 法律法规类型主键
     * @return 结果
     */
    public int deleteLawTypeById(Long id);

    /**
     * 批量删除法律法规类型
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLawTypeByIds(Long[] ids);

    /**
     * 查询指定 ancestors 前缀的所有子节点
     * @param ancestorsPrefix 祖先路径前缀
     * @return 子节点列表
     */
    public List<LawType> selectChildrenByAncestorsLike(String ancestorsPrefix);
}
