package com.jr.util.jcurl;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "jcurl",
        mixinStandardHelpOptions = true,
        version = "jcurl 0.5.0",
        description = "Lightweight curl-like CLI for REST API debugging in Java.")
public class JCurlOptions implements Callable<Integer> {
    @Option(names = {"-X", "--request"}, description = "HTTP method", defaultValue = "GET")
    private String method;

    @Option(names = {"-d", "--data"},
            description = "HTTP request body data, or @file to read from file"
    )
    private String data;

    public void setData(String data) {
        this.data = data;
    }

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

    @Parameters(index = "0", paramLabel = "URL", description = "Target URL")
    private String url;

    @Option(names = "--pretty", description = "Pretty-print JSON responses")
    boolean pretty;

    private final JCurl app;

    public JCurlOptions(JCurl app) {
        this.app = app;
    }

    @Override
    public Integer call() {
        try {
            return app.run(this);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getData() {
        return data;
    }

    public String[] getHeaderPairs() {
        return headerPairs;
    }

    public void setHeaderPairs(String[] headerPairs) {
        this.headerPairs = headerPairs;
    }

    public String getBasicAuth() {
        return basicAuth;
    }

    public void setBasicAuth(String basicAuth) {
        this.basicAuth = basicAuth;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public boolean isInsecure() {
        return insecure;
    }

    public void setInsecure(boolean insecure) {
        this.insecure = insecure;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPretty() {
        return pretty;
    }

    public void setPretty(boolean pretty) {
        this.pretty = pretty;
    }
}
