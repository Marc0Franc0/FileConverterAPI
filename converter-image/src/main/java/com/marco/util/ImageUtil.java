package com.marco.util;

import com.marco.exception.ReadFileException;
import com.marco.exception.WriteFileException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.*;
@Getter

@Component
public class ImageUtil {
    private final Set<String> readableFormats;
    private final Set<String> writeableFormats;

    public ImageUtil() {
        this.readableFormats = new HashSet<>(Arrays.asList(ImageIO.getReaderFormatNames()));
        this.writeableFormats = new HashSet<>(Arrays.asList(ImageIO.getWriterFormatNames()));
    }
    @PostConstruct
    public void initFormats() {
        System.out.println("**IMAGES**");
        System.out.println("Readable Formats: " + readableFormats);
        System.out.println("Writable Formats: " + writeableFormats);
    }

    public void validateReadableFormat(String format) throws ReadFileException {
        if (!readableFormats.contains(format.toLowerCase())) {
            throw new ReadFileException("Unsupported format for reading: " + format);
        }
    }

    public void validateWriteableFormat(String format) throws WriteFileException {
        if (!writeableFormats.contains(format.toLowerCase())) {
            throw new WriteFileException("Unsupported format for writing: " + format);
        }
    }


    public BufferedImage readImage(InputStream inputStream) throws ReadFileException, IOException {
        // Reads the input stream into a BufferedImage object
        BufferedImage image = ImageIO.read(inputStream);
        if (image == null) {
            // Throws an exception if the file cannot be interpreted as a valid image
            throw new ReadFileException("Invalid file for image conversion");
        }
        return image;
    }

    private BufferedImage removeAlphaChannel(BufferedImage image) {
        // If the image does not have an alpha channel, return it as is
        if (!image.getColorModel().hasAlpha()) {
            return image;
        }

        // Create a new image without an alpha channel (TYPE_INT_RGB)
        BufferedImage newImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        // Draw the original image onto the new image
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return newImage;
    }

    private ImageWriter getImageWriter(String targetFormat) throws WriteFileException {
        // Fetches an iterator of ImageWriters that support the target format
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(targetFormat);
        if (!writers.hasNext()) {
            // Throws an exception if no writer is available for the target format
            throw new WriteFileException("No writer found for the format: " + targetFormat);
        }
        // Returns the first available ImageWriter
        return writers.next();
    }



    public void writeImage(BufferedImage image, String targetFormat, OutputStream outputStream)
            throws WriteFileException, IOException {
        // Get an iterator for ImageWriters that support the target format
        ImageWriter writer = getImageWriter(targetFormat);
        ImageWriteParam param = writer.getDefaultWriteParam();
        // Remove the alpha channel if present
        image = removeAlphaChannel(image);
        // Write the image using the specific writer
        writer.setOutput(ImageIO.createImageOutputStream(outputStream));
        writer.write(null, new IIOImage(image, null, null), param);
    }

    public String getImageFormat(InputStream inputStream) throws IOException {
        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream)) {
            if (imageInputStream == null) {
                throw new IOException("Invalid image stream");
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
            if (readers.hasNext()) {
                return readers.next().getFormatName();
            } else {
                throw new IOException("Unsupported image format");
            }
        }
    }

}