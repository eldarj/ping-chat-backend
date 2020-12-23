package com.pingchat.authenticationservice.service.files;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// TODO: Remove and use TusService for any uploads
@Service
public class StaticFileStorageService {
    @Getter
    @Setter
    @Value("${service.profile-images-path}")
    private String profileImagesPath;

    @Getter
    @Setter
    @Value("${service.static-base-path}")
    private String staticBasePath;

    private static final int SCALE_IMAGE_TO_SIZE = 150;

    public void delete(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(staticBasePath + "/uploads/" + fileName));
    }

    //
    
    public String saveProfileImage(MultipartFile file) throws IOException {
        // Create file by path
        String[] split = file.getOriginalFilename().split("/");
        String fileName = split[split.length - 1];

        Path newFilePath = Paths.get(profileImagesPath + "/" + fileName);

        try {
            Files.createFile(newFilePath);
        } catch (Exception ignored) {

        }

        // Scale image
        BufferedImage scaledBufferedImage = scaleImage(file);

        // Wirte image to path
        String path = newFilePath.toString();
        String extension = FilenameUtils.getExtension(path);
        ImageIO.write(scaledBufferedImage, extension, new File(path));

        return fileName;
    }

    private BufferedImage scaleImage(MultipartFile file) throws IOException {
        BufferedImage originalBufferedImage = ImageIO.read(file.getInputStream());

        int originalHeight = originalBufferedImage.getHeight();
        int originalWidth = originalBufferedImage.getWidth();

        double ratio;
        int imageScaleX, imageScaleY;

        if (originalHeight > originalWidth) {
            ratio = (double) originalHeight / originalWidth;
            imageScaleX = (int) (SCALE_IMAGE_TO_SIZE / ratio);
            imageScaleY = SCALE_IMAGE_TO_SIZE;
        } else {
            ratio = (double) originalWidth / originalHeight;
            imageScaleX = SCALE_IMAGE_TO_SIZE;
            imageScaleY = (int) (SCALE_IMAGE_TO_SIZE / ratio);
        }

        Image scaledImage = originalBufferedImage.getScaledInstance(imageScaleX, imageScaleY, Image.SCALE_SMOOTH);
        BufferedImage scaledBufferedImage = new BufferedImage(imageScaleX, imageScaleY, BufferedImage.TYPE_3BYTE_BGR);
        scaledBufferedImage.createGraphics().drawImage(scaledImage, 0, 0 , null);

        return scaledBufferedImage;
    }
}
