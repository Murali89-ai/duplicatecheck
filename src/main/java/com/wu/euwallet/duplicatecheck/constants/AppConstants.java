package com.wu.euwallet.duplicatecheck.constants;

/**
 * Application-wide constant values used across services, adaptors, and config layers.
 */
public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // Header Constants
    public static final String AUTHORIZATION = "Authorization";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String CORRELATION_ID = "correlationId";
    public static final String APPLICATION_ID = "applicationId";
    public static final String CHANNEL = "channel";
    public static final String DEVICE_ID = "deviceId";
    public static final String LOCALE = "locale";

    // Content Types
    public static final String CONTENT_TYPE_JSON = "application/json";

    // General Constants
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";

    // Biz Action Flags
    public static final String BIZ_ENABLED = "Y";

    // Kafka Event Types
    public static final String EVENT_TYPE_PROFILE_UPDATE_SUCCESS = "PROFILE_UPDATE_SUCCESS";
    public static final String EVENT_TYPE_DUPLICATE_FOUND = "DUPLICATE_FOUND";
    public static final String EVENT_TYPE_BIZ_FAILURE = "BIZ_FAILURE";
    public static final String EVENT_TYPE_MARQETA_FAILURE = "MARQETA_FAILURE";
}
