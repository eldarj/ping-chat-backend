package com.pingchat.authenticationservice.api.rest;

import com.pingchat.authenticationservice.model.dto.CountryCodeDto;
import com.pingchat.authenticationservice.service.data.CountryCodeDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
@RestController
@RequestMapping("/api/country-codes")
public class CountryCodesController {
    private final CountryCodeDataService countryCodeDataService;

    public CountryCodesController(CountryCodeDataService countryCodeDataService) {
        this.countryCodeDataService = countryCodeDataService;
    }

    @GetMapping
    public Map<Long, CountryCodeDto> findAllCountryCodes() throws IOException {
        Map<Long, CountryCodeDto> countryCodes = countryCodeDataService.findAllCountryCodes().stream()
                .collect(toMap(CountryCodeDto::getId, c -> c));

        log.info("Fetching country codes {}", countryCodes);
        return countryCodes;
    }

    @PostMapping
    public CountryCodeDto save(@RequestBody CountryCodeDto countryCodeDto) {
        return countryCodeDataService.save(countryCodeDto);
    }
}
