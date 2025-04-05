package com.marco.service;

import com.marco.exception.ConvertException;
import com.marco.interfaces.ConvertService;
import com.marco.util.ImageUtil;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

@Service
public class ImageService implements ConvertService {

    private final ImageUtil imageUtils;

    public ImageService(ImageUtil imageUtils) {
        this.imageUtils = imageUtils;
    }

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream, String targetFormat)
            throws ConvertException {

        try {
            //validate format
            imageUtils.validateImageFormat(targetFormat);
            // Reads the input stream and converts it into a BufferedImage object
            BufferedImage image = readImage(inputStream);
            //validate img
            validateImg(image);
            //img convert
            imageUtils.writeImage(image,targetFormat,outputStream);
           // ImageIO.write(image, targetFormat, outputStream);

        } catch (Exception e) {
            throw new ConvertException("Error during image conversion", e);
        }
    }

    private void validateImg(BufferedImage image) throws ConvertException {
        if (image == null) {
            throw new ConvertException("Invalid file for image conversion");
        }
    }

    private BufferedImage readImage(InputStream inputStream) throws ConvertException, IOException {
        // Reads the input stream into a BufferedImage object
        BufferedImage image = ImageIO.read(inputStream);
        if (image == null) {
            // Throws an exception if the file cannot be interpreted as a valid image
            throw new ConvertException("Invalid file for image conversion");
        }
        return image;
    }

}
