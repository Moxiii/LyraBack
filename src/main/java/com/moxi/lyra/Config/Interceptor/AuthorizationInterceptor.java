package com.moxi.lyra.Config.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.moxi.lyra.Config.CustomAnnotation.RequireAuthorization;
import com.moxi.lyra.Config.Utils.JwtUtil;
import com.moxi.lyra.Config.Utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            RequireAuthorization requireAuthorization = handlerMethod.getMethodAnnotation(RequireAuthorization.class);
            String uri = request.getRequestURI();
            if (uri.startsWith("/api/auth")) {
                return true; //
            }
            if(requireAuthorization != null) {
                if(!SecurityUtils.isAuthorized(request , jwtUtil)){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Unauthorized access");
                    return false;
                }
            }
        }
        return true;
    }
}

