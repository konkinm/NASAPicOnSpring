package ru.konkin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
public class App 
{

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "https://d5dob1n2uv2ss0qvgb76.apigw.yandexcloud.net/give";

    public static void main(String[] args) throws IOException {
        sendGET();
        System.out.println("GET DONE");
    }

    private static void sendGET() throws IOException {
        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            System.out.println("200 OK");
        } else {
            System.err.println(responseCode);
        }
    }
}