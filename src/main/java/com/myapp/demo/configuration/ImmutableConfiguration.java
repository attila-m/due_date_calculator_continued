package com.myapp.demo.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "admin.configuration")
@ConstructorBinding
public class ImmutableConfiguration {
    private final int workStartHour;
    private final int workEndHour;
    private final long workHours;

    public ImmutableConfiguration(int workStartHour, int workEndHour, long workHours) {
        this.workStartHour = workStartHour;
        this.workEndHour = workEndHour;
        this.workHours = workHours;
    }

    public int getWorkStartHour() {
        return workStartHour;
    }

    public int getWorkEndHour() {
        return workEndHour;
    }

    public long getWorkHours() {
        return workHours;
    }
}
