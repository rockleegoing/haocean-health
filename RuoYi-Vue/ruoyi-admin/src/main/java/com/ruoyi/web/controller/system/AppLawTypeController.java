package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.LawType;
import com.ruoyi.system.service.ILawTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Android 端法律类型接口（匿名可访问）
 */
@RestController
@RequestMapping("/app/lawtype")
public class AppLawTypeController extends BaseController {

    @Autowired
    private ILawTypeService lawTypeService;

    /**
     * 获取法律类型树形结构
     */
    @Anonymous
    @GetMapping("/treeList")
    public AjaxResult treeList() {
        LawType lawType = new LawType();
        List<LawType> list = lawTypeService.selectLawTypeList(lawType);
        return AjaxResult.success(handleTree(list));
    }

    /**
     * 处理树形结构
     */
    private List<LawType> handleTree(List<LawType> list) {
        List<LawType> returnList = new ArrayList<>();
        List<Long> tempList = list.stream().map(LawType::getId).collect(Collectors.toList());
        for (LawType lawType : list) {
            if (!tempList.contains(lawType.getParentId())) {
                recursionFn(list, lawType);
                returnList.add(lawType);
            }
        }
        if (returnList.isEmpty()) {
            returnList = list;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<LawType> list, LawType t) {
        List<LawType> childList = getChildList(list, t);
        t.setChildren(childList);
        for (LawType tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 获取子列表
     */
    private List<LawType> getChildList(List<LawType> list, LawType t) {
        List<LawType> tlist = new ArrayList<>();
        for (LawType n : list) {
            if (n.getParentId() != null && n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<LawType> list, LawType t) {
        return getChildList(list, t).size() > 0;
    }
}
