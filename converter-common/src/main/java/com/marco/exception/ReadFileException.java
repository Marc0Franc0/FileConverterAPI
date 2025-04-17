package com.marco.exception;

public class ReadFileException extends ConvertException {

    public ReadFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadFileException(String message) {
        super(message);
    }
}
