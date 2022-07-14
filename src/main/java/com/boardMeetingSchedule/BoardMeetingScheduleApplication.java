package com.boardMeetingSchedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@EnableAutoConfiguration
public class BoardMeetingScheduleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardMeetingScheduleApplication.class, args);
	}

}
