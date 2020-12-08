package com.pingchat.authenticationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class SmsService {
    private final static String SMS_PROVIDER_API_ENDPOINT = "https://api.sms.to/sms/send";
    private final static String SMS_PROVIDER_API_KEY = "3sfKv85So78p3Ax7Lm570RlcEi6X621I";

    private final ObjectMapper objectMapper;

    public SmsService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void sendSms(String message, String to, String senderId) throws IOException, InterruptedException {
        String smsRequestBodyString = objectMapper.writeValueAsString(Map.of(
                "message", message, // eg Your PIN is 1491251 and expires in 10 minutes.
                "to", to, // eg 38762005152
                "sender_id", senderId // eg TaxiApp Authenticator
        ));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(smsRequestBodyString);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SMS_PROVIDER_API_ENDPOINT))
                .headers(
                        "Content-Type", "application/json",
                        "Authorization", "Bearer " + SMS_PROVIDER_API_KEY
                )
                .POST(bodyPublisher)
                .build();

        // uncomment this for sending real SMS -> otherwise just check the mysql db and copy the pin code, authentication
        // will work properly
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        String pox = "asd";
    }
}
