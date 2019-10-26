/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.autodistribution;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimePeriod {
    private Integer startTimeInMinutes;
    private Integer endTimeInMinutes;
    private static final Pattern PERIOD_PATTERN = Pattern.compile("([0-1][0-9]|2[0-3]):[0-5][0-9]-([0-1][0-9]|2[0-3]):[0-5][0-9]");

    public TimePeriod(String period){
        Matcher m = PERIOD_PATTERN.matcher(period);
        if(!m.find()){
            throw new IllegalArgumentException("Not valid period");
        }
        // 12:00-19:00, where 12:00 is start time and 19:00 is end time
        String[] sa = period.split("-");
        String startTime = sa[0];
        String endTime = sa[1];
        
        String[] splitStartTime = startTime.split(":");
        Integer hourStart = Integer.valueOf(splitStartTime[0]);
        Integer minutesStart = Integer.valueOf(splitStartTime[1]);
        startTimeInMinutes = hourStart * 60 + minutesStart;

        String[] splitEndTime = endTime.split(":");
        Integer hourEnd = Integer.valueOf(splitEndTime[0]);
        Integer minutesEnd = Integer.valueOf(splitEndTime[1]);
        endTimeInMinutes = hourEnd * 60 + minutesEnd;
    }

    public boolean between(Integer minutes){
        return startTimeInMinutes <= minutes && minutes <= endTimeInMinutes;
    }

    public Integer getStartTimeInMinutes() {
        return startTimeInMinutes;
    }

    public Integer getEndTimeInMinutes() {
        return endTimeInMinutes;
    }
}
