package com.netgroup.unionfintech.conferencemgr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceCheckStatus {

	private ConferenceRoomAvailability conferenceRoomAvailability;
}
