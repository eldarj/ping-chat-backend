package com.pingchat.authenticationservice.api.rest;

import com.pingchat.authenticationservice.model.UserDto;
import com.pingchat.authenticationservice.service.data.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserDataService userDataService;

    public UserController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @GetMapping
    public Map<Long, UserDto> findAllUsers() throws IOException {
        return userDataService.findAll().stream().collect(Collectors.toMap(
                UserDto::getId, user -> user
        ));
    }

    @PostMapping
    public UserDto updateUser(@RequestBody UserDto userDto) {
        return userDataService.updateUser(userDto);
    }
}
