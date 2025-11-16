package org.tpibackend.mstransportes.logging;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TraceIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TraceIdFilter.class);

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String incomingTraceId = request.getHeader(LoggingConstants.TRACE_ID_HEADER);
        String traceId = StringUtils.hasText(incomingTraceId)
            ? incomingTraceId
            : UUID.randomUUID().toString();

        MDC.put(LoggingConstants.TRACE_ID_HEADER, traceId);
        response.setHeader(LoggingConstants.TRACE_ID_HEADER, traceId);
        log.debug("trace-id initialized for request path {}", request.getRequestURI());
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(LoggingConstants.TRACE_ID_HEADER);
        }
    }
}
