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
    private ImmutableConfiguration configuration;
    private final LocalTime workStart;
    private final LocalTime workEnd;
    private final long workHours;

    IssueTrackingSystemService(@Autowired ImmutableConfiguration configuration) {
        this.configuration = configuration;
        this.workStart = LocalTime.of(configuration.getWorkStartHour(), 0);
        this.workEnd = LocalTime.of(configuration.getWorkEndHour(), 0);
        this.workHours = configuration.getWorkHours();
    }

    public LocalDateTime calculateDueDate(LocalDateTime submissionDate, Duration turnaroundTime) throws CalculateDueDateException {

        Duration duration = Duration.ZERO;

        validateSubmissionDate(submissionDate);
        validateTurnaroundTime(turnaroundTime);

        if (checkIfIssueFitsCurrentDay(turnaroundTime, submissionDate)){
            duration = duration.plusHours(turnaroundTime.toHours());
        } else {
            duration = duration.plusHours(getRemainderDaysNeededForWorkInHours(turnaroundTime.toHours(), submissionDate));
            duration = duration.plusDays(getFullDaysNeededForWork(turnaroundTime.toHours(), submissionDate.getDayOfWeek()));
        }

        LocalDateTime dueDate = submissionDate.plus(duration);

        return dueDate;
    }

    private boolean checkIfIssueFitsCurrentDay(Duration turnaroundTime, LocalDateTime submissionDate) {
        return turnaroundTime.toHours() <= workHours && submissionDate.plus(turnaroundTime).toLocalTime().compareTo(workEnd) <= 0;
    }

    private long getRemainderDaysNeededForWorkInHours(long turnaroundHours, LocalDateTime submissionDate) {
        long hoursNeededForWork = turnaroundHours % workHours;
        long actualHoursOfWork = 0;
        LocalDateTime currentTime;

        for (long i = hoursNeededForWork; i > 0;) {
            currentTime = submissionDate.plus(Duration.ofHours(actualHoursOfWork + 1));
            if(isWorkingDay(currentTime.getDayOfWeek()) && isWorkingHour(currentTime.toLocalTime())) {
                i--;
            }
            actualHoursOfWork++;
        }

        return actualHoursOfWork;
    }

    private long getFullDaysNeededForWork(long turnaroundHours, DayOfWeek dayOfWeek) {
        long daysNeededForWork = turnaroundHours / workHours;
        long actualDaysOfWork = 0;

        for (long i = daysNeededForWork; i > 0;) {
            if (isWorkingDay(dayOfWeek.plus(1))) {
                i--;
                dayOfWeek = dayOfWeek.plus(1);
                actualDaysOfWork++;
            } else {
                actualDaysOfWork += 2;
                dayOfWeek = dayOfWeek.plus(2);
            }
        }
        return actualDaysOfWork;
    }

    private void validateSubmissionDate(LocalDateTime submissionDate) throws CalculateDueDateException {
        if (submissionDate == null || !isWorkingDay(submissionDate.getDayOfWeek()) || !isWorkingHour(submissionDate.toLocalTime())) {
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

    private boolean isTurnaroundTimeGreaterThanZero(Duration turnaroundTime) {
        return !turnaroundTime.isNegative() && !turnaroundTime.isZero();
    }

    private boolean isWorkingHour(LocalTime time) {
        return time.compareTo(workStart) >= 0 && time.compareTo(workEnd) <= 0;
    }

    private boolean isWorkingDay(DayOfWeek dayOfWeek) {
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

}