package com.wu.euwallet.duplicatecheck.constants;

public final class DuplicateCheckConstants {

    private DuplicateCheckConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String DUPLICATE_CHECK_FLOW_NAME = "DUPLICATE_CHECK_FLOW";
    public static final String DUPLICATE_CHECK_SUCCESS_CODE = "DUPLICATE_CHECK_SUCCESS";
    public static final String DUPLICATE_CHECK_ERROR_CODE = "DUPLICATE_CHECK_ERROR";
    public static final String DUPLICATE_FOUND = "DUPLICATE_FOUND";
    public static final String DUPLICATE_NOT_FOUND = "DUPLICATE_NOT_FOUND";

    public static final String LOG_CORRELATION_ID = "correlationId";
    public static final String LOG_EXTERNAL_REF_ID = "externalRefId";
    public static final String LOG_FLOW_NAME = "flowName";
    public static final String LOG_EVENT_TYPE = "eventType";
}
