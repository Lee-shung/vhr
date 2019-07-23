package com.lys.vhr.config;

import com.lys.vhr.bean.Menu;
import com.lys.vhr.bean.Role;
import com.lys.vhr.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

/**
 * @author liwudi
 * @date 2019/7/13 - 13:57
 *
 * 该类用于比对URL，实现动态配置权限
 */
@Component//泛指各种组件，通用，无敌
public class CustomMetadataSource implements FilterInvocationSecurityMetadataSource {
    @Autowired
    MenuService menuService;
    AntPathMatcher antPathMatcher = new AntPathMatcher();
    /*FilterInvocaation里的属性：
     *  private FilterChain chain;
     *  private HttpServletRequest request;
     *  private HttpServletResponse response;
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        String requestUrl = ((FilterInvocation) o).getRequestUrl();//提取出请求的URL
        List<Menu> allMenu = menuService.getAllMenu();//查询出菜单资源
        //遍历菜单资源，跟请求的URL进行比对，对应未匹配成功的，默认都是登录后访问
        for (Menu menu : allMenu) {
            if (antPathMatcher.match(menu.getUrl(),requestUrl) && menu.getRoles().size() > 0) {
                List<Role> roles = menu.getRoles();
                int size = roles.size();
                String[] values = new String[size];//获取权限名并返回
                for (int i = 0;i < size;i ++) {
                    values[i] = roles.get(i).getName();
                }
                return SecurityConfig.createList(values);
            }
        }

        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
