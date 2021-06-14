package com.pingchat.authenticationservice.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.auth.util.SecurityContextUserProvider;
import com.pingchat.authenticationservice.model.dto.DSNodeDto;
import com.pingchat.authenticationservice.service.data.DataSpaceDataService;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.exception.UploadAlreadyLockedException;
import me.desair.tus.server.upload.UploadInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/data-space")
public class DataSpaceController {
    @Value("${service.static-base-path}")
    private String STATIC_BASE_PATH;

    @Value("${service.static-ip-base}")
    private String STATIC_IP_BASE;

    private static final String X_LOCATION_HEADER_KEY = "X-Location";
    private static final String X_NODE_ID_HEADER_KEY = "X-NodeId";

    private final TusFileUploadService tusFileUploadService;
    private final DataSpaceDataService dataSpaceDataService;
    private final ObjectMapper objectMapper;

    public DataSpaceController(TusFileUploadService tusFileUploadService,
                               DataSpaceDataService dataSpaceDataService,
                               ObjectMapper objectMapper) {
        this.tusFileUploadService = tusFileUploadService;
        this.dataSpaceDataService = dataSpaceDataService;
        this.objectMapper = objectMapper;
    }

    // Get user's data space
    @GetMapping("{userId}")
    public List<DSNodeDto> getDataSpace(@PathVariable Long userId) {
        return dataSpaceDataService.getDataSpace(userId);
    }

    // Get Received directory
    @GetMapping("{userId}/received")
    public List<DSNodeDto> getReceivedDataSpace(@PathVariable Long userId) {
        return dataSpaceDataService.getAllReceived(userId);
    }

    // Get Sent directory
    @GetMapping("{userId}/sent")
    public List<DSNodeDto> getReceivedDataSpace(@PathVariable Long userId, @RequestParam Long directoryId) {
        return dataSpaceDataService.getAllSent(userId, directoryId);
    }

    // Get Directory
    @GetMapping("{userId}/{directoryId}")
    public List<DSNodeDto> getDirectory(@PathVariable Long userId, @PathVariable Long directoryId) {
        return dataSpaceDataService.getDirectory(userId, directoryId);
    }

    // Get Shared data (SingleContactActivity and SharedActivity)
    @GetMapping("shared")
    public List<DSNodeDto> getSharedDataSpace(@RequestParam Long userId,
                                              @RequestParam Long contactId,
                                              @RequestParam Optional<Long> nodesCount) {
        if (nodesCount.isPresent()) {
            return dataSpaceDataService.getSharedData(userId, contactId, nodesCount.get());
        } else {
            return dataSpaceDataService.getSharedData(userId, contactId);
        }
    }

    // Create directory
    @PostMapping("{userId}/directory")
    public DSNodeDto createDirectory(@PathVariable Long userId, @RequestBody DSNodeDto dsNode) {
        return dataSpaceDataService.create(dsNode);
    }

    // Delete directory
    @DeleteMapping("directory/{directoryId}")
    public void deleteDirectory(@PathVariable Long directoryId) {
        dataSpaceDataService.deleteDirectoryByNodeId(directoryId);
    }

    // Delete file
    @DeleteMapping
    public void deleteById(@RequestParam Long nodeId, @RequestParam String fileName) throws IOException {
        Long userId = SecurityContextUserProvider.currentUserId();
        dataSpaceDataService.deleteForUser(nodeId, userId);
    }

    // Upload file
    @RequestMapping(value = {"/upload", "/upload/**"},
            method = { RequestMethod.POST, RequestMethod.PATCH, RequestMethod.HEAD })
    public void upload(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse)
            throws IOException, TusException {
        tusFileUploadService.process(servletRequest, servletResponse);

        String uploadUrl = servletRequest.getRequestURI();
        UploadInfo uploadInfo = this.tusFileUploadService.getUploadInfo(uploadUrl);

        if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {

            try (InputStream inputStream = this.tusFileUploadService.getUploadedBytes(uploadUrl)) {
                String fileName = uploadInfo.getFileName();
                String fileUrl = STATIC_IP_BASE + "/uploads/" + fileName;

                Path output = Paths.get(STATIC_BASE_PATH + "/uploads").resolve(fileName);
                Files.copy(inputStream, output, StandardCopyOption.REPLACE_EXISTING);

                String dsNodeEncoded = uploadInfo.getMetadata().get("dsNodeEncoded");
                DSNodeDto dsNodeDto = objectMapper.readValue(dsNodeEncoded, DSNodeDto.class);
                dsNodeDto.setNodePath(output.toString());
                dsNodeDto.setUploadId(uploadInfo.getId().toString());

                DSNodeDto dsNode = dataSpaceDataService.create(dsNodeDto);

                servletResponse.setHeader(X_LOCATION_HEADER_KEY, fileUrl);
                servletResponse.setHeader(X_NODE_ID_HEADER_KEY, String.valueOf(dsNode.getId()));

                log.info("Uploaded file to {}", output);
            }

            this.tusFileUploadService.deleteUpload(uploadUrl);
        }
    }

    // Delete during upload
    @DeleteMapping("/upload/{uploadUrl}")
    @Retryable(value = UploadAlreadyLockedException.class, backoff = @Backoff(delay = 10_000L))
    public ResponseEntity<Object> deleteDuringUpload(@PathVariable String uploadUrl,
                                                     @RequestParam String fileName) throws IOException, TusException {
        UploadInfo uploadInfo = this.tusFileUploadService.getUploadInfo(uploadUrl);

        if (uploadInfo == null && !StringUtils.hasLength(fileName)) {
            return ResponseEntity.notFound().build();
        }

        if (uploadInfo != null) {
            Files.deleteIfExists(Paths.get(STATIC_BASE_PATH + "/uploads").resolve(uploadInfo.getFileName()));
            this.tusFileUploadService.deleteUpload(uploadUrl);

        } else if (StringUtils.hasLength(fileName)) {
            Files.deleteIfExists(Paths.get(STATIC_BASE_PATH + "/uploads").resolve(fileName));
        }

        dataSpaceDataService.deleteByUploadId(uploadUrl);

        return ResponseEntity.noContent().build();
    }
}
