package com.jr.util.jcurl;

import java.util.*;
import java.nio.charset.StandardCharsets;

public class CLIParser {
    public static JCurlConfig parse(String[] args) {
        boolean verbose = false;
        boolean insecure = false;
        boolean includeHeaders = false;
        String method = "GET";
        String url = null;
        Map<String,String> headers = new LinkedHashMap<>();
        String body = null;
        String basicAuth = null;

        List<String> argList = Arrays.asList(args);
        for (int i = 0; i < argList.size(); i++) {
            switch (argList.get(i)) {
                case "-v": verbose = true; break;
                case "-s": break; // silent, implement later
                case "-k": insecure = true; break;
                case "-i": includeHeaders = true; break;
                case "-X": method = argList.get(++i); break;
                case "-d": body = argList.get(++i); break;
                case "-H":
                    String[] kv = argList.get(++i).split(":", 2);
                    headers.put(kv[0].trim(), kv[1].trim());
                    break;
                case "-u":
                    String[] creds = argList.get(++i).split(":",2);
                    basicAuth = Base64.getEncoder()
                            .encodeToString((creds[0]+":"+creds[1]).getBytes(StandardCharsets.UTF_8));
                    break;
                default:
                    url = argList.get(i);
            }
        }

        return new JCurlConfig(url, method, headers, body, basicAuth, verbose, insecure, includeHeaders);
    }
}
