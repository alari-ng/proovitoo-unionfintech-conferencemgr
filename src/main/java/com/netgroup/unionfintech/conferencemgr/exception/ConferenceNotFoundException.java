package com.netgroup.unionfintech.conferencemgr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) 
public class ConferenceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3110036492437938817L;

	public ConferenceNotFoundException(String message) {
		super(message);
	}
}
