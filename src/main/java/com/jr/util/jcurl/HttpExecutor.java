package com.jr.util.jcurl;

import java.net.http.*;
import java.net.URI;

public class HttpExecutor {

    private final JCurlConfig config;

    public HttpExecutor(JCurlConfig config) {
        this.config = config;
    }

    public void execute() throws Exception {
        HttpClient.Builder clientBuilder = HttpClient.newBuilder();
        if (config.insecure()) {
            clientBuilder.sslContext(InsecureSSLContext.get());
        }
        HttpClient client = clientBuilder.build();

        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(config.url()))
                .method(config.method(),
                        config.body() == null ?
                                HttpRequest.BodyPublishers.noBody() :
                                HttpRequest.BodyPublishers.ofString(config.body()));

        config.headers().forEach(req::header);
        if (config.basicAuth() != null) req.header("Authorization", "Basic " + config.basicAuth());

        HttpResponse<String> resp = client.send(req.build(), HttpResponse.BodyHandlers.ofString());

        if (config.includeHeaders() || config.verbose()) {
            System.out.println("HTTP/" + resp.version() + " " + resp.statusCode());
            resp.headers().map().forEach((k, v) -> System.out.println(k + ": " + String.join(",", v)));
            System.out.println();
        }

        System.out.println(resp.body());
    }
}
