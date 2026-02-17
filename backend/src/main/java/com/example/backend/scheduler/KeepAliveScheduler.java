package com.example.backend.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class KeepAliveScheduler {

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveScheduler.class);

    @Value("${RENDER_EXTERNAL_URL:}")
    private String renderUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 840000) // 14 minutes in milliseconds
    public void keepAlive() {
        if (renderUrl != null && !renderUrl.isEmpty()) {
            try {
                String healthUrl = renderUrl + "/api/public/health";
                logger.info("Pinging self at: {}", healthUrl);
                restTemplate.getForEntity(healthUrl, String.class);
                logger.info("Self-ping successful");
            } catch (Exception e) {
                logger.error("Self-ping failed: {}", e.getMessage());
            }
        } else {
            logger.debug("Self-ping skipped: RENDER_EXTERNAL_URL not set");
        }
    }
}
