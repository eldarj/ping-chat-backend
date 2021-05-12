package com.pingchat.authenticationservice.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    protected String staticBasePath;

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

    @GetMapping("{userId}")
    public List<DSNodeDto> getDataSpace(@PathVariable Long userId) {
        return dataSpaceDataService.getDataSpace(userId);
    }

    @GetMapping("{userId}/received")
    public List<DSNodeDto> getReceivedDataSpace(@PathVariable Long userId) {
        return dataSpaceDataService.getAllReceived(userId);
    }

    @GetMapping("{userId}/{directoryId}")
    public List<DSNodeDto> getDirectory(@PathVariable Long userId, @PathVariable Long directoryId) {
        return dataSpaceDataService.getDirectory(userId, directoryId);
    }

    @PostMapping("{userId}/directory")
    public DSNodeDto createDirectory(@PathVariable Long userId, @RequestBody DSNodeDto dsNode) {
        return dataSpaceDataService.create(dsNode);
    }

    @DeleteMapping("directory/{directoryId}")
    public void deleteDirectory(@PathVariable Long directoryId) {
        dataSpaceDataService.deleteDirectoryByNodeId(directoryId);
    }

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

    @RequestMapping(value = {"/upload", "/upload/**"},
            method = {RequestMethod.POST, RequestMethod.PATCH, RequestMethod.HEAD})
    public void upload(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse)
            throws IOException, TusException {
        tusFileUploadService.process(servletRequest, servletResponse);

        String uploadUrl = servletRequest.getRequestURI();
        UploadInfo uploadInfo = this.tusFileUploadService.getUploadInfo(uploadUrl);

        if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {

            try (InputStream inputStream = this.tusFileUploadService.getUploadedBytes(uploadUrl)) {
                String fileName = uploadInfo.getFileName();
                String fileUrl = "http://192.168.1.4:8089/files/uploads/" + fileName;

                Path output = Paths.get(staticBasePath + "/uploads").resolve(fileName);
                Files.copy(inputStream, output, StandardCopyOption.REPLACE_EXISTING);

                String dsNodeEncoded = uploadInfo.getMetadata().get("dsNodeEncoded");
                DSNodeDto dsNodeDto = objectMapper.readValue(dsNodeEncoded, DSNodeDto.class);
                dsNodeDto.setNodePath(output.toString());
                dsNodeDto.setUploadId(uploadInfo.getId().toString());

                DSNodeDto dsNode = dataSpaceDataService.create(dsNodeDto);

                servletResponse.setHeader(X_LOCATION_HEADER_KEY, fileUrl);
                servletResponse.setHeader(X_NODE_ID_HEADER_KEY, String.valueOf(dsNode.getId()));

                log.info("Uploaded file to {}", output.toString());
            }

            this.tusFileUploadService.deleteUpload(uploadUrl);
        }
    }

    @Retryable(value = UploadAlreadyLockedException.class, backoff = @Backoff(delay = 10_000L))
    @DeleteMapping("/upload/{uploadUrl}")
    public ResponseEntity<Object> deleteDuringUpload(@PathVariable String uploadUrl, @RequestParam String fileName)
            throws IOException, TusException {
        UploadInfo uploadInfo = this.tusFileUploadService.getUploadInfo(uploadUrl);

        if (uploadInfo == null && !StringUtils.hasLength(fileName)) {
            return ResponseEntity.notFound().build();
        }

        if (uploadInfo != null) {
            Files.deleteIfExists(Paths.get(staticBasePath + "/uploads").resolve(uploadInfo.getFileName()));
            this.tusFileUploadService.deleteUpload(uploadUrl);
        } else if (StringUtils.hasLength(fileName)) {
            Files.deleteIfExists(Paths.get(staticBasePath + "/uploads").resolve(fileName));
        }
        dataSpaceDataService.deleteByUploadId(uploadUrl);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public void delete(@RequestParam String nodeId, @RequestParam String fileName) throws IOException {
        dataSpaceDataService.deleteByNodeId(Long.parseLong(nodeId));
        Files.deleteIfExists(Paths.get(staticBasePath + "/uploads").resolve(fileName));
    }
}
