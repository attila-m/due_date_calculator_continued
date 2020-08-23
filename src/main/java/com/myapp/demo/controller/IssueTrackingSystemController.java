package com.myapp.demo.controller;

import com.myapp.demo.exception.CalculateDueDateException;
import com.myapp.demo.model.Issue;
import com.myapp.demo.service.IssueTrackingSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class IssueTrackingSystemController {

    @Autowired
    IssueTrackingSystemService service;

    @GetMapping("/issue/getduedate")
    @ResponseBody
    public String getIssueDueDate(@RequestParam(name = "submission") String submission,
                                  @RequestParam(name = "turnaround") long turnaround) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime submissionDate = LocalDateTime.parse(submission, formatter);
        Duration turnaroundTime = Duration.ofHours(turnaround);

        LocalDateTime dueDate = null;
        try {
            dueDate = service.calculateDueDate(submissionDate, turnaroundTime);
        } catch (CalculateDueDateException e) {
            return e.getMessage();
        }

        Issue issue = new Issue(dueDate);

        return "Issue with a due date: "
                + issue.getDueDate()
                + " created with the following id: "
                + issue.getId();
    }

}
