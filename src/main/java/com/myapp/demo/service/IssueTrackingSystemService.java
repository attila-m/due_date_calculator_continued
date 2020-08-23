package com.myapp.demo.service;

import com.myapp.demo.configuration.ImmutableConfiguration;
import com.myapp.demo.exception.CalculateDueDateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class IssueTrackingSystemService {

    private static final Logger LOGGER = LogManager.getLogger(IssueTrackingSystemService.class);
    private final LocalTime workStart;
    private final LocalTime workEnd;
    private final long workHours;

    IssueTrackingSystemService(@Autowired ImmutableConfiguration configuration) {
        this.workStart = LocalTime.of(configuration.getWorkStartHour(), 0);
        this.workEnd = LocalTime.of(configuration.getWorkEndHour(), 0);
        this.workHours = configuration.getWorkHours();
    }

    public LocalDateTime calculateDueDate(LocalDateTime submissionDate, Duration turnaroundTime) throws CalculateDueDateException {

        Duration duration = Duration.ZERO;

        validateSubmissionDate(submissionDate);
        validateTurnaroundTime(turnaroundTime);

        if (doesIssueFitCurrentDay(turnaroundTime, submissionDate)){
            duration = duration.plusHours(turnaroundTime.toHours());
        } else {
            duration = duration.plusHours(getRemainderDaysNeededForWorkInHours(turnaroundTime.toHours(), submissionDate));
            duration = duration.plusDays(getFullDaysNeededForWork(turnaroundTime.toHours(), submissionDate.getDayOfWeek()));
        }

        return submissionDate.plus(duration);
    }

    private long getRemainderDaysNeededForWorkInHours(long turnaroundHours, LocalDateTime submissionDate) {
        long actualHoursOfWork = 0;

        for (long i = turnaroundHours % workHours; i > 0;) {
            if(isWorkingTime(submissionDate.plus(Duration.ofHours(actualHoursOfWork + 1)))) {
                i--;
            }
            actualHoursOfWork++;
        }
        return actualHoursOfWork;
    }

    private long getFullDaysNeededForWork(long turnaroundHours, DayOfWeek dayOfWeek) {
        long actualDaysOfWork = 0;

        for (long i = turnaroundHours / workHours; i > 0;) {
            if (isWorkingDay(dayOfWeek.plus(1))) {
                i--;
                actualDaysOfWork += 1;
                dayOfWeek = dayOfWeek.plus(1);
            } else {
                actualDaysOfWork += 2;
                dayOfWeek = dayOfWeek.plus(2);
            }
        }
        return actualDaysOfWork;
    }

    private void validateSubmissionDate(LocalDateTime submissionDate) throws CalculateDueDateException {
        if (submissionDate == null || !isWorkingTime(submissionDate)) {
            String errorMessage = submissionDate + " is outside of working hours. Working hours are between 9.00 to 17.00, from Monday to Friday.";
            LOGGER.error(errorMessage);
            throw new CalculateDueDateException(errorMessage);
        }
    }

    private void validateTurnaroundTime(Duration turnaroundTime) throws CalculateDueDateException {
        if (!isTurnaroundTimeGreaterThanZero(turnaroundTime)) {
            String errorMessage = turnaroundTime + " is not a valid value. Turnaround time should be more than zero.";
            LOGGER.error(errorMessage);
            throw new CalculateDueDateException(errorMessage);
        }
    }

    private boolean doesIssueFitCurrentDay(Duration turnaroundTime, LocalDateTime submissionDate) {
        return turnaroundTime.toHours() <= workHours && submissionDate.plus(turnaroundTime).toLocalTime().compareTo(workEnd) <= 0;
    }

    private boolean isTurnaroundTimeGreaterThanZero(Duration turnaroundTime) {
        return !turnaroundTime.isNegative() && !turnaroundTime.isZero();
    }

    private boolean isWorkingTime(LocalDateTime time) {
        return isWorkingDay(time.getDayOfWeek()) && isWorkingHour(time.toLocalTime());
    }

    private boolean isWorkingHour(LocalTime time) {
        return time.compareTo(workStart) >= 0 && time.compareTo(workEnd) <= 0;
    }

    private boolean isWorkingDay(DayOfWeek dayOfWeek) {
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

}