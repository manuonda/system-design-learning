package com.spring4.version.config;



public class ApiVersionParser implements org.springframework.web.accept.ApiVersionParser {

    @Override
    public Comparable parseVersion(String version) {
        //Remove "v" prefix if present
        if(version.startsWith("v") || version.startsWith("V")){
            version = version.substring(1);
        }

        //Add .0 if it's a just a single number(1 becomes 1.0, 2 becomes 2.0)
        if(!version.contains(".")) {
            version = version + ".0";
        }
        return version;
    }
}
