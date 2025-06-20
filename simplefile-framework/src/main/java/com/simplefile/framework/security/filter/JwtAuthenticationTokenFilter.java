package com.simplefile.framework.security.filter;

import java.io.IOException;
import java.util.Base64;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.simplefile.common.cache.GuavaCommonLocalCache;
import com.simplefile.common.core.domain.entity.SysUser;
import com.simplefile.common.core.domain.model.WebDavLoginUser;
import com.simplefile.framework.web.service.UserDetailsServiceImpl;
import com.simplefile.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.simplefile.common.core.domain.model.LoginUser;
import com.simplefile.common.utils.SecurityUtils;
import com.simplefile.common.utils.StringUtils;
import com.simplefile.framework.web.service.TokenService;

/**
 * token过滤器 验证token有效性
 *
 * @author ruoyi
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter
{
    @Autowired
    private TokenService tokenService;

    @Resource
    private ISysUserService sysUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        if(request.getRequestURI().contains("system/webdav")){
            // 获取Authorization请求头
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                // 抛出认证异常
                throw new AuthenticationCredentialsNotFoundException("Missing or invalid Basic Authorization header");
            }

            try {
                // 去除"Basic "前缀
                String base64Credentials = authHeader.substring("Basic ".length()).trim();

                // Base64解码
                byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(decodedBytes);

                // 使用冒号分割用户名和密码
                String[] values = credentials.split(":", 2);

                if (values.length != 2) {
                    // 抛出认证异常
                    throw new AuthenticationCredentialsNotFoundException("Missing or invalid Basic Authorization header");
                }

                Object cacheObject = GuavaCommonLocalCache.getCacheObject(GuavaCommonLocalCache.KEY_LOGIN_TOKEN, authHeader);
                if(cacheObject == null){
                    SysUser sysUser = sysUserService.selectUserByUserName(values[0]);
                    if (sysUser == null) {
                        // 抛出认证异常
                        throw new AuthenticationCredentialsNotFoundException("Missing or invalid Basic Authorization header");
                    }
                    if (!SecurityUtils.matchesPassword(values[1], sysUser.getPassword())) {
                        throw new AuthenticationCredentialsNotFoundException("Missing or invalid Basic Authorization header");
                    }
                    // 验证成功
                    WebDavLoginUser loginUser = new WebDavLoginUser();
                    loginUser.setUserName(sysUser.getUserName());
                    loginUser.setUserId(sysUser.getUserId());
                    // 加入缓存
                    GuavaCommonLocalCache.setCacheObject(GuavaCommonLocalCache.KEY_LOGIN_TOKEN, authHeader, loginUser);
                }
                chain.doFilter(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                // 抛出认证异常
                throw new AuthenticationCredentialsNotFoundException("Missing or invalid Basic Authorization header");
            }

        }else{
            LoginUser loginUser = tokenService.getLoginUser(request);
            if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication()))
            {
                tokenService.verifyToken(loginUser);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            chain.doFilter(request, response);
        }

    }
}
