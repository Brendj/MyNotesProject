package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfAddpayments {

    private long idofaddpayment;

    public long getIdofaddpayment() {
        return idofaddpayment;
    }

    public void setIdofaddpayment(long idofaddpayment) {
        this.idofaddpayment = idofaddpayment;
    }

    private long idofcontragentpayer;

    public long getIdofcontragentpayer() {
        return idofcontragentpayer;
    }

    public void setIdofcontragentpayer(long idofcontragentpayer) {
        this.idofcontragentpayer = idofcontragentpayer;
    }

    private long idofcontragentreceiver;

    public long getIdofcontragentreceiver() {
        return idofcontragentreceiver;
    }

    public void setIdofcontragentreceiver(long idofcontragentreceiver) {
        this.idofcontragentreceiver = idofcontragentreceiver;
    }

    private long summa;

    public long getSumma() {
        return summa;
    }

    public void setSumma(long summa) {
        this.summa = summa;
    }

    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private long fromdate;

    public long getFromdate() {
        return fromdate;
    }

    public void setFromdate(long fromdate) {
        this.fromdate = fromdate;
    }

    private long todate;

    public long getTodate() {
        return todate;
    }

    public void setTodate(long todate) {
        this.todate = todate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfAddpayments that = (CfAddpayments) o;

        if (fromdate != that.fromdate) {
            return false;
        }
        if (idofaddpayment != that.idofaddpayment) {
            return false;
        }
        if (idofcontragentpayer != that.idofcontragentpayer) {
            return false;
        }
        if (idofcontragentreceiver != that.idofcontragentreceiver) {
            return false;
        }
        if (summa != that.summa) {
            return false;
        }
        if (todate != that.todate) {
            return false;
        }
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofaddpayment ^ (idofaddpayment >>> 32));
        result = 31 * result + (int) (idofcontragentpayer ^ (idofcontragentpayer >>> 32));
        result = 31 * result + (int) (idofcontragentreceiver ^ (idofcontragentreceiver >>> 32));
        result = 31 * result + (int) (summa ^ (summa >>> 32));
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (int) (fromdate ^ (fromdate >>> 32));
        result = 31 * result + (int) (todate ^ (todate >>> 32));
        return result;
    }

    private CfContragents cfContragentsByIdofcontragentpayer;

    public CfContragents getCfContragentsByIdofcontragentpayer() {
        return cfContragentsByIdofcontragentpayer;
    }

    public void setCfContragentsByIdofcontragentpayer(CfContragents cfContragentsByIdofcontragentpayer) {
        this.cfContragentsByIdofcontragentpayer = cfContragentsByIdofcontragentpayer;
    }

    private CfContragents cfContragentsByIdofcontragentreceiver;

    public CfContragents getCfContragentsByIdofcontragentreceiver() {
        return cfContragentsByIdofcontragentreceiver;
    }

    public void setCfContragentsByIdofcontragentreceiver(CfContragents cfContragentsByIdofcontragentreceiver) {
        this.cfContragentsByIdofcontragentreceiver = cfContragentsByIdofcontragentreceiver;
    }
}
