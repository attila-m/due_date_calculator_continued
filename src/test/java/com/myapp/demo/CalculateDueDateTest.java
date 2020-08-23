package com.myapp.demo;

import com.myapp.demo.exception.CalculateDueDateException;
import com.myapp.demo.service.IssueTrackingSystemService;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CalculateDueDateTest {

    @Autowired
    IssueTrackingSystemService issueTrackingSystemService;

    @Value("${admin.configuration.workStartHour}")
    private int workStartHour;
    @Value("${admin.configuration.workEndHour}")
    private int workEndHour;
    @Value("${admin.configuration.workHours}")
    private int workHours;

    private LocalDateTime submissionDateAtTheEndOfTheStartHour;
    private LocalDateTime submissionDateAtTheEndOfTheDay;
    private LocalDateTime submissionDateOutsideWorkingHours;
    private LocalDateTime submissionDateOutsideWorkingDays;
    private Duration positiveWholeDayTurnaround;

    @BeforeEach
    public void initTestData() {
        submissionDateAtTheEndOfTheStartHour = LocalDateTime.of(2020, Month.AUGUST, 20, workStartHour, 59);
        submissionDateAtTheEndOfTheDay = LocalDateTime.of(2020, Month.AUGUST, 20, workEndHour, 0);
        submissionDateOutsideWorkingHours = LocalDateTime.of(2020, Month.AUGUST, 20, workEndHour, 1);
        submissionDateOutsideWorkingDays = LocalDateTime.of(2020, Month.AUGUST, 22, workStartHour, 0);
        positiveWholeDayTurnaround = Duration.ofHours(workHours);
    }

    @Test
    public void shouldReturnCorrectDueDateWhenTurnaroundTimeIsOneHour() throws CalculateDueDateException {
        Duration turnaroundTime = Duration.ofHours(1);
        LocalDateTime expectedDueDate = LocalDateTime.of(2020, Month.AUGUST, 20, 10, 59);
        LocalDateTime actualDueDate = issueTrackingSystemService.calculateDueDate(submissionDateAtTheEndOfTheStartHour, turnaroundTime);
        assertEquals(expectedDueDate, actualDueDate);
    }

    @Test
    public void shouldReturnDueDateWhenWorkIsDoneOnTheSameDay() throws CalculateDueDateException {
        Duration turnaroundTime = Duration.ofHours(workHours - 1);
        LocalDateTime expectedResolveDate = LocalDateTime.of(2020, Month.AUGUST, 20, 16, 59);
        LocalDateTime actualResolveDate = issueTrackingSystemService.calculateDueDate(submissionDateAtTheEndOfTheStartHour, turnaroundTime);
        assertEquals(expectedResolveDate, actualResolveDate);
    }

    @Test
    public void shouldReturnDueDateWhenTurnaroundTimeIsOneDay() throws CalculateDueDateException {
        Duration turnaroundTimeFullWorkDay = Duration.ofHours(workHours);
        LocalDateTime expectedDueDateWithOneDay = LocalDateTime.of(2020, Month.AUGUST, 21, 9, 59);
        LocalDateTime actualDueDateWithOneDay = issueTrackingSystemService.calculateDueDate(submissionDateAtTheEndOfTheStartHour, turnaroundTimeFullWorkDay);
        assertEquals(expectedDueDateWithOneDay, actualDueDateWithOneDay);
    }

    @Test
    public void shouldReturnDueDateWhenTurnaroundTimeIsTwoDays() throws CalculateDueDateException {
        Duration turnaroundTimeTwoFullWorkDays = Duration.ofHours(workHours * 2);
        LocalDateTime expectedDueDateWithTwoDays = LocalDateTime.of(2020, Month.AUGUST, 24, 9, 59);
        LocalDateTime actualDueDateWithTwoDays = issueTrackingSystemService.calculateDueDate(submissionDateAtTheEndOfTheStartHour, turnaroundTimeTwoFullWorkDays);
        assertEquals(expectedDueDateWithTwoDays, actualDueDateWithTwoDays);
    }

    @Test
    public void shouldReturnDueDateWhenTurnaroundTimeIsAtDayEnd() throws CalculateDueDateException {
        LocalDateTime submissionDate = LocalDateTime.of(2020, Month.AUGUST, 20, workStartHour, 0);
        LocalDateTime expectedDueDate = LocalDateTime.of(2020, Month.AUGUST, 20, workEndHour, 0);
        LocalDateTime actualDueDate = issueTrackingSystemService.calculateDueDate(submissionDate, positiveWholeDayTurnaround);
        assertEquals(expectedDueDate, actualDueDate);
    }

    @Test
    public void shouldReturnDueDateWhenTurnaroundTimeIsAtDayEndPlusOneMinute() throws CalculateDueDateException {
        LocalDateTime submissionDate = LocalDateTime.of(2020, Month.AUGUST, 20, workStartHour, 1);
        LocalDateTime expectedDueDate = LocalDateTime.of(2020, Month.AUGUST, 21, workStartHour, 1);
        LocalDateTime actualDueDate = issueTrackingSystemService.calculateDueDate(submissionDate, positiveWholeDayTurnaround);
        assertEquals(expectedDueDate, actualDueDate);
    }

    @Test
    public void shouldReturnCorrectDueDateWhenTurnaroundTimeIsNextDay() throws CalculateDueDateException {
        Duration turnaroundTime = Duration.ofHours(workHours + 1);
        LocalDateTime expectedDueDate = LocalDateTime.of(2020, Month.AUGUST, 21, 10, 59);
        LocalDateTime actualDueDate = issueTrackingSystemService.calculateDueDate(submissionDateAtTheEndOfTheStartHour, turnaroundTime);
        assertEquals(expectedDueDate, actualDueDate);
    }

    @Test
    public void shouldReturnCorrectDueDateWhenTurnaroundTimeIsAFullWeek() throws CalculateDueDateException {
        Duration turnaroundTime = Duration.ofHours(40);
        LocalDateTime expectedDueDate = LocalDateTime.of(2020, Month.AUGUST, 27, 9, 59);
        LocalDateTime actualDueDate = issueTrackingSystemService.calculateDueDate(submissionDateAtTheEndOfTheStartHour, turnaroundTime);
        assertEquals(expectedDueDate, actualDueDate);
    }

    @Test
    public void shouldReturnDueDateWhenTurnaroundTimeIsNextWeek() throws CalculateDueDateException {
        Duration turnaroundTime = Duration.ofHours(39);
        LocalDateTime submissionDate = LocalDateTime.of(2020, Month.AUGUST, 20, 13, 12);
        LocalDateTime expectedDueDate = LocalDateTime.of(2020, Month.AUGUST, 27, 12, 12);
        LocalDateTime actualDueDate = issueTrackingSystemService.calculateDueDate(submissionDate, turnaroundTime);
        assertEquals(expectedDueDate, actualDueDate);
    }

    @Test
    public void shouldReturnDueDateWhenDueDateIsNextMonth() throws CalculateDueDateException {
        Duration turnaroundTime = Duration.ofHours(169);
        LocalDateTime submissionDate = LocalDateTime.of(2020, Month.AUGUST, 3, 9, 0);
        LocalDateTime expectedDueDate = LocalDateTime.of(2020, Month.SEPTEMBER, 1, 10, 0);
        LocalDateTime actualDueDate = issueTrackingSystemService.calculateDueDate(submissionDate, turnaroundTime);
        assertEquals(expectedDueDate, actualDueDate);
    }

    @Test
    public void shouldNotThrowExceptionWhenTurnaroundTimeIsPositive() throws CalculateDueDateException {
        issueTrackingSystemService.calculateDueDate(submissionDateAtTheEndOfTheStartHour, positiveWholeDayTurnaround);
    }

    @Test
    public void shouldThrowExceptionWhenTurnaroundTimeIsZero() {
        assertThrows(Exception.class, () -> issueTrackingSystemService.calculateDueDate(submissionDateAtTheEndOfTheStartHour, Duration.ZERO));
    }

    @Test
    public void shouldThrowExceptionWhenTurnaroundTimeIsNegative() {
        assertThrows(Exception.class, () -> issueTrackingSystemService.calculateDueDate(submissionDateAtTheEndOfTheStartHour, Duration.ofHours(-5)));
    }

    @Test
    public void shouldNotThrowExceptionWhenSubmissionDateIsOnAWorkday() throws CalculateDueDateException {
        issueTrackingSystemService.calculateDueDate(submissionDateAtTheEndOfTheDay, positiveWholeDayTurnaround);
    }

    @Test
    public void shouldThrowExceptionWhenSubmissionDateIsOutsideOfWorkHoursOrOnTheWeekend() {
        assertThrows(Exception.class, () -> issueTrackingSystemService.calculateDueDate(submissionDateOutsideWorkingHours, positiveWholeDayTurnaround));
    }

    @Test
    public void shouldThrowExceptionWhenSubmissionDateIsOnWeekend() {
        assertThrows(Exception.class, () -> issueTrackingSystemService.calculateDueDate(submissionDateOutsideWorkingDays, positiveWholeDayTurnaround));
    }

    @Test
    public void shouldThrowExceptionWhenSubmissionDateIsNull() {
        assertThrows(Exception.class, () -> issueTrackingSystemService.calculateDueDate(null, positiveWholeDayTurnaround));
    }

}