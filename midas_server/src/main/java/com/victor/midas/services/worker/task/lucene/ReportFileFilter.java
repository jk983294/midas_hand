package com.victor.midas.services.worker.task.lucene;

import java.io.File;
import java.io.FileFilter;

public class ReportFileFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        String path = pathname.getName().toLowerCase();
        return path.endsWith(".pdf")
                && !path.contains("半年度报告")
                && path.contains("年度报告");
    }

}
