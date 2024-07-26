package com.stevesouza.jdk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Sample program to test that Automon detects calls to IO, Http Requests, and JDBC calls.
 */
public class JdkHelloWorld {

    public int readFile(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        int lineCount = countLines(br);
        br.close();
        return lineCount;

    }

    private int countLines(BufferedReader br) throws IOException {
        int lineCount=0;
        while (br.readLine() != null) {
            lineCount++;
        }
        return lineCount;
    }

    public int readUrl(String url) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
        int lineCount = countLines(br);
        br.close();
        return lineCount;
    }

    public int urlWithConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        connection.getContent();
        return connection.getResponseCode();
    }

    public void jdbcInteractions() throws Exception {
        JdbcInteraction jdbcInteraction = new JdbcInteraction();
        jdbcInteraction.runQueries();
    }


    /**
     * Read a file, execute http requests, and make jdbc calls.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String fileName=args[0];
        JdkHelloWorld helloWorld = new JdkHelloWorld();
        System.out.println("   ***"+fileName+" line count="+helloWorld.readFile(fileName));

        String url = "https://www.google.com";
        System.out.println("   ***"+url+" line count="+helloWorld.readUrl(url));

        url =  "http://example.com";
        System.out.println("   ***"+url+" status code="+helloWorld.urlWithConnection(url));

        System.out.println("   *** jdbc interactions");
        helloWorld.jdbcInteractions();
    }
}
