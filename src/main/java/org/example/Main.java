package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;

public class Main {
    public static final String REMOTE_SERVICE_URL = "https://api.nasa.gov/planetary/apod?api_key=Rch1svH8ZxhEyPNNLp9nFWvW5FfcL9Y8acTxHMfX";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        //Создаем клиента
        CloseableHttpClient httpClient = createHttpClient();
        //Создаем запрос
        HttpGet request = createRequest(REMOTE_SERVICE_URL);
        //получаем ответ
        CloseableHttpResponse response = httpClient.execute(request);
        //создаем объект
        NASA nasa = mapper.readValue(response.getEntity().getContent(),
                new TypeReference<NASA>() {
                });
        //получаем ссылку на файл из поля объекта
        final String NASA_PICTURE_URL = nasa.getUrl();
        //записываем имя файла из url
        String fileName = Paths.get(new URL(NASA_PICTURE_URL).getPath()).getFileName().toString();
        //создаем файл
        saveUrl(fileName, NASA_PICTURE_URL);
    }

    //метод создания http клиента
    private static CloseableHttpClient createHttpClient() {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .build())
                .build();
        return httpClient;
    }

    //метод создания запроса
    private static HttpGet createRequest(String url) {
        HttpGet request = new HttpGet(url);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        return request;
    }

    //метод сохранения файла
    private static void saveUrl(String fileName, String url) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream out = new FileOutputStream(fileName)) {
            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}