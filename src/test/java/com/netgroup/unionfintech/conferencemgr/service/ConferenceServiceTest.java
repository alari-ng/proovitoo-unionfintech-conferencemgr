package com.netgroup.unionfintech.conferencemgr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.netgroup.unionfintech.conferencemgr.entity.ConferenceEntity;
import com.netgroup.unionfintech.conferencemgr.exception.ConferenceNotFoundException;
import com.netgroup.unionfintech.conferencemgr.exception.GeneralValidationException;
import com.netgroup.unionfintech.conferencemgr.model.ConferenceRoomAvailability;
import com.netgroup.unionfintech.conferencemgr.model.NewConference;
import com.netgroup.unionfintech.conferencemgr.repository.ConferenceRepository;
import com.netgroup.unionfintech.conferencemgr.repository.ParticipantRepository;

@ExtendWith(MockitoExtension.class)
class ConferenceServiceTest {

	@InjectMocks
	private ConferenceService subject;

	@Mock
	private ConferenceRepository conferenceRepository;

	@Mock
	private ParticipantRepository participantRepository;

	@Test
	void checkConferenceRoomAvailability_returns_NO_MATCH() {
    	when(conferenceRepository.findByName(anyString())).thenReturn(Optional.empty());

    	ConferenceRoomAvailability result = subject.checkConferenceRoomAvailability("missing conference 123");

    	assertEquals(ConferenceRoomAvailability.NO_MATCH, result);
	}

	@Test
	void checkConferenceRoomAvailability_returns_AVAILABLE() {
		String conferenceName = "conference123";
		int seatsMax = 2;
    	int seatsOccupied = 1;
		ConferenceEntity conference = ConferenceEntity.builder().name(conferenceName).seats(seatsMax).build();
    	Optional<ConferenceEntity> conferenceOpt = Optional.of(conference);

    	when(conferenceRepository.findByName(anyString())).thenReturn(conferenceOpt);
		when(participantRepository.countByConference(any())).thenReturn(seatsOccupied);

    	ConferenceRoomAvailability result = subject.checkConferenceRoomAvailability(conferenceName);
    	
    	verify(conferenceRepository).findByName(conferenceName);
    	verify(participantRepository).countByConference(conference);
    	assertEquals(ConferenceRoomAvailability.AVAILABLE, result);
	}

	@Test
	void checkConferenceRoomAvailability_returns_FULL() {
		String conferenceName = "conference123";
		int seatsMax = 2;
    	int seatsOccupied = 2;
		ConferenceEntity conference = ConferenceEntity.builder().name(conferenceName).seats(seatsMax).build();
    	Optional<ConferenceEntity> conferenceOpt = Optional.of(conference);

    	when(conferenceRepository.findByName(anyString())).thenReturn(conferenceOpt);
		when(participantRepository.countByConference(any())).thenReturn(seatsOccupied);

    	ConferenceRoomAvailability result = subject.checkConferenceRoomAvailability(conferenceName);
    	
    	verify(conferenceRepository).findByName(conferenceName);
    	verify(participantRepository).countByConference(conference);
    	assertEquals(ConferenceRoomAvailability.FULL, result);
	}

	@Test
	void checkConferenceRoomAvailability_returns_CANCELLED() {
		String conferenceName = "conference123";
		ConferenceEntity conference = ConferenceEntity.builder().name(conferenceName).seats(10).cancelled(true).build();
    	Optional<ConferenceEntity> conferenceOpt = Optional.of(conference);

    	when(conferenceRepository.findByName(anyString())).thenReturn(conferenceOpt);

    	ConferenceRoomAvailability result = subject.checkConferenceRoomAvailability(conferenceName);
    	
    	verify(conferenceRepository).findByName(conferenceName);
    	assertEquals(ConferenceRoomAvailability.CANCELLED, result);
	}

	@Test
	void createNewConference_sucess() {
		String conferenceName = "test1";
		int seatsMax = 10;
		ConferenceEntity conference = ConferenceEntity.builder()
				.name(conferenceName)
				.seats(seatsMax)
				.build();

		when(conferenceRepository.findByName(anyString())).thenReturn(Optional.empty());

		subject.createNewConference(NewConference.builder()
				.name(conferenceName)
				.seats(seatsMax)
				.build());
    	
    	verify(conferenceRepository).findByName(conferenceName);
    	verify(conferenceRepository).saveAndFlush(conference);
	}

	@Test
	void createNewConference_alreadyExistsError() {
		String conferenceName = "test1";
		int seatsMax = 10;
		ConferenceEntity conference = ConferenceEntity.builder()
				.name(conferenceName)
				.seats(seatsMax)
				.build();

		when(conferenceRepository.findByName(anyString())).thenReturn(Optional.of(conference));

		GeneralValidationException exceptionThrown = assertThrows(GeneralValidationException.class, () ->
			subject.createNewConference(NewConference.builder()
					.name(conferenceName)
					.seats(seatsMax)
					.build()));
    	
    	verify(conferenceRepository).findByName(conferenceName);
    	verify(conferenceRepository, never()).saveAndFlush(any());
    	assertEquals("Conference with name test1 already exists", exceptionThrown.getMessage());
	}

	@Test
	void cancelConference_sucess() {
		String conferenceName = "test1";
		ConferenceEntity conference = ConferenceEntity.builder()
				.name(conferenceName)
				.build();

		when(conferenceRepository.findByName(anyString())).thenReturn(Optional.of(conference));

		subject.cancelConference(conferenceName);
    	
    	verify(conferenceRepository).findByName(conferenceName);
    	verify(conferenceRepository).saveAndFlush(conference);
    	assertTrue(conference.isCancelled());
	}

	@Test
	void cancelConference_notExistError() {
		String conferenceName = "test1";
 
		when(conferenceRepository.findByName(anyString())).thenReturn(Optional.empty());

		 ConferenceNotFoundException exceptionThrown = assertThrows(ConferenceNotFoundException.class, () ->
		 	subject.cancelConference(conferenceName));
    	
    	verify(conferenceRepository).findByName(conferenceName);
    	verify(conferenceRepository, never()).saveAndFlush(any());
    	assertEquals("Conference with name test1 does not exists", exceptionThrown.getMessage());
	}
}
