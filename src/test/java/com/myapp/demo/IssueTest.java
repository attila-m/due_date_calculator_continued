package com.myapp.demo;

import com.myapp.demo.model.Issue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IssueTest {

    @Test
    public void createInstanceTest() {
        Issue issue = new Issue(LocalDateTime.now());
        assertNotNull(issue);
    }

    @Test
    public void randomIdGeneratedTest() {
        Issue issue = new Issue(LocalDateTime.now());
        assertNotNull(issue.getId());
        assertNotEquals("", issue.getId());
    }

}