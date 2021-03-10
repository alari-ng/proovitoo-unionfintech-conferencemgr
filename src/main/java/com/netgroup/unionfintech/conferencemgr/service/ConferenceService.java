package com.netgroup.unionfintech.conferencemgr.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.netgroup.unionfintech.conferencemgr.entity.ConferenceEntity;
import com.netgroup.unionfintech.conferencemgr.exception.ConferenceNotFoundException;
import com.netgroup.unionfintech.conferencemgr.exception.GeneralValidationException;
import com.netgroup.unionfintech.conferencemgr.model.ConferenceRoomAvailability;
import com.netgroup.unionfintech.conferencemgr.model.NewConference;
import com.netgroup.unionfintech.conferencemgr.repository.ConferenceRepository;
import com.netgroup.unionfintech.conferencemgr.repository.ParticipantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConferenceService {

	private final ConferenceRepository conferenceRepository;
	private final ParticipantRepository participantRepository;

    public ConferenceRoomAvailability checkConferenceRoomAvailability(String conferenceName) {
    	Optional<ConferenceEntity> conferenceOpt = conferenceRepository.findByName(conferenceName);
    	if (conferenceOpt.isEmpty()) {
    		return ConferenceRoomAvailability.NO_MATCH;
    	}
    	ConferenceEntity conference = conferenceOpt.get();
    	if (conference.isCancelled()) {
    		return ConferenceRoomAvailability.CANCELLED;
    	}
    	int registeredParticipantsCount = participantRepository.countByConference(conference);
    	int freeSeats = conference.getSeats() - registeredParticipantsCount;
    	return freeSeats > 0 ? ConferenceRoomAvailability.AVAILABLE : ConferenceRoomAvailability.FULL;
    }

    @Transactional
    public void createNewConference(NewConference newConference) {
    	ensureConferenceNotYetExist(newConference);
    	ConferenceEntity conferenceEntity = ConferenceEntity.builder()
    			.name(newConference.getName())
    			.seats(newConference.getSeats())
    			.build();
    	ConferenceEntity storedConference = conferenceRepository.saveAndFlush(conferenceEntity);
    	log.info("New conference created: {}", storedConference);
    }

    @Transactional
    public void cancelConference(String conferenceName) {
    	ConferenceEntity conferenceEntity = getConferenceByName(conferenceName);
    	conferenceEntity.setCancelled(true);
		ConferenceEntity storedConference = conferenceRepository.saveAndFlush(conferenceEntity );
    	log.info("Conference cancelled: {}", storedConference);
    }

	private void ensureConferenceNotYetExist(NewConference newConference) {
		Optional<ConferenceEntity> conferenceOpt = conferenceRepository.findByName(newConference.getName());
    	if (conferenceOpt.isPresent()) {
    		log.error("Cannot create new conference - already exists with name {}", newConference.getName());
    		throw new GeneralValidationException("Conference with name " + newConference.getName() + " already exists");
    	}
	}

	private ConferenceEntity getConferenceByName(String conferenceName) {
		Optional<ConferenceEntity> conferenceOpt = conferenceRepository.findByName(conferenceName);
    	if (conferenceOpt.isEmpty()) {
    		log.error("Specified conference does not exists: {}", conferenceName);
    		throw new ConferenceNotFoundException("Conference with name " + conferenceName + " does not exists");
    	}
    	return conferenceOpt.get();
	}
    
}
