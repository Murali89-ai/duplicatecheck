package com.wu.euwallet.duplicatecheck.scheduler;

import com.wu.euwallet.duplicatecheck.service.AuthTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenScheduler {

    private final AuthTokenService authTokenService;

    // Run every 45 minutes
    @Scheduled(fixedRateString = "${scheduler.frequency:2700000}") // 2700000 ms = 45 min
    public void refreshJwtToken() {
        try {
            String jwtToken = authTokenService.refreshToken(true);
            log.info("Refreshed JWT Token: {}", jwtToken);
        } catch (Exception e) {
            log.error("Failed to refresh JWT token", e);
        }
    }
}
