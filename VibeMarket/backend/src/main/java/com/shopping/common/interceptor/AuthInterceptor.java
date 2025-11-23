package com.shopping.common.interceptor;

import com.shopping.common.annotation.CurrentAdmin;
import com.shopping.common.annotation.CurrentUser;
import com.shopping.admin.entity.Admin;
import com.shopping.admin.mapper.AdminMapper;
import com.shopping.common.exception.BusinessException;
import com.shopping.common.utils.JwtUtils;
import com.shopping.user.entity.User;
import com.shopping.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 检查方法参数中是否有@CurrentUser或@CurrentAdmin注解
        boolean needUserAuth = false;
        boolean needAdminAuth = false;
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(CurrentUser.class)) {
                needUserAuth = true;
            }
            if (parameter.isAnnotationPresent(CurrentAdmin.class)) {
                needAdminAuth = true;
            }
        }

        if (!needUserAuth && !needAdminAuth) {
            return true;
        }

        // 从请求头获取token
        String token = getTokenFromRequest(request);
        if (token == null || jwtUtils.isTokenExpired(token)) {
            throw new BusinessException(401, "未登录或token已过期");
        }

        try {
            io.jsonwebtoken.Claims claims = jwtUtils.parseToken(token);
            String type = (String) claims.get("type");
            
            if ("user".equals(type) && needUserAuth) {
                Long userId = Long.parseLong(claims.get("userId").toString());
                User user = userMapper.selectById(userId);
                if (user == null || user.getStatus() != 1) {
                    throw new BusinessException(401, "用户不存在或已被禁用");
                }
                request.setAttribute("currentUser", user);
            } else if ("admin".equals(type) && needAdminAuth) {
                Long adminId = Long.parseLong(claims.get("adminId").toString());
                Admin admin = adminMapper.selectById(adminId);
                if (admin == null || admin.getStatus() != 1) {
                    throw new BusinessException(401, "管理员不存在或已被禁用");
                }
                request.setAttribute("currentAdmin", admin);
            } else {
                throw new BusinessException(403, "权限不足，请使用正确的账户类型");
            }
        } catch (BusinessException e) {
            // 重新抛出业务异常
            throw e;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new BusinessException(401, "token已过期，请重新登录");
        } catch (io.jsonwebtoken.JwtException e) {
            throw new BusinessException(401, "token无效，请重新登录");
        } catch (Exception e) {
            throw new BusinessException(401, "token解析失败: " + e.getMessage());
        }

        return true;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

