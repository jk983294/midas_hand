package com.victor.midas.crawl.model;

import java.sql.Timestamp;

/**
 * comment for topic
 */
public class GuBaComment {

    private String user;

    private Timestamp time;

    private Long topicId;

    private String content;


    public GuBaComment(String user, Timestamp time, Long topicId, String content) {
        this.user = user;
        this.time = time;
        this.topicId = topicId;
        this.content = content;
    }
}
