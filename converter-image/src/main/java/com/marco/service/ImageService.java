package com.marco.service;

import com.marco.exception.ConvertException;
import com.marco.interfaces.ConvertService;
import com.marco.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImageService implements ConvertService {

    private final ImageUtil imageUtils;

    @Autowired
    public ImageService(ImageUtil imageUtils) {
        this.imageUtils = imageUtils;
    }

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream, String targetFormat)
            throws ConvertException {

        try {
            byte[] imageData = inputStream.readAllBytes();
            //validate writeable format
            imageUtils.validateWriteableFormat(targetFormat);
            //validate readeable format
            imageUtils.validateReadableFormat(
                            imageUtils.getImageFormat(new ByteArrayInputStream(imageData)));
            //img convert
            imageUtils.writeImage(imageUtils.readImage(new ByteArrayInputStream(imageData)),
                    targetFormat, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConvertException("Error during image conversion", e);
        }
    }
    public Set<String> getWriteableFormats(){
        return imageUtils.getWriteableFormats().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }
    public Set<String> getReadableFormats(){
        return imageUtils.getReadableFormats().stream()
                       .map(String::toLowerCase)
                       .collect(Collectors.toSet());
    }
}
