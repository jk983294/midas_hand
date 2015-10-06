package com.victor.utilities.utils;

import com.victor.utilities.visual.VisualAssist;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

/**
 * unit test for MathHelper
 */
public class CrawlHelperTest {

    @Test
    public void testCrawl() throws IOException {
        VisualAssist.print(CrawlHelper.crawl("http://ifzq.gtimg.cn/stock/relate/data/plate?code=sh601600&_var=_IFLOAD_2"));
        VisualAssist.print(CrawlHelper.crawl("http://qt.gtimg.cn/r=0.10460156644694507q=bkqt012007,bkqt021170,bkqt028300,bkqt031100,bkqt04ZC60"));
        VisualAssist.print(CrawlHelper.crawl("http://qt.gtimg.cn/r=0.2024813152384013q=marketStat,stdunixtime,sh601600,"));
    }

    @Test
    public  void parse(){
        String result = "_IFLOAD_2={\"code\":0,\"msg\":\"\",\"data\":[{\"code\":\"012007\",\"name\":\"\\u6709\\u8272\\u91d1\\u5c5e\"},{\"code\":\"021170\",\"name\":\"\\u878d\\u8d44\\u878d\\u5238\"},{\"code\":\"028300\",\"name\":\"\\u4e2d\\u5b57\\u5934\"},{\"code\":\"031100\",\"name\":\"\\u5317\\u4eac\"},{\"code\":\"04ZC60\",\"name\":\"\\u91d1\\u5c5e\\u3001\\u975e\\u91d1\\u5c5e\"},{\"code\":\"061050\",\"name\":\"\\u6709\\u8272\\u91d1\\u5c5e\"},{\"code\":\"621510\",\"name\":\"\\u539f\\u6750\\u6599\"}]}";
        result = result.substring(result.indexOf("{"));
        JSONObject json= new JSONObject(result);
        JSONArray jsonArray=json.getJSONArray("data");
        for(int i=0;i < jsonArray.length(); i++){
            JSONObject concept =(JSONObject) jsonArray.get(i);
            VisualAssist.print(concept.get("code"));
            VisualAssist.print(concept.get("name"));
        }
        VisualAssist.print(result);
    }


}
