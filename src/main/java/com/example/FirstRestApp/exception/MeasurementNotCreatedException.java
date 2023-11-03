package com.example.FirstRestApp.exception;

public class MeasurementNotCreatedException extends RuntimeException {
    public MeasurementNotCreatedException(String message) {
        super(message);
    }
}
