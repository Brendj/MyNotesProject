package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfNotifications {

    private long idofnotification;

    public long getIdofnotification() {
        return idofnotification;
    }

    public void setIdofnotification(long idofnotification) {
        this.idofnotification = idofnotification;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long notificationtime;

    public long getNotificationtime() {
        return notificationtime;
    }

    public void setNotificationtime(long notificationtime) {
        this.notificationtime = notificationtime;
    }

    private int notificationtype;

    public int getNotificationtype() {
        return notificationtype;
    }

    public void setNotificationtype(int notificationtype) {
        this.notificationtype = notificationtype;
    }

    private String notificationtext;

    public String getNotificationtext() {
        return notificationtext;
    }

    public void setNotificationtext(String notificationtext) {
        this.notificationtext = notificationtext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfNotifications that = (CfNotifications) o;

        if (idofclient != that.idofclient) {
            return false;
        }
        if (idofnotification != that.idofnotification) {
            return false;
        }
        if (notificationtime != that.notificationtime) {
            return false;
        }
        if (notificationtype != that.notificationtype) {
            return false;
        }
        if (notificationtext != null ? !notificationtext.equals(that.notificationtext)
                : that.notificationtext != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofnotification ^ (idofnotification >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (notificationtime ^ (notificationtime >>> 32));
        result = 31 * result + notificationtype;
        result = 31 * result + (notificationtext != null ? notificationtext.hashCode() : 0);
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
