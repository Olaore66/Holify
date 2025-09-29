package Holify.holify.config;

import Holify.holify.dto.OtpRequest;
import Holify.holify.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {
    private static final Logger log = LoggerFactory.getLogger(UserEventListener.class);

    @Autowired
    private OtpService otpService;

    @Async
    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setEmail(event.getEmail());
        otpRequest.setPurpose(event.getPurpose());
        otpService.sendOtp(otpRequest).thenAccept(response -> {
            if (!response.isSuccess()) {
                log.error("Failed to send OTP: {}", response.getMessage());
            }
        }).exceptionally(throwable -> {
            log.error("Error processing OTP: {}", throwable.getMessage());
            return null;
        });
    }
}