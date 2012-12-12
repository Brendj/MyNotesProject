package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfTransactionjournal {

    private long idoftransactionjournal;

    public long getIdoftransactionjournal() {
        return idoftransactionjournal;
    }

    public void setIdoftransactionjournal(long idoftransactionjournal) {
        this.idoftransactionjournal = idoftransactionjournal;
    }

    private long transdate;

    public long getTransdate() {
        return transdate;
    }

    public void setTransdate(long transdate) {
        this.transdate = transdate;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long idofinternaloperation;

    public long getIdofinternaloperation() {
        return idofinternaloperation;
    }

    public void setIdofinternaloperation(long idofinternaloperation) {
        this.idofinternaloperation = idofinternaloperation;
    }

    private String ogrn;

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    private String clientsan;

    public String getClientsan() {
        return clientsan;
    }

    public void setClientsan(String clientsan) {
        this.clientsan = clientsan;
    }

    private String clienttype;

    public String getClienttype() {
        return clienttype;
    }

    public void setClienttype(String clienttype) {
        this.clienttype = clienttype;
    }

    private String entername;

    public String getEntername() {
        return entername;
    }

    public void setEntername(String entername) {
        this.entername = entername;
    }

    private String servicecode;

    public String getServicecode() {
        return servicecode;
    }

    public void setServicecode(String servicecode) {
        this.servicecode = servicecode;
    }

    private String transactioncode;

    public String getTransactioncode() {
        return transactioncode;
    }

    public void setTransactioncode(String transactioncode) {
        this.transactioncode = transactioncode;
    }

    private String cardtypecode;

    public String getCardtypecode() {
        return cardtypecode;
    }

    public void setCardtypecode(String cardtypecode) {
        this.cardtypecode = cardtypecode;
    }

    private String cardidentitycode;

    public String getCardidentitycode() {
        return cardidentitycode;
    }

    public void setCardidentitycode(String cardidentitycode) {
        this.cardidentitycode = cardidentitycode;
    }

    private String cardidentityname;

    public String getCardidentityname() {
        return cardidentityname;
    }

    public void setCardidentityname(String cardidentityname) {
        this.cardidentityname = cardidentityname;
    }

    private String uecid;

    public String getUecid() {
        return uecid;
    }

    public void setUecid(String uecid) {
        this.uecid = uecid;
    }

    private long contractid;

    public long getContractid() {
        return contractid;
    }

    public void setContractid(long contractid) {
        this.contractid = contractid;
    }

    private long financialamount;

    public long getFinancialamount() {
        return financialamount;
    }

    public void setFinancialamount(long financialamount) {
        this.financialamount = financialamount;
    }

    private long accountingdate;

    public long getAccountingdate() {
        return accountingdate;
    }

    public void setAccountingdate(long accountingdate) {
        this.accountingdate = accountingdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfTransactionjournal that = (CfTransactionjournal) o;

        if (accountingdate != that.accountingdate) {
            return false;
        }
        if (contractid != that.contractid) {
            return false;
        }
        if (financialamount != that.financialamount) {
            return false;
        }
        if (idofinternaloperation != that.idofinternaloperation) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (idoftransactionjournal != that.idoftransactionjournal) {
            return false;
        }
        if (transdate != that.transdate) {
            return false;
        }
        if (cardidentitycode != null ? !cardidentitycode.equals(that.cardidentitycode)
                : that.cardidentitycode != null) {
            return false;
        }
        if (cardidentityname != null ? !cardidentityname.equals(that.cardidentityname)
                : that.cardidentityname != null) {
            return false;
        }
        if (cardtypecode != null ? !cardtypecode.equals(that.cardtypecode) : that.cardtypecode != null) {
            return false;
        }
        if (clientsan != null ? !clientsan.equals(that.clientsan) : that.clientsan != null) {
            return false;
        }
        if (clienttype != null ? !clienttype.equals(that.clienttype) : that.clienttype != null) {
            return false;
        }
        if (entername != null ? !entername.equals(that.entername) : that.entername != null) {
            return false;
        }
        if (ogrn != null ? !ogrn.equals(that.ogrn) : that.ogrn != null) {
            return false;
        }
        if (servicecode != null ? !servicecode.equals(that.servicecode) : that.servicecode != null) {
            return false;
        }
        if (transactioncode != null ? !transactioncode.equals(that.transactioncode) : that.transactioncode != null) {
            return false;
        }
        if (uecid != null ? !uecid.equals(that.uecid) : that.uecid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoftransactionjournal ^ (idoftransactionjournal >>> 32));
        result = 31 * result + (int) (transdate ^ (transdate >>> 32));
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (idofinternaloperation ^ (idofinternaloperation >>> 32));
        result = 31 * result + (ogrn != null ? ogrn.hashCode() : 0);
        result = 31 * result + (clientsan != null ? clientsan.hashCode() : 0);
        result = 31 * result + (clienttype != null ? clienttype.hashCode() : 0);
        result = 31 * result + (entername != null ? entername.hashCode() : 0);
        result = 31 * result + (servicecode != null ? servicecode.hashCode() : 0);
        result = 31 * result + (transactioncode != null ? transactioncode.hashCode() : 0);
        result = 31 * result + (cardtypecode != null ? cardtypecode.hashCode() : 0);
        result = 31 * result + (cardidentitycode != null ? cardidentitycode.hashCode() : 0);
        result = 31 * result + (cardidentityname != null ? cardidentityname.hashCode() : 0);
        result = 31 * result + (uecid != null ? uecid.hashCode() : 0);
        result = 31 * result + (int) (contractid ^ (contractid >>> 32));
        result = 31 * result + (int) (financialamount ^ (financialamount >>> 32));
        result = 31 * result + (int) (accountingdate ^ (accountingdate >>> 32));
        return result;
    }
}
