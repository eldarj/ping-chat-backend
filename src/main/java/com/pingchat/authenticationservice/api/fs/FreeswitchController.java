package com.pingchat.authenticationservice.api.fs;

import com.pingchat.authenticationservice.model.dto.FsUser;
import com.pingchat.authenticationservice.service.data.UserDataService;
import com.pingchat.authenticationservice.util.UserDirectoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/freeswitch")
public class FreeswitchController {
    private final UserDataService userDataService;
    private final UserDirectoryUtil userDirectoryUtil;

    public FreeswitchController(UserDataService userDataService,
                                UserDirectoryUtil userDirectoryUtil) {
        this.userDataService = userDataService;
        this.userDirectoryUtil = userDirectoryUtil;
    }

    @PostMapping(produces = {MediaType.APPLICATION_XML_VALUE}, path = "/user-directory")
    public String find() throws ParserConfigurationException, TransformerException, IOException {
        List<String> users = userDataService.findAllFsUsers();
        return userDirectoryUtil.buildXmlOfPhoneNumbers(users);
    }
}
