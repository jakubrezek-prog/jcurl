package com.jr.util.jcurl;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;

public class JCurl {
    public static void main(String[] args) throws Exception {
        var argList = Arrays.asList(args);
        if (argList.isEmpty()) {
            System.out.println("Usage: java -jar jcurl.jar [options] <url>");
            return;
        }

        boolean verbose = argList.contains("-v");
        boolean includeHeaders = argList.contains("-i");
        boolean insecure = argList.contains("-k");
        boolean silent = argList.contains("-s");

        String url = argList.get(argList.size() - 1);
        String method = "GET";
        String data = null;
        Map<String, String> headers = new LinkedHashMap<>();
        String basicAuth = null;

        for (int i = 0; i < argList.size(); i++) {
            switch (argList.get(i)) {
                case "-X": method = argList.get(++i); break;
                case "-d": data = argList.get(++i); break;
                case "-H":
                    String[] kv = argList.get(++i).split(":", 2);
                    headers.put(kv[0].trim(), kv[1].trim());
                    break;
                case "-u":
                    String[] creds = argList.get(++i).split(":", 2);
                    String auth = Base64.getEncoder().encodeToString(
                            (creds[0] + ":" + creds[1]).getBytes(StandardCharsets.UTF_8));
                    basicAuth = "Basic " + auth;
                    break;
            }
        }

        HttpClient.Builder builder = HttpClient.newBuilder();
        if (insecure) {
            builder.sslContext(InsecureSSLContext.get()); // trust all SSL
        }
        HttpClient client = builder.build();

        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(url));
        if (data != null) req.method(method, HttpRequest.BodyPublishers.ofString(data));
        else req.method(method, HttpRequest.BodyPublishers.noBody());
        headers.forEach(req::header);
        if (basicAuth != null) req.header("Authorization", basicAuth);

        HttpResponse<String> resp = client.send(req.build(), HttpResponse.BodyHandlers.ofString());

        if (includeHeaders || verbose) {
            System.out.println("HTTP/" + resp.version() + " " + resp.statusCode());
            resp.headers().map().forEach((k, v) -> System.out.println(k + ": " + String.join(", ", v)));
            System.out.println();
        }

        if (!silent) System.out.println(resp.body());
    }
}
