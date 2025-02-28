package com.whitecape.flayes.exception;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -8236225261507157173L;

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}