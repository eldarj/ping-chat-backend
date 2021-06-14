package com.pingchat.authenticationservice.util.seeder;

import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import com.pingchat.authenticationservice.data.mysql.entity.CountryCodeEntity;
import com.pingchat.authenticationservice.data.mysql.entity.MessageEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.repository.ContactRepository;
import com.pingchat.authenticationservice.data.mysql.repository.CountryCodeRepository;
import com.pingchat.authenticationservice.data.mysql.repository.MessageRepository;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import com.pingchat.authenticationservice.enums.MessageType;
import com.pingchat.authenticationservice.service.data.DataSpaceDataService;
import com.pingchat.authenticationservice.util.UniqueUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Slf4j
@Profile("seeder")
@Component("userSeeder")
public class UserSeeder implements CommandLineRunner {
    @Value("${service.static-ip-base}")
    private String STATIC_IP_BASE;

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
            "https://images.pexels.com/users/avatars/2272619/rachel-claire-647.jpeg?auto=compress&fit=crop&h=512&w=512",
            "https://i.pinimg.com/originals/dd/ed/0d/dded0d84d259cbf9b6d1ca78cd3e5d18.jpg",
            "https://i.pinimg.com/originals/32/71/31/327131a71a6aa9a81ccb94ee5e6b1c95.png",
            "https://i.pinimg.com/originals/ed/fd/d0/edfdd098e87e726eeebecaa93552a19a.jpg",
            "https://images.pexels.com/photos/1704488/pexels-photo-1704488.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://ak.picdn.net/shutterstock/videos/4422329/thumb/1.jpg",
            "https://images.pexels.com/photos/3033872/pexels-photo-3033872.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://img.freepik.com/free-photo/portrait-young-beautiful-african-girl-dark-wall_176420-5818.jpg?size=626&ext=jpg",
            "https://mapio.net/images-p/3328081.jpg",
            "https://media-cache.cinematerial.com/p/500x/ps0sh5sx/seoul-station-south-korean-movie-poster.jpg?v=1467970235",
            "https://i.pinimg.com/564x/68/3a/33/683a333a00062940ca0bf03a7ab27770.jpg",
            "https://cdn.mos.cms.futurecdn.net/8HQMVAtZ2LsatZQeWMscpF-1200-80.jpg",
            "https://i.pinimg.com/736x/5b/a2/36/5ba236dbbd29c853af505b824defb285.jpg",
            "https://i.pinimg.com/474x/54/f0/ea/54f0ea6af36893b8a72f00e9ba5045ce.jpg",
            "https://i.pinimg.com/originals/88/00/b3/8800b3ff9675d12ce1ec039c2b838914.jpg",
            "https://i.pinimg.com/originals/5a/06/82/5a06824e004d0ab1c67ec07426ca6428.jpg",
            STATIC_IP_BASE + "/profiles/" + "image_picker570247212431073700.jpg",
            STATIC_IP_BASE + "/profiles/" + "image_picker3411855106648442293.jpg",
            STATIC_IP_BASE + "/profiles/" + "image_picker4151765500727569599.jpg",
            STATIC_IP_BASE + "/profiles/" + "image_picker4490805024644911958.jpg",
            STATIC_IP_BASE + "/profiles/" + "image_picker7462381290976944694.jpg",
            STATIC_IP_BASE + "/profiles/" + "image_picker7794235855316216163.jpg",
            STATIC_IP_BASE + "/profiles/" + "image_picker9222588166119286601.jpg",
            STATIC_IP_BASE + "/profiles/" + "image_picker9222588166119286601.jpg",
            STATIC_IP_BASE + "/profiles/" + "image_picker8156845359036178023.jpg",
            "https://randomwordgenerator.com/img/picture-generator/54e0d6404e56aa14f1dc8460962e33791c3ad6e04e507440742a7ed09149c4_640.jpg",
            "https://randomwordgenerator.com/img/picture-generator/55e5d1424951ab14f1dc8460962e33791c3ad6e04e50744172297ed2914cc2_640.jpg",
            "https://randomwordgenerator.com/img/picture-generator/55e4d5474350b10ff3d8992cc12c30771037dbf852547849712a73d5954d_640.jpg",
            "https://randomwordgenerator.com/img/picture-generator/57e7dc454f54ae14f1dc8460962e33791c3ad6e04e507440742a7ad19148cc_640.jpg",
            "https://randomwordgenerator.com/img/picture-generator/53e7d4464d52aa14f1dc8460962e33791c3ad6e04e50774971267bd19549c6_640.jpg",
            "https://randomwordgenerator.com/img/picture-generator/53e1d7444b52af14f1dc8460962e33791c3ad6e04e507440702d79d29048c4_640.jpg",
            "https://randomwordgenerator.com/img/picture-generator/5fe0d7464a5ab10ff3d8992cc12c30771037dbf85254784d752f7add9e4f_640.jpg"
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
            countryCodeEntity.setCountryName("Bosnia and Herzegovina");
            countryCodeEntity.setDialCode("+387");
            countryCodeEntity = countryCodeRepository.save(countryCodeEntity);

            CountryCodeEntity countryCodeEntity2 = new CountryCodeEntity();
            countryCodeEntity2.setCountryName("Serbia");
            countryCodeEntity2.setDialCode("+381");
            countryCodeEntity2 = countryCodeRepository.save(countryCodeEntity2);
            log.info("Saved country codes {}", List.of(countryCodeEntity, countryCodeEntity2));

            log.info("Seeding users...");
            UserEntity userEntity = new UserEntity();
            userEntity.setCountryCode(countryCodeEntity);
            userEntity.setPhoneNumber("62005152");
            userEntity.setFirstName("Eldar");
            userEntity.setLastName("Jahijagic");

            userEntity = userRepository.save(userEntity);
            dataSpaceDataService.createRootNodes(userEntity);
            log.info("Saved user {}", userEntity);

            UserEntity userEntity2 = new UserEntity();
            userEntity2.setCountryCode(countryCodeEntity);
            userEntity2.setPhoneNumber("62871955");
            userEntity2.setFirstName("Berina");
            userEntity2.setLastName("Halilovic");

            userEntity2 = userRepository.save(userEntity2);
            dataSpaceDataService.createRootNodes(userEntity2);
            log.info("Saved user {}", userEntity);

            // Seed contacts and messages
            long contactBindingId = UniqueUtil.nextUniqueLong();
            ContactEntity contactEntity = new ContactEntity();
            contactEntity.setUser(userEntity);
            contactEntity.setContactUser(userEntity2);
            contactEntity.setContactName(userEntity2.getFirstName() + "\uD83E\uDD29");
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
            messageEntity2.setPinned(true);
            messageRepository.save(messageEntity2);

            // Seed message pinned neutral message
            messageEntity.setId(0);
            messageEntity.setSentTimestamp(Instant.now().toEpochMilli());
            messageEntity.setMessageType(MessageType.PIN_INFO);
            messageEntity.setPinned(true);
            messageRepository.save(messageEntity);

            // Seed sticker
            messageEntity.setId(0);
            messageEntity.setText("panda/panda3.png");
            messageEntity.setSentTimestamp(Instant.now().toEpochMilli());
            messageEntity.setMessageType(MessageType.STICKER);
            messageEntity.setSeen(false);
            messageRepository.save(messageEntity);

            // Seed users and contacts
            for (int i = 0; i < 70; i++) {
                String firstName = seedingFirstNames.get(random.nextInt(seedingFirstNames.size()));
                String lastName = seedingLastNames.get(random.nextInt(seedingLastNames.size()));

                String avatar;
                if (i + 1 > seedingAvatars.size()) {
                    URLConnection con = new URL("https://picsum.photos/400").openConnection();
                    con.connect();
                    InputStream is = con.getInputStream();
                    avatar = con.getURL().toString();
                    is.close();
                } else {
                    avatar = seedingAvatars.get(i);
                }
                int phoneNumber = (int)(Math.random() * (65999999 - 61000000 + 1) + 61000000);

                UserEntity anotherUserEntity = new UserEntity();
                anotherUserEntity.setCountryCode(countryCodeEntity);
                anotherUserEntity.setPhoneNumber(String.valueOf(phoneNumber));
                anotherUserEntity.setFirstName(firstName);
                anotherUserEntity.setLastName(lastName);
                anotherUserEntity.setDisplayMyFullName(i % 2 == 0);

                anotherUserEntity.setProfileImagePath(avatar);

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

                    messageEntity.setSentTimestamp(Instant.now().minus(i * 160, ChronoUnit.MINUTES).toEpochMilli());
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
