package com.wu.euwallet.duplicatecheck.exception.exceptiontype;

public class WUServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final String errorMessage;
    private final Throwable rootCause;
    private final WUExceptionType type;


    public WUServiceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.rootCause = cause;
        this.type = null;
    }

    public WUServiceException(WUExceptionType type) {
        super(type.getMessage());
        this.type = type;
        this.errorCode = type.getCode();
        this.errorMessage = type.getMessage();
        this.rootCause = null;
    }

    public WUServiceException(WUExceptionType type, String customMessage) {
        super(customMessage);
        this.type = type;
        this.errorCode = type.getCode();
        this.errorMessage = customMessage;
        this.rootCause = null;
    }

    public WUServiceException(WUExceptionType type, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.type = type;
        this.errorCode = type.getCode();
        this.errorMessage = customMessage;
        this.rootCause = cause;
    }

    public WUServiceException(String errorCode, String message, WUExceptionType type, Throwable cause) {
        super(message, cause);
        this.type = type;
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.rootCause = cause;
    }

    public WUServiceException(String dlqSerializationFailed, String errorCode, String message, Throwable rootCause, WUExceptionType type) {
        this.errorCode = errorCode;
        this.errorMessage = message;

        this.rootCause = rootCause;
        this.type = type;
    }

    @SuppressWarnings("unused")
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

    @SuppressWarnings("unused")
    public WUExceptionType getType() {
        return type;
    }


    @SuppressWarnings("unused")
    public Throwable getRootCause() {
        return rootCause;
    }
}
