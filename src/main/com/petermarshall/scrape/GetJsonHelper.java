package com.petermarshall.scrape;

import com.petermarshall.mail.SendEmail;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

import static com.petermarshall.scrape.ScrapeTimeout.getRandomTimeoutMs;

public class GetJsonHelper {
    private static String streamToString(InputStream inputStream) {
        String text = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
        return text;
    }

    /*
     * Method will try 3 times to get JSON from url if given a 5XX response. Else will just try once.
     */
    public static String jsonGetRequest(String urlQueryString) {
        return jsonGetRequest(urlQueryString, 1);
    }
    private static String jsonGetRequest(String urlQueryString, int timesCalled) {
        if (timesCalled >= 3) {
            return null;
        }

        String json = null;
        sleep();
        try {
            URL url = new URL(urlQueryString);
//            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", 3128));
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Cache-control", "max-age=0");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Cookie", "_ga=GA1.2.1590148867.1581583494; _ga_6GDFR2Y61X=GS1.1.1592290450.69.1.1592290458.0; _ym_uid=1587632982108619512; _ym_d=1587632982; __gads=ID=54b755e2cc211c07:T=1587632985:S=ALNI_MYAGoctamRCDG2fcmQFVtEh3fwbiQ; __cfduid=d69838dbb1ac6fcb30a2a4eaca9a1b1db1590656860; standaloneuser=false; _gid=GA1.2.1425151592.1592904337; _ym_isad=1; _ym_visorc_54976246=w; _ym_visorc_55064218=w");
            connection.setRequestProperty("Host", "www.sofascore.com");
            connection.setRequestProperty("Referer", "https://www.sofascore.com");
            connection.setRequestProperty("TE", "Trailers");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:77.0) Gecko/20100101 Firefox/77.0");
            connection.connect();
            int respCode = connection.getResponseCode();
            char firstChar = (""+respCode).charAt(0);
            if (firstChar == '5') {
                //server error, try again.
                System.out.println("Server error on JSON request. Trying again...");
                sleep();
                return jsonGetRequest(urlQueryString, timesCalled+1);
            } else if (respCode == 403) {
                SendEmail.sendOutEmail("ATTN: Scraper banned",
                        "Tried connecting to '" + urlQueryString + "'\n" +connection.getResponseMessage());
                throw new RuntimeException("403 forbidden response code received");
            }
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream); // input stream to string
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Continuing despite the above error.");
        }
//        System.out.println(json);
        return json;
    }

    private static void sleep() {
        try {
            Thread.sleep(getRandomTimeoutMs());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
