/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 10.09.2009
 * Time: 12:33:27
 * To change this template use File | Settings | File Templates.
 */
public class FilenameEscapeUtils {

    private FilenameEscapeUtils() {

    }

    public static String escapeDirectoryName(String directoryName) {
        return escapeFileName(directoryName);
    }

    public static String escapeFileName(String fileame) {
        return fileame.replace('\\', '.').replace('/', '.').replace('+', '.').replace(':', '.');
    }
}