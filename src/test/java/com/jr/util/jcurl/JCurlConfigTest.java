package com.jr.util.jcurl;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JCurlConfigTest {
    @Test
    void testDataFromFile() throws Exception {
        // 1. Create a temporary file with known content
        Path tempFile = Files.createTempFile("test", ".txt");
        String fileContent = "hello world";
        Files.writeString(tempFile, fileContent);

        HttpExecutor client = new HttpExecutor();

        // 2. Create JCurl instance and simulate setting -d @file
        JCurl jcurl = new JCurl(client);
        JCurlOptions options = JCurlOptions.builder().app(jcurl).build();
        options.setData("@" + tempFile.toString()); // assume 'data' is the field holding -d


        // 4. Assert the body equals the file content
        assertEquals(fileContent, client.getBody(options));

        // Clean up temp file
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testSilentOption() {
        HttpExecutor client = new HttpExecutor();
        JCurl jcurl = new JCurl(client);
        JCurlOptions options = JCurlOptions.builder().app(jcurl).silent(true).build();

        assertEquals(true, options.isSilent());
    }
}
