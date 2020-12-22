package com.pingchat.authenticationservice.util.seeder;

import com.pingchat.authenticationservice.data.mysql.entity.*;
import com.pingchat.authenticationservice.data.mysql.repository.*;
import com.pingchat.authenticationservice.enums.MessageType;
import com.pingchat.authenticationservice.model.dto.DSNodeDto;
import com.pingchat.authenticationservice.service.data.DataSpaceDataService;
import com.pingchat.authenticationservice.util.UniqueUtil;
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
    private final DataSpaceDataService dataSpaceDataService;

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

    private final List<String> seedingMessages = List.of(
            "How are you?",
            "What's up?",
            "What you up to, wanna hangout?",
            "Are you free for a call?",
            "Is this working?",
            "Thank you.",
            "Anyways, what's your Linkedin url?",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            "Animum autem reliquis rebus ita perfecit, ut corpus.",
            "Sed fac ista esse non inportuna",
            "Quo tandem modo?",
            "Duo Reges: constructio interrete.",
            "Memini vero, inquam.",
            "Haec dicuntur inconstantissime.",
            "Haha",
            "An eiusdem modi?",
            "Itaque hic ipse iam pridem est reiectus; Nobis aliter videtur, recte secusne, postea.",
            "Duo Reges: constructio interrete."
    );

    private final Random random = new Random();

    public UserSeeder(CountryCodeRepository countryCodeRepository,
                      UserRepository userRepository,
                      ContactRepository contactRepository,
                      MessageRepository messageRepository,
                      DataSpaceDataService dataSpaceDataService) {
        this.userRepository = userRepository;
        this.countryCodeRepository = countryCodeRepository;
        this.contactRepository = contactRepository;
        this.messageRepository = messageRepository;
        this.dataSpaceDataService = dataSpaceDataService;
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

            userEntity = userRepository.save(userEntity);
            dataSpaceDataService.createRootNodes(userEntity);

            UserEntity userEntity2 = new UserEntity();
            userEntity2.setCountryCode(countryCodeEntity);
            userEntity2.setPhoneNumber("62154973");
            userEntity2.setFirstName("Sabaha \uD83E\uDD29");
            userEntity2.setLastName("Jahijagic");
            userEntity2 = userRepository.save(userEntity2);
            dataSpaceDataService.createRootNodes(userEntity2);

            log.info("Saved user {}", userEntity);

            // Seedcontacts and messages
            long contactBindingId = UniqueUtil.nextUniqueLong();
            ContactEntity contactEntity = new ContactEntity();
            contactEntity.setUser(userEntity);
            contactEntity.setContactUser(userEntity2);
            contactEntity.setContactName(userEntity2.getFirstName());
            contactEntity.setContactPhoneNumber(userEntity2.getCountryCode().getDialCode() + userEntity2.getPhoneNumber());
            contactEntity.setContactUserExists(true);
            contactEntity.setFavorite(true);
            contactEntity.setContactBindingId(contactBindingId);

            contactRepository.save(contactEntity);

            ContactEntity contactEntity2 = new ContactEntity();
            contactEntity2.setUser(userEntity2);
            contactEntity2.setContactUser(userEntity);
            contactEntity2.setContactName(userEntity.getFirstName());
            contactEntity2.setContactPhoneNumber(userEntity.getCountryCode().getDialCode() + userEntity.getPhoneNumber());
            contactEntity2.setContactUserExists(true);
            contactEntity2.setFavorite(true);
            contactEntity2.setContactBindingId(contactBindingId);

            contactRepository.save(contactEntity2);

            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setText("Hey, what's up?");
            messageEntity.setSender(userEntity);
            messageEntity.setReceiver(userEntity2);
            messageEntity.setSent(true);
            messageEntity.setReceived(true);
            messageEntity.setSeen(true);
            messageEntity.setSenderContactName(contactEntity2.getContactName());
            messageEntity.setReceiverContactName(contactEntity.getContactName());
            messageEntity.setSentTimestamp(Instant.now().minusSeconds(120).toEpochMilli());
            messageEntity.setContactBindingId(contactBindingId);

            messageRepository.save(messageEntity);

            MessageEntity messageEntity2 = new MessageEntity();
            messageEntity2.setText("Hey, whats up, how are you doing? Are you free for a call, perhaps today " +
                    "or tomorrow " +
                    "around 18.00?");
            messageEntity2.setSender(userEntity2);
            messageEntity2.setReceiver(userEntity);
            messageEntity2.setSent(true);
            messageEntity2.setReceived(true);
            messageEntity2.setSeen(true);
            messageEntity2.setSenderContactName(contactEntity.getContactName());
            messageEntity2.setReceiverContactName(contactEntity2.getContactName());
            messageEntity2.setSentTimestamp(Instant.now().toEpochMilli());
            messageEntity2.setContactBindingId(contactBindingId);

            messageRepository.save(messageEntity2);

            for (int i = 0; i < 20; i++) {
                int messagesSize = seedingMessages.size();
                messageEntity2.setId(0);
                messageEntity2.setText(
                        seedingMessages.get(random.nextInt(messagesSize)) + " " +
                                seedingMessages.get(random.nextInt(messagesSize)) + " " +
                                seedingMessages.get(random.nextInt(messagesSize)));
                messageEntity2.setSentTimestamp(Instant.now().toEpochMilli());
                messageRepository.save(messageEntity2);

                messageEntity.setId(0);
                messageEntity.setText(
                        seedingMessages.get(random.nextInt(messagesSize)) + " " +
                                seedingMessages.get(random.nextInt(messagesSize)) + " " +
                                seedingMessages.get(random.nextInt(messagesSize)));
                messageEntity.setSentTimestamp(Instant.now().toEpochMilli());
                messageRepository.save(messageEntity);
            }

            // Seed unicode
            messageEntity2.setId(0);
            messageEntity2.setText("Ok \uD83D\uDE04\uD83D\uDE04");
            messageEntity2.setSentTimestamp(Instant.now().toEpochMilli());
            messageRepository.save(messageEntity2);


            // Seed sticker
            messageEntity.setId(0);
            messageEntity.setText("panda3.png");
            messageEntity.setSentTimestamp(Instant.now().toEpochMilli());
            messageEntity.setMessageType(MessageType.STICKER);
            messageEntity.setSeen(false);
            messageRepository.save(messageEntity);

            // Seed image
            messageEntity2.setId(0);
            messageEntity.setSentTimestamp(Instant.now().toEpochMilli());
            messageEntity2.setFileName("person1.jpeg");
            messageEntity2.setFilePath("/data/user/0/com.eldarjahijagic.ping.flutterping/cache/file_picker/person1.jpeg");
            messageEntity2.setFileUrl("http://192.168.1.4:8089/files/uploads/person1.jpeg");
            messageEntity2.setMessageType(MessageType.IMAGE);
            messageEntity2.setSeen(false);
            messageRepository.save(messageEntity2);

            // Seed image2 (don't display this on the sender, but do download on receiver)
            messageEntity.setId(0);
            messageEntity.setSentTimestamp(Instant.now().toEpochMilli());
            messageEntity.setFileName("person1.jpeg");
            messageEntity.setFilePath("/data/user/0/com.eldarjahijagic.ping" +
                    ".flutterping/cache/file_picker/wrongdir/person1.jpeg");
            messageEntity.setFileUrl("http://192.168.1.4:8089/files/uploads/person1.jpeg");
            messageEntity.setMessageType(MessageType.IMAGE);
            messageEntity.setDeleted(true);
            messageEntity.setSeen(false);
            messageRepository.save(messageEntity);

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

                long anotherContactBindingId = UniqueUtil.nextUniqueLong();
                ContactEntity anotherContactEntity = new ContactEntity();
                anotherContactEntity.setUser(userEntity);
                anotherContactEntity.setContactUser(anotherUserEntity);
                anotherContactEntity.setContactName(anotherUserEntity.getFirstName());
                anotherContactEntity.setContactPhoneNumber(anotherUserEntity.getCountryCode().getDialCode() + anotherUserEntity.getPhoneNumber());
                anotherContactEntity.setFavorite(i < 5);
                anotherContactEntity.setContactBindingId(anotherContactBindingId);

                contactRepository.save(anotherContactEntity);

                ContactEntity anotherContactEntity2 = new ContactEntity();
                anotherContactEntity2.setUser(anotherUserEntity);
                anotherContactEntity2.setContactUser(userEntity);
                anotherContactEntity2.setContactName(userEntity.getFirstName());
                anotherContactEntity2.setContactPhoneNumber(userEntity.getCountryCode().getDialCode() + userEntity.getPhoneNumber());
                anotherContactEntity2.setFavorite(i < 5);
                anotherContactEntity2.setContactBindingId(anotherContactBindingId);

                contactRepository.save(anotherContactEntity2);

                if (i < 20) {
                    messageEntity = new MessageEntity();
                    messageEntity.setText("Hello there" + i);
                    messageEntity.setSent(true);
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

                    messageEntity.setSentTimestamp(Instant.now().minusSeconds(i * 100).toEpochMilli());
                    messageEntity.setContactBindingId(anotherContactBindingId);

                    messageRepository.save(messageEntity);
                }
            }

            log.info("Finished seeding...");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
