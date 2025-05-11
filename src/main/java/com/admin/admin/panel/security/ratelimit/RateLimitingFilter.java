package com.admin.admin.panel.security.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private final Map<String, UserRequestInfo> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientIp = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis() / 60000; // minute granularity

        UserRequestInfo info = requestCounts.computeIfAbsent(clientIp, k -> new UserRequestInfo(currentTime));
        synchronized (info) {
            if (info.minute != currentTime) {
                info.minute = currentTime;
                info.count.set(0);
            }
            if (info.count.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
                response.setStatus(429);
                response.getWriter().write("Too many requests");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private static class UserRequestInfo {
        long minute;
        AtomicInteger count = new AtomicInteger(0);

        UserRequestInfo(long minute) {
            this.minute = minute;
        }
    }
}
