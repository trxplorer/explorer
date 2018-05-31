package io.trxplorer.api.service;

public class ServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9139278958887105664L;

	public ServiceException(String msg,Throwable throwable) {
		super(msg,throwable);
	}
	
	public ServiceException(String msg) {
		super(msg);
	}	
	
}
