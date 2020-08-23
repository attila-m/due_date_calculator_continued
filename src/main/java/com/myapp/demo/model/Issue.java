package com.myapp.demo.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Issue {

    String id;
    LocalDateTime dueDate;


    public Issue(LocalDateTime dueDate) {
        this.id = UUID.randomUUID().toString();
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
