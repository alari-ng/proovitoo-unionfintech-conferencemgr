package com.netgroup.unionfintech.conferencemgr.rest;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.netgroup.unionfintech.conferencemgr.entity.ConferenceEntity;
import com.netgroup.unionfintech.conferencemgr.entity.ParticipantEntity;
import com.netgroup.unionfintech.conferencemgr.repository.ConferenceRepository;
import com.netgroup.unionfintech.conferencemgr.repository.ParticipantRepository;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ParticipantControllerIT {

    private static final String URI_BASE = "conferencemgr/participant";
    private static final String URI_COMMON = URI_BASE + "?conferenceName={conferenceName}&participantName={participantName}";

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @LocalServerPort
    private int serverPort;

	private ConferenceEntity conference;

	final String conferenceName = "test123";
	final String participantName = "participant123";

	@BeforeEach
    void beforeEachTest() {
    	RestAssured.port = serverPort;
       	participantRepository.deleteAll();
       	conferenceRepository.deleteAll();
     	
    	conference = conferenceRepository.saveAndFlush(ConferenceEntity.builder()
				.name(conferenceName)
				.seats(1)
				.build());
    }

    @Test
    void post_createsAddsNewParticipantToConference() {
    	// create new participant
    	when()
			.post(URI_COMMON, Map.of("conferenceName", conferenceName, "participantName", participantName))
		.then()
			.statusCode(HttpStatus.OK.value());

    	// check the conference is available
    	assertTrue(participantRepository.findByConferenceAndName(conference, participantName).isPresent());
    }

    @Test
    void delete_removesParticipantFromConference() {
    	participantRepository.saveAndFlush(ParticipantEntity.builder()
    			.conference(conference)
				.name(participantName)
				.build());

    	// perform participant deletion
    	when()
			.delete(URI_COMMON, Map.of("conferenceName", conferenceName, "participantName", participantName))
		.then()
			.statusCode(HttpStatus.OK.value());

    	// check the participant no more in conference
    	assertFalse(participantRepository.findByConferenceAndName(conference, participantName).isPresent());
    }

    @Test
    void delete_http404_participantNotExist() {
    	when()
			.delete(URI_COMMON, Map.of("conferenceName", conferenceName, "participantName", participantName))
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("message", is("Participant with name participant123 does not exists"));
    }

    @Test
    void delete_http404_conferenceNotExist() {
    	when()
			.delete(URI_COMMON, Map.of("conferenceName", "nonexistent", "participantName", participantName))
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("message", is("Conference with name nonexistent does not exists"));
    }

    @Test
    void post_http400_alreadyExists() {
    	participantRepository.saveAndFlush(ParticipantEntity.builder()
    			.conference(conference)
				.name(participantName)
				.build());

    	when()
			.post(URI_COMMON, Map.of("conferenceName", conferenceName, "participantName", participantName))
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("message", is("Participant with name participant123 already exists"));
    }

    @Test
    void post_http404_conferenceNotExist() {
    	when()
			.post(URI_COMMON, Map.of("conferenceName", "nonexistent", "participantName", participantName))
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("message", is("Conference with name nonexistent does not exists"));
    }
}
