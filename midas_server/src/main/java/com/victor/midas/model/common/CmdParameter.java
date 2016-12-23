package com.victor.midas.model.common;

import java.util.List;

/**
 * cmd constant
 */
public enum CmdParameter {
    guba,
    concept;

    public static CmdParameter getParameter(CmdParameter defaultPara, List<String> params, int index){
        if(params != null && params.size() > index){
            return CmdParameter.valueOf(params.get(index));
        } else {
            return defaultPara;
        }
    }

}
