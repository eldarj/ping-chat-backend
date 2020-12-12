package com.pingchat.authenticationservice.api.rest;

import com.pingchat.authenticationservice.model.dto.CountryCodeDto;
import com.pingchat.authenticationservice.service.data.CountryCodeDataService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static java.util.stream.Collectors.toMap;


@RestController
@RequestMapping("/api/country-codes")
public class CountryCodesController {
    private final CountryCodeDataService countryCodeDataService;

    public CountryCodesController(CountryCodeDataService countryCodeDataService) {
        this.countryCodeDataService = countryCodeDataService;
    }

    @GetMapping
    public Map<Long, CountryCodeDto> findAllCountryCodes() throws IOException {
        return countryCodeDataService.findAllCountryCodes().stream()
                .collect(toMap(CountryCodeDto::getId, c -> c));
    }

    @PostMapping
    public CountryCodeDto save(@RequestBody CountryCodeDto countryCodeDto) {
        return countryCodeDataService.save(countryCodeDto);
    }
}
