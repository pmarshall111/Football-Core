package com.petermarshall.scrape;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

public class GetJsonHelper {
    private static String streamToString(InputStream inputStream) {
        String text = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
        return text;
    }

    /*
     * Creates a random base timeout with the possibility of adding an additional, longer, random timeout to avoid
     * uniform timeouts
     */
    private static int getRandomTimeoutMs() {
        Random r = new Random();
        double rDouble = r.nextDouble();
        int randomBound = r.nextInt(5);
        int rInt = r.nextInt(2 + randomBound);
        double baseTimeout = rDouble * rInt;
        boolean wantBigTimeout = r.nextDouble() < 0.27;
        if (wantBigTimeout) {
            int newAddition = r.nextInt(7);
            return Math.max((int) ((baseTimeout + newAddition*0.97)*1000), 104);
        } else {
            return Math.max((int) ((r.nextDouble() + baseTimeout)*1000), 104);
        }
    }

    public static String jsonGetRequest(String urlQueryString) {
        String json = null;

        try {
            Thread.sleep(getRandomTimeoutMs());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
            connection.setRequestProperty("Cookie", "_ga=GA1.2.1590148867.1581583494; _ga_6GDFR2Y61X=GS1.1.1591459295.58.1.1591465787.0; _ym_uid=1587632982108619512; _ym_d=1587632982; __gads=ID=54b755e2cc211c07:T=1587632985:S=ALNI_MYAGoctamRCDG2fcmQFVtEh3fwbiQ; __cfduid=d69838dbb1ac6fcb30a2a4eaca9a1b1db1590656860; _gid=GA1.2.698203822.1591083644; standaloneuser=false; _ym_isad=1; _ym_visorc_54976246=w; _ym_visorc_55064218=w");
            connection.setRequestProperty("Host", "www.sofascore.com");
            connection.setRequestProperty("Referer", "https://www.sofascore.com/tournament/football/russia/premier-liga/203");
            connection.setRequestProperty("TE", "Trailers");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:77.0) Gecko/20100101 Firefox/77.0");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream); // input stream to string
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//        System.out.println(json);
        return json;
    }
}
