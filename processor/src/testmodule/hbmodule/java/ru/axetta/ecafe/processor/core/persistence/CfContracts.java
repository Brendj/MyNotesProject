package ru.axetta.ecafe.processor.core.persistence;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfContracts {

    private long idofcontract;

    public long getIdofcontract() {
        return idofcontract;
    }

    public void setIdofcontract(long idofcontract) {
        this.idofcontract = idofcontract;
    }

    private String contractnumber;

    public String getContractnumber() {
        return contractnumber;
    }

    public void setContractnumber(String contractnumber) {
        this.contractnumber = contractnumber;
    }

    private String performer;

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    private String customer;

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    private Timestamp dateofconclusion;

    public Timestamp getDateofconclusion() {
        return dateofconclusion;
    }

    public void setDateofconclusion(Timestamp dateofconclusion) {
        this.dateofconclusion = dateofconclusion;
    }

    private Timestamp dateofclosing;

    public Timestamp getDateofclosing() {
        return dateofclosing;
    }

    public void setDateofclosing(Timestamp dateofclosing) {
        this.dateofclosing = dateofclosing;
    }

    private int contractstate;

    public int getContractstate() {
        return contractstate;
    }

    public void setContractstate(int contractstate) {
        this.contractstate = contractstate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfContracts that = (CfContracts) o;

        if (contractstate != that.contractstate) {
            return false;
        }
        if (idofcontract != that.idofcontract) {
            return false;
        }
        if (contractnumber != null ? !contractnumber.equals(that.contractnumber) : that.contractnumber != null) {
            return false;
        }
        if (customer != null ? !customer.equals(that.customer) : that.customer != null) {
            return false;
        }
        if (dateofclosing != null ? !dateofclosing.equals(that.dateofclosing) : that.dateofclosing != null) {
            return false;
        }
        if (dateofconclusion != null ? !dateofconclusion.equals(that.dateofconclusion)
                : that.dateofconclusion != null) {
            return false;
        }
        if (performer != null ? !performer.equals(that.performer) : that.performer != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofcontract ^ (idofcontract >>> 32));
        result = 31 * result + (contractnumber != null ? contractnumber.hashCode() : 0);
        result = 31 * result + (performer != null ? performer.hashCode() : 0);
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        result = 31 * result + (dateofconclusion != null ? dateofconclusion.hashCode() : 0);
        result = 31 * result + (dateofclosing != null ? dateofclosing.hashCode() : 0);
        result = 31 * result + contractstate;
        return result;
    }

    private Collection<CfOrgs> cfOrgsesByIdofcontract;

    public Collection<CfOrgs> getCfOrgsesByIdofcontract() {
        return cfOrgsesByIdofcontract;
    }

    public void setCfOrgsesByIdofcontract(Collection<CfOrgs> cfOrgsesByIdofcontract) {
        this.cfOrgsesByIdofcontract = cfOrgsesByIdofcontract;
    }
}
