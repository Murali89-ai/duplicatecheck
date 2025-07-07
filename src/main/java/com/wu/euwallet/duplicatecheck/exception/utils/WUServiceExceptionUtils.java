package com.wu.euwallet.duplicatecheck.exception.utils;

import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUServiceException;
import org.springframework.stereotype.Component;

@Component
public class WUServiceExceptionUtils {

    public static WUServiceException buildWUServiceException(WUExceptionType type, String message, Throwable cause) {
        return new WUServiceException(type, message, cause);
    }

    public static WUServiceException buildWUServiceException(WUExceptionType type, String message) {
        return new WUServiceException(type, message);
    }

    public static WUServiceException buildWUServiceException(WUExceptionType type) {
        return new WUServiceException(type);
    }

    public static WUServiceException buildWUServiceException(String errorCode, String message, Throwable type) {
        return new WUServiceException(errorCode,message,type);
    }

    public static Exception build(WUExceptionType wuExceptionType, Exception ex) {
        return new WUServiceException(wuExceptionType,ex.getMessage());
    }

    public static Exception buildJsonProcessingException(String dlqSerializationFailed, String message) {
        return new WUServiceException(WUExceptionType.DLQ_SERIALIZATION_FAILED,message);
    }

    public static Exception buildKafkaException(String dlqKafkaError, String message) {
        return new WUServiceException(WUExceptionType.DLQ_KAFKA_ERROR,message);
    }
}
