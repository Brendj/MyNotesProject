package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfDiscountrulesCategoryorg {

    private long idofcatorgdiscrule;

    public long getIdofcatorgdiscrule() {
        return idofcatorgdiscrule;
    }

    public void setIdofcatorgdiscrule(long idofcatorgdiscrule) {
        this.idofcatorgdiscrule = idofcatorgdiscrule;
    }

    private long idofcategoryorg;

    public long getIdofcategoryorg() {
        return idofcategoryorg;
    }

    public void setIdofcategoryorg(long idofcategoryorg) {
        this.idofcategoryorg = idofcategoryorg;
    }

    private long idofrule;

    public long getIdofrule() {
        return idofrule;
    }

    public void setIdofrule(long idofrule) {
        this.idofrule = idofrule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfDiscountrulesCategoryorg that = (CfDiscountrulesCategoryorg) o;

        if (idofcategoryorg != that.idofcategoryorg) {
            return false;
        }
        if (idofcatorgdiscrule != that.idofcatorgdiscrule) {
            return false;
        }
        if (idofrule != that.idofrule) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofcatorgdiscrule ^ (idofcatorgdiscrule >>> 32));
        result = 31 * result + (int) (idofcategoryorg ^ (idofcategoryorg >>> 32));
        result = 31 * result + (int) (idofrule ^ (idofrule >>> 32));
        return result;
    }

    private CfCategoryorg cfCategoryorgByIdofcategoryorg;

    public CfCategoryorg getCfCategoryorgByIdofcategoryorg() {
        return cfCategoryorgByIdofcategoryorg;
    }

    public void setCfCategoryorgByIdofcategoryorg(CfCategoryorg cfCategoryorgByIdofcategoryorg) {
        this.cfCategoryorgByIdofcategoryorg = cfCategoryorgByIdofcategoryorg;
    }

    private CfDiscountrules cfDiscountrulesByIdofrule;

    public CfDiscountrules getCfDiscountrulesByIdofrule() {
        return cfDiscountrulesByIdofrule;
    }

    public void setCfDiscountrulesByIdofrule(CfDiscountrules cfDiscountrulesByIdofrule) {
        this.cfDiscountrulesByIdofrule = cfDiscountrulesByIdofrule;
    }
}
