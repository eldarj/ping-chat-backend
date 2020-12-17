package com.pingchat.authenticationservice.api.rest;

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
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/data-space")
public class DataSpaceController {
    @Value("${service.static-base-path}")
    protected String staticBasePath;

    private static final String X_LOCATION_HEADER_KEY = "X-Location";

    private final TusFileUploadService tusFileUploadService;

    public DataSpaceController(TusFileUploadService tusFileUploadService) {
        this.tusFileUploadService = tusFileUploadService;
    }

    // Hook into db
    @RequestMapping(value = {"/upload", "/upload/**"},
            method = {RequestMethod.POST, RequestMethod.PATCH, RequestMethod.HEAD})
    public void upload(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse)
            throws IOException, TusException {
        tusFileUploadService.process(servletRequest, servletResponse);

        String uploadUrl = servletRequest.getRequestURI();
        UploadInfo uploadInfo = this.tusFileUploadService.getUploadInfo(uploadUrl);

        if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {

            try (InputStream inputStream = this.tusFileUploadService.getUploadedBytes(uploadUrl)) {
                Path output = Paths.get(staticBasePath + "/uploads").resolve(uploadInfo.getFileName());
                Files.copy(inputStream, output, StandardCopyOption.REPLACE_EXISTING);

                servletResponse.setHeader(X_LOCATION_HEADER_KEY,
                        "http://192.168.1.4:8089/files/uploads/" + uploadInfo.getFileName());

                log.info("Saved uploaded file to {}", output.toString());
            }

            this.tusFileUploadService.deleteUpload(uploadUrl);
        }
    }

    // TODO: Hook into db
    @Retryable(value = UploadAlreadyLockedException.class, backoff = @Backoff(delay = 10_000L))
    @DeleteMapping("/upload/{uploadUrl}")
    public ResponseEntity<Object> delete(@PathVariable String uploadUrl, @RequestParam String fileName) throws IOException,
            TusException {
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

        return ResponseEntity.noContent().build();
    }
}
