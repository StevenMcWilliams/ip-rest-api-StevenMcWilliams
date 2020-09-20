package com.trillion.ip_rest_api.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Filter to log request payloads received.
 */
@Configuration
public class RequestLoggingFilterConfig {
    /**
     * @return CommonsRequestLoggingFilter instance to use.
     */
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10_000);
        filter.setIncludeHeaders(false);
        return filter;
    }
}
