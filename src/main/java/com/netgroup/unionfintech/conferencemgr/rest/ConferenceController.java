package com.netgroup.unionfintech.conferencemgr.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netgroup.unionfintech.conferencemgr.model.ConferenceCheckStatus;
import com.netgroup.unionfintech.conferencemgr.model.ConferenceRoomAvailability;
import com.netgroup.unionfintech.conferencemgr.model.NewConference;
import com.netgroup.unionfintech.conferencemgr.service.ConferenceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("conferencemgr/conference")
public class ConferenceController {

	private final ConferenceService conferenceService;

	@PostMapping
	public void createNewConference(@RequestBody NewConference conferenceData) {
		conferenceService.createNewConference(conferenceData);
	}

	@PutMapping("cancel")
	public void cancelConference(@RequestParam String conferenceName) {
		conferenceService.cancelConference(conferenceName);
	}

	@GetMapping("availability")
	public ConferenceCheckStatus checkConferenceRoomAvailability(@RequestParam String conferenceName) {
		ConferenceRoomAvailability conferenceRoomAvailability =
				conferenceService.checkConferenceRoomAvailability(conferenceName);
		return ConferenceCheckStatus.builder().conferenceRoomAvailability(conferenceRoomAvailability).build();
	}
}
