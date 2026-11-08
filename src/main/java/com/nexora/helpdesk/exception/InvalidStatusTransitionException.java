package com.nexora.helpdesk.exception;

public class InvalidStatusTransitionException extends RuntimeException {
	public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
