package ru.konkin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.function.Function;

public class Handler implements Function<String, String> {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "https://d5dob1n2uv2ss0qvgb76.apigw.yandexcloud.net/give";

    private static HttpURLConnection getCon() {
        URL obj;
        try {
            obj = new URL(GET_URL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        con.setRequestProperty("User-Agent", USER_AGENT);
        return con;
    }

    @Override
    public String apply(String s) {
        final HttpURLConnection con = getCon();
        int responseCode;
        try {
            responseCode = con.getResponseCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode != HttpURLConnection.HTTP_OK) { // success
            System.out.println("GET request did not work.");
        }
        return "Done";
    }

    public static void main(String[] args) {
        Handler handler = new Handler();
        handler.apply("");
    }
}