package com.netgroup.unionfintech.conferencemgr.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "conference")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	private String name;

	private Integer seats;

	private boolean cancelled;
}
