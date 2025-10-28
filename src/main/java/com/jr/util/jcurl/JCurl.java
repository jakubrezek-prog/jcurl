package com.jr.util.jcurl;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.*;
import java.util.Base64;


public class JCurl {

    private final HttpExecutor executor;

    public JCurl(HttpExecutor executor) {
        this.executor = executor;
    }

    public static void main(String[] args) {
        //By default, picocli will treat any argument beginning with @ as a “parameter file”
        JCurl app = new JCurl(new HttpExecutor());
        int exitCode = new CommandLine(new JCurlOptions(app))
                .setExpandAtFiles(false)
                .execute(args);
        System.exit(exitCode);
    }

    public int run(JCurlOptions options) throws IOException, InterruptedException {
        JCurlConfig config = buildConfig(options);
        executor.execute(config);

        return 0;
    }


    JCurlConfig buildConfig(JCurlOptions options) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        if (options.getHeaderPairs() != null) {
            for (String h : options.getHeaderPairs()) {
                String[] kv = h.split(":", 2);
                if (kv.length == 2) headers.put(kv[0].trim(), kv[1].trim());
            }
        }

        String encodedAuth = null;
        if (options.getBasicAuth() != null) {
            encodedAuth = Base64.getEncoder().encodeToString(options.getBasicAuth().getBytes());
        }

        String body = options.getData();
        if (body != null && body.startsWith("@")) {
            Path path = Paths.get(body.substring(1));
            body = Files.readString(path, StandardCharsets.UTF_8);
        }

        return new JCurlConfig(
                options.getUrl(),
                options.getMethod(),
                headers,
                body,
                encodedAuth,
                options.isVerbose(),
                options.isInsecure(),
                options.isIncludeHeaders(),
                options.isPretty());
    }
}

