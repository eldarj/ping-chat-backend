package com.pingchat.authenticationservice.util.seeder;

import com.pingchat.authenticationservice.data.mysql.entity.CountryCodeEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.repository.CountryCodeRepository;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Profile("seeder")
public class UserSeeder implements CommandLineRunner {
    private final CountryCodeRepository countryCodeRepository;
    private final UserRepository userRepository;

    public UserSeeder(CountryCodeRepository countryCodeRepository,
                      UserRepository userRepository) {
        this.userRepository = userRepository;
        this.countryCodeRepository = countryCodeRepository;
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

            userEntity.setProfileImagePath("https://media-exp1.licdn.com/dms/image/C5603AQH9KNis_BzaRA/profile" +
                    "-displayphoto" +
                    "-shrink_100_100/0?e=1608768000&v=beta&t=-A__OpLiqt5XbBcRDSoDJdgOjsUszXHJzxhkp8jTMrs");

            UserEntity savedUserEntity = userRepository.save(userEntity);
            log.info("Saved user {}", savedUserEntity);

            log.info("Finished seeding...");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
