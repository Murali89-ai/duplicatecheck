package com.wu.euwallet.duplicatecheck.utils;

import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType.VALIDATION_FAILED;

public class ValidationUtils {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            throw new WUServiceException(VALIDATION_FAILED, sb.toString());
        }
    }
}
