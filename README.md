## Issue tracking system

### The application
The application is written in Java/Spring using JUnit for testing and Spring Web for the REST endpoint. Programmed considering Clean Code and TDD approaches.

The application calculates and returns a due date from a submission date and a turnaround time, given in hours.

### Configuration
It is possible to configure office administration data via the application.properties file.

```
admin.configuration.workStartHour=9
admin.configuration.workEndHour=17
admin.configuration.workHours=8

```

### Error logging
Logging is done via Log4J. 

### Testing
Unit tests are available for calculation method and it is also possible to test the method via the following api call:
`/issue/getduedate?submission=<yyyy-MM-dd HH:mm:ss>&turnaround=<long>`
Should return your calculated due date and its generated id.

## Details of the task

### The problem
We are looking for a solution that implements a due date calculator in an issue
tracking system. Your task is to implement the CalculateDueDate method:
• Input: Takes the submit date/time and turnaround time.
• Output: Returns the date/time when the issue is resolved.

### Rules
• Working hours are from 9AM to 5PM on every working day, Monday to Friday.
• Holidays should be ignored (e.g. A holiday on a Thursday is considered as a
working day. A working Saturday counts as a non-working day.).
• The turnaround time is defined in working hours (e.g. 2 days equal 16 hours).
If a problem was reported at 2:12PM on Tuesday and the turnaround time is
16 hours, then it is due by 2:12PM on Thursday.
• A problem can only be reported during working hours. (e.g. All submit date
values are set between 9AM to 5PM.)
• Do not use any third-party libraries for date/time calculations (e.g. Moment.js,
Carbon, Joda, etc.) or hidden functionalities of the built-in methods.

### Additional info
• Use your favourite programming language.
• Do not implement the user interface or CLI.
• Do not write a pseudo code. Write a code that you would commit/push to a
repository and which solves the given problem.
• You have 24 hours to submit your solution.
• You can submit your solution even if you have not finished it fully.
### Bonus – Not mandatory
• Including automated tests to your solution is a plus.
• Test-driven (TDD) solutions are especially welcome.
• Clean Code (by Robert. C. Martin) makes us happy.



 


Possible to test method via api call:
/issue/getduedate?submission=<yyyy-MM-dd HH:mm:ss>&turnaround=<long>
Should return your calculated due date and its generated id.
  
  
  
  
