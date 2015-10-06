package com.victor.midas.model.db.misc;

import java.util.List;

/**
 * contain a list of stock names
 */
public class FocusedStockNamesDb extends MiscBase {

    public static final String MISC_NAME = "FocusedStockNamesDb";

    private List<String> focus;

    public FocusedStockNamesDb(int[] dates, List<String> focus) {
        super(MISC_NAME);
        this.focus = focus;
    }

    @Override
    public String toString() {
        return "FocusedStockNamesDb{" +
                "focus=" + focus +
                '}';
    }

    public List<String> getFocus() {
        return focus;
    }

    public void setFocus(List<String> focus) {
        this.focus = focus;
    }
}
