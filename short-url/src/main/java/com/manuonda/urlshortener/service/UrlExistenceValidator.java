package com.manuonda.urlshortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Service responsible for validating the existence of URLs.
 */
public class UrlExistenceValidator {

    private static final Logger log = LoggerFactory.getLogger(UrlExistenceValidator.class);

    public static boolean isUrlExists(String urlString){
        try{

            log.debug("Checking if url exists: " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000); // 5 seconds timeout
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            return ( responseCode >= 200 && responseCode < 400 ); // Consider 2xx and 3xx response codes as valid

        } catch (Exception e) {
            log.error("Error while checking URL existence {}{} ",e);
            return false;
        }
    }
}
