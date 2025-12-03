package com.jr.util.jcurl;

import java.io.PrintStream;
import java.net.http.HttpResponse;

public class ResponsePrinter {

    private static final String APPLICATION_JSON = "application/json";

    private final PrintStream out;

    public ResponsePrinter(PrintStream out) {
        this.out = out;
    }

    public ResponsePrinter() {
        this(System.out);
    }

    public void printResponse(JCurlOptions config, HttpResponse<String> response) {
        if (config.isIncludeHeaders() || config.isVerbose()) {
            printStatusLine(response);
            printHeaders(response);
            out.println();
        }

        String body = response.body();
        if (config.isPretty() && isJsonResponse(response)) {
            body = prettyPrintJson(body);
        }
        printBody(body);
    }

    private void printStatusLine(HttpResponse<String> response) {
        out.println("HTTP/" + response.version() + " " + response.statusCode());
    }

    private void printHeaders(HttpResponse<String> response) {
        response.headers().map().forEach((k, v) -> out.println(k + ": " + String.join(",", v)));
    }

    private void printBody(String body) {
        out.println(body);
    }

    private boolean isJsonResponse(HttpResponse<?> response) {
        return response.headers()
                .firstValue("Content-Type")
                .map(v -> v.contains(APPLICATION_JSON))
                .orElse(false);
    }

    private String prettyPrintJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Object obj = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return json;
        }
    }
}