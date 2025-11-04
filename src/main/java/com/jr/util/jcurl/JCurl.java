package com.jr.util.jcurl;

import picocli.CommandLine;

import java.io.IOException;
import java.net.http.HttpResponse;

public class JCurl {

    private final HttpExecutor executor;
    private final ResponsePrinter printer;

    public JCurl(HttpExecutor executor) {
        this(executor, new ResponsePrinter());
    }

    public JCurl(HttpExecutor executor, ResponsePrinter printer) {
        this.executor = executor;
        this.printer = printer;
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
        HttpResponse<String> response = executor.execute(options);
        printer.printResponse(options, response);
        return 0;
    }
}

