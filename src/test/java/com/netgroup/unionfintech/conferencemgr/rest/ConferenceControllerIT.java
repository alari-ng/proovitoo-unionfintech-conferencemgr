package com.netgroup.unionfintech.conferencemgr.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

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
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConferenceControllerIT {

    private static final String URI_COMMON = "conferencemgr/conference";
    private static final String URI_AVAILABILITY = URI_COMMON + "/availability?conferenceName={conferenceName}";
    private static final String URI_CANCEL = URI_COMMON + "/cancel?conferenceName={conferenceName}";

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @LocalServerPort
    private int serverPort;

    @BeforeEach
    void beforeEachTest() {
    	RestAssured.port = serverPort;
    	participantRepository.deleteAll();
    	conferenceRepository.deleteAll();
    }

    @Test
    void checkConferenceStatus_AVAILABLE() {
    	String conferenceName = "test123";
    	int seatsMax = 1;

		conferenceRepository.saveAndFlush(ConferenceEntity.builder()
				.name(conferenceName)
				.seats(seatsMax)
				.build());

    	when()
    		.get(URI_AVAILABILITY, Map.of("conferenceName", conferenceName))
    	.then()
    		.statusCode(HttpStatus.OK.value())
    		.body("conferenceRoomAvailability", is("AVAILABLE"));
    }

    @Test
    void checkConferenceStatus_FULL() {
    	String conferenceName = "test123";
    	int seatsMax = 1;

    	ConferenceEntity conference = conferenceRepository.saveAndFlush(ConferenceEntity.builder()
				.name(conferenceName)
				.seats(seatsMax)
				.build());
		participantRepository.saveAndFlush(ParticipantEntity.builder().conference(conference)
				.name("participant-1")
				.build());

    	when()
    		.get(URI_AVAILABILITY, Map.of("conferenceName", conferenceName))
    	.then()
    		.statusCode(HttpStatus.OK.value())
    		.body("conferenceRoomAvailability", is("FULL"));
    }

    @Test
    void checkConferenceStatus_CANCELLED() {
    	String conferenceName = "test123";
    	int seatsMax = 1;

    	conferenceRepository.saveAndFlush(ConferenceEntity.builder()
				.name(conferenceName)
				.seats(seatsMax)
				.cancelled(true)
				.build());

    	when()
    		.get(URI_AVAILABILITY, Map.of("conferenceName", conferenceName))
    	.then()
    		.statusCode(HttpStatus.OK.value())
    		.body("conferenceRoomAvailability", is("CANCELLED"));
    }

    @Test
    void checkConferenceStatus_NO_MATCH() {
    	String conferenceName = "test123";

    	when()
    		.get(URI_AVAILABILITY, Map.of("conferenceName", conferenceName))
    	.then()
    		.statusCode(HttpStatus.OK.value())
    		.body("conferenceRoomAvailability", is("NO_MATCH"));
    }

    @Test
    void cancel_marksConferenceAsCancelled() {
    	String conferenceName = "test123";
    	int seatsMax = 1;

    	conferenceRepository.saveAndFlush(ConferenceEntity.builder()
				.name(conferenceName)
				.seats(seatsMax)
				.build());

    	// perform cancel
    	when()
			.put(URI_CANCEL, Map.of("conferenceName", conferenceName))
		.then()
			.statusCode(HttpStatus.OK.value());

    	// check the conference status is cancelled
    	when()
    		.get(URI_AVAILABILITY, Map.of("conferenceName", conferenceName))
    	.then()
    		.statusCode(HttpStatus.OK.value())
    		.body("conferenceRoomAvailability", is("CANCELLED"));
    }

    @Test
    void post_createsNewConference() {
    	String conferenceName = "test123";
    	int seatsMax = 1;

    	ConferenceEntity newConferenceToCreate = ConferenceEntity.builder()
				.name(conferenceName)
				.seats(seatsMax)
				.build();

    	// create new conference
    	given()
    		.body(newConferenceToCreate)
    		.contentType(ContentType.JSON)
    	.when()
			.post(URI_COMMON)
		.then()
			.statusCode(HttpStatus.OK.value());

    	// check the conference is available
    	when()
    		.get(URI_AVAILABILITY, Map.of("conferenceName", conferenceName))
    	.then()
    		.statusCode(HttpStatus.OK.value())
    		.body("conferenceRoomAvailability", is("AVAILABLE"));
    }

    @Test
    void post_http400_alreadyExists() {
    	String conferenceName = "test123";
    	int seatsMax = 1;

    	ConferenceEntity newConferenceToCreate = ConferenceEntity.builder()
				.name(conferenceName)
				.seats(seatsMax)
				.build();
 
    	conferenceRepository.saveAndFlush(ConferenceEntity.builder()
				.name(conferenceName)
				.seats(seatsMax)
				.build());

    	// create new conference
    	given()
    		.body(newConferenceToCreate)
    		.contentType(ContentType.JSON)
    	.when()
			.post(URI_COMMON)
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("message", is("Conference with name test123 already exists"));
    }

    @Test
    void cancel_http4040_conferenceNotExist() {
    	when()
			.put(URI_CANCEL, Map.of("conferenceName", "nonexistent"))
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value())
			.body("message", is("Conference with name nonexistent does not exists"));
    }
}
