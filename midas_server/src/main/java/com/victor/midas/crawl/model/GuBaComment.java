package com.victor.midas.crawl.model;

import com.google.common.base.Joiner;
import com.victor.spider.core.selector.Selectable;
import com.victor.utilities.utils.MathHelper;
import com.victor.utilities.utils.RegExpHelper;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * comment for topic
 */
public class GuBaComment {

    private String author;

    private Timestamp time;

    private Long topicId;

    private Long commentId, commentUserId;

    private String content;

    public static GuBaComment generate(Selectable selectable){
        GuBaComment comment = new GuBaComment();
        comment.commentId = MathHelper.tryParse2Long(selectable.xpath("/div/@data-huifuid").toString());
        comment.commentUserId = MathHelper.tryParse2Long(selectable.xpath("/div/@data-huifuuid").toString());
        comment.author = selectable.xpath("/div/div[@class=\"zwlitx\"]/div[@class=\"zwlitxt\"]/div[@class=\"zwlianame\"]//span[@class=\"gray\"]/text()").toString();
        if(comment.author == null){
            comment.author = selectable.xpath("/div/div[@class=\"zwlitx\"]/div[@class=\"zwlitxt\"]/div[@class=\"zwlianame\"]//span[@class=\"zwnick\"]/a/text()").toString();
        }
        comment.time = Timestamp.valueOf(RegExpHelper.extractTimeStr(selectable.xpath("/div/div[@class=\"zwlitx\"]/div[@class=\"zwlitxt\"]/div[@class=\"zwlitime\"]/text()").toString()));
        comment.content = selectable.xpath("/div/div[@class=\"zwlitx\"]/div[@class=\"zwlitxt\"]/div[@class=\"zwlitext\"]/text()").toString();
        String imgs = Joiner.on(",").skipNulls().join(selectable.xpath("/div/div[@class=\"zwlitx\"]/div[@class=\"zwlitxt\"]/div[@class=\"zwlitext\"]/img/@title").all());
        if(StringUtils.isNotEmpty(imgs)){
            comment.content = comment.content + "," + imgs;
        }
        return comment;
    }

    public static List<GuBaComment> generate(List<Selectable> selectables){
        List<GuBaComment> comments = new ArrayList<>();
        for(Selectable selectable : selectables){
            comments.add(generate(selectable));
        }
        return comments;
    }

    public static void setTopicId(List<GuBaComment> comments, Long topicId){
        for(GuBaComment comment : comments){
            comment.topicId = topicId;
        }
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
