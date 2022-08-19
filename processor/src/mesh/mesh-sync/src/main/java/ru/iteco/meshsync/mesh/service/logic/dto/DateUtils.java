package ru.iteco.meshsync.mesh.service.logic.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private final static DateFormat simpleDf = new SimpleDateFormat("yyyy-MM-dd");

    public static Date parseSimpleDate(String date) throws Exception {
        return simpleDf.parse(date);
    }
}
