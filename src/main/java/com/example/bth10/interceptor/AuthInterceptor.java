package com.example.bth10.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.bth10.entity.User;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            response.sendRedirect("/login");
            return false;
        }

        String path = request.getRequestURI();

        if (path.startsWith("/category") || path.startsWith("/product")) {
            if (user.getRole() != User.Role.admin && user.getRole() != User.Role.manager) {
                response.sendRedirect("/login");
                return false;
            }
        } else if (path.startsWith("/admin")) {
            if (user.getRole() != User.Role.admin) {
                response.sendRedirect("/login");
                return false;
            }
        }

        return true;
    }
}
