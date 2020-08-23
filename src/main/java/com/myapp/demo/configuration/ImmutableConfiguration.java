package com.myapp.demo.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "admin.configuration")
@ConstructorBinding
public class ImmutableConfiguration {
    private final long workStartHour;
    private final long workEndHour;
    private final long workHours;

    public ImmutableConfiguration(long workStartHour, long workEndHour, long workHours) {
        this.workStartHour = workStartHour;
        this.workEndHour = workEndHour;
        this.workHours = workHours;
    }

    public long getWorkStartHour() {
        return workStartHour;
    }

    public long getWorkEndHour() {
        return workEndHour;
    }

    public long getWorkHours() {
        return workHours;
    }

}
