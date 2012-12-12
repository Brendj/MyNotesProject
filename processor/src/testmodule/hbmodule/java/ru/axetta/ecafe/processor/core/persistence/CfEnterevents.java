package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfEnterevents {

    private long idofenterevent;

    public long getIdofenterevent() {
        return idofenterevent;
    }

    public void setIdofenterevent(long idofenterevent) {
        this.idofenterevent = idofenterevent;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private String entername;

    public String getEntername() {
        return entername;
    }

    public void setEntername(String entername) {
        this.entername = entername;
    }

    private String turnstileaddr;

    public String getTurnstileaddr() {
        return turnstileaddr;
    }

    public void setTurnstileaddr(String turnstileaddr) {
        this.turnstileaddr = turnstileaddr;
    }

    private int passdirection;

    public int getPassdirection() {
        return passdirection;
    }

    public void setPassdirection(int passdirection) {
        this.passdirection = passdirection;
    }

    private int eventcode;

    public int getEventcode() {
        return eventcode;
    }

    public void setEventcode(int eventcode) {
        this.eventcode = eventcode;
    }

    private long idofcard;

    public long getIdofcard() {
        return idofcard;
    }

    public void setIdofcard(long idofcard) {
        this.idofcard = idofcard;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long idoftempcard;

    public long getIdoftempcard() {
        return idoftempcard;
    }

    public void setIdoftempcard(long idoftempcard) {
        this.idoftempcard = idoftempcard;
    }

    private long evtdatetime;

    public long getEvtdatetime() {
        return evtdatetime;
    }

    public void setEvtdatetime(long evtdatetime) {
        this.evtdatetime = evtdatetime;
    }

    private long idofvisitor;

    public long getIdofvisitor() {
        return idofvisitor;
    }

    public void setIdofvisitor(long idofvisitor) {
        this.idofvisitor = idofvisitor;
    }

    private String visitorfullname;

    public String getVisitorfullname() {
        return visitorfullname;
    }

    public void setVisitorfullname(String visitorfullname) {
        this.visitorfullname = visitorfullname;
    }

    private int doctype;

    public int getDoctype() {
        return doctype;
    }

    public void setDoctype(int doctype) {
        this.doctype = doctype;
    }

    private String docserialnum;

    public String getDocserialnum() {
        return docserialnum;
    }

    public void setDocserialnum(String docserialnum) {
        this.docserialnum = docserialnum;
    }

    private long issuedocdate;

    public long getIssuedocdate() {
        return issuedocdate;
    }

    public void setIssuedocdate(long issuedocdate) {
        this.issuedocdate = issuedocdate;
    }

    private long visitdatetime;

    public long getVisitdatetime() {
        return visitdatetime;
    }

    public void setVisitdatetime(long visitdatetime) {
        this.visitdatetime = visitdatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfEnterevents that = (CfEnterevents) o;

        if (doctype != that.doctype) {
            return false;
        }
        if (eventcode != that.eventcode) {
            return false;
        }
        if (evtdatetime != that.evtdatetime) {
            return false;
        }
        if (idofcard != that.idofcard) {
            return false;
        }
        if (idofclient != that.idofclient) {
            return false;
        }
        if (idofenterevent != that.idofenterevent) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (idoftempcard != that.idoftempcard) {
            return false;
        }
        if (idofvisitor != that.idofvisitor) {
            return false;
        }
        if (issuedocdate != that.issuedocdate) {
            return false;
        }
        if (passdirection != that.passdirection) {
            return false;
        }
        if (visitdatetime != that.visitdatetime) {
            return false;
        }
        if (docserialnum != null ? !docserialnum.equals(that.docserialnum) : that.docserialnum != null) {
            return false;
        }
        if (entername != null ? !entername.equals(that.entername) : that.entername != null) {
            return false;
        }
        if (turnstileaddr != null ? !turnstileaddr.equals(that.turnstileaddr) : that.turnstileaddr != null) {
            return false;
        }
        if (visitorfullname != null ? !visitorfullname.equals(that.visitorfullname) : that.visitorfullname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofenterevent ^ (idofenterevent >>> 32));
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (entername != null ? entername.hashCode() : 0);
        result = 31 * result + (turnstileaddr != null ? turnstileaddr.hashCode() : 0);
        result = 31 * result + passdirection;
        result = 31 * result + eventcode;
        result = 31 * result + (int) (idofcard ^ (idofcard >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (idoftempcard ^ (idoftempcard >>> 32));
        result = 31 * result + (int) (evtdatetime ^ (evtdatetime >>> 32));
        result = 31 * result + (int) (idofvisitor ^ (idofvisitor >>> 32));
        result = 31 * result + (visitorfullname != null ? visitorfullname.hashCode() : 0);
        result = 31 * result + doctype;
        result = 31 * result + (docserialnum != null ? docserialnum.hashCode() : 0);
        result = 31 * result + (int) (issuedocdate ^ (issuedocdate >>> 32));
        result = 31 * result + (int) (visitdatetime ^ (visitdatetime >>> 32));
        return result;
    }
}
