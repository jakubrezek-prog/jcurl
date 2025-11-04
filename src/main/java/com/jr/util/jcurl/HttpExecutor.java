package com.jr.util.jcurl;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.*;
import java.net.URI;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpExecutor {

    private final ResponsePrinter printer;

    public HttpExecutor() {
        this(new ResponsePrinter());
    }

    public HttpExecutor(ResponsePrinter printer) {
        this.printer = printer;
    }

    public HttpResponse<String> execute(JCurlOptions config) throws IOException, InterruptedException {
        HttpClient.Builder clientBuilder = HttpClient.newBuilder();
        if (config.isInsecure()) {
            clientBuilder.sslContext(InsecureSSLContext.get());
        }
        HttpClient client = clientBuilder.build();

        String payload = getBody(config);
        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(config.getUrl()))
                .method(config.getMethod(),
                        payload == null ?
                                HttpRequest.BodyPublishers.noBody() :
                                HttpRequest.BodyPublishers.ofString(payload));

        Map<String, String> headers = getHeaders(config);
        headers.forEach(req::header);
        if (config.getBasicAuth() != null) {
            req.header("Authorization", "Basic " + getAuth(config));
        }

        HttpResponse<String> resp = client.send(req.build(), HttpResponse.BodyHandlers.ofString());

        printer.printResponse(config, resp);

        return resp;
    }


    String getBody(JCurlOptions options) throws IOException {
        String body = options.getData();
        if (body != null && body.startsWith("@")) {
            Path path = Paths.get(body.substring(1));
            body = Files.readString(path, StandardCharsets.UTF_8);
        }
        return body;
    }

    private String getAuth(JCurlOptions options) {
        String encodedAuth = null;
        if (options.getBasicAuth() != null) {
            encodedAuth = Base64.getEncoder().encodeToString(options.getBasicAuth().getBytes());
        }
        return encodedAuth;
    }

    private Map<String, String> getHeaders(JCurlOptions options) {
        Map<String, String> headers = new LinkedHashMap<>();
        if (options.getHeaderPairs() != null) {
            for (String h : options.getHeaderPairs()) {
                String[] kv = h.split(":", 2);
                if (kv.length == 2) headers.put(kv[0].trim(), kv[1].trim());
            }
        }
        return headers;
    }

}
