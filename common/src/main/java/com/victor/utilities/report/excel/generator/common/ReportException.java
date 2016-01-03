package com.victor.utilities.report.excel.generator.common;


public class ReportException extends Exception {
    private static final long serialVersionUID = 16546543L;

    public ReportException() {
        super();
    }

    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportException(String message) {
        super(message);
    }

    public ReportException(Throwable cause) {
        super(cause);
    }

}