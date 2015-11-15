package com.victor.midas.crawl.model;

import com.victor.spider.core.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

/**
 * gu ba topic snapshot
 */
public class GuBaTopicSnapshot {

    private Double readCount, commentCount;

    private String title;

    private String titleType;

    private String author;

    private String lastUpdate;

    private String publishDate;

    private String link;

    public static GuBaTopicSnapshot generate(Selectable selectable){
        GuBaTopicSnapshot snapshot = new GuBaTopicSnapshot();
        snapshot.readCount = Double.valueOf(selectable.xpath("/div/span[@class=\"l1\"]/text()").toString());
        snapshot.commentCount = Double.valueOf(selectable.xpath("/div/span[@class=\"l2\"]/text()").toString());
        snapshot.title = selectable.xpath("/div/span[@class=\"l3\"]/em/text()").toString();
        snapshot.titleType = selectable.xpath("/div/span[@class=\"l3\"]/a/@href").toString();
        snapshot.link = selectable.xpath("/div/span[@class=\"l3\"]/a/@href").toString();
        snapshot.author = selectable.xpath("/div/span[@class=\"l4\"]/a/text()").toString();
        snapshot.publishDate = selectable.xpath("/div/span[@class=\"l5\"]/text()").toString();
        snapshot.lastUpdate = selectable.xpath("/div/span[@class=\"l6\"]/text()").toString();
        return snapshot;
    }

    public static List<GuBaTopicSnapshot> generate(List<Selectable> selectables){
        List<GuBaTopicSnapshot> snapshots = new ArrayList<>();
        for(Selectable selectable : selectables){
            snapshots.add(generate(selectable));
        }
        return snapshots;
    }

    public Double getReadCount() {
        return readCount;
    }

    public void setReadCount(Double readCount) {
        this.readCount = readCount;
    }

    public Double getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Double commentCount) {
        this.commentCount = commentCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getTitleType() {
        return titleType;
    }

    public void setTitleType(String titleType) {
        this.titleType = titleType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
