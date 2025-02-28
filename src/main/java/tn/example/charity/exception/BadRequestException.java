package com.whitecape.flayes.exception;

public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = -8236225261507157173L;

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}
