package com.whitecape.flayes.config.exception;

import java.util.List;

public final class ExceptionResponse {

	private final Integer status;
	private final String error;
	private final String message;
	private final String path;
	private final List<String> details;

	public ExceptionResponse(Integer status, String error, String message, String path, List<String> details) {
		this.details = details;
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}

	public Integer getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getPath() {
		return path;
	}

	public List<String> getDetails() {
		return details;
	}

}