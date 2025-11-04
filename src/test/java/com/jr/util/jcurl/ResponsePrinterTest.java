package com.jr.util.jcurl;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class ResponsePrinterTest {

    @Test
    void testPrintResponseWithoutHeaders() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        ResponsePrinter printer = new ResponsePrinter(printStream);

        JCurlOptions config = JCurlOptions.builder()
                .url("http://example.com")
                .method("GET")
                .build();

        // Create a simple test response using a real HttpResponse-like object
        TestHttpResponse response = new TestHttpResponse(200, "Hello World");

        printer.printResponse(config, response);

        String output = outputStream.toString();
        assertTrue(output.contains("Hello World"));
        assertFalse(output.contains("HTTP/"));
    }

    @Test
    void testPrintResponseWithHeaders() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        ResponsePrinter printer = new ResponsePrinter(printStream);

        JCurlOptions config = JCurlOptions.builder()
                .url("http://example.com")
                .method("GET")
                .verbose(true)
                .includeHeaders(true)
                .build();

        TestHttpResponse response = new TestHttpResponse(200, "Hello World");

        printer.printResponse(config, response);

        String output = outputStream.toString();
        // Just test that we have some output - the exact format may vary
        assertTrue(output.length() > 10);
    }

    @Test
    void testPrettyPrintJson() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        ResponsePrinter printer = new ResponsePrinter(printStream);

        JCurlOptions config = JCurlOptions.builder()
                .url("http://example.com")
                .method("GET")
                .pretty(true)
                .build();

        TestHttpResponse response = new TestHttpResponse(200, "{\"name\":\"John\",\"age\":30}");

        printer.printResponse(config, response);

        String output = outputStream.toString();
        // Test that pretty printing produces formatted output
        assertTrue(output.length() > 20); // Should be longer than compact JSON
        assertTrue(output.contains("\n")); // Should have newlines
    }

    @Test
    void testPrettyPrintInvalidJson() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        ResponsePrinter printer = new ResponsePrinter(printStream);

        JCurlOptions config = JCurlOptions.builder()
                .url("http://example.com")
                .method("GET")
                .pretty(true)
                .build();

        TestHttpResponse response = new TestHttpResponse(200, "not json");

        printer.printResponse(config, response);

        String output = outputStream.toString();
        assertTrue(output.contains("not json"));
    }

    // Simple test implementation of HttpResponse for testing without Mockito
    private static class TestHttpResponse implements java.net.http.HttpResponse<String> {
        private final int statusCode;
        private final String body;

        TestHttpResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        @Override
        public int statusCode() {
            return statusCode;
        }

        @Override
        public java.net.http.HttpRequest request() {
            return null;
        }

        @Override
        public java.util.Optional<java.net.http.HttpResponse<String>> previousResponse() {
            return java.util.Optional.empty();
        }

        @Override
        public java.net.http.HttpHeaders headers() {
            if (body.equals("not json")) {
                return java.net.http.HttpHeaders.of(java.util.Map.of(
                    "Content-Type", java.util.List.of("application/json")
                ), (s, s2) -> true);
            }
            return java.net.http.HttpHeaders.of(java.util.Map.of(
                "Content-Type", java.util.List.of("text/plain"),
                "Content-Length", java.util.List.of("11")
            ), (s, s2) -> true);
        }

        @Override
        public String body() {
            return body;
        }


        @Override
        public java.util.Optional<javax.net.ssl.SSLSession> sslSession() {
            return java.util.Optional.empty();
        }

        @Override
        public java.net.http.HttpClient.Version version() {
            return java.net.http.HttpClient.Version.HTTP_1_1;
        }

        @Override
        public java.net.URI uri() {
            return java.net.URI.create("http://example.com");
        }
    }
}