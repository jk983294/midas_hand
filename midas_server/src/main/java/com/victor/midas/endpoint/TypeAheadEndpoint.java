package com.victor.midas.endpoint;

import com.victor.midas.dao.StockInfoDao;
import com.victor.midas.endpoint.response.TypeAheadResponse;
import com.victor.midas.model.common.CmdType;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.TaskMgr;
import com.victor.midas.services.TypeAhead;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.StringPatternAware;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * for type ahead
 */
@RestController
@RequestMapping("/typeahead")
public class TypeAheadEndpoint {
    private final Logger logger = Logger.getLogger(TypeAheadEndpoint.class);

    @Autowired
    private StockInfoDao stockInfoDao;
    @Autowired
    private TypeAhead typeAhead;
    @Autowired
    private StocksService stocksService;
    @Autowired
    private TaskMgr taskMgr;

    private static final int MAX_TIPS_ENTRY = 10;

    @GET
    @RequestMapping("/{query}")
    @Produces(MediaType.APPLICATION_JSON)
    public TypeAheadResponse getTips(@PathVariable("query") String query) {
        List<String> tips = getTipList(query);
        TypeAheadResponse response = new TypeAheadResponse(tips, MidasConstants.RESPONSE_SUCCESS, null);
        return response;
    }


    /**
     * process query, always get first auto-complete string for sub-query,
     * if could not find matched auto-complete string, use original string
     */
    @GET
    @RequestMapping("/action/{query}")
    @Produces(MediaType.APPLICATION_JSON)
    public TypeAheadResponse getAction(@PathVariable("query") String query) {
        TypeAheadResponse response = new TypeAheadResponse();

        try {
            if(StringUtils.isNotEmpty(query)){
                String[] queries = query.split("\\|");
                List<String> actions = new ArrayList<>();
                for(int j = 0; j < queries.length; j++){
                    String actionStr = queries[j].trim();
                    StringBuilder tip = new StringBuilder("");
                    String[] stringLets = actionStr.split(" ");
                    if(stringLets.length > 0){
                        if("trainSingle".equalsIgnoreCase(stringLets[0])){
                            tip.append(query);
                        } else {
                            for (int i = 0; i < stringLets.length; i++) {
                                List<String> subTips = typeAhead.query(stringLets[i]);
                                if(subTips.size() > 0) {
                                    if(subTips.contains(stringLets[i])){    // use exact match first
                                        tip.append(stringLets[i]).append(" ");
                                    } else {
                                        tip.append(subTips.get(0)).append(" ");
                                    }
                                } else {
                                    tip.append(stringLets[i]).append(" ");
                                }
                            }
                        }
                    }
                    actions.add(tip.toString().trim());
                }
                if(actions.size() == 1){
                    response.setAction(actions.get(0));
                    dealWithAction(actions.get(0), response);
                } else {
                    response.setDescription("Piped Task submitted.");
                    response.setStatus(MidasConstants.RESPONSE_SUCCESS);
                    taskMgr.submitPipedTasks(actions);
                }
            } else {
                response.setStatus(MidasConstants.RESPONSE_FAIL);
                response.setDescription("No query string");
            }
        } catch (Exception e){
            logger.error(e);
            response.setStatus(MidasConstants.RESPONSE_FAIL);
            response.setDescription("error caused by : " + e);
        }
        return response;
    }

    /**
     * deal with action string
     */
    private void dealWithAction(String action, TypeAheadResponse response ){
        logger.info("receive action string : " + action);
        String[] actions = action.split(" ");
        StringBuilder responseStr = new StringBuilder();
        if(actions.length > 0){
            try {
                CmdType cmdType = CmdType.valueOf(actions[0]);
                List<String> params = new ArrayList<>(Arrays.asList(actions));
                params.remove(0);
                taskMgr.cmd(cmdType, params);
                responseStr.append(actions[0] + " task is submitted.");
            } catch (IllegalArgumentException e){
                if(StringPatternAware.isStockCode(actions[0])){
                    responseStr.append("jump to stock detail.");
                } else {
                    responseStr.append("no matched cmd is found");
                    response.setStatus(MidasConstants.RESPONSE_FAIL);
                }
            }
        } else {
            responseStr.append("no action recognized");
        }
        response.setDescription(responseStr.toString());
        if(response.getStatus() == null ){
            response.setStatus(MidasConstants.RESPONSE_SUCCESS);
        }
    }

    /**
     * split query string, get each sub query string's tips, combine all sub query strings' tips
     */
    private List<String> getTipList(String query){
        List<String> totalTips = new ArrayList<>();
        if(StringUtils.isNotEmpty(query)){
            String[] stringLets = query.split(" ");

            int currentMaxTipEntryCnt = MAX_TIPS_ENTRY;
            for (int i = 0; i < stringLets.length; i++) {
                List<String> subTips = typeAhead.query(stringLets[i]);
                if(i < stringLets.length - 1 && subTips.contains(stringLets[i])){  // not last one, when exact match, then use that exact one
                    subTips.clear();
                    subTips.add(stringLets[i]);
                }
                if(subTips.size() == 0){                                            // nothing found, use original one
                    subTips.add(stringLets[i]);
                }
                if("trainSingle".equalsIgnoreCase(stringLets[0]) && i > 1){         // don't infer parameter for trainSingle
                    subTips.clear();
                    subTips.add(stringLets[i]);
                }
                // if current subTips is more than currentMaxTipEntryCnt, cut it to current max
                if( currentMaxTipEntryCnt < subTips.size()){
                    subTips = subTips.subList(0, currentMaxTipEntryCnt);
                }

                if(subTips.size() > 0) {
                    currentMaxTipEntryCnt = Math.max(1, (int) Math.ceil(currentMaxTipEntryCnt / subTips.size()));
                }

                totalTips = mergeTips(totalTips, subTips);
            }
        }
        return totalTips;
    }

    /**
     * get subtips1 * subtips2
     */
    private List<String> mergeTips(List<String> subtips1, List<String> subtips2){
        List<String> totalTips = new ArrayList<>();
        if(subtips1.size() == 0){
            totalTips.addAll(subtips2);
        } else if(subtips2.size() == 0){
            totalTips.addAll(subtips1);
        } else {
            for (int i = 0; i < subtips1.size(); i++) {
                if(StringUtils.isNotEmpty(subtips1.get(i))){
                    String s1 = subtips1.get(i);
                    for (int j = 0; j < subtips2.size(); j++) {
                        if(StringUtils.isNotEmpty(subtips2.get(j))){
                            totalTips.add(s1 + " " + subtips2.get(j));
                        }
                    }
                }
            }
        }
        return totalTips;
    }
}
