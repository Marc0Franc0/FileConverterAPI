package com.marco.util;

import com.marco.exception.ConvertException;
import org.springframework.stereotype.Component;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.IIOImage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
public class ImageUtil {

    private final List<String> supportedFormats = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "tiff");

    public void validateImageFormat(String format) throws ConvertException {
        // Check if the format is not supported
        if (!supportedFormats.contains(format.toLowerCase())) {
            throw new ConvertException("Invalid format: " + format);
        }
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

    private ImageWriter getImageWriter(String targetFormat) throws ConvertException {
        // Fetches an iterator of ImageWriters that support the target format
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(targetFormat);
        if (!writers.hasNext()) {
            // Throws an exception if no writer is available for the target format
            throw new ConvertException("No writer found for the format: " + targetFormat);
        }
        // Returns the first available ImageWriter
        return writers.next();
    }



    public void writeImage(BufferedImage image, String targetFormat, OutputStream outputStream)
            throws ConvertException, IOException {
        // Get an iterator for ImageWriters that support the target format
        ImageWriter writer = getImageWriter(targetFormat);
        ImageWriteParam param = writer.getDefaultWriteParam();
        // Remove the alpha channel if present
        image = removeAlphaChannel(image);
        // Write the image using the specific writer
        writer.setOutput(ImageIO.createImageOutputStream(outputStream));
        writer.write(null, new IIOImage(image, null, null), param);
    }
}