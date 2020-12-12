package com.pingchat.authenticationservice.service.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.data.mysql.entity.CountryCodeEntity;
import com.pingchat.authenticationservice.data.mysql.repository.CountryCodeRepository;
import com.pingchat.authenticationservice.model.dto.CountryCodeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class CountryCodeDataService {
    private final CountryCodeRepository countryCodeRepository;
    private final ObjectMapper objectMapper;

    public CountryCodeDataService(CountryCodeRepository countryCodeRepository,
                                  ObjectMapper objectMapper) {
        this.countryCodeRepository = countryCodeRepository;
        this.objectMapper = objectMapper;
    }

    public CountryCodeDto save(CountryCodeDto countryCodeDto) {
        log.info("Saving countryCodeDto {}", countryCodeDto);
        CountryCodeEntity countryCodeEntity = objectMapper.convertValue(countryCodeDto, CountryCodeEntity.class);
        return objectMapper.convertValue(countryCodeRepository.save(countryCodeEntity), CountryCodeDto.class);
    }

    public List<CountryCodeDto> findAllCountryCodes() throws IOException {
        List<CountryCodeEntity> countryCodeEntities = countryCodeRepository.findAll();
        return objectMapper.readValue(objectMapper.writeValueAsBytes(countryCodeEntities), new TypeReference<>() {});
    }
}
