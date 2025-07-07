package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "common.redis")
@Data
public class RedisConfigProperties {

    private boolean blockWhenExhausted;
    private int maxIdle;
    private int maxTotal;
    private int minIdle;
    private long maxWaitMillis;
    private long minEvictableIdleTimeMillis;
    private int numTestsPerEvictionRun;
    private long timeBetweenEvictionRunsMillis;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean testWhileIdle;
    private boolean retry;
    private boolean encryptionEnabled;
    private boolean clusterModeEnabled;

    private String readRedisHost;
    private int readRedisHostPort;
    private String readWriteRedisHost;
    private int readWriteRedisHostPort;
    private int databaseIndex;
    private String password;
}
