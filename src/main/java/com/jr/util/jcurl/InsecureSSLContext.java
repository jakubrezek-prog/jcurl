package com.jr.util.jcurl;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class InsecureSSLContext {
    private static SSLContext insecureContext;

    private InsecureSSLContext() {}

    public static synchronized SSLContext get() {
        if (insecureContext == null) {
            try {
                TrustManager[] trustAll = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                            @Override
                            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        }
                };

                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAll, new SecureRandom());
                insecureContext = sc;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create insecure SSL context", e);
            }
        }
        return insecureContext;
    }
}
