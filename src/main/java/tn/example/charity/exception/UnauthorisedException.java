package com.whitecape.flayes.exception;

public class UnauthorisedException extends RuntimeException {

	private static final long serialVersionUID = -8236225261507157173L;

	public UnauthorisedException(String message) {
		super(message);
	}

	public UnauthorisedException(String message, Throwable cause) {
		super(message, cause);
	}

}
