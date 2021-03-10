package com.netgroup.unionfintech.conferencemgr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.netgroup.unionfintech.conferencemgr.entity.ParticipantEntity;
import com.netgroup.unionfintech.conferencemgr.exception.ConferenceNotFoundException;
import com.netgroup.unionfintech.conferencemgr.exception.GeneralValidationException;
import com.netgroup.unionfintech.conferencemgr.exception.ParticipantNotFoundException;
import com.netgroup.unionfintech.conferencemgr.repository.ConferenceRepository;
import com.netgroup.unionfintech.conferencemgr.repository.ParticipantRepository;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

	@InjectMocks
	private ParticipantService subject;

	@Mock
	private ConferenceRepository conferenceRepository;

	@Mock
	private ParticipantRepository participantRepository;

	@Test
	void addParticipant_success() {
		String conferenceName = "conference123";
		String participantName = "participant123";

		ConferenceEntity conference = ConferenceEntity.builder().name(conferenceName).build();
		ParticipantEntity participant = ParticipantEntity.builder().name(participantName).conference(conference).build();

		when(conferenceRepository.findByName(anyString())).thenReturn(Optional.of(conference));
		when(participantRepository.findByConferenceAndName(any(), anyString())).thenReturn(Optional.empty());

		subject.addParticipant(conferenceName, participantName);

		verify(conferenceRepository).findByName(conferenceName);
		verify(participantRepository).findByConferenceAndName(conference, participantName);
		verify(participantRepository).saveAndFlush(participant);
	}

	@Test
	void addParticipant_alreadyExistsError() {
		String conferenceName = "conference123";
		String participantName = "participant123";

		ConferenceEntity conference = ConferenceEntity.builder().name(conferenceName).build();
		ParticipantEntity participant = ParticipantEntity.builder().name(participantName).conference(conference).build();

		when(conferenceRepository.findByName(anyString())).thenReturn(Optional.of(conference));
		when(participantRepository.findByConferenceAndName(any(), anyString())).thenReturn(Optional.of(participant));

		GeneralValidationException exceptionThrown = assertThrows(GeneralValidationException.class, () ->
			subject.addParticipant(conferenceName, participantName));

		verify(conferenceRepository).findByName(conferenceName);
		verify(participantRepository).findByConferenceAndName(conference, participantName);
		verify(participantRepository, never()).saveAndFlush(participant);
		assertEquals("Participant with name participant123 already exists", exceptionThrown.getMessage());
	}

	@Test
	void addParticipant_conferenceNotExistsError() {
		String conferenceName = "conference123";
		String participantName = "participant123";

		when(conferenceRepository.findByName(anyString())).thenReturn(Optional.empty());

		ConferenceNotFoundException exceptionThrown = assertThrows(ConferenceNotFoundException.class, () ->
			subject.addParticipant(conferenceName, participantName));

		verify(conferenceRepository).findByName(conferenceName);
		verify(participantRepository, never()).saveAndFlush(any());
		assertEquals("Conference with name conference123 does not exists", exceptionThrown.getMessage());
	}

	@Test
	void removeParticipant_success() {
		String conferenceName = "conference123";
		String participantName = "participant123";

		ConferenceEntity conference = ConferenceEntity.builder().name(conferenceName).build();
		ParticipantEntity participant = ParticipantEntity.builder().name(participantName).conference(conference).build();

		when(conferenceRepository.findByName(anyString())).thenReturn(Optional.of(conference));
		when(participantRepository.findByConferenceAndName(any(), anyString())).thenReturn(Optional.of(participant));

		subject.removeParticipant(conferenceName, participantName);

		verify(conferenceRepository).findByName(conferenceName);
		verify(participantRepository).findByConferenceAndName(conference, participantName);
		verify(participantRepository).delete(participant);
		verify(participantRepository).flush();
	}

	@Test
	void removeParticipant_conferenceNotExistsError() {
		String conferenceName = "conference123";
		String participantName = "participant123";

		when(conferenceRepository.findByName(anyString())).thenReturn(Optional.empty());

		ConferenceNotFoundException exceptionThrown = assertThrows(ConferenceNotFoundException.class, () ->
			subject.removeParticipant(conferenceName, participantName));

		verify(conferenceRepository).findByName(conferenceName);
		verify(participantRepository, never()).delete(any());
		assertEquals("Conference with name conference123 does not exists", exceptionThrown.getMessage());
	}

	@Test
	void removeParticipant_participantNotExistsError() {
		String conferenceName = "conference123";
		String participantName = "participant123";

		ConferenceEntity conference = ConferenceEntity.builder().name(conferenceName).build();

		when(conferenceRepository.findByName(anyString())).thenReturn(Optional.of(conference));
		when(participantRepository.findByConferenceAndName(any(), anyString())).thenReturn(Optional.empty());

		ParticipantNotFoundException exceptionThrown = assertThrows(ParticipantNotFoundException.class, () ->
			subject.removeParticipant(conferenceName, participantName));

		verify(conferenceRepository).findByName(conferenceName);
		verify(participantRepository).findByConferenceAndName(conference, participantName);
		verify(participantRepository, never()).delete(any());
		assertEquals("Participant with name participant123 does not exists", exceptionThrown.getMessage());
	}

}
