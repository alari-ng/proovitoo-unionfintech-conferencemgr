package com.netgroup.unionfintech.conferencemgr.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.netgroup.unionfintech.conferencemgr.entity.ConferenceEntity;
import com.netgroup.unionfintech.conferencemgr.entity.ParticipantEntity;
import com.netgroup.unionfintech.conferencemgr.exception.ConferenceNotFoundException;
import com.netgroup.unionfintech.conferencemgr.exception.GeneralValidationException;
import com.netgroup.unionfintech.conferencemgr.exception.ParticipantNotFoundException;
import com.netgroup.unionfintech.conferencemgr.repository.ConferenceRepository;
import com.netgroup.unionfintech.conferencemgr.repository.ParticipantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipantService {

	private final ConferenceRepository conferenceRepository;
	private final ParticipantRepository participantRepository;

    @Transactional
	public void addParticipant(String conferenceName, String participantName) {
    	ConferenceEntity conference = getConferenceByName(conferenceName);
		ensureParticipantNotYetExist(conference, participantName);
		ParticipantEntity storedParticipant = participantRepository.saveAndFlush(ParticipantEntity.builder()
				.conference(conference)
				.name(participantName)
				.build());
		log.info("New participant stored to conference: {}", storedParticipant);
	}

    @Transactional
	public void removeParticipant(String conferenceName, String participantName) {
    	ConferenceEntity conference = getConferenceByName(conferenceName);
		ParticipantEntity participant = getParticipantByName(conference, participantName);
    	participantRepository.delete(participant);
		log.info("Participant removed from conference: {}", participant);
		participantRepository.flush();
	}

	private ConferenceEntity getConferenceByName(String conferenceName) {
		Optional<ConferenceEntity> conferenceOpt = conferenceRepository.findByName(conferenceName);
    	if (conferenceOpt.isEmpty()) {
    		log.error("Conference for participant does not exists: {}", conferenceName);
    		throw new ConferenceNotFoundException("Conference with name " + conferenceName + " does not exists");
    	}
    	return conferenceOpt.get();
	}

	private void ensureParticipantNotYetExist(ConferenceEntity conference, String participantName) {
		Optional<ParticipantEntity> participantOpt = participantRepository.findByConferenceAndName(conference,
				participantName);
    	if (participantOpt.isPresent()) {
    		log.error("Participant with name \"{}\" already registered to conference \"{}\"", participantName, conference.getName());
    		throw new GeneralValidationException("Participant with name " + participantName + " already exists");
    	}
	}

	private ParticipantEntity getParticipantByName(ConferenceEntity conference, String participantName) {
		Optional<ParticipantEntity> participantOpt = participantRepository.findByConferenceAndName(conference,
				participantName);
    	if (participantOpt.isEmpty()) {
    		log.error("Participant does not exists for name \"{}\" under conference \"{}\"", participantName, conference.getName());
    		throw new ParticipantNotFoundException("Participant with name " + participantName + " does not exists");
    	}
    	return participantOpt.get();
	}
}
