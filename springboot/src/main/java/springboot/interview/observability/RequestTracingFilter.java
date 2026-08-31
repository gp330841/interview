package springboot.interview.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

@Component
public class RequestTracingFilter extends HttpFilter {

    private static final String REQUEST_ID = "requestId";
    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        try {
            String requestId = req.getHeader("X-Request-Id");
            if (requestId == null || requestId.isBlank()) {
                requestId = UUID.randomUUID().toString();
            }
            MDC.put(REQUEST_ID, requestId);

            // Get the current OpenTelemetry Span and extract trace/span ids
            Span current = Span.current();
            SpanContext ctx = current.getSpanContext();
            if (ctx.isValid()) {
                MDC.put(TRACE_ID, ctx.getTraceId());
                MDC.put(SPAN_ID, ctx.getSpanId());
                res.setHeader("traceparent", String.format("00-%s-%s-01", ctx.getTraceId(), ctx.getSpanId()));
            }

            res.setHeader("X-Request-Id", requestId);

            chain.doFilter(req, res);
        } finally {
            MDC.remove(REQUEST_ID);
            MDC.remove(TRACE_ID);
            MDC.remove(SPAN_ID);
        }
    }
}
