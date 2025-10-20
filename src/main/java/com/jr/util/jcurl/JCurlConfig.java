package com.jr.util.jcurl;

import java.util.Map;

public record JCurlConfig(
        String url,
        String method,
        Map<String,String> headers,
        String body,
        String basicAuth,
        boolean verbose,
        boolean insecure,
        boolean includeHeaders) {}
