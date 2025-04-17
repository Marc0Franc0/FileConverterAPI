package com.marco.exception;

public class WriteFileException extends ConvertException {

    public WriteFileException(String message) {
        super(message);
    }

    public WriteFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
