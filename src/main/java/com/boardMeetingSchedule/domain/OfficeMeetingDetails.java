package com.boardMeetingSchedule.domain;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
public class OfficeMeetingDetails {

    private LocalTime officeStartTime;
    private LocalTime officeEndTime;
    private Map<LocalDate, Set<MeetingDetails>> meetings;

    public OfficeMeetingDetails(LocalTime officeStartTime, LocalTime officeEndTime, Map<LocalDate, Set<MeetingDetails>> meetings) {
        this.officeStartTime = officeStartTime;
        this.officeEndTime = officeEndTime;
        this.meetings = meetings;
    }
}
