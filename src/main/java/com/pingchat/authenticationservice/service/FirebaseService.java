package com.pingchat.authenticationservice.service;

import com.google.firebase.messaging.*;
import com.pingchat.authenticationservice.enums.MessageType;
import com.pingchat.authenticationservice.model.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FirebaseService {
    private static final Map<String, String> userFirebaseTokens = new HashMap<>();

    private final FirebaseMessaging firebaseMessaging;

    public FirebaseService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public String getToken(String userPhoneNumber) {
        return userFirebaseTokens.get(userPhoneNumber);
    }

    public void registerToken(String userPhoneNumber, String token) {
        userFirebaseTokens.put(userPhoneNumber, token);
    }

    public void sendContactRegisteredNotification(String title, String body, String receiverFirebaseToken)
            throws FirebaseMessagingException {
        AndroidNotification notification = AndroidNotification.builder()
                .setTitle(title)
                .setBody(body)
                .setIcon("ping_full_launcher_round")
                .build();

        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(notification)
                        .putAllData(Map.of(
                                "click_action", "FLUTTER_CONTACT_REGISTERED"
                        ))
                        .build())
                .setToken(receiverFirebaseToken)
                .build();

        log.info("Sending firebase message(receiver={}, message={})", receiverFirebaseToken, message);
        firebaseMessaging.send(message);
    }

    public void sendCallNotification(String senderContactName, String senderPhoneNumber, String receiverPhoneNumber) {
        try {
            String receiverFirebaseToken = userFirebaseTokens.get(receiverPhoneNumber);
            if (receiverFirebaseToken != null) {
                AndroidNotification notification = AndroidNotification.builder()
                        .setTitle("Incoming call")
                        .setBody("Call from " + senderContactName + " " + senderPhoneNumber)
                        .setIcon("ping_full_launcher_round")
                        .build();

                Message message = Message.builder()
                        .setAndroidConfig(AndroidConfig.builder()
                                .setNotification(notification)
                                .putAllData(Map.of(
                                        "click_action", "FLUTTER_INCOMING_CALL",
                                        "senderPhoneNumber", senderPhoneNumber
                                ))
                                .build())
                        .setToken(receiverFirebaseToken)
                        .build();

                log.info("Sending firebase call notification(receiver={}, message={})", receiverPhoneNumber, message);
                firebaseMessaging.send(message);
            } else {
                log.warn("Firebase token for receiver not found");
            }
        } catch (Exception exception) {
            log.error("Error sending call notifications", exception);
        }
    }

    public void sendMessageNotification(MessageDto messageDto) {
        try {
            String receiverPhoneNumber = messageDto.getReceiver().getFullPhoneNumber();

            String receiverFirebaseToken = userFirebaseTokens.get(receiverPhoneNumber);
            if (receiverFirebaseToken != null) {

                String title = messageDto.getSenderContactName();
                String senderProfileImageUrl = messageDto.getSender().getProfileImagePath();
                MessageType messageType = messageDto.getMessageType();

                String body;
                if (messageType.equals(MessageType.TEXT_MESSAGE)) {
                    body = messageDto.getText();
                } else if (messageType.equals(MessageType.RECORDING)) {
                    body = "Recording";
                } else if (messageType.equals(MessageType.STICKER)) {
                    body = "Sticker";
                } else if (messageType.equals(MessageType.GIF)) {
                    body = "GIF";
                } else if (messageType.equals(MessageType.MAP_LOCATION)) {
                    body = "Location";
                } else if (messageType.equals(MessageType.CALL_INFO)) {
                    body = "Call";
                } else {
                    body = "Media";
                }

                AndroidNotification notification = AndroidNotification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .setIcon("ping_full_launcher_round")
                        .setTag(messageDto.getSender().getPhoneNumber())
                        .setImage(messageDto.getFileUrl())
                        .build();

                Message message = Message.builder()
                        .setAndroidConfig(AndroidConfig.builder()
                                .setNotification(notification)
                                .putAllData(Map.of(
                                        "click_action", "FLUTTER_NEW_MESSAGE"
                                ))
                                .build())
                        .setToken(receiverFirebaseToken)
                        .build();

                log.info("Sending firebase message(receiver={}, message={})", receiverPhoneNumber, message);
                firebaseMessaging.send(message);
            }
        } catch (Exception exception) {
            log.error("Couldn't send message notification", exception);
        }
    }
}
