package com.systrack.pro.exception;

public class DeviceOverloadException extends Exception {
	
    private static final long serialVersionUID = 1L;
    
    public DeviceOverloadException(String message) {
        super(message);
    }
    
    public DeviceOverloadException(String message, Throwable cause) {
        super(message, cause);
    }
}