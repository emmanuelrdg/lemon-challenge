package com.lemon.challenge.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@WebFilter(urlPatterns = "/message")
@Setter
public class ThrottlingFilter extends HttpFilter {
    @Value("${rate-configuration.max-request-rate-limit}")
    private int maxRequestRateLimit;
    @Value("${rate-configuration.period-time}")
    private int periodTime;

    private LoadingCache<String, Set<LocalDateTime>> requestCountsPerUser;

    public ThrottlingFilter() {
        super();
        requestCountsPerUser = CacheBuilder.newBuilder().
                expireAfterWrite(10, TimeUnit.SECONDS).build(new CacheLoader<String, Set<LocalDateTime>>() {
            public Set<LocalDateTime> load(String key) {
                return Collections.newSetFromMap(new LinkedHashMap<>() {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<LocalDateTime, Boolean> eldest) {
                        return size() > maxRequestRateLimit;
                    }
                });
            }
        });
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String userId = httpServletRequest.getHeader("user-id");
        if (userId == null) {
            userId = httpServletRequest.getSession(true).getId();
        }


        if (isMaximumRequestsExceeded(userId)) {

            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.getWriter().write("Too many requests");
            return;
        }

        chain.doFilter(request, response);
    }


    private boolean isMaximumRequestsExceeded(String userKey) {
        Set<LocalDateTime> accessSet = null;
        try {
            accessSet = requestCountsPerUser.get(userKey);
            LocalDateTime lastRecentlyAcceded = (LocalDateTime) accessSet.stream().findFirst().orElse(LocalDateTime.now());
            int size = accessSet.size();
            log.info("--->ID {}", userKey);
            log.info("--->Windows size {}", accessSet.size());
            log.info("--->Current time {}", LocalDateTime.now().toString());
            log.info("--->First Element {}" , lastRecentlyAcceded.toString());
            log.info("--->Seconds Elapsed {}", lastRecentlyAcceded.until(LocalDateTime.now(), ChronoUnit.SECONDS));
            log.info("--->Windows Elements {} ", accessSet);
            if (size >= maxRequestRateLimit && lastRecentlyAcceded.until(LocalDateTime.now(), ChronoUnit.SECONDS) < 10) {
                log.debug("MaximumRequestsPerSecondExceeded");
                return true;
            }
        } catch (ExecutionException e) {
            log.error("ExecutionException when access  Cache", e);
            accessSet = Collections.newSetFromMap(new LinkedHashMap<>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<LocalDateTime, Boolean> eldest) {
                    return size() > maxRequestRateLimit;
                }
            });
        }
        accessSet.add(LocalDateTime.now());
        requestCountsPerUser.put(userKey, accessSet);
        return false;
    }


}
