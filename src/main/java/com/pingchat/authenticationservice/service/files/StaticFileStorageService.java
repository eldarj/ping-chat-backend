package com.pingchat.authenticationservice.service.files;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// TODO: Remove and use TusService for any uploads
@Slf4j
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

    private static final int MAX_IMAGE_SIZE = 750;

    public void delete(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(staticBasePath + "/uploads/" + fileName));
    }

    public String saveProfileImage(MultipartFile file) throws IOException, ImageProcessingException {
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

    private BufferedImage scaleImage(MultipartFile file) throws IOException, ImageProcessingException {
        BufferedImage uploadedBufferedImage = ImageIO.read(file.getInputStream());

        int orientation = getImageOrientation(file);

        BufferedImage rotatedBufferedImage = getRotatedImage(uploadedBufferedImage, orientation);
        int originalHeight = rotatedBufferedImage.getHeight();
        int originalWidth = rotatedBufferedImage.getWidth();

        double ratio;
        int targetWidth, targetHeight;

        if (originalHeight > originalWidth) {
            ratio = (double) originalHeight / originalWidth;
            targetWidth = (int) (MAX_IMAGE_SIZE / ratio);
            targetHeight = MAX_IMAGE_SIZE;
        } else {
            ratio = (double) originalWidth / originalHeight;
            targetWidth = MAX_IMAGE_SIZE;
            targetHeight = (int) (MAX_IMAGE_SIZE / ratio);
        }

        Image scaledImage = rotatedBufferedImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputBufferedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputBufferedImage.getGraphics().drawImage(scaledImage, 0, 0 , null);

        return outputBufferedImage;
    }

    private int getImageOrientation(MultipartFile file) throws IOException, ImageProcessingException {
        Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
        ExifIFD0Directory exifDirectory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        int orientation = 1;
        try {
            orientation = exifDirectory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        } catch (Exception exception) {
            log.warn("Could not get orientation", exception);
        }

        return orientation;
    }

    private BufferedImage getRotatedImage(BufferedImage originalBufferedImage, int orientation) {
        AffineTransform affineTransform = getExifTransformation(orientation,
                originalBufferedImage.getWidth(),
                originalBufferedImage.getHeight());

        AffineTransformOp transformation = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BICUBIC);

        ColorModel colorModel = null;

        if (originalBufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY) {
            colorModel = originalBufferedImage.getColorModel();
        }

        BufferedImage destinationImage = transformation.createCompatibleDestImage(originalBufferedImage, colorModel);
        Graphics2D g = destinationImage.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());

        destinationImage = transformation.filter(originalBufferedImage, destinationImage);

        return destinationImage;
    }

    public static AffineTransform getExifTransformation(int orientation, double width, double height) {

        AffineTransform t = new AffineTransform();

        switch (orientation) {
            case 1:
                break;
            case 2: // Flip X
                t.scale(-1.0, 1.0);
                t.translate(-width, 0);
                break;
            case 3: // PI rotation
                t.translate(width, height);
                t.rotate(Math.PI);
                break;
            case 4: // Flip Y
                t.scale(1.0, -1.0);
                t.translate(0, -height);
                break;
            case 5: // - PI/2 and Flip X
                t.rotate(-Math.PI / 2);
                t.scale(-1.0, 1.0);
                break;
            case 6: // -PI/2 and -width
                t.translate(height, 0);
                t.rotate(Math.PI / 2);
                break;
            case 7: // PI/2 and Flip
                t.scale(-1.0, 1.0);
                t.translate(-height, 0);
                t.translate(0, width);
                t.rotate(  3 * Math.PI / 2);
                break;
            case 8: // PI / 2
                t.translate(0, width);
                t.rotate(  3 * Math.PI / 2);
                break;
        }

        return t;
    }
}
