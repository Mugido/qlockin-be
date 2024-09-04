package com.decagosq022.qlockin.exceptions;

public class NotClockedInException extends RuntimeException {
    public NotClockedInException(String message) {
        super(message);
    }
}
