package com.victor.visualization.utils;

/**
 * exception for index calculation
 */
public class VisualException extends Exception {

    public VisualException() {
    }

    public VisualException(String message) {
        super(message);
    }

    public VisualException(String message, Throwable cause) {
        super(message, cause);
    }

    public VisualException(Throwable cause) {
        super(cause);
    }

    public VisualException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
