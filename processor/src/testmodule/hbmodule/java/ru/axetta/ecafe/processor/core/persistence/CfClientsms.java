package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfClientsms {

    private String idofsms;

    public String getIdofsms() {
        return idofsms;
    }

    public void setIdofsms(String idofsms) {
        this.idofsms = idofsms;
    }

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long idoftransaction;

    public long getIdoftransaction() {
        return idoftransaction;
    }

    public void setIdoftransaction(long idoftransaction) {
        this.idoftransaction = idoftransaction;
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private int contentstype;

    public int getContentstype() {
        return contentstype;
    }

    public void setContentstype(int contentstype) {
        this.contentstype = contentstype;
    }

    private String textcontents;

    public String getTextcontents() {
        return textcontents;
    }

    public void setTextcontents(String textcontents) {
        this.textcontents = textcontents;
    }

    private int deliverystatus;

    public int getDeliverystatus() {
        return deliverystatus;
    }

    public void setDeliverystatus(int deliverystatus) {
        this.deliverystatus = deliverystatus;
    }

    private long servicesenddate;

    public long getServicesenddate() {
        return servicesenddate;
    }

    public void setServicesenddate(long servicesenddate) {
        this.servicesenddate = servicesenddate;
    }

    private long senddate;

    public long getSenddate() {
        return senddate;
    }

    public void setSenddate(long senddate) {
        this.senddate = senddate;
    }

    private long deliverydate;

    public long getDeliverydate() {
        return deliverydate;
    }

    public void setDeliverydate(long deliverydate) {
        this.deliverydate = deliverydate;
    }

    private long price;

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfClientsms that = (CfClientsms) o;

        if (contentstype != that.contentstype) {
            return false;
        }
        if (deliverydate != that.deliverydate) {
            return false;
        }
        if (deliverystatus != that.deliverystatus) {
            return false;
        }
        if (idofclient != that.idofclient) {
            return false;
        }
        if (idoftransaction != that.idoftransaction) {
            return false;
        }
        if (price != that.price) {
            return false;
        }
        if (senddate != that.senddate) {
            return false;
        }
        if (servicesenddate != that.servicesenddate) {
            return false;
        }
        if (version != that.version) {
            return false;
        }
        if (idofsms != null ? !idofsms.equals(that.idofsms) : that.idofsms != null) {
            return false;
        }
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) {
            return false;
        }
        if (textcontents != null ? !textcontents.equals(that.textcontents) : that.textcontents != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = idofsms != null ? idofsms.hashCode() : 0;
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (idoftransaction ^ (idoftransaction >>> 32));
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + contentstype;
        result = 31 * result + (textcontents != null ? textcontents.hashCode() : 0);
        result = 31 * result + deliverystatus;
        result = 31 * result + (int) (servicesenddate ^ (servicesenddate >>> 32));
        result = 31 * result + (int) (senddate ^ (senddate >>> 32));
        result = 31 * result + (int) (deliverydate ^ (deliverydate >>> 32));
        result = 31 * result + (int) (price ^ (price >>> 32));
        return result;
    }

    private CfClients cfClientsByIdofclient;

    public CfClients getCfClientsByIdofclient() {
        return cfClientsByIdofclient;
    }

    public void setCfClientsByIdofclient(CfClients cfClientsByIdofclient) {
        this.cfClientsByIdofclient = cfClientsByIdofclient;
    }

    private CfTransactions cfTransactionsByIdoftransaction;

    public CfTransactions getCfTransactionsByIdoftransaction() {
        return cfTransactionsByIdoftransaction;
    }

    public void setCfTransactionsByIdoftransaction(CfTransactions cfTransactionsByIdoftransaction) {
        this.cfTransactionsByIdoftransaction = cfTransactionsByIdoftransaction;
    }
}
