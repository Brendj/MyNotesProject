/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.file.FileUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class RecoverableService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RecoverableService.class);

    private final String DATE_FORMAT = "dd.MM.yyyy'T'HH:mm:ss";
    private final Integer START_DELAY_MIN = 10;

    private String statusFileNameProperty;
    private String statusFileNameDefaultValue;

    public enum Status {
        RUNNING("running"),
        FINISHED("finished");

        private String status;

        private static Map<String, Status> map = new HashMap<String, Status>();
        static {
            for (Status s : Status.values()) {
                map.put(s.status, s);
            }
        }

        Status(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public static Status parse(String description){
            return map.get(description);
        }
    }

    public abstract void recoveryRun() throws Exception;
    public abstract void scheduleSyncRecovery();

    public boolean isFinishedToday() {
        Status status = getCurrentStatus();
        if (Status.FINISHED != status)
            return  false;

        Date date = getCurrentDate();
        if (null == date)
            return false;

        date = CalendarUtils.startOfDay(date);
        Date now = CalendarUtils.startOfDay(new Date());

        return (date.getTime() == now.getTime());
    }

    public void updateStatusFile(Date date, Status status) {
        String fullFileName = getCurrentFileName();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        String content = format.format(date);
        try {
            FileUtils.writeByteArraysToFile(fullFileName, content.getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            logger.error("RecoverableService: unable to write status file", e);
        }
        String fileName = RuntimeContext
                .getInstance().getPropertiesValue(statusFileNameProperty, statusFileNameDefaultValue);

        File statusFile = new File(fullFileName);
        File newStatusFile = new File(fileName + status.getStatus());
        statusFile.renameTo(newStatusFile);
    }

    private String getCurrentFileName() {
        String fileName = RuntimeContext
                .getInstance().getPropertiesValue(statusFileNameProperty, statusFileNameDefaultValue);
        String fullFileName;
        if (FileUtils.isFileExists(fileName + Status.FINISHED.getStatus())) {
            fullFileName = fileName + Status.FINISHED.getStatus();
        } else if (FileUtils.isFileExists(fileName + Status.RUNNING.getStatus())) {
            fullFileName = fileName + Status.RUNNING.getStatus();
        } else {
            fullFileName = fileName + Status.FINISHED.getStatus();
        }
        return fullFileName;
    }

    private Status getCurrentStatus() {
        String fileName = getCurrentFileName();
        String statusString = fileName.substring(fileName.lastIndexOf('.') + 1);
        return Status.parse(statusString);
    }

    private Date getCurrentDate() {
        String fileName = getCurrentFileName();
        byte [] content;
        try {
            content = FileUtils.loadFileAsBytesArray(fileName);
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
            return format.parse(new String(content));
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            logger.error("RecoverableService: unable to read status file", e);
        }
        return null;
    }

    public Date getFireTime() {
        return CalendarUtils.addMinute(new Date(), START_DELAY_MIN);
    }

    public String getStatusFileNameProperty() {
        return statusFileNameProperty;
    }

    public void setStatusFileNameProperty(String statusFileNameProperty) {
        this.statusFileNameProperty = statusFileNameProperty;
    }

    public String getStatusFileNameDefaultValue() {
        return statusFileNameDefaultValue;
    }

    public void setStatusFileNameDefaultValue(String statusFileNameDefaultValue) {
        this.statusFileNameDefaultValue = statusFileNameDefaultValue;
    }
}
