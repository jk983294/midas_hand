package com.victor.midas.model.report;

/**
 * contains several sheet data and some metadata
 */
public class MidasReportData {

    private String announcementId, announcementTitle;

    private Integer cob;

    public MidasReportData(String announcementId, String announcementTitle, Integer cob) {
        this.announcementId = announcementId;
        this.announcementTitle = announcementTitle;
        this.cob = cob;
    }
}
