package com.jr.util.jcurl;

import picocli.CommandLine.IVersionProvider;

public class JCurlVersionProvider implements IVersionProvider {
    @Override
    public String[] getVersion() {
        Package pkg = JCurlVersionProvider.class.getPackage();
        String version = pkg.getImplementationVersion();
        if (version == null) version = "dev";
        return new String[] { "jcurl " + version };
    }
}
