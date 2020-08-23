package com.myapp.demo;

import com.myapp.demo.exception.CalculateDueDateException;
import com.myapp.demo.service.IssueTrackingSystemService;
import org.apache.tomcat.jni.Local;
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

    @Value("${admin.configuration.workHours}")
    private long workHours;

    private static final LocalDateTime SUBMISSION_DATE = LocalDateTime.of(2020, Month.AUGUST, 20, 9, 59);

    private static final Duration POSITIVE_WHOLE_DAY_TURNAROUND = Duration.ofHours(8);

    private static final LocalDateTime SUBMISSION_DATE_INSIDE_WORKING_HOURS = LocalDateTime.of(2020, Month.AUGUST, 20, 17, 0);
    private static final LocalDateTime SUBMISSION_DATE_OUTSIDE_WORKING_HOURS = LocalDateTime.of(2020, Month.AUGUST, 20, 17, 1);
    private static final LocalDateTime SUBMISSION_DATE_OUTSIDE_WORKING_DAYS = LocalDateTime.of(2020, Month.AUGUST, 22, 13, 0);

    @Test
    public void ShouldReturnSubmissionDatePlusTurnaroundTime() throws CalculateDueDateException {
        Duration turnaroundTime = Duration.ofHours(workHours - 1);

        LocalDateTime expectedResolveDate = LocalDateTime.of(2020, Month.AUGUST, 20, 16, 59);
        LocalDateTime resolveDate = issueTrackingSystemService.calculateDueDate(SUBMISSION_DATE, turnaroundTime);
        assertEquals(expectedResolveDate, resolveDate);
    }

    @Test
    public void ShouldReturnCorrectDueDateWhenTurnaroundTimeIsWholeWorkday() throws CalculateDueDateException {
        Duration turnAroundTimeFullWorkDay = Duration.ofHours(workHours);
        Duration turnAroundTimeTwoFullWorkDays = Duration.ofHours(workHours * 2);

        LocalDateTime expectedDueDateWithOneDay = LocalDateTime.of(2020, Month.AUGUST, 21, 9, 59);
        LocalDateTime actualDueDateWithOneDay = issueTrackingSystemService.calculateDueDate(SUBMISSION_DATE, turnAroundTimeFullWorkDay);
        assertEquals(expectedDueDateWithOneDay, actualDueDateWithOneDay);

        LocalDateTime expectedDueDateWithTwoDays = LocalDateTime.of(2020, Month.AUGUST, 24, 9, 59);
        LocalDateTime actualDueDateWithTwoDays = issueTrackingSystemService.calculateDueDate(SUBMISSION_DATE, turnAroundTimeTwoFullWorkDays);
        assertEquals(expectedDueDateWithTwoDays, actualDueDateWithTwoDays);
    }

    @Test
    public void ShouldReturnCorrectDueDateWhenTurnaroundTimeIsAtDayEnd() throws CalculateDueDateException {
        LocalDateTime submissionDate1 = LocalDateTime.of(2020, Month.AUGUST, 20, 9, 0);
        LocalDateTime submissionDate2 = LocalDateTime.of(2020, Month.AUGUST, 20, 9, 1);

        LocalDateTime expectedDueDate1 = LocalDateTime.of(2020, Month.AUGUST, 20, 17, 0);
        LocalDateTime actualDueDate1 = issueTrackingSystemService.calculateDueDate(submissionDate1, POSITIVE_WHOLE_DAY_TURNAROUND);
        assertEquals(expectedDueDate1, actualDueDate1);

        LocalDateTime expectedDueDate2 = LocalDateTime.of(2020, Month.AUGUST, 21, 9, 1);
        LocalDateTime actualDueDate2 = issueTrackingSystemService.calculateDueDate(submissionDate2, POSITIVE_WHOLE_DAY_TURNAROUND);
        assertEquals(expectedDueDate2, actualDueDate2);
    }

    @Test
    public void ShouldReturnCorrectDueDateWhenTurnaroundTimeIsNotWholeWorkday() throws CalculateDueDateException {
        Duration turnAroundTime1 = Duration.ofHours(9);
        Duration turnAroundTime2 = Duration.ofHours(31);
        Duration turnAroundTime3 = Duration.ofHours(39);

        LocalDateTime expectedDueDate1 = LocalDateTime.of(2020, Month.AUGUST, 21, 10, 59);
        LocalDateTime actualDueDate1 = issueTrackingSystemService.calculateDueDate(SUBMISSION_DATE, turnAroundTime1);
        assertEquals(expectedDueDate1, actualDueDate1);

        LocalDateTime expectedDueDate2 = LocalDateTime.of(2020, Month.AUGUST, 25, 16, 59);
        LocalDateTime actualDueDate2 = issueTrackingSystemService.calculateDueDate(SUBMISSION_DATE, turnAroundTime2);
        assertEquals(expectedDueDate2, actualDueDate2);

        LocalDateTime uniqueSubmissionDate = LocalDateTime.of(2020, Month.AUGUST, 20, 13, 12);

        LocalDateTime expectedDueDate3 = LocalDateTime.of(2020, Month.AUGUST, 27, 12, 12);
        LocalDateTime actualDueDate3 = issueTrackingSystemService.calculateDueDate(uniqueSubmissionDate, turnAroundTime3);
        assertEquals(expectedDueDate3, actualDueDate3);
    }

    @Test
    public void ShouldReturnCorrectDueDateWhenTurnaroundTimeIsOneHour() throws CalculateDueDateException {
        Duration turnAroundTime = Duration.ofHours(1);

        LocalDateTime expectedDueDate1 = LocalDateTime.of(2020, Month.AUGUST, 20, 10, 59);
        LocalDateTime actualDueDate1 = issueTrackingSystemService.calculateDueDate(SUBMISSION_DATE, turnAroundTime);
        assertEquals(expectedDueDate1, actualDueDate1);
    }

    @Test
    public void ShouldReturnCorrectDueDateWhenDueDateIsNextMonth() throws CalculateDueDateException {
        Duration turnAroundTime = Duration.ofHours(169);

        LocalDateTime uniqueSubmissionDate = LocalDateTime.of(2020, Month.AUGUST, 3, 9, 0);

        LocalDateTime expectedDueDate1 = LocalDateTime.of(2020, Month.SEPTEMBER, 1, 10, 0);
        LocalDateTime actualDueDate1 = issueTrackingSystemService.calculateDueDate(uniqueSubmissionDate, turnAroundTime);
        assertEquals(expectedDueDate1, actualDueDate1);
    }

    @Test
    public void ShouldNotThrowExceptionWhenTurnaroundTimeIsPositive() throws CalculateDueDateException {
        issueTrackingSystemService.calculateDueDate(SUBMISSION_DATE, POSITIVE_WHOLE_DAY_TURNAROUND);
    }

    @Test
    public void ShouldThrowExceptionWhenTurnaroundTimeIsNegativeOrZero() {
        assertThrows(Exception.class, () -> issueTrackingSystemService.calculateDueDate(SUBMISSION_DATE, Duration.ZERO));
        assertThrows(Exception.class, () -> issueTrackingSystemService.calculateDueDate(SUBMISSION_DATE, Duration.ofHours(-5)));
    }

    @Test
    public void ShouldNotThrowExceptionWhenSubmissionDateIsOnAWorkday() throws CalculateDueDateException {
        issueTrackingSystemService.calculateDueDate(SUBMISSION_DATE_INSIDE_WORKING_HOURS, POSITIVE_WHOLE_DAY_TURNAROUND);
    }

    @Test
    public void ShouldThrowExceptionWhenSubmissionDateIsOutsideOfWorkHours() {
        assertThrows(Exception.class, () -> issueTrackingSystemService.calculateDueDate(SUBMISSION_DATE_OUTSIDE_WORKING_HOURS, POSITIVE_WHOLE_DAY_TURNAROUND));
    }

    @Test
    public void ShouldThrowExceptionWhenSubmissionDateIsOnWeekend() {
        assertThrows(Exception.class, () -> issueTrackingSystemService.calculateDueDate(SUBMISSION_DATE_OUTSIDE_WORKING_DAYS, POSITIVE_WHOLE_DAY_TURNAROUND));
    }

    @Test
    public void ShouldThrowExceptionWhenSubmissionDateIsNull() {
        assertThrows(Exception.class, () -> issueTrackingSystemService.calculateDueDate(null, POSITIVE_WHOLE_DAY_TURNAROUND));
    }

}