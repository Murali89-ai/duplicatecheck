package com.wu.euwallet.duplicatecheck.utils;


import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUServiceException;
import jakarta.validation.ConstraintViolationException;

public class ValidationUtils {

    private ValidationUtils() {
        // Prevent instantiation
    }

    public static String notBlank(String value, String msg) {
        if (value == null || value.trim().isEmpty()) {
            throw new ConstraintViolationException(msg, null);
        }
        return value;
    }

    public static <T> T notNull(T value, String msg) {
        if (value == null) {
            throw new WUServiceException(WUExceptionType.REQUEST_VALIDATION_EXCEPTION, msg);

        }
        return value;
    }
}