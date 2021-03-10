package com.netgroup.unionfintech.conferencemgr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND) 
public class ParticipantNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2940755261979376733L;

	public ParticipantNotFoundException(String message) {
		super(message);
	}
}
