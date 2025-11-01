package com.jr.util.jcurl;

import picocli.CommandLine;

import java.io.IOException;

public class JCurl {

    private final HttpExecutor executor;

    public JCurl(HttpExecutor executor) {
        this.executor = executor;
    }

    public static void main(String[] args) {
        //By default, picocli will treat any argument beginning with @ as a “parameter file”
        JCurl app = new JCurl(new HttpExecutor());
        JCurlOptions options = JCurlOptions.builder().app(app).build();
        int exitCode = new CommandLine(options)
                .setExpandAtFiles(false)
                .execute(args);
        System.exit(exitCode);
    }

    public int run(JCurlOptions options) throws IOException, InterruptedException {
        executor.execute(options);
        return 0;
    }
}

