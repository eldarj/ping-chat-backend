package com.pingchat.authenticationservice.util.seeder;

import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import com.pingchat.authenticationservice.data.mysql.entity.CountryCodeEntity;
import com.pingchat.authenticationservice.data.mysql.entity.MessageEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.repository.ContactRepository;
import com.pingchat.authenticationservice.data.mysql.repository.CountryCodeRepository;
import com.pingchat.authenticationservice.data.mysql.repository.MessageRepository;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Slf4j
@Component("userSeeder")
@Profile("seeder")
public class UserSeeder implements CommandLineRunner {
    private final CountryCodeRepository countryCodeRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final MessageRepository messageRepository;

    private final List<String> seedingFirstNames = List.of(
            "Alen", "Dino", "Admir", "Kenan","Berina","Emina", "Belma", "Samra",
            "Indira", "Edvina", "Mirka", "Admira", "Selmana", "Samira", "Elizabeta",
            "Jasmina", "Arnela", "Ajla", "Amila", "Edina", "Ivana", "Milica", "Senada", "Senida",
            "Almasa","Armin","Jelena","Igor","Mario","Ivica","Samir","Semir", "Ivana");
    private final List<String> seedingLastNames = List.of(
            "Jenciragic", "Hadzijalagic", "Kot", "Belkisa", "Radic", "Ivanovic",
            "Kuslut", "Kusetovic", "Balaban", "Avdic", "Popara", "Zagrljaca", "Span",
            "Pozdarlic", "Muk", "Potok", "Blitvic", "Jahijagic", "Halilovic", "Hamidovic",
            "Josipovic", "Osim", "Maric", "Lukic", "Borovic", "Mehmedovic", "Spago");
    private final List<String> seedingAvatars = List.of(
            "https://images.pexels.com/users/avatars/2272619/rachel-claire-647.jpeg?auto=compress&fit=crop&h=256&w=256",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTMnQXZsq16O6PnxLOZC40Ry_HgVoI-FjfwBg&usqp=CAU",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSaq3uUJbou0ypc_1QC-AMzJFuON138fJb4HQ&usqp=CAU",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQzd2RuqRy9hb2HyritqnEJMkOgmYByumXQWg&usqp=CAU",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRpApFj077ey3pzZ9cNGOdRUeGMlK40qV8Qkg&usqp=CAU",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTcrNfGlxv29FTGxAeIO9aTl4SdQhEEy8hZpg&usqp=CAU",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT3lwOzijJqyTX9UxQ5CuPVQsnJ5zsttbfYow&usqp=CAU",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTpi269wVddq5JnoYxwpoYTy2YlR7sJXeULiw&usqp=CAU",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRAeftRlPEMaSJip5A02knegRG6dsgiuzos8w&usqp=CAU",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTyR5teUjOuhvk9qnfrfqCj_IuLS0sHOxs17Q&usqp=CAU",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQgHclVKvHedn6Ajd9Zy8MD3VQam9YwVe5Vmg&usqp=CAU",
            "https://a.thumbs.redditmedia.com/DIgiF5PWb2_NQRUhgowmca2zuuJDXHG54NXiNysMmE8.png",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRQF_w6FQwmxVqCg8M2SfFzQbxnKhZBc2E_Gw&usqp=CAU"
    );

    private final Random random = new Random();

    public UserSeeder(CountryCodeRepository countryCodeRepository,
                      UserRepository userRepository,
                      ContactRepository contactRepository,
                      MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.countryCodeRepository = countryCodeRepository;
        this.contactRepository = contactRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void run(String... args) {
        try {
            log.info("Seeding country codes...");
            CountryCodeEntity countryCodeEntity = new CountryCodeEntity();
            countryCodeEntity.setCountryName("Bosna i Hercegovina");
            countryCodeEntity.setDialCode("+387");
            countryCodeEntity = countryCodeRepository.save(countryCodeEntity);

            CountryCodeEntity countryCodeEntity2 = new CountryCodeEntity();
            countryCodeEntity2.setCountryName("Srbija");
            countryCodeEntity2.setDialCode("+381");
            countryCodeEntity2 = countryCodeRepository.save(countryCodeEntity2);
            log.info("Saved country codes {}", List.of(countryCodeEntity, countryCodeEntity2));

            log.info("Seeding users...");
            UserEntity userEntity = new UserEntity();
            userEntity.setCountryCode(countryCodeEntity);
            userEntity.setPhoneNumber("62005152");
            userEntity.setFirstName("Eldar");
            userEntity.setLastName("Jahijagic");

            userEntity.setProfileImagePath("https://media-exp1.licdn.com/dms/image/C5603AQH9KNis_BzaRA/profile-displayphoto-shrink_100_100/0?e=1608768000&v=beta&t=-A__OpLiqt5XbBcRDSoDJdgOjsUszXHJzxhkp8jTMrs");

            UserEntity savedUserEntity = userRepository.save(userEntity);

            UserEntity userEntity2 = new UserEntity();
            userEntity2.setCountryCode(countryCodeEntity);
            userEntity2.setPhoneNumber("62154973");
            userEntity2.setFirstName("Sabaha");
            userEntity2.setLastName("Jahijagic");
            userRepository.save(userEntity2);

            log.info("Saved user {}", savedUserEntity);

            // Seed messages
            ContactEntity contactEntity = new ContactEntity();
            contactEntity.setUser(userEntity);
            contactEntity.setContactUser(userEntity2);
            contactEntity.setContactName(userEntity2.getFirstName());
            contactEntity.setContactPhoneNumber(userEntity2.getCountryCode().getDialCode() + userEntity2.getPhoneNumber());
            contactEntity.setContactUserExists(true);
            contactEntity.setFavorite(true);

            contactRepository.save(contactEntity);

            ContactEntity contactEntity2 = new ContactEntity();
            contactEntity2.setUser(userEntity2);
            contactEntity2.setContactUser(userEntity);
            contactEntity2.setContactName(userEntity.getFirstName());
            contactEntity2.setContactPhoneNumber(userEntity.getCountryCode().getDialCode() + userEntity.getPhoneNumber());
            contactEntity2.setContactUserExists(true);
            contactEntity2.setFavorite(true);

            contactRepository.save(contactEntity2);

            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setText("Hello there");
            messageEntity.setSender(userEntity);
            messageEntity.setReceiver(userEntity2);
            messageEntity.setReceived(true);
            messageEntity.setSeen(true);
            messageEntity.setSenderContactName(contactEntity2.getContactName());
            messageEntity.setReceiverContactName(contactEntity.getContactName());
            messageEntity.setSentTimestamp(Instant.now().plusSeconds(20));

            messageRepository.save(messageEntity);

            MessageEntity messageEntity2 = new MessageEntity();
            messageEntity2.setText("Hey, whats up");
            messageEntity2.setSender(userEntity2);
            messageEntity2.setReceiver(userEntity);
            messageEntity2.setReceived(true);
            messageEntity2.setSeen(false);
            messageEntity2.setSenderContactName(contactEntity.getContactName());
            messageEntity2.setReceiverContactName(contactEntity2.getContactName());
            messageEntity2.setSentTimestamp(Instant.now().plusSeconds(50));

            messageRepository.save(messageEntity2);

            // Seed users and contacts
            for (int i = 0; i < 70; i++) {
                String firstName = seedingFirstNames.get(random.nextInt(seedingFirstNames.size()));
                String lastName = seedingLastNames.get(random.nextInt(seedingLastNames.size()));
                String avatar = seedingAvatars.get(random.nextInt(seedingAvatars.size()));
                int phoneNumber = (int)(Math.random() * (65999999 - 61000000 + 1) + 61000000);

                UserEntity anotherUserEntity = new UserEntity();
                anotherUserEntity.setCountryCode(countryCodeEntity);
                anotherUserEntity.setPhoneNumber(String.valueOf(phoneNumber));
                anotherUserEntity.setFirstName(firstName);
                anotherUserEntity.setLastName(lastName);
                anotherUserEntity.setDisplayMyFullName(i % 2 == 0);

                if (i < 50) {
                    anotherUserEntity.setProfileImagePath(avatar);
                }

                anotherUserEntity = userRepository.save(anotherUserEntity);

                ContactEntity anotherContactEntity = new ContactEntity();
                anotherContactEntity.setUser(userEntity);
                anotherContactEntity.setContactUser(anotherUserEntity);
                anotherContactEntity.setContactName(anotherUserEntity.getFirstName());
                anotherContactEntity.setContactPhoneNumber(anotherUserEntity.getCountryCode().getDialCode() + anotherUserEntity.getPhoneNumber());
                anotherContactEntity.setFavorite(i < 5);

                contactRepository.save(anotherContactEntity);

                ContactEntity anotherContactEntity2 = new ContactEntity();
                anotherContactEntity2.setUser(anotherUserEntity);
                anotherContactEntity2.setContactUser(userEntity);
                anotherContactEntity2.setContactName(userEntity.getFirstName());
                anotherContactEntity2.setContactPhoneNumber(userEntity.getCountryCode().getDialCode() + userEntity.getPhoneNumber());
                anotherContactEntity2.setFavorite(i < 5);

                contactRepository.save(anotherContactEntity2);

                if (i < 20) {
                    messageEntity = new MessageEntity();
                    messageEntity.setText("Hello there" + i);
                    messageEntity.setReceived(true);
                    messageEntity.setSeen(false);


                    if (i % 2 == 0) {
                        messageEntity.setSender(userEntity);
                        messageEntity.setReceiver(anotherUserEntity);
                        messageEntity.setSenderContactName(anotherContactEntity2.getContactName());
                        messageEntity.setReceiverContactName(anotherContactEntity.getContactName());
                    } else {
                        messageEntity.setSender(anotherUserEntity);
                        messageEntity.setReceiver(userEntity);
                        messageEntity.setSenderContactName(anotherContactEntity.getContactName());
                        messageEntity.setReceiverContactName(anotherContactEntity2.getContactName());
                    }
                    messageEntity.setSentTimestamp(Instant.now().minusSeconds(i * 100));

                    messageRepository.save(messageEntity);
                }
            }

            log.info("Finished seeding...");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
