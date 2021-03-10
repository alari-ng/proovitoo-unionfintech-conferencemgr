package com.netgroup.unionfintech.conferencemgr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netgroup.unionfintech.conferencemgr.entity.ConferenceEntity;
import com.netgroup.unionfintech.conferencemgr.entity.ParticipantEntity;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {

	public Optional<ParticipantEntity> findByConferenceAndName(ConferenceEntity conference, String name);

	public int countByConference(ConferenceEntity conference);
}
