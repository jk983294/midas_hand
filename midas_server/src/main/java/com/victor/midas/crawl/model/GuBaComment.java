package com.victor.midas.crawl.model;

import com.google.common.base.Joiner;
import com.victor.spider.core.selector.Selectable;
import com.victor.utilities.utils.MathHelper;
import com.victor.utilities.utils.RegExpHelper;
import com.victor.utilities.utils.TimeHelper;
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

    private GuBaComment commentAgainst;

    public static GuBaComment generate(Selectable selectable){
        GuBaComment comment = new GuBaComment();
        comment.commentId = MathHelper.tryParse2Long(selectable.xpath("/div/@data-huifuid").toString());
        comment.commentUserId = MathHelper.tryParse2Long(selectable.xpath("/div/@data-huifuuid").toString());
        comment.author = selectable.xpath("/div/div[@class=\"zwlitx\"]/div[@class=\"zwlitxt\"]/div[@class=\"zwlianame\"]//span[@class=\"gray\"]/text()").toString();
        if(comment.author == null){
            comment.author = selectable.xpath("/div/div[@class=\"zwlitx\"]/div[@class=\"zwlitxt\"]/div[@class=\"zwlianame\"]//span[@class=\"zwnick\"]/a/text()").toString();
        }
        List<Selectable> againsts = selectable.xpath("/div/div[@class=\"zwlitx\"]/div[@class=\"zwlitxt\"]/div[@class=\"zwlitalkbox\"]").nodes();
        Selectable against = againsts.size() > 0 ? againsts.get(0) : null;
        if(against != null){
            GuBaComment commentAgainst = new GuBaComment();
            commentAgainst.author = against.xpath("/div/div[@class=\"zwlitalkboxh\"]/div[@class=\"zwlitalkboxuinfo\"]/div[@class=\"zwnick\"]/a/text()").toString();
            commentAgainst.commentUserId = MathHelper.tryParse2Long(against.xpath("/div/div[@class=\"zwlitalkboxh\"]/div[@class=\"zwlitalkboxuinfo\"]/div[@class=\"zwnick\"]/a/@data-popper").toString());
            if(comment.author == null){
                commentAgainst.author = against.xpath("/div/div[@class=\"zwlitalkboxh\"]/div[@class=\"zwlitalkboxuinfo\"]/div[@class=\"zwnick\"]/span[@class=\"gray\"]/text()").toString();
            }
            commentAgainst.time = TimeHelper.tryParse2Timestamp(RegExpHelper.extractTimeStr(against.xpath("/div/div[@class=\"zwlitalkboxh\"]/div[@class=\"zwlitalkboxuinfo\"]/div[@class=\"zwlitalkboxtime\"]/text()").toString()));
            commentAgainst.content = getContentFromHtml(against.xpath("/div/div[@class=\"zwlitalkboxtext\"]"));
            comment.commentAgainst = commentAgainst;
        }
        comment.time = TimeHelper.tryParse2Timestamp(RegExpHelper.extractTimeStr(selectable.xpath("/div/div[@class=\"zwlitx\"]/div[@class=\"zwlitxt\"]/div[@class=\"zwlitime\"]/text()").toString()));
        comment.content = getContentFromHtml(selectable.xpath("/div/div[@class=\"zwlitx\"]/div[@class=\"zwlitxt\"]/div[@class=\"zwlitext\"]"));
        return comment;
    }

    private static String getContentFromHtml(Selectable selectable){
        String str = selectable.xpath("/div/text()").toString();
        String imgs = Joiner.on(",").skipNulls().join(selectable.xpath("/div/img/@title").all());
        if(StringUtils.isNotEmpty(imgs)){
            str = str + "," + imgs;
        }
        return str;
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
