package com.marco.controller;

import com.marco.exception.ConvertException;
import com.marco.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {
    private final ImageService imgService;

    @Autowired
    public ImageController(ImageService imgService) {
        this.imgService = imgService;
    }

    @PostMapping("/")
    public ResponseEntity<byte[]> convertImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("format") String format) {
        try {
            // se lee el archivo recibido
            ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // se convierte la img
            imgService.convert(inputStream, outputStream, format);

            // headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("image/" + format));
            headers.add("Content-Disposition", "attachment; filename=\"converted." + format + "\"");

            // se retorna la imagen convertida en el cuerpo de la respuesta
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException | ConvertException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }


    }
}
