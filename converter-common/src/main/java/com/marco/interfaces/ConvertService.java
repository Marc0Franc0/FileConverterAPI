package com.marco.interfaces;


import com.marco.exception.ConvertException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ConvertService {
    void convert(InputStream input, OutputStream output, String targetFormat) throws ConvertException;
}
