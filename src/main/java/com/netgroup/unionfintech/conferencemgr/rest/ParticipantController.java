package com.netgroup.unionfintech.conferencemgr.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netgroup.unionfintech.conferencemgr.service.ParticipantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("conferencemgr/participant")
public class ParticipantController {

	private final ParticipantService participantService;

	@PostMapping
	public void cancelConference(@RequestParam String conferenceName, @RequestParam String participantName) {
		participantService.addParticipant(conferenceName, participantName);
	}

	@DeleteMapping
	public void removeParticipant(@RequestParam String conferenceName, @RequestParam String participantName) {
		participantService.removeParticipant(conferenceName, participantName);
	}
}
