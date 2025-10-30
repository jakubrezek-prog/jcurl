package com.jr.util.jcurl;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.io.*;

public class JCurlExecutorTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setupServer() {
        wireMockServer = new WireMockServer(8089); // pick a port
        wireMockServer.start();
        configureFor("localhost", 8089);
    }

    @AfterAll
    static void stopServer() {
        if (wireMockServer != null) wireMockServer.stop();
    }

    @BeforeEach
    void resetServer() {
        wireMockServer.resetAll();
    }

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return out.toString();
    }


    @Test
    void testSimpleGet() throws Exception {
        stubFor(get("/ping").willReturn(aResponse()
                .withStatus(200)
                .withBody("pong")));

        JCurlOptions options = JCurlOptions.builder().
                url("http://localhost:8089/ping").
                method("GET").
                build();

        String out = captureOutput(() -> {
            try {
                new HttpExecutor().execute(options);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertTrue(out.contains("pong"));
        verify(getRequestedFor(urlEqualTo("/ping")));
    }

    @Test
    void testPostJsonBody() throws Exception {
        stubFor(post("/echo")
                .withRequestBody(equalToJson("{\"name\":\"John Doe\"}"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":\"ok\"}")));

        String[] headers = {"Content-Type", "application/json"};

        JCurlOptions options = JCurlOptions.builder().
                url("http://localhost:8089/echo").
                method("POST").
                data("{\"name\":\"John Doe\"}").
                headerPairs(headers).
                build();

        String out = captureOutput(() -> {
            try {
                new HttpExecutor().execute(options);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertTrue(out.contains("ok"));
        verify(postRequestedFor(urlEqualTo("/echo"))
                .withRequestBody(equalToJson("{\"name\":\"John Doe\"}")));
    }

    @Test
    void testBasicAuthHeader() throws Exception {
        stubFor(get("/secure")
                .withHeader("Authorization", equalTo("Basic YWRtaW46c2VjcmV0")) // admin:secret
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Access granted")));

        JCurlOptions options = JCurlOptions.builder().
                url("http://localhost:8089/secure").
                method("GET").
                basicAuth("admin:secret").
                build();

        String out = captureOutput(() -> {
            try {
                new HttpExecutor().execute(options);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertTrue(out.contains("Access granted"));
        verify(getRequestedFor(urlEqualTo("/secure"))
                .withHeader("Authorization", matching("Basic .*")));
    }


    @Test
    void testVerboseOutputIncludesHeaders() throws Exception {
        stubFor(get("/headers").willReturn(aResponse()
                .withStatus(200)
                .withHeader("X-Test", "HeaderValue")
                .withBody("bodytext")));



        JCurlOptions options = JCurlOptions.builder().
                url("http://localhost:8089/headers").
                method("GET").
                verbose(true).
                includeHeaders(true).
                build();

        String out = captureOutput(() -> {
            try {
                new HttpExecutor().execute(options);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertTrue(out.contains("HTTP/"));
        assertTrue(out.toLowerCase().contains("x-test"));
        assertTrue(out.contains("bodytext"));
    }


    @Test
    void testInsecureFlagCreatesClient() throws Exception {
        // Not truly testing SSL here, just ensuring it doesn’t crash
        System.out.println("=================");

        JCurlOptions options = JCurlOptions.builder().
                url("https://example.com").
                method("GET").
                insecure(true).
                build();

        assertDoesNotThrow(() -> new HttpExecutor().execute(options));
        System.out.println("#######################");
    }

}
