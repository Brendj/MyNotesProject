package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfCategoryorgOrgs {

    private long idoforgscategories;

    public long getIdoforgscategories() {
        return idoforgscategories;
    }

    public void setIdoforgscategories(long idoforgscategories) {
        this.idoforgscategories = idoforgscategories;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long idofcategoryorg;

    public long getIdofcategoryorg() {
        return idofcategoryorg;
    }

    public void setIdofcategoryorg(long idofcategoryorg) {
        this.idofcategoryorg = idofcategoryorg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfCategoryorgOrgs that = (CfCategoryorgOrgs) o;

        if (idofcategoryorg != that.idofcategoryorg) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (idoforgscategories != that.idoforgscategories) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoforgscategories ^ (idoforgscategories >>> 32));
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (idofcategoryorg ^ (idofcategoryorg >>> 32));
        return result;
    }

    private CfCategoryorg cfCategoryorgByIdofcategoryorg;

    public CfCategoryorg getCfCategoryorgByIdofcategoryorg() {
        return cfCategoryorgByIdofcategoryorg;
    }

    public void setCfCategoryorgByIdofcategoryorg(CfCategoryorg cfCategoryorgByIdofcategoryorg) {
        this.cfCategoryorgByIdofcategoryorg = cfCategoryorgByIdofcategoryorg;
    }

    private CfOrgs cfOrgsByIdoforg;

    public CfOrgs getCfOrgsByIdoforg() {
        return cfOrgsByIdoforg;
    }

    public void setCfOrgsByIdoforg(CfOrgs cfOrgsByIdoforg) {
        this.cfOrgsByIdoforg = cfOrgsByIdoforg;
    }
}
