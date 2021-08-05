package com.lemon.challenge.security;

import io.github.bucket4j.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Duration;

/**
 * Alternative to Throttling algorithm, currently disabled
 */
//@Component
@Slf4j
public class BucketFilter implements Filter {

    private Bucket createNewBucket() {

        Refill refill = Refill.greedy(5, Duration.ofSeconds(10));
        Bandwidth limit = Bandwidth.classic(5, refill);
        //Bandwidth limit = Bandwidth.simple(5, Duration.ofSeconds(10));

        return Bucket4j.builder()
                .addLimit(limit)
                .build();

    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(true);

        String appKey = session.getId();
        Bucket bucket = (Bucket) session.getAttribute("throttler-" + appKey);

        if (bucket == null) {
            bucket = createNewBucket();
            session.setAttribute("throttler-" + appKey, bucket);
            log.info("---FirstAccess {}",appKey);
        }

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            // the limit is not exceeded
            log.info("---Remaining Tokens {}",probe.getRemainingTokens());
            chain.doFilter(request, response);
        } else {
            // limit is exceeded
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("text/plain");
            httpResponse.setStatus(429);
            httpResponse.getWriter().append("Too many requests");
        }
    }


}
