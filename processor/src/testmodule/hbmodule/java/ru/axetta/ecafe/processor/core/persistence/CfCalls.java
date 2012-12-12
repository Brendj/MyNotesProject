package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfCalls {

    private long idofcall;

    public long getIdofcall() {
        return idofcall;
    }

    public void setIdofcall(long idofcall) {
        this.idofcall = idofcall;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long calltime;

    public long getCalltime() {
        return calltime;
    }

    public void setCalltime(long calltime) {
        this.calltime = calltime;
    }

    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    private int calltype;

    public int getCalltype() {
        return calltype;
    }

    public void setCalltype(int calltype) {
        this.calltype = calltype;
    }

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfCalls cfCalls = (CfCalls) o;

        if (calltime != cfCalls.calltime) {
            return false;
        }
        if (calltype != cfCalls.calltype) {
            return false;
        }
        if (idofcall != cfCalls.idofcall) {
            return false;
        }
        if (idofclient != cfCalls.idofclient) {
            return false;
        }
        if (state != cfCalls.state) {
            return false;
        }
        if (reason != null ? !reason.equals(cfCalls.reason) : cfCalls.reason != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofcall ^ (idofcall >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (calltime ^ (calltime >>> 32));
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + calltype;
        result = 31 * result + state;
        return result;
    }

    private CfClients cfClientsByIdofclient;

    public CfClients getCfClientsByIdofclient() {
        return cfClientsByIdofclient;
    }

    public void setCfClientsByIdofclient(CfClients cfClientsByIdofclient) {
        this.cfClientsByIdofclient = cfClientsByIdofclient;
    }
}
