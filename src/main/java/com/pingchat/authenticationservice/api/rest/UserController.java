package com.pingchat.authenticationservice.api.rest;

import com.drew.imaging.ImageProcessingException;
import com.pingchat.authenticationservice.auth.util.SecurityContextUserProvider;
import com.pingchat.authenticationservice.model.dto.UserDto;
import com.pingchat.authenticationservice.model.dto.UserSettingsDto;
import com.pingchat.authenticationservice.service.FirebaseService;
import com.pingchat.authenticationservice.service.data.UserDataService;
import com.pingchat.authenticationservice.service.files.StaticFileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${service.static-ip-base}")
    private String STATIC_IP_BASE;

    private final UserDataService userDataService;
    private final StaticFileStorageService staticFileStorageService;
    private final FirebaseService firebaseService;

    public UserController(UserDataService userDataService,
                          StaticFileStorageService staticFileStorageService,
                          FirebaseService firebaseService) {
        this.userDataService = userDataService;
        this.staticFileStorageService = staticFileStorageService;
        this.firebaseService = firebaseService;
    }

    @GetMapping
    public Map<Long, UserDto> findAllUsers() throws IOException {
        return userDataService.findAll().stream().collect(Collectors.toMap(
                UserDto::getId, user -> user
        ));
    }

    @GetMapping("{userId}")
    public UserDto findUser(@PathVariable long userId) {
        return userDataService.findById(userId);
    }

    @PostMapping
    public UserDto updateUser(@RequestBody UserDto userDto) {
        return userDataService.updateUser(userDto);
    }

    @PostMapping("{userId}/settings")
    public UserSettingsDto updateUserSettings(@PathVariable long userId, @RequestBody UserSettingsDto userSettings) {
        return userDataService.updateUserSettings(userSettings);
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
    public String updateProfileImage(@PathVariable long userId, @RequestParam("file") MultipartFile file)
            throws IOException, ImageProcessingException {
        String newFileName = staticFileStorageService.saveProfileImage(file);

        String newProfileImagePath = STATIC_IP_BASE + "/profiles/" + newFileName;

        int updated = userDataService.updateProfileImage(userId, newProfileImagePath);
        if (updated <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("Couldn't update profile image for user with id=%d ", userId)
            );
        }

        return newProfileImagePath;
    }

    @DeleteMapping("/{userId}/profile-image")
    public void removeProfileImage(@PathVariable long userId) {
        userDataService.updateProfileImage(userId, null);
    }

    @PostMapping("/firebase-token")
    public void registerFirebaseToken(@RequestBody String firebaseToken) {
        String currentUserPhoneNumber = SecurityContextUserProvider.currentPhoneNumber();
        firebaseToken = firebaseToken.replaceAll("\"", "");
        firebaseService.registerToken(currentUserPhoneNumber, firebaseToken);
    }
}
