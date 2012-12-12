package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfSettlements {

    private long idofsettlement;

    public long getIdofsettlement() {
        return idofsettlement;
    }

    public void setIdofsettlement(long idofsettlement) {
        this.idofsettlement = idofsettlement;
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

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private long paymentdate;

    public long getPaymentdate() {
        return paymentdate;
    }

    public void setPaymentdate(long paymentdate) {
        this.paymentdate = paymentdate;
    }

    private String paymentdoc;

    public String getPaymentdoc() {
        return paymentdoc;
    }

    public void setPaymentdoc(String paymentdoc) {
        this.paymentdoc = paymentdoc;
    }

    private long summa;

    public long getSumma() {
        return summa;
    }

    public void setSumma(long summa) {
        this.summa = summa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfSettlements that = (CfSettlements) o;

        if (createddate != that.createddate) {
            return false;
        }
        if (idofcontragentpayer != that.idofcontragentpayer) {
            return false;
        }
        if (idofcontragentreceiver != that.idofcontragentreceiver) {
            return false;
        }
        if (idofsettlement != that.idofsettlement) {
            return false;
        }
        if (paymentdate != that.paymentdate) {
            return false;
        }
        if (summa != that.summa) {
            return false;
        }
        if (paymentdoc != null ? !paymentdoc.equals(that.paymentdoc) : that.paymentdoc != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofsettlement ^ (idofsettlement >>> 32));
        result = 31 * result + (int) (idofcontragentpayer ^ (idofcontragentpayer >>> 32));
        result = 31 * result + (int) (idofcontragentreceiver ^ (idofcontragentreceiver >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (paymentdate ^ (paymentdate >>> 32));
        result = 31 * result + (paymentdoc != null ? paymentdoc.hashCode() : 0);
        result = 31 * result + (int) (summa ^ (summa >>> 32));
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
