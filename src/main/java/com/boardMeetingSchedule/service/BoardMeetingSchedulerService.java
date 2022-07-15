package com.boardMeetingSchedule.service;

import com.boardMeetingSchedule.domain.MeetingDetails;
import com.boardMeetingSchedule.domain.OfficeMeetingDetails;
import io.micrometer.core.instrument.util.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.*;

import static java.lang.Integer.parseInt;

@Service
public class BoardMeetingSchedulerService {

    Logger log = LoggerFactory.getLogger(BoardMeetingSchedulerService.class);

   /* *
   *    takes input parameter meetingRequest and
   *    process to check the overlaps
   *    and within office work
   *    hours
   * */
    public OfficeMeetingDetails scheduleMeeting(String meetingRequest) {
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        if(StringUtils.isBlank(meetingRequest) || StringUtils.isEmpty(meetingRequest)) {
            return null;
        }
        String[] inputDetails = meetingRequest.split("\n");
        String[] officeHours = inputDetails[0].split(" ");
        LocalTime officeStartTime = new LocalTime(
                parseInt(officeHours[0].substring(0, 2)),
                parseInt(officeHours[0].substring(2, 4)));
        LocalTime officeEndTime = new LocalTime(
                parseInt(officeHours[1].substring(0, 2)),
                parseInt(officeHours[1].substring(2, 4)));
        Map<LocalDate, Set<MeetingDetails>> meetings = new HashMap<>();

        for (int i = 1; i < inputDetails.length; i = i + 2) {
            if(i+1>=inputDetails.length){
                return null;
            }
            String[] meetingSlotRequest = inputDetails[i + 1].split(" ");
            LocalDate meetingDate =dateFormatter.parseLocalDate(meetingSlotRequest[0]);

            MeetingDetails meetingDetails = retrieveMeeting(inputDetails[i],
                    officeStartTime, officeEndTime, meetingSlotRequest);
            if (meetingDetails != null) {
                if (meetings.containsKey(meetingDate)) {
                    meetings.get(meetingDate).remove(meetingDetails);
                    meetings.get(meetingDate).add(meetingDetails);
                } else {
                    Set<MeetingDetails> meetingsForDay = new TreeSet<MeetingDetails>();
                    meetingsForDay.add(meetingDetails);
                    meetings.put(meetingDate, meetingsForDay);
                }
            }
        }

        return new OfficeMeetingDetails(officeStartTime, officeEndTime,
                meetings);
    }

    /*
    * */
    private MeetingDetails retrieveMeeting(String requestLine,
                                   LocalTime officeStartTime, LocalTime officeFinishTime,
                                   String[] meetingSlotRequest) {
        String[] employeeRequest = requestLine.split(" ");
        String employeeId = employeeRequest[2];

        LocalTime meetingStartTime = LocalTime.parse(meetingSlotRequest[1]);
        LocalTime meetingFinishTime = meetingStartTime.plusHours(parseInt(meetingSlotRequest[2]));

        if (meetingScheduleOutsideOfficeHours(officeStartTime, officeFinishTime,
                meetingStartTime, meetingFinishTime)) {
    //            log.error("EmployeeId:: " + employeeId
    //                    + " requested booking slot which is outside office hour.");
            return null;
        } else {
            return new MeetingDetails(employeeId, meetingStartTime, meetingFinishTime);
        }
    }

    /*
    * checks whether the start time
    * and end time of a meeting are
    * within office hours
    * */
    private boolean meetingScheduleOutsideOfficeHours(LocalTime officeStartTime,
                                                  LocalTime officeFinishTime, LocalTime meetingStartTime,
                                                  LocalTime meetingFinishTime) {
        return meetingStartTime.isBefore(officeStartTime)
                || meetingStartTime.isAfter(officeFinishTime)
                || meetingFinishTime.isAfter(officeFinishTime)
                || meetingFinishTime.isBefore(officeStartTime);
    }


    /*
    * to build the output of the meetings scheduled
    * */
    private String meetingScheduleBuilder(OfficeMeetingDetails meetingsScheduled) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        for (Map.Entry<LocalDate, Set<MeetingDetails>> meetingEntry : meetingsScheduled.getMeetings().entrySet()) {
            LocalDate meetingDate = meetingEntry.getKey();
            sb.append(dateFormatter.print(meetingDate)) .append("\n");
            Set<MeetingDetails> meetings = meetingEntry.getValue();
            for (MeetingDetails meeting : meetings) {
                sb.append(timeFormatter.print(meeting.getMeetingStartTime())).append(" ");
                sb.append(timeFormatter.print(meeting.getMeetingFinishTime())).append(" ");
                sb.append(meeting.getEmployeeId()).append("\n");
            }

        }
        return sb.toString();
    }

    /**
     * bookings 0f the meeting*/
    public String bookMeetings(String meetingRequest){
        OfficeMeetingDetails meetingsScheduleBooked = scheduleMeeting(meetingRequest);
        return meetingScheduleBuilder(meetingsScheduleBooked);
    }
}
