package com.lys.vhr.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author liwudi
 * @date 2019/7/13 - 14:49
 * 该类用于角色信息的比对，在一个请求走完FilterInvocationSecurityMetadataSource中的getAttributtes后
 */
@Component
public class UrlAccessDecisionManager implements AccessDecisionManager {

    @Override
    /**
     * Authentication:当前登录的用户信息(WebSecurityConfigurerAdapter.configure(AuthenticationManagerBuilder auth))
     * Object:FilterInvocation对象(当前对象的请求信息)
     * Collection<ConfigAttribute>:FilterInvocationSecurityMetadataSource返回的角色信息
     */
    public void decide(Authentication auth, Object o, Collection<ConfigAttribute> collection) {
        Iterator<ConfigAttribute> iterator = collection.iterator();
        while (iterator.hasNext()) {
            ConfigAttribute ca = iterator.next();
            //若该用户具备请求需要的角色，且未抛出异常，则直接通过，否则说明权限不足
            if ("ROLE_LOGIN".equals(ca.getAttribute())) {
                if (auth instanceof AnonymousAuthenticationToken) {
                    throw new BadCredentialsException("未登录");
                } else
                     return;
            }
            //若当前所需的角色是ROLE_LOGIN，则只需判断是否为auth的实例，即表示当前用户已经登录
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(ca.getAttribute())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("权限不足！");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
