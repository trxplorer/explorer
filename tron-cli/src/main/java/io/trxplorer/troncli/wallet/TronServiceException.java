package io.trxplorer.troncli.wallet;

public class TronServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TronServiceException(String msg,Throwable err) {
		super(msg,err);
	}
	
}
