/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.utils.FilenameEscapeUtils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.12.2009
 * Time: 14:14:00
 * To change this template use File | Settings | File Templates.
 */
public class DailyFileCreator {

    private static final int MAX_ATTEMPTS = 5;
    private final String basePath;
    private final DateFormat dateFormat;

    public DailyFileCreator(String basePath, DateFormat dateFormat) {
        this.basePath = basePath;
        this.dateFormat = dateFormat;
    }

    public File createFile(String baseName, String suffix) throws Exception {
        int attemp = 0;
        String filename = FilenameEscapeUtils.escapeFileName(baseName);
        while (attemp != MAX_ATTEMPTS) {
            String datePathPart = FilenameEscapeUtils.escapeDirectoryName(dateFormat.format(new Date()));
            File reportDocumentDir = new File(FilenameUtils.concat(basePath, datePathPart));
            if (reportDocumentDir.exists() || reportDocumentDir.mkdirs()) {
                String newFilename;
                if (0 == attemp) {
                    newFilename = String.format("%s.%s", filename, suffix);
                } else {
                    newFilename = String.format("%s-%d.%s", filename, attemp, suffix);
                }
                File reportDocumentFile = new File(FilenameUtils.concat(reportDocumentDir.getPath(), newFilename));
                if (reportDocumentFile.createNewFile()) {
                    return reportDocumentFile;
                }
            }
            ++attemp;
        }
        throw new IllegalArgumentException();
    }

    public String getBasePath() {
        return basePath;
    }
}