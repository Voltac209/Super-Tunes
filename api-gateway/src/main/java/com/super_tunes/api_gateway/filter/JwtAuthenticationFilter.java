package com.super_tunes.api_gateway.filter;

import com.super_tunes.api_gateway.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (isPublic(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublic(String path, String method) {
        if ("/".equals(path) || "/index.html".equals(path)) {
            return true;
        }
        if (path.startsWith("/styles.css") || path.startsWith("/app.js") || path.startsWith("/favicon")) {
            return true;
        }
        if (path.startsWith("/auth/")) {
            return true;
        }
        if (path.startsWith("/api/songs") && "GET".equals(method)) {
            return true;
        }
        if (path.startsWith("/api/users") && "GET".equals(method)) {
            return true;
        }
        if (path.startsWith("/api/playlists") && "GET".equals(method)) {
            return true;
        }
        return false;
    }
}
