package com.example.gptchatsaver.exception;

public class ScanningException extends RuntimeException{

    public ScanningException(String message) {
        super(message);
    }

    public ScanningException(String message, Throwable cause) {
        super(message, cause);
    }

}
