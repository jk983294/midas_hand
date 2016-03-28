package com.victor.midas.calculator.common.model;

import java.util.ArrayList;
import java.util.List;

/**
 * represent one line connected by segment lines
 */
public class IndexLines {

    public List<IndexLine> lines = new ArrayList<>();
    public int pointCnt, lineCnt;
    public boolean isCrossZero;

    public void update(int index, double value){
        isCrossZero = false;
        IndexPoint point = new IndexPoint(index, value);
        if(lines.size() == 0){
            IndexLine line = new IndexLine(point);
            lines.add(line);
        } else {
            IndexLine line = lines.get(lines.size() - 1);
            if(line.update(point)){
                isCrossZero = line.isCrossZero;
            } else {
                IndexLine newLine = new IndexLine(line.point2, point);
                lines.add(newLine);
                isCrossZero = newLine.isCrossZero;
            }
        }
    }

    public IndexLine getLastLine(){
        return lines.get(lines.size() - 1);
    }

    public IndexLine getLastLine(int i){
        if(lines.size() - i > 0) return lines.get(lines.size() - i);
        else return null;
    }
}
