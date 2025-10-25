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

        // 2. Create JCurl instance and simulate setting -d @file
        JCurl jcurl = new JCurl();
        jcurl.setData("@" + tempFile.toString()); // assume 'data' is the field holding -d

        // 3. Build config
        JCurlConfig config = jcurl.buildConfig();

        // 4. Assert the body equals the file content
        assertEquals(fileContent, config.body());

        // Clean up temp file
        Files.deleteIfExists(tempFile);
    }
}
