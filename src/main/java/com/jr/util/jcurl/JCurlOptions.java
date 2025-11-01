package com.jr.util.jcurl;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "jcurl",
        mixinStandardHelpOptions = true,
        version = "jcurl 0.7.0",
        description = "Lightweight curl-like CLI for REST API debugging in Java.")
@Getter
@Setter
@Builder
public class JCurlOptions implements Callable<Integer> {
    @Option(names = {"-X", "--request"}, description = "HTTP method", defaultValue = "GET")
    private String method;

    @Option(names = {"-d", "--data"},
            description = "HTTP request body data, or @file to read from file"
    )
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

    @Parameters(index = "0", paramLabel = "URL", description = "Target URL")
    private String url;

    @Option(names = "--pretty", description = "Pretty-print JSON responses")
    boolean pretty;

    private  JCurl app;

    @Override
    public Integer call() {
        try {
            return app.run(this);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return -1;
        }
    }
}
