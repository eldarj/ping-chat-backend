package com.pingchat.authenticationservice.util.seeder;

import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import com.pingchat.authenticationservice.data.mysql.entity.CountryCodeEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.repository.ContactRepository;
import com.pingchat.authenticationservice.data.mysql.repository.CountryCodeRepository;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@Profile("seeder")
public class UserSeeder implements CommandLineRunner {
    private final CountryCodeRepository countryCodeRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

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
                      ContactRepository contactRepository) {
        this.userRepository = userRepository;
        this.countryCodeRepository = countryCodeRepository;
        this.contactRepository = contactRepository;
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

                ContactEntity contactEntity = new ContactEntity();
                contactEntity.setUser(userEntity);
                contactEntity.setContactUser(anotherUserEntity);
                contactEntity.setContactName(anotherUserEntity.getFirstName());
                contactEntity.setContactPhoneNumber(anotherUserEntity.getCountryCode().getDialCode() + anotherUserEntity.getPhoneNumber());
                contactEntity.setFavorite(i < 5);

                contactRepository.save(contactEntity);
            }

            log.info("Finished seeding...");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
