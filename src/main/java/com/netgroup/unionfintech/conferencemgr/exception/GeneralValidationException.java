package com.netgroup.unionfintech.conferencemgr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) 
public class GeneralValidationException extends RuntimeException {

	private static final long serialVersionUID = -3110036492437938817L;

	public GeneralValidationException(String message) {
		super(message);
	}
}
