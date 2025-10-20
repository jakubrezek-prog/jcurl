package com.jr.util.jcurl;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;

public class JCurl {

    public static void main(String[] args) {
        try {
            JCurlConfig config = CLIParser.parse(args); // parses args & returns config
            HttpExecutor executor = new HttpExecutor(config);
            executor.execute(); // sends request and prints output
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
            System.exit(1);
        }
    }


}
