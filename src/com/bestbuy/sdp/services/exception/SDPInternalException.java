package com.bestbuy.sdp.services.exception;

/**
 * Customize exception class
 *
 * @author a148045
 *
 */
public class SDPInternalException extends Exception {


	private String errorCode;

	private String errorMessage;
	/**
	 * Customize exception for catching all the exception in SDP
	 * @param errorCode
	 * @param errorMessage
	 * @param cause
	 */
	public SDPInternalException(String errorCode, String errorMessage,  Throwable cause) {
		if (cause != null) {
			super.initCause(cause);
		}
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public SDPInternalException(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


}
