package com.boardMeetingSchedule.domain;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.Interval;
import org.joda.time.LocalTime;

@Setter
@Getter
public class MeetingDetails implements Comparable<MeetingDetails>{

    private String employeeId;
    private LocalTime meetingStartTime;
    private LocalTime meetingFinishTime;

    public MeetingDetails(String employeeId, LocalTime meetingStartTime, LocalTime meetingFinishTime) {
        this.employeeId = employeeId;
        this.meetingStartTime = meetingStartTime;
        this.meetingFinishTime = meetingFinishTime;
    }

        public int compareTo(MeetingDetails meetingDetails) {
        Interval meetingInterval = new Interval(meetingStartTime.toDateTimeToday(),
                meetingFinishTime.toDateTimeToday());
        Interval toCompareMeetingInterval = new Interval(meetingDetails.getMeetingStartTime()
                .toDateTimeToday(), meetingDetails.getMeetingFinishTime().toDateTimeToday());

        if (meetingInterval.overlaps(toCompareMeetingInterval)) {
            return 0;
        } else {
            return this.getMeetingStartTime().compareTo(meetingDetails.getMeetingStartTime());
        }

    }
}
