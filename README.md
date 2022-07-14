REQUIREMENT:

Your company has an existing system for employees to submit booking requests for meetings in the
boardroom. Your employer has now asked you to implement a system for processing batches of
booking requests.

SAMPLE INPUT GUIDELINES:

Your processing system must process input as text. The first line of the input paylod
represents the company office hours, in 24 hour clock format (ignore timezones), and the
remainder of the input represents individual booking requests. Each booking request is in
the following format:
[Request submission time, in YYYY-MM-DD HH:MM:SS format] [Employee id]
[Meeting start time, in YYYY-MM-DD HH:MM format] [Meeting duration in hours]
A sample text input follows, to be used in your solution:

0900 1730
2016-07-18 10:17:06 EMP001
2016-07-21 09:00 2
2016-07-18 12:34:56 EMP002
2016-07-21 09:00 2
2016-07-18 09:28:23 EMP003
2016-07-22 14:00 2
2016-07-18 11:23:45 EMP004
2016-07-22 16:00 1
2016-07-15 17:29:12 EMP005
2016-07-21 16:00 3

OUTPUT:

Your system must output a list of bookings in a specific format; with booking grouped
chronologically by day. For the sample input displayed above, your system must provide the
following output

2016-03-21
09:00 11:00 EMP001
2016-03-22
14:00 16:00 EMP003
16:00 17:00 EMP004

RULES:

Booking rules 
No part of a meeting may fall outside of office hours (9:00 to 18:00).
Meetings may not overlap.
The booking submission system only allows one submission at a time, so submission
times are guaranteed to be unique.
Booking must be processed in the chronological order in which they were submitted. 
The ordering of booking submissions in the supplied input is not guaranteed. 

Notes:

Although the system that you produce may open and parse a text file for input, this is
not part of the requirements. As long as the input text is in the correct format, the
method of input is up to the developer.
