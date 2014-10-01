package ru.axetta.ecafe.processor.core.report.model.autoenterevent;

/**
 * User: shamil
 * Date: 23.09.14
 * Time: 17:07
 */
public class Event implements Comparable<Event>{
    private long time; // время события
    private int passdirection; // направление (вошел, вышел)
    private String guardianFIO; // ФИО представителя для садиков

    public Event(long time, int passdirection, String guardianFIO) {
        this.time = time;
        this.passdirection = passdirection;
        this.guardianFIO = guardianFIO;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getPassdirection() {
        return passdirection;
    }

    public void setPassdirection(int passdirection) {
        this.passdirection = passdirection;
    }

    public String getGuardianFIO() {
        return guardianFIO;
    }

    public void setGuardianFIO(String guardianFIO) {
        this.guardianFIO = guardianFIO;
    }

    @Override
    public int compareTo(Event event) {
        return (time<event.getTime() ? -1 : (time==event.getTime() ? 0 : 1));
    }
}

