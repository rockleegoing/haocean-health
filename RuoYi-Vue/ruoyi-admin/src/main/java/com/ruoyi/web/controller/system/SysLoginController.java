package com.ruoyi.web.controller.system;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysIndustryCategoryService;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUnitService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.domain.SysIndustryCategory;
import com.ruoyi.system.domain.SysUnit;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysRoleMapper;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysIndustryCategoryService categoryService;

    @Autowired
    private ISysUnitService unitService;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        if (!loginUser.getPermissions().equals(permissions))
        {
            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        ajax.put("pwdChrtype", getSysAccountChrtype());
        ajax.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
        ajax.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
        return ajax;
    }

    /**
     * Android端数据预加载（无需认证）
     * 同步所有用户（含完整权限信息）、部门、角色、菜单数据到本地
     *
     * @return 用户（含 permissions/roles）、部门、角色、菜单数据
     */
    @com.ruoyi.common.annotation.Anonymous
    @GetMapping("/app/sync")
    public AjaxResult appSync()
    {
        AjaxResult ajax = AjaxResult.success();

        // 1. 获取所有用户（含明文密码），使用 mapper 直接查询绕过数据权限
        List<SysUser> users = userMapper.selectUserListWithoutDataScope();

        // 为每个用户计算完整的权限信息
        List<Map<String, Object>> usersWithPermission = users.stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userId", user.getUserId());
            userMap.put("userName", user.getUserName());
            userMap.put("nickName", user.getNickName());
            userMap.put("email", user.getEmail());
            userMap.put("phonenumber", user.getPhonenumber());
            userMap.put("sex", user.getSex());
            userMap.put("avatar", user.getAvatar());
            userMap.put("status", user.getStatus());
            userMap.put("password", user.getPassword());
            userMap.put("plainPassword", user.getPlainPassword());
            userMap.put("pwdChrtype", getSysAccountChrtype());
            userMap.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
            userMap.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));

            // 获取该用户的角色和权限
            Set<String> roleSet = permissionService.getRolePermission(user);
            Set<String> permissionSet = permissionService.getMenuPermission(user);
            userMap.put("roles", roleSet);
            userMap.put("permissions", permissionSet);

            return userMap;
        }).toList();
        ajax.put("users", usersWithPermission);

        // 2. 获取所有部门，使用 mapper 直接查询绕过数据权限
        List<SysDept> depts = deptMapper.selectDeptListWithoutDataScope();
        ajax.put("depts", depts);

        // 3. 获取所有角色，使用 mapper 直接查询绕过数据权限
        List<SysRole> roles = roleMapper.selectRoleListWithoutDataScope();
        ajax.put("roles", roles);

        // 4. 获取所有菜单
        List<SysMenu> menus = menuService.selectMenuAll();
        ajax.put("menus", menus);

        // 5. 获取所有行业分类
        List<SysIndustryCategory> categories = categoryService.selectSysIndustryCategoryList(new SysIndustryCategory());
        ajax.put("categories", categories);

        // 6. 获取所有执法单位，带行业分类名称
        List<SysUnit> units = unitService.selectUnitListWithCategory(new SysUnit());
        ajax.put("units", units);

        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }

    // 获取用户密码自定义配置规则
    public String getSysAccountChrtype()
    {
        return Convert.toStr(configService.selectConfigByKey("sys.account.chrtype"), "0");
    }

    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate)
    {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate)
    {
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0)
        {
            if (StringUtils.isNull(pwdUpdateDate))
            {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
