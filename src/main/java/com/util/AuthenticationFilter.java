package com.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<Cookie[]> cookies = Optional.ofNullable(request.getCookies());

        String path = request.getRequestURI();

        if (!isThereACustomCookie(cookies) && !isPathContainsLogin(path) && !isPathContainsAssets(path)) {
            String loginURI = request.getContextPath() + "/login";

            response.reset();
            response.resetBuffer();
            response.sendRedirect(loginURI);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isThereACustomCookie(Optional<Cookie[]> cookies) {
        if (cookies.isPresent()) {
            for (Cookie cookie : cookies.get()) {
                if (cookie.getName().equals("CUSTOM_COOKIE")) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isPathContainsLogin(String path) {
        return path.contains("/login");
    }

    private boolean isPathContainsAssets(String path) {
        return path.contains(".js") || path.contains(".css");
    }
}
