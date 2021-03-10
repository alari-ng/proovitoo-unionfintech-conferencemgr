package com.netgroup.unionfintech.conferencemgr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netgroup.unionfintech.conferencemgr.entity.ConferenceEntity;

public interface ConferenceRepository extends JpaRepository<ConferenceEntity, Long> {

	Optional<ConferenceEntity> findByName(String name);
}
