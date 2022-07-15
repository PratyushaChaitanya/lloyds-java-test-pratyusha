package com.boardMeetingSchedule.controller;

import com.boardMeetingSchedule.service.BoardMeetingSchedulerService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BoardMeetingScheduleController {


    @Autowired
    private BoardMeetingSchedulerService boardMeetingSchedulerService;

    /*
    * API to call Meetings scheduled
    * */
    @GetMapping("/meetingScheduler")
    public ResponseEntity<String> getMeetingsScheduled(@RequestParam(value="meetingRequest") String meetingRequest){
        if(StringUtils.isNotBlank(meetingRequest) || StringUtils.isNotEmpty(meetingRequest)) {
            return ResponseEntity.ok().body(boardMeetingSchedulerService.bookMeetings(meetingRequest));
        }else {
            return ResponseEntity.badRequest().body("Invalid Input");
        }

    }

}
