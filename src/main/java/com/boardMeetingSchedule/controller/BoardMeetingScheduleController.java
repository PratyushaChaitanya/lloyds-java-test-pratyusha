package com.boardMeetingSchedule.controller;

import com.boardMeetingSchedule.service.BoardMeetingSchedulerService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/meeting/schedule")
public class BoardMeetingScheduleController {


    @Autowired
    private BoardMeetingSchedulerService boardMeetingSchedulerService;

    /*
    * API to call Meetings scheduled
    * */
    @GetMapping(value = "/{meetingRequest}")
    public ResponseEntity<String> getMeetingsScheduled(@PathVariable(value = "meetingRequest") String meetingRequest){
        if(StringUtils.isNotBlank(meetingRequest) || StringUtils.isNotEmpty(meetingRequest)) {
            return ResponseEntity.ok().body(boardMeetingSchedulerService.bookMeetings(meetingRequest));
        }else {
            return ResponseEntity.badRequest().body("Invalid Input");
        }

    }

}
