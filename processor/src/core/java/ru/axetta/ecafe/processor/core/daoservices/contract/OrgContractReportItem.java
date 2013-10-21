package ru.axetta.ecafe.processor.core.daoservices.contract;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.08.13
 * Time: 12:47
 * To change this template use File | Settings | File Templates.
 */
public class OrgContractReportItem {

    private Long idOfOrg;
    private String shortName;
    private Long idOfContract;
    private String contractNumber;
    private Long idOfContragent;
    private String contragentName;
    private Long idOfSupplier;
    private String supplierName;

    public OrgContractReportItem() {   }

    public OrgContractReportItem(Long idOfOrg, String shortName, Long idOfContract, String contractName,
            Long idOfContragent, String contragentName, Long idOfSupplier, String supplierName) {
        this.idOfOrg = idOfOrg;
        this.shortName = shortName;
        this.idOfContract = idOfContract;
        this.contractNumber = contractName;
        this.idOfContragent = idOfContragent;
        this.contragentName = contragentName;
        this.idOfSupplier = idOfSupplier;
        this.supplierName = supplierName;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }

    public Long getIdOfContract() {
        return idOfContract;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public Long getIdOfContragent() {
        return idOfContragent;
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setIdOfContract(Long idOfContract) {
        this.idOfContract = idOfContract;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public void setIdOfContragent(Long idOfContragent) {
        this.idOfContragent = idOfContragent;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }

    public Long getIdOfSupplier() {
        return idOfSupplier;
    }

    public void setIdOfSupplier(Long idOfSupplier) {
        this.idOfSupplier = idOfSupplier;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
