package com.pingchat.authenticationservice.api.rest;

import com.pingchat.authenticationservice.model.UserDto;
import com.pingchat.authenticationservice.service.data.UserDataService;
import com.pingchat.authenticationservice.service.files.StaticFileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final String STATIC_FILES_BASE_URL = "http://192.168.1.4:8089/files/";

    private final UserDataService userDataService;
    private final StaticFileStorageService staticFileStorageService;

    public UserController(UserDataService userDataService,
                          StaticFileStorageService staticFileStorageService) {
        this.userDataService = userDataService;
        this.staticFileStorageService = staticFileStorageService;
    }

    @GetMapping
    public Map<Long, UserDto> findAllUsers() throws IOException {
        return userDataService.findAll().stream().collect(Collectors.toMap(
                UserDto::getId, user -> user
        ));
    }

    @GetMapping("{userId}")
    public UserDto findDriver(@PathVariable long userId) {
        return userDataService.findById(userId);
    }

    @PostMapping
    public UserDto updateUser(@RequestBody UserDto userDto) {
        return userDataService.updateUser(userDto);
    }

    @PostMapping("/{userId}/name")
    public void updateUserFirstAndLastName(@PathVariable long userId, @RequestBody UserDto userDto) {
        int updated = userDataService.updateFirstNameAndLastName(userId, userDto.getFirstName(),
                userDto.getLastName());
        if (updated <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("User with id=%d not found", userId)
            );
        }
    }

    @PostMapping("/{userId}/profile-image")
    public String handleFileUpload(@PathVariable long userId, @RequestParam("file") MultipartFile file) throws IOException {
        String newFileName = staticFileStorageService.save(file);

        String newProfileImagePath = STATIC_FILES_BASE_URL + newFileName;

        int updated = userDataService.updateProfileImage(userId, newProfileImagePath);
        if (updated <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("Couldn't update profile image for user with id=%d ", userId)
            );
        }

        return newProfileImagePath;
    }

    @PostMapping("/{userId}/logout")
    public void logout(@PathVariable String userId) {
        log.info("User {} logged out.", userId);
    }
}
