package com.jr.util.jcurl;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;
import java.util.*;
import java.util.Base64;

@Command(name = "jcurl",
        mixinStandardHelpOptions = true,
        version = "jcurl 0.2",
        description = "Lightweight curl-like CLI for REST API debugging in Java.")
public class JCurl implements Callable<Integer> {

    @Option(names = {"-X", "--request"}, description = "HTTP method", defaultValue = "GET")
    private String method;

    @Option(names = {"-d", "--data"}, description = "Request body")
    private String data;

    @Option(names = {"-H", "--header"}, description = "HTTP headers", split = ",")
    private String[] headerPairs;

    @Option(names = {"-u", "--user"}, description = "Basic auth username:password")
    private String basicAuth;

    @Option(names = {"-v", "--verbose"}, description = "Verbose output")
    private boolean verbose;

    @Option(names = {"-i", "--include"}, description = "Include response headers")
    private boolean includeHeaders;

    @Option(names = {"-k", "--insecure"}, description = "Ignore SSL certificate errors")
    private boolean insecure;

    @Parameters(paramLabel = "URL", description = "Target URL")
    private String url;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new JCurl()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        JCurlConfig config = buildConfig();
        HttpExecutor executor = new HttpExecutor(config);
        executor.execute();
        return 0;
    }

    private JCurlConfig buildConfig() {
        Map<String, String> headers = new LinkedHashMap<>();
        if (headerPairs != null) {
            for (String h : headerPairs) {
                String[] kv = h.split(":", 2);
                if (kv.length == 2) headers.put(kv[0].trim(), kv[1].trim());
            }
        }

        String encodedAuth = null;
        if (basicAuth != null) {
            encodedAuth = Base64.getEncoder().encodeToString(basicAuth.getBytes());
        }

        return new JCurlConfig(
                url,
                method,
                headers,
                data,
                encodedAuth,
                verbose,
                insecure,
                includeHeaders
        );
    }
}

