package ru.ifmo.se.lab2;


/**
 * Custom exception, thrown when client sends wrong fields
 */
public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}