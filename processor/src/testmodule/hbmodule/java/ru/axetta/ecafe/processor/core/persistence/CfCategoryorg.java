package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfCategoryorg {

    private long idofcategoryorg;

    public long getIdofcategoryorg() {
        return idofcategoryorg;
    }

    public void setIdofcategoryorg(long idofcategoryorg) {
        this.idofcategoryorg = idofcategoryorg;
    }

    private String categoryname;

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfCategoryorg that = (CfCategoryorg) o;

        if (idofcategoryorg != that.idofcategoryorg) {
            return false;
        }
        if (categoryname != null ? !categoryname.equals(that.categoryname) : that.categoryname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofcategoryorg ^ (idofcategoryorg >>> 32));
        result = 31 * result + (categoryname != null ? categoryname.hashCode() : 0);
        return result;
    }

    private Collection<CfCategoryorgOrgs> cfCategoryorgOrgsesByIdofcategoryorg;

    public Collection<CfCategoryorgOrgs> getCfCategoryorgOrgsesByIdofcategoryorg() {
        return cfCategoryorgOrgsesByIdofcategoryorg;
    }

    public void setCfCategoryorgOrgsesByIdofcategoryorg(
            Collection<CfCategoryorgOrgs> cfCategoryorgOrgsesByIdofcategoryorg) {
        this.cfCategoryorgOrgsesByIdofcategoryorg = cfCategoryorgOrgsesByIdofcategoryorg;
    }

    private Collection<CfDiscountrulesCategoryorg> cfDiscountrulesCategoryorgsByIdofcategoryorg;

    public Collection<CfDiscountrulesCategoryorg> getCfDiscountrulesCategoryorgsByIdofcategoryorg() {
        return cfDiscountrulesCategoryorgsByIdofcategoryorg;
    }

    public void setCfDiscountrulesCategoryorgsByIdofcategoryorg(
            Collection<CfDiscountrulesCategoryorg> cfDiscountrulesCategoryorgsByIdofcategoryorg) {
        this.cfDiscountrulesCategoryorgsByIdofcategoryorg = cfDiscountrulesCategoryorgsByIdofcategoryorg;
    }
}
