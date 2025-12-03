package com.jr.util.jcurl;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.net.http.HttpResponse;

public class JCurlExecutorTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setupServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
                .port(8089)
                .httpsPort(8443)
                .keystorePath("src/test/resources/keystore.jks")
                .keystorePassword("password")
                .keyManagerPassword("password"));
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



    @Test
    void testSimpleGet() throws Exception {
        stubFor(get("/ping").willReturn(aResponse()
                .withStatus(200)
                .withBody("pong")));

        JCurlOptions options = JCurlOptions.builder().
                url("http://localhost:8089/ping").
                method("GET").
                build();

        HttpResponse<String> response = new HttpExecutor().execute(options);

        assertEquals(200, response.statusCode());
        assertEquals("pong", response.body());
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

        String[] headers = {"Content-Type: application/json"};

        JCurlOptions options = JCurlOptions.builder().
                url("http://localhost:8089/echo").
                method("POST").
                data("{\"name\":\"John Doe\"}").
                headerPairs(headers).
                build();

        HttpResponse<String> response = new HttpExecutor().execute(options);

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("ok"));
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

        HttpResponse<String> response = new HttpExecutor().execute(options);

        assertEquals(200, response.statusCode());
        assertEquals("Access granted", response.body());
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

        HttpResponse<String> response = new HttpExecutor().execute(options);

        assertEquals(200, response.statusCode());
        assertEquals("bodytext", response.body());
        assertTrue(response.headers().firstValue("x-test").isPresent());
        assertEquals("HeaderValue", response.headers().firstValue("x-test").get());
    }


    @Test
    void testInsecureFlagCreatesClient() throws Exception {
        // Test insecure HTTPS connection with self-signed certificate
        stubFor(get("/secure-ping").willReturn(aResponse()
                .withStatus(200)
                .withBody("secure-pong")));

        // Test with insecure=true - should succeed
        JCurlOptions insecureOptions = JCurlOptions.builder().
                url("https://localhost:8443/secure-ping").
                method("GET").
                insecure(true).
                build();

        HttpResponse<String> response = new HttpExecutor().execute(insecureOptions);

        assertEquals(200, response.statusCode());
        assertEquals("secure-pong", response.body());
        verify(getRequestedFor(urlEqualTo("/secure-ping")));
    }

    @Test
    void testInsecureFlagFalseThrowsSSLException() throws Exception {
        // Test insecure HTTPS connection with self-signed certificate
        stubFor(get("/secure-ping").willReturn(aResponse()
                .withStatus(200)
                .withBody("secure-pong")));

        // Test with insecure=false - should throw SSL exception
        JCurlOptions secureOptions = JCurlOptions.builder().
                url("https://localhost:8443/secure-ping").
                method("GET").
                insecure(false).
                build();

        assertThrows(Exception.class, () -> new HttpExecutor().execute(secureOptions));
    }

    @Test
    void testGetHeadersValidHeaders() {
        HttpExecutor executor = new HttpExecutor();
        JCurlOptions options = JCurlOptions.builder()
                .headerPairs(new String[]{"Content-Type: application/json", "Authorization: Bearer token123"})
                .build();

        Map<String, String> headers = executor.getHeaders(options);

        assertEquals(2, headers.size());
        assertEquals("application/json", headers.get("Content-Type"));
        assertEquals("Bearer token123", headers.get("Authorization"));
    }

    @Test
    void testGetHeadersWithColonsInValue() {
        HttpExecutor executor = new HttpExecutor();
        JCurlOptions options = JCurlOptions.builder()
                .headerPairs(new String[]{"Location: https://example.com:8080/path"})
                .build();

        Map<String, String> headers = executor.getHeaders(options);

        assertEquals(1, headers.size());
        assertEquals("https://example.com:8080/path", headers.get("Location"));
    }

    @Test
    void testGetHeadersNullHeaderPairs() {
        HttpExecutor executor = new HttpExecutor();
        JCurlOptions options = JCurlOptions.builder().build();

        Map<String, String> headers = executor.getHeaders(options);

        assertTrue(headers.isEmpty());
    }

    @Test
    void testGetHeadersEmptyArray() {
        HttpExecutor executor = new HttpExecutor();
        JCurlOptions options = JCurlOptions.builder()
                .headerPairs(new String[]{})
                .build();

        Map<String, String> headers = executor.getHeaders(options);

        assertTrue(headers.isEmpty());
    }

    @Test
    void testGetHeadersSkipsEmptyStrings() {
        HttpExecutor executor = new HttpExecutor();
        JCurlOptions options = JCurlOptions.builder()
                .headerPairs(new String[]{"", "  ", "Content-Type: application/json"})
                .build();

        Map<String, String> headers = executor.getHeaders(options);

        assertEquals(1, headers.size());
        assertEquals("application/json", headers.get("Content-Type"));
    }

    @Test
    void testGetHeadersInvalidFormatNoColon() {
        HttpExecutor executor = new HttpExecutor();
        JCurlOptions options = JCurlOptions.builder()
                .headerPairs(new String[]{"Content-Type"})
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> executor.getHeaders(options));
        assertTrue(exception.getMessage().contains("Invalid header format"));
        assertTrue(exception.getMessage().contains("Content-Type"));
    }

    @Test
    void testGetHeadersInvalidFormatEmptyKey() {
        HttpExecutor executor = new HttpExecutor();
        JCurlOptions options = JCurlOptions.builder()
                .headerPairs(new String[]{":application/json"})
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> executor.getHeaders(options));
        assertTrue(exception.getMessage().contains("Invalid header format"));
        assertTrue(exception.getMessage().contains(":application/json"));
    }

    @Test
    void testGetHeadersInvalidFormatOnlyColon() {
        HttpExecutor executor = new HttpExecutor();
        JCurlOptions options = JCurlOptions.builder()
                .headerPairs(new String[]{":"})
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> executor.getHeaders(options));
        assertTrue(exception.getMessage().contains("Invalid header format"));
    }

}
