package com.victor.midas.model.report;


import java.util.regex.Pattern;

public class ReportLine {

    public int lineNumber, orginalLineNumber;
    public String text;

    public ReportLine(int lineNumber, int orginalLineNumber, String text) {
        this.lineNumber = lineNumber;
        this.orginalLineNumber = orginalLineNumber;
        this.text = text;
    }

    private static final Pattern emptyLinePattern = Pattern.compile("\\s*");
    public static boolean isEmptyLine(String str){
        return str != null && emptyLinePattern.matcher(str).matches();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getOrginalLineNumber() {
        return orginalLineNumber;
    }

    public void setOrginalLineNumber(int orginalLineNumber) {
        this.orginalLineNumber = orginalLineNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
