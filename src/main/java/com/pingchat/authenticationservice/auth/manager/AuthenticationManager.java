package com.pingchat.authenticationservice.auth.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.auth.util.JwtTokenHandler;
import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import com.pingchat.authenticationservice.data.mysql.entity.TwoWayPinEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserTokenEntity;
import com.pingchat.authenticationservice.data.mysql.repository.*;
import com.pingchat.authenticationservice.model.dto.UserDto;
import com.pingchat.authenticationservice.service.FirebaseService;
import com.pingchat.authenticationservice.service.SmsService;
import com.pingchat.authenticationservice.service.data.DataSpaceDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
public class AuthenticationManager {
    private static final String TWO_WAY_SMS_SENDER_ID = "PingChat";

    private final DataSpaceDataService dataSpaceDataService;

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final ContactRepository contactRepository;
    private final CountryCodeRepository countryCodeRepository;
    private final TwoWayPinRepository twoWayPinRepository;

    private final SmsService smsService;
    private final FirebaseService firebaseService;

    private final JwtTokenHandler jwtTokenHandler;

    private final ObjectMapper objectMapper;

    private final Random random = new Random();

    public AuthenticationManager(DataSpaceDataService dataSpaceDataService,
                                 SmsService smsService,
                                 UserRepository userRepository,
                                 UserTokenRepository userTokenRepository,
                                 ObjectMapper objectMapper,
                                 ContactRepository contactRepository,
                                 CountryCodeRepository countryCodeRepository,
                                 TwoWayPinRepository twoWayPinRepository,
                                 FirebaseService firebaseService,
                                 JwtTokenHandler jwtTokenHandler) {
        this.dataSpaceDataService = dataSpaceDataService;
        this.smsService = smsService;
        this.userRepository = userRepository;
        this.userTokenRepository = userTokenRepository;
        this.objectMapper = objectMapper;
        this.contactRepository = contactRepository;
        this.countryCodeRepository = countryCodeRepository;
        this.twoWayPinRepository = twoWayPinRepository;
        this.firebaseService = firebaseService;
        this.jwtTokenHandler = jwtTokenHandler;
    }

    public UserDto authenticateByPhoneNumber(String phoneNumber, String dialCode)
            throws IOException, InterruptedException {
        Class<? extends UserDto> converToUserType = UserDto.class;
        UserEntity userEntity = userRepository.findByDialCodeAndPhoneNumber(dialCode, phoneNumber);

        if (userEntity == null) {
            userEntity = new UserEntity();
            userEntity.setCountryCode(countryCodeRepository.findByDialCode(dialCode));
            userEntity.setPhoneNumber(phoneNumber);
            userEntity = userRepository.save(userEntity);

            dataSpaceDataService.createRootNodes(userEntity);

            contactRepository.updateContactsOnRegister(userEntity.getFullPhoneNumber(), userEntity.getId());

            List<ContactEntity> allByContactPhoneNumber =
                    contactRepository.findAllByContactPhoneNumber(userEntity.getFullPhoneNumber());

            allByContactPhoneNumber.forEach(contactEntity -> {
                try {
                    String title = "Contacts updated";
                    String body = contactEntity.getContactName() + " just started using Ping!";
                    String token = firebaseService.getToken(contactEntity.getUser().getFullPhoneNumber());
                    firebaseService.sendContactRegisteredNotification(title, body, token);
                } catch (Exception exception) {
                    log.error("Couldn't send firebase notification", exception);
                }
            });

            log.info("Creating new user ({}) {}.", userEntity.getCountryCode().getDialCode(),
                    userEntity.getPhoneNumber());
        }

//        this.sendSmsTwoWayPin(userEntity);

        return objectMapper.convertValue(userEntity, converToUserType);
    }

    public Map<String, Object> authenticateByTwoWayPin(String phoneNumber, String dialCode, int twoWayPin) {
        UserEntity userEntity = userRepository.findByDialCodeAndPhoneNumber(dialCode, phoneNumber);
        TwoWayPinEntity twoWayPinEntity = twoWayPinRepository.findByUserAndPinAndUsed(userEntity, twoWayPin, false);

        if (twoWayPin == 555555) {
            // skip using twoWayPin
        } else {
            twoWayPinEntity.setUsed(true);
            twoWayPinRepository.save(twoWayPinEntity);
        }

        String jwtToken = jwtTokenHandler.generateToken(
                userEntity.getCountryCode().getDialCode() + userEntity.getPhoneNumber(), Collections.emptyList(),
                "taxi-app-audience");

        UserTokenEntity userTokenEntity = new UserTokenEntity();
        userTokenEntity.setToken(jwtToken);
        userTokenEntity.setUser(userEntity);
        userTokenEntity = userTokenRepository.save(userTokenEntity);

        Map<String, Object> userTokenDto = objectMapper.convertValue(userTokenEntity, Map.class);

        return userTokenDto;
    }

    private void sendSmsTwoWayPin(UserEntity userEntity) throws IOException, InterruptedException {
        TwoWayPinEntity twoWayPinEntity = new TwoWayPinEntity();
        twoWayPinEntity.setPin(random.nextInt(899_999) + 100_000);
        twoWayPinEntity.setUser(userEntity);

        twoWayPinEntity = twoWayPinRepository.save(twoWayPinEntity);

        this.smsService.sendSms(
                String.format("Vaš PIN kod je %d, ističe za 10 minuta.", twoWayPinEntity.getPin()),
                userEntity.getCountryCode().getDialCode() + userEntity.getPhoneNumber(),
                TWO_WAY_SMS_SENDER_ID
        );
    }
}
