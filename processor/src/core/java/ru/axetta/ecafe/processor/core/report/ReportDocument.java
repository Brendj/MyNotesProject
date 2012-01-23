/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.12.2009
 * Time: 14:14:00
 * To change this template use File | Settings | File Templates.
 */
public class ReportDocument {

    private final List<File> files;

    public ReportDocument(List<File> files) {
        this.files = files;
    }

    public List<File> getFiles() {
        return files;
    }
}
