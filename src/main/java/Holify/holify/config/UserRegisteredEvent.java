package Holify.holify.config;

import org.springframework.context.ApplicationEvent;

public class UserRegisteredEvent extends ApplicationEvent {
    private final String email;
    private final String purpose;

    public UserRegisteredEvent(Object source, String email, String purpose) {
        super(source);
        this.email = email;
        this.purpose = purpose;
    }

    public String getEmail() {
        return email;
    }

    public String getPurpose() {
        return purpose;
    }
}