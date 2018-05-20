package com.tecsolvent.wizspeak.notification.util;

public class ValidationUtil {
	
	/**
	 * Utility method to check for null arguments
	 * @param obj, non null object
	 * @param msg, error msg in case of null obj.
	 */
	public static void validateNonNull(Object obj, String msg) {
		if (obj == null) {
			throw new IllegalArgumentException(msg);
		}
	}

}
