package com.marco.exception;

/**
 * Excepción personalizada para errores durante el proceso de conversión.
 */
public class ConvertException extends Exception {

    /**
     * @param message el mensaje de error
     */
    public ConvertException(String message) {
        super(message);
    }

    /**
     * @param message el mensaje de error
     * @param cause la causa que originó el error
     */
    public ConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
