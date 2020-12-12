package com.pingchat.authenticationservice.util.seeder;

import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import com.pingchat.authenticationservice.data.mysql.entity.MessageEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.repository.ContactRepository;
import com.pingchat.authenticationservice.data.mysql.repository.CountryCodeRepository;
import com.pingchat.authenticationservice.data.mysql.repository.MessageRepository;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
public class ChatSeeder implements CommandLineRunner {
    private final CountryCodeRepository countryCodeRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final MessageRepository messageRepository;

    private final Random random = new Random();

    public ChatSeeder(CountryCodeRepository countryCodeRepository,
                      UserRepository userRepository,
                      ContactRepository contactRepository,
                      MessageRepository messageRepository) {
        this.countryCodeRepository = countryCodeRepository;
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Get user
        UserEntity userEntity = userRepository.findByDialCodeAndPhoneNumber("+38762005152");
        UserEntity userEntity2 = userRepository.findByDialCodeAndPhoneNumber("+38762154973");


    }
}
