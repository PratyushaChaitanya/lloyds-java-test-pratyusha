package com.boardMeetingSchedule.test;

import com.boardMeetingSchedule.controller.BoardMeetingScheduleController;
import com.boardMeetingSchedule.domain.MeetingDetails;
import com.boardMeetingSchedule.domain.OfficeMeetingDetails;
import com.boardMeetingSchedule.service.BoardMeetingSchedulerService;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes={com.boardMeetingSchedule.BoardMeetingScheduleApplication.class})
@RunWith(MockitoJUnitRunner.class)
class BoardMeetingScheduleApplicationTests {


    private String meetingRequest;

    @Mock
    private BoardMeetingSchedulerService boardMeetingSchedulerService;

    @InjectMocks
    private BoardMeetingScheduleController boardMeetingScheduleController;

    @Mock
    private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
    @Mock
    private DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(boardMeetingScheduleController).build();

    }

    @Test
    public void shouldParseOfficeHours() {
        meetingRequest = "0900 1730\n";

        when(boardMeetingSchedulerService.scheduleMeeting(meetingRequest)).thenCallRealMethod();
        OfficeMeetingDetails bookings = boardMeetingSchedulerService.scheduleMeeting(meetingRequest);
        assertEquals(bookings.getOfficeStartTime().getHourOfDay(), 9);
        assertEquals(bookings.getOfficeStartTime().getMinuteOfHour(), 0);
        assertEquals(bookings.getOfficeEndTime().getHourOfDay(), 17);
        assertEquals(bookings.getOfficeEndTime().getMinuteOfHour(), 30);
    }

    @Test
    public void shouldParseMeetingRequest() {
        meetingRequest = "0900 1730\n" + "2016-03-17 10:17:06 EMP001\n"
                + "2016-03-21 09:00 2\n";

        when(boardMeetingSchedulerService.scheduleMeeting(meetingRequest)).thenCallRealMethod();
        OfficeMeetingDetails bookings = boardMeetingSchedulerService.scheduleMeeting(meetingRequest);

        LocalDate meetingDate = new LocalDate(2016,3,21);

        assertEquals(1, bookings.getMeetings().get(meetingDate).size());
        MeetingDetails meeting = bookings.getMeetings().get(meetingDate)
                .toArray(new MeetingDetails[0])[0];
        assertEquals("EMP001", meeting.getEmployeeId());
        assertEquals(9, meeting.getMeetingStartTime().getHourOfDay());
        assertEquals(0, meeting.getMeetingStartTime().getMinuteOfHour());
        assertEquals(11, meeting.getMeetingFinishTime().getHourOfDay());
        assertEquals(0, meeting.getMeetingFinishTime().getMinuteOfHour());
    }

    @Test
    public void noPartOfMeetingMayFallOutsideOfficeHours() {
        meetingRequest = "0900 1730\n" + "2016-03-15 17:29:12 EMP005\n"
                + "2016-03-21 16:00 3\n";

        when(boardMeetingSchedulerService.scheduleMeeting(meetingRequest)).thenCallRealMethod();
        OfficeMeetingDetails bookings = boardMeetingSchedulerService.scheduleMeeting(meetingRequest);

        assertEquals(0, bookings.getMeetings().size());

    }

    @Test
    public void shouldProcessMeetingsInChronologicalOrderOfSubmission() {
        meetingRequest = "0900 1730\n" + "2016-03-17 10:17:06 EMP001\n"
                + "2016-03-21 09:00 2\n" + "2016-03-16 12:34:56 EMP002\n"
                + "2016-03-21 09:00 2\n";

        when(boardMeetingSchedulerService.scheduleMeeting(meetingRequest)).thenCallRealMethod();
        OfficeMeetingDetails bookings = boardMeetingSchedulerService.scheduleMeeting(meetingRequest);

        LocalDate meetingDate = new LocalDate(2016,3,21);

        assertEquals(1, bookings.getMeetings().get(meetingDate).size());
        MeetingDetails meeting = bookings.getMeetings().get(meetingDate)
                .toArray(new MeetingDetails[0])[0];
        assertEquals("EMP002", meeting.getEmployeeId());
        assertEquals(9, meeting.getMeetingStartTime().getHourOfDay());
        assertEquals(0, meeting.getMeetingStartTime().getMinuteOfHour());
        assertEquals(11, meeting.getMeetingFinishTime().getHourOfDay());
        assertEquals(0, meeting.getMeetingFinishTime().getMinuteOfHour());
    }

    @Test
    public void shouldGroupMeetingsChronologically() {
        meetingRequest = "0900 1730\n" + "2016-03-17 10:17:06 EMP004\n"
                + "2016-03-22 16:00 1\n" + "2016-03-16 09:28:23 EMP003\n"
                + "2016-03-22 14:00 2\n";

        when(boardMeetingSchedulerService.scheduleMeeting(meetingRequest)).thenCallRealMethod();

        OfficeMeetingDetails bookings = boardMeetingSchedulerService.scheduleMeeting(meetingRequest);
        LocalDate meetingDate = new LocalDate(2016,3,22);

        assertEquals(1, bookings.getMeetings().size());
        assertEquals(2, bookings.getMeetings().get(meetingDate).size());
        MeetingDetails[] meetings = bookings.getMeetings().get(meetingDate)
                .toArray(new MeetingDetails[0]);

        assertEquals("EMP003", meetings[0].getEmployeeId());
        assertEquals(14, meetings[0].getMeetingStartTime().getHourOfDay());
        assertEquals(0, meetings[0].getMeetingStartTime().getMinuteOfHour());
        assertEquals(16, meetings[0].getMeetingFinishTime().getHourOfDay());
        assertEquals(0, meetings[0].getMeetingFinishTime().getMinuteOfHour());

        assertEquals("EMP004", meetings[1].getEmployeeId());
        assertEquals(16, meetings[1].getMeetingStartTime().getHourOfDay());
        assertEquals(0, meetings[1].getMeetingStartTime().getMinuteOfHour());
        assertEquals(17, meetings[1].getMeetingFinishTime().getHourOfDay());
        assertEquals(0, meetings[1].getMeetingFinishTime().getMinuteOfHour());
    }

    @Test
    public void meetingsShouldNotOverlap() {
        meetingRequest = "0900 1730\n" + "2016-03-17 10:17:06 EMP001\n"
                + "2016-03-21 09:00 2\n" + "2016-03-16 12:34:56 EMP002\n"
                + "2016-03-21 10:00 1\n";

        when(boardMeetingSchedulerService.scheduleMeeting(meetingRequest)).thenCallRealMethod();

        OfficeMeetingDetails bookings = boardMeetingSchedulerService.scheduleMeeting(meetingRequest);
        LocalDate meetingDate = new LocalDate(2016,3,21);

        assertEquals(1, bookings.getMeetings().size());
        assertEquals(1, bookings.getMeetings().get(meetingDate).size());
        MeetingDetails[] meetings = bookings.getMeetings().get(meetingDate)
                .toArray(new MeetingDetails[0]);
        assertEquals("EMP002", meetings[0].getEmployeeId());
        assertEquals(10,meetings[0].getMeetingStartTime().getHourOfDay());
        assertEquals(0, meetings[0].getMeetingStartTime().getMinuteOfHour());
        assertEquals(11, meetings[0].getMeetingFinishTime().getHourOfDay());
        assertEquals(0, meetings[0].getMeetingFinishTime().getMinuteOfHour());
    }

    @Test
    public void emptyInputDataShouldEndWithNull() {
        meetingRequest = null;
        // when(boardMeetingScheduleController.getMeetingsScheduled(meetingRequest)).thenCallRealMethod();
        when(boardMeetingSchedulerService.scheduleMeeting(meetingRequest)).thenCallRealMethod();
        OfficeMeetingDetails bookings = boardMeetingSchedulerService.scheduleMeeting(meetingRequest);
        assertEquals(null, bookings);
    }

    @Test
    public void invalidInputDataShouldEndWithNull() {
        meetingRequest = "0900 1730\n" + "2016-03-17 10:17:06 EMP001\n";

        when(boardMeetingSchedulerService.scheduleMeeting(meetingRequest)).thenCallRealMethod();
        OfficeMeetingDetails bookings = boardMeetingSchedulerService.scheduleMeeting(meetingRequest);

        assertEquals(null, bookings);
    }


    @Test
    public void shouldPrintMeetingSchedule() {
        String meetingRequest = "0900 1730\n" + "2016-03-17 10:17:06 EMP001\n"
                + "2016-03-21 09:00 2\n" + "2016-03-16 12:34:56 EMP002\n"
                + "2016-03-21 09:00 2\n" + "2016-03-16 09:28:23 EMP003\n"
                + "2016-03-22 14:00 2\n" + "2016-03-17 10:17:06 EMP004\n"
                + "2016-03-22 16:00 1\n" + "2016-03-15 17:29:12 EMP005\n"
                + "2016-03-21 16:00 3\n";
        when(boardMeetingScheduleController.getMeetingsScheduled(meetingRequest)).thenCallRealMethod();
        when(boardMeetingSchedulerService.scheduleMeeting(meetingRequest)).thenCallRealMethod();

        String actualoutput = boardMeetingSchedulerService.bookMeetings(meetingRequest);

        String expectedOutput = "2016-03-22\n" +
                "14:00 16:00 EMP003\n" +
                "16:00 17:00 EMP004\n" +
                "2016-03-21\n" +
                "09:00 11:00 EMP002\n";

        assertEquals(expectedOutput,actualoutput);

    }

    @Test
    public void emptyInputDataShouldEndWithInvalidInputError() {
        meetingRequest = "";
        when(boardMeetingScheduleController.getMeetingsScheduled(meetingRequest)).thenCallRealMethod();
        ResponseEntity<String> actualOutput = boardMeetingScheduleController.getMeetingsScheduled(meetingRequest);
        assertEquals(ResponseEntity.badRequest().body("Invalid Input"), actualOutput);
    }
}
