package ru.axetta.ecafe.processor.core.sync;

import java.text.DateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.01.14
 * Time: 18:18
 * To change this template use File | Settings | File Templates.
 */
public class LoadContext {

   private final SyncRequest.MenuGroups menuGroups;
   private final long protoVersion;
   private final DateFormat timeFormat, dateOnlyFormat;

    public LoadContext(SyncRequest.MenuGroups menuGroups, long protoVersion, DateFormat timeFormat,
            DateFormat dateOnlyFormat) {
        this.menuGroups = menuGroups;
        this.protoVersion = protoVersion;
        this.timeFormat = timeFormat;
        this.dateOnlyFormat = dateOnlyFormat;
    }

    public SyncRequest.MenuGroups getMenuGroups() {
        return menuGroups;
    }

    public long getProtoVersion() {
        return protoVersion;
    }

    public DateFormat getTimeFormat() {
        return timeFormat;
    }

    public DateFormat getDateOnlyFormat() {
        return dateOnlyFormat;
    }
}
